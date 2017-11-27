/*
 * L2Properties create one in order to build support some protection!
 * Enclosing it kj2a specifically to support the protection of Free-Core.
 * The author of this product is unknown to me.
 * Everything I wrote importantly good luck to all.
 */
package com.it.br.configuration;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.it.br.util.Util;

public final class L2Properties extends Properties
{
	private static final long serialVersionUID = -4599023842346938325L;
	private static final Log _log = LogFactory.getLog(L2Properties.class);

	
	public L2Properties(String filePath) {
		try(FileReader reader = new FileReader(filePath)) {
			load(reader);
		} catch (IOException e) {
			_log.error(e);
		}
	}
	
	public L2Properties() { }

	public String getString(String key, String defaultValue) {
		return getProperty(key, defaultValue);
	}
	
	public int getInteger(String key, int defaultValue){
		return getInteger(key, 10, defaultValue);
	}
	
	public int getInteger(String key, int radix, int defaultValue) {
		try {
			return Integer.parseInt(getProperty(key), radix);
		} catch (Exception e) {
			_log.warn("Error getting property " + key + ": " + e.getMessage());
		}
		return defaultValue;
	}

	public boolean getBoolean(String key, boolean defaultValue)	{
		String value = getProperty(key);
		if(Util.isNullOrEmpty(value)) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	public List<String> getStringList(String key, String defaultValue, String delim) {
		String[] value = getProperty(key, defaultValue).split(delim);
		return Arrays.asList(value);
	}
	
}