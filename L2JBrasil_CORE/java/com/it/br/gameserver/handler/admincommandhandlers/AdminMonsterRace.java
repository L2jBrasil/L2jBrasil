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
package com.it.br.gameserver.handler.admincommandhandlers;

import com.it.br.Config;
import com.it.br.gameserver.MonsterRace;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.DeleteObject;
import com.it.br.gameserver.network.serverpackets.MonRaceInfo;
import com.it.br.gameserver.network.serverpackets.PlaySound;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class handles following admin commands: - invul = turns invulnerability
 * on/off
 *
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminMonsterRace implements IAdminCommandHandler
{
    protected static int state = -1;
    private static Map<String, Integer> admin = new HashMap<>();

    private boolean checkPermission(String command, L2PcInstance activeChar)
    {
        if (!Config.ALT_PRIVILEGES_ADMIN)
            if (!(checkLevel(command, activeChar.getAccessLevel()) && activeChar.isGM()))
            {
                activeChar.sendMessage("E necessario ter Access Level " + admin.get(command) + " para usar o comando : " + command);
                return true;
            }
        return false;
    }

    private boolean checkLevel(String command, int level)
    {
        Integer requiredAcess = admin.get(command);
        return (level >= requiredAcess);
    }

    public AdminMonsterRace()
    {
        admin.put("admin_mons", Config.admin_mons);
    }

    public Set<String> getAdminCommandList()
    {
        return admin.keySet();
    }

    public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        StringTokenizer st = new StringTokenizer(command);
        String commandName = st.nextToken();

        if(checkPermission(commandName, activeChar)) return false;

        if (command.equalsIgnoreCase("admin_mons"))
        {
            handleSendPacket(activeChar);
        }
        return true;
    }

    private void handleSendPacket(L2PcInstance activeChar)
    {
        /*
         * -1 0 to initialize the race
         * 0 15322 to start race
         * 13765 -1 in middle of race
         * -1 0 to end the race
         *
         * 8003 to 8027
         */

        int[][] codes = { {-1, 0}, {0, 15322}, {13765, -1}, {-1, 0}};
        MonsterRace race = MonsterRace.getInstance();

        if (state == -1)
        {
            state++;
            race.newRace();
            race.newSpeeds();
            MonRaceInfo spk = new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(),
                                              race.getSpeeds());
            activeChar.sendPacket(spk);
            activeChar.broadcastPacket(spk);
        }
        else if (state == 0)
        {
            state++;
            SystemMessage sm = new SystemMessage(SystemMessageId.MONSRACE_RACE_START);
            sm.addNumber(0);
            activeChar.sendPacket(sm);
            PlaySound SRace = new PlaySound(1, "S_Race", 0, 0, 0, 0, 0);
            activeChar.sendPacket(SRace);
            activeChar.broadcastPacket(SRace);
            PlaySound SRace2 = new PlaySound(0, "ItemSound2.race_start", 1, 121209259, 12125, 182487,
                                             -3559);
            activeChar.sendPacket(SRace2);
            activeChar.broadcastPacket(SRace2);
            MonRaceInfo spk = new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(),
                                              race.getSpeeds());
            activeChar.sendPacket(spk);
            activeChar.broadcastPacket(spk);

            ThreadPoolManager.getInstance().scheduleGeneral(new RunRace(codes, activeChar), 5000);
        }

    }

    class RunRace implements Runnable
    {

        private int[][] codes;
        private L2PcInstance activeChar;

        public RunRace(int[][] pCodes, L2PcInstance pActiveChar)
        {
            codes = pCodes;
            activeChar = pActiveChar;
        }


		public void run()
        {
            //int[][] speeds1 = MonsterRace.getInstance().getSpeeds();
            //MonsterRace.getInstance().newSpeeds();
            //int[][] speeds2 = MonsterRace.getInstance().getSpeeds();
            /*
             int[] speed = new int[8];
             for (int i=0; i<8; i++)
             {
             for (int j=0; j<20; j++)
             {
             //System.out.println("Adding "+speeds1[i][j] +" and "+ speeds2[i][j]);
             speed[i] += (speeds1[i][j]*1);// + (speeds2[i][j]*1);
             }
             System.out.println("Total speed for "+(i+1)+" = "+speed[i]);
             }*/

            MonRaceInfo spk = new MonRaceInfo(codes[2][0], codes[2][1],
                                              MonsterRace.getInstance().getMonsters(),
                                              MonsterRace.getInstance().getSpeeds());
            activeChar.sendPacket(spk);
            activeChar.broadcastPacket(spk);
            ThreadPoolManager.getInstance().scheduleGeneral(new RunEnd(activeChar), 30000);
        }
    }

    class RunEnd implements Runnable
    {
        private L2PcInstance activeChar;

        public RunEnd(L2PcInstance pActiveChar)
        {
            activeChar = pActiveChar;
        }


		public void run()
        {
            DeleteObject obj = null;
            for (int i = 0; i < 8; i++)
            {
                obj = new DeleteObject(MonsterRace.getInstance().getMonsters()[i]);
                activeChar.sendPacket(obj);
                activeChar.broadcastPacket(obj);
            }
            state = -1;
        }
    }
}
