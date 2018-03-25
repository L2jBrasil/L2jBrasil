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

import com.it.br.Config;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.zone.L2ZoneType;
import com.it.br.gameserver.util.Rnd;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class L2TownZone extends L2ZoneType
{
	private String _townName;
	private int _townId;
	private int _redirectTownId;
	private int _taxById;
	private boolean _noPeace;
	private List<int[]> _spawnLocs;

	public L2TownZone()
	{
		super();

		_taxById = 0;
		_spawnLocs = new ArrayList<>();

		// Default to Giran
		_redirectTownId = 9;

		// Default peace zone
		_noPeace = false;
	}

	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("name"))
			_townName = value;
		else if (name.equals("townId"))
			_townId = Integer.parseInt(value);
		else if (name.equals("redirectTownId"))
			_redirectTownId = Integer.parseInt(value);
		else if (name.equals("taxById"))
			_taxById = Integer.parseInt(value);
		else if (name.equals("noPeace"))
			_noPeace = Boolean.parseBoolean(value);
		else super.setParameter(name, value);
	}

	@Override
	public void setSpawns(Node node)
	{
		int locs[] = new int[3];
		Node loc = node.getAttributes().getNamedItem("X");
		if (loc != null)
			locs[0] = Integer.parseInt(loc.getNodeValue());
		loc = node.getAttributes().getNamedItem("Y");
		if (loc != null)
			locs[1] = Integer.parseInt(loc.getNodeValue());
		loc = node.getAttributes().getNamedItem("Z");
		if (loc != null)
			locs[2] = Integer.parseInt(loc.getNodeValue());
		if (locs != null)
			_spawnLocs.add(locs);
	}

	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			// PVP possible during siege, now for siege participants only
			// Could also check if this town is in siege, or if any siege is going on
			if (((L2PcInstance)character).getSiegeState() != 0 && Config.ZONE_TOWN == 1)
				return;

			//((L2PcInstance)character).sendMessage("You entered "+_townName);
		}

		if (!_noPeace && Config.ZONE_TOWN != 2) character.setInsideZone(L2Character.ZONE_PEACE, true);
	}

	@Override
	protected void onExit(L2Character character)
	{
		if (!_noPeace) character.setInsideZone(L2Character.ZONE_PEACE, false);

		// if (character instanceof L2PcInstance)
			//((L2PcInstance)character).sendMessage("You left "+_townName);

	}

	@Override
	protected void onDieInside(L2Character character) {}

	@Override
	protected void onReviveInside(L2Character character) {}

	/**
	 * Returns this town zones name
	 * @return
	 */
	@Deprecated
	public String getName()
	{
		return _townName;
	}

	/**
	 * Returns this zones town id (if any)
	 * @return
	 */
	public int getTownId()
	{
		return _townId;
	}

	/**
	 * Gets the id for this town zones redir town
	 * @return
	 */
	@Deprecated
	public int getRedirectTownId()
	{
		return _redirectTownId;
	}

	/**
	 * Returns this zones spawn location
	 * @return
	 */
	public final int[] getSpawnLoc()
	{  
		if(Config.ALLOW_RND_SPAWN)
			return _spawnLocs.get(Rnd.get(_spawnLocs.size()));
		else
			return _spawnLocs.get(0);
	}

	/**
	 * Returns this town zones castle id
	 * @return
	 */
	public final int getTaxById()
	{
		return _taxById;
	}
}