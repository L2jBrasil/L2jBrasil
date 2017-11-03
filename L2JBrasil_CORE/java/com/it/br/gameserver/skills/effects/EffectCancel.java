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
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Env;
import com.it.br.gameserver.skills.Stats;
import com.it.br.util.Rnd;

final class EffectCancel extends L2Effect
{
	public EffectCancel(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public EffectType getEffectType()
	{
		return EffectType.CANCEL;
	}

	/** Notify started */
	@Override
	public boolean onStart()
	{
		int landrate = (int) getEffector().calcStat(Stats.CANCEL_VULN, 90, getEffected(), null);
		if(Rnd.get(100) < landrate)
		{
			L2Effect[] effects = getEffected().getAllEffects();
			int maxdisp = (int) getSkill().getNegatePower();
			if(maxdisp == 0)
			{
				maxdisp = 5;
			}
			for(L2Effect e : effects)
			{
				switch(e.getEffectType())
				{
					case SIGNET_GROUND:
					case SIGNET_EFFECT:
						continue;
				}

				if(e.getSkill().getId() != 4082 && e.getSkill().getId() != 4215 && e.getSkill().getId() != 5182 && e.getSkill().getId() != 4515 && e.getSkill().getId() != 110 && e.getSkill().getId() != 111 && e.getSkill().getId() != 1323 && e.getSkill().getId() != 1325)
				{
					if(e.getSkill().getSkillType() == SkillType.BUFF)
					{
						// TODO Fix cancel debuffs
						if(e.getSkill().getSkillType() != SkillType.DEBUFF) {
							
							int rate = 100;
							int level = e.getLevel();
							if(level > 0)
							{
								rate = Integer.valueOf(150 / (1 + level));
							}
	
							if(rate > 95)
							{
								rate = 95;
							}
							else if(rate < 5)
							{
								rate = 5;
							}
	
							if(Rnd.get(100) < rate)
							{
								e.exit(true);
								maxdisp--;
								if(maxdisp == 0)
								{
									break;
								}
							}
						}
					}
				}
			}
			effects = null;
		}
		else
		{
			if(getEffector() instanceof L2PcInstance)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
				sm.addString(getEffected().getName());
				sm.addSkillName(getSkill().getDisplayId());
				getEffector().sendPacket(sm);
				sm = null;
			}
		}
		return true;
	}

	/** Notify exited */
	@Override
	public void onExit()
	{
	//null
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
 