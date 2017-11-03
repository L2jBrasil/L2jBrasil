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
package com.it.br.loginserver.gameserverpackets;

import com.it.br.loginserver.SessionKey;
import com.it.br.loginserver.clientpackets.ClientBasePacket;

/**
 * @author -Wooden-
 *
 */
public class PlayerAuthRequest extends ClientBasePacket
{
	private String _account;
	private SessionKey _sessionKey;

	/**
	 * @param decrypt
	 */
	public PlayerAuthRequest(byte[] decrypt)
	{
		super(decrypt);
		_account = readS();
		int playKey1 = readD();
		int playKey2 = readD();
		int loginKey1 = readD();
		int loginKey2 = readD();
		_sessionKey = new SessionKey(loginKey1, loginKey2, playKey1, playKey2);
	}
	/**
	 * @return Returns the account.
	 */
	public String getAccount()
	{
		return _account;
	}
	/**
	 * @return Returns the key.
	 */
	public SessionKey getKey()
	{
		return _sessionKey;
	}
}