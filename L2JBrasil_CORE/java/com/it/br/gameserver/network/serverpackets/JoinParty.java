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



/**
 *
 * sample
 * <p>
 * 4c
 * 01 00 00 00
 * <p>
 *
 * format
 * cd
 *
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class JoinParty extends L2GameServerPacket
{
	private static final String _S__4C_JOINPARTY = "[S] 3a JoinParty";
	//private static Logger _log = LoggerFactory.getLogger(JoinParty.class);

	private int _response;

	/**
	 * @param int
	 */
	public JoinParty(int response)
	{
		_response = response;
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0x3a);

		writeD(_response);
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__4C_JOINPARTY;
	}

}
