/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.it.br.gameserver.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.it.br.Config;

/**
 * Cache of Compiled Scripts
 *
 * @author KenM
 */
public class CompiledScriptCache implements Serializable
{
	/**
	 * Version 1
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(CompiledScriptCache.class.getName());
	private Map<String, CompiledScriptHolder> _compiledScriptCache = new HashMap<>();
	private transient boolean _modified = false;

	public CompiledScript loadCompiledScript(ScriptEngine engine, File file) throws FileNotFoundException, ScriptException
	{
		int len = L2ScriptEngineManager.SCRIPT_FOLDER.getPath().length() + 1;
		String relativeName = file.getPath().substring(len);
		CompiledScriptHolder csh = _compiledScriptCache.get(relativeName);
		if (csh != null && csh.matches(file))
		{
			if (Config.DEBUG)
				LOG.fine("Reusing cached compiled script: " + file);
			return csh.getCompiledScript();
		}
		else
		{
			if (Config.DEBUG)
				LOG.info("Compiling script: " + file);
			Compilable eng = (Compilable) engine;
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			// TODO lock file
			CompiledScript cs = eng.compile(reader);
			if (cs instanceof Serializable)
				synchronized (_compiledScriptCache)
				{
					_compiledScriptCache.put(relativeName, new CompiledScriptHolder(cs, file));
					_modified = true;
				}
			return cs;
		}
	}

	public boolean isModified()
	{
		return _modified;
	}

	public void purge()
	{
		synchronized (_compiledScriptCache)
		{
			for (String path : _compiledScriptCache.keySet())
			{
				File file = new File(L2ScriptEngineManager.SCRIPT_FOLDER, path);
				if (!file.isFile())
				{
					_compiledScriptCache.remove(path);
					_modified = true;
				}
			}
		}
	}

	public void save() throws FileNotFoundException, IOException
	{
		synchronized (_compiledScriptCache)
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(L2ScriptEngineManager.SCRIPT_FOLDER, "CompiledScripts.cache")));
			oos.writeObject(this);
			_modified = false;
		}
	}
}
