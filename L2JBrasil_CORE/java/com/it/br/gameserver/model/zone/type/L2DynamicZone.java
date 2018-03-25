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

import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2WorldRegion;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.zone.L2ZoneType;

import java.util.concurrent.Future;

public class L2DynamicZone extends L2ZoneType
{
	private L2WorldRegion _region;
	private L2Character _owner;
	private Future<?> _task;
	private L2Skill _skill;

	@SuppressWarnings("rawtypes")
	protected void setTask(Future task) { _task = task; }

	public L2DynamicZone(L2WorldRegion region, L2Character owner, L2Skill skill)
	{
		super();
		_region = region;
		_owner = owner;
		_skill = skill;

		Runnable r = new Runnable()
		{
			public void run()
			{
				remove();
			}
		};
		setTask(ThreadPoolManager.getInstance().scheduleGeneral(r, skill.getBuffDuration()));
	}

	@Override
	protected void onEnter(L2Character character)
	{
		try 
		{
			if (character instanceof L2PcInstance)
				((L2PcInstance)character).sendMessage("You have entered a temporary zone!");
			_skill.getEffects(_owner, character);
		} catch (NullPointerException e) {}
	}

	@Override
	protected void onExit(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			((L2PcInstance)character).sendMessage("You have left a temporary zone!");
		}
		if(character == _owner)
		{
			remove();
			return;
		}
		character.stopSkillEffects(_skill.getId());
	}

	protected void remove()
	{
		if (_task == null) return;
		_task.cancel(false);
		_task = null;

		_region.removeZone(this);
		for (L2Character member : _characterList.values())
		{
			try 
			{
				member.stopSkillEffects(_skill.getId());
			} catch (NullPointerException e) {}
		}
		_owner.stopSkillEffects(_skill.getId());
	}

	@Override
	protected void onDieInside(L2Character character)
	{
		if(character == _owner)
			remove();
		else
			character.stopSkillEffects(_skill.getId());
	}

	@Override
	protected void onReviveInside(L2Character character)
	{
		_skill.getEffects(_owner, character);
	}
}