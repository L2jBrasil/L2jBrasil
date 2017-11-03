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
import com.it.br.gameserver.instancemanager.QuestManager;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.network.serverpackets.QuestList;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestQuestAbort extends L2GameClientPacket
{
	private static final String _C__64_REQUESTQUESTABORT = "[C] 64 RequestQuestAbort";
	private static Logger _log = Logger.getLogger(RequestQuestAbort.class.getName());

	private int _questId;



	@Override
	protected void readImpl()
	{
		_questId = readD();
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		    return;

        Quest qe = QuestManager.getInstance().getQuest(_questId);
        if (qe != null)
        {
    		QuestState qs = activeChar.getQuestState(qe.getName());
            if(qs != null)
            {
        		qs.exitQuest(true);
        		activeChar.sendPacket(new QuestList());
        		
            } else
            {
                if (Config.DEBUG) _log.info("Player '"+activeChar.getName()+"' try to abort quest "+qe.getName()+" but he didn't have it started.");
            }
        } else
        {
            if (Config.DEBUG) _log.warning("Quest (id='"+_questId+"') not found.");
        }
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__64_REQUESTQUESTABORT;
	}
}