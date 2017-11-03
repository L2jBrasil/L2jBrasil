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
package com.it.br.gameserver.skills.effects;

import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.skills.Env;

/**
 * @author kerberos_20
 *
 */
public class EffectCharmOfLuck extends L2Effect
{
       public EffectCharmOfLuck(Env env, EffectTemplate template)
       {
              super(env, template);
       }

   
       @Override
	public EffectType getEffectType()
       {
              return EffectType.CHARM_OF_LUCK;
       }

       /** Notify started */
   
       @Override
	public boolean onStart()
       {
		if (getEffected() instanceof L2PlayableInstance)
		{
			((L2PlayableInstance) getEffected()).startCharmOfLuck();
			return true;
		}
		return false;
       }

       /** Notify exited */
   
       @Override
	public void onExit() 
       {
              if (getEffected() instanceof L2PlayableInstance)
                     ((L2PlayableInstance)getEffected()).stopCharmOfLuck (this);
       }


    @Override
	public boolean onActionTime()
    {
          // just stop this effect
          return false;
    }
}
