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

public final class TutorialShowHtml extends L2GameServerPacket
{
	private static final String _S__A6_TUTORIALSHOWHTML = "[S] a6 TutorialShowHtml";
	private String _html;

	public TutorialShowHtml(String html)
	{
		_html = html;
	}
	
	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#writeImpl()
	 */
	

	@Override
	protected void writeImpl()
	{
		writeC(0xA0);
		writeS(_html);
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.BasePacket#getType()
	*/
	

	@Override
	public String getType()
	{
		return _S__A6_TUTORIALSHOWHTML;
	}
}