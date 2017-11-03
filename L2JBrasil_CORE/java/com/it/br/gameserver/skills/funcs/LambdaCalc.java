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
package com.it.br.gameserver.skills.funcs;

import com.it.br.gameserver.skills.Env;

/**
 * @author mkizub
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class LambdaCalc extends Lambda {

	public Func[] funcs;
	public LambdaCalc()
	{
		funcs = new Func[0];
	}

	@Override
	public double calc(Env env) {
		double saveValue = env.value;
		try
		{
			env.value = 0;
			for (Func f : funcs)
				f.calc(env);
			return env.value;
		} finally {
			env.value = saveValue;
		}
	}
	public void addFunc(Func f)
	{
		int len = funcs.length;
		Func[] tmp = new Func[len+1];
		for (int i=0; i < len; i++)
			tmp[i] = funcs[i];
		tmp[len] = f;
		funcs = tmp;
	}

}
