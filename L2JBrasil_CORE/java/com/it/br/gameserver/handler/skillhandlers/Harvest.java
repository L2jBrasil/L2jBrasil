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
package com.it.br.gameserver.handler.skillhandlers;


import com.it.br.Config;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2MonsterInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author l3x
 */
public class Harvest implements ISkillHandler {
    private static Logger _log = LoggerFactory.getLogger(Harvest.class);
    private static final SkillType[] SKILL_IDS = {SkillType.HARVEST};

    private L2PcInstance _activeChar;
    private L2MonsterInstance _target;


    public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets) {
        if (!(activeChar instanceof L2PcInstance))
            return;

        _activeChar = (L2PcInstance) activeChar;

        L2Object[] targetList = skill.getTargetList(activeChar);

        InventoryUpdate iu = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();

        if (targetList == null) {
            return;
        }

        if (_log.isDebugEnabled())
            _log.debug("Casting harvest");

        for (int index = 0; index < targetList.length; index++) {
            if (!(targetList[index] instanceof L2MonsterInstance))
                continue;

            _target = (L2MonsterInstance) targetList[index];

            if (_activeChar != _target.getSeeder()) {
                SystemMessage sm = new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_HARVEST);
                _activeChar.sendPacket(sm);
                continue;
            }

            boolean send = false;
            int total = 0;
            int cropId = 0;

            // TODO: check items and amount of items player harvest
            if (_target.isSeeded()) {
                if (calcSuccess()) {
                    L2Attackable.RewardItem[] items = _target.takeHarvest();
                    if (items != null && items.length > 0) {
                        for (L2Attackable.RewardItem ritem : items) {
                            cropId = ritem.getItemId(); // always got 1 type of crop as reward
                            if (_activeChar.isInParty())
                                _activeChar.getParty().distributeItem(_activeChar, ritem, true, _target);
                            else {
                                L2ItemInstance item = _activeChar.getInventory().addItem("Manor", ritem.getItemId(), ritem.getCount(), _activeChar, _target);
                                if (iu != null) iu.addItem(item);
                                send = true;
                                total += ritem.getCount();
                            }
                        }
                        if (send) {
                            SystemMessage smsg = new SystemMessage(SystemMessageId.YOU_PICKED_UP_S1_S2);
                            smsg.addNumber(total);
                            smsg.addItemName(cropId);
                            _activeChar.sendPacket(smsg);
                            if (_activeChar.getParty() != null) {
                                smsg = new SystemMessage(SystemMessageId.S1_HARVESTED_S3_S2S);
                                smsg.addString(_activeChar.getName());
                                smsg.addNumber(total);
                                smsg.addItemName(cropId);
                                _activeChar.getParty().broadcastToPartyMembers(_activeChar, smsg);
                            }

                            if (iu != null) _activeChar.sendPacket(iu);
                            else _activeChar.sendPacket(new ItemList(_activeChar, false));
                        }
                    }
                } else {
                    _activeChar.sendPacket(new SystemMessage(SystemMessageId.THE_HARVEST_HAS_FAILED));
                }
            } else {
                _activeChar.sendPacket(new SystemMessage(SystemMessageId.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN));
            }
        }

    }

    private boolean calcSuccess() {
        int basicSuccess = 100;
        int levelPlayer = _activeChar.getLevel();
        int levelTarget = _target.getLevel();

        int diff = (levelPlayer - levelTarget);
        if (diff < 0)
            diff = -diff;

        // apply penalty, target <=> player levels
        // 5% penalty for each level
        if (diff > 5) {
            basicSuccess -= (diff - 5) * 5;
        }

        // success rate cant be less than 1%
        if (basicSuccess < 1)
            basicSuccess = 1;

        int rate = Rnd.nextInt(99);

        if (rate < basicSuccess)
            return true;
        return false;
    }


    public SkillType[] getSkillIds() {
        return SKILL_IDS;
    }
}
