package com.it.br.configuration.settings;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.it.br.configuration.L2Properties;


public class LoginSettings implements Settings{

	private static final Logger logger = Logger.getLogger(LoginSettings.class.getName());

	private File datapackDirectory;
	private int triesBeforeBan;
	private int timeBlockAfterBan;
	private boolean enabledAnyServer;
	private boolean enabledShowLicence;
	private boolean enabledAutoCreateAccount;
	private int fastConnectionLimit;
	private int fastConnectionTime;
	private int normalConnectionTime;
	private int maxConnectionPerIP;
		
	@Override
	public void load(L2Properties properties) {
		if(properties == null) { 
			return;
		}
		
		triesBeforeBan = properties.getInteger("LoginTriesBeforeBan", 10);
		timeBlockAfterBan = properties.getInteger("BlockTimeAfterBan", 600);
		
		enabledAnyServer  = properties.getBoolean("AllowNewServers", false);
		enabledShowLicence = properties.getBoolean("ShowLicense", true);
		enabledAutoCreateAccount = properties.getBoolean("AllowAutoCreateAccount", true);		
		
		fastConnectionLimit = properties.getInteger("ConnectionFastLimit", 15);
		fastConnectionTime = properties.getInteger("ConnectionFastTime", 350);
		normalConnectionTime = properties.getInteger("ConnectionNormalTime", 700);
		maxConnectionPerIP = properties.getInteger("MaxConnectionPerIP", 50);
		
		loadDatapackDirectory(properties);
		
	}
	
	private void loadDatapackDirectory(L2Properties properties) {
		String datapackPath = properties.getString("DatapackRootDirectory", ".");
		try {
			datapackDirectory = new File(datapackPath).getCanonicalFile();
		} catch (IOException e) {
			logger.warning("Error defining Datapack directory " + datapackPath);
			logger.warning(e.getMessage());
			setDefaultDatapackDirectory();
		}
	}

	private void setDefaultDatapackDirectory() {
		try {
			datapackDirectory = new File(".").getCanonicalFile();
			logger.info("Setting default datapack directory: " + datapackDirectory.getAbsolutePath());
		} catch (IOException e1) {
			logger.severe(e1.getMessage());
		}
	}
	
	public File getDatapackDirectory() {
		return datapackDirectory;
	}
	
	public int getTriesBeforeBan() {
		return triesBeforeBan;
	}
	
	public int getTimeBlockAfterBan() {
		return timeBlockAfterBan;
	}

	public boolean isEnabledAnyServer() {
		return enabledAnyServer;
	}
	
	public boolean isEnabledShowLicence() {
		return enabledShowLicence;
	}
	
	public boolean isEnabledAutoCreateAccount() {
		return enabledAutoCreateAccount;
	}

	public int getFastConnectionLimit() {
		return fastConnectionLimit;
	}
	
	public int getFastConnectionTime() {
		return fastConnectionTime;
	}
	
	public int getNormalConnectionTime() {
		return normalConnectionTime;
	}
	
	public int getMaxConnectionPerIP() {
		return maxConnectionPerIP;
	}
}

