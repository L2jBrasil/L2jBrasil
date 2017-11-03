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

import com.it.br.gameserver.model.L2Party;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ExAskJoinMPCC;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * Format: (ch) S
 * @author chris_00
 *
 * D0 0D 00 5A 00 77 00 65 00 72 00 67 00 00 00
 *
 */
public final class RequestExAskJoinMPCC extends L2GameClientPacket
{
	//private static Logger _log = Logger.getLogger(RequestExAskJoinMPCC.class.getName());
	private static final String _C__D0_0D_REQUESTEXASKJOINMPCC = "[C] D0:0D RequestExAskJoinMPCC";
	private String _name;


	@Override
	protected void readImpl()
	{
		_name = readS();
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2PcInstance player = L2World.getInstance().getPlayer(_name);
		if(player == null)
			return;
		// invite yourself? ;)
		if(activeChar.isInParty() && player.isInParty() && activeChar.getParty().equals(player.getParty()))
			return;

		//activeChar is in a Party?
		if (activeChar.isInParty())
		{
			L2Party activeParty = activeChar.getParty();
			//activeChar is PartyLeader? && activeChars Party is already in a CommandChannel?
			if (activeParty.getPartyMembers().get(0).equals(activeChar))
			{
				// if activeChars Party is in CC, is activeChar CCLeader?
				if (activeParty.isInCommandChannel() && activeParty.getCommandChannel().getChannelLeader().equals(activeChar))
				{
					//in CC and the CCLeader
					//target in a party?
					if (player.isInParty())
					{
						//targets party already in a CChannel?
						if (player.getParty().isInCommandChannel())
						{
							activeChar.sendMessage("Your target is already in a CommandChannel");
						}
						else
						{
							//ready to open a new CC
							//send request to targets Party's PartyLeader
							askJoinMPCC(activeChar, player);
						}
					}
					else
					{
						activeChar.sendMessage("Your target has no Party.");
					}

				}
				else if (activeParty.isInCommandChannel() && !activeParty.getCommandChannel().getChannelLeader().equals(activeChar))
				{
					//in CC, but not the CCLeader
					activeChar.sendMessage("Only the CommandChannelLeader can give out an invite.");
				}
				else
				{
					//target in a party?
					if (player.isInParty())
					{
						//targets party already in a CChannel?
						if (player.getParty().isInCommandChannel())
						{
							activeChar.sendMessage("Your target is already in a CommandChannel");
						}
						else
						{
							//ready to open a new CC
							//send request to targets Party's PartyLeader
							askJoinMPCC(activeChar, player);
						}
					}
					else
					{
						activeChar.sendMessage("Your target has no Party.");
					}
				}
			}
			else
			{
				activeChar.sendMessage("Only the Partyleader can give out an invite.");
			}
		}
	}

	private void askJoinMPCC(L2PcInstance requestor, L2PcInstance target)
	{
		boolean hasRight = false;
		if (requestor.getClan() != null && requestor.getClan().getLeaderId() == requestor.getObjectId()) // Clanleader
			hasRight = true;
		else if (requestor.getInventory().getItemByItemId(8871) != null) // 8871 Strategy Guide. Should destroyed after sucessfull invite?
			hasRight = true;
		else
		{
			for (L2Skill skill : requestor.getAllSkills())
			{
				// Skill Clan Imperium
				if (skill.getId() == 391)
				{
					hasRight = true;
					break;
				}
			}
		}
		if (!hasRight && !requestor.getParty().isInCommandChannel())
		{
			requestor.sendMessage("You dont have the rights to open a Command Channel!");
			return;
		}
		if (!target.isProcessingRequest())
		{
			requestor.onTransactionRequest(target);
			target.getParty().getPartyMembers().get(0).sendPacket(new ExAskJoinMPCC(requestor.getName()));
		    requestor.sendMessage("You invited "+target.getName()+" to your Command Channel.");
		}
		else
		{
		    requestor.sendPacket(new SystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER));
		}
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.BasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__D0_0D_REQUESTEXASKJOINMPCC;
	}
}
