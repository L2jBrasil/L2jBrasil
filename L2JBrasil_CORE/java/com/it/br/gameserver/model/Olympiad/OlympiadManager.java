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
package com.it.br.gameserver.model.Olympiad;

import static com.it.br.configuration.Configurator.getSettings;

import java.util.HashMap;
import java.util.Map;

import com.it.br.Config;
import com.it.br.configuration.settings.OlympiadSettings;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.util.L2FastList;
import com.it.br.util.Rnd;

class OlympiadManager extends Olympiad implements Runnable
{
	private Map<Integer, OlympiadGame> _olympiadInstances;

	public OlympiadManager()
	{
		_olympiadInstances = new HashMap<>();
		_manager = this;
	}

	@Override
	public synchronized void run()
	{
		_cycleTerminated = false;
		if(isOlympiadEnd())
		{
			_scheduledManagerTask.cancel(true);
			_cycleTerminated = true;
			return;
		}
		Map<Integer, OlympiadGameTask> _gamesQueue = new HashMap<>();
		while(inCompPeriod())
		{
			if(_nobles.size() == 0)
			{
				try
				{
					wait(60000);
				}
				catch(InterruptedException ex)
				{
					if(Config.DEBUG)
						ex.printStackTrace();
				}
				continue;
			}

			//_compStarted = true;
			int classBasedPgCount = 0;
			for(L2FastList<L2PcInstance> classList : _classBasedRegisters.values())
			{
				classBasedPgCount += classList.size();
			}
			
			
			OlympiadSettings olympiadSettings = getSettings(OlympiadSettings.class);
			while((_gamesQueue.size() > 0 || classBasedPgCount >= olympiadSettings.getMinimumClassedParticipants()|| 
					_nonClassBasedRegisters.size() >= olympiadSettings.getMinimumNonClassedParticipants()) 
					&& inCompPeriod()) {
				//first cycle do nothing
				int _gamesQueueSize = 0;
				_gamesQueueSize = _gamesQueue.size();
				for(int i = 0; i < _gamesQueueSize; i++)
				{
					if(_gamesQueue.get(i) == null || _gamesQueue.get(i).isTerminated() || _gamesQueue.get(i)._game == null)
					{
						if(_gamesQueue.containsKey(i))
						{
							//removes terminated games from the queue
							try
							{
								_olympiadInstances.remove(i);
								_gamesQueue.remove(i);
								STADIUMS[i].setStadiaFree();
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
						else
						{
							_gamesQueueSize = _gamesQueueSize + 1;
						}
					}
					else if(_gamesQueue.get(i) != null && !_gamesQueue.get(i).isStarted())
					{
						//start new games
						Thread T = new Thread(_gamesQueue.get(i));
						T.start();
					}
				}
				//set up the games queue   				
				for(int i = 0; i < STADIUMS.length; i++)
				{
					if(!existNextOpponents(_nonClassBasedRegisters) && !existNextOpponents(getRandomClassList(_classBasedRegisters)))
					{
						break;
					}
					if(STADIUMS[i].isFreeToUse())
					{
						if(existNextOpponents(_nonClassBasedRegisters))
						{
							try
							{
								_olympiadInstances.put(i, new OlympiadGame(i, COMP_TYPE.NON_CLASSED, nextOpponents(_nonClassBasedRegisters), STADIUMS[i].getCoordinates()));
								_gamesQueue.put(i, new OlympiadGameTask(_olympiadInstances.get(i)));
								STADIUMS[i].setStadiaBusy();
							}
							catch(Exception ex)
							{
								if(Config.DEBUG)
									ex.printStackTrace();
								
								if(_olympiadInstances.get(i) != null)
								{
									for(L2PcInstance player : _olympiadInstances.get(i).getPlayers())
									{
										player.sendMessage("Your olympiad registration was canceled due to an error");
										player.setIsInOlympiadMode(false);
										player.setIsOlympiadStart(false);
										player.setOlympiadSide(-1);
										player.setOlympiadGameId(-1);
									}
									_olympiadInstances.remove(i);
								}
								if(_gamesQueue.get(i) != null)
								{
									_gamesQueue.remove(i);
								}
								STADIUMS[i].setStadiaFree();

								//try to reuse this stadia next time
								i--;
							}
						}
						else if(existNextOpponents(getRandomClassList(_classBasedRegisters)))
						{
							try
							{
								_olympiadInstances.put(i, new OlympiadGame(i, COMP_TYPE.CLASSED, nextOpponents(getRandomClassList(_classBasedRegisters)), STADIUMS[i].getCoordinates()));
								_gamesQueue.put(i, new OlympiadGameTask(_olympiadInstances.get(i)));
								STADIUMS[i].setStadiaBusy();
							}
							catch(Exception ex)
							{
								if(Config.DEBUG)
									ex.printStackTrace();
								
								if(_olympiadInstances.get(i) != null)
								{
									for(L2PcInstance player : _olympiadInstances.get(i).getPlayers())
									{
										player.sendMessage("Your olympiad registration was canceled due to an error");
										player.setIsInOlympiadMode(false);
										player.setIsOlympiadStart(false);
										player.setOlympiadSide(-1);
										player.setOlympiadGameId(-1);
									}
									_olympiadInstances.remove(i);
								}
								if(_gamesQueue.get(i) != null)
								{
									_gamesQueue.remove(i);
								}
								STADIUMS[i].setStadiaFree();

								//try to reuse this stadia next time
								i--;
							}
						}
					}
				}
				//wait 30 sec for !stress the server
				try
				{
					wait(30000);
				}
				catch(InterruptedException e)
				{
					if(Config.DEBUG)
						e.printStackTrace();
				}
			}
			//wait 30 sec for !stress the server
			try
			{
				wait(30000);
			}
			catch(InterruptedException e)
			{
				if(Config.DEBUG)
					e.printStackTrace();
			}
		}

		//when comp time finish wait for all games terminated before execute the cleanup code
		boolean allGamesTerminated = false;
		//wait for all games terminated
		while(!allGamesTerminated)
		{
			try
			{
				wait(30000);
			}
			catch(InterruptedException e)
			{
				if(Config.DEBUG)
					e.printStackTrace();
			}
			if(_gamesQueue.size() == 0)
			{
				allGamesTerminated = true;
			}
			else
			{
				for(OlympiadGameTask game : _gamesQueue.values())
				{
					allGamesTerminated = allGamesTerminated || game.isTerminated();
				}
			}
		}
		_cycleTerminated = true;
		//when all games terminated clear all 
		_gamesQueue.clear();
		/*_classBasedParticipants.clear();
		_nonClassBasedParticipants.clear();*/
		//Wait 20 seconds
		_olympiadInstances.clear();
		_classBasedRegisters.clear();
		_nonClassBasedRegisters.clear();

		_battleStarted = false;
		//_compStarted = false;
	}

	protected OlympiadGame getOlympiadInstance(int index)
	{
		if(_olympiadInstances != null && _olympiadInstances.size() > 0)
			return _olympiadInstances.get(index);
		return null;
	}

	@Override
	public Map<Integer, OlympiadGame> getOlympiadGames()
	{
		return _olympiadInstances == null ? null : _olympiadInstances;
	}

	private L2FastList<L2PcInstance> getRandomClassList(Map<Integer, L2FastList<L2PcInstance>> list)
	{
		if(list.size() == 0)
			return null;

		Map<Integer, L2FastList<L2PcInstance>> tmp = new HashMap<>();
		int tmpIndex = 0;
		for(L2FastList<L2PcInstance> l : list.values())
		{
			tmp.put(tmpIndex, l);
			tmpIndex++;
		}

		L2FastList<L2PcInstance> rndList;
		int classIndex = 0;
		if(tmp.size() == 1)
		{
			classIndex = 0;
		}
		else
		{
			classIndex = Rnd.nextInt(tmp.size());
		}
		rndList = tmp.get(classIndex);
		return rndList;
	}

	private L2FastList<L2PcInstance> nextOpponents(L2FastList<L2PcInstance> list)
	{
		L2FastList<L2PcInstance> opponents = new L2FastList<L2PcInstance>();
		if(list.size() == 0)
			return opponents;
		int loopCount = list.size() / 2;

		int first;
		int second;

		if(loopCount < 1)
			return opponents;

		first = Rnd.nextInt(list.size());
		opponents.add(list.get(first));
		list.remove(first);

		second = Rnd.nextInt(list.size());
		opponents.add(list.get(second));
		list.remove(second);

		return opponents;
	}

	private boolean existNextOpponents(L2FastList<L2PcInstance> list)
	{
		if(list == null)
			return false;
		if(list.size() == 0)
			return false;
		int loopCount = list.size() >> 1;

		if(loopCount < 1)
			return false;
		return true;
	}

	protected String[] getAllTitles()
	{
		
		String[] msg = new String[_olympiadInstances.size()];
		int count = 0;
		int showbattle = 0;

		for(OlympiadGame instance : _olympiadInstances.values())
		{
			if(instance._gamestarted == true)
			{
				showbattle = 1;
			}
			else
			{
				showbattle = 0;
			}
			msg[count] = "<" + showbattle + "><" + instance._stadiumID + "> In Progress " + instance.getTitle();
			count++;
		}
		return msg;
	}
}
