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

import com.it.br.gameserver.model.L2Macro;

/**
 * packet type id 0xe7
 *
 * sample
 *
 * e7
 * d // unknown change of Macro edit,add,delete
 * c // unknown
 * c //count of Macros
 * c // unknown
 *
 * d // id
 * S // macro name
 * S // desc
 * S // acronym
 * c // icon
 * c // count
 *
 * c // entry
 * c // type
 * d // skill id
 * c // shortcut id
 * S // command name
 *
 * format:		cdhcdSSScc (ccdcS)
 * @param decrypt
 */
public class SendMacroList extends L2GameServerPacket
{
	private static final String _S__E7_SENDMACROLIST = "[S] E7 SendMacroList";

	private final int _rev;
	private final int _count;
	private final L2Macro _macro;

	public SendMacroList(int rev, int count, L2Macro macro) {
		_rev = rev;
		_count = count;
		_macro = macro;
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0xE7);

		writeD(_rev); // macro change revision (changes after each macro edition)
		writeC(0);    //unknown
		writeC(_count);    //count of Macros
		writeC(_macro != null ? 1 : 0);    //unknown

		if (_macro != null) {
			writeD(_macro.id); 	//Macro ID
			writeS(_macro.name); 	//Macro Name
			writeS(_macro.descr); 	//Desc
			writeS(_macro.acronym); 	//acronym
			writeC(_macro.icon);		//icon

			writeC(_macro.commands.length);		//count

			for (int i=0; i < _macro.commands.length; i++) {
				L2Macro.L2MacroCmd cmd = _macro.commands[i];
				writeC(i+1);		//i of count
				writeC(cmd.type);		//type  1 = skill, 3 = action, 4 = shortcut
				writeD(cmd.d1);		// skill id
				writeC(cmd.d2);		// shortcut id
				writeS(cmd.cmd);	// command name
			}
		}

//		writeD(1); //unknown change of Macro edit,add,delete
//		writeC(0); //unknown
//		writeC(1);  //count of Macros
//		writeC(1);       //unknown
//
//		writeD(1430); 	//Macro ID
//		writeS("Admin"); 	//Macro Name
//		writeS("Admin Command"); 	//Desc
//		writeS("ADM"); 	//acronym
//		writeC(0);		//icon
//		writeC(2);		//count
//
//		writeC(1);		//i of count
//		writeC(3);		//type  1 = skill, 3 = action, 4 = shortcut
//		writeD(0);		// skill id
//		writeC(0);		// shortcut id
//		writeS("/loc");	// command name
//
//		writeC(2);		//i of count
//		writeC(3);		//type  1 = skill, 3 = action, 4 = shortcut
//		writeD(0);		// skill id
//		writeC(0);		// shortcut id
//		writeS("//admin");	// command name


	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__E7_SENDMACROLIST;
	}

}
