/*
 * L2Properties create one in order to build support some protection!
 * Enclosing it kj2a specifically to support the protection of Free-Core.
 * The author of this product is unknown to me.
 * Everything I wrote importantly good luck to all.
 */
package com.it.br.protect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class L2Properties extends Properties
{
	private static final long serialVersionUID = -4599023842346938325L;
	private static final Log _log = LogFactory.getLog(L2Properties.class);
	private boolean _warn = false;

	public L2Properties()
	{}

	public L2Properties setLog(boolean warn)
	{
		_warn = warn;

		return this;
	}

	public L2Properties(String name) throws IOException
	{
		load(new FileInputStream(name));
	}

	public L2Properties(File file) throws IOException
	{
		load(new FileInputStream(file));
	}

	public L2Properties(InputStream inStream) throws IOException
	{
		load(inStream);
	}

	public L2Properties(Reader reader) throws IOException
	{
		load(reader);
	}

	public void load(String name) throws IOException
	{
		load(new FileInputStream(name));
	}

	public void load(File file) throws IOException
	{
		load(new FileInputStream(file));
	}


	@Override
	public synchronized void load(InputStream inStream) throws IOException
	{
		try
		{
			super.load(inStream);
		}
		finally
		{
			inStream.close();
		}
	}


	@Override
	public synchronized void load(Reader reader) throws IOException
	{
		try
		{
			super.load(reader);
		}
		finally
		{
			reader.close();
		}
	}


	@Override
	public String getProperty(String key)
	{
		String property = super.getProperty(key);

		if (property == null)
		{
			if (_warn)
			{
				_log.warn("L2Properties: Missing property for key - " + key);
			}

			return null;
		}

		return property.trim();
	}


	@Override
	public String getProperty(String key, String defaultValue)
	{
		String property = super.getProperty(key, defaultValue);

		if (property == null)
		{
			if (_warn)
			{
				_log.warn("L2Properties: Missing defaultValue for key - " + key);
			}

			return null;
		}

		return property.trim();
	}
}