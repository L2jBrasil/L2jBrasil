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
package com.it.br.gameserver.handler.chathandlers;

import java.util.Collection;

import com.it.br.Config;
import com.it.br.gameserver.datatables.xml.MapRegionTable;
import com.it.br.gameserver.handler.IChatHandler;
import com.it.br.gameserver.model.BlockList;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.CreatureSay;

/**
 * @Reworked *Slayer
 */

public class ChatShout implements IChatHandler
{
	private static final int[] COMMAND_IDS ={1};

	public void handleChat(int type, L2PcInstance activeChar, String target, String text)
	{
		if (!activeChar.getFloodProtectors().getGlobalChat().tryPerformAction("GlobalChat"))
        {
			activeChar.sendMessage("You can not use Global Chat so fast!");
			return;
        }

		text.replaceAll("\n", "");
		CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers();
		int region = MapRegionTable.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());

		if (Config.DEFAULT_GLOBAL_CHAT.equalsIgnoreCase("on") || (Config.DEFAULT_GLOBAL_CHAT.equalsIgnoreCase("gm") && activeChar.isGM()))
		{
            if(Config.SHOUT_CHAT_PVP)
            {
               if((activeChar.getPvpKills() <= Config.SHOUT_PVP_AMOUNT) && !activeChar.isGM())
               {
                  activeChar.sendMessage(""+ Config.SHOUT_PVP_AMOUNT+" PvP's you need to use '!' chat.");
                  return;
               }
				for (L2PcInstance player : pls)
					if (region == MapRegionTable.getInstance().getMapRegion(player.getX(), player.getY()) && !BlockList.isBlocked(player, activeChar) && player.getInstanceId() == activeChar.getInstanceId())
						player.sendPacket(cs);
            }
            else
			for (L2PcInstance player : pls)
			{
				if (region == MapRegionTable.getInstance().getMapRegion(player.getX(), player.getY()) && !BlockList.isBlocked(player, activeChar) && player.getInstanceId() == activeChar.getInstanceId())
					player.sendPacket(cs);
			}
		}

		else if (Config.DEFAULT_GLOBAL_CHAT.equalsIgnoreCase("global"))
		{
            if(Config.SHOUT_CHAT_PVP)
            {
               if((activeChar.getPvpKills() <= Config.SHOUT_PVP_AMOUNT) && !activeChar.isGM())
               {
                  activeChar.sendMessage(""+ Config.SHOUT_PVP_AMOUNT+" PvP's you need to use '!' chat.");
                  return;
               }
               for (L2PcInstance player : L2World.getInstance().getAllPlayers())
               {
            	   player.sendPacket(cs);
               }
            }
            else
			for (L2PcInstance player : pls)
			{
				if (!BlockList.isBlocked(player, activeChar))
					player.sendPacket(cs);
			}
		}
	}

	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}