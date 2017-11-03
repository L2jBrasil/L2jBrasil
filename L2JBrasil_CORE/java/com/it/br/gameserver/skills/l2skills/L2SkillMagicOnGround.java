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
package com.it.br.gameserver.skills.l2skills;

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.templates.StatsSet;

public final class L2SkillMagicOnGround extends L2Skill
{
	public int effectNpcId;
	public int triggerEffectId;
	
    public L2SkillMagicOnGround(StatsSet set)
    {
        super(set);
        effectNpcId = set.getInteger("effectNpcId", -1);
        triggerEffectId = set.getInteger("triggerEffectId", -1);
    }
    

	@Override
	public void useSkill(L2Character caster, L2Object[] targets)
    {
        if (caster.isAlikeDead())
            return;
        
        getEffectsSelf(caster);
    }
}