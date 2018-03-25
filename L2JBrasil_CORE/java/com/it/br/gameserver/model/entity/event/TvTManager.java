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
package com.it.br.gameserver.model.entity.event;

import com.it.br.configuration.settings.EventSettings;
import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;

import static com.it.br.configuration.Configurator.getSettings;

public class TvTManager
{
	protected static final Logger _log = LoggerFactory.getLogger(TvTManager.class);

	/** Task for event cycles<br> */
	private TvTStartTask _task;

	/**
	 * New instance only by getInstance()<br>
	 */
	private TvTManager()
	{
		if (getSettings(EventSettings.class).isTvTEventEnabled())
		{
			TvTEvent.init();
			this.scheduleEventStart();
			System.out.println("TvT Event Engine : Select STARTED");
		}
		else
		{
			System.out.println("TvT Event Engine : Select DISABLE");
		}
	}

	/**
	 * Initialize new/Returns the one and only instance<br><br>
	 * @return TvTManager<br>
	 */
	public static TvTManager getInstance()
	{
		return SingletonHolder._instance;
	}

	/**
	 * Starts TvTStartTask
	 */
	public void scheduleEventStart()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : getSettings(EventSettings.class).getTvTEventInterval())
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				// If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				// Check for the test date to be the minimum (smallest in the specified list)
				if (nextStartTime == null || testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis())
				{
					nextStartTime = testStartTime;
				}
			}
			_task = new TvTStartTask(nextStartTime.getTimeInMillis());
			ThreadPoolManager.getInstance().executeTask(_task);
		}
		catch (Exception e)
		{
			_log.warn("TvTEventEngine[TvTManager.scheduleEventStart()]: Error figuring out a start time. Check TvTEventInterval in config file.");
		}
	}

	/**
	 * Method to start participation
	 */
	public void startReg()
	{
		if (!TvTEvent.startParticipation())
		{
			Announcements.getInstance().gameAnnounceToAll("TvT Event: Event was cancelled.");
			_log.warn("TvTEventEngine[TvTManager.run()]: Error spawning event npc for participation.");

			this.scheduleEventStart();
		}
		else
		{
			int participationTime = getSettings(EventSettings.class).getTvTEventParticipationTime();
			Announcements.getInstance().gameAnnounceToAll("TvT Event: Registration opened for " + participationTime + " minute(s).");

			// schedule registration end
			_task.setStartTime(System.currentTimeMillis() + Util.minutesToMiliseconds(participationTime));
			ThreadPoolManager.getInstance().executeTask(_task);
		}
	}

	/**
	 * Method to start the fight
	 */
	public void startEvent()
	{
		if (!TvTEvent.startFight())
		{
			Announcements.getInstance().gameAnnounceToAll("TvT Event: Event cancelled due to lack of Participation.");
			System.out.println("TvTEventEngine[TvTManager.run()]: Lack of registration, abort event.");

			this.scheduleEventStart();
		}
		else
		{
			EventSettings eventSettings = getSettings(EventSettings.class);
			TvTEvent.sysMsgToAllParticipants("TvT Event: Teleporting participants to an arena in "	+ eventSettings.getTvTEventStartLeaveTeleportDelay() + " second(s).");
			_task.setStartTime(System.currentTimeMillis() + 60000L * eventSettings.getTvTEventRunningTime());
			ThreadPoolManager.getInstance().executeTask(_task);
		}
	}

	/**
	 * Method to end the event and reward
	 */
	public void endEvent()
	{
		Announcements.getInstance().gameAnnounceToAll(TvTEvent.calculateRewards());
		TvTEvent.sysMsgToAllParticipants("TvT Event: Teleporting back to the registration npc in "	+ getSettings(EventSettings.class).getTvTEventStartLeaveTeleportDelay() + " second(s).");
		TvTEvent.stopFight();

		this.scheduleEventStart();
	}

	public void skipDelay()
	{
		if (_task.nextRun.cancel(false))
		{
			_task.setStartTime(System.currentTimeMillis());
			ThreadPoolManager.getInstance().executeTask(_task);
		}
	}

	/**
	 * Class for TvT cycles
	 */
	class TvTStartTask implements Runnable
	{
		private long _startTime;
		public ScheduledFuture<?> nextRun;

		public TvTStartTask(long startTime)
		{
			_startTime = startTime;
		}

		public void setStartTime(long startTime)
		{
			_startTime = startTime;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			int delay = (int) Math.round((_startTime - System.currentTimeMillis()) / 1000.0);

			if (delay > 0)
			{
				this.announce(delay);
			}

			int nextMsg = 0;
			if (delay > 3600)
			{
				nextMsg = delay - 3600;
			}
			else if (delay > 1800)
			{
				nextMsg = delay - 1800;
			}
			else if (delay > 900)
			{
				nextMsg = delay - 900;
			}
			else if (delay > 600)
			{
				nextMsg = delay - 600;
			}
			else if (delay > 300)
			{
				nextMsg = delay - 300;
			}
			else if (delay > 60)
			{
				nextMsg = delay - 60;
			}
			else if (delay > 5)
			{
				nextMsg = delay - 5;
			}
			else if (delay > 4)
			{
				nextMsg = delay - 4;
			}
			else if (delay > 3)
			{
				nextMsg = delay - 3;
			}
			else if (delay > 2)
			{
				nextMsg = delay - 2;
			}
			else if (delay > 0)
			{
				nextMsg = delay;
			}
			else
			{
				// start
				if (TvTEvent.isInactive())
				{
					TvTManager.this.startReg();
				}
				else if (TvTEvent.isParticipating())
				{
					TvTManager.this.startEvent();
				}
				else
				{
					TvTManager.this.endEvent();
				}
			}
			if (delay > 0)
			{
				nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
			}
		}

		private void announce(long time)
		{
			if (time >= 3600 && time % 3600 == 0)
			{
				if (TvTEvent.isParticipating())
				{
					Announcements.getInstance().gameAnnounceToAll("TvT Event: " + (time / 60 / 60) + " hour(s) until registration is closed!");
				}
				else if (TvTEvent.isStarted())
				{
					TvTEvent.sysMsgToAllParticipants("TvT Event: " + (time / 60 / 60) + " hour(s) until event is finished!");
				}
			}
			else if (time >= 60)
			{
				if (TvTEvent.isParticipating())
				{
					Announcements.getInstance().gameAnnounceToAll("TvT Event: " + (time / 60) + " minute(s) until registration is closed!");
				}
				else if (TvTEvent.isStarted())
				{
					TvTEvent.sysMsgToAllParticipants("TvT Event: " + (time / 60) + " minute(s) until the event is finished!");
				}
			}
			else
			{
				if (TvTEvent.isParticipating())
				{
					Announcements.getInstance().gameAnnounceToAll("TvT Event: " + time + " second(s) until registration is closed!");
				}
				else if (TvTEvent.isStarted())
				{
					TvTEvent.sysMsgToAllParticipants("TvT Event: " + time + " second(s) until the event is finished!");
				}
			}
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final TvTManager _instance = new TvTManager();
	}
}