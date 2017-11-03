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
package com.it.br.gameserver.model.zone.type;

import com.it.br.gameserver.datatables.xml.MapRegionTable;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.Olympiad.Olympiad;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.zone.L2ZoneType;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 * An olympiad stadium
 * 
 * @author L2JFrozen
 */
public class L2OlympiadStadiumZone extends L2ZoneType
{
	private int _stadiumId;

	public L2OlympiadStadiumZone()
	{
		super();
	}

	@Override
	public void setParameter(String name, String value)
	{
		if(name.equals("stadiumId"))
		{
			_stadiumId = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}

	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_PVP, true);
		character.setInsideZone(L2Character.ZONE_NOLANDING, true);
        character.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, true);
		
		if(character instanceof L2PcInstance)
		{
			((L2PcInstance) character).sendPacket(new SystemMessage(SystemMessageId.ENTERED_COMBAT_ZONE));
		}
		
		// Fix against exploit for Olympiad zone during oly period
		if(character instanceof L2PcInstance && Olympiad.getInstance().inCompPeriod() && !((L2PcInstance) character).isInOlympiadMode() && !((L2PcInstance) character).inObserverMode())
		{
			oustAllPlayers();
		}
	}

	@Override
	protected void onExit(L2Character character)
	{
		if(character instanceof L2PcInstance)
		{
			if(((L2PcInstance) character).isInOlympiadMode() && ((L2PcInstance) character).isOlympiadStart())
			{
				int loc[] = ((L2PcInstance) character).getOlympiadPosition();
				((L2PcInstance) character).teleToLocation(loc[0], loc[1], loc[2]);
			}
		}

		character.setInsideZone(L2Character.ZONE_PVP, false);
		character.setInsideZone(L2Character.ZONE_NOLANDING, false);

		if(character instanceof L2PcInstance)
		{
			((L2PcInstance) character).sendPacket(new SystemMessage(SystemMessageId.LEFT_COMBAT_ZONE));
		}
	}

	@Override
	protected void onDieInside(L2Character character)
	{}

	@Override
	protected void onReviveInside(L2Character character)
	{}

	public void oustAllPlayers()
	{
		if(_characterList == null)
			return;

		if(_characterList.isEmpty())
			return;

		for(L2Character character : _characterList.values())
		{
			if(character == null)
			{
				continue;
			}

			if(character instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) character;
				if(player.isOnline() == 1 && !player.isGM() && Olympiad.getInstance().inCompPeriod() && !player.inObserverMode() && !player.isInOlympiadMode())
				{
					player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
				}
				player = null;
			}
		}
	}

	/**
	 * Returns this zones stadium id (if any)
	 * 
	 * @return
	 */
	public int getStadiumId()
	{
		return _stadiumId;
	}
}
