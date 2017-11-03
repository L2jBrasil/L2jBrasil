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

import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.StatsSet;
import com.it.br.util.Rnd;

/**
 * @author Nemesiss
 *
 */
public class L2SkillCreateItem extends L2Skill
{
    private final int[] _createItemId;
    private final int _createItemCount;
    private final int _randomCount;

    public L2SkillCreateItem(StatsSet set)
    {
        super(set);
        _createItemId = set.getIntegerArray("create_item_id");
        _createItemCount = set.getInteger("create_item_count", 0);
        _randomCount = set.getInteger("random_count", 1);
    }

    /**
     * @see com.it.br.gameserver.model.L2Skill#useSkill(com.it.br.gameserver.model.L2Character, com.it.br.gameserver.model.L2Object[])
     */

	@Override
	public void useSkill(L2Character activeChar, L2Object[] targets)
    {
        if (activeChar.isAlikeDead()) return;
        if (_createItemId == null || _createItemCount == 0)
        {
            SystemMessage sm = new SystemMessage(SystemMessageId.S1_NOT_AVAILABLE);
            activeChar.sendPacket(sm);
            return;
        }
        L2PcInstance player = (L2PcInstance) activeChar;
        if (activeChar instanceof L2PcInstance)
        {
            int rnd = Rnd.nextInt(_randomCount) + 1;
            int count = _createItemCount * rnd;
            int rndid = Rnd.nextInt(_createItemId.length);
            giveItems(player, _createItemId[rndid], count);
        }
    }

    /**
     * @param activeChar
     * @param itemId
     * @param count
     */
    @SuppressWarnings("unused")
    public void giveItems(L2PcInstance activeChar, int itemId, int count)
    {
        L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), itemId);
        if (item == null) return;
        item.setCount(count);
        activeChar.getInventory().addItem("Skill", item, activeChar, activeChar);

        if (count > 1)
        {
            SystemMessage smsg = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
            smsg.addItemName(item.getItemId());
            smsg.addNumber(count);
            activeChar.sendPacket(smsg);
        }
        else
        {
            SystemMessage smsg = new SystemMessage(SystemMessageId.EARNED_ITEM);
            smsg.addItemName(item.getItemId());
            activeChar.sendPacket(smsg);
        }
        ItemList il = new ItemList(activeChar, false);
        activeChar.sendPacket(il);
    }
}
