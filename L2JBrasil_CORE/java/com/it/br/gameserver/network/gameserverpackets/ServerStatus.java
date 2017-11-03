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
package com.it.br.gameserver.network.gameserverpackets;

import java.io.IOException;
import java.util.Vector;

/**
 * @author -Wooden-
 *
 */
public class ServerStatus extends GameServerBasePacket
{
    private Vector<Attribute> _attributes;

    public static final String[] STATUS_STRING = {"Auto", "Good", "Normal", "Full", "Down", "Gm Only"};

    public static final int SERVER_LIST_STATUS = 0x01;
    public static final int SERVER_LIST_CLOCK = 0x02;
    public static final int SERVER_LIST_SQUARE_BRACKET = 0x03;
    public static final int MAX_PLAYERS = 0x04;
    public static final int TEST_SERVER = 0x05;

    public static final int STATUS_AUTO = 0x00;
    public static final int STATUS_GOOD = 0x01;
    public static final int STATUS_NORMAL = 0x02;
    public static final int STATUS_FULL = 0x03;
    public static final int STATUS_DOWN = 0x04;
    public static final int STATUS_GM_ONLY = 0x05;

    public static final int ON = 0x01;
    public static final int OFF = 0x00;

    class Attribute
    {
        public int id;
        public int value;

        Attribute(int pId, int pValue)
        {
            id = pId;
            value = pValue;
        }
    }

    public ServerStatus()
    {
        _attributes = new Vector<Attribute>();
    }

    public void addAttribute(int id, int value)
    {
        _attributes.add(new Attribute(id, value));
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.gameserverpackets.GameServerBasePacket#getContent()
     */

    @Override
	public byte[] getContent() throws IOException
    {
        writeC(0x06);
        writeD(_attributes.size());
        for (int i = 0; i < _attributes.size(); i++)
        {
            Attribute temp = _attributes.get(i);

            writeD(temp.id);
            writeD(temp.value);
        }

        return getBytes();
    }

}