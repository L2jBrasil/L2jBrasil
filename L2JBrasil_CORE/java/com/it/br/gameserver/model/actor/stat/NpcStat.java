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
package com.it.br.gameserver.model.actor.stat;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.skills.Stats;

public class NpcStat extends CharStat
{
    // =========================================================
    // Data Field

    // =========================================================
    // Constructor
    public NpcStat(L2NpcInstance activeChar)
    {
        super(activeChar);

        setLevel(getActiveChar().getTemplate().level);
    }

    // =========================================================
    // Method - Public

    // =========================================================
    // Method - Private

    // =========================================================
    // Property - Public

    @Override
	public L2NpcInstance getActiveChar() { return (L2NpcInstance)super.getActiveChar(); }


    @Override
	public final int getMaxHp() { return (int)calcStat(Stats.MAX_HP, getActiveChar().getTemplate().baseHpMax , null, null); }
}
