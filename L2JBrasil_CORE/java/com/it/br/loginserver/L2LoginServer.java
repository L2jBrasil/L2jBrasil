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
package com.it.br.loginserver;

import static com.it.br.configuration.Configurator.getSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.Server;
import com.it.br.configuration.Configurator;
import com.it.br.configuration.settings.LoginSettings;
import com.it.br.configuration.settings.NetworkSettings;
import com.it.br.status.Status;
import com.l2jserver.mmocore.network.SelectorConfig;
import com.l2jserver.mmocore.network.SelectorThread;

/**
 *
 * @author KenM
 */
public class L2LoginServer
{
    /**
	 * Comment for <code>BIND_ALL_INTERFACES</code>
	 */
	private static final String BIND_ALL_INTERFACES = "*";

	public static final int PROTOCOL_REV = 0x0102;

    private static L2LoginServer _instance;
    private Logger _log = Logger.getLogger(L2LoginServer.class.getName());
    private GameServerListener _gameServerListener;
    private SelectorThread<L2LoginClient> _selectorThread;
    private Status _statusServer;

    public static void main(String[] args)
    {
        _instance = new L2LoginServer();
    }

    public static L2LoginServer getInstance()
    {
        return _instance;
    }

    public L2LoginServer()
    {
        Server.serverMode = Server.MODE_LOGINSERVER;
//      Local Constants
        final String LOG_FOLDER = "log"; // Name of folder for log file
        final String LOG_NAME   = "./config/other/log.cfg"; // Name of log file

        /*** Main ***/
        // Create log folder
        LoginSettings loginSettings = Configurator.getSettings(LoginSettings.class);
        File logFolder = new File(loginSettings.getDatapackDirectory(), LOG_FOLDER);
        logFolder.mkdir();

        // Create input stream for log file -- or store file data into memory
        InputStream is = null;
        try
        {
            is = new FileInputStream(new File(LOG_NAME));
            LogManager.getLogManager().readConfiguration(is);
            is.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        // Load Config
        Config.load();

        // Prepare Database
        try
        {
            L2DatabaseFactory.getInstance();
        }
        catch (SQLException e)
        {
            _log.severe("FATAL: Failed initializing database. Reason: "+e.getMessage());
            if (Config.DEVELOPER)
            {
                e.printStackTrace();
            }
            System.exit(1);
        }

        try
        {
            LoginController.load();
        }
        catch (GeneralSecurityException e)
        {
            _log.severe("FATAL: Failed initializing LoginController. Reason: "+e.getMessage());
            if (Config.DEVELOPER)
            {
                e.printStackTrace();
            }
            System.exit(1);
        }

        try
        {
            GameServerTable.load();
        }
        catch (GeneralSecurityException e)
        {
            _log.severe("FATAL: Failed to load GameServerTable. Reason: "+e.getMessage());
            if (Config.DEVELOPER)
            {
                e.printStackTrace();
            }
            System.exit(1);
        }
        catch (SQLException e)
        {
            _log.severe("FATAL: Failed to load GameServerTable. Reason: "+e.getMessage());
            if (Config.DEVELOPER)
            {
                e.printStackTrace();
            }
            System.exit(1);
        }

        loadBanFile();

        InetAddress bindAddress = null;
        NetworkSettings networkSettings = getSettings(NetworkSettings.class);
        String hostname = networkSettings.getLoginHostname();
        if (! BIND_ALL_INTERFACES.equals(hostname) )
        {
            try
            {
                bindAddress = InetAddress.getByName(hostname);
            }
            catch (UnknownHostException e1)
            {
                _log.severe("WARNING: The LoginServer bind address is invalid, using all avaliable IPs. Reason: "+e1.getMessage());
                if (Config.DEVELOPER)
                {
                    e1.printStackTrace();
                }
            }
        }

        final SelectorConfig sc = new SelectorConfig();
        sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
        sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
        sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
        sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
        final L2LoginPacketHandler lph = new L2LoginPacketHandler();
        final SelectorHelper sh = new SelectorHelper();
        try
        {
              _selectorThread = new SelectorThread<L2LoginClient>(sc, sh, lph, sh, sh);
        }
        catch (IOException e)
        {
            _log.severe("FATAL: Failed to open Selector. Reason: "+e.getMessage());
            if (Config.DEVELOPER)
            {
                e.printStackTrace();
            }
            System.exit(1);
        }

        try
        {
            _gameServerListener = new GameServerListener();
            _gameServerListener.start();
            _log.info("Listening for GameServers on "+networkSettings.getLoginListenServerHostname()+":"+networkSettings.getLoginListenServerPort());
        }
        catch (IOException e)
        {
            _log.severe("FATAL: Failed to start the Game Server Listener. Reason: "+e.getMessage());
            if (Config.DEVELOPER)
            {
                e.printStackTrace();
            }
            System.exit(1);
        }

        if ( Config.IS_TELNET_ENABLED )
        {
            try
            {
                _statusServer = new Status(Server.serverMode);
                _statusServer.start();
            }
            catch (IOException e)
            {
                _log.severe("Failed to start the Telnet Server. Reason: "+e.getMessage());
                if (Config.DEVELOPER)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            System.out.println("Telnet server is currently disabled.");
        }

        try
        {
            _selectorThread.openServerSocket(bindAddress, networkSettings.getLoginPort());
        }
        catch (IOException e)
        {
            _log.severe("FATAL: Failed to open server socket. Reason: "+e.getMessage());
            if (Config.DEVELOPER)
            {
                e.printStackTrace();
            }
            System.exit(1);
        }
        _selectorThread.start();
        _log.info("Login Server ready on "+(bindAddress == null ? BIND_ALL_INTERFACES : bindAddress.getHostAddress())+":"+networkSettings.getLoginPort());
    }

    public Status getStatusServer()
    {
        return _statusServer;
    }

    public GameServerListener getGameServerListener()
    {
        return _gameServerListener;
    }

    private void loadBanFile()
    {
        File bannedFile = new File("./config/other/banned_ip.cfg");
        if (bannedFile.exists() && bannedFile.isFile())
        {
            FileInputStream fis = null;
            try
            {
                fis = new FileInputStream(bannedFile);
            }
            catch (FileNotFoundException e)
            {
                _log.warning("Failed to load banned IPs file ("+bannedFile.getName()+") for reading. Reason: "+e.getMessage());
                if (Config.DEVELOPER)
                {
                    e.printStackTrace();
                }
                return;
            }

            LineNumberReader reader = new LineNumberReader(new InputStreamReader(fis));

            String line;
            String[] parts;
            try
            {

                while ((line = reader.readLine()) != null)
                {
                    line = line.trim();
                    // check if this line isnt a comment line
                    if (line.length() > 0 && line.charAt(0) != '#')
                    {
                        // split comments if any
                        parts = line.split("#");

                        // discard comments in the line, if any
                        line = parts[0];

                        parts = line.split(" ");

                        String address = parts[0];

                        long duration = 0;

                        if (parts.length > 1)
                        {
                            try
                            {
                                duration = Long.parseLong(parts[1]);
                            }
                            catch (NumberFormatException e)
                            {
                                _log.warning("Skipped: Incorrect ban duration ("+parts[1]+") on ("+bannedFile.getName()+"). Line: "+reader.getLineNumber());
                                continue;
                            }
                        }

                        try
                        {
                            LoginController.getInstance().addBanForAddress(address, duration);
                        }
                        catch (UnknownHostException e)
                        {
                            _log.warning("Skipped: Invalid address ("+parts[0]+") on ("+bannedFile.getName()+"). Line: "+reader.getLineNumber());
                        }
                    }
                }
            }
            catch (IOException e)
            {
                _log.warning("Error while reading the bans file ("+bannedFile.getName()+"). Details: "+e.getMessage());
                if (Config.DEVELOPER)
                {
                    e.printStackTrace();
                }
            }
            _log.config("Loaded "+LoginController.getInstance().getBannedIps().size()+" IP Bans.");
        }
        else
        {
            _log.config("IP Bans file ("+bannedFile.getName()+") is missing or is a directory, skipped.");
        }
    }

    public void shutdown(boolean restart)
    {
        Runtime.getRuntime().exit(restart ? 2 : 0);
    }
}