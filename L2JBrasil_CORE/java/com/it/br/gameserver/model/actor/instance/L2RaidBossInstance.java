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
package com.it.br.gameserver.model.actor.instance;

import com.it.br.Config;
import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.instancemanager.RaidBossSpawnManager;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.Earthquake;
import com.it.br.gameserver.network.serverpackets.ExRedSky;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.util.Broadcast;

/**
 * This class manages all RaidBoss.
 * In a group mob, there are one master called RaidBoss and several slaves called Minions.
 *
 * @version $Revision: 1.20.4.6 $ $Date: 2005/04/06 16:13:39 $
 */
public final class L2RaidBossInstance extends L2MonsterInstance
{
	private static final int RAIDBOSS_MAINTENANCE_INTERVAL = 30000; // 30 sec

	private RaidBossSpawnManager.StatusEnum _raidStatus;

	public L2RaidBossInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

    @Override
	public boolean isRaid()
    {
        return true;
    }

    @Override
	protected int getMaintenanceInterval() 
    { 
        return RAIDBOSS_MAINTENANCE_INTERVAL; 
    }

    @Override
	public boolean doDie(L2Character killer)
    {
    	if (!super.doDie(killer))
    		return false;
    	if (killer instanceof L2PlayableInstance)
        {
    		// RedSky and Earthquake and Announcement
    		if(Config.EFFECT_KILLED_GRAND_BOSS)
    		{
    			Broadcast.toAllOnlinePlayers(new ExRedSky(10));
    			Broadcast.toAllOnlinePlayers(new Earthquake(killer.getX(), killer.getY(), killer.getZ(), 14, 3));
    			Announcements.announceToPlayers(" " + super.getName() + " has been killed.");
    			broadcastPacket(new SystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
    		}
    		else
    			broadcastPacket(new SystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
        }

        RaidBossSpawnManager.getInstance().updateStatus(this, true);
        return true;
    }

    /**
     * Spawn all minions at a regular interval
     * Also if boss is too far from home location at the time of this check, teleport it home
     *
     */
    @Override
 	protected void manageMinions()
 	{
 		_minionList.spawnMinions();
 		_minionMaintainTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable()
 		{
 			public void run()
 			{
 				// teleport raid boss home if it's too far from home location
 				L2Spawn bossSpawn = getSpawn();

 				int rb_lock_range = Config.RBLOCKRAGE;
 				if(Config.RBS_SPECIFIC_LOCK_RAGE.get(bossSpawn.getNpcid())!=null)
 				{
 					rb_lock_range = Config.RBS_SPECIFIC_LOCK_RAGE.get(bossSpawn.getNpcid());
 				}

 				if(rb_lock_range!=-1 && !isInsideRadius(bossSpawn.getLocx(), bossSpawn.getLocy(), bossSpawn.getLocz(), rb_lock_range, true, false))
 				{
 					teleToLocation(bossSpawn.getLocx(), bossSpawn.getLocy(), bossSpawn.getLocz(), true);
 					healFull(); // Prevents minor exploiting with it
 				}
 				/*
 				if(!isInsideRadius(bossSpawn.getLocx(), bossSpawn.getLocy(), bossSpawn.getLocz(), 5000, true, false))
 				{
 					teleToLocation(bossSpawn.getLocx(), bossSpawn.getLocy(), bossSpawn.getLocz(), true);
 					healFull(); // prevents minor exploiting with it
 				}
 				*/
 				_minionList.maintainMinions();
 				bossSpawn = null;
 			}
 		}, 60000, getMaintenanceInterval());
 	}

    public void setRaidStatus (RaidBossSpawnManager.StatusEnum status)
    {
        _raidStatus = status;
    }

    public RaidBossSpawnManager.StatusEnum getRaidStatus()
    {
        return _raidStatus;
    }

    /**
     * Reduce the current HP of the L2Attackable, update its _aggroList and launch the doDie Task if necessary.<BR><BR>
     *
     */

    @Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake)
    {
        super.reduceCurrentHp(damage, attacker, awake);
    }

    public void healFull()
    {
        super.setCurrentHp(super.getMaxHp());
        super.setCurrentMp(super.getMaxMp());
    }
}
