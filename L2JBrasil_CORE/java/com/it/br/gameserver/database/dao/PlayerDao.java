package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.RecipeController;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.xml.CharTemplateTable;
import com.it.br.gameserver.datatables.xml.HennaTable;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.handler.SkillHandler;
import com.it.br.gameserver.instancemanager.CursedWeaponsManager;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.actor.appearance.PcAppearance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.SubClass;
import com.it.br.gameserver.templates.L2Henna;
import com.it.br.gameserver.templates.L2PcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class PlayerDao
{
    private static final Logger _log = Logger.getLogger(PlayerDao.class.getName());

    private static final String RESTORE_SKILLS_FOR_CHAR = "SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? AND class_index=?";
    private static final String RESTORE_SKILLS_FOR_CHAR_ALT_SUBCLASS = "SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? ORDER BY (skill_level+0)";
    private static final String ADD_NEW_SKILL = "INSERT INTO character_skills (char_obj_id,skill_id,skill_level,skill_name,class_index) VALUES (?,?,?,?,?)";
    private static final String UPDATE_CHARACTER_SKILL_LEVEL = "UPDATE character_skills SET skill_level=? WHERE skill_id=? AND char_obj_id=? AND class_index=?";
    private static final String DELETE_SKILL_FROM_CHAR = "DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? AND class_index=?";
    private static final String DELETE_CHAR_SKILLS = "DELETE FROM character_skills WHERE char_obj_id=? AND class_index=?";

    private static final String ADD_SKILL_SAVE = "INSERT INTO character_skills_save (char_obj_id,skill_id,skill_level,effect_count,effect_cur_time,reuse_delay,restore_type,class_index,buff_index) VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String RESTORE_SKILL_SAVE = "SELECT skill_id,skill_level,effect_count,effect_cur_time, reuse_delay FROM character_skills_save WHERE char_obj_id=? AND class_index=? AND restore_type=? ORDER BY buff_index ASC";
    private static final String DELETE_SKILL_SAVE = "DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=?";

    private static final String UPDATE_CHARACTER = "UPDATE characters SET level=?,maxHp=?,curHp=?,maxCp=?,curCp=?,maxMp=?,curMp=?,str=?,con=?,dex=?,_int=?,men=?,wit=?,face=?,hairStyle=?,hairColor=?,heading=?,x=?,y=?,z=?,exp=?,expBeforeDeath=?,sp=?,karma=?,pvpkills=?,pkkills=?,rec_have=?,rec_left=?,clanid=?,maxload=?,race=?,classid=?,deletetime=?,title=?,accesslevel=?,online=?,isin7sdungeon=?,clan_privs=?,wantspeace=?,base_class=?,onlinetime=?,punish_level=?,punish_timer=?,newbie=?,nobless=?,power_grade=?,subpledge=?,last_recom_date=?,lvl_joined_academy=?,apprentice=?,sponsor=?,varka_ketra_ally=?,clan_join_expiry_time=?,clan_create_expiry_time=?,char_name=?,death_penalty_level=?,pc_point=?,vip=?,vip_end=?,aio=?,aio_end=?,chat_filter_count=? WHERE obj_id=?";
    private static final String RESTORE_CHARACTER = "SELECT account_name, obj_Id, char_name, level, maxHp, curHp, maxCp, curCp, maxMp, curMp, acc, crit, evasion, mAtk, mDef, mSpd, pAtk, pDef, pSpd, runSpd, walkSpd, str, con, dex, _int, men, wit, face, hairStyle, hairColor, sex, heading, x, y, z, movement_multiplier, attack_speed_multiplier, colRad, colHeight, exp, expBeforeDeath, sp, karma, pvpkills, pkkills, clanid, maxload, race, classid, base_class, deletetime, cancraft, title, rec_have, rec_left, accesslevel, online, onlinetime, char_slot, newbie, lastAccess, clan_privs, wantspeace, isin7sdungeon, punish_level, punish_timer, power_grade, nobless, hero, subpledge, last_recom_date, lvl_joined_academy, apprentice, sponsor, varka_ketra_ally, clan_join_expiry_time, clan_create_expiry_time, death_penalty_level, pc_point, vip, vip_end, aio, aio_end, chat_filter_count FROM characters WHERE obj_id=?";
    private static final String RESTORE_CHAR_SUBCLASSES = "SELECT class_id,exp,sp,level,class_index FROM character_subclasses WHERE char_obj_id=? ORDER BY class_index ASC";
    private static final String ADD_CHAR_SUBCLASS = "INSERT INTO character_subclasses (char_obj_id,class_id,exp,sp,level,class_index) VALUES (?,?,?,?,?,?)";
    private static final String UPDATE_CHAR_SUBCLASS = "UPDATE character_subclasses SET exp=?,sp=?,level=?,class_id=? WHERE char_obj_id=? AND class_index =?";
    private static final String DELETE_CHAR_SUBCLASS = "DELETE FROM character_subclasses WHERE char_obj_id=? AND class_index=?";

    private static final String RESTORE_CHAR_HENNAS = "SELECT slot,symbol_id FROM character_hennas WHERE char_obj_id=? AND class_index=?";
    private static final String ADD_CHAR_HENNA = "INSERT INTO character_hennas (char_obj_id,symbol_id,slot,class_index) VALUES (?,?,?,?)";
    private static final String DELETE_CHAR_HENNA = "DELETE FROM character_hennas WHERE char_obj_id=? AND slot=? AND class_index=?";
    private static final String DELETE_CHAR_HENNAS = "DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?";
    private static final String DELETE_CHAR_SHORTCUTS = "DELETE FROM character_shortcuts WHERE char_obj_id=? AND class_index=?";

    private static final String RESTORE_CHAR_RECOMS = "SELECT char_id,target_id FROM character_recommends WHERE char_id=?";
    private static final String ADD_CHAR_RECOM = "INSERT INTO character_recommends (char_id,target_id) VALUES (?,?)";
    private static final String DELETE_CHAR_RECOMS = "DELETE FROM character_recommends WHERE char_id=?";

    private static final String INSERT_CHAR_IN_DB = "INSERT INTO characters " +
            "(account_name,obj_Id,char_name,level,maxHp,curHp,maxCp,curCp,maxMp,curMp," +
            "acc,crit,evasion,mAtk,mDef,mSpd,pAtk,pDef,pSpd,runSpd,walkSpd," +
            "str,con,dex,_int,men,wit,face,hairStyle,hairColor,sex," +
            "movement_multiplier,attack_speed_multiplier,colRad,colHeight," +
            "exp,sp,karma,pvpkills,pkkills,clanid,maxload,race,classid,deletetime," +
            "cancraft,title,accesslevel,online,isin7sdungeon,clan_privs,wantspeace," +
            "base_class,newbie,nobless,power_grade,last_recom_date,pc_point,vip,vip_end,aio,aio_end) " +
            "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


    public static void addRecommends(L2PcInstance player, L2PcInstance target)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(ADD_CHAR_RECOM))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, target.getObjectId());
            statement.execute();
        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": could not update char recommendations:"+e);
        }
    }

    /**
     * Update the characters table of the database with online status and lastAccess of this L2PcInstance (called when login and logout).<BR><BR>
     */
    public static void updateOnlineStatus(L2PcInstance player)
    {
        try(Connection con= L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE obj_id=?"))
        {
            statement.setInt(1, player.isOnline());
            statement.setLong(2, System.currentTimeMillis());
            statement.setInt(3, player.getObjectId());
        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": could not set char online status:"+e);
        }
    }

    public static void updateIsIn7sDungeonStatus(L2PcInstance player)
    {
        try(Connection con= L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("UPDATE characters SET isIn7sDungeon=?, lastAccess=? WHERE obj_id=?"))
        {
            statement.setInt(1, player.isIn7sDungeon() ? 1 : 0);
            statement.setLong(2, System.currentTimeMillis());
            statement.setInt(3, player.getObjectId());
        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": could not set char isIn7sDungeon status:"+e);
        }
    }

    /**
     * Create a new player in the characters table of the database.<BR><BR>
     */
    public static boolean createCharInDb(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(INSERT_CHAR_IN_DB))
        {
            statement.setString(1, player.getAccountName());
            statement.setInt(2, player.getObjectId());
            statement.setString(3, player.getName());
            statement.setInt(4, player.getLevel());
            statement.setInt(5, player.getMaxHp());
            statement.setDouble(6, player.getCurrentHp());
            statement.setInt(7, player.getMaxCp());
            statement.setDouble(8, player.getCurrentCp());
            statement.setInt(9, player.getMaxMp());
            statement.setDouble(10, player.getCurrentMp());
            statement.setInt(11, player.getAccuracy());
            statement.setInt(12, player.getCriticalHit(null, null));
            statement.setInt(13, player.getEvasionRate(null));
            statement.setInt(14, player.getMAtk(null, null));
            statement.setInt(15, player.getMDef(null, null));
            statement.setInt(16, player.getMAtkSpd());
            statement.setInt(17, player.getPAtk(null));
            statement.setInt(18, player.getPDef(null));
            statement.setInt(19, player.getPAtkSpd());
            statement.setInt(20, player.getRunSpeed());
            statement.setInt(21, player.getWalkSpeed());
            statement.setInt(22, player.getSTR());
            statement.setInt(23, player.getCON());
            statement.setInt(24, player.getDEX());
            statement.setInt(25, player.getINT());
            statement.setInt(26, player.getMEN());
            statement.setInt(27, player.getWIT());
            statement.setInt(28, player.getAppearance().getFace());
            statement.setInt(29, player.getAppearance().getHairStyle());
            statement.setInt(30, player.getAppearance().getHairColor());
            statement.setInt(31, player.getAppearance().getSex()? 1 : 0);
            statement.setDouble(32, 1/*getMovementMultiplier()*/);
            statement.setDouble(33, 1/*getAttackSpeedMultiplier()*/);
            statement.setDouble(34, player.getTemplate().collisionRadius/*getCollisionRadius()*/);
            statement.setDouble(35, player.getTemplate().collisionHeight/*getCollisionHeight()*/);
            statement.setLong(36, player.getExp());
            statement.setInt(37, player.getSp());
            statement.setInt(38, player.getKarma());
            statement.setInt(39, player.getPvpKills());
            statement.setInt(40, player.getPkKills());
            statement.setInt(41, player.getClanId());
            statement.setInt(42, player.getMaxLoad());
            statement.setInt(43, player.getRace().ordinal());
            statement.setInt(44, player.getClassId().getId());
            statement.setLong(45, player.getDeleteTimer());
            statement.setInt(46, player.hasDwarvenCraft() ? 1 : 0);
            statement.setString(47, player.getTitle());
            statement.setInt(48, player.getAccessLevel());
            statement.setInt(49, player.isOnline());
            statement.setInt(50, player.isIn7sDungeon() ? 1 : 0);
            statement.setInt(51, player.getClanPrivileges());
            statement.setInt(52, player.getWantsPeace());
            statement.setInt(53, player.getBaseClass());
            statement.setInt(54, player.isNewbie() ? 1 : 0);
            statement.setInt(55, player.isNoble() ? 1 :0);
            statement.setLong(56, 0);
            statement.setLong(57,System.currentTimeMillis());
            statement.setInt(58, player.getPcBangScore());
            statement.setInt(59, player.isVip() ? 1 :0);
            statement.setLong(60, 0);
            statement.setInt(61, player.isAio() ? 1 :0);
            statement.setLong(62, 0);

            statement.executeUpdate();
            statement.close();
        }
        catch (Exception e)
        {
            _log.severe(PlayerDao.class.getName() + ": Could not insert char data: " + e);
            return false;
        }
        return true;
    }

    public static L2PcInstance restorePlayer(int objectId)
    {
        L2PcInstance player = null;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_CHARACTER)) {
            statement.setInt(1, objectId);
            ResultSet rset = statement.executeQuery();

            while(rset.next())
            {
                final int activeClassId = rset.getInt("classid");
                final boolean female = rset.getInt("sex")!=0;
                final L2PcTemplate template = CharTemplateTable.getInstance().getTemplate(activeClassId);
                PcAppearance app = new PcAppearance(rset.getByte("face"), rset.getByte("hairColor"), rset.getByte("hairStyle"), female);

                player = new L2PcInstance(objectId, template, rset.getString("account_name"), app);
                player.setName(rset.getString("char_name"));
                player.setLastAccess(rset.getLong("lastAccess"));
                player.getStat().setExp(rset.getLong("exp"));
                player.setExpBeforeDeath(rset.getLong("expBeforeDeath"));
                player.getStat().setLevel(rset.getByte("level"));
                player.getStat().setSp(rset.getInt("sp"));
                player.setWantsPeace(rset.getInt("wantspeace"));
                player.setHeading(rset.getInt("heading"));
                player.setKarma(rset.getInt("karma"));
                player.setPvpKills(rset.getInt("pvpkills"));
                player.setPkKills(rset.getInt("pkkills"));
                player.setOnlineTime(rset.getLong("onlinetime"));
                player.setNewbie(rset.getInt("newbie")==1);
                player.setNoble(rset.getInt("nobless")==1);
                player.setHero(rset.getInt("hero") == 1);
                player.setClanJoinExpiryTime(rset.getLong("clan_join_expiry_time"));
                if (player.getClanJoinExpiryTime() < System.currentTimeMillis())
                {
                    player.setClanJoinExpiryTime(0);
                }
                player.setClanCreateExpiryTime(rset.getLong("clan_create_expiry_time"));
                if (player.getClanCreateExpiryTime() < System.currentTimeMillis())
                {
                    player.setClanCreateExpiryTime(0);
                }
                int clanId	= rset.getInt("clanid");
                player.setPowerGrade((int)rset.getLong("power_grade"));
                player.setPledgeType(rset.getInt("subpledge"));
                player.setLastRecomUpdate(rset.getLong("last_recom_date"));
                //player.setApprentice(rset.getInt("apprentice"));
                if (clanId > 0)
                {
                    player.setClan(ClanTable.getInstance().getClan(clanId));
                }
                if (player.getClan() != null)
                {
                    if (player.getClan().getLeaderId() != player.getObjectId())
                    {
                        if (player.getPowerGrade() == 0)
                        {
                            player.setPowerGrade(5);
                        }
                        player.setClanPrivileges(player.getClan().getRankPrivs(player.getPowerGrade()));
                    }
                    else
                    {
                        player.setClanPrivileges(L2Clan.CP_ALL);
                        player.setPowerGrade(1);
                    }
                }
                else
                {
                    player.setClanPrivileges(L2Clan.CP_NOTHING);
                }
                player.setDeleteTimer(rset.getLong("deletetime"));
                player.setTitle(rset.getString("title"));
                player.setAccessLevel(rset.getInt("accesslevel"));
                player.setFistsWeaponItem(player.findFistsWeaponItem(activeClassId));
                player.setUptime(System.currentTimeMillis());

                player.setCurrentHp(rset.getDouble("curHp"));

                player.setCurrentCp(rset.getDouble("curCp"));

                player.setCurrentMp(rset.getDouble("curMp"));
                //Check recs
                player.checkRecom(rset.getInt("rec_have"),rset.getInt("rec_left"));
                player.setClassIndex(0);
                try { player.setBaseClass(rset.getInt("base_class")); }
                catch (Exception e) { player.setBaseClass(activeClassId); }

                // Restore Subclass Data (cannot be done earlier in function)
                if (restoreCharSubClasses(player))
                {
                    if (activeClassId != player.getBaseClass())
                    {
                        for (SubClass subClass : player.getSubClasses().values())
                            if (subClass.getClassId() == activeClassId)
                                player.setClassIndex(subClass.getClassIndex());
                    }
                }
                if (player.getClassIndex() == 0 && activeClassId != player.getBaseClass())
                {
                    // Subclass in use but doesn't exist in DB -
                    // a possible restart-while-modifysubclass cheat has been attempted.
                    // Switching to use base class
                    player.setClassId(player.getBaseClass());
                    _log.warning("Player "+player.getName()+" reverted to base class. Possibly has tried a relogin exploit while subclassing.");
                }
                else player.setActiveClass(activeClassId);
                player.setApprentice(rset.getInt("apprentice"));
                player.setSponsor(rset.getInt("sponsor"));
                player.setLvlJoinedAcademy(rset.getInt("lvl_joined_academy"));
                player.setIsIn7sDungeon(rset.getInt("isin7sdungeon")==1);
                player.setPunishLevel(rset.getInt("punish_level"));
                if (player.getPunishLevel() != L2PcInstance.PunishLevel.NONE)
                    player.setPunishTimer(rset.getLong("punish_timer"));
                else
                    player.setPunishTimer(0);
                CursedWeaponsManager.getInstance().checkPlayer(player);
                player.setAllianceWithVarkaKetra(rset.getInt("varka_ketra_ally"));
                player.setDeathPenaltyBuffLevel(rset.getInt("death_penalty_level"));
                player.addPcBangScore(rset.getInt("pc_point"));
                player.setVip(rset.getInt("vip") == 1);
                player.setVipEndTime(rset.getLong("vip_end"));
                player.setAio(rset.getInt("aio") == 1);
                player.setAioEndTime(rset.getLong("aio_end"));
                player.setChatFilterCount(rset.getInt("chat_filter_count"));

                // Add the L2PcInstance object in _allObjects
                //L2World.getInstance().storeObject(player);

                // Set the x,y,z position of the L2PcInstance and make it invisible
                player.setXYZInvisible(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));

                PlayerDao.otherPlayersInAccount(player);
                break;
            }
            rset.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return player;
    }

    private static void otherPlayersInAccount(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name=? AND obj_Id<>?"))
        {
            statement.setString(1, player.getAccountName());
            statement.setInt(2, player.getObjectId());
            ResultSet rset = statement.executeQuery();
            while (rset.next())
            {
                Integer charId = rset.getInt("obj_Id");
                String charName = rset.getString("char_name");
                player.getAccountChars().put(charId, charName);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Restores sub-class data for the L2PcInstance, used to check the current
     * class index for the character.
     */
    private static boolean restoreCharSubClasses(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_CHAR_SUBCLASSES))
        {
            statement.setInt(1, player.getObjectId());
            ResultSet rset = statement.executeQuery();

            while (rset.next())
            {
                SubClass subClass = new SubClass();
                subClass.setClassId(rset.getInt("class_id"));
                subClass.setLevel(rset.getByte("level"));
                subClass.setExp(rset.getLong("exp"));
                subClass.setSp(rset.getInt("sp"));
                subClass.setClassIndex(rset.getInt("class_index"));

                // Enforce the correct indexing of _subClasses against their class indexes.
                player.getSubClasses().put(subClass.getClassIndex(), subClass);
            }

        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not restore classes for " + player.getName() + ": " + e);
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Store recipe book data for this L2PcInstance, if not on an active sub-class.
     */
    public static void storeRecipeBook(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection())
        {
            PreparedStatement statement = con.prepareStatement("DELETE FROM character_recipebook WHERE char_id=?");
            statement.setInt(1, player.getObjectId());
            statement.execute();
            statement.close();

            L2RecipeList[] recipes = player.getCommonRecipeBook();

            for (L2RecipeList recipe : recipes)
            {
                statement = con.prepareStatement("INSERT INTO character_recipebook (char_id, id, type) values(?,?,0)");
                statement.setInt(1, player.getObjectId());
                statement.setInt(2, recipe.getId());
                statement.execute();
                statement.close();
            }

            recipes = player.getDwarvenRecipeBook();
            for (L2RecipeList recipe : recipes)
            {
                statement = con.prepareStatement("INSERT INTO character_recipebook (char_id, id, type) values(?,?,1)");
                statement.setInt(1, player.getObjectId());
                statement.setInt(2, recipe.getId());
                statement.execute();
                statement.close();
            }
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not store recipe book data: " + e);
        }
    }

    public static void restoreRecipeBook(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT id, type FROM character_recipebook WHERE char_id=?"))
        {
            statement.setInt(1, player.getObjectId());
            ResultSet rset = statement.executeQuery();
            L2RecipeList recipe;
            while (rset.next())
            {
                recipe = RecipeController.getInstance().getRecipeList(rset.getInt("id") - 1);

                if (rset.getInt("type") == 1)
                    player.registerDwarvenRecipeList(recipe);
                else
                    player.registerCommonRecipeList(recipe);
            }

        }
        catch (SQLException e) {
            _log.warning(PlayerDao.class.getName() + ": Could not restore recipe book data:" + e);
        }
    }

    public static void storeCharBase(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(UPDATE_CHARACTER))
        {
            // Get the exp, level, and sp of base class to store in base table
            int currentClassIndex = player.getClassIndex();
            player.setClassIndex(0);
            long exp     = player.getStat().getExp();
            int level    = player.getStat().getLevel();
            int sp       = player.getStat().getSp();
            player.setClassIndex(currentClassIndex);

            statement.setInt(1, level);
            statement.setInt(2, player.getMaxHp());
            statement.setDouble(3, player.getCurrentHp());
            statement.setInt(4, player.getMaxCp());
            statement.setDouble(5, player.getCurrentCp());
            statement.setInt(6, player.getMaxMp());
            statement.setDouble(7, player.getCurrentMp());
            statement.setInt(8, player.getSTR());
            statement.setInt(9, player.getCON());
            statement.setInt(10, player.getDEX());
            statement.setInt(11, player.getINT());
            statement.setInt(12, player.getMEN());
            statement.setInt(13, player.getWIT());
            statement.setInt(14, player.getAppearance().getFace());
            statement.setInt(15, player.getAppearance().getHairStyle());
            statement.setInt(16, player.getAppearance().getHairColor());
            statement.setInt(17, player.getHeading());
            statement.setInt(18, player.inObserverMode() ? player.getObsX() : player.getX());
            statement.setInt(19, player.inObserverMode() ? player.getObsY() : player.getY());
            statement.setInt(20, player.inObserverMode() ? player.getObsZ() : player.getZ());
            statement.setLong(21, exp);
            statement.setLong(22, player.getExpBeforeDeath());
            statement.setInt(23, sp);
            statement.setInt(24, player.getKarma());
            statement.setInt(25, player.getPvpKills());
            statement.setInt(26, player.getPkKills());
            statement.setInt(27, player.getRecomHave());
            statement.setInt(28, player.getRecomLeft());
            statement.setInt(29, player.getClanId());
            statement.setInt(30, player.getMaxLoad());
            statement.setInt(31, player.getRace().ordinal());

            statement.setInt(32, player.getClassId().getId());
            statement.setLong(33, player.getDeleteTimer());
            statement.setString(34, player.getTitle());
            statement.setInt(35, player.getAccessLevel());

            if(player.isOffline() || player.isOnline() ==1 ) //in offline mode or online
                statement.setInt(36, 1);
            else
                statement.setInt(36, player.isOnline());

            statement.setInt(37, player.isIn7sDungeon() ? 1 : 0);
            statement.setInt(38, player.getClanPrivileges());
            statement.setInt(39, player.getWantsPeace());
            statement.setInt(40, player.getBaseClass());

            long totalOnlineTime = player.getOnlineTime();

            if (player.getOnlineBeginTime() > 0)
                totalOnlineTime += (System.currentTimeMillis() - player.getOnlineBeginTime()) / 1000;

            statement.setLong(41, totalOnlineTime);
            statement.setInt(42, player.getPunishLevel().value());
            statement.setLong(43, player.getPunishTimer());
            statement.setInt(44, player.isNewbie() ? 1 : 0);
            statement.setInt(45, player.isNoble() ? 1 : 0);
            statement.setLong(46, player.getPowerGrade());
            statement.setInt(47, player.getPledgeType());
            statement.setLong(48, player.getLastRecomUpdate());
            statement.setInt(49, player.getLvlJoinedAcademy());
            statement.setLong(50, player.getApprentice());
            statement.setLong(51, player.getSponsor());
            statement.setInt(52, player.getAllianceWithVarkaKetra());
            statement.setLong(53, player.getClanJoinExpiryTime());
            statement.setLong(54, player.getClanCreateExpiryTime());
            statement.setString(55, player.getName());
            statement.setLong(56, player.getDeathPenaltyBuffLevel());
            statement.setInt(57, player.getPcBangScore());
            statement.setInt(58, player.isVip() ? 1 : 0);
            statement.setLong(59, player.getVipEndTime());
            statement.setInt(60, player.isAio() ? 1 : 0);
            statement.setLong(61, player.getAioEndTime());
            statement.setInt(62, player.getChatFilterCount());
            statement.setInt(63, player.getObjectId());

            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not store char base data: "+ e);
        }
    }

    public static void storeCharSub(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection())
        {
            PreparedStatement statement;

            if (player.getTotalSubClasses() > 0)
            {
                for (SubClass subClass : player.getSubClasses().values())
                {
                    statement = con.prepareStatement(UPDATE_CHAR_SUBCLASS);
                    statement.setLong(1, subClass.getExp());
                    statement.setInt(2, subClass.getSp());
                    statement.setInt(3, subClass.getLevel());
                    statement.setInt(4, subClass.getClassId());
                    statement.setInt(5, player.getObjectId());
                    statement.setInt(6, subClass.getClassIndex());

                    statement.execute();
                    statement.close();
                }
            }
        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not store sub class data for " + player.getName() + ": "+ e);
        }
    }

    public static void deleteSkillsSave(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(DELETE_SKILL_SAVE))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getClassIndex());
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not Delete skills effect data: " + e);
        }
    }

    public static void addSkillsSave(L2PcInstance player, L2Effect effect, int skillId, int buff_index)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(ADD_SKILL_SAVE))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, skillId);
            statement.setInt(3, effect.getSkill().getLevel());
            statement.setInt(4, effect.getCount());
            statement.setInt(5, effect.getTime());

            if (player.getReuseTimeStamps().containsKey(skillId))
            {
                L2PcInstance.TimeStamp t = player.getReuseTimeStamps().remove(skillId);
                statement.setLong(6, t.hasNotPassed() ? t.getReuse() : 0 );
            } else
            {
                statement.setLong(6, 0);
            }

            statement.setInt(7, 0);
            statement.setInt(8, player.getClassIndex());
            statement.setInt(9, buff_index);
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not add skills effect data: " + e);
        }
    }

    public static void addSkillsSave2(L2PcInstance player, L2PcInstance.TimeStamp t, int buff_index)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(ADD_SKILL_SAVE))
        {
            statement.setInt (1, player.getObjectId());
            statement.setInt (2, t.getSkill());
            statement.setInt (3, -1);
            statement.setInt (4, -1);
            statement.setInt (5, -1);
            statement.setLong(6, t.getReuse());
            statement.setInt (7, 1);
            statement.setInt (8, player.getClassIndex());
            statement.setInt(9, buff_index);
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not add skills 2 effect data: " + e);
        }
    }

    public static void removeSkill(L2PcInstance player, L2Skill oldSkill)
    {
        if (oldSkill != null)
        {
            try (Connection con = L2DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(DELETE_SKILL_FROM_CHAR)) {
                // Remove or update a L2PcInstance skill from the character_skills table of the database

                statement.setInt(1, oldSkill.getId());
                statement.setInt(2, player.getObjectId());
                statement.setInt(3, player.getClassIndex());
                statement.execute();
                statement.close();
            }
            catch(SQLException e)
            {
                _log.warning(PlayerDao.class.getName() + ": Error could not delete skill: " + e);
            }
        }
    }

    public static void updateSkillLevel(L2PcInstance player, L2Skill newSkill, L2Skill oldSkill, int classIndex)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_CHARACTER_SKILL_LEVEL))
        {
            statement.setInt(1, newSkill.getLevel());
            statement.setInt(2, oldSkill.getId());
            statement.setInt(3, player.getObjectId());
            statement.setInt(4, classIndex);
            statement.execute();
            statement.close();

        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Error could not delete skill: " + e);
        }
    }

    public static void addNewSkill(L2PcInstance player, L2Skill newSkill, int classIndex)
    {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(ADD_NEW_SKILL))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, newSkill.getId());
            statement.setInt(3, newSkill.getLevel());
            statement.setString(4, newSkill.getName());
            statement.setInt(5, classIndex);
            statement.execute();
            statement.close();
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Error could not delete skill: " + e);
        }
    }

    public static void restoreSkills(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_SKILLS_FOR_CHAR))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getClassIndex());
            ResultSet rset = statement.executeQuery();

            // Go though the recordset of this SQL query
            while (rset.next())
            {
                int id = rset.getInt("skill_id");
                int level = rset.getInt("skill_level");

                if (id > 9000)
                {
                    continue; // fake skills for base stats
                }
                // Create a L2Skill object for each record
                L2Skill skill = SkillTable.getInstance().getInfo(id, level);

                // Add the L2Skill object to the L2Character _skills and its Func objects to the calculator set of the L2Character
                player.addSkill(skill);
            }
            rset.close();
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not restore character skills: " + e);
        }
    }

    public static void restoreSkillsAlternative(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_SKILLS_FOR_CHAR_ALT_SUBCLASS))
        {
            statement.setInt(1, player.getObjectId());
            ResultSet rset = statement.executeQuery();

            // Go though the recordset of this SQL query
            while(rset.next())
            {
                int id = rset.getInt("skill_id");
                int level = rset.getInt("skill_level");

                if(id > 9000)
                {
                    continue; // fake skills for base stats
                }

                // Create a L2Skill object for each record
                L2Skill skill = SkillTable.getInstance().getInfo(id, level);

                // Add the L2Skill object to the L2Character _skills and its Func objects to the calculator set of the L2Character
                player.addSkill(skill);
            }
            rset.close();
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not restore character skills: " + e);
        }
    }

    /**
     *  Restore Type 0
     *  These skill were still in effect on the character
     *  upon logout. Some of which were self casted and
     *  might still have had a long reuse delay which also
     *  is restored.
     *
     * Restore Type 1
     * The remaning skills lost effect upon logout but
     * were still under a high reuse delay.
     */
    public static void restoreEffects(L2PcInstance player, L2Object[] targets, int restoreType)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_SKILL_SAVE))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getClassIndex());
            statement.setInt(3, restoreType);
            ResultSet rset = statement.executeQuery();

            if(restoreType == 0)
            {

                while (rset.next())
                {
                    int skillId = rset.getInt("skill_id");
                    int skillLvl = rset.getInt("skill_level");
                    int effectCount = rset.getInt("effect_count");
                    int effectCurTime = rset.getInt("effect_cur_time");
                    long reuseDelay = rset.getLong("reuse_delay");

                    // Just incase the admin minipulated this table incorrectly :x
                    if(skillId == -1 || effectCount == -1 || effectCurTime == -1 || reuseDelay < 0) continue;

                    L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
                    ISkillHandler IHand = SkillHandler.getInstance().getSkillHandler(skill.getSkillType());
                    if (IHand != null)
                        IHand.useSkill(player, skill, targets);
                    else
                        skill.useSkill(player, targets);

                    if (reuseDelay > 10)
                    {
                        player.disableSkill(skillId, reuseDelay);
                        player.addTimeStamp(player.new TimeStamp(skillId, reuseDelay));
                    }

                    for (L2Effect effect : player.getAllEffects())
                    {
                        if (effect.getSkill().getId() == skillId)
                        {
                            effect.setCount(effectCount);
                            effect.setFirstTime(effectCurTime);
                        }
                    }
                }
            }
            else
            {
                while (rset.next())
                {
                    int skillId = rset.getInt("skill_id");
                    long reuseDelay = rset.getLong("reuse_delay");

                    if (reuseDelay <= 0) continue;

                    player.disableSkill(skillId, reuseDelay);
                    player.addTimeStamp(player.new TimeStamp(skillId, reuseDelay));
                }
                rset.close();

            }
            rset.close();
        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not restore active effect data: " + e);
        }
    }

    public static void restoreHenna(L2PcInstance player)
    {
        L2HennaInstance sym;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_CHAR_HENNAS))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getClassIndex());
            ResultSet rset = statement.executeQuery();

            while (rset.next())
            {
                int slot = rset.getInt("slot");

                if (slot < 1 || slot > 3) {
                    continue;
                }

                int symbol_id = rset.getInt("symbol_id");


                if (symbol_id != 0)
                {
                    L2Henna tpl = HennaTable.getInstance().getTemplate(symbol_id);

                    if (tpl != null)
                    {
                        sym = new L2HennaInstance(tpl);
                        player.setHenna(slot-1, sym);
                    }
                }
            }


        } catch (SQLException e) {
            _log.warning(PlayerDao.class.getName() + ": Could not restore henna : " + e);
            e.printStackTrace();
        }
    }


    public static void restoreRecomends(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_CHAR_RECOMS))
        {
            statement.setInt(1, player.getObjectId());
            ResultSet rset = statement.executeQuery();
            while (rset.next())
            {
                player.getRecomChars().add(rset.getInt("target_id"));
            }
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not restore recommendations : " + e);
            e.printStackTrace();
        }
    }

    public static void deleteHenna(L2PcInstance player, int slot)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(DELETE_CHAR_HENNA))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, slot+1);
            statement.setInt(3, player.getClassIndex());
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not remove Char Henna  : " + e);
            e.printStackTrace();
        }
    }

    public static void addHenna(L2PcInstance player, L2HennaInstance henna, int loopCount)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(ADD_CHAR_HENNA))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, henna.getSymbolId());
            statement.setInt(3, loopCount + 1);
            statement.setInt(4, player.getClassIndex());
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not save Char Henna  : " + e);
            e.printStackTrace();
        }
    }

    public static void addSubClasses(L2PcInstance player, SubClass newClass)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(ADD_CHAR_SUBCLASS))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, newClass.getClassId());
            statement.setLong(3, newClass.getExp());
            statement.setInt(4, newClass.getSp());
            statement.setInt(5, newClass.getLevel());
            statement.setInt(6, newClass.getClassIndex()); // <-- Added
            statement.execute();
        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not add character sub class for " + player.getName() + ": " + e);
        }
    }

    public static void deleteHennas(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(DELETE_CHAR_HENNAS))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getClassIndex());
            statement.execute();
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not remove Char Henna  : " + e);
            e.printStackTrace();
        }
    }

    public static void deleteShortcuts(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(DELETE_CHAR_SHORTCUTS))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getClassIndex()); // <-- Added
            statement.execute();
        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not remove character shortcuts for " + player.getName() + ": " + e);
        }
    }

    public static void deleteSkills(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(DELETE_CHAR_SKILLS))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getClassIndex()); // <-- Added
            statement.execute();
        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not remove character skills for " + player.getName() + ": " + e);
        }
    }

    public static void deleteSubClasses(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(DELETE_CHAR_SUBCLASS))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getClassIndex()); // <-- Added
            statement.execute();
        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not remove character Sub Classes for " + player.getName() + ": " + e);
        }
    }

    public static void deleteRecommends(L2PcInstance player)
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(DELETE_CHAR_RECOMS))
        {
            statement.setInt(1, player.getObjectId());
            statement.execute();
        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not remove character Recommends for " + player.getName() + ": " + e);
        }
    }
}