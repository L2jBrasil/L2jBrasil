/*
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
package com.it.br.gameserver.templates;

/**
 * @author mkizub
 * <BR>Description of Weapon Type
 */
public enum L2WeaponType {
	NONE      ( 1, "Shield"), // Shields!!!
	SWORD     ( 2, "Sword"),
	BLUNT     ( 3, "Blunt"),
	DAGGER    ( 4, "Dagger"),
	BOW       ( 5, "Bow"),
	POLE      ( 6, "Pole"),
	ETC       ( 7, "Etc"),
	FIST      ( 8, "Fist"),
	DUAL      ( 9, "Dual Sword"),
	DUALFIST  (10, "Dual Fist"),
    BIGSWORD  (11, "Big Sword"), // Two Handed Swords
    PET       (12, "Pet"),
	ROD       (13, "Rod"),
	BIGBLUNT  (14, "Big Blunt"); // Two handed blunt

	private final int _id;
	private final String _name;

	/**
	 * Constructor of the L2WeaponType.
	 * @param id : int designating the ID of the WeaponType
	 * @param name : String designating the name of the WeaponType
	 */
	private L2WeaponType(int id, String name)
	{
		_id = id;
		_name = name;
	}

	/**
	 * Returns the ID of the item after applying the mask.
	 * @return int : ID of the item
	 */
	public int mask() {
		return 1<<_id;
	}

    /**
     * Returns the name of the WeaponType
     * @return String
     */

	@Override
	public String toString()
	{
		return _name;
	}

}
