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

import com.it.br.util.Rnd;
/**
 * This class defines the spawn data of a Minion type
 * In a group mob, there are one master called RaidBoss and several slaves called Minions.
 *
 * <B><U> Data</U> :</B><BR><BR>
 * <li>_minionId : The Identifier of the L2Minion to spawn </li>
 * <li>_minionAmount :  The number of this Minion Type to spawn </li><BR><BR>
 *
 */
public class L2MinionData
{

	/** The Identifier of the L2Minion */
	private int _minionId;

	/** The number of this Minion Type to spawn */
	private int _minionAmount;
	private int _minionAmountMin;
	private int _minionAmountMax;


	/**
	 * Set the Identifier of the Minion to spawn.<BR><BR>
	 *
	 * @param if The L2Character Identifier to spawn
	 *
	 */
	public void setMinionId(int id)
	{
		_minionId = id;
	}

	/**
	 * Return the Identifier of the Minion to spawn.<BR><BR>
	 */
	public int getMinionId()
	{
		return _minionId;
	}

	/**
	 * Set the minimum of minions to amount.<BR><BR>
	 *
	 * @param amountMin The minimum quantity of this Minion type to spawn
	 *
	 */
	public void setAmountMin(int amountMin)
	{
		_minionAmountMin = amountMin;
	}

	/**
	 * Set the maximum of minions to amount.<BR><BR>
	 *
	 * @param amountMax The maximum quantity of this Minion type to spawn
	 *
	 */
	public void setAmountMax(int amountMax)
	{
		_minionAmountMax = amountMax;
	}

	/**
	 * Set the amount of this Minion type to spawn.<BR><BR>
	 *
	 * @param amount The quantity of this Minion type to spawn
	 *
	 */
	public void setAmount(int amount)
	{
		_minionAmount = amount;
	}

	/**
	 * Return the amount of this Minion type to spawn.<BR><BR>
	 */
	public int getAmount()
	{
		if (_minionAmountMax > _minionAmountMin)
		{
			_minionAmount = Rnd.get(_minionAmountMin, _minionAmountMax);
			return _minionAmount;
		}
		else
		{
			return _minionAmountMin;
		}
	}

}
