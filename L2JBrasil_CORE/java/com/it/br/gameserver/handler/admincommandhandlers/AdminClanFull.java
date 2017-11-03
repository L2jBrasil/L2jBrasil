// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 26/7/2011 21:21:40
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AdminClanFull.java

package com.it.br.gameserver.handler.admincommandhandlers;

import com.it.br.Config;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.EtcStatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

public class AdminClanFull implements IAdminCommandHandler
{
    public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
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
        player.ClanSkills();
        player.sendPacket(new EtcStatusUpdate(activeChar));
    }

    public String[] getAdminCommandList()
    {
        return ADMIN_COMMANDS;
    }

    private static final String ADMIN_COMMANDS[] = 
    {
        "admin_clanfull"
    };
}