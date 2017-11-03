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

import java.util.logging.Logger;

import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.ManagePledgePower;

public final class RequestPledgePower extends L2GameClientPacket
{
    static Logger _log = Logger.getLogger(ManagePledgePower.class.getName());
    private static final String _C__C0_REQUESTPLEDGEPOWER = "[C] C0 RequestPledgePower";
    private int _rank;
    private int _action;
    private int _privs;


	@Override
	protected void readImpl()
    {
        _rank = readD();
        _action = readD();
        if (_action == 2)
        {
            _privs = readD();
        }
        else _privs = 0;
    }


	@Override
	protected void runImpl()
    {
        L2PcInstance player = getClient().getActiveChar();
        if (player == null) return;

        if(_action == 2)
        {
        	if(player.getClan() != null && player.isClanLeader())
        	{
        	    if(_rank == 9)
        	    {
       	            //The rights below cannot be bestowed upon Academy members:
        	        //Join a clan or be dismissed
        	        //Title management, crest management, master management, level management,
        	        //bulletin board administration
        	        //Clan war, right to dismiss, set functions
        	        //Auction, manage taxes, attack/defend registration, mercenary management
        	        //=> Leaves only CP_CL_VIEW_WAREHOUSE, CP_CH_OPEN_DOOR, CP_CS_OPEN_DOOR?
        	        _privs = (_privs & L2Clan.CP_CL_VIEW_WAREHOUSE) + (_privs & L2Clan.CP_CH_OPEN_DOOR)
        	                 + (_privs & L2Clan.CP_CS_OPEN_DOOR);
        	    }
        		player.getClan().setRankPrivs(_rank, _privs);
        	}
        } else
        {
            ManagePledgePower mpp = new ManagePledgePower(getClient().getActiveChar().getClan(), _action, _rank);
            player.sendPacket(mpp);
        }
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
     */

	@Override
	public String getType()
    {
        return _C__C0_REQUESTPLEDGEPOWER;
    }
}