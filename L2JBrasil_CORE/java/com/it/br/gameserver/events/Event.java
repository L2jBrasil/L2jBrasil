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
package com.it.br.gameserver.events;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Layane
 *
 */
public class Event
{
    private final List<EventHandler> _handlers = new ArrayList<>();

    public void add(EventHandler handler)
    {
        if (!_handlers.contains(handler))
            _handlers.add(handler);
    }

    public void remove(EventHandler handler)
    {
        if (handler != null)
            _handlers.remove(handler);
    }

    public void fire(Object trigger, IEventParams params)
    {
        for (EventHandler handler : _handlers)
            handler.handler(trigger,params);
    }

    public void clear()
    {
        _handlers.clear();
    }
}
