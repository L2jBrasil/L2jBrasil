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
package com.it.br.gameserver.model.entity;

import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.database.dao.CastleDao;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.datatables.xml.MapRegionTable;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.instancemanager.MercTicketManager;
import com.it.br.gameserver.instancemanager.SiegeGuardManager;
import com.it.br.gameserver.instancemanager.SiegeManager;
import com.it.br.gameserver.instancemanager.SiegeManager.SiegeSpawn;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.L2SiegeClan.SiegeClanType;
import com.it.br.gameserver.model.actor.instance.L2ArtefactInstance;
import com.it.br.gameserver.model.actor.instance.L2ControlTowerInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.RelationChanged;
import com.it.br.gameserver.network.serverpackets.SiegeInfo;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.network.serverpackets.UserInfo;
import com.it.br.gameserver.templates.L2NpcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Siege
{
    // ==========================================================================================
    // Message to add/check
    //  id=17  msg=[Castle siege has begun.] c3_attr1=[SystemMsg_k.17]
    //  id=18  msg=[Castle siege is over.]   c3_attr1=[SystemMsg_k.18]
    //  id=288 msg=[The castle gate has been broken down.]
    //  id=291 msg=[Clan $s1 is victorious over $s2's castle siege!]
    //  id=292 msg=[$s1 has announced the castle siege time.]
    //  - id=293 msg=[The registration term for $s1 has ended.]
    //  - id=358 msg=[$s1 hour(s) until castle siege conclusion.]
    //  - id=359 msg=[$s1 minute(s) until castle siege conclusion.]
    //  - id=360 msg=[Castle siege $s1 second(s) left!]
    //  id=640 msg=[You have failed to refuse castle defense aid.]
    //  id=641 msg=[You have failed to approve castle defense aid.]
    //  id=644 msg=[You are not yet registered for the castle siege.]
    //  - id=645 msg=[Only clans with Level 4 and higher may register for a castle siege.]
    //  id=646 msg=[You do not have the authority to modify the castle defender list.]
    //  - id=688 msg=[The clan that owns the castle is automatically registered on the defending side.]
    //  id=689 msg=[A clan that owns a castle cannot participate in another siege.]
    //  id=690 msg=[You cannot register on the attacking side because you are part of an alliance with the clan that owns the castle.]
    //  id=718 msg=[The castle gates cannot be opened and closed during a siege.]
    //  - id=295 msg=[$s1's siege was canceled because there were no clans that participated.]
    //  id=659 msg=[This is not the time for siege registration and so registrations cannot be accepted or rejected.]
    //  - id=660 msg=[This is not the time for siege registration and so registration and cancellation cannot be done.]
    //  id=663 msg=[The siege time has been declared for $s. It is not possible to change the time after a siege time has been declared. Do you want to continue?]
    //  id=667 msg=[You are registering on the attacking side of the $s1 siege. Do you want to continue?]
    //  id=668 msg=[You are registering on the defending side of the $s1 siege. Do you want to continue?]
    //  id=669 msg=[You are canceling your application to participate in the $s1 siege battle. Do you want to continue?]
    //  id=707 msg=[You cannot teleport to a village that is in a siege.]
    //  - id=711 msg=[The siege of $s1 has started.]
    //  - id=712 msg=[The siege of $s1 has finished.]
    //  id=844 msg=[The siege to conquer $s1 has begun.]
    //  - id=845 msg=[The deadline to register for the siege of $s1 has passed.]
    //  - id=846 msg=[The siege of $s1 has been canceled due to lack of interest.]
    //  - id=856 msg=[The siege of $s1 has ended in a draw.]
    //  id=285 msg=[Clan $s1 has succeeded in engraving the ruler!]
    //  - id=287 msg=[The opponent clan has begun to engrave the ruler.]

    public static enum TeleportWhoType
    {
        All, Attacker, DefenderNotOwner, Owner, Spectator
    }

    // ===============================================================
	// Schedule task
	public class ScheduleEndSiegeTask implements Runnable
	{
		private Castle _castleInst;

		public ScheduleEndSiegeTask(Castle pCastle)
		{
			_castleInst = pCastle;
		}

		public void run()
		{
			if (!getIsInProgress())
				return;

			try
			{
				long timeRemaining = _siegeEndDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
				if (timeRemaining > 3600000)
				{
					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_castleInst), timeRemaining - 3600000); // Prepare task for 1 hr left.
				}
				else if ((timeRemaining <= 3600000) && (timeRemaining > 600000))
				{
					announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until " + getCastle().getName() + " siege conclusion.", true);
					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_castleInst), timeRemaining - 600000); // Prepare task for 10 minute left.
				}
				else if ((timeRemaining <= 600000) && (timeRemaining > 300000))
				{
					announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until " + getCastle().getName() + " siege conclusion.", true);
					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_castleInst), timeRemaining - 300000); // Prepare task for 5 minute left.
				}
				else if ((timeRemaining <= 300000) && (timeRemaining > 10000))
				{
					announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until " + getCastle().getName() + " siege conclusion.", true);
					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_castleInst), timeRemaining - 10000); // Prepare task for 10 seconds count down
				}
				else if ((timeRemaining <= 10000) && (timeRemaining > 0))
				{
					announceToPlayer(getCastle().getName() + " siege " + Math.round(timeRemaining / 1000) + " second(s) left!", true);
					ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_castleInst), timeRemaining); // Prepare task for second count down
				}
				else
				{
					_castleInst.getSiege().endSiege();
				}
			}
			catch (Throwable t)
			{

			}
		}
	}

    public class ScheduleStartSiegeTask implements Runnable
    {
        private Castle _castleInst;

        public ScheduleStartSiegeTask(Castle pCastle)
        {
        	_castleInst = pCastle;
        }

		public void run()
        {
            if (getIsInProgress())
            	return;

            try
            {
                long timeRemaining = getSiegeDate().getTimeInMillis()
                    - Calendar.getInstance().getTimeInMillis();
                if (timeRemaining > 86400000)
                {
                    ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst),timeRemaining - 86400000); // Prepare task for 24 before siege start to end registration
                }
                else if ((timeRemaining <= 86400000) && (timeRemaining > 13600000))
                {
                    announceToPlayer("The registration term for " + getCastle().getName() + " has ended.", false);
                    _isRegistrationOver = true;
                    clearSiegeWaitingClan();
                    ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst),timeRemaining - 13600000); // Prepare task for 1 hr left before siege start.
                }
                else if ((timeRemaining <= 13600000) && (timeRemaining > 600000))
                {
                    announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until " + getCastle().getName() + " siege begin.", false);
                    ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst), timeRemaining - 600000); // Prepare task for 10 minute left.
                }
                else if ((timeRemaining <= 600000) && (timeRemaining > 300000))
                {
                    announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until " + getCastle().getName() + " siege begin.", false);
                    ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst), timeRemaining - 300000); // Prepare task for 5 minute left.
                }
                else if ((timeRemaining <= 300000) && (timeRemaining > 10000))
                {
                    announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until " + getCastle().getName() + " siege begin.", false);
                    ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst), timeRemaining - 10000); // Prepare task for 10 seconds count down
                }
                else if ((timeRemaining <= 10000) && (timeRemaining > 0))
                {
                    announceToPlayer(getCastle().getName() + " siege " + Math.round(timeRemaining / 1000) + " second(s) to start!", false);
                    ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst), timeRemaining); // Prepare task for second count down
                }
                else
                {
                	_castleInst.getSiege().startSiege();
                }
            }
            catch (Throwable t)
            {

            }
        }
    }

    // =========================================================
    // Data Field
    // Attacker and Defender
    private List<L2SiegeClan> _attackerClans = new ArrayList<>();
    private List<L2SiegeClan> _defenderClans = new ArrayList<>();
    private List<L2SiegeClan> _defenderWaitingClans = new ArrayList<>();
    private int _defenderRespawnDelayPenalty;

    // Castle setting
    private List<L2ArtefactInstance> _artifacts = new ArrayList<>();
    private List<L2ControlTowerInstance> _controlTowers = new ArrayList<>();
    private Castle[] _castle;
    private boolean _isInProgress = false;
    private boolean _isNormalSide = true; // true = Atk is Atk, false = Atk is Def
    protected boolean _isRegistrationOver = false;
    protected Calendar _siegeEndDate;
    private SiegeGuardManager _siegeGuardManager;
    protected Calendar _siegeRegistrationEndDate;

    // =========================================================
    // Constructor
    public Siege(Castle[] castle)
    {
        _castle = castle;
        _siegeGuardManager = new SiegeGuardManager(getCastle());

        startAutoTask();
    }

    // =========================================================
    // Siege phases
    /**
     * When siege ends<BR><BR>
     */
    public void endSiege()
    {
        if (getIsInProgress())
        {
            announceToPlayer("The siege of " + getCastle().getName() + " has finished!", false);

            if (getCastle().getOwnerId() <= 0)
                announceToPlayer("The siege of " + getCastle().getName() + " has ended in a draw.", false);

            removeFlags(); // Removes all flags. Note: Remove flag before teleporting players
            teleportPlayer(Siege.TeleportWhoType.Attacker, MapRegionTable.TeleportWhereType.Town); // Teleport to the second closest town
            teleportPlayer(Siege.TeleportWhoType.DefenderNotOwner, MapRegionTable.TeleportWhereType.Town); // Teleport to the second closest town
            teleportPlayer(Siege.TeleportWhoType.Spectator, MapRegionTable.TeleportWhereType.Town); // Teleport to the second closest town
            _isInProgress = false; // Flag so that siege instance can be started
            updatePlayerSiegeStateFlags(true);
            saveCastleSiege(); // Save castle specific data
            clearSiegeClan(); // Clear siege clan from db
            removeArtifact(); // Remove artifact from this castle
            removeControlTower(); // Remove all control tower from this castle
            _siegeGuardManager.unspawnSiegeGuard(); // Remove all spawned siege guard from this castle
            if (getCastle().getOwnerId() > 0)
                CastleDao.removeMercs(_siegeGuardManager.getCastle());
            getCastle().spawnDoor(); // Respawn door to castle
            getCastle().getZone().updateZoneStatusForCharactersInside();
        }
    }

    private void removeDefender(L2SiegeClan sc)
    {
        if (sc != null) getDefenderClans().remove(sc);
    }

    private void removeAttacker(L2SiegeClan sc)
    {
        if (sc != null) getAttackerClans().remove(sc);
    }

    private void addDefender(L2SiegeClan sc, SiegeClanType type)
    {
        if(sc == null) return;
        sc.setType(type);
        getDefenderClans().add(sc);
    }

    private void addAttacker(L2SiegeClan sc)
    {
        if(sc == null) return;
        sc.setType(SiegeClanType.ATTACKER);
        getAttackerClans().add(sc);
    }

    /**
     * When control of castle changed during siege<BR><BR>
     */
    public void midVictory()
    {
        if (getIsInProgress()) // Siege still in progress
        {
            if (getCastle().getOwnerId() > 0) CastleDao.removeMercs(_siegeGuardManager.getCastle()); // Remove all merc entry from db

            if (getDefenderClans().size() == 0 && // If defender doesn't exist (Pc vs Npc)
                getAttackerClans().size() == 1 // Only 1 attacker
            )
            {
                L2SiegeClan sc_newowner = getAttackerClan(getCastle().getOwnerId());
                removeAttacker(sc_newowner);
                addDefender(sc_newowner, SiegeClanType.OWNER);
                endSiege();
                return;
            }
            if (getCastle().getOwnerId() > 0)
            {
                int allyId = ClanTable.getInstance().getClan(getCastle().getOwnerId()).getAllyId();
                if (getDefenderClans().size() == 0) // If defender doesn't exist (Pc vs Npc)
                // and only an alliance attacks
                {
                    // The player's clan is in an alliance
                    if (allyId != 0)
                    {
                        boolean allinsamealliance = true;
                        for (L2SiegeClan sc : getAttackerClans())
                        {
                            if(sc != null)
                            {
                                if(ClanTable.getInstance().getClan(sc.getClanId()).getAllyId() != allyId)
                                    allinsamealliance = false;
                            }
                        }
                        if(allinsamealliance)
                        {
                            L2SiegeClan sc_newowner = getAttackerClan(getCastle().getOwnerId());
                            removeAttacker(sc_newowner);
                            addDefender(sc_newowner, SiegeClanType.OWNER);
                            endSiege();
                            return;
                        }
                    }
                }

                for (L2SiegeClan sc : getDefenderClans())
                {
                    if(sc != null)
                    {
                        removeDefender(sc);
                        addAttacker(sc);
                    }
                }

                L2SiegeClan sc_newowner = getAttackerClan(getCastle().getOwnerId());
                removeAttacker(sc_newowner);
                addDefender(sc_newowner, SiegeClanType.OWNER);

                // The player's clan is in an alliance
                if (allyId != 0)
                {
                    L2Clan[] clanList = ClanTable.getInstance().getClans();

                    for (L2Clan clan : clanList)
                    {
                        if (clan.getAllyId() == allyId)
                        {
                            L2SiegeClan sc = getAttackerClan(clan.getClanId());
                            if(sc != null)
                            {
                                removeAttacker(sc);
                                addDefender(sc, SiegeClanType.DEFENDER);
                            }
                        }
                    }
                }

                teleportPlayer(Siege.TeleportWhoType.Attacker, MapRegionTable.TeleportWhereType.SiegeFlag); // Teleport to the second closest town
                teleportPlayer(Siege.TeleportWhoType.Spectator, MapRegionTable.TeleportWhereType.Town);     // Teleport to the second closest town
                removeDefenderFlags(); 		 // Removes defenders' flags
                CastleDao.removeDoorUpgrade(getCastle()); // Remove all castle upgrade
                getCastle().spawnDoor(true); // Respawn door to castle but make them weaker (50% hp)
                updatePlayerSiegeStateFlags(false);
            }
        }
    }

    public void startSiege()
    {
        if (!getIsInProgress())
        {
            if (getAttackerClans().size() <= 0)
            {
                SystemMessage sm;
                if (getCastle().getOwnerId() <= 0)
            		sm = new SystemMessage(SystemMessageId.SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST);
                else
                    sm = new SystemMessage(SystemMessageId.S1_SIEGE_WAS_CANCELED_BECAUSE_NO_CLANS_PARTICIPATED);
                sm.addString(getCastle().getName());
                Announcements.getInstance().announceToAll(sm);
                return;
            }

            _isNormalSide = true; // Atk is now atk
            _isInProgress = true; // Flag so that same siege instance cannot be started again
            loadSiegeClan(); // Load siege clan from db
            updatePlayerSiegeStateFlags(false);
            teleportPlayer(Siege.TeleportWhoType.Attacker, MapRegionTable.TeleportWhereType.Town); // Teleport to the closest town
			teleportPlayer(Siege.TeleportWhoType.Spectator, MapRegionTable.TeleportWhereType.Town); // Teleport to the second closest town
            spawnArtifact(getCastle().getCastleId()); // Spawn artifact
            spawnControlTower(getCastle().getCastleId()); // Spawn control tower
            getCastle().spawnDoor(); // Spawn door
            spawnSiegeGuard(); // Spawn siege guard
            MercTicketManager.getInstance().deleteTickets(getCastle().getCastleId()); // remove the tickets from the ground
            _defenderRespawnDelayPenalty = 0; // Reset respawn delay
            getCastle().getZone().updateZoneStatusForCharactersInside();
            // Schedule a task to prepare auto siege end
            _siegeEndDate = Calendar.getInstance();
            _siegeEndDate.add(Calendar.MINUTE, SiegeManager.getInstance().getSiegeLength());
            ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(getCastle()), 1000); // Prepare auto end task
            announceToPlayer("The siege of " + getCastle().getName() + " has started!", false);
        }
    }

    // =========================================================
    // Method - Public
    /**
     * Announce to player.<BR><BR>
     * @param message The String of the message to send to player
     * @param inAreaOnly The boolean flag to show message to players in area only.
     */
    public void announceToPlayer(String message, boolean inAreaOnly)
    {
    	if (inAreaOnly)
    	{
    		getCastle().getZone().announceToPlayers(message);
    		return;
    	}

        // Get all players
        for (L2PcInstance player : L2World.getInstance().getAllPlayers())
        {
                player.sendMessage(message);
        }
    }

    public void updatePlayerSiegeStateFlags(boolean clear)
    {
    	L2Clan clan;
    	for(L2SiegeClan siegeclan : getAttackerClans())
        {
    		if (siegeclan == null)
    			continue;
    		clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
    		for (L2PcInstance member : clan.getOnlineMembers(""))
    		{
    			if (clear)
    				member.setSiegeState((byte)0);
    			else
    				member.setSiegeState((byte)1);
    			member.sendPacket(new UserInfo(member));
    			for (L2PcInstance player : member.getKnownList().getKnownPlayers().values())
    			{
    				player.sendPacket(new RelationChanged(member, member.getRelation(player), member.isAutoAttackable(player)));
    			}
    		}
        }

    	for(L2SiegeClan siegeclan : getDefenderClans())
        {
    		clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
    		for (L2PcInstance member : clan.getOnlineMembers(""))
    		{
    			if (clear) 
    				member.setSiegeState((byte)0);
    			else member.setSiegeState((byte)2);
    			member.sendPacket(new UserInfo(member));
    			for (L2PcInstance player : member.getKnownList().getKnownPlayers().values()) 
    			{
    				player.sendPacket(new RelationChanged(member, member.getRelation(player), member.isAutoAttackable(player)));
    			}
    		}
        }
    }

    /**
     * Approve clan as defender for siege<BR><BR>
     * @param clanId The int of player's clan id
     */
    public void approveSiegeDefenderClan(int clanId)
    {
        if (clanId <= 0) return;
        saveSiegeClan(ClanTable.getInstance().getClan(clanId), 0, true);
        loadSiegeClan();
    }

    /** Return true if object is inside the zone */
    public boolean checkIfInZone(L2Object object)
    {
        return checkIfInZone(object.getX(), object.getY(), object.getZ());
    }

    /** Return true if object is inside the zone */
    public boolean checkIfInZone(int x, int y, int z)
    {
        return (getIsInProgress() && (getCastle().checkIfInZone(x, y, z))); // Castle zone during siege
    }

    /**
     * Return true if clan is attacker<BR><BR>
     * @param clan The L2Clan of the player
     */
    public boolean checkIsAttacker(L2Clan clan)
    {
        return (getAttackerClan(clan) != null);
    }

    /**
     * Return true if clan is defender<BR><BR>
     * @param clan The L2Clan of the player
     */
    public boolean checkIsDefender(L2Clan clan)
    {
        return (getDefenderClan(clan) != null);
    }

    /**
     * Return true if clan is defender waiting approval<BR><BR>
     * @param clan The L2Clan of the player
     */
    public boolean checkIsDefenderWaiting(L2Clan clan)
    {
        return (getDefenderWaitingClan(clan) != null);
    }

    /** Clear all registered siege clans from database for castle */
    public void clearSiegeClan()
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("DELETE FROM siege_clans WHERE castle_id=?");
            statement.setInt(1, getCastle().getCastleId());
            statement.execute();
            statement.close();

            if (getCastle().getOwnerId() > 0)
            {
            	PreparedStatement statement2 = con.prepareStatement("DELETE FROM siege_clans WHERE clan_id=?");
            	statement2.setInt(1, getCastle().getOwnerId());
            	statement2.execute();
            	statement2.close();
            }

            getAttackerClans().clear();
            getDefenderClans().clear();
            getDefenderWaitingClans().clear();
        }
        catch (Exception e)
        {
            System.out.println("Exception: clearSiegeClan(): " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
            }
        }
    }

    /** Clear all siege clans waiting for approval from database for castle */
    public void clearSiegeWaitingClan()
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("DELETE FROM siege_clans WHERE castle_id=? and type = 2");
            statement.setInt(1, getCastle().getCastleId());
            statement.execute();
            statement.close();

            getDefenderWaitingClans().clear();
        }
        catch (Exception e)
        {
            System.out.println("Exception: clearSiegeWaitingClan(): " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
            }
        }
    }

    /** Return list of L2PcInstance registered as attacker in the zone. */
    public List<L2PcInstance> getAttackersInZone()
    {
    	List<L2PcInstance> players = new ArrayList<>();
    	L2Clan clan;
    	for(L2SiegeClan siegeclan : getAttackerClans())
        {
             clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
             for (L2PcInstance player : clan.getOnlineMembers(""))
             {
             	if (checkIfInZone(player.getX(), player.getY(), player.getZ())) players.add(player);
             }
        }
    	return players;
    }

    /** Return list of L2PcInstance registered as defender but not owner in the zone. */
    public List<L2PcInstance> getDefendersButNotOwnersInZone()
    {
        List<L2PcInstance> players = new ArrayList<>();
    	L2Clan clan;
    	for(L2SiegeClan siegeclan : getDefenderClans())
        {
             clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
             if (clan.getClanId() == getCastle().getOwnerId()) continue;
             for (L2PcInstance player : clan.getOnlineMembers(""))
             {
             	if (checkIfInZone(player.getX(), player.getY(), player.getZ())) players.add(player);
             }
        }
    	return players;
    }

    /** Return list of L2PcInstance in the zone. */
    public List<L2PcInstance> getPlayersInZone()
    {
        return getCastle().getZone().getAllPlayers();
    }

    /** Return list of L2PcInstance owning the castle in the zone. */
    public List<L2PcInstance> getOwnersInZone()
    {
        List<L2PcInstance> players = new ArrayList<>();
    	L2Clan clan;
    	for(L2SiegeClan siegeclan : getDefenderClans())
        {
             clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
             if (clan.getClanId() != getCastle().getOwnerId()) continue;
             for (L2PcInstance player : clan.getOnlineMembers(""))
             {
             	if (checkIfInZone(player.getX(), player.getY(), player.getZ())) players.add(player);
             }
        }
    	return players;
    }

    /** Return list of L2PcInstance not registered as attacker or defender in the zone. */
    public List<L2PcInstance> getSpectatorsInZone()
    {
        List<L2PcInstance> players = new ArrayList<>();

        for (L2PcInstance player : L2World.getInstance().getAllPlayers())
        {
            // quick check from player states, which don't include siege number however
        	if (!player.isInsideZone(L2Character.ZONE_SIEGE) || player.getSiegeState() != 0) continue;
        	if ( checkIfInZone(player.getX(), player.getY(), player.getZ()))
        		players.add(player);
        }

       return players;
    }

    /** Control Tower was skilled */
    public void killedCT(L2NpcInstance ct)
    {
        _defenderRespawnDelayPenalty += SiegeManager.getInstance().getControlTowerLosePenalty(); // Add respawn penalty to defenders for each control tower lose
    }

    /** Remove the flag that was killed */
    public void killedFlag(L2NpcInstance flag)
    {
        if (flag == null) return;
        for (int i = 0; i < getAttackerClans().size(); i++)
        {
            if (getAttackerClan(i).removeFlag(flag)) return;
        }
    }

    /** Display list of registered clans */
    public void listRegisterClan(L2PcInstance player)
    {
        player.sendPacket(new SiegeInfo(getCastle()));
    }

    /**
     * Register clan as attacker<BR><BR>
     * @param player The L2PcInstance of the player trying to register
     */
    public void registerAttacker(L2PcInstance player)
    {
        registerAttacker(player, false);
    }

    public void registerAttacker(L2PcInstance player, boolean force)
    {

    	if(player.getClan() == null)
    		return;

    	int allyId = 0;
    	if (getCastle().getOwnerId() != 0)
    		allyId = ClanTable.getInstance().getClan(getCastle().getOwnerId()).getAllyId();
    	if (allyId != 0)
    	{
    		if(player.getClan().getAllyId() == allyId && !force)
    		{
    			player.sendMessage("You cannot register as an attacker because your alliance owns the castle");
    			return;
    		}
    	}
    	if (force || checkIfCanRegister(player)) 
    		    saveSiegeClan(player.getClan(), 1, false); // Save to database
    }

    /**
     * Register clan as defender<BR><BR>
     * @param player The L2PcInstance of the player trying to register
     */
    public void registerDefender(L2PcInstance player)
    {
        registerDefender(player, false);
    }

    public void registerDefender(L2PcInstance player, boolean force)
    {
        if (getCastle().getOwnerId() <= 0) 
        	player.sendMessage("You cannot register as a defender because "+ getCastle().getName() + " is owned by NPC.");
        else if 
        (force || checkIfCanRegister(player)) saveSiegeClan(player.getClan(), 2, false); // Save to database
    }

    /**
     * Remove clan from siege<BR><BR>
     * @param clanId The int of player's clan id
     */
    public void removeSiegeClan(int clanId)
    {
        if (clanId <= 0) return;

        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("DELETE FROM siege_clans WHERE castle_id=? and clan_id=?");
            statement.setInt(1, getCastle().getCastleId());
            statement.setInt(2, clanId);
            statement.execute();
            statement.close();

            loadSiegeClan();
        }
        catch (Exception e)
        {
        }
        finally
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * Remove clan from siege<BR><BR>
     * @param clan The L2Clan of player/clan being removed
     */
    public void removeSiegeClan(L2Clan clan)
    {
        if (clan == null || clan.getHasCastle() == getCastle().getCastleId()
            || !SiegeManager.getInstance().checkIsRegistered(clan, getCastle().getCastleId())) return;
        removeSiegeClan(clan.getClanId());
    }

    /**
     * Remove clan from siege<BR><BR>
     * @param player The L2PcInstance of player/clan being removed
     */
    public void removeSiegeClan(L2PcInstance player)
    {
        removeSiegeClan(player.getClan());
    }

    /**
     * Start the auto tasks<BR><BR>
     */
    public void startAutoTask()
    {
        correctSiegeDateTime();
        System.out.println("Siege of " + getCastle().getName() + ": " + getCastle().getSiegeDate().getTime());

        loadSiegeClan();

        // Schedule registration end
        _siegeRegistrationEndDate = Calendar.getInstance();
        _siegeRegistrationEndDate.setTimeInMillis(getCastle().getSiegeDate().getTimeInMillis());
        _siegeRegistrationEndDate.add(Calendar.DATE, -1);

        // Schedule siege auto start
        ThreadPoolManager.getInstance().scheduleGeneral(new Siege.ScheduleStartSiegeTask(getCastle()),1000);
    }

    /**
     * Teleport players
     */
    public void teleportPlayer(TeleportWhoType teleportWho,MapRegionTable.TeleportWhereType teleportWhere)
    {
        List<L2PcInstance> players;
        switch (teleportWho)
        {
            case Owner:
                players = getOwnersInZone();
                break;
            case Attacker:
                players = getAttackersInZone();
                break;
            case DefenderNotOwner:
                players = getDefendersButNotOwnersInZone();
                break;
            case Spectator:
                players = getSpectatorsInZone();
                break;
            default:
                players = getPlayersInZone();
        };

        for (L2PcInstance player : players)
        {
            if (player.isGM() || player.isInJail()) continue;
            player.teleToLocation(teleportWhere);
        }
    }

    // =========================================================
    // Method - Private
    /**
     * Add clan as attacker<BR><BR>
     * @param clanId The int of clan's id
     */
    private void addAttacker(int clanId)
    {
        getAttackerClans().add(new L2SiegeClan(clanId, SiegeClanType.ATTACKER)); // Add registered attacker to attacker list
    }

    /**
     * Add clan as defender<BR><BR>
     * @param clanId The int of clan's id
     */
    private void addDefender(int clanId)
    {
        getDefenderClans().add(new L2SiegeClan(clanId, SiegeClanType.DEFENDER)); // Add registered defender to defender list
    }

    /**
     * <p>Add clan as defender with the specified type</p>
     * @param clanId The int of clan's id
     * @param type the type of the clan
     */
    private void addDefender(int clanId, SiegeClanType type)
    {
        getDefenderClans().add(new L2SiegeClan(clanId, type));
    }

    /**
     * Add clan as defender waiting approval<BR><BR>
     * @param clanId The int of clan's id
     */
    private void addDefenderWaiting(int clanId)
    {
        getDefenderWaitingClans().add(new L2SiegeClan(clanId, SiegeClanType.DEFENDER_PENDING)); // Add registered defender to defender list
    }

    /**
     * Return true if the player can register.<BR><BR>
     * @param player The L2PcInstance of the player trying to register
     */
    private boolean checkIfCanRegister(L2PcInstance player)
    {
        if (getIsRegistrationOver())
        {
        	player.sendMessage("The deadline to register for the siege of " + getCastle().getName() + " has passed.");
        	return false;
        }
        else if (getIsInProgress()) 
        {
        	player.sendMessage("This is not the time for siege registration and so registration and cancellation cannot be done.");
        	return false;
        }
        else if (player.getClan() == null || player.getClan().getLevel() < SiegeManager.getInstance().getSiegeClanMinLevel())
        {
        	player.sendMessage("Only clans with Level " + SiegeManager.getInstance().getSiegeClanMinLevel()+ " and higher may register for a castle siege.");
        	return false;
        }
        else if(player.getClan().getHasCastle() > 0 || player.getClan().getHasFort() > 0)
		{
			player.sendMessage("You cannot register because your clan already own a castle/fort.");
			return false;
		}
        else if (player.getClan().getClanId() == getCastle().getOwnerId())
        {
        	player.sendPacket(new SystemMessage(SystemMessageId.CLAN_THAT_OWNS_CASTLE_IS_AUTOMATICALLY_REGISTERED_DEFENDING));
        	return false;
        }
		else
		{
	        for(int i=0; i<10; i++)
	        {
	            if (SiegeManager.getInstance().checkIsRegistered(player.getClan(), i))
	            {
	                player.sendMessage("You are already registered in a Siege.");
	                return false;
	            }
	        }
		}
		return true;
	}

    /**
     * Return the correct siege date as Calendar.<BR><BR>
     */
    private void correctSiegeDateTime()
    {
        boolean corrected = false;

        if (getCastle().getSiegeDate().getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
        {
            // Since siege has past reschedule it to the next one (14 days)
            // This is usually caused by server being down
            corrected = true;
            setNextSiegeDate();
        }

        if (getCastle().getSiegeDate().get(Calendar.DAY_OF_WEEK) != getCastle().getSiegeDayOfWeek())
        {
            corrected = true;
            getCastle().getSiegeDate().set(Calendar.DAY_OF_WEEK, getCastle().getSiegeDayOfWeek());
        }

        if (getCastle().getSiegeDate().get(Calendar.HOUR_OF_DAY) != getCastle().getSiegeHourOfDay())
        {
            corrected = true;
            getCastle().getSiegeDate().set(Calendar.HOUR_OF_DAY, getCastle().getSiegeHourOfDay());
        }

        getCastle().getSiegeDate().set(Calendar.MINUTE, 0);

        if (corrected) saveSiegeDate();
    }

    /** Load siege clans. */
    private void loadSiegeClan()
    {
        Connection con = null;
        try
        {
            getAttackerClans().clear();
            getDefenderClans().clear();
            getDefenderWaitingClans().clear();

            // Add castle owner as defender (add owner first so that they are on the top of the defender list)
            if (getCastle().getOwnerId() > 0)
                addDefender(getCastle().getOwnerId(), SiegeClanType.OWNER);

            PreparedStatement statement = null;
            ResultSet rs = null;

            con = L2DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("SELECT clan_id,type FROM siege_clans where castle_id=?");
            statement.setInt(1, getCastle().getCastleId());
            rs = statement.executeQuery();

            int typeId;
            while (rs.next())
            {
                typeId = rs.getInt("type");
                if (typeId == 0) addDefender(rs.getInt("clan_id"));
                else if (typeId == 1) addAttacker(rs.getInt("clan_id"));
                else if (typeId == 2) addDefenderWaiting(rs.getInt("clan_id"));
            }

            statement.close();
        }
        catch (Exception e)
        {
            System.out.println("Exception: loadSiegeClan(): " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
            }
        }
    }

    /** Remove artifacts spawned. */
    private void removeArtifact()
    {
        if (_artifacts != null)
        {
            // Remove all instance of artifact for this castle
            for (L2ArtefactInstance art : _artifacts)
            {
                if (art != null) art.decayMe();
            }
            _artifacts = null;
        }
    }

    /** Remove all control tower spawned. */
    private void removeControlTower()
    {
        if (_controlTowers != null)
        {
            // Remove all instance of control tower for this castle
            for (L2ControlTowerInstance ct : _controlTowers)
            {
                if (ct != null) ct.decayMe();
            }

            _controlTowers = null;
        }
    }

    /** Remove all flags. */
    private void removeFlags()
    {
        for (L2SiegeClan sc : getAttackerClans())
        {
            if (sc != null) sc.removeFlags();
        }
        for (L2SiegeClan sc : getDefenderClans())
        {
            if (sc != null) sc.removeFlags();
        }
    }

    /** Remove flags from defenders. */
    private void removeDefenderFlags()
    {
        for (L2SiegeClan sc : getDefenderClans())
        {
            if (sc != null) sc.removeFlags();
        }
    }

    /** Save castle siege related to database. */
    private void saveCastleSiege()
    {
        setNextSiegeDate(); // Set the next set date for 2 weeks from now
        saveSiegeDate(); // Save the new date
        startAutoTask(); // Prepare auto start siege and end registration
    }

    /** Save siege date to database. */
    private void saveSiegeDate()
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("Update castle set siegeDate = ? where id = ?");
            statement.setLong(1, getSiegeDate().getTimeInMillis());
            statement.setInt(2, getCastle().getCastleId());
            statement.execute();

            statement.close();
        }
        catch (Exception e)
        {
            System.out.println("Exception: saveSiegeDate(): " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * Save registration to database.<BR><BR>
     * @param clan The L2Clan of player
     * @param typeId -1 = owner 0 = defender, 1 = attacker, 2 = defender waiting
     */
    private void saveSiegeClan(L2Clan clan, int typeId, boolean isUpdateRegistration)
    {
        if (clan.getHasCastle() > 0) return;

        Connection con = null;
        try
        {
            if (typeId == 0 || typeId == 2 || typeId == -1)
            {
                if (getDefenderClans().size() + getDefenderWaitingClans().size() >= SiegeManager.getInstance().getDefenderMaxClans())
                    return;
            }
            else
            {
                if (getAttackerClans().size() >= SiegeManager.getInstance().getAttackerMaxClans())
                    return;
            }

            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement;
            if (!isUpdateRegistration)
            {
                statement = con.prepareStatement("INSERT INTO siege_clans (clan_id,castle_id,type,castle_owner) values (?,?,?,0)");
                statement.setInt(1, clan.getClanId());
                statement.setInt(2, getCastle().getCastleId());
                statement.setInt(3, typeId);
                statement.execute();
                statement.close();
            }
            else
            {
                statement = con.prepareStatement("Update siege_clans set type = ? where castle_id = ? and clan_id = ?");
                statement.setInt(1, typeId);
                statement.setInt(2, getCastle().getCastleId());
                statement.setInt(3, clan.getClanId());
                statement.execute();
                statement.close();
            }

            if (typeId == 0 || typeId == -1)
            {
                addDefender(clan.getClanId());
                announceToPlayer(clan.getName() + " has been registered to defend "
                    + getCastle().getName(), false);
            }
            else if (typeId == 1)
            {
                addAttacker(clan.getClanId());
                announceToPlayer(clan.getName() + " has been registered to attack "
                    + getCastle().getName(), false);
            }
            else if (typeId == 2)
            {
                addDefenderWaiting(clan.getClanId());
                announceToPlayer(clan.getName() + " has requested to defend " + getCastle().getName(),
                                 false);
            }
        }
        catch (Exception e)
        {
            System.out.println("Exception: saveSiegeClan(L2Clan clan, int typeId, boolean isUpdateRegistration): "
                + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
            }
        }
    }

    /** Set the date for the next siege. */
    private void setNextSiegeDate()
    {
        while (getCastle().getSiegeDate().getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
        {
            // Set next siege date if siege has passed
            getCastle().getSiegeDate().add(Calendar.DAY_OF_MONTH, 14); // Schedule to happen in 14 days
        }
        _isRegistrationOver = false; // Allow registration for next siege
    }

    /** Spawn artifact. */
    private void spawnArtifact(int Id)
    {
        //Set artefact array size if one does not exist
        if (_artifacts == null)
            _artifacts = new ArrayList<>();

        for (SiegeSpawn _sp: SiegeManager.getInstance().getArtefactSpawnList(Id))
        {
        	L2ArtefactInstance art;

        	art = new L2ArtefactInstance(IdFactory.getInstance().getNextId(), NpcTable.getInstance().getTemplate(_sp.getNpcId()));
        	art.setCurrentHpMp(art.getMaxHp(), art.getMaxMp());
        	art.setHeading(_sp.getLocation().getHeading());
        	art.spawnMe(_sp.getLocation().getX(),_sp.getLocation().getY(),_sp.getLocation().getZ() + 50);

        	_artifacts.add(art);
        }
    }

    /** Spawn control tower. */
    private void spawnControlTower(int Id)
    {
        //Set control tower array size if one does not exist
        if (_controlTowers == null)
        	_controlTowers = new ArrayList<>();

        for (SiegeSpawn _sp: SiegeManager.getInstance().getControlTowerSpawnList(Id))
        {
        	L2ControlTowerInstance ct;

        	L2NpcTemplate template = NpcTable.getInstance().getTemplate(_sp.getNpcId());

            template.getStatsSet().set("baseHpMax", _sp.getHp());
            // TODO: Check/confirm if control towers have any special weapon resistances/vulnerabilities
            // template.addVulnerability(Stats.BOW_WPN_VULN,0);
            // template.addVulnerability(Stats.BLUNT_WPN_VULN,0);
            // template.addVulnerability(Stats.DAGGER_WPN_VULN,0);

            ct = new L2ControlTowerInstance(IdFactory.getInstance().getNextId(), template);


            ct.setCurrentHpMp(ct.getMaxHp(), ct.getMaxMp());
            ct.spawnMe(_sp.getLocation().getX(),_sp.getLocation().getY(),_sp.getLocation().getZ() + 20);

            _controlTowers.add(ct);
        }
    }

    /**
     * Spawn siege guard.<BR><BR>
     */
    private void spawnSiegeGuard()
    {
        getSiegeGuardManager().spawnSiegeGuard();

        // Register guard to the closest Control Tower
        // When CT dies, so do all the guards that it controls
        if (getSiegeGuardManager().getSiegeGuardSpawn().size() > 0 && _controlTowers.size() > 0)
        {
            L2ControlTowerInstance closestCt;
            double distance, x, y, z;
            double distanceClosest = 0;
            for (L2Spawn spawn : getSiegeGuardManager().getSiegeGuardSpawn())
            {
                if (spawn == null) continue;
                closestCt = null;
                distanceClosest = 0;
                for (L2ControlTowerInstance ct : _controlTowers)
                {
                    if (ct == null) continue;
                    x = (spawn.getLocx() - ct.getX());
                    y = (spawn.getLocy() - ct.getY());
                    z = (spawn.getLocz() - ct.getZ());

                    distance = (x * x) + (y * y) + (z * z);

                    if (closestCt == null || distance < distanceClosest)
                    {
                        closestCt = ct;
                        distanceClosest = distance;
                    }
                }

                if (closestCt != null) closestCt.registerGuard(spawn);
            }
        }
    }

    public final L2SiegeClan getAttackerClan(L2Clan clan)
    {
        if (clan == null) return null;
        return getAttackerClan(clan.getClanId());
    }

    public final L2SiegeClan getAttackerClan(int clanId)
    {
        for (L2SiegeClan sc : getAttackerClans())
            if (sc != null && sc.getClanId() == clanId) return sc;
        return null;
    }

    public final List<L2SiegeClan> getAttackerClans()
    {
        if (_isNormalSide) return _attackerClans;
        return _defenderClans;
    }

    public final int getAttackerRespawnDelay()
    {
        return (SiegeManager.getInstance().getAttackerRespawnDelay());
    }

    public final Castle getCastle()
    {
        if (_castle == null || _castle.length <= 0) return null;
        return _castle[0];
    }

    public final L2SiegeClan getDefenderClan(L2Clan clan)
    {
        if (clan == null) return null;
        return getDefenderClan(clan.getClanId());
    }

    public final L2SiegeClan getDefenderClan(int clanId)
    {
        for (L2SiegeClan sc : getDefenderClans())
            if (sc != null && sc.getClanId() == clanId) return sc;
        return null;
    }

    public final List<L2SiegeClan> getDefenderClans()
    {
        if (_isNormalSide) return _defenderClans;
        return _attackerClans;
    }

    public final L2SiegeClan getDefenderWaitingClan(L2Clan clan)
    {
        if (clan == null) return null;
        return getDefenderWaitingClan(clan.getClanId());
    }

    public final L2SiegeClan getDefenderWaitingClan(int clanId)
    {
        for (L2SiegeClan sc : getDefenderWaitingClans())
            if (sc != null && sc.getClanId() == clanId) return sc;
        return null;
    }

    public final List<L2SiegeClan> getDefenderWaitingClans()
    {
        return _defenderWaitingClans;
    }

    public final int getDefenderRespawnDelay()
    {
        return (SiegeManager.getInstance().getDefenderRespawnDelay() + _defenderRespawnDelayPenalty);
    }

    public final boolean getIsInProgress()
    {
        return _isInProgress;
    }

    public final boolean getIsRegistrationOver()
    {
        return _isRegistrationOver;
    }

    public final Calendar getSiegeDate()
    {
        return getCastle().getSiegeDate();
    }

    public List<L2NpcInstance> getFlag(L2Clan clan)
    {
        if (clan != null)
        {
            L2SiegeClan sc = getAttackerClan(clan);
            if (sc != null) return sc.getFlag();
        }
        return null;
    }

    public final SiegeGuardManager getSiegeGuardManager()
    {
        if (_siegeGuardManager == null)
        {
            _siegeGuardManager = new SiegeGuardManager(getCastle());
        }
        return _siegeGuardManager;
    }
}