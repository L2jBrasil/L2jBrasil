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
package com.it.br.gameserver.model;

import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.handler.SkillHandler;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.network.serverpackets.MagicSkillLaunched;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;
import com.it.br.gameserver.skills.effects.EffectChanceSkillTrigger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Added support for allowing effect as a chance skill trigger (DrHouse)
 *
 * @author  kombat
 */
public class ChanceSkillList extends ConcurrentHashMap<IChanceSkillTrigger, ChanceCondition>
{
	private static final long serialVersionUID = 1L;

	private L2Character _owner;

	public ChanceSkillList(L2Character owner)
	{
		super();
		_owner = owner;
	}

	public L2Character getOwner()
	{
		return _owner;
	}

	public void setOwner(L2Character owner)
	{
		_owner = owner;
	}

	public void onHit(L2Character target, boolean ownerWasHit, boolean wasCrit)
	{
		int event;
		if (ownerWasHit)
		{
			event = ChanceCondition.EVT_ATTACKED | ChanceCondition.EVT_ATTACKED_HIT;
			if (wasCrit)
				event |= ChanceCondition.EVT_ATTACKED_CRIT;
		}
		else
		{
			event = ChanceCondition.EVT_HIT;
			if (wasCrit)
				event |= ChanceCondition.EVT_CRIT;
		}

		onEvent(event, target);
	}

	public void onEvadedHit(L2Character attacker)
	{
		onEvent(ChanceCondition.EVT_EVADED_HIT, attacker);
	}

	public void onSkillHit(L2Character target, boolean ownerWasHit, boolean wasMagic, boolean wasOffensive)
	{
		int event;
		if (ownerWasHit)
		{
			event = ChanceCondition.EVT_HIT_BY_SKILL;
			if (wasOffensive)
			{
				event |= ChanceCondition.EVT_HIT_BY_OFFENSIVE_SKILL;
				event |= ChanceCondition.EVT_ATTACKED;
			} else
				event |= ChanceCondition.EVT_HIT_BY_GOOD_MAGIC;
		}
		else
		{
			event = ChanceCondition.EVT_CAST;
			event |= wasMagic ? ChanceCondition.EVT_MAGIC : ChanceCondition.EVT_PHYSICAL;
			event |= wasOffensive ? ChanceCondition.EVT_MAGIC_OFFENSIVE : ChanceCondition.EVT_MAGIC_GOOD;
		}
		onEvent(event, target);
	}

	public void onEvent(int event, L2Character target)
	{

        for (Map.Entry<IChanceSkillTrigger, ChanceCondition> e : entrySet())
        {
			if (e.getValue() != null && e.getValue().trigger(event))
				if (e.getKey() instanceof L2Skill)
					makeCast((L2Skill)e.getKey(), target);
				else if (e.getKey() instanceof EffectChanceSkillTrigger)
					makeCast((EffectChanceSkillTrigger)e.getKey(), target);
		}
	}

	private void makeCast(L2Skill skill, L2Character target)
	{
		try
		{
			if(skill.getWeaponDependancy(_owner) && skill.checkCondition(_owner, target, false))
			{
				if(skill.triggersChanceSkill()) //skill will trigger another skill, but only if its not chance skill
				{
					skill = SkillTable.getInstance().getInfo(skill.getTriggeredChanceId(), skill.getTriggeredChanceLevel());
					if(skill == null || skill.getSkillType() == SkillType.NOTDONE)
						return;
				}
				
				if (_owner.isSkillDisabled(skill.getId()))
					return;
				
				if (skill.getReuseDelay() > 0)
					_owner.disableSkill(skill.getId(), skill.getReuseDelay());
				
				L2Object[] targets = skill.getTargetList(_owner, false, target);
				
				if (targets.length == 0)
					return;
				
				L2Character firstTarget = (L2Character)targets[0];
				
				ISkillHandler handler = SkillHandler.getInstance().getSkillHandler(skill.getSkillType());
				
				_owner.broadcastPacket(new MagicSkillLaunched(_owner, skill.getDisplayId(), skill.getLevel(), targets));
				_owner.broadcastPacket(new MagicSkillUser(_owner, firstTarget, skill.getDisplayId(), skill.getLevel(), 0, 0));
				
				// Launch the magic skill and calculate its effects
				// TODO: once core will support all possible effects, use effects (not handler)
				if (handler != null)
					handler.useSkill(_owner, skill, targets);
				else
					skill.useSkill(_owner, targets);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void makeCast(EffectChanceSkillTrigger effect, L2Character target)
	{
		try
		{
			if (effect == null || !effect.triggersChanceSkill())
				return;
			
			L2Skill triggered = SkillTable.getInstance().getInfo(effect.getTriggeredChanceId(), effect.getTriggeredChanceLevel());
			if (triggered == null)
				return;
			L2Character caster = triggered.getTargetType() == L2Skill.SkillTargetType.TARGET_SELF ? _owner : effect.getEffector();
			
			if (caster == null
					|| triggered.getSkillType() == SkillType.NOTDONE
					|| caster.isSkillDisabled(triggered.getId()))
				return;
			
			triggered.getEffects(_owner, target);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}