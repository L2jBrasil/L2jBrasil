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
package com.it.br.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.it.br.configuration.settings.Settings;
import com.it.br.util.Util;

/**
 *
 * @author  Alisson Oliveira
 */
class LazyConfiguratorLoader {

	protected static final Logger logger = Logger.getLogger(LazyConfiguratorLoader.class.getName());
	
	private static final Map<Class<? extends Settings>, String> settingsClasses = new HashMap<>();
	
	protected static void load(L2Properties properties)	{
		settingsClasses.clear();
		for(Entry<Object, Object> entry : properties.entrySet()) {
			String className = (String) entry.getKey();
			String fileConfigurationPath = (String) entry.getValue();
			
			addSettingsClass(className, fileConfigurationPath);
		}
		logger.info("Settings classes loaded: " + settingsClasses.size());
	}

	
	protected static void addSettingsClass(String className, String fileConfigurationPath) {
		if(Util.isEmpty(className)) {
			return;
		}
		
		Class<? extends Settings> settings = createSettings(className);
		if(settings != null) { 
			settingsClasses.put(settings, fileConfigurationPath);
		}
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends Settings> createSettings(String className)	{
		try {
			Class<?> clazz = Class.forName(className);
			if(Settings.class.isAssignableFrom(clazz)) {
				return (Class<? extends Settings>) clazz;
			}
		} catch (ClassNotFoundException e)  {
			logger.severe("The class " + className + " was not found!");
			logger.severe(e.getMessage());
		}
		return null;
	}

	protected static <T extends Settings> T getSettings(Class<T> settingsClass) {
		if(!settingsClasses.containsKey(settingsClass)) {
			return null;
		}
		
		logger.info("Lazy Initialization : " +settingsClass.getName());
		String configurationFile = settingsClasses.get(settingsClass);
		return loadSettings(settingsClass, configurationFile);
	}

	private static <T extends Settings> T loadSettings(Class<T> settingsClass, String configurationFile) {
		try {
			/* 
			 * Deprecated since java 9
			 * TODO replace for settingsClass.getDeclaredContructor().newInstance()
			 */
			@SuppressWarnings("deprecation") 
			T settings = settingsClass.newInstance();
			
			L2Properties properties = Util.isEmpty(configurationFile) ? 
					new L2Properties() : L2Properties.load(configurationFile);

			settings.load(properties);
			return settings;
		} catch (InstantiationException | IllegalAccessException e) {
			logger.severe("Error loading Settings " + settingsClass);
			logger.severe(e.getMessage());
		}
		return null;
	}


}

