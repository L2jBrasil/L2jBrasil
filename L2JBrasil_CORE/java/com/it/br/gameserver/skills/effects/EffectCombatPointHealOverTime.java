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
package com.it.br.gameserver.skills.effects;

import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.network.serverpackets.StatusUpdate;
import com.it.br.gameserver.skills.Env;


public class EffectCombatPointHealOverTime extends L2Effect
{
    public EffectCombatPointHealOverTime(Env env, EffectTemplate template)
    {
        super(env, template);
    }


    @Override
	public EffectType getEffectType()
    {
        return EffectType.COMBAT_POINT_HEAL_OVER_TIME;
    }


    @Override
	public boolean onActionTime()
    {
        if (getEffected().isDead())
            return false;

        double cp = getEffected().getCurrentCp();
        double maxcp = getEffected().getMaxCp();
        cp += calc();
        if(cp > maxcp)
        {
            cp = maxcp;
        }
        getEffected().setCurrentCp(cp);
        StatusUpdate sump = new StatusUpdate(getEffected().getObjectId());
        sump.addAttribute(StatusUpdate.CUR_CP, (int)cp);
        getEffected().sendPacket(sump);
        return true;
    }
}
