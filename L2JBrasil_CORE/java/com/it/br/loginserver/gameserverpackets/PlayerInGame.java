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

import com.it.br.loginserver.clientpackets.ClientBasePacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author -Wooden-
 *
 */
public class PlayerInGame extends ClientBasePacket
{
	private List<String> _accounts;
	/**
	 * @param decrypt
	 */
	public PlayerInGame(byte[] decrypt)
	{
		super(decrypt);
		_accounts =  new ArrayList<>();
		int size = readH();
		for (int i = 0; i < size; i++)
		{
			_accounts.add(readS());
		}
	}
	/**
	 * @return Returns the accounts.
	 */
	public List<String> getAccounts()
	{
		return _accounts;
	}
}