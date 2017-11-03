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

import java.util.Collection;
import java.util.concurrent.Future;

import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2SummonInstance;
import com.it.br.gameserver.model.zone.L2ZoneType;

public class L2SkillZone extends L2ZoneType
{
	private int _skillId;
	private int _skillLvl;
	private boolean _onSiege;
	public Future<?> _task;
	public L2Skill _skill = null;
	public int _initialDelay;
	public int _reuse;

	public L2SkillZone()
	{
		super();
		_initialDelay = 0;
		_reuse = 30000;
	}

	@Override
	public void setParameter(String name, String value)
	{
		if(name.equals("skillId"))
			_skillId = Integer.parseInt(value);            
		else if(name.equals("skillLvl"))
			_skillLvl = Integer.parseInt(value);
		else if(name.equals("onSiege"))
			_onSiege = Boolean.parseBoolean(value);  	
		else
			super.setParameter(name, value);

		_skill = SkillTable.getInstance().getInfo(_skillId, _skillLvl);
	}

	@Override
	protected void onEnter(L2Character character)
	{
		if((character instanceof L2PcInstance || character instanceof L2SummonInstance) && (!_onSiege || _onSiege && character.isInsideZone(4))) 
		{ 
			if(character instanceof L2PcInstance) 
			{ 
				((L2PcInstance) character).enterDangerArea(); 
			} 
        		
			SkillTable.getInstance().getInfo(_skillId, _skillLvl).getEffects(character, character); 
		} 
	}

	@Override
	protected void onExit(L2Character character)
	{
		if(character instanceof L2PcInstance || character instanceof L2SummonInstance) 
		{ 
			character.stopSkillEffects(_skillId); 
			if(character instanceof L2PcInstance) 
			{ 
				((L2PcInstance) character).exitDangerArea(); 
			} 
		} 
	}

	protected Collection<L2Character> getCharacterList()
	{
		return _characterList.values();
	}

	class ApplySkill implements Runnable
	{
		ApplySkill()
		{
			if (_skill == null)
				throw new IllegalStateException("No skills defined.");
		}

		public void run()
		{
			for (L2Character temp : L2SkillZone.this.getCharacterList())
			{
				if (temp != null && !temp.isDead())
				{
					if (temp.getFirstEffect(_skill.getId()) == null)
						_skill.getEffects(temp, temp);
				}
			}
		}
	}

	@Override
	protected void onDieInside(L2Character character)
	{
		onExit(character);
	}

	@Override
	protected void onReviveInside(L2Character character)
	{
		onEnter(character);
	}
}