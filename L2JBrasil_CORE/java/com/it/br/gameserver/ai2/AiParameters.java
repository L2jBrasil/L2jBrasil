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

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 * @author -Wooden-
 *
 */
public class AiParameters
{
	private Queue<AiEvent> _eventQueue;
	private L2NpcInstance _actor;
	private List<Hated> _hated;
	private List<Liked> _liked;

	public class Hated
	{
		public L2Character character;
		public HateReason reason;
		public int degree;

	}
	public class Liked
	{
		public L2Character character;
		public LikeReason reason;
		public int degree;
	}
	public enum HateReason
	{
		GAVE_DAMMAGE,
		HEALS_ENNEMY,
		GAVE_DAMMAGE_TO_FRIEND,
		IS_ENNEMY
	}
	public enum LikeReason
	{
		FRIEND,
		HEALED,
		HEALED_FRIEND,
		GAVE_DAMMAGE_TO_ENNEMY
	}
	public AiParameters(L2NpcInstance actor)
	{
		_eventQueue = new PriorityBlockingQueue<AiEvent>();
		_hated = new ArrayList<>();
		_liked = new ArrayList<>();
		_actor = actor;
	}

	/**
	 * @return
	 */
	public boolean hasEvents()
	{
		return _eventQueue.isEmpty();
	}

	/**
	 * @return
	 */
	public AiEvent nextEvent()
	{
		return _eventQueue.poll();
	}
	public void queueEvents(AiEvent set)
	{
		_eventQueue.offer(set);
	}
	public L2NpcInstance getActor()
	{
		return _actor;
	}

	public List<Hated> getHated()
	{
		return _hated;
	}
	public List<Liked> getLiked()
	{
		return _liked;
	}
	public void addLiked(Liked cha)
	{
		_liked.add(cha);
	}
	public void addHated(Hated cha)
	{
		_hated.add(cha);
	}
	public void clear()
	{
		_hated.clear();
		_liked.clear();
		_eventQueue.clear();
	}
}
