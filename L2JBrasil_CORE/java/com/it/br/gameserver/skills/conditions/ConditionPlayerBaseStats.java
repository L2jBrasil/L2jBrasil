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
package com.it.br.gameserver.skills.conditions;

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.skills.Env;

/**
 * @author mkizub
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConditionPlayerBaseStats extends Condition {

	private final BaseStat _stat;
	private final int _value;

	public ConditionPlayerBaseStats(L2Character player, BaseStat stat, int value)
	{
		super();
		_stat = stat;
		_value = value;
	}


	@Override
	public boolean testImpl(Env env) {
		if (!(env.player instanceof L2PcInstance))
			return false;
		L2PcInstance player = (L2PcInstance)env.player;
		switch (_stat)
		{
		case Int: return player.getINT() >= _value;
		case Str: return player.getSTR() >= _value;
		case Con: return player.getCON() >= _value;
		case Dex: return player.getDEX() >= _value;
		case Men: return player.getMEN() >= _value;
		case Wit: return player.getWIT() >= _value;
		}
		return false;
	}
}
enum BaseStat {
	Int,
	Str,
	Con,
	Dex,
	Men,
	Wit
}
