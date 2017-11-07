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
package com.it.br.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


import com.it.br.Config;
import com.it.br.gameserver.handler.voicedcommandhandlers.*;

public class VoicedCommandHandler
{
	private static Logger _log = Logger.getLogger(ItemHandler.class.getName());

	private static VoicedCommandHandler _instance;

	private Map<String, IVoicedCommandHandler> _datatable;

	public static VoicedCommandHandler getInstance()
	{
		if (_instance == null)
		{
			_instance = new VoicedCommandHandler();
		}
		return _instance;
	}

	private VoicedCommandHandler()
	{
		_datatable = new HashMap<>();
		
		if (Config.ALLOW_LOC_VOICECOMMAND)
			registerVoicedCommandHandler(new LocationVoicedCommand());

		if (Config.ALLOW_AWAY_STATUS)
			registerVoicedCommandHandler(new AwayVoicedCommand());

		if (Config.BANKING_SYSTEM_ENABLED) 
	    	registerVoicedCommandHandler(new BankingVoicedCommand());

		if (Config.REC_BUY)
			registerVoicedCommandHandler(new BuyRecVoicedCommand());

		if (Config.ENABLE_INFO)
			registerVoicedCommandHandler(new InfoVoicedCommand());

		if (Config.ENABLE_ONLINE_COMMAND)
			registerVoicedCommandHandler(new OnlineVoicedCommand());

		if (Config.ALLOW_RES_COMMAND)
			registerVoicedCommandHandler(new ResVoicedCommand());

		if (Config.ALLOW_STAT_VIEW)
			registerVoicedCommandHandler(new StatVoicedCommand());

		if (Config.ALLOW_TRADEOFF_VOICE_COMMAND)
			registerVoicedCommandHandler(new TradeOffVoicedCommand());

		if (Config.ENABLE_VIP_TELEPORT)
			registerVoicedCommandHandler(new VipTeleportVoicedCommand());

		if (Config.L2JMOD_ALLOW_WEDDING)
			registerVoicedCommandHandler(new WeddingVoicedCommand());

		if (Config.ALLOW_STATS_COMMAND)
			registerVoicedCommandHandler(new StatsVoicedCommand());

		if (Config.ALLOW_CASTLE_COMMAND)
			registerVoicedCommandHandler(new CastleVoicedCommand());

		if (Config.ALLOW_SET_COMMAND)
			registerVoicedCommandHandler(new SetVoicedCommand());
	}

	public void registerVoicedCommandHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		for (int i = 0; i < ids.length; i++)
		{
			if (Config.DEBUG) _log.fine("Adicionando handler para o comando "+ids[i]);
			_datatable.put(ids[i], handler);
		}
	}

	public IVoicedCommandHandler getVoicedCommandHandler(String voicedCommand)
	{
		String command = voicedCommand;
		if (voicedCommand.indexOf(" ") != -1)
		{
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		}
		if (Config.DEBUG)
			_log.fine("Obter handler para o comando: " + command + " -> "+(_datatable.get(command) != null));
		return _datatable.get(command);
	}
    public int size()
    {
        return _datatable.size();
    }
}