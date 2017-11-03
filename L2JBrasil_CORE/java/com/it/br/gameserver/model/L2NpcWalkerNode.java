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
package com.it.br.gameserver.model;

import com.it.br.gameserver.templates.StatsSet;

/**
 * 
 * @author Rayan RPG
 * @since 927
 *
 */
public class L2NpcWalkerNode 
{
	private int _routeId;
	private int _npcId;
	private String _movePoint;
	private String _chatText;
	private int _moveX;
	private int _moveY;
	private int _moveZ;;
	private int _delay;
	
	private boolean _running;

	public void setRunning(boolean val)
	{
		_running = val;
	}
	
	public void setRouteId(int id)
	{
		_routeId = id;
	}

	public void setNpcId(int id)
	{
		_npcId = id;
	}

	public void setMovePoint(String val)
	{
		_movePoint = val;
	}

	public void setChatText(String val)
	{
		_chatText = val;
	}

	public void setMoveX(int val)
	{
		_moveX = val;
	}

	public void setMoveY(int val)
	{
		_moveY = val;
	}

	public void setMoveZ(int val)
	{
		_moveZ = val;
	}

	public void setDelay(int val)
	{
		_delay = val;
	}

	public int getRouteId()
	{
		return _routeId;
	}

	public int getNpcId()
	{
		return _npcId;
	}

	public String getMovePoint()
	{
		return _movePoint;
	}

	public String getChatText()
	{
		return _chatText;
	}

	public int getMoveX()
	{
		return _moveX;
	}

	public int getMoveY()
	{
		return _moveY;
	}

	public int getMoveZ()
	{
		return _moveZ;
	}

	public int getDelay()
	{
		return _delay;
	}
	
	public boolean getRunning()
	{
		return _running;
	}
	
	/**
	 * Constructor of L2NpcWalker.<BR><BR>
	 * 
	 * @param set The StatsSet object to transfert data to the method
	 * 
	 */
	
	public L2NpcWalkerNode()
	{
		
	}
	
	/**
	 * Constructor of L2NpcWalker.<BR><BR>
	 * 
	 * @param set The StatsSet object to transfert data to the method
	 * 
	 */
		
	public L2NpcWalkerNode(StatsSet set)
	{		
		_npcId       = set.getInteger("npc_id");
		_movePoint   = set.getString("move_point");
		_chatText    = set.getString("chatText");
		_moveX       = set.getInteger("move_x");
		_moveX       = set.getInteger("move_y");
		_moveX       = set.getInteger("move_z");
		_delay       = set.getInteger("delay");
	}
}
