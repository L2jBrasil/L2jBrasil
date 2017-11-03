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
package com.it.br.gameserver.datatables;

import com.it.br.gameserver.GmListTable;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.xml.HelperBuffTable;

/**
 *
 * @author  Guma
 */
public class dbmanager
{
	public static void reloadAll()
	{
		ItemTable.reload();
		SkillTable.reload();
		GmListTable.getInstance();
		AugmentationData.getInstance();
		ClanTable.getInstance();
		HelperBuffTable.getInstance();
	}

}
