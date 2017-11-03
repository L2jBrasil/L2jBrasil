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
 
package com.it.br.gameserver.instancemanager.clanhallsiege;

import java.util.*;
import java.util.logging.Logger;

import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.instancemanager.ClanHallManager;
import com.it.br.gameserver.instancemanager.ClanHallSiege;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.entity.ClanHall;
import com.it.br.gameserver.taskmanager.ExclusiveTask;
import com.it.br.gameserver.templates.L2NpcTemplate;

public class FortResistSiege extends ClanHallSiege
{
	private static final Logger _log = Logger.getLogger(FortResistSiege.class.getName());
	private static FortResistSiege _instance;
	private List<L2Spawn> _questMobs = new ArrayList<>();
	private int	_npcSpawnCount = 0;
	private Map<Integer, DamageInfo> _clansDamageInfo = new HashMap<Integer, DamageInfo>();
	
	public static final FortResistSiege load()
	{
		_log.info("- Fortress Of Resistence");
		if (_instance == null)
		{
			_instance = new FortResistSiege();
		}
		return _instance;
	}

	public static final FortResistSiege getInstance()
	{
		if (_instance == null)
		{
			_instance = new FortResistSiege();
		}
		return _instance;
	}

	private FortResistSiege()
	{
		long siegeDate = restoreSiegeDate(21);
		Calendar tmpDate = Calendar.getInstance();
		tmpDate.setTimeInMillis(siegeDate);
		setSiegeDate(tmpDate);
		setNewSiegeDate(siegeDate,21,22);
		// Schedule siege auto start
		_startSiegeTask.schedule(1000);
	}

	private final ExclusiveTask _endSiegeTask = new ExclusiveTask() {
	
		@Override
		protected void onElapsed()
		{
			if (!getIsInProgress())
			{
				cancel();
				return;
			}
			final long timeRemaining = _siegeEndDate.getTimeInMillis() - System.currentTimeMillis();
			if (timeRemaining <= 0)
			{
				endSiege(false);
				cancel();
				return;
			}
			schedule(timeRemaining);
		}
	};

	private final ExclusiveTask _startSiegeTask = new ExclusiveTask(){
	
		@Override
		protected void onElapsed()
		{
			if (getIsInProgress())
			{
				cancel();
				return;
			}

			final long timeRemaining = getSiegeDate().getTimeInMillis() - System.currentTimeMillis();
			if (timeRemaining <= 0)
			{
				startSiege();
				cancel();
				return;
			}
			
			schedule(timeRemaining);
		}
	};

	public void startSiege()
	{
		setIsInProgress(true);
		_clansDamageInfo.clear();
		
		for (L2Spawn spawn : _questMobs)
		{
			if (spawn != null)
			{
				spawn.init();
			}
		}
		
		_siegeEndDate = Calendar.getInstance();
		_siegeEndDate.add(Calendar.MINUTE, 30);
		_endSiegeTask.schedule(1000);
	}

	public void endSiege(boolean type)
	{
		setIsInProgress(false);
		
		for (L2Spawn spawn : _questMobs)
		{
			if (spawn == null)
			{
				continue;
			}

			spawn.stopRespawn();
			if (spawn.getLastSpawn() != null)
			{
				spawn.getLastSpawn().doDie(spawn.getLastSpawn());
			}
		}
		
		if (type == true)
		{
			L2Clan clanIdMaxDamage = null;
			long tempMaxDamage = 0;
			
			for (DamageInfo damageInfo : _clansDamageInfo.values())
			{
				if (damageInfo != null )
				{
					if (damageInfo._damage > tempMaxDamage && damageInfo._clan.getHasHideout() == 0)
					{
						tempMaxDamage = damageInfo._damage;
						clanIdMaxDamage = damageInfo._clan;
					}
				}
			}
			
			if (clanIdMaxDamage != null)
			{
				ClanHall clanhall = null;
				clanhall = ClanHallManager.getInstance().getClanHallById(21);
				ClanHallManager.getInstance().setOwner(clanhall.getId(), clanIdMaxDamage);
			}
		}
		setNewSiegeDate(getSiegeDate().getTimeInMillis(),21,22);
		_startSiegeTask.schedule(1000);
	}

	public void addSiegeMob(int npcTemplate,int locx,int locy,int locz,int resp)
	{
		try
		{
			final L2NpcTemplate template1 = NpcTable.getInstance().getTemplate(npcTemplate);
			if (template1 != null)
			{
				_npcSpawnCount++;
				final L2Spawn spawn1 = new L2Spawn(template1);
				spawn1.setId(_npcSpawnCount);
				spawn1.setAmount(1);
				spawn1.setLocx(locx);
				spawn1.setLocy(locy);
				spawn1.setLocz(locz);
				spawn1.setHeading(0);
				spawn1.setRespawnDelay(resp);
				spawn1.setLocation(0);
				_questMobs.add(spawn1);
			}
		}
		catch (Exception e)
		{
		}
	}

	@SuppressWarnings("synthetic-access")
	public void addSiegeDamage(L2Clan clan,long damage)
	{
		DamageInfo clanDamage = _clansDamageInfo.get(clan.getClanId());
		if (clanDamage != null)
		{
			clanDamage._damage += damage;
		}
		else
		{
			clanDamage = new DamageInfo();
			clanDamage._clan = clan;
			clanDamage._damage += damage;

			_clansDamageInfo.put(clan.getClanId(), clanDamage);
		}
	}

	private class DamageInfo
	{
		public L2Clan _clan;
		public long _damage;
	}

}