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
package com.it.br.gameserver.model;

import java.util.ArrayList;
import java.util.List;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;

public class L2SiegeClan
{
	// ==========================================================================================
	// Instance
	// ===============================================================
	// Data Field
	private int _clanId                = 0;
	private List<L2NpcInstance> _flag  = new ArrayList<>();
	private int _numFlagsAdded = 0;
	private SiegeClanType _type;

	public enum SiegeClanType
	{
		OWNER,
		DEFENDER,
		ATTACKER,
		DEFENDER_PENDING
	}

	// =========================================================
	// Constructor

	public L2SiegeClan(int clanId, SiegeClanType type)
	{
		_clanId = clanId;
		_type = type;
	}

	// =========================================================
	// Method - Public
	public int getNumFlags()
	{
		return _numFlagsAdded;
	}

	public void addFlag(L2NpcInstance flag)
	{
		_numFlagsAdded++;
		getFlag().add(flag);
	}

	public boolean removeFlag(L2NpcInstance flag)
	{
		if (flag == null) return false;
		boolean ret = getFlag().remove(flag);
		flag.deleteMe();
		_numFlagsAdded--; 
		if (ret)
			while (getFlag().remove(flag)) ;

		// now delete nulls
		int n;
		boolean more = true;
		while (more)
		{
			more = false;
			n = getFlag().size();
			if (n>0)
				for(int i=0; i<n;i++)
					if(getFlag().get(i)==null)
					{
						getFlag().remove(i);
						more = true;
						break;
					}
		}

		flag.deleteMe();
		return ret;
	}

	public void removeFlags()
	{
		for (L2NpcInstance flag: getFlag())
			removeFlag(flag);
	}

	// =========================================================
	// Proeprty
	public final int getClanId() { return _clanId; }

	public final List<L2NpcInstance> getFlag()
	{
		if (_flag == null) _flag  = new ArrayList<>();
		return _flag;
	}

	public SiegeClanType getType() { return _type; }

    public void setType(SiegeClanType setType) { _type = setType; }
}
