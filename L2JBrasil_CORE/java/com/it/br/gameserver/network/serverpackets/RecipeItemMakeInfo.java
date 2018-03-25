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
package com.it.br.gameserver.network.serverpackets;

import com.it.br.Config;
import com.it.br.gameserver.RecipeController;
import com.it.br.gameserver.model.L2RecipeList;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 *
 * format   dddd
 *
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class RecipeItemMakeInfo extends L2GameServerPacket
{
    private static final String _S__D7_RECIPEITEMMAKEINFO = "[S] D7 RecipeItemMakeInfo";
    private static Logger _log = LoggerFactory.getLogger(RecipeItemMakeInfo.class);

    private int _id;
    private L2PcInstance _activeChar;
    private boolean _success;

    public RecipeItemMakeInfo(int id, L2PcInstance player, boolean success)
    {
        _id = id;
        _activeChar = player;
        _success = success;
    }

    public RecipeItemMakeInfo(int id, L2PcInstance player)
    {
        _id = id;
        _activeChar = player;
        _success = true;
    }


	@Override
	protected final void writeImpl()
    {
        L2RecipeList recipe = RecipeController.getInstance().getRecipeById(_id);

        if (recipe != null)
        {
            writeC(0xD7);

            writeD(_id);
            writeD(recipe.isDwarvenRecipe() ? 0 : 1); // 0 = Dwarven - 1 = Common
            writeD((int) _activeChar.getCurrentMp());
            writeD(_activeChar.getMaxMp());
            writeD(_success ? 1 : 0); // item creation success/failed
        }
        else if (Config.DEBUG) _log.info("No recipe found with ID = " + _id);
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
     */

	@Override
	public String getType()
    {
        return _S__D7_RECIPEITEMMAKEINFO;
    }
}
