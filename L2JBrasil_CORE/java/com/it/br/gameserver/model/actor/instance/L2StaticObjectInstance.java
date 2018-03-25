/*
 * $Header: /cvsroot/l2j/L2_Gameserver/java/net/sf/l2j/gameserver/model/L2StaticObjectInstance.java,v 1.3.2.2.2.2 2005/02/04 13:05:27 maximas Exp $
 *
 *
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
package com.it.br.gameserver.model.actor.instance;


import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.cache.HtmCache;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.actor.knownlist.NullKnownList;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.MyTargetSelected;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.ShowTownMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GODSON ROX!
 */
public class L2StaticObjectInstance extends L2Object
{
    private static Logger _log = LoggerFactory.getLogger(L2StaticObjectInstance.class);

    /** The interaction distance of the L2StaticObjectInstance */
    public static final int INTERACTION_DISTANCE = 150;

    private int _staticObjectId;
    private int _type = -1;         // 0 - map signs, 1 - throne , 2 - arena signs
    private int _x;
    private int _y;
    private String _texture;

    /**
     * @return Returns the StaticObjectId.
     */
    public int getStaticObjectId()
    {
        return _staticObjectId;
    }
    /**
     * @param doorId The doorId to set.
     */
    public void setStaticObjectId(int StaticObjectId)
    {
        _staticObjectId = StaticObjectId;
    }
    /**
     */
    public L2StaticObjectInstance(int objectId)
    {
        super(objectId);
        setKnownList(new NullKnownList(this));
    }

    public int getType()
    {
        return _type;
    }

    public void setType(int type)
    {
        _type = type;
    }

    public void setMap(String texture, int x, int y)
    {
        _texture = "town_map."+texture;
        _x = x;
        _y = y;
    }

    private int getMapX()
    {
	return _x;
    }

    private int getMapY()
    {
	return _y;
    }

    /**
     * this is called when a player interacts with this NPC
     * @param player
     */

	@Override
	public void onAction(L2PcInstance player)
    {
	if(_type < 0) _log.info("L2StaticObjectInstance: StaticObject with invalid type! StaticObjectId: "+getStaticObjectId());
        // Check if the L2PcInstance already target the L2NpcInstance
        if (this != player.getTarget())
        {
            // Set the target of the L2PcInstance player
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(getObjectId(), 0));

        } else {

            MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
            player.sendPacket(my);

            // Calculate the distance between the L2PcInstance and the L2NpcInstance
            if (!player.isInsideRadius(this, INTERACTION_DISTANCE, false, false))
            {
                // Notify the L2PcInstance AI with AI_INTENTION_INTERACT
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);

                // Send a Server->Client packet ActionFailed (target is out of interaction range) to the L2PcInstance player
                player.sendPacket(new ActionFailed());
            } else {
			if(_type == 2) {
				String filename = "data/html/signboard.htm";
				String content = HtmCache.getInstance().getHtm(filename);
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

				if (content == null) html.setHtml("<html><body>Signboard is missing:<br>"+filename+"</body></html>");
				else html.setHtml(content);

				player.sendPacket(html);
				player.sendPacket(new ActionFailed());
			} else if(_type == 0) player.sendPacket(new ShowTownMap(_texture, getMapX(), getMapY()));
                    // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
                    player.sendPacket(new ActionFailed());
            }
        }

    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.model.L2Object#isAttackable()
     */

    @Override
	public boolean isAutoAttackable(L2Character attacker)
    {
        return false;
    }
}
