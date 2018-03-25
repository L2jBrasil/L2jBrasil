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
import com.it.br.gameserver.handler.AdminCommandHandler;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.util.Util;

public final class SendBypassBuildCmd extends L2GameClientPacket
{
	private static final String _C__5B_SENDBYPASSBUILDCMD = "[C] 5b SendBypassBuildCmd";
	public final static int GM_MESSAGE = 9;
	public final static int ANNOUNCEMENT = 10;

	private String _command;

	@Override
	protected void readImpl()
	{
		_command = readS();
		if (_command != null)
			_command = _command.trim();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
        if(activeChar == null)
            return;

        if (Config.ALT_PRIVILEGES_ADMIN && !AdminCommandHandler.getInstance().checkPrivileges(activeChar,"admin_"+_command))
            return;

        if(!activeChar.isGM() && !"gm".equalsIgnoreCase(_command))
        {
        	Util.handleIllegalPlayerAction(activeChar,"Warning!! Non-gm character "+activeChar.getName()+" requests gm bypass handler, hack?", Config.DEFAULT_PUNISH);
        	return;
        }

		IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler("admin_"+_command);

		if (ach != null)
		{
			ach.useAdminCommand("admin_"+_command, activeChar);
		} 
		else
		{
			activeChar.sendMessage("The command " + _command + " doesn't exists!");
			_log.warn( "No handler registered for admin command '" + _command + "'");
			return;
		}
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__5B_SENDBYPASSBUILDCMD;
	}
}
