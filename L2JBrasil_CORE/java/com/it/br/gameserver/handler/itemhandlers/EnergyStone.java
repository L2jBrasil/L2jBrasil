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
 * [URL]http://www.gnu.org/copyleft/gpl.html[/URL]
 */
package com.it.br.gameserver.handler.itemhandlers;


import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.EtcStatusUpdate;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.effects.EffectCharge;
import com.it.br.gameserver.skills.l2skills.L2SkillCharge;

public class EnergyStone implements IItemHandler
{
    private static final int[] ITEM_IDS = { 5589 };
    private EffectCharge _effect;
    private L2SkillCharge _skill;


	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
    {

    	L2PcInstance activeChar;
        if (playable instanceof L2PcInstance)
        {
        	activeChar = (L2PcInstance)playable;
        }
        else if (playable instanceof L2PetInstance)
        {
        	activeChar = ((L2PetInstance)playable).getOwner();
        }
        else return;

        if (item.getItemId() != 5589) return;
        int classid = activeChar.getClassId().getId();

        if (classid == 2 || classid == 48 || classid == 88 || classid == 114)
        {

        	if (activeChar.isAllSkillsDisabled())
        	{
        		ActionFailed af = new ActionFailed();
        		activeChar.sendPacket(af);
        		return;
            }

            if (activeChar.isSitting())
            {
            	activeChar.sendPacket(new SystemMessage(SystemMessageId.CANT_MOVE_SITTING));
            	return;
            }

            _skill = getChargeSkill(activeChar);
            if (_skill == null)
            {
            	SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
            	sm.addItemName(5589);
            	activeChar.sendPacket(sm);
            	return;
            }

            _effect = activeChar.getChargeEffect();

            if (_effect == null)
            {
                L2Skill dummy = SkillTable.getInstance().getInfo(_skill.getId(),_skill.getLevel());
                if (dummy != null)
                {
                	dummy.getEffects(null, activeChar);
                	activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false);
                	return;
                }
                return;
            }

            if (_effect.getLevel() < 2)
            {
                MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, _skill.getId(), 1, 1, 0);
                activeChar.sendPacket(MSU);
                activeChar.broadcastPacket(MSU);
                _effect.addNumCharges(1);
                activeChar.sendPacket(new EtcStatusUpdate(activeChar));
                activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
            }
            else if (_effect.getLevel() == 2)
            {
                activeChar.sendPacket(new SystemMessage(SystemMessageId.FORCE_MAXLEVEL_REACHED));
            }
            SystemMessage sm = new SystemMessage(SystemMessageId.FORCE_INCREASED_TO_S1);
            sm.addNumber(_effect.getLevel());
            activeChar.sendPacket(sm);
            return;
        }
        else
        {
             SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
             sm.addItemName(5589);
             activeChar.sendPacket(sm);
             return;
        }
    }

    private L2SkillCharge getChargeSkill(L2PcInstance activeChar)
    {
    L2Skill[] skills = activeChar.getAllSkills();
    for (L2Skill s : skills) {
        if (s.getId() == 50 || s.getId() == 8) {
            return (L2SkillCharge)s;
        }
    }
    return null;
    }


	public int[] getItemIds()
    {
        return ITEM_IDS;
    }
}