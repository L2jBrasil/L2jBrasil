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


import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.database.dao.PetsDao;
import com.it.br.gameserver.datatables.xml.L2PetDataTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.it.br.configuration.Configurator.getSettings;

public class PetNameTable
{
	private static Logger _log = Logger.getLogger(PetNameTable.class.getName());

	private static PetNameTable _instance;

	public static PetNameTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new PetNameTable();
		}
		return _instance;
	}

	public boolean doesPetNameExist(String name, int petNpcId)
	{
		return PetsDao.doesPetNameExist(name, petNpcId);
	}

    public boolean isValidPetName(String name)
    {
        boolean result = true;

        if (!isAlphaNumeric(name)) return result;

        Pattern pattern;
        try {
        	ServerSettings serverSettings = getSettings(ServerSettings.class);
            pattern = Pattern.compile(serverSettings.getPetNameTemplate());
        }
        catch (PatternSyntaxException e) // case of illegal pattern
        {
        	_log.warning("ERROR : Pet name pattern of config is wrong!");
            pattern = Pattern.compile(".*");
        }
        Matcher regexp = pattern.matcher(name);
        if (!regexp.matches())
        {
            result = false;
        }
        return result;
    }

	private boolean isAlphaNumeric(String text)
	{
		boolean result = true;
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			if (!Character.isLetterOrDigit(chars[i]))
			{
				result = false;
				break;
			}
		}
		return result;
	}
}
