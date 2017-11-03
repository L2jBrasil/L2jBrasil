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

import com.it.br.gameserver.model.ChanceCondition;
import com.it.br.gameserver.model.IChanceSkillTrigger;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.skills.Env;

public class EffectChanceSkillTrigger extends L2Effect implements IChanceSkillTrigger
{
	public final int _triggeredId;
	public final int _triggeredLevel;
	public final ChanceCondition _chanceCondition;

	public EffectChanceSkillTrigger(Env env, EffectTemplate template)
    {
		super(env, template);
		_triggeredId = template.triggeredId;
	    _triggeredLevel = template.triggeredLevel;
	    _chanceCondition = template.chanceCondition;
    }


    @Override
	public EffectType getEffectType()
    {
	    return EffectType.CHANCE_SKILL_TRIGGER;
    }


	@Override
	public boolean onStart()
	{
		getEffected().addChanceTrigger(this);
		return true;
	}


    @Override
	public boolean onActionTime()
    {
	    return false;
    }


	@Override
	public void onExit()
	{
        getEffected().removeChanceTrigger(this);
	}


	public int getTriggeredChanceId()
    {
	    return getEffectTemplate().triggeredId;
    }


	public int getTriggeredChanceLevel()
    {
	    return getEffectTemplate().triggeredLevel;
    }


	public boolean triggersChanceSkill()
    {
	    return getTriggeredChanceId() > 1;
    }


	public ChanceCondition getTriggeredChanceCondition()
    {
	    return getEffectTemplate().chanceCondition;
    }
}