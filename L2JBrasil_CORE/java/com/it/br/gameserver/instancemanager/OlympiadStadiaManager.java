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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.zone.type.L2OlympiadStadiumZone;

public class OlympiadStadiaManager
{
	protected static Logger _log = Logger.getLogger(OlympiadStadiaManager.class.getName());

	// =========================================================
	private static OlympiadStadiaManager _instance;

	public static final OlympiadStadiaManager getInstance()
	{
		if(_instance == null)
		{
			_instance = new OlympiadStadiaManager();
		}
		return _instance;
	}

	// =========================================================
	// Data Field
	private List<L2OlympiadStadiumZone> _olympiadStadias;

	// =========================================================
	// Constructor
	public OlympiadStadiaManager()
	{}

	// =========================================================
	// Property - Public

	public void addStadium(L2OlympiadStadiumZone arena)
	{
		if(_olympiadStadias == null)
		{
			_olympiadStadias = new ArrayList<>();
		}

		_olympiadStadias.add(arena);
	}

	public final L2OlympiadStadiumZone getStadium(L2Character character)
	{
		if(_olympiadStadias != null)
		{
			for(L2OlympiadStadiumZone temp : _olympiadStadias)
				if(temp.isCharacterInZone(character))
					return temp;
		}

		return null;
	}

	public final L2OlympiadStadiumZone getStadiumByLoc(int x, int y, int z)
	{
		if(_olympiadStadias != null)
		{
			for(L2OlympiadStadiumZone temp : _olympiadStadias)
				if(temp.isInsideZone(x, y, z))
					return temp;
		}

		return null;
	}

	@Deprecated
	public final L2OlympiadStadiumZone getOlympiadStadiumById(int olympiadStadiumId)
	{
		for(L2OlympiadStadiumZone temp : _olympiadStadias)
			if(temp.getStadiumId() == olympiadStadiumId)
				return temp;
		return null;
	}
}
