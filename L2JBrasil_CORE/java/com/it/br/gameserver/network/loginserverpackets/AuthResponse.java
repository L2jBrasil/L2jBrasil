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
package com.it.br.gameserver.network.loginserverpackets;


/**
 * @author -Wooden-
 *
 */
public class AuthResponse extends LoginServerBasePacket
{

	private int _serverId;
	private String _serverName;

	/**
	 * @param decrypt
	 */
	public AuthResponse(byte[] decrypt)
	{
		super(decrypt);
		_serverId = readC();
		_serverName = readS();
	}

	/**
	 * @return Returns the serverId.
	 */
	public int getServerId()
	{
		return _serverId;
	}

	/**
	 * @return Returns the serverName.
	 */
	public String getServerName()
	{
		return _serverName;
	}

}
