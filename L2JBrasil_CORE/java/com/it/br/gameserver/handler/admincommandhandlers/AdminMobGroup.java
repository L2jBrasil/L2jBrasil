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
package com.it.br.gameserver.handler.admincommandhandlers;

import com.it.br.Config;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;
import com.it.br.gameserver.network.serverpackets.SetupGauge;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.util.Broadcast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author littlecrow
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 * Admin commands handler for controllable mobs
 */
public class AdminMobGroup implements IAdminCommandHandler
{
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

    public AdminMobGroup()
    {
        admin.put("admin_mobmenu", Config.admin_mobmenu);
        admin.put("admin_mobgroup_list", Config.admin_mobgroup_list);
        admin.put("admin_mobgroup_create", Config.admin_mobgroup_create);
        admin.put("admin_mobgroup_remove", Config.admin_mobgroup_remove);
        admin.put("admin_mobgroup_delete", Config.admin_mobgroup_delete);
        admin.put("admin_mobgroup_idle", Config.admin_mobgroup_idle);
        admin.put("admin_mobgroup_attack", Config.admin_mobgroup_attack);
        admin.put("admin_mobgroup_rnd", Config.admin_mobgroup_rnd);
        admin.put("admin_mobgroup_return", Config.admin_mobgroup_return);
        admin.put("admin_mobgroup_follow", Config.admin_mobgroup_follow);
        admin.put("admin_mobgroup_casting", Config.admin_mobgroup_casting);
        admin.put("admin_mobgroup_nomove", Config.admin_mobgroup_nomove);
        admin.put("admin_mobgroup_attackgrp", Config.admin_mobgroup_attackgrp);
        admin.put("admin_mobgroup_invul", Config.admin_mobgroup_invul);
        admin.put("admin_mobinst", Config.admin_mobinst);
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

		GMAudit.auditGMAction(activeChar.getName(), command, "", "");
		if (command.equals("admin_mobmenu"))
		{
			showMainPage(activeChar,command);
			return true;
		}
		else if (command.equals("admin_mobinst"))
		{
			showMainPage(activeChar,command);
			return true;
		}
		else if (command.equals("admin_mobgroup_list"))
			showGroupList(activeChar);
		else if (command.startsWith("admin_mobgroup_create"))
			createGroup(command, activeChar);
		else if (command.startsWith("admin_mobgroup_delete") ||
				command.startsWith("admin_mobgroup_remove"))
			removeGroup(command, activeChar);
		else if (command.startsWith("admin_mobgroup_spawn"))
			spawnGroup(command, activeChar);
		else if (command.startsWith("admin_mobgroup_unspawn"))
			unspawnGroup(command, activeChar);
		else if (command.startsWith("admin_mobgroup_kill"))
			killGroup(command, activeChar);
		else if (command.startsWith("admin_mobgroup_attackgrp"))
			attackGrp(command, activeChar);
		else if (command.startsWith("admin_mobgroup_attack"))
		{
			if (activeChar.getTarget() instanceof L2Character)
			{
				L2Character target = (L2Character) activeChar.getTarget();
				attack(command, activeChar, target);
			}
		}
		else if (command.startsWith("admin_mobgroup_rnd"))
			setNormal(command, activeChar);
		else if (command.startsWith("admin_mobgroup_idle"))
			idle(command, activeChar);
		else if (command.startsWith("admin_mobgroup_return"))
			returnToChar(command, activeChar);
		else if (command.startsWith("admin_mobgroup_follow"))
			follow(command, activeChar, activeChar);
		else if (command.startsWith("admin_mobgroup_casting"))
			setCasting(command, activeChar);
		else if (command.startsWith("admin_mobgroup_nomove"))
			noMove(command, activeChar);
		else if (command.startsWith("admin_mobgroup_invul"))
			invul(command, activeChar);
		else if (command.startsWith("admin_mobgroup_teleport"))
			teleportGroup(command, activeChar);
		showMainPage(activeChar,command);
		return true;
	}

	/**
	 * @param activeChar
	 */
	private void showMainPage(L2PcInstance activeChar, String command)
	{
		String filename = "mobgroup.htm";
		if (command.contains("mobinst"))
			filename = "mobgrouphelp.htm";
		AdminHelpPage.showHelpPage(activeChar, filename);
	}

	private void returnToChar(String command, L2PcInstance activeChar)
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			activeChar.sendMessage("Incorrect command arguments.");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}
		group.returnGroup(activeChar);
	}

	private void idle(String command, L2PcInstance activeChar)
	{
		int groupId;

		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			activeChar.sendMessage("Incorrect command arguments.");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}
		group.setIdleMode();
	}

	private void setNormal(String command, L2PcInstance activeChar)
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e) {
			activeChar.sendMessage("Incorrect command arguments.");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}
		group.setAttackRandom();
	}

	private void attack(String command, L2PcInstance activeChar, L2Character target)
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			activeChar.sendMessage("Incorrect command arguments.");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}
		group.setAttackTarget(target);
	}

	private void follow(String command, L2PcInstance activeChar, L2Character target)
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			activeChar.sendMessage("Incorrect command arguments.");
			return;
		}
		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}
		group.setFollowMode(target);
	}

	private void createGroup(String command, L2PcInstance activeChar)
	{
		int groupId;
		int templateId;
		int mobCount;

		try {
			String[] cmdParams = command.split(" ");

			groupId = Integer.parseInt(cmdParams[1]);
			templateId = Integer.parseInt(cmdParams[2]);
			mobCount = Integer.parseInt(cmdParams[3]);
		}
		catch (Exception e) {
			activeChar.sendMessage("Usage: //mobgroup_create <group> <npcid> <count>");
			return;
		}

		if (MobGroupTable.getInstance().getGroup(groupId) != null)
		{
			activeChar.sendMessage("Mob group " + groupId + " already exists.");
			return;
		}

		L2NpcTemplate template = NpcTable.getInstance().getTemplate(templateId);

		if (template == null)
		{
			activeChar.sendMessage("Invalid NPC ID specified.");
			return;
		}

		MobGroup group = new MobGroup(groupId, template, mobCount);
		MobGroupTable.getInstance().addGroup(groupId, group);

		activeChar.sendMessage("Mob group " + groupId + " created.");
	}

	private void removeGroup(String command, L2PcInstance activeChar)
	{
		int groupId;

		try {
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e) {
			activeChar.sendMessage("Usage: //mobgroup_remove <groupId>");
			return;
		}

		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);

		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}

		doAnimation(activeChar);
		group.unspawnGroup();

		if (MobGroupTable.getInstance().removeGroup(groupId))
			activeChar.sendMessage("Mob group " + groupId + " unspawned and removed.");
	}

	private void spawnGroup(String command, L2PcInstance activeChar)
	{
		int groupId;
		boolean topos = false;
		int posx = 0;
		int posy = 0;
		int posz = 0;

		try {
			String[] cmdParams = command.split(" ");
			groupId = Integer.parseInt(cmdParams[1]);

			try { // we try to get a position
				posx = Integer.parseInt(cmdParams[2]);
				posy = Integer.parseInt(cmdParams[3]);
				posz = Integer.parseInt(cmdParams[4]);
				topos = true;
			}
			catch (Exception e) {
				// no position given
			}
		}
		catch (Exception e) {
			activeChar.sendMessage("Usage: //mobgroup_spawn <group> [ x y z ]");
			return;
		}

		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);

		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}

		doAnimation(activeChar);

		if (topos)
			group.spawnGroup(posx, posy, posz);
		else
			group.spawnGroup(activeChar);

		activeChar.sendMessage("Mob group " + groupId + " spawned.");
	}

	private void unspawnGroup(String command, L2PcInstance activeChar)
	{
		int groupId;

		try {
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e) {
			activeChar.sendMessage("Usage: //mobgroup_unspawn <groupId>");
			return;
		}

		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);

		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}

		doAnimation(activeChar);
		group.unspawnGroup();

		activeChar.sendMessage("Mob group " + groupId + " unspawned.");
	}

	private void killGroup(String command, L2PcInstance activeChar)
	{
		int groupId;

		try {
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e) {
			activeChar.sendMessage("Usage: //mobgroup_kill <groupId>");
			return;
		}

		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);

		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}

		doAnimation(activeChar);
		group.killGroup(activeChar);
	}

	private void setCasting(String command, L2PcInstance activeChar)
	{
		int groupId;

		try {
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e) {
			activeChar.sendMessage("Usage: //mobgroup_casting <groupId>");
			return;
		}

		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);

		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}

		group.setCastMode();
	}

	private void noMove(String command, L2PcInstance activeChar)
	{
		int groupId;
		String enabled;

		try {
			groupId = Integer.parseInt(command.split(" ")[1]);
			enabled = command.split(" ")[2];
		}
		catch (Exception e) {
			activeChar.sendMessage("Usage: //mobgroup_nomove <groupId> <on|off>");
			return;
		}

		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);

		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}

		if (enabled.equalsIgnoreCase("on") || enabled.equalsIgnoreCase("true"))
			group.setNoMoveMode(true);
		else if (enabled.equalsIgnoreCase("off") || enabled.equalsIgnoreCase("false"))
			group.setNoMoveMode(false);
		else
			activeChar.sendMessage("Incorrect command arguments.");
	}

	private void doAnimation(L2PcInstance activeChar)
	{
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUser(activeChar, 1008, 1, 4000, 0), 2250000/*1500*/);
		activeChar.sendPacket(new SetupGauge(0, 4000));
	}

	private void attackGrp(String command, L2PcInstance activeChar)
	{
		int groupId;
		int othGroupId;

		try {
			groupId = Integer.parseInt(command.split(" ")[1]);
			othGroupId = Integer.parseInt(command.split(" ")[2]);
		}
		catch (Exception e) {
			activeChar.sendMessage("Usage: //mobgroup_attackgrp <groupId> <TargetGroupId>");
			return;
		}

		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);

		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}

		MobGroup othGroup = MobGroupTable.getInstance().getGroup(othGroupId);

		if (othGroup == null)
		{
			activeChar.sendMessage("Incorrect target group.");
			return;
		}

		group.setAttackGroup(othGroup);
	}

	private void invul(String command, L2PcInstance activeChar)
	{
		int groupId;
		String enabled;

		try {
			groupId = Integer.parseInt(command.split(" ")[1]);
			enabled = command.split(" ")[2];
		}
		catch (Exception e) {
			activeChar.sendMessage("Usage: //mobgroup_invul <groupId> <on|off>");
			return;
		}

		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);

		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}

		if (enabled.equalsIgnoreCase("on") || enabled.equalsIgnoreCase("true"))
			group.setInvul(true);
		else if (enabled.equalsIgnoreCase("off") || enabled.equalsIgnoreCase("false"))
			group.setInvul(false);
		else
			activeChar.sendMessage("Incorrect command arguments.");
	}

	private void teleportGroup(String command, L2PcInstance activeChar)
	{
		int groupId;
		String targetPlayerStr = null;
		L2PcInstance targetPlayer = null;

		try {
			groupId = Integer.parseInt(command.split(" ")[1]);
			targetPlayerStr = command.split(" ")[2];

			if (targetPlayerStr != null)
				targetPlayer = L2World.getInstance().getPlayer(targetPlayerStr);

			if (targetPlayer == null)
				targetPlayer = activeChar;
		}
		catch (Exception e) {
			activeChar.sendMessage("Usage: //mobgroup_teleport <groupId> [playerName]");
			return;
		}

		MobGroup group = MobGroupTable.getInstance().getGroup(groupId);

		if (group == null)
		{
			activeChar.sendMessage("Invalid group specified.");
			return;
		}

		group.teleportGroup(activeChar);
	}

	private void showGroupList(L2PcInstance activeChar)
	{
		MobGroup[] mobGroupList = MobGroupTable.getInstance().getGroups();

		activeChar.sendMessage("======= <Mob Groups> =======");

		for (MobGroup mobGroup : mobGroupList)
			activeChar.sendMessage(mobGroup.getGroupId() + ": " + mobGroup.getActiveMobCount() + " alive out of " +  mobGroup.getMaxMobCount() +
					" of NPC ID " + mobGroup.getTemplate().npcId + " (" + mobGroup.getStatus() + ")");

		activeChar.sendPacket(new SystemMessage(SystemMessageId.FRIEND_LIST_FOOT));
	}
}