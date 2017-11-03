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
package com.it.br.gameserver.network.serverpackets;

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;


/**
 * sample
 * 06 8f19904b 2522d04b 00000000 80 950c0000 4af50000 08f2ffff 0000    - 0 damage (missed 0x80)
 * 06 85071048 bc0e504b 32000000 10 fc41ffff fd240200 a6f5ffff 0100 bc0e504b 33000000 10                                     3....

 * format
 * dddc dddh (ddc)
 *
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class Attack extends L2GameServerPacket
{
    private class Hit
    {
    	protected int _targetId;
    	protected int _damage;
    	protected int _flags;

        Hit(L2Object target, int damage, boolean miss, boolean crit, boolean shld)
        {
            _targetId = target.getObjectId();
            _damage = damage;
            if (soulshot) _flags |= 0x10 | _grade;
            if (crit)      _flags |= 0x20;
            if (shld)      _flags |= 0x40;
            if (miss)      _flags |= 0x80;

        }
    }

	// dh

	private static final String _S__06_ATTACK = "[S] 06 Attack";
	protected final int _attackerObjId;
	public final boolean soulshot;
    protected int _grade;
	private int _x;
	private int _y;
	private int _z;
	private Hit[] _hits;

	/**
	 * @param attacker the attacker L2Character
	 * @param ss true if useing SoulShots
	 */
	public Attack(L2Character attacker, boolean ss, int grade)
	{
		_attackerObjId = attacker.getObjectId();
		soulshot = ss;
        _grade = grade;
		_x = attacker.getX();
		_y = attacker.getY();
		_z = attacker.getZ();
		_hits = new Hit[0];
	}

	/**
	 * Add this hit (target, damage, miss, critical, shield) to the Server-Client packet Attack.<BR><BR>
	 */
	public void addHit(L2Object target, int damage, boolean miss, boolean crit, boolean shld)
	{
		// Get the last position in the hits table
		int pos = _hits.length;

		// Create a new Hit object
		Hit[] tmp = new Hit[pos+1];

		// Add the new Hit object to hits table
		for (int i=0; i < _hits.length; i++)
			tmp[i] = _hits[i];
		tmp[pos] = new Hit(target, damage, miss, crit, shld);
		_hits = tmp;
	}

	/**
	 * Return True if the Server-Client packet Attack conatins at least 1 hit.<BR><BR>
	 */
	public boolean hasHits()
	{
		return _hits.length > 0;
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0x05);

		writeD(_attackerObjId);
		writeD(_hits[0]._targetId);
		writeD(_hits[0]._damage);
		writeC(_hits[0]._flags);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeH(_hits.length-1);
		for (int i=1; i < _hits.length; i++)
		{
			writeD(_hits[i]._targetId);
			writeD(_hits[i]._damage);
			writeC(_hits[i]._flags);
		}
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__06_ATTACK;
	}
}
