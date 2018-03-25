/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.it.br.gameserver.datatables.sql;

import com.it.br.Config;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.database.dao.ClanDao;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.instancemanager.SiegeManager;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2ClanMember;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Siege;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.*;
import com.it.br.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClanTable
{
	private static Logger _log = LoggerFactory.getLogger(ClanTable.class);
	private static ClanTable _instance;
	private Map<Integer, L2Clan> _clans;

	public static ClanTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new ClanTable();
		}
		return _instance;
	}
	public L2Clan[] getClans()
	{
	    return _clans.values().toArray(new L2Clan[_clans.size()]);
	}

	private ClanTable()
	{
		_clans = new HashMap<>();

		List<Integer> clans = ClanDao.load();

		clans.forEach(c -> {

			_clans.put(c, new L2Clan(c));
			L2Clan clan = getClan(c);

			if (clan.getDissolvingExpiryTime() != 0)
				if (clan.getDissolvingExpiryTime() < System.currentTimeMillis())
					destroyClan(clan.getClanId());
				else
					scheduleRemoveClan(clan.getClanId());
		});

		_log.info("Restored "+ clans.size() +" clans from the database.");
		restorewars();
	}

	/**
	 * @param clanId clan id for get
	 * @return L2Clan
	 */
	public L2Clan getClan(int clanId)
	{
		return _clans.get(clanId);
	}

    public L2Clan getClanByName(String clanName)
    {
		for (L2Clan clan : getClans())
		{
			if (clan.getName().equalsIgnoreCase(clanName))
			{
				return clan;
			}

		}

		return null;
    }

	/**
	 * Creates a new clan and store clan info to database
	 *
	 * @param player leader from clan
	 * @param clanName clan name
	 * @return NULL if clan with same name already exists
	 */
    public L2Clan createClan(L2PcInstance player, String clanName)
    {
    	if (null == player)
    		return null;

        if (Config.DEBUG)
            _log.debug(player.getObjectId() + "(" + player.getName() + ") requested a clan creation.");

        if (Config.MINIMUN_LEVEL_FOR_CREATION_CLAN > player.getLevel() && !player.isGM())
        {
            player.sendPacket(new SystemMessage(SystemMessageId.YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN));
            return null;
        }
        if (0 != player.getClanId())
        {
            player.sendPacket(new SystemMessage(SystemMessageId.FAILED_TO_CREATE_CLAN));
            return null;
        }
		if (System.currentTimeMillis() < player.getClanCreateExpiryTime())
		{
        	player.sendPacket(new SystemMessage(SystemMessageId.YOU_MUST_WAIT_XX_DAYS_BEFORE_CREATING_A_NEW_CLAN));
			return null;
		}
		if (!Util.isAlphaNumeric(clanName) || 2 > clanName.length())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CLAN_NAME_INCORRECT));
			return null;
		}
		if (16 < clanName.length())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CLAN_NAME_TOO_LONG));
			return null;
		}

		if (!Config.CLAN_NAME_PATTERN.matcher(clanName).matches())
		{
			player.sendPacket(SystemMessageId.CLAN_NAME_INCORRECT);
			return null;
		}

		if (null != getClanByName(clanName))
		{
            // clan name is already taken
        	SystemMessage sm = new SystemMessage(SystemMessageId.S1_ALREADY_EXISTS);
        	sm.addString(clanName);
        	player.sendPacket(sm);
        	sm = null;
            return null;
		}

		L2Clan clan = new L2Clan(IdFactory.getInstance().getNextId(), clanName);
		L2ClanMember leader = new L2ClanMember(clan, player.getName(), player.getLevel(), player.getClassId().getId(), player.getObjectId(), player.getPledgeType(), player.getPowerGrade(), player.getTitle());
		clan.setLeader(leader);
		leader.setPlayerInstance(player);
		clan.store();
		player.setClan(clan);
		player.setPledgeClass(L2ClanMember.calculatePledgeClass(player));
		player.setClanPrivileges(L2Clan.CP_ALL);

		if (Config.DEBUG)
			_log.debug("New clan created: "+clan.getClanId() + " " +clan.getName());

		_clans.put(clan.getClanId(), clan);

        //should be update packet only
        player.sendPacket(new PledgeShowInfoUpdate(clan));
        player.sendPacket(new PledgeShowMemberListAll(clan, player));
        player.sendPacket(new UserInfo(player));
        player.sendPacket(new PledgeShowMemberListUpdate(player));
        player.sendPacket(new SystemMessage(SystemMessageId.CLAN_CREATED));
        return clan;
    }

	public synchronized void destroyClan(int clanId)
	{
		L2Clan clan = getClan(clanId);
		if (clan == null)
		{
			return;
		}

		clan.broadcastToOnlineMembers(new SystemMessage(SystemMessageId.CLAN_HAS_DISPERSED));

		int castleId = clan.getHasCastle();

		if(castleId == 0)
		{
			for(Siege siege : SiegeManager.getInstance().getSieges())
			{
				siege.removeSiegeClan(clanId);
			}
		}

	    L2ClanMember leaderMember = clan.getLeader();
	    if(leaderMember == null)
	    	clan.getWarehouse().destroyAllItems("ClanRemove", null, null);
	    else
	    	clan.getWarehouse().destroyAllItems("ClanRemove", clan.getLeader().getPlayerInstance(), null);

	    for (L2ClanMember member : clan.getMembers())
	    {
	    	clan.removeClanMember(member.getName(), 0);
	    }

		_clans.remove(clanId);
		IdFactory.getInstance().releaseId(clanId);
		ClanDao.delete(clan);
	}

	public void scheduleRemoveClan(final int clanId)
	{
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			if (getClan(clanId) == null)
				return;
			if (getClan(clanId).getDissolvingExpiryTime() != 0)
				destroyClan(clanId);
		}, getClan(clanId).getDissolvingExpiryTime() - System.currentTimeMillis());
	}

	public boolean isAllyExists(String allyName)
	{
		for (L2Clan clan : getClans())
		{
			if (clan.getAllyName() != null &&
				clan.getAllyName().equalsIgnoreCase(allyName))
			{
				return true;
			}
		}
		return false;
	}

    public void storeclanswars(int clanId1, int clanId2){
        L2Clan clan1 = ClanTable.getInstance().getClan(clanId1);
        L2Clan clan2 = ClanTable.getInstance().getClan(clanId2);
        clan1.setEnemyClan(clan2);
        clan2.setAttackerClan(clan1);
        clan1.broadcastClanStatus();
        clan2.broadcastClanStatus();

        ClanDao.storeWars(clanId1, clanId2);

        SystemMessage msg = new SystemMessage(SystemMessageId.CLAN_WAR_DECLARED_AGAINST_S1_IF_KILLED_LOSE_LOW_EXP);
        msg.addString(clan2.getName());
        clan1.broadcastToOnlineMembers(msg);

		// clan1 declared clan war.
        msg = new SystemMessage(SystemMessageId.CLAN_S1_DECLARED_WAR);
        msg.addString(clan1.getName());
        clan2.broadcastToOnlineMembers(msg);
    }

    public void deleteclanswars(int clanId1, int clanId2)
    {
        L2Clan clan1 = ClanTable.getInstance().getClan(clanId1);
        L2Clan clan2 = ClanTable.getInstance().getClan(clanId2);
        clan1.deleteEnemyClan(clan2);
        clan2.deleteAttackerClan(clan1);
        clan1.broadcastClanStatus();
        clan2.broadcastClanStatus();

		ClanDao.deleteWars(clan1, clan2);

        SystemMessage msg = new SystemMessage(SystemMessageId.WAR_AGAINST_S1_HAS_STOPPED);
        msg.addString(clan2.getName());
        clan1.broadcastToOnlineMembers(msg);
        msg = new SystemMessage(SystemMessageId.CLAN_S1_HAS_DECIDED_TO_STOP);
        msg.addString(clan1.getName());
        clan2.broadcastToOnlineMembers(msg);
    }

    public void checkSurrender(L2Clan clan1, L2Clan clan2)
    {
        int count = 0;
        for(L2ClanMember player: clan1.getMembers())
        {
            if(player != null && player.getPlayerInstance().getWantsPeace() == 1)
                count++;
        }
        if(count == clan1.getMembers().length-1)
        {
            clan1.deleteEnemyClan(clan2);
            clan2.deleteEnemyClan(clan1);
            deleteclanswars(clan1.getClanId(),clan2.getClanId());
        }
    }

    private void restorewars()
    {
		Map<Integer, List<Integer>> wars = ClanDao.restoreWars();
		wars.forEach((k,v) -> {
			Integer clan1 = v.get(0);
			Integer clan2 = v.get(1);
			getClan(clan1).setEnemyClan(clan2);
			getClan(clan2).setAttackerClan(clan1);
		});
    }
}
