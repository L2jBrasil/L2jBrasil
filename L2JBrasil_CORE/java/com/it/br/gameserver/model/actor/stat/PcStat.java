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
package com.it.br.gameserver.model.actor.stat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.model.base.Experience;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import com.it.br.gameserver.network.serverpackets.SocialAction;
import com.it.br.gameserver.network.serverpackets.StatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.network.serverpackets.UserInfo;

public class PcStat extends PlayableStat
{
	private static Logger _log = Logger.getLogger(L2PcInstance.class.getName());

    // =========================================================
    // Data Field

    private int _oldMaxHp;      // stats watch
    private int _oldMaxMp;      // stats watch
    private int _oldMaxCp;		// stats watch

    // =========================================================
    // Constructor
    public PcStat(L2PcInstance activeChar)
    {
        super(activeChar);
    }

    // =========================================================
    // Method - Public

    @Override
	public boolean addExp(long value)
    {
    	L2PcInstance activeChar = getActiveChar();

        // Set new karma
        if (!activeChar.isCursedWeaponEquipped() && activeChar.getKarma() > 0 && (activeChar.isGM() || !activeChar.isInsideZone(L2Character.ZONE_PVP)))
        {
            int karmaLost = activeChar.calculateKarmaLost(value);
            if (karmaLost > 0) activeChar.setKarma(activeChar.getKarma() - karmaLost);
        }
        //Player is Gm and acces level is below or equal to GM_DONT_TAKE_EXPSP and is in party, don't give Xp
        if (getActiveChar().isGM() && getActiveChar().getAccessLevel() <= Config.GM_DONT_TAKE_EXPSP && getActiveChar().isInParty())
              return false;

		if (!super.addExp(value)) return false;

		/* Micht : Use of UserInfo for C5
        StatusUpdate su = new StatusUpdate(activeChar.getObjectId());
        su.addAttribute(StatusUpdate.EXP, getExp());
        activeChar.sendPacket(su);
        */
        activeChar.sendPacket(new UserInfo(activeChar));
        return true;
    }

    /**
     * Add Experience and SP rewards to the L2PcInstance, remove its Karma (if necessary) and Launch increase level task.<BR><BR>
     *
     * <B><U> Actions </U> :</B><BR><BR>
     * <li>Remove Karma when the player kills L2MonsterInstance</li>
     * <li>Send a Server->Client packet StatusUpdate to the L2PcInstance</li>
     * <li>Send a Server->Client System Message to the L2PcInstance </li>
     * <li>If the L2PcInstance increases it's level, send a Server->Client packet SocialAction (broadcast) </li>
     * <li>If the L2PcInstance increases it's level, manage the increase level task (Max MP, Max MP, Recommandation, Expertise and beginner skills...) </li>
     * <li>If the L2PcInstance increases it's level, send a Server->Client packet UserInfo to the L2PcInstance </li><BR><BR>
     *
     * @param addToExp The Experience value to add
     * @param addToSp The SP value to add
     */

    @Override
	public boolean addExpAndSp(long addToExp, int addToSp)
    {
    	float ratioTakenByPet = 0;
    	//Player is Gm and acces level is below or equal to GM_DONT_TAKE_EXPSP and is in party, don't give Xp/Sp
    	L2PcInstance activeChar = getActiveChar();
    	if (activeChar.isGM() && activeChar.getAccessLevel() <= Config.GM_DONT_TAKE_EXPSP && activeChar.isInParty())
    	     return false;

    	// if this player has a pet that takes from the owner's Exp, give the pet Exp now

    	if (activeChar.getPet() instanceof L2PetInstance )
    	{
    		L2PetInstance pet = (L2PetInstance) activeChar.getPet();
    		ratioTakenByPet = pet.getPetData().getOwnerExpTaken();

    		// only give exp/sp to the pet by taking from the owner if the pet has a non-zero, positive ratio
    		// allow possible customizations that would have the pet earning more than 100% of the owner's exp/sp
    		if (ratioTakenByPet > 0 && !pet.isDead())
    			pet.addExpAndSp((long)(addToExp*ratioTakenByPet), (int)(addToSp*ratioTakenByPet));
    		// now adjust the max ratio to avoid the owner earning negative exp/sp
    		if (ratioTakenByPet > 1)
    			ratioTakenByPet = 1;
    		addToExp = (long)(addToExp*(1-ratioTakenByPet));
    		addToSp = (int)(addToSp*(1-ratioTakenByPet));
    	}

    	if (!super.addExpAndSp(addToExp, addToSp)) return false;

        // Send a Server->Client System Message to the L2PcInstance
        SystemMessage sm = new SystemMessage(SystemMessageId.YOU_EARNED_S1_EXP_AND_S2_SP);
        sm.addNumber((int)addToExp);
        sm.addNumber(addToSp);
        getActiveChar().sendPacket(sm);
        return true;
    }


    @Override
	public boolean removeExpAndSp(long addToExp, int addToSp)
    {
        if (!super.removeExpAndSp(addToExp, addToSp)) return false;

        // Send a Server->Client System Message to the L2PcInstance
        SystemMessage sm = new SystemMessage(SystemMessageId.EXP_DECREASED_BY_S1);
        sm.addNumber((int)addToExp);
        getActiveChar().sendPacket(sm);
        sm = new SystemMessage(SystemMessageId.SP_DECREASED_S1);
        sm.addNumber(addToSp);
        getActiveChar().sendPacket(sm);
        return true;
    }


    @Override
	public final boolean addLevel(byte value)
    {
		if (getLevel() + value > Experience.MAX_LEVEL - 1) return false;

        boolean levelIncreased = super.addLevel(value);

        if (levelIncreased)
        {
			/**
			 * If there are no characters on the server, the bonuses will be applied to the first character that becomes level 6
			 *   and end if this character reaches level 25 or above.
			 * If the first character that becomes level 6 is deleted, the rest of the characters may not receive the new character bonus
			 * If the first character to become level 6 loses a level, and the player makes another character level 6,
			 *   the bonus will be applied to only the first character to achieve level 6.
			 * If the character loses a level after reaching level 25, the character may not receive the bonus.
			 */
        	if (!Config.ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE)
        	{
				if (getActiveChar().getLevel() >= Experience.MIN_NEWBIE_LEVEL && getActiveChar().getLevel() < Experience.MAX_NEWBIE_LEVEL && !getActiveChar().isNewbie())
	        	{
	        		Connection con = null;
					try
					{
						con = L2DatabaseFactory.getInstance().getConnection();
						PreparedStatement statement;

						statement = con.prepareStatement("SELECT value FROM account_data WHERE (account_name=?) AND (var='newbie_char')");
						statement.setString(1, getActiveChar().getAccountName());
						ResultSet rset = statement.executeQuery();

						if (!rset.next())
						{
							PreparedStatement statement1;
							statement1 = con.prepareStatement("INSERT INTO account_data (account_name, var, value) VALUES (?, 'newbie_char', ?)");
                                                        statement1.setString(1, getActiveChar().getAccountName());
							statement1.setInt(2, getActiveChar().getObjectId());
							statement1.executeUpdate();
							statement1.close();

							getActiveChar().setNewbie(true);
							if (Config.DEBUG) _log.info("New newbie character: " + getActiveChar().getCharId());
						};
						rset.close();
						statement.close();
					}
					catch (SQLException e)
					{
						_log.warning("Could not check character for newbie: " + e);
					}
					finally
					{
						try { con.close(); } catch (Exception e) {}
					}
	        	};

	        	if (getActiveChar().getLevel() >= 25 && getActiveChar().isNewbie())
	        	{
	        		getActiveChar().setNewbie(false);
					if (Config.DEBUG) _log.info("Newbie character ended: " + getActiveChar().getCharId());
	        	};
        	};

            getActiveChar().setCurrentCp(getMaxCp());
            getActiveChar().broadcastPacket(new SocialAction(getActiveChar().getObjectId(), 15));
            getActiveChar().sendPacket(new SystemMessage(SystemMessageId.YOU_INCREASED_YOUR_LEVEL));
        }

        getActiveChar().rewardSkills(); // Give Expertise skill of this level
        if (getActiveChar().getClan() != null)
        {
        	getActiveChar().getClan().updateClanMember(getActiveChar());
        	getActiveChar().getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdate(getActiveChar()));
        }
        if (getActiveChar().isInParty()) getActiveChar().getParty().recalculatePartyLevel(); // Recalculate the party level

        StatusUpdate su = new StatusUpdate(getActiveChar().getObjectId());
        su.addAttribute(StatusUpdate.LEVEL, getLevel());
        su.addAttribute(StatusUpdate.MAX_CP, getMaxCp());
        su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
        su.addAttribute(StatusUpdate.MAX_MP, getMaxMp());
        getActiveChar().sendPacket(su);

        // Update the overloaded status of the L2PcInstance
        getActiveChar().refreshOverloaded();
        // Update the expertise status of the L2PcInstance
        getActiveChar().refreshExpertisePenalty();
        // Send a Server->Client packet UserInfo to the L2PcInstance
        getActiveChar().sendPacket(new UserInfo(getActiveChar()));
        return levelIncreased;
    }


    @Override
	public boolean addSp(int value)
    {
        if (!super.addSp(value)) return false;
    
        StatusUpdate su = new StatusUpdate(getActiveChar().getObjectId());
        su.addAttribute(StatusUpdate.SP, getSp());
        getActiveChar().sendPacket(su);
        return true;
    }


    @Override
	public final long getExpForLevel(int level) 
    { 
        return Experience.LEVEL[level]; 
    }

    // =========================================================
    // Method - Private

    // =========================================================
    // Property - Public

    @Override
	public final L2PcInstance getActiveChar() 
    { 
        return (L2PcInstance)super.getActiveChar(); 
    }


    @Override
	public final long getExp()
    {
        if (getActiveChar().isSubClassActive()) 
	        return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getExp();
        return super.getExp();
    }
    

    @Override
	public final void setExp(long value)
    {
        if (getActiveChar().isSubClassActive())
            getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setExp(value);
        else
            super.setExp(value);
    }


    @Override
	public final byte getLevel()
    {
    	try
    	{
	        if (getActiveChar().isSubClassActive()) 
	        	return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getLevel();
	        	
	        return super.getLevel();
    	}
    	catch(NullPointerException e)
    	{
    		return 0;
    	}
    }


    @Override
	public final void setLevel(byte value)
    {
		if (value > Experience.MAX_LEVEL - 1) 
			value = Experience.MAX_LEVEL - 1;
        	
        if (getActiveChar().isSubClassActive())
            getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setLevel(value);
        else
            super.setLevel(value);
    }


    @Override
	public final int getMaxHp()
    {
        // Get the Max HP (base+modifier) of the L2PcInstance
        int val = super.getMaxHp();
        if (val != _oldMaxHp)
        {
            _oldMaxHp = val;

            // Launch a regen task if the new Max HP is higher than the old one
            if (getActiveChar().getStatus().getCurrentHp() != val) getActiveChar().getStatus().setCurrentHp(getActiveChar().getStatus().getCurrentHp()); // trigger start of regeneration
        }

        return val;
    }


    @Override
	public final int getMaxMp()
    {
        // Get the Max MP (base+modifier) of the L2PcInstance
        int val = super.getMaxMp();    
        if (val != _oldMaxMp)
        {
            _oldMaxMp = val;

            // Launch a regen task if the new Max MP is higher than the old one
            if (getActiveChar().getStatus().getCurrentMp() != val) 
            	getActiveChar().getStatus().setCurrentMp(getActiveChar().getStatus().getCurrentMp()); // trigger start of regeneration
        }
        return val;
    }
	
 
	@Override
	public final int getMaxCp() 
	{ 
		int val = super.getMaxCp(); 
		if (val != _oldMaxCp) 
		{ 
			_oldMaxCp = val; 
			if (getActiveChar().getStatus().getCurrentCp() != val) 
			{ 
				getActiveChar().getStatus().setCurrentCp(getActiveChar().getStatus().getCurrentCp()); 
			} 
		} 
		return val; 
	} 

    @Override
	public final int getSp()
    {
        if (getActiveChar().isSubClassActive()) 
        	return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getSp();
        	
        return super.getSp();
    }
	

    @Override
	public final void setSp(int value)
    {
        if (getActiveChar().isSubClassActive())
            getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setSp(value);
        else
            super.setSp(value);
    }
}
