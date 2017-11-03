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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  Julian
 */

public class L2FastMap<K extends Object, V extends Object> extends HashMap<K,V>
{
	static final long serialVersionUID = 1L;
	
	public interface I2ForEach<K,V> {
		public boolean forEach(K key, V obj);
		public Map.Entry<K, V> getNext(HashMap.Entry<K, V> priv);
	}

	public final boolean ForEach(I2ForEach<K,V> func, boolean sync) {
		if (sync)
			synchronized (this) { return forEachP(func); }
		else
			return forEachP(func);
	}
	
	private boolean forEachP(I2ForEach<K,V> func) {
        for (Map.Entry<K, V> e :
             this.entrySet()) {
            if (!func.forEach(e.getKey(),e.getValue())) return false;
        }
		return true;
	}
}
