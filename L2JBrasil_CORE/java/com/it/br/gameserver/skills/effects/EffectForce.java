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
package com.it.br.gameserver.skills.effects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.skills.Env;
import com.it.br.gameserver.util.Util;

/**
 * @author kombat
 *
 */
public class EffectForce extends L2Effect
{
	static final Log _log = LogFactory.getLog(EffectForce.class.getName());

	public int forces = 0;
	private int _range = -1;

	public EffectForce(Env env, EffectTemplate template)
	{
		super(env, template);
		forces = getSkill().getLevel();
		_range = getSkill().getCastRange();
	}


	@Override
	public boolean onActionTime()
	{
		return Util.checkIfInRange(_range, getEffector(), getEffected(), true);
	}


	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}

	public void increaseForce()
	{
		forces++;
		updateBuff();
	}

	public void decreaseForce()
	{
		forces--;
		if(forces < 1)
		{
			exit();
		}
		else
		{
			updateBuff();
		}
	}

	public void updateBuff()
	{
		exit();
		L2Skill newSkill = SkillTable.getInstance().getInfo(getSkill().getId(), forces);
		newSkill.getEffects(getEffector(), getEffected());
	}


	@Override
	public void onExit()
	{
		//try
		//{
		//	getEffector().abortCast();
		//	if(getEffector().getForceBuff() != null)
		//		getEffector().getForceBuff().delete();
		//}
		//catch(Exception e){}
	}
}
