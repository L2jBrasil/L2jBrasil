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

import java.util.HashSet;
import java.util.Set;

import com.it.br.gameserver.datatables.xml.NpcTable;
import sun.text.resources.cldr.zh.FormatData_zh_Hans_SG;

/**
 *
 * @author  -Wooden-
 */
public class AiPlugingParameters
{
	//here is order of priority
	private Set<String> _npcClassTypes;
	@SuppressWarnings("rawtypes")
	private Set<Class> _npcL2jClasses;
	private Set<String> _aiTypes;
	private Set<Integer> _npcIDs;
	private boolean _converted;
	private AiPlugingParameters _but;

	@SuppressWarnings("rawtypes")
	public AiPlugingParameters(Set<String> npcClassTypes, Set<Class> npcL2jClasses, Set<String> aiTypes, Set<Integer> npcIDs, AiPlugingParameters but)
	{
		_npcClassTypes = npcClassTypes;
		_npcL2jClasses = npcL2jClasses;
		_aiTypes = aiTypes;
		_npcIDs = npcIDs;
		_but = but;
		if(_npcIDs == null)
			_npcIDs = new HashSet<>();
	}
	@SuppressWarnings("rawtypes")
	public void convertToIDs()
	{
		if(_but != null && !_but.isEmpty())
			_but.convertToIDs();

		if(_npcClassTypes != null)
			for(String classType : _npcClassTypes)
			{
				_npcIDs.addAll(NpcTable.getInstance().getAllNpcOfClassType(classType));
			}
		if(_npcL2jClasses != null)
			for(Class l2jClass : _npcL2jClasses)
			{
				_npcIDs.addAll(NpcTable.getInstance().getAllNpcOfL2jClass(l2jClass));
			}

		if(_aiTypes != null)
			for(String aiType : _aiTypes)
			{
				_npcIDs.addAll(NpcTable.getInstance().getAllNpcOfAiType(aiType));
			}

		if(_but != null && !_but.isEmpty())
			removeIDs(_but.getIDs());

		_converted = true;
	}

	public boolean isEmpty()
	{
		return (_npcIDs.isEmpty() && _aiTypes.isEmpty() && _npcL2jClasses.isEmpty() && _npcClassTypes.isEmpty());
	}

	/**
	 * If a AiPlugingParameters was converted to IDs its other parameter sets might not be accurate
	 * @return true if AiPlugingParameters where converted to IDs
	 */
	public boolean isConverted()
	{
		return _converted;
	}

	public void removeIDs(Set<Integer> ids)
	{
		_npcIDs.removeAll(ids);
	}

	public Set<Integer> getIDs()
	{
		return _npcIDs;
	}
	/**
	 * @param id
	 * @return
	 */
	public boolean contains(int id)
	{
		if(!_converted)
			throw new IllegalStateException("Can not call 'contain' method on a non-converted AiPluginParameters");
		if(_but == null)
			return _npcIDs.contains(id);
		return _npcIDs.contains(id) && !_but.contains(id);
	}

	public boolean equals(AiPlugingParameters pparams)
	{
		if(!_converted)
			throw new IllegalStateException("Can not call 'equals' method on a non-converted AiPluginParameters");
		return (_npcIDs.containsAll(pparams.getIDs()) && pparams.getIDs().containsAll(_npcIDs));
	}
}
