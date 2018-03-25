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
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SocialAction;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class ...
 *
 * @version $Revision: 1.6.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestSocialAction extends L2GameClientPacket
{
	private static final String _C__1B_REQUESTSOCIALACTION = "[C] 1B RequestSocialAction";
	private static Logger _log = LoggerFactory.getLogger(RequestSocialAction.class);

	// format  cd
	private int _actionId;



	@Override
	protected void readImpl()
	{
		_actionId  = readD();
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		    return;

        // You cannot do anything else while fishing
        if (activeChar.isFishing())
        {
            SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
            activeChar.sendPacket(sm);
            sm = null;
            return;
        }

        // check if its the actionId is allowed
        if (_actionId < 2 || _actionId > 13)
        {
        	Util.handleIllegalPlayerAction(activeChar, "Warning!! Character "+activeChar.getName()+" of account "+activeChar.getAccountName()+" requested an internal Social Action.", Config.DEFAULT_PUNISH);
        	return;
        }

		if (	activeChar.getPrivateStoreType()==0 &&
				activeChar.getActiveRequester()==null &&
				!activeChar.isAlikeDead() &&
				(!activeChar.isAllSkillsDisabled() || activeChar.isInDuel()) &&
				activeChar.getAI().getIntention()==CtrlIntention.AI_INTENTION_IDLE)
		{
			if (Config.DEBUG) _log.debug("Social Action:" + _actionId);

			SocialAction atk = new SocialAction(activeChar.getObjectId(), _actionId);
			activeChar.broadcastPacket(atk);
			/*
			// Schedule a social task to wait for the animation to finish
			ThreadPoolManager.getInstance().scheduleGeneral(new SocialTask(this), 2600);
			activeChar.setIsParalyzed(true);
			*/
		}
	}
	/*
	class SocialTask implements Runnable
	{
		L2PcInstance _player;
		SocialTask(RequestSocialAction action)
		{
			_player = getClient().getActiveChar();
		}
		public void run()
		{
			_player.setIsParalyzed(false);
		}
	}
	*/

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__1B_REQUESTSOCIALACTION;
	}
}
