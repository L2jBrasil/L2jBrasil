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
 * This class trades Gold Bars for Adena and vice versa.
 *
 * @author Ahmed
 */
public class BankingVoicedCommand implements IVoicedCommandHandler
{
	private static String[] _voicedCommands = { "bank", "withdraw", "deposit" };


	public boolean useVoicedCommand(String command, L2PcInstance activeChar,String target)
	{
		if (!activeChar.getFloodProtectors().getBankingSystem().tryPerformAction("BankingSystem"))
		{
        	activeChar.sendMessage("You can not use Banking System so fast!");
        	return false;
		}
	    if (command.equalsIgnoreCase("bank"))
		{
			activeChar.sendMessage(".deposit (" + Config.BANKING_SYSTEM_ADENA + " Adena = " + Config.BANKING_SYSTEM_GOLDBARS + " Goldbar) / .withdraw (" + Config.BANKING_SYSTEM_GOLDBARS + " Goldbar = " + Config.BANKING_SYSTEM_ADENA + " Adena)");
		}
	    else if (command.equalsIgnoreCase("deposit"))
		{
	    	if (activeChar.getInventory().getInventoryItemCount(57, 0) >= Config.BANKING_SYSTEM_ADENA)
			{
				InventoryUpdate iu = new InventoryUpdate();
				activeChar.getInventory().reduceAdena("Goldbar", Config.BANKING_SYSTEM_ADENA, activeChar, null);
				activeChar.getInventory().addItem("Goldbar", Config.BANKING_SYSTEM_GB_ID, Config.BANKING_SYSTEM_GOLDBARS, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(iu);
				activeChar.sendMessage("Thank you, you now have " + Config.BANKING_SYSTEM_GOLDBARS + " Goldbar(s), and " + Config.BANKING_SYSTEM_ADENA + " less adena.");
			}
	    	else
			{
				activeChar.sendMessage("You do not have enough Adena to convert to Goldbar(s), you need " + Config.BANKING_SYSTEM_ADENA + " Adena.");
			}
		}
	    else if (command.equalsIgnoreCase("withdraw"))
		{
			if (activeChar.getInventory().getInventoryItemCount(Config.BANKING_SYSTEM_GB_ID, 0) >= Config.BANKING_SYSTEM_GOLDBARS)
			{
				InventoryUpdate iu = new InventoryUpdate();
				activeChar.getInventory().destroyItemByItemId("Adena", Config.BANKING_SYSTEM_GB_ID, Config.BANKING_SYSTEM_GOLDBARS, activeChar, null);
				activeChar.getInventory().addAdena("Adena", Config.BANKING_SYSTEM_ADENA, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(iu);
				activeChar.sendMessage("Thank you, you now have " + Config.BANKING_SYSTEM_ADENA + " Adena, and " + Config.BANKING_SYSTEM_GOLDBARS + " less Goldbar(s).");
			}
			else
			{
				activeChar.sendMessage("You do not have any Goldbars to turn into " + Config.BANKING_SYSTEM_ADENA + " Adena.");
			}
		}
		return true;
	}

	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}