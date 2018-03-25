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
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.L2CharPosition;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.knownlist.CommanderKnownList;
import com.it.br.gameserver.templates.L2NpcTemplate;

public class L2CommanderInstance extends L2Attackable
{
	private int _homeX;
	private int _homeY;
	private int _homeZ;

	public L2CommanderInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		getKnownList(); // init knownlist
	}

	@Override
	public final CommanderKnownList getKnownList()
	{
		if(super.getKnownList() == null || !(super.getKnownList() instanceof CommanderKnownList))
		{
			setKnownList(new CommanderKnownList(this));
		}
		return (CommanderKnownList) super.getKnownList();
	}

	@Override
	public void addDamageHate(L2Character attacker, int damage, int aggro)
	{
		if(attacker == null)
			return;

		if(!(attacker instanceof L2CommanderInstance))
		{
			super.addDamageHate(attacker, damage, aggro);
		}
	}

	@Override
	public boolean doDie(L2Character killer)
	{
		if(!super.doDie(killer))
			return false;

		return true;
	}

	public void getHomeLocation()
	{
		_homeX = getX();
		_homeY = getY();
		_homeZ = getZ();

		if(Config.DEBUG)
		{
			_log.debug(getObjectId() + ": Home location set to" + " X:" + _homeX + " Y:" + _homeY + " Z:" + _homeZ);
		}
	}

	public int getHomeX()
	{
		return _homeX;
	}

	public int getHomeY()
	{
		return _homeY;
	}

	@Override
	public void returnHome()
	{
		if(!isInsideRadius(_homeX, _homeY, 40, false))
		{
			if(Config.DEBUG)
			{
				_log.debug(getObjectId() + ": moving home");
			}
			setisReturningToSpawnPoint(true);
			clearAggroList();

			if(hasAI())
			{
				getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(_homeX, _homeY, _homeZ, 0));
			}
		}
	}
}