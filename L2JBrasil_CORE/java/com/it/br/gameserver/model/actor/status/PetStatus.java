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

import com.it.br.gameserver.ai.CtrlEvent;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

public class PetStatus extends SummonStatus
{
    // =========================================================
    // Data Field
    private int _currentFed               = 0; //Current Fed of the L2PetInstance

    // =========================================================
    // Constructor
    public PetStatus(L2PetInstance activeChar)
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

        super.reduceHp(value, attacker, awake);

        if (attacker != null)
        {
            SystemMessage sm = new SystemMessage(SystemMessageId.PET_RECEIVED_S2_DAMAGE_BY_S1);
            if (attacker instanceof L2NpcInstance)
                sm.addNpcName(((L2NpcInstance)attacker).getTemplate().idTemplate);
            else
                sm.addString(attacker.getName());
            sm.addNumber((int)value);
            getActiveChar().getOwner().sendPacket(sm);

            getActiveChar().getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, attacker);
        }
    }

    // =========================================================
    // Method - Private

    // =========================================================
    // Property - Public

	@Override
	public L2PetInstance getActiveChar() { return (L2PetInstance)super.getActiveChar(); }

    public int getCurrentFed() { return _currentFed; }
    public void setCurrentFed(int value) { _currentFed = value; }
}