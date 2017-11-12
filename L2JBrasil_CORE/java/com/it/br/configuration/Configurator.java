package com.it.br.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.it.br.configuration.settings.Settings;

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

/**
 *
 * @author Alisson Oliveira
 */
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
	
	private Map<Class<? extends Settings>, Settings> settingsMap;
	
	private Configurator() { 
		settingsMap = new ConcurrentHashMap<>();
		load();
	}

	public static void load() {
		logger.info("Loading Configurations from " + propertiesFile);
		L2Properties properties = L2Properties.load(propertiesFile);
		if(properties.isEmpty()) {
			logger.warning("Configurations not found. No Settings has been loaded");
		} else {
			LazyConfiguratorLoader.load(properties);
		} 
	}
	
	public static void addSettingsClass(String className, String fileConfigurationPath) {
		LazyConfiguratorLoader.addSettingsClass(className, fileConfigurationPath);
	}
	
	public static <T extends Settings> T getSettings(Class<T> settingsClass) {
		return getSettings(settingsClass, false);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Settings> T getSettings(Class<T> settingsClass, boolean forceReload) {
		if(settingsClass == null) {
			logger.warning("Can't load settings from Null class");
			return null;
		}
		
		if(!forceReload && existsSettings(settingsClass)) {
			return (T) getInstance().settingsMap.get(settingsClass);
		} 
		
		T settings = LazyConfiguratorLoader.getSettings(settingsClass);
		if(settings == null) {
			logger.warning("Error loading Settings " + settingsClass.getName() + ". Configuration not found");
		} else {
			getInstance().settingsMap.put(settingsClass, settings);
		}
		return settings;
	}
	

	private static boolean existsSettings(Class<? extends Settings> settingsClass) {
		return getInstance().settingsMap.containsKey(settingsClass) &&  getInstance().settingsMap.get(settingsClass) != null;
	}

	public static void reloadAll() {
		logger.info("Reloading all settings");
		getInstance().settingsMap = new ConcurrentHashMap<>();
	}
	
	public static void reloadSettings(Class<? extends Settings>  settingsClass) {
		logger.info("Reloading settings " + settingsClass.getName());
		getInstance().settingsMap.remove(settingsClass);
	}
	
	private static Configurator getInstance() {
		if(configurator == null) {
			configurator = new Configurator();
		}
		return configurator;
	}

}
