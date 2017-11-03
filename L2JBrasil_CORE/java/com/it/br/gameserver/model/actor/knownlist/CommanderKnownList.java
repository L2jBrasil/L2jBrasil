/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.it.br.gameserver.model.actor.knownlist;

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2CommanderInstance;

public class CommanderKnownList extends AttackableKnownList
{
	// =========================================================
	// Data Field

	// =========================================================
	// Constructor
	public CommanderKnownList(L2CommanderInstance activeChar)
	{
		super(activeChar);
	}

	// =========================================================
	// Method - Public

	@Override
	public boolean addKnownObject(L2Object object)
	{
		return addKnownObject(object, null);
	}


	@Override
	public boolean addKnownObject(L2Object object, L2Character dropper)
	{
		if(!super.addKnownObject(object, dropper))
			return false;

		if(getActiveChar().getHomeX() == 0)
		{
			getActiveChar().getHomeLocation();
		}

		return true;
	}

	// =========================================================
	// Method - Private

	// =========================================================
	// Property - Public

	@Override
	public final L2CommanderInstance getActiveChar()
	{
		return (L2CommanderInstance) super.getActiveChar();
	}
}
