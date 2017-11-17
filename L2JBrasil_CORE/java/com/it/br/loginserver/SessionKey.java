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
package com.it.br.loginserver;

import static com.it.br.configuration.Configurator.getSettings;

import com.it.br.configuration.settings.LoginSettings;

/**
 * <p>This class is used to represent session keys used by the client to authenticate in the gameserver</p>
 * <p>A SessionKey is made up of two 8 bytes keys. One is send in the {@link com.it.br.loginserver.serverpackets.LoginOk LoginOk}
 * packet and the other is sent in {@link com.it.br.loginserver.serverpackets.PlayOk PlayOk}</p>
 * @author -Wooden-
 *
 */
public class SessionKey
{
	public int playOkID1;
	public int playOkID2;
	public int loginOkID1;
	public int loginOkID2;

	public SessionKey(int loginOK1, int loginOK2, int playOK1, int playOK2)
	{
		playOkID1 = playOK1;
		playOkID2 = playOK2;
		loginOkID1 = loginOK1;
		loginOkID2 = loginOK2;
	}

	@Override
	public String toString()
	{
		return "PlayOk: " + playOkID1 + " " + playOkID2 + " LoginOk:" + loginOkID1 + " "
		+ loginOkID2;
	}

	public boolean checkLoginPair(int loginOk1, int loginOk2)
	{
		return loginOkID1 == loginOk1 && loginOkID2 == loginOk2;
	}

	/**
	 * <p>Returns true if keys are equal.</p>
	 * <p>Only checks the PlayOk part of the session key if server doesnt show the licence when player logs in.</p>
	 * @param key
	 */
	public boolean equals(SessionKey key)
	{
		LoginSettings loginSettings = getSettings(LoginSettings.class);
		// when server doesnt show licence it deosnt send the LoginOk packet, client doesnt have this part of the key then.
		if (loginSettings.showLicense())
		{
			return (playOkID1 == key.playOkID1 && loginOkID1 == key.loginOkID1
					&& playOkID2 == key.playOkID2 && loginOkID2 == key.loginOkID2);
		}
		else
		{
			return (playOkID1 == key.playOkID1 && playOkID2 == key.playOkID2);
		}
	}
}