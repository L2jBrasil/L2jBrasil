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
package com.it.br.gameserver.network.clientpackets;

import java.nio.BufferUnderflowException;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.handler.ChatHandler;
import com.it.br.gameserver.handler.IChatHandler;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance.PunishLevel;
import com.it.br.gameserver.network.SystemChatChannelId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.util.Util;

public final class Say2 extends L2GameClientPacket
{
	private static final String _C__38_SAY2 = "[C] 38 Say2";
	private static Logger _log = Logger.getLogger(Say2.class.getName());
	private static Logger _logChat = Logger.getLogger("chat");

	public final static int ALL = 0;
	public final static int SHOUT = 1; //!
	public final static int TELL = 2;
	public final static int PARTY = 3; //#
	public final static int CLAN = 4;  //@
	public final static int GM = 5;
	public final static int PETITION_PLAYER = 6; // used for petition
	public final static int PETITION_GM = 7; //* used for petition
	public final static int TRADE = 8; //+
	public final static int ALLIANCE = 9; //$
	public final static int ANNOUNCEMENT = 10;
	public final static int PARTYROOM_ALL = 16; //(Red)
	public final static int PARTYROOM_COMMANDER = 15; //(Yellow)
	public final static int HERO_VOICE = 17;	

	private final static String[] CHAT_NAMES =
	{
		"ALL  ","SHOUT","TELL ","PARTY","CLAN ","GM","PETITION_PLAYER","PETITION_GM",
		"TRADE","ALLIANCE","ANNOUNCEMENT","WILLCRASHCLIENT:)","FAKEALL?","FAKEALL?",
			"FAKEALL?","PARTYROOM_ALL","PARTYROOM_COMMANDER","HERO_VOICE"
	};

	private static final String[] WALKER_COMMAND_LIST =
	{
		"USESKILL", "USEITEM", "BUYITEM", "SELLITEM", "SAVEITEM", "LOADITEM", "MSG", "DELAY", "LABEL", "JMP", "CALL",
		"RETURN", "MOVETO", "NPCSEL", "NPCDLG", "DLGSEL", "CHARSTATUS", "POSOUTRANGE", "POSINRANGE", "GOHOME", "SAY",
		"EXIT", "PAUSE", "STRINDLG", "STRNOTINDLG", "CHANGEWAITTYPE", "FORCEATTACK", "ISMEMBER", "REQUESTJOINPARTY",
		"REQUESTOUTPARTY", "QUITPARTY", "MEMBERSTATUS", "CHARBUFFS", "ITEMCOUNT", "FOLLOWTELEPORT"
	};

	private String _text;
	private int _type;
	private SystemChatChannelId _type2Check;
	private String _target;

	protected void readImpl()
	{
		_text = readS();
		try
		{
			_type = readD();
			_type2Check = SystemChatChannelId.getChatType(_type);
		}
		catch (BufferUnderflowException e)
		{
			_type = CHAT_NAMES.length;
			_type2Check = SystemChatChannelId.CHAT_NONE;
		}
		_target = (_type == TELL) ? readS() : null;
	}


	protected void runImpl()
	{
		if (Config.DEBUG)
			_log.info("Say2: Msg Type = '" + _type + "' Text = '" + _text + "'.");

		if(_type < 0 || _type >= CHAT_NAMES.length)
		{
			_log.warning("Say2: Invalid type: "+_type);
			return;
		}
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (!getClient().getFloodProtectors().getSayAction().tryPerformAction("Say2"))
		{
			activeChar.sendMessage("You cannot speak too fast.");
			return;
		}
		if (_type2Check == SystemChatChannelId.CHAT_NONE
				|| _type2Check == SystemChatChannelId.CHAT_ANNOUNCE
				|| _type2Check == SystemChatChannelId.CHAT_CRITICAL_ANNOUNCE
				|| _type2Check == SystemChatChannelId.CHAT_SYSTEM
				|| _type2Check == SystemChatChannelId.CHAT_CUSTOM
				|| (_type2Check == SystemChatChannelId.CHAT_GM_PET && !activeChar.isGM()))
		{
		   _log.warning("[Anti-PHX Announce] Illegal Chat channel was used by character: [" + activeChar.getName() + "]");
		   return;
		}	
		if (_text.length() >= 100)
        {
			activeChar.sendMessage("You can not send messages with 100 characters or more!");
			return;
        }
		if (activeChar == null)
		{
			_log.warning("[Say2.java] Active Character is null.");
			return;
		}
		if (activeChar.isOffline())
		{
			activeChar.sendMessage("Player is in offline mode.");
			return;
		}
		if (_text.isEmpty())
		{
			_log.warning(activeChar.getName() + ": sending empty text. Possible packet hack!");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			activeChar.logout();
			return;
		}
		// Even though the client can handle more characters than it's current limit allows, an overflow (critical error) happens if you pass a huge (1000+) message.
		// July 11, 2011 - Verified on High Five 4 official client as 105.
		// Allow higher limit if player shift some item (text is longer then).
		if (!activeChar.isGM() && ((_text.indexOf(8) >= 0 && _text.length() > 500) || (_text.indexOf(8) < 0 && _text.length() > 105)))
		{
			activeChar.sendMessage("Don't Spam");
			return;
		}
		if (Config.L2WALKER_PROTECTION && _type == TELL && checkBot(_text))
		{
			Util.handleIllegalPlayerAction(activeChar, "Client Emulator Detect: Player " + activeChar.getName() + " using l2walker.", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (!activeChar.isGM() && _type == ANNOUNCEMENT)
		{
			activeChar.sendMessage("You may not use the announcement channel without privileges");
			return;
		}
		if (activeChar.isChatBanned() && !_text.startsWith("."))
		{
			if (_type == ALL || _type == SHOUT || _type == TRADE || _type == HERO_VOICE)
			{
				activeChar.sendMessage("You have been punished due to your spam.Chat banned.");
				return;
			}
		}
        if (activeChar.isInJail() && Config.JAIL_DISABLE_CHAT)
        {
            if (_type == TELL || _type == SHOUT || _type == TRADE || _type == HERO_VOICE)
            {
                activeChar.sendMessage("Not allowed to speak while in jail.");
                return;
            }
        }
        if (Config.LOG_CHAT)
		{
			LogRecord record = new LogRecord(Level.INFO, _text);
			record.setLoggerName("chat");
			
			if (_type == TELL)
				record.setParameters(new Object[]{CHAT_NAMES[_type], "[" + activeChar.getName() + " to "+_target+"]"});
			else
				record.setParameters(new Object[]{CHAT_NAMES[_type], "[" + activeChar.getName() + "]"});
			
			_logChat.log(record);
		}
		
		if (_type == PETITION_PLAYER && activeChar.isGM())
			_type = PETITION_GM;
		
		
		if (_text.indexOf(8) >= 0)
			if (!parseAndPublishItem(activeChar))
				return;

		// Say Filter implementation
		if(Config.USE_SAY_FILTER)
		{
			checkText(activeChar);
		}

        IChatHandler handler = ChatHandler.getInstance().getChatHandler(_type);  
     	if (handler != null) 
		{
     		handler.handleChat(_type, activeChar, _target, _text); 
		}
	}

	private void checkText(L2PcInstance activeChar)
	{
		if(Config.USE_SAY_FILTER)
		{
			String filteredText = _text.toLowerCase();
			
			for(String pattern : Config.FILTER_LIST)
			{
				filteredText = filteredText.replaceAll("(?i)" + pattern, Config.CHAT_FILTER_CHARS);
			}

			if(!filteredText.equalsIgnoreCase(_text))
			{
				if(Config.CHAT_FILTER_PUNISHMENT.equalsIgnoreCase("chat"))
				{
					activeChar.setPunishLevel(PunishLevel.CHAT, Config.CHAT_FILTER_PUNISHMENT_PARAM);
					activeChar.sendMessage("Administrator banned you chat from " + Config.CHAT_FILTER_PUNISHMENT_PARAM + " minutes");
				}
				else if(Config.CHAT_FILTER_PUNISHMENT.equalsIgnoreCase("karma"))
				{
					activeChar.setKarma(Config.CHAT_FILTER_PUNISHMENT_KARMA);
					activeChar.sendMessage("You have get " + Config.CHAT_FILTER_PUNISHMENT_KARMA + " karma for bad words");
				}
				else if(Config.CHAT_FILTER_PUNISHMENT.equalsIgnoreCase("jail"))
				{
					activeChar.setPunishLevel(PunishLevel.JAIL, Config.CHAT_FILTER_PUNISHMENT_PARAM);
				}
				activeChar.sendMessage("The word "+_text+" is not allowed!");
				_text = filteredText;
			}
		}
	}

	private boolean checkBot(String text)
	{
		for (String botCommand : WALKER_COMMAND_LIST)
		{
			if (text.startsWith(botCommand))
				return true;
		}
		return false;
	}
	private boolean parseAndPublishItem(L2PcInstance owner)
	{
		int pos1 = -1;
		while ((pos1 = _text.indexOf(8, pos1)) > -1)
		{
			int pos = _text.indexOf("ID=", pos1);
			if (pos == -1)
				return false;
			StringBuilder result = new StringBuilder(9);
			pos += 3;
			while (Character.isDigit(_text.charAt(pos)))
				result.append(_text.charAt(pos++));
			int id = Integer.parseInt(result.toString());
			L2Object item = L2World.getInstance().findObject(id);
			if (item instanceof L2ItemInstance)
			{
				if (owner.getInventory().getItemByObjectId(id) == null)
				{
					_log.info(getClient() + " trying publish item which doesnt own! ID:" + id);
					return false;
				}
				((L2ItemInstance) item).publish();
			}
			else
			{
				_log.info(getClient() + " trying publish object which is not item! Object:" + item);
				return false;
			}
			pos1 = _text.indexOf(8, pos) + 1;
			if (pos1 == 0) // missing ending tag
			{
				_log.info(getClient() + " sent invalid publish item msg! ID:" + id);
				return false;
			}
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	public String getType()
	{
		return _C__38_SAY2;
	}
}
