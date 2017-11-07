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
package com.it.br.gameserver.datatables;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.datatables.xml.HennaTable;
import com.it.br.gameserver.model.L2HennaInstance;
import com.it.br.gameserver.model.base.ClassId;
import com.it.br.gameserver.templates.L2Henna;
/**
 * This class ...
 *
 * @version $Revision$ $Date$
 */
public class HennaTreeTable
{
	private static Logger _log = Logger.getLogger(HennaTreeTable.class.getName());
	private static final HennaTreeTable _instance = new HennaTreeTable();
	private Map<ClassId, List<L2HennaInstance>> _hennaTrees;
	private boolean _initialized = true;

	public static HennaTreeTable getInstance()
	{
		return _instance;
	}

	private HennaTreeTable()
	{
		_hennaTrees = new HashMap<>();
		int classId = 0;
        int count   = 0;
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();)
		{
			PreparedStatement statement = con.prepareStatement("SELECT class_name, id, parent_id FROM class_list ORDER BY id");
			ResultSet classlist = statement.executeQuery();
			List<L2HennaInstance> list;
			//int parentClassId;
			//L2Henna henna;
			while (classlist.next())
			{
				list = new ArrayList<>();
				classId = classlist.getInt("id");
				PreparedStatement statement2 = con.prepareStatement("SELECT class_id, symbol_id FROM henna_trees where class_id=? ORDER BY symbol_id");
				statement2.setInt(1, classId);
				ResultSet hennatree = statement2.executeQuery();


				while (hennatree.next())
				{
					int id = hennatree.getInt("symbol_id");
					//String name = hennatree.getString("name");
					L2Henna template = HennaTable.getInstance().getTemplate(id);
                    if(template == null)
                    {
                    	hennatree.close();
                    	statement2.close();
                    	classlist.close();
                    	statement.close();
                        return;
                    }
			    	L2HennaInstance temp = new L2HennaInstance(template);
					temp.setSymbolId(id);
					temp.setItemIdDye(template.getDyeId());
					temp.setAmountDyeRequire(template.getAmountDyeRequire());
					temp.setPrice(template.getPrice());
					temp.setStatINT(template.getStatINT());
					temp.setStatSTR(template.getStatSTR());
					temp.setStatCON(template.getStatCON());
					temp.setStatMEM(template.getStatMEM());
					temp.setStatDEX(template.getStatDEX());
					temp.setStatWIT(template.getStatWIT());

					list.add(temp);
				}
				_hennaTrees.put(ClassId.values()[classId], list);
				hennatree.close();
				statement2.close();
                count   += list.size();
				_log.fine("Henna Tree for Class: " + classId + " has " + list.size() + " Henna Templates.");
			}

			classlist.close();
			statement.close();


		}
		catch (Exception e)
		{
			_log.warning("error while creating henna tree for classId "+classId + "  "+e);
			e.printStackTrace();
		}

        _log.config("HennaTreeTable: Loaded " + count + " Henna Tree Templates.");

	}



	public L2HennaInstance[] getAvailableHenna(ClassId classId)
	{
		List<L2HennaInstance> result = new ArrayList<>();
		List<L2HennaInstance> henna = _hennaTrees.get(classId);
		if (henna == null)
		{
			// the hennatree for this class is undefined, so we give an empty list
			_log.warning("Hennatree for class " + classId + " is not defined !");
			return new L2HennaInstance[0];
		}


		for (int i = 0; i < henna.size(); i++)
		{
			L2HennaInstance temp = henna.get(i);
			result.add(temp);
		}

		return result.toArray(new L2HennaInstance[result.size()]);
	}

	public boolean isInitialized()
	{
		return _initialized;
	}

}
