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
package com.it.br.gameserver.handler.skillhandlers;

import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class ...
 *
 * @version $Revision: 1.1.2.2.2.9 $ $Date: 2005/04/04 19:08:01 $
 */

public class Charge implements ISkillHandler
{
	static Logger _log = LoggerFactory.getLogger(Charge.class);

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.handler.IItemHandler#useItem(com.it.br.gameserver.model.L2PcInstance, com.it.br.gameserver.model.L2ItemInstance)
	 */
	private static final SkillType[] SKILL_IDS = {/*SkillType.CHARGE*/};


	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{

        for(int index = 0;index < targets.length;index++)
        {
        	if (!(targets[index] instanceof L2PcInstance))
        		continue;
			L2PcInstance target = (L2PcInstance)targets[index];
        	skill.getEffects(activeChar, target);
		}
        // self Effect :]
        L2Effect effect = activeChar.getFirstEffect(skill.getId());
        if (effect != null && effect.isSelfEffect())
        {
        	//Replace old effect with new one.
        	effect.exit();
        }
        skill.getEffectsSelf(activeChar);
	}


	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
