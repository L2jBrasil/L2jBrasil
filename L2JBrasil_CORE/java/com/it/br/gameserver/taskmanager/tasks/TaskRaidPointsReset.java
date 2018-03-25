/* This program is free software; you can redistribute it and/or modify
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
package com.it.br.gameserver.taskmanager.tasks;

import com.it.br.Config;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.instancemanager.RaidBossPointsManager;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.taskmanager.Task;
import com.it.br.gameserver.taskmanager.TaskManager;
import com.it.br.gameserver.taskmanager.TaskManager.ExecutedTask;
import com.it.br.gameserver.taskmanager.TaskTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Map;

public class TaskRaidPointsReset extends Task
{
	private static final Logger _log	= LoggerFactory.getLogger(TaskRaidPointsReset.class);
	public static final	String	NAME	= "raid_points_reset";


	@Override
	public String getName()
	{
		return NAME;
	}


	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		String playerName = "";
		Calendar cal = Calendar.getInstance();

		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
		{
			// reward clan reputation points
			Map<Integer, Integer> rankList = RaidBossPointsManager.getRankList();
			for (L2Clan c : ClanTable.getInstance().getClans())
			{
				for (Map.Entry<Integer, Integer> entry : rankList.entrySet())
				{
					L2Object obj = L2World.getInstance().findObject(entry.getKey());
					if (obj instanceof L2PcInstance)
						playerName = ((L2PcInstance) obj).getName();					
					if (entry.getValue() <= 100 && c.isMember(playerName))
					{
						int reputation = 0;
						switch (entry.getValue())
						{
							case 1:
								reputation = Config.RAID_RANKING_1ST;
								break;
							case 2:
								reputation = Config.RAID_RANKING_2ND;
								break;
							case 3:
								reputation = Config.RAID_RANKING_3RD;
								break;
							case 4:
								reputation = Config.RAID_RANKING_4TH;
								break;
							case 5:
								reputation = Config.RAID_RANKING_5TH;
								break;
							case 6:
								reputation = Config.RAID_RANKING_6TH;
								break;
							case 7:
								reputation = Config.RAID_RANKING_7TH;
								break;
							case 8:
								reputation = Config.RAID_RANKING_8TH;
								break;
							case 9:
								reputation = Config.RAID_RANKING_9TH;
								break;
							case 10:
								reputation = Config.RAID_RANKING_10TH;
								break;
							default:
								if (entry.getValue() <= 50)
									reputation = Config.RAID_RANKING_UP_TO_50TH;
								else
									reputation = Config.RAID_RANKING_UP_TO_100TH;
								break;
						}
						c.setReputationScore(c.getReputationScore() + reputation, true);
					}
				}
			}

			RaidBossPointsManager.cleanUp();
			_log.info("Raid Points Reset Global Task: launched.");
		}
	}


	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "00:10:00", "");
	}
}