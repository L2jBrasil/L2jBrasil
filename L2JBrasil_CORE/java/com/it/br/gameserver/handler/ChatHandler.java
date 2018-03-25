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
package com.it.br.gameserver.handler;

import com.it.br.Config;
import com.it.br.gameserver.handler.chathandlers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ChatHandler
{
	private static Logger _log = LoggerFactory.getLogger(ChatHandler.class);
	private Map<Integer, IChatHandler> _datatable;

	public static ChatHandler getInstance()
	{
		return SingletonHolder._instance;
	}

	ChatHandler()
	{
		_datatable = new HashMap<>();
		registerChatHandler(new ChatAll());
		registerChatHandler(new ChatAlliance());
		registerChatHandler(new ChatClan());
		registerChatHandler(new ChatHeroVoice());
		registerChatHandler(new ChatParty());
		registerChatHandler(new ChatPartyRoomAll());
		registerChatHandler(new ChatPartyRoomCommander());
		registerChatHandler(new ChatPetition());
		registerChatHandler(new ChatShout());
		registerChatHandler(new ChatTell());
		registerChatHandler(new ChatTrade());
	}

	public void registerChatHandler(IChatHandler handler)
	{
		int[] ids = handler.getChatTypeList();
		for (int id : ids) {
			if (Config.DEBUG) {
				_log.debug("Adding handler for chat type " + id);
			}
			_datatable.put(id, handler);
		}
	}

	public IChatHandler getChatHandler(int chatType)
	{
		return _datatable.get(chatType);
	}

	public int size()
	{
		return _datatable.size();
	}

	private final static class SingletonHolder
	{
		protected static final ChatHandler _instance = new ChatHandler();
	}
}