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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.model.zone.L2ZoneType;
import com.it.br.util.Rnd;

public class L2CastleTeleportZone extends L2ZoneType
{
	private int _spawnLoc[];
	private int _castleId;
	private Castle _castle;

	public L2CastleTeleportZone()
	{
		super();
		_spawnLoc = new int[5];
	}

	@Override
	public void setParameter(String name, String value)
	{
		if(name.equals("castleId"))
		{
			_castleId = Integer.parseInt(value);
			_castle = CastleManager.getInstance().getCastleById(_castleId);
			_castle.setTeleZone(this);
		}
		else if(name.equals("spawnMinX"))
		{
			_spawnLoc[0] = Integer.parseInt(value);
		}
		else if(name.equals("spawnMaxX"))
		{
			_spawnLoc[1] = Integer.parseInt(value);
		}
		else if(name.equals("spawnMinY"))
		{
			_spawnLoc[2] = Integer.parseInt(value);
		}
		else if(name.equals("spawnMaxY"))
		{
			_spawnLoc[3] = Integer.parseInt(value);
		}
		else if(name.equals("spawnZ"))
		{
			_spawnLoc[4] = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}

	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, true);
	}

	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(L2Character.ZONE_NOSUMMONFRIEND, false);
	}

	@Override
	public void onDieInside(L2Character l2character){}

	@Override
	public void onReviveInside(L2Character l2character){}

	public List<L2Character> getAllPlayers()
	{
		List<L2Character> players = new ArrayList<>();
		Iterator<L2Character> i$ = _characterList.values().iterator();

		while(i$.hasNext())
		{
			L2Character temp = i$.next();

			if(temp instanceof L2PcInstance)
			{
				players.add(temp);
			}

			temp = null;
		}

		i$ = null;

		return players;
	}

	public void oustAllPlayers()
	{
		if(_characterList == null)
			return;

		if(_characterList.isEmpty())
			return;

		Iterator<L2Character> i$ = _characterList.values().iterator();
		while(i$.hasNext())
		{
			L2Character character = i$.next();

			if(character != null && character instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) character;

				if(player.isOnline() == 1)
				{
					player.teleToLocation(Rnd.get(_spawnLoc[0], _spawnLoc[1]), Rnd.get(_spawnLoc[2], _spawnLoc[3]), _spawnLoc[4]);
				}

				player = null;
			}

			character = null;
		}

		i$ = null;
	}

	public int[] getSpawn()
	{
		return _spawnLoc;
	}
}