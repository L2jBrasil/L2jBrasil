package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


public class LoginSettings implements Settings{

	private static final Logger logger = LoggerFactory.getLogger(LoginSettings.class);

	private File datapackDirectory;
	private int triesBeforeBan;
	private int timeBlockAfterBan;
	private boolean newServerEnabled;
	private boolean showLicense;
	private boolean autoCreateAccountEnabled;
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
		
		newServerEnabled  = properties.getBoolean("AllowNewServers", false);
		showLicense = properties.getBoolean("ShowLicense", true);
		autoCreateAccountEnabled = properties.getBoolean("AllowAutoCreateAccount", true);		
		
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
			logger.warn("Error defining Datapack directory " + datapackPath);
			logger.warn(e.getMessage());
			setDefaultDatapackDirectory();
		}
	}

	private void setDefaultDatapackDirectory() {
		try {
			datapackDirectory = new File(".").getCanonicalFile();
			logger.info("Setting default datapack directory: " + datapackDirectory.getAbsolutePath());
		} catch (IOException e1) {
			logger.error(e1.getMessage());
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

	public boolean isNewServerEnabled() {
		return newServerEnabled;
	}
	
	public boolean showLicense() {
		return showLicense;
	}
	
	public boolean isAutoCreateAccountEnabled() {
		return autoCreateAccountEnabled;
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

