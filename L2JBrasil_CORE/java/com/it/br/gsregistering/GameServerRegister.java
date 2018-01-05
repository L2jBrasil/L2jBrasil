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
package com.it.br.gsregistering;

import com.it.br.Config;
import com.it.br.Server;
import com.it.br.gameserver.LoginServerThread;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.loginserver.GameServerTable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class GameServerRegister
{
	private static String _choice;
	private static boolean _choiceOk;

	public static void main(String[] args) throws IOException
	{
		Server.serverMode = Server.MODE_LOGINSERVER;

		Config.load();

		LineNumberReader _in = new LineNumberReader(new InputStreamReader(System.in));
		try
		{
			GameServerTable.load();
		}
		catch (Exception e)
		{
			System.out.println("FATAL: Failed loading GameServerTable. Reason: "+e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		GameServerTable gameServerTable = GameServerTable.getInstance();
        System.out.println("#----------------------------------------------");
        System.out.println("# Welcome to L2JBrasil GameServer Regitering ");
        System.out.println("#----------------------------------------------");
        System.out.println("# Type the id of the server you want register");
        System.out.println("# Type 'help' or 'ajuda' to get a list of ids.");
        System.out.println("# Type 'clean' or 'l2jbrasil' to unregister all currently");
        System.out.println("# registered gameservers on this LoginServer.");
        System.out.println("#--------------------------------------------");
        
		while (!_choiceOk)
		{
			System.out.println("Your choice:");
			_choice = _in.readLine();
			if((_choice.equalsIgnoreCase("help")) || (_choice.equalsIgnoreCase("ajuda")))
			{
				for (Map.Entry<Integer, String> entry : gameServerTable.getServerNames().entrySet())
				{
					System.out.println("Server: ID: "+entry.getKey()+"\t- "+entry.getValue()+" - In Use: "+(gameServerTable.hasRegisteredGameServerOnId(entry.getKey()) ? "YES" : "NO"));
				}
				System.out.println("You can also see servername.xml");
			}
			else if((_choice.equalsIgnoreCase("clean")) || (_choice.equalsIgnoreCase("l2jbrasil")))
			{
				System.out.print("This is going to UNREGISTER ALL servers from this LoginServer. Are you sure? (y/n) ");
				System.out.print(" Are you sure? (y/n),(s/n)");
				_choice = _in.readLine();
				if ((_choice.equals("y")) || (_choice.equals("s")))
				{
					GameServerRegister.cleanRegisteredGameServersFromDB();
					gameServerTable.getRegisteredGameServers().clear();
				}
				else
				{
					System.out.println("ABORTED");
				}
			}
			else
			{
				try
				{
					int id = new Integer(_choice).intValue();
					int size = gameServerTable.getServerNames().size();

					if (size == 0)
					{
						System.out.println("No server names avalible, please make sure that servername.xml is in the LoginServer directory.");
						System.exit(1);
					}

					String name = gameServerTable.getServerNameById(id);
					if (name == null)
					{
						System.out.println("No name for id: "+id);
						continue;
					}
					else
					{
						if (gameServerTable.hasRegisteredGameServerOnId(id))
						{
							System.out.println("This id is not free");
						}
						else
						{
							byte[] hexId = LoginServerThread.generateHex(16);
							gameServerTable.registerServerOnDB(hexId, id, "");
							Config.saveHexid(id, new BigInteger(hexId).toString(16),"hexid.txt");
							System.out.println("Server Registered hexid saved to 'hexid.txt'");
							System.out.println("Put this file in the /config/other folder of your gameserver and rename it to 'hexid.txt'");
							return;
						}
					}
				}
				catch (NumberFormatException nfe)
				{
					System.out.println("Please, type a number or 'help'");
				}
			}
		}
	}

	public static void cleanRegisteredGameServersFromDB()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM gameservers");
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			System.out.println("SQL error while cleaning registered servers: "+e);
		}
		finally
		{
			try {statement.close();} catch (Exception e) {}
			try { con.close();} catch (Exception e) {}
		}
	}
}