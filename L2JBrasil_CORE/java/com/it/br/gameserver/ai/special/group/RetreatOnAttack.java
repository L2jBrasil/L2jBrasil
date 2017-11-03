/**
 *  @author Vhalior
 */
package com.it.br.gameserver.ai.special.group;

import java.util.HashMap;
import java.util.Map;

import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.L2CharPosition;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.network.serverpackets.CreatureSay;
import com.it.br.util.Rnd;

public class RetreatOnAttack extends Quest
{
	private static Map<Integer, int[]> mobs = new HashMap<>();
	private static Map<Integer, String[]> text = new HashMap<>();

	static
	{
		mobs.put(20432, new int[]{100, 100}); // Elpy
		mobs.put(20002, new int[]{100, 100}); // Rabbit
		mobs.put(20058, new int[]{50, 10}); // Ol Mahum Guard
		mobs.put(20158, new int[]{30, 1}); // Medusa
		mobs.put(20207, new int[]{30, 1}); // Ol Mahum Guerilla
		mobs.put(20208, new int[]{50, 1}); // Ol Mahum Raider
		mobs.put(20497, new int[]{30, 80}); // Turek Orc Skirmisher
		mobs.put(20500, new int[]{30, 70}); // Turek Orc Sentinel
		mobs.put(21508, new int[]{30, 100}); // Splinter Stakato
		mobs.put(21509, new int[]{30, 100}); // Splinter Stakato Walker
		mobs.put(21510, new int[]{30, 100}); // Splinter Stakato Soldier
		mobs.put(21511, new int[]{30, 100}); // Splinter Stakato Drone
		mobs.put(21512, new int[]{30, 100}); // Splinter Stakato Drone
		mobs.put(21513, new int[]{30, 100}); // Needle Stakato
		mobs.put(21514, new int[]{30, 100}); // Needle Stakato Worker
		mobs.put(21515, new int[]{30, 100}); // Needle Stakato Soldier

		text.put(20058, new String[]{"I'll be back!", "You are stronger than expected"});
		text.put(20158, new String[]{"It's a good day to die! Welcome to hell, maggot!", "Some day I will kick ya ass!", "Don't cry, I will come back!", "I'll be back!", "Oh! Oh! Oh! Go your'e way! I die my Lord!!!"});		
		text.put(20207, new String[]{"Invader!", "I'll give you ten million adenas if you let me live!", "I will definitely repay this humiliation!", "There is no reason for you to kill me! I have nothing you need!", "Tactical retreat!", "Some day you will pay!", "Stop hitting!", "We shall see about that!", "I'll kill you next time!", "I definitely kill you next time!"});
		text.put(20208, new String[]{"Retreat!", "Mass fleeing!", "Surrender!", "I'll be back!", "Oh! How strong!"});
		text.put(20497, new String[]{"Oh! How Strong!", "We shall see about that", "I'll definitely kill you next time"});
		text.put(20500, new String[]{"Oh! How Strong!", "We shall see about that", "I'll definitely kill you next time"});
	}

	public RetreatOnAttack(int questId, String name, String descr)
	{
		super(questId, name, descr);
		for(int id : mobs.keySet())
		{
			addAttackId(id);
		}
	}

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		if(event.equals("Retreat") && npc != null && player != null)
		{
			npc.setIsAfraid(false);
			((L2Attackable) npc).addDamageHate(player, 0, 100);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
		}

		return super.onAdvEvent(event, npc, player);
	}

	@Override
	public String onAttack(L2NpcInstance npc, L2PcInstance player, int damage, boolean isPet)
	{
		final int npcId = npc.getNpcId();
		if(mobs.containsKey(npcId))
		{
			if(npc.getCurrentHp() < npc.getMaxHp() * mobs.get(npcId)[0] / 100 && Rnd.get(100) < mobs.get(npcId)[1])
			{
				if(text.containsKey(npcId))
				{
					final String[] msg = text.get(npcId);
					npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 0, npc.getName(), msg[Rnd.get(msg.length)]));
				}
				int posX = npc.getX();
				int posY = npc.getY();
				int posZ = npc.getZ();
				posX = (npc.getX() > player.getX()) ? posX + 500 : posX - 500;
				posY = (npc.getY() > player.getY()) ? posY + 500 : posY - 500;
				npc.setIsAfraid(true);
				npc.setRunning();
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(posX, posY, posZ, 0));
				startQuestTimer("Retreat", 10000, npc, player);
			}
		}

		return super.onAttack(npc, player, damage, isPet);
	}

}