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
package com.it.br.gameserver.model.actor.knownlist;

import com.it.br.gameserver.ai.CtrlEvent;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2FriendlyMobInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

public class FriendlyMobKnownList extends AttackableKnownList
{
    // =========================================================
    // Data Field

    // =========================================================
    // Constructor
    public FriendlyMobKnownList(L2FriendlyMobInstance activeChar)
    {
        super(activeChar);
    }

    // =========================================================
    // Method - Public

	@Override
	public boolean addKnownObject(L2Object object) { return addKnownObject(object, null); }

	@Override
	public boolean addKnownObject(L2Object object, L2Character dropper)
    {
        if (!super.addKnownObject(object, dropper)) return false;

        if (object instanceof L2PcInstance && getActiveChar().getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
            getActiveChar().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null);

        return true;
    }


	@Override
	public boolean removeKnownObject(L2Object object)
    {
        if (!super.removeKnownObject(object)) return false;

        if (!(object instanceof L2Character)) return true;

        if (getActiveChar().hasAI()) {
            L2Character temp = (L2Character)object;
            getActiveChar().getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
            if (getActiveChar().getTarget() == temp) getActiveChar().setTarget(null);
        }

        if (getActiveChar().isVisible() && getKnownPlayers().isEmpty())
        {
            getActiveChar().clearAggroList();
            //removeAllKnownObjects();
            if (getActiveChar().hasAI()) getActiveChar().getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null);
        }

        return true;
    }

    // =========================================================
    // Method - Private

    // =========================================================
    // Property - Public

	@Override
	public final L2FriendlyMobInstance getActiveChar() { return (L2FriendlyMobInstance)super.getActiveChar(); }
}
