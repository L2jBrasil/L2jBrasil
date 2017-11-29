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
package com.it.br.gameserver.model.base;

import static com.it.br.configuration.Configurator.getSettings;

import com.it.br.Config;
import com.it.br.configuration.settings.L2JBrasilSettings;
import com.it.br.configuration.settings.ServerSettings;

/**
 * Character Sub-Class Definition
 * <BR>
 * Used to store key information about a character's sub-class.
 *
 * @author Tempy
 */
public final class SubClass
{
    private PlayerClass _class;
    private long _exp;
    private int _sp = 0;
    private byte _level;
    private int _classIndex = 1;

    public SubClass(int classId, long exp, int sp, byte level, int classIndex)
    {
        _class = PlayerClass.values()[classId];
        _exp = exp;
        _sp = sp;
        _level = level;
        _classIndex = classIndex;
    }

    public SubClass(int classId, int classIndex)
    {
        // Used for defining a sub class using default values for XP, SP and player level.
        _class = PlayerClass.values()[classId];
        int subclassLevel = getSettings(L2JBrasilSettings.class).getAltSubClassLevel();
        _exp = Experience.LEVEL[subclassLevel];
        _level = (byte) subclassLevel;
        _classIndex = classIndex;
    }

    public SubClass()
    {
        // Used for specifying ALL attributes of a sub class directly,
        // using the preset default values.
    	 int subclassLevel = getSettings(L2JBrasilSettings.class).getAltSubClassLevel();
    	_exp = Experience.LEVEL[subclassLevel];
    	_level = (byte) subclassLevel;
    }

    public PlayerClass getClassDefinition()
    {
        return _class;
    }

    public int getClassId()
    {
        return _class.ordinal();
    }

    public long getExp()
    {
        return _exp;
    }

    public int getSp()
    {
        return _sp;
    }

    public byte getLevel()
    {
        return _level;
    }

    public int getClassIndex()
    {
        return _classIndex;
    }

    public void setClassId(int classId)
    {
        _class = PlayerClass.values()[classId];
    }

    public void setExp(long expValue)
    {
		if (expValue > Experience.LEVEL[Experience.MAX_LEVEL])
			expValue = Experience.LEVEL[Experience.MAX_LEVEL];

        _exp = expValue;
    }

    public void setSp(int spValue)
    {
        _sp = spValue;
    }

    public void setClassIndex(int classIndex)
    {
        _classIndex = classIndex;
    }

    public void setLevel(byte levelValue)
    {
    	int subclassLevel = getSettings(L2JBrasilSettings.class).getAltSubClassLevel();
        if (levelValue > (Experience.MAX_LEVEL - 1))
            levelValue = (Experience.MAX_LEVEL - 1);
        else if (levelValue < subclassLevel)
            levelValue = (byte) subclassLevel;

        _level = levelValue;
    }

    public void incLevel()
    {
        if (getLevel() == (Experience.MAX_LEVEL - 1))
            return;

        _level++;
        setExp(Experience.LEVEL[getLevel()]);
    }

    public void decLevel()
    {
        if (getLevel() ==  getSettings(L2JBrasilSettings.class).getAltSubClassLevel())
            return;

        _level--;
        setExp(Experience.LEVEL[getLevel()]);
    }
}