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

import com.it.br.Config;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.datatables.xml.CharTemplateTable;
import com.it.br.gameserver.model.base.ClassId;
import com.it.br.gameserver.model.base.ClassLevel;
import com.it.br.gameserver.model.base.PlayerClass;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.*;
import com.it.br.gameserver.templates.L2NpcTemplate;

/**
 * This class ...
 *
 * @version $Revision: 1.4.2.1.2.7 $ $Date: 2005/03/27 15:29:32 $
 */
public final class L2ClassMasterInstance extends L2FolkInstance
{
	//private static Logger _log = LoggerFactory.getLogger(L2ClassMasterInstance.class);
	private static final int[] SECOND_CLASS_IDS = {2,3,5,6,9,8,12,13,14,16,17,20,21,23,24,27,28,30,33,34,36,37,40,41,43,46,48,51,52,55,57};

	/**
	 * @param template
	 */
	public L2ClassMasterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}


	public void onAction(L2PcInstance player)
	{
		if (!canTarget(player)) return;

		// Check if the L2PcInstance already target the L2NpcInstance
		if (getObjectId() != player.getTargetId())
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
			if (!canInteract(player))
			{
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
				return;
			}

			if (Config.DEBUG)
				_log.debug("ClassMaster activated");

			ClassId classId = player.getClassId();

			int jobLevel = 0;
			int level = player.getLevel();
			ClassLevel lvl = PlayerClass.values()[classId.getId()].getLevel();
			switch (lvl)
			{
				case First:
					jobLevel = 1;
					break;
				case Second:
					jobLevel = 2;
					break;
				default:
					jobLevel = 3;
			}

			if (!Config.ALLOW_CLASS_MASTERS)
				jobLevel = 3;

			if(player.isGM())
			{
				showChatWindowChooseClass(player);
			}
			else if (((level >= 20 && jobLevel == 1 ) ||
				(level >= 40 && jobLevel == 2 )) && Config.ALLOW_CLASS_MASTERS)
			{
				showChatWindow(player, classId.getId());
			}
			else if (level >= 76 && Config.ALLOW_CLASS_MASTERS && classId.getId() < 88)
			{
				for (int i = 0; i < SECOND_CLASS_IDS.length; i++)
				{
					if (classId.getId() == SECOND_CLASS_IDS[i])
					{
                        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                        StringBuilder sb = new StringBuilder();
                        sb.append("<html><body<table width=200>");
                        sb.append("<tr><td><center>"+CharTemplateTable.getClassNameById(player.getClassId().getId())+" Class Master:</center></td></tr>");
                        sb.append("<tr><td><br></td></tr>");
                        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class "+(88+i)+"\">Advance to "+CharTemplateTable.getClassNameById(88+i)+"</a></td></tr>");
                        sb.append("<tr><td><br></td></tr>");
                        sb.append("</table></body></html>");
                        html.setHtml(sb.toString());
                        player.sendPacket(html);
                        break;
					}
				}
			}
			else
			{
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				StringBuilder sb = new StringBuilder();
				sb.append("<html><body>");
				switch (jobLevel)
				{
					case 1:
						sb.append("Come back here when you reach level 20 to change your class.<br>");
						break;
					case 2:
						sb.append("Come back here when you reach level 40 to change your class.<br>");
						break;
					case 3:
						sb.append("There are no more class changes for you.<br>");
						break;
				}

				for (Quest q : Quest.findAllEvents())
					sb.append("Event: <a action=\"bypass -h Quest "+q.getName()+"\">"+q.getDescr()+"</a><br>");

				sb.append("</body></html>");
				html.setHtml(sb.toString());
				player.sendPacket(html);
			}
		}
		player.sendPacket(new ActionFailed());
	}


	public String getHtmlPath(int npcId, int val)
	{
		return "data/html/classmaster/" + val + ".htm";
	}


	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if(command.startsWith("1stClass"))
		{
			if(player.isGM())
			{
				showChatWindow1st(player);
			}
		}
		else if(command.startsWith("2ndClass"))
		{
			if(player.isGM())
			{
				showChatWindow2nd(player);
			}
		}
		else if(command.startsWith("3rdClass"))
		{
			if(player.isGM())
			{
				showChatWindow3rd(player);
			}
		}
		else if(command.startsWith("baseClass"))
		{
			if(player.isGM())
			{
				showChatWindowBase(player);
			}
		}
		else if(command.startsWith("change_class"))
		{
            int val = Integer.parseInt(command.substring(13));

            // Exploit prevention
            ClassId classId = player.getClassId();
            int level = player.getLevel();
            int jobLevel = 0;
            int newJobLevel = 0;

            ClassLevel lvlnow = PlayerClass.values()[classId.getId()].getLevel();

            if(player.isGM())
            {
            	changeClass(player, val);
                player.rewardSkills(); 
                
            	if(val >= 88)
                	player.sendPacket(new SystemMessage(SystemMessageId.THIRD_CLASS_TRANSFER)); // system sound 3rd occupation
                else
                	player.sendPacket(new SystemMessage(SystemMessageId.CLASS_TRANSFER));    // system sound for 1st and 2nd occupation

                NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
                StringBuilder sb = new StringBuilder();
                sb.append("<html><body>");
                sb.append("You have now become a <font color=\"LEVEL\">" + CharTemplateTable.getClassNameById(player.getClassId().getId()) + "</font>.");
                sb.append("</body></html>");

                html.setHtml(sb.toString());
                player.sendPacket(html);
            	return;
            }
            switch (lvlnow)
            {
            	case First:
            		jobLevel = 1;
            		break;
            	case Second:
            		jobLevel = 2;
            		break;
            	case Third:
            		jobLevel = 3;
            		break;
            	default:
            		jobLevel = 4;
            }

            if(jobLevel == 4) return; // no more job changes

            ClassLevel lvlnext = PlayerClass.values()[val].getLevel();
            switch (lvlnext)
            {
            	case First:
            		newJobLevel = 1;
            		break;
            	case Second:
            		newJobLevel = 2;
            		break;
            	case Third:
            		newJobLevel = 3;
            		break;
            	default:
            		newJobLevel = 4;
            }

            // prevents changing between same level jobs
            if(newJobLevel != jobLevel + 1) return;

            if (level < 20 && newJobLevel > 1) return;
            if (level < 40 && newJobLevel > 2) return;
            if (level < 75 && newJobLevel > 3) return;
            // -- prevention ends


            changeClass(player, val);
            player.rewardSkills();

            if(val >= 88)
            	player.sendPacket(new SystemMessage(SystemMessageId.THIRD_CLASS_TRANSFER)); // system sound 3rd occupation
            else
            	player.sendPacket(new SystemMessage(SystemMessageId.CLASS_TRANSFER));    // system sound for 1st and 2nd occupation

            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body>");
            sb.append("You have now become a <font color=\"LEVEL\">" + CharTemplateTable.getClassNameById(player.getClassId().getId()) + "</font>.");
            sb.append("</body></html>");

            html.setHtml(sb.toString());
            player.sendPacket(html);
       }
       else
       {
           super.onBypassFeedback(player, command);
       }
 }
	private void showChatWindowChooseClass(L2PcInstance player)
	{
  		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<table width=200>");
        sb.append("<tr><td><center>GM Class Master:</center></td></tr>");
        sb.append("<tr><td><br></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_baseClass\">Base Classes.</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_1stClass\">1st Classes.</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_2ndClass\">2nd Classes.</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_3rdClass\">3rd Classes.</a></td></tr>");
        sb.append("<tr><td><br></td></tr>");
        sb.append("</table>");
        sb.append("<br><font color=\"LEVEL\">Please notice this menu is only available for Game Masters, not for normal players ;)</font>"); 
        sb.append("</body>");
        sb.append("</html>");
        html.setHtml(sb.toString());
        player.sendPacket(html);
        return;
	}

	private void showChatWindow1st(L2PcInstance player)
	{
  		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<table width=200>");
        sb.append("<tr><td><center>GM Class Master:</center></td></tr>");
        sb.append("<tr><td><br></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 1\">Advance to "+CharTemplateTable.getClassNameById(1)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 4\">Advance to "+CharTemplateTable.getClassNameById(4)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 7\">Advance to "+CharTemplateTable.getClassNameById(7)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 11\">Advance to "+CharTemplateTable.getClassNameById(11)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 15\">Advance to "+CharTemplateTable.getClassNameById(15)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 19\">Advance to "+CharTemplateTable.getClassNameById(19)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 22\">Advance to "+CharTemplateTable.getClassNameById(22)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 26\">Advance to "+CharTemplateTable.getClassNameById(26)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 29\">Advance to "+CharTemplateTable.getClassNameById(29)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 32\">Advance to "+CharTemplateTable.getClassNameById(32)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 35\">Advance to "+CharTemplateTable.getClassNameById(35)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 39\">Advance to "+CharTemplateTable.getClassNameById(39)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 42\">Advance to "+CharTemplateTable.getClassNameById(42)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 45\">Advance to "+CharTemplateTable.getClassNameById(45)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 47\">Advance to "+CharTemplateTable.getClassNameById(47)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 50\">Advance to "+CharTemplateTable.getClassNameById(50)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 54\">Advance to "+CharTemplateTable.getClassNameById(54)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 56\">Advance to "+CharTemplateTable.getClassNameById(56)+"</a></td></tr>");
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");
        html.setHtml(sb.toString());
        player.sendPacket(html);
        return;
	}

	private void showChatWindow2nd(L2PcInstance player)
	{
  		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<table width=200>");
        sb.append("<tr><td><center>GM Class Master:</center></td></tr>");
        sb.append("<tr><td><br></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 2\">Advance to "+CharTemplateTable.getClassNameById(2)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 3\">Advance to "+CharTemplateTable.getClassNameById(3)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 5\">Advance to "+CharTemplateTable.getClassNameById(5)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 6\">Advance to "+CharTemplateTable.getClassNameById(6)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 8\">Advance to "+CharTemplateTable.getClassNameById(8)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 9\">Advance to "+CharTemplateTable.getClassNameById(9)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 12\">Advance to "+CharTemplateTable.getClassNameById(12)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 13\">Advance to "+CharTemplateTable.getClassNameById(13)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 14\">Advance to "+CharTemplateTable.getClassNameById(14)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 16\">Advance to "+CharTemplateTable.getClassNameById(16)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 17\">Advance to "+CharTemplateTable.getClassNameById(17)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 20\">Advance to "+CharTemplateTable.getClassNameById(20)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 21\">Advance to "+CharTemplateTable.getClassNameById(21)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 23\">Advance to "+CharTemplateTable.getClassNameById(23)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 24\">Advance to "+CharTemplateTable.getClassNameById(24)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 27\">Advance to "+CharTemplateTable.getClassNameById(27)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 28\">Advance to "+CharTemplateTable.getClassNameById(28)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 30\">Advance to "+CharTemplateTable.getClassNameById(30)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 33\">Advance to "+CharTemplateTable.getClassNameById(33)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 34\">Advance to "+CharTemplateTable.getClassNameById(34)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 36\">Advance to "+CharTemplateTable.getClassNameById(36)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 37\">Advance to "+CharTemplateTable.getClassNameById(37)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 40\">Advance to "+CharTemplateTable.getClassNameById(40)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 41\">Advance to "+CharTemplateTable.getClassNameById(41)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 43\">Advance to "+CharTemplateTable.getClassNameById(43)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 46\">Advance to "+CharTemplateTable.getClassNameById(46)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 48\">Advance to "+CharTemplateTable.getClassNameById(48)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 51\">Advance to "+CharTemplateTable.getClassNameById(51)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 52\">Advance to "+CharTemplateTable.getClassNameById(52)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 55\">Advance to "+CharTemplateTable.getClassNameById(55)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 57\">Advance to "+CharTemplateTable.getClassNameById(57)+"</a></td></tr>");
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");
        html.setHtml(sb.toString());
        player.sendPacket(html);
        return;
	}

	private void showChatWindow3rd(L2PcInstance player)
	{
  		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<table width=200>");
        sb.append("<tr><td><center>GM Class Master:</center></td></tr>");
        sb.append("<tr><td><br></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 88\">Advance to "+CharTemplateTable.getClassNameById(88)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 89\">Advance to "+CharTemplateTable.getClassNameById(89)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 90\">Advance to "+CharTemplateTable.getClassNameById(90)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 91\">Advance to "+CharTemplateTable.getClassNameById(91)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 92\">Advance to "+CharTemplateTable.getClassNameById(92)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 93\">Advance to "+CharTemplateTable.getClassNameById(93)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 94\">Advance to "+CharTemplateTable.getClassNameById(94)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 95\">Advance to "+CharTemplateTable.getClassNameById(95)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 96\">Advance to "+CharTemplateTable.getClassNameById(96)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 97\">Advance to "+CharTemplateTable.getClassNameById(97)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 98\">Advance to "+CharTemplateTable.getClassNameById(98)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 99\">Advance to "+CharTemplateTable.getClassNameById(99)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 100\">Advance to "+CharTemplateTable.getClassNameById(100)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 101\">Advance to "+CharTemplateTable.getClassNameById(101)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 102\">Advance to "+CharTemplateTable.getClassNameById(102)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 103\">Advance to "+CharTemplateTable.getClassNameById(103)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 104\">Advance to "+CharTemplateTable.getClassNameById(104)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 105\">Advance to "+CharTemplateTable.getClassNameById(105)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 106\">Advance to "+CharTemplateTable.getClassNameById(106)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 107\">Advance to "+CharTemplateTable.getClassNameById(107)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 108\">Advance to "+CharTemplateTable.getClassNameById(108)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 109\">Advance to "+CharTemplateTable.getClassNameById(109)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 110\">Advance to "+CharTemplateTable.getClassNameById(110)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 111\">Advance to "+CharTemplateTable.getClassNameById(111)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 112\">Advance to "+CharTemplateTable.getClassNameById(112)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 113\">Advance to "+CharTemplateTable.getClassNameById(113)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 114\">Advance to "+CharTemplateTable.getClassNameById(114)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 115\">Advance to "+CharTemplateTable.getClassNameById(115)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 116\">Advance to "+CharTemplateTable.getClassNameById(116)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 117\">Advance to "+CharTemplateTable.getClassNameById(117)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 118\">Advance to "+CharTemplateTable.getClassNameById(118)+"</a></td></tr>");
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");
        html.setHtml(sb.toString());
        player.sendPacket(html);
        return;
	}

	private void showChatWindowBase(L2PcInstance player)
	{
  		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<table width=200>");
        sb.append("<tr><td><center>GM Class Master:</center></td></tr>");
        sb.append("<tr><td><br></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 0\">Advance to "+CharTemplateTable.getClassNameById(0)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 10\">Advance to "+CharTemplateTable.getClassNameById(10)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 18\">Advance to "+CharTemplateTable.getClassNameById(18)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 25\">Advance to "+CharTemplateTable.getClassNameById(25)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 31\">Advance to "+CharTemplateTable.getClassNameById(31)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 38\">Advance to "+CharTemplateTable.getClassNameById(38)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 44\">Advance to "+CharTemplateTable.getClassNameById(44)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 49\">Advance to "+CharTemplateTable.getClassNameById(49)+"</a></td></tr>");
        sb.append("<tr><td><a action=\"bypass -h npc_"+getObjectId()+"_change_class 53\">Advance to "+CharTemplateTable.getClassNameById(53)+"</a></td></tr>");
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");
        html.setHtml(sb.toString());
        player.sendPacket(html);
        return;
	}

	private void changeClass(L2PcInstance player, int val)
	{
		if (Config.DEBUG) _log.debug("Changing class to ClassId:"+val);
        player.setClassId(val);

        if (player.isSubClassActive())
            player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
        else
            player.setBaseClass(player.getActiveClass());

		player.broadcastUserInfo();
	}
}
