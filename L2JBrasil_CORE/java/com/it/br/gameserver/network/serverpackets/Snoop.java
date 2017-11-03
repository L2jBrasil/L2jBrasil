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

public class Snoop extends L2GameServerPacket
{
  private static final String _S__D5_SNOOP = "[S] D5 Snoop";
  private int _convoId;
  private String _name;
  private int _type;
  private String _speaker;
  private String _msg;

  public Snoop(int id, String name, int type, String speaker, String msg)
  {
    this._convoId = id;
    this._name = name;
    this._type = type;
    this._speaker = speaker;
    this._msg = msg;
  }

  protected void writeImpl()
  {
    writeC(213);
    writeD(this._convoId);
    writeS(this._name);
    writeD(0);
    writeD(this._type);
    writeS(this._speaker);
    writeS(this._msg);
  }

  public String getType()
  {
		return _S__D5_SNOOP;
	}
}