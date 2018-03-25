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
import com.it.br.gameserver.datatables.xml.SkillTreeTable;
import com.it.br.gameserver.model.L2EnchantSkillLearn;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2ShortCut;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2FolkInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ShortCutRegister;
import com.it.br.gameserver.network.serverpackets.StatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.util.IllegalPlayerAction;
import com.it.br.gameserver.util.Util;
import com.it.br.util.Rnd;


public final class RequestExEnchantSkill extends L2GameClientPacket
{
	private static final String _C__D0_07_REQUESTEXENCHANTSKILL = "[C] D0:07 RequestExEnchantSkill";
	//private static Logger _log = LoggerFactory.getLogger(RequestAquireSkill.class);
	private int _skillId;
	private int _skillLvl;


	@Override
	protected void readImpl()
	{
		_skillId = readD();
		_skillLvl = readD();
	}
	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#runImpl()
	 */

	@Override
	protected
	void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
        if (player == null)
        	return;

        L2FolkInstance trainer = player.getLastFolkNPC();
        if (trainer == null)
        	return;
        int npcid = trainer.getNpcId();
        if ((trainer == null || !player.isInsideRadius(trainer, L2NpcInstance.INTERACTION_DISTANCE, false, false)) && !player.isGM())
            return;
        if (player.getSkillLevel(_skillId) >= _skillLvl)// already knows the skill with this level
            return;
        if (player.getClassId().getId() < 88) // requires to have 3rd class quest completed
    		return;
        if (player.getLevel() < 76) return;
        L2Skill skill = SkillTable.getInstance().getInfo(_skillId, _skillLvl);
        int counts = 0;
        int _requiredSp = 10000000;
        int _requiredExp = 100000;
        byte _rate = 0;
        int _baseLvl = 1;

        L2EnchantSkillLearn[] skills = SkillTreeTable.getInstance().getAvailableEnchantSkills(player);

        for (L2EnchantSkillLearn s : skills)
        {
        	L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
        	if (sk == null || sk != skill || !sk.getCanLearn(player.getClassId())
        			|| !sk.canTeachBy(npcid)) continue;
        	counts++;
        	_requiredSp = s.getSpCost();
        	_requiredExp = s.getExp();
        	_rate = s.getRate(player);
        	_baseLvl = s.getBaseLevel();
        }
        if (counts == 0 && !Config.ALT_GAME_SKILL_LEARN)
        {
        	player.sendMessage("You are trying to learn skill that u can't..");
        	Util.handleIllegalPlayerAction(player, "Player " + player.getName()
        			+ " tried to learn skill that he can't!!!", IllegalPlayerAction.PUNISH_KICK);
        	return;
        }
        if (player.getSp() >= _requiredSp)
        {
        	if (player.getExp() >= _requiredExp)
        	{
        		if (Config.ES_SP_BOOK_NEEDED && (_skillLvl == 101 || _skillLvl == 141)) // only first lvl requires book
            	{
            		int spbId = 6622;

            		L2ItemInstance spb = player.getInventory().getItemByItemId(spbId);

            		if (spb == null)// Haven't spellbook
            		{
            			player.sendPacket(new SystemMessage(SystemMessageId.YOU_DONT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL));
            			return;
            		}
            		// ok
            		player.destroyItem("Consume", spb, trainer, true);
            	}
        	}
        	else
        	{
        		SystemMessage sm = new SystemMessage(SystemMessageId.YOU_DONT_HAVE_ENOUGH_EXP_TO_ENCHANT_THAT_SKILL);
            	player.sendPacket(sm);
        		return;
        	}
        }
        else
        {
        	SystemMessage sm = new SystemMessage(SystemMessageId.YOU_DONT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
        	player.sendPacket(sm);
        	return;
        }
        if (Rnd.get(100) <= _rate)
        {
        	player.addSkill(skill, true);

        	if (Config.DEBUG)
        		_log.debug("Learned skill " + _skillId + " for " + _requiredSp + " SP.");

        	player.getStat().removeExpAndSp(_requiredExp, _requiredSp);

        	StatusUpdate su = new StatusUpdate(player.getObjectId());
        	su.addAttribute(StatusUpdate.SP, player.getSp());
        	player.sendPacket(su);

            SystemMessage ep = new SystemMessage(SystemMessageId.EXP_DECREASED_BY_S1);
            ep.addNumber(_requiredExp);
            sendPacket(ep);

            SystemMessage sp = new SystemMessage(SystemMessageId.SP_DECREASED_S1);
            sp.addNumber(_requiredSp);
            sendPacket(sp);

        	SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_SUCCEEDED_IN_ENCHANTING_THE_SKILL_S1);
        	sm.addSkillName(_skillId);
        	player.sendPacket(sm);
        }
        else
        {
        	if (skill.getLevel() > 100)
        	{
        		_skillLvl = _baseLvl;
        		player.addSkill(SkillTable.getInstance().getInfo(_skillId, _skillLvl), true);
        		player.sendSkillList(); 
        	}
        	SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_ENCHANT_THE_SKILL_S1);
        	sm.addSkillName(_skillId);
        	player.sendPacket(sm);
        }
        trainer.showEnchantSkillList(player, player.getClassId());

        // update all the shortcuts to this skill
        L2ShortCut[] allShortCuts = player.getAllShortCuts();

        for (L2ShortCut sc : allShortCuts)
        {
        	if (sc.getId() == _skillId && sc.getType() == L2ShortCut.TYPE_SKILL)
        	{
        		L2ShortCut newsc = new L2ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), _skillLvl, 1);
        		player.sendPacket(new ShortCutRegister(newsc));
        		player.registerShortCut(newsc);
        	}
        }
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.BasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__D0_07_REQUESTEXENCHANTSKILL;
	}
}
