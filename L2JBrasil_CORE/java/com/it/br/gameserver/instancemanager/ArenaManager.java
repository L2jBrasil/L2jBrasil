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

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.zone.type.L2ArenaZone;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager
{
    // =========================================================
    private static ArenaManager _instance;
    public static final ArenaManager getInstance()
    {
        if (_instance == null)
        {
        	_instance = new ArenaManager();
        }
        return _instance;
    }
    // =========================================================


    // =========================================================
    // Data Field
    private List<L2ArenaZone> _arenas;

    // =========================================================
    // Constructor
    public ArenaManager()
    {
    }

    // =========================================================
    // Property - Public

    public void addArena(L2ArenaZone arena)
    {
    	if (_arenas == null)
    		_arenas = new ArrayList<>();

    	_arenas.add(arena);
    }

    public final L2ArenaZone getArena(L2Character character)
    {
    	for (L2ArenaZone temp : _arenas)
    		if (temp.isCharacterInZone(character)) return temp;

    	return null;
    }
}
