/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.it.br.gameserver.datatables.xml;

import com.it.br.configuration.Configurator;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.model.base.ClassId;
import com.it.br.gameserver.templates.L2PcTemplate;
import com.it.br.gameserver.templates.StatsSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CharTemplateTable {
    private static final Logger _log = LoggerFactory.getLogger(CharTemplateTable.class);

    private static CharTemplateTable _instance;

    private static final String[] CHAR_CLASSES =
            {
                    "Human Fighter",
                    "Warrior",
                    "Gladiator",
                    "Warlord",
                    "Human Knight",
                    "Paladin",
                    "Dark Avenger",
                    "Rogue",
                    "Treasure Hunter",
                    "Hawkeye",
                    "Human Mystic",
                    "Human Wizard",
                    "Sorceror",
                    "Necromancer",
                    "Warlock",
                    "Cleric",
                    "Bishop",
                    "Prophet",
                    "Elven Fighter",
                    "Elven Knight",
                    "Temple Knight",
                    "Swordsinger",
                    "Elven Scout",
                    "Plainswalker",
                    "Silver Ranger",
                    "Elven Mystic",
                    "Elven Wizard",
                    "Spellsinger",
                    "Elemental Summoner",
                    "Elven Oracle",
                    "Elven Elder",
                    "Dark Fighter",
                    "Palus Knight",
                    "Shillien Knight",
                    "Bladedancer",
                    "Assassin",
                    "Abyss Walker",
                    "Phantom Ranger",
                    "Dark Elven Mystic",
                    "Dark Elven Wizard",
                    "Spellhowler",
                    "Phantom Summoner",
                    "Shillien Oracle",
                    "Shillien Elder",
                    "Orc Fighter",
                    "Orc Raider",
                    "Destroyer",
                    "Orc Monk",
                    "Tyrant",
                    "Orc Mystic",
                    "Orc Shaman",
                    "Overlord",
                    "Warcryer",
                    "Dwarven Fighter",
                    "Dwarven Scavenger",
                    "Bounty Hunter",
                    "Dwarven Artisan",
                    "Warsmith",
                    "dummyEntry1",
                    "dummyEntry2",
                    "dummyEntry3",
                    "dummyEntry4",
                    "dummyEntry5",
                    "dummyEntry6",
                    "dummyEntry7",
                    "dummyEntry8",
                    "dummyEntry9",
                    "dummyEntry10",
                    "dummyEntry11",
                    "dummyEntry12",
                    "dummyEntry13",
                    "dummyEntry14",
                    "dummyEntry15",
                    "dummyEntry16",
                    "dummyEntry17",
                    "dummyEntry18",
                    "dummyEntry19",
                    "dummyEntry20",
                    "dummyEntry21",
                    "dummyEntry22",
                    "dummyEntry23",
                    "dummyEntry24",
                    "dummyEntry25",
                    "dummyEntry26",
                    "dummyEntry27",
                    "dummyEntry28",
                    "dummyEntry29",
                    "dummyEntry30",
                    "Duelist",
                    "DreadNought",
                    "Phoenix Knight",
                    "Hell Knight",
                    "Sagittarius",
                    "Adventurer",
                    "Archmage",
                    "Soultaker",
                    "Arcana Lord",
                    "Cardinal",
                    "Hierophant",
                    "Eva Templar",
                    "Sword Muse",
                    "Wind Rider",
                    "Moonlight Sentinel",
                    "Mystic Muse",
                    "Elemental Master",
                    "Eva's Saint",
                    "Shillien Templar",
                    "Spectral Dancer",
                    "Ghost Hunter",
                    "Ghost Sentinel",
                    "Storm Screamer",
                    "Spectral Master",
                    "Shillien Saint",
                    "Titan",
                    "Grand Khauatari",
                    "Dominator",
                    "Doomcryer",
                    "Fortune Seeker",
                    "Maestro"
            };

    private Map<Integer, L2PcTemplate> _templates;

    public static CharTemplateTable getInstance() {
        if (_instance == null) {
            _instance = new CharTemplateTable();
        }

        return _instance;
    }

    private CharTemplateTable() {
        _templates = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        ServerSettings serverSettings = Configurator.getSettings(ServerSettings.class);

        File f = new File(serverSettings.getDatapackDirectory() + "/data/xml/char_template.xml");
        if (!f.exists()) {
            _log.warn("Could not be loaded: file {} not found", f.getAbsolutePath());
            return;
        }
        try {
            InputSource in = new InputSource(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            in.setEncoding("UTF-8");
            Document doc = factory.newDocumentBuilder().parse(in);
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeName().equalsIgnoreCase("list")) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if (d.getNodeName().equalsIgnoreCase("class")) {
                            StatsSet set = new StatsSet();
                            int ID = Integer.valueOf(d.getAttributes().getNamedItem("Id").getNodeValue());
                            String NAME = String.valueOf(d.getAttributes().getNamedItem("name").getNodeValue());
                            int race_id = Integer.valueOf(d.getAttributes().getNamedItem("RaceId").getNodeValue());
                            for (Node t = d.getFirstChild(); t != null; t = t.getNextSibling()) {
                                if (t.getNodeName().equalsIgnoreCase("stats")) {

                                    int STR = Integer.valueOf(t.getAttributes().getNamedItem("str").getNodeValue());
                                    int CON = Integer.valueOf(t.getAttributes().getNamedItem("con").getNodeValue());
                                    int DEX = Integer.valueOf(t.getAttributes().getNamedItem("dex").getNodeValue());
                                    int INT = Integer.valueOf(t.getAttributes().getNamedItem("_int").getNodeValue());
                                    int WIT = Integer.valueOf(t.getAttributes().getNamedItem("wit").getNodeValue());
                                    int MEN = Integer.valueOf(t.getAttributes().getNamedItem("men").getNodeValue());
                                    int PA = Integer.valueOf(t.getAttributes().getNamedItem("p_atk").getNodeValue());
                                    int PD = Integer.valueOf(t.getAttributes().getNamedItem("p_def").getNodeValue());
                                    int MA = Integer.valueOf(t.getAttributes().getNamedItem("m_atk").getNodeValue());
                                    int MD = Integer.valueOf(t.getAttributes().getNamedItem("m_def").getNodeValue());
                                    int PS = Integer.valueOf(t.getAttributes().getNamedItem("p_spd").getNodeValue());
                                    int MS = Integer.valueOf(t.getAttributes().getNamedItem("m_spd").getNodeValue());
                                    int CR = Integer.valueOf(t.getAttributes().getNamedItem("critical").getNodeValue());
                                    int MSP = Integer.valueOf(t.getAttributes().getNamedItem("move_spd").getNodeValue());
                                    int X = Integer.valueOf(t.getAttributes().getNamedItem("x").getNodeValue());
                                    int Y = Integer.valueOf(t.getAttributes().getNamedItem("y").getNodeValue());
                                    int Z = Integer.valueOf(t.getAttributes().getNamedItem("z").getNodeValue());

                                    double COL_R = Double.valueOf(t.getAttributes().getNamedItem("m_col_r").getNodeValue());
                                    double COL_H = Double.valueOf(t.getAttributes().getNamedItem("m_col_h").getNodeValue());
                                    for (Node h = t.getFirstChild(); h != null; h = h.getNextSibling()) {
                                        if (h.getNodeName().equalsIgnoreCase("lvlup")) {
                                            float HPBASE = Float.valueOf(h.getAttributes().getNamedItem("hpbase").getNodeValue());
                                            float HPADD = Float.valueOf(h.getAttributes().getNamedItem("hpadd").getNodeValue());
                                            float HPMOD = Float.valueOf(h.getAttributes().getNamedItem("hpmod").getNodeValue());
                                            float MPBASE = Float.valueOf(h.getAttributes().getNamedItem("mpbase").getNodeValue());
                                            float CPBASE = Float.valueOf(h.getAttributes().getNamedItem("cpbase").getNodeValue());
                                            float CPADD = Float.valueOf(h.getAttributes().getNamedItem("cpadd").getNodeValue());
                                            float CPMOD = Float.valueOf(h.getAttributes().getNamedItem("cpmod").getNodeValue());
                                            float MPADD = Float.valueOf(h.getAttributes().getNamedItem("mpadd").getNodeValue());
                                            float MPMOD = Float.valueOf(h.getAttributes().getNamedItem("mpmod").getNodeValue());
                                            int lvl = Integer.valueOf(h.getAttributes().getNamedItem("class_lvl").getNodeValue());
                                            for (Node q = h.getFirstChild(); q != null; q = q.getNextSibling()) {
                                                if (q.getNodeName().equalsIgnoreCase("item")) {
                                                    set.set("classId", ID);
                                                    set.set("className", NAME);
                                                    set.set("raceId", race_id);
                                                    set.set("baseSTR", STR);
                                                    set.set("baseCON", CON);
                                                    set.set("baseDEX", DEX);
                                                    set.set("baseINT", INT);
                                                    set.set("baseWIT", WIT);
                                                    set.set("baseMEN", MEN);
                                                    set.set("baseHpReg", 1.5);
                                                    set.set("baseMpReg", 0.9);
                                                    set.set("basePAtk", PA);
                                                    set.set("basePDef", PD);
                                                    set.set("baseMAtk", MA);
                                                    set.set("baseMDef", MD);
                                                    set.set("basePAtkSpd", PS);
                                                    set.set("baseMAtkSpd", MS);
                                                    set.set("baseCritRate", CR / 10);
                                                    set.set("baseRunSpd", MSP);
                                                    set.set("baseWalkSpd", 0);
                                                    set.set("baseShldDef", 0);
                                                    set.set("baseShldRate", 0);
                                                    set.set("baseAtkRange", 40);
                                                    set.set("spawnX", X);
                                                    set.set("spawnY", Y);
                                                    set.set("spawnZ", Z);
                                                    set.set("collision_radius", COL_R);
                                                    set.set("collision_height", COL_H);
                                                    set.set("baseHpMax", HPBASE);
                                                    set.set("lvlHpAdd", HPADD);
                                                    set.set("lvlHpMod", HPMOD);
                                                    set.set("baseMpMax", MPBASE);
                                                    set.set("baseCpMax", CPBASE);
                                                    set.set("lvlCpAdd", CPADD);
                                                    set.set("lvlCpMod", CPMOD);
                                                    set.set("lvlMpAdd", MPADD);
                                                    set.set("lvlMpMod", MPMOD);
                                                    set.set("classBaseLevel", lvl);
                                                    //ct = new L2PcTemplate(set);

                                                    L2PcTemplate ct = new L2PcTemplate(set);
                                                    try (Connection con = L2DatabaseFactory.getInstance().getConnection();) {
                                                        PreparedStatement statement2 = con.prepareStatement("SELECT classId, itemId, amount, equipped FROM char_creation_items");
                                                        ResultSet rset2 = statement2.executeQuery();

                                                        int classId, itemId, amount;
                                                        boolean equipped;
                                                        while (rset2.next()) {
                                                            classId = rset2.getInt("classId");
                                                            itemId = rset2.getInt("itemId");
                                                            amount = rset2.getInt("amount");
                                                            equipped = rset2.getString("equipped").equals("true");

                                                            if (ItemTable.getInstance().getTemplate(itemId) != null) {
                                                                if (classId == -1) {
                                                                    ct.addItem(itemId, amount, equipped);
                                                                } else if (classId == ID) {
                                                                    ct.addItem(itemId, amount, equipped);
                                                                }
                                                            } else {
                                                                _log.warn("char_creation_items: No data for itemId {}, defined for classId {}", itemId, classId);
                                                            }
                                                        }
                                                        rset2.close();
                                                        statement2.close();
                                                        rset2 = null;
                                                        statement2 = null;
                                                    } catch (SQLException e) {
                                                        _log.warn("error while loading char templates: {}", e.getMessage());
                                                    }

                                                    _templates.put(ct.classId.getId(), ct);
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SAXException e) {
            _log.error("error while loading char templates", e);
        } catch (IOException e) {
            _log.error("error while loading char templates", e);
        } catch (ParserConfigurationException e) {
            _log.error("error while loading char templates", e);
        }

        _log.info("CharTemplateTable: Loaded {} character templates", _templates.size());
    }

    public L2PcTemplate getTemplate(ClassId classId) {
        return getTemplate(classId.getId());
    }

    public L2PcTemplate getTemplate(int classId) {
        int key = classId;

        return _templates.get(key);
    }

    public static final String getClassNameById(int classId) {
        return CHAR_CLASSES[classId];
    }

    public static final int getClassIdByName(String className) {
        int currId = 1;

        for (String name : CHAR_CLASSES) {
            if (name.equalsIgnoreCase(className)) {
                break;
            }

            currId++;
        }

        return currId;
    }
}