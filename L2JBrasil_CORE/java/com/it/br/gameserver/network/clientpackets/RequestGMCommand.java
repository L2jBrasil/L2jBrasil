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
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.GMViewCharacterInfo;
import com.it.br.gameserver.network.serverpackets.GMViewItemList;
import com.it.br.gameserver.network.serverpackets.GMViewPledgeInfo;
import com.it.br.gameserver.network.serverpackets.GMViewQuestList;
import com.it.br.gameserver.network.serverpackets.GMViewSkillInfo;
import com.it.br.gameserver.network.serverpackets.GMViewWarehouseWithdrawList;

/**
 * This class ...
 *
 * @version $Revision: 1.1.2.2.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestGMCommand extends L2GameClientPacket
{
	private static final String _C__6E_REQUESTGMCOMMAND = "[C] 6e RequestGMCommand";

	private String _targetName;
	private int _command;


	@Override
	protected void readImpl()
	{
		_targetName = readS();
		_command    = readD();
		//_unknown  = readD();
	}


	@Override
	protected void runImpl()
	{
		// prevent non gm or low level GMs from vieweing player stuff
		if (!getClient().getActiveChar().isGM() || getClient().getActiveChar().getAccessLevel()<Config.GM_ALTG_MIN_LEVEL)
			return;

		L2PcInstance player = L2World.getInstance().getPlayer(_targetName);

		L2Clan clan = ClanTable.getInstance().getClanByName(_targetName);;
		
		// player name was incorrect?
		if (player == null && (clan == null || _command != 6)) 
		{
			return;
		}
		switch(_command)
		{
		case 1: // player status
			{
				sendPacket(new GMViewCharacterInfo(player));
				break;
			}
		case 2: // player clan
			{
				if (player.getClan() != null)
					sendPacket(new GMViewPledgeInfo(player.getClan(),player));
				break;
			}
		case 3: // player skills
			{
				sendPacket(new GMViewSkillInfo(player));
				break;
			}
		case 4: // player quests
			{
				sendPacket(new GMViewQuestList(player));
				break;
			}
		case 5: // player inventory
			{
				sendPacket(new GMViewItemList(player));
				break;
			}
		case 6: // player warehouse
			{
				// gm warehouse view to be implemented
				if (player != null) 
	                  sendPacket(new GMViewWarehouseWithdrawList(player)); 
                // clan warehouse 
                else 
                      sendPacket(new GMViewWarehouseWithdrawList(clan)); 
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__6E_REQUESTGMCOMMAND;
	}
}
