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

/**
 * @author L2Scoria dev
 */
package com.it.br.gameserver.model.Olympiad;

import static com.it.br.configuration.Configurator.getSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.configuration.settings.OlympiadSettings;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.instancemanager.OlympiadStadiaManager;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Hero;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.StatsSet;
import com.it.br.util.L2FastList;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class Olympiad
{
	protected static final Logger _log = Logger.getLogger(Olympiad.class.getName());

	private static Olympiad _instance;

    static enum COMP_TYPE
    {
    	CLASSED,
    	NON_CLASSED
    }

	protected static Map<Integer, StatsSet> _nobles = new HashMap<>();
	protected static L2FastList<StatsSet> _heroesToBe;
	protected static L2FastList<L2PcInstance> _nonClassBasedRegisters;
	protected static Map<Integer, L2FastList<L2PcInstance>> _classBasedRegisters;

	private static final String OLYMPIAD_DATA_FILE = "config/other/olympiad.info";
	public static final String OLYMPIAD_HTML_FILE = "data/html/olympiad/";
	private static final String OLYMPIAD_LOAD_NOBLES = "SELECT * from olympiad_nobles";
	private static final String OLYMPIAD_SAVE_NOBLES = "INSERT INTO olympiad_nobles " + "values (?,?,?,?,?)";
	private static final String OLYMPIAD_UPDATE_NOBLES = "UPDATE olympiad_nobles set " + "olympiad_points = ?, competitions_done = ? where char_id = ?";
	private static final String OLYMPIAD_GET_HEROS = "SELECT char_id, char_name from " + "olympiad_nobles where class_id = ? and competitions_done >= 9 order by " + "olympiad_points desc, competitions_done desc";
	private static final String GET_EACH_CLASS_LEADER = "SELECT char_name from " + "olympiad_nobles where class_id = ? order by olympiad_points desc, " + "competitions_done desc";
	private static final String OLYMPIAD_DELETE_ALL = "DELETE from olympiad_nobles";

	private static final int DEFAULT_POINTS = 18;
	protected static final int WEEKLY_POINTS = 3;

	public static final String CHAR_ID = "char_id";
	public static final String CLASS_ID = "class_id";
	public static final String CHAR_NAME = "char_name";
	public static final String POINTS = "olympiad_points";
	public static final String COMP_DONE = "competitions_done";

	
	private int startHour;
	private int startMinute; 
	protected long validationPeriod;
	protected long weeklyPeriod;
	protected long initialWait;
	private long competitionPeriod;
	protected long battlePeriod;
	protected long battleWait;
	
	protected long _olympiadEnd;
	protected long _validationEnd;
	protected int _period;
	protected long _nextWeeklyChange;
	protected int _currentCycle;
	private long _compEnd;
	private Calendar _compStart;
	protected static boolean _inCompPeriod;
	protected static boolean _isOlympiadEnd;
	protected static boolean _compStarted = false;
	protected static boolean _battleStarted;
	protected static boolean _cycleTerminated;
	protected ScheduledFuture<?> _scheduledCompStart;
	protected ScheduledFuture<?> _scheduledCompEnd;
	protected ScheduledFuture<?> _scheduledOlympiadEnd;
	protected ScheduledFuture<?> _scheduledManagerTask;
	protected ScheduledFuture<?> _scheduledWeeklyTask;
	protected ScheduledFuture<?> _scheduledValdationTask;

	// Coordinates Arena teleport for Olympiad Stadium
	protected static final OlympiadStadia[] STADIUMS =
	{
			new OlympiadStadia(-20814, -21189, -3030),
			new OlympiadStadia(-120324, -225077, -3331),
			new OlympiadStadia(-102495, -209023, -3331),
			new OlympiadStadia(-120156, -207378, -3331),
			new OlympiadStadia(-87628, -225021, -3331),
			new OlympiadStadia(-81705, -213209, -3331),
			new OlympiadStadia(-87593, -207339, -3331),
			new OlympiadStadia(-93709, -218304, -3331),
			new OlympiadStadia(-77157, -218608, -3331),
			new OlympiadStadia(-69682, -209027, -3331),
			new OlympiadStadia(-76887, -201256, -3331),
			new OlympiadStadia(-109985, -218701, -3331),
			new OlympiadStadia(-126367, -218228, -3331),
			new OlympiadStadia(-109629, -201292, -3331),
			new OlympiadStadia(-87523, -240169, -3331),
			new OlympiadStadia(-81748, -245950, -3331),
			new OlympiadStadia(-77123, -251473, -3331),
			new OlympiadStadia(-69778, -241801, -3331),
			new OlympiadStadia(-76754, -234014, -3331),
			new OlympiadStadia(-93742, -251032, -3331),
			new OlympiadStadia(-87466, -257752, -3331),
			new OlympiadStadia(-114413, -213241, -3331)
	};

	protected static OlympiadManager _manager;

	public static Olympiad getInstance()
	{
		if(_instance == null)
		{
			_instance = new Olympiad();
		}
		return _instance;
	}

	public Olympiad() {
		OlympiadSettings olympiadSettings = getSettings(OlympiadSettings.class);
		startHour = olympiadSettings.getStartHour();
		startMinute = olympiadSettings.getStartMinute();
		competitionPeriod = olympiadSettings.getCompetitionPeriod();
		initialWait = olympiadSettings.getInitialWaitTime();
		battlePeriod = olympiadSettings.getBattlePeriod();
		battleWait = olympiadSettings.getBattleWaitTime();
		validationPeriod = olympiadSettings.getValidationPeriod();
		weeklyPeriod = olympiadSettings.getWeeklyPeriod();
	}

	public void load()
	{
		_nobles.clear();

		boolean initialized = true;
		Properties OlympiadProperties = new Properties();
		InputStream is = null;
		try
		{
			is = new FileInputStream(new File("./" + OLYMPIAD_DATA_FILE));
			
			OlympiadProperties.load(is);
			
			_currentCycle = Integer.parseInt(OlympiadProperties.getProperty("CurrentCycle", "1"));
			_period = Integer.parseInt(OlympiadProperties.getProperty("Period", "0"));
			_olympiadEnd = Long.parseLong(OlympiadProperties.getProperty("OlympiadEnd", "0"));
			_validationEnd = Long.parseLong(OlympiadProperties.getProperty("ValdationEnd", "0"));
			_nextWeeklyChange = Long.parseLong(OlympiadProperties.getProperty("NextWeeklyChange", "0"));

			switch(_period)
			{
				case 0:
					if(_olympiadEnd == 0 || _olympiadEnd < Calendar.getInstance().getTimeInMillis())
					{
						setNewOlympiadEnd();
					}
					else
					{
						_isOlympiadEnd = false;
					}
					break;
				case 1:
					if(_validationEnd > Calendar.getInstance().getTimeInMillis())
					{
						_isOlympiadEnd = true;

						_scheduledValdationTask = ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
							@Override
							public void run()
							{
								_period = 0;
								_currentCycle++;
								deleteNobles();
								setNewOlympiadEnd();
								init();
							}
						}, getMillisToValidationEnd());
					}
					else
					{
						_currentCycle++;
						_period = 0;
						deleteNobles();
						setNewOlympiadEnd();
					}
					break;
				default:
					_log.warning("Olympiad System: Omg something went wrong in loading!! Period = " + _period);
					initialized = false;
			}
		}
		catch(FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			
		}finally{
			
			if(is != null){
				
				try
				{
					is.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		if(!initialized)
			return;
		
		Connection con = null;
		
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(OLYMPIAD_LOAD_NOBLES);
			ResultSet rset = statement.executeQuery();

			while(rset.next())
			{
				StatsSet statDat = new StatsSet();
				int charId = rset.getInt(CHAR_ID);
				statDat.set(CLASS_ID, rset.getInt(CLASS_ID));
				statDat.set(CHAR_NAME, rset.getString(CHAR_NAME));
				statDat.set(POINTS, rset.getInt(POINTS));
				statDat.set(COMP_DONE, rset.getInt(COMP_DONE));
				statDat.set("to_save", false);
				_nobles.put(charId, statDat);
			}
			rset.close();
			statement.close();
			rset = null;
			statement = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		synchronized (this)
		{
			_log.info("Olympiad System: Loading Olympiad System....");
			if(_period == 0)
			{
				_log.info("Olympiad System: Currently in Olympiad Period");
			}
			else
			{
				_log.info("Olympiad System: Currently in Validation Period");
			}

			_log.info("Olympiad System: Period Ends....");

			long milliToEnd;
			if(_period == 0)
			{
				milliToEnd = getMillisToOlympiadEnd();
			}
			else
			{
				milliToEnd = getMillisToValidationEnd();
			}

			double numSecs = milliToEnd / 1000 % 60;
			double countDown = (milliToEnd / 1000 - numSecs) / 60;
			int numMins = (int) Math.floor(countDown % 60);
			countDown = (countDown - numMins) / 60;
			int numHours = (int) Math.floor(countDown % 24);
			int numDays = (int) Math.floor((countDown - numHours) / 24);

			_log.info("Olympiad System: In " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");

			if(_period == 0)
			{
				_log.info("Olympiad System: Next Weekly Change is in....");

				milliToEnd = getMillisToWeekChange();

				double numSecs2 = milliToEnd / 1000 % 60;
				double countDown2 = (milliToEnd / 1000 - numSecs2) / 60;
				int numMins2 = (int) Math.floor(countDown2 % 60);
				countDown2 = (countDown2 - numMins2) / 60;
				int numHours2 = (int) Math.floor(countDown2 % 24);
				int numDays2 = (int) Math.floor((countDown2 - numHours2) / 24);

				_log.info("Olympiad System: " + numDays2 + " days, " + numHours2 + " hours and " + numMins2 + " mins.");
			}
		}
		_log.info("Olympiad System: Loaded " + _nobles.size() + " Nobles");

		if(_period == 0)
		{
			init();
		}
	}

	protected void init()
	{
		if(_period == 1)
			return;
		_nonClassBasedRegisters = new L2FastList<L2PcInstance>();
		_classBasedRegisters = new HashMap<>();

		_compStart = Calendar.getInstance();
		_compStart.set(Calendar.HOUR_OF_DAY, startHour);
		_compStart.set(Calendar.MINUTE, startMinute);
		_compEnd = _compStart.getTimeInMillis() + competitionPeriod;

		_scheduledOlympiadEnd = ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
			@Override
			public void run()
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.OLYMPIAD_PERIOD_S1_HAS_ENDED);
				sm.addNumber(_currentCycle);

				Announcements.getInstance().announceToAll(sm);
				Announcements.getInstance().announceToAll("Olympiad Validation Period has began");

				_isOlympiadEnd = true;
				if(_scheduledManagerTask != null)
				{
					_scheduledManagerTask.cancel(true);
				}
				if(_scheduledWeeklyTask != null)
				{
					_scheduledWeeklyTask.cancel(true);
				}

				Calendar validationEnd = Calendar.getInstance();
				_validationEnd = validationEnd.getTimeInMillis() + validationPeriod;

				saveNobleData();

				_period = 1;

				sortHerosToBe();

				giveHeroBonus();

				Hero.getInstance().computeNewHeroes(_heroesToBe);

				try
				{
					save();
				}
				catch(Exception e)
				{
					if(Config.DEBUG)
						e.printStackTrace();
					
					_log.warning("Olympiad System: Failed to save Olympiad configuration: " + e);
				}

				_scheduledValdationTask = ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
					@Override
					public void run()
					{
						Announcements.getInstance().announceToAll("Olympiad Validation Period has ended");
						_period = 0;
						_currentCycle++;
						deleteNobles();
						setNewOlympiadEnd();
						init();
					}
				}, getMillisToValidationEnd());
			}
		}, getMillisToOlympiadEnd());

		updateCompStatus();
		scheduleWeeklyChange();
	}

	public boolean registerNoble(L2PcInstance noble, boolean classBased)
	{
		SystemMessage sm;

		/*
		if (_compStarted)
		{
		    noble.sendMessage("Cant Register whilst competition is under way");
		    return false;
		}
		*/

		if(noble.getKarma() > 0)
		{
			noble.sendMessage("You can't participate to Olympiad with karma.");
			return false;
		}

		if(!_inCompPeriod)
		{
			sm = new SystemMessage(SystemMessageId.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
			noble.sendPacket(sm);
			return false;
		}

		if(noble.isInFunEvent())
		{
			noble.sendMessage("You can't participate to Olympiad. You are in Event now.");
			return false;
		}

		if(noble.isCursedWeaponEquipped())
		{
			noble.sendMessage("You can't participate to Olympiad while holding a cursed weapon.");
			return false;
		}

		if(!noble.isNoble())
		{
			sm = new SystemMessage(SystemMessageId.ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD);
			noble.sendPacket(sm);
			return false;
		}
		
		if(noble.inObserverMode())
		{
			noble.sendMessage("You can't participate to Olympiad. You are in Observer Mode");
			return false;
		}
		
		if(noble.getBaseClass() != noble.getClassId().getId())
		{
			sm = new SystemMessage(SystemMessageId.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
			noble.sendPacket(sm);
			return false;
		}

		final int noble_obj_id = noble.getObjectId();
		if(!_nobles.containsKey(noble_obj_id))
		{
			StatsSet statDat = new StatsSet();
			statDat.set(CLASS_ID, noble.getClassId().getId());
			statDat.set(CHAR_NAME, noble.getName());
			statDat.set(POINTS, DEFAULT_POINTS);
			statDat.set(COMP_DONE, 0);
			statDat.set("to_save", true);

			_nobles.put(noble_obj_id, statDat);
		}

		if(_classBasedRegisters.containsKey(noble.getClassId().getId()))
		{
			final L2FastList<L2PcInstance> classed = _classBasedRegisters.get(noble.getClassId().getId());
			for(final L2PcInstance partecipant : classed)
			{
				if(partecipant!=null) {
					if(partecipant.getObjectId() == noble_obj_id)
					{
						sm = new SystemMessage(SystemMessageId.YOU_ARE_ALREADY_ON_THE_WAITING_LIST_TO_PARTICIPATE_IN_THE_GAME_FOR_YOUR_CLASS);
						noble.sendPacket(sm);
						return false;
					}
				} else 
					classed.remove(partecipant);
			}
		}

		final L2FastList<L2PcInstance> not_classed = _nonClassBasedRegisters;
		for(final L2PcInstance partecipant : not_classed)
		{
			if(partecipant!=null) {
				if(partecipant.getObjectId() == noble_obj_id)
				{
					sm = new SystemMessage(SystemMessageId.YOU_ARE_ALREADY_ON_THE_WAITING_LIST_FOR_ALL_CLASSES_WAITING_TO_PARTICIPATE_IN_THE_GAME);
					noble.sendPacket(sm);
					return false;
				}
			} else 
				_nonClassBasedRegisters.remove(partecipant);
		}

		final Collection<OlympiadGame> games =  _manager.getOlympiadGames().values();
		for(final OlympiadGame g :games)
		{
			if (g == null)
				continue;

			for(final L2PcInstance player : g.getPlayers())
			{
				if(player.getObjectId() == noble_obj_id)
				{
					sm = new SystemMessage(SystemMessageId.YOU_ARE_ALREADY_ON_THE_WAITING_LIST_FOR_ALL_CLASSES_WAITING_TO_PARTICIPATE_IN_THE_GAME);
					noble.sendPacket(sm);
					return false;
				}
			}
		}
		if(classBased && getNoblePoints(noble_obj_id) < 3)
		{
			noble.sendMessage("Cant register when you have less than 3 points");
			return false;
		}
		if(!classBased && getNoblePoints(noble_obj_id) < 5)
		{
			noble.sendMessage("Cant register when you have less than 5 points");
			return false;
		}

		if(classBased)
		{
			if(_classBasedRegisters.containsKey(noble.getClassId().getId()))
			{
				L2FastList<L2PcInstance> classed = _classBasedRegisters.get(noble.getClassId().getId());
				classed.add(noble);

				_classBasedRegisters.remove(noble.getClassId().getId());
				_classBasedRegisters.put(noble.getClassId().getId(), classed);

				sm = new SystemMessage(SystemMessageId.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_CLASSIFIED_GAMES);
				noble.sendPacket(sm);
			}
			else
			{
				L2FastList<L2PcInstance> classed = new L2FastList<L2PcInstance>();
				classed.add(noble);

				_classBasedRegisters.put(noble.getClassId().getId(), classed);

				sm = new SystemMessage(SystemMessageId.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_CLASSIFIED_GAMES);
				noble.sendPacket(sm);
			}
		}
		else
		{
			_nonClassBasedRegisters.add(noble);
			sm = new SystemMessage(SystemMessageId.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_NO_CLASS_GAMES);
			noble.sendPacket(sm);
		}

		return true;
	}

	public boolean isRegistered(L2PcInstance noble)
	{
		if(_nonClassBasedRegisters == null)
			return false;
		if(_classBasedRegisters == null)
			return false;
		if(!_nonClassBasedRegisters.contains(noble))
		{
			if(!_classBasedRegisters.containsKey(noble.getClassId().getId()))
				return false;
			
			L2FastList<L2PcInstance> classed = _classBasedRegisters.get(noble.getClassId().getId());
			if(classed == null || !classed.contains(noble))
				return false;
		}
		return true;
	}

	public boolean unRegisterNoble(L2PcInstance noble)
	{
		SystemMessage sm;
		/*
		if (_compStarted)
		{
			noble.sendMessage("Cant Unregister whilst competition is under way");
			return false;
		}
		*/

		if(!_inCompPeriod)
		{
			sm = new SystemMessage(SystemMessageId.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
			noble.sendPacket(sm);
			return false;
		}

		if(!noble.isNoble())
		{
			sm = new SystemMessage(SystemMessageId.ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD);
			noble.sendPacket(sm);
			return false;
		}

		if(!isRegistered(noble))
		{
			sm = new SystemMessage(SystemMessageId.YOU_HAVE_NOT_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_A_GAME);
			noble.sendPacket(sm);
			return false;
		}

		if(_nonClassBasedRegisters.contains(noble))
		{
			_nonClassBasedRegisters.remove(noble);
		}
		else
		{
			L2FastList<L2PcInstance> classed = _classBasedRegisters.get(noble.getClassId().getId());
			classed.remove(noble);

			_classBasedRegisters.remove(noble.getClassId().getId());
			_classBasedRegisters.put(noble.getClassId().getId(), classed);
		}

		for(OlympiadGame game : _manager.getOlympiadGames().values())
		{
			if(game._playerOne.getObjectId() == noble.getObjectId() || game._playerTwo.getObjectId() == noble.getObjectId())
			{
				noble.sendMessage("Cant unregister while you are already selected for a game");
				return false;
			}
		}

		sm = new SystemMessage(SystemMessageId.YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME);
		noble.sendPacket(sm);

		return true;
	}

	public void removeDisconnectedCompetitor(L2PcInstance player)
	{
		if(_manager == null || _manager.getOlympiadInstance(player.getOlympiadGameId()) == null)
			return;

		_manager.getOlympiadInstance(player.getOlympiadGameId()).handleDisconnect(player);
	}

	private void updateCompStatus()
	{
		//_compStarted = false;

		synchronized (this)
		{
			long milliToStart = getMillisToCompBegin();

			double numSecs = milliToStart / 1000 % 60;
			double countDown = (milliToStart / 1000 - numSecs) / 60;
			int numMins = (int) Math.floor(countDown % 60);
			countDown = (countDown - numMins) / 60;
			int numHours = (int) Math.floor(countDown % 24);
			int numDays = (int) Math.floor((countDown - numHours) / 24);

			_log.info("Olympiad System: Competition Period Starts in " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");

			_log.info("Olympiad System: Event starts/started : " + _compStart.getTime());
		}

		_scheduledCompStart = ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
			@Override
			public void run()
			{
				if(isOlympiadEnd())
					return;

				_inCompPeriod = true;
				OlympiadManager om = new OlympiadManager();

				Announcements.getInstance().announceToAll(new SystemMessage(SystemMessageId.THE_OLYMPIAD_GAME_HAS_STARTED));
				_log.info("Olympiad System: Olympiad Game Started");

				Thread olyCycle = new Thread(om);
				olyCycle.start();

				//_scheduledManagerTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(om, INITIAL_WAIT, BATTLE_WAIT);

				_scheduledCompEnd = ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
					@Override
					public void run()
					{
						if(isOlympiadEnd())
							return;
						//_scheduledManagerTask.cancel(true);
						_inCompPeriod = false;
						Announcements.getInstance().announceToAll(new SystemMessage(SystemMessageId.THE_OLYMPIAD_GAME_HAS_ENDED));
						_log.info("Olympiad System: Olympiad Game Ended");

						try
						{
							while(_battleStarted)
							{
								try
								{
									//wait 1 minutes for end of pendings games
									Thread.sleep(60000);
								}
								catch(InterruptedException e)
								{
									if(Config.DEBUG)
										e.printStackTrace();
								}
							}
							save();
						}
						catch(Exception e)
						{
							if(Config.DEBUG)
								e.printStackTrace();
							
							_log.warning("Olympiad System: Failed to save Olympiad configuration: " + e);
						}

						init();
					}
				}, getMillisToCompEnd());
			}
		}, getMillisToCompBegin());
	}

	private long getMillisToOlympiadEnd()
	{
		//if (_olympiadEnd > Calendar.getInstance().getTimeInMillis())
		return _olympiadEnd - Calendar.getInstance().getTimeInMillis();
		//return 10L;
	}

	public void manualSelectHeroes()
	{
		SystemMessage sm = new SystemMessage(SystemMessageId.OLYMPIAD_PERIOD_S1_HAS_ENDED);
		sm.addNumber(_currentCycle);

		Announcements.getInstance().announceToAll(sm);
		Announcements.getInstance().announceToAll("Olympiad Validation Period has began");

		_isOlympiadEnd = true;
		if(_scheduledManagerTask != null)
		{
			_scheduledManagerTask.cancel(true);
		}
		if(_scheduledWeeklyTask != null)
		{
			_scheduledWeeklyTask.cancel(true);
		}
		if(_scheduledOlympiadEnd != null)
		{
			_scheduledOlympiadEnd.cancel(true);
		}

		Calendar validationEnd = Calendar.getInstance();
		_validationEnd = validationEnd.getTimeInMillis() + validationPeriod;

		saveNobleData();

		_period = 1;

		sortHerosToBe();

		giveHeroBonus();

		Hero.getInstance().computeNewHeroes(_heroesToBe);

		try
		{
			save();
		}
		catch(Exception e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
			
			_log.warning("Olympiad System: Failed to save Olympiad configuration: " + e);
		}

		_scheduledValdationTask = ThreadPoolManager.getInstance().scheduleGeneral(new Runnable() {
			@Override
			public void run()
			{
				Announcements.getInstance().announceToAll("Olympiad Validation Period has ended");
				_period = 0;
				_currentCycle++;
				deleteNobles();
				setNewOlympiadEnd();
				init();
			}
		}, getMillisToValidationEnd());
	}

	protected long getMillisToValidationEnd()
	{
		if(_validationEnd > Calendar.getInstance().getTimeInMillis())
			return _validationEnd - Calendar.getInstance().getTimeInMillis();
		return 10L;
	}

	public boolean isOlympiadEnd()
	{
		return _isOlympiadEnd;
	}

	protected void setNewOlympiadEnd()
	{
		SystemMessage sm = new SystemMessage(SystemMessageId.OLYMPIAD_PERIOD_S1_HAS_STARTED);
		sm.addNumber(_currentCycle);

		Announcements.getInstance().announceToAll(sm);

		Calendar currentTime = Calendar.getInstance();
		currentTime.set(Calendar.AM_PM, Calendar.AM);
		currentTime.set(Calendar.HOUR, 12);
		currentTime.set(Calendar.MINUTE, 0);
		currentTime.set(Calendar.SECOND, 0);
		
		OlympiadSettings olympiadSettings = getSettings(OlympiadSettings.class);
		OlympiadPeriod period = OlympiadPeriod.valueOf(olympiadSettings.getPeriod());
		int periodMultiplier = olympiadSettings.getPeriodMultiplier();
		if(period == null) {
			period = OlympiadPeriod.MONTH;
		}
		
		switch(period){
			case DAY:{
				currentTime.add(Calendar.DAY_OF_MONTH, periodMultiplier);
				currentTime.add(Calendar.DAY_OF_MONTH, -1); //last day is for validation
				
				if(periodMultiplier >= 14){
					Calendar nextChange = Calendar.getInstance();
					_nextWeeklyChange = nextChange.getTimeInMillis() + weeklyPeriod;
				}else if(periodMultiplier >= 7){
					Calendar nextChange = Calendar.getInstance();
					_nextWeeklyChange = nextChange.getTimeInMillis() + (weeklyPeriod/2);
				}else{
					//nothing to do, too low period
				}
				
			}
			break;
			case WEEK:{
				currentTime.add(Calendar.WEEK_OF_MONTH, periodMultiplier);
				currentTime.add(Calendar.DAY_OF_MONTH, -1); //last day is for validation
				
				if(periodMultiplier > 1 ){
					Calendar nextChange = Calendar.getInstance();
					_nextWeeklyChange = nextChange.getTimeInMillis() + weeklyPeriod;
				}else{
					Calendar nextChange = Calendar.getInstance();
					_nextWeeklyChange = nextChange.getTimeInMillis() + (weeklyPeriod/2);
				}
				
			}
			break;
			case MONTH:{
				currentTime.add(Calendar.MONTH, periodMultiplier);
				currentTime.add(Calendar.DAY_OF_MONTH, -1); //last day is for validation
				
				Calendar nextChange = Calendar.getInstance();
				_nextWeeklyChange = nextChange.getTimeInMillis() + weeklyPeriod;

			}
			break;
		}
		
		_olympiadEnd = currentTime.getTimeInMillis();

		
		_isOlympiadEnd = false;
	}

	public boolean inCompPeriod()
	{
		return _inCompPeriod;
	}

	private long getMillisToCompBegin()
	{
		if(_compStart.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() && _compEnd > Calendar.getInstance().getTimeInMillis())
			return 10L;

		if(_compStart.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
			return _compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

		return setNewCompBegin();
	}

	private long setNewCompBegin()
	{
		_compStart = Calendar.getInstance();
		_compStart.set(Calendar.HOUR_OF_DAY, startHour);
		_compStart.set(Calendar.MINUTE, startMinute);
		_compStart.add(Calendar.HOUR_OF_DAY, 24);
		_compEnd = _compStart.getTimeInMillis() + competitionPeriod;

		_log.info("Olympiad System: New Schedule @ " + _compStart.getTime());

		return _compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
	}

	protected long getMillisToCompEnd()
	{
		//if (_compEnd > Calendar.getInstance().getTimeInMillis())
		return _compEnd - Calendar.getInstance().getTimeInMillis();
		//return 10L;
	}

	private long getMillisToWeekChange()
	{
		if(_nextWeeklyChange > Calendar.getInstance().getTimeInMillis())
			return _nextWeeklyChange - Calendar.getInstance().getTimeInMillis();
		return 10L;
	}

	private void scheduleWeeklyChange()
	{
		OlympiadSettings olympiadSettings = getSettings(OlympiadSettings.class);
		OlympiadPeriod period = OlympiadPeriod.valueOf(olympiadSettings.getPeriod());
		int periodMultiplier = olympiadSettings.getPeriodMultiplier();
		if(period == null) {
			period = OlympiadPeriod.MONTH;
		}
		
		switch(period){
			case MONTH:{
				_scheduledWeeklyTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable() {
					@Override
					public void run()
					{
						addWeeklyPoints();
						_log.info("Olympiad System: Added weekly points to nobles");

						Calendar nextChange = Calendar.getInstance();
						_nextWeeklyChange = nextChange.getTimeInMillis() + weeklyPeriod;
					}
				}, getMillisToWeekChange(), weeklyPeriod);
			}
			break;
			case WEEK:{
				
				if(periodMultiplier > 1){
					_scheduledWeeklyTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable() {
						@Override
						public void run()
						{
							addWeeklyPoints();
							_log.info("Olympiad System: Added weekly points to nobles");

							Calendar nextChange = Calendar.getInstance();
							_nextWeeklyChange = nextChange.getTimeInMillis() + weeklyPeriod;
						}
					}, getMillisToWeekChange(), weeklyPeriod);
				}else{
					_scheduledWeeklyTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable() {
						@Override
						public void run()
						{
							addWeeklyPoints();
							_log.info("Olympiad System: Added weekly points to nobles");

							Calendar nextChange = Calendar.getInstance();
							_nextWeeklyChange = nextChange.getTimeInMillis() + weeklyPeriod/2;
						}
					}, getMillisToWeekChange(), weeklyPeriod/2);
				}
				
			}
			break;
			case DAY:{
				
				if(periodMultiplier >= 14){
					_scheduledWeeklyTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable() {
						@Override
						public void run()
						{
							addWeeklyPoints();
							_log.info("Olympiad System: Added weekly points to nobles");

							Calendar nextChange = Calendar.getInstance();
							_nextWeeklyChange = nextChange.getTimeInMillis() + weeklyPeriod;
						}
					}, getMillisToWeekChange(), weeklyPeriod);
				}else if(periodMultiplier >= 7){
					_scheduledWeeklyTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable() {
						@Override
						public void run()
						{
							addWeeklyPoints();
							_log.info("Olympiad System: Added weekly points to nobles");

							Calendar nextChange = Calendar.getInstance();
							_nextWeeklyChange = nextChange.getTimeInMillis() + weeklyPeriod/2;
						}
					}, getMillisToWeekChange(), weeklyPeriod/2);
				}else{
					//nothing to do, too low period
				}
				
			}
			break;
		}
		
		
	}

	protected synchronized void addWeeklyPoints()
	{
		if(_period == 1)
			return;

		for(Integer nobleId : _nobles.keySet())
		{
			StatsSet nobleInfo = _nobles.get(nobleId);
			int currentPoints = nobleInfo.getInteger(POINTS);
			currentPoints += WEEKLY_POINTS;
			nobleInfo.set(POINTS, currentPoints);

			_nobles.remove(nobleId);
			_nobles.put(nobleId, nobleInfo);
		}
	}

	public String[] getMatchList()
	{
		return _manager == null ? null : _manager.getAllTitles();
	}

	public L2PcInstance[] getPlayers(int Id)
	{
		if(_manager == null || _manager.getOlympiadInstance(Id) == null)
			return null;
		L2PcInstance[] players = _manager.getOlympiadInstance(Id).getPlayers();
		return players;
	}

	public int getCurrentCycle()
	{
		return _currentCycle;
	}

	public void addSpectator(int id, L2PcInstance spectator)
	{
		for(L2PcInstance player : _nonClassBasedRegisters)
		{
			if(spectator.getObjectId() == player.getObjectId())
			{
				spectator.sendMessage("You are already registered for a competition");
				return;
			}
		}
		for(L2FastList<L2PcInstance> list : _classBasedRegisters.values())
		{
			for(L2PcInstance player : list)
			{
				if(spectator.getObjectId() == player.getObjectId())
				{
					spectator.sendMessage("You are already registered for a competition");
					return;
				}
			}
		}
		if(spectator.getOlympiadGameId() != -1)
		{
			spectator.sendMessage("You are already registered for a competition");
			return;
		}
		if(_manager == null || _manager.getOlympiadInstance(id) == null)
		{
			spectator.sendPacket(new SystemMessage(SystemMessageId.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS));
			return;
		}

		L2PcInstance[] players = _manager.getOlympiadInstance(id).getPlayers();

		if(players == null)
			return;

		spectator.enterOlympiadObserverMode(STADIUMS[id].getCoordinates()[0], STADIUMS[id].getCoordinates()[1], STADIUMS[id].getCoordinates()[2], id);

		_manager.getOlympiadInstance(id).addSpectator(spectator);
	}

	public void removeSpectator(int id, L2PcInstance spectator)
	{
		if(_manager == null || _manager.getOlympiadInstance(id) == null)
			return;

		_manager.getOlympiadInstance(id).removeSpectator(spectator);
	}

	/**
	 * @param noble the noblesse player to check.
	 * @return true if the player is an spectator, false otherwise.
	 */
	public boolean isSpectator(L2PcInstance noble)
	{
		if(_manager == null)
		{
			return false;
		}
		
		for(OlympiadGame game : _manager.getOlympiadGames().values())
		{
			if(game.getSpectators().contains(noble))
			{
				return true;
			}
		}
		return false;
	}

	
	public L2FastList<L2PcInstance> getSpectators(int id)
	{
		if(_manager == null || _manager.getOlympiadInstance(id) == null)
			return null;
		return _manager.getOlympiadInstance(id).getSpectators();
	}

	public Map<Integer, OlympiadGame> getOlympiadGames()
	{
		return _manager.getOlympiadGames();
	}

	public boolean playerInStadia(L2PcInstance player)
	{
		return OlympiadStadiaManager.getInstance().getStadium(player) != null;
	}

	public int[] getWaitingList()
	{
		int[] array = new int[2];

		if(!inCompPeriod())
			return null;

		int classCount = 0;

		if(_classBasedRegisters.size() != 0)
		{
			for(L2FastList<L2PcInstance> classed : _classBasedRegisters.values())
			{
				classCount += classed.size();
			}
		}

		array[0] = classCount;
		array[1] = _nonClassBasedRegisters.size();

		return array;
	}

	protected synchronized void saveNobleData()
	{
		Connection con = null;

		if(_nobles == null)
			return;

		final Map<Integer, StatsSet> nobles = _nobles;
		
		PreparedStatement statement;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			final Set<Integer> nobles_list = nobles.keySet();
			for(final Integer nobleId : nobles_list)
			{
				final StatsSet nobleInfo = nobles.get(nobleId);

				int charId = nobleId;
				int classId = nobleInfo.getInteger(CLASS_ID);
				String charName = nobleInfo.getString(CHAR_NAME);
				int points = nobleInfo.getInteger(POINTS);
				int compDone = nobleInfo.getInteger(COMP_DONE);
				boolean toSave = nobleInfo.getBool("to_save");

				try{
					
					if(toSave)
					{
						statement = con.prepareStatement(OLYMPIAD_SAVE_NOBLES);
						statement.setInt(1, charId);
						statement.setInt(2, classId);
						statement.setString(3, charName);
						statement.setInt(4, points);
						statement.setInt(5, compDone);
						statement.execute();

						statement.close();

						nobleInfo.set("to_save", false);

						nobles.remove(nobleId);
						nobles.put(nobleId, nobleInfo);
					}
					else
					{
						statement = con.prepareStatement(OLYMPIAD_UPDATE_NOBLES);
						statement.setInt(1, points);
						statement.setInt(2, compDone);
						statement.setInt(3, charId);
						statement.execute();
						statement.close();
						
					}
					
				}
				catch(MySQLIntegrityConstraintViolationException e){ //update to avoid miss information
					
					try{
					
						statement = con.prepareStatement(OLYMPIAD_UPDATE_NOBLES);
						statement.setInt(1, points);
						statement.setInt(2, compDone);
						statement.setInt(3, charId);
						statement.execute();
						statement.close();
						
						nobleInfo.set("to_save", false);

						nobles.remove(nobleId);
						nobles.put(nobleId, nobleInfo);
						
					}catch(SQLException e1)
					{
						e.printStackTrace();
						_log.warning("Olympiad System: Couldn't save nobles info in db");
					}
					
				}
				
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			_log.warning("Olympiad System: Couldn't save nobles info in db");
		}
	}

	protected void sortHerosToBe()
	{
		if(_period != 1)
			return;

		_heroesToBe = new L2FastList<StatsSet>();

		Connection con = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			ResultSet rset;
			StatsSet hero;

			for (int i = 88; i < 119; i++)
			{
				statement = con.prepareStatement(OLYMPIAD_GET_HEROS);
				statement.setInt(1, i);
				rset = statement.executeQuery();

				if(rset.next())
				{
					hero = new StatsSet();
					hero.set(CLASS_ID, i);
					hero.set(CHAR_ID, rset.getInt(CHAR_ID));
					hero.set(CHAR_NAME, rset.getString(CHAR_NAME));

					_heroesToBe.add(hero);
				}

				statement.close();
				rset.close();
			}
		}
		catch(SQLException e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
			
			_log.warning("Olympiad System: Couldn't  read heroes from db");
		}
	}

	public L2FastList<String> getClassLeaderBoard(int classId)
	{
		//if (_period != 1) return;

		L2FastList<String> names = new L2FastList<String>();

		Connection con = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			ResultSet rset;
			statement = con.prepareStatement(GET_EACH_CLASS_LEADER);
			statement.setInt(1, classId);
			rset = statement.executeQuery();

			while(rset.next())
			{
				names.add(rset.getString(CHAR_NAME));
			}

			statement.close();
			rset.close();

		}
		catch(SQLException e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
			
			_log.warning("Olympiad System: Couldnt heros from db");
		}
		return names;

	}

	protected void giveHeroBonus()
	{
		if(_heroesToBe.size() == 0)
			return;

		for(StatsSet hero : _heroesToBe)
		{
			int charId = hero.getInteger(CHAR_ID);

			StatsSet noble = _nobles.get(charId);
			int currentPoints = noble.getInteger(POINTS);
			currentPoints +=  getSettings(OlympiadSettings.class).getHeroPoints();
			noble.set(POINTS, currentPoints);

			_nobles.remove(charId);
			_nobles.put(charId, noble);
		}
	}

	public int getNoblessePasses(int objId)
	{
		if(_period != 1 || _nobles.size() == 0)
			return 0;

		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		int points = noble.getInteger(POINTS);
		OlympiadSettings olympiadSettings = getSettings(OlympiadSettings.class);
		if(points <= olympiadSettings.getMinimumPointForExchange())
			return 0;

		noble.set(POINTS, 0);
		_nobles.remove(objId);
		_nobles.put(objId, noble);

		points *= olympiadSettings.getGatePassPerPoint();

		return points;
	}

	public boolean isRegisteredInComp(L2PcInstance player)
	{
		boolean result = false;

		if(player == null)
			return false;
		
		if(_nonClassBasedRegisters != null && _nonClassBasedRegisters.contains(player))
		{
			result = true;
		}
		else if(_classBasedRegisters != null && _classBasedRegisters.containsKey(player.getClassId().getId()))
		{
			L2FastList<L2PcInstance> classed = _classBasedRegisters.get(player.getClassId().getId());
			if(classed.contains(player))
			{
				result = true;
			}
		}
		if(_inCompPeriod)
		{
			if(_manager != null && _manager.getOlympiadGames() != null)
			{
				for(OlympiadGame game : _manager.getOlympiadGames().values())
				{
					if(game._playerOne!= null && game._playerOne.getObjectId() == player.getObjectId() || game._playerTwo!=null && game._playerTwo.getObjectId() == player.getObjectId())
					{
						result = true;
						break;
					}
				}
			}
		}

		return result;
	}

	public int getNoblePoints(int objId)
	{
		if(_nobles.size() == 0)
			return 0;

		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		int points = noble.getInteger(POINTS);

		return points;
	}

	public int getCompetitionDone(int objId)
	{
		if(_nobles.size() == 0)
			return 0;

		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		int points = noble.getInteger(COMP_DONE);

		return points;
	}

	protected void deleteNobles()
	{
		Connection con = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(OLYMPIAD_DELETE_ALL);
			statement.execute();
			statement.close();
		}
		catch(SQLException e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
			
			_log.warning("Olympiad System: Couldnt delete nobles from db");
		}
		_nobles.clear();
	}

	public void save()
	{
		saveNobleData();

		Properties OlympiadProperties = new Properties();
		
		File file = null;
		FileOutputStream fos = null;
		try
		{
			ServerSettings serverSettings = getSettings(ServerSettings.class);
			file = new File(serverSettings.getDatapackDirectory(), OLYMPIAD_DATA_FILE);
			fos = new FileOutputStream(file);
			
			OlympiadProperties.setProperty("CurrentCycle", String.valueOf(_currentCycle));
			OlympiadProperties.setProperty("Period", String.valueOf(_period));
			OlympiadProperties.setProperty("OlympiadEnd", String.valueOf(_olympiadEnd));
			OlympiadProperties.setProperty("ValdationEnd", String.valueOf(_validationEnd));
			OlympiadProperties.setProperty("NextWeeklyChange", String.valueOf(_nextWeeklyChange));

			GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
			gc.clear();
			gc.setTimeInMillis(_nextWeeklyChange);
			
			OlympiadProperties.setProperty("NextWeeklyChange_DateFormat", DateFormat.getDateTimeInstance().format(gc.getTime()));
			//System.out.println("NextPoints: "+DateFormat.getInstance().format(gc.getTime()));
			
			gc.clear();
			gc.setTimeInMillis(_olympiadEnd);
			
			OlympiadProperties.setProperty("OlympiadEnd_DateFormat", DateFormat.getDateTimeInstance().format(gc.getTime()));
			//System.out.println("NextOlyDate: "+DateFormat.getInstance().format(gc.getTime()));
			
			OlympiadProperties.store(fos, "Olympiad Properties");
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		
		}finally{
			
			if(fos != null)
				try
				{
					fos.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			
		}

	}

	public void logoutPlayer(L2PcInstance player)
	{
		_classBasedRegisters.remove(Integer.valueOf(player.getClassId().getId()));
		_nonClassBasedRegisters.remove(player);
	}
}
