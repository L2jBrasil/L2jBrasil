package com.it.br.configuration;

import com.it.br.configuration.settings.Settings;
import com.it.br.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class LazyConfiguratorLoader {

    protected static final Logger logger = LoggerFactory.getLogger(LazyConfiguratorLoader.class);

    private final Map<Class<? extends Settings>, String> settingsClasses = new HashMap<>();

    protected LazyConfiguratorLoader() {
    }

    protected void load(L2Properties properties) {
        settingsClasses.clear();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            String className = (String) entry.getKey();
            String fileConfigurationPath = (String) entry.getValue();

            addSettingsClass(className, fileConfigurationPath);
        }
        logger.info("Settings classes loaded: {}", settingsClasses.size());
    }

    protected void addSettingsClass(String className, String fileConfigurationPath) {
        if (Util.isNullOrEmpty(className)) {
            return;
        }

        Class<? extends Settings> settings = createSettings(className);
        if (settings != null) {
            settingsClasses.put(settings, fileConfigurationPath);
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Settings> createSettings(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (Settings.class.isAssignableFrom(clazz)) {
                return (Class<? extends Settings>) clazz;
            }
        } catch (ClassNotFoundException e) {
            logger.error("The class {} was not found", className);
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    protected <T extends Settings> T getSettings(Class<T> settingsClass) {
        String configurationFile = null;
        if (settingsClasses.containsKey(settingsClass)) {
            configurationFile = settingsClasses.get(settingsClass);
        }
        return loadSettings(settingsClass, configurationFile);
    }

    private static <T extends Settings> T loadSettings(Class<T> settingsClass, String configurationFile) {
        try {
            return newSettingsInstance(settingsClass, configurationFile);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Error loading Settings {}", settingsClass);
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private static <T extends Settings> T newSettingsInstance(Class<T> settingsClass, String configurationFile)
            throws InstantiationException, IllegalAccessException {
        /*
         * Deprecated since java 9
         * TODO replace for settingsClass.getDeclaredContructor().newInstance()
         */
        T settings = settingsClass.newInstance();
        L2Properties properties = Util.isNullOrEmpty(configurationFile) ? new L2Properties() : new L2Properties(configurationFile);

        logger.info("Lazy Initialization: {}", settingsClass.getName());
        settings.load(properties);
        return settings;
    }
}

