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
import com.it.br.gameserver.handler.IChatHandler;
import com.it.br.gameserver.handler.IVoicedCommandHandler;
import com.it.br.gameserver.handler.VoicedCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.CreatureSay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class ChatAll implements IChatHandler
{
	private static final int[] COMMAND_IDS = { 0 };
	private static Logger _log = LoggerFactory.getLogger(ChatAll.class);

	/**
	 * Handle chat type 'all'
	 *
	 * @see com.it.br.gameserver.handler.IChatHandler#handleChat(int, com.it.br.gameserver.model.actor.instance.L2PcInstance, java.lang.String)
	 */

	public void handleChat(int type, L2PcInstance activeChar, String target, String text)
	{
		if (text.startsWith("."))
		{
			StringTokenizer st = new StringTokenizer(text);
			IVoicedCommandHandler vch;
			String command = "";
			if (st.countTokens() > 1)
			{
				command = st.nextToken().substring(1);
				target = text.substring(command.length() + 2);
				vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
			}
			else
			{
				command = text.substring(1);
				if (Config.DEBUG) {
					_log.info("Command: " + command);
				}
				vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
			}
			if (vch != null)
			{
				vch.useVoicedCommand(command, activeChar, target);
			}
			else
			{
				if (Config.DEBUG) {
					_log.warn("No handler registered for bypass '" + command + "'");
				}
			}
		}
		else
		{
			CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
			for (L2PcInstance player : activeChar.getKnownList().getKnownPlayers().values())
			{
				if (player != null && activeChar.isInsideRadius(player, 1250, false, true))
				{
					player.sendPacket(cs);
				}
			}
			activeChar.sendPacket(cs);
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