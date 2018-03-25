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
import com.it.br.gameserver.SevenSignsFestival;
import com.it.br.gameserver.communitybbs.Manager.RegionBBSManager;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.model.Inventory;
import com.it.br.gameserver.model.L2Party;
import com.it.br.gameserver.model.Olympiad.Olympiad;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.L2GameClient;
import com.it.br.gameserver.network.L2GameClient.GameClientState;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.CharSelectInfo;
import com.it.br.gameserver.network.serverpackets.RestartResponse;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.taskmanager.AttackStanceTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestRestart extends L2GameClientPacket
{
    private static final String _C__46_REQUESTRESTART = "[C] 46 RequestRestart";
    private static Logger _log = LoggerFactory.getLogger(RequestRestart.class);

    @Override
	protected void readImpl()
    {}

    @Override
	protected void runImpl()
    {
        L2PcInstance player = getClient().getActiveChar();
        if (player == null)
        {
            return;
        }
        if (player.getActiveEnchantItem() != null)
		{
			sendPacket(RestartResponse.valueOf(false));
			return;
		}

		if (player.isLocked())
		{
			_log.warn(player.getName() + " tried to restart during class change.");
			sendPacket(RestartResponse.valueOf(false));
			return;
		}

		if (player.isProcessingTransaction())
		{
			player.sendMessage("Cannot restart while offer trading");
			return;
		}

		// Check if player is in private store
		if(player.getPrivateStoreType() != 0)
		{
			player.sendMessage("Cannot restart while trading.");
			sendPacket(RestartResponse.valueOf(false));
			return;
		}

        if (player.getOlympiadGameId() > 0 || player.isInOlympiadMode() || Olympiad.getInstance().isRegistered(player))
        {
            player.sendMessage("You can't restart in olympiad mode");
			sendPacket(RestartResponse.valueOf(false));
            return;
        }

    	if (TvTEvent.isStarted() && TvTEvent.isPlayerParticipant(player.getObjectId()))
        { 
    		sendPacket(RestartResponse.valueOf(false));
        	player.sendMessage("You can not logout when you are registered in the TvTEvent."); 
        	return; 
        }

        if (player.atEvent)
        {
            return;
        }

        if (player.isCastingNow()) 
        {
            player.abortCast();
        }

        if (player.isTeleporting())
        {
        	return; 
        }
        
        player.getInventory().updateDatabase();

        if (player.getPrivateStoreType() != 0)
        {
            player.sendMessage("You can not logout while maintaining a private store)");
            return;
        }     
        if (player.getPvpFlag() >= 1)
        {
        	SystemMessage sm = new SystemMessage(SystemMessageId.CANT_RESTART_WHILE_FIGHTING);
			player.sendPacket(sm);
			return;
        }		
        if (player.getActiveRequester() != null)
        {
            player.getActiveRequester().onTradeCancel(player);
            player.onTradeCancel(player.getActiveRequester());
            sendPacket(RestartResponse.valueOf(false));
        }
        if (player.isInDuel())
        {
            player.sendMessage("You are in a duel!");
            sendPacket(RestartResponse.valueOf(false));
            return;
        }
        if (AttackStanceTaskManager.getInstance().getAttackStanceTask(player) && !(player.isGM() && Config.GM_RESTART_FIGHTING))
        {
            if (Config.DEBUG)
                _log.debug("Player " + player.getName() + " tried to logout while fighting.");

            player.sendPacket(new SystemMessage(SystemMessageId.CANT_RESTART_WHILE_FIGHTING));
            sendPacket(RestartResponse.valueOf(false));
            return;
        }

        // Prevent player from restarting if they are a festival participant
        // and it is in progress, otherwise notify party members that the player
        // is not longer a participant.
        if (player.isFestivalParticipant())
        {
        	if (SevenSignsFestival.getInstance().isFestivalInitialized())
        	{
        		player.sendPacket(SystemMessage.sendString("You cannot restart while you are a participant in a festival."));
        		sendPacket(RestartResponse.valueOf(false));
        		return;
        	}
        	L2Party playerParty = player.getParty();

        	if (playerParty != null)
        		player.getParty().broadcastToPartyMembers(SystemMessage.sendString(player.getName()+ " has been removed from the upcoming festival."));
        }
        if (player.isFlying())
        {
        	player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
        }
        if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND)!=null
        && player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).isAugmented())
        {
        player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).getAugmentation().removeBonus(player);
        }
        L2GameClient client = getClient();

        // detach the client from the char so that the connection isnt closed in the deleteMe
        player.setClient(null);

        RegionBBSManager.getInstance().changeCommunityBoard();

        // removing player from the world
        player.deleteMe();
        L2GameClient.saveCharToDisk(client.getActiveChar());

        getClient().setActiveChar(null);

        // return the client to the authed status
        client.setState(GameClientState.AUTHED);

        sendPacket(RestartResponse.valueOf(true));

        // send char list
        CharSelectInfo cl = new CharSelectInfo(client.getAccountName(),client.getSessionId().playOkID1);
        sendPacket(cl);
        client.setCharSelection(cl.getCharInfo());
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
     */

    @Override
	public String getType()
    {
        return _C__46_REQUESTRESTART;
    }
}