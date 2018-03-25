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

import com.it.br.Config;
import com.it.br.configuration.settings.CommandSettings;
import com.it.br.configuration.settings.L2JModsSettings;
import com.it.br.gameserver.handler.voicedcommandhandlers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.it.br.configuration.Configurator.getSettings;

public class VoicedCommandHandler
{
	private static Logger _log = LoggerFactory.getLogger(ItemHandler.class);

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
		
		CommandSettings commandSettings = getSettings(CommandSettings.class);
		
		if (commandSettings.isLocVoiceCommandEnabled())
			registerVoicedCommandHandler(new LocationVoicedCommand());

		if (commandSettings.isAwayStatusEnabled())
			registerVoicedCommandHandler(new AwayVoicedCommand());

		if (commandSettings.isBankingEnabled()) 
	    	registerVoicedCommandHandler(new BankingVoicedCommand());

		if (commandSettings.isBuyRecEnabled())
			registerVoicedCommandHandler(new BuyRecVoicedCommand());

		if (commandSettings.isInfoViewEnabled())
			registerVoicedCommandHandler(new InfoVoicedCommand());

		if (commandSettings.isOnlinePlayersCommandEnabled())
			registerVoicedCommandHandler(new OnlineVoicedCommand());

		if (commandSettings.isResCommandEnabled())
			registerVoicedCommandHandler(new ResVoicedCommand());

		if (commandSettings.isStatViewEnabled())
			registerVoicedCommandHandler(new StatVoicedCommand());

		if (commandSettings.isTradeOffCommandEnabled())
			registerVoicedCommandHandler(new TradeOffVoicedCommand());

		if (commandSettings.isVipTeleportEnabled())
			registerVoicedCommandHandler(new VipTeleportVoicedCommand());

		if (getSettings(L2JModsSettings.class).isWeddingEnabled())
			registerVoicedCommandHandler(new WeddingVoicedCommand());

		if (commandSettings.isStatsCommandEnabled())
			registerVoicedCommandHandler(new StatsVoicedCommand());

		if (commandSettings.isCastleCommandEnabled())
			registerVoicedCommandHandler(new CastleVoicedCommand());

		if (commandSettings.isSetCommandEnabled())
			registerVoicedCommandHandler(new SetVoicedCommand());
	}

	public void registerVoicedCommandHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		for (int i = 0; i < ids.length; i++)
		{
			if (Config.DEBUG) _log.debug("Adicionando handler para o comando "+ids[i]);
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
			_log.debug("Obter handler para o comando: " + command + " -> "+(_datatable.get(command) != null));
		return _datatable.get(command);
	}
    public int size()
    {
        return _datatable.size();
    }
}