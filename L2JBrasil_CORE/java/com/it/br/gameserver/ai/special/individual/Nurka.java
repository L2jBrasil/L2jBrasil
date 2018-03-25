package com.it.br.gameserver.ai.special.individual;

import com.it.br.gameserver.instancemanager.clanhallsiege.FortResistSiege;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.State;

import java.util.ArrayList;
import java.util.List;

public class Nurka extends Quest implements Runnable
{
	private static final int NURKA = 35368;
	private static final int MESSENGER = 35382;
	private static List<String> CLAN_LEADERS = new ArrayList<>();

	public Nurka(int questId, String name, String descr)
	{
		super(questId, name, descr);
		State CREATED = new State("Start", this);
		this.setInitialState(CREATED);
				
		addTalkId(MESSENGER);
		addStartNpc(MESSENGER);
		addAttackId(NURKA);
		addKillId(NURKA);
	}
	
	@Override
	public String onTalk(L2NpcInstance npc, L2PcInstance player)
	{
		if(npc.getNpcId() == MESSENGER)
		{
			if(player != null && CLAN_LEADERS.contains(player.getName()))
				return "<html><body>Messenger:<br>You already registered!</body></html>";
			else if(FortResistSiege.Conditions(player))
			{
				CLAN_LEADERS.add(player.getName());
				return "<html><body>Messenger:<br>You have successful registered on a siege!</body></html>";
			}
			else
				return "<html><body>Messenger:<br>Condition are not allow to do that!</body></html>";		
		}

		return super.onTalk(npc, player);
	}

	@Override
	public String onAttack(L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if(attacker != null && npc.getNpcId() == NURKA && CLAN_LEADERS.contains(attacker.getName()));
			FortResistSiege.getInstance().addSiegeDamage(attacker.getClan(),damage);

		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance killer, boolean isPet)
	{
		FortResistSiege.getInstance().CaptureFinish();
			return super.onKill(npc, killer, isPet);
	}

    public static void main(String[] args)
    {
    	new Nurka(-1,"Nurka","ai");
    }

	@Override
	public void run()
	{}
}