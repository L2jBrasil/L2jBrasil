package com.it.br.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.it.br.configuration.settings.Settings;

public class Configurator {
	
	/*
	 * TODO implement auto reload. 
	 * 	Decide on: 
	 * 		reload after some time interval
	 * 		left java decide through WeakReferences 
	 */
	private static final Logger logger = Logger.getLogger(Configurator.class.getName());
	private static final String propertiesFile = "./config/configurator.properties";
	private static Configurator configurator;
	private LazyConfiguratorLoader loader;
	
	private Map<Class<? extends Settings>, Settings> settingsMap;
	
	private Configurator() { 
		settingsMap = new ConcurrentHashMap<>();
		loader = new LazyConfiguratorLoader();
		load();
	}

	private void load() {
		logger.info("Loading Configurations from " + propertiesFile);
		L2Properties properties = new L2Properties(propertiesFile);
		if(properties.isEmpty()) {
			logger.severe("Configurations not found. No Settings has been loaded");
		} else {
			loader.load(properties);
		} 
	}
	
	public void addSettingsClass(String className, String fileConfigurationPath) {
		loader.addSettingsClass(className, fileConfigurationPath);
	}
	
	public static <T extends Settings> T getSettings(Class<T> settingsClass) {
		return getSettings(settingsClass, false);
	}
	
	public static <T extends Settings> T getSettings(Class<T> settingsClass, boolean forceReload) {
		if(settingsClass == null) {
			logger.warning("Can't load settings from Null class");
			return null;
		}
		
		Configurator instance = getInstance();
		
		if(!forceReload && instance.hasSettings(settingsClass)) {
			return instance.get(settingsClass);
		}
		
		return instance.getFromLoader(settingsClass);
		
	}
	
	private <T extends Settings> T getFromLoader(Class<T> settingsClass) {
		T settings = loader.getSettings(settingsClass);
		if(settings != null) {
			settingsMap.put(settingsClass, settings);
		}
		return settings;
	}

	@SuppressWarnings("unchecked")
	private <T extends Settings> T get(Class<T> settingsClass){
		return (T) settingsMap.get(settingsClass);
	}

	private  boolean hasSettings(Class<? extends Settings> settingsClass) {
		return settingsMap.containsKey(settingsClass) &&  settingsMap.get(settingsClass) != null;
	}

	public static void reloadAll() {
		logger.info("Reloading all settings");
		getInstance().reload();
	}
	
	private void reload() {
		settingsMap.clear();
		load();
	}

	public static void reloadSettings(Class<? extends Settings>  settingsClass) {
		logger.info("Reloading settings " + settingsClass.getName());
		getInstance().removeSettings(settingsClass);
	}
	
	
	private void removeSettings(Class<? extends Settings> settingsClass) {
		settingsMap.remove(settingsClass);
	}

	public static Configurator getInstance() {
		if(configurator == null) {
			configurator = new Configurator();
		}
		return configurator;
	}

}
