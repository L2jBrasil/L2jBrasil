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
package com.it.br.gameserver.network.clientpackets;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.Announcements;
import com.it.br.gameserver.GmListTable;
import com.it.br.gameserver.SevenSigns;
import com.it.br.gameserver.TaskPriority;
import com.it.br.gameserver.cache.HtmCache;
import com.it.br.gameserver.communitybbs.Manager.RegionBBSManager;
import com.it.br.gameserver.datatables.GMSkillTable;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.xml.MapRegionTable;
import com.it.br.gameserver.handler.AdminCommandHandler;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.instancemanager.ClanHallManager;
import com.it.br.gameserver.instancemanager.CoupleManager;
import com.it.br.gameserver.instancemanager.CrownManager;
import com.it.br.gameserver.instancemanager.DimensionalRiftManager;
import com.it.br.gameserver.instancemanager.PetitionManager;
import com.it.br.gameserver.instancemanager.SiegeManager;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2ClanMember;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.Olympiad.Olympiad;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.model.entity.ClanHall;
import com.it.br.gameserver.model.entity.Couple;
import com.it.br.gameserver.model.entity.Hero;
import com.it.br.gameserver.model.entity.L2Event;
import com.it.br.gameserver.model.entity.Siege;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.CreatureSay;
import com.it.br.gameserver.network.serverpackets.Earthquake;
import com.it.br.gameserver.network.serverpackets.EtcStatusUpdate;
import com.it.br.gameserver.network.serverpackets.ExShowScreenMessage;
import com.it.br.gameserver.network.serverpackets.ExStorageMaxCount;
import com.it.br.gameserver.network.serverpackets.FriendList;
import com.it.br.gameserver.network.serverpackets.HennaInfo;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.PledgeShowMemberListAll;
import com.it.br.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import com.it.br.gameserver.network.serverpackets.PledgeSkillList;
import com.it.br.gameserver.network.serverpackets.PledgeStatusChanged;
import com.it.br.gameserver.network.serverpackets.QuestList;
import com.it.br.gameserver.network.serverpackets.ShortCutInit;
import com.it.br.gameserver.network.serverpackets.SignsSky;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.network.serverpackets.UserInfo;
import com.it.br.gameserver.templates.L2Item;
import com.it.br.gameserver.util.Util;

public class EnterWorld extends L2GameClientPacket
{
	private static final String _C__03_ENTERWORLD = "[C] 03 EnterWorld";
	//private static Logger _log = Logger.getLogger(EnterWorld.class.getName());
	public TaskPriority getPriority() { return TaskPriority.PR_URGENT; }
	long _daysleft;
	SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy");
	private static final SimpleDateFormat fmt = new SimpleDateFormat("H:mm.");

	@Override
	protected void readImpl()// this is just a trigger packet. it has no content
	{}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			_log.warning("EnterWorld failed! activeChar is null...");
			getClient().closeNow();
		    return;
		}
		if (!activeChar.isAttackable())
		{
			_log.warning("EnterWorld failed! activeChar is not Attackable...");
			getClient().closeNow();
		    return;
		}

		try { activeChar.decayMe(); } catch (Throwable t) {}

		if (L2World.getInstance().findObject(activeChar.getObjectId()) != null)
		{
			if(Config.DEBUG)
				_log.warning("User already exist in OID map! User "+activeChar.getName()+" is character clone");
			// check for over enchant
			for (L2ItemInstance i : activeChar.getInventory().getItems())
			{
				if (i.isEquipable() && !activeChar.isGM() || !i.isEquipable() && !activeChar.isGM())
				{
					int itemType2 = i.getItem().getType2();
					if (itemType2 == L2Item.TYPE2_WEAPON)
					{
						if (i.getEnchantLevel() > Config.ENCHANT_MAX_ALLOWED_WEAPON)
						{
							if (i.getEnchantLevel() > Config.MAX_ITEM_ENCHANT_KICK)
							// Delete Item Over enchanted
							activeChar.getInventory().destroyItem(null, i, activeChar, null);
							// Message to Player
							activeChar.sendMessage("[Server]:You have Items over enchanted you will be kikked!");
							// Punishment e log in audit
							Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " have item Overenchanted ", Config.DEFAULT_PUNISH);
							// Log in console
							_log.info("#### ATTENCTION ####");
							_log.info(i + " item has been removed from player.");
						}
					}
					if (itemType2 == L2Item.TYPE2_SHIELD_ARMOR)
					{
						if (i.getEnchantLevel() > Config.ENCHANT_MAX_ALLOWED_ARMOR)
						{
							// Delete Item Over enchanted
							activeChar.getInventory().destroyItem(null, i, activeChar, null);
							// Message to Player
							activeChar.sendMessage("[Server]:You have Items over enchanted you will be kikked!");
							// Punishment e log in audit
							Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " have item Overenchanted ", Config.DEFAULT_PUNISH);
							// Log in console
							_log.info("#### ATTENCTION ####");
							_log.info(i + " item has been removed from player.");
						}
					}
					if (itemType2 == L2Item.TYPE2_ACCESSORY)
					{
						if (i.getEnchantLevel() > Config.ENCHANT_MAX_ALLOWED_JEWELRY)
						{
							// Delete Item Over enchanted
							activeChar.getInventory().destroyItem(null, i, activeChar, null);
							// Message to Player
							activeChar.sendMessage("[Server]:You have Items over enchanted you will be kikked!");
							// Punishment e log in audit
							Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " have item Overenchanted ", Config.DEFAULT_PUNISH);
							// Log in console
							_log.info("#### ATTENCTION ####");
							_log.info(i + " item has been removed from player.");
						}
					}
				}
			}
		}

		if (activeChar.isGM())
        {
        	if (Config.GM_STARTUP_INVULNERABLE && (!Config.ALT_PRIVILEGES_ADMIN && activeChar.getAccessLevel() >= Config.admin_invul
        			  || Config.ALT_PRIVILEGES_ADMIN && AdminCommandHandler.getInstance().checkPrivileges(activeChar, "admin_invul")))
        		activeChar.setIsInvul(true);

        	if(Config.GM_SUPER_HASTE)
                SkillTable.getInstance().getInfo(7029, 4).getEffects(activeChar, activeChar);

            if (Config.GM_STARTUP_INVISIBLE && (!Config.ALT_PRIVILEGES_ADMIN && activeChar.getAccessLevel() >= Config.admin_invis
                      || Config.ALT_PRIVILEGES_ADMIN && AdminCommandHandler.getInstance().checkPrivileges(activeChar, "admin_invisible")))
                activeChar.getAppearance().setInvisible();

            if (Config.GM_STARTUP_SILENCE && (!Config.ALT_PRIVILEGES_ADMIN && activeChar.getAccessLevel() >= Config.admin_admin
                      || Config.ALT_PRIVILEGES_ADMIN && AdminCommandHandler.getInstance().checkPrivileges(activeChar, "admin_silence")))
                activeChar.setMessageRefusal(true);

            if(Config.GM_SPECIAL_EFFECT)
            {
                Earthquake eq = new Earthquake(activeChar.getX(), activeChar.getY(), activeChar.getZ(), 70, 30);
                activeChar.broadcastPacket(eq);
            }

            if(Config.GM_GIVE_SPECIAL_SKILLS)
                GMSkillTable.getInstance().addSkills(activeChar);

            if (Config.GM_STARTUP_AUTO_LIST && (!Config.ALT_PRIVILEGES_ADMIN && activeChar.getAccessLevel() >= Config.admin_admin
                      || Config.ALT_PRIVILEGES_ADMIN && AdminCommandHandler.getInstance().checkPrivileges(activeChar, "admin_gmliston")))
            	GmListTable.getInstance().addGm(activeChar, false);
            else
            	GmListTable.getInstance().addGm(activeChar, true);

            if(Config.GM_STARTUP_DIET && AdminCommandHandler.getInstance().checkPrivileges(activeChar, "admin_diet"))
                activeChar.setDietMode(true);

            if (Config.GM_NAME_COLOR_ENABLED)
            {
                if (activeChar.getAccessLevel() >= 101)
                    activeChar.getAppearance().setNameColor(Config.ADMIN_NAME_COLOR);
                else if (activeChar.getAccessLevel() >= 75 && activeChar.getAccessLevel() <= 100)
                    activeChar.getAppearance().setNameColor(Config.GM_NAME_COLOR);
            }

            if (Config.GM_TITLE_COLOR_ENABLED)
            {
            	if (activeChar.getAccessLevel() >= 101)
            		activeChar.getAppearance().setTitleColor(Config.ADMIN_TITLE_COLOR);
            	else if (activeChar.getAccessLevel() >= 75 && activeChar.getAccessLevel() <= 100)
            		activeChar.getAppearance().setTitleColor(Config.GM_TITLE_COLOR);
            }
        }

        if(Config.ALLOW_MESSAGE_ON_ENTER)
        {
        	activeChar.sendPacket(new ExShowScreenMessage(Config.MESSAGE_ON_ENTER, 10000, 2, true));
        }

        if(Config.ONLINE_PLAYERS_ON_LOGIN)
        {
            int PLAYERS_ONLINE = L2World.getInstance().getAllPlayers().size() + Config.PLAYERS_ONLINE_TRICK;
            activeChar.sendMessage((new StringBuilder()).append("Players online: ").append(PLAYERS_ONLINE).toString());
        }

        if (Config.CLAN_LEADER_COLOR_ENABLED)
        {
            if(activeChar.isClanLeader())
 		    {
			   activeChar.getAppearance().setNameColor(Config.CLAN_LEADER_NAME_COLOR);
			   activeChar.getAppearance().setTitleColor(Config.CLAN_LEADER_TITLE_COLOR);
        	}
        }

        if (Config.ANNOUNCE_GM_LOGIN) 
        { 
        	if (activeChar.getAccessLevel() >= 101) 
        	{ 
        		Announcements.getInstance().announceToAll("Admin: "+activeChar.getName()+" has been logged in."); 
        	} 
        	else if (activeChar.getAccessLevel() >= 75 && activeChar.getAccessLevel() <= 100) 
        	{ 
        		Announcements.getInstance().announceToAll("GM: "+activeChar.getName()+" has been logged in."); 
        	} 
        } 

        if (Config.PLAYER_SPAWN_PROTECTION > 0)
		{
        	activeChar.setProtection(true);
			if (Config.PLAYER_SPAWN_PROTECTION_EFFECT)
				activeChar.startAbnormalEffect(Config.PLAYER_EFFECT_ID);
		}

        if(!activeChar.isGM() && (activeChar.getName().length() < 3 || activeChar.getName().length() > 16 || !Util.isAlphaNumeric(activeChar.getName()) || !isValidName(activeChar.getName())))
        {
            _log.warning((new StringBuilder()).append("Charname: ").append(activeChar.getName()).append(" is invalid. EnterWorld failed.").toString());
            getClient().closeNow();
            return;
        }

        activeChar.sendMessage("SVR time is " + fmt.format(new Date(System.currentTimeMillis())));
		activeChar.spawnMe(activeChar.getX(), activeChar.getY(), activeChar.getZ());

		if (L2Event.active && L2Event.connectionLossData.containsKey(activeChar.getName()) && L2Event.isOnEvent(activeChar))
            L2Event.restoreChar(activeChar);
        else if (L2Event.connectionLossData.containsKey(activeChar.getName()))
            L2Event.restoreAndTeleChar(activeChar);

		if (SevenSigns.getInstance().isSealValidationPeriod())
			sendPacket(new SignsSky());

        // buff and status icons
		if (Config.STORE_SKILL_COOLTIME)
            activeChar.restoreEffects();

        activeChar.sendPacket(new EtcStatusUpdate(activeChar));

        // engage and notify Partner
        if(Config.L2JMOD_ALLOW_WEDDING)
        {
            engage(activeChar);
            notifyPartner(activeChar,activeChar.getPartnerId());
        }

        if (Config.ANNOUNCE_CASTLE_LORDS)
        	notifyCastleOwner(activeChar);

        if (Config.ANNOUNCE_VIP_LOGIN && activeChar.isVip())
        {
        	Announcements.getInstance().announceToAll("Vip: "+activeChar.getName()+" has been logged in.");
        }

        if (Config.ANNOUNCE_AIO_LOGIN && activeChar.isAio())
        {
        	Announcements.getInstance().announceToAll("Aio: "+activeChar.getName()+" has been logged in.");
        }

        if (activeChar.getAllEffects() != null)
        {
            for (L2Effect e : activeChar.getAllEffects())
            {
                if (e.getEffectType() == L2Effect.EffectType.HEAL_OVER_TIME)
                {
                    activeChar.stopEffects(L2Effect.EffectType.HEAL_OVER_TIME);
                    activeChar.removeEffect(e);
                }

                if (e.getEffectType() == L2Effect.EffectType.COMBAT_POINT_HEAL_OVER_TIME)
                {
                    activeChar.stopEffects(L2Effect.EffectType.COMBAT_POINT_HEAL_OVER_TIME);
                    activeChar.removeEffect(e);
                }
            }
        }

        // apply augmentation bonus for equipped items
		for (L2ItemInstance temp : activeChar.getInventory().getAugmentedItems())
		{
			if (temp != null && temp.isEquipped())
				temp.getAugmentation().applyBonus(activeChar);
		}

        //Expand Skill
        ExStorageMaxCount esmc = new ExStorageMaxCount(activeChar);
        activeChar.sendPacket(esmc);

        activeChar.getMacroses().sendUpdate();
        SystemMessage sm = new SystemMessage(SystemMessageId.WELCOME_TO_LINEAGE);
        sendPacket(sm);

        SevenSigns.getInstance().sendCurrentPeriodMsg(activeChar);
        Announcements.getInstance().showAnnouncements(activeChar);
        CrownManager.getInstance().checkCrowns(activeChar);

		Quest.playerEnter(activeChar);
		activeChar.sendPacket(new QuestList());
		
		if (!Config.ALT_DEV_NO_TUTORIAL)
			loadTutorial(activeChar);

 		if (Config.PVP_COLOR_SYSTEM_ENABLED)
 		{
 			activeChar.updatePvPColor();
 		}
 		if (Config.PK_COLOR_SYSTEM_ENABLED)
 		{
 			activeChar.updatePkColor();
 		}

        if (Config.SERVER_NEWS)
		{
			String serverNews = HtmCache.getInstance().getHtm("data/html/servnews.htm");
			if (serverNews != null)
				sendPacket(new NpcHtmlMessage(1, serverNews));
		}

	    // check for ilegal skills 
	    if (Config.L2JMOD_CHECK_SKILLS_ON_ENTER && !Config.ALT_GAME_SKILL_LEARN && !activeChar.isAio() && (activeChar.isVip() && !Config.ENABLE_VIP_SYSTEM)) 
	    	activeChar.checkAllowedSkills();

	    if(Config.GM_WELCOME_HTM && activeChar.isGM() && isValidName(activeChar.getName()))
        {
            String Welcome_Path = "data/html/mods/welcome/welcomegm.htm";
            File mainText = new File(Config.DATAPACK_ROOT, Welcome_Path);
            if(mainText.exists())
            {
                NpcHtmlMessage html = new NpcHtmlMessage(1);
                html.setFile(Welcome_Path);
                html.replace("%name%", activeChar.getName());
                sendPacket(html);
            }
        }
        else if(Config.WELCOME_HTM && isValidName(activeChar.getName()))
        {
            String Welcome_Path = "data/html/mods/welcome/welcome.htm";
            File mainText = new File(Config.DATAPACK_ROOT, Welcome_Path);
            if(mainText.exists())
            {
                NpcHtmlMessage html = new NpcHtmlMessage(1);
                html.setFile(Welcome_Path);
                html.replace("%name%", activeChar.getName());
                html.replace("%rate_xp%", String.valueOf(Config.RATE_XP));
                html.replace("%rate_sp%", String.valueOf(Config.RATE_SP));
                html.replace("%rate_party_xp%", String.valueOf(Config.RATE_PARTY_XP));
                html.replace("%rate_party_sp%", String.valueOf(Config.RATE_PARTY_SP));
                html.replace("%rate_adena%", String.valueOf(Config.RATE_DROP_ADENA));
                html.replace("%rate_items%", String.valueOf(Config.RATE_DROP_ITEMS));
                html.replace("%rate_spoil%", String.valueOf(Config.RATE_DROP_SPOIL));
                html.replace("%rate_drop_manor%", String.valueOf(Config.RATE_DROP_MANOR));
                html.replace("%rate_quest_reward%", String.valueOf(Config.RATE_QUESTS_REWARD));
                html.replace("%rate_drop_quest%", String.valueOf(Config.RATE_DROP_QUEST));
                html.replace("%pet_rate_xp%", String.valueOf(Config.PET_XP_RATE));
                html.replace("%sineater_rate_xp%", String.valueOf(Config.SINEATER_XP_RATE));
                html.replace("%pet_food_rate%", String.valueOf(Config.PET_FOOD_RATE));
                sendPacket(html);
            }
        }

		if(activeChar.isVip())
			onEnterVip(activeChar);

		if(activeChar.isAio())
			onEnterAio(activeChar);

		if(Config.ALLOW_VIP_NCOLOR && activeChar.isVip())
		activeChar.getAppearance().setNameColor(Config.VIP_NCOLOR);

		if(Config.ALLOW_VIP_TCOLOR && activeChar.isVip())
		activeChar.getAppearance().setTitleColor(Config.VIP_TCOLOR);

		if(Config.ALLOW_AIO_NCOLOR && activeChar.isAio())
		activeChar.getAppearance().setNameColor(Config.AIO_NCOLOR);

		if(Config.ALLOW_AIO_TCOLOR && activeChar.isAio())
		activeChar.getAppearance().setTitleColor(Config.AIO_TCOLOR);

        if (Config.SHOW_WELCOME_PM)
        {
           CreatureSay np = new CreatureSay(0, Say2.TELL,Config.PM_FROM,Config.PM_TEXT1); 
           CreatureSay na = new CreatureSay(0, Say2.TELL,Config.PM_FROM,Config.PM_TEXT2); 
           activeChar.sendPacket(np); 
           activeChar.sendPacket(na);
        }     

		PetitionManager.getInstance().checkPetitionMessages(activeChar);
        // send user info again .. just like the real client
        //sendPacket(ui);

        if (activeChar.getClanId() != 0 && activeChar.getClan() != null)
        {
        	sendPacket(new PledgeShowMemberListAll(activeChar.getClan(), activeChar));
        	sendPacket(new PledgeStatusChanged(activeChar.getClan()));
        }

		if (activeChar.isAlikeDead())
		{
			// no broadcast needed since the player will already spawn dead to others
			// FIXME call Die Packet direct not was working, not appering window for revive actions when players logout dead, so call method doDie
			activeChar.doDie(activeChar); 
		}

        if (Hero.getInstance().getHeroes() != null && Hero.getInstance().getHeroes().containsKey(activeChar.getObjectId()))
        	activeChar.setHero(true);

        setPledgeClass(activeChar);

		//add char to online characters
		activeChar.setOnlineStatus(true);
        notifyFriends(activeChar);
		notifyClanMembers(activeChar);
		notifySponsorOrApprentice(activeChar);
		activeChar.onPlayerEnter();

        if(Config.PCB_ENABLE)
        {
            activeChar.showPcBangWindow();
        }

		for (L2ItemInstance i : activeChar.getWarehouse().getItems())
		{
			if (i.isTimeLimitedItem())	
				i.scheduleLifeTimeTask();
		}

		if (Olympiad.getInstance().playerInStadia(activeChar))
 	    {
 		    activeChar.doRevive();
 		    activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
 		    activeChar.sendMessage("You have been teleported to the nearest town due to you being in an Olympiad Stadium.");
 	    }

        if (DimensionalRiftManager.getInstance().checkIfInRiftZone(activeChar.getX(), activeChar.getY(), activeChar.getZ(), false))
        {
            DimensionalRiftManager.getInstance().teleportToWaitingRoom(activeChar);
        }

		if (activeChar.getClanJoinExpiryTime() > System.currentTimeMillis())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED));
		}

		if (activeChar.getClan() != null)
		{
			activeChar.sendPacket(new PledgeSkillList(activeChar.getClan()));

			for (Siege siege : SiegeManager.getInstance().getSieges())
			{
				if (!siege.getIsInProgress())
				{
					continue;
				}

				if (siege.checkIsAttacker(activeChar.getClan()))
				{
					activeChar.setSiegeState((byte) 1);
					break;
				}
				else if (siege.checkIsDefender(activeChar.getClan()))
				{
					activeChar.setSiegeState((byte) 2);
					break;
				}
			}

			ClanHall clanHall = ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan());
			if(clanHall != null)
			{
				if(!clanHall.getPaid())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW));
				}
			}
		}

		if (!activeChar.isGM() && activeChar.getSiegeState() < 2 && activeChar.isInsideZone(L2Character.ZONE_SIEGE))
		{
            // Attacker or spectator logging in to a siege zone. Actually should be checked for inside castle only?
			activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
            activeChar.sendMessage("You have been teleported to the nearest town due to you being in siege zone");
		}

		RegionBBSManager.getInstance().changeCommunityBoard();

        /*if(Config.GAMEGUARD_ENFORCE) - disabled by KenM will be reenabled later
            activeChar.sendPacket(new GameGuardQuery());*/

        TvTEvent.onLogin(activeChar);

		if(!activeChar.checkMultiBox())
		{ //means that it's not ok multiBox situation, so logout
			activeChar.sendMessage("I'm sorry, but multibox is not allowed here.");
			activeChar.logout();
		}

        activeChar.decayMe();
        activeChar.spawnMe();

        sendPacket(new UserInfo(activeChar));
        sendPacket(new HennaInfo(activeChar));
        sendPacket(new FriendList(activeChar));
        sendPacket(new ItemList(activeChar, false));
        sendPacket(new ShortCutInit(activeChar));
        activeChar.sendSkillList();
        activeChar.broadcastUserInfo();
        activeChar.sendPacket(new EtcStatusUpdate(activeChar));
	}

    /**
     * @param cha
     */
    private static void engage(L2PcInstance cha)
    {
        int _chaid = cha.getObjectId();

        for(Couple cl: CoupleManager.getInstance().getCouples())
        {
           if(cl.getPlayer1Id()==_chaid || cl.getPlayer2Id()==_chaid)
            {
                if(cl.getMaried())
                    cha.setMarried(true);

                cha.setCoupleId(cl.getId());

                if(cl.getPlayer1Id()==_chaid)
                    cha.setPartnerId(cl.getPlayer2Id());
                else
                    cha.setPartnerId(cl.getPlayer1Id());
            }
        }
    }

    /**
     * @param cha 
     * @param partnerId 
     */
    private static void notifyPartner(L2PcInstance cha, int partnerId)
    {
        if(cha == null)
            return;
        if(cha.getPartnerId() != 0)
        {
            L2PcInstance partner = (L2PcInstance)L2World.getInstance().findObject(cha.getPartnerId());
            if(cha.isMarried() && Config.L2JMOD_WEDDING_COLOR_NAME)
                cha.getAppearance().setNameColor(Config.L2JMOD_WEDDING_COLOR_NAMES);
            if(partner != null && partner.getAppearance().getSex() == cha.getAppearance().getSex() && cha.isMarried() && Config.L2JMOD_WEDDING_COLOR_NAME)
            {
                if(cha.getAppearance().getSex())
                {
                    cha.getAppearance().setNameColor(Config.L2JMOD_WEDDING_COLOR_NAMES_LIZ);
                    partner.getAppearance().setNameColor(Config.L2JMOD_WEDDING_COLOR_NAMES_LIZ);
                } 
                else
                {
                    cha.getAppearance().setNameColor(Config.L2JMOD_WEDDING_COLOR_NAMES_GEY);
                    partner.getAppearance().setNameColor(Config.L2JMOD_WEDDING_COLOR_NAMES_GEY);
                }
                partner.sendMessage("Your Partner has logged in");
				partner.broadcastUserInfo();
                //partner.sendPacket(new UserInfo(partner));
            }
            partner = null;
        }
    }

	/**
	 * @param cha
	 */
	@SuppressWarnings("null")
	private static void notifyFriends(L2PcInstance cha)
	{
		Connection con = null;

		try 
		{
		    con = L2DatabaseFactory.getInstance().getConnection();
		    PreparedStatement statement;
		    statement = con.prepareStatement("SELECT friend_name FROM character_friends WHERE char_id=?");
		    statement.setInt(1, cha.getObjectId());
		    ResultSet rset = statement.executeQuery();

		    L2PcInstance friend;
            String friendName;

            SystemMessage sm = new SystemMessage(SystemMessageId.FRIEND_S1_HAS_LOGGED_IN);
            sm.addString(cha.getName());

            while (rset.next())
            {
                friendName = rset.getString("friend_name");

                friend = L2World.getInstance().getPlayer(friendName);

                if (friend != null) //friend logged in.
                {
                	friend.sendPacket(new FriendList(friend));
                    friend.sendPacket(sm);
                }
		    }
            sm = null;

            rset.close();
            statement.close();
        }
		catch (Exception e) 
		{
            _log.warning("could not restore friend data:"+e);
        }
		finally 
		{
            	try
				{
					con.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
        }
	}

	/**
	 * @param activeChar
	 */
	private void notifyClanMembers(L2PcInstance activeChar)
	{
		L2Clan clan = activeChar.getClan();
		if (clan != null)
		{
			clan.getClanMember(activeChar.getName()).setPlayerInstance(activeChar);
			SystemMessage msg = new SystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN);
			msg.addString(activeChar.getName());
			clan.broadcastToOtherOnlineMembers(msg, activeChar);
			msg = null;
			clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(activeChar), activeChar);
			
			if (activeChar.getClan() != null)
			{
				//you probably have something here to check the player's clan hall and stuff
				if(activeChar.getClan().isNoticeEnabled() && activeChar.getClan().getNotice()!="")
				{
					sendPacket(new NpcHtmlMessage(1, "<html><body><center><font color=\"LEVEL\">"+activeChar.getClan().getName()+" Clan Notice</font></center><br>"+activeChar.getClan().getNotice()+"</body></html>"));
				}
			}
		}
	}

	/**
	 * @param activeChar
	 */
	private static void notifySponsorOrApprentice(L2PcInstance activeChar)
	{
		if (activeChar.getSponsor() != 0)
		{
			L2PcInstance sponsor = (L2PcInstance)L2World.getInstance().findObject(activeChar.getSponsor());

			if (sponsor != null)
			{
				SystemMessage msg = new SystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN);
				msg.addString(activeChar.getName());
				sponsor.sendPacket(msg);
			}
		}
		else if (activeChar.getApprentice() != 0)
		{
			L2PcInstance apprentice = (L2PcInstance)L2World.getInstance().findObject(activeChar.getApprentice());

			if (apprentice != null)
			{
				SystemMessage msg = new SystemMessage(SystemMessageId.YOUR_SPONSOR_S1_HAS_LOGGED_IN);
				msg.addString(activeChar.getName());
				apprentice.sendPacket(msg);
			}
		}
	}



	private static void loadTutorial(L2PcInstance player)  
	{  
	 	QuestState qs = player.getQuestState("255_Tutorial");  
	 	if(qs != null)  
	 	         qs.getQuest().notifyEvent("UC", null, player);  
	}

	@Override
	public String getType()
	{
		return _C__03_ENTERWORLD;
	}
	
	private static void notifyCastleOwner(L2PcInstance activeChar)
	{
		L2Clan clan = activeChar.getClan();
		
		if (clan != null)
		{
			if (clan.getHasCastle() > 0)
			{
				Castle castle = CastleManager.getInstance().getCastleById(clan.getHasCastle());
				if ((castle != null) && (activeChar.getObjectId() == clan.getLeaderId()))
					Announcements.getInstance().announceToAll("Lord " + activeChar.getName() + " Ruler Of " + castle.getName() + " Castle is Now Online!");
			}
		}
	}

	private static void setPledgeClass(L2PcInstance activeChar)
	{
		int pledgeClass = 0;
		if ( activeChar.getClan() != null)
		{
			activeChar.getClan().getClanMember(activeChar.getObjectId());
			pledgeClass = L2ClanMember.calculatePledgeClass(activeChar);
		}

		if (activeChar.isNoble() && pledgeClass < 5)
	           pledgeClass = 5;

	    if (activeChar.isHero())
	           pledgeClass = 8;

	    activeChar.setPledgeClass(pledgeClass);
	 }

	private void onEnterAio(L2PcInstance activeChar)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = activeChar.getAioEndTime();
		if(now > endDay)
		{
			activeChar.setAio(false);
			activeChar.setAioEndTime(0);
	        activeChar.lostAioSkills();
			if(Config.AIO_EFFECT)
				activeChar.stopAbnormalEffect(Config.AIO_EFFECT_ID);
	        activeChar.sendMessage("Removed your Aio stats... period ends ");
		}
		else
		{
			if(Config.AIO_EFFECT)
				activeChar.startAbnormalEffect(Config.AIO_EFFECT_ID);
			activeChar.rewardAioSkills();
			Date dt = new Date(endDay);
	        _daysleft = (endDay - now)/86400000;
	        if(_daysleft > 30)
	                activeChar.sendMessage("Aio period ends in " + df.format(dt) + ". enjoy the Game");
	        else if(_daysleft > 0)
	                activeChar.sendMessage("left " + (int)_daysleft + " days for Aio period ends");
	        else if(_daysleft < 1)
	        {
	        	long hour = (endDay - now)/3600000;
	        	activeChar.sendMessage("left " + (int)hour + " hours to Aio period ends");
	        }
		}
	}
	
	private void onEnterVip(L2PcInstance activeChar)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = activeChar.getVipEndTime();
		if(now > endDay)
		{
			activeChar.setVip(false);
	        activeChar.setVipEndTime(0);
	        activeChar.lostVipSkills();
	        activeChar.sendMessage("Removed your Vip stats... period ends ");
		}
		else
		{
			if(Config.ENABLE_VIP_SYSTEM)
			{
				activeChar.rewardVipSkills();
			}
			Date dt = new Date(endDay);
			_daysleft = (endDay - now)/86400000;
			if(_daysleft > 30)
				activeChar.sendMessage("Vip period ends in " + df.format(dt) + ". enjoy the Game");
			else if(_daysleft > 0)
				activeChar.sendMessage("left " + (int)_daysleft + " days for Vip period ends");
			else if(_daysleft < 1)
			{
				long hour = (endDay - now)/3600000;
				activeChar.sendMessage("left " + (int)hour + " hours to Vip period ends");
			}
		}
	}

	private static boolean isValidName(String text)
    {
        boolean result = true;
        String test = text;
        Pattern pattern;
        try
        {
            pattern = Pattern.compile(Config.CNAME_TEMPLATE);
        }
        catch(PatternSyntaxException e)
        {
            _log.warning("ERROR : Character name pattern of config is wrong!");
            pattern = Pattern.compile(".*");
        }
        Matcher regexp = pattern.matcher(test);
        if(!regexp.matches())
            result = false;
        return result;
    }
}