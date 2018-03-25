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
package com.it.br.gameserver.model.actor.instance;

import com.it.br.configuration.settings.L2JModsSettings;
import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.instancemanager.CoupleManager;
import com.it.br.gameserver.model.Inventory;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.entity.Couple;
import com.it.br.gameserver.network.serverpackets.*;
import com.it.br.gameserver.templates.L2NpcTemplate;

import static com.it.br.configuration.Configurator.getSettings;

public class L2WeddingManagerInstance extends L2NpcInstance
{
	/**
	* @author evill33t & squeezed
	*/
	public L2WeddingManagerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}


	@Override
	public void onAction(L2PcInstance player)
	{
		if (!canTarget(player)) return;

		// Check if the L2PcInstance already target the L2NpcInstance
		if (this != player.getTarget())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);

			// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));

			// Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client
			player.sendPacket(new ValidateLocation(this));
		}
		else
		{
			// Calculate the distance between the L2PcInstance and the L2NpcInstance
			if (!canInteract(player))
			{
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
			}
			else
			{
				showMessageWindow(player);
			}
		}
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(new ActionFailed());
	}

    private void showMessageWindow(L2PcInstance player)
    {
        String filename = "data/html/mods/Wedding_start.htm";
        String replace = String.valueOf(getSettings(L2JModsSettings.class).getWeddingPrice());

        NpcHtmlMessage html = new NpcHtmlMessage(1);
        html.setFile(filename);
        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%replace%", replace);
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }


    @Override
	public void onBypassFeedback(L2PcInstance player, String command)
    {
        // standard msg
        String filename = "data/html/mods/Wedding_start.htm";
        String replace = "";

        
        // if player has no partner
        if(player.getPartnerId()==0)
        {
            filename = "data/html/mods/Wedding_nopartner.htm";
            sendHtmlMessage(player, filename, replace);
            return;
        }
        else
        {
            L2PcInstance ptarget = (L2PcInstance)L2World.getInstance().findObject(player.getPartnerId());
            // partner online ?
            if(ptarget==null || ptarget.isOnline()==0)
            {
                filename = "data/html/mods/Wedding_notfound.htm";
                sendHtmlMessage(player, filename, replace);
                return;
            }
            else
            {
            	L2JModsSettings l2jModsSettings = getSettings(L2JModsSettings.class);
                // already married ?
                if(player.isMarried())
                {
                    filename = "data/html/mods/Wedding_already.htm";
                    sendHtmlMessage(player, filename, replace);
                    return;
                }
                else if (player.isMarryAccepted())
                {
                    filename = "data/html/mods/Wedding_waitforpartner.htm";
                    sendHtmlMessage(player, filename, replace);
                    return;
                }
                else if (command.startsWith("AcceptWedding"))
                {
                    // accept the wedding request
                    player.setMarryAccepted(true);
                    Couple couple = CoupleManager.getInstance().getCouple(player.getCoupleId());
                    couple.marry();
                    
                    //messages to the couple
                    player.sendMessage("Congratulations you are married!");
                    player.setMarried(true);
                    player.setMarryRequest(false);
                    ptarget.sendMessage("Congratulations you are married!");
                    ptarget.setMarried(true);
                    ptarget.setMarryRequest(false);
                    
                    // give cupid's bows to couple's 
                    player.getInventory().addItem("Cupids Bow",9140,1,player,null); // give cupids bow 
                    player.getInventory().updateDatabase(); // update database 
                    ptarget.getInventory().addItem("Cupids Bow",9140,1,ptarget,null); // give cupids bow 
                    ptarget.getInventory().updateDatabase(); // update database
                    
                    //wedding march
                    MagicSkillUser MSU = new MagicSkillUser(player, player, 2230, 1, 1, 0);
                    player.broadcastPacket(MSU);
                    MSU = new MagicSkillUser(ptarget, ptarget, 2230, 1, 1, 0);
                    ptarget.broadcastPacket(MSU);

                    // fireworks
                    L2Skill skill = SkillTable.getInstance().getInfo(2025,1);
                    if (skill != null)
                    {
                        MSU = new MagicSkillUser(player, player, 2025, 1, 1, 0);
                        player.sendPacket(MSU);
                        player.broadcastPacket(MSU);
                        player.useMagic(skill, false, false);

                        MSU = new MagicSkillUser(ptarget, ptarget, 2025, 1, 1, 0);
                        ptarget.sendPacket(MSU);
                        ptarget.broadcastPacket(MSU);
                        ptarget.useMagic(skill, false, false);

                    }
                    if (l2jModsSettings.isAnnounceWeddingsEnabled())  { 
                    	Announcements.getInstance().announceToAll("Congratulations to "+player.getName()+" and "+ptarget.getName()+"! They have been married.");
                    }

                    filename = "data/html/mods/Wedding_accepted.htm";
                    replace = ptarget.getName();
                    sendHtmlMessage(ptarget, filename, replace);
                    return;
                }
                else if (command.startsWith("DeclineWedding"))
                {
                    player.setMarryRequest(false);
                    ptarget.setMarryRequest(false);
                    player.setMarryAccepted(false);
                    ptarget.setMarryAccepted(false);
                    player.sendMessage("You declined");
                    ptarget.sendMessage("Your partner declined");
                    replace = ptarget.getName();
                    filename = "data/html/mods/Wedding_declined.htm";
                    sendHtmlMessage(ptarget, filename, replace);
                    return;
                }
                else if (player.isMarryRequest())
                {
                    // check for formalwear
                	boolean useFormalWear = l2jModsSettings.isWeddingFormalWearEnabled();
                	if(useFormalWear)
                	{
                		Inventory inv3 = player.getInventory();
                		L2ItemInstance item3 = inv3.getPaperdollItem(10);
                		if(null==item3)
                		{
                			player.setIsWearingFormalWear(false);
                		}
                		else
                		{
	                		String strItem = Integer.toString(item3.getItemId());
	                		String frmWear = Integer.toString(6408);
	                		player.sendMessage(strItem);
	                		if(strItem.equals(frmWear))
	                		{
	                			player.setIsWearingFormalWear(true);
	                		}else{
	                			player.setIsWearingFormalWear(false);
	                		}
                		}
                	}
                    if(useFormalWear && !player.isWearingFormalWear())
                    {
                        filename = "data/html/mods/Wedding_noformal.htm";
                        sendHtmlMessage(player, filename, replace);
                        return;
                    }
                    filename = "data/html/mods/Wedding_ask.htm";
                    player.setMarryRequest(false);
                    ptarget.setMarryRequest(false);
                    replace = ptarget.getName();
                    sendHtmlMessage(player, filename, replace);
                    return;
                }
                else if (command.startsWith("AskWedding"))
                {
                    // check for formalwear
                	boolean useFormalWear = l2jModsSettings.isWeddingFormalWearEnabled();
                	if(useFormalWear)
                	{
                		Inventory inv3 = player.getInventory();
                		L2ItemInstance item3 = inv3.getPaperdollItem(10);

                		if (null==item3)
                		{
                			player.setIsWearingFormalWear(false);
                		}
                		else
                		{
	                		String frmWear = Integer.toString(6408);
	                		String strItem = null;
	               			strItem = Integer.toString(item3.getItemId());

	                		if(null != strItem && strItem.equals(frmWear))
	                		{
	                			player.setIsWearingFormalWear(true);
	                		}else{
	                			player.setIsWearingFormalWear(false);
	                		}
                		}
                	}
                    if(useFormalWear && !player.isWearingFormalWear())
                    {
                        filename = "data/html/mods/Wedding_noformal.htm";
                        sendHtmlMessage(player, filename, replace);
                        return;
                    }
                    else if(player.getAdena()< l2jModsSettings.getWeddingPrice())
                    {
                        filename = "data/html/mods/Wedding_adena.htm";
                        replace = String.valueOf(l2jModsSettings.getWeddingPrice());
                        sendHtmlMessage(player, filename, replace);
                        return;
                    }
                    else
                    {
                        player.setMarryAccepted(true);
                        ptarget.setMarryRequest(true);
                        replace = ptarget.getName();
                        filename = "data/html/mods/Wedding_requested.htm";
                        player.getInventory().reduceAdena("Wedding", l2jModsSettings.getWeddingPrice(), player, player.getLastFolkNPC());
                        sendHtmlMessage(player, filename, replace);
                        return;
                    }
                }
            }
        }
        sendHtmlMessage(player, filename, replace);
    }

    private void sendHtmlMessage(L2PcInstance player, String filename, String replace)
    {
        NpcHtmlMessage html = new NpcHtmlMessage(1);
        html.setFile(filename);
        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%replace%", replace);
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }
}