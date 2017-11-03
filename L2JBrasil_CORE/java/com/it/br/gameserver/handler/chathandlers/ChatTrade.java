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

public class ChatTrade implements IChatHandler
{
	private static final int[] COMMAND_IDS = { 8 };

	public void handleChat(int type, L2PcInstance activeChar, String target, String text)
	{
		if (!activeChar.getFloodProtectors().getTradeVoice().tryPerformAction("TradeChat"))
        {
			activeChar.sendMessage("You can not use Trade Chat so fast!");
			return;
        }
		
		CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);

		int region = MapRegionTable.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());
		
		if (Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("on") || Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("gm") && activeChar.isGM())
		{
            if(Config.TRADE_CHAT_PVP)
            {
               if((activeChar.getPvpKills() <= Config.TRADE_PVP_AMOUNT) && !activeChar.isGM())
               {
                  activeChar.sendMessage(""+ Config.TRADE_PVP_AMOUNT+" PvP's you need to use Global chat.");
                  return;
               }
               for (L2PcInstance player : L2World.getInstance().getAllPlayers())
               {
            	   player.sendPacket(cs);
               }
            }
            else 
            for (L2PcInstance player : L2World.getInstance().getAllPlayers())
            {
            	player.sendPacket(cs);
            }
		}
		else if (Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("limited"))
		{
            if(Config.TRADE_CHAT_PVP)
            {
               if((activeChar.getPvpKills() <= Config.TRADE_PVP_AMOUNT) && !activeChar.isGM())
               {
                  activeChar.sendMessage(""+ Config.TRADE_PVP_AMOUNT+" PvP's you need to use Global chat.");
                  return;
               }
				for (L2PcInstance player : L2World.getInstance().getAllPlayers())
				{
					if (region == MapRegionTable.getInstance().getMapRegion(player.getX(), player.getY()) && !BlockList.isBlocked(player, activeChar) && player.getInstanceId() == activeChar.getInstanceId())
					{
						player.sendPacket(cs);
					}
				}
            }
			else
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if (region == MapRegionTable.getInstance().getMapRegion(player.getX(), player.getY()) && !BlockList.isBlocked(player, activeChar) && player.getInstanceId() == activeChar.getInstanceId())
				{
					player.sendPacket(cs);
				}
			}
		}
	}
	/**
	 * Returns the chat types registered to this handler
	 *
	 * @see com.it.br.gameserver.handler.IChatHandler#getChatTypeList()
	 */

	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}