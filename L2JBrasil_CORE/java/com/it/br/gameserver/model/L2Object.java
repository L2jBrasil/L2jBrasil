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
package com.it.br.gameserver.model;

import com.it.br.Config;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.instancemanager.ItemsOnGroundManager;
import com.it.br.gameserver.instancemanager.MercTicketManager;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.knownlist.ObjectKnownList;
import com.it.br.gameserver.model.actor.poly.ObjectPoly;
import com.it.br.gameserver.model.actor.position.ObjectPosition;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.network.L2GameClient;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.GetItem;

public abstract class L2Object
{
    // Data Field
    private boolean _isVisible;                 // Object visibility
    private ObjectKnownList _knownList;
    private String _name;
    private int _objectId;                      // Object identifier
    private ObjectPoly _poly;
    private ObjectPosition _position;
    private int _instanceId = 0;

    // Constructor
    public L2Object(int objectId)
    {
        _objectId = objectId;
    }

    // Event - Public
    public void onAction(L2PcInstance player)
    {
        player.sendPacket(new ActionFailed());
    }

    public void onActionShift(L2GameClient client)
    {
        client.getActiveChar().sendPacket(new ActionFailed());
    }

    public void onForcedAttack(L2PcInstance player)
    {
        player.sendPacket(new ActionFailed());
    }

    public void onSpawn(){}

    // Position - Should remove to fully move to L2ObjectPosition
    public final void setXYZ(int x, int y, int z)
    {
        getPosition().setXYZ(x, y, z);
    }

    public final void setXYZInvisible(int x, int y, int z)
    {
        getPosition().setXYZInvisible(x, y, z);
    }

    public final int getX()
    {
        if (Config.ASSERT) assert getPosition().getWorldRegion() != null || _isVisible;
        return getPosition().getX();
    }
    /**
	 * @return The id of the instance zone the object is in - id 0 is global
	 * since everything like dropped items, mobs, players can be in a instanciated area, it must be in l2object
	 */
	public int getInstanceId()
	{
		return _instanceId;
	}
	
	/**
	 * @param instanceId The id of the instance zone the object is in - id 0 is global
	 */
	public void setInstanceId(int instanceId)
	{
		if (_instanceId == instanceId)
			return;
			
			if (((L2PcInstance)this).getPet() != null)
				((L2PcInstance)this).getPet().setInstanceId(instanceId);
		
		_instanceId = instanceId;

		// If we change it for visible objects, me must clear & revalidate knownlists
		if (_isVisible && _knownList != null)
		{
			if (this instanceof L2PcInstance)
			{
				// We don't want some ugly looking disappear/appear effects, so don't update
				// the knownlist here, but players usually enter instancezones through teleporting
				// and the teleport will do the revalidation for us.
			}
			else
			{
				decayMe();
				spawnMe();
			}
		}
	}
    public final int getY()
    {
        if (Config.ASSERT) assert getPosition().getWorldRegion() != null || _isVisible;
        return getPosition().getY();
    }

    public final int getZ()
    {
        if (Config.ASSERT) assert getPosition().getWorldRegion() != null || _isVisible;
        return getPosition().getZ();
    }

    // Method - Public
    /**
     * Remove a L2Object from the world.<BR><BR>
     *
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Remove the L2Object from the world</li><BR><BR>
     *
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _allObjects of L2World </B></FONT><BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packets to players</B></FONT><BR><BR>
     *
     * <B><U> Assert </U> :</B><BR><BR>
     * <li> _worldRegion != null <I>(L2Object is visible at the beginning)</I></li><BR><BR>
     *
     * <B><U> Example of use </U> :</B><BR><BR>
     * <li> Delete NPC/PC or Unsummon</li><BR><BR>
     *
     */
    public final void decayMe()
    {
        if (Config.ASSERT) 
        	assert getPosition().getWorldRegion() != null;

        L2WorldRegion reg = getPosition().getWorldRegion();

        synchronized (this)
        {
            _isVisible = false;
            getPosition().setWorldRegion(null);
        }

        // this can synchronize on others instancies, so it's out of
        // synchronized, to avoid deadlocks
        // Remove the L2Object from the world
        L2World.getInstance().removeVisibleObject(this, reg);
        L2World.getInstance().removeObject(this);
        if (Config.SAVE_DROPPED_ITEM)
        	ItemsOnGroundManager.getInstance().removeObject(this);
    }


    /**
     * Remove a L2ItemInstance from the world and send server->client GetItem packets.<BR><BR>
     *
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Send a Server->Client Packet GetItem to player that pick up and its _knowPlayers member </li>
     * <li>Remove the L2Object from the world</li><BR><BR>
     *
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _allObjects of L2World </B></FONT><BR><BR>
     *
     * <B><U> Assert </U> :</B><BR><BR>
     * <li> this instanceof L2ItemInstance</li>
     * <li> _worldRegion != null <I>(L2Object is visible at the beginning)</I></li><BR><BR>
     *
     * <B><U> Example of use </U> :</B><BR><BR>
     * <li> Do Pickup Item : PCInstance and Pet</li><BR><BR>
     *
     * @param player Player that pick up the item
     *
     */
    public final void pickupMe(L2Character player) // NOTE: Should move this function into L2ItemInstance because it does not apply to L2Character
    {
        if (Config.ASSERT) assert this instanceof L2ItemInstance;
        if (Config.ASSERT) assert getPosition().getWorldRegion() != null;

        L2WorldRegion oldregion = getPosition().getWorldRegion();

        // Create a server->client GetItem packet to pick up the L2ItemInstance
        GetItem gi = new GetItem((L2ItemInstance)this, player.getObjectId());
        player.broadcastPacket(gi);

        synchronized (this)
        {
            _isVisible = false;
            getPosition().setWorldRegion(null);
        }

        // if this item is a mercenary ticket, remove the spawns!
        if (this instanceof L2ItemInstance)
        {
        	int itemId = ((L2ItemInstance)this).getItemId();
        	if (MercTicketManager.getInstance().getTicketCastleId(itemId) > 0)
        	{
        		MercTicketManager.getInstance().removeTicket((L2ItemInstance)this);
        		ItemsOnGroundManager.getInstance().removeObject(this);
        	}
        	if (itemId == 57 || itemId == 6353)
			{
				QuestState qs = null;
				if (player instanceof L2Summon)
				{
					qs = ((L2Summon) player).getOwner().getQuestState("255_Tutorial");
					if (qs != null) {
						qs.getQuest().notifyEvent("CE" + itemId + "", null, ((L2Summon) player).getOwner());
					}
				}
				else if (player instanceof L2PcInstance)
				{
					qs = ((L2PcInstance) player).getQuestState("255_Tutorial");
					if (qs != null) {
						qs.getQuest().notifyEvent("CE" + itemId + "", null, (L2PcInstance) player);
					}
				}
			}
		}

        // this can synchronize on others instancies, so it's out of
        // synchronized, to avoid deadlocks
        // Remove the L2ItemInstance from the world
        L2World.getInstance().removeVisibleObject(this, oldregion);
    }

    public void refreshID()
    {
        L2World.getInstance().removeObject(this);
        IdFactory.getInstance().releaseId(getObjectId());
        _objectId = IdFactory.getInstance().getNextId();
    }

    /**
     * Init the position of a L2Object spawn and add it in the world as a visible object.<BR><BR>
     *
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Set the x,y,z position of the L2Object spawn and update its _worldregion </li>
     * <li>Add the L2Object spawn in the _allobjects of L2World </li>
     * <li>Add the L2Object spawn to _visibleObjects of its L2WorldRegion</li>
     * <li>Add the L2Object spawn in the world as a <B>visible</B> object</li><BR><BR>
     *
     * <B><U> Assert </U> :</B><BR><BR>
     * <li> _worldRegion == null <I>(L2Object is invisible at the beginning)</I></li><BR><BR>
     *
     * <B><U> Example of use </U> :</B><BR><BR>
     * <li> Create Door</li>
     * <li> Spawn : Monster, Minion, CTs, Summon...</li><BR>
     *
     */
    public final void spawnMe()
    {
        if (Config.ASSERT) assert getPosition().getWorldRegion() == null && getPosition().getWorldPosition().getX() != 0 && getPosition().getWorldPosition().getY() != 0 && getPosition().getWorldPosition().getZ() != 0;

        synchronized (this)
        {
            // Set the x,y,z position of the L2Object spawn and update its _worldregion
            _isVisible = true;
            getPosition().setWorldRegion(L2World.getInstance().getRegion(getPosition().getWorldPosition()));

            // Add the L2Object spawn in the _allobjects of L2World
            L2World.getInstance().storeObject(this);

            // Add the L2Object spawn to _visibleObjects and if necessary to _allplayers of its L2WorldRegion
            getPosition().getWorldRegion().addVisibleObject(this);
        }

        // this can synchronize on others instancies, so it's out of
        // synchronized, to avoid deadlocks
        // Add the L2Object spawn in the world as a visible object
        L2World.getInstance().addVisibleObject(this, getPosition().getWorldRegion(), null);

        onSpawn();
    }

	public final void spawnMe(int x, int y, int z)
	{
		if(Config.ASSERT)
		{
			assert getPosition().getWorldRegion() == null;
		}

		synchronized (this)
		{
			// Set the x,y,z position of the L2Object spawn and update its _worldregion
			_isVisible = true;

			if(x > L2World.MAP_MAX_X)
			{
				x = L2World.MAP_MAX_X - 5000;
			}
			if(x < L2World.MAP_MIN_X)
			{
				x = L2World.MAP_MIN_X + 5000;
			}
			if(y > L2World.MAP_MAX_Y)
			{
				y = L2World.MAP_MAX_Y - 5000;
			}
			if(y < L2World.MAP_MIN_Y)
			{
				y = L2World.MAP_MIN_Y + 5000;
			}

			getPosition().setWorldPosition(x, y, z);
			getPosition().setWorldRegion(L2World.getInstance().getRegion(getPosition().getWorldPosition()));
		}

		// these can synchronize on others instances, so they're out of
		// synchronized, to avoid deadlocks
		// Add the L2Object spawn in the _allobjects of L2World
		L2World.getInstance().storeObject(this);

		// Add the L2Object spawn to _visibleObjects and if necessary to _allplayers of its L2WorldRegion
		L2WorldRegion region = getPosition().getWorldRegion();
		if(region!=null)
			region.addVisibleObject(this);
		else{
			System.out.println("ATTENTION: no region found for location "+x+","+y+","+z+". It's not possible to spawn object "+_objectId+" here...");
			return;
		}
		// this can synchronize on others instances, so it's out of
		// synchronized, to avoid deadlocks
		// Add the L2Object spawn in the world as a visible object
		L2World.getInstance().addVisibleObject(this, region, null);

		onSpawn();
	}

    public void toggleVisible()
    {
        if (isVisible())
            decayMe();
        else
            spawnMe();
    }

    public boolean isAttackable()
    {
        return false;
    }

    public abstract boolean isAutoAttackable(L2Character attacker);

    public boolean isMarker()
    {
        return false;
    }

    /**
     * Return the visibilty state of the L2Object. <BR><BR>
     *
     * <B><U> Concept</U> :</B><BR><BR>
     * A L2Object is visble if <B>__IsVisible</B>=true and <B>_worldregion</B>!=null <BR><BR>
     */
    public final boolean isVisible()
    {
        //return getPosition().getWorldRegion() != null && _IsVisible;
        return getPosition().getWorldRegion() != null;
    }
    public final void setIsVisible(boolean value)
    {
        _isVisible = value;
        if (!_isVisible) getPosition().setWorldRegion(null);
    }

    public ObjectKnownList getKnownList()
    {
        if (_knownList == null) _knownList = new ObjectKnownList(this);
        return _knownList;
    }
    public final void setKnownList(ObjectKnownList value) { _knownList = value; }

    public final String getName()
    {
        return _name;
    }
    public final void setName(String value)
    {
        _name = value;
    }

    public final int getObjectId()
    {
        return _objectId;
    }

    public final ObjectPoly getPoly()
    {
        if (_poly == null) _poly = new ObjectPoly(this);
        return _poly;
    }

    public final ObjectPosition getPosition()
    {
        if (_position == null) _position = new ObjectPosition(this);
        return _position;
    }

    /**
     * returns reference to region this object is in
     */
    public L2WorldRegion getWorldRegion()
    {
        return getPosition().getWorldRegion();
    }	

	@Override
	public String toString()
    {
        return "" + getObjectId();
    }
    
    public L2PcInstance getActingPlayer()
	{
		return null;
	}
}