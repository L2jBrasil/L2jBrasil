/*
 * $Header: /cvsroot/l2j/L2_Gameserver/java/net/sf/l2j/gameserver/serverpackets/ASendPacket.java,v 1.14.2.3 2005/01/25 15:12:27 luisantonioa Exp $
 *
 *
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


import com.it.br.gameserver.model.actor.instance.L2StaticObjectInstance;

public class StaticObject extends L2GameServerPacket
{

        private static final String _S__99_StaticObjectPacket = "[S] 99 StaticObjectPacket";
        private L2StaticObjectInstance _staticObject;

        /**
         * [S]0x99 StaticObjectPacket   dd
         * @param _
         */

        public StaticObject(L2StaticObjectInstance StaticObject)
        {
            _staticObject = StaticObject;           // staticObjectId

        }


		@Override
		protected final void writeImpl()
        {

            writeC(0x99);
            writeD(_staticObject.getStaticObjectId());    //staticObjectId
            writeD(_staticObject.getObjectId());    //objectId


        }


    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
     */

	@Override
	public String getType()
    {
        return _S__99_StaticObjectPacket;
    }
}
