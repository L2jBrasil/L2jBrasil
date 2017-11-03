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
package com.it.br.gameserver.model.actor.knownlist;

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;


/**
 * @author Maktakien
 *
 */
public class BoatKnownList extends CharKnownList
{

	/**
	 * @param activeChar
	 */
	public BoatKnownList(L2Character activeChar)
	{
		super(activeChar);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getDistanceToForgetObject(L2Object object)
    {
        if (!(object instanceof L2PcInstance))
            return 0;
        return 8000;
    }


	@Override
	public int getDistanceToWatchObject(L2Object object)
    {
        if (!(object instanceof L2PcInstance))
            return 0;
        return 4000;
    }

}
