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
package com.it.br.gameserver.network.serverpackets;

public class CharCreateFail extends L2GameServerPacket
{
	private static final String _S__26_CHARCREATEFAIL = "[S] 1a CharCreateFail";

	public static final int REASON_CREATION_FAILED = 0x00; // "Your character creation has failed."
	public static final int REASON_TOO_MANY_CHARACTERS = 0x01; // "You cannot create another character. Please delete the existing character and try again." Removes all settings that were selected (race, class, etc).
	public static final int REASON_NAME_ALREADY_EXISTS = 0x02; // "This name already exists."
	public static final int REASON_16_ENG_CHARS = 0x03; // "Your title cannot exceed 16 characters in length. Please try again."
	public static final int REASON_INCORRECT_NAME = 0x04; // "Incorrect name. Please try again."
	

	private int _error;

	public CharCreateFail(int errorCode)
	{
		_error = errorCode;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x1a);
		writeD(_error);
	}

	/* (non-Javadoc)
	 * @see com.l2jfrenetic.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__26_CHARCREATEFAIL;
	}

}
