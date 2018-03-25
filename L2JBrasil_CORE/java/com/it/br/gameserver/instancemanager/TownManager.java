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

import com.it.br.gameserver.datatables.xml.MapRegionTable;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.model.zone.type.L2TownZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TownManager
{
    private static final Logger _log = LoggerFactory.getLogger(TownManager.class);

    // =========================================================
    private static TownManager _instance;
    public static final TownManager getInstance()
    {
        if (_instance == null)
        {
        	_instance = new TownManager();
        }
        return _instance;
    }
    // =========================================================


    // =========================================================
    // Data Field
    private List<L2TownZone> _towns;

    // =========================================================
    // Constructor
    public TownManager(){ }

    // =========================================================
    // Property - Public

    public void addTown(L2TownZone arena)
    {
    	if (_towns == null)
    		_towns = new ArrayList<>();

    	_towns.add(arena);
    }

    public final L2TownZone getClosestTown(L2Object activeObject)
    {
        switch (MapRegionTable.getInstance().getMapRegion(activeObject.getPosition().getX(), activeObject.getPosition().getY()))
		{
			case 0:
				return getTown(2); // TI
			case 1:
				return getTown(3); // Elven
			case 2:
				return getTown(1); // DE
			case 3:
				return getTown(4); // Orc
			case 4:
				return getTown(6); // Dwarven
			case 5:
				return getTown(7); // Gludio
			case 6:
				return getTown(5); // Gludin
			case 7:
				return getTown(8); // Dion
			case 8:
				return getTown(9); // Giran
			case 9:
				return getTown(10); // Oren
			case 10:
				return getTown(12); // Aden
			case 11:
				return getTown(11); // HV
			case 12:
				return getTown(9); // Giran Harbour
			case 13:
				return getTown(15); // Heine
			case 14:
				return getTown(14); // Rune
			case 15:
				return getTown(13); // Goddard
			case 16:
				return getTown(17); // Schuttgart
			case 17:
				return getTown(16); // Floran
			case 18:
				return getTown(19); //Primeval Isle
		}

        return getTown(16); // Default to floran
    }

    public final boolean townHasCastleInSiege(int townId)
    {
    	//int[] castleidarray = {0,0,0,0,0,0,0,1,2,3,4,0,5,0,0,6,0};
    	int[] castleidarray = {0,0,0,0,0,0,0,1,2,3,4,0,5,7,8,6,0,9,0,0};
    	int castleIndex= castleidarray[townId] ;

    	if ( castleIndex > 0 )
        {
           	Castle castle = CastleManager.getInstance().getCastles().get(CastleManager.getInstance().getCastleIndex(castleIndex));
           	if (castle != null)
           		return castle.getSiege().getIsInProgress();
        }
        return false;
    }

    public final boolean townHasCastleInSiege(int x, int y)
    {
        int curtown= (MapRegionTable.getInstance().getMapRegion(x, y));
        //int[] castleidarray = {0,0,0,0,0,1,0,2,3,4,5,0,0,6,0,0,0,0};
        int[] castleidarray = {0,0,0,0,0,1,0,2,3,4,5,0,0,6,8,7,9,0,0};
        //find an instance of the castle for this town.
        int castleIndex = castleidarray[curtown];
        if ( castleIndex > 0 )
        {
        	Castle castle = CastleManager.getInstance().getCastles().get(CastleManager.getInstance().getCastleIndex(castleIndex));
        	if (castle != null)
        		return castle.getSiege().getIsInProgress();
        }
        return false;
    }

    public final L2TownZone getTown(int townId)
    {
        for (L2TownZone temp : _towns)
        	if (temp.getTownId() == townId) return temp;
        return null;
    }

    /**
     * Returns the town at that position (if any)
     * @param x
     * @param y
     * @param z
     * @return
     */
    public final L2TownZone getTown(int x, int y, int z)
    {
        for (L2TownZone temp : _towns)
        	if (temp.isInsideZone(x, y, z)) return temp;
        return null;
    }
}
