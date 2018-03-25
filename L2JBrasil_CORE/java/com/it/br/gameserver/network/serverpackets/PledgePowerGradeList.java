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

import com.it.br.gameserver.model.L2Clan.RankPrivs;

/**
 *
 *
 * sample
 * 0000: 9c c10c0000 48 00 61 00 6d 00 62 00 75 00 72    .....H.a.m.b.u.r
 * 0010: 00 67 00 00 00 00000000 00000000 00000000 00000000 00000000 00000000
 * 00 00
 * 00000000                                           ...

  * format   dd ??
 *
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class PledgePowerGradeList extends L2GameServerPacket
{
	private static final String _S__FE_3B_PLEDGEPOWERGRADELIST = "[S] FE:3B PledgePowerGradeList";
    private RankPrivs[] _privs;

	public PledgePowerGradeList(RankPrivs[] privs)
	{
        _privs = privs;
	}


	@Override
	protected final void writeImpl()
	{
        writeC(0xFE);
        writeH(0x3b);
        writeD(_privs.length);
        for (int i =0; i<_privs.length; i++)
        {
            writeD(_privs[i].getRank());
            writeD(_privs[i].getParty());
            //_log.warn("rank: "+_privs[i].getRank()+" party: "+_privs[i].getParty());
        }

	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__FE_3B_PLEDGEPOWERGRADELIST;
	}

}
