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
package com.it.br.gameserver.model.actor.instance;

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.network.L2GameClient;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.templates.L2NpcTemplate;

public class L2EffectPointInstance extends L2NpcInstance
{
    private L2Character _owner;

    public L2EffectPointInstance(int objectId, L2NpcTemplate template, L2Character owner)
    {
    	super(objectId, template);
    	_owner = owner;
    }
    
    public L2Character getOwner()
    {
    	return _owner;
    }
    
    /**
	 * this is called when a player interacts with this NPC
	 * @param player
	 */

	@Override
	public void onAction(L2PcInstance player)
	{
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(new ActionFailed());
	}
	

    @Override
	public void onActionShift(L2GameClient client)
    {
        L2PcInstance player = client.getActiveChar();
        if (player == null) return;
        player.sendPacket(new ActionFailed());
    }
}