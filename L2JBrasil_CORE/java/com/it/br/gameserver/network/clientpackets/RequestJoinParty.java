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

import com.it.br.Config;
import com.it.br.gameserver.model.L2Party;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.AskJoinParty;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 *  sample
 *  29
 *  42 00 00 10
 *  01 00 00 00
 *
 *  format  cdd
 *
 *
 * @version $Revision: 1.7.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestJoinParty extends L2GameClientPacket
{
	private static final String _C__29_REQUESTJOINPARTY = "[C] 29 RequestJoinParty";

	private String _name;
	private int _itemDistribution;


	@Override
	protected void readImpl()
	{
        _name = readS();
        _itemDistribution = readD();
	}


	@Override
	protected void runImpl()
	{
        L2PcInstance requestor = getClient().getActiveChar();
        L2PcInstance target = L2World.getInstance().getPlayer(_name);

	if (requestor == null)
            return;

        if (target == null)
        {
            requestor.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
            return;
        }
        
        if (target.getAppearance().getInvisible()) 
	    { 
            requestor.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT)); 
            return; 
        } 
		if (target.isInParty())
        {
			SystemMessage msg = new SystemMessage(SystemMessageId.S1_IS_ALREADY_IN_PARTY);
			msg.addString(target.getName());
			requestor.sendPacket(msg);
			return;
		}

		if (target == requestor)
        {
			requestor.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}

		if (target.isCursedWeaponEquipped() || requestor.isCursedWeaponEquipped())
        {
			requestor.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}

		if (target.isInJail() || requestor.isInJail())
        {
			SystemMessage sm = SystemMessage.sendString("Player is in Jail");
			requestor.sendPacket(sm);
			return;
		}

		if (target.getClient().isDetached()) 
        { 
            requestor.sendMessage("Player is in offline mode."); 
            return; 
        } 
		
        if (target.isInOlympiadMode() || requestor.isInOlympiadMode())
            return;

        if (target.isInDuel() || requestor.isInDuel())
            return;

        if (!requestor.isInParty())     //Asker has no party
        {
            createNewParty(target, requestor);
        }
        else                            //Asker is in party
        {
            if(requestor.getParty().isInDimensionalRift())
            {
                requestor.sendMessage("Voce nao pode convidar um jogador quando em Dimensional Rift.");
            }
            else
            {
                addTargetToParty(target, requestor);
            }
        }
    }

	/**
	 * @param client
	 * @param itemDistribution
	 * @param target
	 * @param requestor
	 */
	private void addTargetToParty(L2PcInstance target, L2PcInstance requestor)
	{
		SystemMessage msg;

       // summary of ppl already in party and ppl that get invitation
        if (requestor.getParty().getMemberCount() + requestor.getParty().getPendingInvitationNumber() >= 9 )
        {
			requestor.sendPacket(new SystemMessage(SystemMessageId.PARTY_FULL));
			return;
		}

		if (!requestor.getParty().isLeader(requestor))
        {
			requestor.sendPacket(new SystemMessage(SystemMessageId.ONLY_LEADER_CAN_INVITE));
			return;
		}

		if (!target.isProcessingRequest())
		{
		    requestor.onTransactionRequest(target);
		    // in case a leader change has happened, use party's mode 
            target.sendPacket(new AskJoinParty(requestor.getName(), requestor.getParty().getLootDistribution())); 
		 	requestor.getParty().increasePendingInvitationNumber();

		    if (Config.DEBUG)
		        _log.fine("sent out a party invitation to:"+target.getName());

		    msg = new SystemMessage(SystemMessageId.YOU_INVITED_S1_TO_PARTY);
		    msg.addString(target.getName());
		    requestor.sendPacket(msg);
		}
		else
		{
		    msg = new SystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER);
		    requestor.sendPacket(msg);

		    if (Config.DEBUG)
		        _log.warning(requestor.getName() + " ja recebeu um convite de party");
		}
		msg = null;
	}


	/**
	 * @param client
	 * @param itemDistribution
	 * @param target
	 * @param requestor
	 */
	private void createNewParty(L2PcInstance target, L2PcInstance requestor)
	{
		SystemMessage msg;

		if (!target.isProcessingRequest())
		{
		    requestor.setParty(new L2Party(requestor, _itemDistribution));

		    requestor.onTransactionRequest(target);
		    target.sendPacket(new AskJoinParty(requestor.getName(), _itemDistribution));
		    requestor.getParty().increasePendingInvitationNumber();

		    if (Config.DEBUG)
		        _log.fine("sent out a party invitation to:"+target.getName());

		    msg = new SystemMessage(SystemMessageId.YOU_INVITED_S1_TO_PARTY);
		    msg.addString(target.getName());
		    requestor.sendPacket(msg);
		}
		else
		{
		    msg = new SystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER);
		    msg.addString(target.getName());
		    requestor.sendPacket(msg);

		    if (Config.DEBUG)
		        _log.warning(requestor.getName() + " ja recebeu um convite de party");
		}
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__29_REQUESTJOINPARTY;
	}
}
