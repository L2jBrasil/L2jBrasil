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
package com.it.br.gameserver.skills.effects;

import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.skills.Env;

/**
 * @author Ahmed
 */
public final class EffectImmobileUntilAttacked extends L2Effect
{
	public EffectImmobileUntilAttacked(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	

	@Override
	public EffectType getEffectType()
	{
		return EffectType.IMMOBILE_UNTIL_ATTACKED;
	}
	
	/** Notify started */

	@Override
	public boolean onStart()
	{
		getEffected().isImmobileUntilAttacked();
		return true;
	}
	
	/** Notify exited */

	@Override
	public void onExit()
	{
		getEffected().setIsImmobileUntilAttacked(false);
	}

	/**
	 * @see com.l2j.gameserver.model.L2Effect#onActionTime()
	 */

	@Override
	public boolean onActionTime()
	{
		// TODO Auto-generated method stub
		return false;
	}
}