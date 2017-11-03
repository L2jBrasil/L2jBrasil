/*
 * $HeadURL: $
 *
 * $Author: $
 * $Date: $
 * $Revision: $
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
package com.it.br.gameserver.util;

/**
 * This interface can be implemented by extensions to register simple functions with the DynamicExtension handler It's
 * in the responsibility of the extensions to interpret the get and set functions
 * 
 * @version $Revision: $ $Date: $
 * @author Galun
 */
public interface ExtensionFunction
{

	/**
	 * get an object identified with a name (should have a human readable output with toString())
	 * 
	 * @param name the name of an object or a result of a function
	 * @return the object
	 */
	public Object get(String name);

	/**
	 * set the named object to the new value supplied in obj
	 * 
	 * @param name the name of the object
	 * @param obj the new value
	 */
	public void set(String name, Object obj);
}
