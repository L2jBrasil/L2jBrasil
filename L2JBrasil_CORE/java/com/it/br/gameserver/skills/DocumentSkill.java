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
package com.it.br.gameserver.skills;

import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.skills.conditions.Condition;
import com.it.br.gameserver.templates.StatsSet;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mkizub
 */
final class DocumentSkill extends DocumentBase {

    public class Skill {
        public int id;
        public String name;
        public StatsSet[] sets;
        public StatsSet[] enchsets1;
        public StatsSet[] enchsets2;
        public int currentLevel;
        public List<L2Skill> skills = new ArrayList<>();
        public List<L2Skill> currentSkills = new ArrayList<>();
    }

    private Skill _currentSkill;
    private List<L2Skill> _skillsInFile = new ArrayList<>();

    DocumentSkill(File file) {
        super(file);
    }

    private void setCurrentSkill(Skill skill) {
        _currentSkill = skill;
    }


    @Override
    protected StatsSet getStatsSet() {
        return _currentSkill.sets[_currentSkill.currentLevel];
    }

    protected List<L2Skill> getSkills() {
        return _skillsInFile;
    }


    @Override
    protected String getTableValue(String name) {
        try {
            return _tables.get(name)[_currentSkill.currentLevel];
        } catch (RuntimeException e) {
            _log.error("Error in table: {} of Skill Id {}. {}", name, _currentSkill.id, e);
            return "";
        }
    }


    @Override
    protected String getTableValue(String name, int idx) {
        try {
            return _tables.get(name)[idx - 1];
        } catch (RuntimeException e) {
            _log.error("wrong level count in skill Id {}. {}", _currentSkill.id, e);
            return "";
        }
    }


    @Override
    protected void parseDocument(Document doc) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("skill".equalsIgnoreCase(d.getNodeName())) {
                        setCurrentSkill(new Skill());
                        parseSkill(d);
                        _skillsInFile.addAll(_currentSkill.skills);
                        resetTable();
                    }
                }
            } else if ("skill".equalsIgnoreCase(n.getNodeName())) {
                setCurrentSkill(new Skill());
                parseSkill(n);
                _skillsInFile.addAll(_currentSkill.skills);
            }
        }
    }

    protected void parseSkill(Node n) {
        NamedNodeMap attrs = n.getAttributes();
        int enchantLevels1 = 0;
        int enchantLevels2 = 0;
        int skillId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
        String skillName = attrs.getNamedItem("name").getNodeValue();
        String levels = attrs.getNamedItem("levels").getNodeValue();
        int lastLvl = Integer.parseInt(levels);
        if (attrs.getNamedItem("enchantLevels1") != null)
            enchantLevels1 = Integer.parseInt(attrs.getNamedItem("enchantLevels1").getNodeValue());
        if (attrs.getNamedItem("enchantLevels2") != null)
            enchantLevels2 = Integer.parseInt(attrs.getNamedItem("enchantLevels2").getNodeValue());

        _currentSkill.id = skillId;
        _currentSkill.name = skillName;
        _currentSkill.sets = new StatsSet[lastLvl];
        _currentSkill.enchsets1 = new StatsSet[enchantLevels1];
        _currentSkill.enchsets2 = new StatsSet[enchantLevels2];

        for (int i = 0; i < lastLvl; i++) {
            _currentSkill.sets[i] = new StatsSet();
            _currentSkill.sets[i].set("skill_id", _currentSkill.id);
            _currentSkill.sets[i].set("level", i + 1);
            _currentSkill.sets[i].set("name", _currentSkill.name);
        }

        if (_currentSkill.sets.length != lastLvl)
            throw new RuntimeException("Skill id=" + skillId + " number of levels missmatch, " + lastLvl + " levels expected");

        Node first = n.getFirstChild();
        for (n = first; n != null; n = n.getNextSibling()) {
            if ("table".equalsIgnoreCase(n.getNodeName()))
                parseTable(n);
        }
        for (int i = 1; i <= lastLvl; i++) {
            for (n = first; n != null; n = n.getNextSibling()) {
                if ("set".equalsIgnoreCase(n.getNodeName()))
                    parseBeanSet(n, _currentSkill.sets[i - 1], i);
            }
        }
        for (int i = 0; i < enchantLevels1; i++) {
            _currentSkill.enchsets1[i] = new StatsSet();
            _currentSkill.enchsets1[i].set("skill_id", _currentSkill.id);
            //currentSkill.enchsets1[i] = currentSkill.sets[currentSkill.sets.length-1];
            _currentSkill.enchsets1[i].set("level", i + 101);
            _currentSkill.enchsets1[i].set("name", _currentSkill.name);
            //currentSkill.enchsets1[i].set("skillType",     "NOTDONE");

            for (n = first; n != null; n = n.getNextSibling()) {
                if ("set".equalsIgnoreCase(n.getNodeName()))
                    parseBeanSet(n, _currentSkill.enchsets1[i], _currentSkill.sets.length);
            }

            for (n = first; n != null; n = n.getNextSibling()) {
                if ("enchant1".equalsIgnoreCase(n.getNodeName()))
                    parseBeanSet(n, _currentSkill.enchsets1[i], i + 1);
            }
        }

        if (_currentSkill.enchsets1.length != enchantLevels1)
            throw new RuntimeException("Skill id=" + skillId + " number of levels missmatch, " + enchantLevels1 + " levels expected");

        for (int i = 0; i < enchantLevels2; i++) {
            _currentSkill.enchsets2[i] = new StatsSet();
            //currentSkill.enchsets2[i] = currentSkill.sets[currentSkill.sets.length-1];
            _currentSkill.enchsets2[i].set("skill_id", _currentSkill.id);
            _currentSkill.enchsets2[i].set("level", i + 141);
            _currentSkill.enchsets2[i].set("name", _currentSkill.name);
            //currentSkill.enchsets2[i].set("skillType",     "NOTDONE");

            for (n = first; n != null; n = n.getNextSibling()) {
                if ("set".equalsIgnoreCase(n.getNodeName()))
                    parseBeanSet(n, _currentSkill.enchsets2[i], _currentSkill.sets.length);
            }

            for (n = first; n != null; n = n.getNextSibling()) {
                if ("enchant2".equalsIgnoreCase(n.getNodeName()))
                    parseBeanSet(n, _currentSkill.enchsets2[i], i + 1);
            }
        }

        if (_currentSkill.enchsets2.length != enchantLevels2)
            throw new RuntimeException("Skill id=" + skillId + " number of levels missmatch, " + enchantLevels2 + " levels expected");

        makeSkills();
        for (int i = 0; i < lastLvl; i++) {
            _currentSkill.currentLevel = i;
            for (n = first; n != null; n = n.getNextSibling()) {
                if ("cond".equalsIgnoreCase(n.getNodeName())) {
                    Condition condition = parseCondition(n.getFirstChild(), _currentSkill.currentSkills.get(i));
                    Node msg = n.getAttributes().getNamedItem("msg");
                    if (condition != null && msg != null)
                        condition.setMessage(msg.getNodeValue());
                    _currentSkill.currentSkills.get(i).attach(condition, false);
                }
                if ("for".equalsIgnoreCase(n.getNodeName())) {
                    parseTemplate(n, _currentSkill.currentSkills.get(i));
                }
            }
        }
        for (int i = lastLvl; i < lastLvl + enchantLevels1; i++) {
            _currentSkill.currentLevel = i - lastLvl;
            boolean found = false;
            for (n = first; n != null; n = n.getNextSibling()) {
                if ("enchant1cond".equalsIgnoreCase(n.getNodeName())) {
                    found = true;
                    Condition condition = parseCondition(n.getFirstChild(), _currentSkill.currentSkills.get(i));
                    Node msg = n.getAttributes().getNamedItem("msg");
                    if (condition != null && msg != null)
                        condition.setMessage(msg.getNodeValue());
                    _currentSkill.currentSkills.get(i).attach(condition, false);
                }
                if ("enchant1for".equalsIgnoreCase(n.getNodeName())) {
                    found = true;
                    parseTemplate(n, _currentSkill.currentSkills.get(i));
                }
            }
            // If none found, the enchanted skill will take effects from maxLvL of norm skill
            if (!found) {
                _currentSkill.currentLevel = lastLvl - 1;
                for (n = first; n != null; n = n.getNextSibling()) {
                    if ("cond".equalsIgnoreCase(n.getNodeName())) {
                        Condition condition = parseCondition(n.getFirstChild(), _currentSkill.currentSkills.get(i));
                        Node msg = n.getAttributes().getNamedItem("msg");
                        if (condition != null && msg != null)
                            condition.setMessage(msg.getNodeValue());
                        _currentSkill.currentSkills.get(i).attach(condition, false);
                    }
                    if ("for".equalsIgnoreCase(n.getNodeName())) {
                        parseTemplate(n, _currentSkill.currentSkills.get(i));
                    }
                }
            }
        }
        for (int i = lastLvl + enchantLevels1; i < lastLvl + enchantLevels1 + enchantLevels2; i++) {
            boolean found = false;
            _currentSkill.currentLevel = i - lastLvl - enchantLevels1;
            for (n = first; n != null; n = n.getNextSibling()) {
                if ("enchant2cond".equalsIgnoreCase(n.getNodeName())) {
                    found = true;
                    Condition condition = parseCondition(n.getFirstChild(), _currentSkill.currentSkills.get(i));
                    Node msg = n.getAttributes().getNamedItem("msg");
                    if (condition != null && msg != null)
                        condition.setMessage(msg.getNodeValue());
                    _currentSkill.currentSkills.get(i).attach(condition, false);
                }
                if ("enchant2for".equalsIgnoreCase(n.getNodeName())) {
                    found = true;
                    parseTemplate(n, _currentSkill.currentSkills.get(i));
                }
            }
            // If none found, the enchanted skill will take effects from maxLvL of norm skill
            if (!found) {
                _currentSkill.currentLevel = lastLvl - 1;
                for (n = first; n != null; n = n.getNextSibling()) {
                    if ("cond".equalsIgnoreCase(n.getNodeName())) {
                        Condition condition = parseCondition(n.getFirstChild(), _currentSkill.currentSkills.get(i));
                        Node msg = n.getAttributes().getNamedItem("msg");
                        if (condition != null && msg != null)
                            condition.setMessage(msg.getNodeValue());
                        _currentSkill.currentSkills.get(i).attach(condition, false);
                    }
                    if ("for".equalsIgnoreCase(n.getNodeName())) {
                        parseTemplate(n, _currentSkill.currentSkills.get(i));
                    }
                }
            }
        }
        _currentSkill.skills.addAll(_currentSkill.currentSkills);
    }

    private void makeSkills() {
        int count = 0;
        _currentSkill.currentSkills = new ArrayList<>(_currentSkill.sets.length + _currentSkill.enchsets1.length + _currentSkill.enchsets2.length);

        for (int i = 0; i < _currentSkill.sets.length; i++) {
            try {
                _currentSkill.currentSkills.add(i, _currentSkill.sets[i].getEnum("skillType", SkillType.class).makeSkill(_currentSkill.sets[i]));
                count++;
            } catch (Exception e) {
                L2Skill skill = _currentSkill.sets[i].getEnum("skillType", SkillType.class).makeSkill(_currentSkill.sets[i]);
                _log.error("Skill id={} level {}. {}", skill.getDisplayId(), skill.getLevel(), e);
            }
        }
        int _count = count;
        for (int i = 0; i < _currentSkill.enchsets1.length; i++) {
            try {
                _currentSkill.currentSkills.add(_count + i, _currentSkill.enchsets1[i].getEnum("skillType", SkillType.class).makeSkill(_currentSkill.enchsets1[i]));
                count++;
            } catch (Exception e) {
                L2Skill skill = _currentSkill.enchsets1[i].getEnum("skillType", SkillType.class).makeSkill(_currentSkill.enchsets1[i]);
                _log.error("Skill id={} level={}. {}", skill.getDisplayId(), skill.getLevel(), e);
            }
        }
        _count = count;
        for (int i = 0; i < _currentSkill.enchsets2.length; i++) {
            try {
                _currentSkill.currentSkills.add(_count + i, _currentSkill.enchsets2[i].getEnum("skillType", SkillType.class).makeSkill(_currentSkill.enchsets2[i]));
                count++;
            } catch (Exception e) {
                L2Skill skill = _currentSkill.enchsets2[i].getEnum("skillType", SkillType.class).makeSkill(_currentSkill.enchsets2[i]);
                _log.error("Skill id={} level={}", skill.getDisplayId(), skill.getLevel(), e);
            }
        }
    }
}
