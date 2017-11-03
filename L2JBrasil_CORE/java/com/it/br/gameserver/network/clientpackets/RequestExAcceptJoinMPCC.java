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

import com.it.br.gameserver.model.L2CommandChannel;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author -Wooden-
 */
public final class RequestExAcceptJoinMPCC extends L2GameClientPacket
{
	private static final String _C__D0_0E_REQUESTEXASKJOINMPCC = "[C] D0:0E RequestExAcceptJoinMPCC";
	private int _response;

	/**
	 * @param buf
	 * @param client
	 */

	@Override
	protected void readImpl()
	{
		_response = readD();
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#runImpl()
	 */

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
        if(player != null)
        {
	    	L2PcInstance requestor = player.getActiveRequester();
			if (requestor == null)
			    return;

			if (_response == 1)
	        {
				if(!requestor.getParty().isInCommandChannel())
				{
					new L2CommandChannel(requestor); // Create new CC
				}
				requestor.getParty().getCommandChannel().addParty(player.getParty());
			}
			else
	        {
				requestor.sendMessage("The player declined to join your Command Channel.");
			}

			player.setActiveRequester(null);
			requestor.onTransactionResponse();
        }
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.BasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__D0_0E_REQUESTEXASKJOINMPCC;
	}
}
