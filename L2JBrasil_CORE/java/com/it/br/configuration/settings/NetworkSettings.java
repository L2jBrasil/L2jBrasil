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
package com.it.br.configuration.settings;

import java.util.List;

import com.it.br.configuration.L2Properties;

/**
 *
 * @author  Alisson Oliveira
 */
public class NetworkSettings implements Settings{

	
	private String serverHostname;
	private int serverPort;
	private String serverExternalHostname;
	private String serverInternalHostname;
	
	private String loginListenServerHostname;
	private int loginListenServerPort;

	private String loginHostname;
	private int loginPort;
	private String loginExternalHostname;
	private String loginInternalHostname;
	private boolean enabledTelnet;
	private int telnetPort;
	private List<String> telnetAcceeptHosts;
	private String telnetPassword;
	
	@Override
	public void load(L2Properties properties) {
		if(properties == null) { 
			return;
		}
		
		serverHostname = properties.getString("server.hostname", "*");
		serverPort = properties.getInteger("server.port", 7777);
		serverExternalHostname = properties.getString("server.hostname.extern", "127.0.0.1");
		serverInternalHostname = properties.getString("server.hostname.intern", "127.0.0.1");
		
		loginHostname = properties.getString("login.hostname", "*");
		loginPort = properties.getInteger("login.port", 2106);
		loginExternalHostname = properties.getString("login.hostname.extern", "127.0.0.1");
		loginInternalHostname = properties.getString("login.hostname.intern", "127.0.0.1");
		
		loginListenServerHostname = properties.getString("login.listen.server.hostname", "127.0.0.1");
		loginListenServerPort = properties.getInteger("login.listen.server.port", 9014);
		
		enabledTelnet = properties.getBoolean("telnet.enable", false);
		telnetPort = properties.getInteger("telnet.port", 12345);
		telnetAcceeptHosts = properties.getStringList("telnet.accept.hosts", "127.0.0.1,localhost", ",");
		telnetPassword = properties.getString("telnet.password", null);
				
	}
	
	public String getServerHostname() {
		return serverHostname;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public String getServerExternalHostname() {
		return serverExternalHostname;
	}
	
	public String getServerInternalHostname() {
		return serverInternalHostname;
	}
	
	public String getLoginListenServerHostname() {
		return loginListenServerHostname;
	}

	public int getLoginListenServerPort() {
		return loginListenServerPort;
	}
	
	public String getLoginHostname() {
		return loginHostname;
	}
	
	public int getLoginPort() {
		return loginPort;
	}
	
	public String getLoginExternalHostname() {
		return loginExternalHostname;
	}
	
	public String getLoginInternalHostname() {
		return loginInternalHostname;
	}
	

	public boolean isEnabledTelnet() {
		return enabledTelnet;
	}
	
	public int getTelnetPort() {
		return telnetPort;
	}
	
	public List<String> getTelnetAcceeptHosts() {
		return telnetAcceeptHosts;
	}
	
	public String getTelnetPassword() {
		return telnetPassword;
	}
}

