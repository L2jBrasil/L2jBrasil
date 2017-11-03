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
 package com.it.br.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.logging.Logger;

import com.it.br.L2DatabaseFactory;

/**
 *
 *
 * @author Akumu, EFI dev.
 */
 
public abstract class ClanHallSiege
{
	private static final Logger _log = Logger.getLogger(ClanHallSiege.class.getName());
	private Calendar _siegeDate;
	public Calendar _siegeEndDate;	
	private boolean	_inProgress = false;	

	public long restoreSiegeDate(int ClanHallId)
	{
		long res = 0;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT siege_data FROM clanhall_siege WHERE id=?");
			statement.setInt(1, ClanHallId);
			ResultSet rs = statement.executeQuery();

			if (rs.next())
			{
				res = rs.getLong("siege_data");
			}

			rs.close();
			
			statement.close();
			statement = null;
		}
		catch (Exception e)
		{
			_log.warning("Exception: can't get clanhall siege date: " + e);
		}
		finally
		{
			try { con.close(); } catch(Exception e) { }
			con = null;
		}
		return res;
	}

	public void setNewSiegeDate(long siegeDate, int ClanHallId ,int hour)
	{
		Calendar tmpDate = Calendar.getInstance();
		
		if (siegeDate <= System.currentTimeMillis())
		{
			tmpDate.setTimeInMillis(System.currentTimeMillis());
			tmpDate.add(Calendar.DAY_OF_MONTH, 3);
			tmpDate.set(Calendar.DAY_OF_WEEK, 6);
			tmpDate.set(Calendar.HOUR_OF_DAY, hour);
			tmpDate.set(Calendar.MINUTE, 0);
			tmpDate.set(Calendar.SECOND, 0);

			setSiegeDate(tmpDate);
			
			Connection con = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("UPDATE clanhall_siege SET siege_data=? WHERE id = ?");
				statement.setLong(1, getSiegeDate().getTimeInMillis());
				statement.setInt(2, ClanHallId);
				statement.execute();
				statement.close();
				
				statement = null;
			}
			catch (Exception e)
			{
				_log.warning("Exception: can't save clanhall siege date: " + e);
			}
			finally
			{
				try { con.close(); } catch(Exception e) { }
				con = null;
			}
		}
	}

	public final Calendar getSiegeDate()
	{
		return _siegeDate;	
	}

	public final void setSiegeDate(Calendar val)
	{
		_siegeDate = val;
	}

	public final boolean getIsInProgress()
	{
		return _inProgress;
	}

	public final void setIsInProgress(boolean val)
	{
		_inProgress = val;
	}	
}