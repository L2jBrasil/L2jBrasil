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

import com.it.br.gameserver.script.DateRange;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class manage drop of Special Events created by GM for a defined period.
 * During a Special Event all L2Attackable can drop extra Items.
 * Those extra Items are defined in the table <B>allNpcDateDrops</B>.
 * Each Special Event has a start and end date to stop to drop extra Items automaticaly.<BR><BR>
 */

public class EventDroplist
{

    //private static Logger _log = LoggerFactory.getLogger(EventDroplist.class);

	private static EventDroplist _instance;

	/** The table containing all DataDrop object */
	private List<DateDrop> _allNpcDateDrops;

	public static EventDroplist getInstance()
	{
		if (_instance == null)
		{
			_instance = new EventDroplist();
		}
		return _instance;
	}


	public class DateDrop
	{
		/** Start and end date of the Event */
        public DateRange dateRange;

		/** The table containing Item identifier that can be dropped as extra Items during the Event */
	    public int[] items;

		/** The min number of Item dropped in one time during this Event */
	    public int min;

		/** The max number of Item dropped in one time during this Event */
	    public int max;

		/** The rate of drop for this Event */
	    public int chance;


	}


	/**
	 * Constructor of EventDroplist.<BR><BR>
	 */
	private EventDroplist()
	{
		_allNpcDateDrops = new ArrayList<>();
	}


	/**
	 * Create and Init a new DateDrop then add it to the allNpcDateDrops of EventDroplist .<BR><BR>
	 *
	 * @param items The table containing all item identifier of this DateDrop
	 * @param count The table containing min and max value of this DateDrop
	 * @param chance The chance to obtain this drop
	 * @param range The DateRange object to add to this DateDrop
	 *
	 */
	public void addGlobalDrop(int[] items, int[] count, int chance, DateRange range)
	{

	    DateDrop date = new DateDrop();

	    date.dateRange = range;
	    date.items = items;
	    date.min = count[0];
	    date.max = count[1];
	    date.chance = chance;

	    _allNpcDateDrops.add(date);
	}

	/**
	 * Return all DateDrop of EventDroplist allNpcDateDrops within the date range.<BR><BR>
	 */
	public List<DateDrop> getAllDrops()
	{
	    List<DateDrop> list = new ArrayList<>();

	    for (DateDrop drop : _allNpcDateDrops)
	    {
	        Date currentDate = new Date();
	        //System.out.println("From: "+drop.from+" To: "+drop.to+" Now: "+ currentDate);
	        if (drop.dateRange.isWithinRange(currentDate))
	        {
	            list.add(drop);
	        }
	    }

	    return list;
	}

}
