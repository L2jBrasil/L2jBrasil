package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;

public interface Settings {
	
	/*
	 * Some settings can be changed directly from the Game. So this should be saved back to configuration file.
	 * 
	 * TODO implement strategy to save updated settings back to configuration file.
	 * 
	 */
	
	void load(L2Properties properties);

}
