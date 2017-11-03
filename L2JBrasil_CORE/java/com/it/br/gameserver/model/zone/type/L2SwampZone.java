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
package com.it.br.gameserver.model.zone.type;

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.zone.L2ZoneType;

public class L2SwampZone extends L2ZoneType
{
	private int _move_bonus;

	public L2SwampZone()
	{
		super();

		// Setup default speed reduce (in %)
		_move_bonus = -50;
	}

	@Override
	public void setParameter(String name, String value)
	{
		if(name.equals("move_bonus"))
		{
			_move_bonus = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}

	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_SWAMP, true);
		if(character instanceof L2PcInstance)
		{
			((L2PcInstance) character).broadcastUserInfo();
		}
	}

	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_SWAMP, false);
		if(character instanceof L2PcInstance)
		{
			((L2PcInstance) character).broadcastUserInfo();
		}
	}

	public int getMoveBonus()
	{
		return _move_bonus;
	}

	@Override
	public void onDieInside(L2Character character){}

	@Override
	public void onReviveInside(L2Character character){}
}