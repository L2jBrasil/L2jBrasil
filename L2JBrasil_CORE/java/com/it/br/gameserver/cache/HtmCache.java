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
package com.it.br.gameserver.cache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.configuration.Configurator;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.util.Util;

/**
 * @author Layane
 *
 */
public class HtmCache
{
    private static Logger _log = Logger.getLogger(HtmCache.class.getName());
    private static HtmCache _instance;

    private Map<Integer, String> _cache;

    private int _loadedFiles;
    private long _bytesBuffLen;

    public static HtmCache getInstance()
    {
        if (_instance == null)
            _instance = new HtmCache();

        return _instance;
    }

    public HtmCache()
    {
        _cache = new HashMap<>();
        reload();
    }

    public void reload()
    {
    	
        reload(Configurator.getSettings(ServerSettings.class).getDatapackDirectory());
    }

    public void reload(File f)
    {
        if (!Config.LAZY_CACHE)
        {
        	_log.info("Html cache start...");
            parseDir(f);
            _log.info("Cache[HTML]: " + String.format("%.3f",getMemoryUsage())  + " megabytes on " + getLoadedFiles() + " files loaded");
        }
        else
        {
        	_cache.clear();
        	_loadedFiles = 0;
        	_bytesBuffLen = 0;
            _log.info("Cache[HTML]: Running lazy cache");
        }
    }

    public void reloadPath(File f)
    {
    	parseDir(f);
    	_log.info("Cache[HTML]: Reloaded specified path.");
    }

    public double getMemoryUsage()
    {
    	return ((float)_bytesBuffLen/1048576);
    }

    public int getLoadedFiles()
    {
        return _loadedFiles;
    }

    class HtmFilter implements FileFilter
    {

		public boolean accept(File file)
        {
            if (!file.isDirectory())
            {
                return (file.getName().endsWith(".htm") || file.getName().endsWith(".html"));
            }
            return true;
        }
    }

    private void parseDir(File dir)
    {
        FileFilter filter = new HtmFilter();
        File[] files = dir.listFiles(filter);

        for (File file : files)
        {
            if (!file.isDirectory())
                loadFile(file);
            else
                parseDir(file);
        }
    }

    public String loadFile(File file)
    {
        HtmFilter filter = new HtmFilter();

        if (file.exists() && filter.accept(file) && !file.isDirectory())
        {
            String content;
            FileInputStream fis = null;

            try
            {
                fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                int bytes = bis.available();
                byte[] raw = new byte[bytes];

                bis.read(raw);
                content = new String(raw, "UTF-8");
                content = content.replaceAll("\r\n","\n");

                ServerSettings serverSettings = Configurator.getSettings(ServerSettings.class);
                String relpath = Util.getRelativePath(serverSettings.getDatapackDirectory(),file);
                int hashcode = relpath.hashCode();

                String oldContent = _cache.get(hashcode);

                if (oldContent == null)
                {
                    _bytesBuffLen += bytes;
                    _loadedFiles++;
                }
                else
                {
                    _bytesBuffLen = _bytesBuffLen - oldContent.length() + bytes;
                }

                _cache.put(hashcode,content);

                return content;
            }
            catch (Exception e)
            {
                _log.warning("problem with htm file " + e);
            }
            finally
            {
                try { fis.close(); } catch (Exception e1) { }
            }
        }

        return null;
    }

    public String getHtmForce(String path)
    {
        String content = getHtm(path);

        if (content == null)
        {
            content = "<html><body>My text is missing:<br>" + path + "</body></html>";
            _log.warning("Cache[HTML]: Missing HTML page: " + path);
        }

        return content;
    }

    public String getHtm(String path)
    {
        String content = _cache.get(path.hashCode());

        if (Config.LAZY_CACHE && content == null) {
        	ServerSettings serverSettings = Configurator.getSettings(ServerSettings.class);
        	content = loadFile(new File(serverSettings.getDatapackDirectory(), path));
        }

        return content;
    }
    
	/**
	 * @param prefix
	 * @param path
	 * @return
	 */
	public String getHtm(String prefix, String path)
	{
		String newPath = null;
		String content;
		if (prefix != null && !prefix.isEmpty())
		{
			newPath = prefix + path;
			content = getHtm(newPath);
			if (content != null)
				return content;
		}

		content = getHtm(path);
		if (content != null && newPath != null)
			_cache.put(newPath.hashCode(), content);

		return content;
	}

    public boolean contains(String path)
    {
        return _cache.containsKey(path.hashCode());
    }

    /**
     * Check if an HTM exists and can be loaded
     * @param
     * path The path to the HTM
     * */
    public boolean isLoadable(String path)
    {
    	File file = new File(path);
        HtmFilter filter = new HtmFilter();

        if (file.exists() && filter.accept(file) && !file.isDirectory())
	        return true;

    	return false;
    }
}
