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
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.Inventory;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.CharInfo;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.network.serverpackets.UserInfo;
import com.it.br.gameserver.util.IllegalPlayerAction;
import com.it.br.gameserver.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * This class handles following admin commands:
 * - enchant_armor
 *
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminEnchant implements IAdminCommandHandler
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

    public AdminEnchant()
    {
        admin.put("admin_seteh", Config.admin_seteh);
        admin.put("admin_setec", Config.admin_setec);
        admin.put("admin_seteg", Config.admin_seteg);
        admin.put("admin_setel", Config.admin_setel);
        admin.put("admin_seteb", Config.admin_seteb);
        admin.put("admin_setew", Config.admin_setew);
        admin.put("admin_setes", Config.admin_setes);
        admin.put("admin_setle", Config.admin_setle);
        admin.put("admin_setre", Config.admin_setre);
        admin.put("admin_setlf", Config.admin_setlf);
        admin.put("admin_setrf", Config.admin_setrf);
        admin.put("admin_seten", Config.admin_seten);
        admin.put("admin_setun", Config.admin_setun);
        admin.put("admin_setba", Config.admin_setba);
        admin.put("admin_enchant", Config.admin_enchant);
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

        if (command.equals("admin_enchant"))
        {
            showMainPage(activeChar);
        } else
        {
            int armorType = -1;

            if (command.startsWith("admin_seteh"))
                armorType = Inventory.PAPERDOLL_HEAD;
            else if (command.startsWith("admin_setec"))
                armorType = Inventory.PAPERDOLL_CHEST;
            else if (command.startsWith("admin_seteg"))
                armorType = Inventory.PAPERDOLL_GLOVES;
            else if (command.startsWith("admin_seteb"))
                armorType = Inventory.PAPERDOLL_FEET;
            else if (command.startsWith("admin_setel"))
                armorType = Inventory.PAPERDOLL_LEGS;
            else if (command.startsWith("admin_setew"))
                armorType = Inventory.PAPERDOLL_RHAND;
            else if (command.startsWith("admin_setes"))
                armorType = Inventory.PAPERDOLL_LHAND;
            else if (command.startsWith("admin_setle"))
                armorType = Inventory.PAPERDOLL_LEAR;
            else if (command.startsWith("admin_setre"))
                armorType = Inventory.PAPERDOLL_REAR;
            else if (command.startsWith("admin_setlf"))
                armorType = Inventory.PAPERDOLL_LFINGER;
            else if (command.startsWith("admin_setrf"))
                armorType = Inventory.PAPERDOLL_RFINGER;
            else if (command.startsWith("admin_seten"))
                armorType = Inventory.PAPERDOLL_NECK;
            else if (command.startsWith("admin_setun"))
                armorType = Inventory.PAPERDOLL_UNDER;
            else if (command.startsWith("admin_setba"))
                armorType = Inventory.PAPERDOLL_BACK;

            if (armorType != -1)
            {
                try
                {
                    int ench = Integer.parseInt(command.substring(12));

                    // check value
                    if (ench < 0 || ench > 65535)
                        activeChar.sendMessage("You must set the enchant level to be between 0-65535.");
                    else
					{
                    	L2Object target = activeChar.getTarget();
                    	L2PcInstance player = (L2PcInstance) target;
                    	if (ench > Config.GM_OVER_ENCHANT && Config.GM_OVER_ENCHANT != 0 && player != null && !player.isGM())
                    	{
                    		player.sendMessage("A GM tried to overenchant you. You will both be banned.");  
                    		Util.handleIllegalPlayerAction(player,"The player "+player.getName()+" has been edited. BAN!", IllegalPlayerAction.PUNISH_KICKBAN);  
	                    	activeChar.sendMessage("You tried to overenchant somebody. You will both be banned.");  
	                    	Util.handleIllegalPlayerAction(activeChar,"The GM "+activeChar.getName()+" has overenchanted the player "+player.getName()+". BAN!", IllegalPlayerAction.PUNISH_KICKBAN);;
                    	}
                    else
                        setEnchant(activeChar, ench, armorType);
                }
             }
                    catch (StringIndexOutOfBoundsException e)
                {
                    if (Config.DEVELOPER) System.out.println("Set enchant error: " + e);
                    activeChar.sendMessage("Please specify a new enchant value.");
                }
                catch (NumberFormatException e)
                {
                    if (Config.DEVELOPER) System.out.println("Set enchant error: " + e);
                    activeChar.sendMessage("Please specify a valid new enchant value.");
                }
            }

            // show the enchant menu after an action
            showMainPage(activeChar);
        }

        return true;
    }

    private void setEnchant(L2PcInstance activeChar, int ench, int armorType)
    {
        // get the target
        L2Object target = activeChar.getTarget();
        if (target == null) target = activeChar;
        L2PcInstance player = null;
        if (target instanceof L2PcInstance)
        {
            player = (L2PcInstance) target;
        }
        else
        {
            activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
            return;
        }

        // now we need to find the equipped weapon of the targeted character...
        int curEnchant = 0; // display purposes only
        L2ItemInstance itemInstance = null;

        // only attempt to enchant if there is a weapon equipped
        L2ItemInstance parmorInstance = player.getInventory().getPaperdollItem(armorType);
        if (parmorInstance != null && parmorInstance.getEquipSlot() == armorType)
        {
            itemInstance = parmorInstance;
        } else
        {
            // for bows and double handed weapons
            parmorInstance = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LRHAND);
            if (parmorInstance != null && parmorInstance.getEquipSlot() == Inventory.PAPERDOLL_LRHAND)
                itemInstance = parmorInstance;
        }

        if (itemInstance != null)
        {
            curEnchant = itemInstance.getEnchantLevel();

            // set enchant value
            player.getInventory().unEquipItemInSlotAndRecord(armorType);
            curEnchant = itemInstance.getEnchantLevel();
            itemInstance.setEnchantLevel(ench);
            player.getInventory().equipItemAndRecord(itemInstance);

            // send packets
            InventoryUpdate iu = new InventoryUpdate();
            iu.addModifiedItem(itemInstance);
            player.sendPacket(iu);
            player.broadcastPacket(new CharInfo(player));
            player.sendPacket(new UserInfo(player));

            // informations
            activeChar.sendMessage("Changed enchantment of " + player.getName() + "'s " + itemInstance.getItem().getName() + " from " + curEnchant + " to " + ench + ".");
            player.sendMessage("Admin has changed the enchantment of your " + itemInstance.getItem().getName() + " from " + curEnchant + " to " + ench + ".");

            // log
            GMAudit.auditGMAction(activeChar.getName(), "enchant", player.getName(), itemInstance.getItem().getName() + " from " + curEnchant + " to " + ench);
        }
    }

    private void showMainPage(L2PcInstance activeChar)
    {
    	AdminHelpPage.showHelpPage(activeChar, "enchant.htm");
    }
}