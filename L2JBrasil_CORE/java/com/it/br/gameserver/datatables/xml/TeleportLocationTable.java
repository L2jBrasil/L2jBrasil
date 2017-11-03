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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.it.br.Config;
import com.it.br.gameserver.model.L2TeleportLocation;

public class TeleportLocationTable
{
	private static final Log _log = LogFactory.getLog(TeleportLocationTable.class.getName());

	private static TeleportLocationTable _instance;

	private Map<Integer, L2TeleportLocation> _teleports;

	public static TeleportLocationTable getInstance()
	{
		if(_instance == null)
		{
			_instance = new TeleportLocationTable();
		}

		return _instance;
	}

	private TeleportLocationTable()
	{
		reloadAll();
	}

	public void reloadAll()
	{
		_teleports = new HashMap<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		File f = new File(Config.DATAPACK_ROOT + "/data/xml/teleports.xml");
		if(!f.exists())
		{
			_log.warn("teleports.xml could not be loaded: file not found");
			return;
		}
		try
		{
			InputSource in = new InputSource(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			in.setEncoding("UTF-8");
			Document doc = factory.newDocumentBuilder().parse(in);
			L2TeleportLocation teleport;
			for(Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if(n.getNodeName().equalsIgnoreCase("list"))
				{
					for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if(d.getNodeName().equalsIgnoreCase("teleport"))
						{
							teleport = new L2TeleportLocation();
							int id = Integer.valueOf(d.getAttributes().getNamedItem("id").getNodeValue());
							int loc_x = Integer.valueOf(d.getAttributes().getNamedItem("loc_x").getNodeValue());
							int loc_y = Integer.valueOf(d.getAttributes().getNamedItem("loc_y").getNodeValue());
							int loc_z = Integer.valueOf(d.getAttributes().getNamedItem("loc_z").getNodeValue());
							int price = Integer.valueOf(d.getAttributes().getNamedItem("price").getNodeValue());
							int fornoble = Integer.valueOf(d.getAttributes().getNamedItem("fornoble").getNodeValue());

							teleport.setTeleId(id);
							teleport.setLocX(loc_x);
							teleport.setLocY(loc_y);
							teleport.setLocZ(loc_z);
							teleport.setPrice(price);
							teleport.setIsForNoble(fornoble == 1);

							_teleports.put(teleport.getTeleId(), teleport);
							teleport = null;
						}
					}
				}
			}
		}
		catch(SAXException e)
		{
			_log.error("Error while creating table", e);
		}
		catch(IOException e)
		{
			_log.error("Error while creating table", e);
		}
		catch(ParserConfigurationException e)
		{
			_log.error("Error while creating table", e);
		}

		_log.info("TeleportLocationTable: Loaded " + _teleports.size() + " teleport location templates.");
	}
	public L2TeleportLocation getTemplate(int id)
	{
		return _teleports.get(id);
	}
}