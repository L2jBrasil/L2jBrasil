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

/**
 * Format: (ch) Sd
 * @author  -Wooden-
 *
 */
public final class RequestPledgeSetMemberPowerGrade extends L2GameClientPacket
{
    private static final String _C__D0_1C_REQUESTPLEDGESETMEMBERPOWERGRADE = "[C] D0:1C RequestPledgeSetMemberPowerGrade";
    private int _powerGrade;
    private String _member;



	@Override
	protected void readImpl()
    {
        _member = readS();
        _powerGrade = readD();
    }

    /**
     * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#runImpl()
     */

	@Override
	protected void runImpl()
    {
        L2PcInstance activeChar = getClient().getActiveChar();
        if(activeChar == null)
        	return;
        L2Clan clan = activeChar.getClan();
        if(clan == null)
        	return;
        L2ClanMember member = clan.getClanMember(_member);
        if(member == null)
        	return;
        if(member.getPledgeType() == L2Clan.SUBUNIT_ACADEMY)
        {
        	// also checked from client side
        	activeChar.sendMessage("You cannot change academy member grade");
        	return;
        }
        member.setPowerGrade(_powerGrade);
        clan.broadcastClanStatus();
    }

    /**
     * @see com.it.br.gameserver.BasePacket#getType()
     */

    @Override
	public String getType()
    {
        return _C__D0_1C_REQUESTPLEDGESETMEMBERPOWERGRADE;
    }

}