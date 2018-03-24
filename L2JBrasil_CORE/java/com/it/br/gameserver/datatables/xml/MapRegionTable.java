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
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.instancemanager.ArenaManager;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.instancemanager.ClanHallManager;
import com.it.br.gameserver.instancemanager.TownManager;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.Location;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.model.entity.ClanHall;
import com.it.br.gameserver.model.zone.type.L2ArenaZone;
import com.it.br.gameserver.model.zone.type.L2ClanHallZone;

public class MapRegionTable {
    private static final Logger _log = LoggerFactory.getLogger(MapRegionTable.class);

    private static MapRegionTable _instance;

    private final int[][] _regions = new int[19][21];

    private final int[][] _pointsWithKarmas = new int[19][3];

    public static enum TeleportWhereType {
        Castle,
        ClanHall,
        SiegeFlag,
        Town
    }

    public static MapRegionTable getInstance() {
        if (_instance == null) {
            _instance = new MapRegionTable();
        }

        return _instance;
    }

    private MapRegionTable() {
        int t = 0;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        ServerSettings serverSettings = getSettings(ServerSettings.class);

        File f = new File(serverSettings.getDatapackDirectory() + "/data/xml/map_region.xml");
        if (!f.exists()) {
            _log.warn("could not be loaded: file not found {} ", f.getAbsolutePath());
            return;
        }

        try {
            int region;
            InputSource in = new InputSource(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            in.setEncoding("UTF-8");
            Document doc = factory.newDocumentBuilder().parse(in);
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeName().equalsIgnoreCase("list")) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if (d.getNodeName().equalsIgnoreCase("map")) {
                            region = Integer.valueOf(d.getAttributes().getNamedItem("region").getNodeValue());
                            for (int j = 0; j < 10; j++) {
                                _regions[j][region] = Integer.valueOf(d.getAttributes().getNamedItem("sec" + j).getNodeValue());
                            }
                            t++;
                        }
                    }
                }
            }
        } catch (SAXException e) {
            _log.error("error while loading map region", e);
        } catch (IOException e) {
            _log.error("error while loading map region", e);
        } catch (ParserConfigurationException e) {
            _log.error("error while loading map region", e);
        }

        _log.info("MapRegionTable: Loaded {} regions ", t);

        //Talking Island
        _pointsWithKarmas[0][0] = -79077;
        _pointsWithKarmas[0][1] = 240355;
        _pointsWithKarmas[0][2] = -3440;
        //Elven
        _pointsWithKarmas[1][0] = 43503;
        _pointsWithKarmas[1][1] = 40398;
        _pointsWithKarmas[1][2] = -3450;
        //DarkElven
        _pointsWithKarmas[2][0] = 1675;
        _pointsWithKarmas[2][1] = 19581;
        _pointsWithKarmas[2][2] = -3110;
        //Orc
        _pointsWithKarmas[3][0] = -44413;
        _pointsWithKarmas[3][1] = -121762;
        _pointsWithKarmas[3][2] = -235;
        //Dwalf
        _pointsWithKarmas[4][0] = 12009;
        _pointsWithKarmas[4][1] = -187319;
        _pointsWithKarmas[4][2] = -3309;
        //Gludio
        _pointsWithKarmas[5][0] = -18872;
        _pointsWithKarmas[5][1] = 126216;
        _pointsWithKarmas[5][2] = -3280;
        //Gludin
        _pointsWithKarmas[6][0] = -85915;
        _pointsWithKarmas[6][1] = 150402;
        _pointsWithKarmas[6][2] = -3060;
        //Dion
        _pointsWithKarmas[7][0] = 23652;
        _pointsWithKarmas[7][1] = 144823;
        _pointsWithKarmas[7][2] = -3330;
        //Giran
        _pointsWithKarmas[8][0] = 79125;
        _pointsWithKarmas[8][1] = 154197;
        _pointsWithKarmas[8][2] = -3490;
        //Oren
        _pointsWithKarmas[9][0] = 73840;
        _pointsWithKarmas[9][1] = 58193;
        _pointsWithKarmas[9][2] = -2730;
        //Aden
        _pointsWithKarmas[10][0] = 44413;
        _pointsWithKarmas[10][1] = 22610;
        _pointsWithKarmas[10][2] = 235;
        //Hunters
        _pointsWithKarmas[11][0] = 114137;
        _pointsWithKarmas[11][1] = 72993;
        _pointsWithKarmas[11][2] = -2445;
        //Giran
        _pointsWithKarmas[12][0] = 79125;
        _pointsWithKarmas[12][1] = 154197;
        _pointsWithKarmas[12][2] = -3490;
        // heine
        _pointsWithKarmas[13][0] = 119536;
        _pointsWithKarmas[13][1] = 218558;
        _pointsWithKarmas[13][2] = -3495;
        // Rune Castle Town
        _pointsWithKarmas[14][0] = 42931;
        _pointsWithKarmas[14][1] = -44733;
        _pointsWithKarmas[14][2] = -1326;
        // Goddard
        _pointsWithKarmas[15][0] = 147419;
        _pointsWithKarmas[15][1] = -64980;
        _pointsWithKarmas[15][2] = -3457;
        // Schuttgart
        _pointsWithKarmas[16][0] = 85184;
        _pointsWithKarmas[16][1] = -138560;
        _pointsWithKarmas[16][2] = -2256;
        //TODO Primeval Isle
        _pointsWithKarmas[18][0] = 10468;
        _pointsWithKarmas[18][1] = -24569;
        _pointsWithKarmas[18][2] = -3645;
    }

    public final int getMapRegion(int posX, int posY) {
        return _regions[getMapRegionX(posX)][getMapRegionY(posY)];
    }

    public final int getMapRegionX(int posX) {
        return (posX >> 15) + 4;// + centerTileX;
    }

    public final int getMapRegionY(int posY) {
        return (posY >> 15) + 10;// + centerTileX;
    }

    public int getAreaCastle(L2Character activeChar) {
        int area = getClosestTownNumber(activeChar);
        int castle;

        switch (area) {
            case 0:
                castle = 1;
                break;//Talking Island Village
            case 1:
                castle = 4;
                break; //Elven Village
            case 2:
                castle = 4;
                break; //Dark Elven Village
            case 3:
                castle = 9;
                break; //Orc Village
            case 4:
                castle = 9;
                break; //Dwarven Village
            case 5:
                castle = 1;
                break; //Town of Gludio
            case 6:
                castle = 1;
                break; //Gludin Village
            case 7:
                castle = 2;
                break; //Town of Dion
            case 8:
                castle = 3;
                break; //Town of Giran
            case 9:
                castle = 4;
                break; //Town of Oren
            case 10:
                castle = 5;
                break; //Town of Aden
            case 11:
                castle = 5;
                break; //Hunters Village
            case 12:
                castle = 3;
                break; //Giran Harbor
            case 13:
                castle = 6;
                break; //Heine
            case 14:
                castle = 8;
                break; //Rune Township
            case 15:
                castle = 7;
                break; //Town of Goddard
            case 16:
                castle = 9;
                break; //Town of Shuttgart
            case 17:
                castle = 2;
                break; //Floran Village
            case 18:
                castle = 8;
                break; //Primeval Isle Wharf
            default:
                castle = 5;
                break; //Town of Aden
        }
        return castle;
    }

    public int getClosestTownNumber(L2Character activeChar) {
        return getMapRegion(activeChar.getX(), activeChar.getY());
    }

    public String getClosestTownName(L2Character activeChar) {
        int nearestTownId = getMapRegion(activeChar.getX(), activeChar.getY());
        String nearestTown;

        switch (nearestTownId) {
            case 0:
                nearestTown = "Talking Island Village";
                break;
            case 1:
                nearestTown = "Elven Village";
                break;
            case 2:
                nearestTown = "Dark Elven Village";
                break;
            case 3:
                nearestTown = "Orc Village";
                break;
            case 4:
                nearestTown = "Dwarven Village";
                break;
            case 5:
                nearestTown = "Town of Gludio";
                break;
            case 6:
                nearestTown = "Gludin Village";
                break;
            case 7:
                nearestTown = "Town of Dion";
                break;
            case 8:
                nearestTown = "Town of Giran";
                break;
            case 9:
                nearestTown = "Town of Oren";
                break;
            case 10:
                nearestTown = "Town of Aden";
                break;
            case 11:
                nearestTown = "Hunters Village";
                break;
            case 12:
                nearestTown = "Giran Harbor";
                break;
            case 13:
                nearestTown = "Heine";
                break;
            case 14:
                nearestTown = "Rune Township";
                break;
            case 15:
                nearestTown = "Town of Goddard";
                break;
            case 16:
                nearestTown = "Town of Shuttgart";
                break; ////TODO@ (Check mapregion table)[Luno]
            case 18:
                nearestTown = "Primeval Isle";
                break;
            default:
                nearestTown = "Town of Aden";
                break;

        }

        return nearestTown;
    }

    public Location getTeleToLocation(L2Character activeChar, TeleportWhereType teleportWhere) {
        int[] coord;

        if (activeChar instanceof L2PcInstance) {
            L2PcInstance player = (L2PcInstance) activeChar;

            // If in Monster Derby Track
            if (player.isInsideZone(L2Character.ZONE_MONSTERTRACK))
                return new Location(12661, 181687, -3560);

            Castle castle = null;
            ClanHall clanhall = null;

            if (player.getClan() != null) {
                // If teleport to clan hall
                if (teleportWhere == TeleportWhereType.ClanHall) {

                    clanhall = ClanHallManager.getInstance().getClanHallByOwner(player.getClan());
                    if (clanhall != null) {
                        L2ClanHallZone zone = clanhall.getZone();
                        if (zone != null)
                            return zone.getSpawn();
                    }
                }

                // If teleport to castle
                if (teleportWhere == TeleportWhereType.Castle) {
                    castle = CastleManager.getInstance().getCastleByOwner(player.getClan());
                }

                // Check if player is on castle ground
                if (castle == null) {
                    castle = CastleManager.getInstance().getCastle(player);
                }

                if (castle != null && castle.getCastleId() > 0) {
                    // If Teleporting to castle or
                    // If is on caslte with siege and player's clan is defender
                    if (teleportWhere == TeleportWhereType.Castle || teleportWhere == TeleportWhereType.Castle && castle.getSiege().getIsInProgress() && castle.getSiege().getDefenderClan(player.getClan()) != null) {
                        coord = castle.getCastleZone().getSpawn();
                        return new Location(coord[0], coord[1], coord[2]);
                    }

                    if (teleportWhere == TeleportWhereType.SiegeFlag && castle.getSiege().getIsInProgress()) {
                        // Check if player's clan is attacker
                        List<L2NpcInstance> flags = castle.getSiege().getFlag(player.getClan());
                        if (flags != null && !flags.isEmpty()) {
                            // Spawn to flag - Need more work to get player to the nearest flag
                            L2NpcInstance flag = flags.get(0);
                            return new Location(flag.getX(), flag.getY(), flag.getZ());
                        }
                        flags = null;
                    }
                }
            }

            castle = null;

            // teleport RED PK 5+ to Floran Village
            if (player.getPkKills() > 5 && player.getKarma() > 1)
                return new Location(17817, 170079, -3530);

            //Karma player land out of city
            if (player.getKarma() > 1) {
                int closest = getMapRegion(activeChar.getX(), activeChar.getY());

                if (closest >= 0 && closest < _pointsWithKarmas.length)
                    return new Location(_pointsWithKarmas[closest][0], _pointsWithKarmas[closest][1], _pointsWithKarmas[closest][2]);
                else
                    return new Location(17817, 170079, -3530);
            }

            // Checking if in arena
            L2ArenaZone arena = ArenaManager.getInstance().getArena(player);
            if (arena != null) {
                coord = arena.getSpawnLoc();
                return new Location(coord[0], coord[1], coord[2]);
            }
            arena = null;
            player = null;
        }

        // Get the nearest town
        // TODO: Micht: Maybe we should add some checks to prevent exception here.
        coord = TownManager.getInstance().getClosestTown(activeChar).getSpawnLoc();

        return new Location(coord[0], coord[1], coord[2]);
    }
}