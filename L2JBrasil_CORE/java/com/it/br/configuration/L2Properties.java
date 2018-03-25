/*
 * L2Properties create one in order to build support some protection!
 * Enclosing it kj2a specifically to support the protection of Free-Core.
 * The author of this product is unknown to me.
 * Everything I wrote importantly good luck to all.
 */
package com.it.br.configuration;

import com.it.br.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class L2Properties extends Properties
{
	private static final long serialVersionUID = -4599023842346938325L;
	private static final Logger _log = LoggerFactory.getLogger(L2Properties.class);

	
	public L2Properties(String filePath) {
		try(FileReader reader = new FileReader(filePath)) {
			load(reader);
		} catch (IOException e) {
			_log.error(e.getMessage(), e);
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
			_log.warn("Error getting property {} : {} ", key, e.getMessage());
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

	public List<String> getStringList(String key, String defaultValue, String delimiter) {
		String[] values = getProperty(key, defaultValue).split(delimiter);
		return Stream.of(values).filter(Util::isNotEmpty).collect(Collectors.toList());
	}

	public Map<Integer, Integer> getIntegerMap(String key, String entryDelimiter, String valueDelimiter) {
		String[] values = getProperty(key, "").split(entryDelimiter);
		Map<Integer, Integer> map = new HashMap<>();
		
		Stream.of(values).filter(Util::isNotEmpty).forEach(v -> 
			putInMap(key, valueDelimiter, map, v));
		
		return map;
	}

	private static void putInMap(String key, String valueDelimiter, Map<Integer, Integer> map, String entry) {
		try {
			String[] value = entry.split(valueDelimiter);
			int mapKey = Integer.parseInt(value[0].trim());
			int mapValue = Integer.parseInt(value[1].trim());
			map.put(mapKey, mapValue);
		} catch (Exception e) {
			_log.warn("Error getting property {} on entry {}: {}", key, entry, e.getMessage());

		}
	}

	public List<Integer> getIntegerList(String key, String delimiter) {
		String[] values = getProperty(key).split(delimiter);
		List<Integer> list = new ArrayList<>(values.length);
		Stream.of(values).filter(Util::isNotEmpty).forEach( v -> {
			try {
				int value = Integer.parseInt(v.trim());
				list.add(value);
			} catch (Exception e) {
				_log.warn("Error getting property " + key + " on value " + v +" : " + e.getMessage());
			}
		});
		return list;
	}

	public double getDouble(String key, double defaultValue) {
		try {
			return Double.parseDouble(getProperty(key));
		} catch (Exception e) {
			_log.warn("Error getting property " + key + ": " + e.getMessage());
		}
		return defaultValue;
	}

	public float getFloat(String key, float defaultValue) {
		try {
			return Float.parseFloat(getProperty(key));
		} catch (Exception e) {
			_log.warn("Error getting property " + key + ": " + e.getMessage());
		}
		return defaultValue;
	}

	public int[] getIntegerArray(String key, String delimiter) {
		String[] values = getProperty(key).split(",");
		int[] array = new int[values.length];
		int index = 0;
		for (String v : values) {
			
			if(Util.isNullOrEmpty(v)) {
				continue;
			}
			
			try {
				int value = Integer.parseInt(v);
				array[index++] = value;
			} catch (Exception e) {
				_log.warn("Error getting property " + key + ": " + e.getMessage());
			}
		}
		return array;
	}

	public long getLong(String key, long defaultValue) {
		try {
			return Long.parseLong(getProperty(key));
		} catch (Exception e) {
			_log.warn("Error getting property " + key + ": " + e.getMessage());
		}
		return defaultValue;
	}

}