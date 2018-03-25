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
package com.it.br.gameserver.network.clientpackets;

import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.datatables.xml.MapRegionTable;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.instancemanager.ClanHallManager;
import com.it.br.gameserver.model.L2SiegeClan;
import com.it.br.gameserver.model.Location;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.model.entity.ClanHall;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.Revive;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.util.IllegalPlayerAction;
import com.it.br.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.it.br.configuration.Configurator.getSettings;

public final class RequestRestartPoint extends L2GameClientPacket
{
	private static final String _C__6d_REQUESTRESTARTPOINT = "[C] 6d RequestRestartPoint";
	private static Logger _log = LoggerFactory.getLogger(RequestRestartPoint.class);

	protected int     _requestedPointType;
	protected boolean _continuation;



	@Override
	protected void readImpl()
	{
		_requestedPointType = readD();
	}

	class DeathTask implements Runnable
	{
		L2PcInstance activeChar;
		DeathTask (L2PcInstance _activeChar)
		{
			activeChar = _activeChar;
		}

	
		@SuppressWarnings("unused")
		public void run()
		{
			try
			{
				Location loc = null;
				Castle castle=null;

				if (activeChar.isInJail()) 
					_requestedPointType = 27;
				else if (activeChar.isFestivalParticipant())
					_requestedPointType = 4;

				switch (_requestedPointType)
				{
					case 1: // to clanhall
						if (activeChar.getClan().getHasHideout() == 0)
						{
							//cheater
							activeChar.sendMessage("You may not use this respawn point!");
							Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalPlayerAction.PUNISH_KICK);
							return;
						}
						loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.ClanHall);

						if (ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan())!= null &&
								ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()).getFunction(ClanHall.FUNC_RESTORE_EXP) != null)
						{
							activeChar.restoreExp(ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()).getFunction(ClanHall.FUNC_RESTORE_EXP).getLvl());
						}
						break;

					case 2: // to castle
						Boolean isInDefense = false;
						int instanceId = 0;
						castle = CastleManager.getInstance().getCastle(activeChar);
						if (castle != null && castle.getSiege().getIsInProgress())
						{
							//siege in progress
							if (castle.getSiege().checkIsDefender(activeChar.getClan()))
								isInDefense = true;
						}
						if (activeChar.getClan().getHasCastle() == 0 && !isInDefense)
						{
							//cheater
							activeChar.sendMessage("You may not use this respawn point!");
							Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalPlayerAction.PUNISH_KICK);
							return;
						}
						loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.Castle);
						break;

					case 3: // to siege HQ
						L2SiegeClan siegeClan = null;
						castle = CastleManager.getInstance().getCastle(activeChar);

						if (castle != null && castle.getSiege().getIsInProgress())
							siegeClan = castle.getSiege().getAttackerClan(activeChar.getClan());

						if (siegeClan == null || siegeClan.getFlag().size() == 0)
						{
							//cheater
							activeChar.sendMessage("You may not use this respawn point!");
							Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalPlayerAction.PUNISH_KICK);
							return;
						}
						loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.SiegeFlag);
						break;

					case 4: // Fixed or Player is a festival participant
						if (!activeChar.isGM() && !activeChar.isFestivalParticipant())
						{
							//cheater
							activeChar.sendMessage("You may not use this respawn point!");
							Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " used respawn cheat.", IllegalPlayerAction.PUNISH_KICK);
							return;
						}
						instanceId = activeChar.getInstanceId(); 
						loc = new Location(activeChar.getX(), activeChar.getY(), activeChar.getZ()); // spawn them where they died
						break;

					case 27: // to jail
						if (!activeChar.isInJail()) return;
						loc = new Location(-114356, -249645, -2984);
						break;

					default:
						L2JBrasilSettings l2jBrasilSettings = getSettings(L2JBrasilSettings.class);
						if (l2jBrasilSettings.isCustomReSpawnEnabled())
						{
							loc = new Location(l2jBrasilSettings.getRespawnLocationX(), l2jBrasilSettings.getRespawnLocationY(), l2jBrasilSettings.getRespawnLocationZ());
						}
						else
						{
							loc = MapRegionTable.getInstance().getTeleToLocation(activeChar, MapRegionTable.TeleportWhereType.Town);
						}
					break;
				}

				Object instanceId = null;
				//Teleport and revive
				activeChar.setInstanceId(instanceId);
				activeChar.setIsIn7sDungeon(false);
				activeChar.setIsPendingRevive(true);
				activeChar.teleToLocation(loc, true);
			} catch (Throwable e) {
				//_log.error( "", e);
			}
		}
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();

		if (activeChar == null)
			return;

        if (TvTEvent.isStarted() && TvTEvent.isPlayerParticipant(activeChar.getObjectId()))
			return;

		//SystemMessage sm2 = new SystemMessage(SystemMessage.S1_S2);
		//sm2.addString("type:"+requestedPointType);
		//activeChar.sendPacket(sm2);

		if (activeChar.isFakeDeath())
		{
			activeChar.stopFakeDeath(null);
			activeChar.broadcastPacket(new Revive(activeChar));
			return;
		}
		else if (!activeChar.isAlikeDead())
		{
			_log.warn("Living player ["+activeChar.getName()+"] called RestartPointPacket due to her/his alikedead mode! Ban this player!");
			return;
		}

		Castle castle = CastleManager.getInstance().getCastle(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		if (castle != null && castle.getSiege().getIsInProgress())
		{
			//DeathFinalizer df = new DeathFinalizer(10000);
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
			if (activeChar.getClan() != null && castle.getSiege().checkIsAttacker(activeChar.getClan()))
			{
				// Schedule respawn delay for attacker
				ThreadPoolManager.getInstance().scheduleGeneral(new DeathTask(activeChar), castle.getSiege().getAttackerRespawnDelay());
				sm.addString("You will be re-spawned in " + castle.getSiege().getAttackerRespawnDelay()/1000 + " seconds");
				activeChar.sendPacket(sm);
			}
			else
			{
				// Schedule respawn delay for defender with penalty for CT lose
				ThreadPoolManager.getInstance().scheduleGeneral(new DeathTask(activeChar), castle.getSiege().getDefenderRespawnDelay());
				sm.addString("You will be re-spawned in " + castle.getSiege().getDefenderRespawnDelay()/1000 + " seconds");
				activeChar.sendPacket(sm);
			}
			sm = null;
			return;
		}

		ThreadPoolManager.getInstance().scheduleGeneral(new DeathTask(activeChar), 1);
	}

	/* (non-Javadoc)
     * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
     */

	@Override
	public String getType()
	{
		return _C__6d_REQUESTRESTARTPOINT;
	}
}
