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

import com.it.br.gameserver.GameTimeController;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 *
 * @version $Revision: 1.4.2.5.2.6 $ $Date: 2005/03/27 15:29:39 $
 */
public class CharSelected extends L2GameServerPacket
{
	//   SdSddddddddddffddddddddddddddddddddddddddddddddddddddddd d
	private static final String _S__21_CHARSELECTED = "[S] 15 CharSelected";
	private L2PcInstance _activeChar;
	private int _sessionId;

	/**
	 * @param _characters
	 */
	public CharSelected(L2PcInstance cha, int sessionId)
	{
		_activeChar = cha;
		_sessionId = sessionId;
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0x15);

		writeS(_activeChar.getName());
		writeD(_activeChar.getCharId()); // ??
		writeS(_activeChar.getTitle());
		writeD(_sessionId);
		writeD(_activeChar.getClanId());
		writeD(0x00);  //??
		writeD(_activeChar.getAppearance().getSex()? 1 : 0);
		writeD(_activeChar.getRace().ordinal());
		writeD(_activeChar.getClassId().getId());
		writeD(0x01); // active ??
		writeD(_activeChar.getX());
		writeD(_activeChar.getY());
		writeD(_activeChar.getZ());

		writeF(_activeChar.getCurrentHp());
		writeF(_activeChar.getCurrentMp());
		writeD(_activeChar.getSp());
		writeQ(_activeChar.getExp());
		writeD(_activeChar.getLevel());
		writeD(_activeChar.getKarma());	// thx evill33t
		writeD(0x0);	//?
		writeD(_activeChar.getINT());
		writeD(_activeChar.getSTR());
		writeD(_activeChar.getCON());
		writeD(_activeChar.getMEN());
		writeD(_activeChar.getDEX());
		writeD(_activeChar.getWIT());
		for (int i=0; i<30; i++)
		{
			writeD(0x00);
		}
//		writeD(0); //c3
//writeD(0); //c3
//		writeD(0); //c3


		writeD(0x00);       //c3  work
		writeD(0x00);       //c3  work

		// extra info
		writeD(GameTimeController.getInstance().getGameTime());	// in-game time

		writeD(0x00);   //

		writeD(0x00);       //c3

		writeD(0x00);       //c3 InspectorBin
		writeD(0x00);       //c3
		writeD(0x00);       //c3
		writeD(0x00);       //c3

		writeD(0x00);       //c3 InspectorBin for 528 client
		writeD(0x00);       //c3
		writeD(0x00);       //c3
		writeD(0x00);       //c3
		writeD(0x00);       //c3
		writeD(0x00);       //c3
		writeD(0x00);       //c3
		writeD(0x00);       //c3

	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__21_CHARSELECTED;
	}

}
