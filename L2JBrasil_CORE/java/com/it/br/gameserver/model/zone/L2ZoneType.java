/* This program is free software; you can redistribute it and/or modify
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
package com.it.br.gameserver.model.zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Node;

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.network.serverpackets.L2GameServerPacket;

/**
 * Abstract base class for any zone type
 * Handles basic operations
 *
 * @author  durgus
 */
public abstract class L2ZoneType
{
	protected L2ZoneForm _zone;
	protected Map<Integer, L2Character> _characterList;

	/** Parameters to affect specific characters */
	private boolean _checkAffected;

	private int _minLvl;
	private int _maxLvl;
	private int[] _race;
	private int[] _class;
	private char _classType;
	private Map<Quest.QuestEventType, List<Quest>> _questEvents;

	protected L2ZoneType()
	{
		_characterList = new ConcurrentHashMap<>();

		_checkAffected = false;

		_minLvl = 0;
		_maxLvl = 0xFF;

		_classType = 0;

		_race = null;
		_class = null;
	}

	/**
	 * Setup new parameters for this zone
	 * @param value
	 */
	public void setParameter(String name, String value)
	{
		_checkAffected = true;

		// Minimum leve
		if (name.equals("affectedLvlMin"))
		{
			_minLvl = Integer.parseInt(value);
		}
		// Maximum level
		else if (name.equals("affectedLvlMax"))
		{
			_maxLvl = Integer.parseInt(value);
		}
		// Affected Races
		else if (name.equals("affectedRace"))
		{
			// Create a new array holding the affected race
			if (_race == null)
			{
				_race = new int[1];
				_race[0] = Integer.parseInt(value);
			}
			else
			{
				int[] temp = new int[_race.length+1];

				int i=0;
				for (; i < _race.length; i++)
					temp[i] = _race[i];

				temp[i] = Integer.parseInt(value);

				_race = temp;
			}
		}
		// Affected classes
		else if (name.equals("affectedClassId"))
		{
			// Create a new array holding the affected classIds
			if (_class == null)
			{
				_class = new int[1];
				_class[0] = Integer.parseInt(value);
			}
			else
			{
				int[] temp = new int[_class.length+1];

				int i=0;
				for (; i < _class.length; i++)
					temp[i] = _class[i];

				temp[i] = Integer.parseInt(value);

				_class = temp;
			}
		}
		// Affected class type
		else if (name.equals("affectedClassType"))
		{
			if (value.equals("Fighter"))
			{
				_classType = 1;
			}
			else
			{
				_classType = 2;
			}
		}
	}
	
	public void setSpawns(Node node){}

	/**
	 * Checks if the given character is affected by this zone
	 * @param character
	 * @return
	 */
	private boolean isAffected(L2Character character)
	{
		// Check lvl
		if (character.getLevel() < _minLvl || character.getLevel() > _maxLvl) return false;

		if (character instanceof L2PcInstance)
		{
			// Check class type
			if (_classType != 0)
			{
				if (((L2PcInstance)character).isMageClass())
				{
					if (_classType == 1) return false;
				}
				else if (_classType == 2) return false;
			}

			// Check race
			if (_race != null)
			{
				boolean ok = false;

				for (int i=0; i < _race.length; i++)
				{
					if (((L2PcInstance)character).getRace().ordinal() == _race[i])
					{
						ok = true;
						break;
					}
				}

				if (!ok) return false;
			}

			// Check class
			if (_class != null)
			{
				boolean ok = false;

				for (int i=0; i < _class.length; i++)
				{
					if (((L2PcInstance)character).getClassId().ordinal() == _class[i])
					{
						ok = true;
						break;
					}
				}

				if (!ok) return false;
			}
		}
		return true;
	}

	/**
	 * Set the zone for this L2ZoneType Instance
	 * @param zone
	 */
	public void setZone(L2ZoneForm zone)
	{
		_zone = zone;
	}

	/**
	 * Returns this zones zone form
	 * @return
	 */
	public L2ZoneForm getZone()
	{
		return _zone;
	}

	/**
	 * Checks if the given coordinates are within the zone
	 * @param x
	 * @param y
	 * @param z
	 */
	public boolean isInsideZone(int x, int y, int z)
	{
		return _zone.isInsideZone(x, y, z);
	}

	/**
	 * Checks if the given obejct is inside the zone.
	 *
	 * @param object
	 */
	public boolean isInsideZone(L2Object object)
	{
		return _zone.isInsideZone(object.getX(), object.getY(), object.getZ());
	}

	public double getDistanceToZone(int x, int y)
	{
		return _zone.getDistanceToZone(x, y);
	}

	public double getDistanceToZone(L2Object object)
	{
		return _zone.getDistanceToZone(object.getX(), object.getY());
	}

	public void revalidateInZone(L2Character character)
	{
		// If the character cant be affected by this zone return
		if (_checkAffected)
			if (!isAffected(character)) 
				return;
		
		// If the object is inside the zone...
		if (isInsideZone(character.getX(), character.getY(), character.getZ()))
		{
			// Was the character not yet inside this zone?
			if (!_characterList.containsKey(character.getObjectId()))
			{
				List<Quest> quests = this.getQuestByEvent(Quest.QuestEventType.ON_ENTER_ZONE);
				if (quests != null)
					for (Quest quest : quests)
						quest.notifyEnterZone(character, this);
				_characterList.put(character.getObjectId(), character);
				onEnter(character);
			}
		}
		else
		{
			// Was the character inside this zone?
			if (_characterList.containsKey(character.getObjectId()))
			{
				List<Quest> quests = this.getQuestByEvent(Quest.QuestEventType.ON_EXIT_ZONE);
				if (quests != null)
					for (Quest quest : quests)
						quest.notifyExitZone(character, this);
				_characterList.remove(character.getObjectId());
				onExit(character);
			}
		}
	}

	/**
	 * Force fully removes a character from the zone
	 * Should use during teleport / logoff
	 * @param character
	 */
	public void removeCharacter(L2Character character)
	{
		if (_characterList.containsKey(character.getObjectId()))
		{
			_characterList.remove(character.getObjectId());
			onExit(character);
		}
	}


	/**
	 * Will scan the zones char list for the character
	 * @param character
	 * @return
	 */
	public boolean isCharacterInZone(L2Character character)
	{
		return _characterList.containsKey(character.getObjectId());
	}

	protected abstract void onEnter(L2Character character);
	protected abstract void onExit(L2Character character);
	protected abstract void onDieInside(L2Character character);
	protected abstract void onReviveInside(L2Character character);

	public void addQuestEvent(Quest.QuestEventType EventType, Quest q)
	{
		if (_questEvents == null)
			_questEvents = new HashMap<>();
		List<Quest> questByEvents = _questEvents.get(EventType);
		if (questByEvents == null)
			questByEvents = new ArrayList<>();
		if (!questByEvents.contains(q))
			questByEvents.add(q);
		_questEvents.put(EventType, questByEvents);
	}
	
	public List<Quest> getQuestByEvent(Quest.QuestEventType EventType)
	{
		if (_questEvents == null)
			return null;
		return _questEvents.get(EventType);
	}
	
	/**
	 * Broadcasts packet to all players inside the zone
	 */
	public void broadcastPacket(L2GameServerPacket packet)
	{
		if (_characterList.isEmpty())
			return;
		for (L2Character character : _characterList.values())
			if (character instanceof L2PcInstance)
				character.sendPacket(packet);
	}
	
	public Map<Integer, L2Character> getCharactersInside()
	{
		return _characterList;
	}
}
