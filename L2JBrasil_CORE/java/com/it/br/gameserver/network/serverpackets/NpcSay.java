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
 *
 * @author Kerberos
 */
public final class NpcSay extends L2GameServerPacket
{
    // dddS
	private static final String _S__30_NPCSAY = "[S] 30 NpcSay";
	private int _objectId;
	private int _textType;
	private int _npcId;
	private String _text;

	/**
	* @param _characters
	*/
	public NpcSay(int objectId, int messageType, int npcId, String text)
	{
		_objectId = objectId;
		_textType = messageType;
		_npcId = 1000000+npcId;
		_text = text;
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0x30);
		writeD(_objectId);
		writeD(_textType);
		writeD(_npcId);
		writeS(_text);
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */
	

	@Override
	public String getType()
	{
		return _S__30_NPCSAY;
	}
}