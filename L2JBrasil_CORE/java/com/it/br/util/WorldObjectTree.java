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
package com.it.br.util;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.it.br.gameserver.model.L2Object;
/**
 *
 * @author  dishkols
 */
public class WorldObjectTree<T extends L2Object> extends L2ObjectMap<T>
{
	private final TreeMap<Integer, T> _objectMap = new TreeMap<Integer,T>();
	private final ReentrantReadWriteLock _rwl = new ReentrantReadWriteLock();
    private final Lock _r = _rwl.readLock();
    private final Lock _w = _rwl.writeLock();

	/**
	 * @see com.it.br.util.L2ObjectMap#size()
	 */

	@Override
	public int size()
	{
		_r.lock();
		try {
			return _objectMap.size();
		}
		finally { _r.unlock(); }
	}

	/**
	 * @see com.it.br.util.L2ObjectMap#isEmpty()
	 */

	@Override
	public boolean isEmpty()
	{
		_r.lock();
		try {
			return _objectMap.isEmpty();
		}
		finally { _r.unlock(); }
	}

	/**
	 * @see com.it.br.util.L2ObjectMap#clear()
	 */

	@Override
	public void clear()
	{
		_w.lock();
		try {
			_objectMap.clear();
		}
		finally { _w.unlock(); }
	}

	/**
	 * @see com.it.br.util.L2ObjectMap#put(T)
	 */

	@Override
	public void put(T obj)
	{
		if ( obj != null) {
			_w.lock();
			try {
				_objectMap.put(obj.getObjectId(),obj);
			}
			finally { _w.unlock(); }
		}
	}

	/**
	 * @see com.it.br.util.L2ObjectMap#remove(T)
	 */

	@Override
	public void remove(T obj)
	{
		if (obj != null) {
			_w.lock();
			try {
				_objectMap.remove(obj.getObjectId());
			}
			finally { _w.unlock(); }
		}
	}

	/**
	 * @see com.it.br.util.L2ObjectMap#get(int)
	 */

	@Override
	public T get(int id)
	{
		_r.lock();
		try {
			return _objectMap.get(id);
		}
		finally { _r.unlock(); }
	}

	/**
	 * @see com.it.br.util.L2ObjectMap#contains(T)
	 */

	@Override
	public boolean contains(T obj)
	{
		if (obj == null) return false;
		_r.lock();
		try {
			return _objectMap.containsValue(obj);
		}
		finally { _r.unlock(); }
	}

	/**
	 * @see com.it.br.util.L2ObjectMap#iterator()
	 */

	@Override
	public Iterator<T> iterator()
	{
		_r.lock();
		try {
			return _objectMap.values().iterator();
		}
		finally { _r.unlock(); }
	}

}
