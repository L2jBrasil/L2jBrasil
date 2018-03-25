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
package com.it.br.gameserver.network.clientpackets;

import com.it.br.Config;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.xml.SkillSpellbookTable;
import com.it.br.gameserver.datatables.xml.SkillTreeTable;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.actor.instance.*;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.*;

/**
 * This class ...
 *
 * @version $Revision: 1.7.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestAquireSkill extends L2GameClientPacket
{
	private static final String _C__6C_REQUESTAQUIRESKILL = "[C] 6C RequestAquireSkill";
	//private static Logger _log = LoggerFactory.getLogger((RequestAquireSkill).class);
	private int _id;
	private int _level;
	private int _skillType;


	@Override
	protected void readImpl()
	{
		_id = readD();
		_level = readD();
		_skillType = readD();
	}


	@Override
	protected void runImpl()
	{

		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
			return;

		L2FolkInstance trainer = player.getLastFolkNPC();
		if (trainer == null)
			return;

		int npcid = trainer.getNpcId();

		if (!player.isInsideRadius(trainer, L2NpcInstance.INTERACTION_DISTANCE, false, false) && !player.isGM())
			return;

		if (!Config.ALT_GAME_SKILL_LEARN) player.setSkillLearningClassId(player.getClassId());

		if (player.getSkillLevel(_id) >= _level)
		{
			// already knows the skill with this level
			return;
		}

		L2Skill skill = SkillTable.getInstance().getInfo(_id, _level);

		int counts = 0;
		int _requiredSp = 10000000;

		if (_skillType == 0)
		{
			L2SkillLearn[] skills = SkillTreeTable.getInstance() .getAvailableSkills(player, player.getSkillLearningClassId());

			for (L2SkillLearn s : skills)
			{
				L2Skill sk = SkillTable.getInstance().getInfo(s.getId(),s.getLevel());
				if (sk == null || sk != skill || !sk.getCanLearn(player.getSkillLearningClassId()) || !sk.canTeachBy(npcid))
					continue;
				counts++;
				_requiredSp = SkillTreeTable.getInstance().getSkillCost(player,skill);
			}

			if (counts == 0 && !Config.ALT_GAME_SKILL_LEARN)
			{
				player.sendMessage("Can't learn that skill.");
				return;
			}

			if (player.getSp() >= _requiredSp)
			{
				if (Config.SP_BOOK_NEEDED)
				{
					int spbId = -1; 
					if (skill.getId() == L2Skill.SKILL_DIVINE_INSPIRATION) 
						spbId = SkillSpellbookTable.getInstance().getBookForSkill(skill, _level); 
					else 
						spbId = SkillSpellbookTable.getInstance().getBookForSkill(skill); 

					if (skill.getLevel() == 1 && spbId > -1)
					{
						L2ItemInstance spb = player.getInventory() .getItemByItemId(spbId);

						if (spb == null)
						{
							// Haven't spellbook
							player.sendPacket(new SystemMessage(SystemMessageId.ITEM_MISSING_TO_LEARN_SKILL));
							return;
						}
						player.destroyItem("Consume", spb, trainer, true);
					}
				}
			} 
			else
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.NOT_ENOUGH_SP_TO_LEARN_SKILL);
				player.sendPacket(sm);
				sm = null;
				return;
			}
		} 
		else if (_skillType == 1)
		{
			int costid = 0;
			int costcount = 0;
			// Skill Learn bug Fix
			L2SkillLearn[] skillsc = SkillTreeTable.getInstance() .getAvailableSkills(player);

			for (L2SkillLearn s : skillsc)
			{
				L2Skill sk = SkillTable.getInstance().getInfo(s.getId(),s.getLevel());

				if (sk == null || sk != skill)
					continue;

				counts++;
				costid = s.getIdCost();
				costcount = s.getCostCount();
				_requiredSp = s.getSpCost();
			}
			if (counts == 0)
			{
				player.sendMessage("Can't learn that skill.");
				return;
			}

			if (player.getSp() >= _requiredSp)
			{
				if (!player.destroyItemByItemId("Consume", costid, costcount,trainer, false))
				{
					// Haven't spellbook
					player.sendPacket(new SystemMessage(SystemMessageId.ITEM_MISSING_TO_LEARN_SKILL));
					return;
				}
				SystemMessage sm = new SystemMessage(SystemMessageId.DISSAPEARED_ITEM);
				sm.addNumber(costcount);
				sm.addItemName(costid);
				sendPacket(sm);
				sm = null;
			} 
			else
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.NOT_ENOUGH_SP_TO_LEARN_SKILL);
				player.sendPacket(sm);
				sm = null;
				return;
			}
		}
		else if (_skillType == 2) //pledgeskills TODO: Find appropriate system messages.
        {
            if (!player.isClanLeader())
            {
            	// TODO: Find and add system msg
            	player.sendMessage("This feature is available only for the clan's leader");
            	return;
            }

			int itemId = 0;
            int repCost = 100000000;
            // Skill Learn bug Fix
            L2PledgeSkillLearn[] skills = SkillTreeTable.getInstance().getAvailablePledgeSkills(player);

            for (L2PledgeSkillLearn s : skills)
            {
                L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());

                if (sk == null || sk != skill)
                    continue;

                counts++;
                itemId = s.getItemId();
                repCost = s.getRepCost();
            }

            if (counts == 0)
            {
                player.sendMessage("Can't learn that skill.");
                return;
            }

            if (player.getClan().getReputationScore() >= repCost)
            {
            	if (Config.LIFE_CRYSTAL_NEEDED)
            	{
            		if (!player.destroyItemByItemId("Consume", itemId, 1, trainer, false))
            		{
            			// Haven't spellbook
            			player.sendPacket(new SystemMessage(SystemMessageId.ITEM_MISSING_TO_LEARN_SKILL));
            			return;
            		}
            		SystemMessage sm = new SystemMessage(SystemMessageId.DISSAPEARED_ITEM);
            		sm.addItemName(itemId);
            		sm.addNumber(1);
            		sendPacket(sm);
            		sm = null;
            	}
            }
            else
            {
                SystemMessage sm = new SystemMessage(SystemMessageId.ACQUIRE_SKILL_FAILED_BAD_CLAN_REP_SCORE);
                player.sendPacket(sm);
                //sm = null;
                return;
            }
            player.getClan().setReputationScore(player.getClan().getReputationScore()-repCost, true);
            player.getClan().addNewSkill(skill);

            if (Config.DEBUG)
                _log.debug("Learned pledge skill " + _id + " for " + _requiredSp + " SP.");

            SystemMessage cr = new SystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP);
            cr.addNumber(repCost);
            player.sendPacket(cr);
            SystemMessage sm = new SystemMessage(SystemMessageId.CLAN_SKILL_S1_ADDED);
            sm.addSkillName(_id);
            player.sendPacket(sm);
            sm = null;
            
            player.getClan().broadcastToOnlineMembers(new PledgeSkillList(player.getClan()));
            
            for(L2PcInstance member: player.getClan().getOnlineMembers("")) 
            {
            	member.sendSkillList();
            }
            ((L2VillageMasterInstance)trainer).showPledgeSkillList(player); //Maybe we shoud add a check here...
            
            return;
        }
		else
		{
			_log.warn("Recived Wrong Packet Data in Aquired Skill - unk1:" + _skillType);
			return;
		}

		player.addSkill(skill, true);

		if (Config.DEBUG)
			_log.debug("Learned skill " + _id + " for " + _requiredSp + " SP.");

		player.setSp(player.getSp() - _requiredSp);

		StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdate.SP, player.getSp());
		player.sendPacket(su);

                SystemMessage sp = new SystemMessage(SystemMessageId.SP_DECREASED_S1);
                sp.addNumber(_requiredSp);
                sendPacket(sp);

		SystemMessage sm = new SystemMessage(SystemMessageId.LEARNED_SKILL_S1);
		sm.addSkillName(_id);
		player.sendPacket(sm);
		sm = null;
		
		// update all the shortcuts to this skill
		if (_level > 1)
		{
			L2ShortCut[] allShortCuts = player.getAllShortCuts();

			for (L2ShortCut sc : allShortCuts)
			{
				if (sc.getId() == _id && sc.getType() == L2ShortCut.TYPE_SKILL)
				{
					L2ShortCut newsc = new L2ShortCut(sc.getSlot(), sc .getPage(), sc.getType(), sc.getId(), _level, 1);
					player.sendPacket(new ShortCutRegister(newsc));
					player.registerShortCut(newsc);
				}
			}
		}

		if (trainer instanceof L2FishermanInstance)
			((L2FishermanInstance) trainer).showSkillList(player);
		else
			trainer.showSkillList(player, player.getSkillLearningClassId());

		if (_id >= 1368 && _id <= 1372) // if skill is expand sendpacket :)
		{
			ExStorageMaxCount esmc = new ExStorageMaxCount(player);
			player.sendPacket(esmc);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__6C_REQUESTAQUIRESKILL;
	}
}
