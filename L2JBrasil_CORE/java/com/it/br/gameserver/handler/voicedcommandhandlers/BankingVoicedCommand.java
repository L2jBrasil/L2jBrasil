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

import com.it.br.configuration.settings.CommandSettings;
import com.it.br.gameserver.handler.IVoicedCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;

import static com.it.br.configuration.Configurator.getSettings;

/**
 * This class trades Gold Bars for Adena and vice versa.
 *
 * @author Ahmed
 */
public class BankingVoicedCommand implements IVoicedCommandHandler
{
	private static String[] _voicedCommands = { "bank", "withdraw", "deposit" };


	public boolean useVoicedCommand(String command, L2PcInstance activeChar,String target)
	{
		CommandSettings commandSettings = getSettings(CommandSettings.class);
		int goldBarId = commandSettings.getBankingGoldbarId();
		int goldBarCount = commandSettings.getBankingGoldbarCount();
		int adenaCount = commandSettings.getBankingAdenaCount();
		if (!activeChar.getFloodProtectors().getBankingSystem().tryPerformAction("BankingSystem"))
		{
        	activeChar.sendMessage("You can not use Banking System so fast!");
        	return false;
		}
	    if (command.equalsIgnoreCase("bank"))
		{
			activeChar.sendMessage(".deposit (" + adenaCount + " Adena = " + goldBarCount + " Goldbar) / .withdraw (" + goldBarCount + " Goldbar = " + adenaCount + " Adena)");
		}
	    else if (command.equalsIgnoreCase("deposit"))
		{
	    	if (activeChar.getInventory().getInventoryItemCount(57, 0) >= adenaCount)
			{
				InventoryUpdate iu = new InventoryUpdate();
				activeChar.getInventory().reduceAdena("Goldbar", adenaCount, activeChar, null);
				activeChar.getInventory().addItem("Goldbar", goldBarId, goldBarCount, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(iu);
				activeChar.sendMessage("Thank you, you now have " + goldBarCount + " Goldbar(s), and " + adenaCount + " less adena.");
			}
	    	else
			{
				activeChar.sendMessage("You do not have enough Adena to convert to Goldbar(s), you need " + adenaCount + " Adena.");
			}
		}
	    else if (command.equalsIgnoreCase("withdraw"))
		{
			if (activeChar.getInventory().getInventoryItemCount(goldBarId, 0) >= goldBarCount)
			{
				InventoryUpdate iu = new InventoryUpdate();
				activeChar.getInventory().destroyItemByItemId("Adena", goldBarId, goldBarCount, activeChar, null);
				activeChar.getInventory().addAdena("Adena", adenaCount, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(iu);
				activeChar.sendMessage("Thank you, you now have " + adenaCount + " Adena, and " + goldBarCount + " less Goldbar(s).");
			}
			else
			{
				activeChar.sendMessage("You do not have any Goldbars to turn into " + adenaCount + " Adena.");
			}
		}
		return true;
	}

	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}