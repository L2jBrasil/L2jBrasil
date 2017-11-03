/*
 * $Header: Point3D.java, 19/07/2005 21:33:07 luisantonioa Exp $
 *
 * $Author: luisantonioa $
 * $Date: 19/07/2005 21:33:07 $
 * $Revision: 1 $
 * $Log: Point3D.java,v $
 * Revision 1  19/07/2005 21:33:07  luisantonioa
 * Added copyright notice
 *
 *
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
package com.it.br.util;

import java.io.Serializable;

/**
 * This class ...
 *
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public class Point3D implements Serializable
{
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 4638345252031872576L;

	private volatile int _x, _y, _z;

	public Point3D(int pX, int pY, int pZ)
	{
		_x = pX;
		_y = pY;
		_z = pZ;
	}

	public Point3D(int pX, int pY)
	{
		_x = pX;
		_y = pY;
		_z = 0;
	}

	/**
	 * @param worldPosition
	 */
	public Point3D(Point3D worldPosition)
	{
		synchronized (worldPosition)
		{
			_x = worldPosition._x;
			_y = worldPosition._y;
			_z = worldPosition._z;
		}
	}

	public synchronized void setTo(Point3D point)
	{
		synchronized (point)
		{
			_x = point._x;
			_y = point._y;
			_z = point._z;
		}
	}


	@Override
	public String toString()
	{
		return "(" + _x + ", " + _y + ", " + _z + ")";
	}


	@Override
	public int hashCode()
	{
		return _x ^ _y ^ _z;
	}


	@Override
	public synchronized boolean equals(Object o)
	{
		if (o instanceof Point3D)
		{
			Point3D point3D = (Point3D) o;
			boolean ret;
			synchronized (point3D)
			{
				ret = point3D._x == _x && point3D._y == _y && point3D._z == _z;
			}
			return ret;
		}
		return false;
	}

	public synchronized boolean equals(int pX, int pY, int pZ)
	{
		return _x == pX && _y == pY && _z == pZ;
	}

	public synchronized long distanceSquaredTo(Point3D point)
	{
		long dx, dy;
		synchronized (point)
		{
			dx = _x - point._x;
			dy = _y - point._y;
		}
		return (dx * dx) + (dy * dy);
	}

	public static long distanceSquared(Point3D point1, Point3D point2)
	{
		long dx, dy;
		synchronized (point1)
		{
			synchronized (point2)
			{
				dx = point1._x - point2._x;
				dy = point1._y - point2._y;
			}
		}
		return (dx * dx) + (dy * dy);
	}

	public static boolean distanceLessThan(Point3D point1, Point3D point2,
			double distance)
	{
		return distanceSquared(point1, point2) < distance * distance;
	}

	public int getX()
	{
		return _x;
	}

	public synchronized void setX(int pX)
	{
		_x = pX;
	}

	public int getY()
	{
		return _y;
	}

	public synchronized void setY(int pY)
	{
		_y = pY;
	}

	public int getZ()
	{
		return _z;
	}

	public synchronized void setZ(int pZ)
	{
		_z = pZ;
	}

	public synchronized void setXYZ(int pX, int pY, int pZ)
	{
		_x = pX;
		_y = pY;
		_z = pZ;
	}
}
