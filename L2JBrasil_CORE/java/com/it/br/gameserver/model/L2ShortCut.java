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
 * @version $Revision: 1.3.4.1 $ $Date: 2005/03/27 15:29:32 $
 */
public class L2ShortCut
{
	public final static int TYPE_ITEM = 1;
	public final static int TYPE_SKILL = 2;
	public final static int TYPE_ACTION = 3;
	public final static int TYPE_MACRO = 4;
    public final static int TYPE_RECIPE = 5;

	private final int _slot;
	private final int _page;
	private final int _type;
	private final int _id;
	private final int _level;

	public L2ShortCut(int slotId, int pageId, int shortcutType,
                      int shortcutId, int shortcutLevel, int unknown)
	{
		_slot = slotId;
		_page = pageId;
		_type = shortcutType;
		_id = shortcutId;
		_level = shortcutLevel;
        //_unk = unknown;
	}

    public int getId()
    {
        return _id;
    }

    public int getLevel()
    {
        return _level;
    }

    public int getPage()
    {
        return _page;
    }

    public int getSlot()
    {
        return _slot;
    }

    public int getType()
    {
        return _type;
    }
}
