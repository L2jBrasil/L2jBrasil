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
package com.it.br.gameserver.model;

import com.it.br.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 *
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:33 $
 */
public final class L2EnchantSkillLearn
{
    // these two build the primary key
    private final int _id;
    private final int _level;

    // not needed, just for easier debug
    private final String _name;

    private final int _spCost;
    private final int _baseLvl;
    private final int _minSkillLevel;
    private final int _exp;
    private final byte _rate76;
    private final byte _rate77;
    private final byte _rate78;

    public L2EnchantSkillLearn(int id, int lvl, int minSkillLvl, int baseLvl, String name, int cost, int exp, byte rate76, byte rate77, byte rate78)
    {
        _id = id;
        _level = lvl;
        _baseLvl = baseLvl;
        _minSkillLevel = minSkillLvl;
        _name = name.intern();
        _spCost = cost;
        _exp = exp;
        _rate76 = rate76;
        _rate77 = rate77;
        _rate78 = rate78;
    }

    /**
     * @return Returns the id.
     */
    public int getId()
    {
        return _id;
    }

    /**
     * @return Returns the level.
     */
    public int getLevel()
    {
        return _level;
    }

    /**
     * @return Returns the minLevel.
     */
    public int getBaseLevel()
    {
        return _baseLvl;
    }

    /**
     * @return Returns the minSkillLevel.
     */
    public int getMinSkillLevel()
    {
        return _minSkillLevel;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return _name;
    }

    /**
     * @return Returns the spCost.
     */
    public int getSpCost()
    {
        return _spCost;
    }
    public int getExp()
    {
        return _exp;
    }

    public byte getRate(L2PcInstance ply)
    {
        byte result;
        switch (ply.getLevel())
        {
        case 76:
            result = _rate76;
            break;
        case 77:
            result = _rate77;
            break;
        case 78:
            result = _rate78;
            break;
        default:
            result = _rate78;
            break;
        }
        return result;
    }
}