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
package com.it.br.gameserver.model.actor.status;

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;

public class NpcStatus extends CharStatus
{
    // =========================================================
    // Data Field

    // =========================================================
    // Constructor
    public NpcStatus(L2NpcInstance activeChar)
    {
        super(activeChar);
    }

    // =========================================================
    // Method - Public

	@Override
	public final void reduceHp(double value, L2Character attacker) { reduceHp(value, attacker, true); }


	@Override
	public final void reduceHp(double value, L2Character attacker, boolean awake)
    {
        if (getActiveChar().isDead()) return;

        // Add attackers to npc's attacker list
        if (attacker != null) getActiveChar().addAttackerToAttackByList(attacker);

        super.reduceHp(value, attacker, awake);
    }

    // =========================================================
    // Method - Private

    // =========================================================
    // Property - Public

	@Override
	public L2NpcInstance getActiveChar() { return (L2NpcInstance)super.getActiveChar(); }
}
