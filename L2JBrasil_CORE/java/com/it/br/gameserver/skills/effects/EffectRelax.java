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

import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Env;


public class EffectRelax extends L2Effect
{
	public EffectRelax(Env env, EffectTemplate template)
	{
		super(env, template);
	}


	@Override
	public EffectType getEffectType()
	{
		return EffectType.RELAXING;
	}

	/** Notify started */

	@Override
	public boolean onStart()
	{

		if (getEffected() instanceof L2PcInstance)
		{
			setRelax(true);
			((L2PcInstance) getEffected()).sitDown();
		}
		else
			getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_REST);
		return super.onStart();
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.model.L2Effect#onExit()
	 */

	@Override
	public void onExit() 
        {
               setRelax(false);
	       super.onExit();
	}


	@Override
	public boolean onActionTime()
	{
            boolean retval = true;
		if(getEffected().isDead())
            retval = false;

		if(getEffected() instanceof L2PcInstance)
		{
			if(!((L2PcInstance)getEffected()).isSitting())
				retval = false;
		}

		if (getEffected().getCurrentHp()+1 > getEffected().getMaxHp()) 
                {
			if(getSkill().isToggle())
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
				sm.addString("Fully rested. Effect of " + getSkill().getName() + " has been removed.");
				getEffected().sendPacket(sm);
				if (getEffected() instanceof L2PcInstance)((L2PcInstance)getEffected()).standUp();
                retval = false;
			}
		}

		double manaDam = calc();

		if(manaDam > getEffected().getCurrentMp())
		{
			if(getSkill().isToggle())
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
				getEffected().sendPacket(sm);
				if (getEffected() instanceof L2PcInstance)((L2PcInstance)getEffected()).standUp();
                retval = false;
			}
		}

        if (!retval)
            setRelax(retval);
        else
            getEffected().reduceCurrentMp(manaDam);

        return retval;
	}

    private void setRelax(boolean val)
    {
        if(getEffected() instanceof L2PcInstance)
        	((L2PcInstance)getEffected()).setRelax(val);
    }
}
