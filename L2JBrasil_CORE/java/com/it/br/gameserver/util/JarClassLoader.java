/*
 * This program is free software; you can redistribute it and/or modify
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

package com.it.br.gameserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This is a class loader for the dynamic extensions used by DynamicExtension class.
 * 
 * @version $Revision: $ $Date: $
 * @author galun
 */
public class JarClassLoader extends ClassLoader
{
	private static Logger _log = LoggerFactory.getLogger(JarClassLoader.class.getCanonicalName());
	HashSet<String> _jars = new HashSet<String>();

	public void addJarFile(String filename)
	{
		_jars.add(filename);
	}


	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException
	{
		try
		{
			byte[] b = loadClassData(name);
			return defineClass(name, b, 0, b.length);
		}
		catch(Exception e)
		{
			throw new ClassNotFoundException(name);
		}
	}

	private byte[] loadClassData(String name) throws IOException
	{
		byte[] classData = null;

		for(String jarFile : _jars)
		{
			try
			{
				File file = new File(jarFile);
				ZipFile zipFile = new ZipFile(file);
				String fileName = name.replace('.', '/') + ".class";
				ZipEntry entry = zipFile.getEntry(fileName);

				if(entry == null)
				{
					continue;
				}

				classData = new byte[(int) entry.getSize()];
				DataInputStream zipStream = new DataInputStream(zipFile.getInputStream(entry));
				zipStream.readFully(classData, 0, (int) entry.getSize());
				break;
			}
			catch(IOException e)
			{
				_log.warn( jarFile + ":" + e.toString(), e);
				continue;
			}
		}
		if(classData == null)
			throw new IOException("class not found in " + _jars);
		return classData;
	}
}
