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
import com.it.br.gameserver.skills.conditions.Condition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author mkizub
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class FuncTemplate {

	public Condition attachCond;
	public Condition applayCond;
	public final Class<?> func;
	public final Constructor<?> constructor;
	public final Stats stat;
	public final int order;
	public final Lambda lambda;

	public FuncTemplate(Condition pAttachCond, Condition pApplayCond, String pFunc, Stats pStat, int pOrder, Lambda pLambda)
	{
		attachCond = pAttachCond;
		applayCond = pApplayCond;
		stat = pStat;
		order = pOrder;
		lambda = pLambda;
		try {
			func = Class.forName("com.it.br.gameserver.skills.funcs.Func"+pFunc);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		try {
			constructor = func.getConstructor(
				new Class[]{
						Stats.class, // stats to update
						Integer.TYPE, // order of execution
						Object.class, // owner
						Lambda.class // value for function
				});
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public Func getFunc(Env env, Object owner)
	{
		if (attachCond != null && !attachCond.test(env))
			return null;
		try {
			Func f = (Func)constructor.newInstance(stat, order, owner, lambda);
			if (applayCond != null)
				f.setCondition(applayCond);
			return f;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}

	}
}
