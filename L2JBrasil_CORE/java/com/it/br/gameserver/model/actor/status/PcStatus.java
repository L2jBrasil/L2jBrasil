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

import com.it.br.Config;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Summon;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.model.actor.instance.L2SummonInstance;
import com.it.br.gameserver.model.entity.Duel;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Stats;
import com.it.br.gameserver.util.Util;

public class PcStatus extends PlayableStatus
{
    // =========================================================
    // Data Field

    // =========================================================
    // Constructor
    public PcStatus(L2PcInstance activeChar)
    {
        super(activeChar);
    }

    // =========================================================
    // Method - Public

    @Override
	public final void reduceHp(double value, L2Character attacker) 
    { 
        reduceHp(value, attacker, true); 
    }

    @Override
	public final void reduceHp(double value, L2Character attacker, boolean awake)
    {
        if (getActiveChar().isInvul() && getActiveChar() != attacker) 
        	return;

		if (attacker instanceof L2PcInstance)
		{
			if (getActiveChar().isInDuel())
			{
				// the duel is finishing - players do not recive damage
				if (getActiveChar().getDuelState() == Duel.DUELSTATE_DEAD) return;
				else if (getActiveChar().getDuelState() == Duel.DUELSTATE_WINNER) return;

				// cancel duel if player got hit by another player, that is not part of the duel
				if (((L2PcInstance)attacker).getDuelId() != getActiveChar().getDuelId())
					getActiveChar().setDuelState(Duel.DUELSTATE_INTERRUPTED);
			}

			if (getActiveChar().isDead() && !getActiveChar().isFakeDeath()) return;
		} else {
			// if attacked by a non L2PcInstance & non L2SummonInstance the duel gets canceled
			if (getActiveChar().isInDuel() && !(attacker instanceof L2SummonInstance)) getActiveChar().setDuelState(Duel.DUELSTATE_INTERRUPTED);
			if (getActiveChar().isDead()) return;
		}

		int fullValue = (int) value;

        if (attacker != null && attacker != getActiveChar())
        {
            // Check and calculate transfered damage
            L2Summon summon = getActiveChar().getPet();
            //TODO correct range
            if (summon != null && summon instanceof L2SummonInstance && Util.checkIfInRange(900, getActiveChar(), summon, true))
            {
                int tDmg = (int)value * (int)getActiveChar().getStat().calcStat(Stats.TRANSFER_DAMAGE_PERCENT, 0, null, null) /100;

                // Only transfer dmg up to current HP, it should not be killed
                if (summon.getCurrentHp() < tDmg) tDmg = (int)summon.getCurrentHp() - 1;
                if (tDmg > 0)
                {
                    summon.reduceCurrentHp(tDmg, attacker);
                    value -= tDmg;
                    fullValue = (int) value; // reduce the annouced value here as player will get a message about summon dammage
                }
            }

            if (attacker instanceof L2PlayableInstance)
            {
                if (getCurrentCp() >= value)
                {
                    setCurrentCp(getCurrentCp() - value);   // Set Cp to diff of Cp vs value
                    value = 0;                              // No need to subtract anything from Hp
                }
                else
                {
                    value -= getCurrentCp();                // Get diff from value vs Cp; will apply diff to Hp
                    setCurrentCp(0);                        // Set Cp to 0
                }
            }
        }

        super.reduceHp(value, attacker, awake);

        if (!getActiveChar().isDead() && getActiveChar().isSitting())
            getActiveChar().standUp();

        if (getActiveChar().isFakeDeath())
            getActiveChar().stopFakeDeath(null);

        if (attacker != null && attacker != getActiveChar() && fullValue > 0)
        {
            // Send a System Message to the L2PcInstance
            SystemMessage smsg = new SystemMessage(SystemMessageId.S1_HIT_YOU_S2_DMG);

            if (Config.DEBUG)
                _log.debug("Attacker:" + attacker.getName());

            if (attacker instanceof L2NpcInstance)
            {
                int mobId = ((L2NpcInstance)attacker).getTemplate().idTemplate;

                if (Config.DEBUG)
                    _log.debug("mob id:" + mobId);

                smsg.addNpcName(mobId);
            }
            else if (attacker instanceof L2Summon)
            {
                int mobId = ((L2Summon)attacker).getTemplate().idTemplate;

                smsg.addNpcName(mobId);
            }
            else
            {
                smsg.addString(attacker.getName());
            }

            smsg.addNumber(fullValue);
            getActiveChar().sendPacket(smsg);
        }
    }

    // =========================================================
    // Method - Private

    // =========================================================
    // Property - Public

    @Override
	public L2PcInstance getActiveChar() 
    { 
       return (L2PcInstance)super.getActiveChar(); 
    }
}
