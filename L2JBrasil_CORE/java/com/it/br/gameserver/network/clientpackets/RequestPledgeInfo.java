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
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.PledgeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class ...
 *
 * @version $Revision: 1.5.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestPledgeInfo extends L2GameClientPacket
{
	private static final String _C__66_REQUESTPLEDGEINFO = "[C] 66 RequestPledgeInfo";

	private static Logger _log = LoggerFactory.getLogger(RequestPledgeInfo.class);

	private int _clanId;


	@Override
	protected void readImpl()
	{
		_clanId = readD();
	}


	@Override
	protected void runImpl()
	{
		if (Config.DEBUG)
			_log.debug("infos for clan " + _clanId + " requested");

		L2PcInstance activeChar = getClient().getActiveChar();
		L2Clan clan = ClanTable.getInstance().getClan(_clanId);
		if (clan == null)
		{
			_log.warn("Clan data for clanId " + _clanId + " is missing");
			return; // we have no clan data ?!? should not happen
		}

		PledgeInfo pc = new PledgeInfo(clan);
		if (activeChar != null)
		{
			activeChar.sendPacket(pc);

			/*
			 * if (clan.getClanId() == activeChar.getClanId()) {
			 * activeChar.sendPacket(new PledgeShowMemberListDeleteAll());
			 * PledgeShowMemberListAll pm = new PledgeShowMemberListAll(clan,
			 * activeChar); activeChar.sendPacket(pm); }
			 */
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__66_REQUESTPLEDGEINFO;
	}
}
