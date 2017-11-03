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
package com.it.br.gameserver.script;


/**
 * @author Luis Arias
 *
 */
public class IntList
{
    public static int[] parse(String range)
    {
        if (range.contains("-"))
        {
            return getIntegerList(range.split("-"));
        }
        else if (range.contains(","))
        {
            return getIntegerList(range.split(","));
        }

        int[] list = {getInt(range)};
        return list;
    }

    private static int getInt(String number)
    {
        return Integer.parseInt(number);
    }

    private static int[] getIntegerList(String[] numbers)
    {
        int[] list = new int[numbers.length];
        for (int i=0; i<list.length; i++)
            list[i] = getInt(numbers[i]);
        return list;
    }
}
