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
package com.it.br.gameserver.handler.itemhandlers;

import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2MonsterInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * This class ...
 *
 * @version $Revision: 1.2.4 $ $Date: 2005/08/14 21:31:07 $
 */

public class SoulCrystals implements IItemHandler
{
	// First line is for Red Soul Crystals, second is Green and third is Blue Soul Crystals,
	// ordered by ascending level, from 0 to 13...
	private static final int[] ITEM_IDS = { 4629, 4630, 4631, 4632, 4633, 4634, 4635, 4636, 4637, 4638, 4639, 5577, 5580, 5908,
									  4640, 4641, 4642, 4643, 4644, 4645, 4646, 4647, 4648, 4649, 4650, 5578, 5581, 5911,
									  4651, 4652, 4653, 4654, 4655, 4656, 4657, 4658, 4659, 4660, 4661, 5579, 5582, 5914};

	// Our main method, where everything goes on

	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance)playable;
		L2Object target = activeChar.getTarget();
		if (!(target instanceof L2MonsterInstance))
		{
			// Send a System Message to the caster
            SystemMessage sm = new SystemMessage(SystemMessageId.INCORRECT_TARGET);
			activeChar.sendPacket(sm);

			// Send a Server->Client packet ActionFailed to the L2PcInstance
            ActionFailed af = new ActionFailed();
            activeChar.sendPacket(af);

            return;
		}

        // u can use soul crystal only when target hp goes below 50%
        if(((L2MonsterInstance)target).getCurrentHp() > ((L2MonsterInstance)target).getMaxHp()/2.0)
        {
            ActionFailed af = new ActionFailed();
            activeChar.sendPacket(af);
            return;
        }

		int crystalId = item.getItemId();

        // Soul Crystal Casting section
        L2Skill skill = SkillTable.getInstance().getInfo(2096, 1);
        activeChar.useMagic(skill, false, true);
        // End Soul Crystal Casting section

        // Continue execution later
        CrystalFinalizer cf = new CrystalFinalizer(activeChar, target, crystalId);
        ThreadPoolManager.getInstance().scheduleEffect(cf, skill.getHitTime());

	}

	static class CrystalFinalizer implements Runnable
	{
		private L2PcInstance _activeChar;
		private L2Attackable _target;
		private int _crystalId;

		CrystalFinalizer(L2PcInstance activeChar, L2Object target, int crystalId)
		{
		    _activeChar = activeChar;
		    _target = (L2Attackable)target;
		    _crystalId = crystalId;
		}

	
		public void run()
		{
        	if (_activeChar.isDead() || _target.isDead())
                return;
        	_activeChar.enableAllSkills();
            try {
            	_target.addAbsorber(_activeChar, _crystalId);
            	_activeChar.setTarget(_target);
            } catch (Throwable e) {
                e.printStackTrace();
            }
		}
	}


	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
