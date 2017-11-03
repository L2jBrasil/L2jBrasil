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

import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.RecipeController;

public final class RequestRecipeBookOpen extends L2GameClientPacket
{
    private static final String _C__AC_REQUESTRECIPEBOOKOPEN = "[C] AC RequestRecipeBookOpen";
	private static Logger _log = Logger.getLogger(RequestRecipeBookOpen.class.getName());

    private boolean _isDwarvenCraft;


	@Override
	protected void readImpl()
	{
        _isDwarvenCraft = (readD() == 0);
        if (Config.DEBUG)
        {
        	_log.info("RequestRecipeBookOpen : " + (_isDwarvenCraft ? "dwarvenCraft" : "commonCraft"));
        }
	}


	@Override
	protected void runImpl()
	{
	    if (getClient().getActiveChar() == null)
	        return;

        if (getClient().getActiveChar().getPrivateStoreType() != 0)
        {
            getClient().getActiveChar().sendMessage("Cannot use recipe book while trading");
            return;
        }

        RecipeController.getInstance().requestBookOpen(getClient().getActiveChar(), _isDwarvenCraft);
	}

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
     */

	@Override
	public String getType()
    {
        return _C__AC_REQUESTRECIPEBOOKOPEN;
    }
}
