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
package com.it.br.gameserver;

import com.it.br.Config;
import com.it.br.Server;
import com.it.br.configuration.settings.*;
import com.it.br.gameserver.ai.special.AiLoader;
import com.it.br.gameserver.cache.CrestCache;
import com.it.br.gameserver.cache.HtmCache;
import com.it.br.gameserver.communitybbs.Manager.ForumsBBSManager;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.database.dao.OfflineTradeDao;
import com.it.br.gameserver.datatables.*;
import com.it.br.gameserver.datatables.csv.ExtractableItemsData;
import com.it.br.gameserver.datatables.sql.*;
import com.it.br.gameserver.datatables.xml.*;
import com.it.br.gameserver.geoeditorcon.GeoEditorListener;
import com.it.br.gameserver.handler.*;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.instancemanager.*;
import com.it.br.gameserver.instancemanager.clanhallsiege.FortResistSiege;
import com.it.br.gameserver.model.AutoChatHandler;
import com.it.br.gameserver.model.AutoSpawnHandler;
import com.it.br.gameserver.model.L2Manor;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.Olympiad.Olympiad;
import com.it.br.gameserver.model.entity.Hero;
import com.it.br.gameserver.model.entity.PcPoint;
import com.it.br.gameserver.model.entity.event.TvTManager;
import com.it.br.gameserver.network.L2GameClient;
import com.it.br.gameserver.network.L2GamePacketHandler;
import com.it.br.gameserver.pathfinding.geonodes.GeoPathFinding;
import com.it.br.gameserver.script.faenor.FaenorScriptEngine;
import com.it.br.gameserver.scripting.CompiledScriptCache;
import com.it.br.gameserver.scripting.L2ScriptEngineManager;
import com.it.br.gameserver.taskmanager.TaskManager;
import com.it.br.gameserver.util.DynamicExtension;
import com.it.br.protect.nProtect;
import com.it.br.status.Status;
import com.it.br.util.IPv4Filter;
import com.it.br.util.Util;
import com.l2jserver.mmocore.network.SelectorConfig;
import com.l2jserver.mmocore.network.SelectorThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

import static com.it.br.configuration.Configurator.getSettings;

public class GameServer {
    private static Logger _log;
    private final SelectorThread<L2GameClient> _selectorThread;
    public static GameServer gameServer;
    private static Status _statusServer;
    private static ClanHallManager _cHManager;
    @SuppressWarnings("unused")
    private final ThreadPoolManager _threadpools;
    public static final Calendar dateTimeServerStarted = Calendar.getInstance();

    public SelectorThread<L2GameClient> getSelectorThread() {
        return _selectorThread;
    }

    public ClanHallManager getCHManager() {
        return _cHManager;
    }

    public GameServer() throws Exception {

        long serverLoadStart = System.currentTimeMillis();
        gameServer = this;

        Util.printSection("ID-Factory");
        IdFactory.getInstance();

        ServerSettings serverSettings = getSettings(ServerSettings.class);
        File datapack = serverSettings.getDatapackDirectory();

        _threadpools = ThreadPoolManager.getInstance();

        new File(datapack, "client-ac").mkdirs();
        new File(datapack, "data/clans").mkdirs();
        new File(datapack, "data/crests").mkdirs();
        new File(datapack, "data/pathnode").mkdirs();
        new File(datapack, "data/geodata").mkdirs();

        Util.printSection("L2JBrasil - Info");
        L2JBrasil.L2JBrasilInfo();
        L2ScriptEngineManager.getInstance();
        GameTimeController.getInstance();

        if (getSettings(L2JBrasilSettings.class).isGuardSystemEnabled()) {
            nProtect.getInstance();
            _log.info("nProtect System Enabled");
        }

        Util.printSection("Skills");
        SkillTable.getInstance();
        SkillTreeTable.getInstance();
        SkillSpellbookTable.getInstance();
        NobleSkillTable.getInstance();
        HeroSkillTable.getInstance();
        GMSkillTable.getInstance();

        Util.printSection("Items");
        ItemTable.getInstance();
        ArmorSetsTable.getInstance();
        SummonItemsData.getInstance();
        StaticObjects.getInstance();
        ExtractableItemsData.getInstance();
        FishTable.getInstance();

        if (Config.SAVE_DROPPED_ITEM)
            ItemsOnGroundManager.getInstance();
        if (Config.AUTODESTROY_ITEM_AFTER > 0 || Config.HERB_AUTO_DESTROY_TIME > 0)
            ItemsAutoDestroy.getInstance();

        Util.printSection("Castles Sieges");
        CastleManager.getInstance();
        SiegeManager.getInstance();

        Util.printSection("Zones");
        ZoneManager.getInstance();

        Util.printSection("Npc");
        if (Config.ALLOW_NPC_WALKERS)
            NpcWalkerRoutesTable.getInstance().load();

        NpcTable.getInstance();

        Util.printSection("Cache");
        HtmCache.getInstance();
        CrestCache.load();

        Util.printSection("World");
        L2World.getInstance();
        MapRegionTable.getInstance();
        TerritoryTable.getInstance();

        Util.printSection("Teleport");
        TeleportLocationTable.getInstance();
        DuelManager.getInstance();

        Util.printSection("Characters");
        if (Config.COMMUNITY_TYPE.equals("full")) {
            ForumsBBSManager.getInstance();
        }
        CharTemplateTable.getInstance();
        LevelUpData.getInstance();
        CharNameTable.getInstance();

        Util.printSection("Henna Table");
        if (!HennaTable.getInstance().isInitialized()) {
            throw new Exception("Could not initialize the Henna Table");
        }
        if (!HennaTreeTable.getInstance().isInitialized()) {
            throw new Exception("Could not initialize the Henna Tree Table");
        }

        Util.printSection("Helper Buff Table");
        if (!HelperBuffTable.getInstance().isInitialized()) {
            throw new Exception("Could not initialize the Helper Buff Table");
        }

        Util.printSection("Geodata - PathNodes");
        GeoData.getInstance();
        if (Config.GEODATA == 2)
            GeoPathFinding.getInstance();

        Util.printSection("Trade Instance");
        TradeController.getInstance();

        Util.printSection("Clan Halls");
        ClanHallManager.getInstance();
        AuctionManager.getInstance();
        ClanTable.getInstance();
        FortResistSiege.getInstance();

        Util.printSection("Spawnlist");
        if (!Config.ALT_DEV_NO_SPAWNS) {
            SpawnTable.getInstance();
        } else {
            _log.info("Spawn: disable load.");
        }

        if (!Config.ALT_DEV_NO_RB) {
            RaidBossSpawnManager.getInstance();
            GrandBossManager.getInstance();
            RaidBossPointsManager.init();
        } else {
            _log.info("RaidBoss: disable load.");
        }
        DayNightSpawnManager.getInstance().notifyChangeMode();

        Util.printSection("Four Sepulchers");
        FourSepulchersManager.getInstance().init();

        Util.printSection("Dimensional Rift");
        DimensionalRiftManager.getInstance();

        Util.printSection("Misc");
        RecipeController.getInstance();
        EventDroplist.getInstance();
        AugmentationData.getInstance();
        MonsterRace.getInstance();
        CrownManager.getInstance();
        //PartyCommandManager.getInstance();
        //FloodProtector.getInstance();
        MercTicketManager.getInstance();
        PetitionManager.getInstance();
        CursedWeaponsManager.getInstance();
        TaskManager.getInstance();
        Universe.getInstance();
        L2PetDataTable.getInstance().loadPetsData();

        Util.printSection("Announcements");
        Announcements.getInstance();
        AutoAnnouncementHandler.getInstance();

        Util.printSection("Manor");
        L2Manor.getInstance();
        CastleManorManager.getInstance();
        BoatManager.getInstance();

        Util.printSection("Doors");
        DoorTable.getInstance().parseData();

        Util.printSection("Seven Signs");
        SevenSignsFestival.getInstance();
        SevenSigns.getInstance().spawnSevenSignsNPC();

        Util.printSection("Access Levels");
        GmListTable.getInstance();
        _log.info("Acesslvl: loaded.");

        Util.printSection("Olympiad - Heroes");
        Olympiad.getInstance().load();
        Hero.getInstance();

        Util.printSection("Handlers");
        AutoChatHandler.getInstance();
        AutoSpawnHandler.getInstance();
        VoicedCommandHandler.getInstance();
        ItemHandler.getInstance();
        SkillHandler.getInstance();
        UserCommandHandler.getInstance();
        ChatHandler.getInstance();
        AdminCommandHandler.getInstance();

        _log.info("AutoChatHandler : Loaded " + AutoChatHandler.getInstance().size() + " handlers in total.");
        _log.info("AutoSpawnHandler : Loaded " + AutoSpawnHandler.getInstance().size() + " handlers in total.");
        _log.info("VoicedCommandHandler: Loaded " + VoicedCommandHandler.getInstance().size() + " handlers in total.");
        _log.info("ItemHandler: Loaded " + ItemHandler.getInstance().size() + " handlers in total.");
        _log.info("SkillHandler: Loaded " + SkillHandler.getInstance().size() + " handlers in total.");
        _log.info("UserCommandHandler: Loaded " + UserCommandHandler.getInstance().size() + " handlers in total.");
        _log.info("ChatHandler: Loaded " + ChatHandler.getInstance().size() + " handlers in total.");
        _log.info("AdminCommandHandler: Loaded " + AdminCommandHandler.getInstance().size() + " handlers in total.");

        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
        try {
            DoorTable doorTable = DoorTable.getInstance();
            doorTable.getDoor(19160010).openMe();
            doorTable.getDoor(19160011).openMe();
            doorTable.getDoor(19160012).openMe();
            doorTable.getDoor(19160013).openMe();
            doorTable.getDoor(19160014).openMe();
            doorTable.getDoor(19160015).openMe();
            doorTable.getDoor(19160016).openMe();
            doorTable.getDoor(19160017).openMe();
            doorTable.getDoor(24190001).openMe();
            doorTable.getDoor(24190002).openMe();
            doorTable.getDoor(24190003).openMe();
            doorTable.getDoor(24190004).openMe();
            doorTable.getDoor(23180001).openMe();
            doorTable.getDoor(23180002).openMe();
            doorTable.getDoor(23180003).openMe();
            doorTable.getDoor(23180004).openMe();
            doorTable.getDoor(23180005).openMe();
            doorTable.getDoor(23180006).openMe();
            doorTable.checkAutoOpen();
            doorTable = null;
        } catch (NullPointerException e) {
            _log.info("There is errors in your Door.csv file. Update door.csv");
            if (Config.DEBUG)
                e.printStackTrace();
        }

        Util.printSection("Custom Mods");
        TvTManager.getInstance();
        L2JModsSettings l2jModsSettings = getSettings(L2JModsSettings.class);
        if (l2jModsSettings.isWeddingEnabled()) {
            CoupleManager.getInstance();
            _log.info("Wedding Manager is Enable");
        } else
            _log.info("Wedding Manager is Disabled");

        if (getSettings(CommandSettings.class).isAwayStatusEnabled()) {
            AwayManager.getInstance();
            _log.info("Away is Enable");
        } else
            _log.info("Away is Disabled");

        if (l2jModsSettings.isPcBangPointEnabled()) {
            ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(PcPoint.getInstance(), Util.secondsToMilliseconds(l2jModsSettings.getPcBangPointTimeStamp()), Util.secondsToMilliseconds(l2jModsSettings.getPcBangPointTimeStamp()));
            _log.info("PC Bang Manager is Enable");
        } else
            _log.info("PC Bang Manager is Disabled");

        if ((l2jModsSettings.isOfflineTradeEnabled() || l2jModsSettings.isOfflineCraftEnabled()) && l2jModsSettings.isRestoreOfflinersEnabled())
            OfflineTradeDao.restoreOfflineTraders();

        try {
            DynamicExtension.getInstance();
        } catch (Exception ex) {
            _log.warn("DynamicExtension could not be loaded and initialized", ex);
        }

        Util.printSection("Quests");
        if (!Config.ALT_DEV_NO_QUESTS) {
            QuestManager.getInstance();
        } else
            _log.info("Quest: disable load.");

        if (!Config.ALT_DEV_NO_SCRIPT) {
            FaenorScriptEngine.getInstance();
        } else {
            _log.info("Script: disable load.");
        }

        Util.printSection("AI");
        if (!Config.ALT_DEV_NO_AI) {
            AiLoader.init();
        } else {
            _log.info("AI: disable load.");
        }

        Util.printSection("Scripts");
        try {
            File scripts = new File(datapack + "/data/jscript/scripts.cfg");
            L2ScriptEngineManager.getInstance().executeScriptList(scripts);
        } catch (IOException ioe) {
            _log.info("Failed loading scripts.cfg, no script going to be loaded");
        }
        try {
            CompiledScriptCache compiledScriptCache = L2ScriptEngineManager.getInstance().getCompiledScriptCache();
            if (compiledScriptCache == null) {
                _log.info("Compiled Scripts Cache is disabled.");
            } else {
                compiledScriptCache.purge();
                if (compiledScriptCache.isModified()) {
                    compiledScriptCache.save();
                    _log.info("Compiled Scripts Cache was saved.");
                } else
                    _log.info("Compiled Scripts Cache is up-to-date.");
            }
        } catch (IOException e) {
            _log.info("Failed to store Compiled Scripts Cache." + e);
        }
        QuestManager.getInstance().report();

        if (Config.ACCEPT_GEOEDITOR_CONN) {
            GeoEditorListener.getInstance();
        }

        if (Config.SAVE_DROPPED_ITEM) {
            ItemsOnGroundManager.getInstance();
        }

        if (Config.AUTODESTROY_ITEM_AFTER > 0 || Config.HERB_AUTO_DESTROY_TIME > 0) {
            ItemsAutoDestroy.getInstance();
        }

        Util.printSection("Login");
        LoginServerThread.getInstance().start();
        MmoCoreSettings mmoSettings = getSettings(MmoCoreSettings.class);

        final SelectorConfig sc = new SelectorConfig();
        sc.MAX_READ_PER_PASS = mmoSettings.getMaxReadPerPass();
        sc.MAX_SEND_PER_PASS = mmoSettings.getMaxSendPerPass();
        sc.SLEEP_TIME = mmoSettings.getSleepTime();
        sc.HELPER_BUFFER_COUNT = mmoSettings.getHelperBufferCount();
        final L2GamePacketHandler gph = new L2GamePacketHandler();

        _selectorThread = new SelectorThread<>(sc, gph, gph, gph, new IPv4Filter());


        NetworkSettings networkSettings = getSettings(NetworkSettings.class);
        InetAddress bindAddress = null;
        String hostname = networkSettings.getServerHostname();

        if (!"*".equals(hostname)) {
            try {
                bindAddress = InetAddress.getByName(hostname);
            } catch (UnknownHostException e1) {
                _log.error("WARNING: The GameServer bind address is invalid, using all avaliable IPs. Reason: {}", e1.getMessage());
                if (Config.DEVELOPER) {
                    e1.printStackTrace();
                }
            }
        }
        try {
            _selectorThread.openServerSocket(bindAddress, networkSettings.getServerPort());
        } catch (IOException e) {
            _log.error("FATAL: Failed to open server socket. Reason: {}", e.getMessage());
            if (Config.DEVELOPER)
                e.printStackTrace();
            System.exit(1);
        }
        _selectorThread.start();
        _log.error("Maximum Numbers of Connected Players: {}", serverSettings.getPlayerOnlineMaxCount());
        long serverLoadEnd = System.currentTimeMillis();
        _log.info("Server Loaded in " + ((serverLoadEnd - serverLoadStart) / 1000) + " seconds");

        if (networkSettings.isTelnetEnabled()) {
            _statusServer = new Status(Server.serverMode);
            _statusServer.start();
        } else
            System.out.println("Telnet is disabled.");
    }

    public static void main(String[] args) throws Exception {
        configureLogger();

        _log.info("Game Server is initializing");

        Server.serverMode = Server.MODE_GAMESERVER;

        ServerSettings serverSettings = getSettings(ServerSettings.class);
        if (serverSettings.getMinProtocol() > serverSettings.getMaxProtocol()) {
            throw new Error("protocol.min is bigger than protocol.min in server configuration file.");
        }

        // Initialize config
        Config.load();

        Util.printSection("Data Base");
        L2DatabaseFactory.getInstance();
        gameServer = new GameServer();
    }

    private static void configureLogger() {
        String logConfigurationFile = System.getProperty("log4j.configurationFile");
        if(logConfigurationFile == null || logConfigurationFile.isEmpty()) {
            System.setProperty("log4j.configurationFile", "log4j.xml");
        }

        _log  = LoggerFactory.getLogger(GameServer.class);
    }
}