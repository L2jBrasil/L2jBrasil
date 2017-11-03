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
package com.it.br.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import com.it.br.Config;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.templates.L2Item;

/**
 * This class handles following admin commands:
 * - itemcreate = show menu
 * - create_item <id> [num] = creates num items with respective id, if num is not specified, assumes 1.
 *
 * @version $Revision: 1.2.2.2.2.3 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminCreateItem implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_itemcreate",
		"admin_create_item",
		"admin_mass_create"
	};
	private static final int REQUIRED_LEVEL = Config.GM_CREATE_ITEM;


	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
		{
			if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM()))
				return false;
		}

		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null?activeChar.getTarget().getName():"no-target"), "");

		if (command.equals("admin_itemcreate"))
		{
			AdminHelpPage.showHelpPage(activeChar, "itemcreation.htm");
		}
		else if (command.startsWith("admin_create_item")) 
		{
			try
			{
				String val = command.substring(17);
				StringTokenizer st = new StringTokenizer(val);
				if (st.countTokens()== 2)
				{
					String id = st.nextToken();
					int idval = Integer.parseInt(id);
					String num = st.nextToken();
					int numval = Integer.parseInt(num);
					createItem(activeChar,idval,numval);
				}
				else if (st.countTokens()== 1)
				{
					String id = st.nextToken();
					int idval = Integer.parseInt(id);
					createItem(activeChar,idval,1);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //itemcreate <itemId> [amount]");
			}
			catch (NumberFormatException nfe)
			{
				activeChar.sendMessage("Specify a valid number.");
			}
			AdminHelpPage.showHelpPage(activeChar, "itemcreation.htm");
		}
        else if (command.startsWith("admin_mass_create")) 
	    { 
	    try 
	    { 
	         String val = command.substring(17); 
	         StringTokenizer st = new StringTokenizer(val); 
	         if (st.countTokens()== 2) 
             { 
	             String id = st.nextToken(); 
                 int idval = Integer.parseInt(id); 
                 String num = st.nextToken(); 
	             int numval = Integer.parseInt(num); 
	             massCreate(activeChar,idval,numval); 
             } 
             else if (st.countTokens()== 1) 
             { 
  	              String id = st.nextToken(); 
	              int idval = Integer.parseInt(id); 
	              massCreate(activeChar,idval,1); 
                  } 
	         } 
	         catch (StringIndexOutOfBoundsException e) 
	         { 
                  activeChar.sendMessage("Usage: //itemcreate <itemId> [amount]"); 
	         } 
	         catch (NumberFormatException nfe) 
             { 
	              activeChar.sendMessage("Specify a valid number."); 
	         } 
	    } 
		return true;
        } 
	    private void massCreate(L2PcInstance activeChar, int id, int num) 
        { 
	        for (L2PcInstance _players : L2World.getInstance().getAllPlayers()) 
	        { 
            if (_players == activeChar) continue; 
            _players.getInventory().addItem("Admin", id, num, _players, null); 

	        ItemList il = new ItemList(_players, true); 
            _players.sendPacket(il); 
            } 
 
            activeChar.sendMessage("You have spawned " + num + " item(s) number " + id + " in all chars inventory."); 
	}


	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	private boolean checkLevel(int level)
	{
		return (level >= REQUIRED_LEVEL);
	}

	private void createItem(L2PcInstance activeChar, int id, int num)
	{
		if (num > 20)
		{
			L2Item template = ItemTable.getInstance().getTemplate(id);
			if (!template.isStackable())
			{
				activeChar.sendMessage("This item does not stack - Creation aborted.");
				return;
			}
		}

		activeChar.getInventory().addItem("Admin", id, num, activeChar, null);

		ItemList il = new ItemList(activeChar, true);
		activeChar.sendPacket(il);

		activeChar.sendMessage("You have spawned " + num + " item(s) number " + id + " in your inventory.");
	}
}
