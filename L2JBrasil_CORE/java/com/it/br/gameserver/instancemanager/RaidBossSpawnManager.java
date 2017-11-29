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
package com.it.br.gameserver.instancemanager;


import static com.it.br.configuration.Configurator.getSettings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.GmListTable;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.datatables.sql.SpawnTable;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.actor.instance.L2RaidBossInstance;
import com.it.br.gameserver.skills.Stats;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.templates.StatsSet;
import com.it.br.util.Rnd;

public class RaidBossSpawnManager 
{

    private static Logger _log = Logger.getLogger(RaidBossSpawnManager.class.getName());

    private static RaidBossSpawnManager _instance;
    protected static Map<Integer, L2RaidBossInstance> _bosses;
    protected static Map<Integer, L2Spawn> _spawns;
    protected static Map<Integer, StatsSet> _storedInfo;
    @SuppressWarnings("rawtypes")
    protected static Map<Integer, ScheduledFuture> _schedules;

    public static enum StatusEnum 
    {
        ALIVE,
        DEAD,
        UNDEFINED
    }

    public RaidBossSpawnManager()
    {
        init();
    }

    public static RaidBossSpawnManager getInstance()
    {
        if (_instance == null)
            _instance = new RaidBossSpawnManager();

        return _instance;
    }

    @SuppressWarnings("rawtypes")
    private void init()
    {
        _bosses = new HashMap<>();
        _schedules = new HashMap<>();
        _storedInfo = new HashMap<>();
        _spawns = new HashMap<>();

        Connection con = null;

        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();

            PreparedStatement statement = con.prepareStatement("SELECT * from raidboss_spawnlist ORDER BY boss_id");
            ResultSet rset = statement.executeQuery();

            L2Spawn spawnDat;
            L2NpcTemplate template;
            long respawnTime;
            while (rset.next())
            {
                template = getValidTemplate(rset.getInt("boss_id"));
                if (template != null)
                {
                    spawnDat = new L2Spawn(template);
                    spawnDat.setLocx(rset.getInt("loc_x"));
                    spawnDat.setLocy(rset.getInt("loc_y"));
                    spawnDat.setLocz(rset.getInt("loc_z"));
                    spawnDat.setAmount(rset.getInt("amount"));
                    spawnDat.setHeading(rset.getInt("heading"));
                    spawnDat.setRespawnMinDelay(rset.getInt("respawn_min_delay"));
                    spawnDat.setRespawnMaxDelay(rset.getInt("respawn_max_delay"));
                    respawnTime = rset.getLong("respawn_time");

                    addNewSpawn(spawnDat, respawnTime, rset.getDouble("currentHP"), rset.getDouble("currentMP"), false);
                }
                else
                {
                    _log.warning("RaidBossSpawnManager: Could not load raidboss #" + rset.getInt("boss_id") + " from DB");
                }
            }

            _log.info("RaidBossSpawnManager: Loaded " + _bosses.size() + " Instances");
            _log.info("RaidBossSpawnManager: Scheduled " + _schedules.size() + " Instances");

            rset.close();
            statement.close();
        }
        catch (SQLException e)
        {
            _log.warning("RaidBossSpawnManager: Couldnt load raidboss_spawnlist table");
        }
        catch (Exception e) {e.printStackTrace();}
        finally
        {
            try {con.close();} catch(Exception e) {e.printStackTrace();}
        }
    }

    private class spawnSchedule implements Runnable
    {
        private int bossId;

        public spawnSchedule(int npcId)
        {
        	bossId = npcId;
        }


		public void run()
        {
            L2RaidBossInstance raidboss = null;

            if (bossId == 25328)
                raidboss = DayNightSpawnManager.getInstance().handleBoss(_spawns.get(bossId));
            else
                raidboss = (L2RaidBossInstance)_spawns.get(bossId).doSpawn();

            if (raidboss != null)
            {
                raidboss.setRaidStatus(StatusEnum.ALIVE);

                StatsSet info = new StatsSet();
                info.set("currentHP", raidboss.getCurrentHp());
                info.set("currentMP", raidboss.getCurrentMp());
                info.set("respawnTime", 60000L);

                _storedInfo.put(bossId, info);

                GmListTable.broadcastMessageToGMs("Spawning Raid Boss " + raidboss.getName());
                if(getSettings(L2JBrasilSettings.class).isAnnounceSpawnRaidEnabled()) {
					Announcements.announceToPlayers("Raid boss " + raidboss.getName() + " spawned in world.");
				}
                _bosses.put(bossId, raidboss);
            }

            _schedules.remove(bossId);
        }
    }

    @SuppressWarnings("rawtypes")
    public void updateStatus(L2RaidBossInstance boss, boolean isBossDead)
    {
        if (!_storedInfo.containsKey(boss.getNpcId()))
            return;

        StatsSet info = _storedInfo.get(boss.getNpcId());

        if (isBossDead)
        {
            boss.setRaidStatus(StatusEnum.DEAD);
            
            long respawnTime;
            int RespawnMinDelay = boss.getSpawn().getRespawnMinDelay();
            int RespawnMaxDelay = boss.getSpawn().getRespawnMaxDelay();
            long respawn_delay = Rnd.get((int)(RespawnMinDelay*1000*Config.RAID_MIN_RESPAWN_MULTIPLIER),(int)(RespawnMaxDelay*1000*Config.RAID_MAX_RESPAWN_MULTIPLIER));
            respawnTime = Calendar.getInstance().getTimeInMillis() + respawn_delay;

            info.set("currentHP", boss.getMaxHp());
            info.set("currentMP", boss.getMaxMp());
            info.set("respawnTime", respawnTime);

            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(respawnTime);
            _log.info("RaidBossSpawnManager: Updated " + boss.getName() + " respawn time to " + time.getTime());

            ScheduledFuture futureSpawn;
            futureSpawn = ThreadPoolManager.getInstance().scheduleGeneral(new spawnSchedule(boss.getNpcId()), respawn_delay);

            _schedules.put(boss.getNpcId(), futureSpawn);
            //To update immediately Database uncomment on the following line, to post the hour of respawn raid boss on your site for example or to envisage a crash landing of the waiter.
            if (Config.FORCE_UPDATE_RAIDBOSS_ON_DB)
            updateDb();
        }
        else
        {
            boss.setRaidStatus(StatusEnum.ALIVE);

            info.set("currentHP", boss.getCurrentHp());
            info.set("currentMP", boss.getCurrentMp());
            info.set("respawnTime", 60000L);
        }

        _storedInfo.remove(boss.getNpcId());
        _storedInfo.put(boss.getNpcId(), info);
    }

    @SuppressWarnings("rawtypes")
    public void addNewSpawn(L2Spawn spawnDat, long respawnTime, double currentHP, double currentMP, boolean storeInDb)
    {
    	if (spawnDat == null) return;
    	if (_spawns.containsKey(spawnDat.getNpcid())) return;

    	int bossId = spawnDat.getNpcid();
    	long time = Calendar.getInstance().getTimeInMillis();

        SpawnTable.getInstance().addNewSpawn(spawnDat, false);

        if (respawnTime == 0L || (time > respawnTime))
        {
            L2RaidBossInstance raidboss = null;

            if (bossId == 25328)
                raidboss = DayNightSpawnManager.getInstance().handleBoss(spawnDat);
            else
                raidboss = (L2RaidBossInstance)spawnDat.doSpawn();

            if (raidboss != null)
            {
				double bonus = raidboss.getStat().calcStat(Stats.MAX_HP, 1, raidboss, null);
							
				if(Config.DEBUG)
				{		
				System.out.println(" bossId: "+bossId );			
				System.out.println(" 	maxHp: "+raidboss.getMaxHp() );			
				System.out.println(" 	currHp: "+(int)currentHP );				
				System.out.println(" 	bonusHp: "+bonus);				
				System.out.println(" 	calculatedHp: "+(int)(bonus*currentHP));			
				}							
				//if new spawn, the currentHp is equal to maxHP/bonus, so set it to max			
				if((int)(bonus*currentHP)==raidboss.getMaxHp())
				{	
				currentHP = (raidboss.getMaxHp());				
				}
                raidboss.setCurrentHp(currentHP);
                raidboss.setCurrentMp(currentMP);
                raidboss.setRaidStatus(StatusEnum.ALIVE);

                _bosses.put(bossId, raidboss);

                StatsSet info = new StatsSet();
                info.set("currentHP", currentHP);
                info.set("currentMP", currentMP);
                info.set("respawnTime", 60000L);

                _storedInfo.put(bossId, info);
            }
        }
        else
        {
            ScheduledFuture futureSpawn;
            long spawnTime = respawnTime - Calendar.getInstance().getTimeInMillis();

            futureSpawn = ThreadPoolManager.getInstance().scheduleGeneral(new spawnSchedule(bossId), spawnTime);

            _schedules.put(bossId, futureSpawn);
        }

        _spawns.put(bossId, spawnDat);

        if (storeInDb)
        {
            Connection con = null;

            try
            {
                con = L2DatabaseFactory.getInstance().getConnection();
                PreparedStatement statement = con.prepareStatement("INSERT INTO raidboss_spawnlist (boss_id,amount,loc_x,loc_y,loc_z,heading,respawn_time,currentHp,currentMp) values(?,?,?,?,?,?,?,?,?)");
                statement.setInt(1, spawnDat.getNpcid());
                statement.setInt(2, spawnDat.getAmount());
                statement.setInt(3, spawnDat.getLocx());
                statement.setInt(4, spawnDat.getLocy());
                statement.setInt(5, spawnDat.getLocz());
                statement.setInt(6, spawnDat.getHeading());
                statement.setLong(7, respawnTime);
                statement.setDouble(8, currentHP);
                statement.setDouble(9, currentMP);
                statement.execute();
                statement.close();
            }
            catch (Exception e)
            {
                // problem with storing spawn
                _log.warning("RaidBossSpawnManager: Could not store raidboss #" + bossId + " in the DB:" + e);
            }
            finally
            {
                try { con.close(); } catch (Exception e) {}
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void deleteSpawn(L2Spawn spawnDat, boolean updateDb)
    {
    	if (spawnDat == null) return;
    	if (!_spawns.containsKey(spawnDat.getNpcid())) return;

    	int bossId = spawnDat.getNpcid();

    	SpawnTable.getInstance().deleteSpawn(spawnDat, false);
        _spawns.remove(bossId);

        if (_bosses.containsKey(bossId))
        	_bosses.remove(bossId);

        if (_schedules.containsKey(bossId))
        {
           	ScheduledFuture f = _schedules.get(bossId);
           	f.cancel(true);
           	_schedules.remove(bossId);
        }

        if (_storedInfo.containsKey(bossId))
        	_storedInfo.remove(bossId);

        if (updateDb)
        {
            Connection con = null;

            try
            {
                con = L2DatabaseFactory.getInstance().getConnection();
                PreparedStatement statement = con.prepareStatement("DELETE FROM raidboss_spawnlist WHERE boss_id=?");
                statement.setInt(1, bossId);
                statement.execute();
                statement.close();
            }
            catch (Exception e)
            {
                // problem with deleting spawn
                _log.warning("RaidBossSpawnManager: Could not remove raidboss #" + bossId + " from DB: " + e);
            }
            finally
            {
                try { con.close(); } catch (Exception e) {}
            }
        }
    }

    private void updateDb()
    {
        for (Integer bossId : _storedInfo.keySet())
        {
            Connection con = null;

            try
            {
                con = L2DatabaseFactory.getInstance().getConnection();

                L2RaidBossInstance boss = _bosses.get(bossId);

                if (boss == null) continue;

                if (boss.getRaidStatus().equals(StatusEnum.ALIVE))
                    updateStatus(boss, false);

                StatsSet info = _storedInfo.get(bossId);

                if (info == null) continue;

                PreparedStatement statement = con.prepareStatement("UPDATE raidboss_spawnlist set respawn_time = ?, currentHP = ?, currentMP = ? where boss_id = ?");
                statement.setLong(1, info.getLong("respawnTime"));
                statement.setDouble(2, info.getDouble("currentHP"));
                statement.setDouble(3, info.getDouble("currentMP"));
                statement.setInt(4, bossId);
                statement.execute();

                statement.close();
            }
            catch (SQLException e){ _log.warning("RaidBossSpawnManager: Couldnt update raidboss_spawnlist table");}
            finally
            {
                try {con.close();} catch(Exception e) {e.printStackTrace();}
            }
        }
    }

    public String[] getAllRaidBossStatus()
    {
        String[] msg = new String[_bosses == null ? 0 : _bosses.size()];

        if (_bosses == null)
        {
            msg[0] = "None";
            return msg;
        }

        int index = 0;

        for (int i : _bosses.keySet())
        {
            L2RaidBossInstance boss = _bosses.get(i);

            msg[index] = boss.getName() + ": " + boss.getRaidStatus().name();
            index++;
        }

        return msg;
    }

    public String getRaidBossStatus(int bossId)
    {
        String msg = "RaidBoss Status....\n";

        if (_bosses == null)
        {
            msg += "None";
            return msg;
        }

        if (_bosses.containsKey(bossId))
        {
            L2RaidBossInstance boss = _bosses.get(bossId);

            msg += boss.getName() + ": " + boss.getRaidStatus().name();
        }

        return msg;
    }

    public StatusEnum getRaidBossStatusId(int bossId)
    {
        if (_bosses.containsKey(bossId))
        	return _bosses.get(bossId).getRaidStatus();
        else
        	if (_schedules.containsKey(bossId))
        		return StatusEnum.DEAD;
            else
        		return StatusEnum.UNDEFINED;
    }

    public L2NpcTemplate getValidTemplate(int bossId)
    {
        L2NpcTemplate template = NpcTable.getInstance().getTemplate(bossId);
        if (template == null) return null;
        if (!template.type.equalsIgnoreCase("L2RaidBoss")) return null;
        return template;
    }

    public void notifySpawnNightBoss(L2RaidBossInstance raidboss)
    {
        StatsSet info = new StatsSet();
        info.set("currentHP", raidboss.getCurrentHp());
        info.set("currentMP", raidboss.getCurrentMp());
        info.set("respawnTime", 60000L);

        raidboss.setRaidStatus(StatusEnum.ALIVE);

        _storedInfo.put(raidboss.getNpcId(), info);

        GmListTable.broadcastMessageToGMs("Spawning Raid Boss " + raidboss.getName());

        _bosses.put(raidboss.getNpcId(), raidboss);
        
	        if (raidboss.getNpcId() == 25325) 
	        { 
	           Announcements _an = Announcements.getInstance(); 
	           _an.announceToAll("Flame of Splendor Barakiel is Now ALIVE!"); 
	        }  
    }

    public boolean isDefined(int bossId)
    {
    	return _spawns.containsKey(bossId);
    }

    public Map<Integer, L2RaidBossInstance> getBosses()
    {
        return _bosses;
    }

    public Map<Integer, L2Spawn> getSpawns()
    {
        return _spawns;
    }

    public void reloadBosses()
    {
        init();
    }

    /**
     * Saves all raidboss status and then clears all info from memory,
     * including all schedules.
     */

    @SuppressWarnings("rawtypes")
    public void cleanUp()
    {
        updateDb();

        _bosses.clear();

        if (_schedules != null)
        {
            for (Integer bossId : _schedules.keySet())
            {
            	ScheduledFuture f = _schedules.get(bossId);
                f.cancel(true);
            }
        }

        _schedules.clear();
        _storedInfo.clear();
        _spawns.clear();
    }
}