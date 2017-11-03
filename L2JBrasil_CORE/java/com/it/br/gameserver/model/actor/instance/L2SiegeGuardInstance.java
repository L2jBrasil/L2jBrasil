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
package com.it.br.gameserver.model.actor.instance;

import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.ai.L2CharacterAI;
import com.it.br.gameserver.ai.L2SiegeGuardAI;
import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.L2CharPosition;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.knownlist.SiegeGuardKnownList;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.MyTargetSelected;
import com.it.br.gameserver.network.serverpackets.SocialAction;
import com.it.br.gameserver.network.serverpackets.StatusUpdate;
import com.it.br.gameserver.network.serverpackets.ValidateLocation;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.util.Rnd;

/**
 * This class represents all guards in the world. It inherits all methods from
 * L2Attackable and adds some more such as tracking PK's or custom interactions.
 *
 * @version $Revision: 1.11.2.1.2.7 $ $Date: 2005/04/06 16:13:40 $
 */
public final class L2SiegeGuardInstance extends L2Attackable
{
    @SuppressWarnings("hiding")
	private static Logger _log = Logger.getLogger(L2GuardInstance.class.getName());

    private int _homeX;
    private int _homeY;
    private int _homeZ;

    public L2SiegeGuardInstance(int objectId, L2NpcTemplate template)
    {
        super(objectId, template);
        getKnownList(); //inits the knownlist
    }


    @Override
	public final SiegeGuardKnownList getKnownList()
    {
    	if(super.getKnownList() == null || !(super.getKnownList() instanceof SiegeGuardKnownList))
    		setKnownList(new SiegeGuardKnownList(this));
    	return (SiegeGuardKnownList)super.getKnownList();
    }


	@Override
	public L2CharacterAI getAI()
	{
		synchronized(this)
		{
			if (_ai == null)
				_ai = new L2SiegeGuardAI(new AIAccessor());
		}
		return _ai;
	}

	/**
	 * Return True if a siege is in progress and the L2Character attacker isn't a Defender.<BR><BR>
	 *
	 * @param attacker The L2Character that the L2SiegeGuardInstance try to attack
	 *
	 */

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		// Attackable during siege by all except defenders ( Castle or Fort )
		return attacker != null && attacker instanceof L2PcInstance && (getCastle() != null && getCastle().getCastleId() > 0 && getCastle().getSiege().getIsInProgress() && !getCastle().getSiege().checkIsDefender(((L2PcInstance) attacker).getClan()));
	}
 
    @Override
	public boolean hasRandomAnimation() 
    { 
       return false; 
    } 
    /**
     * Sets home location of guard. Guard will always try to return to this location after
     * it has killed all PK's in range.
     *
     */
    public void getHomeLocation()
    {
        _homeX = getX();
        _homeY = getY();
        _homeZ = getZ();

        if (Config.DEBUG)
            _log.finer(getObjectId()+": Home location set to"+
                    " X:" + _homeX + " Y:" + _homeY + " Z:" + _homeZ);
    }

    public int getHomeX()
    {
    	return _homeX;
    }

    public int getHomeY()
    {
    	return _homeY;
    }

    /**
     * This method forces guard to return to home location previously set
     *
     */

	@Override
	public void returnHome()
    {
        if (!isInsideRadius(_homeX, _homeY, 40, false))
        {
            if (Config.DEBUG) _log.fine(getObjectId()+": moving home");
            setisReturningToSpawnPoint(true);    
            clearAggroList();
            
            if (hasAI())
                getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(_homeX, _homeY, _homeZ, 0));
        }
    }

	/**
	* Custom onAction behaviour. Note that super() is not called because guards need
	* extra check to see if a player should interact or ATTACK them when clicked.
	* 
	*/

	@Override
	public void onAction(L2PcInstance player)
	{
		if (!canTarget(player)) return;

		// Check if the L2PcInstance already target the L2NpcInstance
		if (this != player.getTarget())
		{
			if (Config.DEBUG) _log.fine("new target selected:"+getObjectId());

			// Set the target of the L2PcInstance player
			player.setTarget(this);

			// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);

			// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
			StatusUpdate su = new StatusUpdate(getObjectId());
			su.addAttribute(StatusUpdate.CUR_HP, (int)getStatus().getCurrentHp() );
			su.addAttribute(StatusUpdate.MAX_HP, getMaxHp() );
			player.sendPacket(su);

			// Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client
			player.sendPacket(new ValidateLocation(this));
		}
		else
		{
			if (isAutoAttackable(player) && !isAlikeDead())
			{
				if (Math.abs(player.getZ() - getZ()) < 600) // this max heigth difference might need some tweaking
				{
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
				}
				else
				{
					// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
					player.sendPacket(new ActionFailed());
				}
			}
			if(!isAutoAttackable(player))
			{
				if (!canInteract(player))
				{
					// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
				}
				else 
				{
					SocialAction sa = new SocialAction(getObjectId(), Rnd.nextInt(8));
					broadcastPacket(sa);
					sendPacket(sa);
					showChatWindow(player, 0);
				}
			}
		}
	}


    @Override
	public void addDamageHate(L2Character attacker, int damage, int aggro)
    {
        if (attacker == null)
            return;

        if (!(attacker instanceof L2SiegeGuardInstance))
        {
            super.addDamageHate(attacker, damage, aggro);
        }
    }
}
