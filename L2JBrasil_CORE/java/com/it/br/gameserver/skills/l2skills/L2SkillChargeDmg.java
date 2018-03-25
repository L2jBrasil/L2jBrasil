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
package com.it.br.gameserver.skills.l2skills;

import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.EtcStatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Formulas;
import com.it.br.gameserver.skills.effects.EffectCharge;
import com.it.br.gameserver.templates.L2WeaponType;
import com.it.br.gameserver.templates.StatsSet;

public class L2SkillChargeDmg extends L2Skill
{

	final int chargeSkillId;

	public L2SkillChargeDmg(StatsSet set)
    {
		super(set);
		chargeSkillId = set.getInteger("charge_skill_id");
	}


	@Override
	public boolean checkCondition(L2Character activeChar, L2Object target, boolean itemOrWeapon)
	{
		if (activeChar instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance)activeChar;
			EffectCharge e = (EffectCharge)player.getFirstEffect(chargeSkillId);
			if(e == null || e.numCharges < getNumCharges())
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
				sm.addSkillName(getId());
				activeChar.sendPacket(sm);
				return false;
			}
		}
		return super.checkCondition(activeChar, target, itemOrWeapon);
	}


	@Override
	public void useSkill(L2Character caster, L2Object[] targets)
    {
		if (caster.isAlikeDead())
        {
			return;
        }

		// get the effect
		EffectCharge effect = (EffectCharge) caster.getFirstEffect(chargeSkillId);
		if (effect == null || effect.numCharges < getNumCharges())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
			sm.addSkillName(getId());
			caster.sendPacket(sm);
			return;
		}
        double modifier = 0;
        modifier = (effect.numCharges-getNumCharges())*0.33;
		if (getTargetType() != SkillTargetType.TARGET_AREA && getTargetType() != SkillTargetType.TARGET_MULTIFACE)
			effect.numCharges -= getNumCharges();
		if (caster instanceof L2PcInstance)
			caster.sendPacket(new EtcStatusUpdate((L2PcInstance)caster));
        if (effect.numCharges == 0)
        	{effect.exit();}
        for (int index = 0;index < targets.length;index++)
        {
        	L2ItemInstance weapon = caster.getActiveWeaponInstance();
        	L2Character target = (L2Character)targets[index];
        	if (target.isAlikeDead())
        		continue;

			// TODO: should we use dual or not?
			// because if so, damage are lowered but we dont do anything special with dual then
			// like in doAttackHitByDual which in fact does the calcPhysDam call twice

			//boolean dual  = caster.isUsingDualWeapon();
			boolean shld = Formulas.calcShldUse(caster, target);
			boolean crit = Formulas.calcCrit(caster.getCriticalHit(target, this));
			boolean soul = (weapon != null
							&& weapon.getChargedSoulshot() == L2ItemInstance.CHARGED_SOULSHOT
							&& weapon.getItemType() != L2WeaponType.DAGGER );

			// damage calculation, crit is static 2x
			int damage = (int)Formulas.calcPhysDam(caster, target, this, shld, false, false, soul);
			if (crit) damage *= 2;

			if (damage > 0)
            {
                double finalDamage = damage;
                finalDamage = finalDamage+(modifier*finalDamage);
				target.reduceCurrentHp(finalDamage, caster);

				caster.sendDamageMessage(target, (int)finalDamage, false, crit, false);

				if (soul && weapon!= null)
					weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
			}
            else
            {
				caster.sendDamageMessage(target, 0, false, false, true);
			}
		}
        // effect self :]
        L2Effect seffect = caster.getFirstEffect(getId());
        if (seffect != null && seffect.isSelfEffect())
        {
            //Replace old effect with new one.
            seffect.exit();
        }
        // cast self effect if any
        getEffectsSelf(caster);
	}

}
