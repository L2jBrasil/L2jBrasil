package com.it.br.gameserver.handler.admincommandhandlers;

import com.it.br.Config;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.EtcStatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminClanFull implements IAdminCommandHandler
{
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

    public AdminClanFull()
    {
        admin.put("admin_clanfull", Config.admin_clanfull);
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

        if(command.startsWith("admin_clanfull"))
        {
            try
            {
                adminAddClanSkill(activeChar);
                activeChar.sendMessage("Sucessfull usage //clanfull !");
            }
            catch(Exception e)
            {
                activeChar.sendMessage("Usage: //clanfull");
            }
        }
        return true;
    }

    private void adminAddClanSkill(L2PcInstance activeChar)
    {
        L2Object target = activeChar.getTarget();
        if(target == null)
            target = activeChar;
        L2PcInstance player = null;
        if(target instanceof L2PcInstance)
        {
            player = (L2PcInstance)target;
        }
        else
        {
            activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
            return;
        }

        if(!player.isClanLeader())
        {
        	player.sendPacket((new SystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER)).addString(player.getName()));
        	return;
        }
        player.getClan().changeLevel(Config.CLAN_LEVEL);
        player.getClan().giveAllClanSkillsAndReputation(player, Config.REPUTATION_QUANTITY);
        player.sendPacket(new EtcStatusUpdate(activeChar));
    }
}