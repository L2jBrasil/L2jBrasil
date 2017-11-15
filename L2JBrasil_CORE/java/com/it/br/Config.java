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
package com.it.br;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.it.br.gameserver.model.Olympiad.OlympiadPeriod;
import com.it.br.gameserver.util.FloodProtectorConfig;
import com.it.br.gameserver.util.StringUtil;

/**
 * This class contains global server configuration.<br>
 * It has static final fields initialized from configuration files.<br>
 * It's initialized at the very begin of startup, and later JIT will optimize
 * away debug/unused code.
 *
 * @author by Guma
 * @reworked by Tayran
 */
public final class Config
{
    protected static final Logger _log = Logger.getLogger(Config.class.getName());

    /* Properties Files Definitions */
    public static final String LOGIN_FILE			= "./config/login.properties";
    public static final String SERVER_FILE			= "./config/server.properties";
    public static final String MMOCORE_CONFIG_FILE	= "./config/mmocore.properties";
    public static final String TELNET_FILE			= "./config/telnet.properties";

    public static final String COMMAND_FILE		    = "./config/custom/command.properties";
    public static final String L2JBRASIL_FILE		= "./config/custom/l2jbrasil.properties";
    public static final String L2JMOD_FILE			= "./config/custom/l2jmods.properties";

    public static final String CH_FILE              = "./config/event/clanhall.properties";
    public static final String SEPULCHERS_FILE		= "./config/event/sepulchers.properties";
    public static final String OLYMPIAD_FILE		= "./config/event/olympiad.properties";
    public static final String SEVENSIGNS_FILE		= "./config/event/sevensigns.properties";
    public static final String SIEGE_FILE			= "./config/event/siege.properties";
    public static final String EVENT_CONFIG_FILE	= "./config/event/event.properties";

    public static final String ADMIN_FILE			= "./config/main/admin.properties";
    public static final String ALTSETTINGS_FILE	    = "./config/main/altsettings.properties";
    public static final String BOSS_FILE			= "./config/main/boss.properties";
    public static final String CLAN_FILE			= "./config/main/clan.properties";
    public static final String CLASS_FILE			= "./config/main/class.properties";
    public static final String ENCHANT_FILE		    = "./config/main/enchant.properties";
    public static final String EXTENSIONS_FILE		= "./config/main/extensions.properties";
    public static final String OPTIONS_FILE		    = "./config/main/options.properties";
    public static final String OTHER_FILE			= "./config/main/other.properties";
    public static final String PVP_FILE			    = "./config/main/pvp.properties";
    public static final String RATES_FILE			= "./config/main/rates.properties";

    public static final String CHAT_FILTER_FILE	    = "./config/other/chatfilter.txt";
    public static final String ADMINCOMMAND_FILE	= "./config/other/admin-command.properties";
    public static final String HEXID_FILE			= "./config/other/hexid.txt";

    public static final String FLOODPROTECTOR_FILE	= "./config/protect/floodprotector.properties";
    public static final String ID_CONFIG_FILE		= "./config/protect/factory.properties";
    public static final String SCRIPTING_FILE		= "./config/protect/scripting.properties";

    public static final String BANNED_IP_XML		= "./config/banned.xml";

	// --------------------------------------------- //
    // -           GM ACCESS PROPERTIES            - //
    // --------------------------------------------- //
    // ============================================================
    public static String  GM_ADMIN_MENU_STYLE;
    public static boolean GM_EDIT;
    public static boolean ONLY_GM_ITEMS_FREE;
    public static boolean GM_DISABLE_TRANSACTION;
    public static int GM_TRANSACTION_MIN;
    public static int GM_TRANSACTION_MAX;
    public static boolean GM_TRADE_RESTRICTED_ITEMS;
    public static boolean GM_RESTART_FIGHTING;
    public static int GM_CAN_GIVE_DAMAGE;
    public static int GM_DONT_TAKE_EXPSP;
    public static int GM_DONT_TAKE_AGGRO;
    public static boolean GM_NAME_COLOR_ENABLED;
    public static int ADMIN_NAME_COLOR;
    public static int GM_NAME_COLOR;
    public static boolean GM_TITLE_COLOR_ENABLED;
    public static int ADMIN_TITLE_COLOR;
    public static int GM_TITLE_COLOR;
    public static boolean GM_HERO_AURA;
    public static boolean GM_STARTUP_INVULNERABLE;
    public static boolean GM_SUPER_HASTE;
    public static boolean GM_STARTUP_INVISIBLE;
    public static boolean GM_GIVE_SPECIAL_SKILLS;
    public static boolean GM_SPECIAL_EFFECT;
    public static boolean GM_STARTUP_SILENCE;
    public static boolean GM_STARTUP_AUTO_LIST;
    public static boolean GM_STARTUP_DIET;
    public static boolean GM_WELCOME_HTM;
    public static int GM_ACCESSLEVEL;
    public static int GM_ALTG_MIN_LEVEL;

    public static int GM_ESCAPE;
    public static int GM_PEACEATTACK;


    // ============================================================

    // Class AdminAdmin
    public static int admin_admin;
    public static int admin_admin1;
    public static int admin_admin2;
    public static int admin_admin3;
    public static int admin_admin4;
    public static int admin_admin5;
    public static int admin_gmliston;
    public static int admin_gmlistoff;
    public static int admin_silence;
    public static int admin_diet;
    public static int admin_tradeoff;
    public static int admin_reload;
    public static int admin_set;
    public static int admin_set_menu;
    public static int admin_set_mod;
    public static int admin_saveolymp;
    public static int admin_manualhero;

    // Class AdminAio
    public static int admin_setaio;
    public static int admin_removeaio;

    // Class AdminAnnouncements
    public static int admin_list_announcements;
    public static int admin_reload_announcements;
    public static int admin_announce_announcements;
    public static int admin_add_announcement;
    public static int admin_del_announcement;
    public static int admin_announce;
    public static int admin_announce_menu;

    // Class AdminAutoAnnouncements
    public static int admin_list_autoannouncements;
    public static int admin_add_autoannouncement;
    public static int admin_del_autoannouncement;
    public static int admin_autoannounce;

    // Class AdminBan
    public static int admin_ban;
    public static int admin_unban;
    public static int admin_jail;
    public static int admin_unjail;

    // Class AdminBanChat
    public static int admin_banchat;
    public static int admin_unbanchat;

    // Class AdminBBS
    public static int admin_bbs;

    // Class AdminCache
    public static int admin_cache_htm_rebuild;
    public static int admin_cache_htm_reload;
    public static int admin_cache_reload_path;
    public static int admin_cache_reload_file;
    public static int admin_cache_crest_fix;

    // Class AdminChangeAccessLevel
    public static int admin_changelvl;

    // Class AdminClanFull
    public static int admin_clanfull;

    // Class AdminCreateItem
    public static int admin_itemcreate;
    public static int admin_create_item;
    public static int admin_mass_create;

    // Class AdminCursedWeapons
    public static int admin_cw_info;
    public static int admin_cw_remove;
    public static int admin_cw_goto;
    public static int admin_cw_reload;
    public static int admin_cw_add;
    public static int admin_cw_info_menu;

    // Class AdminDebug
    public static int admin_debug;

    // Class AdminDelete
    public static int admin_delete;

    //Class AdminDisconnect
    public static int admin_character_disconnect;

    // Class AdminDoorControl
    public static int admin_open;
    public static int admin_close;
    public static int admin_openall;
    public static int admin_closeall;

    // Class AdminEditChar
    public static int admin_edit_character;
    public static int admin_current_player;
    public static int admin_nokarma;
    public static int admin_setkarma;
    public static int admin_character_list;
    public static int admin_character_info;
    public static int admin_show_characters;
    public static int admin_find_character;
    public static int admin_find_ip;
    public static int admin_find_account;
    public static int admin_save_modifications;
    public static int admin_rec;
    public static int admin_settitle;
    public static int admin_setname;
    public static int admin_setsex;
    public static int admin_setcolor;
    public static int admin_setclass;
    public static int admin_fullfood;
    public static int admin_sethero;
    public static int admin_setnoble;

    // Class AdminEditNpc
    public static int admin_edit_npc;
    public static int admin_save_npc;
    public static int admin_show_droplist;
    public static int admin_edit_drop;
    public static int admin_add_drop;
    public static int admin_del_drop;
    public static int admin_showShop;
    public static int admin_showShopList;
    public static int admin_addShopItem;
	public static int admin_delShopItem;
    public static int admin_box_access;
    public static int admin_editShopItem;
    public static int admin_close_window;
    public static int admin_show_skilllist_npc;
    public static int admin_add_skill_npc;
    public static int admin_edit_skill_npc;
    public static int admin_del_skill_npc;

    // Class AdminEffects
    public static int admin_invis;
    public static int admin_invisible;
    public static int admin_vis;
    public static int admin_invis_menu;
    public static int admin_earthquake;
    public static int admin_earthquake_menu;
    public static int admin_bighead;
    public static int admin_shrinkhead;
    public static int admin_gmspeed;
    public static int admin_gmspeed_menu;
    public static int admin_unpara_all;
    public static int admin_para_all;
    public static int admin_unpara;
    public static int admin_para;
    public static int admin_unpara_all_menu;
    public static int admin_para_all_menu;
    public static int admin_unpara_menu;
    public static int admin_para_menu;
    public static int admin_polyself;
    public static int admin_unpolyself;
    public static int admin_polyself_menu;
    public static int admin_unpolyself_menu;
    public static int admin_changename;
    public static int admin_setteam_close;
    public static int admin_setteam;
    public static int admin_social;
    public static int admin_effect;
    public static int admin_social_menu;
    public static int admin_effect_menu;
    public static int admin_abnormal;
    public static int admin_abnormal_menu;
    public static int admin_play_sounds;
    public static int admin_play_sound;
    public static int admin_atmosphere;
    public static int admin_atmosphere_menu;

    // Class AdminEnchant
    public static int admin_seteh;
    public static int admin_setec;
    public static int admin_seteg;
    public static int admin_setel;
    public static int admin_seteb;
    public static int admin_setew;
    public static int admin_setes;
    public static int admin_setle;
    public static int admin_setre;
    public static int admin_setlf;
    public static int admin_setrf;
    public static int admin_seten;
    public static int admin_setun;
    public static int admin_setba;
    public static int admin_enchant;

    // Class AdminEventEngine
    public static int admin_event;
    public static int admin_event_new;
    public static int admin_event_choose;
    public static int admin_event_store;
    public static int admin_event_set;
    public static int admin_event_change_teams_number;
    public static int admin_event_announce;
    public static int admin_event_panel;
    public static int admin_event_control_begin;
    public static int admin_event_control_teleport;
    public static int admin_add;
    public static int admin_event_see;
    public static int admin_event_del;
    public static int admin_delete_buffer;
    public static int admin_event_control_sit;
    public static int admin_event_name;
    public static int admin_event_control_kill;
    public static int admin_event_control_res;
    public static int admin_event_control_poly;
    public static int admin_event_control_unpoly;
    public static int admin_event_control_prize;
    public static int admin_event_control_chatban;
    public static int admin_event_control_finish;

    // Class AdminExpSp
    public static int admin_add_exp_sp;
    public static int admin_remove_exp_sp;

    // Class AdminFightCalculator
    public static int admin_fight_calculator;
    public static int admin_fight_calculator_show;
    public static int admin_fcs;

    // Class AdminGeodata
    public static int admin_geo_z;
    public static int admin_geo_type;
    public static int admin_geo_nswe;
    public static int admin_geo_los;
    public static int admin_geo_position;
    public static int admin_geo_bug;
    public static int admin_geo_load;
    public static int admin_geo_unload;

    // Class AdminGeoEditor
    public static int admin_ge_status;
    public static int admin_ge_mode;
    public static int admin_ge_join;
    public static int admin_ge_leave;

    // Class AdminGm
    public static int admin_gm;

    // Class AdminGmChat
    public static int admin_gmchat;
    public static int admin_snoop;
    public static int admin_gmchat_menu;

    // Class AdminHeal
    public static int admin_heal;

    // Class AdminHelpPage
    public static int admin_help;

    // Class AdminInvul
    public static int admin_invul;
    public static int admin_setinvul;

    // Class AdminKick
    public static int admin_kick;
    public static int admin_kick_non_gm;

    // Class AdminKill
    public static int admin_kill;
    public static int admin_kill_monster;

    // Class AdminLevel
    public static int admin_remlevel;
    public static int admin_addlevel;
    public static int admin_setlevel;

    // Class AdminLogin
    public static int admin_server_gm_only;
    public static int admin_server_all;
    public static int admin_server_max_player;
    public static int admin_server_list_clock;
    public static int admin_server_login;

    // Class AdminMammon
    public static int admin_mammon_find;
    public static int admin_mammon_respawn;
    public static int admin_list_spawns;
    public static int admin_msg;

    // Class AdminManor
    public static int admin_manor;
    public static int admin_manor_approve;
    public static int admin_manor_setnext;
    public static int admin_manor_reset;
    public static int admin_manor_setmaintenance;
    public static int admin_manor_save;
    public static int admin_manor_disable;

    // Class AdminMassHero
    public static int admin_masshero;
    public static int admin_allhero;

    // Class AdminMassRecall
    public static int admin_recallclan;
    public static int admin_recallparty;
    public static int admin_recallally;

    // Class AdminMassRecall
    public static int admin_char_manage;
    public static int admin_teleport_character_to_menu;
    public static int admin_recall_char_menu;
    public static int admin_recall_party_menu;
    public static int admin_recall_clan_menu;
    public static int admin_goto_char_menu;
    public static int admin_kick_menu;
    public static int admin_ban_menu;
    public static int admin_unban_menu;

    // Class AdminMobGroup
    public static int admin_mobmenu;
    public static int admin_mobgroup_list;
    public static int admin_mobgroup_create;
    public static int admin_mobgroup_remove;
    public static int admin_mobgroup_delete;
    public static int admin_mobgroup_idle;
    public static int admin_mobgroup_attack;
    public static int admin_mobgroup_rnd;
    public static int admin_mobgroup_return;
    public static int admin_mobgroup_follow;
    public static int admin_mobgroup_casting;
    public static int admin_mobgroup_nomove;
    public static int admin_mobgroup_attackgrp;
    public static int admin_mobgroup_invul;
    public static int admin_mobinst;

    // Class AdminMonsterRace
    public static int admin_mons;

    // Class AdminPathNode
    public static int admin_pn_info;
    public static int admin_show_path;
    public static int admin_path_debug;
    public static int admin_show_pn;
    public static int admin_find_path;

    // Class AdminPetition
    public static int admin_view_petitions;
    public static int admin_view_petition;
    public static int admin_accept_petition;
    public static int admin_reject_petition;
    public static int admin_reset_petitions;
    public static int admin_force_peti;
    public static int admin_add_peti_chat;

    // Class AdminPForge
    public static int admin_forge;
    public static int admin_forge2;
    public static int admin_forge3;

    // Class AdminPledge
    public static int admin_pledge;

    // Class AdminPolymorph
    public static int admin_polymorph;
    public static int admin_unpolymorph;
    public static int admin_polymorph_menu;
    public static int admin_unpolymorph_menu;

    // Class AdminQuest
    public static int admin_quest_reload;

    // Class AdminRecallAll
    public static int admin_recallall;

    // Class AdminRepairChar
    public static int admin_restore;
    public static int admin_repair;

    // Class AdminRes
    public static int admin_res;
    public static int admin_res_monster;

    // Class AdminRideWynvern
    public static int admin_ride_wyvern;
    public static int admin_ride_strider;
    public static int admin_unride_wyvern;
    public static int admin_unride_strider;
    public static int admin_unride;

    // Class AdminShop
    public static int admin_buy;
    public static int admin_gmshop;

    // Class AdminShutdown
    public static int admin_server_shutdown;
    public static int admin_server_restart;
    public static int admin_server_abort;

    // Class AdminSiege
    public static int admin_siege;
    public static int admin_add_attacker;
    public static int admin_add_defender;
    public static int admin_add_guard;
    public static int admin_list_siege_clans;
    public static int admin_clear_siege_list;
    public static int admin_move_defenders;
    public static int admin_spawn_doors;
    public static int admin_endsiege;
    public static int admin_startsiege;
    public static int admin_setcastle;
    public static int admin_removecastle;
    public static int admin_clanhall;
    public static int admin_clanhallset;
    public static int admin_clanhalldel;
    public static int admin_clanhallopendoors;
    public static int admin_clanhallclosedoors;
    public static int admin_clanhallteleportself;

    // Class AdminSkill
    public static int admin_show_skills;
    public static int admin_remove_skills;
    public static int admin_skill_list;
    public static int admin_skill_index;
    public static int admin_add_skill;
    public static int admin_remove_skill;
    public static int admin_get_skills;
    public static int admin_reset_skills;
    public static int admin_give_all_skills;
    public static int admin_remove_all_skills;
    public static int admin_add_clan_skill;

    // Class AdminSpawn
    public static int admin_show_spawns;
    public static int admin_spawn;
    public static int admin_spawn_monster;
    public static int admin_spawn_index;
    public static int admin_unspawnall;
    public static int admin_respawnall;
    public static int admin_spawn_reload;
    public static int admin_npc_index;
    public static int admin_spawn_once;
    public static int admin_show_npcs;
    public static int admin_teleport_reload;
    public static int admin_spawnnight;
    public static int admin_spawnday;

    // Class AdminTarget
    public static int admin_target;

    // Class AdminTeleport
    public static int admin_show_moves;
    public static int admin_show_moves_other;
    public static int admin_show_teleport;
    public static int admin_teleport_to_character;
    public static int admin_recall;
    public static int admin_walk;
    public static int admin_explore;
    public static int admin_recall_npc;
    public static int admin_gonorth;
    public static int admin_gosouth;
    public static int admin_goeast;
    public static int admin_gowest;
    public static int admin_goup;
    public static int admin_godown;
    public static int admin_tele;
    public static int admin_teleto;
    public static int admin_instant_move;
    public static int admin_sendhome;

    // Class AdminTest
    public static int admin_test;
    public static int admin_stats;
    public static int admin_skill_test;
    public static int admin_st;
    public static int admin_mp;
    public static int admin_known;

    // Class AdminTvTEvent
    public static int admin_tvt_add;
    public static int admin_tvt_remove;
    public static int admin_tvt_advance;

    // Class AdminUnblockIp
    public static int admin_unblockip;

   // Class AdminVip
    public static int admin_setvip;
    public static int admin_removevip;

   // Class AdminZone
    public static int admin_zone_check;
    public static int admin_zone_reload;


    // ============================================================

    public static void loadGMAcessConfig()
	{
	    try(InputStream is = new FileInputStream(new File(ADMIN_FILE)))
	    {
	        Properties gmSettings = new Properties();
	        gmSettings.load(is);

	        GM_ADMIN_MENU_STYLE = gmSettings.getProperty("GMAdminMenuStyle", "modern");
	        GM_EDIT = Boolean.valueOf(gmSettings.getProperty("GMEdit", "False"));
	        ONLY_GM_ITEMS_FREE = Boolean.valueOf(gmSettings.getProperty("OnlyGMItemsFree", "True"));
	        String gmTrans = gmSettings.getProperty("GMDisableTransaction", "False");
	        if (!gmTrans.equalsIgnoreCase("false"))
	        {
	            String[] params = gmTrans.split(",");
	            GM_DISABLE_TRANSACTION = true;
	            GM_TRANSACTION_MIN = Integer.parseInt(params[0]);
	            GM_TRANSACTION_MAX = Integer.parseInt(params[1]);
	        }
	        else
	        {
	            GM_DISABLE_TRANSACTION = false;
	        }
	        GM_TRADE_RESTRICTED_ITEMS = Boolean.parseBoolean(gmSettings.getProperty("GMTradeRestrictedItems", "False"));
	        GM_RESTART_FIGHTING = Boolean.parseBoolean(gmSettings.getProperty("GMRestartFighting", "False"));
	        GM_CAN_GIVE_DAMAGE = Integer.parseInt(gmSettings.getProperty("GMCanGiveDamage", "90"));
	        GM_DONT_TAKE_AGGRO = Integer.parseInt(gmSettings.getProperty("GMDontTakeAggro", "90"));
	        GM_DONT_TAKE_EXPSP = Integer.parseInt(gmSettings.getProperty("GMDontGiveExpSp", "90"));
	        GM_NAME_COLOR_ENABLED = Boolean.parseBoolean(gmSettings.getProperty("GMNameColorEnabled", "False"));
	        GM_NAME_COLOR = Integer.decode("0x" + gmSettings.getProperty("GMNameColor", "FFFF00"));
	        ADMIN_NAME_COLOR = Integer.decode("0x" + gmSettings.getProperty("AdminNameColor", "00FF00"));
	        GM_TITLE_COLOR_ENABLED = Boolean.parseBoolean(gmSettings.getProperty("GMNameTitleEnabled", "False"));
	        GM_TITLE_COLOR = Integer.decode("0x" + gmSettings.getProperty("GMTitleColor", "FFFF00"));
	        ADMIN_TITLE_COLOR = Integer.decode("0x" + gmSettings.getProperty("AdminTitleColor", "00FF00"));
	        GM_HERO_AURA = Boolean.parseBoolean(gmSettings.getProperty("GMHeroAura", "True"));
	        GM_STARTUP_INVULNERABLE = Boolean.parseBoolean(gmSettings.getProperty("GMStartupInvulnerable", "True"));
	        GM_SUPER_HASTE = Boolean.parseBoolean(gmSettings.getProperty("GMStartupSuperHaste", "False"));
	        GM_STARTUP_INVISIBLE = Boolean.parseBoolean(gmSettings.getProperty("GMStartupInvisible", "True"));
            GM_GIVE_SPECIAL_SKILLS = Boolean.parseBoolean(gmSettings.getProperty("GMGiveSpecialSkills", "False"));
            GM_SPECIAL_EFFECT = Boolean.parseBoolean(gmSettings.getProperty("GMLoginSpecialEffect", "False"));
            GM_STARTUP_SILENCE = Boolean.parseBoolean(gmSettings.getProperty("GMStartupSilence", "True"));
            GM_STARTUP_AUTO_LIST = Boolean.parseBoolean(gmSettings.getProperty("GMStartupAutoList", "True"));
            GM_STARTUP_DIET = Boolean.parseBoolean(gmSettings.getProperty("GMStartupDiet", "False"));
            GM_WELCOME_HTM = Boolean.parseBoolean(gmSettings.getProperty("GMWelcomeHtm", "False"));
            GM_ALTG_MIN_LEVEL = Integer.parseInt(gmSettings.getProperty("GMCanAltG", "100"));
            GM_ESCAPE = Integer.parseInt(gmSettings.getProperty("GMFastUnstuck", "100"));
            GM_PEACEATTACK = Integer.parseInt(gmSettings.getProperty("GMPeaceAttack", "100"));
            admin_admin = Integer.parseInt(gmSettings.getProperty("admin_admin", "100"));
            admin_admin1 = Integer.parseInt(gmSettings.getProperty("admin_admin1", "100"));
            admin_admin2 = Integer.parseInt(gmSettings.getProperty("admin_admin2", "100"));
            admin_admin3 = Integer.parseInt(gmSettings.getProperty("admin_admin3", "100"));
            admin_admin4 = Integer.parseInt(gmSettings.getProperty("admin_admin4", "100"));
            admin_admin5 = Integer.parseInt(gmSettings.getProperty("admin_admin5", "100"));

            admin_gmliston = Integer.parseInt(gmSettings.getProperty("admin_gmliston", "100"));
            admin_gmlistoff = Integer.parseInt(gmSettings.getProperty("admin_gmlistoff", "100"));
            admin_silence = Integer.parseInt(gmSettings.getProperty("admin_silence", "100"));
            admin_diet = Integer.parseInt(gmSettings.getProperty("admin_diet", "100"));
            admin_tradeoff = Integer.parseInt(gmSettings.getProperty("admin_tradeoff", "100"));
            admin_reload = Integer.parseInt(gmSettings.getProperty("admin_reload", "100"));
            admin_set = Integer.parseInt(gmSettings.getProperty("admin_set", "100"));
            admin_set_menu = Integer.parseInt(gmSettings.getProperty("admin_set_menu", "100"));
            admin_set_mod = Integer.parseInt(gmSettings.getProperty("admin_set_mod", "100"));
            admin_saveolymp = Integer.parseInt(gmSettings.getProperty("admin_saveolymp", "100"));
            admin_manualhero = Integer.parseInt(gmSettings.getProperty("admin_manualhero", "100"));

            admin_setaio = Integer.parseInt(gmSettings.getProperty("admin_setaio", "100"));
            admin_removeaio = Integer.parseInt(gmSettings.getProperty("admin_removeaio", "100"));

            admin_reload_announcements = Integer.parseInt(gmSettings.getProperty("admin_reload_announcements", "100"));
            admin_announce_announcements = Integer.parseInt(gmSettings.getProperty("admin_announce_announcements", "100"));
            admin_add_announcement = Integer.parseInt(gmSettings.getProperty("admin_add_announcement", "100"));
            admin_del_announcement = Integer.parseInt(gmSettings.getProperty("admin_del_announcement", "100"));
            admin_announce = Integer.parseInt(gmSettings.getProperty("admin_announce", "100"));
            admin_announce_menu = Integer.parseInt(gmSettings.getProperty("admin_announce_menu", "100"));

            admin_list_autoannouncements = Integer.parseInt(gmSettings.getProperty("admin_list_autoannouncements", "100"));
            admin_add_autoannouncement = Integer.parseInt(gmSettings.getProperty("admin_add_autoannouncement", "100"));
            admin_del_autoannouncement = Integer.parseInt(gmSettings.getProperty("admin_del_autoannouncement", "100"));
            admin_autoannounce = Integer.parseInt(gmSettings.getProperty("admin_autoannounce", "100"));

            admin_ban = Integer.parseInt(gmSettings.getProperty("admin_ban", "100"));
            admin_unban = Integer.parseInt(gmSettings.getProperty("admin_unban", "100"));
            admin_jail = Integer.parseInt(gmSettings.getProperty("admin_jail", "100"));
            admin_unjail = Integer.parseInt(gmSettings.getProperty("admin_unjail", "100"));

            admin_banchat = Integer.parseInt(gmSettings.getProperty("admin_banchat", "100"));
            admin_unbanchat = Integer.parseInt(gmSettings.getProperty("admin_unbanchat", "100"));

            admin_bbs = Integer.parseInt(gmSettings.getProperty("admin_bbs", "100"));

            admin_cache_htm_rebuild = Integer.parseInt(gmSettings.getProperty("admin_cache_htm_rebuild", "100"));
            admin_cache_htm_reload = Integer.parseInt(gmSettings.getProperty("admin_cache_htm_reload", "100"));
            admin_cache_reload_path = Integer.parseInt(gmSettings.getProperty("admin_cache_reload_path", "100"));
            admin_cache_reload_file = Integer.parseInt(gmSettings.getProperty("admin_cache_reload_file", "100"));
            admin_cache_crest_fix = Integer.parseInt(gmSettings.getProperty("admin_cache_crest_fix", "100"));

            admin_changelvl = Integer.parseInt(gmSettings.getProperty("admin_changelvl", "100"));

            admin_clanfull = Integer.parseInt(gmSettings.getProperty("admin_clanfull", "100"));

            admin_itemcreate = Integer.parseInt(gmSettings.getProperty("admin_itemcreate", "100"));
            admin_create_item = Integer.parseInt(gmSettings.getProperty("admin_create_item", "100"));
            admin_mass_create = Integer.parseInt(gmSettings.getProperty("admin_mass_create", "100"));

            admin_cw_info = Integer.parseInt(gmSettings.getProperty("admin_cw_info", "100"));
            admin_cw_remove = Integer.parseInt(gmSettings.getProperty("admin_cw_remove", "100"));
            admin_cw_goto = Integer.parseInt(gmSettings.getProperty("admin_cw_goto", "100"));
            admin_cw_reload = Integer.parseInt(gmSettings.getProperty("admin_cw_reload", "100"));
            admin_cw_add = Integer.parseInt(gmSettings.getProperty("admin_cw_add", "100"));
            admin_cw_info_menu = Integer.parseInt(gmSettings.getProperty("admin_cw_info_menu", "100"));

            admin_debug = Integer.parseInt(gmSettings.getProperty("admin_debug", "100"));

            admin_delete = Integer.parseInt(gmSettings.getProperty("admin_delete", "100"));

            admin_character_disconnect = Integer.parseInt(gmSettings.getProperty("admin_character_disconnect", "100"));

            admin_open = Integer.parseInt(gmSettings.getProperty("admin_open", "100"));
            admin_close = Integer.parseInt(gmSettings.getProperty("admin_close", "100"));
            admin_openall = Integer.parseInt(gmSettings.getProperty("admin_openall", "100"));
            admin_closeall = Integer.parseInt(gmSettings.getProperty("admin_closeall", "100"));

            admin_edit_character = Integer.parseInt(gmSettings.getProperty("admin_edit_character", "100"));
            admin_current_player = Integer.parseInt(gmSettings.getProperty("admin_current_player", "100"));
            admin_nokarma = Integer.parseInt(gmSettings.getProperty("admin_nokarma", "100"));
            admin_setkarma = Integer.parseInt(gmSettings.getProperty("admin_setkarma", "100"));
            admin_character_list = Integer.parseInt(gmSettings.getProperty("admin_character_list", "100"));
            admin_character_info = Integer.parseInt(gmSettings.getProperty("admin_character_info", "100"));
            admin_show_characters = Integer.parseInt(gmSettings.getProperty("admin_show_characters", "100"));
            admin_find_character = Integer.parseInt(gmSettings.getProperty("admin_find_character", "100"));
            admin_find_ip = Integer.parseInt(gmSettings.getProperty("admin_find_ip", "100"));
            admin_find_account = Integer.parseInt(gmSettings.getProperty("admin_find_account", "100"));
            admin_save_modifications = Integer.parseInt(gmSettings.getProperty("admin_save_modifications", "100"));
            admin_rec = Integer.parseInt(gmSettings.getProperty("admin_rec", "100"));
            admin_settitle = Integer.parseInt(gmSettings.getProperty("admin_settitle", "100"));
            admin_setname = Integer.parseInt(gmSettings.getProperty("admin_setname", "100"));
            admin_setsex = Integer.parseInt(gmSettings.getProperty("admin_setsex", "100"));
            admin_setcolor = Integer.parseInt(gmSettings.getProperty("admin_setcolor", "100"));
            admin_setclass = Integer.parseInt(gmSettings.getProperty("admin_setclass", "100"));
            admin_fullfood = Integer.parseInt(gmSettings.getProperty("admin_fullfood", "100"));
            admin_sethero = Integer.parseInt(gmSettings.getProperty("admin_sethero", "100"));
            admin_setnoble = Integer.parseInt(gmSettings.getProperty("admin_setnoble", "100"));

            admin_edit_npc = Integer.parseInt(gmSettings.getProperty("admin_edit_npc", "100"));
            admin_save_npc = Integer.parseInt(gmSettings.getProperty("admin_save_npc", "100"));
            admin_show_droplist = Integer.parseInt(gmSettings.getProperty("admin_show_droplist", "100"));
            admin_edit_drop = Integer.parseInt(gmSettings.getProperty("admin_edit_drop", "100"));
            admin_add_drop = Integer.parseInt(gmSettings.getProperty("admin_add_drop", "100"));
            admin_del_drop = Integer.parseInt(gmSettings.getProperty("admin_del_drop", "100"));
            admin_showShop = Integer.parseInt(gmSettings.getProperty("admin_showShop", "100"));
            admin_showShopList = Integer.parseInt(gmSettings.getProperty("admin_showShopList", "100"));
            admin_addShopItem = Integer.parseInt(gmSettings.getProperty("admin_addShopItem", "100"));
            admin_delShopItem = Integer.parseInt(gmSettings.getProperty("admin_delShopItem", "100"));
            admin_box_access = Integer.parseInt(gmSettings.getProperty("admin_box_access", "100"));
            admin_editShopItem = Integer.parseInt(gmSettings.getProperty("admin_editShopItem", "100"));
            admin_close_window = Integer.parseInt(gmSettings.getProperty("admin_close_window", "100"));
            admin_show_skilllist_npc = Integer.parseInt(gmSettings.getProperty("admin_show_skilllist_npc", "100"));
            admin_add_skill_npc = Integer.parseInt(gmSettings.getProperty("admin_add_skill_npc", "100"));
            admin_edit_skill_npc = Integer.parseInt(gmSettings.getProperty("admin_edit_skill_npc", "100"));
            admin_del_skill_npc = Integer.parseInt(gmSettings.getProperty("admin_del_skill_npc", "100"));

            admin_invis = Integer.parseInt(gmSettings.getProperty("admin_invis", "100"));
            admin_invisible = Integer.parseInt(gmSettings.getProperty("admin_invisible", "100"));
            admin_vis = Integer.parseInt(gmSettings.getProperty("admin_vis", "100"));
            admin_invis_menu = Integer.parseInt(gmSettings.getProperty("admin_invis_menu", "100"));
            admin_earthquake = Integer.parseInt(gmSettings.getProperty("admin_earthquake", "100"));
            admin_earthquake_menu = Integer.parseInt(gmSettings.getProperty("admin_earthquake_menu", "100"));
            admin_bighead = Integer.parseInt(gmSettings.getProperty("admin_bighead", "100"));
            admin_shrinkhead = Integer.parseInt(gmSettings.getProperty("admin_shrinkhead", "100"));
            admin_gmspeed = Integer.parseInt(gmSettings.getProperty("admin_gmspeed", "100"));
            admin_gmspeed_menu = Integer.parseInt(gmSettings.getProperty("admin_gmspeed_menu", "100"));
            admin_unpara_all = Integer.parseInt(gmSettings.getProperty("admin_unpara_all", "100"));
            admin_para_all = Integer.parseInt(gmSettings.getProperty("admin_para_all", "100"));
            admin_unpara = Integer.parseInt(gmSettings.getProperty("admin_unpara", "100"));
            admin_para = Integer.parseInt(gmSettings.getProperty("admin_para", "100"));
            admin_unpara_all_menu = Integer.parseInt(gmSettings.getProperty("admin_unpara_all_menu", "100"));
            admin_para_all_menu = Integer.parseInt(gmSettings.getProperty("admin_para_all_menu", "100"));
            admin_unpara_menu = Integer.parseInt(gmSettings.getProperty("admin_unpara_menu", "100"));
            admin_para_menu = Integer.parseInt(gmSettings.getProperty("admin_unpara_menu", "100"));
            admin_polyself = Integer.parseInt(gmSettings.getProperty("admin_para_menu", "100"));
            admin_unpolyself = Integer.parseInt(gmSettings.getProperty("admin_polyself", "100"));
            admin_polyself_menu = Integer.parseInt(gmSettings.getProperty("admin_polyself_menu", "100"));
            admin_unpolyself_menu = Integer.parseInt(gmSettings.getProperty("admin_unpolyself_menu", "100"));
            admin_changename = Integer.parseInt(gmSettings.getProperty("admin_changename", "100"));
            admin_setteam_close = Integer.parseInt(gmSettings.getProperty("admin_setteam_close", "100"));
            admin_setteam = Integer.parseInt(gmSettings.getProperty("admin_setteam", "100"));
            admin_social = Integer.parseInt(gmSettings.getProperty("admin_social", "100"));
            admin_effect = Integer.parseInt(gmSettings.getProperty("admin_effect", "100"));
            admin_social_menu = Integer.parseInt(gmSettings.getProperty("admin_social_menu", "100"));
            admin_effect_menu = Integer.parseInt(gmSettings.getProperty("admin_effect_menu", "100"));
            admin_abnormal = Integer.parseInt(gmSettings.getProperty("admin_abnormal", "100"));
            admin_abnormal_menu = Integer.parseInt(gmSettings.getProperty("admin_abnormal_menu", "100"));
            admin_play_sounds = Integer.parseInt(gmSettings.getProperty("admin_play_sounds", "100"));
            admin_play_sound = Integer.parseInt(gmSettings.getProperty("admin_play_sound", "100"));
            admin_atmosphere = Integer.parseInt(gmSettings.getProperty("admin_atmosphere", "100"));
            admin_atmosphere_menu = Integer.parseInt(gmSettings.getProperty("admin_atmosphere_menu", "100"));

            admin_seteh = Integer.parseInt(gmSettings.getProperty("admin_seteh", "100"));
            admin_setec = Integer.parseInt(gmSettings.getProperty("admin_setec", "100"));
            admin_seteg = Integer.parseInt(gmSettings.getProperty("admin_seteg", "100"));
            admin_setel = Integer.parseInt(gmSettings.getProperty("admin_setel", "100"));
            admin_seteb = Integer.parseInt(gmSettings.getProperty("admin_seteb", "100"));
            admin_setew = Integer.parseInt(gmSettings.getProperty("admin_setew", "100"));
            admin_setes = Integer.parseInt(gmSettings.getProperty("admin_setes", "100"));
            admin_setle = Integer.parseInt(gmSettings.getProperty("admin_setle", "100"));
            admin_setre = Integer.parseInt(gmSettings.getProperty("admin_setre", "100"));
            admin_setlf = Integer.parseInt(gmSettings.getProperty("admin_setlf", "100"));
            admin_setrf = Integer.parseInt(gmSettings.getProperty("admin_setrf", "100"));
            admin_seten = Integer.parseInt(gmSettings.getProperty("admin_seten", "100"));
            admin_setun = Integer.parseInt(gmSettings.getProperty("admin_setun", "100"));
            admin_setba = Integer.parseInt(gmSettings.getProperty("admin_setba", "100"));
            admin_enchant = Integer.parseInt(gmSettings.getProperty("admin_enchant", "100"));

            admin_event = Integer.parseInt(gmSettings.getProperty("admin_event", "100"));
            admin_event_new = Integer.parseInt(gmSettings.getProperty("admin_event_new", "100"));
            admin_event_choose = Integer.parseInt(gmSettings.getProperty("admin_event_choose", "100"));
            admin_event_store = Integer.parseInt(gmSettings.getProperty("admin_event_store", "100"));
            admin_event_set = Integer.parseInt(gmSettings.getProperty("admin_event_set", "100"));
            admin_event_change_teams_number = Integer.parseInt(gmSettings.getProperty("admin_event_change_teams_number", "100"));
            admin_event_announce = Integer.parseInt(gmSettings.getProperty("admin_event_announce", "100"));
            admin_event_panel = Integer.parseInt(gmSettings.getProperty("admin_event_panel", "100"));
            admin_event_control_begin = Integer.parseInt(gmSettings.getProperty("admin_event_control_begin", "100"));
            admin_event_control_teleport = Integer.parseInt(gmSettings.getProperty("admin_event_control_teleport", "100"));
            admin_add = Integer.parseInt(gmSettings.getProperty("admin_add", "100"));
            admin_event_see = Integer.parseInt(gmSettings.getProperty("admin_event_see", "100"));
            admin_event_del = Integer.parseInt(gmSettings.getProperty("admin_event_del", "100"));
            admin_delete_buffer = Integer.parseInt(gmSettings.getProperty("admin_delete_buffer", "100"));
            admin_event_control_sit = Integer.parseInt(gmSettings.getProperty("admin_event_control_sit", "100"));
            admin_event_name = Integer.parseInt(gmSettings.getProperty("admin_event_name", "100"));
            admin_event_control_kill = Integer.parseInt(gmSettings.getProperty("admin_event_control_kill", "100"));
            admin_event_control_res = Integer.parseInt(gmSettings.getProperty("admin_event_control_res", "100"));
            admin_event_control_poly = Integer.parseInt(gmSettings.getProperty("admin_event_control_poly", "100"));
            admin_event_control_unpoly = Integer.parseInt(gmSettings.getProperty("admin_event_control_unpoly", "100"));
            admin_event_control_prize = Integer.parseInt(gmSettings.getProperty("admin_event_control_prize", "100"));
            admin_event_control_chatban = Integer.parseInt(gmSettings.getProperty("admin_event_control_chatban", "100"));
            admin_event_control_finish = Integer.parseInt(gmSettings.getProperty("admin_event_control_finish", "100"));

            admin_add_exp_sp = Integer.parseInt(gmSettings.getProperty("admin_add_exp_sp", "100"));
            admin_remove_exp_sp = Integer.parseInt(gmSettings.getProperty("admin_remove_exp_sp", "100"));

            admin_fight_calculator  = Integer.parseInt(gmSettings.getProperty("admin_fight_calculator", "100"));
            admin_fight_calculator_show = Integer.parseInt(gmSettings.getProperty("admin_fight_calculator_show", "100"));
            admin_fcs = Integer.parseInt(gmSettings.getProperty("admin_fcs", "100"));

            admin_geo_z = Integer.parseInt(gmSettings.getProperty("admin_geo_z", "100"));
            admin_geo_type = Integer.parseInt(gmSettings.getProperty("admin_geo_type", "100"));
            admin_geo_nswe = Integer.parseInt(gmSettings.getProperty("admin_geo_nswe", "100"));
            admin_geo_los = Integer.parseInt(gmSettings.getProperty("admin_geo_los", "100"));
            admin_geo_position = Integer.parseInt(gmSettings.getProperty("admin_geo_position", "100"));
            admin_geo_bug = Integer.parseInt(gmSettings.getProperty("admin_geo_bug", "100"));
            admin_geo_load = Integer.parseInt(gmSettings.getProperty("admin_geo_load", "100"));
            admin_geo_unload = Integer.parseInt(gmSettings.getProperty("admin_geo_unload", "100"));

            admin_ge_status = Integer.parseInt(gmSettings.getProperty("admin_ge_status", "100"));
            admin_ge_mode = Integer.parseInt(gmSettings.getProperty("admin_ge_mode", "100"));
            admin_ge_join = Integer.parseInt(gmSettings.getProperty("admin_ge_join", "100"));
            admin_ge_leave = Integer.parseInt(gmSettings.getProperty("admin_ge_leave", "100"));

            admin_gm = Integer.parseInt(gmSettings.getProperty("admin_gm", "100"));

            admin_gmchat = Integer.parseInt(gmSettings.getProperty("admin_gmchat", "100"));
            admin_snoop = Integer.parseInt(gmSettings.getProperty("admin_snoop", "100"));
            admin_gmchat_menu = Integer.parseInt(gmSettings.getProperty("admin_gmchat_menu", "100"));

            admin_heal = Integer.parseInt(gmSettings.getProperty("admin_heal", "100"));

            admin_help = Integer.parseInt(gmSettings.getProperty("admin_help", "100"));

            admin_invul = Integer.parseInt(gmSettings.getProperty("admin_invul", "100"));
            admin_setinvul = Integer.parseInt(gmSettings.getProperty("admin_setinvul", "100"));

            admin_kick = Integer.parseInt(gmSettings.getProperty("admin_kick", "100"));
            admin_kick_non_gm = Integer.parseInt(gmSettings.getProperty("admin_kick_non_gm", "100"));

            admin_kill = Integer.parseInt(gmSettings.getProperty("admin_kill", "100"));
            admin_kill_monster = Integer.parseInt(gmSettings.getProperty("admin_kill_monster", "100"));

            admin_remlevel = Integer.parseInt(gmSettings.getProperty("admin_remlevel", "100"));
            admin_addlevel = Integer.parseInt(gmSettings.getProperty("admin_addlevel", "100"));
            admin_setlevel = Integer.parseInt(gmSettings.getProperty("admin_setlevel", "100"));

            admin_server_gm_only = Integer.parseInt(gmSettings.getProperty("admin_server_gm_only", "100"));
            admin_server_all = Integer.parseInt(gmSettings.getProperty("admin_server_all", "100"));
            admin_server_max_player = Integer.parseInt(gmSettings.getProperty("admin_server_max_player", "100"));
            admin_server_list_clock = Integer.parseInt(gmSettings.getProperty("admin_server_list_clock", "100"));
            admin_server_login = Integer.parseInt(gmSettings.getProperty("admin_server_login", "100"));

            admin_mammon_find = Integer.parseInt(gmSettings.getProperty("admin_mammon_find", "100"));
            admin_mammon_respawn = Integer.parseInt(gmSettings.getProperty("admin_mammon_respawn", "100"));
            admin_list_spawns = Integer.parseInt(gmSettings.getProperty("admin_list_spawns", "100"));
            admin_msg = Integer.parseInt(gmSettings.getProperty("admin_msg", "100"));

            admin_manor = Integer.parseInt(gmSettings.getProperty("admin_manor", "100"));
            admin_manor_approve = Integer.parseInt(gmSettings.getProperty("admin_manor_approve", "100"));
            admin_manor_setnext = Integer.parseInt(gmSettings.getProperty("admin_manor_setnext", "100"));
            admin_manor_reset = Integer.parseInt(gmSettings.getProperty("admin_manor_reset", "100"));
            admin_manor_setmaintenance = Integer.parseInt(gmSettings.getProperty("admin_manor_setmaintenance", "100"));
            admin_manor_save = Integer.parseInt(gmSettings.getProperty("admin_manor_save", "100"));
            admin_manor_disable = Integer.parseInt(gmSettings.getProperty("admin_manor_disable", "100"));

            admin_masshero = Integer.parseInt(gmSettings.getProperty("admin_masshero", "100"));
            admin_allhero = Integer.parseInt(gmSettings.getProperty("admin_allhero", "100"));

            admin_recallclan = Integer.parseInt(gmSettings.getProperty("admin_recallclan", "100"));
            admin_recallparty = Integer.parseInt(gmSettings.getProperty("admin_recallparty", "100"));
            admin_recallally = Integer.parseInt(gmSettings.getProperty("admin_recallally", "100"));

            admin_char_manage = Integer.parseInt(gmSettings.getProperty("admin_char_manage", "100"));
            admin_teleport_character_to_menu = Integer.parseInt(gmSettings.getProperty("admin_teleport_character_to_menu", "100"));
            admin_recall_char_menu = Integer.parseInt(gmSettings.getProperty("admin_recall_char_menu", "100"));
            admin_recall_party_menu = Integer.parseInt(gmSettings.getProperty("admin_recall_party_menu", "100"));
            admin_recall_clan_menu = Integer.parseInt(gmSettings.getProperty("admin_recall_clan_menu", "100"));
            admin_goto_char_menu = Integer.parseInt(gmSettings.getProperty("admin_goto_char_menu", "100"));
            admin_kick_menu = Integer.parseInt(gmSettings.getProperty("admin_kick_menu", "100"));
            admin_ban_menu = Integer.parseInt(gmSettings.getProperty("admin_ban_menu", "100"));
            admin_unban_menu = Integer.parseInt(gmSettings.getProperty("admin_unban_menu", "100"));

            admin_unban_menu = Integer.parseInt(gmSettings.getProperty("admin_unban_menu", "100"));

            admin_mobmenu = Integer.parseInt(gmSettings.getProperty("admin_mobmenu", "100"));
            admin_mobgroup_list = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_list", "100"));
            admin_mobgroup_create = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_create", "100"));
            admin_mobgroup_remove = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_remove", "100"));
            admin_mobgroup_delete = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_delete", "100"));
            admin_mobgroup_idle = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_idle", "100"));
            admin_mobgroup_attack = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_attack", "100"));
            admin_mobgroup_rnd = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_rnd", "100"));
            admin_mobgroup_return = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_return", "100"));
            admin_mobgroup_follow = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_follow", "100"));
            admin_mobgroup_casting = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_casting", "100"));
            admin_mobgroup_nomove = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_nomove", "100"));
            admin_mobgroup_attackgrp = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_attackgrp", "100"));
            admin_mobgroup_invul = Integer.parseInt(gmSettings.getProperty("admin_mobgroup_invul", "100"));
            admin_mobinst = Integer.parseInt(gmSettings.getProperty("admin_mobinst", "100"));

            admin_mons = Integer.parseInt(gmSettings.getProperty("admin_mons", "100"));

            admin_pn_info = Integer.parseInt(gmSettings.getProperty("admin_pn_info", "100"));
            admin_show_path = Integer.parseInt(gmSettings.getProperty("admin_show_path", "100"));
            admin_path_debug = Integer.parseInt(gmSettings.getProperty("admin_path_debug", "100"));
            admin_show_pn = Integer.parseInt(gmSettings.getProperty("admin_show_pn", "100"));
            admin_find_path = Integer.parseInt(gmSettings.getProperty("admin_find_path", "100"));

            admin_find_path = Integer.parseInt(gmSettings.getProperty("admin_find_path", "100"));

            admin_view_petitions = Integer.parseInt(gmSettings.getProperty("admin_view_petitions", "100"));
            admin_view_petition = Integer.parseInt(gmSettings.getProperty("admin_view_petition", "100"));
            admin_accept_petition = Integer.parseInt(gmSettings.getProperty("admin_accept_petition", "100"));
            admin_reject_petition = Integer.parseInt(gmSettings.getProperty("admin_reject_petition", "100"));
            admin_reset_petitions = Integer.parseInt(gmSettings.getProperty("admin_reset_petitions", "100"));
            admin_force_peti = Integer.parseInt(gmSettings.getProperty("admin_force_peti", "100"));
            admin_add_peti_chat = Integer.parseInt(gmSettings.getProperty("admin_add_peti_chat", "100"));

            admin_forge = Integer.parseInt(gmSettings.getProperty("admin_forge", "100"));
            admin_forge2 = Integer.parseInt(gmSettings.getProperty("admin_forge2", "100"));
            admin_forge3 = Integer.parseInt(gmSettings.getProperty("admin_forge3", "100"));

            admin_pledge = Integer.parseInt(gmSettings.getProperty("admin_pledge", "100"));

            admin_polymorph = Integer.parseInt(gmSettings.getProperty("admin_polymorph", "100"));
            admin_unpolymorph = Integer.parseInt(gmSettings.getProperty("admin_unpolymorph", "100"));
            admin_polymorph_menu = Integer.parseInt(gmSettings.getProperty("admin_polymorph_menu", "100"));
            admin_unpolymorph_menu = Integer.parseInt(gmSettings.getProperty("admin_unpolymorph_menu", "100"));

            admin_quest_reload = Integer.parseInt(gmSettings.getProperty("admin_quest_reload", "100"));

            admin_recallall = Integer.parseInt(gmSettings.getProperty("admin_recallall", "100"));

            admin_restore = Integer.parseInt(gmSettings.getProperty("admin_restore", "100"));
            admin_repair = Integer.parseInt(gmSettings.getProperty("admin_repair", "100"));

            admin_res = Integer.parseInt(gmSettings.getProperty("admin_res", "100"));
            admin_res_monster = Integer.parseInt(gmSettings.getProperty("admin_res_monster", "100"));

            admin_ride_wyvern = Integer.parseInt(gmSettings.getProperty("admin_ride_wyvern", "100"));
            admin_ride_strider = Integer.parseInt(gmSettings.getProperty("admin_ride_strider", "100"));
            admin_unride_wyvern = Integer.parseInt(gmSettings.getProperty("admin_unride_wyvern", "100"));
            admin_unride_strider = Integer.parseInt(gmSettings.getProperty("admin_unride_strider", "100"));
            admin_unride = Integer.parseInt(gmSettings.getProperty("admin_unride", "100"));

            admin_buy = Integer.parseInt(gmSettings.getProperty("admin_buy", "100"));
            admin_gmshop = Integer.parseInt(gmSettings.getProperty("admin_gmshop", "100"));

            admin_server_shutdown = Integer.parseInt(gmSettings.getProperty("admin_server_shutdown", "100"));
            admin_server_restart = Integer.parseInt(gmSettings.getProperty("admin_server_restart", "100"));
            admin_server_abort = Integer.parseInt(gmSettings.getProperty("admin_server_abort", "100"));

            admin_siege = Integer.parseInt(gmSettings.getProperty("admin_siege", "100"));
            admin_add_attacker = Integer.parseInt(gmSettings.getProperty("admin_add_attacker", "100"));
            admin_add_defender = Integer.parseInt(gmSettings.getProperty("admin_add_defender", "100"));
            admin_add_guard = Integer.parseInt(gmSettings.getProperty("admin_add_guard", "100"));
            admin_list_siege_clans = Integer.parseInt(gmSettings.getProperty("admin_list_siege_clans", "100"));
            admin_clear_siege_list = Integer.parseInt(gmSettings.getProperty("admin_clear_siege_list", "100"));
            admin_move_defenders = Integer.parseInt(gmSettings.getProperty("admin_move_defenders", "100"));
            admin_spawn_doors = Integer.parseInt(gmSettings.getProperty("admin_spawn_doors", "100"));
            admin_endsiege = Integer.parseInt(gmSettings.getProperty("admin_endsiege", "100"));
            admin_startsiege = Integer.parseInt(gmSettings.getProperty("admin_startsiege", "100"));
            admin_setcastle = Integer.parseInt(gmSettings.getProperty("admin_setcastle", "100"));
            admin_removecastle = Integer.parseInt(gmSettings.getProperty("admin_removecastle", "100"));
            admin_clanhall = Integer.parseInt(gmSettings.getProperty("admin_clanhall", "100"));
            admin_clanhallset = Integer.parseInt(gmSettings.getProperty("admin_clanhallset", "100"));
            admin_clanhalldel = Integer.parseInt(gmSettings.getProperty("admin_clanhalldel", "100"));
            admin_clanhallopendoors = Integer.parseInt(gmSettings.getProperty("admin_clanhallopendoors", "100"));
            admin_clanhallclosedoors = Integer.parseInt(gmSettings.getProperty("admin_clanhallclosedoors", "100"));
            admin_clanhallteleportself = Integer.parseInt(gmSettings.getProperty("admin_clanhallteleportself", "100"));

            admin_show_skills = Integer.parseInt(gmSettings.getProperty("admin_show_skills", "100"));
            admin_remove_skills = Integer.parseInt(gmSettings.getProperty("admin_remove_skills", "100"));
            admin_skill_list = Integer.parseInt(gmSettings.getProperty("admin_skill_list", "100"));
            admin_skill_index = Integer.parseInt(gmSettings.getProperty("admin_skill_index", "100"));
            admin_add_skill = Integer.parseInt(gmSettings.getProperty("admin_add_skill", "100"));
            admin_remove_skill = Integer.parseInt(gmSettings.getProperty("admin_remove_skill", "100"));
            admin_get_skills = Integer.parseInt(gmSettings.getProperty("admin_get_skills", "100"));
            admin_reset_skills = Integer.parseInt(gmSettings.getProperty("admin_reset_skills", "100"));
            admin_give_all_skills = Integer.parseInt(gmSettings.getProperty("admin_give_all_skills", "100"));
            admin_remove_all_skills = Integer.parseInt(gmSettings.getProperty("admin_remove_all_skills", "100"));
            admin_add_clan_skill = Integer.parseInt(gmSettings.getProperty("admin_add_clan_skill", "100"));

            admin_show_spawns = Integer.parseInt(gmSettings.getProperty("admin_show_spawns", "100"));
            admin_spawn = Integer.parseInt(gmSettings.getProperty("admin_spawn", "100"));
            admin_spawn_monster = Integer.parseInt(gmSettings.getProperty("admin_spawn_monster", "100"));
            admin_spawn_index = Integer.parseInt(gmSettings.getProperty("admin_spawn_index", "100"));
            admin_unspawnall = Integer.parseInt(gmSettings.getProperty("admin_unspawnall", "100"));
            admin_respawnall = Integer.parseInt(gmSettings.getProperty("admin_respawnall", "100"));
            admin_spawn_reload = Integer.parseInt(gmSettings.getProperty("admin_spawn_reload", "100"));
            admin_npc_index = Integer.parseInt(gmSettings.getProperty("admin_npc_index", "100"));
            admin_spawn_once = Integer.parseInt(gmSettings.getProperty("admin_spawn_once", "100"));
            admin_show_npcs = Integer.parseInt(gmSettings.getProperty("admin_show_npcs", "100"));
            admin_teleport_reload = Integer.parseInt(gmSettings.getProperty("admin_teleport_reload", "100"));
            admin_spawnnight = Integer.parseInt(gmSettings.getProperty("admin_spawnnight", "100"));
            admin_spawnday = Integer.parseInt(gmSettings.getProperty("admin_spawnday", "100"));

            admin_target = Integer.parseInt(gmSettings.getProperty("admin_target", "100"));

            admin_show_moves = Integer.parseInt(gmSettings.getProperty("admin_show_moves", "100"));
            admin_show_moves_other = Integer.parseInt(gmSettings.getProperty("admin_show_moves_other", "100"));
            admin_show_teleport = Integer.parseInt(gmSettings.getProperty("admin_show_teleport", "100"));
            admin_teleport_to_character = Integer.parseInt(gmSettings.getProperty("admin_teleport_to_character", "100"));
            admin_recall = Integer.parseInt(gmSettings.getProperty("admin_recall", "100"));
            admin_walk = Integer.parseInt(gmSettings.getProperty("admin_walk", "100"));
            admin_explore = Integer.parseInt(gmSettings.getProperty("admin_explore", "100"));
            admin_recall_npc = Integer.parseInt(gmSettings.getProperty("admin_recall_npc", "100"));
            admin_gonorth = Integer.parseInt(gmSettings.getProperty("admin_gonorth", "100"));
            admin_gosouth = Integer.parseInt(gmSettings.getProperty("admin_gosouth", "100"));
            admin_goeast = Integer.parseInt(gmSettings.getProperty("admin_goeast", "100"));
            admin_gowest = Integer.parseInt(gmSettings.getProperty("admin_gowest", "100"));
            admin_goup = Integer.parseInt(gmSettings.getProperty("admin_goup", "100"));
            admin_godown = Integer.parseInt(gmSettings.getProperty("admin_godown", "100"));
            admin_tele = Integer.parseInt(gmSettings.getProperty("admin_tele", "100"));
            admin_teleto = Integer.parseInt(gmSettings.getProperty("admin_teleto", "100"));
            admin_instant_move = Integer.parseInt(gmSettings.getProperty("admin_instant_move", "100"));
            admin_sendhome = Integer.parseInt(gmSettings.getProperty("admin_sendhome", "100"));

            admin_test = Integer.parseInt(gmSettings.getProperty("admin_test", "100"));
            admin_stats = Integer.parseInt(gmSettings.getProperty("admin_stats", "100"));
            admin_skill_test = Integer.parseInt(gmSettings.getProperty("admin_skill_test", "100"));
            admin_st = Integer.parseInt(gmSettings.getProperty("admin_st", "100"));
            admin_mp = Integer.parseInt(gmSettings.getProperty("admin_mp", "100"));
            admin_known = Integer.parseInt(gmSettings.getProperty("admin_known", "100"));

            admin_tvt_add = Integer.parseInt(gmSettings.getProperty("admin_tvt_add", "100"));
            admin_tvt_remove = Integer.parseInt(gmSettings.getProperty("admin_tvt_remove", "100"));
            admin_tvt_advance = Integer.parseInt(gmSettings.getProperty("admin_tvt_advance", "100"));

            admin_unblockip = Integer.parseInt(gmSettings.getProperty("admin_unblockip", "100"));

            admin_removevip = Integer.parseInt(gmSettings.getProperty("admin_removevip", "100"));
            admin_setvip = Integer.parseInt(gmSettings.getProperty("admin_setvip", "100"));

            admin_zone_check = Integer.parseInt(gmSettings.getProperty("admin_zone_check", "100"));
            admin_zone_reload = Integer.parseInt(gmSettings.getProperty("admin_zone_reload", "100"));


        }
	    catch (Exception e)
	    {
            e.printStackTrace();
            throw new Error("Failed to Load " + ADMIN_FILE + " File.");
        }
    }


    // --------------------------------------------- //
    public static boolean ALLOW_RES_COMMAND;
    // ============================================================
    public static int RES_CMD_CONSUME_ID;
    // --------------------------------------------- //
    public static int RES_ITEM_COUNT;
    // -        VOICE COMMAND PROPERTIES           - //
    public static boolean REC_BUY;
    public static int REC_ITEM_ID;
    public static int REC_ITEM_COUNT;
    public static int REC_REWARD;
    public static boolean ALLOW_LOC_VOICECOMMAND;
    public static boolean ALLOW_TRADEOFF_VOICE_COMMAND;
    public static boolean ENABLE_VIP_TELEPORT;
    public static boolean ENABLE_ONLINE_COMMAND;
    public static boolean ALLOW_STAT_VIEW;
    public static boolean ENABLE_INFO;
    public static boolean BANKING_SYSTEM_ENABLED;
	public static boolean ALLOW_STATS_COMMAND;
	public static boolean ALLOW_CASTLE_COMMAND;
	public static boolean ALLOW_SET_COMMAND;
    public static int BANKING_SYSTEM_GOLDBARS;
    public static int BANKING_SYSTEM_ADENA;
    public static int BANKING_SYSTEM_GB_ID;
    public static boolean ALLOW_AWAY_STATUS;
	public static boolean AWAY_PEACE_ZONE;
	public static boolean ALT_AWAY_ALLOW_INTERFERENCE;
	public static boolean AWAY_PLAYER_TAKE_AGGRO;
	public static int AWAY_TITLE_COLOR;
	public static int AWAY_TIMER;
	public static int BACK_TIMER;
	// ============================================================

    public static void loadCommandConfig()
    {
	    try(InputStream is = new FileInputStream(new File(COMMAND_FILE)))
	    {
	    	Properties Command = new Properties();
	    	Command.load(is);

	    	ALLOW_RES_COMMAND = Boolean.parseBoolean(Command.getProperty("AllowResCommand", "False"));
	    	RES_CMD_CONSUME_ID = Integer.parseInt(Command.getProperty("ResCommandConsumeId", "3470"));
	    	RES_ITEM_COUNT = Integer.parseInt(Command.getProperty("ResItemCount", "1"));
	    	REC_BUY = Boolean.parseBoolean(Command.getProperty("AlowBuyRec", "True"));
	    	REC_ITEM_ID = Integer.parseInt(Command.getProperty("RecItemID", "57"));
	    	REC_ITEM_COUNT = Integer.parseInt(Command.getProperty("RecItemCount", "1000000000"));
	    	REC_REWARD = Integer.parseInt(Command.getProperty("RecReward", "1"));
	    	ALLOW_LOC_VOICECOMMAND = Boolean.parseBoolean(Command.getProperty("LocVoiceCommand", "False"));
	    	ALLOW_TRADEOFF_VOICE_COMMAND = Boolean.parseBoolean(Command.getProperty("TradeOffCommand","False"));
	    	ENABLE_VIP_TELEPORT = Boolean.parseBoolean(Command.getProperty("VipTeleport","False"));
	    	ENABLE_ONLINE_COMMAND = Boolean.parseBoolean(Command.getProperty("EnableOnlinePlayersCommand", "False"));
	    	ALLOW_STAT_VIEW = Boolean.valueOf(Command.getProperty("AllowStatView", "False"));
	    	ENABLE_INFO = Boolean.parseBoolean(Command.getProperty("AllowinfoView","False"));
	    	BANKING_SYSTEM_ENABLED = Boolean.parseBoolean(Command.getProperty("BankingEnabled", "False"));
	    	BANKING_SYSTEM_GOLDBARS = Integer.parseInt(Command.getProperty("BankingGoldbarCount", "1"));
	    	BANKING_SYSTEM_ADENA = Integer.parseInt(Command.getProperty("BankingAdenaCount", "500000000"));
	    	BANKING_SYSTEM_GB_ID = Integer.parseInt(Command.getProperty("BankingGoldbarId", "3470"));
			ALLOW_STATS_COMMAND = Boolean.parseBoolean(Command.getProperty("StatsCommandEnabled", "False"));
			ALLOW_CASTLE_COMMAND = Boolean.parseBoolean(Command.getProperty("CastleCommandEnabled", "False"));
			ALLOW_SET_COMMAND = Boolean.parseBoolean(Command.getProperty("SetCommandEnabled", "False"));
			ALLOW_AWAY_STATUS = Boolean.parseBoolean(Command.getProperty("AllowAwayStatus", "False"));
	    	AWAY_PEACE_ZONE = Boolean.parseBoolean(Command.getProperty("AwayOnlyInPeaceZone", "False"));
	    	ALT_AWAY_ALLOW_INTERFERENCE = Boolean.parseBoolean(Command.getProperty("AwayAllowInterference", "False"));
	    	AWAY_PLAYER_TAKE_AGGRO = Boolean.parseBoolean(Command.getProperty("AwayPlayerTakeAggro", "False"));
	    	AWAY_TITLE_COLOR = Integer.decode("0x" + Command.getProperty("AwayTitleColor", "0000FF"));
	    	AWAY_TIMER = Integer.parseInt(Command.getProperty("AwayTimer", "30"));
	    	BACK_TIMER = Integer.parseInt(Command.getProperty("BackTimer", "30"));
	    }
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load " + COMMAND_FILE + " File.");
		}
    }

	// --------------------------------------------- //
    // -            L2JBRASIL PROPERTIES           - //
    // --------------------------------------------- //
    // ============================================================
	public static byte LEVEL_ON_ENTER;
    public static int SP_ON_ENTER;
    public static int STARTING_ADENA;
    public static boolean CUSTOM_STARTER_ITEMS_ENABLED;
	public static List<int[]> STARTING_CUSTOM_ITEMS_F = new ArrayList<>();
	public static List<int[]> STARTING_CUSTOM_ITEMS_M = new ArrayList<>();
	public static int STARTING_GB_ID;
    public static long STARTING_GB_COUNT;
    public static boolean ALT_NEW_SPAWN;
    public static int ALT_NEW_SPAWN_X;
    public static int ALT_NEW_SPAWN_Y;
    public static int ALT_NEW_SPAWN_Z;
	public static boolean CUSTOM_RESPAWN;
	public static int RESPAWN_X;
	public static int RESPAWN_Y;
	public static int RESPAWN_Z;
	public static boolean CHARS_TITLE;
    public static String  TITLE_FOR_NEW_CHARS;
    public static int MANA_POTION_RES;
    public static int ALT_SUBCLASS_LEVEL;
    public static boolean DISABLE_GRADE_PENALTY;
    public static boolean ALLOW_CLASS_USE_HEAVY;
    public static List<Integer> NOT_ALLOWED_USE_HEAVY;
    public static boolean ALLOW_CLASS_USE_LIGHT;
    public static List<Integer> NOT_ALLOWED_USE_LIGHT;
    public static boolean ALLOW_DAGGERS_WEAR_HEAVY;
	public static boolean SHOW_NPC_CREST;
    public static boolean WELCOME_HTM;
    public static boolean ALLOW_MESSAGE_ON_ENTER;
    public static String MESSAGE_ON_ENTER;
    public static boolean ONLINE_PLAYERS_ON_LOGIN;
    public static int PLAYERS_ONLINE_TRICK;
    public static boolean SHOW_WELCOME_PM;
    public static String  PM_FROM;
    public static String  PM_TEXT1;
    public static String  PM_TEXT2;
    public static boolean ANNOUNCE_GM_LOGIN;
    public static boolean ANNOUNCE_CASTLE_LORDS;
    public static boolean ANNOUNCE_VIP_LOGIN;
    public static boolean ANNOUNCE_AIO_LOGIN;
    public static boolean ANNOUNCE_TO_ALL_SPAWN_RB;
    public static String  SERVERNAME;
	public static boolean ANNOUNCE_BAN_CHAT;
	public static boolean ANNOUNCE_UNBAN_CHAT;
    public static boolean ENABLE_MODIFY_SKILL_DURATION;
    public static Map<Integer, Integer> SKILL_DURATION_LIST;
    public static boolean ENABLE_MODIFY_SKILL_REUSE;
    public static Map<Integer, Integer> SKILL_REUSE_LIST;
    public static boolean LEAVE_BUFFS_ON_DIE;
    public static boolean ALLOW_HERO_SKILLS_ON_SUB;
    public static boolean RESTORE_EFFECTS_ON_SUBCLASS_CHANGE;
    public static boolean KEEP_SUBCLASS_SKILLS;
    public static boolean ALLOW_DUALBOX;
	public static int ALLOWED_BOXES;
	public static boolean ALLOW_DUALBOX_OLY;
	public static boolean ALLOW_DUALBOX_EVENT;
	public static boolean ALLOW_CHAR_KILL_PROTECT;
	public static boolean L2WALKER_PROTECTION;
	public static boolean ALLOW_SAME_IP_NOT_GIVE_PVP_POINT;
	public static boolean GG_ENABLE;
	public static boolean ALLOW_PARTY_TRADE;
	public static String[] FORBIDDEN_NAMES;
	public static int ALT_PLAYER_PROTECTION_LEVEL;
	public static int MAX_RUN_SPEED;
	public static int RUN_SPD_BOOST;
	public static int MAX_PATK_SPEED;
    public static int MAX_MATK_SPEED;
    public static int MAX_EVASION;
    public static int MAX_PCRIT_RATE;
    public static int MAX_MCRIT_RATE;
    public static double MULTIPLE_MCRIT;
    public static boolean CUSTOM_SPAWNLIST_TABLE;
    public static boolean DELETE_GMSPAWN_ON_CUSTOM;
    public static int DUEL_SPAWN_X;
    public static int DUEL_SPAWN_Y;
    public static int DUEL_SPAWN_Z;
    public static boolean ALLOW_NOBLE_CUSTOM_ITEM;
    public static int NOBLE_CUSTOM_ITEM_ID;
    public static boolean ACTIVE_SUB_NEEDED_TO_USE_NOBLE_ITEM;
    public static int NOBLE_CUSTOM_LEVEL;
    public static boolean ALLOW_HERO_CUSTOM_ITEM;
    public static int HERO_CUSTOM_ITEM_ID;
    public static boolean NOBLE_STATUS_NEEDED_TO_USE_HERO_ITEM;
    public  static  boolean ALT_DISABLE_RAIDBOSS_PETRIFICATION;
    public static int PLAYER_SPAWN_PROTECTION;
    public static boolean PLAYER_SPAWN_PROTECTION_EFFECT;
    public static int PLAYER_EFFECT_ID;
    public static int ALT_WH_DEPOSIT_FEE;
    public static boolean ARGUMENTS_RETAIL;
	public static boolean SELL_BY_ITEM;
	public static int SELL_ITEM;
	public static int PVP_TO_USE_STORE;

    // ============================================================
	public static void loadBrasilConfig()
	{
	    try(InputStream is = new FileInputStream(new File(L2JBRASIL_FILE)))
	    {
	    	Properties L2JBrasil = new Properties();
	    	L2JBrasil.load(is);

	    	LEVEL_ON_ENTER = Byte.parseByte(L2JBrasil.getProperty("LevelOnEnter", "0"));
	    	SP_ON_ENTER = Integer.parseInt(L2JBrasil.getProperty("SPOnEnter", "0"));
	    	STARTING_ADENA = Integer.parseInt(L2JBrasil.getProperty("StartingAdena", "100"));
	    	CUSTOM_STARTER_ITEMS_ENABLED = Boolean.parseBoolean(L2JBrasil.getProperty("CustomStarterItemsEnabled", "False"));
	    	if (Config.CUSTOM_STARTER_ITEMS_ENABLED)
	    	{
	    		String[] propertySplit = L2JBrasil.getProperty("StartingItemsMage", "57,0").split(";");
	    		for (String reward : propertySplit)
	    		{
	    			String[] rewardSplit = reward.split(",");
	    			if (rewardSplit.length != 2)
	    				_log.warning("StartingCustomItemsMage[Config.load()]: invalid config property -> StartingCustomItemsMage \"" + reward + "\"");
	    			else
	    			{
	    				try
	    				{
	    					STARTING_CUSTOM_ITEMS_M.add(new int[]{Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1])});
	    				}
	    				catch (NumberFormatException nfe)
	    				{
	    					nfe.printStackTrace();
	    					if (!reward.isEmpty())
	    						_log.warning("StartingCustomItemsMage[Config.load()]: invalid config property -> StartingCustomItemsMage \"" + reward + "\"");
	    				}
	    			}
	    		}
	    		propertySplit = L2JBrasil.getProperty("StartingItemsFighter", "57,0").split(";");
	    		for (String reward : propertySplit)
	    		{
	    			String[] rewardSplit = reward.split(",");
	    			if (rewardSplit.length != 2)
	    				_log.warning("StartingCustomItemsFighter[Config.load()]: invalid config property -> StartingCustomItemsFighter \"" + reward + "\"");
	    			else
	    			{
	    				try
	    				{
	    					STARTING_CUSTOM_ITEMS_F.add(new int[]{Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1])});
	    				}
	    				catch (NumberFormatException nfe)
	    				{
	    					nfe.printStackTrace();
	    					if (!reward.isEmpty())
	    						_log.warning("StartingCustomItemsFighter[Config.load()]: invalid config property -> StartingCustomItemsFighter \"" + reward + "\"");
	    				}
	    			}
	    		}
	    	}
	    	STARTING_GB_ID = Integer.parseInt(L2JBrasil.getProperty("StartingGBId", "3470"));
	    	STARTING_GB_COUNT = Long.parseLong(L2JBrasil.getProperty("StartingGBCount", "0"));
	    	ALT_NEW_SPAWN = Boolean.parseBoolean(L2JBrasil.getProperty("Customspawn", "False"));
	    	ALT_NEW_SPAWN_X = Integer.parseInt(L2JBrasil.getProperty("CustomSpawnX", ""));
	    	ALT_NEW_SPAWN_Y = Integer.parseInt(L2JBrasil.getProperty("CustomSpawnY", ""));
	    	ALT_NEW_SPAWN_Z = Integer.parseInt(L2JBrasil.getProperty("CustomSpawnZ", ""));
	    	CUSTOM_RESPAWN = Boolean.parseBoolean(L2JBrasil.getProperty("CustomReSpawn", "False"));
	    	RESPAWN_X = Integer.parseInt(L2JBrasil.getProperty("RespawnLocationX", ""));
	    	RESPAWN_Y = Integer.parseInt(L2JBrasil.getProperty("RespawnLocationY", ""));
	    	RESPAWN_Z = Integer.parseInt(L2JBrasil.getProperty("RespawnLocationZ", ""));
	    	CHARS_TITLE = Boolean.parseBoolean(L2JBrasil.getProperty("NewCharTitle", "False"));
	    	TITLE_FOR_NEW_CHARS = L2JBrasil.getProperty("CharTitle", "L2JBrasil");
	    	MANA_POTION_RES = Integer.parseInt(L2JBrasil.getProperty("ManaPotionMPRes", "200"));
	    	ALT_SUBCLASS_LEVEL = Integer.parseInt(L2JBrasil.getProperty("AltSubClassLevel", "40"));
	    	DISABLE_GRADE_PENALTY = Boolean.parseBoolean(L2JBrasil.getProperty("DisableGradePenalty", "False"));
	    	ALLOW_CLASS_USE_LIGHT = Boolean.parseBoolean(L2JBrasil.getProperty("AllowHeavyUseLight", "False"));
            NOT_ALLOWED_USE_LIGHT = new ArrayList<>();
            for(String classId : L2JBrasil.getProperty("NotAllowedUseLight", "").split(","))
            {
                NOT_ALLOWED_USE_LIGHT.add(Integer.parseInt(classId));
            }
            ALLOW_CLASS_USE_HEAVY = Boolean.parseBoolean(L2JBrasil.getProperty("AllowLightUseHeavy", "False"));
            NOT_ALLOWED_USE_HEAVY = new ArrayList<>();
            for(String classId : L2JBrasil.getProperty("NotAllowedUseHeavy", "").split(","))
            {
                NOT_ALLOWED_USE_HEAVY.add(Integer.parseInt(classId));
            }
	    	ALLOW_DAGGERS_WEAR_HEAVY = Boolean.parseBoolean(L2JBrasil.getProperty("AllowDaggersUseHeavy", "True"));
	    	SHOW_NPC_CREST = Boolean.parseBoolean(L2JBrasil.getProperty("ShowNpcCrest", "False"));
	    	WELCOME_HTM = Boolean.parseBoolean(L2JBrasil.getProperty("WelcomeHtm", "False"));
	    	ALLOW_MESSAGE_ON_ENTER = Boolean.parseBoolean(L2JBrasil.getProperty("AllowMessageOnEnter", "False"));
	    	MESSAGE_ON_ENTER = L2JBrasil.getProperty("MessageOnEnter", "L2Frenetic Project!");
	    	ONLINE_PLAYERS_ON_LOGIN = Boolean.parseBoolean(L2JBrasil.getProperty("OnlineOnLogin", "False"));
	    	PLAYERS_ONLINE_TRICK = Integer.parseInt(L2JBrasil.getProperty("OnlinePlayerAdd", "0"));
	    	SHOW_WELCOME_PM = Boolean.parseBoolean(L2JBrasil.getProperty("ShowWelcomePM", "False"));
	    	PM_FROM = L2JBrasil.getProperty("PMFrom", "Server");
	    	PM_TEXT1 = L2JBrasil.getProperty("PMText1", "Welcome to our server");
	    	PM_TEXT2 = L2JBrasil.getProperty("PMText2", "Visit our web http://Your.Web.Adress");
	    	ANNOUNCE_GM_LOGIN = Boolean.parseBoolean(L2JBrasil.getProperty("AnnounceGMLogin", "False"));
	    	ANNOUNCE_CASTLE_LORDS = Boolean.parseBoolean(L2JBrasil.getProperty("AnnounceCastleLords", "False"));
	    	ANNOUNCE_VIP_LOGIN = Boolean.parseBoolean(L2JBrasil.getProperty("AnnounceVipLogin", "False"));
	    	ANNOUNCE_AIO_LOGIN = Boolean.parseBoolean(L2JBrasil.getProperty("AnnounceAioLogin", "False"));
	    	ANNOUNCE_TO_ALL_SPAWN_RB = Boolean.parseBoolean(L2JBrasil.getProperty("AnnounceSpawnRaid", "False"));
	    	SERVERNAME = L2JBrasil.getProperty("ServerName", "L2JBrasil");
	    	ANNOUNCE_BAN_CHAT = Boolean.parseBoolean(L2JBrasil.getProperty("AnnounceBanChat", "false"));
	    	ANNOUNCE_UNBAN_CHAT = Boolean.parseBoolean(L2JBrasil.getProperty("AnnounceUnbanChat", "false"));
	    	ENABLE_MODIFY_SKILL_DURATION = Boolean.parseBoolean(L2JBrasil.getProperty("EnableModifySkillDuration", "False"));
	    	if (ENABLE_MODIFY_SKILL_DURATION)
	    	{
	    		String[] propertySplit = L2JBrasil.getProperty("SkillDurationList", "").split(";");
	    		SKILL_DURATION_LIST = new HashMap<>(propertySplit.length);
	    		for (String skill : propertySplit)
	    		{
	    			String[] skillSplit = skill.split(",");
	    			if (skillSplit.length != 2)
	    			{
	    				System.out.println("[SkillDurationList]: invalid config property -> SkillDurationList \"" + skill + "\"");
	    			}
	    			else
	    			{
	    				try
	    				{
	    					SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
	    				}
	    				catch (NumberFormatException nfe)
	    				{
	    					if (!skill.equals(""))
	    					{
	    						System.out.println("[SkillDurationList]: invalid config property -> SkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
	    					}
	    				}
	    			}
	    		}
	    	}
	    	ENABLE_MODIFY_SKILL_REUSE = Boolean.parseBoolean(L2JBrasil.getProperty("EnableModifySkillReuse", "false"));
	    	if (ENABLE_MODIFY_SKILL_REUSE)
	    	{
	    		String[] propertySplit = L2JBrasil.getProperty("SkillReuseList", "").split(";");
	    		SKILL_REUSE_LIST = new HashMap<>(propertySplit.length);
	    		for (String skill : propertySplit)
	    		{
	    			String[] skillSplit = skill.split(",");
	    			if (skillSplit.length != 2)
	    			{
	    				System.out.println("[SkillReuseList]: invalid config property -> SkillReuseList \"" + skill + "\"");
	    			}
	    			else
	    			{
	    				try
	    				{
	    					SKILL_REUSE_LIST.put(Integer.valueOf(skillSplit[0]), Integer.valueOf(skillSplit[1]));
	    				}
	    				catch (NumberFormatException nfe)
	    				{
	    					if (!skill.equals(""))
	    					{
	    						System.out.println("[SkillReuseList]: invalid config property -> SkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
	            	  		}
	    				}
	    			}
	    		}
	    	}
	    	LEAVE_BUFFS_ON_DIE = Boolean.parseBoolean(L2JBrasil.getProperty("NoRemoveBuffsOnDie", "False"));
	    	ALLOW_HERO_SKILLS_ON_SUB = Boolean.parseBoolean(L2JBrasil.getProperty("AllowHeroSkillsOnSub", "False"));
	    	RESTORE_EFFECTS_ON_SUBCLASS_CHANGE = Boolean.parseBoolean(L2JBrasil.getProperty("RestoreEffectsOnSub", "False"));
	    	KEEP_SUBCLASS_SKILLS = Boolean.parseBoolean(L2JBrasil.getProperty("KeepSubClassSkills", "False"));
	    	ALLOW_DUALBOX = Boolean.parseBoolean(L2JBrasil.getProperty("AllowDualBox", "True"));
	    	ALLOWED_BOXES = Integer.parseInt(L2JBrasil.getProperty("AllowedBoxes", "2"));
	    	ALLOW_DUALBOX_OLY = Boolean.parseBoolean(L2JBrasil.getProperty("AllowDualBoxInOly", "True"));
	    	ALLOW_DUALBOX_EVENT = Boolean.parseBoolean(L2JBrasil.getProperty("AllowDualBoxInEvent", "True"));
	    	ALLOW_CHAR_KILL_PROTECT = Boolean.parseBoolean(L2JBrasil.getProperty("AllowLowLvlProtect", "False"));
	    	L2WALKER_PROTECTION = Boolean.parseBoolean(L2JBrasil.getProperty("L2WalkerProtection", "False"));
	    	ALLOW_SAME_IP_NOT_GIVE_PVP_POINT = Boolean.parseBoolean(L2JBrasil.getProperty("AllowSameIPDontGivePvPPoint", "False"));
            GG_ENABLE = Boolean.parseBoolean(L2JBrasil.getProperty("GuardSystem", "False"));
	    	ALLOW_PARTY_TRADE = Boolean.parseBoolean(L2JBrasil.getProperty("AllowPartyTrade", "False"));
	    	FORBIDDEN_NAMES = L2JBrasil.getProperty("ForbiddenNames", "").split(",");
	    	ALT_PLAYER_PROTECTION_LEVEL = Integer.parseInt(L2JBrasil.getProperty("AltPlayerProtectionLevel", "0"));
	    	MAX_RUN_SPEED = Integer.parseInt(L2JBrasil.getProperty("MaxRunSpeed", "250"));
	    	RUN_SPD_BOOST = Integer.parseInt(L2JBrasil.getProperty("RunSpeedBoost", "0"));
	    	MAX_PATK_SPEED = Integer.parseInt(L2JBrasil.getProperty("MaxPAtkSpeed", "1500"));
	    	MAX_MATK_SPEED = Integer.parseInt(L2JBrasil.getProperty("MaxMAtkSpeed", "1999"));
	    	MAX_EVASION = Integer.parseInt(L2JBrasil.getProperty("MaxEvasion", "200"));
	    	MAX_PCRIT_RATE = Integer.parseInt(L2JBrasil.getProperty("MaxPCritRate", "500"));
	    	MAX_MCRIT_RATE = Integer.parseInt(L2JBrasil.getProperty("MaxMCritRate", "300"));
	    	MULTIPLE_MCRIT = Double.parseDouble(L2JBrasil.getProperty("MultipleMCrit", "4.0"));
	    	CUSTOM_SPAWNLIST_TABLE = Boolean.valueOf(L2JBrasil.getProperty("GmSpawnOnCustom", "True"));
	    	DELETE_GMSPAWN_ON_CUSTOM = Boolean.valueOf(L2JBrasil.getProperty("DeleteGmSpawnOnCustom", "False"));
	    	DUEL_SPAWN_X = Integer.parseInt(L2JBrasil.getProperty("PartyDuelSpawnX", "149319"));
	    	DUEL_SPAWN_Y = Integer.parseInt(L2JBrasil.getProperty("PartyDuelSpawnY", "46710"));
	    	DUEL_SPAWN_Z = Integer.parseInt(L2JBrasil.getProperty("PartyDuelSpawnZ", "-3413"));
	    	ALLOW_NOBLE_CUSTOM_ITEM = Boolean.parseBoolean(L2JBrasil.getProperty("AllowNobleCustomItem", "False"));
	    	NOBLE_CUSTOM_ITEM_ID = Integer.parseInt(L2JBrasil.getProperty("NobleItemId", "6673"));
	    	ACTIVE_SUB_NEEDED_TO_USE_NOBLE_ITEM = Boolean.parseBoolean(L2JBrasil.getProperty("ActiveSubNeededToUseNobleItem", "True"));
	    	NOBLE_CUSTOM_LEVEL = Integer.parseInt(L2JBrasil.getProperty("LevelNeededToUseNobleCustomItem", "76"));
	    	ALLOW_HERO_CUSTOM_ITEM = Boolean.parseBoolean(L2JBrasil.getProperty("AllowHeroCustomItem", "False"));
	    	HERO_CUSTOM_ITEM_ID = Integer.parseInt(L2JBrasil.getProperty("HeroCustomItemID", "7196"));
	    	NOBLE_STATUS_NEEDED_TO_USE_HERO_ITEM = Boolean.parseBoolean(L2JBrasil.getProperty("NobleStatusNeededToUseHeroItem", "True"));
	    	ALT_DISABLE_RAIDBOSS_PETRIFICATION  = Boolean.parseBoolean(L2JBrasil.getProperty("DisableRaidBossPetrification", "False"));
	    	PLAYER_SPAWN_PROTECTION = Integer.parseInt(L2JBrasil.getProperty("PlayerSpawnProtection", "0"));
	    	PLAYER_SPAWN_PROTECTION_EFFECT = Boolean.parseBoolean(L2JBrasil.getProperty("PlayerSpawnEffect", "0"));
	    	PLAYER_EFFECT_ID = Integer.parseInt(L2JBrasil.getProperty("PlayerEffectId", "0"));
	    	ALT_WH_DEPOSIT_FEE = Integer.parseInt(L2JBrasil.getProperty("AltWarehouseDepositFee", "30"));
	    	ARGUMENTS_RETAIL = Boolean.parseBoolean(L2JBrasil.getProperty("ArgumentsRetailLike", "true"));
	    	SELL_BY_ITEM = Boolean.parseBoolean(L2JBrasil.getProperty("SellByItem", "False"));
	    	SELL_ITEM = Integer.parseInt(L2JBrasil.getProperty("SellItem", "57"));
	    	PVP_TO_USE_STORE = Integer.parseInt(L2JBrasil.getProperty("PvPToUseStore", "1"));
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    	throw new Error("Failed to Load " + L2JBRASIL_FILE + " File.");
	    }
	}

	// --------------------------------------------- //
    // -             L2JMODS PROPERTIES            - //
    // --------------------------------------------- //
    // ============================================================
    public static boolean L2JMOD_CHAMPION_ENABLE;
    public static int L2JMOD_CHAMPION_FREQUENCY;
    public static int L2JMOD_CHAMP_MIN_LVL;
    public static int L2JMOD_CHAMP_MAX_LVL;
    public static int L2JMOD_CHAMPION_HP;
    public static int L2JMOD_CHAMPION_REWARDS;
    public static float L2JMOD_CHAMPION_ADENAS_REWARDS;
    public static float L2JMOD_CHAMPION_HP_REGEN;
    public static float L2JMOD_CHAMPION_ATK;
    public static float L2JMOD_CHAMPION_SPD_ATK;
    public static int L2JMOD_CHAMPION_REWARD;
    public static int L2JMOD_CHAMPION_REWARD_ID;
    public static int L2JMOD_CHAMPION_REWARD_QTY;
    public static boolean L2JMOD_WEDDING_ANNOUNCE;
    public static boolean L2JMOD_ALLOW_WEDDING;
    public static int L2JMOD_WEDDING_PRICE;
    public static boolean L2JMOD_WEDDING_PUNISH_INFIDELITY;
    public static boolean L2JMOD_WEDDING_TELEPORT;
    public static int L2JMOD_WEDDING_TELEPORT_PRICE;
    public static int L2JMOD_WEDDING_TELEPORT_DURATION;
    public static boolean L2JMOD_WEDDING_SAMESEX;
    public static boolean L2JMOD_WEDDING_FORMALWEAR;
    public static int L2JMOD_WEDDING_DIVORCE_COSTS;
	public static boolean L2JMOD_WEDDING_COLOR_NAME;
	public static int L2JMOD_WEDDING_COLOR_NAMES;
	public static int L2JMOD_WEDDING_COLOR_NAMES_GEY;
	public static int L2JMOD_WEDDING_COLOR_NAMES_LIZ;
    public static boolean PCB_ENABLE;
    public static int PCB_MIN_LEVEL;
    public static int PCB_POINT_MIN;
    public static int PCB_POINT_MAX;
    public static int PCB_CHANCE_DUAL_POINT;
    public static int PCB_INTERVAL;
    public static int PCB_ITEMS_ID;
	public static boolean OFFLINE_TRADE_ENABLE;
	public static boolean OFFLINE_CRAFT_ENABLE;
	public static boolean OFFLINE_SET_NAME_COLOR;
	public static int OFFLINE_NAME_COLOR;
	public static boolean OFFLINE_LOGOUT;
	public static boolean OFFLINE_SLEEP_EFFECT;
	public static boolean OFFLINE_RESTORE_OFFLINERS;
	public static int OFFLINE_MAX_DAYS;
	public static boolean OFFLINE_DISCONNECT_FINISHED;
	public static int OFFLINE_LOGOUT_ITEM_ID;
	public static int OFFLINE_LOGOUT_ITEM_COUNT;
    public static boolean L2JMOD_CHECK_SKILLS_ON_ENTER;
    public static List<Integer>  L2JMOD_ALLOWED_SKILLS_LIST;
    public static boolean L2JMOD_CHECK_HERO_SKILLS;
    public static boolean L2JMOD_CHECK_NOBLE_SKILLS;
	public static List<Integer> L2JMOD_LIST_NO_CHECK_SKILLS;

	// ============================================================
	public static void loadL2JModConfig()
	{
		try(InputStream is = new FileInputStream(new File(L2JMOD_FILE)))
	    {
			Properties L2JModSettings = new Properties();
	        L2JModSettings.load(is);

	        L2JMOD_CHAMPION_ENABLE = Boolean.parseBoolean(L2JModSettings.getProperty("ChampionEnable", "false"));
	        L2JMOD_CHAMPION_FREQUENCY = Integer.parseInt(L2JModSettings.getProperty("ChampionFrequency", "0"));
	        L2JMOD_CHAMP_MIN_LVL = Integer.parseInt(L2JModSettings.getProperty("ChampionMinLevel", "20"));
	        L2JMOD_CHAMP_MAX_LVL = Integer.parseInt(L2JModSettings.getProperty("ChampionMaxLevel", "60"));
	        L2JMOD_CHAMPION_HP = Integer.parseInt(L2JModSettings.getProperty("ChampionHp", "7"));
	        L2JMOD_CHAMPION_REWARDS = Integer.parseInt(L2JModSettings.getProperty("ChampionRewards", "8"));
	        L2JMOD_CHAMPION_ADENAS_REWARDS = Float.parseFloat(L2JModSettings.getProperty("ChampionAdenasRewards", "1"));
	        L2JMOD_CHAMPION_HP_REGEN = Float.parseFloat(L2JModSettings.getProperty("ChampionHpRegen", "1."));
	        L2JMOD_CHAMPION_ATK = Float.parseFloat(L2JModSettings.getProperty("ChampionAtk", "1."));
	        L2JMOD_CHAMPION_SPD_ATK = Float.parseFloat(L2JModSettings.getProperty("ChampionSpdAtk", "1."));
	        L2JMOD_CHAMPION_REWARD = Integer.parseInt(L2JModSettings.getProperty("ChampionRewardItem", "0"));
	        L2JMOD_CHAMPION_REWARD_ID = Integer.parseInt(L2JModSettings.getProperty("ChampionRewardItemID", "6393"));
	        L2JMOD_CHAMPION_REWARD_QTY = Integer.parseInt(L2JModSettings.getProperty("ChampionRewardItemQty", "1"));

	        /* L2JMOD Wedding system  */
	        L2JMOD_WEDDING_ANNOUNCE = Boolean.parseBoolean(L2JModSettings.getProperty("AnnounceWeddings", "True"));
	        L2JMOD_ALLOW_WEDDING = Boolean.valueOf(L2JModSettings.getProperty("AllowWedding", "False"));
	        L2JMOD_WEDDING_PRICE = Integer.parseInt(L2JModSettings.getProperty("WeddingPrice", "250000000"));
	        L2JMOD_WEDDING_PUNISH_INFIDELITY = Boolean.parseBoolean(L2JModSettings.getProperty("WeddingPunishInfidelity", "True"));
	        L2JMOD_WEDDING_TELEPORT = Boolean.parseBoolean(L2JModSettings.getProperty("WeddingTeleport", "True"));
	        L2JMOD_WEDDING_TELEPORT_PRICE = Integer.parseInt(L2JModSettings.getProperty("WeddingTeleportPrice", "50000"));
	        L2JMOD_WEDDING_TELEPORT_DURATION = Integer.parseInt(L2JModSettings.getProperty("WeddingTeleportDuration", "60"));
	        L2JMOD_WEDDING_SAMESEX = Boolean.parseBoolean(L2JModSettings.getProperty("WeddingAllowSameSex", "False"));
	        L2JMOD_WEDDING_FORMALWEAR = Boolean.parseBoolean(L2JModSettings.getProperty("WeddingFormalWear", "True"));
	        L2JMOD_WEDDING_DIVORCE_COSTS = Integer.parseInt(L2JModSettings.getProperty("WeddingDivorceCosts", "20"));
	        L2JMOD_WEDDING_COLOR_NAME = Boolean.parseBoolean(L2JModSettings.getProperty("ColorWeddingName", "True"));
	        L2JMOD_WEDDING_COLOR_NAMES = Integer.decode("0x" + L2JModSettings.getProperty("WeddingNameColor", "FFFF00"));
	        L2JMOD_WEDDING_COLOR_NAMES_GEY = Integer.decode("0x" + L2JModSettings.getProperty("WeddingNameGeyColor", "FF0000"));
	        L2JMOD_WEDDING_COLOR_NAMES_LIZ = Integer.decode("0x" + L2JModSettings.getProperty("WeddingNameLizColor", "F0F000"));
	        PCB_ENABLE = Boolean.parseBoolean(L2JModSettings.getProperty("PcBangPointEnable", "true"));
		    PCB_MIN_LEVEL = Integer.parseInt(L2JModSettings.getProperty("PcBangPointMinLevel", "20"));
		    PCB_POINT_MIN = Integer.parseInt(L2JModSettings.getProperty("PcBangPointMinCount", "20"));
		    PCB_POINT_MAX = Integer.parseInt(L2JModSettings.getProperty("PcBangPointMaxCount", "1000000"));
		    if(PCB_POINT_MAX < 1)
		    {
		      PCB_POINT_MAX = Integer.MAX_VALUE;
		    }
		    PCB_CHANCE_DUAL_POINT = Integer.parseInt(L2JModSettings.getProperty("PcBangPointDualChance", "20"));
		    PCB_INTERVAL = Integer.parseInt(L2JModSettings.getProperty("PcBangPointTimeStamp", "900"));
		    PCB_ITEMS_ID = Integer.parseInt(L2JModSettings.getProperty("PcBangPointId","65436"));
			OFFLINE_TRADE_ENABLE = Boolean.parseBoolean(L2JModSettings.getProperty("OfflineTradeEnable", "false"));
			OFFLINE_CRAFT_ENABLE = Boolean.parseBoolean(L2JModSettings.getProperty("OfflineCraftEnable", "false"));
			OFFLINE_SET_NAME_COLOR = Boolean.parseBoolean(L2JModSettings.getProperty("OfflineNameColorEnable", "false"));
			OFFLINE_NAME_COLOR = Integer.decode("0x" + L2JModSettings.getProperty("OfflineNameColor", "ff00ff"));
			OFFLINE_LOGOUT = Boolean.parseBoolean(L2JModSettings.getProperty("OfflineLogout", "False"));
			OFFLINE_SLEEP_EFFECT = Boolean.parseBoolean(L2JModSettings.getProperty("OfflineSleepEffect", "True"));
			OFFLINE_RESTORE_OFFLINERS = Boolean.parseBoolean(L2JModSettings.getProperty("RestoreOffliners", "false"));
			OFFLINE_MAX_DAYS = Integer.parseInt(L2JModSettings.getProperty("OfflineMaxDays", "10"));
			OFFLINE_DISCONNECT_FINISHED = Boolean.parseBoolean(L2JModSettings.getProperty("OfflineDisconnectFinished", "true"));
		    OFFLINE_LOGOUT_ITEM_ID = Integer.parseInt(L2JModSettings.getProperty("LogoutItemId", "5283"));
		    OFFLINE_LOGOUT_ITEM_COUNT = Integer.parseInt(L2JModSettings.getProperty("LogoutItemCount", "10"));
	        L2JMOD_CHECK_SKILLS_ON_ENTER = Boolean.parseBoolean(L2JModSettings.getProperty("CheckSkillsOnEnter", "False"));
	        L2JMOD_ALLOWED_SKILLS_LIST = new ArrayList<>();
	        for (String id : L2JModSettings.getProperty("AllowedSkills", "10").split(","))
	        {
	        	L2JMOD_ALLOWED_SKILLS_LIST.add(Integer.parseInt(id.trim()));
	        }
	        L2JMOD_CHECK_HERO_SKILLS = Boolean.valueOf(L2JModSettings.getProperty("CheckHeroSkills", "True"));
	        L2JMOD_CHECK_NOBLE_SKILLS = Boolean.valueOf(L2JModSettings.getProperty("CheckNobleSkills", "True"));
	        L2JMOD_LIST_NO_CHECK_SKILLS = new ArrayList<>();
	        for (String id : L2JModSettings.getProperty("NonCheckSkills", "10000").split(","))
			{
	        	L2JMOD_LIST_NO_CHECK_SKILLS.add(Integer.parseInt(id.trim()));
	        }
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        throw new Error("Failed to Load " + L2JMOD_FILE + " File.");
	    }
	}

	// --------------------------------------------- //
    // -        ELITE CLAN HALL PROPERTIES         - //
    // --------------------------------------------- //
	// ============================================================
	public static int DEVASTATED_DAY;
	public static int DEVASTATED_HOUR;
	public static int DEVASTATED_MINUTES;
	public static int PARTISAN_DAY;
	public static int PARTISAN_HOUR;
	public static int PARTISAN_MINUTES;
	// ============================================================
	public static void loadCHConfig()
	{
		try(InputStream is = new FileInputStream(new File(CH_FILE)))
		{
			Properties clanHallSettings = new Properties();
			clanHallSettings.load(is);

			DEVASTATED_DAY = Integer.valueOf(clanHallSettings.getProperty("DevastatedDay", "1"));
			DEVASTATED_HOUR = Integer.valueOf(clanHallSettings.getProperty("DevastatedHour", "18"));
			DEVASTATED_MINUTES = Integer.valueOf(clanHallSettings.getProperty("DevastatedMinutes", "0"));
			PARTISAN_DAY = Integer.valueOf(clanHallSettings.getProperty("PartisanDay", "5"));
			PARTISAN_HOUR = Integer.valueOf(clanHallSettings.getProperty("PartisanHour", "21"));
			PARTISAN_MINUTES = Integer.valueOf(clanHallSettings.getProperty("PartisanMinutes", "0"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load " + CH_FILE + " File.");
		}
	}

	// --------------------------------------------- //
    // -       SEPULCHERS CONFIG PROPERTIES        - //
    // --------------------------------------------- //
    // ============================================================
	public static int FS_TIME_ATTACK;
	public static int FS_TIME_COOLDOWN;
	public static int FS_TIME_ENTRY;
	public static int FS_TIME_WARMUP;
	public static int FS_PARTY_MEMBER_COUNT;
    // ============================================================

	public static void loadSepulchersConfig()
	{
	    try(InputStream is = new FileInputStream(SEPULCHERS_FILE))
		{
			Properties Sepulchers = new Properties();
			Sepulchers.load(is);

			FS_TIME_ATTACK = Integer.parseInt(Sepulchers.getProperty("TimeOfAttack", "50"));
			FS_TIME_COOLDOWN = Integer.parseInt(Sepulchers.getProperty("TimeOfCoolDown", "5"));
			FS_TIME_ENTRY = Integer.parseInt(Sepulchers.getProperty("TimeOfEntry", "3"));
			FS_TIME_WARMUP = Integer.parseInt(Sepulchers.getProperty("TimeOfWarmUp", "2"));
			FS_PARTY_MEMBER_COUNT = Integer.parseInt(Sepulchers.getProperty("NumberOfNecessaryPartyMembers", "4"));
			if (FS_TIME_ATTACK <= 0)
				FS_TIME_ATTACK = 50;
			if (FS_TIME_COOLDOWN <= 0)
				FS_TIME_COOLDOWN = 5;
			if (FS_TIME_ENTRY <= 0)
				FS_TIME_ENTRY = 3;
			if (FS_TIME_ENTRY <= 0)
				FS_TIME_ENTRY = 3;
			if (FS_TIME_ENTRY <= 0)
				FS_TIME_ENTRY = 3;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load " + SEPULCHERS_FILE + " File.");
		}
	}

	// --------------------------------------------- //
    // -             OLYMPIAD PROPIETERS           - //
    // --------------------------------------------- //
    // ============================================================
	public static int ALT_OLY_START_TIME;
	public static int ALT_OLY_MIN;
	public static long ALT_OLY_CPERIOD;
	public static long ALT_OLY_BATTLE;
	public static long ALT_OLY_BWAIT;
	public static long ALT_OLY_IWAIT;
	public static long ALT_OLY_WPERIOD;
	public static long ALT_OLY_VPERIOD;
	public static int ALT_OLY_CLASSED;
	public static int ALT_OLY_NONCLASSED;
	public static int ALT_OLY_BATTLE_REWARD_ITEM;
	public static int ALT_OLY_CLASSED_RITEM_C;
	public static int ALT_OLY_NONCLASSED_RITEM_C;
	public static int ALT_OLY_COMP_RITEM;
	public static int ALT_OLY_GP_PER_POINT;
	public static int ALT_OLY_MIN_POINT_FOR_EXCH;
	public static int ALT_OLY_HERO_POINTS;
	public static List<Integer> ALT_OLY_RESTRICTED_ITEMS;
	public static boolean ALT_OLY_RESTRICTED_ITEMS_S;
	public static OlympiadPeriod ALT_OLY_PERIOD;
	public static int ALT_OLY_PERIOD_MULTIPLIER;
	public static boolean ALLOW_EVENTS_DURING_OLY;
	public static boolean ALT_OLY_RECHARGE_SKILLS;
	public static int ALT_OLY_ENCHANT_LIMIT;
	public static boolean ALLOW_SKILL_AUGMENTS_IN_OLYM;
	public static boolean OLY_SKILL_PROTECT;
	public static List<Integer> OLY_SKILL_LIST;
	//============================================================

	public static void loadOlympConfig()
	{
	    try(InputStream is = new FileInputStream(new File(OLYMPIAD_FILE)))
		{
			Properties Olym = new Properties();
			Olym.load(is);

			ALT_OLY_START_TIME = Integer.parseInt(Olym.getProperty("AltOlyStartTime", "18"));
			ALT_OLY_MIN = Integer.parseInt(Olym.getProperty("AltOlyMin", "00"));
			ALT_OLY_CPERIOD = Long.parseLong(Olym.getProperty("AltOlyCPeriod", "21600000"));
			ALT_OLY_BATTLE = Long.parseLong(Olym.getProperty("AltOlyBattle", "360000"));
			ALT_OLY_BWAIT = Long.parseLong(Olym.getProperty("AltOlyBWait", "600000"));
			ALT_OLY_IWAIT = Long.parseLong(Olym.getProperty("AltOlyIWait", "300000"));
			ALT_OLY_WPERIOD = Long.parseLong(Olym.getProperty("AltOlyWPeriod", "604800000"));
			ALT_OLY_VPERIOD = Long.parseLong(Olym.getProperty("AltOlyVPeriod", "86400000"));
			ALT_OLY_CLASSED = Integer.parseInt(Olym.getProperty("AltOlyClassedParticipants", "5"));
			ALT_OLY_NONCLASSED = Integer.parseInt(Olym.getProperty("AltOlyNonClassedParticipants", "9"));
			ALT_OLY_BATTLE_REWARD_ITEM = Integer.parseInt(Olym.getProperty("AltOlyBattleRewItem", "6651"));
			ALT_OLY_CLASSED_RITEM_C = Integer.parseInt(Olym.getProperty("AltOlyClassedRewItemCount", "50"));
			ALT_OLY_NONCLASSED_RITEM_C = Integer.parseInt(Olym.getProperty("AltOlyNonClassedRewItemCount", "30"));
			ALT_OLY_COMP_RITEM = Integer.parseInt(Olym.getProperty("AltOlyCompRewItem", "6651"));
			ALT_OLY_GP_PER_POINT = Integer.parseInt(Olym.getProperty("AltOlyGPPerPoint", "1000"));
			ALT_OLY_MIN_POINT_FOR_EXCH = Integer.parseInt(Olym.getProperty("AltOlyMinPointForExchange", "50"));
			ALT_OLY_HERO_POINTS = Integer.parseInt(Olym.getProperty("AltOlyHeroPoints", "300"));
	    	ALT_OLY_RESTRICTED_ITEMS = new ArrayList<>();
	    	for (String id : Olym.getProperty("OlyRestrictedItems","0").split(","))
	    	{
	    		ALT_OLY_RESTRICTED_ITEMS.add(Integer.parseInt(id));
	    	}
	    	ALT_OLY_RESTRICTED_ITEMS_S = Boolean.parseBoolean(Olym.getProperty("AllowOlyGradS", "False"));
			ALT_OLY_PERIOD = OlympiadPeriod.valueOf(Olym.getProperty("AltOlyPeriod", "MONTH"));
			ALT_OLY_PERIOD_MULTIPLIER = Integer.parseInt(Olym.getProperty("AltOlyPeriodMultiplier", "1"));
			ALLOW_EVENTS_DURING_OLY = Boolean.parseBoolean(Olym.getProperty("AllowEventsDuringOly", "False"));
			ALT_OLY_RECHARGE_SKILLS = Boolean.parseBoolean(Olym.getProperty("AltOlyRechargeSkills", "False"));
			ALT_OLY_ENCHANT_LIMIT = Integer.parseInt(Olym.getProperty("OlyMaxEnchant", "-1"));
			ALLOW_SKILL_AUGMENTS_IN_OLYM = Boolean.parseBoolean(Olym.getProperty("AllowSkillAugmentInOlym", "True"));
			OLY_SKILL_PROTECT = Boolean.parseBoolean(Olym.getProperty("OlySkillProtect", "True"));
            OLY_SKILL_LIST = new ArrayList<>();
			for (String id : Olym.getProperty("OllySkillId","0").split(","))
			{
				OLY_SKILL_LIST.add(Integer.parseInt(id));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load " + OLYMPIAD_FILE + " File.");
		}
	}

    // --------------------------------------------- //
    // -          SEVEN SIGNS PROPERTIES           - //
    // --------------------------------------------- //
    // ============================================================
    public static int ALT_FESTIVAL_MIN_PLAYER;
    public static int ALT_MAXIMUM_PLAYER_CONTRIB;
    public static long ALT_FESTIVAL_MANAGER_START;
    public static long ALT_FESTIVAL_LENGTH;
    public static long ALT_FESTIVAL_CYCLE_LENGTH;
    public static long ALT_FESTIVAL_FIRST_SPAWN;
    public static long ALT_FESTIVAL_FIRST_SWARM;
    public static long ALT_FESTIVAL_SECOND_SPAWN;
    public static long ALT_FESTIVAL_SECOND_SWARM;
    public static long ALT_FESTIVAL_CHEST_SPAWN;
	// ============================================================
    public static void loadSevenSignsConfig()
    {
	    try(InputStream is = new FileInputStream(new File(SEVENSIGNS_FILE)))
	    {
	    	Properties SevenSettings = new Properties();
	    	SevenSettings.load(is);

	    	ALT_FESTIVAL_MIN_PLAYER = Integer.parseInt(SevenSettings.getProperty("AltFestivalMinPlayer", "5"));
	    	ALT_MAXIMUM_PLAYER_CONTRIB = Integer.parseInt(SevenSettings.getProperty("AltMaxPlayerContrib", "1000000"));
	    	ALT_FESTIVAL_MANAGER_START = Long.parseLong(SevenSettings.getProperty("AltFestivalManagerStart", "2"));
	    	ALT_FESTIVAL_LENGTH = Long.parseLong(SevenSettings.getProperty("AltFestivalLength", "18"));
	    	ALT_FESTIVAL_CYCLE_LENGTH = Long.parseLong(SevenSettings.getProperty("AltFestivalCycleLength", "38"));
	    	ALT_FESTIVAL_FIRST_SPAWN = Long.parseLong(SevenSettings.getProperty("AltFestivalFirstSpawn", "2"));
	    	ALT_FESTIVAL_FIRST_SWARM = Long.parseLong(SevenSettings.getProperty("AltFestivalFirstSwarm", "5"));
	    	ALT_FESTIVAL_SECOND_SPAWN = Long.parseLong(SevenSettings.getProperty("AltFestivalSecondSpawn", "9"));
	    	ALT_FESTIVAL_SECOND_SWARM = Long.parseLong(SevenSettings.getProperty("AltFestivalSecondSwarm", "12"));
	    	ALT_FESTIVAL_CHEST_SPAWN = Long.parseLong(SevenSettings.getProperty("AltFestivalChestSpawn", "15"));
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	        throw new Error("Failed to Load "+SEVENSIGNS_FILE+" File.");
	    }
    }

    // --------------------------------------------- //
    // -           TVT EVENT PROPERTIES            - //
    // --------------------------------------------- //
    // ============================================================
    public static int HIDE_IMAGEM_ITEM;
    public static int HIDE_REWARD_ITEM;
    public static int HIDE_REWARD_COUNT;
    public static boolean TVT_EVENT_ENABLED;
	public static String[] TVT_EVENT_INTERVAL;
	public static int TVT_EVENT_PARTICIPATION_TIME;
	public static int TVT_EVENT_RUNNING_TIME;
	public static int TVT_EVENT_PARTICIPATION_NPC_ID;
	public static int[] TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static int[] TVT_EVENT_PARTICIPATION_FEE = new int[2];
	public static int TVT_EVENT_MIN_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_MAX_PLAYERS_IN_TEAMS;
	public static byte TVT_EVENT_MIN_LVL;
	public static byte TVT_EVENT_MAX_LVL;
	public static int TVT_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int TVT_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static String TVT_EVENT_TEAM_1_NAME;
	public static int[] TVT_EVENT_TEAM_1_COORDINATES = new int[3];
	public static String TVT_EVENT_TEAM_2_NAME;
	public static int[] TVT_EVENT_TEAM_2_COORDINATES = new int[3];
	public static List<int[]> TVT_EVENT_REWARDS;
	public static boolean TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
	public static boolean TVT_EVENT_SCROLL_ALLOWED;
	public static boolean TVT_EVENT_POTIONS_ALLOWED;
	public static boolean TVT_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> TVT_DOORS_IDS_TO_OPEN;
	public static List<Integer> TVT_DOORS_IDS_TO_CLOSE;
	public static boolean TVT_REWARD_TEAM_TIE;
	public static int TVT_EVENT_EFFECTS_REMOVAL;
    public static Map<Integer, Integer> TVT_EVENT_FIGHTER_BUFFS;
    public static Map<Integer, Integer> TVT_EVENT_MAGE_BUFFS;
    public static String TVT_EVENT_ON_KILL;
	public static boolean TVT_RESTORE_PLAYER_POS;
	public static List<Integer> LIST_TVT_RESTRICTED_ITEMS = new ArrayList<>();
	public static boolean TVT_REWARD_ONLY_KILLERS;
	public static int TVT_EVENT_REWARD_KILL;
	public static boolean TVT_EVENT_ALLOW_PEACE_ATTACK;
	public static boolean TVT_EVENT_ALLOW_FLAG;
	public static boolean TVT_EVENT_RESTORE_CPHPMP;
	// ============================================================

    public static void loadTvTConfig()
    {
	    try(InputStream is = new FileInputStream(new File(EVENT_CONFIG_FILE)))
	    {
	        Properties TvTevent  = new Properties();
	        TvTevent.load(is);

	        HIDE_IMAGEM_ITEM = Integer.parseInt(TvTevent.getProperty("HideImageItem", "7683"));
	        HIDE_REWARD_ITEM = Integer.parseInt(TvTevent.getProperty("HideRewardItem", "2807"));
	        HIDE_REWARD_COUNT = Integer.parseInt(TvTevent.getProperty("HideRewardCount", "50"));

	        TVT_EVENT_ENABLED = Boolean.parseBoolean(TvTevent.getProperty("TvTEventEnabled", "False"));
			TVT_EVENT_INTERVAL = TvTevent.getProperty("TvTEventInterval", "20:00").split(",");
			TVT_EVENT_PARTICIPATION_TIME = Integer.parseInt(TvTevent.getProperty("TvTEventParticipationTime", "3600"));
			TVT_EVENT_RUNNING_TIME = Integer.parseInt(TvTevent.getProperty("TvTEventRunningTime", "1800"));
			TVT_EVENT_PARTICIPATION_NPC_ID = Integer.parseInt(TvTevent.getProperty("TvTEventParticipationNpcId", "0"));
			if (TVT_EVENT_PARTICIPATION_NPC_ID == 0)
			{
				TVT_EVENT_ENABLED = false;
				_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcId");
			}
			else
			{
				String[] propertySplit = TvTevent.getProperty("TvTEventParticipationNpcCoordinates", "0,0,0").split(",");
				if (propertySplit.length < 3)
				{
					TVT_EVENT_ENABLED = false;
					_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcCoordinates");
				}
				else
				{
					TVT_EVENT_REWARDS = new ArrayList<>();
					TVT_DOORS_IDS_TO_OPEN = new ArrayList<>();
					TVT_DOORS_IDS_TO_CLOSE = new ArrayList<>();
					TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
					TVT_EVENT_TEAM_1_COORDINATES = new int[3];
					TVT_EVENT_TEAM_2_COORDINATES = new int[3];
					TVT_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
					TVT_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
					TVT_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
					if (propertySplit.length == 4)
						TVT_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(propertySplit[3]);
					TVT_EVENT_MIN_PLAYERS_IN_TEAMS = Integer.parseInt(TvTevent.getProperty("TvTEventMinPlayersInTeams", "1"));
					TVT_EVENT_MAX_PLAYERS_IN_TEAMS = Integer.parseInt(TvTevent.getProperty("TvTEventMaxPlayersInTeams", "20"));
					TVT_EVENT_MIN_LVL = (byte)Integer.parseInt(TvTevent.getProperty("TvTEventMinPlayerLevel", "1"));
					TVT_EVENT_MAX_LVL = (byte)Integer.parseInt(TvTevent.getProperty("TvTEventMaxPlayerLevel", "80"));
					TVT_EVENT_RESPAWN_TELEPORT_DELAY = Integer.parseInt(TvTevent.getProperty("TvTEventRespawnTeleportDelay", "20"));
					TVT_EVENT_START_LEAVE_TELEPORT_DELAY = Integer.parseInt(TvTevent.getProperty("TvTEventStartLeaveTeleportDelay", "20"));
					TVT_EVENT_EFFECTS_REMOVAL = Integer.parseInt(TvTevent.getProperty("TvTEventEffectsRemoval", "0"));
					TVT_RESTORE_PLAYER_POS = Boolean.parseBoolean(TvTevent.getProperty("TvTRestorePlayerOldPosition", "False"));
					String[] split = TvTevent.getProperty("TvTRestrictedItems","0").split(",");
					LIST_TVT_RESTRICTED_ITEMS = new ArrayList<>();
					for (String id : split)
					{
						LIST_TVT_RESTRICTED_ITEMS.add(Integer.parseInt(id));
					}
					TVT_REWARD_ONLY_KILLERS = Boolean.parseBoolean(TvTevent.getProperty("TvTRewardOnlyKillers", "False"));
					TVT_EVENT_ALLOW_PEACE_ATTACK = Boolean.parseBoolean(TvTevent.getProperty("TvTAllowPeaceAttack", "True"));
					TVT_EVENT_ALLOW_FLAG = Boolean.parseBoolean(TvTevent.getProperty("TvTAllowFlag", "True"));
					TVT_EVENT_RESTORE_CPHPMP = Boolean.parseBoolean(TvTevent.getProperty("TvTRestoreCPHPMP", "False"));
					TVT_EVENT_TEAM_1_NAME = TvTevent.getProperty("TvTEventTeam1Name", "Team1");
					propertySplit = TvTevent.getProperty("TvTEventTeam1Coordinates", "0,0,0").split(",");
					if (propertySplit.length < 3)
					{
						TVT_EVENT_ENABLED = false;
						_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam1Coordinates");
					}
					else
					{
						TVT_EVENT_TEAM_1_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
						TVT_EVENT_TEAM_1_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
						TVT_EVENT_TEAM_1_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
						TVT_EVENT_TEAM_2_NAME = TvTevent.getProperty("TvTEventTeam2Name", "Team2");
						propertySplit = TvTevent.getProperty("TvTEventTeam2Coordinates", "0,0,0").split(",");
						if (propertySplit.length < 3)
						{
							TVT_EVENT_ENABLED= false;
							_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam2Coordinates");
						}
						else
						{
							TVT_EVENT_TEAM_2_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
							TVT_EVENT_TEAM_2_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
							TVT_EVENT_TEAM_2_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
							propertySplit = TvTevent.getProperty("TvTEventParticipationFee", "0,0").split(",");
							try
							{
								TVT_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(propertySplit[0]);
								TVT_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(propertySplit[1]);
							}
							catch (NumberFormatException nfe)
							{
								if (propertySplit.length > 0)
									_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationFee");
							}
							propertySplit = TvTevent.getProperty("TvTEventReward", "57,100000").split(";");
							for (String reward : propertySplit)
							{
								String[] rewardSplit = reward.split(",");
								if (rewardSplit.length != 2)
									_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward \"", reward, "\""));
								else
								{
									try
									{
										TVT_EVENT_REWARDS.add(new int[]{Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1])});
									}
									catch (NumberFormatException nfe)
									{
										if (!reward.isEmpty())
											_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward \"", reward, "\""));
									}
								}
							}
							TVT_EVENT_ON_KILL = TvTevent.getProperty("TvTEventOnKill", "pmteam");
							TVT_EVENT_REWARD_KILL = Integer.parseInt(TvTevent.getProperty("TvTEventRewardKill", "2"));
							TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = Boolean.parseBoolean(TvTevent.getProperty("TvTEventTargetTeamMembersAllowed", "True"));
							TVT_EVENT_SCROLL_ALLOWED = Boolean.parseBoolean(TvTevent.getProperty("TvTEventScrollsAllowed", "False"));
							TVT_EVENT_POTIONS_ALLOWED = Boolean.parseBoolean(TvTevent.getProperty("TvTEventPotionsAllowed", "False"));
							TVT_EVENT_SUMMON_BY_ITEM_ALLOWED = Boolean.parseBoolean(TvTevent.getProperty("TvTEventSummonByItemAllowed", "False"));
							TVT_REWARD_TEAM_TIE = Boolean.parseBoolean(TvTevent.getProperty("TvTRewardTeamTie", "False"));
							propertySplit = TvTevent.getProperty("TvTDoorsToOpen", "").split(";");
							for (String door : propertySplit)
							{
								try
								{
									TVT_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
								}
								catch (NumberFormatException nfe)
								{
									if (!door.isEmpty())
										_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToOpen \"", door, "\""));
								}
							}

							propertySplit = TvTevent.getProperty("TvTDoorsToClose", "").split(";");
							for (String door : propertySplit)
							{
								try
								{
									TVT_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
								}
								catch (NumberFormatException nfe)
								{
									if (!door.isEmpty())
										_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToClose \"", door, "\""));
								}
							}

							propertySplit = TvTevent.getProperty("TvTEventFighterBuffs", "").split(";");
							if (!propertySplit[0].isEmpty())
							{
								TVT_EVENT_FIGHTER_BUFFS = new HashMap<>();
								for (String skill : propertySplit)
								{
									String[] skillSplit = skill.split(",");
									if (skillSplit.length != 2)
										_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs \"", skill, "\""));
									else
									{
										try
										{
											TVT_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
										}
										catch (NumberFormatException nfe)
										{
											if (!skill.isEmpty())
												_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs \"", skill, "\""));
										}
									}
								}
							}

							propertySplit = TvTevent.getProperty("TvTEventMageBuffs", "").split(";");
							if (!propertySplit[0].isEmpty())
							{
								TVT_EVENT_MAGE_BUFFS = new HashMap<>();
								for (String skill : propertySplit)
								{
									String[] skillSplit = skill.split(",");
									if (skillSplit.length != 2)
										_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs \"", skill, "\""));
									else
									{
										try
										{
											TVT_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
										}
										catch (NumberFormatException nfe)
										{
											if (!skill.isEmpty())
												_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs \"", skill, "\""));
										}
									}
								}
							}
						}
					}
				}
			}
        }
        catch (Exception e)
        {
                e.printStackTrace();
                throw new Error("Failed to Load " + EVENT_CONFIG_FILE + " File.");
        }
    }

    // --------------------------------------------- //
    // -       ALTSETTINGS PROPERTIES              - //
    // --------------------------------------------- //
    // =========================================================
    public static boolean AUTO_LOOT;
    public static List<Integer> NO_AUTO_LOOT_LIST = new ArrayList<>();
    public static boolean AUTO_LOOT_HERBS;
    public static boolean AUTO_LOOT_RAIDS;
    public static int ALT_PARTY_RANGE;
    public static int ALT_PARTY_RANGE2;
    public static double ALT_WEIGHT_LIMIT;
    public static boolean ENABLE_FALLING_DAMAGE;
    public static boolean ALT_GAME_DELEVEL;
    public static boolean ALT_GAME_VIEWNPC;
    public static boolean ALT_GAME_MOB_ATTACK_AI;
    public static boolean ALT_MOB_AGRO_IN_PEACEZONE;
    public static boolean ALT_GAME_FREIGHTS;
    public static int ALT_GAME_FREIGHT_PRICE;
    public static float ALT_GAME_EXPONENT_XP;
    public static float ALT_GAME_EXPONENT_SP;
    public static boolean ALT_GAME_TIREDNESS;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_GK;
    public static boolean ALT_GAME_FLAGED_PLAYER_CAN_USE_GK;
    public static boolean ALT_GAME_COMBAT_PLAYER_CAN_USE_GK;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_TELEPORT;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_TRADE;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE;
    public static boolean ALT_GAME_FREE_TELEPORT;
    public static boolean ALT_RECOMMEND;
    public static boolean IS_CRAFTING_ENABLED;
    public static int DWARF_RECIPE_LIMIT;
    public static int COMMON_RECIPE_LIMIT;
    public static boolean ALT_GAME_CREATION;
    public static double ALT_GAME_CREATION_SPEED;
    public static double ALT_GAME_CREATION_XP_RATE;
    public static double ALT_GAME_CREATION_SP_RATE;
    public static boolean ALT_BLACKSMITH_USE_RECIPES;
    public static boolean ALLOW_CLASS_MASTERS;
	public static boolean SP_BOOK_NEEDED;
	public static boolean ES_SP_BOOK_NEEDED;
	public static boolean AUTO_LEARN_SKILLS;
	public static boolean ALT_GAME_SKILL_LEARN;
	public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
    public static boolean ALT_SUB_WITHOUT_FATES;
    public static int ALT_MAX_SUBCLASS;
    public static byte BUFFS_MAX_AMOUNT;
    public static byte DEBUFFS_MAX_AMOUNT;
	public static boolean ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE;
	public static boolean ALLOW_MANOR;
    public static int ALT_MANOR_REFRESH_TIME;
    public static int ALT_MANOR_REFRESH_MIN;
    public static int ALT_MANOR_APPROVE_TIME;
    public static int ALT_MANOR_APPROVE_MIN;
    public static int ALT_MANOR_MAINTENANCE_PERIOD;
    public static boolean ALT_MANOR_SAVE_ALL_ACTIONS;
    public static int ALT_MANOR_SAVE_PERIOD_RATE;
    public static int ALT_LOTTERY_PRIZE;
    public static int ALT_LOTTERY_TICKET_PRICE;
    public static float ALT_LOTTERY_5_NUMBER_RATE;
    public static float ALT_LOTTERY_4_NUMBER_RATE;
    public static float ALT_LOTTERY_3_NUMBER_RATE;
    public static int ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
    public static boolean ALT_DEV_NO_QUESTS;
    public static boolean ALT_DEV_NO_TUTORIAL;
    public static boolean ALT_DEV_NO_SPAWNS;
	public static boolean ALT_DEV_NO_SCRIPT;
	public static boolean ALT_DEV_NO_RB;
	public static boolean ALT_DEV_NO_AI;
    public static int RIFT_MIN_PARTY_SIZE;
    public static int RIFT_MAX_JUMPS;
    public static int RIFT_SPAWN_DELAY;
    public static int RIFT_AUTO_JUMPS_TIME_MIN;
    public static int RIFT_AUTO_JUMPS_TIME_MAX;
    public static float RIFT_BOSS_ROOM_TIME_MUTIPLY;
    public static int RIFT_ENTER_COST_RECRUIT;
    public static int RIFT_ENTER_COST_SOLDIER;
    public static int RIFT_ENTER_COST_OFFICER;
    public static int RIFT_ENTER_COST_CAPTAIN;
    public static int RIFT_ENTER_COST_COMMANDER;
    public static int RIFT_ENTER_COST_HERO;
    public static int RAID_RANKING_1ST;
	public static int RAID_RANKING_2ND;
	public static int RAID_RANKING_3RD;
	public static int RAID_RANKING_4TH;
	public static int RAID_RANKING_5TH;
	public static int RAID_RANKING_6TH;
	public static int RAID_RANKING_7TH;
	public static int RAID_RANKING_8TH;
	public static int RAID_RANKING_9TH;
	public static int RAID_RANKING_10TH;
	public static int RAID_RANKING_UP_TO_50TH;
	public static int RAID_RANKING_UP_TO_100TH;
	public static boolean DONT_DESTROY_SS;
	public static boolean ALLOW_RND_SPAWN;
    // =========================================================
    public static void loadAltSettingsConfig()
	{
	    try(InputStream is = new FileInputStream(new File(ALTSETTINGS_FILE)))
	    {
	        Properties AltSettings  = new Properties();
	        AltSettings.load(is);

	        AUTO_LOOT = AltSettings.getProperty("AutoLoot").equalsIgnoreCase("True");
	        String[] noAutoLoot = AltSettings.getProperty("NoAutoLoot","0").split(",");
            if (noAutoLoot.length > 0)
            {
                for (String id : noAutoLoot)
                {
                    try
                    {
                        int npcId = Integer.valueOf(id);
                        if (npcId != 0) NO_AUTO_LOOT_LIST.add(npcId);
                    }
                    catch (NumberFormatException nfe) { }
                }
            }
            AUTO_LOOT_HERBS = AltSettings.getProperty("AutoLootHerbs").equalsIgnoreCase("True");
			AUTO_LOOT_RAIDS = Boolean.parseBoolean(AltSettings.getProperty("AutoLootRaid", "False"));
	        ALT_PARTY_RANGE = Integer.parseInt(AltSettings.getProperty("AltPartyRange", "1600"));
	        ALT_PARTY_RANGE2 = Integer.parseInt(AltSettings.getProperty("AltPartyRange2", "1400"));
	        ALT_WEIGHT_LIMIT = Double.parseDouble(AltSettings.getProperty("AltWeightLimit", "1"));
	        String str = AltSettings.getProperty("EnableFallingDamage", "auto");
	        ENABLE_FALLING_DAMAGE = "auto".equalsIgnoreCase(str) ? GEODATA > 0 : Boolean.parseBoolean(str);
	        ALT_GAME_DELEVEL = Boolean.parseBoolean(AltSettings.getProperty("Delevel", "True"));
	        ALT_GAME_MOB_ATTACK_AI = Boolean.parseBoolean(AltSettings.getProperty("AltGameMobAttackAI", "False"));
	        ALT_GAME_VIEWNPC = Boolean.parseBoolean(AltSettings.getProperty("AltGameViewNpc", "False"));
	        ALT_MOB_AGRO_IN_PEACEZONE = Boolean.parseBoolean(AltSettings.getProperty("AltMobAgroInPeaceZone", "True"));
	        ALT_GAME_FREIGHTS = Boolean.parseBoolean(AltSettings.getProperty("AltGameFreights", "False"));
	        ALT_GAME_FREIGHT_PRICE = Integer.parseInt(AltSettings.getProperty("AltGameFreightPrice", "1000"));
	        ALT_GAME_EXPONENT_XP = Float.parseFloat(AltSettings.getProperty("AltGameExponentXp", "0."));
	        ALT_GAME_EXPONENT_SP = Float.parseFloat(AltSettings.getProperty("AltGameExponentSp", "0."));
	        ALT_GAME_TIREDNESS = Boolean.parseBoolean(AltSettings.getProperty("AltGameTiredness", "False"));
	        ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Boolean.valueOf(AltSettings.getProperty("AltKarmaPlayerCanBeKilledInPeaceZone", "False"));
	        ALT_GAME_KARMA_PLAYER_CAN_SHOP = Boolean.valueOf(AltSettings.getProperty("AltKarmaPlayerCanShop", "True"));
	        ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Boolean.valueOf(AltSettings.getProperty("AltKarmaPlayerCanTeleport", "True"));
	        ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Boolean.valueOf(AltSettings.getProperty("AltKarmaPlayerCanUseGK", "False"));
	        ALT_GAME_FLAGED_PLAYER_CAN_USE_GK = Boolean.parseBoolean(AltSettings.getProperty("AltFlaggedPlayerCanUseGK", "True"));
            ALT_GAME_COMBAT_PLAYER_CAN_USE_GK = Boolean.parseBoolean(AltSettings.getProperty("AltCombatPlayerCanUseGK", "True"));
            ALT_GAME_KARMA_PLAYER_CAN_TRADE = Boolean.valueOf(AltSettings.getProperty("AltKarmaPlayerCanTrade", "True"));
	        ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Boolean.valueOf(AltSettings.getProperty("AltKarmaPlayerCanUseWareHouse", "True"));
	        ALT_GAME_FREE_TELEPORT = Boolean.parseBoolean(AltSettings.getProperty("AltFreeTeleporting", "False"));
	        ALT_RECOMMEND = Boolean.parseBoolean(AltSettings.getProperty("AltRecommend", "False"));
	        IS_CRAFTING_ENABLED = Boolean.parseBoolean(AltSettings.getProperty("CraftingEnabled", "True"));
	        DWARF_RECIPE_LIMIT = Integer.parseInt(AltSettings.getProperty("DwarfRecipeLimit","50"));
	        COMMON_RECIPE_LIMIT = Integer.parseInt(AltSettings.getProperty("CommonRecipeLimit","50"));
	        ALT_GAME_CREATION = Boolean.parseBoolean(AltSettings.getProperty("AltGameCreation", "False"));
	        ALT_GAME_CREATION_SPEED = Double.parseDouble(AltSettings.getProperty("AltGameCreationSpeed", "1"));
	        ALT_GAME_CREATION_XP_RATE = Double.parseDouble(AltSettings.getProperty("AltGameCreationRateXp", "1"));
	        ALT_GAME_CREATION_SP_RATE = Double.parseDouble(AltSettings.getProperty("AltGameCreationRateSp", "1"));
	        ALT_BLACKSMITH_USE_RECIPES = Boolean.parseBoolean(AltSettings.getProperty("AltBlacksmithUseRecipes", "Frue"));
	        ALLOW_CLASS_MASTERS = Boolean.valueOf(AltSettings.getProperty("AllowClassMasters", "False"));
		    ALT_GAME_SKILL_LEARN = Boolean.parseBoolean(AltSettings.getProperty("AltGameSkillLearn", "False"));
		    SP_BOOK_NEEDED = Boolean.parseBoolean(AltSettings.getProperty("SpBookNeeded", "true"));
		    ES_SP_BOOK_NEEDED = Boolean.parseBoolean(AltSettings.getProperty("EnchantSkillSpBookNeeded","True"));
		    AUTO_LEARN_SKILLS = Boolean.parseBoolean(AltSettings.getProperty("AutoLearnSkills", "False"));
			ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Boolean.parseBoolean(AltSettings.getProperty("AltSubClassWithoutQuests", "False"));
	        ALT_SUB_WITHOUT_FATES = Boolean.parseBoolean(AltSettings.getProperty("AltSubWithoutFates", "False"));
	        ALT_MAX_SUBCLASS = Integer.parseInt(AltSettings.getProperty("AltMaxSubNumber", "3"));
	        BUFFS_MAX_AMOUNT = Byte.parseByte(AltSettings.getProperty("MaxBuffAmount","24"));
	        DEBUFFS_MAX_AMOUNT = Byte.parseByte(AltSettings.getProperty("MaxDebuffAmount","6"));
	        ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE = Boolean.parseBoolean(AltSettings.getProperty("AltNewCharAlwaysIsNewbie", "False"));
	        ALLOW_MANOR = Boolean.parseBoolean(AltSettings.getProperty("AllowManor", "False"));
	        ALT_MANOR_REFRESH_TIME = Integer.parseInt(AltSettings.getProperty("AltManorRefreshTime","20"));
	        ALT_MANOR_REFRESH_MIN  = Integer.parseInt(AltSettings.getProperty("AltManorRefreshMin","00"));
	        ALT_MANOR_APPROVE_TIME = Integer.parseInt(AltSettings.getProperty("AltManorApproveTime","6"));
	        ALT_MANOR_APPROVE_MIN  = Integer.parseInt(AltSettings.getProperty("AltManorApproveMin","00"));
	        ALT_MANOR_MAINTENANCE_PERIOD = Integer.parseInt(AltSettings.getProperty("AltManorMaintenancePeriod","360000"));
	        ALT_MANOR_SAVE_ALL_ACTIONS = Boolean.parseBoolean(AltSettings.getProperty("AltManorSaveAllActions","False"));
	        ALT_MANOR_SAVE_PERIOD_RATE = Integer.parseInt(AltSettings.getProperty("AltManorSavePeriodRate","2"));
	        ALT_LOTTERY_PRIZE = Integer.parseInt(AltSettings.getProperty("AltLotteryPrize","50000"));
	        ALT_LOTTERY_TICKET_PRICE = Integer.parseInt(AltSettings.getProperty("AltLotteryTicketPrice","2000"));
	        ALT_LOTTERY_5_NUMBER_RATE = Float.parseFloat(AltSettings.getProperty("AltLottery5NumberRate","0.6"));
	        ALT_LOTTERY_4_NUMBER_RATE = Float.parseFloat(AltSettings.getProperty("AltLottery4NumberRate","0.2"));
	        ALT_LOTTERY_3_NUMBER_RATE = Float.parseFloat(AltSettings.getProperty("AltLottery3NumberRate","0.2"));
	        ALT_LOTTERY_2_AND_1_NUMBER_PRIZE = Integer.parseInt(AltSettings.getProperty("AltLottery2and1NumberPrize","200"));
	        ALT_DEV_NO_QUESTS = Boolean.parseBoolean(AltSettings.getProperty("AltDevNoQuests", "False"));
	        ALT_DEV_NO_TUTORIAL = Boolean.parseBoolean(AltSettings.getProperty("AltDevNoTutorial", "False"));
	        ALT_DEV_NO_SPAWNS = Boolean.parseBoolean(AltSettings.getProperty("AltDevNoSpawns", "False"));
			ALT_DEV_NO_SCRIPT = Boolean.parseBoolean(AltSettings.getProperty("AltDevNoScript", "False"));
			ALT_DEV_NO_AI = Boolean.parseBoolean(AltSettings.getProperty("AltDevNoAI", "False"));
			ALT_DEV_NO_RB = Boolean.parseBoolean(AltSettings.getProperty("AltDevNoRB", "False"));
	        RIFT_MIN_PARTY_SIZE = Integer.parseInt(AltSettings.getProperty("RiftMinPartySize", "5"));
	        RIFT_MAX_JUMPS = Integer.parseInt(AltSettings.getProperty("MaxRiftJumps", "4"));
			RIFT_SPAWN_DELAY = Integer.parseInt(AltSettings.getProperty("RiftSpawnDelay", "10000"));
	        RIFT_AUTO_JUMPS_TIME_MIN = Integer.parseInt(AltSettings.getProperty("AutoJumpsDelayMin", "480"));
	        RIFT_AUTO_JUMPS_TIME_MAX = Integer.parseInt(AltSettings.getProperty("AutoJumpsDelayMax", "600"));
	        RIFT_BOSS_ROOM_TIME_MUTIPLY = Float.parseFloat(AltSettings.getProperty("BossRoomTimeMultiply", "1.5"));
	        RIFT_ENTER_COST_RECRUIT = Integer.parseInt(AltSettings.getProperty("RecruitCost", "18"));
	        RIFT_ENTER_COST_SOLDIER = Integer.parseInt(AltSettings.getProperty("SoldierCost", "21"));
	        RIFT_ENTER_COST_OFFICER = Integer.parseInt(AltSettings.getProperty("OfficerCost", "24"));
	        RIFT_ENTER_COST_CAPTAIN = Integer.parseInt(AltSettings.getProperty("CaptainCost", "27"));
	        RIFT_ENTER_COST_COMMANDER = Integer.parseInt(AltSettings.getProperty("CommanderCost", "30"));
	        RIFT_ENTER_COST_HERO = Integer.parseInt(AltSettings.getProperty("HeroCost", "33"));
			RAID_RANKING_1ST = Integer.parseInt(AltSettings.getProperty("1stRaidRankingPoints", "1250"));
			RAID_RANKING_2ND = Integer.parseInt(AltSettings.getProperty("2ndRaidRankingPoints", "900"));
			RAID_RANKING_3RD = Integer.parseInt(AltSettings.getProperty("3rdRaidRankingPoints", "700"));
			RAID_RANKING_4TH = Integer.parseInt(AltSettings.getProperty("4thRaidRankingPoints", "600"));
			RAID_RANKING_5TH = Integer.parseInt(AltSettings.getProperty("5thRaidRankingPoints", "450"));
			RAID_RANKING_6TH = Integer.parseInt(AltSettings.getProperty("6thRaidRankingPoints", "350"));
			RAID_RANKING_7TH = Integer.parseInt(AltSettings.getProperty("7thRaidRankingPoints", "300"));
			RAID_RANKING_8TH = Integer.parseInt(AltSettings.getProperty("8thRaidRankingPoints", "200"));
			RAID_RANKING_9TH = Integer.parseInt(AltSettings.getProperty("9thRaidRankingPoints", "150"));
			RAID_RANKING_10TH = Integer.parseInt(AltSettings.getProperty("10thRaidRankingPoints", "100"));
			RAID_RANKING_UP_TO_50TH = Integer.parseInt(AltSettings.getProperty("UpTo50thRaidRankingPoints", "25"));
			RAID_RANKING_UP_TO_100TH = Integer.parseInt(AltSettings.getProperty("UpTo100thRaidRankingPoints", "12"));
			DONT_DESTROY_SS = Boolean.parseBoolean(AltSettings.getProperty("DontDestroySS", "false"));
			ALLOW_RND_SPAWN = Boolean.parseBoolean(AltSettings.getProperty("EnableRndSpawns", "True"));
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	throw new Error("Failed to Load " + ALTSETTINGS_FILE + " File.");
	    }
	}

    // --------------------------------------------- //
    // -          BOSS PROPERTIES                  - //
    // --------------------------------------------- //
    // =========================================================
	public static int RBLOCKRAGE;
	public static Map<Integer, Integer> RBS_SPECIFIC_LOCK_RAGE;
	public static boolean FORCE_UPDATE_RAIDBOSS_ON_DB;
	public static boolean ALLOW_DIRECT_TP_TO_BOSS_ROOM;
	public static boolean EFFECT_KILLED_GRAND_BOSS;

	public static int ANTHARAS_DESPAWN_TIME;
	public static int ANTHARAS_RESP_FIRST;
	public static int ANTHARAS_RESP_SECOND;
	public static int ANTHARAS_WAIT_TIME;

	public static int BAIUM_SLEEP;
	public static int BAIUM_RESP_FIRST;
	public static int BAIUM_RESP_SECOND;

	public static int CORE_RESP_MINION;
	public static int CORE_RESP_FIRST;
	public static int CORE_RESP_SECOND;
	public static int CORE_RING_CHANCE;

	public static int ZAKEN_RESP_FIRST;
	public static int ZAKEN_RESP_SECOND;
	public static int ZAKEN_EARRING_CHANCE;

	public static int ORFEN_RESP_FIRST;
	public static int ORFEN_RESP_SECOND;
	public static int ORFEN_EARRING_CHANCE;

	public static int VALAKAS_RESP_FIRST;
	public static int VALAKAS_RESP_SECOND;
	public static int VALAKAS_WAIT_TIME;
	public static int VALAKAS_DESPAWN_TIME;

	public static int QA_RESP_NURSE;
	public static int QA_RESP_ROYAL;
	public static int QA_RESP_FIRST;
	public static int QA_RESP_SECOND;
	public static int QA_RING_CHANCE;

	public static int FRINTEZZA_RESP_FIRST;
	public static int FRINTEZZA_RESP_SECOND;
	public static boolean BYPASS_FRINTEZZA_PARTIES_CHECK;
	public static int FRINTEZZA_MIN_PARTIES;
	public static int FRINTEZZA_MAX_PARTIES;

	public static int HPH_FIXINTERVALOFHALTER;
	public static int HPH_RANDOMINTERVALOFHALTER;
	public static int HPH_APPTIMEOFHALTER;
	public static int HPH_ACTIVITYTIMEOFHALTER;
	public static int HPH_FIGHTTIMEOFHALTER;
	public static int HPH_CALLROYALGUARDHELPERCOUNT;
	public static int HPH_CALLROYALGUARDHELPERINTERVAL;
	public static int HPH_INTERVALOFDOOROFALTER;
	public static int HPH_TIMEOFLOCKUPDOOROFALTAR;
	// ============================================================

	public static void loadBossConfig()
	{
		try(InputStream is = new FileInputStream(new File(BOSS_FILE)))
		{
			Properties bossSettings = new Properties();
			bossSettings.load(is);

			RBLOCKRAGE = Integer.parseInt(bossSettings.getProperty("RBlockRage", "5000"));
			if(RBLOCKRAGE > 0 && RBLOCKRAGE < 100)
			{
				_log.info("ATTENTION: RBlockRage, if enabled (>0), must be >=100");
				_log.info("	-- RBlockRage setted to 100 by default");
				RBLOCKRAGE = 100;
			}
			RBS_SPECIFIC_LOCK_RAGE = new HashMap<>();
			String RBS_SPECIFIC_LOCK_RAGE_String = bossSettings.getProperty("RaidBossesSpecificLockRage","");
			if(!RBS_SPECIFIC_LOCK_RAGE_String.equals(""))
			{
				String[] locked_bosses = RBS_SPECIFIC_LOCK_RAGE_String.split(";");
				for(String actual_boss_rage:locked_bosses)
				{
					String[] boss_rage = actual_boss_rage.split(",");
					int specific_rage = Integer.parseInt(boss_rage[1]);
					if(specific_rage>0 && specific_rage<100)
					{
						_log.info("ATTENTION: RaidBossesSpecificLockRage Value for boss "+boss_rage[0]+", if enabled (>0), must be >=100");
						_log.info("	-- RaidBossesSpecificLockRage Value for boss "+boss_rage[0]+" setted to 100 by default");
						specific_rage = 100;
					}
					RBS_SPECIFIC_LOCK_RAGE.put(Integer.parseInt(boss_rage[0]), specific_rage);
				}
			}
			FORCE_UPDATE_RAIDBOSS_ON_DB = Boolean.parseBoolean(bossSettings.getProperty("ForceUpdateRaidbossOnDb", "False"));
			ALLOW_DIRECT_TP_TO_BOSS_ROOM = Boolean.valueOf(bossSettings.getProperty("AllowDirectTeleportToBossRoom", "False"));
			EFFECT_KILLED_GRAND_BOSS = Boolean.valueOf(bossSettings.getProperty("EffectRaidKill", "False"));

			//Antharas
			ANTHARAS_DESPAWN_TIME = Integer.parseInt(bossSettings.getProperty("AntharasDespawnTime", "240"));
			ANTHARAS_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("AntharasRespFirst", "192"));
			ANTHARAS_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("AntharasRespSecond", "145"));
			ANTHARAS_WAIT_TIME = Integer.parseInt(bossSettings.getProperty("AntharasWaitTime", "30"));
			//============================================================
			//Baium
			BAIUM_SLEEP = Integer.parseInt(bossSettings.getProperty("BaiumSleep", "1800"));
			BAIUM_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("BaiumRespFirst", "121"));
			BAIUM_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("BaiumRespSecond", "8"));
			//============================================================
			//Core
			CORE_RESP_MINION = Integer.parseInt(bossSettings.getProperty("CoreRespMinion", "60"));
			CORE_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("CoreRespFirst", "37"));
			CORE_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("CoreRespSecond", "42"));
			CORE_RING_CHANCE = Integer.parseInt(bossSettings.getProperty("CoreRingChance", "0"));
			//============================================================
			//Queen Ant
			QA_RESP_NURSE = Integer.parseInt(bossSettings.getProperty("QueenAntRespNurse", "60"));
			QA_RESP_ROYAL = Integer.parseInt(bossSettings.getProperty("QueenAntRespRoyal", "120"));
			QA_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("QueenAntRespFirst", "19"));
			QA_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("QueenAntRespSecond", "35"));
			QA_RING_CHANCE = Integer.parseInt(bossSettings.getProperty("QARingChance", "0"));
			//============================================================
			//ZAKEN
			ZAKEN_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("ZakenRespFirst", "60"));
			ZAKEN_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("ZakenRespSecond", "8"));
			ZAKEN_EARRING_CHANCE = Integer.parseInt(bossSettings.getProperty("ZakenEarringChance", "0"));
			//============================================================
			//ORFEN
			ORFEN_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("OrfenRespFirst", "20"));
			ORFEN_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("OrfenRespSecond", "8"));
			ORFEN_EARRING_CHANCE = Integer.parseInt(bossSettings.getProperty("OrfenEarringChance", "0"));
			//============================================================
			//VALAKAS
			VALAKAS_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("ValakasRespFirst", "192"));
			VALAKAS_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("ValakasRespSecond", "44"));
			VALAKAS_WAIT_TIME = Integer.parseInt(bossSettings.getProperty("ValakasWaitTime", "30"));
			VALAKAS_DESPAWN_TIME = Integer.parseInt(bossSettings.getProperty("ValakasDespawnTime", "15"));
			//============================================================
			//FRINTEZZA
			FRINTEZZA_RESP_FIRST = Integer.parseInt(bossSettings.getProperty("FrintezzaRespFirst", "48"));
			FRINTEZZA_RESP_SECOND = Integer.parseInt(bossSettings.getProperty("FrintezzaRespSecond", "8"));
			BYPASS_FRINTEZZA_PARTIES_CHECK = Boolean.valueOf(bossSettings.getProperty("BypassPartiesCheck", "false"));
			FRINTEZZA_MIN_PARTIES = Integer.parseInt(bossSettings.getProperty("FrintezzaMinParties", "4"));
			FRINTEZZA_MAX_PARTIES = Integer.parseInt(bossSettings.getProperty("FrintezzaMaxParties", "5"));
			//============================================================
			//High Priestess van Halter
			HPH_FIXINTERVALOFHALTER = Integer.parseInt(bossSettings.getProperty("FixIntervalOfHalter", "172800"));
			if(HPH_FIXINTERVALOFHALTER < 300 || HPH_FIXINTERVALOFHALTER > 864000)
			{
				HPH_FIXINTERVALOFHALTER = 172800;
			}
			HPH_FIXINTERVALOFHALTER *= 6000;

			HPH_RANDOMINTERVALOFHALTER = Integer.parseInt(bossSettings.getProperty("RandomIntervalOfHalter", "86400"));
			if(HPH_RANDOMINTERVALOFHALTER < 300 || HPH_RANDOMINTERVALOFHALTER > 864000)
			{
				HPH_RANDOMINTERVALOFHALTER = 86400;
			}
			HPH_RANDOMINTERVALOFHALTER *= 6000;

			HPH_APPTIMEOFHALTER = Integer.parseInt(bossSettings.getProperty("AppTimeOfHalter", "20"));
			if(HPH_APPTIMEOFHALTER < 5 || HPH_APPTIMEOFHALTER > 60)
			{
				HPH_APPTIMEOFHALTER = 20;
			}
			HPH_APPTIMEOFHALTER *= 6000;

			HPH_ACTIVITYTIMEOFHALTER = Integer.parseInt(bossSettings.getProperty("ActivityTimeOfHalter", "21600"));
			if(HPH_ACTIVITYTIMEOFHALTER < 7200 || HPH_ACTIVITYTIMEOFHALTER > 86400)
			{
				HPH_ACTIVITYTIMEOFHALTER = 21600;
			}
			HPH_ACTIVITYTIMEOFHALTER *= 1000;

			HPH_FIGHTTIMEOFHALTER = Integer.parseInt(bossSettings.getProperty("FightTimeOfHalter", "7200"));
			if(HPH_FIGHTTIMEOFHALTER < 7200 || HPH_FIGHTTIMEOFHALTER > 21600)
			{
				HPH_FIGHTTIMEOFHALTER = 7200;
			}
			HPH_FIGHTTIMEOFHALTER *= 6000;

			HPH_CALLROYALGUARDHELPERCOUNT = Integer.parseInt(bossSettings.getProperty("CallRoyalGuardHelperCount", "6"));
			if(HPH_CALLROYALGUARDHELPERCOUNT < 1 || HPH_CALLROYALGUARDHELPERCOUNT > 6)
			{
				HPH_CALLROYALGUARDHELPERCOUNT = 6;
			}

			HPH_CALLROYALGUARDHELPERINTERVAL = Integer.parseInt(bossSettings.getProperty("CallRoyalGuardHelperInterval", "10"));
			if(HPH_CALLROYALGUARDHELPERINTERVAL < 1 || HPH_CALLROYALGUARDHELPERINTERVAL > 60)
			{
				HPH_CALLROYALGUARDHELPERINTERVAL = 10;
			}
			HPH_CALLROYALGUARDHELPERINTERVAL *= 6000;

			HPH_INTERVALOFDOOROFALTER = Integer.parseInt(bossSettings.getProperty("IntervalOfDoorOfAlter", "5400"));
			if(HPH_INTERVALOFDOOROFALTER < 60 || HPH_INTERVALOFDOOROFALTER > 5400)
			{
				HPH_INTERVALOFDOOROFALTER = 5400;
			}
			HPH_INTERVALOFDOOROFALTER *= 6000;

			HPH_TIMEOFLOCKUPDOOROFALTAR = Integer.parseInt(bossSettings.getProperty("TimeOfLockUpDoorOfAltar", "180"));
			if(HPH_TIMEOFLOCKUPDOOROFALTAR < 60 || HPH_TIMEOFLOCKUPDOOROFALTAR > 600)
			{
				HPH_TIMEOFLOCKUPDOOROFALTAR = 180;
			}
			HPH_TIMEOFLOCKUPDOOROFALTAR *= 6000;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load " + BOSS_FILE + " File.");
		}
	}

	// --------------------------------------------- //
    // -        CLAN SETTINGS PROPERTIES           - //
    // --------------------------------------------- //
    // =========================================================
	public static int CLAN_JOIN_DAYS;
	public static int CLAN_CREATE_DAYS;
    public static int CLAN_DISSOLVE_DAYS;
    public static int ALLY_JOIN_DAYS_WHEN_LEAVED;
    public static int ALLY_JOIN_DAYS_WHEN_DISMISSED;
    public static int ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
    public static int CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	public static boolean ALLOW_WYVERN_DURING_SIEGE;
	public static int MINIMUN_LEVEL_FOR_CREATION_CLAN;
	public static Pattern CLAN_NAME_PATTERN;
	public static Pattern CLAN_ALLY_NAME_PATTERN;
	public static int CLAN_LEVEL_6_COST;
	public static int CLAN_LEVEL_7_COST;
	public static int CLAN_LEVEL_8_COST;
    public static int CLAN_LEVEL_6_MEMBERS;
    public static int CLAN_LEVEL_7_MEMBERS;
    public static int CLAN_LEVEL_8_MEMBERS;
	public static boolean CLEAR_CREST_CACHE;
	public static int TAKE_CASTLE_POINTS;
	public static int LOOSE_CASTLE_POINTS;
	public static int CASTLE_DEFENDED_POINTS;
	public static int FESTIVAL_WIN_POINTS;
	public static int HERO_POINTS;
	public static int JOIN_ACADEMY_MIN_REP_SCORE;
	public static int JOIN_ACADEMY_MAX_REP_SCORE;
    public static int MAX_NUM_OF_CLANS_IN_ALLY;
    public static int CLAN_MEMBERS_FOR_WAR;
    public static int ALT_REPUTATION_SCORE_PER_KILL;
    public static boolean MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
    public static boolean REMOVE_CASTLE_CIRCLETS;
    public static boolean LIFE_CRYSTAL_NEEDED;
    public static boolean ENABLE_CLAN_SYSTEM;
    public static Map<Integer, Integer>CLAN_SKILLS;
    public static byte CLAN_LEVEL;
    public static int REPUTATION_QUANTITY;
    public static boolean CLAN_LEADER_COLOR_ENABLED;
    public static int CLAN_LEADER_NAME_COLOR;
    public static int CLAN_LEADER_TITLE_COLOR;
    public static boolean GAME_REQUIRE_CASTLE_DAWN;
    public static boolean GAME_REQUIRE_CLAN_CASTLE;
    public static boolean CASTLE_SHIELD;
    public static boolean CLANHALL_SHIELD;
    public static boolean APELLA_ARMORS;
    public static boolean OATH_ARMORS;
    public static boolean CASTLE_CROWN;
    public static boolean CASTLE_CIRCLETS;
    public static long CH_TELE_FEE_RATIO;
    public static int CH_TELE1_FEE;
    public static int CH_TELE2_FEE;
    public static int CH_TELE3_FEE;
    public static int CH_TELE4_FEE;
    public static long CH_SUPPORT_FEE_RATIO;
    public static int CH_SUPPORT1_FEE;
    public static int CH_SUPPORT2_FEE;
    public static int CH_SUPPORT3_FEE;
    public static int CH_SUPPORT4_FEE;
    public static int CH_SUPPORT5_FEE;
    public static int CH_SUPPORT6_FEE;
    public static int CH_SUPPORT7_FEE;
    public static int CH_SUPPORT8_FEE;
    public static long CH_MPREG_FEE_RATIO;
    public static int CH_MPREG1_FEE;
    public static int CH_MPREG2_FEE;
    public static int CH_MPREG3_FEE;
    public static int CH_MPREG4_FEE;
    public static int CH_MPREG5_FEE;
    public static long CH_HPREG_FEE_RATIO;
    public static int CH_HPREG1_FEE;
    public static int CH_HPREG2_FEE;
    public static int CH_HPREG3_FEE;
    public static int CH_HPREG4_FEE;
    public static int CH_HPREG5_FEE;
    public static int CH_HPREG6_FEE;
    public static int CH_HPREG7_FEE;
    public static int CH_HPREG8_FEE;
    public static int CH_HPREG9_FEE;
    public static int CH_HPREG10_FEE;
    public static int CH_HPREG11_FEE;
    public static int CH_HPREG12_FEE;
    public static int CH_HPREG13_FEE;
    public static long CH_EXPREG_FEE_RATIO;
    public static int CH_EXPREG1_FEE;
    public static int CH_EXPREG2_FEE;
    public static int CH_EXPREG3_FEE;
    public static int CH_EXPREG4_FEE;
    public static int CH_EXPREG5_FEE;
    public static int CH_EXPREG6_FEE;
    public static int CH_EXPREG7_FEE;
    public static long CH_ITEM_FEE_RATIO;
    public static int CH_ITEM1_FEE;
    public static int CH_ITEM2_FEE;
    public static int CH_ITEM3_FEE;
    public static long CH_CURTAIN_FEE_RATIO;
    public static int CH_CURTAIN1_FEE;
    public static int CH_CURTAIN2_FEE;
    public static long CH_FRONT_FEE_RATIO;
    public static int CH_FRONT1_FEE;
    public static int CH_FRONT2_FEE;
    // ============================================================

	public static void loadClanConfig()
	{
	    try(InputStream is = new FileInputStream(new File(CLAN_FILE)))
	    {
	        Properties clanSettings = new Properties();
	        clanSettings.load(is);

	        CLAN_JOIN_DAYS = Integer.parseInt(clanSettings.getProperty("DaysBeforeJoinAClan", "5"));
	        CLAN_CREATE_DAYS = Integer.parseInt(clanSettings.getProperty("DaysBeforeCreateAClan", "10"));
	        CLAN_DISSOLVE_DAYS = Integer.parseInt(clanSettings.getProperty("DaysToPassToDissolveAClan", "7"));
	        ALLY_JOIN_DAYS_WHEN_LEAVED = Integer.parseInt(clanSettings.getProperty("DaysBeforeJoinAllyWhenLeaved", "1"));
	        ALLY_JOIN_DAYS_WHEN_DISMISSED = Integer.parseInt(clanSettings.getProperty("DaysBeforeJoinAllyWhenDismissed", "1"));
	        ACCEPT_CLAN_DAYS_WHEN_DISMISSED = Integer.parseInt(clanSettings.getProperty("DaysBeforeAcceptNewClanWhenDismissed", "1"));
	        CREATE_ALLY_DAYS_WHEN_DISSOLVED = Integer.parseInt(clanSettings.getProperty("DaysBeforeCreateNewAllyWhenDissolved", "10"));
			ALLOW_WYVERN_DURING_SIEGE = Boolean.parseBoolean(clanSettings.getProperty("AllowRideWyvernDuringSiege", "True"));
			MINIMUN_LEVEL_FOR_CREATION_CLAN = Integer.parseInt(clanSettings.getProperty("MinLevelToCreateClan", "10"));
			try
			{
				CLAN_NAME_PATTERN = Pattern.compile(clanSettings.getProperty("ClanNameTemplate", "[A-Za-z0-9 \\-]{3,16}"));
			}
			catch (PatternSyntaxException e)
			{
				System.out.println("GameServer: Clan name pattern is wrong!");
				CLAN_NAME_PATTERN = Pattern.compile("[A-Za-z0-9 \\-]{3,16}");
			}
			try
			{
				CLAN_ALLY_NAME_PATTERN = Pattern.compile(clanSettings.getProperty("ClanAllyNameTemplate", "[A-Za-z0-9 \\-]{3,16}"));
			}
			catch (PatternSyntaxException e)
			{
				System.out.println("GameServer: Clan and ally name pattern is wrong!");
				CLAN_ALLY_NAME_PATTERN = Pattern.compile("[A-Za-z0-9 \\-]{3,16}");
			}
			CLAN_LEVEL_6_COST = Integer.parseInt(clanSettings.getProperty("ClanLevel6Cost", "5000"));
			CLAN_LEVEL_7_COST = Integer.parseInt(clanSettings.getProperty("ClanLevel7Cost", "10000"));
			CLAN_LEVEL_8_COST = Integer.parseInt(clanSettings.getProperty("ClanLevel8Cost", "20000"));
	       	CLAN_LEVEL_6_MEMBERS = Integer.parseInt(clanSettings.getProperty("ClanLevel6Members", "30"));
	        CLAN_LEVEL_7_MEMBERS = Integer.parseInt(clanSettings.getProperty("ClanLevel7Members", "80"));
	      	CLAN_LEVEL_8_MEMBERS = Integer.parseInt(clanSettings.getProperty("ClanLevel8Members", "120"));
	      	CLEAR_CREST_CACHE = Boolean.parseBoolean(clanSettings.getProperty("ClearClanCache", "false"));
			TAKE_CASTLE_POINTS = Integer.parseInt(clanSettings.getProperty("TakeCastlePoints", "1500"));
			LOOSE_CASTLE_POINTS = Integer.parseInt(clanSettings.getProperty("LooseCastlePoints", "3000"));
			CASTLE_DEFENDED_POINTS = Integer.parseInt(clanSettings.getProperty("CastleDefendedPoints", "750"));
			FESTIVAL_WIN_POINTS = Integer.parseInt(clanSettings.getProperty("FestivalOfDarknessWin", "200"));
			HERO_POINTS = Integer.parseInt(clanSettings.getProperty("HeroPoints", "1000"));
			JOIN_ACADEMY_MIN_REP_SCORE = Integer.parseInt(clanSettings.getProperty("CompleteAcademyMinPoints", "190"));
			JOIN_ACADEMY_MAX_REP_SCORE = Integer.parseInt(clanSettings.getProperty("CompleteAcademyMaxPoints", "650"));
	        MAX_NUM_OF_CLANS_IN_ALLY  = Integer.parseInt(clanSettings.getProperty("MaxNumOfClansInAlly", "3"));
	        CLAN_MEMBERS_FOR_WAR = Integer.parseInt(clanSettings.getProperty("ClanMembersForWar", "15"));
	        ALT_REPUTATION_SCORE_PER_KILL = Integer.parseInt(clanSettings.getProperty("ReputationScorePerKill", "1"));
	        MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Boolean.parseBoolean(clanSettings.getProperty("AltMembersCanWithdrawFromClanWH", "False"));
	        REMOVE_CASTLE_CIRCLETS = Boolean.parseBoolean(clanSettings.getProperty("RemoveCastleCirclets", "True"));
	        LIFE_CRYSTAL_NEEDED = Boolean.parseBoolean(clanSettings.getProperty("LifeCrystalNeeded", "True"));
            ENABLE_CLAN_SYSTEM = Boolean.parseBoolean(clanSettings.getProperty("EnableClanSystem", "True"));
            if(ENABLE_CLAN_SYSTEM)
            {
                String ClanSkillsSplit[] = clanSettings.getProperty("ClanSkills", "").split(";");
                CLAN_SKILLS = new HashMap<>(ClanSkillsSplit.length);
                String arr$[] = ClanSkillsSplit;
                int len$ = arr$.length;
                for(int i$ = 0; i$ < len$; i$++)
                {
                    String skill = arr$[i$];
                    String skillSplit[] = skill.split(",");
                    if(skillSplit.length != 2)
                    {
                        System.out.println((new StringBuilder()).append("[Clan System]: invalid config property in Clan.prop -> ClanSkills \"").append(skill).append("\"").toString());
                        continue;
                    }
                    try
                    {
                        CLAN_SKILLS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
                        continue;
                    }
                    catch(NumberFormatException nfe) { }
                    if(!skill.equals(""))
                        System.out.println((new StringBuilder()).append("[Clan System]: invalid config property in Clan.prop -> ClanSkills \"").append(skillSplit[0]).append("\"").append(skillSplit[1]).toString());
                }
            }
            CLAN_LEVEL = Byte.parseByte(clanSettings.getProperty("ClanSetLevel", "8"));
            REPUTATION_QUANTITY = Integer.parseInt(clanSettings.getProperty("ReputationScore", "10000"));
        	CLAN_LEADER_COLOR_ENABLED = Boolean.parseBoolean(clanSettings.getProperty("ClanLeaderColorEnabled", "False"));
        	CLAN_LEADER_NAME_COLOR = Integer.decode("0x" + clanSettings.getProperty("ClanLeaderNameColor", "00FF00"));
        	CLAN_LEADER_TITLE_COLOR = Integer.decode("0x" + clanSettings.getProperty("ClanLeaderTitleColor", "00FF00"));
	        GAME_REQUIRE_CASTLE_DAWN = Boolean.parseBoolean(clanSettings.getProperty("RequireCastleForDawn", "False"));
			GAME_REQUIRE_CLAN_CASTLE = Boolean.parseBoolean(clanSettings.getProperty("RequireClanCastle", "False"));
			CASTLE_SHIELD = Boolean.parseBoolean(clanSettings.getProperty("CastleShieldRestriction", "True"));
			CLANHALL_SHIELD = Boolean.parseBoolean(clanSettings.getProperty("ClanHallShieldRestriction", "True"));
			APELLA_ARMORS = Boolean.parseBoolean(clanSettings.getProperty("ApellaArmorsRestriction", "True"));
			OATH_ARMORS = Boolean.parseBoolean(clanSettings.getProperty("OathArmorsRestriction", "True"));
			CASTLE_CROWN = Boolean.parseBoolean(clanSettings.getProperty("CastleLordsCrownRestriction", "True"));
			CASTLE_CIRCLETS = Boolean.parseBoolean(clanSettings.getProperty("CastleCircletsRestriction", "True"));
	      	CH_TELE_FEE_RATIO = Long.valueOf(clanSettings.getProperty("ClanHallTeleportFunctionFeeRation", "86400000"));
	      	CH_TELE1_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallTeleportFunctionFeeLvl1", "86400000"));
	        CH_TELE2_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallTeleportFunctionFeeLvl2", "86400000"));
	        CH_TELE3_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallTeleportFunctionFeeLvl3", "86400000"));
	        CH_TELE4_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallTeleportFunctionFeeLvl4", "86400000"));
	      	CH_SUPPORT_FEE_RATIO = Long.valueOf(clanSettings.getProperty("ClanHallSupportFunctionFeeRation", "86400000"));
	        CH_SUPPORT1_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallSupportFeeLvl1", "86400000"));
	        CH_SUPPORT2_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallSupportFeeLvl2", "86400000"));
	        CH_SUPPORT3_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallSupportFeeLvl3", "86400000"));
	        CH_SUPPORT4_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallSupportFeeLvl4", "86400000"));
	        CH_SUPPORT5_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallSupportFeeLvl5", "86400000"));
	        CH_SUPPORT6_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallSupportFeeLvl6", "86400000"));
	        CH_SUPPORT7_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallSupportFeeLvl7", "86400000"));
	        CH_SUPPORT8_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallSupportFeeLvl8", "86400000"));
	        CH_MPREG_FEE_RATIO = Long.valueOf(clanSettings.getProperty("ClanHallMpRegenerationFunctionFeeRation", "86400000"));
	        CH_MPREG1_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallMpRegenerationFeeLvl1", "86400000"));
	        CH_MPREG2_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallMpRegenerationFeeLvl2", "86400000"));
	        CH_MPREG3_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallMpRegenerationFeeLvl3", "86400000"));
	        CH_MPREG4_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallMpRegenerationFeeLvl4", "86400000"));
	        CH_MPREG5_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallMpRegenerationFeeLvl5", "86400000"));
	        CH_HPREG_FEE_RATIO = Long.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFunctionFeeRation", "86400000"));
	        CH_HPREG1_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl1", "86400000"));
	        CH_HPREG2_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl2", "86400000"));
	        CH_HPREG3_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl3", "86400000"));
	        CH_HPREG4_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl4", "86400000"));
	        CH_HPREG5_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl5", "86400000"));
	        CH_HPREG6_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl6", "86400000"));
	        CH_HPREG7_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl7", "86400000"));
	        CH_HPREG8_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl8", "86400000"));
	        CH_HPREG9_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl9", "86400000"));
	        CH_HPREG10_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl10", "86400000"));
	        CH_HPREG11_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl11", "86400000"));
	        CH_HPREG12_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl12", "86400000"));
	        CH_HPREG13_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallHpRegenerationFeeLvl13", "86400000"));
	        CH_EXPREG_FEE_RATIO = Long.valueOf(clanSettings.getProperty("ClanHallExpRegenerationFunctionFeeRation", "86400000"));
	        CH_EXPREG1_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallExpRegenerationFeeLvl1", "86400000"));
	        CH_EXPREG2_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallExpRegenerationFeeLvl2", "86400000"));
	        CH_EXPREG3_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallExpRegenerationFeeLvl3", "86400000"));
	        CH_EXPREG4_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallExpRegenerationFeeLvl4", "86400000"));
	        CH_EXPREG5_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallExpRegenerationFeeLvl5", "86400000"));
	        CH_EXPREG6_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallExpRegenerationFeeLvl6", "86400000"));
	        CH_EXPREG7_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallExpRegenerationFeeLvl7", "86400000"));
	        CH_ITEM_FEE_RATIO = Long.valueOf(clanSettings.getProperty("ClanHallItemCreationFunctionFeeRation", "86400000"));
	        CH_ITEM1_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallItemCreationFunctionFeeLvl1", "86400000"));
	        CH_ITEM2_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallItemCreationFunctionFeeLvl2", "86400000"));
	        CH_ITEM3_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallItemCreationFunctionFeeLvl3", "86400000"));
	        CH_CURTAIN_FEE_RATIO = Long.valueOf(clanSettings.getProperty("ClanHallCurtainFunctionFeeRation", "86400000"));
	        CH_CURTAIN1_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallCurtainFunctionFeeLvl1", "86400000"));
	        CH_CURTAIN2_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallCurtainFunctionFeeLvl2", "86400000"));
	        CH_FRONT_FEE_RATIO = Long.valueOf(clanSettings.getProperty("ClanHallFrontPlatformFunctionFeeRation", "86400000"));
	        CH_FRONT1_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallFrontPlatformFunctionFeeLvl1", "86400000"));
	        CH_FRONT2_FEE = Integer.valueOf(clanSettings.getProperty("ClanHallFrontPlatformFunctionFeeLvl2", "86400000"));
		}
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        throw new Error("Failed to Load " + CLAN_FILE + " File.");
	    }
	}

	// --------------------------------------------- //
    // -        CLASS SETTINGS PROPERTIES          - //
    // --------------------------------------------- //
    // =========================================================
	public static int FRONT_BLOW_CHANCE;
    public static int SIDE_BLOW_CHANCE;
    public static int BEHIND_BLOW_CHANCE;
    public static boolean BACKSTABRESTRICTION;
    public static boolean GAME_MAGICFAILURES;
    public static boolean GAME_CANCEL_BOW;
    public static boolean GAME_CANCEL_CAST;
    public static boolean GAME_SHIELD_BLOCKS;
    public static int PERFECT_SHIELD_BLOCK;
    public static int ALT_CLASSID;
    public static float ALT_DAMAGE;
    public static boolean ALT_DAGGER;
    public static boolean ALT_BOW;
    public static boolean ALT_BLUNT;
    public static boolean ALT_DUALFIST;
    public static boolean ALT_DUAL;
    public static boolean ALT_SWORD;
    public static boolean ALT_POLE;
    public static boolean ALT_DISABLE_BOW_CLASSES;
	public static List<Integer> DISABLE_BOW_CLASSES;
	// =========================================================

	public static void loadClassConfig()
	{
		try(InputStream is = new FileInputStream(new File(CLASS_FILE)))
		{
			Properties Class = new Properties();
			Class.load(is);

			FRONT_BLOW_CHANCE = Integer.parseInt(Class.getProperty("FrontBlowChance", "50"));
	        SIDE_BLOW_CHANCE = Integer.parseInt(Class.getProperty("SideBlowChance", "60"));
	        BEHIND_BLOW_CHANCE = Integer.parseInt(Class.getProperty("BehindBlowChance", "70"));
	        BACKSTABRESTRICTION = Boolean.parseBoolean(Class.getProperty("RestrictionBackstab", "False"));
	        GAME_MAGICFAILURES = Boolean.parseBoolean(Class.getProperty("MagicFailures", "false"));
	        GAME_CANCEL_BOW = Class.getProperty("GameCancelByHit", "Cast").equalsIgnoreCase("bow") || Class.getProperty("GameCancelByHit", "Cast").equalsIgnoreCase("all");
	        GAME_CANCEL_CAST = Class.getProperty("GameCancelByHit", "Cast").equalsIgnoreCase("cast") || Class.getProperty("GameCancelByHit", "Cast").equalsIgnoreCase("all");
	        GAME_SHIELD_BLOCKS = Boolean.parseBoolean(Class.getProperty("ShieldBlocks", "false"));
	        PERFECT_SHIELD_BLOCK = Integer.parseInt(Class.getProperty("PerfectShieldBlockRate", "10"));
	        ALT_CLASSID = Integer.parseInt(Class.getProperty("ClassID", "90"));
	        ALT_DAMAGE = Float.parseFloat(Class.getProperty("Damage", "1.5"));
	        ALT_DAGGER = Class.getProperty("WeaponType", "DAGGER").equalsIgnoreCase("DAGGER");
	        ALT_BOW = Class.getProperty("WeaponType", "DAGGER").equalsIgnoreCase("BOW");
	        ALT_BLUNT = Class.getProperty("WeaponType", "DAGGER").equalsIgnoreCase("BLUNT");
	        ALT_DUALFIST = Class.getProperty("WeaponType", "DAGGER").equalsIgnoreCase("DUALFIST");
	        ALT_DUAL = Class.getProperty("WeaponType", "DAGGER").equalsIgnoreCase("DUAL");
	        ALT_SWORD = Class.getProperty("WeaponType", "DAGGER").equalsIgnoreCase("SWORD");
	        ALT_POLE = Class.getProperty("WeaponType", "DAGGER").equalsIgnoreCase("POLE");
	        ALT_DISABLE_BOW_CLASSES = Boolean.parseBoolean(Class.getProperty("AltDisableBow", "False"));
			DISABLE_BOW_CLASSES = new ArrayList<>();
			for (String class_id : Class.getProperty("DisableBowForClasses", "").split(","))
			{
				if(!class_id.equals(""))
					DISABLE_BOW_CLASSES.add(Integer.parseInt(class_id));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load "+CLASS_FILE+" File.");
		}
	}

	// --------------------------------------------- //
    // -         ENCHANT CONFIG PROPERTIES         - //
    // --------------------------------------------- //
    // ============================================================
    public static int ENCHANT_CHANCE_WEAPON;
    public static int ENCHANT_CHANCE_ARMOR;
    public static int ENCHANT_CHANCE_JEWELRY;
    public static int ENCHANT_CHANCE_WEAPON_CRYSTAL;
    public static int ENCHANT_CHANCE_ARMOR_CRYSTAL;
    public static int ENCHANT_CHANCE_JEWELRY_CRYSTAL;
    public static int ENCHANT_CHANCE_WEAPON_BLESSED;
    public static int ENCHANT_CHANCE_ARMOR_BLESSED;
    public static int ENCHANT_CHANCE_JEWELRY_BLESSED;
    /* Enchating Chance System */
    /* -- Scroll */
	public static boolean ENABLE_ENCHANT_CHANCE_SCROLL_WEAPON;
	public static Map<Integer, Integer> ENCHANT_CHANCE_SCROLL_WEAPON_LIST;
	public static boolean ENABLE_ENCHANT_CHANCE_CRYSTAL_WEAPON;
	public static Map<Integer, Integer> ENCHANT_CHANCE_CRYSTAL_WEAPON_LIST;
	public static boolean ENABLE_ENCHANT_CHANCE_BLESSED_WEAPON;
	public static Map<Integer, Integer> ENCHANT_CHANCE_BLESSED_WEAPON_LIST;
	/* Crystal */
	public static boolean ENABLE_ENCHANT_CHANCE_SCROLL_ARMOR;
	public static Map<Integer, Integer> ENCHANT_CHANCE_SCROLL_ARMOR_LIST;
	public static boolean ENABLE_ENCHANT_CHANCE_CRYSTAL_ARMOR;
	public static Map<Integer, Integer> ENCHANT_CHANCE_CRYSTAL_ARMOR_LIST;
	public static boolean ENABLE_ENCHANT_CHANCE_BLESSED_ARMOR;
	public static Map<Integer, Integer> ENCHANT_CHANCE_BLESSED_ARMOR_LIST;
	/* Blessed */
	public static boolean ENABLE_ENCHANT_CHANCE_SCROLL_JEWELRY;
	public static Map<Integer, Integer> ENCHANT_CHANCE_SCROLL_JEWELRY_LIST;
	public static boolean ENABLE_ENCHANT_CHANCE_CRYSTAL_JEWELRY;
	public static Map<Integer, Integer> ENCHANT_CHANCE_CRYSTAL_JEWELRY_LIST;
	public static boolean ENABLE_ENCHANT_CHANCE_BLESSED_JEWELRY;
	public static Map<Integer, Integer> ENCHANT_CHANCE_BLESSED_JEWELRY_LIST;
    /* Vip Enchanting Config */
    public static boolean VIP_ENCH_RATES;
    public static int ENCHANT_CHANCE_WEAPON_VIP;
    public static int ENCHANT_CHANCE_ARMOR_VIP;
    public static int ENCHANT_CHANCE_JEWELRY_VIP;
    public static int ENCHANT_CHANCE_JEWELRY_BLESSED_VIP;
    public static int ENCHANT_CHANCE_ARMOR_BLESSED_VIP;
    public static int ENCHANT_CHANCE_WEAPON_BLESSED_VIP;
    public static int ENCHANT_CHANCE_WEAPON_CRYSTAL_VIP;
    public static int ENCHANT_CHANCE_ARMOR_CRYSTAL_VIP;
    public static int ENCHANT_CHANCE_JEWELRY_CRYSTAL_VIP;
    public static int ENCHANT_SAFE_MAX;
    public static int ENCHANT_SAFE_MAX_FULL;
    public static int ENCHANT_MAX_WEAPON;
    public static int ENCHANT_MAX_ARMOR;
    public static int ENCHANT_MAX_JEWELRY;

    public static Map<Integer, Integer> ALT_ENCHANTS_LIST;
	public static int ENCHANT_MAX_ALLOWED_WEAPON;
	public static int ENCHANT_MAX_ALLOWED_ARMOR;
	public static int ENCHANT_MAX_ALLOWED_JEWELRY;
    public static boolean OVER_ENCHANT_PROTECTION_ENABLED;
	public static int GM_OVER_ENCHANT;
    public static int MAX_ITEM_ENCHANT_KICK;
	public static boolean ENCHANT_HERO_WEAPONS;
    public static boolean ENABLE_DWARF_ENCHANT_BONUS;
	public static int DWARF_ENCHANT_MIN_LEVEL;
	public static int DWARF_ENCHANT_BONUS;
	public static int ALTERNATIVE_ENCHANT_VALUE;
    // ============================================================
	public static void loadEnchantConfig()
	{
	    try(InputStream is = new FileInputStream(new File(ENCHANT_FILE)))
	    {
	        Properties EnchantSettings = new Properties();
	        EnchantSettings.load(is);

	        ENCHANT_CHANCE_WEAPON = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceWeapon", "68"));
	        ENCHANT_CHANCE_ARMOR = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceArmor", "52"));
	        ENCHANT_CHANCE_JEWELRY = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceJewelry", "54"));
	        ENCHANT_CHANCE_WEAPON_CRYSTAL = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceWeaponCrystal", "100"));
	        ENCHANT_CHANCE_ARMOR_CRYSTAL = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceArmorCrystal", "100"));
	        ENCHANT_CHANCE_JEWELRY_CRYSTAL = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceJewelryCrystal", "100"));
	        ENCHANT_CHANCE_WEAPON_BLESSED = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceWeaponBlessed", "85"));
	        ENCHANT_CHANCE_ARMOR_BLESSED = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceArmorBlessed", "85"));
	        ENCHANT_CHANCE_JEWELRY_BLESSED = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceJewelryBlessed", "85"));
	        /* Enchanting System Scroll Chance */
			ENABLE_ENCHANT_CHANCE_SCROLL_WEAPON = Boolean.parseBoolean(EnchantSettings.getProperty("EnableEnchantChanceScrollWeapon", "False"));
	        if (ENABLE_ENCHANT_CHANCE_SCROLL_WEAPON)
	        {
	            ENCHANT_CHANCE_SCROLL_WEAPON_LIST = new HashMap<>();
                String[] propertySplit;
	            propertySplit = EnchantSettings.getProperty("ChanceScrollWeaponList", "").split(";");
	            for (String enchant : propertySplit)
	            {
	                String[] enchantSplit = enchant.split(",");
	                if (enchantSplit.length != 2)
	                {
	                    System.out.println("[ChanceScrollWeaponList]: invalid config property -> ChanceScrollWeaponList \"" + enchant + "\"");
	                }
	                else
	                {
	                    try
	                    {
	                        ENCHANT_CHANCE_SCROLL_WEAPON_LIST.put(Integer.parseInt(enchantSplit[0]), Integer.parseInt(enchantSplit[1]));
	                    }
	                    catch (NumberFormatException nfe)
	                    {
	                        if (!enchant.equals(""))
	                        {
	                            System.out.println("[ChanceScrollWeaponList]: invalid config property -> ChanceScrollWeaponList \"" + enchantSplit[0] + "\"" + enchantSplit[1]);
	                        }
	                    }
	                }
	            }
	        }
			ENABLE_ENCHANT_CHANCE_SCROLL_ARMOR = Boolean.parseBoolean(EnchantSettings.getProperty("EnableEnchantChanceScrollArmor", "False"));
	        if (ENABLE_ENCHANT_CHANCE_SCROLL_ARMOR)
	        {
	            ENCHANT_CHANCE_SCROLL_ARMOR_LIST = new HashMap<>();
	            String[] propertySplit;
	            propertySplit = EnchantSettings.getProperty("ChanceScrollArmorList", "").split(";");
	            for (String enchant : propertySplit)
	            {
	                String[] enchantSplit = enchant.split(",");
	                if (enchantSplit.length != 2)
	                {
	                    System.out.println("[ChanceScrollArmorList]: invalid config property -> ChanceScrollArmorList \"" + enchant + "\"");
	                }
	                else
	                {
	                    try
	                    {
	                        ENCHANT_CHANCE_SCROLL_ARMOR_LIST.put(Integer.parseInt(enchantSplit[0]), Integer.parseInt(enchantSplit[1]));
	                    }
	                    catch (NumberFormatException nfe)
	                    {
	                        if (!enchant.equals(""))
	                        {
	                            System.out.println("[ChanceScrollArmorList]: invalid config property -> ChanceScrollArmorList \"" + enchantSplit[0] + "\"" + enchantSplit[1]);
	                        }
	                    }
	                }
	            }
	        }
			ENABLE_ENCHANT_CHANCE_SCROLL_JEWELRY = Boolean.parseBoolean(EnchantSettings.getProperty("EnableEnchantChanceScrollJewelry", "False"));
	        if (ENABLE_ENCHANT_CHANCE_SCROLL_JEWELRY)
	        {
	            ENCHANT_CHANCE_SCROLL_JEWELRY_LIST = new HashMap<>();
	            String[] propertySplit;
	            propertySplit = EnchantSettings.getProperty("ChanceScrollJewelryList", "").split(";");
	            for (String enchant : propertySplit)
	            {
	                String[] enchantSplit = enchant.split(",");
	                if (enchantSplit.length != 2)
	                {
	                    System.out.println("[ChanceScrollJewelryList]: invalid config property -> ChanceScrollJewelryList \"" + enchant + "\"");
	                }
	                else
	                {
	                    try
	                    {
	                        ENCHANT_CHANCE_SCROLL_JEWELRY_LIST.put(Integer.parseInt(enchantSplit[0]), Integer.parseInt(enchantSplit[1]));
	                    }
	                    catch (NumberFormatException nfe)
	                    {
	                        if (!enchant.equals(""))
	                        {
	                            System.out.println("[ChanceScrollJewelryList]: invalid config property -> ChanceScrollJewelryList \"" + enchantSplit[0] + "\"" + enchantSplit[1]);
	                        }
	                    }
	                }
	            }
	        }
	        /* Enchanting System Blessed Chance */
			ENABLE_ENCHANT_CHANCE_BLESSED_WEAPON = Boolean.parseBoolean(EnchantSettings.getProperty("EnableEnchantChanceBlessedWeapon", "False"));
	        if (ENABLE_ENCHANT_CHANCE_BLESSED_WEAPON)
	        {
	            ENCHANT_CHANCE_BLESSED_WEAPON_LIST = new HashMap<>();
	            String[] propertySplit;
	            propertySplit = EnchantSettings.getProperty("ChanceBlessedWeaponList", "").split(";");
	            for (String enchant : propertySplit)
	            {
	                String[] enchantSplit = enchant.split(",");
	                if (enchantSplit.length != 2)
	                {
	                    System.out.println("[ChanceBlessedWeaponList]: invalid config property -> ChanceBlessedWeaponList \"" + enchant + "\"");
	                }
	                else
	                {
	                    try
	                    {
	                        ENCHANT_CHANCE_BLESSED_WEAPON_LIST.put(Integer.parseInt(enchantSplit[0]), Integer.parseInt(enchantSplit[1]));
	                    }
	                    catch (NumberFormatException nfe)
	                    {
	                        if (!enchant.equals(""))
	                        {
	                            System.out.println("[ChanceBlessedWeaponList]: invalid config property -> ChanceBlessedWeaponList \"" + enchantSplit[0] + "\"" + enchantSplit[1]);
	                        }
	                    }
	                }
	            }
	        }
			ENABLE_ENCHANT_CHANCE_BLESSED_ARMOR = Boolean.parseBoolean(EnchantSettings.getProperty("EnableEnchantChanceBlessedArmor", "False"));
	        if (ENABLE_ENCHANT_CHANCE_BLESSED_ARMOR)
	        {
	            ENCHANT_CHANCE_BLESSED_ARMOR_LIST = new HashMap<>();
	            String[] propertySplit;
	            propertySplit = EnchantSettings.getProperty("ChanceBlessedArmorList", "").split(";");
	            for (String enchant : propertySplit)
	            {
	                String[] enchantSplit = enchant.split(",");
	                if (enchantSplit.length != 2)
	                {
	                    System.out.println("[ChanceBlessedArmorList]: invalid config property -> ChanceBlessedArmorList \"" + enchant + "\"");
	                }
	                else
	                {
	                    try
	                    {
	                        ENCHANT_CHANCE_BLESSED_ARMOR_LIST.put(Integer.parseInt(enchantSplit[0]), Integer.parseInt(enchantSplit[1]));
	                    }
	                    catch (NumberFormatException nfe)
	                    {
	                        if (!enchant.equals(""))
	                        {
	                            System.out.println("[ChanceBlessedArmorList]: invalid config property -> ChanceBlessedArmorList \"" + enchantSplit[0] + "\"" + enchantSplit[1]);
	                        }
	                    }
	                }
	            }
	        }
			ENABLE_ENCHANT_CHANCE_BLESSED_JEWELRY = Boolean.parseBoolean(EnchantSettings.getProperty("EnableEnchantChanceBlessedJewelry", "False"));
	        if (ENABLE_ENCHANT_CHANCE_BLESSED_JEWELRY)
	        {
	            ENCHANT_CHANCE_BLESSED_JEWELRY_LIST = new HashMap<>();
	            String[] propertySplit;
	            propertySplit = EnchantSettings.getProperty("ChanceBlessedJewelryList", "").split(";");
	            for (String enchant : propertySplit)
	            {
	                String[] enchantSplit = enchant.split(",");
	                if (enchantSplit.length != 2)
	                {
	                    System.out.println("[ChanceBlessedJewelryList]: invalid config property -> ChanceBlessedJewelryList \"" + enchant + "\"");
	                }
	                else
	                {
	                    try
	                    {
	                        ENCHANT_CHANCE_BLESSED_JEWELRY_LIST.put(Integer.parseInt(enchantSplit[0]), Integer.parseInt(enchantSplit[1]));
	                    }
	                    catch (NumberFormatException nfe)
	                    {
	                        if (!enchant.equals(""))
	                        {
	                            System.out.println("[ChanceBlessedJewelryList]: invalid config property -> ChanceBlessedJewelryList \"" + enchantSplit[0] + "\"" + enchantSplit[1]);
	                        }
	                    }
	                }
	            }
	        }
	        /* Enchanting System Crystal Chance */
			ENABLE_ENCHANT_CHANCE_CRYSTAL_WEAPON = Boolean.parseBoolean(EnchantSettings.getProperty("EnableEnchantChanceCrystalWeapon", "False"));
	        if (ENABLE_ENCHANT_CHANCE_CRYSTAL_WEAPON)
	        {
	            ENCHANT_CHANCE_CRYSTAL_WEAPON_LIST = new HashMap<>();
	            String[] propertySplit;
	            propertySplit = EnchantSettings.getProperty("ChanceCrystalWeaponList", "").split(";");
	            for (String enchant : propertySplit)
	            {
	                String[] enchantSplit = enchant.split(",");
	                if (enchantSplit.length != 2)
	                {
	                    System.out.println("[ChanceCrystalWeaponList]: invalid config property -> ChanceCrystalWeaponList \"" + enchant + "\"");
	                }
	                else
	                {
	                    try
	                    {
	                        ENCHANT_CHANCE_CRYSTAL_WEAPON_LIST.put(Integer.parseInt(enchantSplit[0]), Integer.parseInt(enchantSplit[1]));
	                    }
	                    catch (NumberFormatException nfe)
	                    {
	                        if (!enchant.equals(""))
	                        {
	                            System.out.println("[ChanceCrystalWeaponList]: invalid config property -> ChanceCrystalWeaponList \"" + enchantSplit[0] + "\"" + enchantSplit[1]);
	                        }
	                    }
	                }
	            }
	        }
			ENABLE_ENCHANT_CHANCE_CRYSTAL_ARMOR = Boolean.parseBoolean(EnchantSettings.getProperty("EnableEnchantChanceCrystalArmor", "False"));
	        if (ENABLE_ENCHANT_CHANCE_CRYSTAL_ARMOR)
	        {
	            ENCHANT_CHANCE_CRYSTAL_ARMOR_LIST = new HashMap<>();
	            String[] propertySplit;
	            propertySplit = EnchantSettings.getProperty("ChanceCrystalArmorList", "").split(";");
	            for (String enchant : propertySplit)
	            {
	                String[] enchantSplit = enchant.split(",");
	                if (enchantSplit.length != 2)
	                {
	                    System.out.println("[ChanceCrystalArmorList]: invalid config property -> ChanceCrystalArmorList \"" + enchant + "\"");
	                }
	                else
	                {
	                    try
	                    {
	                        ENCHANT_CHANCE_CRYSTAL_ARMOR_LIST.put(Integer.parseInt(enchantSplit[0]), Integer.parseInt(enchantSplit[1]));
	                    }
	                    catch (NumberFormatException nfe)
	                    {
	                        if (!enchant.equals(""))
	                        {
	                            System.out.println("[ChanceCrystalArmorList]: invalid config property -> ChanceCrystalArmorList \"" + enchantSplit[0] + "\"" + enchantSplit[1]);
	                        }
	                    }
	                }
	            }
	        }
			ENABLE_ENCHANT_CHANCE_CRYSTAL_JEWELRY = Boolean.parseBoolean(EnchantSettings.getProperty("EnableEnchantChanceCrystalJewelry", "False"));
	        if (ENABLE_ENCHANT_CHANCE_CRYSTAL_JEWELRY)
	        {
	            ENCHANT_CHANCE_CRYSTAL_JEWELRY_LIST = new HashMap<>();
	            String[] propertySplit;
	            propertySplit = EnchantSettings.getProperty("ChanceCrystalJewelryList", "").split(";");
	            for (String enchant : propertySplit)
	            {
	                String[] enchantSplit = enchant.split(",");
	                if (enchantSplit.length != 2)
	                {
	                    System.out.println("[ChanceCrystalJewelryList]: invalid config property -> ChanceCrystalJewelryList \"" + enchant + "\"");
	                }
	                else
	                {
	                    try
	                    {
	                        ENCHANT_CHANCE_CRYSTAL_JEWELRY_LIST.put(Integer.parseInt(enchantSplit[0]), Integer.parseInt(enchantSplit[1]));
	                    }
	                    catch (NumberFormatException nfe)
	                    {
	                        if (!enchant.equals(""))
	                        {
	                            System.out.println("[ChanceCrystalJewelryList]: invalid config property -> ChanceCrystalJewelryList \"" + enchantSplit[0] + "\"" + enchantSplit[1]);
	                        }
	                    }
	                }
	            }
	        }
	        /* Vip Enchanting */
	        VIP_ENCH_RATES = Boolean.parseBoolean(EnchantSettings.getProperty("VipEnchantEnabled", "False"));
	        ENCHANT_CHANCE_WEAPON_VIP = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceWeaponVip", "68"));
	        ENCHANT_CHANCE_ARMOR_VIP = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceArmorVip", "52"));
	        ENCHANT_CHANCE_JEWELRY_VIP = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceJewelryVip", "54"));
	        ENCHANT_CHANCE_WEAPON_CRYSTAL_VIP = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceWeaponCrystalVip", "100"));
	        ENCHANT_CHANCE_ARMOR_CRYSTAL_VIP = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceArmorCrystalVip", "100"));
	        ENCHANT_CHANCE_JEWELRY_CRYSTAL_VIP = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceJewelryCrystalVip", "100"));
	        ENCHANT_CHANCE_WEAPON_BLESSED_VIP = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceWeaponBlessedVip", "85"));
	        ENCHANT_CHANCE_ARMOR_BLESSED_VIP = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceArmorBlessedVip", "85"));
	        ENCHANT_CHANCE_JEWELRY_BLESSED_VIP = Integer.parseInt(EnchantSettings.getProperty("EnchantChanceJewelryBlessedVip", "85"));
	        ENCHANT_SAFE_MAX = Integer.parseInt(EnchantSettings.getProperty("EnchantSafeMax", "3"));
	        ENCHANT_SAFE_MAX_FULL = Integer.parseInt(EnchantSettings.getProperty("EnchantSafeMaxFull", "4"));
	        ENCHANT_MAX_WEAPON = Integer.parseInt(EnchantSettings.getProperty("EnchantMaxWeapon", "255"));
	        ENCHANT_MAX_ARMOR = Integer.parseInt(EnchantSettings.getProperty("EnchantMaxArmor", "255"));
	        ENCHANT_MAX_JEWELRY = Integer.parseInt(EnchantSettings.getProperty("EnchantMaxJewelry", "255"));
	        ALT_ENCHANTS_LIST = new HashMap<>();
	        for (String id : EnchantSettings.getProperty("EnchantMaxItem", "10").split(";"))
	        {
                String[] enchantConfig = id.split(",");
                ALT_ENCHANTS_LIST.put(Integer.valueOf(enchantConfig[0]), Integer.valueOf(enchantConfig[1]));
	        }
	        ENCHANT_MAX_ALLOWED_WEAPON = Integer.parseInt(EnchantSettings.getProperty("EnchantMaxAllowedWeapon", "65535"));
	        ENCHANT_MAX_ALLOWED_ARMOR = Integer.parseInt(EnchantSettings.getProperty("EnchantMaxAllowedArmor", "65535"));
	        ENCHANT_MAX_ALLOWED_JEWELRY = Integer.parseInt(EnchantSettings.getProperty("EnchantMaxAllowedJewelry", "65535"));
	        OVER_ENCHANT_PROTECTION_ENABLED = Boolean.parseBoolean(EnchantSettings.getProperty("OverEnchantProtection", "True"));
	        GM_OVER_ENCHANT = Integer.parseInt(EnchantSettings.getProperty("GMOverEnchant", "25"));
	        MAX_ITEM_ENCHANT_KICK = Integer.parseInt(EnchantSettings.getProperty("EnchantKick", "11"));
	        ENCHANT_HERO_WEAPONS = Boolean.parseBoolean(EnchantSettings.getProperty("EnchantHeroWeapons", "False"));
	        ENABLE_DWARF_ENCHANT_BONUS = Boolean.parseBoolean(EnchantSettings.getProperty("EnableDwarfEnchantBonus", "False"));
	        DWARF_ENCHANT_MIN_LEVEL = Integer.parseInt(EnchantSettings.getProperty("DwarfEnchantMinLevel", "80"));
	        DWARF_ENCHANT_BONUS = Integer.parseInt(EnchantSettings.getProperty("DwarfEncahntBonus", "15"));
	        ALTERNATIVE_ENCHANT_VALUE = Integer.parseInt(EnchantSettings.getProperty("AlternativeEnchantValue", "1"));
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        throw new Error("Failed to Load " + ENCHANT_FILE + " File.");
	    }
	}

	// --------------------------------------------- //
    // -        EXTENSION CONFIG PROPERTIES        - //
    // --------------------------------------------- //
    // ============================================================
	/* Aio System */
	public static boolean ENABLE_AIO_SYSTEM;
	public static Map<Integer, Integer> AIO_SKILLS;
	public static boolean ALLOW_AIO_NCOLOR;
	public static int AIO_NCOLOR;
	public static boolean ALLOW_AIO_TCOLOR;
	public static int AIO_TCOLOR;
    public static boolean ALLOW_AIO_USE_GK;
    public static boolean AIO_EFFECT;
    public static int AIO_EFFECT_ID;
	public static boolean ALLOW_AIO_ITEM;
	public static int AIO_ITEMID;

	/* Vip System */
	public static boolean ALLOW_VIP_NCOLOR;
	public static int VIP_NCOLOR;
	public static boolean ALLOW_VIP_TCOLOR;
	public static int VIP_TCOLOR;
	public static boolean ALLOW_VIP_XPSP;
	public static int VIP_XP;
	public static int VIP_SP;
    public static boolean ENABLE_VIP_SYSTEM;
    public static Map<Integer, Integer> VIP_SKILLS;
    // ============================================================

	public static void loadExtensionsConfig()
	{
	    try(InputStream is = new FileInputStream(EXTENSIONS_FILE))
	    {
	    	Properties Extensions = new Properties();
	    	Extensions.load(is);

	    	ALLOW_AIO_NCOLOR = Boolean.parseBoolean(Extensions.getProperty("AllowAioNameColor", "True"));
	    	AIO_NCOLOR = Integer.decode("0x" + Extensions.getProperty("AioNameColor", "88AA88"));
	    	ALLOW_AIO_TCOLOR = Boolean.parseBoolean(Extensions.getProperty("AllowAioTitleColor", "True"));
	    	AIO_TCOLOR = Integer.decode("0x" + Extensions.getProperty("AioTitleColor", "88AA88"));
	    	ALLOW_AIO_USE_GK = Boolean.parseBoolean(Extensions.getProperty("AllowAioUseGk", "False"));
        	AIO_EFFECT = Boolean.parseBoolean(Extensions.getProperty("AllowAioEffect", "False"));
        	AIO_EFFECT_ID = Integer.decode("0x" + Extensions.getProperty("AioEffectId", "000001"));
	    	ALLOW_AIO_ITEM = Boolean.parseBoolean(Extensions.getProperty("AllowAIOItem", "False"));
	        AIO_ITEMID = Integer.parseInt(Extensions.getProperty("ItemIdAio", "9945"));
	        ENABLE_AIO_SYSTEM = Boolean.parseBoolean(Extensions.getProperty("EnableAioSystem", "True"));
	        if(ENABLE_AIO_SYSTEM) //create map if system is enabled
	    	{
	    		String[] AioSkillsSplit = Extensions.getProperty("AioSkills", "").split(";");
	    		AIO_SKILLS = new HashMap<>(AioSkillsSplit.length);
	    		for (String skill : AioSkillsSplit)
	    		{
	    			String[] skillSplit = skill.split(",");
	    			if (skillSplit.length != 2)
	    			{
	    				System.out.println("[Aio System]: invalid config property in Extensions.properties -> AioSkills \"" + skill + "\"");
	    			}
	    			else
	    			{
	    				try
	    				{
	    					AIO_SKILLS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
	    				}
	    				catch (NumberFormatException nfe)
	    				{
	    					if (!skill.equals(""))
	    					{
	    						System.out.println("[Aio System]: invalid config property in Extensions.properties -> AioSkills \"" + skillSplit[0] + "\"" + skillSplit[1]);
	    					}
	    				}
	    			}
	    		}
	    	}
	    	ALLOW_VIP_NCOLOR = Boolean.parseBoolean(Extensions.getProperty("AllowVipNameColor", "True"));
	    	VIP_NCOLOR = Integer.decode("0x" + Extensions.getProperty("VipNameColor", "0088FF"));
	    	ALLOW_VIP_TCOLOR = Boolean.parseBoolean(Extensions.getProperty("AllowVipTitleColor", "True"));
	    	VIP_TCOLOR = Integer.decode("0x" + Extensions.getProperty("VipTitleColor", "0088FF"));
	    	ENABLE_VIP_SYSTEM = Boolean.parseBoolean(Extensions.getProperty("EnableVipSystem", "True"));
	        if(ENABLE_VIP_SYSTEM) //create map if system is enabled
	    	{
	    		String[] VipSkillsSplit = Extensions.getProperty("VipSkills", "").split(";");
	    		VIP_SKILLS = new HashMap<>(VipSkillsSplit.length);
	    		for (String skill : VipSkillsSplit)
	    		{
	    			String[] skillSplit = skill.split(",");
	    			if (skillSplit.length != 2)
	    			{
	    				System.out.println("[Vip System]: invalid config property in Extensions.properties -> VipSkills \"" + skill + "\"");
	    			}
	    			else
	    			{
	    				try
	    				{
	    					VIP_SKILLS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
	    				}
	    				catch (NumberFormatException nfe)
	    				{
	    					if (!skill.equals(""))
	    					{
	    						System.out.println("[Vip System]: invalid config property in Extensions.properties -> VipSkills \"" + skillSplit[0] + "\"" + skillSplit[1]);
	    					}
	    				}
	    			}
	    		}
	    	}
	    	ALLOW_VIP_XPSP = Boolean.parseBoolean(Extensions.getProperty("AllowVipMulXpSp", "True"));
	    	VIP_XP = Integer.parseInt(Extensions.getProperty("VipMulXp", "2"));
	    	VIP_SP = Integer.parseInt(Extensions.getProperty("VipMulSp", "2"));
	    }
	    catch (Exception e)
	    {
	    	_log.warning("Could not load extensions file (" + EXTENSIONS_FILE + ").");
	    }
	}

	// --------------------------------------------- //
    // -             OPTIONS PROPERTIES            - //
    // --------------------------------------------- //
    // ============================================================
    public static boolean SERVER_LIST_TESTSERVER;
    public static boolean ACCEPT_GEOEDITOR_CONN;
    public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
    public static boolean SERVER_LIST_BRACKET;
    public static boolean SERVER_LIST_CLOCK;
    public static boolean SERVER_GMONLY;
    public static int COORD_SYNCHRONIZE;
    public static int ZONE_TOWN;
    public static String  DEFAULT_GLOBAL_CHAT;
    public static String  DEFAULT_TRADE_CHAT;
    public static int DEFAULT_PUNISH;
    public static int DEFAULT_PUNISH_PARAM;
    public static boolean BYPASS_VALIDATION;
    public static boolean GAMEGUARD_ENFORCE;
    public static boolean GAMEGUARD_PROHIBITACTION;
    public static int DELETE_DAYS;
    public static int FLOODPROTECTOR_INITIALSIZE;
    public static boolean ALLOW_DISCARDITEM;
    public static int AUTODESTROY_ITEM_AFTER;
    public static int HERB_AUTO_DESTROY_TIME;
    public static List<Integer> LIST_PROTECTED_ITEMS;
    public static boolean DESTROY_DROPPED_PLAYER_ITEM;
    public static boolean DESTROY_EQUIPABLE_PLAYER_ITEM;
    public static boolean SAVE_DROPPED_ITEM;
    public static boolean EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD;
    public static int SAVE_DROPPED_ITEM_INTERVAL;
    public static boolean CLEAR_DROPPED_ITEM_TABLE;
    public static boolean AUTODELETE_INVALID_QUEST_DATA;
    public static boolean PRECISE_DROP_CALCULATION;
    public static boolean MULTIPLE_ITEM_DROP;
    public static boolean FORCE_INVENTORY_UPDATE;
    public static int MAX_MULTISELL;
    public static boolean LAZY_CACHE;
    public static int MAX_DRIFT_RANGE;
    public static int MIN_NPC_ANIMATION;
    public static int MAX_NPC_ANIMATION;
    public static int MIN_MONSTER_ANIMATION;
    public static int MAX_MONSTER_ANIMATION;
    public static boolean SERVER_NEWS;
    public static boolean SHOW_NPC_LVL;
    public static boolean ALT_ATTACKABLE_NPCS;
    public static boolean ACTIVATE_POSITION_RECORDER;
    public static boolean ALLOW_WAREHOUSE;
    public static boolean WAREHOUSE_CACHE;
    public static int WAREHOUSE_CACHE_TIME;
    public static boolean ALLOW_FREIGHT;
    public static boolean ALLOW_WEAR;
    public static int WEAR_DELAY;
    public static int WEAR_PRICE;
    public static boolean ALLOW_LOTTERY;
    public static boolean ALLOW_RACE;
    public static boolean ALLOW_WATER;
    public static boolean ALLOW_RENTPET;
    public static boolean ALLOWFISHING;
    public static boolean ALLOW_BOAT;
    public static boolean ALLOW_CURSED_WEAPONS;
    public static boolean ALLOW_NPC_WALKERS;
    public static boolean LOG_CHAT;
    public static boolean LOG_ITEMS;
    public static boolean GMAUDIT;
    public static boolean LOG_GAME;
    public static boolean LOG_PACKETS;
    public static String COMMUNITY_TYPE;
    public static boolean BBS_SHOW_PLAYERLIST;
    public static String BBS_DEFAULT;
    public static boolean SHOW_LEVEL_COMMUNITYBOARD;
    public static boolean SHOW_STATUS_COMMUNITYBOARD;
    public static int NAME_PAGE_SIZE_COMMUNITYBOARD;
    public static int NAME_PER_ROW_COMMUNITYBOARD;
    public static int THREAD_P_EFFECTS;
    public static int THREAD_P_GENERAL;
    public static int IO_PACKET_THREAD_CORE_SIZE;
    public static int GENERAL_PACKET_THREAD_CORE_SIZE;
    public static int GENERAL_THREAD_CORE_SIZE;
    public static int AI_MAX_THREAD;
    public static int PACKET_LIFETIME;
    public static boolean GRIDS_ALWAYS_ON;
    public static int GRID_NEIGHBOR_TURNON_TIME;
    public static int GRID_NEIGHBOR_TURNOFF_TIME;
    public static int GEODATA;
    public static boolean FORCE_GEODATA;
    public static boolean USE_SAY_FILTER;
	public static String CHAT_FILTER_CHARS;
	public static String CHAT_FILTER_PUNISHMENT;
	public static int CHAT_FILTER_PUNISHMENT_PARAM;
	public static int CHAT_FILTER_PUNISHMENT_KARMA;
	public static List<String> FILTER_LIST;
	// ---------------------------------------------------
    // Configuration values not found in config files
    // ---------------------------------------------------
	public static boolean USE_3D_MAP;
	public static int PATH_NODE_RADIUS;
    public static int NEW_NODE_ID;
    public static int SELECTED_NODE_ID;
    public static int LINKED_NODE_ID;
    public static String NEW_NODE_TYPE;
    public static boolean COUNT_PACKETS = false;
    public static boolean DUMP_PACKET_COUNTS = false;
    public static int DUMP_INTERVAL_SECONDS = 60;
    public static int MINIMUM_UPDATE_DISTANCE;
    public static int MINIMUN_UPDATE_TIME;
    public static boolean CHECK_KNOWN;
    public static int KNOWNLIST_FORGET_DELAY;
	// ============================================================

    public static void loadOptionConfig()
    {
    	try(InputStream is = new FileInputStream(new File(OPTIONS_FILE)))
	    {
		    Properties optionsSettings = new Properties();
		    optionsSettings.load(is);

		    SERVER_LIST_TESTSERVER = Boolean.parseBoolean(optionsSettings.getProperty("TestServer", "false"));
		    DEBUG = Boolean.parseBoolean(optionsSettings.getProperty("Debug", "false"));
		    ASSERT = Boolean.parseBoolean(optionsSettings.getProperty("Assert", "false"));
		    DEVELOPER = Boolean.parseBoolean(optionsSettings.getProperty("Developer", "false"));
		    ACCEPT_GEOEDITOR_CONN = Boolean.parseBoolean(optionsSettings.getProperty("AcceptGeoeditorConn", "False"));
		    EVERYBODY_HAS_ADMIN_RIGHTS = Boolean.parseBoolean(optionsSettings.getProperty("EverybodyHasAdminRights", "false"));
		    SERVER_LIST_BRACKET = Boolean.valueOf(optionsSettings.getProperty("ServerListBrackets", "false"));
		    SERVER_LIST_CLOCK = Boolean.valueOf(optionsSettings.getProperty("ServerListClock", "false"));
		    SERVER_GMONLY = Boolean.valueOf(optionsSettings.getProperty("ServerGMOnly", "false"));
		    COORD_SYNCHRONIZE = Integer.parseInt(optionsSettings.getProperty("CoordSynchronize", "-1"));
		    ZONE_TOWN = Integer.parseInt(optionsSettings.getProperty("ZoneTown", "0"));
		    DEFAULT_GLOBAL_CHAT = optionsSettings.getProperty("GlobalChat", "ON");
		    DEFAULT_TRADE_CHAT = optionsSettings.getProperty("TradeChat", "ON");
		    DEFAULT_PUNISH = Integer.parseInt(optionsSettings.getProperty("DefaultPunish", "2"));
		    DEFAULT_PUNISH_PARAM = Integer.parseInt(optionsSettings.getProperty("DefaultPunishParam", "0"));
		    BYPASS_VALIDATION = Boolean.valueOf(optionsSettings.getProperty("BypassValidation", "True"));
		    GAMEGUARD_ENFORCE = Boolean.valueOf(optionsSettings.getProperty("GameGuardEnforce", "False"));
		    GAMEGUARD_PROHIBITACTION = Boolean.valueOf(optionsSettings.getProperty("GameGuardProhibitAction", "False"));
		    DELETE_DAYS = Integer.parseInt(optionsSettings.getProperty("DeleteCharAfterDays", "7"));
		    FLOODPROTECTOR_INITIALSIZE = Integer.parseInt(optionsSettings.getProperty("FloodProtectorInitialSize", "50"));
		    ALLOW_DISCARDITEM = Boolean.valueOf(optionsSettings.getProperty("AllowDiscardItem", "True"));
		    AUTODESTROY_ITEM_AFTER = Integer.parseInt(optionsSettings.getProperty("AutoDestroyDroppedItemAfter", "0"));
		    HERB_AUTO_DESTROY_TIME = Integer.parseInt(optionsSettings.getProperty("AutoDestroyHerbTime","15"));
            LIST_PROTECTED_ITEMS = new ArrayList<>();
            for (String id : optionsSettings.getProperty("ListOfProtectedItems").split(","))
		    {
                LIST_PROTECTED_ITEMS.add(Integer.parseInt(id));
		    }
		    DESTROY_DROPPED_PLAYER_ITEM = Boolean.valueOf(optionsSettings.getProperty("DestroyPlayerDroppedItem", "false"));
		    DESTROY_EQUIPABLE_PLAYER_ITEM = Boolean.valueOf(optionsSettings.getProperty("DestroyEquipableItem", "false"));
		    SAVE_DROPPED_ITEM  = Boolean.valueOf(optionsSettings.getProperty("SaveDroppedItem", "false"));
		    EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = Boolean.valueOf(optionsSettings.getProperty("EmptyDroppedItemTableAfterLoad", "false"));
		    SAVE_DROPPED_ITEM_INTERVAL = Integer.parseInt(optionsSettings.getProperty("SaveDroppedItemInterval", "0"));
		    CLEAR_DROPPED_ITEM_TABLE = Boolean.valueOf(optionsSettings.getProperty("ClearDroppedItemTable", "false"));
		    AUTODELETE_INVALID_QUEST_DATA = Boolean.valueOf(optionsSettings.getProperty("AutoDeleteInvalidQuestData", "False"));
		    PRECISE_DROP_CALCULATION = Boolean.valueOf(optionsSettings.getProperty("PreciseDropCalculation", "True"));
		    MULTIPLE_ITEM_DROP = Boolean.valueOf(optionsSettings.getProperty("MultipleItemDrop", "True"));
		    FORCE_INVENTORY_UPDATE = Boolean.valueOf(optionsSettings.getProperty("ForceInventoryUpdate", "False"));
		    MAX_MULTISELL = Integer.parseInt(optionsSettings.getProperty("MaxMultisell", "5000"));
		    LAZY_CACHE = Boolean.valueOf(optionsSettings.getProperty("LazyCache", "False"));
		    MAX_DRIFT_RANGE = Integer.parseInt(optionsSettings.getProperty("MaxDriftRange", "300"));
		    MIN_NPC_ANIMATION = Integer.parseInt(optionsSettings.getProperty("MinNPCAnimation", "10"));
		    MAX_NPC_ANIMATION = Integer.parseInt(optionsSettings.getProperty("MaxNPCAnimation", "20"));
		    MIN_MONSTER_ANIMATION = Integer.parseInt(optionsSettings.getProperty("MinMonsterAnimation", "5"));
		    MAX_MONSTER_ANIMATION = Integer.parseInt(optionsSettings.getProperty("MaxMonsterAnimation", "20"));
		    SERVER_NEWS = Boolean.valueOf(optionsSettings.getProperty("ShowServerNews", "False"));
		    SHOW_NPC_LVL = Boolean.valueOf(optionsSettings.getProperty("ShowNpcLevel", "False"));
		    ALT_ATTACKABLE_NPCS = Boolean.parseBoolean(optionsSettings.getProperty("NpcAttackable", "False"));
		    ACTIVATE_POSITION_RECORDER = Boolean.valueOf(optionsSettings.getProperty("ActivatePositionRecorder", "False"));
		    ALLOW_WAREHOUSE = Boolean.valueOf(optionsSettings.getProperty("AllowWarehouse", "True"));
		    WAREHOUSE_CACHE = Boolean.valueOf(optionsSettings.getProperty("WarehouseCache", "False"));
		    WAREHOUSE_CACHE_TIME = Integer.parseInt(optionsSettings.getProperty("WarehouseCacheTime", "15"));
		    ALLOW_FREIGHT = Boolean.valueOf(optionsSettings.getProperty("AllowFreight", "True"));
		    ALLOW_WEAR = Boolean.valueOf(optionsSettings.getProperty("AllowWear", "False"));
		    WEAR_DELAY = Integer.parseInt(optionsSettings.getProperty("WearDelay", "5"));
		    WEAR_PRICE = Integer.parseInt(optionsSettings.getProperty("WearPrice", "10"));
		    ALLOW_LOTTERY = Boolean.valueOf(optionsSettings.getProperty("AllowLottery", "False"));
		    ALLOW_RACE = Boolean.valueOf(optionsSettings.getProperty("AllowRace", "False"));
		    ALLOW_WATER = Boolean.valueOf(optionsSettings.getProperty("AllowWater", "False"));
		    ALLOW_RENTPET = Boolean.valueOf(optionsSettings.getProperty("AllowRentPet", "False"));
		    ALLOWFISHING = Boolean.valueOf(optionsSettings.getProperty("AllowFishing", "False"));
		    ALLOW_BOAT = Boolean.valueOf(optionsSettings.getProperty("AllowBoat", "False"));
		    ALLOW_CURSED_WEAPONS = Boolean.valueOf(optionsSettings.getProperty("AllowCursedWeapons", "False"));
		    ALLOW_NPC_WALKERS = Boolean.valueOf(optionsSettings.getProperty("AllowNpcWalkers", "true"));
		    LOG_CHAT = Boolean.valueOf(optionsSettings.getProperty("LogChat", "False"));
		    LOG_ITEMS = Boolean.valueOf(optionsSettings.getProperty("LogItems", "False"));
		    GMAUDIT = Boolean.valueOf(optionsSettings.getProperty("GMAudit", "False"));
		    LOG_GAME = Boolean.parseBoolean(optionsSettings.getProperty("LogGame", "False"));
		    LOG_PACKETS = Boolean.valueOf(optionsSettings.getProperty("LogPackets", "False"));
		    COMMUNITY_TYPE = optionsSettings.getProperty("CommunityType", "off").toLowerCase();
		    BBS_SHOW_PLAYERLIST = Boolean.valueOf(optionsSettings.getProperty("BBSShowPlayerList", "false"));
		    BBS_DEFAULT = optionsSettings.getProperty("BBSDefault", "_bbshome");
		    SHOW_LEVEL_COMMUNITYBOARD = Boolean.valueOf(optionsSettings.getProperty("ShowLevelOnCommunityBoard", "False"));
		    SHOW_STATUS_COMMUNITYBOARD = Boolean.valueOf(optionsSettings.getProperty("ShowStatusOnCommunityBoard", "True"));
		    NAME_PAGE_SIZE_COMMUNITYBOARD = Integer.parseInt(optionsSettings.getProperty("NamePageSizeOnCommunityBoard", "50"));
		    NAME_PER_ROW_COMMUNITYBOARD = Integer.parseInt(optionsSettings.getProperty("NamePerRowOnCommunityBoard", "5"));
		    THREAD_P_EFFECTS = Integer.parseInt(optionsSettings.getProperty("ThreadPoolSizeEffects", "6"));
		    THREAD_P_GENERAL = Integer.parseInt(optionsSettings.getProperty("ThreadPoolSizeGeneral", "15"));
		    IO_PACKET_THREAD_CORE_SIZE =Integer.parseInt(optionsSettings.getProperty("UrgentPacketThreadCoreSize", "2"));
		    GENERAL_PACKET_THREAD_CORE_SIZE = Integer.parseInt(optionsSettings.getProperty("GeneralPacketThreadCoreSize", "4"));
		    GENERAL_THREAD_CORE_SIZE = Integer.parseInt(optionsSettings.getProperty("GeneralThreadCoreSize", "4"));
		    AI_MAX_THREAD = Integer.parseInt(optionsSettings.getProperty("AiMaxThread", "10"));
		    PACKET_LIFETIME = Integer.parseInt(optionsSettings.getProperty("PacketLifeTime", "0"));
		    GRIDS_ALWAYS_ON = Boolean.parseBoolean(optionsSettings.getProperty("GridsAlwaysOn", "False"));
		    GRID_NEIGHBOR_TURNON_TIME = Integer.parseInt(optionsSettings.getProperty("GridNeighborTurnOnTime", "30"));
		    GRID_NEIGHBOR_TURNOFF_TIME = Integer.parseInt(optionsSettings.getProperty("GridNeighborTurnOffTime", "300"));
		    GEODATA = Integer.parseInt(optionsSettings.getProperty("GeoData", "0"));
		    FORCE_GEODATA = Boolean.parseBoolean(optionsSettings.getProperty("ForceGeoData", "True"));
		    USE_SAY_FILTER = Boolean.parseBoolean(optionsSettings.getProperty("UseChatFilter", "false"));
		    CHAT_FILTER_CHARS = optionsSettings.getProperty("ChatFilterChars", "[I love L2JFrenetic]");
		    CHAT_FILTER_PUNISHMENT = optionsSettings.getProperty("ChatFilterPunishment", "off");
		    CHAT_FILTER_PUNISHMENT_PARAM = Integer.parseInt(optionsSettings.getProperty("ChatFilterPunishmentParam", "1"));
		    CHAT_FILTER_PUNISHMENT_KARMA = Integer.parseInt(optionsSettings.getProperty("ChatFilterPunishmentKarma", "1000"));
		    if (USE_SAY_FILTER)
		    {
                FILTER_LIST = new ArrayList<>();
		    	try
		    	{
		    		lnr = new LineNumberReader(new BufferedReader(new FileReader(new File(CHAT_FILTER_FILE))));
		    		String line = null;
		    		while ((line = lnr.readLine()) != null)
		    		{
		    			if (line.trim().length() == 0 || line.startsWith("#"))
		    			{
		    				continue;
		    			}
		    			FILTER_LIST.add(line.trim());
		    		}
		    		_log.info("Chat Filter: Loaded " + FILTER_LIST.size() + " words");
		    	}
		    	catch (Exception e)
		    	{
		    		e.printStackTrace();
		    		throw new Error("Failed to Load " + CHAT_FILTER_FILE + " File.");
		    	}
		    }
            // ---------------------------------------------------
            // Configuration values not found in config files
            // ---------------------------------------------------
            USE_3D_MAP = Boolean.valueOf(optionsSettings.getProperty("Use3DMap", "False"));
            PATH_NODE_RADIUS = Integer.parseInt(optionsSettings.getProperty("PathNodeRadius", "50"));
            NEW_NODE_ID = Integer.parseInt(optionsSettings.getProperty("NewNodeId", "7952"));
            SELECTED_NODE_ID = Integer.parseInt(optionsSettings.getProperty("NewNodeId", "7952"));
            LINKED_NODE_ID = Integer.parseInt(optionsSettings.getProperty("NewNodeId", "7952"));
            NEW_NODE_TYPE = optionsSettings.getProperty("NewNodeType", "npc");
            COUNT_PACKETS = Boolean.valueOf(optionsSettings.getProperty("CountPacket", "false"));
            DUMP_PACKET_COUNTS = Boolean.valueOf(optionsSettings.getProperty("DumpPacketCounts", "false"));
            DUMP_INTERVAL_SECONDS = Integer.parseInt(optionsSettings.getProperty("PacketDumpInterval", "60"));
            MINIMUM_UPDATE_DISTANCE = Integer.parseInt(optionsSettings.getProperty("MaximumUpdateDistance", "50"));
            MINIMUN_UPDATE_TIME = Integer.parseInt(optionsSettings.getProperty("MinimumUpdateTime", "500"));
            CHECK_KNOWN = Boolean.valueOf(optionsSettings.getProperty("CheckKnownList", "false"));
            KNOWNLIST_FORGET_DELAY = Integer.parseInt(optionsSettings.getProperty("KnownListForgetDelay", "10000"));
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		    throw new Error("Failed to Load " + OPTIONS_FILE + " File.");
		}
    }

	// --------------------------------------------- //
    // -              OTHER PROPERTIES             - //
    // --------------------------------------------- //
    // ============================================================
    public static int WYVERN_SPEED;
    public static int STRIDER_SPEED;
    public static boolean ALLOW_WYVERN_UPGRADER;
    public static boolean EFFECT_CANCELING;
    public static boolean ALLOW_GUARDS;
    public static boolean DEEPBLUE_DROP_RULES;
    public static int INVENTORY_MAXIMUM_NO_DWARF;
    public static int INVENTORY_MAXIMUM_DWARF;
    public static int INVENTORY_MAXIMUM_GM;
    public static int MAX_ITEM_IN_PACKET;
    public static int WAREHOUSE_SLOTS_NO_DWARF;
    public static int WAREHOUSE_SLOTS_DWARF;
    public static int WAREHOUSE_SLOTS_CLAN;
    public static int FREIGHT_SLOTS;
    public static double HP_REGEN_MULTIPLIER;
    public static double MP_REGEN_MULTIPLIER;
    public static double CP_REGEN_MULTIPLIER;
    public static double RAID_HP_REGEN_MULTIPLIER;
    public static double RAID_MP_REGEN_MULTIPLIER;
    public static double RAID_DEFENCE_MULTIPLIER;
    public static float RAID_MIN_RESPAWN_MULTIPLIER;
    public static float RAID_MAX_RESPAWN_MULTIPLIER;
    public static double RAID_MINION_RESPAWN_TIMER;
    public static int UNSTUCK_INTERVAL;
    public static int PLAYER_FAKEDEATH_UP_PROTECTION;
    public static String PARTY_XP_CUTOFF_METHOD;
    public static int PARTY_XP_CUTOFF_LEVEL;
    public static double PARTY_XP_CUTOFF_PERCENT;
    public static double RESPAWN_RESTORE_CP;
    public static double RESPAWN_RESTORE_HP;
    public static double RESPAWN_RESTORE_MP;
    public static boolean RESPAWN_RANDOM_ENABLED;
    public static int RESPAWN_RANDOM_MAX_OFFSET;
    public static int MAX_PVTSTORE_SLOTS_DWARF;
    public static int MAX_PVTSTORE_SLOTS_OTHER;
    public static boolean STORE_SKILL_COOLTIME;
    public static List<Integer> FORBIDDEN_RAID_SKILLS_LIST;
    public static List<Integer> LIST_PET_RENT_NPC;
    public static boolean ANNOUNCE_MAMMON_SPAWN;
    public static boolean ALT_PRIVILEGES_ADMIN;
    public static boolean ALT_PRIVILEGES_SECURE_CHECK;
    public static int ALT_PRIVILEGES_DEFAULT_LEVEL;
    public static boolean PETITIONING_ALLOWED;
    public static int MAX_PETITIONS_PER_PLAYER;
    public static int MAX_PETITIONS_PENDING;
    public static boolean JAIL_IS_PVP;
    public static boolean JAIL_DISABLE_CHAT;
    public static boolean JAIL_SET_PARA;
	public static boolean JAIL_DISABLE_TRANSACTION;
    public static int DEATH_PENALTY_CHANCE;
    public static int AUGSKILL_CHANCE;
    public static int SOUL_CRYSTAL_BREAK_CHANCE;
    public static int SOUL_CRYSTAL_LEVEL_CHANCE;
    public static boolean ALLOW_RAID_LETHAL;
	public static boolean ALLOW_LETHAL_PROTECTION_MOBS;
	public static List<Integer> LIST_LETHAL_PROTECTED_MOBS;
    public static List<Integer> LIST_NONDROPPABLE_ITEMS;
	// ============================================================

    public static void loadOtherConfig()
    {
	    try(InputStream is = new FileInputStream(new File(OTHER_FILE)))
	    {
	        Properties otherSettings = new Properties();
	        otherSettings.load(is);

	        WYVERN_SPEED = Integer.parseInt(otherSettings.getProperty("WyvernSpeed", "100"));
	        STRIDER_SPEED = Integer.parseInt(otherSettings.getProperty("StriderSpeed", "80"));
	        ALLOW_WYVERN_UPGRADER = Boolean.valueOf(otherSettings.getProperty("AllowWyvernUpgrader", "False"));
	        EFFECT_CANCELING = Boolean.valueOf(otherSettings.getProperty("CancelLesserEffect", "True"));
	        ALLOW_GUARDS = Boolean.valueOf(otherSettings.getProperty("AllowGuards", "False"));
	        DEEPBLUE_DROP_RULES = Boolean.parseBoolean(otherSettings.getProperty("UseDeepBlueDropRules", "True"));
	        INVENTORY_MAXIMUM_NO_DWARF  = Integer.parseInt(otherSettings.getProperty("MaximumSlotsForNoDwarf", "80"));
	        INVENTORY_MAXIMUM_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumSlotsForDwarf", "100"));
	        INVENTORY_MAXIMUM_GM  = Integer.parseInt(otherSettings.getProperty("MaximumSlotsForGMPlayer", "250"));
	        MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, Math.max(INVENTORY_MAXIMUM_DWARF, INVENTORY_MAXIMUM_GM));
	        WAREHOUSE_SLOTS_NO_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumWarehouseSlotsForNoDwarf", "100"));
	        WAREHOUSE_SLOTS_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumWarehouseSlotsForDwarf", "120"));
	        WAREHOUSE_SLOTS_CLAN = Integer.parseInt(otherSettings.getProperty("MaximumWarehouseSlotsForClan", "150"));
	        FREIGHT_SLOTS = Integer.parseInt(otherSettings.getProperty("MaximumFreightSlots", "20"));
	        HP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("HpRegenMultiplier", "100")) /100;
	        MP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("MpRegenMultiplier", "100")) /100;
	        CP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("CpRegenMultiplier", "100")) /100;
	        RAID_HP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("RaidHpRegenMultiplier", "100")) /100;
	        RAID_MP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("RaidMpRegenMultiplier", "100")) /100;
	        RAID_DEFENCE_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("RaidDefenceMultiplier", "100")) /100;
	        RAID_MIN_RESPAWN_MULTIPLIER = Float.parseFloat(otherSettings.getProperty("RaidMinRespawnMultiplier", "1.0"));
	        RAID_MAX_RESPAWN_MULTIPLIER = Float.parseFloat(otherSettings.getProperty("RaidMaxRespawnMultiplier", "1.0"));
	        RAID_MINION_RESPAWN_TIMER = Integer.parseInt(otherSettings.getProperty("RaidMinionRespawnTime", "300000"));
	        UNSTUCK_INTERVAL = Integer.parseInt(otherSettings.getProperty("UnstuckInterval", "300"));
	        PLAYER_FAKEDEATH_UP_PROTECTION = Integer.parseInt(otherSettings.getProperty("PlayerFakeDeathUpProtection", "0"));
	        PARTY_XP_CUTOFF_METHOD  = otherSettings.getProperty("PartyXpCutoffMethod", "percentage");
	        PARTY_XP_CUTOFF_PERCENT = Double.parseDouble(otherSettings.getProperty("PartyXpCutoffPercent", "3."));
	        PARTY_XP_CUTOFF_LEVEL   = Integer.parseInt(otherSettings.getProperty("PartyXpCutoffLevel", "30"));
	        RESPAWN_RESTORE_CP = Double.parseDouble(otherSettings.getProperty("RespawnRestoreCP", "0")) / 100;
	        RESPAWN_RESTORE_HP = Double.parseDouble(otherSettings.getProperty("RespawnRestoreHP", "70")) / 100;
	        RESPAWN_RESTORE_MP = Double.parseDouble(otherSettings.getProperty("RespawnRestoreMP", "70")) / 100;
	        RESPAWN_RANDOM_ENABLED = Boolean.parseBoolean(otherSettings.getProperty("RespawnRandomInTown", "False"));
	        RESPAWN_RANDOM_MAX_OFFSET = Integer.parseInt(otherSettings.getProperty("RespawnRandomMaxOffset", "50"));
	        MAX_PVTSTORE_SLOTS_DWARF = Integer.parseInt(otherSettings.getProperty("MaxPvtStoreSlotsDwarf", "5"));
	        MAX_PVTSTORE_SLOTS_OTHER = Integer.parseInt(otherSettings.getProperty("MaxPvtStoreSlotsOther", "4"));
	        STORE_SKILL_COOLTIME = Boolean.parseBoolean(otherSettings.getProperty("StoreSkillCooltime", "true"));
            FORBIDDEN_RAID_SKILLS_LIST = new ArrayList<>();
	        for (String id : otherSettings.getProperty("ForbiddenRaidSkills",  "1064,100").split(","))
	        {
	            FORBIDDEN_RAID_SKILLS_LIST.add(Integer.parseInt(id.trim()));
	        }
	        LIST_PET_RENT_NPC = new ArrayList<>();
	        for (String id : otherSettings.getProperty("ListPetRentNpc", "30827").split(","))
	        {
	            LIST_PET_RENT_NPC.add(Integer.parseInt(id));
	        }
	        ANNOUNCE_MAMMON_SPAWN = Boolean.parseBoolean(otherSettings.getProperty("AnnounceMammonSpawn", "True"));
	        ALT_PRIVILEGES_ADMIN = Boolean.parseBoolean(otherSettings.getProperty("AltPrivilegesAdmin", "False"));
	        ALT_PRIVILEGES_SECURE_CHECK  = Boolean.parseBoolean(otherSettings.getProperty("AltPrivilegesSecureCheck", "True"));
	        ALT_PRIVILEGES_DEFAULT_LEVEL = Integer.parseInt(otherSettings.getProperty("AltPrivilegesDefaultLevel", "100"));
	        PETITIONING_ALLOWED = Boolean.parseBoolean(otherSettings.getProperty("PetitioningAllowed", "True"));
	        MAX_PETITIONS_PER_PLAYER = Integer.parseInt(otherSettings.getProperty("MaxPetitionsPerPlayer", "5"));
	        MAX_PETITIONS_PENDING = Integer.parseInt(otherSettings.getProperty("MaxPetitionsPending", "25"));
	        JAIL_IS_PVP = Boolean.valueOf(otherSettings.getProperty("JailIsPvp", "True"));
	        JAIL_DISABLE_CHAT = Boolean.valueOf(otherSettings.getProperty("JailDisableChat", "True"));
	        JAIL_SET_PARA = Boolean.valueOf(otherSettings.getProperty("JailParaPlayer", "False"));
	        JAIL_DISABLE_TRANSACTION = Boolean.parseBoolean(otherSettings.getProperty("JailDisableTransaction", "False"));
	        DEATH_PENALTY_CHANCE = Integer.parseInt(otherSettings.getProperty("DeathPenaltyChance", "20"));
	        AUGSKILL_CHANCE = Integer.parseInt(otherSettings.getProperty("AugSkillChance", "11"));
	        SOUL_CRYSTAL_BREAK_CHANCE = Integer.parseInt(otherSettings.getProperty("SoulCrystalBreakChance", "10"));
	        SOUL_CRYSTAL_LEVEL_CHANCE = Integer.parseInt(otherSettings.getProperty("SoulCrystalLevelChance", "32"));
	        ALLOW_RAID_LETHAL = Boolean.parseBoolean(otherSettings.getProperty("AllowLethalOnRaids", "False"));
			ALLOW_LETHAL_PROTECTION_MOBS = Boolean.parseBoolean(otherSettings.getProperty("AllowLethalProtectionMobs", "False"));
			LIST_LETHAL_PROTECTED_MOBS = new ArrayList<>();
			for(String id : otherSettings.getProperty("LethalProtectedMobs", "").split(","))
			{
				LIST_LETHAL_PROTECTED_MOBS.add(Integer.parseInt(id));
			}
	        LIST_NONDROPPABLE_ITEMS = new ArrayList<>();
	        for (String id : otherSettings.getProperty("ListOfNonDroppableItems", "1147,425,1146,461,10,2368,7,6,2370,2369,5598").split(","))
	        {
	            LIST_NONDROPPABLE_ITEMS.add(Integer.parseInt(id));
	        }
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        throw new Error("Failed to Load "+OTHER_FILE+" File.");
	    }
    }

	// --------------------------------------------- //
    // -               PVP PROPERTIES              - //
    // --------------------------------------------- //
    // ============================================================
    public static int KARMA_MIN_KARMA;
    public static int KARMA_MAX_KARMA;
    public static int KARMA_XP_DIVIDER;
    public static int KARMA_LOST_BASE;
    public static boolean KARMA_DROP_GM;
    public static List<Integer> KARMA_LIST_NONDROPPABLE_PET_ITEMS;
    public static List<Integer> KARMA_LIST_NONDROPPABLE_ITEMS;
    public static int KARMA_PK_LIMIT;
    public static boolean KARMA_AWARD_PK_KILL;
    public static int PVP_NORMAL_TIME;
    public static int PVP_PVP_TIME;
    public static boolean CUSTOM_FIGHT_STATS;
	public static boolean ANNOUNCE_PK_PVP;
	public static boolean ANNOUNCE_PK_PVP_NORMAL_MESSAGE;
	public static String ANNOUNCE_PK_MSG;
	public static String ANNOUNCE_PVP_MSG;
	public static boolean TRADE_CHAT_PVP;
	public static int TRADE_PVP_AMOUNT;
	public static boolean SHOUT_CHAT_PVP;
	public static int SHOUT_PVP_AMOUNT;
    public static boolean PVP_COLOR_SYSTEM_ENABLED;
    public static Map<Integer, Integer> PVP_COLOR_LIST;
    public static boolean PK_COLOR_SYSTEM_ENABLED;
    public static Map<Integer, Integer> PK_COLOR_LIST;
    public static boolean ALLOW_PVP_REWARD;
    public static int PVP_REWARD_ITEM;
    public static int PVP_REWARD_COUNT;
    public static boolean ALLOW_PK_REWARD;
    public static int PK_REWARD_ITEM;
    public static int PK_REWARD_COUNT;
    public static boolean DEFAULT_PK_SYSTEM;
    public static boolean CUSTOM_PK_SYSTEM;
    // ============================================================

    public static void loadPvPConfig()
    {
	    try(InputStream is = new FileInputStream(new File(PVP_FILE)))
	    {
	        Properties pvpSettings = new Properties();
	        pvpSettings.load(is);

	        KARMA_MIN_KARMA = Integer.parseInt(pvpSettings.getProperty("MinKarma", "240"));
	        KARMA_MAX_KARMA = Integer.parseInt(pvpSettings.getProperty("MaxKarma", "10000"));
	        KARMA_XP_DIVIDER = Integer.parseInt(pvpSettings.getProperty("XPDivider", "260"));
	        KARMA_LOST_BASE = Integer.parseInt(pvpSettings.getProperty("BaseKarmaLost", "0"));
	        KARMA_DROP_GM = Boolean.parseBoolean(pvpSettings.getProperty("CanGMDropEquipment", "false"));
	        KARMA_LIST_NONDROPPABLE_PET_ITEMS = new ArrayList<>();
	        for (String id : pvpSettings.getProperty("ListOfPetItems", "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650").split(","))
	        {
	        	KARMA_LIST_NONDROPPABLE_PET_ITEMS.add(Integer.parseInt(id));
	        }
	        KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<>();
	        for (String id : pvpSettings.getProperty("ListOfNonDroppableItems", "57,6621").split(","))
	        {
	        	KARMA_LIST_NONDROPPABLE_ITEMS.add(Integer.parseInt(id));
	        }
	        KARMA_PK_LIMIT = Integer.parseInt(pvpSettings.getProperty("MinimumPKRequiredToDrop", "5"));
	        KARMA_AWARD_PK_KILL = Boolean.parseBoolean(pvpSettings.getProperty("AwardPKKillPVPPoint", "true"));
	        PVP_NORMAL_TIME = Integer.parseInt(pvpSettings.getProperty("PvPVsNormalTime", "15000"));
	        PVP_PVP_TIME = Integer.parseInt(pvpSettings.getProperty("PvPVsPvPTime", "30000"));
	        CUSTOM_FIGHT_STATS = Boolean.parseBoolean(pvpSettings.getProperty("CustomFightStats", "False"));
			ANNOUNCE_PK_PVP = Boolean.parseBoolean(pvpSettings.getProperty("AnnouncePkPvP", "False"));
			ANNOUNCE_PK_PVP_NORMAL_MESSAGE = Boolean.parseBoolean(pvpSettings.getProperty("AnnouncePkPvPNormalMessage", "True"));
			ANNOUNCE_PK_MSG = pvpSettings.getProperty("AnnouncePkMsg", "$killer has slaughtered $target");
			ANNOUNCE_PVP_MSG = pvpSettings.getProperty("AnnouncePvpMsg", "$killer has defeated $target");
			TRADE_CHAT_PVP = Boolean.valueOf(pvpSettings.getProperty("TradeChatPvP", "false"));
			TRADE_PVP_AMOUNT = Integer.parseInt(pvpSettings.getProperty("TradePvPAmount", "1500"));
			SHOUT_CHAT_PVP = Boolean.valueOf(pvpSettings.getProperty("ShoutChatPvP", "false"));
			SHOUT_PVP_AMOUNT = Integer.parseInt(pvpSettings.getProperty("ShoutPvPAmount", "1500"));
			PVP_COLOR_SYSTEM_ENABLED = Boolean.parseBoolean(pvpSettings.getProperty("EnablePvPColorSystem", "false"));
        	if(PVP_COLOR_SYSTEM_ENABLED)
        	{
        		PVP_COLOR_LIST = new HashMap<>();
        		for(String pvp : pvpSettings.getProperty("PvpsColors", "").split(";"))
        		{
        			String[] pvps_colors = pvp.split(",");
        			if(pvps_colors.length != 2)
        			{
        				System.out.println("Invalid properties.");
        			}
        			else
        			{
        				PVP_COLOR_LIST.put(Integer.parseInt(pvps_colors[0]), Integer.decode("0x" + pvps_colors[1]));
        			}
        		}
        	}
        	PK_COLOR_SYSTEM_ENABLED = Boolean.parseBoolean(pvpSettings.getProperty("EnablePkColorSystem", "false"));
        	if(PK_COLOR_SYSTEM_ENABLED)
        	{
        		PK_COLOR_LIST = new HashMap<>();
        		for(String pk : pvpSettings.getProperty("PksColors", "").split(";"))
        		{
        			String[] pks_colors = pk.split(",");
        			if(pks_colors.length != 2)
        			{
        				System.out.println("Invalid properties.");
        			}
        			else
        			{
        				PK_COLOR_LIST.put(Integer.parseInt(pks_colors[0]), Integer.decode("0x" + pks_colors[1]));
        			}
        		}
        	}
	        ALLOW_PVP_REWARD = Boolean.parseBoolean(pvpSettings.getProperty("AllowPvpRewardSystem", "False"));
	        PVP_REWARD_ITEM = Integer.parseInt(pvpSettings.getProperty("PvpRewardItem", "57"));
	        PVP_REWARD_COUNT = Integer.parseInt(pvpSettings.getProperty("PvpRewardAmount", "1"));
	        ALLOW_PK_REWARD = Boolean.parseBoolean(pvpSettings.getProperty("AllowPkRewardSystem", "False"));
	        PK_REWARD_ITEM = Integer.parseInt(pvpSettings.getProperty("PkRewardItem", "57"));
	        PK_REWARD_COUNT = Integer.parseInt(pvpSettings.getProperty("PkRewardAmount", "1"));
	        DEFAULT_PK_SYSTEM = Boolean.parseBoolean(pvpSettings.getProperty("UseDefaultSystem", "True"));
	        CUSTOM_PK_SYSTEM = Boolean.parseBoolean(pvpSettings.getProperty("UseCustomSystem", "False"));
	     }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        throw new Error("Failed to Load "+PVP_FILE+" File.");
	    }
    }

	// --------------------------------------------- //
    // -              RATES PROPERTIES             - //
    // --------------------------------------------- //
    // ============================================================
    public static float RATE_XP;
    public static float RATE_SP;
    public static float RATE_PARTY_XP;
    public static float RATE_PARTY_SP;
    public static float RATE_DROP_ADENA;
    public static float RATE_CONSUMABLE_COST;
    public static float RATE_DROP_ITEMS;
    public static float RATE_DROP_ITEMS_BY_RAID;
    public static float RATE_DROP_SPOIL;
    public static int RATE_DROP_MANOR;
    public static float RATE_DROP_QUEST;
    public static float RATE_QUESTS_REWARD;
    public static float RATE_KARMA_EXP_LOST;
    public static float RATE_SIEGE_GUARDS_PRICE;
    public static int PLAYER_DROP_LIMIT;
    public static int PLAYER_RATE_DROP;
    public static int PLAYER_RATE_DROP_ITEM;
    public static int PLAYER_RATE_DROP_EQUIP;
    public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
    public static int KARMA_DROP_LIMIT;
    public static int KARMA_RATE_DROP;
    public static int KARMA_RATE_DROP_ITEM;
    public static int KARMA_RATE_DROP_EQUIP;
    public static int KARMA_RATE_DROP_EQUIP_WEAPON;
    public static float PET_XP_RATE;
    public static int PET_FOOD_RATE;
    public static float SINEATER_XP_RATE;
    public static float RATE_DROP_COMMON_HERBS;
    public static float RATE_DROP_MP_HP_HERBS;
    public static float RATE_DROP_GREATER_HERBS;
    public static float RATE_DROP_SUPERIOR_HERBS;
    public static float RATE_DROP_SPECIAL_HERBS;
	// ============================================================
    public static void loadRatesConfig()
    {
    	try(InputStream is = new FileInputStream(new File(RATES_FILE)))
	    {
	    	Properties ratesSettings = new Properties();
	        ratesSettings.load(is);

	        RATE_XP = Float.parseFloat(ratesSettings.getProperty("RateXp", "1.00"));
	        RATE_SP = Float.parseFloat(ratesSettings.getProperty("RateSp", "1.00"));
	        RATE_PARTY_XP = Float.parseFloat(ratesSettings.getProperty("RatePartyXp", "1.00"));
	        RATE_PARTY_SP = Float.parseFloat(ratesSettings.getProperty("RatePartySp", "1.00"));
	        RATE_DROP_ADENA = Float.parseFloat(ratesSettings.getProperty("RateDropAdena", "1.00"));
	        RATE_CONSUMABLE_COST = Float.parseFloat(ratesSettings.getProperty("RateConsumableCost", "1.00"));
	        RATE_DROP_ITEMS = Float.parseFloat(ratesSettings.getProperty("RateDropItems", "1.00"));
	        RATE_DROP_ITEMS_BY_RAID = Float.parseFloat(ratesSettings.getProperty("RateRaidDropItems", "1."));
            RATE_DROP_SPOIL = Float.parseFloat(ratesSettings.getProperty("RateDropSpoil", "1.00"));
	        RATE_DROP_MANOR = Integer.parseInt(ratesSettings.getProperty("RateDropManor", "1"));
	        RATE_DROP_QUEST = Float.parseFloat(ratesSettings.getProperty("RateDropQuest", "1.00"));
	        RATE_QUESTS_REWARD = Float.parseFloat(ratesSettings.getProperty("RateQuestsReward", "1.00"));
	        RATE_KARMA_EXP_LOST = Float.parseFloat(ratesSettings.getProperty("RateKarmaExpLost", "1.00"));
	        RATE_SIEGE_GUARDS_PRICE = Float.parseFloat(ratesSettings.getProperty("RateSiegeGuardsPrice", "1.00"));
	        PLAYER_DROP_LIMIT = Integer.parseInt(ratesSettings.getProperty("PlayerDropLimit", "3"));
	        PLAYER_RATE_DROP = Integer.parseInt(ratesSettings.getProperty("PlayerRateDrop", "5"));
	        PLAYER_RATE_DROP_ITEM = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropItem", "70"));
	        PLAYER_RATE_DROP_EQUIP = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropEquip", "25"));
	        PLAYER_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropEquipWeapon", "5"));
	        KARMA_DROP_LIMIT = Integer.parseInt(ratesSettings.getProperty("KarmaDropLimit", "10"));
	        KARMA_RATE_DROP = Integer.parseInt(ratesSettings.getProperty("KarmaRateDrop", "70"));
	        KARMA_RATE_DROP_ITEM = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropItem", "50"));
	        KARMA_RATE_DROP_EQUIP = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropEquip", "40"));
	        KARMA_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropEquipWeapon", "10"));
	        PET_XP_RATE = Float.parseFloat(ratesSettings.getProperty("PetXpRate", "1.00"));
	        PET_FOOD_RATE = Integer.parseInt(ratesSettings.getProperty("PetFoodRate", "1"));
	        SINEATER_XP_RATE = Float.parseFloat(ratesSettings.getProperty("SinEaterXpRate", "1.00"));
	        RATE_DROP_COMMON_HERBS = Float.parseFloat(ratesSettings.getProperty("RateCommonHerbs", "15.00"));
	        RATE_DROP_MP_HP_HERBS = Float.parseFloat(ratesSettings.getProperty("RateHpMpHerbs", "10.00"));
	        RATE_DROP_GREATER_HERBS = Float.parseFloat(ratesSettings.getProperty("RateGreaterHerbs", "4.00"));
	        RATE_DROP_SUPERIOR_HERBS = Float.parseFloat(ratesSettings.getProperty("RateSuperiorHerbs", "0.8"))*10;
	        RATE_DROP_SPECIAL_HERBS = Float.parseFloat(ratesSettings.getProperty("RateSpecialHerbs", "0.2"))*10;
	   }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    	throw new Error("Failed to Load " + RATES_FILE + " File.");
	    }
    }

	// --------------------------------------------- //
    // -         LOGINSERVER PROPERTIES            - //
    // --------------------------------------------- //
    // ============================================================
    public static String EXTERNAL_HOSTNAME;
    public static String INTERNAL_HOSTNAME;
    public static String LOGIN_BIND_ADDRESS;
    public static int PORT_LOGIN;
    public static int LOGIN_TRY_BEFORE_BAN;
    public static int LOGIN_BLOCK_AFTER_BAN;
    public static String GAME_SERVER_LOGIN_HOST;
    public static int GAME_SERVER_LOGIN_PORT;
    public static boolean ACCEPT_NEW_GAMESERVER;
    public static boolean SHOW_LICENCE;
    public static String DATABASE_DRIVER;
    public static String DATABASE_URL;
    public static String DATABASE_LOGIN;
    public static String DATABASE_PASSWORD;
    public static int DATABASE_MAX_CONNECTIONS;
	public static int DATABASE_MAX_IDLE_TIME;
    public static boolean AUTO_CREATE_ACCOUNTS;
    public static int IP_UPDATE_TIME;
    public static boolean DEBUG;
    public static boolean ASSERT;
    public static boolean DEVELOPER;
    public static boolean FORCE_GGAUTH;
    public static int FAST_CONNECTION_LIMIT;
    public static int NORMAL_CONNECTION_TIME;
    public static int FAST_CONNECTION_TIME;
    public static int MAX_CONNECTION_PER_IP;
	// ============================================================

    public static void loadLoginServerConfig()
    {
    	try(InputStream is = new FileInputStream(new File(LOGIN_FILE)))
    	{
    		Properties serverSettings = new Properties();
    		serverSettings.load(is);

    		EXTERNAL_HOSTNAME = serverSettings.getProperty("ExternalHostname", "localhost");
    		INTERNAL_HOSTNAME = serverSettings.getProperty("InternalHostname", "localhost");
    		LOGIN_BIND_ADDRESS = serverSettings.getProperty("LoginserverHostname", "*");
    		PORT_LOGIN = Integer.parseInt(serverSettings.getProperty("LoginserverPort", "2106"));
    		LOGIN_TRY_BEFORE_BAN = Integer.parseInt(serverSettings.getProperty("LoginTryBeforeBan", "10"));
    		LOGIN_BLOCK_AFTER_BAN = Integer.parseInt(serverSettings.getProperty("LoginBlockAfterBan", "600"));
    		GAME_SERVER_LOGIN_HOST = serverSettings.getProperty("LoginHostname","*");
    		GAME_SERVER_LOGIN_PORT = Integer.parseInt(serverSettings.getProperty("LoginPort","9013"));
    		ACCEPT_NEW_GAMESERVER = Boolean.parseBoolean(serverSettings.getProperty("AcceptNewGameServer","True"));
    		SHOW_LICENCE = Boolean.parseBoolean(serverSettings.getProperty("ShowLicence", "true"));
    		DATABASE_DRIVER = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
    		DATABASE_URL = serverSettings.getProperty("URL", "jdbc:mysql://localhost/l2jdb");
    		DATABASE_LOGIN = serverSettings.getProperty("Login", "root");
    		DATABASE_PASSWORD = serverSettings.getProperty("Password", "");
    		DATABASE_MAX_CONNECTIONS = Integer.parseInt(serverSettings.getProperty("MaximumDbConnections", "10"));
    		DATABASE_MAX_IDLE_TIME = Integer.parseInt(serverSettings.getProperty("MaximumDbIdleTime", "0"));
    		AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(serverSettings.getProperty("AutoCreateAccounts","True"));
    		IP_UPDATE_TIME = Integer.parseInt(serverSettings.getProperty("IpUpdateTime","15"));
    		DEBUG = Boolean.parseBoolean(serverSettings.getProperty("Debug", "false"));
    		ASSERT = Boolean.parseBoolean(serverSettings.getProperty("Assert", "false"));
    		DEVELOPER = Boolean.parseBoolean(serverSettings.getProperty("Developer", "false"));
    		FORCE_GGAUTH = Boolean.parseBoolean(serverSettings.getProperty("ForceGGAuth", "false"));
    		FAST_CONNECTION_LIMIT = Integer.parseInt(serverSettings.getProperty("FastConnectionLimit","15"));
    		NORMAL_CONNECTION_TIME = Integer.parseInt(serverSettings.getProperty("NormalConnectionTime","700"));
    		FAST_CONNECTION_TIME = Integer.parseInt(serverSettings.getProperty("FastConnectionTime","350"));
    		MAX_CONNECTION_PER_IP = Integer.parseInt(serverSettings.getProperty("MaxConnectionPerIP","50"));
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		throw new Error("Failed to Load " + LOGIN_FILE + " File.");
    	}
    }

	// --------------------------------------------- //
    // -          GAME SERVER PROPERTIES        - //
    // --------------------------------------------- //
    // ============================================================
    public static String GAMESERVER_HOSTNAME;
    public static int PORT_GAME;
    public static int REQUEST_ID;
    public static boolean ACCEPT_ALTERNATE_ID;
    public static File DATAPACK_ROOT;
    public static String CNAME_TEMPLATE;
    public static String PET_NAME_TEMPLATE;
    public static int MAX_CHARACTERS_NUMBER_PER_ACCOUNT;
    public static int MAXIMUM_ONLINE_USERS;
	public static int MAX_UNKNOWN_PACKETS;
	public static boolean PACKET_HANDLER_DEBUG;
	public static int MIN_PROTOCOL_REVISION;
    public static int MAX_PROTOCOL_REVISION;
	// ============================================================

    public static void loadGameServerConfig()
    {
    	try(InputStream is = new FileInputStream(new File(SERVER_FILE)))
    	{
    		Properties serverSettings = new Properties();
    		serverSettings.load(is);

    		GAMESERVER_HOSTNAME = serverSettings.getProperty("GameserverHostname");
    		PORT_GAME = Integer.parseInt(serverSettings.getProperty("GameserverPort", "7777"));
    		EXTERNAL_HOSTNAME = serverSettings.getProperty("ExternalHostname", "*");
    		INTERNAL_HOSTNAME = serverSettings.getProperty("InternalHostname", "*");
    		GAME_SERVER_LOGIN_PORT = Integer.parseInt(serverSettings.getProperty("LoginPort","9014"));
    		GAME_SERVER_LOGIN_HOST = serverSettings.getProperty("LoginHost","127.0.0.1");
    		REQUEST_ID = Integer.parseInt(serverSettings.getProperty("RequestServerID","0"));
    		ACCEPT_ALTERNATE_ID = Boolean.parseBoolean(serverSettings.getProperty("AcceptAlternateID","True"));
    		DATABASE_DRIVER = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
    		DATABASE_URL = serverSettings.getProperty("URL", "jdbc:mysql://localhost/l2jdb");
    		DATABASE_LOGIN = serverSettings.getProperty("Login", "root");
    		DATABASE_PASSWORD = serverSettings.getProperty("Password", "");
    		DATABASE_MAX_CONNECTIONS = Integer.parseInt(serverSettings.getProperty("MaximumDbConnections", "10"));
			DATABASE_MAX_IDLE_TIME = Integer.parseInt(serverSettings.getProperty("MaximumDbIdleTime", "0"));
    		DATAPACK_ROOT = new File(serverSettings.getProperty("DatapackRoot", ".")).getCanonicalFile();
    		CNAME_TEMPLATE = serverSettings.getProperty("CnameTemplate", ".*");
    		PET_NAME_TEMPLATE = serverSettings.getProperty("PetNameTemplate", ".*");
    		MAX_CHARACTERS_NUMBER_PER_ACCOUNT = Integer.parseInt(serverSettings.getProperty("CharMaxNumber", "0"));
    		MAXIMUM_ONLINE_USERS = Integer.parseInt(serverSettings.getProperty("MaximumOnlineUsers", "100"));
    		MAX_UNKNOWN_PACKETS = Integer.parseInt(serverSettings.getProperty("UnkPacketsBeforeBan", "5"));
    		PACKET_HANDLER_DEBUG = Boolean.parseBoolean(serverSettings.getProperty("PacketHandlerDebug", "False"));
    		MIN_PROTOCOL_REVISION = Integer.parseInt(serverSettings.getProperty("MinProtocolRevision", "660"));
    		MAX_PROTOCOL_REVISION = Integer.parseInt(serverSettings.getProperty("MaxProtocolRevision", "665"));
    		if (MIN_PROTOCOL_REVISION > MAX_PROTOCOL_REVISION)
    		{
    			throw new Error("MinProtocolRevision is bigger than MaxProtocolRevision in server configuration file.");
    		}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		throw new Error("Failed to Load " + SERVER_FILE + " File.");
    	}
    }

	// --------------------------------------------- //
    // -         FLOOD PROTECTOR PROPERTIES        - //
    // --------------------------------------------- //
    // ============================================================
	public static boolean FLOOD_PROTECTION;
    public static final FloodProtectorConfig FLOOD_PROTECTOR_USE_ITEM = new FloodProtectorConfig("UseItemFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_ROLL_DICE = new FloodProtectorConfig("RollDiceFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_FIREWORK = new FloodProtectorConfig("FireworkFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_ITEM_PET_SUMMON = new FloodProtectorConfig("ItemPetSummonFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_HERO_VOICE = new FloodProtectorConfig("HeroVoiceFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_GLOBAL_CHAT = new FloodProtectorConfig("GlobalChatFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_SUBCLASS = new FloodProtectorConfig("SubclassFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_DROP_ITEM = new FloodProtectorConfig("DropItemFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_SERVER_BYPASS = new FloodProtectorConfig("ServerBypassFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_MULTISELL = new FloodProtectorConfig("MultiSellFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_TRANSACTION = new FloodProtectorConfig("TransactionFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_PACKET = new FloodProtectorConfig("PacketFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_CRYSTALLIZE_ITEM = new FloodProtectorConfig("CrystallizeItemFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_TRADE_CHAT = new FloodProtectorConfig("TradeChatFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_DEPOSIT_ITEM = new FloodProtectorConfig("DepositItemFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_WITHDRAW_ITEM = new FloodProtectorConfig("WithdrawcItemFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_BANKING_SYSTEM = new FloodProtectorConfig("BankingSystemFloodProtector");
	public static final FloodProtectorConfig FLOOD_PROTECTOR_SAY_ACTION = new FloodProtectorConfig("SaySystemFloodProtector");
	// ============================================================

	public static void loadFloodConfig()
	{
    	try(InputStream is = new FileInputStream(new File(FLOODPROTECTOR_FILE)))
    	{
        	Properties FloodProtector = new Properties();
        	FloodProtector.load(is);

        	FLOOD_PROTECTION = Boolean.parseBoolean(FloodProtector.getProperty("EnableFloodProtection","True"));
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_USE_ITEM, "UseItem", "4");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_ROLL_DICE, "RollDice", "42");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_FIREWORK, "Firework", "42");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_ITEM_PET_SUMMON, "ItemPetSummon", "16");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_HERO_VOICE, "HeroVoice", "100");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_GLOBAL_CHAT, "GlobalChat", "5");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_SUBCLASS, "Subclass", "20");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_DROP_ITEM, "DropItem", "10");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_SERVER_BYPASS, "ServerBypass", "5");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_MULTISELL, "MultiSell", "1");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_TRANSACTION, "Transaction", "10");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_PACKET, "packet", "5");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_CRYSTALLIZE_ITEM, "CrystallizeItem", "10");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_TRADE_CHAT, "TradeChat", "10");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_DEPOSIT_ITEM, "DepositItem", "10");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_WITHDRAW_ITEM, "WithdrawItem", "10");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_BANKING_SYSTEM, "BankingSystem", "100");
            loadFloodProtectorConfig(FloodProtector, FLOOD_PROTECTOR_SAY_ACTION, "SayAction", "100");
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		throw new Error("Failed to Load " + FLOODPROTECTOR_FILE + " File.");
    	}
    }

	private static void loadFloodProtectorConfig(final Properties properties, final FloodProtectorConfig config, final String configString, final String defaultInterval)
	{
		config.FLOOD_PROTECTION_INTERVAL = Integer.parseInt(properties.getProperty(StringUtil.concat("FloodProtector", configString, "Interval"), defaultInterval));
		config.LOG_FLOODING = Boolean.parseBoolean(properties.getProperty(StringUtil.concat("FloodProtector", configString, "LogFlooding"), "False"));
		config.PUNISHMENT_LIMIT = Integer.parseInt(properties.getProperty(StringUtil.concat("FloodProtector", configString, "PunishmentLimit"), "0"));
		config.PUNISHMENT_TYPE = properties.getProperty(StringUtil.concat("FloodProtector", configString, "PunishmentType"), "none");
		config.PUNISHMENT_TIME = Integer.parseInt(properties.getProperty(StringUtil.concat("FloodProtector", configString, "PunishmentTime"), "0"));
	}

	// --------------------------------------------- //
    // -          ID FACTORY PROPERTIES            - //
    // --------------------------------------------- //
    // ============================================================
	public static ObjectMapType MAP_TYPE;
	public static ObjectSetType SET_TYPE;
	public static IdFactoryType IDFACTORY_TYPE;
	public static boolean BAD_ID_CHECKING;
	public enum ObjectMapType
	{
		L2ObjectHashMap,WorldObjectMap
	}
	public enum ObjectSetType
	{
		L2ObjectHashSet,WorldObjectSet
	}
    public enum IdFactoryType
    {
        Compaction,BitSet,Stack
    }
    // ============================================================

	public static void loadIdFactoryConfig()
	{
	    try(InputStream is = new FileInputStream(new File(ID_CONFIG_FILE)))
	    {
	        Properties idSettings = new Properties();
	        idSettings.load(is);

	        MAP_TYPE = ObjectMapType.valueOf(idSettings.getProperty("L2Map", "WorldObjectMap"));
	        SET_TYPE = ObjectSetType.valueOf(idSettings.getProperty("L2Set", "WorldObjectSet"));
	        IDFACTORY_TYPE = IdFactoryType.valueOf(idSettings.getProperty("IDFactory", "Compaction"));
	        BAD_ID_CHECKING = Boolean.valueOf(idSettings.getProperty("BadIdChecking", "True"));
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        throw new Error("Failed to Load "+ID_CONFIG_FILE+" File.");
	    }
	}

	// --------------------------------------------- //
    // -           SCRIPTING PROPERTIES            - //
    // --------------------------------------------- //
    // ============================================================
    public static boolean VERBOSE_LOADING;
	public static boolean ATTEMPT_COMPILATION;
	public static boolean USE_COMPILED_CACHE;
	public static boolean PURGE_ERROR_LOG;
	// ============================================================
    public static void loadScriptingConfig()
    {
	    try(InputStream is = new FileInputStream(SCRIPTING_FILE))
	    {
	    	Properties Scripting = new Properties();
	    	Scripting.load(is);

	    	VERBOSE_LOADING = Boolean.parseBoolean(Scripting.getProperty("VerboseLoading", "False"));
	    	ATTEMPT_COMPILATION =Boolean.parseBoolean(Scripting.getProperty("AttempCompilation", "True"));
	    	USE_COMPILED_CACHE = Boolean.parseBoolean(Scripting.getProperty("UseCompiledCache", "False"));
	    	PURGE_ERROR_LOG = Boolean.parseBoolean(Scripting.getProperty("PurgeErrorLog", "True"));

	    }
	    catch (Exception e)
	    {
	    	_log.warning("Could not load extensions file (" + SCRIPTING_FILE + ").");
	    }
    }

    // --------------------------------------------- //
    // -             TELNET PROPERTIES             - //
    // --------------------------------------------- //
    // ============================================================
    public static boolean IS_TELNET_ENABLED;
	// ============================================================

    public static void loadTelnetConfig()
    {
	    try(InputStream is = new FileInputStream(new File(TELNET_FILE)))
	    {
	        Properties telnetSettings = new Properties();
	        telnetSettings.load(is);

	        IS_TELNET_ENABLED = Boolean.valueOf(telnetSettings.getProperty("EnableTelnet", "false"));
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        throw new Error("Failed to Load " + TELNET_FILE + " File.");
	    }
    }

	// --------------------------------------------- //
    // -             MMOCORE PROPERTIES            - //
    // --------------------------------------------- //
    // ============================================================
    public static int MMO_SELECTOR_SLEEP_TIME;
    public static int MMO_IO_SELECTOR_THREAD_COUNT;
    public static int MMO_MAX_SEND_PER_PASS;
    public static int MMO_MAX_READ_PER_PASS;
    public static int MMO_HELPER_BUFFER_COUNT;
	// ============================================================

    public static void loadMMOCoreConfig()
    {
		try(InputStream is = new FileInputStream(new File(MMOCORE_CONFIG_FILE)))
		{
			Properties mmoSettings = new Properties();
			mmoSettings.load(is);

			MMO_SELECTOR_SLEEP_TIME = Integer.parseInt(mmoSettings.getProperty("SleepTime", "20"));
			MMO_IO_SELECTOR_THREAD_COUNT = Integer.parseInt(mmoSettings.getProperty("IOSelectorThreadCount", "2"));
			MMO_MAX_SEND_PER_PASS = Integer.parseInt(mmoSettings.getProperty("MaxSendPerPass", "12"));
			MMO_MAX_READ_PER_PASS = Integer.parseInt(mmoSettings.getProperty("MaxReadPerPass", "12"));
			MMO_HELPER_BUFFER_COUNT = Integer.parseInt(mmoSettings.getProperty("HelperBufferCount", "20"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load " + MMOCORE_CONFIG_FILE + " File.");
		}
    }

    // --------------------------------------------- //
    // -              HEXID PROPERTIES             - //
    // --------------------------------------------- //
    // ============================================================
    public static int SERVER_ID;
    public static byte[]HEX_ID;
	// ============================================================
    public static void loadHexidConfig()
    {
		try(InputStream is = new FileInputStream(HEXID_FILE))
	    {
	        Properties Settings = new Properties();
	        Settings.load(is);

	        SERVER_ID = Integer.parseInt(Settings.getProperty("ServerID"));
	        HEX_ID = new BigInteger(Settings.getProperty("HexID"), 16).toByteArray();
	    }
	    catch (Exception e)
	    {
	        _log.warning("Could not load HexID file (" + HEXID_FILE + "). Hopefully login will give us one.");
	    }
    }
    public static boolean RESERVE_HOST_ON_LOGIN = false;

	private static LineNumberReader lnr;

    /**
     * Set a new value to a game parameter from the admin console.
     * @param pName (String) : name of the parameter to change
     * @param pValue (String) : new value of the parameter
     * @return boolean : true if modification has been made
     * @link useAdminCommand
     */
    public static boolean setParameterValue(String pName, String pValue)
    {
        if (pName.equalsIgnoreCase("RateXp")) RATE_XP = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateSp")) RATE_SP = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RatePartyXp")) RATE_PARTY_XP = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RatePartySp")) RATE_PARTY_SP = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateQuestsReward")) RATE_QUESTS_REWARD = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateDropAdena")) RATE_DROP_ADENA = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateConsumableCost")) RATE_CONSUMABLE_COST = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateDropItems")) RATE_DROP_ITEMS = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateRaidDropItems")) RATE_DROP_ITEMS_BY_RAID = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateDropSpoil")) RATE_DROP_SPOIL = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateDropManor")) RATE_DROP_MANOR = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("RateDropQuest")) RATE_DROP_QUEST = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateKarmaExpLost")) RATE_KARMA_EXP_LOST = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("RateSiegeGuardsPrice")) RATE_SIEGE_GUARDS_PRICE = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("PlayerDropLimit")) PLAYER_DROP_LIMIT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PlayerRateDrop")) PLAYER_RATE_DROP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PlayerRateDropItem")) PLAYER_RATE_DROP_ITEM = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PlayerRateDropEquip")) PLAYER_RATE_DROP_EQUIP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PlayerRateDropEquipWeapon")) PLAYER_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("KarmaDropLimit")) KARMA_DROP_LIMIT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("KarmaRateDrop")) KARMA_RATE_DROP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("KarmaRateDropItem")) KARMA_RATE_DROP_ITEM = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("KarmaRateDropEquip")) KARMA_RATE_DROP_EQUIP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("KarmaRateDropEquipWeapon")) KARMA_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AutoDestroyDroppedItemAfter")) AUTODESTROY_ITEM_AFTER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("DestroyPlayerDroppedItem")) DESTROY_DROPPED_PLAYER_ITEM = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("DestroyEquipableItem")) DESTROY_EQUIPABLE_PLAYER_ITEM = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("SaveDroppedItem")) SAVE_DROPPED_ITEM = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("EmptyDroppedItemTableAfterLoad")) EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("SaveDroppedItemInterval")) SAVE_DROPPED_ITEM_INTERVAL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ClearDroppedItemTable")) CLEAR_DROPPED_ITEM_TABLE = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("PreciseDropCalculation")) PRECISE_DROP_CALCULATION = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("MultipleItemDrop")) MULTIPLE_ITEM_DROP = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("CoordSynchronize")) COORD_SYNCHRONIZE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("DeleteCharAfterDays")) DELETE_DAYS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AllowDiscardItem")) ALLOW_DISCARDITEM = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AllowFreight")) ALLOW_FREIGHT = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AllowWarehouse")) ALLOW_WAREHOUSE = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AllowWear")) ALLOW_WEAR = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("WearDelay")) WEAR_DELAY = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("WearPrice")) WEAR_PRICE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AllowWater")) ALLOW_WATER = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AllowRentPet")) ALLOW_RENTPET = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AllowBoat")) ALLOW_BOAT = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AllowCursedWeapons")) ALLOW_CURSED_WEAPONS = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AllowManor")) ALLOW_MANOR = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AllowNpcWalkers")) ALLOW_NPC_WALKERS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("BypassValidation")) BYPASS_VALIDATION = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("CommunityType")) COMMUNITY_TYPE = pValue.toLowerCase();
        else if (pName.equalsIgnoreCase("BBSDefault")) BBS_DEFAULT = pValue;
        else if (pName.equalsIgnoreCase("ShowLevelOnCommunityBoard")) SHOW_LEVEL_COMMUNITYBOARD = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("ShowStatusOnCommunityBoard")) SHOW_STATUS_COMMUNITYBOARD = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("NamePageSizeOnCommunityBoard")) NAME_PAGE_SIZE_COMMUNITYBOARD = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("BBSShowPlayerList")) BBS_SHOW_PLAYERLIST = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("NamePerRowOnCommunityBoard")) NAME_PER_ROW_COMMUNITYBOARD = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ShowServerNews")) SERVER_NEWS = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("ShowNpcLevel")) SHOW_NPC_LVL = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("ForceInventoryUpdate")) FORCE_INVENTORY_UPDATE = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AutoDeleteInvalidQuestData")) AUTODELETE_INVALID_QUEST_DATA = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("MaximumOnlineUsers")) MAXIMUM_ONLINE_USERS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("UnknownPacketsBeforeBan")) MAX_UNKNOWN_PACKETS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ZoneTown")) ZONE_TOWN = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumUpdateDistance")) MINIMUM_UPDATE_DISTANCE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MinimumUpdateTime")) MINIMUN_UPDATE_TIME = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("CheckKnownList")) CHECK_KNOWN = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("KnownListForgetDelay")) KNOWNLIST_FORGET_DELAY = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("UseDeepBlueDropRules")) DEEPBLUE_DROP_RULES = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AllowGuards")) ALLOW_GUARDS = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("CancelLesserEffect")) EFFECT_CANCELING = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("WyvernSpeed")) WYVERN_SPEED = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("StriderSpeed")) STRIDER_SPEED = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumSlotsForNoDwarf")) INVENTORY_MAXIMUM_NO_DWARF = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumSlotsForDwarf")) INVENTORY_MAXIMUM_DWARF = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumSlotsForGMPlayer")) INVENTORY_MAXIMUM_GM = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumWarehouseSlotsForNoDwarf")) WAREHOUSE_SLOTS_NO_DWARF = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumWarehouseSlotsForDwarf")) WAREHOUSE_SLOTS_DWARF = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumWarehouseSlotsForClan")) WAREHOUSE_SLOTS_CLAN = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaximumFreightSlots")) FREIGHT_SLOTS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantChanceWeapon")) ENCHANT_CHANCE_WEAPON = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantChanceArmor")) ENCHANT_CHANCE_ARMOR = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantChanceJewelry")) ENCHANT_CHANCE_JEWELRY = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantMaxWeapon")) ENCHANT_MAX_WEAPON = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantMaxArmor")) ENCHANT_MAX_ARMOR = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantMaxJewelry")) ENCHANT_MAX_JEWELRY = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantSafeMax")) ENCHANT_SAFE_MAX = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnchantSafeMaxFull")) ENCHANT_SAFE_MAX_FULL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("GMOverEnchant")) GM_OVER_ENCHANT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("HpRegenMultiplier")) HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("MpRegenMultiplier")) MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("CpRegenMultiplier")) CP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("RaidHpRegenMultiplier")) RAID_HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("RaidMpRegenMultiplier")) RAID_MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("RaidDefenceMultiplier")) RAID_DEFENCE_MULTIPLIER = Double.parseDouble(pValue) /100;
        else if (pName.equalsIgnoreCase("RaidMinionRespawnTime")) RAID_MINION_RESPAWN_TIMER =Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("StartingAdena")) STARTING_ADENA = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("UnstuckInterval")) UNSTUCK_INTERVAL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PlayerSpawnProtection")) PLAYER_SPAWN_PROTECTION = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PlayerFakeDeathUpProtection")) PLAYER_FAKEDEATH_UP_PROTECTION = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PartyXpCutoffMethod")) PARTY_XP_CUTOFF_METHOD = pValue;
        else if (pName.equalsIgnoreCase("PartyXpCutoffPercent")) PARTY_XP_CUTOFF_PERCENT = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("PartyXpCutoffLevel")) PARTY_XP_CUTOFF_LEVEL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("RespawnRestoreCP")) RESPAWN_RESTORE_CP = Double.parseDouble(pValue) / 100;
        else if (pName.equalsIgnoreCase("RespawnRestoreHP")) RESPAWN_RESTORE_HP = Double.parseDouble(pValue) / 100;
        else if (pName.equalsIgnoreCase("RespawnRestoreMP")) RESPAWN_RESTORE_MP = Double.parseDouble(pValue) / 100;
        else if (pName.equalsIgnoreCase("MaxPvtStoreSlotsDwarf")) MAX_PVTSTORE_SLOTS_DWARF = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaxPvtStoreSlotsOther")) MAX_PVTSTORE_SLOTS_OTHER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("StoreSkillCooltime")) STORE_SKILL_COOLTIME = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AnnounceMammonSpawn")) ANNOUNCE_MAMMON_SPAWN = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltGameTiredness")) ALT_GAME_TIREDNESS = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltGameCreation")) ALT_GAME_CREATION = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltGameCreationSpeed")) ALT_GAME_CREATION_SPEED = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("AltGameCreationXpRate")) ALT_GAME_CREATION_XP_RATE = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("AltGameCreationSpRate")) ALT_GAME_CREATION_SP_RATE = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("AltWeightLimit")) ALT_WEIGHT_LIMIT = Double.parseDouble(pValue);
        else if (pName.equalsIgnoreCase("EnableFallingDamage")) ENABLE_FALLING_DAMAGE = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltBlacksmithUseRecipes")) ALT_BLACKSMITH_USE_RECIPES = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltGameSkillLearn")) ALT_GAME_SKILL_LEARN = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("RemoveCastleCirclets")) REMOVE_CASTLE_CIRCLETS = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("ReputationScorePerKill")) ALT_REPUTATION_SCORE_PER_KILL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("Delevel")) ALT_GAME_DELEVEL = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltGameMobAttackAI")) ALT_GAME_MOB_ATTACK_AI = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltMobAgroInPeaceZone")) ALT_MOB_AGRO_IN_PEACEZONE = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltGameExponentXp")) ALT_GAME_EXPONENT_XP = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("AltGameExponentSp")) ALT_GAME_EXPONENT_SP = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("AllowClassMasters")) ALLOW_CLASS_MASTERS = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltGameFreights")) ALT_GAME_FREIGHTS = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltGameFreightPrice")) ALT_GAME_FREIGHT_PRICE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AltPartyRange")) ALT_PARTY_RANGE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AltPartyRange2")) ALT_PARTY_RANGE2 = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("CraftingEnabled")) IS_CRAFTING_ENABLED = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("LifeCrystalNeeded")) LIFE_CRYSTAL_NEEDED = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("SpBookNeeded")) SP_BOOK_NEEDED = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AutoLoot")) AUTO_LOOT = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AutoLootRaids")) AUTO_LOOT_RAIDS = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AutoLootHerbs")) AUTO_LOOT_HERBS = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("MaxPAtkSpeed")) MAX_PATK_SPEED = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaxMAtkSpeed")) MAX_MATK_SPEED = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AltKarmaPlayerCanBeKilledInPeaceZone")) ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltKarmaPlayerCanShop")) ALT_GAME_KARMA_PLAYER_CAN_SHOP = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltKarmaPlayerCanUseGK")) ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltKarmaPlayerCanTeleport")) ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltFlagedPlayerCanUseGK")) ALT_GAME_FLAGED_PLAYER_CAN_USE_GK = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltKarmaPlayerCanTrade")) ALT_GAME_KARMA_PLAYER_CAN_TRADE = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltKarmaPlayerCanUseWareHouse")) ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltRequireCastleForDawn")) GAME_REQUIRE_CASTLE_DAWN = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltRequireClanCastle")) GAME_REQUIRE_CLAN_CASTLE = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltFreeTeleporting")) ALT_GAME_FREE_TELEPORT = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("AltSubClassWithoutQuests")) ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltSubWithoutFates")) ALT_SUB_WITHOUT_FATES = Boolean.valueOf(pValue);
		else if (pName.equalsIgnoreCase("AltMaxSubNumber")) ALT_MAX_SUBCLASS = Integer.parseInt(pValue);
	    else if (pName.equalsIgnoreCase("AltNewCharAlwaysIsNewbie")) ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AltMembersCanWithdrawFromClanWH")) MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("DwarfRecipeLimit")) DWARF_RECIPE_LIMIT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("CommonRecipeLimit")) COMMON_RECIPE_LIMIT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionEnable")) L2JMOD_CHAMPION_ENABLE =  Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("ChampionFrequency")) L2JMOD_CHAMPION_FREQUENCY = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionMinLevel")) L2JMOD_CHAMP_MIN_LVL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionMaxLevel")) L2JMOD_CHAMP_MAX_LVL = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionHp")) L2JMOD_CHAMPION_HP = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionHpRegen")) L2JMOD_CHAMPION_HP_REGEN = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("ChampionRewards")) L2JMOD_CHAMPION_REWARDS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionAdenasRewards")) L2JMOD_CHAMPION_ADENAS_REWARDS = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("ChampionAtk")) L2JMOD_CHAMPION_ATK = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("ChampionSpdAtk")) L2JMOD_CHAMPION_SPD_ATK = Float.parseFloat(pValue);
        else if (pName.equalsIgnoreCase("ChampionRewardItem")) L2JMOD_CHAMPION_REWARD = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionRewardItemID")) L2JMOD_CHAMPION_REWARD_ID = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ChampionRewardItemQty")) L2JMOD_CHAMPION_REWARD_QTY = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("AllowWedding")) L2JMOD_ALLOW_WEDDING = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("WeddingPrice")) L2JMOD_WEDDING_PRICE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("WeddingPunishInfidelity")) L2JMOD_WEDDING_PUNISH_INFIDELITY = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("WeddingTeleport")) L2JMOD_WEDDING_TELEPORT = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("WeddingTeleportPrice")) L2JMOD_WEDDING_TELEPORT_PRICE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("WeddingTeleportDuration")) L2JMOD_WEDDING_TELEPORT_DURATION = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("WeddingAllowSameSex")) L2JMOD_WEDDING_SAMESEX = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("WeddingFormalWear")) L2JMOD_WEDDING_FORMALWEAR = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("WeddingDivorceCosts")) L2JMOD_WEDDING_DIVORCE_COSTS = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("EnableRndSpawns")) ALLOW_RND_SPAWN = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("TvTEventEnabled")) TVT_EVENT_ENABLED = Boolean.parseBoolean(pValue);
        else if (pName.equalsIgnoreCase("TvTEventInterval")) TVT_EVENT_INTERVAL = pValue.split(",");
        else if (pName.equalsIgnoreCase("TvTEventParticipationTime")) TVT_EVENT_PARTICIPATION_TIME = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("TvTEventRunningTime")) TVT_EVENT_RUNNING_TIME = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("TvTEventParticipationNpcId")) TVT_EVENT_PARTICIPATION_NPC_ID = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("ServerName")) SERVERNAME = (pValue);
        else if (pName.equalsIgnoreCase("MinKarma")) KARMA_MIN_KARMA = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("MaxKarma")) KARMA_MAX_KARMA = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("XPDivider")) KARMA_XP_DIVIDER = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("BaseKarmaLost")) KARMA_LOST_BASE = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("CanGMDropEquipment")) KARMA_DROP_GM = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("AwardPKKillPVPPoint")) KARMA_AWARD_PK_KILL = Boolean.valueOf(pValue);
        else if (pName.equalsIgnoreCase("MinimumPKRequiredToDrop")) KARMA_PK_LIMIT = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PvPVsNormalTime")) PVP_NORMAL_TIME = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("PvPVsPvPTime")) PVP_PVP_TIME = Integer.parseInt(pValue);
        else if (pName.equalsIgnoreCase("GlobalChat")) DEFAULT_GLOBAL_CHAT = pValue;
        else if (pName.equalsIgnoreCase("TradeChat"))  DEFAULT_TRADE_CHAT = pValue;
        else if (pName.equalsIgnoreCase("MenuStyle"))  GM_ADMIN_MENU_STYLE = pValue;
        else if (pName.equalsIgnoreCase("WeaponType"))
		{
			ALT_DAGGER	= pValue.equalsIgnoreCase("DAGGER");
			ALT_BOW	= pValue.equalsIgnoreCase("BOW");
			ALT_BLUNT = pValue.equalsIgnoreCase("BLUNT");
			ALT_DUALFIST = pValue.equalsIgnoreCase("DUALFIST");
			ALT_DUAL = pValue.equalsIgnoreCase("DUAL");
			ALT_SWORD = pValue.equalsIgnoreCase("SWORD");
			ALT_POLE = pValue.equalsIgnoreCase("POLE");
		}
        else return false;
        return true;
    }

    // it has no instances
    private Config() {}

    /**
     * Save hexadecimal ID of the server in the properties file.
     * @param string (String) : hexadecimal ID of the server to store
     * @link LoginServerThread
     */
    public static void saveHexid(int serverId, String string)
    {
        Config.saveHexid(serverId, string, HEXID_FILE);
    }

    /**
     * Save hexadecimal ID of the server in the properties file.
     * @param hexId (String) : hexadecimal ID of the server to store
     * @param fileName (String) : name of the properties file
     */
    public static void saveHexid(int serverId, String hexId, String fileName)
    {
        try
        {
            Properties hexSetting = new Properties();
            File file = new File(fileName);
            //Create a new empty file only if it doesn't exist
            file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            hexSetting.setProperty("ServerID",String.valueOf(serverId));
            hexSetting.setProperty("HexID",hexId);
            hexSetting.store(out,"the hexID to auth into login");
            out.close();
        }
        catch (Exception e)
        {
            _log.warning("Failed to save hex id to "+fileName+" File.");
            e.printStackTrace();
        }
    }

    public static void load()
	{
    	if(Server.serverMode == Server.MODE_GAMESERVER)
		{
    		loadGameServerConfig();
    		loadMMOCoreConfig();
    		loadTelnetConfig();

    		loadBrasilConfig();
    		loadCommandConfig();
    		loadL2JModConfig();

    		loadCHConfig();
    		loadSepulchersConfig();
    		loadTvTConfig();
    		loadOlympConfig();
    		loadSevenSignsConfig();

    		loadGMAcessConfig();
    		loadAltSettingsConfig();
    		loadBossConfig();
    		loadClanConfig();
    		loadClassConfig();
    		loadEnchantConfig();
    		loadExtensionsConfig();
    		loadOtherConfig();
    		loadOptionConfig();
    		loadPvPConfig();
    		loadRatesConfig();

    		loadFloodConfig();
    		loadHexidConfig();
    		loadIdFactoryConfig();
    		loadScriptingConfig();
		}
		else if(Server.serverMode == Server.MODE_LOGINSERVER)
		{
			loadLoginServerConfig();
			loadTelnetConfig();
			loadMMOCoreConfig();
		}
		else
		{
			_log.warning("Can't load config: server mode isn't set");
		}
	}
}