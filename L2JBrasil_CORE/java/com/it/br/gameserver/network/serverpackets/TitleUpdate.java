/* This program is free software; you can redistribute it and/or modify
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

import com.it.br.gameserver.model.actor.instance.L2PcInstance;

/**
 *
 * @author  devScarlet
 */
public class TitleUpdate extends L2GameServerPacket
{
	private static final String _S__CC_TITLE_UPDATE = "[S] cc TitleUpdate";
	private String _title;
	private int _objectId;

	public TitleUpdate(L2PcInstance cha)
	{
		_objectId = cha.getObjectId();
		_title = cha.getTitle();
	}

	/**
	 * @see com.it.br.gameserver.network.serverpackets.L2GameServerPacket#writeImpl()
	 */

	@Override
	protected void writeImpl()
	{
		writeC(0xcc);
		writeD(_objectId);
		writeS(_title);
	}

	/**
	 * @see com.it.br.gameserver.network.serverpackets.L2GameServerPacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__CC_TITLE_UPDATE;
	}

}
