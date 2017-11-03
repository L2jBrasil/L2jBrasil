/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.it.br.gameserver.model.actor.instance;

import com.it.br.Config;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.instancemanager.GrandBossManager;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.templates.L2NpcTemplate;

public final class L2GrandBossInstance extends L2MonsterInstance
{
    private static final int BOSS_MAINTENANCE_INTERVAL = 10000;

	public L2GrandBossInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

    @Override
	protected int getMaintenanceInterval() 
    { 
        return BOSS_MAINTENANCE_INTERVAL; 
    }

    @Override
	public void onSpawn()
    {
    	super.onSpawn();
        GrandBossManager.getInstance().addBoss(this);
    }

    @Override
	protected void manageMinions()
	{
		_minionList.spawnMinions();
		_minionMaintainTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				// Teleport raid boss home if it's too far from home location
				L2Spawn bossSpawn = getSpawn();
				
				int rb_lock_range = Config.RBLOCKRAGE;
				if(Config.RBS_SPECIFIC_LOCK_RAGE.get(bossSpawn.getNpcid())!=null)
				{
					rb_lock_range = Config.RBS_SPECIFIC_LOCK_RAGE.get(bossSpawn.getNpcid());
				}
				
				if(rb_lock_range >= 100 && !isInsideRadius(bossSpawn.getLocx(), bossSpawn.getLocy(), bossSpawn.getLocz(), rb_lock_range, true, false))
				{
					teleToLocation(bossSpawn.getLocx(), bossSpawn.getLocy(), bossSpawn.getLocz(), true);
					//healFull(); // Prevents minor exploiting with it
				}
				_minionList.maintainMinions();
			}
		}, 60000, getMaintenanceInterval());
	}

    @Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake)
    {
        super.reduceCurrentHp(damage, attacker, awake);
    }

    @Override
	public boolean isRaid()
    {
        return true;
    }

    protected boolean _isInSocialAction = false;

    public boolean IsInSocialAction()
    {
        return _isInSocialAction;
    }

    public void setIsInSocialAction(boolean value)
    {
        _isInSocialAction = value;
    }
}
