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
import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.gameserver.Shutdown;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.SendTradeRequest;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.it.br.configuration.Configurator.getSettings;

public final class TradeRequest extends L2GameClientPacket
{
	private static final String TRADEREQUEST__C__15 = "[C] 15 TradeRequest";
	private static Logger _log = LoggerFactory.getLogger(TradeRequest.class);

	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
	    if (Shutdown.getCounterInstance() != null)
		{
			player.sendMessage("You can't trade when restarting / shutdown of the server");
			return;
		}

        if (player == null) 
        	return;

        if (Config.GM_DISABLE_TRANSACTION && player.getAccessLevel() >= Config.GM_TRANSACTION_MIN && player.getAccessLevel() <= Config.GM_TRANSACTION_MAX)
        {
        	player.sendMessage("Transactions are disable for your Access Level");
        	sendPacket(new ActionFailed());
        	return;
        }

        if (player.getActiveEnchantItem() != null)
    	{
        	player.setAccessLevel(-100);
        	player.setAccountAccesslevel(-100);
        	player.sendMessage("[Cheat Guard]: You are Banned for Trying to use Enchant Exploit!");
        	player.closeNetConnection();
        	return;
    	}

		L2Object target = L2World.getInstance().findObject(_objectId);
        if (target == null || !player.getKnownList().knowsObject(target) || !(target instanceof L2PcInstance) || (target.getObjectId() == player.getObjectId()))
		{
			player.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
			return;
		}

        L2PcInstance partner = (L2PcInstance)target;

        if (partner.isInOlympiadMode() || partner.isInCombat() || partner.isFlying() || partner.isEnchanting() || partner.isCastingNow() || partner.isInvisible() ||  partner.isInDuel())
        {
            player.sendPacket(new ActionFailed());
            return;
        }

        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && (player.getKarma() > 0 || partner.getKarma() > 0))
        {
            player.sendMessage("Chaotic players can't use Trade.");
            return;
        }

        if (player.getPrivateStoreType() != 0 || partner.getPrivateStoreType() != 0)
        {
            player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE));
            return;
        }

		if (Config.JAIL_DISABLE_TRANSACTION && (player.isInJail() || partner.isInJail()))
		{
			player.sendMessage("You cannot trade while you are in in Jail.");
			return;
		}

        if (player.isProcessingTransaction())
	    {
        	if (Config.DEBUG) _log.debug("already trading with someone");
        	player.sendPacket(new SystemMessage(SystemMessageId.ALREADY_TRADING));
        	return;
	    }

		if (partner.isProcessingRequest() || partner.isProcessingTransaction())
		{
			if (Config.DEBUG) _log.info("transaction already in progress.");
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER);
			sm.addString(partner.getName());
			player.sendPacket(sm);
			return;
		}

		if ((player.isInParty() || partner.isInParty()) && !getSettings(L2JBrasilSettings.class).isPartyTradeEnabled() )
		{
			player.sendMessage("Cannot trade in party mode for security reasons");
			partner.sendMessage("Cannot trade in party mode for security reasons");
			return;
		}

        if (partner.getTradeRefusal())
        {
            player.sendMessage("Target is in trade refusal mode");
            return;
        }

	    if (Util.calculateDistance(player, partner, true) > 100) 
	    { 
            SystemMessage sm = new SystemMessage(SystemMessageId.TARGET_TOO_FAR); 
            player.sendPacket(sm); 
            return; 
        }

	    if (partner.getAllowTrade() == false)
        {
            player.sendMessage("Target is not allowed to receive more than one trade request at the same time.");
        	return;
        }

        partner.setAllowTrade(false);
        player.setAllowTrade(false); 
		player.onTransactionRequest(partner);
		partner.sendPacket(new SendTradeRequest(player.getObjectId()));
		SystemMessage sm = new SystemMessage(SystemMessageId.REQUEST_S1_FOR_TRADE);
		sm.addString(partner.getName());
		player.sendPacket(sm);
	}

	@Override
	public String getType()
	{
		return TRADEREQUEST__C__15;
	}
}