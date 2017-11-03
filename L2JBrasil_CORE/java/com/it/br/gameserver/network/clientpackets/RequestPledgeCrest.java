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

import com.it.br.Config;
import com.it.br.gameserver.cache.CrestCache;
import com.it.br.gameserver.cache.CrestCache.CrestType;
import com.it.br.gameserver.network.serverpackets.PledgeCrest;

/**
 * This class ...
 *
 * @version $Revision: 1.4.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestPledgeCrest extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(RequestPledgeCrest.class.getName());
	private static final String _C__68_REQUESTPLEDGECREST = "[C] 68 RequestPledgeCrest";

	private int _crestId;


	@Override
	protected void readImpl()
	{
		_crestId = readD();
	}


	@Override
	protected void runImpl()
	{
		if (_crestId == 0)
		    return;
		if (Config.DEBUG) _log.fine("crestid " + _crestId + " requested");

        byte[] data = CrestCache.getCrest(CrestType.PLEDGE, _crestId);

		if (data != null)
		{
			PledgeCrest pc = new PledgeCrest(_crestId, data);
			sendPacket(pc);
		}
		else
		{
			if (Config.DEBUG) _log.fine("crest is missing:" + _crestId);
		}
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__68_REQUESTPLEDGECREST;
	}
}
