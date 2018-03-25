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
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.util.IllegalPlayerAction;
import com.it.br.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
/**
 * This class handles following admin commands:
 * <li> add_exp_sp_to_character <i>shows menu for add or remove</i>
 * <li> add_exp_sp exp sp <i>Adds exp & sp to target, displays menu if a parameter is missing</i>
 * <li> remove_exp_sp exp sp <i>Removes exp & sp from target, displays menu if a parameter is missing</i>
 * @version $Revision: 3.0.3 $ $Date: 2017/11/09 $
 */
public class AdminExpSp implements IAdminCommandHandler
{
    private static Logger _log = LoggerFactory.getLogger(AdminExpSp.class);
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


    public AdminExpSp()
    {
        admin.put("admin_add_exp_sp", Config.admin_add_exp_sp);
        admin.put("admin_remove_exp_sp", Config.admin_remove_exp_sp);
        admin.put("admin_add_exp_sp_to_character", Config.admin_add_exp_sp_to_character);
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

		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget()!=null ? activeChar.getTarget().getName() : "no-target"), "");

		if (command.startsWith("admin_add_exp_sp"))
		{
			try
			{
				String val = command.substring(16);
				if (!adminAddExpSp(activeChar, val))
					activeChar.sendMessage("Usage: //add_exp_sp exp sp");
			}
			catch (StringIndexOutOfBoundsException e)
			{	//Case of missing parameter
				activeChar.sendMessage("Usage: //add_exp_sp exp sp");
			}
		}
		else if(command.startsWith("admin_remove_exp_sp"))
		{
			try
			{
				String val = command.substring(19);
				if (!adminRemoveExpSP(activeChar, val))
					activeChar.sendMessage("Usage: //remove_exp_sp exp sp");
			}
			catch (StringIndexOutOfBoundsException e)
			{   //Case of missing parameter
				activeChar.sendMessage("Usage: //remove_exp_sp exp sp");
			}
		}
		addExpSp(activeChar);
		return true;
	}

	private void addExpSp(L2PcInstance activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
			player = (L2PcInstance)target;
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return;
		}
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile("data/html/admin/expsp.htm");
		adminReply.replace("%name%", player.getName());
		adminReply.replace("%level%", String.valueOf(player.getLevel()));
		adminReply.replace("%xp%", String.valueOf(player.getExp()));
		adminReply.replace("%sp%", String.valueOf(player.getSp()));
		adminReply.replace("%class%", player.getTemplate().className);
		activeChar.sendPacket(adminReply);
	}

	private boolean adminAddExpSp(L2PcInstance activeChar, String ExpSp)
	{
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
		{
			player = (L2PcInstance)target;
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return false;
		}
		StringTokenizer st = new StringTokenizer(ExpSp);
		if (st.countTokens()!=2)
		{
			return false;
		}
		else
		{
			String exp = st.nextToken();
			String sp = st.nextToken();
			long expval = 0;
			int spval = 0;
			try
			{
				expval = Long.parseLong(exp);
				spval = Integer.parseInt(sp);
			}
			catch(Exception e)
			{
				return false;
			}
            /** 
            * Anti-Corrupt GMs Protection. 
            * If GMEdit enabled, a GM won't be able to Add Exp or SP to any other 
            * player that's NOT  a GM character. And in addition.. both player and 
            * GM WILL be banned. 
            */ 
            if(Config.GM_EDIT && (expval != 0 || spval != 0)&& !player.isGM()) 
            { 
             //Warn the player about his inmediate ban. 
             player.sendMessage("A GM tried to edit you in "+expval+" exp points and in "+spval+" sp points.You will both be banned."); 
                 Util.handleIllegalPlayerAction(player,"The player "+player.getName()+" has been edited. BAN!!", IllegalPlayerAction.PUNISH_KICKBAN); 
             //Warn the GM about his inmediate ban. 
             player.sendMessage("You tried to edit "+player.getName()+" by "+expval+" exp points and "+spval+". You both be banned now."); 
                 Util.handleIllegalPlayerAction(activeChar,"El GM "+activeChar.getName()+" ha editado a alguien. BAN!!", IllegalPlayerAction.PUNISH_KICKBAN); 
             _log.error("GM "+activeChar.getName()+" tried to edit "+player.getName()+". They both have been Banned.");
             } 
			if(expval != 0 || spval != 0)
			{
				//Common character information
				player.sendMessage("Admin is adding you "+expval+" xp and "+spval+" sp.");
				player.addExpAndSp(expval,spval);
				//Admin information
				activeChar.sendMessage("Added "+expval+" xp and "+spval+" sp to "+player.getName()+".");
				if (Config.DEBUG)
					_log.debug("GM: "+activeChar.getName()+"("+activeChar.getObjectId()+") added "+expval+
							" xp and "+spval+" sp to "+player.getObjectId()+".");
			}
		}
		return true;
	}

	private boolean adminRemoveExpSP(L2PcInstance activeChar, String ExpSp)
	{
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance)
		{
			player = (L2PcInstance)target;
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			return false;
		}
		StringTokenizer st = new StringTokenizer(ExpSp);
		if (st.countTokens()!=2)
			return false;
		else
		{
			String exp = st.nextToken();
			String sp = st.nextToken();
			long expval = 0;
			int spval = 0;
			try
			{
				expval = Long.parseLong(exp);
				spval = Integer.parseInt(sp);
			}
			catch (Exception e)
			{
				return false;
			}
			if(expval != 0 || spval != 0)
			{
				//Common character information
				player.sendMessage("Admin is removing you "+expval+" xp and "+spval+" sp.");
				player.removeExpAndSp(expval,spval);
				//Admin information
				activeChar.sendMessage("Removed "+expval+" xp and "+spval+" sp from "+player.getName()+".");
				if (Config.DEBUG)
					_log.debug("GM: "+activeChar.getName()+"("+activeChar.getObjectId()+") removed "+expval+
							" xp and "+spval+" sp from "+player.getObjectId()+".");
			}
		}
		return true;
	}
}
