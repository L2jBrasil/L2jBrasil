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
package com.it.br.loginserver.serverpackets;

import com.it.br.loginserver.L2LoginClient;

/**
 * Format: dd b dddd s
 * d: session id
 * d: protocol revision
 * b: 0x90 bytes : 0x80 bytes for the scrambled RSA public key
 *                 0x10 bytes at 0x00
 * d: unknow
 * d: unknow
 * d: unknow
 * d: unknow
 * s: blowfish key
 *
 */
public final class Init extends L2LoginServerPacket
{
	private int _sessionId;

	private byte[] _publicKey;
	private byte[] _blowfishKey;

	public Init(L2LoginClient client)
	{
		this(client.getScrambledModulus(), client.getBlowfishKey(), client.getSessionId());
	}

    public Init(byte[] publickey, byte[] blowfishkey, int sessionId)
    {
    	_sessionId = sessionId;
    	_publicKey = publickey;
    	_blowfishKey = blowfishkey;
    }

	/**
	 * @see com.l2jserver.mmocore.network.SendablePacket#write()
	 */
	@Override
	protected void write()
	{
		writeC(0x00); // init packet id
    	writeD(_sessionId); // session id
    	writeD(0x0000c621); // protocol revision
    	writeB(_publicKey); // RSA Public Key
    	// unk GG related?
    	writeD(0x29DD954E);
    	writeD(0x77C39CFC);
    	writeD(0x97ADB620);
    	writeD(0x07BDE0F7);
    	writeB(_blowfishKey); // BlowFish key
        writeC(0x00); // null termination ;)
	}
}
