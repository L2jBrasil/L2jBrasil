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

import com.it.br.Config;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.model.L2Skill;
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
import java.util.HashMap;
import java.util.Map;

import static com.it.br.configuration.Configurator.getSettings;

public class SkillSpellbookTable {
    private static final Logger _log = LoggerFactory.getLogger(SkillSpellbookTable.class);

    private static SkillSpellbookTable _instance;

    private static Map<Integer, Integer> _skillSpellbooks;

    public static SkillSpellbookTable getInstance() {
        if (_instance == null) {
            _instance = new SkillSpellbookTable();
        }

        return _instance;
    }

    private SkillSpellbookTable() {
        if (!Config.SP_BOOK_NEEDED)
            return;

        _skillSpellbooks = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        ServerSettings serverSettings = getSettings(ServerSettings.class);
        File f = new File(serverSettings.getDatapackDirectory() + "/data/xml/spellbooks.xml");
        if (!f.exists()) {
            _log.warn("skill_spellbooks.xml could not be loaded: file {} not found", f.getAbsolutePath());
            return;
        }
        try {
            InputSource in = new InputSource(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            in.setEncoding("UTF-8");
            Document doc = factory.newDocumentBuilder().parse(in);
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeName().equalsIgnoreCase("list")) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if (d.getNodeName().equalsIgnoreCase("book")) {
                            _skillSpellbooks.put(Integer.valueOf(d.getAttributes().getNamedItem("skill_id").getNodeValue()), Integer.valueOf(d.getAttributes().getNamedItem("item_id").getNodeValue()));
                        }
                    }
                }
            }
        } catch (SAXException e) {
            _log.error("Error while creating table", e);
        } catch (IOException e) {
            _log.error("Error while creating table", e);
        } catch (ParserConfigurationException e) {
            _log.error("Error while creating table", e);
        }

        _log.info("SkillSpellbookTable: Loaded {} spellbooks", _skillSpellbooks.size());
    }

    public int getBookForSkill(int skillId, int level) {
        if (skillId == L2Skill.SKILL_DIVINE_INSPIRATION && level != -1) {
            switch (level) {
                case 1:
                    return 8618; // Ancient Book - Divine Inspiration (Modern Language Version)
                case 2:
                    return 8619; // Ancient Book - Divine Inspiration (Original Language Version)
                case 3:
                    return 8620; // Ancient Book - Divine Inspiration (Manuscript)
                case 4:
                    return 8621; // Ancient Book - Divine Inspiration (Original Version)
                default:
                    return -1;
            }
        }

        if (!_skillSpellbooks.containsKey(skillId))
            return -1;

        return _skillSpellbooks.get(skillId);
    }

    public int getBookForSkill(L2Skill skill) {
        return getBookForSkill(skill.getId(), -1);
    }

    public int getBookForSkill(L2Skill skill, int level) {
        return getBookForSkill(skill.getId(), level);
    }
}