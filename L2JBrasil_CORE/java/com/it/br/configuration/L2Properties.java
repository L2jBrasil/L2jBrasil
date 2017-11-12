/*
 * L2Properties create one in order to build support some protection!
 * Enclosing it kj2a specifically to support the protection of Free-Core.
 * The author of this product is unknown to me.
 * Everything I wrote importantly good luck to all.
 */
package com.it.br.configuration;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.it.br.util.Util;

public final class L2Properties extends Properties
{
	private static final long serialVersionUID = -4599023842346938325L;
	private static final Log _log = LogFactory.getLog(L2Properties.class);
	
	public static L2Properties load(String filePath) {
		L2Properties properties = new L2Properties();
		try(FileReader reader = new FileReader(filePath)) {
			properties.load(reader);
		} catch (IOException e) {
			_log.error(e);
		}
		
		return properties;
	}

	public String getString(String key, String defaultValue) {
		return getProperty(key, defaultValue);
	}

	
	public int getInteger(String key, int defaultValue){
		try {
			return Integer.parseInt(getProperty(key));
		} catch (Exception e) {
			_log.warn("Error getting property " + key + ": " + e.getMessage());
		}
		return defaultValue;
	}

	public boolean getBoolean(String key, boolean defaultValue)	{
		String value = getProperty(key);
		if(Util.isEmpty(value)) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}
}