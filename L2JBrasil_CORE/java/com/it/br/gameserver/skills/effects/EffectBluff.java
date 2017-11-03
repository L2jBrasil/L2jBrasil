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
import com.it.br.gameserver.model.actor.instance.L2FolkInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2SiegeSummonInstance;
import com.it.br.gameserver.network.serverpackets.BeginRotation;
import com.it.br.gameserver.network.serverpackets.StopRotation;
import com.it.br.gameserver.skills.Env;

/**
 * @author decad
 *
 * Implementation of the Bluff Effect
 */
public class EffectBluff extends L2Effect
{

    public EffectBluff(Env env, EffectTemplate template)
    {
        super(env, template);
    }


    @Override
	public EffectType getEffectType()
    {
        return EffectType.BLUFF; //test for bluff effect
    }

    /** Notify started */

    @Override
	public boolean onStart()
    {
    	if (getEffected() instanceof L2FolkInstance) return false;
    	// if (getEffected() instanceof L2SiegeGuardInstance) return;
    	// Cannot be used on Headquarters Flag.
    	// bluff now is a PVE PVP skill
    	if (getEffected() instanceof L2NpcInstance && ((L2NpcInstance)getEffected()).getNpcId() == 35062 || getSkill().getId() != 358) 
    		return false;

    	if (getEffected() instanceof L2SiegeSummonInstance)
    		return false;
    	
    	getEffected().broadcastPacket(new BeginRotation(getEffected().getObjectId(), getEffected().getHeading(), 1, 65535));
	getEffected().broadcastPacket(new StopRotation(getEffected().getObjectId(), getEffector().getHeading(), 65535));
	getEffected().setHeading(getEffector().getHeading());  
	        return true; 
    }

    @Override
	public void onExit()
    {
        getEffected().stopFear(this);
    }

    @Override
	public boolean onActionTime()
    {
        return false;
    }
}
