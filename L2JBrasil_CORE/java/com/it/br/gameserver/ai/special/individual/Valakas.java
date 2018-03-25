package com.it.br.gameserver.ai.special.individual;

import com.it.br.Config;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.xml.DoorTable;
import com.it.br.gameserver.instancemanager.GrandBossManager;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.actor.instance.L2GrandBossInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.quest.QuestTimer;
import com.it.br.gameserver.model.quest.State;
import com.it.br.gameserver.model.zone.type.L2BossZone;
import com.it.br.gameserver.network.serverpackets.PlaySound;
import com.it.br.gameserver.network.serverpackets.SocialAction;
import com.it.br.gameserver.network.serverpackets.SpecialCamera;
import com.it.br.gameserver.templates.StatsSet;
import com.it.br.gameserver.util.Util;
import com.it.br.util.Rnd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_FOLLOW;
import static com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;

/**
 * Valakas AI
 * @reworked *slayer
 */

public class Valakas extends Quest implements Runnable
{
	private int i_ai0 = 0;
	private int i_ai1 = 0;
	private int i_ai2 = 0;
	private int i_ai3 = 0;
	private int i_ai4 = 0;
	private int i_quest0 = 0;
	private long i_quest1 = 0; // time to tracking valakas when was last time attacked
	private int i_quest2 = 0; // hate value for 1st player
	private int i_quest3 = 0; // hate value for 2nd player
	private int i_quest4 = 0; // hate value for 3rd player
	private L2Character c_quest2 = null; // 1st most hated target
	private L2Character c_quest3 = null; // 2nd most hated target
	private L2Character c_quest4 = null; // 3rd most hated target
	private int count = 0;

	private static final int VALAKAS = 29028;

	private static final int HEART = 31385;     // Heart of Volcano : Teleport into Lair of Valakas
	private static final int KLEIN = 31540;     // Watcher of Valakas Klein : Teleport into Hall of Flames
	private static final int GK_VOLC = 31384;   // Gatekeeper of Fire Dragon : Opening some doors
	private static final int GK_VOLC1 = 31686;  //  Gatekeeper of Fire Dragon : Opens doors to Heart of Volcano
	private static final int GK_VOLC2 = 31687;  //Gatekeeper of Fire Dragon : Opens doors to Heart of Volcano
	private static final int CUBIC = 31759;     //Teleportation  : Teleport out of Lair of Valakas

	//Valakas Status Tracking :
	private static final int DORMANT = 0;     	//Valakas is spawned and no one has entered yet. Entry is unlocked
	private static final int WAITING = 1;     	//Valakas is spawend and someone has entered, triggering a 30 minute window for additional people to enter
	                							//before he unleashes his attack. Entry is unlocked
	private static final int FIGHTING = 2;    	//Valakas is engaged in battle, annihilating his foes. Entry is locked
	private static final int DEAD = 3;        	//Valakas has been killed. Entry is locked

	private static L2BossZone _Zone;

	// Boss: Valakas
	public Valakas(int id,String name,String descr)
	{
        super(id,name,descr);
        int[] mob = {VALAKAS};
        int[] npcs = {HEART, KLEIN, GK_VOLC1, GK_VOLC2,CUBIC};
        State state = new State("Start", this);
        setInitialState(state);
        for (int npc : npcs)
        {
        	addStartNpc(npc);
            addTalkId(npc);
        }
        this.registerMobs(mob);
        i_ai0 = 0;
        i_ai1 = 0;
        i_ai2 = 0;
        i_ai3 = 0;
        i_ai4 = 0;
        i_quest0 = 0;
        i_quest1 = System.currentTimeMillis();
        _Zone = GrandBossManager.getInstance().getZone(212852,-114842,-1632);
        StatsSet info = GrandBossManager.getInstance().getStatsSet(VALAKAS);
        int status = GrandBossManager.getInstance().getBossStatus(VALAKAS);
        if (status == DEAD)
        {
            // load the unlock date and time for valakas from DB
            long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
            // if valakas is locked until a certain time, mark it so and start the unlock timer
            // the unlock time has not yet expired.  Mark valakas as currently locked.  Setup a timer
            // to fire at the correct time (calculate the time between now and the unlock time,
            // setup a timer to fire after that many msec)
            if (temp > 0)
            {
                this.startQuestTimer("valakas_unlock", temp, null, null);
            }
            else
            {
                // the time has already expired while the server was offline. Immediately spawn valakas in his cave.
                // also, the status needs to be changed to DORMANT
                L2GrandBossInstance valakas = (L2GrandBossInstance) addSpawn(VALAKAS,-105200,-253104,-15264,0,false,0);
                GrandBossManager.getInstance().setBossStatus(VALAKAS,DORMANT);
                GrandBossManager.getInstance().addBoss(valakas);
                final L2NpcInstance _valakas = valakas;
                ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
                {
    				public void run()
    				{
    					try
    		            {
    						_valakas.setIsInvul(true);
    		                _valakas.setRunning();
    		            }
    		            catch (Throwable e)
    		            {
    		            }
    				}
    			},100);
                startQuestTimer("1003", 60000, valakas, null, true);
            }
        }
        else
        {
            int loc_x = info.getInteger("loc_x");
            int loc_y = info.getInteger("loc_y");
            int loc_z = info.getInteger("loc_z");
            int heading = info.getInteger("heading");
            final int hp = info.getInteger("currentHP");
            final int mp = info.getInteger("currentMP");
            L2GrandBossInstance valakas = (L2GrandBossInstance) addSpawn(VALAKAS,loc_x,loc_y,loc_z,heading,false,0);
            GrandBossManager.getInstance().addBoss(valakas);
            final L2NpcInstance _valakas = valakas;
            ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
            {
				public void run()
				{
					try
		            {
			            _valakas.setCurrentHpMp(hp,mp);
						_valakas.setIsInvul(true);
		                _valakas.setRunning();
		            }
		            catch (Throwable e)
		            {
		            }
				}
			},100);

            startQuestTimer("1003", 60000, valakas, null, true);
            if (status == WAITING)
            {
                // Start timer to lock entry after 30 minutes
                startQuestTimer("1001",Config.VALAKAS_WAIT_TIME * 60000, valakas, null);
            }
            else if (status == FIGHTING)
            {
                // Start repeating timer to check for inactivity
                startQuestTimer("valakas_despawn",60000, valakas, null, true);
                valakas.setIsInvul(false);
            }
        }
	}

	public String onAdvEvent (String event, L2NpcInstance npc, L2PcInstance player)
	{
        if (npc != null)
        {
        	long temp = 0;
			if (event.equalsIgnoreCase("1001"))
            {
                npc.teleToLocation(212852,-114842,-1632);
                i_quest1 = System.currentTimeMillis();
                final L2NpcInstance _valakas = npc;
                ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
                {
    				public void run()
    				{
    					try
    		            {
    						broadcastSpawn(_valakas);
    		            }
    		            catch (Throwable e)
    		            {
    		            }
    				}
    			},1);
                startQuestTimer("1004",2000, npc, null);
            }
            else if (event.equalsIgnoreCase("1002"))
            {
            	int lvl = 0;
    			int sk_4691 = 0;
    			L2Effect[] effects = npc.getAllEffects();
    			if (effects.length != 0 || effects != null)
    			{
    				for (L2Effect e : effects)
    				{
    					if (e.getSkill().getId() == 4629)
    					{
    						sk_4691 = 1;
    						lvl = e.getSkill().getLevel();
    						break;
    					}
    				}
    	        }
            	if (GrandBossManager.getInstance().getBossStatus(VALAKAS) == FIGHTING)
            	{
            		temp = (System.currentTimeMillis() - i_quest1);
            		if (temp > Config.VALAKAS_DESPAWN_TIME * 60000)
            		{
            			npc.getAI().setIntention(AI_INTENTION_IDLE);
            			npc.teleToLocation(-105200,-253104,-15264);
            			GrandBossManager.getInstance().setBossStatus(VALAKAS,DORMANT);
            			npc.setCurrentHpMp(npc.getMaxHp(),npc.getMaxMp());
            			_Zone.oustAllPlayers();
            			cancelQuestTimer("1002", npc, null);
            			i_quest2 = 0;
            			i_quest3 = 0;
            			i_quest4 = 0;
            		}
                }
            	else if (npc.getCurrentHp() > ( ( npc.getMaxHp() * 1 ) / 4 ) )
            	{
            		if (sk_4691 == 0 ||(sk_4691 == 1 && lvl != 4))
            		{
            			npc.setTarget(npc);
            			npc.doCast(SkillTable.getInstance().getInfo(4691,4));
            		}
            	}
            	else if (npc.getCurrentHp() > ( ( npc.getMaxHp() * 2 ) / 4 ) )
            	{
            		if (sk_4691 == 0 ||(sk_4691 == 1 && lvl != 3))
            		{
            			npc.setTarget(npc);
            			npc.doCast(SkillTable.getInstance().getInfo(4691,3));
            		}
            	}
            	else if (npc.getCurrentHp() > ( ( npc.getMaxHp() * 3 ) / 4 ) )
            	{
            		if (sk_4691 == 0 ||(sk_4691 == 1 && lvl != 2))
            		{
            			npc.setTarget(npc);
            			npc.doCast(SkillTable.getInstance().getInfo(4691,2));
            		}
            	}
            	else if (sk_4691 == 0 ||(sk_4691 == 1 && lvl != 1))
            	{
            		npc.setTarget(npc);
            		npc.doCast(SkillTable.getInstance().getInfo(4691,1));
            	}
            }
            else if (event.equalsIgnoreCase("1003") && npc != null)
            {
            	if (!npc.isInvul())
            		getRandomSkill(npc);
            	else
            		npc.getAI().setIntention(AI_INTENTION_IDLE);
            }
            else if (event.equalsIgnoreCase("1004"))
            {
                startQuestTimer("1102",1500, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),1800,180,-1,1500,15000));
            }
            else if (event.equalsIgnoreCase("1102"))
            {
                startQuestTimer("1103",3300, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1300,180,-5,3000,15000));
            }
            else if (event.equalsIgnoreCase("1103"))
            {
                startQuestTimer("1104",2900, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 500,180,-8,600,15000));
            }
            else if (event.equalsIgnoreCase("1104"))
            {
                startQuestTimer("1105",2700, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1200,180,-5,300,15000));
            }
            else if (event.equalsIgnoreCase("1105"))
            {
            	startQuestTimer("1106",1, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),2800,250,70,0,15000));
            }
            else if (event.equalsIgnoreCase("1106"))
            {
                startQuestTimer("1107",3200, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),2600,30,60,3400,15000));
            }
            else if (event.equalsIgnoreCase("1107"))
            {
                startQuestTimer("1108",1400, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),700,150,-65,0,15000));
            }
            else if (event.equalsIgnoreCase("1108"))
            {
                startQuestTimer("1109",6700, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),1200,150,-55,2900,15000));
            }
            else if (event.equalsIgnoreCase("1109"))
            {
                startQuestTimer("1110",5700, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),750,170,-10,1700,5700));
            }
            else if (event.equalsIgnoreCase("1110"))
            {
            	GrandBossManager.getInstance().setBossStatus(VALAKAS,FIGHTING);
            	startQuestTimer("1002",60000, npc, null, true);
            	npc.setIsInvul(false);
            	getRandomSkill(npc);
            }
            else if (event.equalsIgnoreCase("1111"))
            {
                startQuestTimer("1112",3500, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),1300,200,-8,3000,10000));
            }
            else if (event.equalsIgnoreCase("1112"))
            {
                startQuestTimer("1113",4500, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),1300,200,-8,3000,10000));
            }
            else if (event.equalsIgnoreCase("1113"))
            {
                startQuestTimer("1114",500, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),1000,190,0,3000,10000));
            }
            else if (event.equalsIgnoreCase("1114"))
            {
                startQuestTimer("1115",4600, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),1700,120,0,2500,10000));
            }
            else if (event.equalsIgnoreCase("1115"))
            {
                startQuestTimer("1116",750, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),1700,20,0,3000,10000));
            }
            else if (event.equalsIgnoreCase("1116"))
            {
                startQuestTimer("1117",2500, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),1700,10,0,3000,10000));
            }
            else if (event.equalsIgnoreCase("1117"))
            {
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),1700,10,0,3000,250));
                addSpawn(31759,212852,-114842,-1632,0,false,900000);
                int radius = 1500;
                for (int i=0; i < 20; i++)
                {
                    int x = (int) (radius*Math.cos(i*.331)); //.331~2pi/19
                    int y = (int) (radius*Math.sin(i*.331));
                    addSpawn(31759,212852+x,-114842+y,-1632,0,false,900000);
                }
                cancelQuestTimer("1002", npc, null);
            }
        }
        else
        {
            if (event.equalsIgnoreCase("valakas_unlock"))
            {
                L2GrandBossInstance valakas = (L2GrandBossInstance) addSpawn(VALAKAS,-105200,-253104,-15264,32768,false,0);
                GrandBossManager.getInstance().addBoss(valakas);
                GrandBossManager.getInstance().setBossStatus(VALAKAS,DORMANT);
            }

        }
        return super.onAdvEvent(event, npc, player);
	}

	public String onAttack(L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.isInvul())
		{
			return null;
		}
		i_quest1 = System.currentTimeMillis();
		if (GrandBossManager.getInstance().getBossStatus(VALAKAS) != FIGHTING)
		{
			attacker.teleToLocation(150037, -57255, -2976);
		}
		if (attacker.getMountType() == 1)
		{
			int sk_4258 = 0;
			L2Effect[] effects = attacker.getAllEffects();
			if (effects != null && effects.length != 0)
			{
				for (L2Effect e : effects)
				{
					if (e.getSkill().getId() == 4258)
					{
						sk_4258 = 1;
					}
				}
			}
			if (sk_4258 == 0)
			{
				npc.setTarget(attacker);
				npc.doCast(SkillTable.getInstance().getInfo(4258, 1));
			}
		}
		if (attacker.getZ() < (npc.getZ() + 200))
		{
			if (i_ai2 == 0)
			{
				i_ai1 = (i_ai1 + damage);
			}
			if (i_quest0 == 0)
			{
				i_ai4 = (i_ai4 + damage);
			}
			if (i_quest0 == 0)
			{
				i_ai3 = (i_ai3 + damage);
			}
			else if (i_ai2 == 0)
			{
				i_ai0 = (i_ai0 + damage);
			}
			if (i_quest0 == 0)
			{
				if ((((i_ai4 / npc.getMaxHp()) * 100)) > 1)
				{
					if (i_ai3 > (i_ai4 - i_ai3))
					{
						i_ai3 = 0;
						i_ai4 = 0;
						npc.setTarget(npc);
						npc.doCast(SkillTable.getInstance().getInfo(4687, 1));
						i_quest0 = 1;
					}
				}
			}
		}
		int i1 = 0;
		L2Skill skill = null; // TODO: attack handler require skill
		if(skill == null)
		{
			if (attacker == c_quest2)
			{
				if (((damage * 1000) + 1000) > i_quest2)
				{
					i_quest2 = ((damage * 1000) + Rnd.get(3000));
				}
			}
			else if (attacker == c_quest3)
			{
				if (((damage * 1000) + 1000) > i_quest3)
				{
					i_quest3 = ((damage * 1000) + Rnd.get(3000));
				}
			}
			else if (attacker == c_quest4)
			{
				if (((damage * 1000) + 1000) > i_quest4)
				{
					i_quest4 = ((damage * 1000) + Rnd.get(3000));
				}
			}
			else if (i_quest2 > i_quest3)
			{
				i1 = 3;
			}
			else if (i_quest2 == i_quest3)
			{
				if (Rnd.get(100) < 50)
				{
					i1 = 2;
				}
				else
				{
					i1 = 3;
				}
			}
			else if (i_quest2 < i_quest3)
			{
				i1 = 2;
			}
			if (i1 == 2)
			{
				if (i_quest2 > i_quest4)
				{
					i1 = 4;
				}
				else if (i_quest2 == i_quest4)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 2;
					}
					else
					{
						i1 = 4;
					}
				}
				else if (i_quest2 < i_quest4)
				{
					i1 = 2;
				}
			}
			else if (i1 == 3)
			{
				if (i_quest3 > i_quest4)
				{
					i1 = 4;
				}
				else if (i_quest3 == i_quest4)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 3;
					}
					else
					{
						i1 = 4;
					}
				}
				else if (i_quest3 < i_quest4)
				{
					i1 = 3;
				}
			}
			if (i1 == 2)
			{
				i_quest2 = (damage * 1000) + Rnd.get(3000);
				c_quest2 = attacker;
			}
			else if (i1 == 3)
			{
				i_quest3 = (damage * 1000) + Rnd.get(3000);
				c_quest3 = attacker;
			}
			else if (i1 == 4)
			{
				i_quest4 = (damage * 1000) + Rnd.get(3000);
				c_quest4 = attacker;
			}
		}
		/*
		else if (npc.getCurrentHp() > ((npc.getMaxHp() * 1) / 4))
		{
			if (attacker == c_quest2)
			{
				if ((((damage / 30) * 1000) + 1000) > i_quest2)
				{
					i_quest2 = (((damage / 30) * 1000) + Rnd.get(3000));
				}
			}
			else if (attacker == c_quest3)
			{
				if ((((damage / 30) * 1000) + 1000) > i_quest3)
				{
					i_quest3 = (((damage / 30) * 1000) + Rnd.get(3000));
				}
			}
			else if (attacker == c_quest4)
			{
				if ((((damage / 30) * 1000) + 1000) > i_quest4)
				{
					i_quest4 = (((damage / 30) * 1000) + Rnd.get(3000));
				}
			}
			else if (i_quest2 > i_quest3)
			{
				i1 = 3;
			}
			else if (i_quest2 == i_quest3)
			{
				if (Rnd.get(100) < 50)
				{
					i1 = 2;
				}
				else
				{
					i1 = 3;
				}
			}
			else if (i_quest2 < i_quest3)
			{
				i1 = 2;
			}
			if (i1 == 2)
			{
				if (i_quest2 > i_quest4)
				{
					i1 = 4;
				}
				else if (i_quest2 == i_quest4)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 2;
					}
					else
					{
						i1 = 4;
					}
				}
				else if (i_quest2 < i_quest4)
				{
					i1 = 2;
				}
			}
			else if (i1 == 3)
			{
				if (i_quest3 > i_quest4)
				{
					i1 = 4;
				}
				else if (i_quest3 == i_quest4)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 3;
					}
					else
					{
						i1 = 4;
					}
				}
				else if (i_quest3 < i_quest4)
				{
					i1 = 3;
				}
			}
			if (i1 == 2)
			{
				i_quest2 = (((damage / 30) * 1000) + Rnd.get(3000));
				c_quest2 = attacker;
			}
			else if (i1 == 3)
			{
				i_quest3 = (((damage / 30) * 1000) + Rnd.get(3000));
				c_quest3 = attacker;
			}
			else if (i1 == 4)
			{
				i_quest4 = (((damage / 30) * 1000) + Rnd.get(3000));
				c_quest4 = attacker;
			}
		}
		else if (npc.getCurrentHp() > ((npc.getMaxHp() * 2) / 4))
		{
			if (attacker == c_quest2)
			{
				if ((((damage / 50) * 1000) + 1000) > i_quest2)
				{
					i_quest2 = (((damage / 50) * 1000) + Rnd.get(3000));
				}
			}
			else if (attacker == c_quest3)
			{
				if ((((damage / 50) * 1000) + 1000) > i_quest3)
				{
					i_quest3 = (((damage / 50) * 1000) + Rnd.get(3000));
				}
			}
			else if (attacker == c_quest4)
			{
				if ((((damage / 50) * 1000) + 1000) > i_quest4)
				{
					i_quest4 = (((damage / 50) * 1000) + Rnd.get(3000));
				}
			}
			else if (i_quest2 > i_quest3)
			{
				i1 = 3;
			}
			else if (i_quest2 == i_quest3)
			{
				if (Rnd.get(100) < 50)
				{
					i1 = 2;
				}
				else
				{
					i1 = 3;
				}
			}
			else if (i_quest2 < i_quest3)
			{
				i1 = 2;
			}
			if (i1 == 2)
			{
				if (i_quest2 > i_quest4)
				{
					i1 = 4;
				}
				else if (i_quest2 == i_quest4)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 2;
					}
					else
					{
						i1 = 4;
					}
				}
				else if (i_quest2 < i_quest4)
				{
					i1 = 2;
				}
			}
			else if (i1 == 3)
			{
				if (i_quest3 > i_quest4)
				{
					i1 = 4;
				}
				else if (i_quest3 == i_quest4)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 3;
					}
					else
					{
						i1 = 4;
					}
				}
				else if (i_quest3 < i_quest4)
				{
					i1 = 3;
				}
			}
			if (i1 == 2)
			{
				i_quest2 = (((damage / 50) * 1000) + Rnd.get(3000));
				c_quest2 = attacker;
			}
			else if (i1 == 3)
			{
				i_quest3 = (((damage / 50) * 1000) + Rnd.get(3000));
				c_quest3 = attacker;
			}
			else if (i1 == 4)
			{
				i_quest4 = (((damage / 50) * 1000) + Rnd.get(3000));
				c_quest4 = attacker;
			}
		}
		else if (npc.getCurrentHp() > ((npc.getMaxHp() * 3) / 4.0))
		{
			if (attacker == c_quest2)
			{
				if ((((damage / 100) * 1000) + 1000) > i_quest2)
				{
					i_quest2 = (((damage / 100) * 1000) + Rnd.get(3000));
				}
			}
			else if (attacker == c_quest3)
			{
				if ((((damage / 100) * 1000) + 1000) > i_quest3)
				{
					i_quest3 = (((damage / 100) * 1000) + Rnd.get(3000));
				}
			}
			else if (attacker == c_quest4)
			{
				if ((((damage / 100) * 1000) + 1000) > i_quest4)
				{
					i_quest4 = (((damage / 100) * 1000) + Rnd.get(3000));
				}
			}
			else if (i_quest2 > i_quest3)
			{
				i1 = 3;
			}
			else if (i_quest2 == i_quest3)
			{
				if (Rnd.get(100) < 50)
				{
					i1 = 2;
				}
				else
				{
					i1 = 3;
				}
			}
			else if (i_quest2 < i_quest3)
			{
				i1 = 2;
			}
			if (i1 == 2)
			{
				if (i_quest2 > i_quest4)
				{
					i1 = 4;
				}
				else if (i_quest2 == i_quest4)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 2;
					}
					else
					{
						i1 = 4;
					}
				}
				else if (i_quest2 < i_quest4)
				{
					i1 = 2;
				}
			}
			else if (i1 == 3)
			{
				if (i_quest3 > i_quest4)
				{
					i1 = 4;
				}
				else if (i_quest3 == i_quest4)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 3;
					}
					else
					{
						i1 = 4;
					}
				}
				else if (i_quest3 < i_quest4)
				{
					i1 = 3;
				}
				if (i1 == 2)
				{
					i_quest2 = (((damage / 100) * 1000) + Rnd.get(3000));
					c_quest2 = attacker;
				}
				else if (i1 == 3)
				{
					i_quest3 = (((damage / 100) * 1000) + Rnd.get(3000));
					c_quest3 = attacker;
				}
				else if (i1 == 4)
				{
					i_quest4 = (((damage / 100) * 1000) + Rnd.get(3000));
					c_quest4 = attacker;
				}
			}
		}
		else if (attacker == c_quest2)
		{
			if ((((damage / 150) * 1000) + 1000) > i_quest2)
			{
				i_quest2 = (((damage / 150) * 1000) + Rnd.get(3000));
			}
		}
		else if (attacker == c_quest3)
		{
			if ((((damage / 150) * 1000) + 1000) > i_quest3)
			{
				i_quest3 = (((damage / 150) * 1000) + Rnd.get(3000));
			}
		}
		else if (attacker == c_quest4)
		{
			if ((((damage / 150) * 1000) + 1000) > i_quest4)
			{
				i_quest4 = (((damage / 150) * 1000) + Rnd.get(3000));
			}
		}
		else if (i_quest2 > i_quest3)
		{
			i1 = 3;
		}
		else if (i_quest2 == i_quest3)
		{
			if (Rnd.get(100) < 50)
			{
				i1 = 2;
			}
			else
			{
				i1 = 3;
			}
		}
		else if (i_quest2 < i_quest3)
		{
			i1 = 2;
		}
		if (i1 == 2)
		{
			if (i_quest2 > i_quest4)
			{
				i1 = 4;
			}
			else if (i_quest2 == i_quest4)
			{
				if (Rnd.get(100) < 50)
				{
					i1 = 2;
				}
				else
				{
					i1 = 4;
				}
			}
			else if (i_quest2 < i_quest4)
			{
				i1 = 2;
			}
		}
		else if (i1 == 3)
		{
			if (i_quest3 > i_quest4)
			{
				i1 = 4;
			}
			else if (i_quest3 == i_quest4)
			{
				if (Rnd.get(100) < 50)
				{
					i1 = 3;
				}
				else
				{
					i1 = 4;
				}
			}
			else if (i_quest3 < i_quest4)
			{
				i1 = 3;
			}
		}
		if (i1 == 2)
		{
			i_quest2 = (((damage / 150) * 1000) + Rnd.get(3000));
			c_quest2 = attacker;
		}
		else if (i1 == 3)
		{
			i_quest3 = (((damage / 150) * 1000) + Rnd.get(3000));
			c_quest3 = attacker;
		}
		else if (i1 == 4)
		{
			i_quest4 = (((damage / 150) * 1000) + Rnd.get(3000));
			c_quest4 = attacker;
		}*/
		getRandomSkill(npc);
		return super.onAttack(npc, attacker, damage, isPet);
	}

    public String onKill (L2NpcInstance npc, L2PcInstance killer, boolean isPet) 
    { 
    	startQuestTimer("1111",500, npc, null);
        npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),1700,2000,130,-1,0));
        npc.broadcastPacket(new PlaySound(1, "B03_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
        GrandBossManager.getInstance().setBossStatus(VALAKAS,DEAD);
        long respawnTime = (long)(Config.VALAKAS_RESP_FIRST + Rnd.get(Config.VALAKAS_RESP_SECOND)) * 3600000;
        this.startQuestTimer("valakas_unlock", respawnTime, null, null);
        // also save the respawn time so that the info is maintained past reboots
        StatsSet info = GrandBossManager.getInstance().getStatsSet(VALAKAS);
        info.set("respawn_time",(System.currentTimeMillis() + respawnTime));
        GrandBossManager.getInstance().setStatsSet(VALAKAS,info);
        return super.onKill(npc,killer,isPet);
    }

	public void getRandomSkill(L2NpcInstance npc)
	{
		if (npc.isInvul())
		{
			return;
		}
		L2Skill skill = null;
		int i0 = 0;
		int i1 = 0;
		int i2 = 0;
		L2Character c2 = null;
		if (c_quest2 == null)
			i_quest2 = 0;
		else if (!Util.checkIfInRange(5000, npc, c_quest2, true) || c_quest2.isDead())
			i_quest2 = 0;
		if (c_quest3 == null)
			i_quest3 = 0;
		else if (!Util.checkIfInRange(5000, npc, c_quest3, true) || c_quest3.isDead())
			i_quest3 = 0;	
		if (c_quest4 == null)
			i_quest4 = 0;
		else if (!Util.checkIfInRange(5000, npc, c_quest4, true) || c_quest4.isDead())
			i_quest4 = 0;
		if (i_quest2 > i_quest3)
		{
			i1 = 2;
			i2 = i_quest2;
			c2 = c_quest2;
		}
		else
		{
			i1 = 3;
			i2 = i_quest3;
			c2 = c_quest3;
		}
		if( i_quest4 > i2 )
		{
			i1 = 4;
			i2 = i_quest4;
			c2 = c_quest4;
		}
		if (i2 == 0)
			c2 = getRandomTarget(npc);
		if( i2 > 0 )
		{
			if( Rnd.get(100) < 70)
			{
				if (i1 == 2)
					i_quest2 = 500;
				else if (i1 == 3)
					i_quest3 = 500;
				else if (i1 == 4)
					i_quest4 = 500;
			}
			if( npc.getCurrentHp() > ( ( npc.getMaxHp() * 1 ) / 4 ) )
			{
				i0 = 0;
				i1 = 0;
				if (Util.checkIfInRange(1423, npc, c2, true))
				{
					i0 = 1;
					i1 = 1;
				}
				if (c2.getZ() < (npc.getZ() + 200))
				{
					if( Rnd.get(100) < 20)
					{
						skill = SkillTable.getInstance().getInfo(4690,1);
					}
					else if( Rnd.get(100) < 15)
					{
						skill = SkillTable.getInstance().getInfo(4689,1);
					}
					else if( Rnd.get(100) < 15 && i0 == 1 && i_quest0 == 1 )
					{
						skill = SkillTable.getInstance().getInfo(4685,1);
						i_quest0 = 0;
					}
					else if( Rnd.get(100) < 10 && i1 == 1 )
					{
						skill = SkillTable.getInstance().getInfo(4688,1);
					}
					else if( Rnd.get(100) < 35)
					{
						skill = SkillTable.getInstance().getInfo(4683,1);
					}
					else
					{
						if( Rnd.get(2) == 0) // TODO: replace me with direction, to check if player standing on left or right side of valakas
							skill = SkillTable.getInstance().getInfo(4681,1); // left hand
						else
							skill = SkillTable.getInstance().getInfo(4682,1); // right hand
					}
				}
				else if( Rnd.get(100) < 20)
				{
					skill = SkillTable.getInstance().getInfo(4690,1);
				}
				else if( Rnd.get(100) < 15)
				{
					skill = SkillTable.getInstance().getInfo(4689,1);
				}
				else
				{
					skill = SkillTable.getInstance().getInfo(4684,1);
				}
			}
			else if( npc.getCurrentHp() > ( ( npc.getMaxHp() * 2 ) / 4 ) )
			{
				i0 = 0;
				i1 = 0;
				if (Util.checkIfInRange(1423, npc, c2, true))
				{
					i0 = 1;
					i1 = 1;
				}
				if (c2.getZ() < (npc.getZ() + 200))
				{
					if( Rnd.get(100) < 5 )
					{
						skill = SkillTable.getInstance().getInfo(4690,1);
					}
					else if( Rnd.get(100) < 10 )
					{
						skill = SkillTable.getInstance().getInfo(4689,1);
					}
					else if( Rnd.get(100) < 10 && i0 == 1 && i_quest0 == 1 )
					{
						skill = SkillTable.getInstance().getInfo(4685,1);
						i_quest0 = 0;
					}
					else if( Rnd.get(100) < 10 && i1 == 1 )
					{
						skill = SkillTable.getInstance().getInfo(4688,1);
					}
					else if( Rnd.get(100) < 20 )
					{
						skill = SkillTable.getInstance().getInfo(4683,1);
					}
					else
					{
						if( Rnd.get(2) == 0) // TODO: replace me with direction, to check if player standing on left or right side of valakas
							skill = SkillTable.getInstance().getInfo(4681,1); // left hand
						else
							skill = SkillTable.getInstance().getInfo(4682,1); // right hand
					}
				}
				else if( Rnd.get(100) < 5)
				{
					skill = SkillTable.getInstance().getInfo(4690,1);
				}
				else if( Rnd.get(100) < 10)
				{
					skill = SkillTable.getInstance().getInfo(4689,1);
				}
				else
				{
					skill = SkillTable.getInstance().getInfo(4684,1);
				}
			}
			else if( npc.getCurrentHp() > ( ( npc.getMaxHp() * 3 ) / 4 ) )
			{
				i0 = 0;
				i1 = 0;
				if (Util.checkIfInRange(1423, npc, c2, true))
				{
					i0 = 1;
					i1 = 1;
				}
				if (c2.getZ() < (npc.getZ() + 200))
				{
					if( Rnd.get(100) < 0 )
					{
						skill = SkillTable.getInstance().getInfo(4690,1);
					}
					else if( Rnd.get(100) < 5 )
					{
						skill = SkillTable.getInstance().getInfo(4689,1);
					}
					else if( Rnd.get(100) < 5 && i0 == 1 && i_quest0 == 1 )
					{
						skill = SkillTable.getInstance().getInfo(4685,1);
						i_quest0 = 0;
					}
					else if( Rnd.get(100) < 10 && i1 == 1 )
					{
						skill = SkillTable.getInstance().getInfo(4688,1);
					}
					else if( Rnd.get(100) < 15 )
					{
						skill = SkillTable.getInstance().getInfo(4683,1);
					}
					else
					{
						if( Rnd.get(2) == 0) // TODO: replace me with direction, to check if player standing on left or right side of valakas
							skill = SkillTable.getInstance().getInfo(4681,1); // left hand
						else
							skill = SkillTable.getInstance().getInfo(4682,1); // right hand
					}
				}
				else if( Rnd.get(100) < 0)
				{
					skill = SkillTable.getInstance().getInfo(4690,1);
				}
				else if( Rnd.get(100) < 5)
				{
					skill = SkillTable.getInstance().getInfo(4689,1);
				}
				else
				{
					skill = SkillTable.getInstance().getInfo(4684,1);
				}
			}
			else
			{
				i0 = 0;
				i1 = 0;
				if (Util.checkIfInRange(1423, npc, c2, true))
				{
					i0 = 1;
					i1 = 1;
				}
				if (c2.getZ() < (npc.getZ() + 200))
				{
					if( Rnd.get(100) < 0 )
					{
						skill = SkillTable.getInstance().getInfo(4690,1);
					}
					else if( Rnd.get(100) < 10 )
					{
						skill = SkillTable.getInstance().getInfo(4689,1);
					}
					else if( Rnd.get(100) < 5 && i0 == 1 && i_quest0 == 1 )
					{
						skill = SkillTable.getInstance().getInfo(4685,1);
						i_quest0 = 0;
					}
					else if( Rnd.get(100) < 10 && i1 == 1 )
					{
						skill = SkillTable.getInstance().getInfo(4688,1);
					}
					else if( Rnd.get(100) < 15 )
					{
						skill = SkillTable.getInstance().getInfo(4683,1);
					}
					else
					{
						if( Rnd.get(2) == 0) // TODO: replace me with direction, to check if player standing on left or right side of valakas
							skill = SkillTable.getInstance().getInfo(4681,1); // left hand
						else
							skill = SkillTable.getInstance().getInfo(4682,1); // right hand
					}
				}
				else if( Rnd.get(100) < 0)
				{
					skill = SkillTable.getInstance().getInfo(4690,1);
				}
				else if( Rnd.get(100) < 10)
				{
					skill = SkillTable.getInstance().getInfo(4689,1);
				}
				else
				{
					skill = SkillTable.getInstance().getInfo(4684,1);
				}
			}
		}
		if (skill != null)
			callSkillAI(npc,c2,skill);
	}

	public void callSkillAI(L2NpcInstance npc, L2Character c2, L2Skill skill)
	{
		QuestTimer timer = getQuestTimer("1003", npc, null);
		if (npc == null)
		{
			if (timer != null)
				timer.cancel();
			return;
		}

		if (npc.isInvul())
			return;

		if (c2 == null || c2.isDead() || c2.isAlikeDead() || timer == null)
		{
			c2 = getRandomTarget(npc); // just in case if hate AI fail
			if (timer == null)
			{
				startQuestTimer("1003", 500, npc, null, true);
				return;
			}
		}
		L2Character target = c2;
		if (target == null || target.isDead() || target.isAlikeDead())
		{
			return;
		}

		if (Util.checkIfInRange(skill.getCastRange(), npc, target, true))
		{
			if (timer != null)
				timer.cancel();
			npc.getAI().setIntention(AI_INTENTION_IDLE);
			npc.setTarget(target);
			npc.doCast(skill);
		}
		else
		{
			npc.getAI().setIntention(AI_INTENTION_FOLLOW, target, null);
		}
	}
	public void broadcastSpawn(L2NpcInstance npc)
	{
		Collection<L2Object> objs = npc.getKnownList().getKnownObjects().values();
		{
			for (L2Object obj : objs)
			{
				if (obj instanceof L2PcInstance)
				{
					if (Util.checkIfInRange(10000, npc, obj, true))
					{
						((L2Character) obj).sendPacket(new PlaySound(1, "B03_A", 1, npc.getObjectId(), 212852,-114842,-1632));
						((L2Character) obj).sendPacket(new SocialAction(npc.getObjectId(),3));
					}
				}
			}
		}
		return;
	}

	public L2Character getRandomTarget(L2NpcInstance npc)
	{
		List<L2Character> result = new ArrayList<>();
		Collection<L2Object> objs = npc.getKnownList().getKnownObjects().values();
		{
			for (L2Object obj : objs)
			{
				if (obj instanceof L2PcInstance)
				{
					if (Util.checkIfInRange(5000, npc, obj, true) && !((L2Character) obj).isDead() && !((L2Character) obj).isAlikeDead())
						result.add((L2PcInstance) obj);
				}
				if (obj instanceof L2Summon)
				{
					if (Util.checkIfInRange(5000, npc, obj, true) && !((L2Character) obj).isDead() && !((L2Character) obj).isAlikeDead())
						result.add((L2Summon) obj);
				}
			}
		}
		if (!result.isEmpty() && result.size() != 0)
		{
			Object[] characters = result.toArray();
			return (L2Character) characters[Rnd.get(characters.length)];
		}
		return null;
	}

    public String onSpellFinished(L2NpcInstance npc, L2PcInstance player, L2Skill skill)
    {
		if (npc.isInvul())
		{
			return null;
		}
		else if (npc.getNpcId() == VALAKAS && !npc.isInvul())
    	{
    		getRandomSkill(npc);
    	}
    	return super.onSpellFinished(npc, player, skill);
    }

    public String onAggroRangeEnter (L2NpcInstance npc, L2PcInstance player, boolean isPet) 
    {
    	int i1 = 0;
		if (GrandBossManager.getInstance().getBossStatus(VALAKAS) == FIGHTING)
		{
			if (npc.getCurrentHp() > ((npc.getMaxHp() * 1) / 4))
			{
				if (player == c_quest2)
				{
					if (((10 * 1000) + 1000) > i_quest2)
					{
						i_quest2 = ((10 * 1000) + Rnd.get(3000));
					}
				}
				else if (player == c_quest3)
				{
					if (((10 * 1000) + 1000) > i_quest3)
					{
						i_quest3 = ((10 * 1000) + Rnd.get(3000));
					}
				}
				else if (player == c_quest4)
				{
					if (((10 * 1000) + 1000) > i_quest4)
					{
						i_quest4 = ((10 * 1000) + Rnd.get(3000));
					}
				}
				else if (i_quest2 > i_quest3)
				{
					i1 = 3;
				}
				else if (i_quest2 == i_quest3)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 2;
					}
					else
					{
						i1 = 3;
					}
				}
				else if (i_quest2 < i_quest3)
				{
					i1 = 2;
				}
				if (i1 == 2)
				{
					if (i_quest2 > i_quest4)
					{
						i1 = 4;
					}
					else if (i_quest2 == i_quest4)
					{
						if (Rnd.get(100) < 50)
						{
							i1 = 2;
						}
						else
						{
							i1 = 4;
						}
					}
					else if (i_quest2 < i_quest4)
					{
						i1 = 2;
					}
				}
				else if (i1 == 3)
				{
					if (i_quest3 > i_quest4)
					{
						i1 = 4;
					}
					else if (i_quest3 == i_quest4)
					{
						if (Rnd.get(100) < 50)
						{
							i1 = 3;
						}
						else
						{
							i1 = 4;
						}
					}
					else if (i_quest3 < i_quest4)
					{
						i1 = 3;
					}
				}
				if (i1 == 2)
				{
					i_quest2 = ((10 * 1000) + Rnd.get(3000));
					c_quest2 = player;
				}
				else if (i1 == 3)
				{
					i_quest3 = ((10 * 1000) + Rnd.get(3000));
					c_quest3 = player;
				}
				else if (i1 == 4)
				{
					i_quest4 = ((10 * 1000) + Rnd.get(3000));
					c_quest4 = player;
				}
			}
			else if (npc.getCurrentHp() > ((npc.getMaxHp() * 2) / 4))
			{
				if (player == c_quest2)
				{
					if (((6 * 1000) + 1000) > i_quest2)
					{
						i_quest2 = ((6 * 1000) + Rnd.get(3000));
					}
				}
				else if (player == c_quest3)
				{
					if (((6 * 1000) + 1000) > i_quest3)
					{
						i_quest3 = ((6 * 1000) + Rnd.get(3000));
					}
				}
				else if (player == c_quest4)
				{
					if (((6 * 1000) + 1000) > i_quest4)
					{
						i_quest4 = ((6 * 1000) + Rnd.get(3000));
					}
				}
				else if (i_quest2 > i_quest3)
				{
					i1 = 3;
				}
				else if (i_quest2 == i_quest3)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 2;
					}
					else
					{
						i1 = 3;
					}
				}
				else if (i_quest2 < i_quest3)
				{
					i1 = 2;
				}
				if (i1 == 2)
				{
					if (i_quest2 > i_quest4)
					{
						i1 = 4;
					}
					else if (i_quest2 == i_quest4)
					{
						if (Rnd.get(100) < 50)
						{
							i1 = 2;
						}
						else
						{
							i1 = 4;
						}
					}
					else if (i_quest2 < i_quest4)
					{
						i1 = 2;
					}
				}
				else if (i1 == 3)
				{
					if (i_quest3 > i_quest4)
					{
						i1 = 4;
					}
					else if (i_quest3 == i_quest4)
					{
						if (Rnd.get(100) < 50)
						{
							i1 = 3;
						}
						else
						{
							i1 = 4;
						}
					}
					else if (i_quest3 < i_quest4)
					{
						i1 = 3;
					}
				}
				if (i1 == 2)
				{
					i_quest2 = ((6 * 1000) + Rnd.get(3000));
					c_quest2 = player;
				}
				else if (i1 == 3)
				{
					i_quest3 = ((6 * 1000) + Rnd.get(3000));
					c_quest3 = player;
				}
				else if (i1 == 4)
				{
					i_quest4 = ((6 * 1000) + Rnd.get(3000));
					c_quest4 = player;
				}
			}
			else if (npc.getCurrentHp() > ((npc.getMaxHp() * 3) / 4.0))
			{
				if (player == c_quest2)
				{
					if (((3 * 1000) + 1000) > i_quest2)
					{
						i_quest2 = ((3 * 1000) + Rnd.get(3000));
					}
				}
				else if (player == c_quest3)
				{
					if (((3 * 1000) + 1000) > i_quest3)
					{
						i_quest3 = ((3 * 1000) + Rnd.get(3000));
					}
				}
				else if (player == c_quest4)
				{
					if (((3 * 1000) + 1000) > i_quest4)
					{
						i_quest4 = ((3 * 1000) + Rnd.get(3000));
					}
				}
				else if (i_quest2 > i_quest3)
				{
					i1 = 3;
				}
				else if (i_quest2 == i_quest3)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 2;
					}
					else
					{
						i1 = 3;
					}
				}
				else if (i_quest2 < i_quest3)
				{
					i1 = 2;
				}
				if (i1 == 2)
				{
					if (i_quest2 > i_quest4)
					{
						i1 = 4;
					}
					else if (i_quest2 == i_quest4)
					{
						if (Rnd.get(100) < 50)
						{
							i1 = 2;
						}
						else
						{
							i1 = 4;
						}
					}
					else if (i_quest2 < i_quest4)
					{
						i1 = 2;
					}
				}
				else if (i1 == 3)
				{
					if (i_quest3 > i_quest4)
					{
						i1 = 4;
					}
					else if (i_quest3 == i_quest4)
					{
						if (Rnd.get(100) < 50)
						{
							i1 = 3;
						}
						else
						{
							i1 = 4;
						}
					}
					else if (i_quest3 < i_quest4)
					{
						i1 = 3;
					}
				}
				if (i1 == 2)
				{
					i_quest2 = ((3 * 1000) + Rnd.get(3000));
					c_quest2 = player;
				}
				else if (i1 == 3)
				{
					i_quest3 = ((3 * 1000) + Rnd.get(3000));
					c_quest3 = player;
				}
				else if (i1 == 4)
				{
					i_quest4 = ((3 * 1000) + Rnd.get(3000));
					c_quest4 = player;
				}
			}
			else if (player == c_quest2)
			{
				if (((2 * 1000) + 1000) > i_quest2)
				{
					i_quest2 = ((2 * 1000) + Rnd.get(3000));
				}
			}
			else if (player == c_quest3)
			{
				if (((2 * 1000) + 1000) > i_quest3)
				{
					i_quest3 = ((2 * 1000) + Rnd.get(3000));
				}
			}
			else if (player == c_quest4)
			{
				if (((2 * 1000) + 1000) > i_quest4)
				{
					i_quest4 = ((2 * 1000) + Rnd.get(3000));
				}
			}
			else if (i_quest2 > i_quest3)
			{
				i1 = 3;
			}
			else if (i_quest2 == i_quest3)
			{
				if (Rnd.get(100) < 50)
				{
					i1 = 2;
				}
				else
				{
					i1 = 3;
				}
			}
			else if (i_quest2 < i_quest3)
			{
				i1 = 2;
			}
			if (i1 == 2)
			{
				if (i_quest2 > i_quest4)
				{
					i1 = 4;
				}
				else if (i_quest2 == i_quest4)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 2;
					}
					else
					{
						i1 = 4;
					}
				}
				else if (i_quest2 < i_quest4)
				{
					i1 = 2;
				}
			}
			else if (i1 == 3)
			{
				if (i_quest3 > i_quest4)
				{
					i1 = 4;
				}
				else if (i_quest3 == i_quest4)
				{
					if (Rnd.get(100) < 50)
					{
						i1 = 3;
					}
					else
					{
						i1 = 4;
					}
				}
				else if (i_quest3 < i_quest4)
				{
					i1 = 3;
				}
			}
			if (i1 == 2)
			{
				i_quest2 = ((2 * 1000) + Rnd.get(3000));
				c_quest2 = player;
			}
			else if (i1 == 3)
			{
				i_quest3 = ((2 * 1000) + Rnd.get(3000));
				c_quest3 = player;
			}
			else if (i1 == 4)
			{
				i_quest4 = ((2 * 1000) + Rnd.get(3000));
				c_quest4 = player;
			}
		}
		else if (player == c_quest2)
		{
			if (((1 * 1000) + 1000) > i_quest2)
			{
				i_quest2 = ((1 * 1000) + Rnd.get(3000));
			}
		}
		else if (player == c_quest3)
		{
			if (((1 * 1000) + 1000) > i_quest3)
			{
				i_quest3 = ((1 * 1000) + Rnd.get(3000));
			}
		}
		else if (player == c_quest4)
		{
			if (((1 * 1000) + 1000) > i_quest4)
			{
				i_quest4 = ((1 * 1000) + Rnd.get(3000));
			}
		}
		else if (i_quest2 > i_quest3)
		{
			i1 = 3;
		}
		else if (i_quest2 == i_quest3)
		{
			if (Rnd.get(100) < 50)
			{
				i1 = 2;
			}
			else
			{
				i1 = 3;
			}
		}
		else if (i_quest2 < i_quest3)
		{
			i1 = 2;
		}
		if (i1 == 2)
		{
			if (i_quest2 > i_quest4)
			{
				i1 = 4;
			}
			else if (i_quest2 == i_quest4)
			{
				if (Rnd.get(100) < 50)
				{
					i1 = 2;
				}
				else
				{
					i1 = 4;
				}
			}
			else if (i_quest2 < i_quest4)
			{
				i1 = 2;
			}
		}
		else if (i1 == 3)
		{
			if (i_quest3 > i_quest4)
			{
				i1 = 4;
			}
			else if (i_quest3 == i_quest4)
			{
				if (Rnd.get(100) < 50)
				{
					i1 = 3;
				}
				else
				{
					i1 = 4;
				}
			}
			else if (i_quest3 < i_quest4)
			{
				i1 = 3;
			}
		}
		if (i1 == 2)
		{
			i_quest2 = ((1 * 1000) + Rnd.get(3000));
			c_quest2 = player;
		}
		else if (i1 == 3)
		{
			i_quest3 = ((1 * 1000) + Rnd.get(3000));
			c_quest3 = player;
		}
		else if (i1 == 4)
		{
			i_quest4 = ((1 * 1000) + Rnd.get(3000));
			c_quest4 = player;
		}
		if (GrandBossManager.getInstance().getBossStatus(VALAKAS) == FIGHTING && !npc.isInvul())
		{
			getRandomSkill(npc);
		}
		else
			return null;
		return super.onAggroRangeEnter(npc, player, isPet);
    }

	public String onSkillUse(L2NpcInstance npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		if (npc.isInvul())
		{
			return null;
		}
		npc.setTarget(caster);
		return super.onSkillUse(npc, caster, skill, targets, isPet);
	}

	 public String onTalk(L2NpcInstance npc,L2PcInstance player)
	 {
		 QuestState qs = player.getQuestState("valakas");
		 int npcId = npc.getNpcId();
		 L2GrandBossInstance valakas = GrandBossManager.getInstance().getBoss(VALAKAS);
		 String htmltext = "";	 
		 if(npcId == KLEIN)
		 {
			 if (qs.getQuestItemsCount(7267) > 0)
			 {
				 qs.takeItems(7267,1);
				 player.teleToLocation(183813,-115157,-3303);
				 qs.set("allowEnter","1");
			 }
			 else htmltext = "<html><body>Watcher of Valakas Klein:<br> In order to enter the Hall of Flames, you'll need a Floating Stone. " +
			 		"To obtain one, you must do a small favor for me. Agreed?</body></html>";
		 }
		 else if(npcId == GK_VOLC)
			 DoorTable.getInstance().getDoor(24210004).openMe();
		 else if(npcId == GK_VOLC1)
	        DoorTable.getInstance().getDoor(24210005).openMe();
		 else if(npcId == GK_VOLC2)
	        DoorTable.getInstance().getDoor(24210006).openMe();
		 else if(npcId == HEART)
		 {
	        int status = GrandBossManager.getInstance().getBossStatus(VALAKAS);
	        if(status == DORMANT || status == WAITING)
	        {
	        	if(count >= 200)
	        		htmltext = "<html><body>[Whispering]<br><font color='LEVEL'>Too many have entered, " +
	        		"I can't let you go in.</font></body></html>";
	                	   
	        	else if(qs.getInt("allowEnter") == 1)
	        	{
	        		qs.unset("allowEnter");
	        		if(_Zone == null)
	        			_Zone = GrandBossManager.getInstance().getZone(212852,-114842,-1632);
	        		if(_Zone == null)
	        			return "<html><body>[Whispering]<br>You may not enter while admin disabled this zone</body></html>";
	        		_Zone.allowPlayerEntry(player,30);
	        		int x = 204328 + Rnd.get(600);
	        		int y = -111874 + Rnd.get(600);
	        		player.teleToLocation(x,y,70);
	        		count++;
	        		if(status == DORMANT)
	        		{
	        			startQuestTimer("1001", Config.VALAKAS_WAIT_TIME * 60000, valakas, null);
	        			GrandBossManager.getInstance().setBossStatus(29028,WAITING);
	        		}
	        	}
	        	else htmltext = "<html><body>[Whispering]<br>You can't enter.</body></html>";
	        }
	        else if(status == FIGHTING)
	        	htmltext = "<html><body>[Whispering]<br>You can't enter now.</body></html>";
	        else htmltext = "<html><body>[Whispering]<br><font color='LEVEL'>You can't confront " +
	        		"Valakas yet. Away with you!</font></body></html>";
		 }
		 else if(npcId == CUBIC)
		 {
	        int x = 150037 + Rnd.get(500);
	        int y = -57720 + Rnd.get(500);
	        player.teleToLocation(x,y,-2976);
		 }
		 return htmltext;
	 }

    public static void main(String[] args)
    {
    	// now call the constructor (starts up the ai)
    	new Valakas(-1,"valakas","ai");
    }

	@Override
	public void run()
	{}
}