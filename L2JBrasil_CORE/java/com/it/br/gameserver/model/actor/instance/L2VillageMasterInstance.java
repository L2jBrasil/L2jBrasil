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
package com.it.br.gameserver.model.actor.instance;

import com.it.br.Config;
import com.it.br.gameserver.Shutdown;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.datatables.xml.CharTemplateTable;
import com.it.br.gameserver.datatables.xml.SkillTreeTable;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.instancemanager.SiegeManager;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.L2Clan.SubPledge;
import com.it.br.gameserver.model.Olympiad.Olympiad;
import com.it.br.gameserver.model.base.*;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.*;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.util.Util;

import java.util.Iterator;
import java.util.Set;

public final class L2VillageMasterInstance extends L2FolkInstance
{

	public L2VillageMasterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public void onBypassFeedback(L2PcInstance player, String command)
	{
		String[] commandStr = command.split(" ");
		String actualCommand = commandStr[0]; // Get actual command
		String cmdParams = "";
		String cmdParams2 = "";
		if (commandStr.length >= 2)
			cmdParams = commandStr[1];
		if (commandStr.length >= 3)
			cmdParams2 = commandStr[2];
		if(player.isLearningSkill() || player.isLocked())
		{
	   		player.sendMessage("You cannot use village master now..");
    		return;
	   	}
		if (actualCommand.equalsIgnoreCase("create_clan"))
		{
			if (cmdParams.equals(""))
				return;
			ClanTable.getInstance().createClan(player, cmdParams);
		}
		else if (actualCommand.equalsIgnoreCase("create_academy"))
		{
			if (cmdParams.equals(""))
				return;
			createSubPledge(player, cmdParams, null, L2Clan.SUBUNIT_ACADEMY, 5);
		}
		else if (actualCommand.equalsIgnoreCase("create_royal"))
		{
			if (cmdParams.equals(""))
				return;
			createSubPledge(player, cmdParams, cmdParams2, L2Clan.SUBUNIT_ROYAL1, 6);
        } 
        else if (actualCommand.equalsIgnoreCase("assign_subpl_leader")) 
        {
            if (cmdParams.equals("")) return; 

            assignSubPledgeLeader(player, cmdParams, cmdParams2); 
        } 
        else if (actualCommand.equalsIgnoreCase("rename_royal1") || 
                    actualCommand.equalsIgnoreCase("rename_royal2") || 
                    actualCommand.equalsIgnoreCase("rename_knights1") || 
                    actualCommand.equalsIgnoreCase("rename_knights2") || 
                    actualCommand.equalsIgnoreCase("rename_knights3") || 
                    actualCommand.equalsIgnoreCase("rename_knights4")) 
        { 
            if (cmdParams.equals(""))  
                return; 
            renameSubPledge(player, cmdParams, actualCommand); 
		}
		else if (actualCommand.equalsIgnoreCase("create_knight"))
		{
			if (cmdParams.equals(""))
				return;
			createSubPledge(player, cmdParams, cmdParams2, L2Clan.SUBUNIT_KNIGHT1, 7);
		}
		else if (actualCommand.equalsIgnoreCase("assign_subpl_leader"))
		{
			if (cmdParams.equals(""))
				return;
			assignSubPledgeLeader(player, cmdParams, cmdParams2);
		}
		else if (actualCommand.equalsIgnoreCase("create_ally"))
		{
			if (cmdParams.equals(""))
				return;
			if (!player.isClanLeader())
			{
				player.sendPacket(new SystemMessage(SystemMessageId.ONLY_CLAN_LEADER_CREATE_ALLIANCE));
				return;
			}

			if (player.getClan() == null)
				return;		
			player.getClan().createAlly(player, cmdParams);
		}
		else if (actualCommand.equalsIgnoreCase("dissolve_ally"))
		{
			if (!player.isClanLeader())
			{
				player.sendPacket(new SystemMessage(SystemMessageId.FEATURE_ONLY_FOR_ALLIANCE_LEADER));
				return;
			}
			player.getClan().dissolveAlly(player);
		}
		else if (actualCommand.equalsIgnoreCase("dissolve_clan"))
		{
			dissolveClan(player, player.getClanId());
		}
		else if (actualCommand.equalsIgnoreCase("change_clan_leader"))
		{
			if (cmdParams.equals(""))
				return;
			changeClanLeader(player, cmdParams);
		}
		else if (actualCommand.equalsIgnoreCase("recover_clan"))
		{
			recoverClan(player, player.getClanId());
		}
		else if (actualCommand.equalsIgnoreCase("increase_clan_level"))
		{
			if (!player.isClanLeader())
			{
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
				return;
			}
			player.getClan().levelUpClan(player);
		}
		else if (actualCommand.equalsIgnoreCase("learn_clan_skills"))
		{
			showPledgeSkillList(player);
		}
		else if (command.startsWith("Subclass"))
		{
			int cmdChoice = Integer.parseInt(command.substring(9, 10).trim());
			// Subclasses may not be changed while a skill is in use.
			
			/** Not change sub-class when restarting / shutdown of the server is protect against exploit by kj2a. **/
			if (Shutdown.getCounterInstance() != null)
			{
				player.sendMessage("Can't Use a sub-class when restarting / shutdown of the server");
				return;
			} 
			
			if (Olympiad.getInstance().isRegistered(player))
			{
				player.sendMessage("You can't do this while registered in Olympiad Game");
				return;
			}
			
			if (player.isCastingNow() || player.isAllSkillsDisabled() || player.isInCombat() || player.isCursedWeaponEquipped())
				return;
			
			StringBuilder content = new StringBuilder("<html><body>");
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			Set<PlayerClass> subsAvailable;
			int paramOne = 0;
			int paramTwo = 0;
			try
			{
				int endIndex = command.length();
				if (command.length() > 13)
				{
					endIndex = 13;
					paramTwo = Integer.parseInt(command.substring(13).trim());
				}
				paramOne = Integer.parseInt(command.substring(11, endIndex).trim());
			}
			catch (Exception NumberFormatException)
			{
			}
			switch (cmdChoice)
			{
				case 1: // Add Subclass - Initial
					// Avoid giving player an option to add a new sub class, if they have three already.
					if (!isInsideRadius(player, 150, false, false))
					{
						player.sendPacket(new ActionFailed());
						return;
					}
					if (player.isInOlympiadMode() || player.isParalyzed() || player.isInCombat() || player.isSilentMoving() || player.isEnchanting() || player.isCastingNow() || player.isInvisible() || player.isDead() || player.isAlikeDead() || player.isCursedWeaponEquipped())
					{
						player.sendPacket(new ActionFailed());
						player.sendMessage("Action failed due to an unimportant action!");
						return;
					}
					if (player.getTotalSubClasses() == Config.ALT_MAX_SUBCLASS)
					{
						player.sendMessage("You can now only change one of your current sub classes.");
						return;
					}
					subsAvailable = getAvailableSubClasses(player);
					if (subsAvailable != null && !subsAvailable.isEmpty())
					{
						content.append("Add Subclass:<br>Which sub class do you wish to add?<br>");
						for (PlayerClass subClass : subsAvailable)
							content.append("<a action=\"bypass -h npc_" + getObjectId() + "_Subclass 4 " + subClass.ordinal() + "\" msg=\"1268;" + formatClassForDisplay(subClass) + "\">" + formatClassForDisplay(subClass) + "</a><br>");
					}
					else
					{
						player.sendMessage("There are no sub classes available at this time.");
						return;
					}
					break;
				case 2: // Change Class - Initial
					content.append("Change Subclass:<br>");
					final int baseClassId = player.getBaseClass();
					if (player.getSubClasses().isEmpty())
					{
						content.append("You can't change sub classes when you don't have a sub class to begin with.<br>" + "<a action=\"bypass -h npc_" + getObjectId() + "_Subclass 1\">Add subclass.</a>");
					}
					else if (!isInsideRadius(player, 150, false, false))
					{
						player.sendPacket(new ActionFailed());
						return;
					}
					else if (player.isInOlympiadMode() || player.isParalyzed() || player.isInCombat() || player.isSilentMoving() || player.isEnchanting() || player.isCastingNow() || player.isInvisible() || player.isDead() || player.isAlikeDead())
					{
						player.sendPacket(new ActionFailed());
						player.sendMessage("Action Failed due to an unimportant action!");
						return;
					}
					else
					{
						content.append("Which class would you like to switch to?<br>");
						if (baseClassId == player.getActiveClass())
							content.append(CharTemplateTable.getClassNameById(baseClassId) + "&nbsp;<font color=\"LEVEL\">(Base Class)</font><br><br>");
						else
							content.append("<a action=\"bypass -h npc_" + getObjectId() + "_Subclass 5 0\">" + CharTemplateTable.getClassNameById(baseClassId) + "</a>&nbsp;" + "<font color=\"LEVEL\">(Base Class)</font><br><br>");
						for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext();)
						{
							SubClass subClass = subList.next();
							int subClassId = subClass.getClassId();
							if (subClassId == player.getActiveClass())
								content.append(CharTemplateTable.getClassNameById(subClassId) + "<br>");
							else
								content.append("<a action=\"bypass -h npc_" + getObjectId() + "_Subclass 5 " + subClass.getClassIndex() + "\">" + CharTemplateTable.getClassNameById(subClassId) + "</a><br>");
						}
					}
					break;
				case 3: // Change/Cancel Subclass - Initial
					content.append("Change Subclass:<br>Which of the following sub classes would you like to change?<br>");
					int classIndex = 1;
					if (!isInsideRadius(player, 150, false, false))
					{
						player.sendPacket(new ActionFailed());
						return;
					}
					if (player.isInOlympiadMode() || player.isParalyzed() || player.isInCombat() || player.isSilentMoving() || player.isEnchanting() || player.isCastingNow() || player.isInvisible() || player.isDead() || player.isAlikeDead() || player.isCursedWeaponEquipped())
					{
						player.sendPacket(new ActionFailed());
						player.sendMessage("Action Failed due to an unimportant action!");
						return;
					}
					for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext();)
					{
						SubClass subClass = subList.next();
						content.append("Sub-class " + classIndex + "<br1>");
						content.append("<a action=\"bypass -h npc_" + getObjectId() + "_Subclass 6 " + subClass.getClassIndex() + "\">" + CharTemplateTable.getClassNameById(subClass.getClassId()) + "</a><br>");
						classIndex++;
					}
					content.append("<br>If you change a sub class, you'll start at level 40 after the 2nd class transfer.");
					break;
				case 4: // Add Subclass - Action (Subclass 4 x[x])

					// Fix exploit stuck subclass and skills
					if(player.isLearningSkill() || player.isLocked())
            			return;

            		player.setLocked(true);
            		boolean allowAddition = true;

					if(TvTEvent.isPlayerParticipant(getObjectId()) || player.isInFunEvent())
					{
						player.sendMessage("You can't add a subclass while in an event.");
						player.setLocked(false);
						return;
					}
					
					// You can't add Subclass when you are registered in Olympiad
					if(Olympiad.getInstance().isRegisteredInComp(player) || player.getOlympiadGameId() > 0)
					{
						player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_ALREADY_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_AN_EVENT));
						player.setLocked(false);
						return;
					}

					if (player.getActiveWeaponInstance() != null || player.getActiveShieldInstance() != null)
					{
						player.disarmWeapons();
					}
					
					if (player.getActiveChestArmorItem() != null)
					{
						L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
						if (chest != null)
				    	{
				    		L2ItemInstance[] unequipped = player.getInventory().unEquipItemInBodySlotAndRecord(chest.getItem().getBodyPart());
				    		InventoryUpdate iu = new InventoryUpdate();
				    		for (L2ItemInstance element : unequipped)
				    			iu.addModifiedItem(element);
				    		sendPacket(iu);
				    	}
					}
					
					if (!isInsideRadius(player, 150, false, false))
					{
						player.sendPacket(new ActionFailed());
						return;
					}

					if (!TvTEvent.onEscapeUse(player.getObjectId()))
					{ 
						player.sendPacket(new ActionFailed());
						return;
					}
					if (!player.getFloodProtectors().getSubclass().tryPerformAction("Subclass"))
					{
						player.sendMessage("You can not Add Subclass so fast!");
						player.sendPacket(new ActionFailed());
						return;
					}
					if (player.getLevel() < 75)
					{
						player.sendMessage("You may not add a new sub class before you are level 75 on your previous class.");
						allowAddition = false;
					}
					if (Olympiad.getInstance().isRegisteredInComp(player) || player.getOlympiadGameId() > 0)
					{
						player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_ALREADY_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_AN_EVENT));
						return;
					}
					if (allowAddition)
					{
						if (!player.getSubClasses().isEmpty())
						{
							for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext();)
							{
								SubClass subClass = subList.next();
								if (subClass.getLevel() < 75)
								{
									player.sendMessage("You may not add a new sub class before you are level 75 on your previous sub class.");
									allowAddition = false;
									break;
								}
							}
						}
					}
					/*
					 * If quest checking is enabled, verify if the character has completed the Mimir's Elixir (Path to Subclass) and Fate's Whisper (A Grade Weapon) quests by checking for instances of their unique reward items. If they both exist, remove both unique items and continue with adding the sub-class.
					 */
					if (!Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS)
					{
						QuestState qs = player.getQuestState("235_MimirsElixir");
						if (qs == null || !qs.isCompleted())
						{
							player.sendMessage("You must have completed the Mimir's Elixir quest to continue adding your sub class.");
							return;
						}
						if(!Config.ALT_SUB_WITHOUT_FATES)
						{
							qs = player.getQuestState("234_FatesWhisper");
							if (qs == null || !qs.isCompleted())
							{
								player.sendMessage("You must have completed the Fate's Whisper quest to continue adding your sub class.");
								return;
							}
						}
					}
					if (allowAddition)
					{
						String className = CharTemplateTable.getClassNameById(paramOne);
						if (!player.addSubClass(paramOne, player.getTotalSubClasses() + 1))
						{
							player.sendMessage("The sub class could not be added.");
							return;
						}
						player.setActiveClass(player.getTotalSubClasses());

						content.append("Add Subclass:<br>The sub class of <font color=\"LEVEL\">" + className + "</font> has been added.");
						player.sendPacket(new SystemMessage(SystemMessageId.CLASS_TRANSFER)); // Transfer to new class.
					}
					else
					{
						html.setFile("data/html/villagemaster/SubClass_Fail.htm");
					}					
					player.setLocked(false);
					break;
				case 5: // Change Class - Action
                                        /*
					* If the character is less than level 75 on any of their previously chosen classes then disallow them to change to their most recently added sub-class choice.
					* 
					* Note: paramOne = classIndex
					*/
					
					// Fix exploit stuck subclass
					if(player.isLocked())
            		{
            			player.sendMessage("You can't add subclass right now.");
            			return;
            		}
					else
					{
            			player.setLocked(true);
            		}
					if (player.getActiveWeaponInstance() != null || player.getActiveShieldInstance() != null)
					{
						player.disarmWeapons();
					}
					
					if (player.getActiveChestArmorItem() != null)
					{
						L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
						if (chest != null)
				    	{
				    		L2ItemInstance[] unequipped = player.getInventory().unEquipItemInBodySlotAndRecord(chest.getItem().getBodyPart());
				    		InventoryUpdate iu = new InventoryUpdate();
				    		for (L2ItemInstance element : unequipped)
				    			iu.addModifiedItem(element);
				    		sendPacket(iu);
				    	}
					}

					if (Olympiad.getInstance().isRegisteredInComp(player) || player.getOlympiadGameId() > 0)
					{
						player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_ALREADY_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_AN_EVENT));
						return;
					}
					if (!isInsideRadius(player, 150, false, false))
					{
						player.sendPacket(new ActionFailed());
						return;
					}
					if (player.isInOlympiadMode() || player.isParalyzed() || player.isInCombat() || player.isSilentMoving() || player.isEnchanting() || player.isCastingNow() || player.isInvisible() || player.isDead() || player.isAlikeDead() || player.isCursedWeaponEquipped())
					{
						player.sendPacket(new ActionFailed());
						player.sendMessage("Action Failed due to an unimportant action!");
						return;
					}
					if (!TvTEvent.onEscapeUse(player.getObjectId()))
					{ 
						player.sendPacket(new ActionFailed());
						return;
					}
					if (!player.getFloodProtectors().getSubclass().tryPerformAction("Subclass"))
					{
						player.sendMessage("You can not Change Subclass so fast!");
						player.sendPacket(new ActionFailed());
						return;
					}
					player.setActiveClass(paramOne);
					content.append("Change Subclass:<br>Your active sub class is now a <font color=\"LEVEL\">" + CharTemplateTable.getClassNameById(player.getActiveClass()) + "</font>.");
					player.sendPacket(new SystemMessage(SystemMessageId.SUBCLASS_TRANSFER_COMPLETED)); // Transfer completed.
					player.setLocked(false);
					break;
				case 6: // Change/Cancel Subclass - Choice
					content.append("Please choose a sub class to change to. If the one you are looking for is not here, " + "please seek out the appropriate master for that class.<br>" + "<font color=\"LEVEL\">Warning!</font> All classes and skills for this class will be removed.<br><br>");
					subsAvailable = getAvailableSubClasses(player);
					if (!isInsideRadius(player, 150, false, false))
					{
						player.sendPacket(new ActionFailed());
						return;
					}
					if (player.isInOlympiadMode() || player.isParalyzed() || player.isInCombat() || player.isSilentMoving() || player.isEnchanting() || player.isCastingNow() || player.isInvisible() || player.isDead() || player.isAlikeDead() || player.isCursedWeaponEquipped())
					{
						player.sendPacket(new ActionFailed());
						player.sendMessage("Action Failed due to an unimportant action!");
						return;
					}
					if (subsAvailable != null && !subsAvailable.isEmpty())
					{
						for (PlayerClass subClass : subsAvailable)
							content.append("<a action=\"bypass -h npc_" + getObjectId() + "_Subclass 7 " + paramOne + " " + subClass.ordinal() + "\">" + formatClassForDisplay(subClass) + "</a><br>");
					}
					else
					{
						player.sendMessage("There are no sub classes available at this time.");
						return;
					}
					break;
				case 7: // Change Subclass - Action
					/*
					 * Warning: the information about this subclass will be removed from the subclass list even if false!
					 */
					
					// Fix exploit stuck subclass
					if(player.isLocked())
            		{
            			player.sendMessage("You can't add subclass right now.");
            			return;
            		}
					else
					{
            			player.setLocked(true);
            		}
					if (player.getActiveWeaponInstance() != null || player.getActiveShieldInstance() != null)
					{
						player.disarmWeapons();
					}
					
					if (player.getActiveChestArmorItem() != null)
					{
						L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
						if (chest != null)
				    	{
				    		L2ItemInstance[] unequipped = player.getInventory().unEquipItemInBodySlotAndRecord(chest.getItem().getBodyPart());
				    		InventoryUpdate iu = new InventoryUpdate();
				    		for (L2ItemInstance element : unequipped)
				    			iu.addModifiedItem(element);
				    		sendPacket(iu);
				    	}
					}

					if (!isInsideRadius(player, 150, false, false))
					{
						player.sendPacket(new ActionFailed());
						return;
					}
					if (player.isInOlympiadMode() || player.isParalyzed() || player.isInCombat() || player.isSilentMoving() || player.isEnchanting() || player.isCastingNow() || player.isInvisible() || player.isDead() || player.isAlikeDead() || player.isCursedWeaponEquipped())
					{
						player.sendPacket(new ActionFailed());
						player.sendMessage("Action Failed due to an unimportant action!");
						return;
					}
					if (!TvTEvent.onEscapeUse(player.getObjectId()))
					{ 
						player.sendPacket(new ActionFailed());
						player.sendMessage("Can't use subclass in TvT.");
						return;
					}
					if (!player.getFloodProtectors().getSubclass().tryPerformAction("Subclass"))
					{
						player.sendMessage("You can not Change a Subclass so fast!");
						player.sendPacket(new ActionFailed());
						return;
					}
					if (!isInsideRadius(player, 150, false, false))
					{
						player.sendPacket(new ActionFailed());
						player.sendMessage("[Server]: You are too far away!");
						return;
					}
					if (player.isInOlympiadMode() || player.isParalyzed() || player.isInCombat() || player.isSilentMoving() || player.isEnchanting() || player.isCastingNow() || player.isInvisible() || player.isDead() || player.isAlikeDead())
					{
						player.sendPacket(new ActionFailed());
						player.sendMessage("Action Failed due to an unimportant action!");
						return;
					}
					else if (player.modifySubClass(paramOne, paramTwo))
					{
						player.setActiveClass(paramOne);

						content.append("Change Subclass:<br>Your sub class has been changed to <font color=\"LEVEL\">" + CharTemplateTable.getClassNameById(paramTwo) + "</font>.");

						player.sendPacket(new SystemMessage(SystemMessageId.ADD_NEW_SUBCLASS)); // Subclass added.
					}
					else
					{
						/*
						 * This isn't good! modifySubClass() removed subclass from memory we must update _classIndex! Else IndexOutOfBoundsException can turn up some place down the line along with
						 * other seemingly unrelated problems.
						 */
						player.setActiveClass(0); // Also updates _classIndex plus switching _classid to baseclass.

						player.sendMessage("The sub class could not be added, you have been reverted to your base class.");
						return;
					}
					player.setLocked(false);
					break;
			}
			content.append("</body></html>");
			// If the content is greater than for a basic blank page,
			// then assume no external HTML file was assigned.
			if (content.length() > 26)
				html.setHtml(content.toString());
			player.sendPacket(html);
		}
		else
		{
			// this class don't know any other commands, let forward
			// the command to the parent class
			super.onBypassFeedback(player, command);
		}
	}


	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;
		return "data/html/villagemaster/" + pom + ".htm";
	}

	// Private stuff
	public void dissolveClan(L2PcInstance player, int clanId)
	{
		if (Config.DEBUG)
		_log.debug(player.getObjectId() + "(" + player.getName() + ") requested dissolve a clan from " + getObjectId() + "(" + getName() + ")");
		if (!player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
			return;
		}
		 
      /*  
       * Until proper clan leader change support is done, this is a little 
       * exploit fix (leader, while fliying wyvern changes clan leader and the new leader 
       * can ride the wyvern too) 
       */ 
       if (player.isFlying()) 
       { 
           player.sendMessage("Please, stop flying"); 
           return; 
       } 

		L2Clan clan = player.getClan();
		if (clan.getAllyId() != 0)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_DISPERSE_THE_CLANS_IN_ALLY));
			return;
		}
		if (clan.isAtWar() != 0)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_DISSOLVE_WHILE_IN_WAR));
			return;
		}
		if (player.isInCombat())
		{
			player.sendMessage("You may not dissolve clan while you are in combat.");
			return;
		}
		if (player.isInOlympiadMode())
		{
			player.sendMessage("You may not dissolve clan while you are in olympiad.");
			return;
		}
		if(clan.getHasCastle() != 0 || clan.getHasHideout() != 0 || clan.getHasFort() != 0)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_DISSOLVE_WHILE_OWNING_CLAN_HALL_OR_CASTLE));
			return;
		}
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			if (SiegeManager.getInstance().checkIsRegistered(clan, castle.getCastleId()))
			{
				player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_DISSOLVE_CAUSE_CLAN_WILL_PARTICIPATE_IN_CASTLE_SIEGE));
				return;
			}
		}

		if (player.isInsideZone(L2PcInstance.ZONE_SIEGE))
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_DISSOLVE_WHILE_IN_SIEGE));
			return;
		}
		if (clan.getDissolvingExpiryTime() > System.currentTimeMillis())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.DISSOLUTION_IN_PROGRESS));
			return;
		}
		clan.setDissolvingExpiryTime(System.currentTimeMillis() + Config.CLAN_DISSOLVE_DAYS * 86400000L); // 24*60*60*1000 = 86400000
		clan.updateClanInDB();
		ClanTable.getInstance().scheduleRemoveClan(clan.getClanId());
		// The clan leader should take the XP penalty of a full death.
		player.deathPenalty(false);
	}

	public void recoverClan(L2PcInstance player, int clanId)
	{
		if (Config.DEBUG)
		_log.debug(player.getObjectId() + "(" + player.getName() + ") requested recover a clan from " + getObjectId() + "(" + getName() + ")");
		if (!player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
			return;
		}
		L2Clan clan = player.getClan();
		clan.setDissolvingExpiryTime(0);
		clan.updateClanInDB();
	}

	public void changeClanLeader(L2PcInstance player, String target)
	{
		if (Config.DEBUG)
		_log.debug(player.getObjectId() + "(" + player.getName() + ") requested change a clan leader from " + getObjectId() + "(" + getName() + ")");
		if (player.isFlying() || player.isMounted() || player.isRiding())
        {
        	player.sendMessage("You can not give your Clan while you are Mounted!");
        	return;
        }
		if (!player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
			return;
		}
		if (player.getName().equalsIgnoreCase(target))
		{
			return;
		}
		L2Clan clan = player.getClan();
		L2ClanMember member = clan.getClanMember(target);
		if (member == null)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_DOES_NOT_EXIST);
			sm.addString(target);
			player.sendPacket(sm);
			sm = null;
			return;
		}
		if (!member.isOnline())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.INVITED_USER_NOT_ONLINE));
			return;
		}
		clan.setNewLeader(member, player);
	}

	public void createSubPledge(L2PcInstance player, String clanName, String leaderName, int pledgeType, int minClanLvl)
	{
		// if (Config.DEBUG)
		_log.debug(player.getObjectId() + "(" + player.getName() + ") requested sub clan creation from " + getObjectId() + "(" + getName() + ")");
		if (!player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
			return;
		}
		L2Clan clan = player.getClan();
		if (clan.getLevel() < minClanLvl)
		{
			if (pledgeType == L2Clan.SUBUNIT_ACADEMY)
			{
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN_ACADEMY));
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessageId.YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_MILITARY_UNIT));
			}
			return;
		}
		if (!Util.isAlphaNumeric(clanName) || 2 > clanName.length())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CLAN_NAME_INCORRECT));
			return;
		}
		if (clanName.length() > 16)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.CLAN_NAME_TOO_LONG));
			return;
		}

		if (pledgeType != L2Clan.SUBUNIT_ACADEMY && clan.getClanMember(leaderName) == null)
			return;
         
        if(clan.getLeaderSubPledge(leaderName)!=0) 
        { 
                player.sendMessage(leaderName+" is already a sub unit leader."); 
                    return; 
            } 

		for (L2Clan tempClan : ClanTable.getInstance().getClans())
		{
			if (tempClan.getSubPledge(clanName) != null)
			{
				if (pledgeType == L2Clan.SUBUNIT_ACADEMY)
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_ALREADY_EXISTS);
					sm.addString(clanName);
					player.sendPacket(sm);
					sm = null;
				}
				else
				{
					player.sendPacket(new SystemMessage(SystemMessageId.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME));
				}
				return;
			}
		}
		if (pledgeType != L2Clan.SUBUNIT_ACADEMY)
			if (clan.getClanMember(leaderName) == null || clan.getClanMember(leaderName).getPledgeType() != 0)
			{
				if (pledgeType >= L2Clan.SUBUNIT_KNIGHT1)
				{
					player.sendPacket(new SystemMessage(SystemMessageId.CAPTAIN_OF_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED));
				}
				else if (pledgeType >= L2Clan.SUBUNIT_ROYAL1)
				{
					player.sendPacket(new SystemMessage(SystemMessageId.CAPTAIN_OF_ROYAL_GUARD_CANNOT_BE_APPOINTED));
				}
				return;
			}
		if (clan.createSubPledge(player, pledgeType, leaderName, clanName) == null)
			return;
		SystemMessage sm;
		if (pledgeType == L2Clan.SUBUNIT_ACADEMY)
		{
			sm = new SystemMessage(SystemMessageId.THE_S1S_CLAN_ACADEMY_HAS_BEEN_CREATED);
			sm.addString(player.getClan().getName());
		}
		else if (pledgeType >= L2Clan.SUBUNIT_KNIGHT1)
		{
			sm = new SystemMessage(SystemMessageId.THE_KNIGHTS_OF_S1_HAVE_BEEN_CREATED);
			sm.addString(player.getClan().getName());
		}
		else if (pledgeType >= L2Clan.SUBUNIT_ROYAL1)
		{
			sm = new SystemMessage(SystemMessageId.THE_ROYAL_GUARD_OF_S1_HAVE_BEEN_CREATED);
			sm.addString(player.getClan().getName());
		}
		else
			sm = new SystemMessage(SystemMessageId.CLAN_CREATED);
		player.sendPacket(sm);
		if (pledgeType != L2Clan.SUBUNIT_ACADEMY)
		{
			L2ClanMember leaderSubPledge = clan.getClanMember(leaderName);
			if (leaderSubPledge.getPlayerInstance() == null)
				return;
			leaderSubPledge.getPlayerInstance().setPledgeClass(L2ClanMember.calculatePledgeClass(leaderSubPledge.getPlayerInstance()));
			leaderSubPledge.getPlayerInstance().sendPacket(new UserInfo(leaderSubPledge.getPlayerInstance()));
            try 
            { 
                clan.getClanMember(leaderName).updatePledgeType(); 
                for (L2Skill skill : leaderSubPledge.getPlayerInstance().getAllSkills()) 
                    leaderSubPledge.getPlayerInstance().removeSkill(skill,false); 
                clan.getClanMember(leaderName).getPlayerInstance().setActiveClass(0); 
            } 
            catch(Throwable t){} 
            for (L2ClanMember member : clan.getMembers()) 
            { 
                if (member == null || member.getPlayerInstance()==null || member.getPlayerInstance().isOnline()==0) 
                    continue; 
                SubPledge[] subPledge = clan.getAllSubPledges(); 
                for (SubPledge element : subPledge)  
                { 
                    member.getPlayerInstance().sendPacket(new PledgeReceiveSubPledgeCreated(element)); 
                } 
            } 
 
        } 
    } 
 
    public void renameSubPledge(L2PcInstance player, String newName, String command){ 
        if (player == null || player.getClan() == null || !player.isClanLeader()) 
        { 
            if (player!=null)  
                player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT)); 
            return; 
        } 
        L2Clan clan = player.getClan(); 
        SubPledge[] subPledge = clan.getAllSubPledges(); 
        for (SubPledge element : subPledge)  
        { 
            switch(element.getId()){ 
            case 100: // 1st Royal Guard 
                if (command.equalsIgnoreCase("rename_royal1")){ 
                    changeSubPledge(clan, element, newName); 
                    return; 
                } 
                break; 
            case 200: // 2nd Royal Guard 
                if (command.equalsIgnoreCase("rename_royal2")){ 
                    changeSubPledge(clan, element, newName); 
                    return; 
                } 
                break;               
            case 1001: // 1st Order of Knights 
                if (command.equalsIgnoreCase("rename_knights1")){ 
                    changeSubPledge(clan, element, newName); 
                    return; 
                } 
                break;               
            case 1002: // 2nd Order of Knights 
                if (command.equalsIgnoreCase("rename_knights2")){ 
                    changeSubPledge(clan, element, newName); 
                    return; 
                } 
                break;               
            case 2001: // 3rd Order of Knights 
                if (command.equalsIgnoreCase("rename_knights3")){ 
                    changeSubPledge(clan, element, newName); 
                    return; 
                } 
                break;               
            case 2002: // 4th Order of Knights 
                if (command.equalsIgnoreCase("rename_knights4")){ 
                    changeSubPledge(clan, element, newName); 
                    return; 
                } 
                break; 
            } 
        } 
        player.sendMessage("Sub unit not found."); 
    } 
     
    public void changeSubPledge(L2Clan clan, SubPledge element, String newName){ 
        if (newName.length() > 16 || newName.length() < 3) 
        { 
            clan.getLeader().getPlayerInstance().sendPacket(new SystemMessage(SystemMessageId.CLAN_NAME_TOO_LONG)); 
            return; 
        } 
        String oldName = element.getName();  
        element.setName(newName); 
        clan.updateSubPledgeInDB(element.getId()); 
        for (L2ClanMember member : clan.getMembers()) 
        { 
            if (member == null || member.getPlayerInstance()==null || member.getPlayerInstance().isOnline()==0) 
                continue; 
            SubPledge[] subPledge = clan.getAllSubPledges(); 
            for (SubPledge sp : subPledge)  
            { 
                member.getPlayerInstance().sendPacket(new PledgeReceiveSubPledgeCreated(sp)); 
            } 
            if (member.getPlayerInstance()!= null) 
                member.getPlayerInstance().sendMessage("Clan sub unit "+oldName+"'s name has been changed into "+newName+"."); 
        } 
    } 
     
    public void assignSubPledgeLeader(L2PcInstance player, String subUnitName, String newLeaderName) 
    { 
        if (player.getClan()==null) 
        { 
            player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT)); 
            return;          
        } 
        String leaderName = ""; 
        try 
        { 
            leaderName = player.getClan().getSubPledge(subUnitName).getLeaderName(); 
        } 
        catch(Throwable t) 
        { 
            _log.warn("could not find sub unit leader name for sub unit: "+subUnitName+" in clan "+player.getClan());
            return; 
        }
		if (Config.DEBUG)
            _log.info(player.getObjectId() + "(" + player.getName() + ") requested to assign a new sub clan for " + player.getClan().getName() + " leader " 
                       + "(" + leaderName + " --> "+newLeaderName+")");
		
		if (!player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
			return;
		}

		if (leaderName.length() > 16 || newLeaderName.length() > 16)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.NAMING_CHARNAME_UP_TO_16CHARS));
			return;
		}
		
        if(player.getName().equals(newLeaderName)) 
        { 
            player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT)); 
		}
		L2Clan clan = player.getClan();

        if(leaderName.equals(newLeaderName) || clan.getLeaderSubPledge(newLeaderName)!=0) 
        { 
            player.sendMessage(newLeaderName+" is already a sub unit leader."); 
            return; 
        } 
        L2PcInstance newLeader = L2World.getInstance().getPlayer(newLeaderName); 
        if (newLeader!=null) 
        { 
            if (newLeader.getClan() == null || newLeader.getClan()!=clan){ 
                player.sendMessage(newLeaderName+" is not in your clan!"); 
                return; 
            } 
        } 
        int leadssubpledge = clan.getLeaderSubPledge(leaderName); 
 
        if (leadssubpledge == 0)  
        { 
            player.sendMessage(leaderName + " is not a sub unit leader."); 
            return; 
        } 
        try 
        { 
            clan.getSubPledge(leadssubpledge).setLeaderName(newLeaderName); 
            clan.getClanMember(newLeaderName).updatePledgeType(); 
            clan.getClanMember(leaderName).setPledgeType(0); 
            clan.getClanMember(leaderName).updatePledgeType(); 
        } 
        catch(Throwable t){} 
        try 
        { 
            clan.getClanMember(leaderName);
			clan.getClanMember(leaderName).getPlayerInstance().setPledgeClass(L2ClanMember.calculatePledgeClass(clan.getClanMember(leaderName).getPlayerInstance())); 
            clan.getClanMember(leaderName).getPlayerInstance().setActiveClass(0); 
        } 
        catch(Throwable t){} 
         
        clan.updateSubPledgeInDB(leadssubpledge);        
        for (L2PcInstance member : clan.getOnlineMembers("")) 
        { 
            member.sendMessage(newLeaderName+" has been appointed as sub unit leader instead of "+leaderName+"."); 
            member.broadcastUserInfo(); 
        } 
        for (L2ClanMember member : clan.getMembers()) 
        { 
            if (member == null || member.getPlayerInstance()==null || member.getPlayerInstance().isOnline()==0) 
                continue; 
            SubPledge[] subPledge = clan.getAllSubPledges(); 
            for (SubPledge element : subPledge) 
            { 
                member.getPlayerInstance().sendPacket(new PledgeReceiveSubPledgeCreated(element)); 
            } 
        } 
        L2ClanMember leaderSubPledge = clan.getClanMember(newLeaderName); 
        if (leaderSubPledge.getPlayerInstance() == null) return; 
        leaderSubPledge.getPlayerInstance().setPledgeClass(L2ClanMember.calculatePledgeClass(leaderSubPledge.getPlayerInstance())); 
        leaderSubPledge.getPlayerInstance().sendPacket(new UserInfo(leaderSubPledge.getPlayerInstance())); 
        for (L2Skill skill : leaderSubPledge.getPlayerInstance().getAllSkills()) 
            leaderSubPledge.getPlayerInstance().removeSkill(skill,false); 
        leaderSubPledge.getPlayerInstance().setActiveClass(0); 
	}

	private final Set<PlayerClass> getAvailableSubClasses(L2PcInstance player)
	{
		int charClassId = player.getBaseClass();

		if (charClassId >= 88)
			charClassId = ClassId.values()[charClassId].getParent().getId();

		final PlayerRace npcRace = getVillageMasterRace();
		final ClassType npcTeachType = getVillageMasterTeachType();

		PlayerClass currClass = PlayerClass.values()[charClassId];

		/**
		 * If the race of your main class is Elf or Dark Elf, you may not select each class as a subclass to the other class, and you may not select Overlord and Warsmith class as a subclass.
		 * 
		 * You may not select a similar class as the subclass. The occupations classified as similar classes are as follows:
		 * 
		 * Treasure Hunter, Plainswalker and Abyss Walker Hawkeye, Silver Ranger and Phantom Ranger Paladin, Dark Avenger, Temple Knight and Shillien Knight Warlocks, Elemental Summoner and Phantom
		 * Summoner Elder and Shillien Elder Swordsinger and Bladedancer Sorcerer, Spellsinger and Spellhowler
		 * 
		 */
		Set<PlayerClass> availSubs = currClass.getAvailableSubclasses(player);

		if (availSubs != null)
		{
			for (PlayerClass availSub : availSubs)
			{
				for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext();)
				{
					SubClass prevSubClass = subList.next();
					int subClassId = prevSubClass.getClassId();
					if (subClassId >= 88)
						subClassId = ClassId.values()[subClassId].getParent().getId();

					if (availSub.ordinal() == subClassId || availSub.ordinal() == charClassId)
						availSubs.remove(PlayerClass.values()[availSub.ordinal()]);
				}

				if ((npcRace == PlayerRace.Human || npcRace == PlayerRace.LightElf))
				{
					// If the master is human or light elf, ensure that fighter-type
					// masters only teach fighter classes, and priest-type masters
					// only teach priest classes etc.
					if (!availSub.isOfType(npcTeachType))
						availSubs.remove(availSub);

					// Remove any non-human or light elf classes.
					else if (!availSub.isOfRace(PlayerRace.Human) && !availSub.isOfRace(PlayerRace.LightElf))
						availSubs.remove(availSub);
				}
				else
				{
					// If the master is not human and not light elf,
					// then remove any classes not of the same race as the master.
					if ((npcRace != PlayerRace.Human && npcRace != PlayerRace.LightElf) && !availSub.isOfRace(npcRace))
						availSubs.remove(availSub);
				}
			}
		}

		return availSubs;
        }

	/**
	 * this displays PledgeSkillList to the player.
	 * 
	 * @param player
	 */
	public void showPledgeSkillList(L2PcInstance player)
	{

		if (Config.DEBUG)
			_log.debug("PledgeSkillList activated on: " + getObjectId());
		if (player.getClan() == null)
			return;

		L2PledgeSkillLearn[] skills = SkillTreeTable.getInstance().getAvailablePledgeSkills(player);
		AquireSkillList asl = new AquireSkillList(AquireSkillList.skillType.Clan);
		int counts = 0;

		for (L2PledgeSkillLearn s : skills)
		{
			int cost = s.getRepCost();
			counts++;

			asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), cost, 0);
		}

		if (counts == 0)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(1);

			if (player.getClan().getLevel() < 8)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN);
				sm.addNumber(player.getClan().getLevel() + 1);
				player.sendPacket(sm);
			}
			else
			{
				StringBuilder sb = new StringBuilder();
				sb.append("<html><body>");
				sb.append("You've learned all skills available for your Clan.<br>");
				sb.append("</body></html>");
				html.setHtml(sb.toString());
				player.sendPacket(html);
			}
		}
		else
		{
			player.sendPacket(asl);
		}

		player.sendPacket(new ActionFailed());
	}

	private final String formatClassForDisplay(PlayerClass className)
	{
		String classNameStr = className.toString();
		char[] charArray = classNameStr.toCharArray();

		for (int i = 1; i < charArray.length; i++)
			if (Character.isUpperCase(charArray[i]))
				classNameStr = classNameStr.substring(0, i) + " " + classNameStr.substring(i);

		return classNameStr;
	}

	private final PlayerRace getVillageMasterRace()
	{
		String npcClass = getTemplate().getStatsSet().getString("jClass").toLowerCase();

		if (npcClass.indexOf("human") > -1)
			return PlayerRace.Human;

		if (npcClass.indexOf("darkelf") > -1)
			return PlayerRace.DarkElf;

		if (npcClass.indexOf("elf") > -1)
			return PlayerRace.LightElf;

		if (npcClass.indexOf("orc") > -1)
			return PlayerRace.Orc;

		return PlayerRace.Dwarf;
	}

	private final ClassType getVillageMasterTeachType()
	{
		String npcClass = getTemplate().getStatsSet().getString("jClass");

		if (npcClass.indexOf("sanctuary") > -1 || npcClass.indexOf("clergyman") > -1 || npcClass.indexOf("temple") > -1)
			return ClassType.Priest;

		if (npcClass.indexOf("mageguild") > -1 || npcClass.indexOf("patriarch") > -1)
			return ClassType.Mystic;

		return ClassType.Fighter;
	}

	private Iterator<SubClass> iterSubClasses(L2PcInstance player)
	{
		return player.getSubClasses().values().iterator();
	}
	
}
