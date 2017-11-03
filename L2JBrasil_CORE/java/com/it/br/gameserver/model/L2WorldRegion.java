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
package com.it.br.gameserver.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.ai.L2AttackableAI;
import com.it.br.gameserver.datatables.sql.SpawnTable;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.model.zone.L2ZoneManager;
import com.it.br.gameserver.model.zone.L2ZoneType;
import com.it.br.util.L2ObjectSet;


/**
 * This class ...
 *
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/27 15:29:33 $
 */
public final class L2WorldRegion
{
    private static Logger _log = Logger.getLogger(L2WorldRegion.class.getName());

    /** L2ObjectHashSet(L2PlayableInstance) containing L2PlayableInstance of all player & summon in game in this L2WorldRegion */
    private L2ObjectSet<L2PlayableInstance> _allPlayable;

    /** L2ObjectHashSet(L2Object) containing L2Object visible in this L2WorldRegion */
    private L2ObjectSet<L2Object> _visibleObjects;

    private List<L2WorldRegion> _surroundingRegions;
    private int _tileX, _tileY;
    private Boolean _active = false;
    @SuppressWarnings("rawtypes")
	private ScheduledFuture _neighborsTask = null;

    private L2ZoneManager _zoneManager;

    public L2WorldRegion(int pTileX, int pTileY)
    {
        _allPlayable = L2ObjectSet.createL2PlayerSet(); //new L2ObjectHashSet<L2PcInstance>();
        _visibleObjects = L2ObjectSet.createL2ObjectSet(); // new L2ObjectHashSet<L2Object>();
        _surroundingRegions = new ArrayList<>();
        //_surroundingRegions.add(this); //done in L2World.initRegions()

        _tileX = pTileX;
        _tileY = pTileY;

        // default a newly initialized region to inactive, unless always on is specified
        if (Config.GRIDS_ALWAYS_ON)
            _active = true;
        else
            _active = false;
    }

    public void addZone(L2ZoneType zone)
    {
    	if (_zoneManager == null)
    	{
    		_zoneManager = new L2ZoneManager();
    	}
    	_zoneManager.registerNewZone(zone);
    }

    public void removeZone(L2ZoneType zone)
    {
    	if (_zoneManager == null)
    		return;
    	_zoneManager.unregisterZone(zone);
    }

    public void revalidateZones(L2Character character)
    {
    	if (_zoneManager == null) return;

    	if (_zoneManager != null)
    	{
    		_zoneManager.revalidateZones(character);
    	}
    }

    public void removeFromZones(L2Character character)
    {
    	if (_zoneManager == null) return;

    	if (_zoneManager != null)
    	{
    		_zoneManager.removeCharacter(character);
    	}
    }

    public void onDeath(L2Character character)
    {
    	if (_zoneManager == null) return;

    	if (_zoneManager != null)
    	{
    		_zoneManager.onDeath(character);
    	}
    }

    public void onRevive(L2Character character)
    {
    	if (_zoneManager == null) return;

    	if (_zoneManager != null)
    	{
    		_zoneManager.onRevive(character);
    	}
    }

    /** Task of AI notification */
    public class NeighborsTask implements Runnable
    {
        private boolean _isActivating;

        public NeighborsTask(boolean isActivating)
        {
            _isActivating = isActivating;
        }


		public void run()
        {
            if (_isActivating)
            {
                // for each neighbor, if it's not active, activate.
                for (L2WorldRegion neighbor: getSurroundingRegions())
                    neighbor.setActive(true);
            }
            else
            {
                if(areNeighborsEmpty())
                    setActive(false);

                // check and deactivate
                for (L2WorldRegion neighbor: getSurroundingRegions())
                    if(neighbor.areNeighborsEmpty())
                        neighbor.setActive(false);
            }
        }
    }

    private void switchAI(Boolean isOn)
    {
        int c = 0;
        if (!isOn)
        {
            for(L2Object o: _visibleObjects)
            {
                if (o instanceof L2Attackable)
                {
                    c++;
                    L2Attackable mob = (L2Attackable)o;

                    // Set target to null and cancel Attack or Cast
                    mob.setTarget(null);

                    // Stop movement
                    mob.stopMove(null);

                    // Stop all active skills effects in progress on the L2Character
                    mob.stopAllEffects();

                    mob.clearAggroList();
                    mob.getKnownList().removeAllKnownObjects();

                    mob.getAI().setIntention(com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE);

                    // stop the ai tasks
                    ((L2AttackableAI) mob.getAI()).stopAITask();

                    // Stop HP/MP/CP Regeneration task
                    // try this: allow regen, but only until mob is 100% full...then stop
                    // it until the grid is made active.
                    //mob.getStatus().stopHpMpRegeneration();
                }
            }
            _log.fine(c+ " mobs were turned off");
        }
        else
        {
            for(L2Object o: _visibleObjects)
            {
            	if (o instanceof L2Attackable)
                {
                    c++;
                    // Start HP/MP/CP Regeneration task
                    ((L2Attackable)o).getStatus().startHpMpRegeneration();

                    // start the ai
                    //((L2AttackableAI) mob.getAI()).startAITask();
                }
            	else if (o instanceof L2NpcInstance)
                {
                    // Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it
                    // L2Monsterinstance/L2Attackable socials are handled by AI (TODO: check the instances)
            		((L2NpcInstance)o).startRandomAnimationTimer();
                }
            }
            _log.fine(c+ " mobs were turned on");
        }

    }

    public Boolean isActive()
    {
        return _active;
    }

    // check if all 9 neighbors (including self) are inactive or active but with no players.
    // returns true if the above condition is met.
    public Boolean areNeighborsEmpty()
    {
        // if this region is occupied, return false.
        if (isActive() && (_allPlayable.size() > 0 ))
            return false;

        // if any one of the neighbors is occupied, return false
        for (L2WorldRegion neighbor: _surroundingRegions)
            if (neighbor.isActive() && (neighbor._allPlayable.size() > 0))
                return false;

        // in all other cases, return true.
        return true;
    }

    /**
     * this function turns this region's AI and geodata on or off
     * @param value
     */
    public void setActive(boolean value)
    {
        if (_active == value)
            return;

        _active = value;

        // turn the AI on or off to match the region's activation.
        switchAI(value);

        // TODO
        // turn the geodata on or off to match the region's activation.
        if(value)
            _log.fine("Starting Grid " + _tileX + ","+ _tileY);
        else
            _log.fine("Stoping Grid " + _tileX + ","+ _tileY);
    }

    /** Immediately sets self as active and starts a timer to set neighbors as active
     * this timer is to avoid turning on neighbors in the case when a person just
     * teleported into a region and then teleported out immediately...there is no
     * reason to activate all the neighbors in that case.
     */
    private void startActivation()
    {
        // first set self to active and do self-tasks...
        setActive(true);

        // if the timer to deactivate neighbors is running, cancel it.
        if(_neighborsTask !=null)
        {
            _neighborsTask.cancel(true);
            _neighborsTask = null;
        }

        // then, set a timer to activate the neighbors
        _neighborsTask = ThreadPoolManager.getInstance().scheduleGeneral(new NeighborsTask(true), 1000*Config.GRID_NEIGHBOR_TURNON_TIME);
    }

    /** starts a timer to set neighbors (including self) as inactive
     * this timer is to avoid turning off neighbors in the case when a person just
     * moved out of a region that he may very soon return to.  There is no reason
     * to turn self & neighbors off in that case.
     */
    private void startDeactivation()
    {
        // if the timer to activate neighbors is running, cancel it.
        if(_neighborsTask !=null)
        {
            _neighborsTask.cancel(true);
            _neighborsTask = null;
        }

        // start a timer to "suggest" a deactivate to self and neighbors.
        // suggest means: first check if a neighbor has L2PcInstances in it.  If not, deactivate.
        _neighborsTask = ThreadPoolManager.getInstance().scheduleGeneral(new NeighborsTask(false), 1000*Config.GRID_NEIGHBOR_TURNOFF_TIME);
    }

    /**
     * Add the L2Object in the L2ObjectHashSet(L2Object) _visibleObjects containing L2Object visible in this L2WorldRegion <BR>
     * If L2Object is a L2PcInstance, Add the L2PcInstance in the L2ObjectHashSet(L2PcInstance) _allPlayable
     * containing L2PcInstance of all player in game in this L2WorldRegion <BR>
     * Assert : object.getCurrentWorldRegion() == this
     */
    public void addVisibleObject(L2Object object)
    {
        if (Config.ASSERT) assert object.getWorldRegion() == this;
        
        if (object == null) return;
        _visibleObjects.put(object);

        if (object instanceof L2PlayableInstance)
        {
            _allPlayable.put((L2PlayableInstance) object);

            // if this is the first player to enter the region, activate self & neighbors
            if ((_allPlayable.size() == 1) && (!Config.GRIDS_ALWAYS_ON))
                startActivation();
        }
    }

    /**
     * Remove the L2Object from the L2ObjectHashSet(L2Object) _visibleObjects in this L2WorldRegion <BR><BR>
     *
     * If L2Object is a L2PcInstance, remove it from the L2ObjectHashSet(L2PcInstance) _allPlayable of this L2WorldRegion <BR>
     * Assert : object.getCurrentWorldRegion() == this || object.getCurrentWorldRegion() == null
     */
    public void removeVisibleObject(L2Object object)
    {
        if (Config.ASSERT) assert object.getWorldRegion() == this || object.getWorldRegion() == null;

        if (object == null) return;
        _visibleObjects.remove(object);

        if (object instanceof L2PlayableInstance)
        {
            _allPlayable.remove((L2PlayableInstance) object);

            if ((_allPlayable.size() == 0 ) && (!Config.GRIDS_ALWAYS_ON))
                startDeactivation();
        }
    }

    public void addSurroundingRegion(L2WorldRegion region)
    {
        _surroundingRegions.add(region);
    }

    /**
     * Return the FastList _surroundingRegions containing all L2WorldRegion around the current L2WorldRegion
     */
    public List<L2WorldRegion> getSurroundingRegions()
    {
        //change to return L2WorldRegion[] ?
        //this should not change after initialization, so maybe changes are not necessary

        return _surroundingRegions;
    }

    public Iterator<L2PlayableInstance> iterateAllPlayers()
    {
        return _allPlayable.iterator();
    }

    public L2ObjectSet<L2Object> getVisibleObjects()
    {
        return _visibleObjects;
    }

    public String getName()
    {
        return "(" + _tileX + ", " + _tileY + ")";
    }

    /**
     * Deleted all spawns in the world.
     */
    public synchronized void deleteVisibleNpcSpawns()
    {
        _log.fine("Deleting all visible NPC's in Region: " + getName());
        for (L2Object obj : _visibleObjects)
        {
            if (obj instanceof L2NpcInstance)
            {
                L2NpcInstance target = (L2NpcInstance) obj;
                target.deleteMe();
                L2Spawn spawn = target.getSpawn();
                if (spawn != null)
                {
                    spawn.stopRespawn();
                    SpawnTable.getInstance().deleteSpawn(spawn, false);
                }
                _log.finest("Removed NPC " + target.getObjectId());
            }
        }
        _log.info("All visible NPC's deleted in Region: " + getName());
    }
}
