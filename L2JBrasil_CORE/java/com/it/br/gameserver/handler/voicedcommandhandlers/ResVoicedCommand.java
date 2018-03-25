package com.it.br.gameserver.handler.voicedcommandhandlers;

import com.it.br.configuration.settings.CommandSettings;
import com.it.br.gameserver.handler.IVoicedCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;

import static com.it.br.configuration.Configurator.getSettings;

public class ResVoicedCommand implements IVoicedCommandHandler
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
           
           CommandSettings commandSettings = getSettings(CommandSettings.class);
           int consumeId = commandSettings.getResCommandConsumeId();
           
           if(activeChar.getInventory().getItemByItemId(consumeId) == null)
           {
              activeChar.sendMessage("You need 1 or more Gold Bars to use the ressurection system.");
             return false;
           }
           
              activeChar.sendMessage("You have been ressurected!");
              activeChar.getInventory().destroyItemByItemId("RessSystem", consumeId, 1, activeChar, activeChar.getTarget());
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