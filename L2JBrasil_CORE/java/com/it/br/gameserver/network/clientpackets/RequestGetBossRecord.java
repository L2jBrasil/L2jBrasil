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

import com.it.br.gameserver.instancemanager.RaidBossPointsManager;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.ExGetBossRecord;

import java.util.Map;

public class RequestGetBossRecord extends L2GameClientPacket
{
	//protected static final Logger _log = LoggerFactory.getLogger(RequestGetBossRecord.class);
    private static final String _C__D0_18_REQUESTGETBOSSRECORD = "[C] D0:18 RequestGetBossRecord";
    private int _bossId;


    @Override
	protected void readImpl()
    {
        _bossId = readD();
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
		
		if (_bossId != 0)
		{
			_log.info("C5: RequestGetBossRecord: d: "+_bossId+" ActiveChar: "+activeChar); // should be always 0, log it if isnt 0 for furture research
		}
		
		RaidBossPointsManager.getInstance();
		int points = RaidBossPointsManager.getPointsByOwnerId(activeChar.getObjectId());
		RaidBossPointsManager.getInstance();
		int ranking = RaidBossPointsManager.calculateRanking(activeChar.getObjectId());
		
		RaidBossPointsManager.getInstance();
		Map<Integer, Integer> list = RaidBossPointsManager.getList(activeChar);
		
		// trigger packet
		activeChar.sendPacket(new ExGetBossRecord(ranking, points, list));
    }

    /**
     * @see com.it.br.gameserver.BasePacket#getType()
     */

    @Override
	public String getType()
    {
        return _C__D0_18_REQUESTGETBOSSRECORD;
    }
}