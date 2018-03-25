/* This program is free software; you can redistribute it and/or modify
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

import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestSurrenderPersonally extends L2GameClientPacket
{
    private static final String _C__69_REQUESTSURRENDERPERSONALLY = "[C] 69 RequestSurrenderPersonally";
    private static Logger _log = LoggerFactory.getLogger(RequestSurrenderPledgeWar.class);

    private String _pledgeName;
    private L2Clan _clan;
    private L2PcInstance _activeChar;


	@Override
	protected void readImpl()
    {
        _pledgeName  = readS();
    }


	@Override
	protected void runImpl()
    {
    	_activeChar = getClient().getActiveChar();
    	if (_activeChar == null)
    		return;
        _log.info("RequestSurrenderPersonally by "+getClient().getActiveChar().getName()+" with "+_pledgeName);
        _clan = getClient().getActiveChar().getClan();
        L2Clan clan = ClanTable.getInstance().getClanByName(_pledgeName);

        if(_clan == null)
            return;

        if(clan == null)
        {
        	_activeChar.sendMessage("No such clan.");
        	_activeChar.sendPacket(new ActionFailed());
            return;
        }

        if(!_clan.isAtWarWith(clan.getClanId()) || _activeChar.getWantsPeace() == 1)
        {
        	_activeChar.sendMessage("You aren't at war with this clan.");
        	_activeChar.sendPacket(new ActionFailed());
            return;
        }

        _activeChar.setWantsPeace(1);
        _activeChar.deathPenalty(false);
        SystemMessage msg = new SystemMessage(SystemMessageId.YOU_HAVE_PERSONALLY_SURRENDERED_TO_THE_S1_CLAN);
        msg.addString(_pledgeName);
        _activeChar.sendPacket(msg);
        msg = null;
        ClanTable.getInstance().checkSurrender(_clan, clan);
    }


	@Override
	public String getType()
    {
        return _C__69_REQUESTSURRENDERPERSONALLY;
    }
}