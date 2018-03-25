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
package com.it.br.loginserver;

import com.it.br.Config;
import com.it.br.configuration.settings.NetworkSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.it.br.configuration.Configurator.getSettings;

/**
 *
 * @author KenM
 */
public class GameServerListener extends FloodProtectedListener
{
	private static Logger _log = LoggerFactory.getLogger(GameServerListener.class);
	private static List<GameServerThread> _gameServers = new ArrayList<>();

	public GameServerListener() throws IOException
	{
		super(getSettings(NetworkSettings.class).getLoginListenServerHostname(), getSettings(NetworkSettings.class).getLoginListenServerPort());
	}

	/**
	 * @see com.it.br.loginserver.FloodProtectedListener#addClient(java.net.Socket)
	 */
	@Override
	public void addClient(Socket s)
	{
		if (Config.DEBUG)
		{
			_log.info("Received gameserver connection from: "+s.getInetAddress().getHostAddress());
		}
		GameServerThread gst = new GameServerThread(s);
		_gameServers.add(gst);
	}

	public void removeGameServer(GameServerThread gst)
	{
		_gameServers.remove(gst);
	}
}
