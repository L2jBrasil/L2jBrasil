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

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.it.br.configuration.L2Properties;

/**
 *
 * @author  Alisson Oliveira
 */
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
		
		triesBeforeBan = properties.getInteger("loginTriesBeforeBan", 10);
		timeBlockAfterBan = properties.getInteger("blockTimeAfterBan", 600);
		
		enabledAnyServer  = properties.getBoolean("allowNewServers", false);
		enabledShowLicence = properties.getBoolean("showLicense", true);
		enabledAutoCreateAccount = properties.getBoolean("AllowAutoCreateAccount", true);		
		
		fastConnectionLimit = properties.getInteger("connectionFastLimit", 15);
		fastConnectionTime = properties.getInteger("connectionFastTime", 350);
		normalConnectionTime = properties.getInteger("ConnectionNormalTime", 700);
		maxConnectionPerIP = properties.getInteger("maxConnectionPerIP", 50);
		
		String datapackPath = properties.getString("datapackRootDirectory", ".");
		try {
			datapackDirectory = new File(datapackPath).getCanonicalFile();
		} catch (IOException e) {
			logger.severe("Error defining Datapack directory " + datapackPath);
			logger.severe(e.getMessage());
			try {
				datapackDirectory = new File(".").getCanonicalFile();
			} catch (IOException e1) {
				logger.severe("Error defining Datapack directory " + datapackPath);
				logger.severe(e1.getMessage());
			}
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

