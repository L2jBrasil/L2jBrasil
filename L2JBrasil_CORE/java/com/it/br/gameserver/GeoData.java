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
package com.it.br.gameserver;

import com.it.br.Config;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.Location;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoData
{
	private static Logger _log = LoggerFactory.getLogger(GeoData.class);
	private static GeoData _instance;

	public static GeoData getInstance()
    {
        if(_instance == null)
        {
        	if (Config.GEODATA > 0)
        		_instance = GeoEngine.getInstance();
        	else
        	{
        		_instance = new GeoData();
        		_log.info("Geodata Engine: Disabled.");
        	}
        }
        return _instance;
    }

    public short getType  (int x, int y)
    {
        return 0;
    }

    public short getHeight(int x, int y, int z)
    {
        return (short)z;
    }

    public short getSpawnHeight(int x, int y, int zmin, int zmax, int spawnid)
    {
        return (short)zmin;
    }

    public String geoPosition(int x, int y)
    {
    	return "";
    }

    public boolean canSeeTarget(L2Object cha, L2Object target)
    {
    	//If geo is off do simple check :]
    	//Don't allow casting on players on different dungeon lvls etc
        return (Math.abs(target.getZ() - cha.getZ()) < 1000);
    }

    public boolean canSeeTargetDebug(L2PcInstance gm, L2Object target)
    {
        return true;
    }

    public short getNSWE(int x, int y, int z)
    {
        return 15;
    }

    public Location moveCheck(int x, int y, int z, int tx, int ty, int tz)
    {
        return new Location(tx,ty,tz);
    }

    public void addGeoDataBug(L2PcInstance gm, String comment)
    {
    	//Do Nothing
    }

    public static void unloadGeodata(byte rx, byte ry)
    {
    }

    @Deprecated //TODO: cleanup?
    public static boolean loadGeodataFile(byte rx, byte ry)
    {
    	return false;
    }
}