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
package com.it.br.gameserver.datatables.xml;

import com.it.br.configuration.Configurator;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.instancemanager.ClanHallManager;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.entity.ClanHall;
import com.it.br.gameserver.pathfinding.AbstractNodeLoc;
import com.it.br.gameserver.templates.L2CharTemplate;
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
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class DoorTable {
    private static final Logger _log = LoggerFactory.getLogger(DoorTable.class);

    private Map<Integer, L2DoorInstance> _staticItems;

    private static DoorTable _instance;

    public static DoorTable getInstance() {
        if (_instance == null) {
            _instance = new DoorTable();
        }

        return _instance;
    }

    public DoorTable() {
        _staticItems = new HashMap<>();
    }

    public void reloadAll() {
        respawn();
    }

    public void respawn() {
        _staticItems = null;
        _instance = null;
        _instance = new DoorTable();
    }

    public void parseData() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        ServerSettings serverSettings = Configurator.getSettings(ServerSettings.class);
        File file = new File(serverSettings.getDatapackDirectory() + "/data/xml/doors.xml");
        if (!file.exists()) {
            _log.warn("doors.xml is missing in data folder {}.", file.getAbsolutePath());
            return;
        }
        try {
            InputSource in = new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            in.setEncoding("UTF-8");
            Document doc = factory.newDocumentBuilder().parse(file);
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if ("list".equalsIgnoreCase(n.getNodeName())) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if (d.getNodeName().equalsIgnoreCase("door")) {
                            String name = String.valueOf(d.getAttributes().getNamedItem("name").getNodeValue());
                            int id = Integer.valueOf(d.getAttributes().getNamedItem("id").getNodeValue());
                            int x = Integer.valueOf(d.getAttributes().getNamedItem("x").getNodeValue());
                            int y = Integer.valueOf(d.getAttributes().getNamedItem("y").getNodeValue());
                            int z = Integer.valueOf(d.getAttributes().getNamedItem("z").getNodeValue());
                            int rangeXMin = Integer.valueOf(d.getAttributes().getNamedItem("XMin").getNodeValue());
                            int rangeYMin = Integer.valueOf(d.getAttributes().getNamedItem("YMin").getNodeValue());
                            int rangeZMin = Integer.valueOf(d.getAttributes().getNamedItem("ZMin").getNodeValue());
                            int rangeXMax = Integer.valueOf(d.getAttributes().getNamedItem("XMax").getNodeValue());
                            int rangeYMax = Integer.valueOf(d.getAttributes().getNamedItem("YMax").getNodeValue());
                            int rangeZMax = Integer.valueOf(d.getAttributes().getNamedItem("ZMax").getNodeValue());
                            int hp = Integer.valueOf(d.getAttributes().getNamedItem("hp").getNodeValue());
                            int pdef = Integer.valueOf(d.getAttributes().getNamedItem("pdef").getNodeValue());
                            int mdef = Integer.valueOf(d.getAttributes().getNamedItem("mdef").getNodeValue());
                            boolean unlockable = Boolean.valueOf(d.getAttributes().getNamedItem("unlockable").getNodeValue());
                            boolean autoOpen = Boolean.valueOf(d.getAttributes().getNamedItem("autoOpen").getNodeValue());
                            if (rangeXMin > rangeXMax)
                                _log.warn("DoorTable: Error on rangeX min/max, ID: {}", id);
                            if (rangeYMin > rangeYMax)
                                _log.warn("DoorTable: Error on rangeY min/max, ID: {}", id);
                            if (rangeZMin > rangeZMax)
                                _log.warn("DoorTable: Error on rangeZ min/max, ID: {}", id);

                            int collisionRadius; // (max) radius for movement checks
                            if ((rangeXMax - rangeXMin) > (rangeYMax - rangeYMin))
                                collisionRadius = rangeYMax - rangeYMin;
                            else
                                collisionRadius = rangeXMax - rangeXMin;
                            StatsSet npcDat = new StatsSet();
                            npcDat.set("npcId", id);
                            npcDat.set("level", 0);
                            npcDat.set("jClass", "door");
                            npcDat.set("baseSTR", 0);
                            npcDat.set("baseCON", 0);
                            npcDat.set("baseDEX", 0);
                            npcDat.set("baseINT", 0);
                            npcDat.set("baseWIT", 0);
                            npcDat.set("baseMEN", 0);
                            npcDat.set("baseShldDef", 0);
                            npcDat.set("baseShldRate", 0);
                            npcDat.set("baseAccCombat", 38);
                            npcDat.set("baseEvasRate", 38);
                            npcDat.set("baseCritRate", 38);
                            npcDat.set("collision_radius", collisionRadius);
                            npcDat.set("collision_height", rangeZMax - rangeZMin);
                            npcDat.set("sex", "male");
                            npcDat.set("type", "");
                            npcDat.set("baseAtkRange", 0);
                            npcDat.set("baseMpMax", 0);
                            npcDat.set("baseCpMax", 0);
                            npcDat.set("rewardExp", 0);
                            npcDat.set("rewardSp", 0);
                            npcDat.set("basePAtk", 0);
                            npcDat.set("baseMAtk", 0);
                            npcDat.set("basePAtkSpd", 0);
                            npcDat.set("aggroRange", 0);
                            npcDat.set("baseMAtkSpd", 0);
                            npcDat.set("rhand", 0);
                            npcDat.set("lhand", 0);
                            npcDat.set("armor", 0);
                            npcDat.set("baseWalkSpd", 0);
                            npcDat.set("baseRunSpd", 0);
                            npcDat.set("name", name);
                            npcDat.set("baseHpMax", hp);
                            npcDat.set("baseHpReg", 3.e-3f);
                            npcDat.set("baseMpReg", 3.e-3f);
                            npcDat.set("basePDef", pdef);
                            npcDat.set("baseMDef", mdef);

                            L2CharTemplate template = new L2CharTemplate(npcDat);
                            L2DoorInstance door = new L2DoorInstance(IdFactory.getInstance().getNextId(), template, id, name, unlockable);
                            door.setRange(rangeXMin, rangeYMin, rangeZMin, rangeXMax, rangeYMax, rangeZMax);
                            try {
                                door.setMapRegion(MapRegionTable.getInstance().getMapRegion(x, y));
                            } catch (Exception e) {
                                _log.error("Error in door data, ID {} : {}", id, e);
                            }
                            door.setCurrentHpMp(door.getMaxHp(), door.getMaxMp());
                            door.setOpen(autoOpen);
                            door.setXYZInvisible(x, y, z);

                            putDoor(door);
                            door.spawnMe(door.getX(), door.getY(), door.getZ());
                            ClanHall clanhall = ClanHallManager.getInstance().getNearbyClanHall(door.getX(), door.getY(), 500);
                            if (clanhall != null) {
                                clanhall.getDoors().add(door);
                                door.setClanHall(clanhall);
                            }
                        }
                    }
                }
            }

            _log.info("DoorTable: Loaded {} doors templates.", _staticItems.size());
        } catch (SAXException e) {
            _log.error("DoorTable: Error while creating table", e);
        } catch (IOException e) {
            _log.error("DoorTable: Error while creating table", e);
        } catch (ParserConfigurationException e) {
            _log.error("DoorTable: Error while creating table", e);
        }
    }

    public static L2DoorInstance parseList(String line) {
        StringTokenizer st = new StringTokenizer(line, ";");

        String name = st.nextToken();
        int id = Integer.parseInt(st.nextToken());
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        int z = Integer.parseInt(st.nextToken());
        int rangeXMin = Integer.parseInt(st.nextToken());
        int rangeYMin = Integer.parseInt(st.nextToken());
        int rangeZMin = Integer.parseInt(st.nextToken());
        int rangeXMax = Integer.parseInt(st.nextToken());
        int rangeYMax = Integer.parseInt(st.nextToken());
        int rangeZMax = Integer.parseInt(st.nextToken());
        int hp = Integer.parseInt(st.nextToken());
        int pdef = Integer.parseInt(st.nextToken());
        int mdef = Integer.parseInt(st.nextToken());

        boolean unlockable = false;

        if (st.hasMoreTokens()) {
            unlockable = Boolean.parseBoolean(st.nextToken());
        }
        boolean autoOpen = false;

        if (st.hasMoreTokens()) {
            autoOpen = Boolean.parseBoolean(st.nextToken());
        }

        st = null;

        if (rangeXMin > rangeXMax) {
            _log.warn("Error in door data, ID: {}", id);
        }

        if (rangeYMin > rangeYMax) {
            _log.warn("Error in door data, ID: {}", id);
        }

        if (rangeZMin > rangeZMax) {
            _log.warn("Error in door data, ID: {}", id);
        }

        int collisionRadius;

        if (rangeXMax - rangeXMin > rangeYMax - rangeYMin) {
            collisionRadius = rangeYMax - rangeYMin;
        } else {
            collisionRadius = rangeXMax - rangeXMin;
        }

        StatsSet npcDat = new StatsSet();
        npcDat.set("npcId", id);
        npcDat.set("level", 0);
        npcDat.set("jClass", "door");
        npcDat.set("baseSTR", 0);
        npcDat.set("baseCON", 0);
        npcDat.set("baseDEX", 0);
        npcDat.set("baseINT", 0);
        npcDat.set("baseWIT", 0);
        npcDat.set("baseMEN", 0);
        npcDat.set("baseShldDef", 0);
        npcDat.set("baseShldRate", 0);
        npcDat.set("baseAccCombat", 38);
        npcDat.set("baseEvasRate", 38);
        npcDat.set("baseCritRate", 38);
        npcDat.set("collision_radius", collisionRadius);
        npcDat.set("collision_height", rangeZMax - rangeZMin);
        npcDat.set("sex", "male");
        npcDat.set("type", "");
        npcDat.set("baseAtkRange", 0);
        npcDat.set("baseMpMax", 0);
        npcDat.set("baseCpMax", 0);
        npcDat.set("rewardExp", 0);
        npcDat.set("rewardSp", 0);
        npcDat.set("basePAtk", 0);
        npcDat.set("baseMAtk", 0);
        npcDat.set("basePAtkSpd", 0);
        npcDat.set("aggroRange", 0);
        npcDat.set("baseMAtkSpd", 0);
        npcDat.set("rhand", 0);
        npcDat.set("lhand", 0);
        npcDat.set("armor", 0);
        npcDat.set("baseWalkSpd", 0);
        npcDat.set("baseRunSpd", 0);
        npcDat.set("name", name);
        npcDat.set("baseHpMax", hp);
        npcDat.set("baseHpReg", 3.e-3f);
        npcDat.set("baseMpReg", 3.e-3f);
        npcDat.set("basePDef", pdef);
        npcDat.set("baseMDef", mdef);

        L2CharTemplate template = new L2CharTemplate(npcDat);
        L2DoorInstance door = new L2DoorInstance(IdFactory.getInstance().getNextId(), template, id, name, unlockable);
        door.setRange(rangeXMin, rangeYMin, rangeZMin, rangeXMax, rangeYMax, rangeZMax);
        name = null;
        npcDat = null;
        template = null;
        try {
            door.setMapRegion(MapRegionTable.getInstance().getMapRegion(x, y));
        } catch (Exception e) {
            _log.error("Error in door data, ID {} : {}", id, e);
        }
        door.setCurrentHpMp(door.getMaxHp(), door.getMaxMp());
        door.setOpen(autoOpen);
        door.setXYZInvisible(x, y, z);

        return door;
    }

    public boolean isInitialized() {
        return _initialized;
    }

    private boolean _initialized = true;

    public L2DoorInstance getDoor(Integer id) {
        return _staticItems.get(id);
    }

    public void putDoor(L2DoorInstance door) {
        _staticItems.put(door.getDoorId(), door);
    }

    public L2DoorInstance[] getDoors() {
        L2DoorInstance[] _allTemplates = _staticItems.values().toArray(new L2DoorInstance[_staticItems.size()]);
        return _allTemplates;
    }

    public void checkAutoOpen() {
        for (L2DoorInstance doorInst : getDoors()) {
            if (doorInst.getDoorName().startsWith("goe")) {
                doorInst.setAutoActionDelay(420000);
            } else if (doorInst.getDoorName().startsWith("aden_tower")) {
                doorInst.setAutoActionDelay(300000);
            } else if (doorInst.getDoorName().startsWith("cruma")) {
                doorInst.setAutoActionDelay(1200000);
            }
        }
    }

    public boolean checkIfDoorsBetween(AbstractNodeLoc start, AbstractNodeLoc end) {
        return checkIfDoorsBetween(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ());
    }

    public boolean checkIfDoorsBetween(int x, int y, int z, int tx, int ty, int tz) {
        int region;
        try {
            region = MapRegionTable.getInstance().getMapRegion(x, y);
        } catch (Exception e) {
            return false;
        }

        for (L2DoorInstance doorInst : getDoors()) {
            if (doorInst.getMapRegion() != region) {
                continue;
            }
            if (doorInst.getXMax() == 0) {
                continue;
            }

            // line segment goes through box
            // heavy approximation disabling some shooting angles especially near 2-piece doors
            // but most calculations should stop short
            // phase 1, x
            if (x <= doorInst.getXMax() && tx >= doorInst.getXMin() || tx <= doorInst.getXMax() && x >= doorInst.getXMin()) {
                //phase 2, y
                if (y <= doorInst.getYMax() && ty >= doorInst.getYMin() || ty <= doorInst.getYMax() && y >= doorInst.getYMin()) {
                    // phase 3, basically only z remains but now we calculate it with another formula (by rage)
                    // in some cases the direct line check (only) in the beginning isn't sufficient,
                    // when char z changes a lot along the path
                    if (doorInst.getStatus().getCurrentHp() > 0 && !doorInst.getOpen()) {
                        int px1 = doorInst.getXMin();
                        int py1 = doorInst.getYMin();
                        int pz1 = doorInst.getZMin();
                        int px2 = doorInst.getXMax();
                        int py2 = doorInst.getYMax();
                        int pz2 = doorInst.getZMax();

                        int l = tx - x;
                        int m = ty - y;
                        int n = tz - z;

                        int dk;

                        if ((dk = (doorInst.getA() * l + doorInst.getB() * m + doorInst.getC() * n)) == 0)
                            continue; // Parallel

                        float p = (float) (doorInst.getA() * x + doorInst.getB() * y + doorInst.getC() * z + doorInst.getD()) / (float) dk;

                        int fx = (int) (x - l * p);
                        int fy = (int) (y - m * p);
                        int fz = (int) (z - n * p);

                        if ((Math.min(x, tx) <= fx && fx <= Math.max(x, tx))
                                && (Math.min(y, ty) <= fy && fy <= Math.max(y, ty))
                                && (Math.min(z, tz) <= fz && fz <= Math.max(z, tz))) {

                            if (((fx >= px1 && fx <= px2) || (fx >= px2 && fx <= px1))
                                    && ((fy >= py1 && fy <= py2) || (fy >= py2 && fy <= py1))
                                    && ((fz >= pz1 && fz <= pz2) || (fz >= pz2 && fz <= pz1)))
                                return true; // Door between
                        }
                    }
                }
            }
        }
        return false;
    }


}