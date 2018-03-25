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
 * [URL]http://www.gnu.org/copyleft/gpl.html[/URL]
 */
package com.it.br.gameserver.model.actor.instance;

import com.it.br.configuration.settings.EventSettings;
import com.it.br.gameserver.cache.HtmCache;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.templates.L2NpcTemplate;

import static com.it.br.configuration.Configurator.getSettings;

public class L2TvTEventNpcInstance extends L2NpcInstance
{
	private static final String htmlPath="data/html/mods/";
	
	public L2TvTEventNpcInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2PcInstance playerInstance, String command)
	{
		TvTEvent.onBypass(command, playerInstance);
	}

	@Override
	public void showChatWindow(L2PcInstance playerInstance, int val)
	{
		if (playerInstance == null)
			return;

		if (TvTEvent.isParticipating())
		{
			final boolean isParticipant = TvTEvent.isPlayerParticipant(playerInstance.getObjectId()); 
			final String htmContent;

			if (!isParticipant)
				htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Participation.htm");
			else
				htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "RemoveParticipation.htm");

	    	if (htmContent != null)
	    	{
	    		EventSettings eventSettings = getSettings(EventSettings.class);
	    		int[] teamsPlayerCounts = TvTEvent.getTeamsPlayerCounts();
	    		NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				npcHtmlMessage.setHtml(htmContent);
	    		npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%team1name%", eventSettings.getTvTEventTeam1Name());
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team2name%", eventSettings.getTvTEventTeam2Name());
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%playercount%", String.valueOf(teamsPlayerCounts[0]+teamsPlayerCounts[1]));
				if (!isParticipant)
					npcHtmlMessage.replace("%fee%", TvTEvent.getParticipationFee());

				playerInstance.sendPacket(npcHtmlMessage);
	    	}
		}
		else if (TvTEvent.isStarting() || TvTEvent.isStarted())
		{
			final String htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Status.htm");

	    	if (htmContent != null)
	    	{
	    		int[] teamsPlayerCounts = TvTEvent.getTeamsPlayerCounts();
	    		int[] teamsPointsCounts = TvTEvent.getTeamsPoints();
	    		NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
	    		EventSettings eventSettings = getSettings(EventSettings.class);
				npcHtmlMessage.setHtml(htmContent);
	    		//npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%team1name%", eventSettings.getTvTEventTeam1Name());
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team1points%", String.valueOf(teamsPointsCounts[0]));
				npcHtmlMessage.replace("%team2name%",  eventSettings.getTvTEventTeam2Name());
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%team2points%", String.valueOf(teamsPointsCounts[1])); // <---- array index from 0 to 1 thx DaRkRaGe
	    		playerInstance.sendPacket(npcHtmlMessage);
	    	}
		}

		playerInstance.sendPacket(new ActionFailed());
	}
}