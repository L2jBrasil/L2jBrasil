/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.it.br.gameserver.instancemanager;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.SetupGauge;
import com.it.br.gameserver.network.serverpackets.SocialAction;
import com.it.br.gameserver.ThreadPoolManager;

/**
 * @author *Slayer
 */
public final class AwayManager
{
	protected static final Logger _log = Logger.getLogger(AwayManager.class.getName());
	private static AwayManager _instance;
	public Map<L2PcInstance, RestoreData> _awayPlayers;

	public static final AwayManager getInstance()
	{
		if(_instance == null)
		{
			_instance = new AwayManager();
		}
		return _instance;
	}

	private final class RestoreData
	{
		private final String _originalTitle;
		private final int _originalTitleColor;
		private final boolean _sitForced;

		public RestoreData(L2PcInstance activeChar)
		{
			_originalTitle = activeChar.getTitle();
			_originalTitleColor = activeChar.getAppearance().getTitleColor();
			_sitForced = !activeChar.isSitting();
		}

		public boolean isSitForced()
		{
			return _sitForced;
		}

		public void restore(L2PcInstance activeChar)
		{
			activeChar.getAppearance().setTitleColor(_originalTitleColor);
			activeChar.setTitle(_originalTitle);
		}
	}

	private AwayManager()
	{
		_awayPlayers = Collections.synchronizedMap(new WeakHashMap<L2PcInstance, RestoreData>());
	}

	/**
	 * @param activeChar
	 * @param text
	 */
	public void setAway(L2PcInstance activeChar, String text)
	{
		activeChar.set_awaying(true);
		activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), 9));
		activeChar.sendMessage("Your status is Away in " + Config.AWAY_TIMER + " Sec.");
		activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		SetupGauge sg = new SetupGauge(SetupGauge.BLUE, Config.AWAY_TIMER * 1000);
		activeChar.sendPacket(sg);
		sg = null;
		ThreadPoolManager.getInstance().scheduleGeneral(new setPlayerAwayTask(activeChar, text), Config.AWAY_TIMER * 1000);
	}

	/**
	 * @param activeChar
	 */
	public void setBack(L2PcInstance activeChar)
	{
		activeChar.sendMessage("You are back from Away Status in " + Config.BACK_TIMER + " Sec.");
		SetupGauge sg = new SetupGauge(SetupGauge.BLUE, Config.BACK_TIMER * 1000);
		activeChar.sendPacket(sg);
		sg = null;
		ThreadPoolManager.getInstance().scheduleGeneral(new setPlayerBackTask(activeChar), Config.BACK_TIMER * 1000);
	}

	public void extraBack(L2PcInstance activeChar)
	{
		if(activeChar == null)
			return;
		RestoreData rd = _awayPlayers.get(activeChar);
		if(rd == null)
			return;

		rd.restore(activeChar);
		rd = null;
		_awayPlayers.remove(activeChar);
	}

	class setPlayerAwayTask implements Runnable
	{

		private final L2PcInstance _activeChar;
		private final String _awayText;

		setPlayerAwayTask(L2PcInstance activeChar, String awayText)
		{
			_activeChar = activeChar;
			_awayText = awayText;
		}

		@Override
		public void run()
		{
			if(_activeChar == null)
				return;
			if(_activeChar.isAttackingNow() || _activeChar.isCastingNow())
				return;

			_awayPlayers.put(_activeChar, new RestoreData(_activeChar));

			_activeChar.disableAllSkills();
			_activeChar.abortAttack();
			_activeChar.abortCast();
			_activeChar.setTarget(null);
			if(!_activeChar.isSitting())
			{
				_activeChar.sitDown();
			}
			if(_awayText.length() <= 1)
			{
				_activeChar.sendMessage("You are now *Away*");
			}
			else
			{
				_activeChar.sendMessage("You are now Away *" + _awayText + "*");
			}

			_activeChar.getAppearance().setTitleColor(Config.AWAY_TITLE_COLOR);

			if(_awayText.length() <= 1)
			{
				_activeChar.setTitle("*Away*");
			}
			else
			{
				_activeChar.setTitle("Away*" + _awayText + "*");
			}

			_activeChar.broadcastUserInfo();
			_activeChar.setIsParalyzed(true);
			_activeChar.setIsAway(true);
			_activeChar.set_awaying(false);
		}
	}

	class setPlayerBackTask implements Runnable
	{

		private final L2PcInstance _activeChar;

		setPlayerBackTask(L2PcInstance activeChar)
		{
			_activeChar = activeChar;
		}

		@Override
		public void run()
		{
			if(_activeChar == null)
				return;
			RestoreData rd = _awayPlayers.get(_activeChar);

			if(rd == null)
				return;

			_activeChar.setIsParalyzed(false);
			_activeChar.enableAllSkills();
			_activeChar.setIsAway(false);

			if(rd.isSitForced())
			{
				_activeChar.standUp();
			}

			rd.restore(_activeChar);
			rd = null;
			_awayPlayers.remove(_activeChar);
			_activeChar.broadcastUserInfo();
			_activeChar.sendMessage("You are Back now!");
		}
	}
}