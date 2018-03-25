/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.it.br.gameserver.ai.special.individual;

import com.it.br.Config;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.instancemanager.GrandBossManager;
import com.it.br.gameserver.model.L2CharPosition;
import com.it.br.gameserver.model.actor.instance.L2GrandBossInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.State;
import com.it.br.gameserver.model.zone.type.L2BossZone;
import com.it.br.gameserver.network.serverpackets.Earthquake;
import com.it.br.gameserver.network.serverpackets.PlaySound;
import com.it.br.gameserver.network.serverpackets.SpecialCamera;
import com.it.br.gameserver.templates.StatsSet;
import com.it.br.util.Rnd;

/**
 * Antharas AI
 * @author Emperorc
 * @reworked *slayer
 */
public class Antharas extends Quest implements Runnable
{
	private static final int ANTHARAS = 29019;
	private static final int HEART = 13001;

	//Antharas Status Tracking :
	private static final byte DORMANT = 0;     	//Antharas is spawned and no one has entered yet. Entry is unlocked
	private static final byte WAITING = 1;     	//Antharas is spawend and someone has entered, triggering a 30 minute window for additional people to enter
	                							//before he unleashes his attack. Entry is unlocked
	private static final byte FIGHTING = 2;    	//Antharas is engaged in battle, annihilating his foes. Entry is locked
	private static final byte DEAD = 3;        	//Antharas has been killed. Entry is locked

	private static long _LastAction = 0;
	private static L2BossZone _Zone;

	// Boss: Antharas
	public Antharas(int id,String name,String descr)
	{
        super(id,name,descr);
        int[] mob = {ANTHARAS};
        State state = new State("Start", this);
        setInitialState(state);
        this.registerMobs(mob);
        addStartNpc(HEART);
        addTalkId(HEART);
        _Zone = GrandBossManager.getInstance().getZone(179700,113800,-7709);
        StatsSet info = GrandBossManager.getInstance().getStatsSet(ANTHARAS);
        int status = GrandBossManager.getInstance().getBossStatus(ANTHARAS);
        if (status == DEAD)
        {
            // load the unlock date and time for antharas from DB
            long temp = info.getLong("respawn_time") - System.currentTimeMillis();
            // if antharas is locked until a certain time, mark it so and start the unlock timer
            // the unlock time has not yet expired.  Mark Antharas as currently locked.  Setup a timer
            // to fire at the correct time (calculate the time between now and the unlock time,
            // setup a timer to fire after that many msec)
            if (temp > 0)
            {
                startQuestTimer("antharas_unlock", temp, null, null);
            }
            else
            {
                // the time has already expired while the server was offline. Immediately spawn antharas in his cave.
                // also, the status needs to be changed to DORMANT
                L2GrandBossInstance antharas = (L2GrandBossInstance) addSpawn(ANTHARAS,185708,114298,-8221,32768,false,0);
                GrandBossManager.getInstance().setBossStatus(ANTHARAS,DORMANT);
                antharas.broadcastPacket(new Earthquake(185708,114298,-8221,20,10));
                GrandBossManager.getInstance().addBoss(antharas);
            }
        }
        else
        {
            int loc_x = info.getInteger("loc_x");
            int loc_y = info.getInteger("loc_y");
            int loc_z = info.getInteger("loc_z");
            int heading = info.getInteger("heading");
            int hp = info.getInteger("currentHP");
            int mp = info.getInteger("currentMP");
            L2GrandBossInstance antharas = (L2GrandBossInstance) addSpawn(ANTHARAS,loc_x,loc_y,loc_z,heading,false,0);
            GrandBossManager.getInstance().addBoss(antharas);
            antharas.setCurrentHpMp(hp,mp);
            if (status == WAITING)
            {
                // Start timer to lock entry after 30 minutes
                startQuestTimer("waiting",Config.ANTHARAS_WAIT_TIME * 60000, antharas, null);
            }
            else if (status == FIGHTING)
            {
                _LastAction = System.currentTimeMillis();
                // Start repeating timer to check for inactivity
                startQuestTimer("antharas_despawn",Config.ANTHARAS_DESPAWN_TIME * 60000, antharas, null, true);
            }
        }
	}

	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
        if (npc != null)
        {
        	long temp = 0;
			if (event.equalsIgnoreCase("waiting"))
            {
                npc.teleToLocation(185477,114660,-8221);
                npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(181060,114860,-7678,0));
                startQuestTimer("antharas_has_arrived",2000, npc, null, true);
                npc.broadcastPacket(new PlaySound(1, "BS02_A", 1, npc.getObjectId(), 185477, 114660, -8221));
                GrandBossManager.getInstance().setBossStatus(ANTHARAS,FIGHTING);
            }
            else if (event.equalsIgnoreCase("camera_1"))
            {
            	 if (npc.getX() == 181060 && npc.getY() == 114860)
            	 {
            		 startQuestTimer("camera_2",3000, npc, null);
            		 npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),700,13,-19,0,20000));
            	 }
            	 else
            		 startQuestTimer("antharas_has_arrived",2000, npc, null, true);
            }
            else if (event.equalsIgnoreCase("camera_2"))
            {
                startQuestTimer("camera_3",10000, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),700,13,0,6000,20000));
            }
            else if (event.equalsIgnoreCase("camera_3"))
            {
                startQuestTimer("camera_4",200, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),3700,0,-3,0,10000));
            }
            else if (event.equalsIgnoreCase("camera_4"))
            {
                startQuestTimer("camera_5",10800, npc, null);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),1100,0,-3,22000,30000));
            }
            else if (event.equalsIgnoreCase("camera_5"))
            {
                startQuestTimer("antharas_despawn",60000, npc, null, true);
                npc.broadcastPacket(new SpecialCamera(npc.getObjectId(),1100,0,-3,300,7000));
                npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO,new L2CharPosition(179730,115018,-7733,0));
                _LastAction = System.currentTimeMillis();
            }
            else if (event.equalsIgnoreCase("antharas_despawn"))
            {
                temp = System.currentTimeMillis() - _LastAction;
                if (temp > 900000)
                {
                    npc.teleToLocation(185708,114298,-8221);
                    GrandBossManager.getInstance().setBossStatus(ANTHARAS,DORMANT);
                    npc.setCurrentHpMp(npc.getMaxHp(),npc.getMaxMp());
                    _Zone.oustAllPlayers();
                    cancelQuestTimer("antharas_despawn", npc, null);
                }
            }
            else if (event.equalsIgnoreCase("antharas_has_arrived"))
            {
               if (npc.getX() == 181060 && npc.getY() == 114860)
               {
                   startQuestTimer("camera_1",4000, npc, null);
                   npc.getSpawn().setLocx(181060);
                   npc.getSpawn().setLocy(114870);
                   npc.getSpawn().setLocz(-7677);
                   npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                   cancelQuestTimer("antharas_has_arrived", npc, null);
               }
               else
               {
                   npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO,new L2CharPosition(181060,114860,-7678,0));
               }
            }
            else if (event.equalsIgnoreCase("spawn_cubes"))
            {
                addSpawn(31859,177615,114941,-7709,0,false,900000);
                int radius = 1500;
                for (int i=0; i < 20;i++)
                {
                    int x = (int) (radius*Math.cos(i*.331)); //.331~2pi/19
                    int y = (int) (radius*Math.sin(i*.331));
                    addSpawn(31859,177615+x,114941+y,-7709,0,false,900000);
                }
                cancelQuestTimer("antharas_despawn", npc, null);
                startQuestTimer("remove_players",900000, null, null);
            }
        }
        else
        {
            if (event.equalsIgnoreCase("antharas_unlock"))
            {
                L2GrandBossInstance antharas = (L2GrandBossInstance) addSpawn(ANTHARAS,185708,114298,-8221,32768,false,0);
                GrandBossManager.getInstance().addBoss(antharas);
                GrandBossManager.getInstance().setBossStatus(ANTHARAS,DORMANT);
                antharas.broadcastPacket(new Earthquake(185708,114298,-8221,20,10));
            }
            else if (event.equalsIgnoreCase("remove_players"))
            {
                _Zone.oustAllPlayers();
            }
        }
        return super.onAdvEvent(event, npc, player);
	}

	public String onAttack(L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
        _LastAction = System.currentTimeMillis();
        if (GrandBossManager.getInstance().getBossStatus(ANTHARAS) != FIGHTING)
        {
            _Zone.oustAllPlayers();
        }
        return super.onAttack(npc, attacker, damage, isPet);
	}

    public String onKill(L2NpcInstance npc, L2PcInstance killer, boolean isPet)
    {
        npc.broadcastPacket(new PlaySound(1, "BS01_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
        startQuestTimer("spawn_cubes", 10000, npc, null);
        GrandBossManager.getInstance().setBossStatus(ANTHARAS,DEAD);
		long respawnTime = (Config.ANTHARAS_RESP_FIRST + Rnd.get(Config.ANTHARAS_RESP_SECOND)) * 3600000;
        startQuestTimer("antharas_unlock", respawnTime, null, null);
        // also save the respawn time so that the info is maintained past reboots
        StatsSet info = GrandBossManager.getInstance().getStatsSet(ANTHARAS);
        info.set("respawn_time",(System.currentTimeMillis() + respawnTime));
        GrandBossManager.getInstance().setStatsSet(ANTHARAS,info);
        return super.onKill(npc,killer,isPet);
    }

    public String onTalk(L2NpcInstance npc,L2PcInstance player)
    {
        int npcId = npc.getNpcId();
        L2GrandBossInstance antharas = GrandBossManager.getInstance().getBoss(ANTHARAS);
        String htmltext = "";
        if (_Zone == null)
            _Zone = GrandBossManager.getInstance().getZone(179700,113800,-7709);
        if (_Zone == null)
        	return "<html><body>Heart of Warding:<br>You may not enter while admin disabled this zone</body></html>";

        if (npcId == HEART)
        {
            if (GrandBossManager.getInstance().getBossStatus(ANTHARAS) == DORMANT)
            {
                if (player.getQuestState("antharas").getQuestItemsCount(3865) > 0) // Portal Stone
                {
                    player.getQuestState("antharas").takeItems(3865,1);
                    // allow entry for the player for the next 30 secs (more than enough time for the TP to happen)
                    // Note: this just means 30secs to get in, no limits on how long it takes before we get out.
                    startQuestTimer("waiting",Config.ANTHARAS_WAIT_TIME * 60000, antharas, null);
                    _Zone.allowPlayerEntry(player,30);
                    player.teleToLocation(177879,114826,-7708);
                    GrandBossManager.getInstance().setBossStatus(ANTHARAS,WAITING);
                }
                else
                    htmltext = "<html><body>Heart of Warding:<br>You may not confront Antharas now. Step back.</body></html>";
            }
            else if (GrandBossManager.getInstance().getBossStatus(ANTHARAS) == WAITING)
            {
                if (player.getQuestState("antharas").getQuestItemsCount(3865) > 0) // Portal Stone
                {
                    player.getQuestState("antharas").takeItems(3865,1);
                    // allow entry for the player for the next 30 secs (more than enough time for the TP to happen)
                    // Note: this just means 30secs to get in, no limits on how long it takes before we get out.
                    _Zone.allowPlayerEntry(player,30);
                    player.teleToLocation(177879,114826,-7708);
                }
                else
                    htmltext = "<html><body>Heart of Warding:<br>You may not confront Antharas now. Step back.</body></html>";
            }
            else
                htmltext = "<html><body>Heart of Warding:<br>You may not confront Antharas now. Step back.</body></html>";          
        }
        return htmltext;
    }

    public static void main(String[] args)
    {
    	// now call the constructor (starts up the ai)
    	new Antharas(-1,"antharas","ai");
    }

	@Override
	public void run()
	{}
}