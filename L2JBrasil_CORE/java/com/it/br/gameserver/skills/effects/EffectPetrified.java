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

/**
 * @author decad
 *
 * Implementation of the Petrified effect
 */
package com.it.br.gameserver.skills.effects;

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.skills.Env;

public class EffectPetrified extends L2Effect 
{

    public EffectPetrified(Env env, EffectTemplate template)
    {
        super(env, template);
    }


    @Override
	public EffectType getEffectType()
    {
        return EffectType.PETRIFIED;
    }
    

    @Override
	public boolean onStart()
    {
        getEffected().abortAttack();
        getEffected().abortCast();
        getEffected().startAbnormalEffect(L2Character.ABNORMAL_EFFECT_HOLD_2);
        getEffected().setIsParalyzed(true);
        return true;
    }
    

    @Override
	public void onExit()
    {
        getEffected().stopAbnormalEffect(L2Character.ABNORMAL_EFFECT_HOLD_2);
        getEffected().setIsParalyzed(false);
    }
    

    @Override
	public boolean onActionTime()
    {
        return false;
    }
}
