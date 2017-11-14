package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;

public class MmoCoreSettings implements Settings{


	private int maxReadPerPass;
	private int maxSendPerPass;
	private int sleepTime;
	private int helperBufferCount;

	@Override
	public void load(L2Properties properties) {
		if(properties == null) { 
			return;
		}
		
		maxReadPerPass = properties.getInteger("MaxReadPerPass", 12);
		maxSendPerPass = properties.getInteger("MaxSendPerPass", 12);
		sleepTime = properties.getInteger("SleepTime", 20);
		helperBufferCount = properties.getInteger("MaxHelperBuffer", 20);
	}
	
	public int getMaxReadPerPass() {
		return maxReadPerPass;
	}
	
	public int getSleepTime() {
		return sleepTime;
	}
	
	public int getMaxSendPerPass() {
		return maxSendPerPass;
	}
	
	public int getHelperBufferCount() {
		return helperBufferCount;
	}
	

}

