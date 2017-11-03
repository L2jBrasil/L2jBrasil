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

import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.network.serverpackets.MyTargetSelected;
import com.it.br.gameserver.skills.Env;

/**
 * @author -Nemesiss-
 */
public class EffectTargetMe extends L2Effect
{
    public EffectTargetMe(Env env, EffectTemplate template)
    {
        super(env, template);
    }


    @Override
	public EffectType getEffectType()
    {
        return EffectType.TARGET_ME;
    }
    
    /**
     * 
     * @see com.it.br.gameserver.model.L2Effect#onStart()
     */
    /** Notify started */

    @Override
	public boolean onStart()
    {
		getEffected().setTarget(getEffector());
		MyTargetSelected my = new MyTargetSelected(getEffector().getObjectId(), 0);
		getEffected().sendPacket(my);
		getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getEffector());
		return true;
    }

    /**
     *  
     * @see com.it.br.gameserver.model.L2Effect#onExit()
     */
    /** Notify exited */

    @Override
	public void onExit() 
    {
        //nothing
    }


    @Override
	public boolean onActionTime()
    {
    	//nothing
        return false;
    }
}
