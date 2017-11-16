package com.it.br.gameserver.handler.admincommandhandlers;

import com.it.br.Config;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2Party;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * This class handles following admin commands:
 * - recallparty
 * - recallclan
 * - recallally
 * 
 * @author  Yamaneko
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminMassRecall implements IAdminCommandHandler
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

    public AdminMassRecall()
    {
        admin.put("admin_recallclan", Config.admin_recallclan);
        admin.put("admin_recallparty", Config.admin_recallparty);
        admin.put("admin_recallally", Config.admin_recallally);
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

        if (command.startsWith("admin_recallclan"))
        {
        	try
        	{
            	String val = command.substring(17).trim();

	            L2Clan clan = ClanTable.getInstance().getClanByName(val);
	            if(clan == null)
	            {
	            	activeChar.sendMessage("This clan doesn't exists.");
	            	return true;
	            }

	            L2PcInstance[] m = clan.getOnlineMembers("");
	            for (L2PcInstance element : m) {
					Teleport(element, activeChar.getX(), activeChar.getY(), activeChar.getZ(), "Admin is teleporting you");
				}
        	}
        	catch(Exception e)
        	{
        		activeChar.sendMessage("Error in recallclan command.");
        	}
        }
        else if (command.startsWith("admin_recallally"))
        {
        	try
        	{
        		String val = command.substring(17).trim();
	            L2Clan clan = ClanTable.getInstance().getClanByName(val);
	            if(clan == null) 
	            {
	            	activeChar.sendMessage("This clan doesn't exists.");
	            	return true;
	            }

	            int ally = clan.getAllyId();
                if (ally == 0)
	            {
		            L2PcInstance[] m = clan.getOnlineMembers("");
		            for (L2PcInstance element : m) {
						Teleport(element, activeChar.getX(), activeChar.getY(), activeChar.getZ(), "Admin is teleporting you");
					}
	            }
	            else
	            {
	            	for(L2Clan aclan : ClanTable.getInstance().getClans())
	            	{
	            		if(aclan.getAllyId() == ally)
	            		{
	            			L2PcInstance[] m = aclan.getOnlineMembers("");
	            			for (L2PcInstance element : m) {
								Teleport(element, activeChar.getX(), activeChar.getY(), activeChar.getZ(), "Admin is teleporting you");
							}
	            		}
	            	}
	            }
        	}
        	catch(Exception e)
        	{
        		activeChar.sendMessage("Error in recallally command.");
        	}
        }
        else if (command.startsWith("admin_recallparty"))
        {
        	try
        	{
        		String val = command.substring(18).trim();
        		L2PcInstance player = L2World.getInstance().getPlayer(val);
        		if(player == null)
        		{
        			activeChar.sendMessage("Target error.");
        			return true;
        		}
        		if(!player.isInParty())
        		{
        			activeChar.sendMessage("Player is not in party.");
        			return true;
        		}
        		L2Party p = player.getParty();
        		for(L2PcInstance ppl : p.getPartyMembers()) {
	    			Teleport(ppl, activeChar.getX(), activeChar.getY(), activeChar.getZ(), "Admin is teleporting you");
        		}
        	}
        	catch(Exception e)
        	{
        		activeChar.sendMessage("Error in recallparty command.");
        	}
        }
        return true;
    }

    private void Teleport(L2PcInstance player, int X, int Y, int Z, String Message)
    {
    	player.sendMessage(Message);
    	player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
    	player.teleToLocation(X, Y, Z, true);
    }
}