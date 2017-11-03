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

import com.it.br.gameserver.SevenSigns;
/**
 * Changes the sky color depending on the outcome
 * of the Seven Signs competition.
 *
 * packet type id 0xf8
 * format: c h
 *
 * @author Tempy
 */
public class SignsSky extends L2GameServerPacket
{
    private static final String _S__F8_SignsSky = "[S] F8 SignsSky";

    private static int _state = 0;

    public SignsSky()
    {
        int compWinner = SevenSigns.getInstance().getCabalHighestScore();

        if (SevenSigns.getInstance().isSealValidationPeriod())
            if (compWinner == SevenSigns.CABAL_DAWN)
                _state = 2;
            else if (compWinner == SevenSigns.CABAL_DUSK)
                _state = 1;
    }

    public SignsSky(int state)
    {
        _state = state;
    }


	@Override
	protected final void writeImpl()
    {
        writeC(0xf8);

        if (_state == 2) // Dawn Sky
            writeH(258);
        else if (_state == 1) // Dusk Sky
            writeH(257);
        //else
        //writeH(256);
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
     */

	@Override
	public String getType()
    {
        return _S__F8_SignsSky;
    }
}
