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

import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.instancemanager.CastleManorManager;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2MonsterInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * @author  l3x
 */
public class Harvester implements IItemHandler {

	private static final int[] ITEM_IDS = { /* Harvester */ 5125 };
    L2PcInstance _activeChar;
    L2MonsterInstance _target;


	public void useItem(L2PlayableInstance playable, L2ItemInstance _item) {
    	if (!(playable instanceof L2PcInstance))
            return;

    	if (CastleManorManager.getInstance().isDisabled())
        	return;

        _activeChar = (L2PcInstance)playable;

        if(_activeChar.getTarget() == null || !(_activeChar.getTarget() instanceof L2MonsterInstance)) {
            _activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_IS_INCORRECT));
            _activeChar.sendPacket(new ActionFailed());
            return;
        }

        _target = (L2MonsterInstance)_activeChar.getTarget();

        if (_target == null || !_target.isDead()) {
        	_activeChar.sendPacket(new ActionFailed());
        	return;
        }

        L2Skill skill = SkillTable.getInstance().getInfo(2098, 1); //harvesting skill
    	_activeChar.useMagic(skill,false,false);
    }


	public int[] getItemIds() {
        return ITEM_IDS;
    }
}