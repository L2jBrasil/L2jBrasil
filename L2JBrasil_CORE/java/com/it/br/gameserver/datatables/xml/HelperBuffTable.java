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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.it.br.configuration.Configurator;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.templates.L2HelperBuff;
import com.it.br.gameserver.templates.StatsSet;

public class HelperBuffTable
{
	private static final Log _log = LogFactory.getLog(HelperBuffTable.class.getName());

	private List<L2HelperBuff> _helperBuff;
	private boolean _initialized = false;
	private int _magicClassLowestLevel = 100;
	private int _physicClassLowestLevel = 100;

	private int _magicClassHighestLevel = 1;
	private int _physicClassHighestLevel = 1;

	public static HelperBuffTable getInstance()
	{
		return SingletonHolder._instance;
	}

	private HelperBuffTable()
	{
		_helperBuff = new ArrayList<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		ServerSettings serverSettings = Configurator.getSettings(ServerSettings.class);
		File f = new File(serverSettings.getDatapackDirectory() + "/data/xml/helper_buff_list.xml");
		if(!f.exists())
		{
			_log.warn("HelperBuffTable: helper_buff_list.xml could not be loaded: file not found");
			return;
		}
		try
		{
			InputSource in = new InputSource(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			in.setEncoding("UTF-8");
			Document doc = factory.newDocumentBuilder().parse(in);
			for(Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if(n.getNodeName().equalsIgnoreCase("list"))
				{
					for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if(d.getNodeName().equalsIgnoreCase("buff"))
						{
							int id = Integer.valueOf(d.getAttributes().getNamedItem("id").getNodeValue());
							int skill_id = Integer.valueOf(d.getAttributes().getNamedItem("skill_id").getNodeValue());
							int skill_level = Integer.valueOf(d.getAttributes().getNamedItem("skill_level").getNodeValue());
							int lower_level = Integer.valueOf(d.getAttributes().getNamedItem("lower_level").getNodeValue());
							int upper_level = Integer.valueOf(d.getAttributes().getNamedItem("upper_level").getNodeValue());
							boolean is_magic_class = Boolean.valueOf(d.getAttributes().getNamedItem("is_magic_class").getNodeValue());

							StatsSet helperBuffDat = new StatsSet();

							helperBuffDat.set("id", id);
							helperBuffDat.set("skillID", skill_id);
							helperBuffDat.set("skillLevel", skill_level);
							helperBuffDat.set("lowerLevel", lower_level);
							helperBuffDat.set("upperLevel", upper_level);
							helperBuffDat.set("isMagicClass", is_magic_class);
							
							if(!is_magic_class)
							{
								if(lower_level < _physicClassLowestLevel)
								{
									_physicClassLowestLevel = lower_level;
								}

								if(upper_level > _physicClassHighestLevel)
								{
									_physicClassHighestLevel = upper_level;
								}
							}
							else
							{
								if(lower_level < _magicClassLowestLevel)
								{
									_magicClassLowestLevel = lower_level;
								}

								if(upper_level > _magicClassHighestLevel)
								{
									_magicClassHighestLevel = upper_level;
								}
							}

							// Add this Helper Buff to the Helper Buff List
							L2HelperBuff template = new L2HelperBuff(helperBuffDat);
							_helperBuff.add(template);
						}
					}
				}
			}
		}
		catch (Exception e)
        {
            _log.error("Error while creating table", e);
        }
		_log.info("HelperBuffTable: Loaded " + _helperBuff.size() + " buffs.");
        _initialized = true;
	}

	public List<L2HelperBuff> getHelperBuffTable()
	{
		return _helperBuff;
	}

	public int getMagicClassHighestLevel()
	{
		return _magicClassHighestLevel;
	}

	public int getMagicClassLowestLevel()
	{
		return _magicClassLowestLevel;
	}

	public int getPhysicClassHighestLevel()
	{
		return _physicClassHighestLevel;
	}

	public int getPhysicClassLowestLevel()
	{
		return _physicClassLowestLevel;
	}

	private static class SingletonHolder
	{
		protected static final HelperBuffTable _instance = new HelperBuffTable();
	}
	
	public boolean isInitialized()
	{
		return _initialized;
	}

}