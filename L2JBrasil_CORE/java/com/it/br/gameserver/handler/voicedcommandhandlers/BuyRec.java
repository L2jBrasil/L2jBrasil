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
package com.it.br.gameserver.handler.voicedcommandhandlers;

import com.it.br.Config;
import com.it.br.gameserver.handler.IVoicedCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author Rayder
 */
public class BuyRec implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = {"buyrec"};


	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equalsIgnoreCase("buyrec"))
		{
            if(activeChar.getInventory().getItemByItemId(Config.REC_ITEM_ID) != null && activeChar.getInventory().getItemByItemId(Config.REC_ITEM_ID).getCount() >= Config.REC_ITEM_COUNT)
            {
            	InventoryUpdate iu = new InventoryUpdate();
            	activeChar.getInventory().destroyItemByItemId("Rec", Config.REC_ITEM_ID, Config.REC_ITEM_COUNT, activeChar, activeChar.getTarget());
            	activeChar.setRecomHave(activeChar.getRecomHave() + Config.REC_REWARD);
                activeChar.sendMessage("You Have Earned "+Config.REC_REWARD+" Recomends.");
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(iu);
				activeChar.broadcastUserInfo();
              }
            else
            {
               	activeChar.sendMessage("You don't have enought items");
                return true;
            }
		}
		return false;
		}

	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}