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
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author evill33t
 *
 */
public class SummonTreasureKey implements ISkillHandler
{
    static Logger _log = LoggerFactory.getLogger(SummonTreasureKey.class);
    private static final SkillType[] SKILL_IDS = {SkillType.SUMMON_TREASURE_KEY};


	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
    {
        if (activeChar == null || !(activeChar instanceof L2PcInstance)) return;

        L2PcInstance player = (L2PcInstance) activeChar;

        try
        {

            int item_id = 0;

            switch (skill.getLevel())
            {
                case 1:
                {
                  item_id = Rnd.get(6667, 6669);
                  break;
                }
                case 2:
                {
                  item_id = Rnd.get(6668, 6670);
                  break;
                }
                case 3:
                {
                  item_id = Rnd.get(6669, 6671);
                  break;
                }
                case 4:
                {
                  item_id = Rnd.get(6670, 6672);
                  break;
                }
            }
            player.addItem("Skill", item_id, Rnd.get(2,3), player, false);
        }
        catch (Exception e)
        {
            _log.warn("Error using skill summon Treasure Key:" + e);
        }
    }


	public SkillType[] getSkillIds()
    {
        return SKILL_IDS;
    }

}
