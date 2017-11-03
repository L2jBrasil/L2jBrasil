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
package com.it.br.gameserver.network.clientpackets;

/**
 * Format: (ch)
 * just a trigger
 * @author  -Wooden-
 *
 */
public final class RequestExFishRanking extends L2GameClientPacket
{
    private static final String _C__D0_1F_REQUESTEXFISHRANKING = "[C] D0:1F RequestExFishRanking";


	@Override
	protected void readImpl()
    {
    	// trigger
    }

    /**
     * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#runImpl()
     */

	@Override
	protected void runImpl()
    {
        System.out.println("C5: RequestExFishRanking");
    }

    /**
     * @see com.it.br.gameserver.BasePacket#getType()
     */

    @Override
	public String getType()
    {
        return _C__D0_1F_REQUESTEXFISHRANKING;
    }
}