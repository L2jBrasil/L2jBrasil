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
package com.it.br.gameserver.network.clientpackets;

import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.RecipeShopItemInfo;

/**
 * This class ...
 * cdd
 * @version $Revision: 1.1.2.1.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestRecipeShopMakeInfo extends L2GameClientPacket
{
    private static final String _C__B5_RequestRecipeShopMakeInfo = "[C] b5 RequestRecipeShopMakeInfo";
    //private static Logger _log = LoggerFactory.getLogger(RequestRecipeShopMakeInfo.class);

    private int _playerObjectId;
    private int _recipeId;


	@Override
	protected void readImpl()
    {
        _playerObjectId = readD();
        _recipeId = readD();
	}


	@Override
	protected void runImpl()
	{
        L2PcInstance player = getClient().getActiveChar();
        if (player == null)
        	return;


        player.sendPacket(new RecipeShopItemInfo(_playerObjectId,_recipeId));

    }



	@Override
	public String getType()
    {
        return _C__B5_RequestRecipeShopMakeInfo;
    }


}
