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
 * [URL]http://www.gnu.org/copyleft/gpl.html[/URL]
 */
package com.it.br.gameserver.handler.admincommandhandlers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import com.it.br.Config;
import com.it.br.gameserver.datatables.sql.SpawnTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.L2Event;
import com.it.br.gameserver.network.serverpackets.CharInfo;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.PlaySound;
import com.it.br.gameserver.network.serverpackets.Revive;
import com.it.br.gameserver.network.serverpackets.SocialAction;
import com.it.br.gameserver.network.serverpackets.UserInfo;
/**
 * This class handles following admin commands:
 * - admin = shows menu
 *
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminEventEngine implements IAdminCommandHandler {

 private static final String[] ADMIN_COMMANDS = {"admin_event","admin_event_new","admin_event_choose",
                                           "admin_event_store","admin_event_set","admin_event_change_teams_number",
                                           "admin_event_announce","admin_event_panel","admin_event_control_begin",
                                           "admin_event_control_teleport",
                                           "admin_add",
                                           "admin_event_see",
                                           "admin_event_del",
                                           "admin_delete_buffer",
                                           "admin_event_control_sit",
                                           "admin_event_name",
                                           "admin_event_control_kill",
                                           "admin_event_control_res",
                                           "admin_event_control_poly",
                                           "admin_event_control_unpoly",
                                           "admin_event_control_prize",
                                           "admin_event_control_chatban",
                                           "admin_event_control_finish"};
 private static final int REQUIRED_LEVEL = Config.GM_MENU;
 private static String tempBuffer = "";
 private static String tempName = "";
 private static String tempName2 = "";
 private static boolean npcsDeleted = false;

	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
        if (!Config.ALT_PRIVILEGES_ADMIN)
        	if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM()))
        		return false;

		if (command.equals("admin_event")) showMainPage(activeChar);

        else if (command.equals("admin_event_new"))
		{
            showNewEventPage(activeChar);
		}
        else if (command.startsWith("admin_add"))
        {
            tempBuffer+=command.substring(10);
            showNewEventPage(activeChar);

        }
         else if (command.startsWith("admin_event_see"))
        {
            String eventName = command.substring(16);
            try {
                NpcHtmlMessage adminReply = new NpcHtmlMessage(5);


                DataInputStream in =
                    new DataInputStream(
                      new BufferedInputStream(
                        new FileInputStream ("data/events/" + eventName)));
                  BufferedReader inbr =
                    new BufferedReader(new InputStreamReader(in));


                StringBuilder replyMSG = new StringBuilder("<html><body>");
                replyMSG.append("<center><font color=\"LEVEL\">" + eventName + "</font><font color=\"FF0000\"> bY " + inbr.readLine() + "</font></center><br>");

                replyMSG.append("<br>" + inbr.readLine());
                replyMSG.append("</body></html>");
                adminReply.setHtml(replyMSG.toString());
                activeChar.sendPacket(adminReply);
                }
                catch (Exception e) {System.out.println(e);}

        }
        else if (command.startsWith("admin_event_del"))
        {
            String eventName = command.substring(16);
            File file = new File("data/events/" + eventName);
            file.delete();
            showMainPage(activeChar);

        }

        else if (command.startsWith("admin_event_name"))
        {
            tempName+=command.substring(17);
            showNewEventPage(activeChar);

        }

        else if (command.equalsIgnoreCase("admin_delete_buffer"))
        {
            try {tempBuffer+=tempBuffer.substring(0,tempBuffer.length()-10);
            showNewEventPage(activeChar); }
            catch (Exception e) {tempBuffer="";}
        }

        else if (command.startsWith("admin_event_store"))
        {

            try{
                FileOutputStream file = new FileOutputStream("data/events/" + tempName);
                PrintStream p = new PrintStream(file);
                p.println(activeChar.getName());
                p.println(tempBuffer);
                file.close();
            }
            catch (Exception e) {System.out.println(e);}
            tempBuffer = "";
            tempName = "";
            showMainPage(activeChar);
        }
        else if (command.startsWith("admin_event_set"))
        {
            L2Event.eventName = command.substring(16);
            showEventParameters(activeChar, 2);

        }
        else if (command.startsWith("admin_event_change_teams_number"))
        {
            showEventParameters(activeChar, Integer.parseInt(command.substring(32)));
                }
        else if (command.startsWith("admin_event_panel"))
        {
             showEventControl(activeChar);
         }
        else if (command.startsWith("admin_event_control_begin"))
        {


            try {

            L2Event.active = true;
            L2Event.players.clear();
            L2Event.connectionLossData.clear();

            for(int j=0;j<L2Event.teamsNumber;j++){
                LinkedList<String> link = new LinkedList<String>();
                L2Event.players.put(j+1,link);

            }
            int i = 0;



            while(L2Event.participatingPlayers.size()>0){
                String target = getMaxLeveledPlayer();

                if(!target.equals("")) {

                    L2Event.players.get(i+1).add(target);
                    i=(i+1)%L2Event.teamsNumber;
                }



            }

            destroyEventNpcs();
            npcsDeleted = true;

            } catch(Exception e) {System.out.println(e);}
            showEventControl(activeChar);
        }
        else if (command.startsWith("admin_event_control_teleport"))
        {
            StringTokenizer st = new StringTokenizer(command.substring(29),"-");

            while(st.hasMoreElements()){
                teleportTeam(activeChar, Integer.parseInt(st.nextToken()));
            }
            showEventControl(activeChar);
        }


        else if (command.startsWith("admin_event_control_sit"))
        {
            StringTokenizer st = new StringTokenizer(command.substring(24),"-");

            while(st.hasMoreElements()){
                sitTeam(Integer.parseInt(st.nextToken()));
            }
            showEventControl(activeChar);
        }
        else if (command.startsWith("admin_event_control_kill"))
        {
            StringTokenizer st = new StringTokenizer(command.substring(25),"-");

            while(st.hasMoreElements()){
                killTeam(activeChar, Integer.parseInt(st.nextToken()));
            }
            showEventControl(activeChar);
        }
        else if (command.startsWith("admin_event_control_res"))
        {
            StringTokenizer st = new StringTokenizer(command.substring(24),"-");

            while(st.hasMoreElements()){
                resTeam(Integer.parseInt(st.nextToken()));
            }
            showEventControl(activeChar);
        }
          else if (command.startsWith("admin_event_control_poly"))
        {
            StringTokenizer st0 = new StringTokenizer(command.substring(25));
            StringTokenizer st = new StringTokenizer(st0.nextToken(),"-");
            String id = st0.nextToken();
            while(st.hasMoreElements()){
                polyTeam(Integer.parseInt(st.nextToken()), id);
            }
            showEventControl(activeChar);
        }
        else if (command.startsWith("admin_event_control_unpoly"))
        {
            StringTokenizer st = new StringTokenizer(command.substring(27),"-");

            while(st.hasMoreElements()){
                unpolyTeam(Integer.parseInt(st.nextToken()));
            }
            showEventControl(activeChar);
        }
        else if (command.startsWith("admin_event_control_prize"))
        {
            StringTokenizer st0 = new StringTokenizer(command.substring(26));
            StringTokenizer st = new StringTokenizer(st0.nextToken(),"-");
            String n = st0.nextToken();
            StringTokenizer st1 = new StringTokenizer(n,"*");
            n = st1.nextToken();
            String type = "";
            if(st1.hasMoreElements()) type = st1.nextToken();

            String id = st0.nextToken();
            while(st.hasMoreElements()){
                regardTeam(activeChar, Integer.parseInt(st.nextToken()),Integer.parseInt(n), Integer.parseInt(id), type);
            }
            showEventControl(activeChar);
        }
        else if (command.startsWith("admin_event_control_finish"))
        {
            for(int i = 0; i<L2Event.teamsNumber; i++){
                telePlayersBack(i+1);
            }

            L2Event.eventName = "";
            L2Event.teamsNumber = 0;
            L2Event.names.clear();
            L2Event.participatingPlayers.clear();
            L2Event.players.clear();
            L2Event.id = 12760;
            L2Event.npcs.clear();
            L2Event.active = false;
            npcsDeleted = false;


        }


        else if (command.startsWith("admin_event_announce"))
        {
            StringTokenizer st = new StringTokenizer(command.substring(21));
            L2Event.id = Integer.parseInt(st.nextToken());
            L2Event.teamsNumber = Integer.parseInt(st.nextToken());
            String temp = " ";
            String temp2 = "";
            while(st.hasMoreElements()){
                temp+=st.nextToken() + " ";
            }

            st = new StringTokenizer(temp,"-");

            Integer i = 1;

            while(st.hasMoreElements()){
                temp2 = st.nextToken();
                if(!temp2.equals(" ")){
                 L2Event.names.put (i, temp2.substring(1,temp2.length()-1));
                 i++;}
            }


            L2Event.participatingPlayers.clear();

            muestraNpcConInfoAPlayers(activeChar, L2Event.id);

            PlaySound _snd = new PlaySound(1,"B03_F",0,0,0,0,0);
            activeChar.sendPacket(_snd);
            activeChar.broadcastPacket(_snd);

            NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

            StringBuilder replyMSG = new StringBuilder("<html><body>");

            replyMSG.append("<center><font color=\"LEVEL\">[ L2J EVENT ENGINE</font></center><br>");
            replyMSG.append("<center>The event <font color=\"LEVEL\">"+ L2Event.eventName + "</font> has been announced, now you can type //event_panel to see the event panel control</center><br>");
            replyMSG.append("</body></html>");
            adminReply.setHtml(replyMSG.toString());
            activeChar.sendPacket(adminReply);
      }


		return true;
	}

	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	private boolean checkLevel(int level)
	{
		return (level >= REQUIRED_LEVEL);
	}


	String showStoredEvents(){
        File dir = new File("data/events");
        String[] files = dir.list();
        String result = "";
        if(files == null)
        {
        	result = "No 'data/events' directory!";
        	return result;
        }
        for (int i = 0; i < files.length; i++) {

            File file = new File("data/events/" + files[i]);
            result+="<font color=\"LEVEL\">" + file.getName() + " </font><br><button value=\"select\" action=\"bypass -h admin_event_set " + file.getName() + "\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><button value=\"ver\" action=\"bypass -h admin_event_see " + file.getName() + "\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><button value=\"delete\" action=\"bypass -h admin_event_del " + file.getName() + "\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"><br><br>";
        }
        return result;
   }


    public void showMainPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuilder replyMSG = new StringBuilder("<html><body>");

		replyMSG.append("<center><font color=\"LEVEL\">[ L2J EVENT ENGINE ]</font></center><br>");
		replyMSG.append("<br><center><button value=\"Create NEW event \" action=\"bypass -h admin_event_new\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		replyMSG.append("<center><br>Stored Events<br></center>");
		replyMSG.append(showStoredEvents());
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
	}
    public void showNewEventPage(L2PcInstance activeChar)
    {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

        StringBuilder replyMSG = new StringBuilder("<html><body>");

        replyMSG.append("<center><font color=\"LEVEL\">[ L2J EVENT ENGINE ]</font></center><br>");
        replyMSG.append("<br><center>Event's Title <br><font color=\"LEVEL\">");
        if(tempName.equals(""))
            replyMSG.append("Use //event_name text to insert a new title");
        else replyMSG.append(tempName);
        replyMSG.append("</font></center><br><br>Event's description<br>");
       if(tempBuffer.equals(""))
            replyMSG.append("Use //add text o //delete_buffer to modify this text field");
        else replyMSG.append(tempBuffer);

       if(!(tempName.equals("")&&tempBuffer.equals(""))) replyMSG.append("<br><button value=\"Crear\" action=\"bypass -h admin_event_store\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");


        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }
    public void showEventParameters(L2PcInstance activeChar, int teamnumbers)
    {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

        StringBuilder replyMSG = new StringBuilder("<html><body>");

        replyMSG.append("<center><font color=\"LEVEL\">[ L2J EVENT ENGINE ]</font></center><br>");
        replyMSG.append("<center><font color=\"LEVEL\">" + L2Event.eventName + "</font></center><br>");
        replyMSG.append("<br><center><button value=\"Change number of teams to\" action=\"bypass -h admin_event_change_teams_number $event_teams_number\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> <edit var=\"event_teams_number\" width=100 height=20><br><br>");
        replyMSG.append("<font color=\"LEVEL\">Team's Names</font><br>");
        for (int i = 0; i<teamnumbers; i++){
            replyMSG.append((i+1) + ".- <edit var=\"event_teams_name" + (i+1) + "\" width=100 height=20><br>");
        }
        replyMSG.append("<br><br>Announcer NPC id<edit var=\"event_npcid\" width=100 height=20><br><br><button value=\"Announce Event!!\" action=\"bypass -h admin_event_announce $event_npcid " + teamnumbers + " ");
        for (int i = 0; i<teamnumbers; i++){
            replyMSG.append("$event_teams_name" + (i+1) + " - ");
        }
        replyMSG.append("\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    void muestraNpcConInfoAPlayers(L2PcInstance activeChar, int id){
        L2Event.npcs.clear();
        LinkedList <L2PcInstance> temp = new LinkedList<L2PcInstance>();
        temp.clear();
        for (L2PcInstance player : L2World.getInstance().getAllPlayers())
        {

            if(!temp.contains(player)) {

                L2Event.spawn(player, id);

            temp.add(player);}
            for (L2PcInstance playertemp : player.getKnownList().getKnownPlayers().values()){
              if( (Math.abs(playertemp.getX()-player.getX()) < 500) && (Math.abs(playertemp.getY()-player.getY()) < 500) &&  (Math.abs(playertemp.getZ()-player.getZ()) < 500) ) temp.add(playertemp);
            }

        }
        L2Event.announceAllPlayers(activeChar.getName() + " wants to make an event !!! (you'll find a npc with the details around)");
    }
   void showEventControl(L2PcInstance activeChar){

       NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

       StringBuilder replyMSG = new StringBuilder("<html><body>");

       replyMSG.append("<center><font color=\"LEVEL\">[ L2J EVENT ENGINE ]</font></center><br><font color=\"LEVEL\">" + L2Event.eventName + "</font><br><br><table width=200>");
       replyMSG.append("<tr><td>Apply this command to teams number </td><td><edit var=\"team_number\" width=100 height=15></td></tr>");
       replyMSG.append("<tr><td>&nbsp;</td></tr>");
       if(!npcsDeleted) replyMSG.append("<tr><td><button value=\"Start\" action=\"bypass -h admin_event_control_begin\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td><font color=\"LEVEL\">Destroys all event npcs so no more people can't participate now on</font></td></tr>");
       replyMSG.append("<tr><td>&nbsp;</td></tr>");
       replyMSG.append("<tr><td><button value=\"Teleport\" action=\"bypass -h admin_event_control_teleport $team_number\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td><font color=\"LEVEL\">Teleports the specified team to your position</font></td></tr>");
       replyMSG.append("<tr><td>&nbsp;</td></tr>");
       replyMSG.append("<tr><td><button value=\"Sit\" action=\"bypass -h admin_event_control_sit $team_number\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td><font color=\"LEVEL\">Sits/Stands up the team</font></td></tr>");
       replyMSG.append("<tr><td>&nbsp;</td></tr>");
       replyMSG.append("<tr><td><button value=\"Kill\" action=\"bypass -h admin_event_control_kill $team_number\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td><font color=\"LEVEL\">Finish with the life of all the players in the selected team</font></td></tr>");
       replyMSG.append("<tr><td>&nbsp;</td></tr>");
       replyMSG.append("<tr><td><button value=\"Resurrect\" action=\"bypass -h admin_event_control_res $team_number\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td><font color=\"LEVEL\">Resurrect Team's members</font></td></tr>");
       replyMSG.append("<tr><td>&nbsp;</td></tr>");
       replyMSG.append("<tr><td><button value=\"Polymorph\" action=\"bypass -h admin_event_control_poly $team_number $poly_id\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td><edit var=\"poly_id\" width=100 height=15><font color=\"LEVEL\">Polymorphs the team into the NPC with the id specified</font></td></tr>");
       replyMSG.append("<tr><td>&nbsp;</td></tr>");
       replyMSG.append("<tr><td><button value=\"UnPolymorph\" action=\"bypass -h admin_event_control_unpoly $team_number\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td><font color=\"LEVEL\">Unpolymorph the team</font></td></tr>");replyMSG.append("<tr><td>&nbsp;</td></tr>");
       replyMSG.append("<tr><td>&nbsp;</td></tr>");
       replyMSG.append("<tr><td><button value=\"Give Item\" action=\"bypass -h admin_event_control_prize $team_number $n $id\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> number <edit var=\"n\" width=100 height=15> item id <edit var=\"id\" width=100 height=15></td><td><font color=\"LEVEL\">Give the specified item id to every single member of the team, you can put 5*level, 5*kills or 5 in the number field for example</font></td></tr>");
       replyMSG.append("<tr><td>&nbsp;</td></tr>");
       replyMSG.append("<tr><td><button value=\"End\" action=\"bypass -h admin_event_control_finish\" width=90 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td><font color=\"LEVEL\">Will finish the event teleporting back all the players</font></td></tr>");
       replyMSG.append("</table></body></html>");

       adminReply.setHtml(replyMSG.toString());
       activeChar.sendPacket(adminReply);

   }

   @SuppressWarnings("rawtypes")
String getMaxLeveledPlayer(){
       Iterator it = L2Event.participatingPlayers.iterator();
       L2PcInstance pc = null;
       int max = 0;
       String name = "";
       while(it.hasNext()){
           try{tempName2 =it.next().toString();
           pc = L2World.getInstance().getPlayer(tempName2);
           if( max < pc.getLevel() ) {
               max = pc.getLevel();
               name = pc.getName();
           }}catch(Exception e){try{L2Event.participatingPlayers.remove(tempName2);} catch(Exception e2){}}
       }
       L2Event.participatingPlayers.remove(name);
       return name;

   }

   void destroyEventNpcs(){
       L2NpcInstance npc;
       while(L2Event.npcs.size()>0){
           try{
               npc = (L2NpcInstance) L2World.getInstance().findObject(Integer.parseInt(L2Event.npcs.getFirst()));


           L2Spawn spawn = npc.getSpawn();

           if (spawn != null)
           {
               spawn.stopRespawn();
               SpawnTable.getInstance().deleteSpawn(spawn, true);
           }
           npc.deleteMe();
           L2Event.npcs.removeFirst();}catch(Exception e) {L2Event.npcs.removeFirst();}
       }
   }
   @SuppressWarnings("rawtypes")
void teleportTeam(L2PcInstance activeChar, int team){
       LinkedList linked = L2Event.players.get(team);
       Iterator it = linked.iterator();
       while(it.hasNext()){
           try{L2PcInstance pc = L2World.getInstance().getPlayer(it.next().toString());
           pc.setTitle(L2Event.names.get(team));
           pc.teleToLocation(activeChar.getX(), activeChar.getY(), activeChar.getZ(), true);}catch(Exception e){}
       }

   }
   @SuppressWarnings("rawtypes")
void sitTeam(int team){
       LinkedList linked = L2Event.players.get(team);
       Iterator it = linked.iterator();
       while(it.hasNext()){
           try{L2PcInstance pc = L2World.getInstance().getPlayer(it.next().toString());
           pc.eventSitForced = !pc.eventSitForced;
           if(pc.eventSitForced) pc.sitDown();
           else pc.standUp();}catch(Exception e){}
       }

   }
   @SuppressWarnings("rawtypes")
void killTeam(L2PcInstance activeChar, int team){
       LinkedList linked = L2Event.players.get(team);
       Iterator it = linked.iterator();
       while(it.hasNext()){
           try{L2PcInstance target = L2World.getInstance().getPlayer(it.next().toString());
           target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, activeChar);}catch(Exception e){}
       }

   }
   @SuppressWarnings("rawtypes")
void resTeam(int team){
       LinkedList linked = L2Event.players.get(team);
       Iterator it = linked.iterator();
       while(it.hasNext()){
           try{L2PcInstance character = L2World.getInstance().getPlayer(it.next().toString());
           character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp());
           character.setCurrentCp(character.getMaxCp());
           Revive revive = new Revive(character);
           SocialAction sa = new SocialAction(character.getObjectId(), 15);
           character.broadcastPacket(sa);
           character.sendPacket(sa);
           character.sendPacket(revive);
           character.broadcastPacket(revive);}catch(Exception e){}
       }

   }

   @SuppressWarnings("rawtypes")
void polyTeam(int team, String id){
       LinkedList linked = L2Event.players.get(team);
       Iterator it = linked.iterator();
       while(it.hasNext()){
        try{   L2PcInstance target = L2World.getInstance().getPlayer(it.next().toString());
        target.getPoly().setPolyInfo("npc", id);
        target.teleToLocation(target.getX(), target.getY(), target.getZ(), true);
        CharInfo info1 = new CharInfo(target);
        target.broadcastPacket(info1);
            UserInfo info2 = new UserInfo(target);
            target.sendPacket(info2);}catch(Exception e){}
       }

   }
   @SuppressWarnings("rawtypes")
void unpolyTeam(int team){
       LinkedList linked = L2Event.players.get(team);
       Iterator it = linked.iterator();
       while(it.hasNext()){
          try{ L2PcInstance target = L2World.getInstance().getPlayer(it.next().toString());

           target.getPoly().setPolyInfo(null, "1");
        target.decayMe();
        target.spawnMe(target.getX(),target.getY(),target.getZ());
        CharInfo info1 = new CharInfo(target);
        target.broadcastPacket(info1);
            UserInfo info2 = new UserInfo(target);
            target.sendPacket(info2);}catch(Exception e){}
       }

   }
   private void createItem(L2PcInstance activeChar, L2PcInstance player, int id, int num)
    {
	   player.getInventory().addItem("Event", id, num, player, activeChar);
        ItemList il = new ItemList(player, true);
        player.sendPacket(il);

        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        StringBuilder replyMSG = new StringBuilder("<html><body>");

        replyMSG.append("CONGRATULATIONS, you should have a present in your inventory");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        player.sendPacket(adminReply);
    }

   @SuppressWarnings("rawtypes")
void regardTeam(L2PcInstance activeChar, int team, int n, int id, String type){
       LinkedList linked = L2Event.players.get(team);
       int temp = n;
       Iterator it = linked.iterator();
       while(it.hasNext()){
          try{ L2PcInstance target = L2World.getInstance().getPlayer(it.next().toString());
           if(type.equalsIgnoreCase("level")) temp = n * target.getLevel();
           else if(type.equalsIgnoreCase("kills")) temp = n * target.kills.size();
           else temp = n;
           createItem(activeChar, target, id, temp);}catch(Exception e){}
       }

   }
   @SuppressWarnings("rawtypes")
void telePlayersBack(int team){
       resTeam(team);
       unpolyTeam(team);
       LinkedList linked = L2Event.players.get(team);
       Iterator it = linked.iterator();
       while(it.hasNext()){
         try{ L2PcInstance target = L2World.getInstance().getPlayer(it.next().toString());
          target.setTitle(target.eventTitle);
          target.setKarma(target.eventkarma);
          target.setPvpKills(target.eventpvpkills);
          target.setPkKills(target.eventpkkills);
          target.teleToLocation(target.eventX, target.eventY, target.eventZ, true);
          target.kills.clear();
          target.eventSitForced = false;
          target.atEvent = false;}catch(Exception e){}
       }
   }

}
