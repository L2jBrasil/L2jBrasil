package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ServerSettings implements Settings{

	private static final Logger logger = LoggerFactory.getLogger(ServerSettings.class);
	
	private int serverId;
	private boolean acceptAlternativeIdEnabled;
	
	private File datapackDirectory;

	private String characterNameTemplate;
	private String petNameTemplate;
	private int characterMaxCount;
	private int playerOnlineMaxCount;

	private int maxUnknownPacket;
	private boolean debugPacketEnabled;
	private int minProtocol;
	private int maxProtocol;
	
	@Override
	public void load(L2Properties properties) {
		if(properties == null) { 
			return;
		}
		
		serverId = properties.getInteger("RequestServerID", 1);
		acceptAlternativeIdEnabled = properties.getBoolean("AcceptAlternativeId", true);
		
		characterNameTemplate = properties.getString("CharNameTemplate", ".*");
		petNameTemplate = properties.getString("PNameTemplate", ".*");
		characterMaxCount = properties.getInteger("CharMaxCount", 7);
		playerOnlineMaxCount = properties.getInteger("MaxPlayerOnline", 100);
		
		maxUnknownPacket = properties.getInteger("MaxUnknownPacket", 5);
		debugPacketEnabled = properties.getBoolean("PacketDebug", false);
		minProtocol = properties.getInteger("MinProtocolRevision", 740);
		maxProtocol = properties.getInteger("MaxProtocolRevision", 746);
		
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
	
	public int getServerId() {
		return serverId;
	}
	
	public boolean isAcceptAlternativeIdEnabled() {
		return acceptAlternativeIdEnabled;
	}
	
	public File getDatapackDirectory() {
		return datapackDirectory;
	}
	
	public String getCharacterNameTemplate() {
		return characterNameTemplate;
	}
	
	public String getPetNameTemplate()
	{
		return petNameTemplate;
	}
	
	public int getCharacterMaxCount() {
		return characterMaxCount;
	}
	
	public int getPlayerOnlineMaxCount() {
		return playerOnlineMaxCount;
	}
	
	public void setPlayerOnlineMaxCount(int count) {
		this.playerOnlineMaxCount = count;
	}
	
	public boolean isDebugPacketEnabled() {
		return debugPacketEnabled;
	}

	public int getMaxUnknownPacket() {
		return maxUnknownPacket;
	}
	
	public void setMaxUnknownPacket(int count) {
		this.maxUnknownPacket = count;
	}
	
	public int getMinProtocol() {
		return minProtocol;
	}

	public int getMaxProtocol() {
		return maxProtocol;
	}
}

