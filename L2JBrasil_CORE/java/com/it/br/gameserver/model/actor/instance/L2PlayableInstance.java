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
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Summon;
import com.it.br.gameserver.model.actor.knownlist.PlayableKnownList;
import com.it.br.gameserver.model.actor.stat.PlayableStat;
import com.it.br.gameserver.model.actor.status.PlayableStatus;
import com.it.br.gameserver.templates.L2CharTemplate;

/**
 * This class represents all Playable characters in the world.<BR><BR>
 *
 * L2PlayableInstance :<BR><BR>
 * <li>L2PcInstance</li>
 * <li>L2Summon</li><BR><BR>
 *
 */
public abstract class L2PlayableInstance extends L2Character
{
	private boolean _isNoblesseBlessed = false; // for Noblesse Blessing skill, restores buffs after death
	private boolean _getCharmOfLuck = false; // Charm of Luck - During a Raid/Boss war, decreased chance for death penalty
	private boolean _ProtectionBlessing = false; // Protection for newbies
	private boolean _isPhoenixBlessed = false; // for Soul of The Phoenix or Salvation buffs 
	private boolean _isSoulOfThePhoenix = false;
	public int lastBufferPage = 0;
	/**
	 * Constructor of L2PlayableInstance (use L2Character constructor).<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Call the L2Character constructor to create an empty _skills slot and link copy basic Calculator set to this L2PlayableInstance </li><BR><BR>
	 *
	 * @param objectId Identifier of the object to initialized
	 * @param template The L2CharTemplate to apply to the L2PlayableInstance
	 *
	 */
	public L2PlayableInstance(int objectId, L2CharTemplate template)
	{
		super(objectId, template);
		getKnownList();	// init knownlist
        getStat();			// init stats
        getStatus();		// init status
	}


	@Override
	public PlayableKnownList getKnownList()
    {
    	if(super.getKnownList() == null || !(super.getKnownList() instanceof PlayableKnownList))
    		setKnownList(new PlayableKnownList(this));
    	return (PlayableKnownList)super.getKnownList();
    }


	@Override
	public PlayableStat getStat()
    {
    	if(super.getStat() == null || !(super.getStat() instanceof PlayableStat))
    		setStat(new PlayableStat(this));
    	return (PlayableStat)super.getStat();
    }


	@Override
	public PlayableStatus getStatus()
    {
    	if(super.getStatus() == null || !(super.getStatus() instanceof PlayableStatus))
    		setStatus(new PlayableStatus(this));
    	return (PlayableStatus)super.getStatus();
    }


	@Override
	public boolean doDie(L2Character killer)
    {
    	if (!super.doDie(killer))
    		return false;

    	if (killer != null)
        {
            L2PcInstance player = null;
            if (killer instanceof L2PcInstance)
                player = (L2PcInstance)killer;
            else if (killer instanceof L2Summon)
                player = ((L2Summon)killer).getOwner();
            
            if (player != null) 
            	player.onKillUpdatePvPKarma(this); 
        }
    	return true;
    }

    public boolean checkIfPvP(L2Character target)
    {
        if (target == null) return false;                                               // Target is null
        if (target == this) return false;                                               // Target is self
        if (!(target instanceof L2PlayableInstance)) return false;                      // Target is not a L2PlayableInstance

        L2PcInstance player = null;
        if (this instanceof L2PcInstance)
            player = (L2PcInstance)this;
        else if (this instanceof L2Summon)
            player = ((L2Summon)this).getOwner();

        if (player == null) return false;                                               // Active player is null
        if (player.getKarma() != 0) return false;                                       // Active player has karma

        L2PcInstance targetPlayer = null;
        if (target instanceof L2PcInstance)
            targetPlayer = (L2PcInstance)target;
        else if (target instanceof L2Summon)
            targetPlayer = ((L2Summon)target).getOwner();

        if (targetPlayer == null) return false;                                         // Target player is null
        if (targetPlayer == this) return false;                                         // Target player is self
        if (targetPlayer.getKarma() != 0) return false;                                 // Target player has karma
        if (targetPlayer.getPvpFlag() == 0) return false;

        return true;
        /*  Even at war, there should be PvP flag
        if(
                player.getClan() == null ||
                targetPlayer.getClan() == null ||
                (
                        !targetPlayer.getClan().isAtWarWith(player.getClanId()) &&
                        targetPlayer.getWantsPeace() == 0 &&
                        player.getWantsPeace() == 0
                )
            )
        {
            return true;
        }

        return false;
        */
    }

    /**
	 * Return True.<BR><BR>
	 */

	@Override
	public boolean isAttackable()
    {
        return true;
    }
	//Support for Soul of the Phoenix and Salvation skills 
	public final boolean isPhoenixBlessed() { return _isPhoenixBlessed; } 
	public final void setIsPhoenixBlessed(boolean value) { _isPhoenixBlessed = value; } 
       
	public final void startPhoenixBlessing() 
	{ 
		setIsPhoenixBlessed(true); 
		updateAbnormalEffect(); 
	} 
	public final void stopPhoenixBlessing(L2Effect effect) 
	{ 
		if (effect == null) 
			stopEffects(L2Effect.EffectType.PHOENIX_BLESSING); 
		else 
			removeEffect(effect); 
		setIsPhoenixBlessed(false); 
		updateAbnormalEffect(); 
	} 
	public final boolean isSoulOfThePhoenix()
	{
		return _isSoulOfThePhoenix;
	}

	public final void setIsSoulOfThePhoenix(boolean value)
	{
		_isSoulOfThePhoenix = value;
	}

	public final void startSoulOfThePhoenix()
	{
		setIsSoulOfThePhoenix(true);
		updateAbnormalEffect();
	}

	public final void stopSoulOfThePhoenix(L2Effect effect)
	{
		if (effect == null)
		{
			stopEffects(L2Effect.EffectType.SOUL_OF_THE_PHOENIX);
		}
		else
		{
			removeEffect(effect);
		}
		setIsSoulOfThePhoenix(false);
		updateAbnormalEffect();
	}
	
    // Support for Noblesse Blessing skill, where buffs are retained
    // after resurrect
    public final boolean isNoblesseBlessed() { return _isNoblesseBlessed; }
    public final void setIsNoblesseBlessed(boolean value) { _isNoblesseBlessed = value; }

    public final void startNoblesseBlessing()
    {
    	setIsNoblesseBlessed(true);
    	updateAbnormalEffect();
    }

    public final void stopNoblesseBlessing(L2Effect effect)
    {
    	if (effect == null)
    		stopEffects(L2Effect.EffectType.NOBLESSE_BLESSING);
    	else
    		removeEffect(effect);

    	setIsNoblesseBlessed(false);
    	updateAbnormalEffect();
    }

	public abstract boolean destroyItemByItemId(String process, int itemId, int count, L2Object reference, boolean sendMessage);
	public abstract boolean destroyItem(String process, int objectId, int count, L2Object reference, boolean sendMessage);

	//Charm of Luck - During a Raid/Boss war, decreased chance for death penalty
	public final boolean getCharmOfLuck() { return _getCharmOfLuck; }
	public final void setCharmOfLuck(boolean value) { _getCharmOfLuck = value; }

	public final void startCharmOfLuck()
	{
		setCharmOfLuck(true);
		updateAbnormalEffect();
	}

	public final void stopCharmOfLuck(L2Effect effect)
	{
		if (effect == null)
			stopEffects(L2Effect.EffectType.CHARM_OF_LUCK);
		else
			removeEffect(effect);

		setCharmOfLuck(false);
		updateAbnormalEffect();
	}
	
	public final boolean getProtectionBlessing() { return _ProtectionBlessing; }
	public final void setProtectionBlessing(boolean value) { _ProtectionBlessing = value;}
	
	public void startProtectionBlessing()
	{
		setProtectionBlessing(true);
		updateAbnormalEffect();
	}
	
	/** 
	* @param blessing 
	*/ 
	public void stopProtectionBlessing(L2Effect effect) 
	{ 
		if (effect == null) stopEffects(L2Effect.EffectType.PROTECTION_BLESSING); 
		else removeEffect(effect); 
		setProtectionBlessing(false); 
		updateAbnormalEffect(); 
	}         
}