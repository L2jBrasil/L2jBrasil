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

/**
 * This class ...
 *
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:33 $
 */
public final class L2PledgeSkillLearn
{
    // these two build the primary key
    private final int _id;
    private final int _level;

    // not needed, just for easier debug
    private final String _name;

    private final int _repCost;
    private final int _baseLvl;
    private final int _itemId;

    public L2PledgeSkillLearn(int id, int lvl, int baseLvl, String name, int cost, int itemId)
    {
        _id = id;
        _level = lvl;
        _baseLvl = baseLvl;
        _name = name.intern();
        _repCost = cost;
        _itemId = itemId;
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
     * @return Returns the name.
     */
    public String getName()
    {
        return _name;
    }

    /**
     * @return Returns the spCost.
     */
    public int getRepCost()
    {
        return _repCost;
    }

    public int getItemId()
    {
        return _itemId;
    }
}