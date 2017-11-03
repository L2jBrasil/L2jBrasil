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
package com.it.br.gameserver.model.actor.poly;

import com.it.br.gameserver.model.L2Object;

public class ObjectPoly
{
    // =========================================================
    // Data Field
    private L2Object _activeObject;
    private int _polyId;
    private String _polyType;

    // =========================================================
    // Constructor
    public ObjectPoly(L2Object activeObject)
    {
        _activeObject = activeObject;
    }

    // =========================================================
    // Method - Public
    public void setPolyInfo(String polyType, String polyId)
    {
        setPolyId(Integer.parseInt(polyId));
        setPolyType(polyType);
    }

    // =========================================================
    // Method - Private

    // =========================================================
    // Property - Public
    public final L2Object getActiveObject()
    {
        return _activeObject;
    }

    public final boolean isMorphed() { return getPolyType() != null; }

    public final int getPolyId() { return _polyId; }
    public final void setPolyId(int value) { _polyId = value; }

    public final String getPolyType() { return _polyType; }
    public final void setPolyType(String value) { _polyType = value; }
}
