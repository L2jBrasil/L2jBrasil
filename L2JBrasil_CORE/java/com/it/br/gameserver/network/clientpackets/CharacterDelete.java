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
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.CharDeleteFail;
import com.it.br.gameserver.network.serverpackets.CharDeleteOk;
import com.it.br.gameserver.network.serverpackets.CharSelectInfo;

/**
 * This class ...
 *
 * @version $Revision: 1.8.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class CharacterDelete extends L2GameClientPacket
{
	private static final String _C__0C_CHARACTERDELETE = "[C] 0C CharacterDelete";
	//private static Logger _log = LoggerFactory.getLogger(CharacterDelete.class);

	// cd
	private int _charSlot;


	@Override
	protected void readImpl()
	{
		_charSlot = readD();
	}


	@Override
	protected void runImpl()
	{
		if (Config.DEBUG) _log.debug("deleting slot:" + _charSlot);

		L2PcInstance character = null;
		try
		{
		    if (Config.DELETE_DAYS == 0)
		    	character = getClient().deleteChar(_charSlot);
		    else
		    	character = getClient().markToDeleteChar(_charSlot);
		}
		catch (Exception e)
		{
			_log.error( "Error:", e);
		}

		if (character == null)
		{
			sendPacket(new CharDeleteOk());
		}
		else
		{
			if (character.isClanLeader())
			{
				sendPacket(new CharDeleteFail(CharDeleteFail.REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED));
			}
			else
			{
				sendPacket(new CharDeleteFail(CharDeleteFail.REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER));
			}
                        if (character.isChatBanned())
			{
				sendPacket(new CharDeleteFail(CharDeleteFail.REASON_DELETION_FAILED));
			}

		}

		CharSelectInfo cl = new CharSelectInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1, 0);
		sendPacket(cl);
		getClient().setCharSelection(cl.getCharInfo());
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__0C_CHARACTERDELETE;
	}
}
