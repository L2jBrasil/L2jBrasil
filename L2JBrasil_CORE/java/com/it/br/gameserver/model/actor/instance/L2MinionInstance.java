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

import com.it.br.gameserver.ai.L2AttackableAI;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.L2WorldRegion;
import com.it.br.gameserver.templates.L2NpcTemplate;

/**
 * This class manages all Minions.
 * In a group mob, there are one master called RaidBoss and several slaves called Minions.
 *
 * @version $Revision: 1.20.4.6 $ $Date: 2005/04/06 16:13:39 $
 */
public final class L2MinionInstance extends L2MonsterInstance
{
	//private static Logger _log = LoggerFactory.getLogger(L2RaidMinionInstance.class);

	/** The master L2Character whose depends this L2MinionInstance on */
	private L2MonsterInstance _master;

	/**
	 * Constructor of L2MinionInstance (use L2Character and L2NpcInstance constructor).<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Call the L2Character constructor to set the _template of the L2MinionInstance (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR) </li>
	 * <li>Set the name of the L2MinionInstance</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it </li><BR><BR>
	 *
	 * @param objectId Identifier of the object to initialized
	 * @param L2NpcTemplate Template to apply to the NPC
	 */
	public L2MinionInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	/** Return True if the L2Character is minion of RaidBoss. */

	@Override
	public boolean isRaid()
	{
		return (getLeader() instanceof L2RaidBossInstance);
	}

	/**
	 * Return the master of this L2MinionInstance.<BR><BR>
	 */
	public L2MonsterInstance getLeader()
	{
		return _master;
	}


	@Override
	public void onSpawn()
	{
		setIsNoRndWalk(true);

		if (getLeader() != null)
 		{
 			if (getLeader().isRaid())
 			{
 				setIsRaidMinion(true);
 			}
 			// Notify Leader that Minion has Spawned
 			getLeader().notifyMinionSpawned(this);
 		}
		// check the region where this mob is, do not activate the AI if region is inactive.
		L2WorldRegion region = L2World.getInstance().getRegion(getX(),getY());
		if ((region !=null) && (!region.isActive()))
			((L2AttackableAI) getAI()).stopAITask();
                super.onSpawn();
	}

	/**
	 * @param b
	 */
	private void setIsNoRndWalk(boolean b)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Set the master of this L2MinionInstance.<BR><BR>
	 *
	 * @param leader The L2Character that leads this L2MinionInstance
	 *
	 */
	public void setLeader(L2MonsterInstance leader)
	{
		_master = leader;
	}

	/**
	* Manages the doDie event for this L2MinionInstance.<BR><BR>
	*
	* @param killer The L2Character that killed this L2MinionInstance.<BR><BR>
	*/

	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
			return false;
		_master.notifyMinionDied(this);
		return true;
	}
}
