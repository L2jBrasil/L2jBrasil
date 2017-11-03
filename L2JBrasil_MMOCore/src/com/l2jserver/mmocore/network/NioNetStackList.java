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
package com.l2jserver.mmocore.network;

/**
 * @author Forsaiken
 */
public final class NioNetStackList<E>
{
    private final NioNetStackNode _start = new NioNetStackNode();
    
    private final NioNetStackNodeBuf _buf = new NioNetStackNodeBuf();
    
    private NioNetStackNode _end = new NioNetStackNode();
    
    public NioNetStackList()
    {
        clear();
    }
    
    public final void addLast(final E elem)
    {
    	final NioNetStackNode newEndNode = _buf.removeFirst();
    	_end._value = elem;
        _end._next = newEndNode;
        _end = newEndNode;
    }
    
    public final E removeFirst()
    {
    	final NioNetStackNode old = _start._next;
    	final E value = old._value;
    	_start._next = old._next;
    	_buf.addLast(old);
        return value;
    }
    
    public final boolean isEmpty()
    {
        return _start._next == _end;
    }
    
    public final void clear()
    {
        _start._next = _end;
    }
    
    private final class NioNetStackNode
    {
    	private NioNetStackNode _next;
    	private E _value;
        
    	private NioNetStackNode()
        {
        	
        }
    }
    
    private final class NioNetStackNodeBuf
    {
    	private final NioNetStackNode _start = new NioNetStackNode();
        private NioNetStackNode _end = new NioNetStackNode();
        
        NioNetStackNodeBuf()
        {
        	_start._next = _end;
        }
        
        final void addLast(final NioNetStackNode node)
        {
        	node._next = null;
        	node._value = null;
            _end._next = node;
            _end = node;
        }
        
        final NioNetStackNode removeFirst()
        {
        	if (_start._next == _end)
        		return new NioNetStackNode();
        	
        	final NioNetStackNode old = _start._next;
        	_start._next = old._next;
            return old;
        }
    }
}