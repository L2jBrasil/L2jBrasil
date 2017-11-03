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

import java.util.StringTokenizer;

import com.it.br.gameserver.cache.HtmCache;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.ShowBoard;

public class TopBBSManager extends BaseBBSManager
{

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.communitybbs.Manager.BaseBBSManager#parsecmd(java.lang.String, com.it.br.gameserver.model.actor.instance.L2PcInstance)
	 */

	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		if(command.equals("_bbstop"))
		{
			String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/index.htm");
			if (content == null)
			{
				content = "<html><body><br><br><center>404 :File Not foud: 'data/html/CommunityBoard/index.htm' </center></body></html>";
			}
			separateAndSend(content,activeChar);
		}
		else if(command.equals("_bbshome"))
		{
			String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/index.htm");
			if (content == null)
			{
				content = "<html><body><br><br><center>404 :File Not foud: 'data/html/CommunityBoard/index.htm' </center></body></html>";
			}
			separateAndSend(content,activeChar);
		}
		else if(command.startsWith("_bbstop;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			int idp = Integer.parseInt(st.nextToken());
			String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/"+idp+".htm");
			if (content == null)
			{
				content = "<html><body><br><br><center>404 :File Not foud: 'data/html/CommunityBoard/"+idp+".htm' </center></body></html>";
			}
			separateAndSend(content,activeChar);
		}
		else
		{
		ShowBoard sb = new ShowBoard("<html><body><br><br><center>the command: "+command+" is not implemented yet</center><br><br></body></html>","101");
		activeChar.sendPacket(sb);
		activeChar.sendPacket(new ShowBoard(null,"102"));
		activeChar.sendPacket(new ShowBoard(null,"103"));
		}
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.communitybbs.Manager.BaseBBSManager#parsewrite(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.it.br.gameserver.model.actor.instance.L2PcInstance)
	 */

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		// TODO Auto-generated method stub

	}

	private static TopBBSManager _instance = new TopBBSManager();

	/**
	 * @return
	 */
	public static TopBBSManager getInstance()
	{
		return _instance;
	}

}