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

import java.util.ArrayList;
import java.util.List;

import com.it.br.gameserver.TaskPriority;

/**
 *@author -Wooden-
 */
public class EventHandlerSet implements Comparable<EventHandlerSet>
{
	private int _comparatorPrio;
	private long _insertionTime;
	private List<EventHandler> _handlers;
	private AiEventType _eventType;

	public EventHandlerSet(AiEventType event, List<EventHandler> handlers, TaskPriority prio)
	{
		_comparatorPrio = (prio.ordinal()+1)*3;
		_handlers = new ArrayList<>();
		_eventType = event;
		for(EventHandler handler : handlers)
			addHandler(handler);
	}

	public EventHandlerSet(EventHandler handler, TaskPriority prio)
	{
		_comparatorPrio = (prio.ordinal()+1)*3;
		_handlers = new ArrayList<>();
		_eventType = handler.getEvenType();
		addHandler(handler);
	}

	public void addHandler(EventHandler handler)
	{
		if(handler == null)
			return;
		int prio = handler.getPriority();
		int index = -1;
		for(EventHandler eventHandler : _handlers)
		{
			if(eventHandler.getPriority() <= prio)
			{
				index = eventHandler.getPriority();
				break;
			}
		}
		if(index != -1)
		{
			_handlers.add(index, handler);
		}
		else
		{
			_handlers.add(handler);
		}
	}

	public void setPrio(TaskPriority prio)
	{
		_comparatorPrio = (prio.ordinal()+1)*3;
	}

	public void stampInsertionTime()
	{
		 _insertionTime = System.currentTimeMillis();
	}

	public int getComparatorPriority()
	{
		return _comparatorPrio;
	}

	public List<EventHandler> getHandlers()
	{
		return _handlers;
	}

	public AiEventType getEventType()
	{
		return _eventType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */

	public int compareTo(EventHandlerSet es)
	{
		return (int)( (System.currentTimeMillis() - _insertionTime)/1000) + _comparatorPrio - es.getComparatorPriority();
	}


	@Override
	public String toString()
	{
		String str = "EventHandlerSet: size:"+_handlers.size()+" Priority:"+_comparatorPrio+(_insertionTime != 0 ? " TimePoints: "+(int)( (System.currentTimeMillis() - _insertionTime)/1000) : "");
		for (EventHandler handler : _handlers)
		{
			str = str.concat(" - "+handler.toString());
		}
		return str;
	}
}
