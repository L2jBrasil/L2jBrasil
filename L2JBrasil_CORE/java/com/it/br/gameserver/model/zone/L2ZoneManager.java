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
package com.it.br.gameserver.model.zone;

import com.it.br.gameserver.model.L2Character;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages all zones for a given world region
 *
 * @author  durgus
 */
public class L2ZoneManager
{
	private List<L2ZoneType> _zones;

	/**
	 * The Constructor creates an initial zone list
	 * use registerNewZone() / unregisterZone() to
	 * change the zone list
	 *
	 */
	public L2ZoneManager()
	{
		_zones = new ArrayList<>();
	}

	/**
	 * Register a new zone object into the manager
	 * @param zone
	 */
	public void registerNewZone(L2ZoneType zone)
	{
		_zones.add(zone);
	}

	/**
	 * Unregister a given zone from the manager (e.g. dynamic zones)
	 * @param zone
	 */
	public void unregisterZone(L2ZoneType zone)
	{
		_zones.remove(zone);
	}

	public void revalidateZones(L2Character character)
	{
		for (L2ZoneType e : _zones)
		{
			if(e != null) e.revalidateInZone(character);
		}
	}

	public void removeCharacter(L2Character character)
	{
		for (L2ZoneType e : _zones)
		{
			if(e != null) e.removeCharacter(character);
		}
	}

	public void onDeath(L2Character character)
	{
		for (L2ZoneType e : _zones)
		{
			if(e != null) e.onDieInside(character);
		}
	}

	public void onRevive(L2Character character)
	{
		for (L2ZoneType e : _zones)
		{
			if(e != null) e.onReviveInside(character);
		}
	}

}
