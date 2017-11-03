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

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Env;

public class EffectSilentMove extends L2Effect
{
	public EffectSilentMove(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	/** Notify started */

	@Override
	public boolean onStart()
	{
		super.onStart();

		L2Character effected = getEffected();
		if (effected instanceof L2PcInstance)
		{
			((L2PcInstance) effected).setSilentMoving(true);
			return true;
		}
		return false;
	}

	@Override
	public void onExit()
	{
		super.onExit();

		L2Character effected = getEffected();
		if (effected instanceof L2PcInstance)
			((L2PcInstance)effected).setSilentMoving(false);
	}


	@Override
	public EffectType getEffectType()
	{
		return EffectType.SILENT_MOVE;
	}


	@Override
	public boolean onActionTime()
	{
		 // Only cont skills shouldn't end
		if(getSkill().getSkillType() != SkillType.CONT)
			return false;

		if(getEffected().isDead())
			return false;
		
		if(getEffected().isAttackingNow())
			return false;
		
		if(getEffected().isCastingNow())
			return false;
		
		double manaDam = calc();

		if(manaDam > getEffected().getCurrentMp())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
			getEffected().sendPacket(sm);
			return false;
		}

		getEffected().reduceCurrentMp(manaDam);
		return true;
	}

}
