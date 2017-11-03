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

public class AiEvent
{
	private AiEventType _type;
	private L2Character _source;
	private L2Character _target;

	public AiEvent(AiEventType type, L2Character source, L2Character target)
	{
		_type = type;
		_source = source;
		_target = target;
	}
	public AiEventType getType()
	{
		return _type;
	}
	public L2Character getSource()
	{
		return _source;
	}
	public L2Character getTarget()
	{
		return _target;
	}
}