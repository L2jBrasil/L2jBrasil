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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.model.GMAudit;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class handles following admin commands: - delete = deletes target
 *
 * @version $Revision: 1.1.2.6.2.3 $ $Date: 2005/04/11 10:05:59 $
 */
public class AdminRepairChar implements IAdminCommandHandler
{
    private static Logger _log = Logger.getLogger(AdminRepairChar.class.getName());

    private static final String[] ADMIN_COMMANDS = { "admin_restore", "admin_repair" };

    private static final int REQUIRED_LEVEL = Config.GM_CHAR_EDIT;


	public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        if (!Config.ALT_PRIVILEGES_ADMIN)
        {
            if (!(checkLevel(activeChar.getAccessLevel()) && activeChar.isGM()))
                return false;
        }

		String target = (activeChar.getTarget() != null?activeChar.getTarget().getName():"no-target");
        GMAudit.auditGMAction(activeChar.getName(), command, target, "");

        handleRepair(command);
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

    private void handleRepair(String command)
    {
        String[] parts = command.split(" ");
        if (parts.length != 2)
        {
            return;
        }

        String cmd = "UPDATE characters SET x=-84318, y=244579, z=-3730 WHERE char_name=?";
        java.sql.Connection connection = null;
        try
        {
            connection = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement(cmd);
            statement.setString(1,parts[1]);
            statement.execute();
            statement.close();

            statement = connection.prepareStatement("SELECT obj_id FROM characters where char_name=?");
            statement.setString(1,parts[1]);
            ResultSet rset = statement.executeQuery();
            int objId = 0;
            if (rset.next())
            {
                objId = rset.getInt(1);
            }

            rset.close();
            statement.close();

            if (objId == 0) {connection.close(); return;}

            //connection = L2DatabaseFactory.getInstance().getConnection();
            statement = connection.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=?");
            statement.setInt(1, objId);
            statement.execute();
            statement.close();


            //connection = L2DatabaseFactory.getInstance().getConnection();
            statement = connection.prepareStatement("UPDATE items SET loc=\"INVENTORY\" WHERE owner_id=?");
            statement.setInt(1, objId);
            statement.execute();
            statement.close();
            connection.close();
        }
        catch (Exception e)
        {
			_log.log(Level.WARNING, "could not repair char:", e);
        }
        finally
        {
            try { connection.close(); } catch (Exception e) {}
        }
    }
}
