/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
 package com.it.br.gameserver.network.serverpackets;
	
public class TutorialEnableClientEvent extends L2GameServerPacket
{
	private static final String _S__A8_TUTORIALENABLECLIENTEVENT = "[S] a8 TutorialEnableClientEvent";
	private int _eventId = 0;

	public TutorialEnableClientEvent(int event)
	{
		_eventId = event;
	}


	@Override
	protected void writeImpl()
	{
		writeC(0xA2);
		writeD(_eventId);
	}


	@Override
	public String getType()
	{
		return _S__A8_TUTORIALENABLECLIENTEVENT;
	}
} 