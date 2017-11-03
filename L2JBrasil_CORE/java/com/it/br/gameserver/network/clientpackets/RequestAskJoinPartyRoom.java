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
 * Format: (ch) S
 * @author  -Wooden-
 *
 */
public class RequestAskJoinPartyRoom extends L2GameClientPacket
{
    private static final String _C__D0_14_REQUESTASKJOINPARTYROOM = "[C] D0:14 RequestAskJoinPartyRoom";
    private String _player; // not tested, just guessed


	@Override
	protected void readImpl()
    {
        _player = readS();
    }


	@Override
	protected void runImpl()
    {
        // TODO
        System.out.println("C5:RequestAskJoinPartyRoom: S: "+_player);
    }


    @Override
	public String getType()
    {
        return _C__D0_14_REQUESTASKJOINPARTYROOM;
    }
}