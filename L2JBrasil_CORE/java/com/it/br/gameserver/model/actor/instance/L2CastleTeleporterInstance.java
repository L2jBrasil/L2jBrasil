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

/**
 * @author NightMarez
 * @version $Revision: 1.3.2.2.2.5 $ $Date: 2005/03/27 15:29:32 $
 *
 */

import com.it.br.Config;
import com.it.br.gameserver.datatables.xml.TeleportLocationTable;
import com.it.br.gameserver.model.L2CharPosition;
import com.it.br.gameserver.model.L2TeleportLocation;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.templates.L2NpcTemplate;

import java.util.StringTokenizer;

public final class L2CastleTeleporterInstance extends L2FolkInstance
{
	//private static Logger _log = LoggerFactory.getLogger(L2TeleporterInstance.class);

	private static final int COND_ALL_FALSE = 0;
	private static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	private static final int COND_OWNER = 2;
	private static final int COND_REGULAR = 3;

	/**
	 * @param template
	 */
	public L2CastleTeleporterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}


	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		int condition = validateCondition(player);
		if (condition <= COND_BUSY_BECAUSE_OF_SIEGE)
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command

		if (actualCommand.equalsIgnoreCase("goto"))
		{
			if (st.countTokens() <= 0) {return;}
			int whereTo = Integer.parseInt(st.nextToken());
			if (condition == COND_REGULAR)
			{
				doTeleport(player, whereTo);
				return;
			}
			else if (condition == COND_OWNER)
			{
				int minPrivilegeLevel = 0; // NOTE: Replace 0 with highest level when privilege level is implemented
				if (st.countTokens() >= 1) {minPrivilegeLevel = Integer.parseInt(st.nextToken());}
				if (10 >= minPrivilegeLevel) // NOTE: Replace 10 with privilege level of player
					doTeleport(player, whereTo);
				else
					player.sendMessage("You don't have the sufficient access level to teleport there.");
				return;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}


	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}

		return "data/html/teleporter/" + pom + ".htm";
	}



	@Override
	public void showChatWindow(L2PcInstance player)
	{
		String filename = "data/html/teleporter/castleteleporter-no.htm";

		int condition = validateCondition(player);
		if (condition == COND_REGULAR)
		{
			super.showChatWindow(player);
			return;
		}
		else if (condition > COND_ALL_FALSE)
		{
			if (condition == COND_BUSY_BECAUSE_OF_SIEGE)
				filename = "data/html/teleporter/castleteleporter-busy.htm"; // Busy because of siege
			else if (condition == COND_OWNER)                                // Clan owns castle
				filename = "data/html/teleporter/" + getNpcId() + ".htm";    // Owner message window
		}

		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}

	private void doTeleport(L2PcInstance player, int val)
	{
		L2TeleportLocation list = TeleportLocationTable.getInstance().getTemplate(val);
		if (list != null)
		{
			if(player.reduceAdena("Teleport", list.getPrice(), player.getLastFolkNPC(), true))
			{
				if (Config.DEBUG)
					_log.debug("Teleporting player "+player.getName()+" to new location: "+list.getLocX()+":"+list.getLocY()+":"+list.getLocZ());

				// teleport
				player.teleToLocation(list.getLocX(), list.getLocY(), list.getLocZ(), true);
				player.stopMove(new L2CharPosition(list.getLocX(), list.getLocY(), list.getLocZ(), player.getHeading()));
			}
		}
		else
		{
			_log.warn("No teleport destination with id:" +val);
		}
		player.sendPacket( new ActionFailed() );
	}

	private int validateCondition(L2PcInstance player)
	{
		if (player.getClan() != null && getCastle() != null)
		{
			if (getCastle().getSiege().getIsInProgress())
				return COND_BUSY_BECAUSE_OF_SIEGE;                    // Busy because of siege
			else if (getCastle().getOwnerId() == player.getClanId())  // Clan owns castle
				return COND_OWNER;	// Owner
		}

		return COND_ALL_FALSE;
	}
}
