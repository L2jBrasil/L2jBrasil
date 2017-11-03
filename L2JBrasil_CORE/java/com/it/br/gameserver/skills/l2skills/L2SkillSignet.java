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

/**
 * @author Forsaiken
 */

import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2EffectPointInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.templates.StatsSet;
import com.it.br.util.Point3D;


public final class L2SkillSignet extends L2Skill
{
	private int _effectNpcId;
	public int effectId;
	
    public L2SkillSignet(StatsSet set)
    {
        super(set);
        _effectNpcId = set.getInteger("effectNpcId", -1);
        effectId = set.getInteger("effectId", -1);
    }
    

	@Override
	public void useSkill(L2Character caster, L2Object[] targets)
    {
        if (caster.isAlikeDead())
            return;
        
        L2NpcTemplate template = NpcTable.getInstance().getTemplate(_effectNpcId);        
        L2EffectPointInstance effectPoint = new L2EffectPointInstance(IdFactory.getInstance().getNextId(),  template,  caster);
        effectPoint.setCurrentHp(effectPoint.getMaxHp());
        effectPoint.setCurrentMp(effectPoint.getMaxMp());
        L2World.getInstance().storeObject(effectPoint);
        
        int x = caster.getX();
        int y = caster.getY();
        int z = caster.getZ();
        
        if (caster instanceof L2PcInstance && getTargetType() == SkillTargetType.TARGET_GROUND)
        {
            Point3D wordPosition = ((L2PcInstance)caster).getCurrentSkillWorldPosition();
            
            if (wordPosition != null)
            {
                x = wordPosition.getX();
                y = wordPosition.getY();
                z = wordPosition.getZ();
            }
        }
        getEffects(caster, effectPoint);
        
        effectPoint.setIsInvul(true);
        effectPoint.spawnMe(x, y, z);
    }
}
