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
public class ServerSettings implements Settings{

	private static final Logger logger = Logger.getLogger(ServerSettings.class.getName());
	
	private int serverId;
	private boolean acceptAlternativeId;
	
	private File datapackDirectory;

	private String characterNameTemplate;
	private String petNameTemplate;
	private int characterMaxCount;
	private int playerOnlineMaxCount;

	private int maxUnknownPacket;
	private boolean debugPacket;
	private int minProtocol;
	private int maxProtocol;
	
	@Override
	public void load(L2Properties properties) {
		if(properties == null) { 
			return;
		}
		
		serverId = properties.getInteger("server.id", 1);
		acceptAlternativeId = properties.getBoolean("accept.alternative.id", true);
		
		characterNameTemplate = properties.getString("character.name.template", ".*");
		petNameTemplate = properties.getString("pet.name.template", ".*");
		characterMaxCount = properties.getInteger("character.count.max", 7);
		playerOnlineMaxCount = properties.getInteger("player.online.count.max", 100);
		
		maxUnknownPacket = properties.getInteger("packet.unknown.max", 5);
		debugPacket = properties.getBoolean("packet.debug", false);
		minProtocol = properties.getInteger("protocol.min", 740);
		maxProtocol = properties.getInteger("protocol.max", 746);
		
		String datapackPath = properties.getString("datapack.directory", ".");
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
	
	public int getServerId() {
		return serverId;
	}
	
	public boolean isAcceptAlternativeId() {
		return acceptAlternativeId;
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
	
	public boolean isDebugPacket() {
		return debugPacket;
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

