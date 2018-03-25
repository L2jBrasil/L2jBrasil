/* This program is free software; you can redistribute it and/or modify
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
package com.it.br.status;

import com.it.br.Server;
import com.it.br.configuration.settings.NetworkSettings;
import com.it.br.util.Rnd;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.it.br.configuration.Configurator.getSettings;


public class Status extends Thread
{

    private ServerSocket    statusServerSocket;

    private int             		_uptime;
    private int             		_statusPort;
    private String          		_statusPw;
    private int						_mode;
    private List<LoginStatusThread> _loginStatus;

    @Override
	public void run()
    {
        while (true)
        {
            try
            {
                Socket connection = statusServerSocket.accept();

                if(_mode == Server.MODE_GAMESERVER)
                {
                	new GameStatusThread(connection, _uptime, _statusPw);
                }
                else if(_mode == Server.MODE_LOGINSERVER)
                {
                	LoginStatusThread lst = new LoginStatusThread(connection, _uptime);
                	if(lst.isAlive())
                	{
                		_loginStatus.add(lst);
                	}
                }
                if (this.isInterrupted())
                {
                    try
                    {
                        statusServerSocket.close();
                    }
                    catch (IOException io) { io.printStackTrace(); }
                    break;
                }
            }
            catch (IOException e)
            {
                if (this.isInterrupted())
                {
                    try
                    {
                        statusServerSocket.close();
                    }
                    catch (IOException io) { io.printStackTrace(); }
                    break;
                }
            }
        }
    }

    public Status(int mode) throws IOException
    {
        super("Status");
        _mode= mode;
        
        NetworkSettings networkSettings = getSettings(NetworkSettings.class);
        _statusPort = networkSettings.getTelnetPort();
        _statusPw   = networkSettings.getTelnetPassword();
        
        if (_statusPw == null)  {
            System.out.println("Server's Telnet Function Has No Password Defined!");
            System.out.println("A Password Has Been Automaticly Created!");
            _statusPw = rndPW(10);
        }
        
        System.out.println("StatusServer Started! - Listening on Port: " + _statusPort);
        System.out.println("Password Has Been Set To: " + _statusPw);

        statusServerSocket = new ServerSocket(_statusPort);
        _uptime = (int) System.currentTimeMillis();
        _loginStatus = new ArrayList<>();
    }



    private String rndPW(int length)
    {
        StringBuilder password = new StringBuilder();
        String lowerChar= "qwertyuiopasdfghjklzxcvbnm";
        String upperChar = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String digits = "1234567890";
        for (int i = 0; i < length; i++)
        {
            int charSet = Rnd.nextInt(3);
            switch (charSet)
            {
                case 0:
                    password.append(lowerChar.charAt(Rnd.nextInt(lowerChar.length()-1)));
                    break;
                case 1:
                    password.append(upperChar.charAt(Rnd.nextInt(upperChar.length()-1)));
                    break;
                case 2:
                    password.append(digits.charAt(Rnd.nextInt(digits.length()-1)));
                    break;
            }
        }
        return password.toString();
    }

    public void sendMessageToTelnets(String msg)
    {
    	List<LoginStatusThread> lsToRemove = new ArrayList<>();
    	for(LoginStatusThread ls :_loginStatus)
    	{
    		if(ls.isInterrupted())
    		{
    			lsToRemove.add(ls);
    		}
    		else
    		{
    			ls.printToTelnet(msg);
    		}
    	}
    }
}
