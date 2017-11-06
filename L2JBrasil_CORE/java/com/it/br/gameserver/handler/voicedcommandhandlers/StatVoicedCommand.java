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
package com.it.br.gameserver.handler.voicedcommandhandlers;

import com.it.br.gameserver.handler.IVoicedCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;

public class StatVoicedCommand implements IVoicedCommandHandler
{
    private static final String[] VOICED_COMMANDS = { "stat" };


	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
    {
        if (command.equalsIgnoreCase("stat"))
        {
        	if (activeChar.getTarget()==activeChar)
            {
                activeChar.sendMessage("You can't see your own status.");
                return false;
            }
            if (activeChar.getTarget()==null)
            {
                activeChar.sendMessage("You have no one targeted.");
                return false;
            }
            if (!(activeChar.getTarget() instanceof L2PcInstance))
            {
                activeChar.sendMessage("You can only get the info of a player.");

                return false;
            }
            
            NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
            L2PcInstance targetp = (L2PcInstance)activeChar.getTarget();



            StringBuilder replyMSG = new StringBuilder("<html><body><center>");
			replyMSG.append("<br><br><font color=\"00FF00\">=========>>" + targetp.getName() + "<<=========</font><br>");
			replyMSG.append("<font color=\"FF0000\">Level: " + targetp.getLevel() + "</font><br>");

			if(targetp.getClan() != null)
			{
				replyMSG.append("<font color=\"FF0000\">Clan: " + targetp.getClan().getName() + "</font><br>");
				replyMSG.append("<font color=\"FF0000\">Alliance: " + targetp.getClan().getAllyName() + "</font><br>");
			}
			else
			{
				replyMSG.append("<font color=\"FF0000\">Alliance: None</font><br>");
				replyMSG.append("<font color=\"FF0000\">Clan: None</font><br>");
			}

			replyMSG.append("<font color=\"FF0000\">Adena: " + targetp.getAdena() + "</font><br>");

			if(targetp.getInventory().getItemByItemId(6393) == null)
			{
				replyMSG.append("<font color=\"FF0000\">Medals : 0</font><br>");
			}
			else
			{
				replyMSG.append("<font color=\"FF0000\">Medals : " + targetp.getInventory().getItemByItemId(6393).getCount() + "</font><br>");
			}

			if(targetp.getInventory().getItemByItemId(3470) == null)
			{
				replyMSG.append("<font color=\"FF0000\">Gold Bars : 0</font><br>");
			}
			else
			{
				replyMSG.append("<font color=\"FF0000\">Gold Bars : " + targetp.getInventory().getItemByItemId(3470).getCount() + "</font><br>");
			}

			replyMSG.append("<font color=\"FF0000\">PvP Kills: " + targetp.getPvpKills() + "</font><br>");
			replyMSG.append("<font color=\"FF0000\">PvP Flags: " + targetp.getPvpFlag() + "</font><br>");
			replyMSG.append("<font color=\"FF0000\">PK Kills: " + targetp.getPkKills() + "</font><br>");
			replyMSG.append("<font color=\"FF0000\">HP, CP, MP: " + targetp.getMaxHp() + ", " + targetp.getMaxCp() + ", " + targetp.getMaxMp() + "</font><br>");
			
			if (targetp.getActiveWeaponInstance() == null)
			{
				replyMSG.append("<font color=\"FF0000\">No Weapon!</font><br>");
			}
			else
		    {			
			    replyMSG.append("<font color=\"FF0000\">Wep Enchant: " + targetp.getActiveWeaponInstance().getEnchantLevel() + "</font><br>");
			}
			
			replyMSG.append("<font color=\"00FF00\">=========>>" + targetp.getName() + "<<=========" + "</font><br>");
			replyMSG.append("</center></body></html>");

			adminReply.setHtml(replyMSG.toString());
			activeChar.sendPacket(adminReply);

			adminReply = null;
			targetp = null;
			replyMSG = null;
        }
			return true;
		}

	public String[] getVoicedCommandList()
    {
        return VOICED_COMMANDS;
    }
}