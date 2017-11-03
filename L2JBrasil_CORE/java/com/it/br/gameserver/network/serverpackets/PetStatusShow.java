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

import com.it.br.gameserver.model.L2Summon;

/**
 * This class ...
 *
 * @author Yme
 * @version $Revision: 1.3.2.2.2.4 $ $Date: 2005/03/29 23:15:10 $
 */
public class PetStatusShow extends L2GameServerPacket
{
	private static final String _S__C9_PETSTATUSSHOW = "[S] B0 PetStatusShow";
	private int _summonType;

	public PetStatusShow(L2Summon summon)
	{
		_summonType = summon.getSummonType();
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0xB0);
		writeD(_summonType);
	}


	@Override
	public String getType()
	{
		return _S__C9_PETSTATUSSHOW;
	}
}
