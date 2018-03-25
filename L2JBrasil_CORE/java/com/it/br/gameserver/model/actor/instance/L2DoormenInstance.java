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

import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.instancemanager.ClanHallManager;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.entity.ClanHall;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.MyTargetSelected;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.ValidateLocation;
import com.it.br.gameserver.templates.L2NpcTemplate;

import java.util.StringTokenizer;

/**
 * This class ...
 *
 * @version $Revision$ $Date$
 */
public class L2DoormenInstance extends L2FolkInstance
{
    private ClanHall _clanHall;
    private static int COND_ALL_FALSE = 0;
    private static int COND_BUSY_BECAUSE_OF_SIEGE = 1;
    private static int COND_CASTLE_OWNER = 2;
    private static int COND_HALL_OWNER = 3;
	private static int COND_FORT_OWNER = 4;

    /**
     * @param template
     */
    public L2DoormenInstance(int objectID, L2NpcTemplate template)
    {
        super(objectID, template);
    }

    public final ClanHall getClanHall()
    {
        //_log.warn(this.getName()+" searching ch");
        if (_clanHall == null)
            _clanHall = ClanHallManager.getInstance().getNearbyClanHall(getX(), getY(), 500);
        //if (_ClanHall != null)
        //    _log.warn(this.getName()+" found ch "+_ClanHall.getName());
        return _clanHall;
    }


    @Override
	public void onBypassFeedback(L2PcInstance player, String command)
    {
        int condition = validateCondition(player);
        if (condition <= COND_ALL_FALSE) return;
        if (condition == COND_BUSY_BECAUSE_OF_SIEGE) return;
        else if(condition == COND_CASTLE_OWNER || condition == COND_HALL_OWNER || condition == COND_FORT_OWNER)
        {
            if (command.startsWith("Chat"))
            {
                showMessageWindow(player);
                return;
            }
            else if (command.startsWith("open_doors"))
			{
				if(condition == COND_HALL_OWNER)
				{
					getClanHall().openCloseDoors(true);
					player.sendPacket(new NpcHtmlMessage(getObjectId(), "<html><body>You have <font color=\"LEVEL\">opened</font> the clan hall door.<br>Outsiders may enter the clan hall while the door is open. Please close it when you've finished your business.<br><center><button value=\"Close\" action=\"bypass -h npc_" + getObjectId() + "_close_doors\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center></body></html>"));
				}
				else if(condition == COND_CASTLE_OWNER)
				{
					//DoorTable doorTable = DoorTable.getInstance();
					StringTokenizer st = new StringTokenizer(command.substring(10), ", ");
					st.nextToken(); // Bypass first value since its castleid/hallid

					while(st.hasMoreTokens())
					{
						getCastle().openDoor(player, Integer.parseInt(st.nextToken()));
					}

					st = null;
					return;
				}
			}
            else if (command.startsWith("close_doors"))
            {
				if(condition == COND_HALL_OWNER)
				{
					getClanHall().openCloseDoors(false);
					player.sendPacket(new NpcHtmlMessage(getObjectId(), "<html><body>You have <font color=\"LEVEL\">closed</font> the clan hall door.<br>Good day!<br><center><button value=\"To Begining\" action=\"bypass -h npc_" + getObjectId() + "_Chat\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center></body></html>"));
				}
				else if(condition == COND_CASTLE_OWNER)
				{
					//DoorTable doorTable = DoorTable.getInstance();
					StringTokenizer st = new StringTokenizer(command.substring(11), ", ");
					st.nextToken(); // Bypass first value since its castleid/hallid
					//L2Clan playersClan = player.getClan();

					while(st.hasMoreTokens())
					{
						getCastle().closeDoor(player, Integer.parseInt(st.nextToken()));
					}

					st = null;
					return;
				}
            }
        }

        super.onBypassFeedback(player, command);
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
			MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
			player.sendPacket(my);

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

	public void showMessageWindow(L2PcInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/doormen/" + getTemplate().npcId + "-no.htm";

		int condition = validateCondition(player);
		if(condition == COND_BUSY_BECAUSE_OF_SIEGE)
		{
			filename = "data/html/doormen/" + getTemplate().npcId + "-busy.htm"; // Busy because of siege
		}
		else if(condition == COND_CASTLE_OWNER)
		{
			filename = "data/html/doormen/" + getTemplate().npcId + ".htm"; // Owner message window
		}
		else if(condition == COND_FORT_OWNER)
		{
			filename = "data/html/doormen/fortress/" + getTemplate().npcId + ".htm"; // Owner message window
		}

		// Prepare doormen for clan hall
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		String str;
		if(getClanHall() != null)
		{
			if(condition == COND_HALL_OWNER)
			{
				str = "<html><body>Hello!<br><font color=\"55FFFF\">" + getName() + "</font>, I am honored to serve your clan.<br>How may i assist you?<br>";
				str += "<center><br><button value=\"Open Door\" action=\"bypass -h npc_%objectId%_open_doors\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><br>";
				str += "<button value=\"Close Door\" action=\"bypass -h npc_%objectId%_close_doors\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><br>";
				if(getClanHall().getId() >= 36 && getClanHall().getId() <= 41)
				{
					str += "<button value=\"Wyvern Exchange\" action=\"bypass -h npc_%objectId%_RideWyvern\" width=85 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center></body></html>";
				}
				else
				{
					str += "</center></body></html>";
				}
			}
			else
			{
				L2Clan owner = ClanTable.getInstance().getClan(getClanHall().getOwnerId());
				if(owner != null && owner.getLeader() != null)
				{
					str = "<html><body>Hello there!<br>This clan hall is owned by <font color=\"55FFFF\">" + owner.getLeader().getName() + " who is the Lord of the ";
					str += owner.getName() + "</font> clan.<br>";
					str += "I am sorry, but only the clan members who belong to the <font color=\"55FFFF\">" + owner.getName() + "</font> clan can enter the clan hall.</body></html>";
				}
				else
				{
					str = "<html><body>" + getName() + ":<br1>Clan hall <font color=\"LEVEL\">" + getClanHall().getName() + "</font> have no owner clan.<br>You can rent it at auctioneers..</body></html>";
				}
			}
			html.setHtml(str);
		}
		else
		{
			html.setFile(filename);
		}

		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);

		filename = null;
		html = null;
		str = null;
	}

	private int validateCondition(L2PcInstance player)
	{
		if(player.getClan() != null)
		{
			// Prepare doormen for clan hall
			if(getClanHall() != null)
			{
				if(player.getClanId() == getClanHall().getOwnerId())
					return COND_HALL_OWNER;
				return COND_ALL_FALSE;
			}
			// Prepare doormen for Castle
			if(getCastle() != null && getCastle().getCastleId() > 0)
			{
				if(getCastle().getOwnerId() == player.getClanId()) // Clan owns castle
					return COND_CASTLE_OWNER; // Owner
			}
		}
		return COND_ALL_FALSE;
	}
}