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

import com.it.br.gameserver.model.L2Multisell;
import com.it.br.gameserver.templates.L2NpcTemplate;

/**
 * @author zabbix
 * Lets drink to code!
 */
public class L2BlacksmithInstance extends L2FolkInstance
{
	public L2BlacksmithInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}


	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (command.startsWith("multisell"))
		{
			int listId = Integer.parseInt(command.substring(9).trim());
			L2Multisell.getInstance().SeparateAndSend(listId, player, false, getCastle().getTaxRate());
		}
		super.onBypassFeedback(player,command);
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

		return "data/html/blacksmith/" + pom + ".htm";
	}
}
