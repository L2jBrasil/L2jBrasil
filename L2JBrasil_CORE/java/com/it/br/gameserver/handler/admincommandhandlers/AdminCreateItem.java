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

import com.it.br.Config;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.templates.L2Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class handles following admin commands:
 * - itemcreate = show menu
 * - create_item <id> [num] = creates num items with respective id, if num is not specified, assumes 1.
 *
 * @version $Revision: 3.0.2 $ $Date: 2017/11/09 $
 */
public class AdminCreateItem implements IAdminCommandHandler
{
    private static Map<String, Integer> admin = new HashMap<>();

    private boolean checkPermission(String command, L2PcInstance activeChar)
    {
        if (!Config.ALT_PRIVILEGES_ADMIN)
            if (!(checkLevel(command, activeChar.getAccessLevel()) && activeChar.isGM()))
            {
                activeChar.sendMessage("E necessario ter Access Level " + admin.get(command) + " para usar o comando : " + command);
                return true;
            }
        return false;
    }

    private boolean checkLevel(String command, int level)
    {
        Integer requiredAcess = admin.get(command);
        return (level >= requiredAcess);
    }

    public AdminCreateItem()
    {
        admin.put("admin_itemcreate", Config.admin_itemcreate);
        admin.put("admin_create_item", Config.admin_create_item);
        admin.put("admin_mass_create", Config.admin_mass_create);
    }

    public Set<String> getAdminCommandList()
    {
        return admin.keySet();
    }

	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
        StringTokenizer st = new StringTokenizer(command);
        String commandName = st.nextToken();

        if(checkPermission(commandName, activeChar)) return false;

		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null?activeChar.getTarget().getName():"no-target"), "");

		if (command.equals("admin_itemcreate"))
		{
			AdminHelpPage.showHelpPage(activeChar, "itemcreation.htm");
		}
		else if (command.startsWith("admin_create_item")) 
		{
			try
			{
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
