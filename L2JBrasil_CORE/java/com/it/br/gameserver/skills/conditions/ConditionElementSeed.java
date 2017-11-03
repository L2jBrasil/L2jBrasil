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

import com.it.br.gameserver.skills.Env;
import com.it.br.gameserver.skills.effects.EffectSeed;


/**
 * @author Advi
 *
 */
public class ConditionElementSeed extends Condition
{
    private static int[] seedSkills = {1285, 1286, 1287};
    private final int[] _requiredSeeds;

    public ConditionElementSeed(int[] seeds)
    {
        _requiredSeeds = seeds;
    }

    ConditionElementSeed(int fire, int water, int wind, int various, int any)
    {
        _requiredSeeds = new int[5];
        _requiredSeeds[0] = fire;
        _requiredSeeds[1] = water;
        _requiredSeeds[2] = wind;
        _requiredSeeds[3] = various;
        _requiredSeeds[4] = any;
    }


	@Override
	public boolean testImpl(Env env)
    {
        int[] Seeds = new int[3];
        for (int i = 0; i < Seeds.length; i++)
        {
            Seeds[i] = (env.player.getFirstEffect(seedSkills[i]) instanceof EffectSeed ? ((EffectSeed)env.player.getFirstEffect(seedSkills[i])).getPower() : 0);
            if (Seeds[i] >= _requiredSeeds[i])
                Seeds[i] -= _requiredSeeds[i];
            else return false;
        }

        if (_requiredSeeds[3] > 0)
        {
            int count = 0;
            for (int i = 0; i < Seeds.length && count < _requiredSeeds[3]; i++)
            {
                if (Seeds[i] > 0)
                {
                    Seeds[i]--;
                    count++;
                }
            }
            if (count < _requiredSeeds[3]) return false;
        }

        if (_requiredSeeds[4] > 0)
        {
            int count = 0;
            for (int i = 0; i < Seeds.length && count < _requiredSeeds[4]; i++)
            {
                count += Seeds[i];
            }
            if (count < _requiredSeeds[4]) return false;
        }

        return true;
    }
}
