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

import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.AskJoinAlly;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestJoinAlly extends L2GameClientPacket
{

	private static final String _C__82_REQUESTJOINALLY = "[C] 82 RequestJoinAlly";
	//private static Logger _log = LoggerFactory.getLogger(RequestJoinAlly.class);

	private int _id;


	@Override
	protected void readImpl()
	{
		_id = readD();
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
		    return;
		}
		if (!(L2World.getInstance().findObject(_id) instanceof L2PcInstance))
		{
        	activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET));
		    return;
		}
		if(activeChar.getClan() == null)
        {
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER));
            return;
        }
		L2PcInstance target = (L2PcInstance) L2World.getInstance().findObject(_id);
        L2Clan clan = activeChar.getClan();
        if (!clan.checkAllyJoinCondition(activeChar, target))
        {
        	return;
        }
        if (!activeChar.getRequest().setRequest(target, this))
        {
        	return;
        }

		SystemMessage sm = new SystemMessage(SystemMessageId.S2_ALLIANCE_LEADER_OF_S1_REQUESTED_ALLIANCE);
		sm.addString(activeChar.getClan().getAllyName());
		sm.addString(activeChar.getName());
		target.sendPacket(sm);
		sm = null;
		AskJoinAlly aja = new AskJoinAlly(activeChar.getObjectId(), activeChar.getClan().getAllyName());
		target.sendPacket(aja);
	    return;
	}


	@Override
	public String getType()
	{
		return _C__82_REQUESTJOINALLY;
	}
}
