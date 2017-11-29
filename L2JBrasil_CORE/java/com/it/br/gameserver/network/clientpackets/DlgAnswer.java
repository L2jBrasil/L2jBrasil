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

import static com.it.br.configuration.Configurator.getSettings;

import com.it.br.Config;
import com.it.br.configuration.settings.L2JModsSettings;
import com.it.br.gameserver.network.SystemMessageId;

/**
 * @author Dezmond_snz
 * Format: cddd
 */
public final class DlgAnswer extends L2GameClientPacket
{
	private static final String _C__C5_DLGANSWER = "[C] C5 DlgAnswer";
	//private static Logger _log = Logger.getLogger(DlgAnswer.class.getName());
	private int _messageId;
	private int _answer, _unk;

	@Override
	protected void readImpl()
	{
		_messageId = readD();
		_answer = readD();
		_unk = readD();
	}

	@Override
	public void runImpl()
	{
		if (Config.DEBUG)
			_log.fine(getType()+": Answer acepted. Message ID "+_messageId+", asnwer "+_answer+", unknown field "+_unk);
		if (_messageId == SystemMessageId.RESSURECTION_REQUEST.getId())
			getClient().getActiveChar().reviveAnswer(_answer);
		else if (_messageId==614 && getSettings(L2JModsSettings.class).isWeddingEnabled())
						getClient().getActiveChar().EngageAnswer(_answer);
	}


	@Override
	public String getType()
	{
		return _C__C5_DLGANSWER;
	}
}
