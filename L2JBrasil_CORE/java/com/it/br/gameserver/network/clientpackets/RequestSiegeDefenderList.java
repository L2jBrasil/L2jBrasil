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

import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.network.serverpackets.SiegeDefenderList;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestSiegeDefenderList extends L2GameClientPacket
{
    private static final String _C__a3_RequestSiegeDefenderList = "[C] a3 RequestSiegeDefenderList";
    //private static Logger _log = LoggerFactory.getLogger(RequestJoinParty.class);

    private int _castleId;


    @Override
	protected void readImpl()
    {
        _castleId = readD();
    }


    @Override
	protected void runImpl()
    {
        Castle castle = CastleManager.getInstance().getCastleById(_castleId);
        if (castle == null) return;
        SiegeDefenderList sdl = new SiegeDefenderList(castle);
        sendPacket(sdl);
    }



    @Override
	public String getType()
    {
        return _C__a3_RequestSiegeDefenderList;
    }
}
