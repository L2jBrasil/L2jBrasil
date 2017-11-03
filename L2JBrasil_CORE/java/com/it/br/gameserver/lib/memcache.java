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
package com.it.br.gameserver.lib;

import java.util.HashMap;
import java.util.logging.Logger;

@Deprecated
public class memcache
{
	private static Logger _log = Logger.getLogger(memcache.class.getName());
	private HashMap<Integer,String> _hms;
	private HashMap<Integer,Integer> _hmi;
	private HashMap<Integer,Long> _lastAccess;

	private static final memcache _instance = new memcache();

	public static memcache getInstance()
	{
		return _instance;
	}

	private memcache()
	{
		_hms = new HashMap<Integer,String>();
		_hmi = new HashMap<Integer,Integer>();
		_lastAccess = new HashMap<Integer,Long>();
	}

	private void checkExpired()
	{
		for(Integer k : _hmi.keySet())
			if(_lastAccess.get(k) + 3600000 < System.currentTimeMillis())
			{
//				_hmi.remove(k);
//				_last_access.remove(k);
		   	}

		for(Integer k : _hms.keySet())
			if(_lastAccess.get(k) + 3600000 < System.currentTimeMillis())
			{
//				_hms.remove(k);
//				_last_access.remove(k);
		   	}
	}

	public void set(String type, String key, int value)
	{
		int hash = (type+"->"+key).hashCode();
//	    _log.fine("Set memcache "+type+"("+key+")["+hash+"] to "+value);
		_hmi.put(hash, value);
		_lastAccess.put(hash, System.currentTimeMillis());
		checkExpired();
	}

	@Deprecated
	public boolean isSet(String type, String key)
	{
		int hash = (type+"->"+key).hashCode();
		boolean exists = _hmi.containsKey(hash) || _hms.containsKey(hash);
		if(exists)
			_lastAccess.put(hash, System.currentTimeMillis());

		checkExpired();
	    _log.fine("Check exists memcache "+type+"("+key+")["+hash+"] is "+exists);
		return exists;
	}

	@Deprecated
	public Integer getInt(String type, String key)
	{
		int hash = (type+"->"+key).hashCode();
		_lastAccess.put(hash, System.currentTimeMillis());
		checkExpired();
	    _log.fine("Get memcache "+type+"("+key+")["+hash+"] = "+_hmi.get(hash));
		return _hmi.get(hash);
	}
}
