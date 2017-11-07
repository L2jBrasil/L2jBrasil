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
package com.it.br.gameserver.communitybbs.Manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.GameServer;
import com.it.br.gameserver.model.BlockList;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.Experience;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.clientpackets.Say2;
import com.it.br.gameserver.network.serverpackets.CreatureSay;
import com.it.br.gameserver.network.serverpackets.ShowBoard;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

public class RegionBBSManager extends BaseBBSManager
{
	private static Logger _logChat = Logger.getLogger("chat");
	/* (non-Javadoc)
	 * @see com.it.br.gameserver.communitybbs.Manager.BaseBBSManager#parsecmd(java.lang.String, com.it.br.gameserver.model.actor.instance.L2PcInstance)
	 */

	public void parsecmd(String command, L2PcInstance activeChar)
	{
		if (command.equals("_bbsloc"))
		{
			showOldCommunity(activeChar, 1);
		}
		else if (command.startsWith("_bbsloc;page;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int page = 0;
            try
            {
                page = Integer.parseInt(st.nextToken());
            } catch (NumberFormatException nfe) {}

			showOldCommunity(activeChar, page);
		}
		else if (command.startsWith("_bbsloc;playerinfo;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			String name = st.nextToken();

			showOldCommunityPI(activeChar, name);
		}
		else
		{
			if(Config.COMMUNITY_TYPE.equals("old"))
			{
				showOldCommunity(activeChar, 1);
			}
			else
			{
    			ShowBoard sb = new ShowBoard("<html><body><br><br><center>the command: "+command+" is not implemented yet</center><br><br></body></html>","101");
    			activeChar.sendPacket(sb);
    			activeChar.sendPacket(new ShowBoard(null,"102"));
    			activeChar.sendPacket(new ShowBoard(null,"103"));
			}
		}
	}

	/**
	 * @param activeChar
	 * @param name
	 */
	private void showOldCommunityPI(L2PcInstance activeChar, String name)
	{
        StringBuilder htmlCode = new StringBuilder("<html><body><br>");
		htmlCode.append("<table border=0><tr><td FIXWIDTH=15></td><td align=center>L2J Community Board<img src=\"sek.cbui355\" width=610 height=1></td></tr><tr><td FIXWIDTH=15></td><td>");
		L2PcInstance player = L2World.getInstance().getPlayer(name);

		if (player != null)
		{
		    String sex = "Male";
		    if (player.getAppearance().getSex())
		    {
		        sex = "Female";
		    }
		    String levelApprox = "low";
		    if (player.getLevel() >= 60)
		        levelApprox = "very high";
		    else if (player.getLevel() >= 40)
		        levelApprox = "high";
		    else if (player.getLevel() >= 20)
		        levelApprox = "medium";
		    htmlCode.append("<table border=0><tr><td>"+player.getName()+" ("+sex+" "+player.getTemplate().className+"):</td></tr>");
		    htmlCode.append("<tr><td>Level: "+levelApprox+"</td></tr>");
		    htmlCode.append("<tr><td><br></td></tr>");

		    if (activeChar != null && (activeChar.isGM() || player.getObjectId() == activeChar.getObjectId()
		            || Config.SHOW_LEVEL_COMMUNITYBOARD))
		    {
		        long nextLevelExp = 0;
		        long nextLevelExpNeeded = 0;
		        if (player.getLevel() < (Experience.MAX_LEVEL - 1))
		        {
		            nextLevelExp = Experience.LEVEL[player.getLevel() + 1];
		            nextLevelExpNeeded = nextLevelExp-player.getExp();
		        }

		        htmlCode.append("<tr><td>Level: "+player.getLevel()+"</td></tr>");
		        htmlCode.append("<tr><td>Experience: "+player.getExp()+"/"+nextLevelExp+"</td></tr>");
		        htmlCode.append("<tr><td>Experience needed for level up: "+nextLevelExpNeeded+"</td></tr>");
		        htmlCode.append("<tr><td><br></td></tr>");
		    }

		    int uptime = (int)player.getUptime()/1000;
		    int h = uptime/3600;
		    int m = (uptime-(h*3600))/60;
		    int s = ((uptime-(h*3600))-(m*60));

		    htmlCode.append("<tr><td>Uptime: "+h+"h "+m+"m "+s+"s</td></tr>");
		    htmlCode.append("<tr><td><br></td></tr>");

		    if (player.getClan() != null)
		    {
		        htmlCode.append("<tr><td>Clan: "+player.getClan().getName()+"</td></tr>");
		        htmlCode.append("<tr><td><br></td></tr>");
		    }

		    htmlCode.append("<tr><td><multiedit var=\"pm\" width=240 height=40><button value=\"Send PM\" action=\"Write Region PM "+player.getName()+" pm pm pm\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr><tr><td><br><button value=\"Back\" action=\"bypass _bbsloc\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr></table>");
		    htmlCode.append("</td></tr></table>");
	          htmlCode.append("</body></html>");
	          separateAndSend(htmlCode.toString(),activeChar);
		}
		else
		{
			ShowBoard sb = new ShowBoard("<html><body><br><br><center>No player with name "+name+"</center><br><br></body></html>","101");
			activeChar.sendPacket(sb);
			activeChar.sendPacket(new ShowBoard(null,"102"));
			activeChar.sendPacket(new ShowBoard(null,"103"));
		}
	}

	/**
	 * @param activeChar
	 */
	private void showOldCommunity(L2PcInstance activeChar,int page)
	{
        separateAndSend(getCommunityPage(page, activeChar.isGM() ? "gm" : "pl"),activeChar);
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.communitybbs.Manager.BaseBBSManager#parsewrite(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.it.br.gameserver.model.actor.instance.L2PcInstance)
	 */

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
        if (activeChar == null)
            return;

		if (ar1.equals("PM"))
		{
            StringBuilder htmlCode = new StringBuilder("<html><body><br>");
            htmlCode.append("<table border=0><tr><td FIXWIDTH=15></td><td align=center>L2J Community Board<img src=\"sek.cbui355\" width=610 height=1></td></tr><tr><td FIXWIDTH=15></td><td>");

            try
            {

            	L2PcInstance receiver = L2World.getInstance().getPlayer(ar2);
            	if (receiver == null)
            	{
            		htmlCode.append("Player not found!<br><button value=\"Back\" action=\"bypass _bbsloc;playerinfo;"+ar2+"\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
            		htmlCode.append("</td></tr></table></body></html>");
            		separateAndSend(htmlCode.toString(),activeChar);
            		return;
            	}
        		if (Config.JAIL_DISABLE_CHAT && receiver.isInJail())
		        {
		                activeChar.sendMessage("Player is in jail.");
		                return;
		        }
				if (receiver.isChatBanned())
		        {
		                activeChar.sendMessage("Player is chat banned.");
		                return;
		        }
                if (activeChar.isInJail() && Config.JAIL_DISABLE_CHAT)
                {
                    activeChar.sendMessage("You can not chat while in jail.");
                    return;
                }

                if (Config.LOG_CHAT)
            	{
            		LogRecord record = new LogRecord(Level.INFO, ar3);
            		record.setLoggerName("chat");
            		record.setParameters(new Object[]{"TELL", "[" + activeChar.getName() + " to "+receiver.getName()+"]"});
            		_logChat.log(record);
				}
            	CreatureSay cs = new CreatureSay(activeChar.getObjectId(), Say2.TELL, activeChar.getName(), ar3);
            	if (receiver != null &&
            			!BlockList.isBlocked(receiver, activeChar))
				{
            		if (!receiver.getMessageRefusal())
            		{
            			receiver.sendPacket(cs);
            			activeChar.sendPacket(new CreatureSay(activeChar.getObjectId(), Say2.TELL, "->" + receiver.getName(), ar3));
            			htmlCode.append("Message Sent<br><button value=\"Back\" action=\"bypass _bbsloc;playerinfo;"+receiver.getName()+"\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
            			htmlCode.append("</td></tr></table></body></html>");
            			separateAndSend(htmlCode.toString(),activeChar)  ;
					}
            		else
            		{
            			SystemMessage sm = new SystemMessage(SystemMessageId.THE_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
            			activeChar.sendPacket(sm);
            			parsecmd("_bbsloc;playerinfo;"+receiver.getName(), activeChar);
					}
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_IS_NOT_ONLINE);
					sm.addString(receiver.getName());
					activeChar.sendPacket(sm);
					sm = null;
				}
			}
            catch (StringIndexOutOfBoundsException e)
            {
            	// ignore
			}
		}
		else
		{
			ShowBoard sb = new ShowBoard("<html><body><br><br><center>the command: "+ar1+" is not implemented yet</center><br><br></body></html>","101");
			activeChar.sendPacket(sb);
			activeChar.sendPacket(new ShowBoard(null,"102"));
			activeChar.sendPacket(new ShowBoard(null,"103"));
		}

	}
	private static RegionBBSManager _instance = null;
	private int _onlineCount = 0;
	private int _onlineCountGm = 0;
	private static Map<Integer, List<L2PcInstance>> _onlinePlayers = new ConcurrentHashMap<>();
	private static Map<Integer, Map<String, String>> _communityPages = new ConcurrentHashMap<>();
	/**
	 * @return
	 */
	public static RegionBBSManager getInstance()
	{
		if(_instance == null)
		{
			_instance = new RegionBBSManager();
		}
		return _instance;
	}

	public synchronized void changeCommunityBoard()
	{
		Collection<L2PcInstance> players = L2World.getInstance().getAllPlayers();
		List<L2PcInstance> sortedPlayers = new ArrayList<>();
		sortedPlayers.addAll(players);

        sortedPlayers.sort((L2PcInstance p1, L2PcInstance p2) -> p1.getName().compareToIgnoreCase(p2.getName()));

		_onlinePlayers.clear();
		_onlineCount = 0;
		_onlineCountGm = 0;

		for (L2PcInstance player : sortedPlayers)
		{
			addOnlinePlayer(player);
		}

		_communityPages.clear();
		writeCommunityPages();
	}

	private void addOnlinePlayer(L2PcInstance player)
	{
		boolean added = false;

		for (List<L2PcInstance> page : _onlinePlayers.values())
		{
			if (page.size() < Config.NAME_PAGE_SIZE_COMMUNITYBOARD)
			{
				if (!page.contains(player))
				{
					page.add(player);
					if (!player.getAppearance().getInvisible())
						_onlineCount++;
					_onlineCountGm++;
				}
				added = true;
				break;
			}
			else if (page.contains(player))
			{
				added = true;
				break;
			}
		}

		if (!added)
		{
			List<L2PcInstance> temp = new ArrayList<>();
			int page = _onlinePlayers.size()+1;
			if (temp.add(player))
			{
				_onlinePlayers.put(page, temp);
				if (!player.getAppearance().getInvisible())
					_onlineCount++;
				_onlineCountGm++;
			}
		}
	}

	private void writeCommunityPages()
	{
		for (int page : _onlinePlayers.keySet())
		{
	        Map<String, String> communityPage = new HashMap<>();
	        StringBuilder htmlCode = new StringBuilder("<html><body><br>");
	        String tdClose = "</td>";
	        String tdOpen = "<td align=left valign=top>";
	        String trClose = "</tr>";
	        String trOpen = "<tr>";
	        String colSpacer = "<td FIXWIDTH=15></td>";

	        htmlCode.append("<table>");

	        htmlCode.append(trOpen);
	        htmlCode.append("<td align=left valign=top>Server Restarted: " + GameServer.dateTimeServerStarted.getTime() + tdClose);
	        htmlCode.append(trClose);

	        htmlCode.append("</table>");

	        htmlCode.append("<table>");

	        htmlCode.append(trOpen);
	        htmlCode.append(tdOpen + "XP Rate: x" + Config.RATE_XP + tdClose);
	        htmlCode.append(colSpacer);
	        htmlCode.append(tdOpen + "Party XP Rate: x" +  Config.RATE_XP * Config.RATE_PARTY_XP + tdClose);
	        htmlCode.append(colSpacer);
	        htmlCode.append(tdOpen + "XP Exponent: " + Config.ALT_GAME_EXPONENT_XP + tdClose);
	        htmlCode.append(trClose);

	        htmlCode.append(trOpen);
	        htmlCode.append(tdOpen + "SP Rate: x" + Config.RATE_SP + tdClose);
	        htmlCode.append(colSpacer);
	        htmlCode.append(tdOpen + "Party SP Rate: x" + Config.RATE_SP * Config.RATE_PARTY_SP + tdClose);
	        htmlCode.append(colSpacer);
	        htmlCode.append(tdOpen + "SP Exponent: " + Config.ALT_GAME_EXPONENT_SP + tdClose);
	        htmlCode.append(trClose);

	        htmlCode.append(trOpen);
	        htmlCode.append(tdOpen + "Drop Rate: " + Config.RATE_DROP_ITEMS + tdClose);
	        htmlCode.append(colSpacer);
	        htmlCode.append(tdOpen + "Spoil Rate: " + Config.RATE_DROP_SPOIL + tdClose);
	        htmlCode.append(colSpacer);
	        htmlCode.append(tdOpen + "Adena Rate: " + Config.RATE_DROP_ADENA + tdClose);
	        htmlCode.append(trClose);
	        htmlCode.append("</table>");

	        htmlCode.append("<table>");
	        htmlCode.append(trOpen);
	        htmlCode.append("<td><img src=\"sek.cbui355\" width=600 height=1><br></td>");
	        htmlCode.append(trClose);

            htmlCode.append(trOpen);
            htmlCode.append(tdOpen + L2World.getInstance().getAllVisibleObjectsCount() + " Object count</td>");
            htmlCode.append(trClose);

	        htmlCode.append(trOpen);
	        htmlCode.append(tdOpen + getOnlineCount("gm") + " Player(s) Online</td>");
	        htmlCode.append(trClose);
	        htmlCode.append("</table>");

            int cell = 0;
	        if (Config.BBS_SHOW_PLAYERLIST)
	        {
    	        htmlCode.append("<table border=0>");
    	        htmlCode.append("<tr><td><table border=0>");

    	        for (L2PcInstance player : getOnlinePlayers(page))
    	        {
    	            cell++;
    
    	            if (cell == 1) htmlCode.append(trOpen);
    
    	            htmlCode.append("<td align=left valign=top FIXWIDTH=110><a action=\"bypass _bbsloc;playerinfo;" + player.getName() + "\">");
    
    	            if (player.isGM()) htmlCode.append("<font color=\"LEVEL\">" + player.getName()+ "</font>");
    	            else if(player.isAway() && Config.ALLOW_AWAY_STATUS)
    	            	htmlCode.append(player.getName() + "*Away*");
    	            else htmlCode.append(player.getName());
    
    	            htmlCode.append("</a></td>");
    
    	            if (cell < Config.NAME_PER_ROW_COMMUNITYBOARD) htmlCode.append(colSpacer);
    
    	            if (cell == Config.NAME_PER_ROW_COMMUNITYBOARD)
    	            {
    	                cell = 0;
    	                htmlCode.append(trClose);
    	            }
    	        }
    	        if (cell > 0 && cell < Config.NAME_PER_ROW_COMMUNITYBOARD) htmlCode.append(trClose);
    	        htmlCode.append("</table><br></td></tr>");
    
    	        htmlCode.append(trOpen);
    	        htmlCode.append("<td><img src=\"sek.cbui355\" width=600 height=1><br></td>");
    	        htmlCode.append(trClose);
    
    	        htmlCode.append("</table>");
			}

	        if (getOnlineCount("gm") > Config.NAME_PAGE_SIZE_COMMUNITYBOARD)
	        {
		        htmlCode.append("<table border=0 width=600>");

		        htmlCode.append("<tr>");
	            if (page == 1) htmlCode.append("<td align=right width=190><button value=\"Prev\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	            else htmlCode.append("<td align=right width=190><button value=\"Prev\" action=\"bypass _bbsloc;page;"
	                + (page - 1)
	                + "\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	            htmlCode.append("<td FIXWIDTH=10></td>");
	            htmlCode.append("<td align=center valign=top width=200>Displaying " + (((page - 1) * Config.NAME_PAGE_SIZE_COMMUNITYBOARD) + 1) + " - "
	                + (((page -1) * Config.NAME_PAGE_SIZE_COMMUNITYBOARD) + getOnlinePlayers(page).size()) + " player(s)</td>");
	            htmlCode.append("<td FIXWIDTH=10></td>");
	            if (getOnlineCount("gm") <= (page * Config.NAME_PAGE_SIZE_COMMUNITYBOARD)) htmlCode.append("<td width=190><button value=\"Next\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	            else htmlCode.append("<td width=190><button value=\"Next\" action=\"bypass _bbsloc;page;"
	                + (page + 1)
	                + "\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	            htmlCode.append("</tr>");
	            htmlCode.append("</table>");
	        }

	        htmlCode.append("</body></html>");

	        communityPage.put("gm", htmlCode.toString());

	        htmlCode = new StringBuilder("<html><body><br>");
	        htmlCode.append("<table>");

	        htmlCode.append(trOpen);
	        htmlCode.append("<td align=left valign=top>Server Restarted: " + GameServer.dateTimeServerStarted.getTime() + tdClose);
	        htmlCode.append(trClose);

	        htmlCode.append("</table>");

	        htmlCode.append("<table>");

	        htmlCode.append(trOpen);
	        htmlCode.append(tdOpen + "XP Rate: " + Config.RATE_XP + tdClose);
	        htmlCode.append(colSpacer);
	        htmlCode.append(tdOpen + "Party XP Rate: " + Config.RATE_PARTY_XP + tdClose);
	        htmlCode.append(colSpacer);
	        htmlCode.append(tdOpen + "XP Exponent: " + Config.ALT_GAME_EXPONENT_XP + tdClose);
	        htmlCode.append(trClose);

	        htmlCode.append(trOpen);
	        htmlCode.append(tdOpen + "SP Rate: " + Config.RATE_SP + tdClose);
	        htmlCode.append(colSpacer);
	        htmlCode.append(tdOpen + "Party SP Rate: " + Config.RATE_PARTY_SP + tdClose);
	        htmlCode.append(colSpacer);
	        htmlCode.append(tdOpen + "SP Exponent: " + Config.ALT_GAME_EXPONENT_SP + tdClose);
	        htmlCode.append(trClose);

	        htmlCode.append(trOpen);
	        htmlCode.append(tdOpen + "Drop Rate: " + Config.RATE_DROP_ITEMS + tdClose);
	        htmlCode.append(colSpacer);
	        htmlCode.append(tdOpen + "Spoil Rate: " + Config.RATE_DROP_SPOIL + tdClose);
	        htmlCode.append(colSpacer);
	        htmlCode.append(tdOpen + "Adena Rate: " + Config.RATE_DROP_ADENA + tdClose);
	        htmlCode.append(trClose);

	        htmlCode.append("</table>");

	        htmlCode.append("<table>");
	        htmlCode.append(trOpen);
	        htmlCode.append("<td><img src=\"sek.cbui355\" width=600 height=1><br></td>");
	        htmlCode.append(trClose);

	        htmlCode.append(trOpen);
	        htmlCode.append(tdOpen + getOnlineCount("pl") + " Player(s) Online</td>");
	        htmlCode.append(trClose);
	        htmlCode.append("</table>");

	        if (Config.BBS_SHOW_PLAYERLIST) 
	        {
    	        htmlCode.append("<table border=0>");
    	        htmlCode.append("<tr><td><table border=0>");
    
    	        cell = 0;
    	        for (L2PcInstance player : getOnlinePlayers(page))
    	        {
    	            if ((player == null) || (player.getAppearance().getInvisible()))
    	                continue;                           // Go to next
    
    	            cell++;
    
    	            if (cell == 1) htmlCode.append(trOpen);
    
    	            htmlCode.append("<td align=left valign=top FIXWIDTH=110><a action=\"bypass _bbsloc;playerinfo;"
    	                + player.getName() + "\">");
    
    	            if (player.isGM()) htmlCode.append("<font color=\"LEVEL\">" + player.getName()
    	                + "</font>");
    	            else htmlCode.append(player.getName());
    
    	            htmlCode.append("</a></td>");
    
    	            if (cell < Config.NAME_PER_ROW_COMMUNITYBOARD) htmlCode.append(colSpacer);
    
    	            if (cell == Config.NAME_PER_ROW_COMMUNITYBOARD)
    	            {
    	                cell = 0;
    	                htmlCode.append(trClose);
    	            }
    	        }
    	        if (cell > 0 && cell < Config.NAME_PER_ROW_COMMUNITYBOARD) htmlCode.append(trClose);
    	        htmlCode.append("</table><br></td></tr>");
    
    	        htmlCode.append(trOpen);
    	        htmlCode.append("<td><img src=\"sek.cbui355\" width=600 height=1><br></td>");
    	        htmlCode.append(trClose);
    
    	        htmlCode.append("</table>");
			}
	        
	        if (getOnlineCount("pl") > Config.NAME_PAGE_SIZE_COMMUNITYBOARD)
	        {
		        htmlCode.append("<table border=0 width=600>");

		        htmlCode.append("<tr>");
	            if (page == 1) htmlCode.append("<td align=right width=190><button value=\"Prev\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	            else htmlCode.append("<td align=right width=190><button value=\"Prev\" action=\"bypass _bbsloc;page;"
	                + (page - 1)
	                + "\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	            htmlCode.append("<td FIXWIDTH=10></td>");
	            htmlCode.append("<td align=center valign=top width=200>Displaying " + (((page - 1) * Config.NAME_PAGE_SIZE_COMMUNITYBOARD) + 1) + " - "
	                + (((page -1) * Config.NAME_PAGE_SIZE_COMMUNITYBOARD) + getOnlinePlayers(page).size()) + " player(s)</td>");
	            htmlCode.append("<td FIXWIDTH=10></td>");
	            if (getOnlineCount("pl") <= (page * Config.NAME_PAGE_SIZE_COMMUNITYBOARD)) htmlCode.append("<td width=190><button value=\"Next\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	            else htmlCode.append("<td width=190><button value=\"Next\" action=\"bypass _bbsloc;page;"
	                + (page + 1)
	                + "\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	            htmlCode.append("</tr>");
	            htmlCode.append("</table>");
	        }

	        htmlCode.append("</body></html>");

	        communityPage.put("pl", htmlCode.toString());

	        _communityPages.put(page, communityPage);
		}
	}

	private int getOnlineCount(String type)
	{
		if (type.equalsIgnoreCase("gm"))
			return _onlineCountGm;
		else
			return _onlineCount;
	}

	private List<L2PcInstance> getOnlinePlayers(int page)
	{
		return _onlinePlayers.get(page);
	}

	public String getCommunityPage(int page, String type)
	{
		if (_communityPages.get(page) != null)
			return _communityPages.get(page).get(type);
		else return null;
	}
}