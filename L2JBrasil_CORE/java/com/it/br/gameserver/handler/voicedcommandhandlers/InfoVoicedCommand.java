/* This program is free software; you can redistribute it and/or modify
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

import com.it.br.gameserver.GameServer;
import com.it.br.gameserver.cache.HtmCache;
import com.it.br.gameserver.handler.IVoicedCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * @author Rayder
 *
 */
public class InfoVoicedCommand implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = {"info"};

	private static final Logger _log = LoggerFactory.getLogger(GameServer.class);


	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equalsIgnoreCase("info"))
		{
			String htmFile = "data/html/info/info.htm";
			String htmContent = HtmCache.getInstance().getHtm(htmFile);
			if (htmContent != null)
			{
				NpcHtmlMessage infoHtml = new NpcHtmlMessage(1);
				infoHtml.setHtml(htmContent);
				activeChar.sendPacket(infoHtml);
			}
			else
			{
				activeChar.sendMessage("Function temporary disabled.");
				_log.info("Failed to load ServerInfos file!");
			}
			return true;
		}
		return false;
    }


	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}