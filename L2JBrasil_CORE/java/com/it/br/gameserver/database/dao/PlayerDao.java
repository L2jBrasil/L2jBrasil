package com.it.br.gameserver.database.dao;

import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2HennaInstance;
import com.it.br.gameserver.model.L2RecipeList;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.base.SubClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author Tayran
 * @version 3.0.3
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
            statement.setString(1, player.get_accountName());
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

    public static ResultSet restorePlayer(int objectId)
    {
        ResultSet rset = null;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_CHARACTER)) {
            statement.setInt(1, objectId);
            rset = statement.executeQuery();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return rset;
    }

    public static ResultSet otherPlayersInAccount(int objectId, String accountName)
    {
        ResultSet rset = null;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name=? AND obj_Id<>?"))
        {
            statement.setString(1, accountName);
            statement.setInt(2, objectId);
            rset = statement.executeQuery();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return rset;
    }

    public static ResultSet restoreCharSubClasses(L2PcInstance player)
    {
        ResultSet rset;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_CHAR_SUBCLASSES))
        {
            statement.setInt(1, player.getObjectId());

            rset = statement.executeQuery();
        }
        catch (Exception e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not restore classes for " + player.getName() + ": " + e);
            e.printStackTrace();
            return null;
        }
        return rset;
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

    public static ResultSet restoreRecipeBook(L2PcInstance player)
    {
        ResultSet rset = null;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT id, type FROM character_recipebook WHERE char_id=?"))
        {
            statement.setInt(1, player.getObjectId());
            rset = statement.executeQuery();
        }
        catch (SQLException e) {
            _log.warning(PlayerDao.class.getName() + ": Could not restore recipe book data:" + e);
        }
        return rset;
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

    public static ResultSet restoreSkills(L2PcInstance player)
    {
        ResultSet rset;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_SKILLS_FOR_CHAR))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getClassIndex());
            rset = statement.executeQuery();
            return rset;
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not restore character skills: " + e);
        }
        return null;
    }

    public static ResultSet restoreSkillsAlternative(L2PcInstance player)
    {
        ResultSet rset;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_SKILLS_FOR_CHAR_ALT_SUBCLASS))
        {
            statement.setInt(1, player.getObjectId());
            rset = statement.executeQuery();
            return rset;
        }catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not restore character skills: " + e);
        }
        return null;
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
    public static ResultSet restoreEffects(L2PcInstance player, int restoreType)
    {
        ResultSet rset;

        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_SKILL_SAVE))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getClassIndex());
            statement.setInt(3, restoreType);
            rset = statement.executeQuery();
            return rset;
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not restore active effect data: " + e);
        }
        return null;
    }


    public static ResultSet restoreHenna(L2PcInstance player)
    {
        ResultSet rset;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_CHAR_HENNAS))
        {
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, player.getClassIndex());
            rset = statement.executeQuery();
            return rset;

        } catch (SQLException e) {
            _log.warning(PlayerDao.class.getName() + ": Could not restore henna : " + e);
            e.printStackTrace();
        }
        return null;
    }


    public static ResultSet restoreRecomends(L2PcInstance player)
    {
        ResultSet rset;
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement(RESTORE_CHAR_RECOMS))
        {
            statement.setInt(1, player.getObjectId());
            rset = statement.executeQuery();
            return rset;
        }
        catch (SQLException e)
        {
            _log.warning(PlayerDao.class.getName() + ": Could not restore recommendations : " + e);
            e.printStackTrace();
        }
        return null;
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