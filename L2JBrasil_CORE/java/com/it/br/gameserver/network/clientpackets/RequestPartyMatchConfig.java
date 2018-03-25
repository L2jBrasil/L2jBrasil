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

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.2 $ $Date: 2005/03/27 15:29:30 $
 */

public final class RequestPartyMatchConfig extends L2GameClientPacket
{
	private static final String _C__6F_REQUESTPARTYMATCHCONFIG = "[C] 6F RequestPartyMatchConfig";
	//private static Logger _log = LoggerFactory.getLogger(RequestPartyMatchConfig.class);

	private int _automaticRegistration;
	private int _showLevel;
	private int _showClass;
	private String _memo;



	@Override
	protected void readImpl()
	{
		_automaticRegistration    = readD();
		_showLevel                = readD();
		_showClass                = readD();

        /*
         *  TODO: Check if this this part of the packet has been
         *  removed by latest versions.
         *
		try
        {
            _memo                 = readS();
        }
		catch (BufferUnderflowException e)
        {
            _memo                 = "";
            _log.warn("Memo field non existant in packet. Notify devs.");
            e.printStackTrace();
        }*/
	}


	@Override
	protected void runImpl()
	{
		// TODO: this packet is currently for creating a new party room
		if (getClient().getActiveChar() == null)
		    return;

		getClient().getActiveChar().setPartyMatchingAutomaticRegistration(_automaticRegistration == 1);
		getClient().getActiveChar().setPartyMatchingShowLevel(_showLevel == 1);
		getClient().getActiveChar().setPartyMatchingShowClass(_showClass == 1);
		getClient().getActiveChar().setPartyMatchingMemo(_memo);
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.clientpackets.ClientBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _C__6F_REQUESTPARTYMATCHCONFIG;
	}
}
