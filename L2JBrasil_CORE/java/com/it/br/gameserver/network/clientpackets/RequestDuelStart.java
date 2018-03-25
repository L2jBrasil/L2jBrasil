/* This program is free software; you can redistribute it and/or modify
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

import com.it.br.Config;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ExDuelAskStart;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

public final class RequestDuelStart extends L2GameClientPacket
{
	private static final String _C__D0_27_REQUESTDUELSTART = "[C] D0:27 RequestDuelStart";
	//private static Logger _log = LoggerFactory.getLogger(RequestDuelStart.class);
	private String _player;
	private int _partyDuel;


	@Override
	protected void readImpl()
	{
		_player = readS();
		_partyDuel = readD();
	}

	/**
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#runImpl()
	 */

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
        L2PcInstance targetChar = L2World.getInstance().getPlayer(_player);
        if (activeChar == null)
            return;
        if (targetChar == null)
        {
        	activeChar.sendPacket(new SystemMessage(SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL));
        	return;
        }
        if (activeChar == targetChar)
        {
        	activeChar.sendPacket(new SystemMessage(SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL));
        	return;
        }

        // Check if duel is possible
        if (!activeChar.canDuel())
        {
        	activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME));
        	return;
        }
        else if (!targetChar.canDuel())
        {
        	activeChar.sendPacket(targetChar.getNoDuelReason());
        	return;
        }
        // Players may not be too far apart
        else if (!activeChar.isInsideRadius(targetChar, 250, false, false))
        {
        	SystemMessage msg = new SystemMessage(SystemMessageId.S1_CANNOT_RECEIVE_A_DUEL_CHALLENGE_BECAUSE_S1_IS_TOO_FAR_AWAY);
        	msg.addString(targetChar.getName());
        	activeChar.sendPacket(msg);
        	return;
        }

        // Duel is a party duel
		if (_partyDuel == 1)
		{
			// Player must be in a party & the party leader
			if (!activeChar.isInParty() || !(activeChar.isInParty() && activeChar.getParty().isLeader(activeChar)))
			{
				activeChar.sendMessage("You have to be the leader of a party in order to request a party duel.");
				return;
			}
			// Target must be in a party
			else if (!targetChar.isInParty())
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY));
				return;
			}
			// Target may not be of the same party
			else if (activeChar.getParty().getPartyMembers().contains(targetChar))
			{
				activeChar.sendMessage("This player is a member of your own party.");
				return;
			}

			// Check if every player is ready for a duel
			for (L2PcInstance temp : activeChar.getParty().getPartyMembers())
			{
				if (!temp.canDuel())
				{
					activeChar.sendMessage("Not all the members of your party are ready for a duel.");
					return;
				}
			}
			L2PcInstance partyLeader = null; // snatch party leader of targetChar's party
			for (L2PcInstance temp : targetChar.getParty().getPartyMembers())
			{
				if (partyLeader == null) partyLeader = temp;
				if (!temp.canDuel())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.THE_OPPOSING_PARTY_IS_CURRENTLY_UNABLE_TO_ACCEPT_A_CHALLENGE_TO_A_DUEL));
					return;
				}
			}

			// Send request to targetChar's party leader
			if (!partyLeader.isProcessingRequest())
			{
				activeChar.onTransactionRequest(partyLeader);
				partyLeader.sendPacket(new ExDuelAskStart(activeChar.getName(), _partyDuel));

				if (Config.DEBUG)
			        _log.debug(activeChar.getName() + " requested a duel with " + partyLeader.getName());

				SystemMessage msg = new SystemMessage(SystemMessageId.S1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL);
				msg.addString(partyLeader.getName());
				activeChar.sendPacket(msg);

				msg = new SystemMessage(SystemMessageId.S1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL);
				msg.addString(activeChar.getName());
				targetChar.sendPacket(msg);
			}
			else
			{
				SystemMessage msg = new SystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER);
				msg.addString(partyLeader.getName());
				activeChar.sendPacket(msg);
			}
		}
		else // 1vs1 duel
		{
			if (!targetChar.isProcessingRequest())
			{
				activeChar.onTransactionRequest(targetChar);
				targetChar.sendPacket(new ExDuelAskStart(activeChar.getName(), _partyDuel));

				if (Config.DEBUG)
			        _log.debug(activeChar.getName() + " requested a duel with " + targetChar.getName());

				SystemMessage msg = new SystemMessage(SystemMessageId.S1_HAS_BEEN_CHALLENGED_TO_A_DUEL);
				msg.addString(targetChar.getName());
				activeChar.sendPacket(msg);

				msg = new SystemMessage(SystemMessageId.S1_HAS_CHALLENGED_YOU_TO_A_DUEL);
				msg.addString(activeChar.getName());
				targetChar.sendPacket(msg);
			}
			else
			{
				SystemMessage msg = new SystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER);
				msg.addString(targetChar.getName());
				activeChar.sendPacket(msg);
			}
		}
	}

	/**
	 * @see com.it.br.gameserver.BasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__D0_27_REQUESTDUELSTART;
	}
}
