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

import static com.it.br.configuration.Configurator.getSettings;

import java.util.List;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Multisell;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.Olympiad.Olympiad;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ExHeroList;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2NpcTemplate;

public class L2OlympiadManagerInstance extends L2FolkInstance
{
    private static Logger _logOlymp = Logger.getLogger(L2OlympiadManagerInstance.class.getName());

    private static final int GATE_PASS = Config.ALT_OLY_COMP_RITEM;

    public L2OlympiadManagerInstance (int objectId, L2NpcTemplate template)
    {
        super(objectId, template);
    }


    @Override
	public void onBypassFeedback (L2PcInstance player, String command)
    {
        if (command.startsWith("OlympiadDesc"))
        {
            int val = Integer.parseInt(command.substring(13,14));
            String suffix = command.substring(14);
            showChatWindow(player, val, suffix);
        }
        else if (command.startsWith("OlympiadNoble"))
        {
        	if (!player.isNoble() || player.getClassId().getId()<88)
                return;

                if (player.isAttackingNow() || player.isDead() || player.isAlikeDead() || player.isFakeDeath() || player.isCastingNow())
                {
                   player.sendMessage("Server: You cannot register in Olympiad when you are attacking,DeaD,FakeDeath,or Using Skills!");
                   return;
                }
            
            if (TvTEvent.isParticipating()) 
            { 
                   player.sendMessage("You cannot register for olympiad when you are registered for TvT Event."); 
                   return; 
            } 
         	
            int val = Integer.parseInt(command.substring(14));
            NpcHtmlMessage reply;
            StringBuilder replyMSG;
            
            
            switch(val)
            {
                case 1:
                    Olympiad.getInstance().unRegisterNoble(player);
                    break;
                case 2:
                    int classed = 0;
                    int nonClassed = 0;
                    int[] array = Olympiad.getInstance().getWaitingList();

                    if (array != null)
                    {
                        classed = array[0];
                        nonClassed = array[1];
                    }

                    reply = new NpcHtmlMessage(getObjectId());
                    replyMSG = new StringBuilder("<html><body>");
                    replyMSG.append("The number of people on the waiting list for " +
                            "Grand Olympiad" +
                            "<center>" +
                            "<img src=\"L2UI.SquareWhite\" width=270 height=1><img src=\"L2UI.SquareBlank\" width=1 height=3>" +
                            "<table width=270 border=0 bgcolor=\"000000\">" +
                            "<tr>" +
                            "<td align=\"left\">General</td>" +
                            "<td align=\"right\">"+ classed + "</td>" +
                            "</tr>" +
                            "<tr>" +
                            "<td align=\"left\">Not class-defined</td>" +
                            "<td align=\"right\">" + nonClassed + "</td>" +
                            "</tr>" +
                            "</table><br>" +
                            "<img src=\"L2UI.SquareWhite\" width=270 height=1> <img src=\"L2UI.SquareBlank\" width=1 height=3>" +
                            "<button value=\"Back\" action=\"bypass -h npc_"+getObjectId()+"_OlympiadDesc 2a\" " +
                            "width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center>");

                    replyMSG.append("</body></html>");

                    reply.setHtml(replyMSG.toString());
                    player.sendPacket(reply);
                    break;
                case 3:
                    int points = Olympiad.getInstance().getNoblePoints(player.getObjectId());
                    if (points >= 0)
                    {
                        reply = new NpcHtmlMessage(getObjectId());
                        replyMSG = new StringBuilder("<html><body>");
                        replyMSG.append("There are " + points + " Grand Olympiad " + "points granted for this event.<br><br>" + "<a action=\"bypass -h npc_"+getObjectId()+"_OlympiadDesc 2a\">Return</a>");
                        replyMSG.append("</body></html>");

                        reply.setHtml(replyMSG.toString());
                        player.sendPacket(reply);
                    }
                    break;
                case 4:
					if (player._active_boxes > 1 && !getSettings(L2JBrasilSettings.class).isDualBoxInOlyEnabled())
					{
						boolean already_in_oly = false;
						List<String> players_in_boxes = player.active_boxes_characters;

						if (players_in_boxes!=null && players_in_boxes.size()>1)
							for (String character_name: players_in_boxes)
							{
								L2PcInstance actual_player = L2World.getInstance().getPlayer(character_name);

								if (actual_player!=null && actual_player.isInOlympiadMode() || Olympiad.getInstance().isRegistered(actual_player))
								{
									already_in_oly = true;
									break;
								}
							}
						if (already_in_oly)
							player.sendMessage("Dual Box not allowed in Olympiad Event");
						else
							Olympiad.getInstance().registerNoble(player, false);
						break;
					}
					else
						Olympiad.getInstance().registerNoble(player, false);
					break;
				case 5:
					if(player._active_boxes > 1 && !getSettings(L2JBrasilSettings.class).isDualBoxInOlyEnabled())
					{
						boolean already_in_oly = false;
						List<String> players_in_boxes = player.active_boxes_characters;

						if (players_in_boxes!=null && players_in_boxes.size() > 1)
							for(String character_name: players_in_boxes)
							{
								L2PcInstance actual_player = L2World.getInstance().getPlayer(character_name);
								if (actual_player!=null && actual_player.isInOlympiadMode() || Olympiad.getInstance().isRegistered(actual_player))
								{
									already_in_oly = true;
									break;
								}
							}
						if (already_in_oly)
							player.sendMessage("Dual Box not allowed in Olympiad Event");
						else
							Olympiad.getInstance().registerNoble(player, true);
						break;
					}
					else
						Olympiad.getInstance().registerNoble(player, true);
					break;
                case 6:
                	int passes = Olympiad.getInstance().getNoblessePasses(player.getObjectId());
                    if (passes > 0)
                    {
                        L2ItemInstance item = player.getInventory().addItem("Olympiad", GATE_PASS, passes, player, this);

                        InventoryUpdate iu = new InventoryUpdate();
                        iu.addModifiedItem(item);
                        player.sendPacket(iu);

                        SystemMessage sm = new SystemMessage(SystemMessageId.EARNED_ITEM);
                        sm.addNumber(passes);
                        sm.addItemName(item.getItemId());
                        player.sendPacket(sm);
                    }
                    else
                    {
                        player.sendMessage("Not enough points, or not currently in Validation Period");
                        //TODO Send HTML packet "Saying not enough olympiad points.
                    }
                    break;
                case 7:
                	L2Multisell.getInstance().SeparateAndSend(102, player, false, getCastle().getTaxRate());
                    break;
                    default:
                        _logOlymp.warning("Olympiad System: Couldnt send packet for request " + val);
                    break;

            }
        }
        else if (command.startsWith("Olympiad"))
        {
            int val = Integer.parseInt(command.substring(9,10));

            NpcHtmlMessage reply = new NpcHtmlMessage(getObjectId());
            StringBuilder replyMSG = new StringBuilder("<html><body>");

            switch (val)
            {
                case 1:
                    String[] matches = Olympiad.getInstance().getMatchList();

                    replyMSG.append("Grand Olympiad Games Overview<br><br>" + "* Caution: Please note, if you watch an Olympiad " + "game, the summoning of your Servitors or Pets will be " + "cancelled. Be careful.<br>");

                    if (matches == null)
                        replyMSG.append("<br>There are no matches at the moment");
                    else
                    {
                        for (int i = 0; i < matches.length; i++)
                        {
                            replyMSG.append("<br><a action=\"bypass -h npc_"+getObjectId()+"_Olympiad 3_" + i + "\">" +
                                    matches[i] + "</a>");
                        }
                    }
                    replyMSG.append("</body></html>");

                    reply.setHtml(replyMSG.toString());
                    player.sendPacket(reply);
                    break;
                case 2:
                    // for example >> Olympiad 1_88
                    int classId = Integer.parseInt(command.substring(11));
                    if (classId >= 88)
                    {
                        replyMSG.append("<center>Grand Olympiad Ranking");
                        replyMSG.append("<img src=\"L2UI.SquareWhite\" width=270 height=1><img src=\"L2UI.SquareBlank\" width=1 height=3>");

                        List<String> names = Olympiad.getInstance().getClassLeaderBoard(classId);
                        if (names.size() != 0)
                        {
                            replyMSG.append("<table width=270 border=0 bgcolor=\"000000\">");

                            int index = 1;

                            for (String name : names)
                            {
                                replyMSG.append("<tr>");
                                replyMSG.append("<td align=\"left\">" + index + "</td>");
                                replyMSG.append("<td align=\"right\">" + name + "</td>");
                                replyMSG.append("</tr>");
                                index++;
                            }

                            replyMSG.append("</table>");
                        }

                        replyMSG.append("<img src=\"L2UI.SquareWhite\" width=270 height=1> <img src=\"L2UI.SquareBlank\" width=1 height=3>");
                        replyMSG.append("</center>");
                        replyMSG.append("</body></html>");

                        reply.setHtml(replyMSG.toString());
                        player.sendPacket(reply);
                    }
                    break;
                case 3:
                    int id = Integer.parseInt(command.substring(11));
                    Olympiad.getInstance().addSpectator(id, player);
                    break;
                case 4:
                    player.sendPacket(new ExHeroList());
                    break;
                    default:
                        _logOlymp.warning("Olympiad System: Couldn't send packet for request " + val);
                    break;
            }
        }
        else
            super.onBypassFeedback(player, command);
    }

    private void showChatWindow(L2PcInstance player, int val, String suffix)
    {
        String filename = Olympiad.OLYMPIAD_HTML_FILE;

        filename += "noble_desc" + val;
        filename += (suffix != null)? suffix + ".htm" : ".htm";

        if (filename.equals(Olympiad.OLYMPIAD_HTML_FILE + "noble_desc0.htm"))
            filename = Olympiad.OLYMPIAD_HTML_FILE + "noble_main.htm";

        showChatWindow(player, filename);
    }
}