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

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2WorldRegion;
import com.it.br.gameserver.model.zone.L2ZoneType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  durgus, Forsaiken
 */

public class L2SignetZone extends L2ZoneType
{
    private L2WorldRegion _region;
    private L2Character _owner;
    private final boolean _isOffensive;
    private final int _toRemoveOnOwnerExit;
    private final L2Skill _skill;
    
    public L2SignetZone(L2WorldRegion region, L2Character owner, boolean isOffensive, int toRemoveOnOwnerExit, L2Skill skill)
    {
        super();
        _region = region;
        _owner = owner;
        _isOffensive = isOffensive;
        _toRemoveOnOwnerExit = toRemoveOnOwnerExit;
        _skill = skill;
    }


    @Override
	protected void onEnter(L2Character character)
    {
        if (!_isOffensive)
            _skill.getEffects(_owner, character);
        else if (character != _owner && !L2Character.isInsidePeaceZone(_owner, character))
        	_skill.getEffects(_owner, character);
    }
    

    @Override
	protected void onExit(L2Character character)
    {
        if (character == _owner && _toRemoveOnOwnerExit > 0)
        {
            _owner.stopSkillEffects(_toRemoveOnOwnerExit);
            return;
        }
        
        character.stopSkillEffects(_skill.getId());
    }
    
    public void remove()
    {
        _region.removeZone(this);
        
        for (L2Character member : _characterList.values())
            member.stopSkillEffects(_skill.getId());
        
        if (!_isOffensive)
            _owner.stopSkillEffects(_skill.getId());
    }
    

    @Override
	protected void onDieInside(L2Character character) 
    {
        if (character == _owner && _toRemoveOnOwnerExit > 0)
            _owner.stopSkillEffects(_toRemoveOnOwnerExit);
        else
            character.stopSkillEffects(_skill.getId());
    }
    

    @Override
	protected void onReviveInside(L2Character character) 
    {
    	if (!_isOffensive)
            _skill.getEffects(_owner, character);
        else if (character != _owner && !L2Character.isInsidePeaceZone(_owner, character))
        	_skill.getEffects(_owner, character);
    }
    
    public L2Character[] getCharactersInZone()
    {
    	List<L2Character> charsInZone = new ArrayList<>();
    	for (L2Character character : _characterList.values())
    	{
    		if (!_isOffensive)
    			charsInZone.add(character);
            else if (character != _owner && !L2Character.isInsidePeaceZone(_owner, character))
            	charsInZone.add(character);
    	}
        return charsInZone.toArray(new L2Character[_characterList.size()]);
    }
}
