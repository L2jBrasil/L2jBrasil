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

import static com.it.br.configuration.Configurator.getSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.model.L2SummonItem;

public class SummonItemsData {
    private static final Logger _log = LoggerFactory.getLogger(SummonItemsData.class);

    private Map<Integer, L2SummonItem> _summonitems;

    private static SummonItemsData _instance;

    public static SummonItemsData getInstance() {
        if (_instance == null) {
            _instance = new SummonItemsData();
        }

        return _instance;
    }

    public SummonItemsData() {
        _summonitems = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        ServerSettings serverSettings = getSettings(ServerSettings.class);
        File f = new File(serverSettings.getDatapackDirectory() + "/data/xml/summon_items.xml");
        if (!f.exists()) {
            _log.warn("Could not be loaded: file {} not found", f.getAbsolutePath());
            return;
        }
        int itemID = 0, npcID = 0;
        byte summonType = 0;
        try {
            InputSource in = new InputSource(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            in.setEncoding("UTF-8");
            Document doc = factory.newDocumentBuilder().parse(in);
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeName().equalsIgnoreCase("list")) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if (d.getNodeName().equalsIgnoreCase("summon_item")) {
                            itemID = Integer.valueOf(d.getAttributes().getNamedItem("itemID").getNodeValue());
                            npcID = Integer.valueOf(d.getAttributes().getNamedItem("npcID").getNodeValue());
                            summonType = Byte.valueOf(d.getAttributes().getNamedItem("summonType").getNodeValue());
                            L2SummonItem summonitem = new L2SummonItem(itemID, npcID, summonType);
                            _summonitems.put(itemID, summonitem);
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

        _log.info("Summon: Loaded {} summon items.", _summonitems.size());
    }

    public L2SummonItem getSummonItem(int itemId) {
        return _summonitems.get(itemId);
    }

    public int[] itemIDs() {
        int size = _summonitems.size();
        int[] result = new int[size];
        int i = 0;

        for (L2SummonItem si : _summonitems.values()) {
            result[i] = si.getItemId();
            i++;
        }
        return result;
    }
}