/* This program is free software; you can redistribute it and/or modify
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
package com.it.br.gameserver.instancemanager;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.L2WorldRegion;
import com.it.br.gameserver.model.zone.L2ZoneType;
import com.it.br.gameserver.model.zone.form.ZoneCuboid;
import com.it.br.gameserver.model.zone.form.ZoneNPoly;
import com.it.br.gameserver.model.zone.type.L2ArenaZone;
import com.it.br.gameserver.model.zone.type.L2BigheadZone;
import com.it.br.gameserver.model.zone.type.L2BossZone;
import com.it.br.gameserver.model.zone.type.L2CastleTeleportZone;
import com.it.br.gameserver.model.zone.type.L2CastleZone;
import com.it.br.gameserver.model.zone.type.L2ClanHallZone;
import com.it.br.gameserver.model.zone.type.L2DamageZone;
import com.it.br.gameserver.model.zone.type.L2DerbyTrackZone;
import com.it.br.gameserver.model.zone.type.L2FishingZone;
import com.it.br.gameserver.model.zone.type.L2JailZone;
import com.it.br.gameserver.model.zone.type.L2MotherTreeZone;
import com.it.br.gameserver.model.zone.type.L2NoLandingZone;
import com.it.br.gameserver.model.zone.type.L2OlympiadStadiumZone;
import com.it.br.gameserver.model.zone.type.L2PeaceZone;
import com.it.br.gameserver.model.zone.type.L2PoisonZone;
import com.it.br.gameserver.model.zone.type.L2SiegeZone;
import com.it.br.gameserver.model.zone.type.L2SkillZone;
import com.it.br.gameserver.model.zone.type.L2SwampZone;
import com.it.br.gameserver.model.zone.type.L2TownZone;
import com.it.br.gameserver.model.zone.type.L2WaterZone;

public class ZoneManager
{
	private static final Logger _log = Logger.getLogger(ZoneManager.class.getName());
	private final Map<Integer, L2ZoneType> _zones = new HashMap<>();
	private static ZoneManager _instance;

	public static final ZoneManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new ZoneManager();
		}
		return _instance;
	}

	// Constructor
	public ZoneManager()
	{
		_log.info("Loading zones...");

		load();
	}

	// Method - Private
	private final void load()
	{
		Connection con = null;
		int zoneCount = 0;

		// Get the world regions
		L2WorldRegion[][] worldRegions = L2World.getInstance().getAllWorldRegions();

		// Load the zone xml
		try
		{
			// Get a sql connection here
			con = L2DatabaseFactory.getInstance().getConnection();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);

			File file = new File(Config.DATAPACK_ROOT+"/data/xml/zone.xml");
			if (!file.exists())
			{
				if (Config.DEBUG)
					_log.info("The zone.xml file is missing.");
				return;
			}

			Document doc = factory.newDocumentBuilder().parse(file);

			for (Node n=doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n.getNodeName()))
				{
					for (Node d=n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if ("zone".equalsIgnoreCase(d.getNodeName()))
						{
							NamedNodeMap attrs = d.getAttributes();
							int zoneId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
							int minZ = Integer.parseInt(attrs.getNamedItem("minZ").getNodeValue());
							int maxZ = Integer.parseInt(attrs.getNamedItem("maxZ").getNodeValue());
							String zoneType = attrs.getNamedItem("type").getNodeValue();
							String zoneShape = attrs.getNamedItem("shape").getNodeValue();

							// Create the zone
							L2ZoneType temp = null;

							if (zoneType.equals("FishingZone"))
								 temp = new L2FishingZone();
							else if (zoneType.equals("ClanHallZone"))
								 temp = new L2ClanHallZone();
							else if (zoneType.equals("PeaceZone"))
								temp = new L2PeaceZone();
							else if (zoneType.equals("Town"))
								temp = new L2TownZone();
							else if (zoneType.equals("OlympiadStadium"))
								temp = new L2OlympiadStadiumZone();
							else if (zoneType.equals("CastleZone"))
								temp = new L2CastleZone();
							else if (zoneType.equals("SiegeZone"))
								temp = new L2SiegeZone();
							else if (zoneType.equals("DamageZone"))
								temp = new L2DamageZone();
							else if (zoneType.equals("Arena"))
								temp = new L2ArenaZone();
							else if (zoneType.equals("MotherTree"))
								temp = new L2MotherTreeZone();
							else if (zoneType.equals("BigheadZone"))
								temp = new L2BigheadZone();
							else if (zoneType.equals("NoLandingZone"))
								temp = new L2NoLandingZone();
							else if (zoneType.equals("JailZone"))
								temp = new L2JailZone();
							else if (zoneType.equals("DerbyTrackZone"))
								temp = new L2DerbyTrackZone();
							else if(zoneType.equals("BossZone"))
								temp = new L2BossZone();
							else if (zoneType.equals("WaterZone"))
                                temp = new L2WaterZone();
                            else if(zoneType.equals("SkillZone"))
                                temp = new L2SkillZone();
                            else if(zoneType.equals("PoisonZone"))
                                temp = new L2PoisonZone();
                            else if(zoneType.equals("SwampZone"))
                                temp = new L2SwampZone();
                            else if(zoneType.equals("CastleTeleportZone"))
                                temp = new L2CastleTeleportZone();


							// Check for unknown type
							if (temp == null)
							{
								_log.warning("ZoneManager: No such zone type: "+zoneType);
								continue;
							}

							// Get the zone shape from sql
							try
							{
								PreparedStatement statement = null;

								// Set the correct query
								statement = con.prepareStatement("SELECT x,y FROM zone_vertices WHERE id=? ORDER BY 'order' ASC ");

								statement.setInt(1, zoneId);
								ResultSet rset = statement.executeQuery();

								// Create this zone.  Parsing for cuboids is a bit different than for other polygons
								// cuboids need exactly 2 points to be defined.  Other polygons need at least 3 (one per vertex)
								if (zoneShape.equals("Cuboid"))
								{
									int[] x = {0,0};
									int[] y = {0,0};
									boolean successfulLoad = true;
									
									for (int i=0;i<2; i++)
									{
										if ( rset.next() )
										{
											x[i] = rset.getInt("x");
											y[i] = rset.getInt("y");
										}
										else
										{
											_log.warning("ZoneManager: Missing cuboid vertex in sql data for zone: "+zoneId);
											rset.close();
											statement.close();
											successfulLoad = false;
											break;
										}
									}

									if (successfulLoad)
										temp.setZone(new ZoneCuboid(x[0],x[1], y[0],y[1],minZ,maxZ));
									else
										continue;
								}
								else if (zoneShape.equals("NPoly"))
								{
									List<Integer> fl_x = new ArrayList<>(), fl_y = new ArrayList<>();

									// Load the rest
									while (rset.next())
									{
										fl_x.add(rset.getInt("x"));
										fl_y.add(rset.getInt("y"));
									}

									// An nPoly needs to have at least 3 vertices
									if ((fl_x.size() == fl_y.size()) && (fl_x.size() > 2))
									{
										// Create arrays
										int[] aX = new int[fl_x.size()];
										int[] aY = new int[fl_y.size()];
	
										// This runs only at server startup so dont complain :>
										for (int i=0; i < fl_x.size(); i++) { aX[i] = fl_x.get(i); aY[i] = fl_y.get(i); }
	
										// Create the zone
										temp.setZone(new ZoneNPoly(aX, aY, minZ, maxZ));
									}
									else
									{
										_log.warning("ZoneManager: Bad sql data for zone: "+zoneId);
										rset.close();
										statement.close();
										continue;
									}
								}
								else
								{
									_log.warning("ZoneManager: Unknown shape: "+zoneShape);
									rset.close();
									statement.close();
									continue;
								}

								rset.close();
								statement.close();
							}
							catch (Exception e)
							{
								_log.warning("ZoneManager: Failed to load zone coordinates: " + e);
							}


							// Check for aditional parameters
							for (Node cd=d.getFirstChild(); cd != null; cd = cd.getNextSibling())
							{
								if ("stat".equalsIgnoreCase(cd.getNodeName()))
								{
									attrs = cd.getAttributes();
									String name = attrs.getNamedItem("name").getNodeValue();
                            		String val = attrs.getNamedItem("val").getNodeValue();
                            		temp.setParameter(name, val);
								}
								if ("spawn".equalsIgnoreCase(cd.getNodeName()))
									temp.setSpawns(cd);
								
							}

							// Skip checks for fishing zones & add to fishing zone manager
							if (temp instanceof L2FishingZone)
							{
								FishingZoneManager.getInstance().addFishingZone((L2FishingZone)temp);
								continue;
							}

							// Register the zone into any world region it intersects with...
							// currently 11136 test for each zone :>
							int ax,ay,bx,by;
							for (int x=0; x < worldRegions.length; x++)
							{
								for (int y=0; y < worldRegions[x].length; y++)
								{
									ax = (x-L2World.OFFSET_X) << L2World.SHIFT_BY;
									bx = ((x+1)-L2World.OFFSET_X) << L2World.SHIFT_BY;
									ay = (y-L2World.OFFSET_Y) << L2World.SHIFT_BY;
									by = ((y+1)-L2World.OFFSET_Y) << L2World.SHIFT_BY;

									if (temp.getZone().intersectsRectangle(ax, bx, ay, by))
									{
										if (Config.DEBUG)
										{
											_log.info("Zone ("+zoneId+") added to: "+x+" "+y);
										}
										worldRegions[x][y].addZone(temp);
									}
								}
							}

							// Special managers for arenas, towns...
							if (temp instanceof L2ArenaZone)
								ArenaManager.getInstance().addArena((L2ArenaZone)temp);
							else if (temp instanceof L2TownZone)
								TownManager.getInstance().addTown((L2TownZone)temp);
							else if (temp instanceof L2OlympiadStadiumZone)
								OlympiadStadiaManager.getInstance().addStadium((L2OlympiadStadiumZone)temp);
							else if (temp instanceof L2BossZone) 
								GrandBossManager.getInstance().addZone((L2BossZone) temp);

							// Increase the counter
							zoneCount++;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Error while loading zones.", e);
			return ;
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (Exception e)
			{
			}
		}
		GrandBossManager.getInstance().initZones();
		_log.info("Done: loaded "+zoneCount+" zones.");
	}
	
	public L2ZoneType getZoneById(int id)
	{
		return _zones.get(id);
	}
}
