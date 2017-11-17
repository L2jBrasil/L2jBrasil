package com.it.br.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.it.br.configuration.settings.Settings;
import com.it.br.util.Util;

class LazyConfiguratorLoader {

	protected static final Logger logger = Logger.getLogger(LazyConfiguratorLoader.class.getName());
	
	private final Map<Class<? extends Settings>, String> settingsClasses = new HashMap<>();

	protected LazyConfiguratorLoader() { }
	
	protected void load(L2Properties properties)	{
		settingsClasses.clear();
		for(Entry<Object, Object> entry : properties.entrySet()) {
			String className = (String) entry.getKey();
			String fileConfigurationPath = (String) entry.getValue();
			
			addSettingsClass(className, fileConfigurationPath);
		}
		logger.info("Settings classes loaded: " + settingsClasses.size());
	}
	
	protected  void addSettingsClass(String className, String fileConfigurationPath) {
		if(Util.isNullOrEmpty(className)) {
			return;
		}
		
		Class<? extends Settings> settings = createSettings(className);
		if(settings != null) { 
			settingsClasses.put(settings, fileConfigurationPath);
		}
	}

	@SuppressWarnings("unchecked")
	private  Class<? extends Settings> createSettings(String className)	{
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

	protected  <T extends Settings> T getSettings(Class<T> settingsClass) {
		String configurationFile = null;
		if(settingsClasses.containsKey(settingsClass)) {
			configurationFile = settingsClasses.get(settingsClass);
		}
		return loadSettings(settingsClass, configurationFile);
	}

	private  <T extends Settings> T loadSettings(Class<T> settingsClass, String configurationFile) {
		try {
			return newSettingsInstance(settingsClass, configurationFile);
		} catch (InstantiationException | IllegalAccessException e) {
			logger.severe("Error loading Settings " + settingsClass);
			logger.severe(e.getMessage());
		}
		return null;
	}

	private <T extends Settings> T newSettingsInstance(Class<T> settingsClass, String configurationFile)
			throws InstantiationException, IllegalAccessException {
		/* 
		 * Deprecated since java 9
		 * TODO replace for settingsClass.getDeclaredContructor().newInstance()
		 */
		@SuppressWarnings("deprecation") 
		T settings = settingsClass.newInstance();
		L2Properties properties = Util.isNullOrEmpty(configurationFile) ?  new L2Properties() : new L2Properties(configurationFile);

		logger.info("Lazy Initialization : " +settingsClass.getName());
		settings.load(properties);
		return settings;
	}
}

