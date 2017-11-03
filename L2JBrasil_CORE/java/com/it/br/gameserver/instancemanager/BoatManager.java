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
package com.it.br.gameserver.instancemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.model.actor.instance.L2BoatInstance;
import com.it.br.gameserver.templates.L2CharTemplate;
import com.it.br.gameserver.templates.StatsSet;




public class BoatManager
{
	private static final Logger _log = Logger.getLogger(BoatManager.class.getName());

    // =========================================================
	private static BoatManager _instance;
	public static final BoatManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new BoatManager();
			_instance.load();
		}
		return _instance;
	}
    // =========================================================


    // =========================================================
    // Data Field
	private Map<Integer,L2BoatInstance> _staticItems = new HashMap<>();
	@SuppressWarnings("unused")
	private boolean _initialized;

	// =========================================================
    // Constructor
	public BoatManager()
	{
	}

	// =========================================================
    // Method - Private
	private final void load()
	{
		_initialized = true;
		if(!Config.ALLOW_BOAT)
		{
			_initialized = false;
			return;
		}
		LineNumberReader lnr = null;
		try
		{
			File doorData = new File(Config.DATAPACK_ROOT, "data/csv/boat.csv");
			lnr = new LineNumberReader(new BufferedReader(new FileReader(doorData)));

			String line = null;
			while ((line = lnr.readLine()) != null)
			{
				if (line.trim().length() == 0 || line.startsWith("#"))
					continue;
				L2BoatInstance boat = parseLine(line);
				boat.spawn();
				_staticItems.put(boat.getObjectId(), boat);
				if(Config.DEBUG)
				{
				System.out.println("Boat ID : " + boat.getObjectId());
				}
			}
		}
		catch (FileNotFoundException e)
		{
			_initialized = false;
			_log.warning("boat.csv is missing in data folder");
		}
		catch (Exception e)
		{
			_initialized = false;
			_log.warning("error while creating boat table " + e);
			e.printStackTrace();
		}
		finally
		{
			try { lnr.close(); } catch (Exception e1) { /* ignore problems */ }
		}
	}

	/**
	 * @param line
	 * @return
	 */
	private L2BoatInstance parseLine(String line)
	{
		L2BoatInstance boat;
		StringTokenizer st = new StringTokenizer(line, ";");

		String name = st.nextToken();
		int id = Integer.parseInt(st.nextToken());
		int xspawn = Integer.parseInt(st.nextToken());
		int yspawn = Integer.parseInt(st.nextToken());
		int zspawn = Integer.parseInt(st.nextToken());
		int heading = Integer.parseInt(st.nextToken());

		StatsSet npcDat = new StatsSet();
		npcDat.set("npcId", id);
		npcDat.set("level", 0);
		npcDat.set("jClass", "boat");

		npcDat.set("baseSTR", 0);
		npcDat.set("baseCON", 0);
		npcDat.set("baseDEX", 0);
		npcDat.set("baseINT", 0);
		npcDat.set("baseWIT", 0);
		npcDat.set("baseMEN", 0);

		npcDat.set("baseShldDef", 0);
		npcDat.set("baseShldRate", 0);
		npcDat.set("baseAccCombat", 38);
		npcDat.set("baseEvasRate",  38);
		npcDat.set("baseCritRate",  38);

		//npcDat.set("name", "");
		npcDat.set("collision_radius", 0);
		npcDat.set("collision_height", 0);
		npcDat.set("sex", "male");
		npcDat.set("type", "");
		npcDat.set("baseAtkRange", 0);
        npcDat.set("baseMpMax", 0);
        npcDat.set("baseCpMax", 0);
		npcDat.set("rewardExp", 0);
		npcDat.set("rewardSp", 0);
		npcDat.set("basePAtk", 0);
		npcDat.set("baseMAtk", 0);
		npcDat.set("basePAtkSpd", 0);
		npcDat.set("aggroRange", 0);
		npcDat.set("baseMAtkSpd", 0);
		npcDat.set("rhand", 0);
		npcDat.set("lhand", 0);
		npcDat.set("armor", 0);
		npcDat.set("baseWalkSpd", 0);
		npcDat.set("baseRunSpd", 0);
		npcDat.set("name", name);
		npcDat.set("baseHpMax", 50000);
		npcDat.set("baseHpReg", 3.e-3f);
		npcDat.set("baseMpReg", 3.e-3f);
		npcDat.set("basePDef", 100);
		npcDat.set("baseMDef", 100);
		L2CharTemplate template = new L2CharTemplate(npcDat);
		boat = new L2BoatInstance(IdFactory.getInstance().getNextId(),template,name);
		boat.getPosition().setHeading(heading);
		boat.setXYZ(xspawn,yspawn,zspawn);
		//boat.spawnMe();

		int IdWaypoint1 = Integer.parseInt(st.nextToken());
		int IdWTicket1 = Integer.parseInt(st.nextToken());
		int ntx1 = Integer.parseInt(st.nextToken());
		int nty1 = Integer.parseInt(st.nextToken());
		int ntz1 = Integer.parseInt(st.nextToken());
		String npc1 = st.nextToken();
		String mess10_1 = st.nextToken();
		String mess5_1 = st.nextToken();
		String mess1_1 = st.nextToken();
		String mess0_1 = st.nextToken();
		String messb_1 = st.nextToken();
		boat.setTrajet1(IdWaypoint1,IdWTicket1,ntx1,nty1,ntz1,npc1,mess10_1,mess5_1,mess1_1,mess0_1,messb_1);
		IdWaypoint1 = Integer.parseInt(st.nextToken());
		IdWTicket1 = Integer.parseInt(st.nextToken());
		ntx1 = Integer.parseInt(st.nextToken());
		nty1 = Integer.parseInt(st.nextToken());
		ntz1 = Integer.parseInt(st.nextToken());
		npc1 = st.nextToken();
		mess10_1 = st.nextToken();
		mess5_1 = st.nextToken();
		mess1_1 = st.nextToken();
		mess0_1 = st.nextToken();
		messb_1 = st.nextToken();
		boat.setTrajet2(IdWaypoint1,IdWTicket1,ntx1,nty1,ntz1,npc1,mess10_1,mess5_1,mess1_1,mess0_1,messb_1);
		return boat;
	}

	// =========================================================
    // Property - Public
	/**
	 * @param boatId
	 * @return
	 */
	public L2BoatInstance GetBoat(int boatId)
	{
		if (_staticItems == null) _staticItems = new HashMap<>();
		return _staticItems.get(boatId);
	}
}