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
import com.it.br.gameserver.model.L2ClanMember;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 *
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestGiveNickName extends L2GameClientPacket
{
	private static final String _C__55_REQUESTGIVENICKNAME = "[C] 55 RequestGiveNickName";
	private String _target;
	private String _title;


	@Override
	protected void readImpl()
	{
		_target = readS();
		_title  = readS();
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		    return;

		// Noblesse can bestow a title to themselves
		if (activeChar.isNoble() && _target.matches(activeChar.getName()))
		{
			activeChar.setTitle(_title);
			SystemMessage sm = new SystemMessage(SystemMessageId.TITLE_CHANGED);
			activeChar.sendPacket(sm);
			activeChar.broadcastTitleInfo();
		}
		//Can the player change/give a title?
		else if ((activeChar.getClanPrivileges() & L2Clan.CP_CL_GIVE_TITLE) == L2Clan.CP_CL_GIVE_TITLE)
		{
			if (activeChar.getClan().getLevel() < 3)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.CLAN_LVL_3_NEEDED_TO_ENDOWE_TITLE);
                activeChar.sendPacket(sm);
                sm = null;
				return;
			}

			L2ClanMember member1 = activeChar.getClan().getClanMember(_target);
            if (member1 != null)
            {
                L2PcInstance member = member1.getPlayerInstance();
                if (member != null)
                {
        			//is target from the same clan?
    				member.setTitle(_title);
    				SystemMessage sm = new SystemMessage(SystemMessageId.TITLE_CHANGED);
    				member.sendPacket(sm);
					member.broadcastTitleInfo();
					sm = null;
                }
                else
                {
                    SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
                    sm.addString("Target needs to be online to get a title");
                    activeChar.sendPacket(sm);
                    sm = null;
                }
			}
            else
            {
                SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);
                sm.addString("Target does not belong to your clan");
                activeChar.sendPacket(sm);
                sm = null;
            }
		}
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__55_REQUESTGIVENICKNAME;
	}
}
