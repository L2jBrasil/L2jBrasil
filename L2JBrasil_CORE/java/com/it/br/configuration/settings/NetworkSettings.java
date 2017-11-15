package com.it.br.configuration.settings;

import java.util.List;

import com.it.br.configuration.L2Properties;

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
	private boolean telnetEnabled;
	private int telnetPort;
	private List<String> telnetAcceeptHosts;
	private String telnetPassword;
	
	@Override
	public void load(L2Properties properties) {
		if(properties == null) { 
			return;
		}
		
		serverHostname = properties.getString("ServerHostname", "*");
		serverPort = properties.getInteger("ServerPort", 7777);
		serverExternalHostname = properties.getString("ServerExternalHostname", "127.0.0.1");
		serverInternalHostname = properties.getString("ServerInternalHostname", "127.0.0.1");
		
		loginHostname = properties.getString("LoginHostname", "*");
		loginPort = properties.getInteger("LoginPort", 2106);
		loginExternalHostname = properties.getString("LoginExternalHostname", "127.0.0.1");
		loginInternalHostname = properties.getString("LoginInternalHostname", "127.0.0.1");
		
		loginListenServerHostname = properties.getString("LoginListenServerHostname", "127.0.0.1");
		loginListenServerPort = properties.getInteger("LoginListenServerPort", 9014);
		
		telnetEnabled = properties.getBoolean("EnableTelnet", false);
		
		if(telnetEnabled) {
			telnetPort = properties.getInteger("TelnetPort", 12345);
			telnetAcceeptHosts = properties.getStringList("TelnetAcceptedHosts", "127.0.0.1,localhost", ",");
			telnetPassword = properties.getString("TelnetPassword", null);
		}
				
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
	

	public boolean isTelnetEnabled() {
		return telnetEnabled;
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

