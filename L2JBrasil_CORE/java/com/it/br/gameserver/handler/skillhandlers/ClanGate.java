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

import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.instancemanager.GrandBossManager;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

public class ClanGate implements ISkillHandler
{
	//private static Logger _log = LoggerFactory.getLogger(Recall.class);
	private static final SkillType[] SKILL_IDS = { SkillType.CLAN_GATE };

	public void useSkill(L2Character activeChar, L2Skill skill, L2Object targets[])
    {
        L2PcInstance player = null;
        if(activeChar instanceof L2PcInstance)
            player = (L2PcInstance)activeChar;
        else
            return;

        if(player.isInFunEvent() || player.isInsideZone(64) || player.isInOlympiadMode() || player.isInsideZone(1) || GrandBossManager.getInstance().getZone(player) != null)
        {
            player.sendMessage("Cannot open the portal here.");
            return;
        }
        L2Clan clan = player.getClan();
        if(clan != null && CastleManager.getInstance().getCastleByOwner(clan) != null)
        {
            Castle castle = CastleManager.getInstance().getCastleByOwner(clan);
            if(player.isCastleLord(castle.getCastleId()))
            {
                ThreadPoolManager.getInstance().scheduleGeneral(new RemoveClanGate(castle.getCastleId(), player), skill.getTotalLifeTime());
                castle.createClanGate(player.getX(), player.getY(), player.getZ() + 20);
                player.getClan().broadcastToOnlineMembers(new SystemMessage(SystemMessageId.COURT_MAGICIAN_CREATED_PORTAL));
                player.setIsParalyzed(true);
            }
        }
        L2Effect effect = player.getFirstEffect(skill.getId());
        if(effect != null && effect.isSelfEffect())
            effect.exit();
        skill.getEffectsSelf(player);
    }

	private class RemoveClanGate implements Runnable
    {
		private final int castle;
		private final L2PcInstance player;

		protected RemoveClanGate(int castle, L2PcInstance player)
		{
			this.castle = castle;
			this.player = player;
		}

        @Override
        public void run()
        {
            if(player != null)
            {
                player.setIsParalyzed(false);
            }
            CastleManager.getInstance().getCastleById(castle).destroyClanGate();
        }

    }

    public SkillType[] getSkillIds()
    {
        return SKILL_IDS;
    }
}