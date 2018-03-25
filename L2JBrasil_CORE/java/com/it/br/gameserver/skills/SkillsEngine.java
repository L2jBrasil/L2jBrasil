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

import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.Item;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.templates.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.it.br.configuration.Configurator.getSettings;

/**
 * @author mkizub
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SkillsEngine 
{
    protected static final Logger _log = LoggerFactory.getLogger(SkillsEngine.class);

	private static final SkillsEngine _instance = new SkillsEngine();

	private List<File> _armorFiles     = new ArrayList<>();
	private List<File> _weaponFiles    = new ArrayList<>();
	private List<File> _etcitemFiles   = new ArrayList<>();
	private List<File> _skillFiles     = new ArrayList<>();

    public static SkillsEngine getInstance()
	{
		return _instance;
	}

	private SkillsEngine()
	{
		//hashFiles("data/stats/etcitem", _etcitemFiles);
		hashFiles("data/stats/armor", _armorFiles);
		hashFiles("data/stats/weapon", _weaponFiles);
		hashFiles("data/stats/skills", _skillFiles);
	}

	private void hashFiles(String dirname, List<File> hash)
	{
		ServerSettings serverSettings = getSettings(ServerSettings.class);
		File datapack = serverSettings.getDatapackDirectory();
		File dir = new File(datapack, dirname);
		if (!dir.exists())
		{
			_log.info("Dir "+dir.getAbsolutePath()+" not exists");
			return;
		}
		File[] files = dir.listFiles();
		for (File f : files)
		{
			if (f.getName().endsWith(".xml"))
			    if (!f.getName().startsWith("custom"))
				hash.add(f);
		}
		File customfile = new File(datapack, dirname+"/custom.xml");
		if (customfile.exists())
		    hash.add(customfile);
	}

	public List<L2Skill> loadSkills(File file)
	{
		if (file == null)
		{
			_log.info("Skill file not found.");
			return null;
		}
		DocumentSkill doc = new DocumentSkill(file);
		doc.parse();
		return doc.getSkills();
	}

	public void loadAllSkills(Map<Integer, L2Skill> allSkills)
	{
		int count = 0;
		for (File file : _skillFiles)
		{
			List<L2Skill> s  = loadSkills(file);
			if (s == null)
				continue;
			for (L2Skill skill : s)
            {
				allSkills.put(SkillTable.getSkillHashCode(skill), skill);
				count++;
            }
		}
		_log.info("SkillsEngine: Loaded "+count+" Skill templates from XML files.");
	}

    public List<L2Armor> loadArmors(Map<Integer, Item> armorData)
    {
        List<L2Armor> list  = new ArrayList<>();
        for (L2Item item : loadData(armorData, _armorFiles))
        {
            list.add((L2Armor)item);
        }
        return list;
    }

    public List<L2Weapon> loadWeapons(Map<Integer, Item> weaponData)
    {
        List<L2Weapon> list  = new ArrayList<>();
        for (L2Item item : loadData(weaponData, _weaponFiles))
        {
            list.add((L2Weapon)item);
        }
        return list;
    }

    public List<L2EtcItem> loadItems(Map<Integer, Item> itemData)
    {
        List<L2EtcItem> list  = new ArrayList<>();
        for (L2Item item : loadData(itemData, _etcitemFiles))
        {
            list.add((L2EtcItem)item);
        }
        if (list.size() == 0)
        {
            for (Item item : itemData.values())
            {
                list.add(new L2EtcItem((L2EtcItemType)item.type, item.set));
            }
        }
        return list;
    }

    public List<L2Item> loadData(Map<Integer, Item> itemData, List<File> files)
    {
        List<L2Item> list  = new ArrayList<>();
        for (File f : files)
        {
            DocumentItem document   = new DocumentItem(itemData, f);
            document.parse();
            list.addAll(document.getItemList());
        }
        return list;
    }
}
