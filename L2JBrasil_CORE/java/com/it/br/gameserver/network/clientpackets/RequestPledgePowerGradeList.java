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
import com.it.br.gameserver.model.L2Clan.RankPrivs;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.PledgePowerGradeList;

/**
 * Format: (ch)
 * @author  -Wooden-
 *
 */
public final class RequestPledgePowerGradeList extends L2GameClientPacket
{
    private static final String _C__D0_1A_REQUESTPLEDGEPOWERGRADELIST = "[C] D0:1A RequestPledgePowerGradeList";



	@Override
	protected void readImpl()
    {
    	// trigger
    }

    /**
     * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#runImpl()
     */

	@Override
	protected void runImpl()
    {
    	L2PcInstance player = getClient().getActiveChar();
        L2Clan clan = player.getClan();
        if (clan != null)
        {
            RankPrivs[] privs = clan.getAllRankPrivs();
            player.sendPacket(new PledgePowerGradeList(privs));
            //_log.warn("plegdepowergradelist send, privs length: "+privs.length);
        }
    }

    /**
     * @see com.it.br.gameserver.BasePacket#getType()
     */

    @Override
	public String getType()
    {
        return _C__D0_1A_REQUESTPLEDGEPOWERGRADELIST;
    }

}