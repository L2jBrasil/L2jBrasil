/* This program is free software; you can redistribute it and/or modify
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
package com.it.br.gameserver.skills.effects;

import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.skills.Env;

/**
 *
 * @author LBaldi
 */
final class EffectBigHead extends L2Effect
{

	public EffectBigHead(Env env, EffectTemplate template)
	{
		super(env, template);
	}


	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}


	@Override
	public boolean onStart() 
	{
		 getEffected().startAbnormalEffect(0x02000);
		 return true;
	}


	@Override
	public void onExit() 
	{
		 getEffected().stopAbnormalEffect(0x02000);
	}


	@Override
	public boolean onActionTime() {
		return false;
	}
}
