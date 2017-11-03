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

/**
 * This class ...
 *
 * @version $Revision: 1.1.6.2 $ $Date: 2005/03/27 15:29:39 $
 */
public class PlaySound extends L2GameServerPacket
{
    private static final String _S__98_PlaySound = "[S] 98 PlaySound";
    private int _unknown1;
    private String _soundFile;
    private int _unknown3;
    private int _unknown4;
    private int _unknown5;
    private int _unknown6;
    private int _unknown7;

    public PlaySound(String soundFile)
    {
        _unknown1   = 0;
        _soundFile  = soundFile;
        _unknown3   = 0;
        _unknown4   = 0;
        _unknown5   = 0;
        _unknown6   = 0;
        _unknown7   = 0;
    }

    public PlaySound(int unknown1, String soundFile, int unknown3, int unknown4, int unknown5, int unknown6, int unknown7)
    {
        _unknown1   = unknown1;
        _soundFile  = soundFile;
        _unknown3   = unknown3;
        _unknown4   = unknown4;
        _unknown5   = unknown5;
        _unknown6   = unknown6;
        _unknown7   = unknown7;
    }



	@Override
	protected final void writeImpl()
    {
        writeC(0x98);
        writeD(_unknown1);              //unknown 0 for quest and ship;
        writeS(_soundFile);
        writeD(_unknown3);              //unknown 0 for quest; 1 for ship;
        writeD(_unknown4);              //0 for quest; objectId of ship
        writeD(_unknown5);              //x
        writeD(_unknown6);              //y
        writeD(_unknown7);				//z
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
     */

	@Override
	public String getType()
    {
        return _S__98_PlaySound;
    }
}
