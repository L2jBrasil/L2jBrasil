package com.it.br.gameserver.handler.voicedcommandhandlers;

import com.it.br.Config;
import com.it.br.gameserver.handler.IVoicedCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;

public class Res implements IVoicedCommandHandler
{
    private static final String[] VOICED_COMMANDS = { "res" };


	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
    {   
        if (command.equalsIgnoreCase("res"))
        {
           if (!activeChar.isAlikeDead())
           {
                 activeChar.sendMessage("You cannot be ressurected while alive.");
             return false;
           }
           if(activeChar.isInOlympiadMode())
           {
              activeChar.sendMessage("You cannot use this feature during olympiad.");
             return false;
           }
           if(activeChar.atEvent && TvTEvent.isStarted()) 
           { 
        	 	 activeChar.sendMessage("You cannot use this feature during TvT."); 
        	 return false; 
           } 
           if(activeChar.getInventory().getItemByItemId(Config.RES_CMD_CONSUME_ID) == null)
           {
              activeChar.sendMessage("You need 1 or more Gold Bars to use the ressurection system.");
             return false;
           }
           
              activeChar.sendMessage("You have been ressurected!");
              activeChar.getInventory().destroyItemByItemId("RessSystem", Config.RES_CMD_CONSUME_ID, 1, activeChar, activeChar.getTarget());
              activeChar.doRevive();
              activeChar.broadcastUserInfo();
              activeChar.sendMessage("One GoldBar has dissapeared! Thank you!");
        }
       return true;
    }

	public String[] getVoicedCommandList()
    {
        return VOICED_COMMANDS;
    }
}