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
package com.it.br.gameserver.ai.special.group;

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.network.serverpackets.CreatureSay;
import com.it.br.util.Rnd;

public class CatsEyeBandit extends Quest
{
	private static final int NPC = 27038;
	private static boolean _FirstAttacked;

	public CatsEyeBandit(int questId, String name, String descr)
	{
		super(questId, name, descr);

		_FirstAttacked = false;

		addEventId(NPC, Quest.QuestEventType.ON_ATTACK);
		addEventId(NPC, Quest.QuestEventType.ON_KILL);
	}

	@Override
	public String onAttack (L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if(npc.getNpcId() == NPC)
		{
			if(_FirstAttacked)
			{
				if(Rnd.get(40) != 0)
				{
					return null;
				}
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(),0,npc.getName(),"You childish fool, do you think you can catch me?"));
			}
			else
			{
				_FirstAttacked = true;
				{
					return null;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance killer, boolean isPet)
	{
		if(npc.getNpcId() == NPC)
		{
			if(Rnd.get(80) != 0)
			{
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(),0,npc.getName(),"I must do something about this shameful incident..."));
				_FirstAttacked = false;
			}
		}
		else if(_FirstAttacked)
		{
			addSpawn(npc.getNpcId(),npc.getX(), npc.getY(), npc.getZ(),npc.getHeading(),true,0);
		}
		return super.onKill(npc,killer,isPet);
	}

}