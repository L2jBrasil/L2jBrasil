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
package com.it.br.gameserver.skills.conditions;

import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.skills.Env;

import java.util.List;

public class ConditionTargetClassIdRestriction extends Condition {

	private final List<Integer> _classIds;

	public ConditionTargetClassIdRestriction(List<Integer> classId)
	{
		_classIds = classId;
	}


	@Override
	public boolean testImpl(Env env) {
		if (!(env.target instanceof L2PcInstance))
			return true;
		return (!_classIds.contains(((L2PcInstance)env.target).getClassId().getId()));
	}
}
