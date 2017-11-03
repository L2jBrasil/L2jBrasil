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

/**
 * @author Layane
 *
 */
public abstract class EventHandler
{
    private Object _owner;

    public EventHandler(Object owner)
    {
        _owner = owner;
    }

    public final Object getOwner()
    {
        return _owner;
    }

    @Override
	public final boolean equals(Object object)
    {
        if (object instanceof EventHandler && _owner == ((EventHandler)object)._owner)
            return true;
        return false;
    }
    public abstract void handler(Object trigger, IEventParams params);
}
