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
package com.it.br.gameserver.network.gameserverpackets;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Logger;

import javax.crypto.Cipher;

/**
 * @author -Wooden-
 *
 */
public class BlowFishKey extends GameServerBasePacket
{
	private static Logger _log = Logger.getLogger(BlowFishKey.class.getName());
	/**
	 * @param blowfishKey
	 * @param publicKey
	 */
	public BlowFishKey(byte[] blowfishKey, RSAPublicKey publicKey)
	{
		writeC(0x00);
		byte[] encrypted =null;
		try
		{
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
	        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
	        encrypted = rsaCipher.doFinal(blowfishKey);
		}
		catch(GeneralSecurityException e)
		{
			_log.severe("Error While encrypting blowfish key for transmision (Crypt error)");
			e.printStackTrace();
		}
		writeD(encrypted.length);
		writeB(encrypted);
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.gameserverpackets.GameServerBasePacket#getContent()
	 */

	@Override
	public byte[] getContent() throws IOException
	{
		return getBytes();
	}

}
