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
package com.it.br.gameserver.handler.itemhandlers;

import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2NpcTemplate;

public class ChristmasTree implements IItemHandler
{
    private static final int[] ITEM_IDS = {
    										5560, /* x-mas tree */
    										5561  /* Special x-mas tree*/
    									  };

    private static final int[] NPC_IDS = {
    										13006, /* Christmas tree w. flashing lights and snow */
    										13007
    									 };


	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
    {
        L2PcInstance activeChar = (L2PcInstance)playable;
        L2NpcTemplate template1 = null;

        int itemId = item.getItemId();
        for (int i = 0; i < ITEM_IDS.length; i++)
        {
            if (ITEM_IDS[i] == itemId)
            {
                template1 = NpcTable.getInstance().getTemplate(NPC_IDS[i]);
                break;
            }
        }

        if (template1 == null)
            return;

        L2Object target = activeChar.getTarget();
        if (target == null)
            target = activeChar;

        try
        {
            L2Spawn spawn = new L2Spawn(template1);
            spawn.setId(IdFactory.getInstance().getNextId());
            spawn.setLocx(target.getX());
            spawn.setLocy(target.getY());
            spawn.setLocz(target.getZ());
            L2World.getInstance().storeObject(spawn.spawnOne());

            activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);

            SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
            sm.addString("Created " + template1.name + " at x: " + spawn.getLocx() + " y: " + spawn.getLocy() + " z: " + spawn.getLocz());
            activeChar.sendPacket(sm);
        }
        catch (Exception e)
        {
            SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
            sm.addString("Target is not ingame.");
            activeChar.sendPacket(sm);
        }
    }


	public int[] getItemIds()
    {
        return ITEM_IDS;
    }
}
