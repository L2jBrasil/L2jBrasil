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
package com.it.br.gameserver.skills.conditions;

//import java.util.logging.Logger;

import com.it.br.gameserver.skills.Env;



/**
 * @author mkizub
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Condition implements ConditionListener {

	//private static final Logger _log = Logger.getLogger(Condition.class.getName());

	private ConditionListener _listener;
	private String _msg;
	private boolean _result;

	public final void setMessage(String msg)
	{
		_msg = msg;
	}

	public final String getMessage()
	{
		return _msg;
	}

	void setListener(ConditionListener listener)
	{
		_listener = listener;
		notifyChanged();
	}

	final ConditionListener getListener()
	{
		return _listener;
	}

	public final boolean test(Env env)
	{
		boolean res = testImpl(env);
		if (_listener != null && res != _result)
		{
			_result = res;
			notifyChanged();
		}
		return res;
	}

	abstract boolean testImpl(Env env);


	public void notifyChanged()
	{
		if (_listener != null)
			_listener.notifyChanged();
	}
}
