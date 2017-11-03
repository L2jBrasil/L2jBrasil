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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.model.base.ClassId;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.CharInfo;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.SocialAction;
import com.it.br.gameserver.network.serverpackets.StatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.network.serverpackets.UserInfo;
import com.it.br.gameserver.util.Util;

/**
 * This class handles following admin commands:
 * - edit_character
 * - current_player
 * - character_list
 * - show_characters
 * - find_character
 * - find_ip
 * - find_account
 * - rec
 * - nokarma
 * - setkarma
 * - settitle
 * - setname
 * - setsex
 * - setclass
 * - fullfood
 * - save_modifications
 * - setnoble
 * - sethero
 *
 * @version $Revision: 1.3.2.1.2.10 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminEditChar implements IAdminCommandHandler
{
	private static Logger _log = Logger.getLogger(AdminEditChar.class.getName());

	private static final String[] ADMIN_COMMANDS =
		{
		"admin_edit_character",
		"admin_current_player",
		"admin_nokarma", // this is to remove karma from selected char...
		"admin_setkarma", // sets karma of target char to any amount. //setkarma <karma>
		"admin_character_list", //same as character_info, kept for compatibility purposes
		"admin_character_info", //given a player name, displays an information window
		"admin_show_characters",//list of characters
		"admin_find_character", //find a player by his name or a part of it (case-insensitive)
		"admin_find_ip", // find all the player connections from a given IPv4 number
		"admin_find_account", //list all the characters from an account (useful for GMs w/o DB access)
		"admin_save_modifications", //consider it deprecated...
		"admin_rec",      // gives recommendation points
		"admin_settitle", // changes char title
		"admin_setname",  // changes char name
		"admin_setsex",   // changes characters' sex
		"admin_setcolor", // change charnames' color display
		"admin_setclass", // changes chars' classId
		"admin_fullfood",  // fulfills a pet's food bar
		"admin_sethero",  // sets the target hero 
		"admin_setnoble" // sets the target noble 
		};

	private static final int REQUIRED_LEVEL = Config.GM_CHAR_EDIT;
	private static final int REQUIRED_LEVEL2 = Config.GM_CHAR_EDIT_OTHER;
	private static final int REQUIRED_LEVEL_VIEW = Config.GM_CHAR_VIEW;

	@SuppressWarnings("unused")
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (!Config.ALT_PRIVILEGES_ADMIN)
			if (!((checkLevel(activeChar.getAccessLevel()) || checkLevel2(activeChar.getAccessLevel())) && activeChar.isGM()))
				return false;

		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null) ? activeChar.getTarget().getName() : "no-target", "");

		if (command.equals("admin_current_player"))
		{
			showCharacterInfo(activeChar, null);
		}
		else if ((command.startsWith("admin_character_list"))||(command.startsWith("admin_character_info")))
		{
			try
			{
				String val = command.substring(21);
				L2PcInstance target = L2World.getInstance().getPlayer(val);
				if (target != null)
					showCharacterInfo(activeChar, target);
				else
					activeChar.sendPacket(new SystemMessage(SystemMessageId.CHARACTER_DOES_NOT_EXIST));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Usage: //character_info <player_name>");
			}
		}
		else if (command.startsWith("admin_show_characters"))
		{
			try
			{
				String val = command.substring(22);
				int page = Integer.parseInt(val);
				listCharacters(activeChar, page);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				//Case of empty page number
				activeChar.sendMessage("Usage: //show_characters <page_number>");
			}
		}
		else if (command.startsWith("admin_find_character"))
		{
			try
			{
				String val = command.substring(21);
				findCharacter(activeChar, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{	//Case of empty character name
				activeChar.sendMessage("Usage: //find_character <character_name>");
				listCharacters(activeChar, 0);
			}
		}
		else if (command.startsWith("admin_find_ip"))
		{
			try
			{
				String val = command.substring(14);
				findCharactersPerIp(activeChar, val);
			}
			catch (Exception e)
			{	//Case of empty or malformed IP number
				activeChar.sendMessage("Usage: //find_ip <www.xxx.yyy.zzz>");
				listCharacters(activeChar, 0);
			}
		}
		else if (command.startsWith("admin_find_account"))
		{
			try
			{
				String val = command.substring(19);
				findCharactersPerAccount(activeChar, val);
			}
			catch (Exception e)
			{	//Case of empty or malformed player name
				activeChar.sendMessage("Usage: //find_account <player_name>");
				listCharacters(activeChar, 0);
			}
		}
		else if (command.equals("admin_edit_character"))
			editCharacter(activeChar);
		// Karma control commands
		else if (command.equals("admin_nokarma"))
			setTargetKarma(activeChar, 0);
		else if (command.startsWith("admin_setkarma"))
		{
			try
			{
				String val = command.substring(15);
				int karma = Integer.parseInt(val);
				if (activeChar == activeChar.getTarget() || activeChar.getAccessLevel()>=REQUIRED_LEVEL2)
					GMAudit.auditGMAction(activeChar.getName(), command, activeChar.getName(), "");
				setTargetKarma(activeChar, karma);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				if (Config.DEVELOPER )
					System.out.println("Set karma error: "+e);
				activeChar.sendMessage("Usage: //setkarma <new_karma_value>");
			}
		}
		else if (command.startsWith("admin_save_modifications"))
		{
			try
			{
				String val = command.substring(24);
				if (activeChar == activeChar.getTarget() || activeChar.getAccessLevel()>=REQUIRED_LEVEL2)
					GMAudit.auditGMAction(activeChar.getName(), command, activeChar.getName(), "");
				adminModifyCharacter(activeChar, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{	//Case of empty character name
				activeChar.sendMessage("Error while modifying character.");
				listCharacters(activeChar, 0);
			}
		}
		else if (command.startsWith("admin_rec"))
		{
			try
			{
				String val = command.substring(10);
				int recVal = Integer.parseInt(val);
				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;
				if (activeChar != target && activeChar.getAccessLevel()<REQUIRED_LEVEL2)
					return false;
				if (target instanceof L2PcInstance) {
					player = (L2PcInstance)target;
				} else {
					return false;
				}
				player.setRecomHave(recVal);
				player.sendMessage("You have been recommended by a GM");
				player.broadcastUserInfo();
			} catch (Exception e)
			{
				activeChar.sendMessage("Usage: //rec number");
			}
		}
		else if (command.startsWith("admin_setclass"))
		{
			try
			{
				String val = command.substring(15);
				int classidval = Integer.parseInt(val);
				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;
				if (activeChar != target && activeChar.getAccessLevel()<REQUIRED_LEVEL2)
					return false;
				if (target instanceof L2PcInstance)
					player = (L2PcInstance)target;
				else
					return false;
				boolean valid=false;
				for (ClassId classid: ClassId.values())
					if (classidval == classid.getId())
						valid = true;
				if (valid && (player.getClassId().getId() != classidval))
				{
					player.setClassId(classidval);
					if (!player.isSubClassActive())
						player.setBaseClass(classidval);
					String newclass = player.getTemplate().className;
					player.store();
					player.sendMessage("A GM changed your class to "+newclass);
					player.broadcastUserInfo();
					activeChar.sendMessage(player.getName()+" is a "+newclass);
				}
				activeChar.sendMessage("Usage: //setclass <valid_new_classid>");
			}
			catch (StringIndexOutOfBoundsException e)
			{
				AdminHelpPage.showHelpPage(activeChar, "charclasses.htm");
			}
		}
		else if (command.startsWith("admin_settitle"))
		{
			try
			{
				String val = command.substring(15);
				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;
				if (activeChar != target && activeChar.getAccessLevel()<REQUIRED_LEVEL2)
					return false;
				if (target instanceof L2PcInstance) {
					player = (L2PcInstance)target;
				} else {
					return false;
				}
				player.setTitle(val);
				player.sendMessage("Your title has been changed by a GM");
				player.broadcastTitleInfo();
			}
			catch (StringIndexOutOfBoundsException e)
			{   //Case of empty character title
				activeChar.sendMessage("You need to specify the new title.");
			}
		}
		else if (command.startsWith("admin_setname"))
		{
			try
			{
				String val = command.substring(14);
				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;
				if (activeChar != target && activeChar.getAccessLevel()<REQUIRED_LEVEL2)
					return false;
				if (target instanceof L2PcInstance) {
					player = (L2PcInstance)target;
				} else {
					return false;
				}
				player.setName(val);
				player.sendMessage("Your name has been changed by a GM");
				player.broadcastUserInfo();
				player.store();
			}
			catch (StringIndexOutOfBoundsException e)
			{   //Case of empty character name
				activeChar.sendMessage("Usage: //setname new_name_for_target");
			}
		}
		else if (command.startsWith("admin_setsex"))
		{
			L2Object target = activeChar.getTarget();
			L2PcInstance player = null;
			if (activeChar != target && activeChar.getAccessLevel()<REQUIRED_LEVEL2)
				return false;
			if (target instanceof L2PcInstance) {
				player = (L2PcInstance)target;
			} else {
				return false;
			}
			player.getAppearance().setSex(player.getAppearance().getSex()? false : true);
			player.sendMessage("Your gender has been changed by a GM");
			player.broadcastUserInfo();
			player.decayMe();
			player.spawnMe(player.getX(),player.getY(),player.getZ());
		}
		else if (command.startsWith("admin_sethero") || command.startsWith("admin_manualhero")) 
		{ 
			L2Object target = activeChar.getTarget(); 

			if (target instanceof L2PcInstance) 
			{ 
				L2PcInstance targetPlayer = (L2PcInstance) target;              
				boolean isHero = targetPlayer.isHero(); 
				String targetNotifyMsg = ""; 
				String gmBroadcastMsg = ""; 
              
				if (isHero) 
				{ 
					targetNotifyMsg = "You are no longer a Hero."; 
					gmBroadcastMsg = "A GM has removed hero status from " + targetPlayer.getName() + "."; 
					targetPlayer.setHero(false); 
					targetPlayer.sendMessage(targetNotifyMsg); 
					updateDatabase(targetPlayer, false, false); 
				} 
				else 
				{ 
					targetNotifyMsg = "You are now a hero, congratulations!"; 
					gmBroadcastMsg = "A GM has given hero status to " + targetPlayer.getName() + "."; 
					targetPlayer.setHero(true); 
					targetPlayer.broadcastPacket(new SocialAction(targetPlayer.getObjectId(), 16)); 
					targetPlayer.sendMessage(targetNotifyMsg); 
					updateDatabase(targetPlayer, true, false); 
				} 
				targetPlayer.broadcastUserInfo(); 
			} 
			else 
			{ 
				_log.info("GM: " + activeChar.getName() + " is trying to set a non Player Target as Hero."); 
				return false; 
			} 
		}                               
		else if (command.startsWith("admin_setnoble")) 
		{ 
			L2Object target = activeChar.getTarget(); 
			if (target instanceof L2PcInstance) 
			{ 
				L2PcInstance targetPlayer = (L2PcInstance) target; 
				boolean isNoble = targetPlayer.isNoble(); 
				String targetNotifyMsg = ""; 
				String gmBroadcastMsg = ""; 
				if (isNoble) 
				{ 
					targetNotifyMsg = "You are no longer a Noble."; 
					gmBroadcastMsg = "A GM has removed noblesse status from " + targetPlayer.getName() + "."; 
					targetPlayer.setNoble(false); 
					targetPlayer.sendMessage(targetNotifyMsg);         
					//general info 
					updateDatabase(targetPlayer, false, false); 
				} 
				else 
				{ 
					targetNotifyMsg = "You are now a noble, congratulations!"; 
					gmBroadcastMsg = "A GM has given noble status to " + targetPlayer.getName() + "."; 
					// now we do the work 
					// ------------------ 
					targetPlayer.setNoble(true); 
					targetPlayer.broadcastPacket(new SocialAction(targetPlayer.getObjectId(), 16)); 
					targetPlayer.sendMessage(targetNotifyMsg); 
					// general info storage and notify 
					// ------------------------------ 
					updateDatabase(activeChar, false, true); 
				} 
				targetPlayer.broadcastUserInfo(); 
			} 
			else 
			{ 
				_log.info("GM: " + activeChar.getName() + " is trying to set a non Player Target as Noble."); 
				return false; 
			} 
		} 
		else if (command.startsWith("admin_setcolor"))
		{
			try
			{
				String val          = command.substring(15);
				L2Object target     = activeChar.getTarget();
				L2PcInstance player = null;
				if (activeChar != target && activeChar.getAccessLevel()<REQUIRED_LEVEL2)
					return false;
				if (target instanceof L2PcInstance) {
					player = (L2PcInstance)target;
				} else {
					return false;
				}
				player.getAppearance().setNameColor(Integer.decode("0x"+val));
				player.sendMessage("Your name color has been changed by a GM");
				player.broadcastUserInfo();
			}
			catch (StringIndexOutOfBoundsException e)
			{   //Case of empty color
				activeChar.sendMessage("You need to specify the new color.");
			}
		}
		else if (command.startsWith("admin_fullfood"))
		{
			L2Object target = activeChar.getTarget();
			if (target instanceof L2PetInstance)
			{
				L2PetInstance targetPet = (L2PetInstance)target;
				targetPet.setCurrentFed(targetPet.getMaxFed());
			}
			else
				activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
		}
		return true;
	}

	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}

	private boolean checkLevel(int level) {
		return (level >= REQUIRED_LEVEL);
	}
	private boolean checkLevel2(int level) {
		return (level >= REQUIRED_LEVEL_VIEW);
	}

	private void listCharacters(L2PcInstance activeChar, int page)
	{
		Collection<L2PcInstance> allPlayers = L2World.getInstance().getAllPlayers();
		L2PcInstance[] players = allPlayers.toArray(new L2PcInstance[allPlayers.size()]);

		int MaxCharactersPerPage = 20;
		int MaxPages = players.length / MaxCharactersPerPage;

		if (players.length > MaxCharactersPerPage * MaxPages)
			MaxPages++;

		//Check if number of users changed
		if (page>MaxPages)
			page=MaxPages;

		int CharactersStart = MaxCharactersPerPage*page;
		int CharactersEnd = players.length;
		if (CharactersEnd - CharactersStart > MaxCharactersPerPage)
			CharactersEnd = CharactersStart + MaxCharactersPerPage;

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("data/html/admin/charlist.htm");
		StringBuilder replyMSG = new StringBuilder();
		for (int x=0; x<MaxPages; x++)
		{
			int pagenr = x + 1;
			replyMSG.append("<center><a action=\"bypass -h admin_show_characters " + x + "\">Page " + pagenr + "</a></center>");
		}
		adminReply.replace("%pages%", replyMSG.toString());
		replyMSG.setLength(0);
		for (int i = CharactersStart; i < CharactersEnd; i++)
		{	//Add player info into new Table row
			replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_info " + players[i].getName() + "\">" + players[i].getName() + "</a></td><td width=110>" + players[i].getTemplate().className + "</td><td width=40>" + players[i].getLevel() + "</td></tr>");
		}
		adminReply.replace("%players%", replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showCharacterInfo(L2PcInstance activeChar, L2PcInstance player)
	{
		if (player == null)
		{
			L2Object target = activeChar.getTarget();
			if (target instanceof L2PcInstance)
				player = (L2PcInstance)target;
			else
				return;
		}
		else
			activeChar.setTarget(player);
		gatherCharacterInfo(activeChar, player,"charinfo.htm");
	}

	/**
	 * @param activeChar
	 * @param player
	 */
	private void gatherCharacterInfo(L2PcInstance activeChar, L2PcInstance player, String filename)
	{
		String ip = "N/A";
		String account = "N/A";
		try
		{
			StringTokenizer clientinfo= new StringTokenizer(player.getClient().toString()," ]:-[");
			clientinfo.nextToken();
			clientinfo.nextToken();
			clientinfo.nextToken();
			account = clientinfo.nextToken();
			clientinfo.nextToken();
			ip = clientinfo.nextToken();
		}
		catch (Exception e) {}
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("data/html/admin/"+filename);
		adminReply.replace("%name%", player.getName());
		adminReply.replace("%level%", String.valueOf(player.getLevel()));
		adminReply.replace("%clan%", String.valueOf(ClanTable.getInstance().getClan(player.getClanId())));
		adminReply.replace("%xp%", String.valueOf(player.getExp()));
		adminReply.replace("%sp%", String.valueOf(player.getSp()));
		adminReply.replace("%class%", player.getTemplate().className);
		adminReply.replace("%ordinal%", String.valueOf(player.getClassId().ordinal()));
		adminReply.replace("%classid%", String.valueOf(player.getClassId()));
		adminReply.replace("%x%", String.valueOf(player.getX()));
		adminReply.replace("%y%", String.valueOf(player.getY()));
		adminReply.replace("%z%", String.valueOf(player.getZ()));
		adminReply.replace("%currenthp%", String.valueOf((int)player.getCurrentHp()));
		adminReply.replace("%maxhp%", String.valueOf(player.getMaxHp()));
		adminReply.replace("%karma%", String.valueOf(player.getKarma()));
		adminReply.replace("%currentmp%", String.valueOf((int)player.getCurrentMp()));
		adminReply.replace("%maxmp%", String.valueOf(player.getMaxMp()));
		adminReply.replace("%pvpflag%", String.valueOf(player.getPvpFlag()));
		adminReply.replace("%currentcp%", String.valueOf((int)player.getCurrentCp()));
		adminReply.replace("%maxcp%", String.valueOf(player.getMaxCp()));
		adminReply.replace("%pvpkills%", String.valueOf(player.getPvpKills()));
		adminReply.replace("%pkkills%", String.valueOf(player.getPkKills()));
		adminReply.replace("%currentload%", String.valueOf(player.getCurrentLoad()));
		adminReply.replace("%maxload%", String.valueOf(player.getMaxLoad()));
		adminReply.replace("%percent%", String.valueOf(Util.roundTo(((float)player.getCurrentLoad()/(float)player.getMaxLoad())*100, 2)));
		adminReply.replace("%patk%", String.valueOf(player.getPAtk(null)));
		adminReply.replace("%matk%", String.valueOf(player.getMAtk(null,null)));
		adminReply.replace("%pdef%", String.valueOf(player.getPDef(null)));
		adminReply.replace("%mdef%", String.valueOf(player.getMDef(null, null)));
		adminReply.replace("%accuracy%", String.valueOf(player.getAccuracy()));
		adminReply.replace("%evasion%", String.valueOf(player.getEvasionRate(null)));
		adminReply.replace("%critical%", String.valueOf(player.getCriticalHit(null,null)));
		adminReply.replace("%runspeed%", String.valueOf(player.getRunSpeed()));
		adminReply.replace("%patkspd%", String.valueOf(player.getPAtkSpd()));
		adminReply.replace("%matkspd%", String.valueOf(player.getMAtkSpd()));
		adminReply.replace("%access%",String.valueOf(player.getAccessLevel()));
		adminReply.replace("%account%",account);
		adminReply.replace("%ip%",ip);
		activeChar.sendPacket(adminReply);
	}

	private void setTargetKarma(L2PcInstance activeChar, int newKarma) {
		// function to change karma of selected char
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance)target;
		else
			return;

		if ( newKarma >= 0 )
		{
			// for display
			int oldKarma = player.getKarma();
			// update karma
			player.setKarma(newKarma);
			//Common character information
			player.sendPacket( new SystemMessage(SystemMessageId.YOUR_KARMA_HAS_BEEN_CHANGED_TO).addString(String.valueOf(newKarma)));
			//Admin information
			activeChar.sendMessage("Successfully Changed karma for "+player.getName()+" from (" + oldKarma + ") to (" + newKarma + ").");
			if (Config.DEBUG)
				_log.fine("[SET KARMA] [GM]"+activeChar.getName()+" Changed karma for "+player.getName()+" from (" + oldKarma + ") to (" + newKarma + ").");
		}
		else
		{
			// tell admin of mistake
			activeChar.sendMessage("You must enter a value for karma greater than or equal to 0.");
			if (Config.DEBUG)
				_log.fine("[SET KARMA] ERROR: [GM]"+activeChar.getName()+" entered an incorrect value for new karma: " + newKarma + " for "+player.getName()+".");
		}
	}

	private void adminModifyCharacter(L2PcInstance activeChar, String modifications)
	{
		L2Object target = activeChar.getTarget();

		if (!(target instanceof L2PcInstance))
			return;

		L2PcInstance player = (L2PcInstance)target;
		StringTokenizer st = new StringTokenizer(modifications);

		if (st.countTokens() != 6) {
			editCharacter(player);
			return;
		}

		String hp = st.nextToken();
		String mp = st.nextToken();
		String cp = st.nextToken();
		String pvpflag = st.nextToken();
		String pvpkills = st.nextToken();
		String pkkills = st.nextToken();

		int hpval = Integer.parseInt(hp);
		int mpval = Integer.parseInt(mp);
		int cpval = Integer.parseInt(cp);
		int pvpflagval = Integer.parseInt(pvpflag);
		int pvpkillsval = Integer.parseInt(pvpkills);
		int pkkillsval = Integer.parseInt(pkkills);

		//Common character information
		player.sendMessage("Admin has changed your stats." + "  HP: " + hpval + "  MP: " + mpval + "  CP: " + cpval + "  PvP Flag: " + pvpflagval + " PvP/PK " + pvpkillsval + "/" + pkkillsval);
		player.setCurrentHp(hpval);
		player.setCurrentMp(mpval);
		player.setCurrentCp(cpval);
		player.setPvpFlag(pvpflagval);
		player.setPvpKills(pvpkillsval);
		player.updatePvPColor();
		player.setPkKills(pkkillsval);
		player.updatePkColor();

		// Save the changed parameters to the database.
		player.store();

		StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdate.CUR_HP, hpval);
		su.addAttribute(StatusUpdate.MAX_HP, player.getMaxHp());
		su.addAttribute(StatusUpdate.CUR_MP, mpval);
		su.addAttribute(StatusUpdate.MAX_MP, player.getMaxMp());
		su.addAttribute(StatusUpdate.CUR_CP, cpval);
		su.addAttribute(StatusUpdate.MAX_CP, player.getMaxCp());
		player.sendPacket(su);

		//Admin information
		player.sendMessage("Changed stats of " + player.getName() + "." + "  HP: " + hpval + "  MP: " + mpval + "  CP: " + cpval + "  PvP: " + pvpflagval + " / " + pvpkillsval );

		if (Config.DEBUG)
			_log.fine("[GM]"+activeChar.getName()+" changed stats of "+player.getName()+". " + " HP: "+hpval+" MP: "+mpval+" CP: " + cpval + " PvP: "+pvpflagval+" / "+pvpkillsval);

		showCharacterInfo(activeChar, null); //Back to start

		player.broadcastPacket(new CharInfo(player));
		player.sendPacket(new UserInfo(player));
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.decayMe();
		player.spawnMe(activeChar.getX(),activeChar.getY(),activeChar.getZ());
	}

	private void editCharacter(L2PcInstance activeChar)
	{
		L2Object target = activeChar.getTarget();
		if (!(target instanceof L2PcInstance))
			return;
		L2PcInstance player = (L2PcInstance)target;
		gatherCharacterInfo(activeChar, player,"charedit.htm");
	}

	/**
	 * @param activeChar
	 * @param CharacterToFind
	 */
	private void findCharacter(L2PcInstance activeChar, String CharacterToFind)
	{
		int CharactersFound = 0;
		String name;
		Collection<L2PcInstance> allPlayers = L2World.getInstance().getAllPlayers();
		L2PcInstance[] players = allPlayers.toArray(new L2PcInstance[allPlayers.size()]);
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("data/html/admin/charfind.htm");
		StringBuilder replyMSG = new StringBuilder();
		for (int i = 0; i < players.length; i++)
		{	//Add player info into new Table row
			name = players[i].getName();
			if (name.toLowerCase().contains(CharacterToFind.toLowerCase()))
			{
				CharactersFound = CharactersFound+1;
				replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_list "+name+"\">"+name+"</a></td><td width=110>" + players[i].getTemplate().className + "</td><td width=40>"+players[i].getLevel()+"</td></tr>");
			}
			if (CharactersFound > 20)
				break;
		}
		adminReply.replace("%results%", replyMSG.toString());
		replyMSG.setLength(0);
		if (CharactersFound==0)
			replyMSG.append("s. Please try again.");
		else if (CharactersFound > 20)
		{
			adminReply.replace("%number%", " more than 20");
			replyMSG.append("s.<br>Please refine your search to see all of the results.");
		}
		else if (CharactersFound==1)
			replyMSG.append(".");
		else
			replyMSG.append("s.");
		adminReply.replace("%number%", String.valueOf(CharactersFound));
		adminReply.replace("%end%", replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	/**
	 * @param activeChar
	 * @param IpAdress
	 * @throws IllegalArgumentException
	 */
	private void findCharactersPerIp(L2PcInstance activeChar, String IpAdress) throws IllegalArgumentException
	{
		if(!IpAdress.matches("^(?:(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))\\.){3}(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))$"))
			throw new IllegalArgumentException("Malformed IPv4 number");
		
		Collection<L2PcInstance> allPlayers = L2World.getInstance().getAllPlayers();
		L2PcInstance[] players = allPlayers.toArray(new L2PcInstance[allPlayers.size()]);
		allPlayers = null;

		int CharactersFound = 0;

		String name, ip = "0.0.0.0";
		StringBuilder replyMSG = new StringBuilder();
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("data/html/admin/ipfind.htm");

		for(L2PcInstance player : players)
		{
			if(player.getClient()==null
					|| player.getClient().getConnection()==null
					|| player.getClient().getConnection().getInetAddress()==null
					|| player.getClient().getConnection().getInetAddress().getHostAddress()==null){
				
				continue;
				
			}	
			
			ip = player.getClient().getConnection().getInetAddress().getHostAddress();
			
			if(ip.equals(IpAdress))
			{
				name = player.getName();
				CharactersFound = CharactersFound + 1;
				replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_list " + name + "\">" + name + "</a></td><td width=110>" + player.getTemplate().className + "</td><td width=40>" + player.getLevel() + "</td></tr>");
			}

			if(CharactersFound > 20)
			{
				break;
			}
		}
		
		name = null;
		players = null;

		adminReply.replace("%results%", replyMSG.toString());
		replyMSG.setLength(0);

		if(CharactersFound == 0)
		{
			replyMSG.append("s. Maybe they got d/c? :)");
		}
		else if(CharactersFound > 20)
		{
			adminReply.replace("%number%", " more than " + String.valueOf(CharactersFound));
			replyMSG.append("s.<br>In order to avoid you a client crash I won't <br1>display results beyond the 20th character.");
		}
		else if(CharactersFound == 1)
		{
			replyMSG.append(".");
		}
		else
		{
			replyMSG.append("s.");
		}
		adminReply.replace("%ip%", ip);
		adminReply.replace("%number%", String.valueOf(CharactersFound));
		adminReply.replace("%end%", replyMSG.toString());
		activeChar.sendPacket(adminReply);
		ip = null;
		replyMSG = null;
		adminReply = null;
	}


	/**
	 * @param activeChar
	 * @param characterName
	 * @throws IllegalArgumentException
	 */
	private void findCharactersPerAccount(L2PcInstance activeChar, String characterName) throws IllegalArgumentException
	{
		if (characterName.matches(Config.CNAME_TEMPLATE))
		{
			String account=null;
			Map<Integer, String> chars;
			L2PcInstance player = L2World.getInstance().getPlayer(characterName);
			if (player == null)
				throw new IllegalArgumentException("Player doesn't exist");
			chars=player.getAccountChars();
			account = player.getAccountName();
			StringBuilder replyMSG = new StringBuilder();
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setFile("data/html/admin/accountinfo.htm");
			for (String charname : chars.values())
				replyMSG.append(charname+"<br1>");
			adminReply.replace("%characters%", replyMSG.toString());
			adminReply.replace("%account%", account);
			adminReply.replace("%player%", characterName);
			activeChar.sendPacket(adminReply);
		}
		else
			throw new IllegalArgumentException("Malformed character name");
	}
	private void updateDatabase(L2PcInstance player, boolean newHero, boolean newNoble) 
	{ 
		Connection con = null; 
		try 
		{ 
			//prevents any NPE. 
			// ---------------- 
			if (player == null) 
			return; 

			//Define more variables 
			// --------------------------------------------- 
			String charName = player.getName(); 
			int charId = player.getObjectId(); 
			boolean insert = newHero || newNoble; 

			// Database Connection 
			// --------------------------------------------- 
			con = L2DatabaseFactory.getInstance().getConnection(); 
			PreparedStatement stmt = con.prepareStatement(insert ? DATA_INSERT : DATA_DELETE); 

			// custom variables 
			// --------------------------------------------- 
			boolean isNoble = player.isNoble(); 
			boolean isHero = player.isHero(); 
    
			//if it is a new hero insert proper data 
			// --------------------------------------------- 
			if (newHero) 
			{ 
				stmt.setInt(1, charId); 
				stmt.setString(2, charName); 
				stmt.setInt(3, 1); //sets new char as hero 
				stmt.setInt(4, isNoble ? 1 : 0); //keeps noble data if already set 
				stmt.execute(); 
				stmt.close(); 
			} 
			// if it is a new noble inserts and keeps proper data 
			// --------------------------------------------- 
			else if (newNoble) 
			{ 
				stmt.setInt(1, charId); 
				stmt.setString(2, charName); 
				stmt.setInt(3, isHero ? 1 : 0); //keeps hero data if any 
				stmt.setInt(4, 1); //sets char as noble 
				stmt.execute(); 
				stmt.close(); 
			} 
			else 
			// not a new hero or noble so we delete data 
			// --------------------------------------------- 
			{ 
				stmt.setInt(1, newHero ? 0 : 1); 
				stmt.setInt(2, newNoble ? 0 : 1); 
				stmt.setInt(3, charId); 
				stmt.execute(); 
				stmt.close(); 
			} 
		} 
		catch (Exception e) 
		{ 
			System.out.println("Error: could not update database: "); 
		} 
		finally 
		{ 
			try 
			{ 
				if (con != null) 
				con.close(); 
			} 
			catch (Exception e) 
			{ 
				System.out.println("Error: could not update database: "); 
			} 
		} 
	} 

	// Updates That Will be Executed by MySQL 
	// --------------------------------------------- 
	String  DATA_INSERT     = "REPLACE INTO characters (charId, char_name, nobless ) VALUES (?,?,?)"; 
	String  DATA_DELETE     = "UPDATE characters SET noble = ? WHERE charId=?"; 
}
