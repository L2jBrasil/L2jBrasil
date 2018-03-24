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
import com.it.br.gameserver.model.FishData;
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
import java.util.ArrayList;
import java.util.List;

public class FishTable {
    private static final Logger _log = LoggerFactory.getLogger(SkillTreeTable.class);

    static class instance {
        static final FishTable _instance = new FishTable();
    }

    private static List<FishData> _fishsNormal, _fishsEasy, _fishsHard;

    public static FishTable getInstance() {
        return instance._instance;
    }

    public FishTable() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        int count = 0;
        ServerSettings serverSettings = Configurator.getSettings(ServerSettings.class);
        File f = new File(serverSettings.getDatapackDirectory() + "/data/xml/fish.xml");
        if (!f.exists()) {
            _log.warn("fish.xml could not be loaded: file {} not found", f.getAbsolutePath());
            return;
        }
        try {
            _fishsEasy = new ArrayList<>();
            _fishsNormal = new ArrayList<>();
            _fishsHard = new ArrayList<>();
            FishData fish;

            InputSource in = new InputSource(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            in.setEncoding("UTF-8");
            Document doc = factory.newDocumentBuilder().parse(in);
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeName().equalsIgnoreCase("list")) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if (d.getNodeName().equalsIgnoreCase("fish")) {
                            int id = Integer.valueOf(d.getAttributes().getNamedItem("id").getNodeValue());
                            int lvl = Integer.valueOf(d.getAttributes().getNamedItem("level").getNodeValue());
                            String name = String.valueOf(d.getAttributes().getNamedItem("name").getNodeValue());
                            int hp = Integer.valueOf(d.getAttributes().getNamedItem("hp").getNodeValue());
                            int hpreg = Integer.valueOf(d.getAttributes().getNamedItem("hpregen").getNodeValue());
                            int type = Integer.valueOf(d.getAttributes().getNamedItem("fish_type").getNodeValue());
                            int group = Integer.valueOf(d.getAttributes().getNamedItem("fish_group").getNodeValue());
                            int fish_guts = Integer.valueOf(d.getAttributes().getNamedItem("fish_guts").getNodeValue());
                            int guts_check_time = Integer.valueOf(d.getAttributes().getNamedItem("guts_check_time").getNodeValue());
                            int wait_time = Integer.valueOf(d.getAttributes().getNamedItem("wait_time").getNodeValue());
                            int combat_time = Integer.valueOf(d.getAttributes().getNamedItem("combat_time").getNodeValue());

                            fish = new FishData(id, lvl, name, hp, hpreg, type, group, fish_guts, guts_check_time, wait_time, combat_time);
                            switch (fish.getGroup()) {
                                case 0:
                                    _fishsEasy.add(fish);
                                    break;
                                case 1:
                                    _fishsNormal.add(fish);
                                    break;
                                case 2:
                                    _fishsHard.add(fish);
                            }
                        }

                        count = _fishsEasy.size() + _fishsNormal.size() + _fishsHard.size();
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

        _log.info("FishTable: Loaded {} easy fishes.", _fishsEasy.size());
        _log.info("FishTable: Loaded {} normal fishes.", _fishsNormal.size());
        _log.info("FishTable: Loaded {} hard fishes.", _fishsHard.size());
        _log.info("FishTable: Loaded {} fishes.", count);
    }

    public List<FishData> getfish(int lvl, int type, int group) {
        List<FishData> result = new ArrayList<>();
        List<FishData> _Fishs = null;

        switch (group) {
            case 0:
                _Fishs = _fishsEasy;
                break;
            case 1:
                _Fishs = _fishsNormal;
                break;
            case 2:
                _Fishs = _fishsHard;
        }

        if (_Fishs == null) {
            _log.warn("Fish are not defined!");
            return null;
        }

        for (FishData f : _Fishs) {
            if (f.getLevel() != lvl) {
                continue;
            }

            if (f.getType() != type) {
                continue;
            }

            result.add(f);
        }

        if (result.size() == 0) {
            _log.warn("Can't Find Any Fish!? - Lvl: {} Type: {}", lvl, type);
        }

        _Fishs = null;

        return result;
    }
}