package com.it.br.gameserver.database.dao;

import com.it.br.gameserver.Item;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.model.L2ClanMember;
import com.it.br.gameserver.templates.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tayran
 * @version 3.0.4
 */
public class ItemsDao {
    private static final Logger _log = LoggerFactory.getLogger(ItemsDao.class);

    private static String DELETE = "DELETE FROM items WHERE owner_id = ? and item_id = ?";
    /**
     * Table of SQL request in order to obtain items from tables [etcitem], [armor], [weapon]
     */
    private static final String[] SQL_ITEM_SELECTS = {
        "SELECT item_id, name, crystallizable, item_type, weight, consume_type, material, crystal_type, duration, price, crystal_count, sellable, dropable, destroyable, tradeable FROM etcitem",

        "SELECT item_id, name, bodypart, crystallizable, armor_type, weight," +
            " material, crystal_type, avoid_modify, duration, p_def, m_def, mp_bonus," +
            " price, crystal_count, sellable, dropable, destroyable, tradeable, item_skill_id, item_skill_lvl FROM armor",

        "SELECT item_id, name, bodypart, crystallizable, weight, soulshots, spiritshots," +
            " material, crystal_type, p_dam, rnd_dam, weaponType, critical, hit_modify, avoid_modify," +
            " shield_def, shield_def_rate, atk_speed, mp_consume, m_dam, duration, price, crystal_count," +
            " sellable, dropable, destroyable, tradeable, item_skill_id, item_skill_lvl,enchant4_skill_id,enchant4_skill_lvl, onCast_skill_id, onCast_skill_lvl," +
            " onCast_skill_chance, onCrit_skill_id, onCrit_skill_lvl, onCrit_skill_chance FROM weapon"
    };

    public static List<Item> loadEtcItems() {
        List<Item> list = new ArrayList<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SQL_ITEM_SELECTS[0])) {
            ResultSet rset = statement.executeQuery();

            // Add item in correct FastMap
            while (rset.next()) {
                Item newItem = readItem(rset);
                list.add(newItem);
            }

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            _log.warn("{}: Exception: delete(L2PcInstance, int itemId)",ItemsDao.class.getName());
            _log.warn(e.getMessage(), e);
        }
        return list;
    }

    public static List<Item> loadArmors() {
        List<Item> list = new ArrayList<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SQL_ITEM_SELECTS[0])) {
            ResultSet rset = statement.executeQuery();

            // Add item in correct FastMap
            while (rset.next()) {
                Item newItem = readArmor(rset);
                list.add(newItem);
            }

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            _log.warn("{} : Exception: delete(L2PcInstance, int itemId)", ItemsDao.class.getName());
            _log.warn(e.getMessage(), e);
        }
        return list;
    }

    public static List<Item> loadWeapons() {
        List<Item> list = new ArrayList<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SQL_ITEM_SELECTS[0])) {
            ResultSet rset = statement.executeQuery();

            // Add item in correct FastMap
            while (rset.next()) {
                Item newItem = readWeapon(rset);
                list.add(newItem);
            }

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            _log.warn("{}: Exception: delete(L2PcInstance, int itemId)", ItemsDao.class.getName());
            _log.warn(e.getMessage(), e);
        }
        return list;
    }

    public static void delete(L2ClanMember owner, int itemId) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE)) {
            statement.setInt(1, owner.getObjectId());
            statement.setInt(2, itemId);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            _log.warn("{}: Exception: delete(L2ClanMember, int itemId)", ItemsDao.class.getName());
            _log.warn(e.getMessage(), e);
        }
    }

    /**
     * Returns object Item from the record of the database
     *
     * @param rset : ResultSet designating a record of the [etcitem] table of database
     * @return Item : object created from the database record
     * @throws SQLException exception
     */
    private static Item readItem(ResultSet rset) throws SQLException {
        Item item = new Item();
        item.set = new StatsSet();
        item.id = rset.getInt("item_id");

        item.set.set("item_id", item.id);
        item.set.set("crystallizable", Boolean.valueOf(rset.getString("crystallizable")));
        item.set.set("type1", L2Item.TYPE1_ITEM_QUESTITEM_ADENA);
        item.set.set("type2", L2Item.TYPE2_OTHER);
        item.set.set("bodypart", 0);
        item.set.set("crystal_count", rset.getInt("crystal_count"));
        item.set.set("sellable", Boolean.valueOf(rset.getString("sellable")));
        item.set.set("dropable", Boolean.valueOf(rset.getString("dropable")));
        item.set.set("destroyable", Boolean.valueOf(rset.getString("destroyable")));
        item.set.set("tradeable", Boolean.valueOf(rset.getString("tradeable")));

        String itemType = rset.getString("item_type");
        switch (itemType) {
            case "none":
                item.type = L2EtcItemType.OTHER; // only for default
                break;
            case "castle_guard":
                item.type = L2EtcItemType.SCROLL; // dummy
                break;
            case "material":
                item.type = L2EtcItemType.MATERIAL;
                break;
            case "pet_collar":
                item.type = L2EtcItemType.PET_COLLAR;
                break;
            case "potion":
                item.type = L2EtcItemType.POTION;
                break;
            case "recipe":
                item.type = L2EtcItemType.RECEIPE;
                break;
            case "scroll":
                item.type = L2EtcItemType.SCROLL;
                break;
            case "seed":
                item.type = L2EtcItemType.SEED;
                break;
            case "shot":
                item.type = L2EtcItemType.SHOT;
                break;
            case "spellbook":
                item.type = L2EtcItemType.SPELLBOOK; // Spellbook, Amulet, Blueprint
                break;
            case "herb":
                item.type = L2EtcItemType.HERB;
                break;
            case "arrow":
                item.type = L2EtcItemType.ARROW;
                item.set.set("bodypart", L2Item.SLOT_L_HAND);
                break;
            case "quest":
                item.type = L2EtcItemType.QUEST;
                item.set.set("type2", L2Item.TYPE2_QUEST);
                break;
            case "lure":
                item.type = L2EtcItemType.OTHER;
                item.set.set("bodypart", L2Item.SLOT_L_HAND);
                break;
            default:
                _log.debug("unknown etcitem type: {}", itemType);
                item.type = L2EtcItemType.OTHER;
                break;
        }

        String consume = rset.getString("consume_type");
        switch (consume) {
            case "asset":
                item.type = L2EtcItemType.MONEY;
                item.set.set("stackable", true);
                item.set.set("type2", L2Item.TYPE2_MONEY);
                break;
            case "stackable":
                item.set.set("stackable", true);
                break;
            default:
                item.set.set("stackable", false);
                break;
        }

        int material = ItemTable.getMaterials().get(rset.getString("material"));
        item.set.set("material", material);

        int crystal = ItemTable.getCrystalTypes().get(rset.getString("crystal_type"));
        item.set.set("crystal_type", crystal);

        int weight = rset.getInt("weight");
        item.set.set("weight", weight);
        item.name = rset.getString("name");
        item.set.set("name", item.name);

        item.set.set("duration", rset.getInt("duration"));
        item.set.set("price", rset.getInt("price"));

        return item;
    }


    /**
     * Returns object Item from the record of the database
     * @param rset : ResultSet designating a record of the [armor] table of database
     * @return Item : object created from the database record
     * @throws SQLException exception
     */
    private static Item readArmor(ResultSet rset) throws SQLException
    {
        Item item   = new Item();
        item.set    = new StatsSet();
        item.type   = ItemTable.getArmorTypes().get(rset.getString("armor_type"));
        item.id     = rset.getInt("item_id");
        item.name   = rset.getString("name");

        item.set.set("item_id", item.id);
        item.set.set("name", item.name);
        int bodypart = ItemTable.getSlots().get(rset.getString("bodypart"));
        item.set.set("bodypart", bodypart);
        item.set.set("crystallizable", Boolean.valueOf(rset.getString("crystallizable")));
        item.set.set("crystal_count", rset.getInt("crystal_count"));
        item.set.set("sellable", Boolean.valueOf(rset.getString("sellable")));
        item.set.set("dropable", Boolean.valueOf(rset.getString("dropable")));
        item.set.set("destroyable", Boolean.valueOf(rset.getString("destroyable")));
        item.set.set("tradeable", Boolean.valueOf(rset.getString("tradeable")));
        item.set.set("item_skill_id", rset.getInt("item_skill_id"));
        item.set.set("item_skill_lvl", rset.getInt("item_skill_lvl"));

        if (bodypart == L2Item.SLOT_NECK ||
                bodypart == L2Item.SLOT_HAIR ||
                bodypart == L2Item.SLOT_FACE ||
                bodypart == L2Item.SLOT_DHAIR ||
                (bodypart & L2Item.SLOT_L_EAR) != 0 ||
                (bodypart & L2Item.SLOT_L_FINGER) != 0)
        {
            item.set.set("type1", L2Item.TYPE1_WEAPON_RING_EARRING_NECKLACE);
            item.set.set("type2", L2Item.TYPE2_ACCESSORY);
        }
        else
        {
            item.set.set("type1", L2Item.TYPE1_SHIELD_ARMOR);
            item.set.set("type2", L2Item.TYPE2_SHIELD_ARMOR);
        }

        item.set.set("weight", rset.getInt("weight"));
        item.set.set("material", ItemTable.getMaterials().get(rset.getString("material")));
        item.set.set("crystal_type", ItemTable.getCrystalTypes().get(rset.getString("crystal_type")));
        item.set.set("avoid_modify", rset.getInt("avoid_modify"));
        item.set.set("duration", rset.getInt("duration"));
        item.set.set("p_def", rset.getInt("p_def"));
        item.set.set("m_def", rset.getInt("m_def"));
        item.set.set("mp_bonus", rset.getInt("mp_bonus"));
        item.set.set("price", rset.getInt("price"));

        if (item.type == L2ArmorType.PET)
        {
            item.set.set("type1", L2Item.TYPE1_SHIELD_ARMOR);
            if (item.set.getInteger("bodypart") == L2Item.SLOT_WOLF)
                item.set.set("type2", L2Item.TYPE2_PET_WOLF);
            else if (item.set.getInteger("bodypart") == L2Item.SLOT_HATCHLING)
                item.set.set("type2", L2Item.TYPE2_PET_HATCHLING);
            else if (item.set.getInteger("bodypart") == L2Item.SLOT_BABYPET)
                item.set.set("type2", L2Item.TYPE2_PET_BABY);
            else
                item.set.set("type2", L2Item.TYPE2_PET_STRIDER);

            item.set.set("bodypart", L2Item.SLOT_CHEST);
        }

        return item;
    }


    /**
     * Returns object Item from the record of the database
     * @param rset : ResultSet designating a record of the [weapon] table of database
     * @return Item : object created from the database record
     * @throws SQLException exception
     */
    private static Item readWeapon(ResultSet rset) throws SQLException
    {
        Item item   = new Item();
        item.set    = new StatsSet();
        item.type   = ItemTable.getWeaponTypes().get(rset.getString("weaponType"));
        item.id     = rset.getInt("item_id");
        item.name   = rset.getString("name");

        item.set.set("item_id", item.id);
        item.set.set("name", item.name);

        // lets see if this is a shield
        if (item.type == L2WeaponType.NONE)
        {
            item.set.set("type1", L2Item.TYPE1_SHIELD_ARMOR);
            item.set.set("type2", L2Item.TYPE2_SHIELD_ARMOR);
        }
        else
        {
            item.set.set("type1", L2Item.TYPE1_WEAPON_RING_EARRING_NECKLACE);
            item.set.set("type2", L2Item.TYPE2_WEAPON);
        }

        item.set.set("bodypart",       ItemTable.getSlots().get(rset.getString("bodypart")));
        item.set.set("material",       ItemTable.getMaterials().get(rset.getString("material")));
        item.set.set("crystal_type",   ItemTable.getCrystalTypes().get(rset.getString("crystal_type")));
        item.set.set("crystallizable", Boolean.valueOf(rset.getString("crystallizable")));
        item.set.set("weight",         rset.getInt("weight"));
        item.set.set("soulshots",      rset.getInt("soulshots"));
        item.set.set("spiritshots",    rset.getInt("spiritshots"));
        item.set.set("p_dam",          rset.getInt("p_dam"));
        item.set.set("rnd_dam",        rset.getInt("rnd_dam"));
        item.set.set("critical",       rset.getInt("critical"));
        item.set.set("hit_modify",     rset.getDouble("hit_modify"));
        item.set.set("avoid_modify",   rset.getInt("avoid_modify"));
        item.set.set("shield_def",     rset.getInt("shield_def"));
        item.set.set("shield_def_rate",rset.getInt("shield_def_rate"));
        item.set.set("atk_speed",      rset.getInt("atk_speed"));
        item.set.set("mp_consume",     rset.getInt("mp_consume"));
        item.set.set("m_dam",          rset.getInt("m_dam"));
        item.set.set("duration",     rset.getInt("duration"));
        item.set.set("price",          rset.getInt("price"));
        item.set.set("crystal_count",  rset.getInt("crystal_count"));
        item.set.set("sellable",       Boolean.valueOf(rset.getString("sellable")));
        item.set.set("dropable",       Boolean.valueOf(rset.getString("dropable")));
        item.set.set("destroyable",       Boolean.valueOf(rset.getString("destroyable")));
        item.set.set("tradeable",       Boolean.valueOf(rset.getString("tradeable")));

        item.set.set("item_skill_id", rset.getInt("item_skill_id"));
        item.set.set("item_skill_lvl", rset.getInt("item_skill_lvl"));

        item.set.set("enchant4_skill_id", rset.getInt("enchant4_skill_id"));
        item.set.set("enchant4_skill_lvl", rset.getInt("enchant4_skill_lvl"));

        item.set.set("onCast_skill_id", rset.getInt("onCast_skill_id"));
        item.set.set("onCast_skill_lvl", rset.getInt("onCast_skill_lvl"));
        item.set.set("onCast_skill_chance", rset.getInt("onCast_skill_chance"));

        item.set.set("onCrit_skill_id", rset.getInt("onCrit_skill_id"));
        item.set.set("onCrit_skill_lvl", rset.getInt("onCrit_skill_lvl"));
        item.set.set("onCrit_skill_chance", rset.getInt("onCrit_skill_chance"));

        if (item.type == L2WeaponType.PET)
        {
            item.set.set("type1", L2Item.TYPE1_WEAPON_RING_EARRING_NECKLACE);
            if (item.set.getInteger("bodypart") == L2Item.SLOT_WOLF)
                item.set.set("type2", L2Item.TYPE2_PET_WOLF);
            else if (item.set.getInteger("bodypart") == L2Item.SLOT_HATCHLING)
                item.set.set("type2", L2Item.TYPE2_PET_HATCHLING);
            else if (item.set.getInteger("bodypart") == L2Item.SLOT_BABYPET)
                item.set.set("type2", L2Item.TYPE2_PET_BABY);
            else
                item.set.set("type2", L2Item.TYPE2_PET_STRIDER);

            item.set.set("bodypart", L2Item.SLOT_R_HAND);
        }

        return item;
    }

}
