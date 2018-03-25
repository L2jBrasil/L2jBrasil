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

import com.it.br.gameserver.RecipeController;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class RequestRecipeItemMakeSelf extends L2GameClientPacket
{
    private static final String _C__AF_REQUESTRECIPEITEMMAKESELF = "[C] AF RequestRecipeItemMakeSelf";
	//private static Logger _log = LoggerFactory.getLogger(RequestSellItem.class);

	private int _id;


	@Override
	protected void readImpl()
	{
		_id = readD();
	}


	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		    return;

        if (activeChar.getPrivateStoreType() != 0)
        {
            activeChar.sendMessage("Cannot make items while trading");
            return;
        }

        if (activeChar.isInCraftMode())
        {
            activeChar.sendMessage("Currently in Craft Mode");
            return;
        }

		RecipeController.getInstance().requestMakeItem(activeChar, _id);
	}

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
     */

	@Override
	public String getType()
    {
        return _C__AF_REQUESTRECIPEITEMMAKESELF;
    }

}
