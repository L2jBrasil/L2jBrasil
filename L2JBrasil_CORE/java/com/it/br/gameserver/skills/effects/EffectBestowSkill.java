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

import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.skills.Env;

/**
 * @author Guma
 */
final class EffectBestowSkill extends L2Effect
{
	public EffectBestowSkill(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	/**
	 * 
	 * @see com.it.br.gameserver.model.L2Effect#getEffectType()
	 */

	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}
	
	/**
	 * 
	 * @see com.it.br.gameserver.model.L2Effect#onStart()
	 */

	@Override
	public boolean onStart()
	{
		L2Skill tempSkill = SkillTable.getInstance().getInfo(getSkill().getTriggeredId(), getSkill().getTriggeredLevel());
		if (tempSkill != null)
		{
			getEffected().addSkill(tempSkill);
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @see com.it.br.gameserver.model.L2Effect#onExit()
	 */

	@Override
	public void onExit()
	{
		getEffected().removeSkill(getSkill().getTriggeredId());
	}
	
	/**
	 * 
	 * @see com.it.br.gameserver.model.L2Effect#onActionTime()
	 */

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
