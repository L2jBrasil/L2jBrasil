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

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.EtcStatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.effects.EffectCharge;
import com.it.br.gameserver.templates.StatsSet;

public class L2SkillCharge extends L2Skill {

	public L2SkillCharge(StatsSet set)
	{
		super(set);
	}


	@Override
	public void useSkill(L2Character caster, L2Object[] targets)
	{
		if (caster.isAlikeDead())
			return;

		// get the effect
		EffectCharge effect = (EffectCharge) caster.getFirstEffect(this.getId());
		if (effect != null)
		{
			if (effect.numCharges < getNumCharges())
			{
				effect.numCharges++;
				if (caster instanceof L2PcInstance)
				{
					caster.sendPacket(new EtcStatusUpdate((L2PcInstance)caster));
					SystemMessage sm = new SystemMessage(SystemMessageId.FORCE_INCREASED_TO_S1);
					sm.addNumber(effect.numCharges);
					caster.sendPacket(sm);
				}
			}
			else
            {
                SystemMessage sm = new SystemMessage(SystemMessageId.FORCE_MAXIMUM);
                caster.sendPacket(sm);
            }
            return;
		}
		getEffects(caster, caster);

        //effect self :]
        //L2Effect seffect = caster.getEffect(getId());
        //TODO ?? this is always null due to a return in the if block above!
        //if (effect != null && seffect.isSelfEffect())
        //{
            //Replace old effect with new one.
        //    seffect.exit();
        //}
        // cast self effect if any
        getEffectsSelf(caster);
	}

}
