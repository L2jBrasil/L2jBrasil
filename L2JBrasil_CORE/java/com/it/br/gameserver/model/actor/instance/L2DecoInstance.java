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

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.templates.L2NpcTemplate;

public class L2DecoInstance extends L2NpcInstance
{
	/**
	 * @param objectId
	 * @param template
	 */
	public L2DecoInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}


	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake)
	{
		damage = 0;
		super.reduceCurrentHp(damage, attacker, awake);
	}
}
