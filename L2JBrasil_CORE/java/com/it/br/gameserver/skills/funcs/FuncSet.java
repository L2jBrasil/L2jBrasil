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
import com.it.br.gameserver.skills.Stats;



public class FuncSet extends Func {
	private final Lambda _lambda;
	public FuncSet(Stats pStat, int pOrder, Object owner, Lambda lambda) {
		super(pStat, pOrder, owner);
		_lambda = lambda;
	}

	@Override
	public void calc(Env env)
	{
		if (cond == null || cond.test(env))
			env.value = _lambda.calc(env);
	}
}
