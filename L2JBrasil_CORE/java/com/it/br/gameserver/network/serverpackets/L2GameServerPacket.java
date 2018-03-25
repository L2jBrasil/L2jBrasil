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
package com.it.br.gameserver.network.serverpackets;

import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.network.L2GameClient;
import com.l2jserver.mmocore.network.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.it.br.configuration.Configurator.getSettings;

/**
 *
 * @author  KenM
 */
public abstract class L2GameServerPacket extends SendablePacket<L2GameClient>
{
	private static final Logger _log = LoggerFactory.getLogger(L2GameServerPacket.class);

	/**
	 * @see com.l2jserver.mmocore.network.SendablePacket#write()
	 */

	@Override
	protected void write()
	{
		try
		{
			writeImpl();
		}
		catch (Throwable t)
		{
			ServerSettings serverSettings = getSettings(ServerSettings.class);
			if (!serverSettings.isDebugPacketEnabled())
				return;
			
			_log.error("Client: "+getClient().toString()+" - Failed writing: "+getType());
			t.printStackTrace();
		}
	}

	public void runImpl()
	{

	}

	protected abstract void writeImpl();

	/**
	 * @return A String with this packet name for debuging purposes
	 */
	public abstract String getType();
}
