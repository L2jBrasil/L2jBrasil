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

import static com.it.br.configuration.Configurator.getSettings;

import com.it.br.configuration.settings.CommandSettings;
import com.it.br.gameserver.handler.IVoicedCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author Rayder
 */
public class BuyRecVoicedCommand implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = {"buyrec"};


	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equalsIgnoreCase("buyrec"))
		{
			CommandSettings commandSettings = getSettings(CommandSettings.class);
			int itemId = commandSettings.getRecItemID();
			int itemCount = commandSettings.getRecItemCount();
			int rewardCount = commandSettings.getRecReward();
            if(activeChar.getInventory().getItemByItemId(itemId) != null && activeChar.getInventory().getItemByItemId(itemId).getCount() >= itemCount)
            {
            	InventoryUpdate iu = new InventoryUpdate();
            	activeChar.getInventory().destroyItemByItemId("Rec", itemId, itemCount, activeChar, activeChar.getTarget());
            	activeChar.setRecomHave(activeChar.getRecomHave() + rewardCount);
                activeChar.sendMessage("You Have Earned " + rewardCount + " Recomends.");
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