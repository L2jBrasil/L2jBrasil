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
package com.it.br.gameserver.ai2;

import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.ai2.AiInstance.QueueEventRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;

/**
 * This class will load all the AI's and retain all of them.
 * It is a singleton
 * @author -Wooden-
 *
 */
public class AiManager
{
	protected static final Logger _log = LoggerFactory.getLogger( AiManager.class);
	private static AiManager _instance;
	private List<AiInstance> _aiList;
	private Map<Integer, AiInstance> _aiMap;
	private ThreadPoolManager _tpm;
	private Map<String,String> _paramcache;

	public static AiManager getInstance()
	{
		if(_instance == null)
		{
			_instance = new AiManager();
		}
		return _instance;
	}

	private AiManager()
	{
		_aiList = new ArrayList<>();
		_aiMap = new HashMap<>();
		_tpm = ThreadPoolManager.getInstance();
		_paramcache = new HashMap<>();
		load();
	}

	@SuppressWarnings("rawtypes")
	public void load()
	{
		try
		{
			//TODO redo all the SpecificAiManger loading it's completely messed up:
			@SuppressWarnings("unused")
			JarFile jar = new JarFile("./l2jserver.jar");
			URL url = Class.class.getResource("/net/sf/l2j/gameserver/ai/managers");
			//jar.getJarEntry("/net/sf/l2j/gameserver/ai/managers").get;
			if(url == null)
			{
				_log.error("Could not open the ai managers folder. No ai will be loaded!");
				return;
			}
			File directory = new File(url.getFile());
			for(String file : directory.list())
			{
				if(file.endsWith(".class"))
				{
					try
					{
						Class managerClass = Class.forName("com.it.br.gameserver.ai.managers."+file.substring(0, file.length() - 6));
						Object managerObject = managerClass.newInstance();
						if(!(managerObject instanceof ISpecificAiManager))
						{
							_log.info("A class that was not a ISpecificAiManager was found in the ai managers folder.");
							continue;
						}
						ISpecificAiManager managerInstance = (ISpecificAiManager)managerObject;
						for(EventHandler handler : managerInstance.getEventHandlers())
						{
							AiPlugingParameters pparams = handler.getPlugingParameters();
							pparams.convertToIDs();
							boolean perfectMatch = false;
							// let's check if any previously created AiInstance is allready used for the NPC concerned by this handler
							List<Intersection> intersections = new ArrayList<>();
							for(AiInstance ai : _aiList)
							{
								if(ai.getPluginingParamaters().equals(pparams))
								{
									ai.addHandler(handler);
									perfectMatch = true;
									break;
								}
								Intersection intersection = new Intersection(ai);
								for(int id : pparams.getIDs())
								{
									if(ai.getPluginingParamaters().contains(id))
									{
										//intersection with this AI
										intersection.ids.add(id);
									}
								}
								if(!intersection.isEmpty())
									intersections.add(intersection);
							}
							if(perfectMatch)
								continue;
							for(Intersection i : intersections)
							{
								//remove secant ids on both AiInstances
								pparams.removeIDs(i.ids);
								i.ai.getPluginingParamaters().removeIDs(i.ids); //TODO if this is extracted to a more general purpose method, dont forget to update linkages to AiIntances
								//create a new instance with the secant ids that will inherit all the handlers from the secant Ai
								AiPlugingParameters newAiPparams = new AiPlugingParameters(null,null,null,i.ids,null);
								AiInstance newAi = new AiInstance(i.ai,newAiPparams);
								newAi.addHandler(handler);
								_aiList.add(newAi);
							}
							if(pparams.isEmpty())
								continue;
							// create a new instance with the remaining ids
							AiInstance newAi = new AiInstance(pparams);
							newAi.addHandler(handler);
							_aiList.add(newAi);
						}
					}
					catch(ClassCastException e)
					{
						e.printStackTrace();
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
					catch (IllegalArgumentException e)
					{
						e.printStackTrace();
					}
					catch (SecurityException e)
					{
						e.printStackTrace();
					}
					catch (InstantiationException e)
					{
						e.printStackTrace();
					}
					catch (IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// build a mighty map
		for(AiInstance ai : _aiList)
		{
			for(Integer i : ai.getHandledNPCIds())
			{
				_aiMap.put(i, ai);
			}
		}
	}

	public void executeEventHandler(QueueEventRunner runner)
	{
		_tpm.executeAi(runner);
	}

	/**
	 * @param instance
	 */
	public void addAiInstance(AiInstance instance)
	{
		_aiList.add(instance);
	}

	/**
	 * @param npcId
	 * @return
	 */
	public AiInstance getAiForNPCId(int npcId)
	{
		return _aiMap.get(npcId);
	}

	public String getParameter(String who, String paramsType, String param1, String param2)
	{
		String key = who+":"+paramsType+":"+param1+":"+param2;
		String cacheResult = _paramcache.get(key);
		if(cacheResult != null)
			return cacheResult;
		String result = null;
		// get from SQL
		_paramcache.put(key, result);
		return null;
	}

	private class Intersection
	{
		public AiInstance ai;
		public Set<Integer> ids;

		public Intersection(AiInstance instance)
		{
			ai = instance;
			ids = new HashSet<>();
		}

		public boolean isEmpty()
		{
			return ids.isEmpty();
		}
	}
}
