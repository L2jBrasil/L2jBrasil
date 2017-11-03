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

import com.it.br.Config;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.instancemanager.CastleManorManager;
import com.it.br.gameserver.instancemanager.CastleManorManager.CropProcure;

import java.util.ArrayList;
import java.util.List;

/**
 * Format: (ch) dd [dddc]
 * d - manor id
 * d - size
 * [
 * d - crop id
 * d - sales
 * d - price
 * c - reward type
 * ]
 * @author l3x
 *
 */
public class RequestSetCrop extends L2GameClientPacket {
	private static final String _C__D0_0B_REQUESTSETCROP = "[C] D0:0B RequestSetCrop";

	private int _size;

	private int _manorId;

	private int[] _items; // _size*4


	@Override
	protected void readImpl()
	{
		_manorId = readD();
		_size = readD();
		if (_size * 13 > _buf.remaining() || _size > 500)
		{
			_size = 0;
			return;
		}
		_items = new int[_size * 4];
		for (int i = 0; i < _size; i++)
		{
			int itemId = readD();
			_items[i * 4 + 0] = itemId;
			int sales = readD();
			_items[i * 4 + 1] = sales;
			int price = readD();
			_items[i * 4 + 2] = price;
			int type = readC();
			_items[i * 4 + 3] = type;
		}
	}


    @Override
	protected void runImpl()
	{
		if (_size < 1)
			return;

		List<CropProcure> crops = new ArrayList<>();
		for (int i = 0; i < _size; i++)
		{
			int id = _items[i * 4 + 0];
			int sales = _items[i * 4 + 1];
			int price = _items[i * 4 + 2];
			int type = _items[i * 4 + 3];
			if (id > 0)
			{
				CropProcure s = CastleManorManager.getInstance().getNewCropProcure(id, sales, type, price, sales);
				crops.add(s);
			}
		}

		CastleManager.getInstance().getCastleById(_manorId).setCropProcure(crops,
				CastleManorManager.PERIOD_NEXT);
		if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
			CastleManager.getInstance().getCastleById(_manorId).saveCropData(CastleManorManager.PERIOD_NEXT);
	}


	@Override
	public String getType()
	{
		return _C__D0_0B_REQUESTSETCROP;
	}
}
