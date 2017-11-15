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
package com.it.br.gameserver.handler.admincommandhandlers;

import java.util.*;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.GmListTable;
import com.it.br.gameserver.datatables.sql.SpawnTable;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.datatables.xml.TeleportLocationTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.instancemanager.DayNightSpawnManager;
import com.it.br.gameserver.instancemanager.RaidBossSpawnManager;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2NpcTemplate;

/**
 * This class handles following admin commands: - show_spawns = shows menu -
 * spawn_index lvl = shows menu for monsters with respective level -
 * spawn_monster id = spawns monster id on target
 *
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminSpawn implements IAdminCommandHandler
{
    public static Logger _log = Logger.getLogger(AdminSpawn.class.getName());
    private static Map<String, Integer> admin = new HashMap<>();

    private boolean checkPermission(String command, L2PcInstance activeChar)
    {
        if (!Config.ALT_PRIVILEGES_ADMIN)
            if (!(checkLevel(command, activeChar.getAccessLevel()) && activeChar.isGM()))
            {
                activeChar.sendMessage("E necessario ter Access Level " + admin.get(command) + " para usar o comando : " + command);
                return true;
            }
        return false;
    }

    private boolean checkLevel(String command, int level)
    {
        Integer requiredAcess = admin.get(command);
        return (level >= requiredAcess);
    }

    public AdminSpawn()
    {
        admin.put("admin_show_spawns", Config.admin_show_spawns);
        admin.put("admin_spawn", Config.admin_spawn);
        admin.put("admin_spawn_monster", Config.admin_spawn_monster);
        admin.put("admin_spawn_index", Config.admin_spawn_index);
        admin.put("admin_unspawnall", Config.admin_unspawnall);
        admin.put("admin_respawnall", Config.admin_respawnall);
        admin.put("admin_spawn_reload", Config.admin_spawn_reload);
        admin.put("admin_npc_index", Config.admin_npc_index);
        admin.put("admin_spawn_once", Config.admin_spawn_once);
        admin.put("admin_show_npcs", Config.admin_show_npcs);
        admin.put("admin_teleport_reload", Config.admin_teleport_reload);
        admin.put("admin_spawnnight", Config.admin_spawnnight);
        admin.put("admin_spawnday", Config.admin_spawnday);
    }

    public Set<String> getAdminCommandList()
    {
        return admin.keySet();
    }

    public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        StringTokenizer st = new StringTokenizer(command);
        String commandName = st.nextToken();

        if(checkPermission(commandName, activeChar)) return false;

		if (command.equals("admin_show_spawns"))
			AdminHelpPage.showHelpPage(activeChar, "spawns.htm");
		else if (command.startsWith("admin_spawn_index"))
		{
			try
			{
				int level = Integer.parseInt(st.nextToken());
				int from = 0;
				try
				{
					from = Integer.parseInt(st.nextToken());
				}
				catch (NoSuchElementException nsee) {}
				showMonsters(activeChar, level, from);
			}
			catch (Exception e)
			{
				AdminHelpPage.showHelpPage(activeChar, "spawns.htm");
			}
		}
		else if (command.equals("admin_show_npcs"))
			AdminHelpPage.showHelpPage(activeChar, "npcs.htm");
		else if (command.startsWith("admin_npc_index"))
		{
			try
			{
				String letter = st.nextToken();
				int from = 0;
				try
				{
					from = Integer.parseInt(st.nextToken());
				}
				catch (NoSuchElementException nsee) {}
				showNpcs(activeChar, letter, from);
			}
			catch (Exception e)
			{
				AdminHelpPage.showHelpPage(activeChar, "npcs.htm");
			}
		}
		else if (command.startsWith("admin_spawn")|| command.startsWith("admin_spawn_monster"))
		{
			try
			{
				String id = st.nextToken();
				int respawnTime = 0;
				int mobCount = 1;
				if (st.hasMoreTokens())
					mobCount = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
					respawnTime = Integer.parseInt(st.nextToken());
				if (commandName.equalsIgnoreCase("admin_spawn_once"))
					spawnMonster(activeChar, id, respawnTime, mobCount,false);
				else
					spawnMonster(activeChar, id, respawnTime, mobCount,true);
			}
			catch (Exception e)
			{	// Case of wrong or missing monster data
				AdminHelpPage.showHelpPage(activeChar, "spawns.htm");
			}
		}
		else if (command.startsWith("admin_unspawnall"))
		{
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
				player.sendPacket(new SystemMessage(SystemMessageId.NPC_SERVER_NOT_OPERATING));
			RaidBossSpawnManager.getInstance().cleanUp();
			DayNightSpawnManager.getInstance().cleanUp();
			L2World.getInstance().deleteVisibleNpcSpawns();
			GmListTable.broadcastMessageToGMs("NPC Unspawn completed!");
		}
		else if (command.startsWith("admin_spawnday"))
			DayNightSpawnManager.getInstance().spawnDayCreatures();
		else if (command.startsWith("admin_spawnnight"))
			DayNightSpawnManager.getInstance().spawnNightCreatures();
		else if (command.startsWith("admin_respawnall") || command.startsWith("admin_spawn_reload"))
		{
			// make sure all spawns are deleted
			RaidBossSpawnManager.getInstance().cleanUp();
			DayNightSpawnManager.getInstance().cleanUp();
			L2World.getInstance().deleteVisibleNpcSpawns();
			// now respawn all
			NpcTable.getInstance().reloadAllNpc();
			SpawnTable.getInstance().reloadAll();
			RaidBossSpawnManager.getInstance().reloadBosses();
			GmListTable.broadcastMessageToGMs("NPC Respawn completed!");
		}
		else if (command.startsWith("admin_teleport_reload"))
		{
			TeleportLocationTable.reloadAll();
			GmListTable.broadcastMessageToGMs("Teleport List Table reloaded.");
		}
		return true;
	}

	private void spawnMonster(L2PcInstance activeChar, String monsterId, int respawnTime, int mobCount,boolean permanent)
	{
		L2Object target = activeChar.getTarget();
		if (target == null)
			target = activeChar;

		L2NpcTemplate template1;
		if (monsterId.matches("[0-9]*"))
		{
			//First parameter was an ID number
			int monsterTemplate = Integer.parseInt(monsterId);
			template1 = NpcTable.getInstance().getTemplate(monsterTemplate);
		}
		else
		{
			//First parameter wasn't just numbers so go by name not ID
			monsterId = monsterId.replace('_', ' ');
			template1 = NpcTable.getInstance().getTemplateByName(monsterId);
		}

		try
		{
			L2Spawn spawn = new L2Spawn(template1);
			spawn.setLocx(target.getX());
			spawn.setLocy(target.getY());
			spawn.setLocz(target.getZ());
			spawn.setAmount(mobCount);
			spawn.setHeading(activeChar.getHeading());
			spawn.setRespawnDelay(respawnTime);
			if (RaidBossSpawnManager.getInstance().isDefined(spawn.getNpcid()))
				activeChar.sendMessage("You cannot spawn another instance of " + template1.name + ".");
			else
			{
				if (RaidBossSpawnManager.getInstance().getValidTemplate(spawn.getNpcid()) != null)
					RaidBossSpawnManager.getInstance().addNewSpawn(spawn, 0, template1.getStatsSet().getDouble("baseHpMax"), template1.getStatsSet().getDouble("baseMpMax"), permanent);
				else
					SpawnTable.getInstance().addNewSpawn(spawn, permanent);
				spawn.init();
				if (!permanent)
					spawn.stopRespawn();
				activeChar.sendMessage("Created " + template1.name + " on " + target.getObjectId());
			}
		}
		catch (Exception e)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_CANT_FOUND));
		}
	}

	private void showMonsters(L2PcInstance activeChar, int level, int from)
	{
		StringBuilder tb = new StringBuilder();

		L2NpcTemplate[] mobs = NpcTable.getInstance().getAllMonstersOfLevel(level);

		// Start
		tb.append("<html><title>Spawn Monster:</title><body><p> Level "+level+":<br>Total Npc's : "+mobs.length+"<br>");
		String end1 = "<br><center><button value=\"Next\" action=\"bypass -h admin_spawn_index "+level+" $from$\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center></body></html>";
		String end2 = "<br><center><button value=\"Back\" action=\"bypass -h admin_show_spawns\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center></body></html>";

		// Loop
		boolean ended = true;
		for (int i=from; i<mobs.length; i++)
		{
			String txt = "<a action=\"bypass -h admin_spawn_monster "+mobs[i].npcId+"\">"+mobs[i].name+"</a><br1>";

			if ((tb.length() + txt.length() + end2.length()) > 8192)
			{
				end1 = end1.replace("$from$", ""+i);
				ended = false;
				break;
			}

			tb.append(txt);
		}

		// End
		if (ended)
			tb.append(end2);
		else
			tb.append(end1);

		activeChar.sendPacket(new NpcHtmlMessage(5, tb.toString()));
	}

	private void showNpcs(L2PcInstance activeChar, String starting, int from)
	{
		StringBuilder tb = new StringBuilder();
		L2NpcTemplate[] mobs = NpcTable.getInstance().getAllNpcStartingWith(starting);
		// Start
		tb.append("<html><title>Spawn Monster:</title><body><p> There are "+mobs.length+" Npcs whose name starts with "+starting+":<br>");
		String end1 = "<br><center><button value=\"Next\" action=\"bypass -h admin_npc_index "+starting+" $from$\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center></body></html>";
		String end2 = "<br><center><button value=\"Back\" action=\"bypass -h admin_show_npcs\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center></body></html>";
		// Loop
		boolean ended = true;
		for (int i=from; i<mobs.length; i++)
		{
			String txt = "<a action=\"bypass -h admin_spawn_monster "+mobs[i].npcId+"\">"+mobs[i].name+"</a><br1>";

			if ((tb.length() + txt.length() + end2.length()) > 8192)
			{
				end1 = end1.replace("$from$", ""+i);
				ended = false;
				break;
			}
			tb.append(txt);
		}
		// End
		if (ended)
			tb.append(end2);
		else
			tb.append(end1);
		activeChar.sendPacket(new NpcHtmlMessage(5, tb.toString()));
	}
}
