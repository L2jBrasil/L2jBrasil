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
package com.it.br.gameserver.model.zone.form;

import com.it.br.gameserver.model.zone.L2ZoneForm;

/**
 * A primitive circular zone
 *
 *
 * @author  durgus
 */
public class ZoneCylinder extends L2ZoneForm
{
	private int _x, _y, _z1, _z2, _rad, _radS;

	public ZoneCylinder(int x, int y, int z1, int z2, int rad)
	{
		_x = x;
		_y = y;
		_z1 = z1;
		_z2 = z2;
		_rad  = rad;
		_radS = rad*rad;
	}


	@Override
	public boolean isInsideZone(int x, int y, int z)
	{
		if ( (Math.pow(_x-x, 2) + Math.pow(_y-y, 2)) > _radS || z < _z1 || z > _z2) return false;
		return true;
	}


	@Override
	public boolean intersectsRectangle(int ax1, int ax2, int ay1, int ay2)
	{
		// Circles point inside the rectangle?
		if (_x > ax1 && _x < ax2 && _y > ay1 && _y < ay2) return true;

		// Any point of the rectangle intersecting the Circle?
		if ( (Math.pow(ax1-_x, 2) + Math.pow(ay1-_y, 2)) < _radS ) return true;
		if ( (Math.pow(ax1-_x, 2) + Math.pow(ay2-_y, 2)) < _radS ) return true;
		if ( (Math.pow(ax2-_x, 2) + Math.pow(ay1-_y, 2)) < _radS ) return true;
		if ( (Math.pow(ax2-_x, 2) + Math.pow(ay2-_y, 2)) < _radS ) return true;

		// Collision on any side of the rectangle?
		if (_x > ax1 && _x < ax2)
		{
			if (Math.abs(_y-ay2) < _rad) return true;
			if (Math.abs(_y-ay1) < _rad) return true;
		}
		if (_y > ay1 && _y < ay2)
		{
			if (Math.abs(_x-ax2) < _rad) return true;
			if (Math.abs(_x-ax1) < _rad) return true;
		}

		return false;
	}


	@Override
	public double getDistanceToZone(int x, int y)
	{
		return (Math.sqrt( (Math.pow(_x-x, 2) + Math.pow(_y-y, 2)) ) - _rad);
	}

	/* getLowZ() / getHighZ() - These two functions were added to cope with the demand of the new
	 * fishing algorithms, wich are now able to correctly place the hook in the water, thanks to getHighZ().
	 * getLowZ() was added, considering potential future modifications.
	 */

	@Override
	public int getLowZ()
	{
		return _z1;
	}


	@Override
	public int getHighZ()
	{
		return _z2;
	}
}
