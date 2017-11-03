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

/**
 * This class ...
 * cS
 * @version $Revision: 1.1.2.2.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestRecipeShopMessageSet extends L2GameClientPacket
{
    private static final String _C__B1_RequestRecipeShopMessageSet = "[C] b1 RequestRecipeShopMessageSet";
    //private static Logger _log = Logger.getLogger(RequestRecipeShopMessageSet.class.getName());

    private String _name;


	@Override
	protected void readImpl()
    {
        _name = readS();
	}


	@Override
	protected void runImpl()
	{
        L2PcInstance player = getClient().getActiveChar();
	if (player == null)
	    return;
        /*if (player.getCreateList() == null)
        {
            player.setCreateList(new L2ManufactureList());
        }*/
        if (player.getCreateList() != null)
        {
            player.getCreateList().setStoreName(_name);
        }

    }


	@Override
	public String getType()
    {
        return _C__B1_RequestRecipeShopMessageSet;
    }
}
