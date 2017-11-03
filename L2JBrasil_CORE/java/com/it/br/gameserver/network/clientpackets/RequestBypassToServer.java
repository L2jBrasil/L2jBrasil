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

import java.util.logging.Level;

import com.it.br.Config;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.communitybbs.CommunityBoard;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.handler.AdminCommandHandler;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2CharPosition;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.L2Event;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.GMViewPledgeInfo;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.network.serverpackets.WareHouseDepositList;
import com.it.br.gameserver.network.serverpackets.WareHouseWithdrawalList;

/**
 * This class ...
 *
 * @version $Revision: 1.12.4.5 $ $Date: 2005/04/11 10:06:11 $
 */
public final class RequestBypassToServer extends L2GameClientPacket
{
	private static final String _C__21_REQUESTBYPASSTOSERVER = "[C] 21 RequestBypassToServer";
	//private static Logger _log = Logger.getLogger(RequestBypassToServer.class.getName());

	// S
	private String _command;

	/**
	 * @param decrypt
	 */

	@Override
	protected void readImpl()
	{
		_command = readS();
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();

		if (activeChar == null)
		    return;
		
		if (!getClient().getFloodProtectors().getServerBypass().tryPerformAction(_command))
        {
			activeChar.sendMessage("You can not Request Bypass so fast!");
			return;
        }

		try
		{
			if (_command.startsWith("admin_")) //&& activeChar.getAccessLevel() >= Config.GM_ACCESSLEVEL)
			{
                if (Config.ALT_PRIVILEGES_ADMIN && !AdminCommandHandler.getInstance().checkPrivileges(activeChar, _command))
                {
                    _log.info("<GM>" + activeChar + " does not have sufficient privileges for command '" + _command + "'.");
                    return;
                }

				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(_command);

				if (ach != null)
					ach.useAdminCommand(_command, activeChar);
				else
					_log.warning("No handler registered for bypass '"+_command+"'");
			}
			else if (_command.equals("come_here") && activeChar.getAccessLevel() >= Config.GM_ACCESSLEVEL)
			{
				comeHere(activeChar);
	            } 
         else if (_command.startsWith("show_clan_info ")) 
         { 
             activeChar.sendPacket(new GMViewPledgeInfo(ClanTable.getInstance().getClanByName(_command.substring(15)),activeChar));
			}
			else if (_command.startsWith("wh_deposit"))
			{
				if (Config.DEBUG)
				_log.fine("Showing items to deposit");

				activeChar.sendPacket(new WareHouseDepositList(activeChar, WareHouseDepositList.PRIVATE));
				}

			else if (_command.startsWith("wh_withdraw"))
			{
				if (activeChar.getActiveWarehouse().getSize() == 0)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH));
					return;
					}
				
					if (Config.DEBUG)
					_log.fine("Showing stored items");
				
					activeChar.sendPacket(new WareHouseWithdrawalList(activeChar, WareHouseWithdrawalList.PRIVATE));
					}

		else if (_command.startsWith("player_help "))
		{
			playerHelp(activeChar, _command.substring(12));
		}
		else if (_command.startsWith("npc_"))
		{
			if(!activeChar.validateBypass(_command))
				return;

				int endOfId = _command.indexOf('_', 5);
				String id;
				if (endOfId > 0)
					id = _command.substring(4, endOfId);
				else
					id = _command.substring(4);
				try
				{
					L2Object object = L2World.getInstance().findObject(Integer.parseInt(id));
					if (_command.substring(endOfId+1).startsWith("event_participate")) L2Event.inscribePlayer(activeChar);
					else if (object != null && object instanceof L2NpcInstance && endOfId > 0 && activeChar.isInsideRadius(object, L2NpcInstance.INTERACTION_DISTANCE, false, false))
					{
						((L2NpcInstance)object).onBypassFeedback(activeChar, _command.substring(endOfId+1));
					}
					activeChar.sendPacket(new ActionFailed());
				}
				catch (NumberFormatException nfe) {}
			}
			//	Draw a Symbol
			else if (_command.equals("menu_select?ask=-16&reply=1"))
			{
                L2Object object = activeChar.getTarget();
                if (object instanceof L2NpcInstance)
                {
                    ((L2NpcInstance) object).onBypassFeedback(activeChar, _command);
                }
			}
			else if (_command.equals("menu_select?ask=-16&reply=2"))
			{
                L2Object object = activeChar.getTarget();
                if (object instanceof L2NpcInstance)
                {
                    ((L2NpcInstance) object).onBypassFeedback(activeChar, _command);
                }
			}
			// Navigate throught Manor windows
            else if (_command.startsWith("manor_menu_select?"))
            {
            	L2Object object = activeChar.getTarget();
                if (object instanceof L2NpcInstance)
                {
                    ((L2NpcInstance) object).onBypassFeedback(activeChar, _command);
                }
            }
			else if (_command.startsWith("bbs_"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
			}
			else if (_command.startsWith("_bbs"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
			}
			else if (_command.startsWith("Quest "))
			{
				if(!activeChar.validateBypass(_command))
					return;

				L2PcInstance player = getClient().getActiveChar();
				if (player == null) 
					return;

				String p = _command.substring(6).trim();
				int idx = p.indexOf(' ');
				if (idx < 0)
					player.processQuestEvent(p, "");
				else
					player.processQuestEvent(p.substring(0, idx), p.substring(idx).trim());
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Bad RequestBypassToServer: ", e);
		}
//		finally
//		{
//			activeChar.clearBypass();
//		}
	}

	/**
	 * @param client
	 */
	private void comeHere(L2PcInstance activeChar)
	{
		L2Object obj = activeChar.getTarget();
		if (obj == null) 
			return;
		if (obj instanceof L2NpcInstance)
		{
			L2NpcInstance temp = (L2NpcInstance) obj;
			temp.setTarget(activeChar);
			temp.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO,
					new L2CharPosition(activeChar.getX(),activeChar.getY(), activeChar.getZ(), 0 ));
  			//temp.moveTo(player.getX(),player.getY(), player.getZ(), 0 );
		}

	}

	private void playerHelp(L2PcInstance activeChar, String path)
	{
        if (path.indexOf("..") != -1)
            return;

		String filename = "data/html/help/"+path;
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile(filename);
		activeChar.sendPacket(html);
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__21_REQUESTBYPASSTOSERVER;
	}
}
