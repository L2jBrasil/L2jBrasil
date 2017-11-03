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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import com.it.br.Config;
import com.it.br.gameserver.ItemsAutoDestroy;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.ai.L2AttackableAI;
import com.it.br.gameserver.ai.L2CharacterAI;
import com.it.br.gameserver.ai.L2SiegeGuardAI;
import com.it.br.gameserver.datatables.EventDroplist;
import com.it.br.gameserver.datatables.EventDroplist.DateDrop;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.instancemanager.CursedWeaponsManager;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.actor.instance.L2FolkInstance;
import com.it.br.gameserver.model.actor.instance.L2GrandBossInstance;
import com.it.br.gameserver.model.actor.instance.L2MinionInstance;
import com.it.br.gameserver.model.actor.instance.L2MonsterInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.model.actor.instance.L2RaidBossInstance;
import com.it.br.gameserver.model.actor.instance.L2SiegeGuardInstance;
import com.it.br.gameserver.model.actor.instance.L2SummonInstance;
import com.it.br.gameserver.model.actor.knownlist.AttackableKnownList;
import com.it.br.gameserver.model.base.SoulCrystal;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.clientpackets.Say2;
import com.it.br.gameserver.network.serverpackets.CreatureSay;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Stats;
import com.it.br.gameserver.templates.L2EtcItemType;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.gameserver.util.Util;
import com.it.br.util.Rnd;

public class L2Attackable extends L2NpcInstance
{

    public final class AggroInfo
    {
        /** The attaker L2Character concerned by this AggroInfo of this L2Attackable */
    	protected L2Character _attacker;

    	/** Hate level of this L2Attackable against the attaker L2Character (hate = damage) */
    	protected int _hate;

        /** Number of damages that the attaker L2Character gave to this L2Attackable */
        protected int _damage;

        AggroInfo(L2Character pAttacker)
        {
            _attacker = pAttacker;
        }

        /**
         * Verify is object is equal to this AggroInfo.<BR><BR>
         */
        @Override
		public boolean equals(Object obj)
        {
        	if (this == obj) 
        		return true;
            if (obj instanceof AggroInfo)
            	return (((AggroInfo)obj)._attacker == _attacker);
            return false;
        }

        @Override
		public int hashCode()
        {
            return _attacker.getObjectId();
        }
    }

    /**
     * This class contains all RewardInfo of the L2Attackable against the any attacker L2Character, based on amount of damage done.<BR><BR>
     *
     * <B><U> Data</U> :</B><BR><BR>
     * <li>attacker : The attaker L2Character concerned by this RewardInfo of this L2Attackable </li>
     * <li>dmg : Total amount of damage done by the attacker to this L2Attackable (summon + own) </li>
     *
     */
    protected final class RewardInfo
    {
        protected L2Character _attacker;
        protected int _dmg = 0;

        public RewardInfo(L2Character pAttacker, int pDmg)
        {
            _attacker = pAttacker;
            _dmg = pDmg;
        }

        public void addDamage(int pDmg)
        {
            _dmg += pDmg;
        }

        @Override
		public boolean equals(Object obj)
        {
            if (this == obj) 
            	return true;
            if (obj instanceof RewardInfo) 
            	return (((RewardInfo)obj)._attacker == _attacker);
            return false;
        }

        @Override
		public int hashCode()
        {
            return _attacker.getObjectId();
        }
    }

    /**
     * This class contains all AbsorberInfo of the L2Attackable against the absorber L2Character.<BR><BR>
     *
     * <B><U> Data</U> :</B><BR><BR>
     * <li>absorber : The attaker L2Character concerned by this AbsorberInfo of this L2Attackable </li>
     *
     */
    public final class AbsorberInfo
    {
        /** The attaker L2Character concerned by this AbsorberInfo of this L2Attackable */
    	protected L2PcInstance _absorber;
    	protected int _crystalId;
    	protected double _absorbedHP;

        /**
         * Constructor of AbsorberInfo.<BR><BR>
         */
        AbsorberInfo(L2PcInstance attacker, int pCrystalId, double pAbsorbedHP)
        {
            _absorber = attacker;
            _crystalId = pCrystalId;
            _absorbedHP = pAbsorbedHP;
        }

        /**
         * Verify is object is equal to this AbsorberInfo.<BR><BR>
         */
        @Override
		public boolean equals(Object obj)
        {
            if (this == obj) 
            	return true;
            if (obj instanceof AbsorberInfo) 
            	return (((AbsorberInfo)obj)._absorber == _absorber);
            return false;
        }

        /**
         * Return the Identifier of the absorber L2Character.<BR><BR>
         */
        @Override
		public int hashCode()
        {
            return _absorber.getObjectId();
        }
    }

    /**
     * This class is used to create item reward lists instead of creating item instances.<BR><BR>
     */
    public final class RewardItem
    {
        protected int _itemId;
        protected int _count;

        public RewardItem(int itemId, int count)
        {
            _itemId = itemId;
            _count = count;
        }

        public int getItemId() 
        { 
        	return _itemId;
        }

        public int getCount() 
        {
        	return _count;
        }
    }

    /** The table containing all autoAttackable L2Character in its Aggro Range and L2Character that attacked the L2Attackable
     * This Map is Thread Safe, but Removing Object While Interating Over It Will Result NPE
     * */
    private Map<L2Character, AggroInfo> _aggroList = new ConcurrentHashMap<>();
    /** Use this to Read or Put Object to this Map */
    public final Map<L2Character, AggroInfo> getAggroListRP()
    {
    	return _aggroList;
    }
    /** Use this to Remove Object from this Map
     * This Should be Synchronized While Interating over This Map - ie u cant interating and removing object at once*/
    public final Map<L2Character, AggroInfo> getAggroList()
    {
    	return _aggroList;
    }

	private boolean _isReturningToSpawnPoint = false;
	
    public final boolean isReturningToSpawnPoint() 
    { 
    	return _isReturningToSpawnPoint; 
    }
    
	public final void setisReturningToSpawnPoint(boolean value) 
	{ 
		_isReturningToSpawnPoint = value; 
	}

    /** Table containing all Items that a Dwarf can Sweep on this L2Attackable */
    private RewardItem[] _sweepItems;

    /** crops */
    private RewardItem[] _harvestItems;
    private boolean _seeded;
    private int _seedType = 0;
    private L2PcInstance _seeder = null;

    /** True if an over-hit enabled skill has successfully landed on the L2Attackable */
    private boolean _overhit;

    /** Stores the extra (over-hit) damage done to the L2Attackable when the attacker uses an over-hit enabled skill */
    private double _overhitDamage;

    /** Stores the attacker who used the over-hit enabled skill on the L2Attackable */
    private L2Character _overhitAttacker;

    /** First CommandChannel who attacked the L2Attackable and meet the requirements **/
    private L2CommandChannel _firstCommandChannelAttacked = null;
    private CommandChannelTimer _commandChannelTimer = null;

    /** True if a Soul Crystal was successfuly used on the L2Attackable */
    private boolean _absorbed;

    /** The table containing all L2PcInstance that successfuly absorbed the soul of this L2Attackable */
    private Map<L2PcInstance, AbsorberInfo> _absorbersList = new ConcurrentHashMap<>();
    /** Have this L2Attackable to reward Exp and SP on Die? **/
    private boolean _mustGiveExpSp;
    /**
     * Constructor of L2Attackable (use L2Character and L2NpcInstance constructor).<BR><BR>
     *
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Call the L2Character constructor to set the _template of the L2Attackable (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR) </li>
     * <li>Set the name of the L2Attackable</li>
     * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it </li><BR><BR>
     *
     * @param objectId Identifier of the object to initialized
     * @param template to apply to the NPC
     */
    public L2Attackable(int objectId, L2NpcTemplate template)
    {
        super(objectId, template);
        getKnownList(); // init knownlist
        _mustGiveExpSp = true;
    }

    @Override
	public AttackableKnownList getKnownList()
    {
    	if(super.getKnownList() == null || !(super.getKnownList() instanceof AttackableKnownList))
    		setKnownList(new AttackableKnownList(this));
    	return (AttackableKnownList)super.getKnownList();
    }

    /**
     * Return the L2Character AI of the L2Attackable and if its null create a new one.<BR><BR>
     */
    @Override
	public L2CharacterAI getAI()
    {
        if (_ai == null)
        {
            synchronized(this)
            {
                if (_ai == null)
                    _ai = new L2AttackableAI(new AIAccessor());
            }
        }
        return _ai;
    }

    // get condition to hate, actually isAggressive() is checked
    // by monster and karma by guards in motheds that overwrite this one.
    /**
     * Not used.<BR><BR>
     *
     * @deprecated
     */
    @Deprecated
    public boolean getCondition2(L2Character target)
    {
        if (target instanceof L2FolkInstance || target instanceof L2DoorInstance)
            return false;

        if (target.isAlikeDead() || !isInsideRadius(target, getAggroRange(), false, false)
            || Math.abs(getZ()-target.getZ()) > 100)
        	return false;
        
        return !target.isInvul();
    }

    /**
     * Reduce the current HP of the L2Attackable.<BR><BR>
     *
     * @param damage The HP decrease value
     * @param attacker The L2Character who attacks
     *
     */
    @Override
	public void reduceCurrentHp(double damage, L2Character attacker)
    {
        reduceCurrentHp(damage, attacker, true);
    }

    /**
     * Reduce the current HP of the L2Attackable, update its _aggroList and launch the doDie Task if necessary.<BR><BR>
     *
     * @param damage The HP decrease value
     * @param attacker The L2Character who attacks
     * @param awake The awake state (If True : stop sleeping)
     *
     */
    @Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake)
    {
    	// CommandChannel
        if (_commandChannelTimer == null && this.isRaid())
        {
        	if (attacker.isInParty() && attacker.getParty() != null && attacker.getParty().isInCommandChannel() && attacker.getParty().getCommandChannel().meetRaidWarCondition(this))
        	{
        		_firstCommandChannelAttacked = attacker.getParty().getCommandChannel();
        		_commandChannelTimer = new CommandChannelTimer(this, attacker.getParty().getCommandChannel());
        		ThreadPoolManager.getInstance().scheduleGeneral(_commandChannelTimer, 300000); // 5 min
        		_firstCommandChannelAttacked.broadcastToChannelMembers(new CreatureSay(0, Say2.PARTYROOM_ALL, "", "You have looting rights!"));
        	}
        }

        if (isEventMob) 
        	return;

        // Add damage and hate to the attacker AggroInfo of the L2Attackable _aggroList
        if (attacker != null)
        	addDamage(attacker, (int)damage);

        // If this L2Attackable is a L2MonsterInstance and it has spawned minions, call its minions to battle
        if (this instanceof L2MonsterInstance)
        {
            L2MonsterInstance master = (L2MonsterInstance) this;
            if (this instanceof L2MinionInstance)
            {
                master = ((L2MinionInstance)this).getLeader();
                if (!master.isInCombat()&&!master.isDead())
                	master.addDamage(attacker, 1);
            }
            if (master.hasMinions())
                master.callMinionsToAssist(attacker);
        }

        // Reduce the current HP of the L2Attackable and launch the doDie Task if necessary
        super.reduceCurrentHp(damage, attacker, awake);
    }

    public synchronized void setMustRewardExpSp(boolean value) 
	{
    	_mustGiveExpSp = value;
    }

    public synchronized boolean getMustRewardExpSP() 
    { 
    	return _mustGiveExpSp; 
    }

    /**
     * Kill the L2Attackable (the corpse disappeared after 7 seconds), distribute rewards (EXP, SP, Drops...) and notify Quest Engine.<BR><BR>
     *
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Distribute Exp and SP rewards to L2PcInstance (including Summon owner) that hit the L2Attackable and to their Party members </li>
     * <li>Notify the Quest Engine of the L2Attackable death if necessary</li>
     * <li>Kill the L2NpcInstance (the corpse disappeared after 7 seconds) </li><BR><BR>
     *
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T GIVE rewards to L2PetInstance</B></FONT><BR><BR>
     *
     * @param killer The L2Character that has killed the L2Attackable
     *
     */
    @Override
	public boolean doDie(L2Character killer)
    {
    	// Kill the L2NpcInstance (the corpse disappeared after 7 seconds)
        if (!super.doDie(killer))
        	return false;

    	// Enhance soul crystals of the attacker if this L2Attackable had its soul absorbed
        try 
        {
            if (killer instanceof L2PcInstance)
            {
                levelSoulCrystals(killer);
            }
        }
        catch (Exception e) { _log.log(Level.SEVERE, "", e); }

        // Notify the Quest Engine of the L2Attackable death if necessary
        try 
        {
            if (killer instanceof L2PcInstance || killer instanceof L2Summon)
            {
                L2PcInstance player = killer instanceof L2PcInstance?(L2PcInstance)killer:((L2Summon)killer).getOwner();

            	if (getTemplate().getEventQuests(Quest.QuestEventType.ON_KILL) != null)
            		for (Quest quest: getTemplate().getEventQuests(Quest.QuestEventType.ON_KILL))
            			quest.notifyKill(this, player, killer instanceof L2Summon);
            }
        }
        catch (Exception e) { _log.log(Level.SEVERE, "", e); }
        setChampion(false);
        if (Config.L2JMOD_CHAMPION_ENABLE)
        {
        	//Set champion on next spawn
        	if (!(this instanceof L2GrandBossInstance) && !(this instanceof L2RaidBossInstance) && !(this instanceof L2MinionInstance) && this instanceof L2MonsterInstance && Config.L2JMOD_CHAMPION_FREQUENCY > 0 && getLevel()>=Config.L2JMOD_CHAMP_MIN_LVL && getLevel()<=Config.L2JMOD_CHAMP_MAX_LVL)
        	{
        		int random = Rnd.get(100);
        		if (random < Config.L2JMOD_CHAMPION_FREQUENCY)
        			setChampion(true);
        	}
        }
        return true;
    }

    /**
     * Distribute Exp and SP rewards to L2PcInstance (including Summon owner) that hit the L2Attackable and to their Party members.<BR><BR>
     *
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Get the L2PcInstance owner of the L2SummonInstance (if necessary) and L2Party in progress </li>
     * <li>Calculate the Experience and SP rewards in function of the level difference</li>
     * <li>Add Exp and SP rewards to L2PcInstance (including Summon penalty) and to Party members in the known area of the last attacker </li><BR><BR>
     *
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T GIVE rewards to L2PetInstance</B></FONT><BR><BR>
     *
     * @param lastAttacker The L2Character that has killed the L2Attackable
     *
     */

    @Override
	protected void calculateRewards(L2Character lastAttacker)
    {
        // Creates an empty list of rewards
        Map<L2Character, RewardInfo> rewards = new ConcurrentHashMap<>();

        try
        {
        	if (getAggroListRP().isEmpty()) 
        		return;

        	// Manage Base, Quests and Sweep drops of the L2Attackable
            doItemDrop(lastAttacker);
            // Manage drop of Special Events created by GM for a defined period
            doEventDrop(lastAttacker);

        	if (!getMustRewardExpSP()) 
        		return;

        	int rewardCount = 0;
        	int damage;
        	L2Character attacker, ddealer;
        	RewardInfo reward;

        	// While Interating over This Map Removing Object is Not Allowed
        	synchronized (getAggroList())
        	{
        		// Go through the _aggroList of the L2Attackable
        		for (AggroInfo info : getAggroListRP().values())
        		{
        			if (info == null) continue;

        			// Get the L2Character corresponding to this attacker
        			attacker = info._attacker;

        			// Get damages done by this attacker
        			damage = info._damage;

        			// Prevent unwanted behavior
        			if (damage > 1)
        			{
        				if ((attacker instanceof L2SummonInstance) ||
        					((attacker instanceof L2PetInstance) &&
        					((L2PetInstance)attacker).getPetData().getOwnerExpTaken() > 0))
        					ddealer = ((L2Summon)attacker).getOwner();
        				else
        					ddealer = info._attacker;

        				// Check if ddealer isn't too far from this (killed monster)
        				if (!Util.checkIfInRange(Config.ALT_PARTY_RANGE, this, ddealer, true)) 
        					continue;

        				// Calculate real damages (Summoners should get own damage plus summon's damage)
        				reward = rewards.get(ddealer);

        				if (reward == null)
        				{
        					reward = new RewardInfo(ddealer, damage);
        					rewardCount++;
        				}
        				else
        				{
        					reward.addDamage(damage);
        				}
        				rewards.put(ddealer, reward);
        			}
        		}
        	}
        	if (!rewards.isEmpty())
        	{
        		L2Party attackerParty;
        		long exp;
        		int levelDiff, partyDmg, partyLvl, sp;
        		float partyMul, penalty;
        		int[] tmp;

                for (RewardInfo reward2 : rewards.values())
        		{
        			if (reward2 == null)
        				continue;

        			// Penalty applied to the attacker's XP
        			penalty = 0;

        			// Attacker to be rewarded
        			attacker = reward2._attacker;

        			// Total amount of damage done
        			damage = reward2._dmg;

        			// If the attacker is a Pet, get the party of the owner
        			if (attacker instanceof L2PetInstance)
        				attackerParty = ((L2PetInstance)attacker).getParty();
        			else if (attacker instanceof L2PcInstance)
        				attackerParty = ((L2PcInstance)attacker).getParty();
        			else
        				return;

        			// If this attacker is a L2PcInstance with a summoned L2SummonInstance, get Exp Penalty applied for the current summoned L2SummonInstance
        			if (attacker instanceof L2PcInstance && ((L2PcInstance)attacker).getPet() instanceof L2SummonInstance)
        			{
        				penalty = ((L2SummonInstance)((L2PcInstance)attacker).getPet()).getExpPenalty();
        			}

        			// We must avoid "over damage", if any
        			if (damage > getMaxHp()) damage = getMaxHp();

        			// If there's NO party in progress
        			if (attackerParty == null)
        			{
        				// Calculate Exp and SP rewards
        				if (attacker.getKnownList().knowsObject(this))
        				{
        					// Calculate the difference of level between this attacker (L2PcInstance or L2SummonInstance owner) and the L2Attackable
        					// mob = 24, atk = 10, diff = -14 (full xp)
        					// mob = 24, atk = 28, diff = 4 (some xp)
        					// mob = 24, atk = 50, diff = 26 (no xp)
        					levelDiff = attacker.getLevel() - getLevel();

        					tmp = calculateExpAndSp(levelDiff, damage);
        					exp = tmp[0];
        					exp *= 1 - penalty;
        					sp = tmp[1];

        					if (Config.L2JMOD_CHAMPION_ENABLE && isChampion())
        					{
        						exp *= Config.L2JMOD_CHAMPION_REWARDS;
        						sp *= Config.L2JMOD_CHAMPION_REWARDS;
        					}

        					// Check for an over-hit enabled strike
        					if (attacker instanceof L2PcInstance)
        					{
        						L2PcInstance player = (L2PcInstance)attacker;
        						if (isOverhit() && attacker == getOverhitAttacker())
        						{
        							player.sendPacket(new SystemMessage(SystemMessageId.OVER_HIT));
        							exp += calculateOverhitExp(exp);
        						}
        						if (player.isVip() && Config.ALLOW_VIP_XPSP)
        						{
        							exp *= Config.VIP_XP;
        							sp *= Config.VIP_SP;
        						}
        					}

        					// Distribute the Exp and SP between the L2PcInstance and its L2Summon
        					if (!attacker.isDead())
        						attacker.addExpAndSp(Math.round(attacker.calcStat(Stats.EXPSP_RATE, exp, null, null)),
        								(int)attacker.calcStat(Stats.EXPSP_RATE, sp, null, null));
        				}
        			}
        			else
        			{
        				//share with party members
        				partyDmg = 0;
        				partyMul = 1.f;
        				partyLvl = 0;

        				// Get all L2Character that can be rewarded in the party
        				List<L2PlayableInstance> rewardedMembers = new ArrayList<>();

        				// Go through all L2PcInstance in the party
        				List<L2PcInstance> groupMembers;
        				if (attackerParty.isInCommandChannel())
        					groupMembers = attackerParty.getCommandChannel().getMembers();
        				else
        					groupMembers = attackerParty.getPartyMembers();

        				for (L2PcInstance pl : groupMembers)
        				{
        					if (pl == null || pl.isDead())
        						continue;

        					// Get the RewardInfo of this L2PcInstance from L2Attackable rewards
        					reward2 = rewards.get(pl);

        					// If the L2PcInstance is in the L2Attackable rewards add its damages to party damages
        					if (reward2 != null)
        					{
        						if (Util.checkIfInRange(Config.ALT_PARTY_RANGE, this, pl, true))
        						{
        							partyDmg += reward2._dmg; // Add L2PcInstance damages to party damages
        							rewardedMembers.add(pl);
        							if (pl.getLevel() > partyLvl)
        							{
        								if(attackerParty.isInCommandChannel())
        									partyLvl = attackerParty.getCommandChannel().getLevel();
        								else
        									partyLvl = pl.getLevel();
        							}
        						}
        						rewards.remove(pl); // Remove the L2PcInstance from the L2Attackable rewards
        					}
        					else
        					{
        						// Add L2PcInstance of the party (that have attacked or not) to members that can be rewarded
        						// and in range of the monster.
        						if (Util.checkIfInRange(Config.ALT_PARTY_RANGE, this, pl, true))
        						{
        							rewardedMembers.add(pl);
        							if (pl.getLevel() > partyLvl)
        							{
        								if(attackerParty.isInCommandChannel())
        									partyLvl = attackerParty.getCommandChannel().getLevel();
        								else
        									partyLvl = pl.getLevel();
        							}
        						}
        					}
        					L2PlayableInstance summon = pl.getPet();
        					if (summon != null && summon instanceof L2PetInstance)
        					{
        						reward2 = rewards.get(summon);
        						if (reward2 != null) // Pets are only added if they have done damage
        						{
        							if (Util.checkIfInRange(Config.ALT_PARTY_RANGE, this, summon, true))
        							{
        								partyDmg += reward2._dmg; // Add summon damages to party damages
        								rewardedMembers.add(summon);
        								if (summon.getLevel() > partyLvl)
        									partyLvl = summon.getLevel();
        							}
        							rewards.remove(summon); // Remove the summon from the L2Attackable rewards
        						}
        					}
        				}

        				// If the party didn't killed this L2Attackable alone
        				if (partyDmg < getMaxHp()) 
        					partyMul = ((float)partyDmg / (float)getMaxHp());

        				// Avoid "over damage"
        				if (partyDmg > getMaxHp()) 
        					partyDmg = getMaxHp();

        				// Calculate the level difference between Party and L2Attackable
        				levelDiff = partyLvl - getLevel();

        				// Calculate Exp and SP rewards
        				tmp = calculateExpAndSp(levelDiff, partyDmg);
        				exp = tmp[0];
        				sp = tmp[1];

        				if (Config.L2JMOD_CHAMPION_ENABLE && isChampion())
        				{
        					exp *= Config.L2JMOD_CHAMPION_REWARDS;
        					sp *= Config.L2JMOD_CHAMPION_REWARDS;
        				}

        				exp *= partyMul;
        				sp *= partyMul;

        				// Check for an over-hit enabled strike
        				// (When in party, the over-hit exp bonus is given to the whole party and splitted proportionally through the party members)
        				if (attacker instanceof L2PcInstance)
        				{
        					L2PcInstance player = (L2PcInstance)attacker;
        					if (isOverhit() && attacker == getOverhitAttacker())
        					{
        						player.sendPacket(new SystemMessage(SystemMessageId.OVER_HIT));
        						exp += calculateOverhitExp(exp);
        					}
        				}

        				// Distribute Experience and SP rewards to L2PcInstance Party members in the known area of the last attacker
        				if (partyDmg > 0) 
        					attackerParty.distributeXpAndSp(exp, sp, rewardedMembers, partyLvl);
        			}
        		}
        	}

        	rewards = null;

        }
        catch (Exception e)
        {
        	_log.log(Level.SEVERE, "", e);
        }
    }


    /**
     * Add damage and hate to the attacker AggroInfo of the L2Attackable _aggroList.<BR><BR>
     *
     * @param attacker The L2Character that gave damages to this L2Attackable
     * @param damage The number of damages given by the attacker L2Character
     *
     */
    public void addDamage(L2Character attacker, int damage)
    {
    	// Notify the L2Attackable AI with EVT_ATTACKED
    	if (!isDead() && damage > 0)
    	{
			try
			{
				L2PcInstance player = attacker.getActingPlayer();
				if (player != null)
					if (getTemplate().getEventQuests(Quest.QuestEventType.ON_ATTACK) !=null)
						for (Quest quest: getTemplate().getEventQuests(Quest.QuestEventType.ON_ATTACK))
					quest.notifyAttack(this, player, damage, attacker instanceof L2Summon);
			}
			catch (Exception e) { _log.log(Level.SEVERE, "", e); 
			}
    	}		
        addDamageHate(attacker, damage, damage);
    }

    /**
     * Add damage and hate to the attacker AggroInfo of the L2Attackable _aggroList.<BR><BR>
     *
     * @param attacker The L2Character that gave damages to this L2Attackable
     * @param damage The number of damages given by the attacker L2Character
     * @param aggro The hate (=damage) given by the attacker L2Character
     *
     */
    public void addDamageHate(L2Character attacker, int damage, int aggro)
    {
        if (attacker == null) 
        	return;

        // Get the AggroInfo of the attacker L2Character from the _aggroList of the L2Attackable
        AggroInfo ai = getAggroListRP().get(attacker);
        if (ai == null)
        {
            ai = new AggroInfo(attacker);
            getAggroListRP().put(attacker, ai);
            ai._damage = 0;
            ai._hate = 0;
        }
        ai._damage += damage;
        ai._hate += aggro;

        L2PcInstance targetPlayer = attacker.getActingPlayer();
        if (targetPlayer != null && aggro == 0)
			if (getTemplate().getEventQuests(Quest.QuestEventType.ON_AGGRO_RANGE_ENTER) !=null)
        		for (Quest quest: getTemplate().getEventQuests(Quest.QuestEventType.ON_AGGRO_RANGE_ENTER))
        			quest.notifyAggroRangeEnter(this, targetPlayer, (attacker instanceof L2Summon));

		// Set the intention to the L2Attackable to AI_INTENTION_ACTIVE
		if (aggro > 0 && getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

    public void reduceHate(L2Character target, int amount)
    {
    	if (getAI() instanceof L2SiegeGuardAI)
    	{
    		// TODO: this just prevents error until siege guards are handled properly
    		stopHating(target);
        	setTarget(null);
        	getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
    		return;
    	}
    	if (target == null) // whole aggrolist
    	{
    		L2Character mostHated = getMostHated();
        	if (mostHated == null) // makes target passive for a moment more
        	{
        		((L2AttackableAI)getAI()).setGlobalAggro(-25);
        		return;
        	}
        	else
        	{
        		for(L2Character aggroed : getAggroListRP().keySet())
        		{
        			AggroInfo ai = getAggroListRP().get(aggroed);
        	    	if (ai == null) return;
        	    	ai._hate -= amount;
        		}
        	}

        	amount = getHating(mostHated);
        	if (amount <= 0)
                {
        		((L2AttackableAI)getAI()).setGlobalAggro(-25);
        		clearAggroList();
        		getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        		setWalking();
                }
        	return;
    	}
    	AggroInfo ai = getAggroListRP().get(target);
    	if (ai == null) return;
    	ai._hate -= amount;

        if (ai._hate <= 0)
        {
        	if (getMostHated() == null)
        	{
        		((L2AttackableAI)getAI()).setGlobalAggro(-25);
        		clearAggroList();
        		getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        		setWalking();
        	}
        }
    }

    /**
     * Clears _aggroList hate of the L2Character without removing from the list.<BR><BR>
     */
    public void stopHating(L2Character target)
    {
    	if (target == null) 
    		return;
    	AggroInfo ai = getAggroListRP().get(target);
    	if (ai == null)
    		return;
    	ai._hate = 0;
    }

    /**
     * Return the most hated L2Character of the L2Attackable _aggroList.<BR><BR>
     */
    public L2Character getMostHated()
    {
    	if (getAggroListRP().isEmpty() || isAlikeDead()) 
    		return null;

        L2Character mostHated = null;
        int maxHate = 0;

        // While Interating over This Map Removing Object is Not Allowed
        synchronized (getAggroList())
        {
            // Go through the aggroList of the L2Attackable
            for (AggroInfo ai : getAggroListRP().values())
            {
            	if (ai == null) 
            		continue;
            	if(ai._attacker.isAlikeDead() || !getKnownList().knowsObject(ai._attacker) || !ai._attacker.isVisible() || ai._attacker instanceof L2PcInstance && ((L2PcInstance)ai._attacker).isOffline())
    				ai._hate = 0;
            	if (ai._hate > maxHate)
            	{
            		mostHated = ai._attacker;
            		maxHate = ai._hate;
            	}
            }
        }
        return mostHated;
    }

    /**
     * Return the hate level of the L2Attackable against this L2Character contained in _aggroList.<BR><BR>
     *
     * @param target The L2Character whose hate level must be returned
     *
     */
    public int getHating(L2Character target)
    {
    	if (getAggroList().isEmpty() || target == null) 
                return 0;

    	AggroInfo ai = getAggroListRP().get(target);
        if (ai == null) return 0;
    	if (ai._attacker instanceof L2PcInstance && (((L2PcInstance)ai._attacker).getAppearance().getInvisible() || ai._attacker.isInvul()))
    	{
    		//Remove Object Should Use This Method and Can be Blocked While Interating
    		getAggroList().remove(target);
    		return 0;
    	}
    	if (!ai._attacker.isVisible())
    	{
    		getAggroList().remove(target);
    		return 0;
    	}
    	if (ai._attacker.isAlikeDead())
        {
            ai._hate = 0;
            return 0;
        }
    	return ai._hate;
    }

    /**
     * Calculates quantity of items for specific drop acording to current situation <br>
     *
     * @param drop The L2DropData count is being calculated for
     * @param lastAttacker The L2PcInstance that has killed the L2Attackable
     * @param isSweep
     * @param levelModifier level modifier in %'s (will be subtracted from drop chance)
     */
     private RewardItem calculateRewardItem(L2PcInstance lastAttacker, L2DropData drop, int levelModifier, boolean isSweep)
     {
         // Get default drop chance
         float dropChance = drop.getChance();

         int deepBlueDrop = 1;
         if (Config.DEEPBLUE_DROP_RULES)
         {
             if (levelModifier > 0)
             {
                 // We should multiply by the server's drop rate, so we always get a low chance of drop for deep blue mobs.
                 // NOTE: This is valid only for adena drops! Others drops will still obey server's rate
                 deepBlueDrop = 3;
                 if (drop.getItemId() == 57) deepBlueDrop *= isRaid()? (int)Config.RATE_DROP_ITEMS_BY_RAID : (int)Config.RATE_DROP_ITEMS;
             }
         }

         if(deepBlueDrop == 0) //avoid div by 0
        	 deepBlueDrop = 1;
         // Check if we should apply our maths so deep blue mobs will not drop that easy
         if (Config.DEEPBLUE_DROP_RULES) dropChance = ((drop.getChance() - ((drop.getChance() * levelModifier)/100)) / deepBlueDrop);

         // Applies Drop rates
         if (drop.getItemId() == 57) dropChance *= Config.RATE_DROP_ADENA;
         else if (isSweep) dropChance *= Config.RATE_DROP_SPOIL;
         else dropChance *= isRaid() ? Config.RATE_DROP_ITEMS_BY_RAID : Config.RATE_DROP_ITEMS;

         if (Config.L2JMOD_CHAMPION_ENABLE && isChampion())
	         dropChance *= Config.L2JMOD_CHAMPION_REWARDS;

         // Round drop chance
         dropChance = Math.round(dropChance);

         // Set our limits for chance of drop
         if (dropChance < 1) dropChance = 1;
//         if (drop.getItemId() == 57 && dropChance > L2DropData.MAX_CHANCE) dropChance = L2DropData.MAX_CHANCE; // If item is adena, dont drop multiple time

         // Get min and max Item quantity that can be dropped in one time
         int minCount = drop.getMinDrop();
         int maxCount = drop.getMaxDrop();
         int itemCount = 0;

         // Count and chance adjustment for high rate servers
         if (dropChance > L2DropData.MAX_CHANCE && !Config.PRECISE_DROP_CALCULATION)
         {
             int multiplier = (int)dropChance / L2DropData.MAX_CHANCE;
             if (minCount < maxCount) itemCount += Rnd.get(minCount * multiplier, maxCount * multiplier);
             else if (minCount == maxCount) itemCount += minCount * multiplier;
             else itemCount += multiplier;

             dropChance = dropChance % L2DropData.MAX_CHANCE;
         }

         // Check if the Item must be dropped
         int random = Rnd.get(L2DropData.MAX_CHANCE);
         while (random < dropChance)
         {
             // Get the item quantity dropped
             if (minCount < maxCount) itemCount += Rnd.get(minCount, maxCount);
             else if (minCount == maxCount) itemCount += minCount;
             else itemCount++;

             // Prepare for next iteration if dropChance > L2DropData.MAX_CHANCE
             dropChance -= L2DropData.MAX_CHANCE;
         }
		 if (Config.L2JMOD_CHAMPION_ENABLE)
			if ((drop.getItemId() == 57 || (drop.getItemId() >= 6360 && drop.getItemId() <= 6362)) && isChampion())
				itemCount *= Config.L2JMOD_CHAMPION_ADENAS_REWARDS;

         if (itemCount > 0) return new RewardItem(drop.getItemId(), itemCount);
         else if (itemCount == 0 && Config.DEBUG) _log.fine("Roll produced 0 items to drop...");

         return null;
	 }

     /**
      * Calculates quantity of items for specific drop CATEGORY according to current situation <br>
      * Only a max of ONE item from a category is allowed to be dropped.
      *
      * @param categoryDrops
      * @param lastAttacker The L2PcInstance that has killed the L2Attackable
      * @param levelModifier level modifier in %'s (will be subtracted from drop chance)
      */
      private RewardItem calculateCategorizedRewardItem(L2PcInstance lastAttacker, L2DropCategory categoryDrops, int levelModifier)
      {
        if (categoryDrops == null)
            return null;

          // Get default drop chance for the category (that's the sum of chances for all items in the category)
          // keep track of the base category chance as it'll be used later, if an item is drop from the category.
          // for everything else, use the total "categoryDropChance"
          int basecategoryDropChance = categoryDrops.getCategoryChance() ;
          int categoryDropChance = basecategoryDropChance;

          int deepBlueDrop = 1;
          if (Config.DEEPBLUE_DROP_RULES)
          {
              if (levelModifier > 0)
              {
                  // We should multiply by the server's drop rate, so we always get a low chance of drop for deep blue mobs.
                  // NOTE: This is valid only for adena drops! Others drops will still obey server's rate
                  deepBlueDrop = 3;
              }
          }

          if(deepBlueDrop == 0) //avoid div by 0
           deepBlueDrop = 1;
          // Check if we should apply our maths so deep blue mobs will not drop that easy
          if (Config.DEEPBLUE_DROP_RULES) categoryDropChance = ((categoryDropChance - ((categoryDropChance * levelModifier)/100)) / deepBlueDrop);

          // Applies Drop rates
          categoryDropChance *= isRaid() ? Config.RATE_DROP_ITEMS_BY_RAID : Config.RATE_DROP_ITEMS;
          if (Config.L2JMOD_CHAMPION_ENABLE && isChampion())
			categoryDropChance *= Config.L2JMOD_CHAMPION_REWARDS;

          // Round drop chance
          categoryDropChance = Math.round(categoryDropChance);

          // Set our limits for chance of drop
          if (categoryDropChance < 1) categoryDropChance = 1;

          // Check if an Item from this category must be dropped
          if (Rnd.get(L2DropData.MAX_CHANCE) < categoryDropChance)
          {
        	  L2DropData drop = categoryDrops.dropOne(isRaid());
        	  if (drop == null)
        		  return null;

        	  // Now decide the quantity to drop based on the rates and penalties.  To get this value
        	  // simply divide the modified categoryDropChance by the base category chance.  This
        	  // results in a chance that will dictate the drops amounts: for each amount over 100
        	  // that it is, it will give another chance to add to the min/max quantities.
        	  //
        	  // For example, If the final chance is 120%, then the item should drop between
        	  // its min and max one time, and then have 20% chance to drop again.  If the final
        	  // chance is 330%, it will similarly give 3 times the min and max, and have a 30%
        	  // chance to give a 4th time.
        	  // At least 1 item will be dropped for sure.  So the chance will be adjusted to 100%
        	  // if smaller.

        	  int dropChance = drop.getChance();
              if (drop.getItemId() == 57) dropChance *= Config.RATE_DROP_ADENA;
              else dropChance *= isRaid() ? Config.RATE_DROP_ITEMS_BY_RAID : Config.RATE_DROP_ITEMS;
              if (Config.L2JMOD_CHAMPION_ENABLE && isChampion())
				dropChance *= Config.L2JMOD_CHAMPION_REWARDS;

              dropChance = Math.round(dropChance);

        	  if (dropChance < L2DropData.MAX_CHANCE)
        		  dropChance = L2DropData.MAX_CHANCE;

              // Get min and max Item quantity that can be dropped in one time
              int min = drop.getMinDrop();
              int max = drop.getMaxDrop();

              // Get the item quantity dropped
              int itemCount = 0;

              // Count and chance adjustment for high rate servers
              if (dropChance > L2DropData.MAX_CHANCE && !Config.PRECISE_DROP_CALCULATION)
              {
                  int multiplier = dropChance / L2DropData.MAX_CHANCE;
                  if (min < max) itemCount += Rnd.get(min * multiplier, max * multiplier);
                  else if (min == max) itemCount += min * multiplier;
                  else itemCount += multiplier;

                  dropChance = dropChance % L2DropData.MAX_CHANCE;
              }

              // Check if the Item must be dropped
              int random = Rnd.get(L2DropData.MAX_CHANCE);
              while (random < dropChance)
              {
                  // Get the item quantity dropped
                  if (min < max) itemCount += Rnd.get(min, max);
                  else if (min == max) itemCount += min;
                  else itemCount++;


                  // Prepare for next iteration if dropChance > L2DropData.MAX_CHANCE
                  dropChance -= L2DropData.MAX_CHANCE;
              }
              if (Config.L2JMOD_CHAMPION_ENABLE)
            	  if ((drop.getItemId() == 57 || (drop.getItemId() >= 6360 && drop.getItemId() <= 6362)) && isChampion())
            		  itemCount *= Config.L2JMOD_CHAMPION_ADENAS_REWARDS;
              if (!Config.MULTIPLE_ITEM_DROP && !ItemTable.getInstance().getTemplate(drop.getItemId()).isStackable() && itemCount > 1) 
                      itemCount = 1; 
              if (itemCount > 0)
                  return new RewardItem(drop.getItemId(), itemCount);
              else if (itemCount == 0 && Config.DEBUG) _log.fine("Roll produced 0 items to drop...");
        }
        return null;


        /*
         // Applies Drop rates
         if (drop.getItemId() == 57) dropChance *= Config.RATE_DROP_ADENA;
         else if (isSweep) dropChance *= Config.RATE_DROP_SPOIL;
         else dropChance *= Config.RATE_DROP_ITEMS;

         // Round drop chance
         dropChance = Math.round(dropChance);

         // Set our limits for chance of drop
         if (dropChance < 1) dropChance = 1;
//         if (drop.getItemId() == 57 && dropChance > L2DropData.MAX_CHANCE) dropChance = L2DropData.MAX_CHANCE; // If item is adena, dont drop multiple time

         // Get min and max Item quantity that can be dropped in one time
         int minCount = drop.getMinDrop();
         int maxCount = drop.getMaxDrop();
         int itemCount = 0;



         if (itemCount > 0) return new RewardItem(drop.getItemId(), itemCount);
         else if (itemCount == 0 && Config.DEBUG) _log.fine("Roll produced 0 items to drop...");

         return null;
         */
     }

     /**
      * Calculates the level modifier for drop<br>
      *
      * @param lastAttacker The L2PcInstance that has killed the L2Attackable
      */
     private int calculateLevelModifierForDrop(L2PcInstance lastAttacker)
     {
         if (Config.DEEPBLUE_DROP_RULES)
         {
             int highestLevel = lastAttacker.getLevel();

             // Check to prevent very high level player to nearly kill mob and let low level player do the last hit.
             if (getAttackByList() != null && !getAttackByList().isEmpty())
             {
                 for (L2Character atkChar: getAttackByList())
                     if (atkChar != null && atkChar.getLevel() > highestLevel) highestLevel = atkChar.getLevel();
             }

             // According to official data (Prima), deep blue mobs are 9 or more levels below players
             if (highestLevel - 9 >= getLevel()) return ((highestLevel - (getLevel() + 8)) * 9);
         }

         return 0;
     }
     public void doItemDrop(L2Character lastAttacker)
     {
    	 doItemDrop(getTemplate(),lastAttacker);
     }
     /**
      * Manage Base, Quests and Special Events drops of L2Attackable (called by calculateRewards).<BR><BR>
      *
      * <B><U> Concept</U> :</B><BR><BR>
      * During a Special Event all L2Attackable can drop extra Items.
      * Those extra Items are defined in the table <B>allNpcDateDrops</B> of the EventDroplist.
      * Each Special Event has a start and end date to stop to drop extra Items automaticaly. <BR><BR>
      *
      * <B><U> Actions</U> : </B><BR><BR>
      * <li>Manage drop of Special Events created by GM for a defined period </li>
      * <li>Get all possible drops of this L2Attackable from L2NpcTemplate and add it Quest drops</li>
      * <li>For each possible drops (base + quests), calculate which one must be dropped (random) </li>
      * <li>Get each Item quantity dropped (random) </li>
      * <li>Create this or these L2ItemInstance corresponding to each Item Identifier dropped</li>
      * <li>If the autoLoot mode is actif and if the L2Character that has killed the L2Attackable is a L2PcInstance, give this or these Item(s) to the L2PcInstance that has killed the L2Attackable</li>
      * <li>If the autoLoot mode isn't actif or if the L2Character that has killed the L2Attackable is not a L2PcInstance, add this or these Item(s) in the world as a visible object at the position where mob was last</li><BR><BR>
      *
      * @param lastAttacker The L2Character that has killed the L2Attackable
      */
     public void doItemDrop(L2NpcTemplate npcTemplate, L2Character lastAttacker)
     {
         L2PcInstance player = null;
         if (lastAttacker instanceof L2PcInstance) player = (L2PcInstance)lastAttacker;
         else if (lastAttacker instanceof L2Summon) player = ((L2Summon)lastAttacker).getOwner();

         if (player == null) return; // Don't drop anything if the last attacker or ownere isn't L2PcInstance

         int levelModifier = calculateLevelModifierForDrop(player);          // level modifier in %'s (will be subtracted from drop chance)

         // Check the drop of a cursed weapon
         if (levelModifier == 0 && player.getLevel() > 20) // Not deep blue mob
         	CursedWeaponsManager.getInstance().checkDrop(this, player);

         // now throw all categorized drops and handle spoil.
    	 for(L2DropCategory cat:npcTemplate.getDropData())
    	 {
    		 RewardItem item = null;
             if (cat.isSweep())
    		 {
            	 // according to sh1ny, seeded mobs CAN be spoiled and swept.
            	 if ( isSpoil()/* && !isSeeded() */)
            	 {
	    			 List<RewardItem> sweepList = new ArrayList<>();

	    	         for(L2DropData drop: cat.getAllDrops() )
	    	         {
	    	        	 item = calculateRewardItem(player, drop, levelModifier, true);
	    	        	 if (item == null) continue;

	    	        	 if (Config.DEBUG) _log.fine("Item id to spoil: " + item.getItemId() + " amount: " + item.getCount());
	    	        	 sweepList.add(item);
	    	         }

	    	         // Set the table _sweepItems of this L2Attackable
	    	         if (!sweepList.isEmpty())
	    	        	 _sweepItems = sweepList.toArray(new RewardItem[sweepList.size()]);
	    		 }
    		 }
    		 else
    		 {
                 if (isSeeded())
                 {
                	 L2DropData drop = cat.dropSeedAllowedDropsOnly();
                	 if(drop == null)
                		 continue;

                	 item = calculateRewardItem(player, drop, levelModifier, false);
                 }
                 else
                 {
	    			 item = calculateCategorizedRewardItem(player, cat, levelModifier);
                 }

    			 if (item != null)
    			 {
    				 if (Config.DEBUG) _log.fine("Item id to drop: " + item.getItemId() + " amount: " + item.getCount());

					 // Check if the autoLoot mode is active
    				 if (Config.AUTO_LOOT_RAIDS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId()) && isRaid()) 
                         player.doAutoLoot(this, item); // Give this or these Item(s) to the L2PcInstance that has killed the L2Attackable 
    				 else if (Config.AUTO_LOOT  && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId()) && !isRaid()) 
    					 player.doAutoLoot(this, item); 	
    				 else DropItem(player, item); // drop the item on the ground

					 // Broadcast message if RaidBoss was defeated
                     if (this instanceof L2RaidBossInstance || this instanceof L2GrandBossInstance)
		             {
		                 SystemMessage sm;
		                 sm = new SystemMessage(SystemMessageId.S1_DIED_DROPPED_S3_S2);
		                 sm.addString(getName());
		                 sm.addItemName(item.getItemId());
		                 sm.addNumber(item.getCount());
		                 broadcastPacket(sm);
		             }
    			 }
    		 }
    	 }

    	 // Apply Special Item drop with rnd qty for champions
    	 if (Config.L2JMOD_CHAMPION_ENABLE && isChampion() && (player.getLevel() <= getLevel()) && Config.L2JMOD_CHAMPION_REWARD > 0 && (Rnd.get(100) < Config.L2JMOD_CHAMPION_REWARD))
		 {
    		 int champqty = Rnd.get(Config.L2JMOD_CHAMPION_REWARD_QTY);
   			 champqty++; //quantity should actually vary between 1 and whatever admin specified as max, inclusive.

    		 RewardItem item = new RewardItem(Config.L2JMOD_CHAMPION_REWARD_ID,champqty);
    		 if (Config.AUTO_LOOT && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId()) ) player.addItem("ChampionLoot", item.getItemId(), item.getCount(), this, true); // Give this or these Item(s) to the L2PcInstance that has killed the L2Attackable
             if (Config.AUTO_LOOT_RAIDS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId()) && isRaid()) 
                        player.doAutoLoot(this, item); // Give this or these Item(s) to the L2PcInstance that has killed the L2Attackable 
             else if (Config.AUTO_LOOT && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())  && !isRaid()) 
                        player.doAutoLoot(this, item); 
    		 else DropItem(player, item);
    	 }

         //Instant Item Drop :>
    	 double rateHp = getStat().calcStat(Stats.MAX_HP , 1, this, null);
         if (rateHp <= 1 && String.valueOf(npcTemplate.type).contentEquals("L2Monster")) //only L2Monster with <= 1x HP can drop herbs
         {
        	 boolean _hp = false;
        	 boolean _mp = false;
        	 boolean _spec = false;

        	 //ptk - patk type enhance
        	 int random = Rnd.get(1000); // note *10
             if ((random < Config.RATE_DROP_SPECIAL_HERBS) && !_spec) // && !_spec useless yet
             {
                 RewardItem item = new RewardItem(8612, 1); // Herb of Warrior
                 if (Config.AUTO_LOOT && Config.AUTO_LOOT_HERBS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.addItem("Loot", item.getItemId(), item.getCount(), this, true);
                 else DropItem(player, item);
                 _spec = true;
             }
             else for (int i = 0; i < 3; i++)
             {
            	 random = Rnd.get(100);
            	 if (random < Config.RATE_DROP_COMMON_HERBS)
            	 {
            		 RewardItem item = null;
            		 if (i == 0) item = new RewardItem(8606, 1); // Herb of Power
            		 if (i == 1) item = new RewardItem(8608, 1); // Herb of Atk. Spd.
            		 if (i == 2) item = new RewardItem(8610, 1); // Herb of Critical Attack

            		 if (Config.AUTO_LOOT && Config.AUTO_LOOT_HERBS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.addItem("Loot", item.getItemId(), item.getCount(), this, true);
            		 else DropItem(player, item);
            		 break;
            	 }
             }

             //mtk - matk type enhance
             random = Rnd.get(1000); // note *10
             if ((random < Config.RATE_DROP_SPECIAL_HERBS) && !_spec)
             {
                 RewardItem item = new RewardItem(8613, 1); // Herb of Mystic
                 if (Config.AUTO_LOOT && Config.AUTO_LOOT_HERBS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.addItem("Loot", item.getItemId(), item.getCount(), this, true);
                 else DropItem(player, item);
                 _spec = true;
             }
             else for (int i = 0; i < 2; i++)
             {
            	 random = Rnd.get(100);
            	 if (random < Config.RATE_DROP_COMMON_HERBS)
            	 {
            		 RewardItem item = null;
            		 if (i == 0) item = new RewardItem(8607, 1); // Herb of Magic
            		 if (i == 1) item = new RewardItem(8609, 1); // Herb of Casting Speed

            		 if (Config.AUTO_LOOT && Config.AUTO_LOOT_HERBS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.addItem("Loot", item.getItemId(), item.getCount(), this, true);
            		 else DropItem(player, item);
            		 break;
            	 }
             }

             //hp+mp type
             random = Rnd.get(1000); // note *10
             if ((random < Config.RATE_DROP_SPECIAL_HERBS) && !_spec)
             {
                 RewardItem item = new RewardItem(8614, 1); // Herb of Recovery
                 if (Config.AUTO_LOOT && Config.AUTO_LOOT_HERBS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.addItem("Loot", item.getItemId(), item.getCount(), this, true);
                 else DropItem(player, item);
                 _mp = true;
                 _hp = true;
                 _spec = true;
             }
             //hp - restore hp type
             if (!_hp)
             {
            	 random = Rnd.get(100);
            	 if (random < Config.RATE_DROP_MP_HP_HERBS)
            	 {
            		 RewardItem item = new RewardItem(8600, 1); // Herb of Life
            		 if (Config.AUTO_LOOT && Config.AUTO_LOOT_HERBS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.addItem("Loot", item.getItemId(), item.getCount(), this, true);
            		 else DropItem(player, item);
            		 _hp = true;
            	 }
             }
             if (!_hp)
             {
            	 random = Rnd.get(100);
            	 if (random < Config.RATE_DROP_GREATER_HERBS)
            	 {
            		 RewardItem item = new RewardItem(8601, 1); // Greater Herb of Life
            		 if (Config.AUTO_LOOT && Config.AUTO_LOOT_HERBS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.addItem("Loot", item.getItemId(), item.getCount(), this, true);
            		 else DropItem(player, item);
            		 _hp = true;
            	 }
             }
             if (!_hp)
             {
            	 random = Rnd.get(1000); // note *10
            	 if (random < Config.RATE_DROP_SUPERIOR_HERBS)
            	 {
            		 RewardItem item = new RewardItem(8602, 1); // Superior Herb of Life
            		 if (Config.AUTO_LOOT && Config.AUTO_LOOT_HERBS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.addItem("Loot", item.getItemId(), item.getCount(), this, true);
            		 else DropItem(player, item);
            	 }
             }
             //mp - restore mp type
             if (!_mp)
             {
            	 random = Rnd.get(100);
            	 if (random < Config.RATE_DROP_MP_HP_HERBS)
            	 {
            		 RewardItem item = new RewardItem(8603, 1); // Herb of Manna
            		 if (Config.AUTO_LOOT && Config.AUTO_LOOT_HERBS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.addItem("Loot", item.getItemId(), item.getCount(), this, true);
            		 else DropItem(player, item);
            		 _mp = true;
            	 }
             }
             if (!_mp)
             {
            	 random = Rnd.get(100);
            	 if (random < Config.RATE_DROP_GREATER_HERBS)
            	 {
            		 RewardItem item = new RewardItem(8604, 1); // Greater Herb of Mana
            		 if (Config.AUTO_LOOT && Config.AUTO_LOOT_HERBS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.addItem("Loot", item.getItemId(), item.getCount(), this, true);
            		 else DropItem(player, item);
            		 _mp = true;
            	 }
             }
             if (!_mp)
             {
            	 random = Rnd.get(1000); // note *10
            	 if (random < Config.RATE_DROP_SUPERIOR_HERBS)
            	 {
            		 RewardItem item = new RewardItem(8605, 1); // Superior Herb of Mana
            		 if (Config.AUTO_LOOT && Config.AUTO_LOOT_HERBS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.addItem("Loot", item.getItemId(), item.getCount(), this, true);
            		 else DropItem(player, item);
            	 }
             }
             // speed enhance type
             random = Rnd.get(100);
             if (random < Config.RATE_DROP_COMMON_HERBS)
             {
                 RewardItem item = new RewardItem(8611, 1);  // Herb of Speed
                 if (Config.AUTO_LOOT && Config.AUTO_LOOT_HERBS && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.addItem("Loot", item.getItemId(), item.getCount(), this, true);
                 else DropItem(player, item);
             }
         }
     }

     /**
      * Manage Special Events drops created by GM for a defined period.<BR><BR>
      *
      * <B><U> Concept</U> :</B><BR><BR>
      * During a Special Event all L2Attackable can drop extra Items.
      * Those extra Items are defined in the table <B>allNpcDateDrops</B> of the EventDroplist.
      * Each Special Event has a start and end date to stop to drop extra Items automaticaly. <BR><BR>
      *
      * <B><U> Actions</U> : <I>If an extra drop must be generated</I></B><BR><BR>
      * <li>Get an Item Identifier (random) from the DateDrop Item table of this Event </li>
      * <li>Get the Item quantity dropped (random) </li>
      * <li>Create this or these L2ItemInstance corresponding to this Item Identifier</li>
      * <li>If the autoLoot mode is actif and if the L2Character that has killed the L2Attackable is a L2PcInstance, give this or these Item(s) to the L2PcInstance that has killed the L2Attackable</li>
      * <li>If the autoLoot mode isn't actif or if the L2Character that has killed the L2Attackable is not a L2PcInstance, add this or these Item(s) in the world as a visible object at the position where mob was last</li><BR><BR>
      *
      * @param lastAttacker The L2Character that has killed the L2Attackable
      */
     public void doEventDrop(L2Character lastAttacker)
     {
         L2PcInstance player = null;
         if (lastAttacker instanceof L2PcInstance)
             player = (L2PcInstance)lastAttacker;
         else if (lastAttacker instanceof L2Summon)
             player = ((L2Summon)lastAttacker).getOwner();

         if (player == null) return; // Don't drop anything if the last attacker or ownere isn't L2PcInstance

         if (player.getLevel() - getLevel() > 9) return;

         // Go through DateDrop of EventDroplist allNpcDateDrops within the date range
         for (DateDrop drop : EventDroplist.getInstance().getAllDrops())
         {
             if (Rnd.get(L2DropData.MAX_CHANCE) < drop.chance)
             {
                 RewardItem item = new RewardItem(drop.items[Rnd.get(drop.items.length)], Rnd.get(drop.min, drop.max));
                 if (Config.AUTO_LOOT && !Config.NO_AUTO_LOOT_LIST.contains(this.getNpcId())) player.doAutoLoot(this, item); // Give this or these Item(s) to the L2PcInstance that has killed the L2Attackable
                 else DropItem(player, item); // drop the item on the ground
             }
         }
     }

     /**
      * Drop reward item.<BR><BR>
      */
     public L2ItemInstance DropItem(L2PcInstance lastAttacker, RewardItem item)
     {
         int randDropLim = 70;

         L2ItemInstance ditem = null;
         for (int i = 0; i < item.getCount(); i++)
         {
             // Randomize drop position
             int newX = getX() + Rnd.get(randDropLim * 2 + 1) - randDropLim;
             int newY = getY() + Rnd.get(randDropLim * 2 + 1) - randDropLim;
             int newZ = Math.max(getZ(), lastAttacker.getZ()) + 20; // TODO: temp hack, do somethign nicer when we have geodatas

             // Init the dropped L2ItemInstance and add it in the world as a visible object at the position where mob was last
             ditem = ItemTable.getInstance().createItem("Loot", item.getItemId(), item.getCount(), lastAttacker, this);
             ditem.dropMe(this, newX, newY, newZ);

             // Add drop to auto destroy item task
             if (!Config.LIST_PROTECTED_ITEMS.contains(item.getItemId())){
            	 if ((Config.AUTODESTROY_ITEM_AFTER > 0 && ditem.getItemType() != L2EtcItemType.HERB)
            	  || (Config.HERB_AUTO_DESTROY_TIME > 0 && ditem.getItemType() == L2EtcItemType.HERB))
            	 ItemsAutoDestroy.getInstance().addItem(ditem);
             }
             ditem.setProtected(false);
             // If stackable, end loop as entire count is included in 1 instance of item
             if (ditem.isStackable() || !Config.MULTIPLE_ITEM_DROP) break;
         }
         return ditem;
     }

	public L2ItemInstance DropItem(L2PcInstance lastAttacker, int itemId, int itemCount)
	{
		return DropItem(lastAttacker, new RewardItem(itemId, itemCount));
	}

    /**
     * Return the active weapon of this L2Attackable (= null).<BR><BR>
     */
    public L2ItemInstance getActiveWeapon()
    {
        return null;
    }

    /**
     * Return True if the _aggroList of this L2Attackable is Empty.<BR><BR>
     */
    public boolean noTarget()
    {
    	return getAggroListRP().isEmpty();
    }

    /**
     * Return True if the _aggroList of this L2Attackable contains the L2Character.<BR><BR>
     *
     * @param player The L2Character searched in the _aggroList of the L2Attackable
     *
     */
    public boolean containsTarget(L2Character player)
    {
    	return getAggroListRP().containsKey(player);
    }

    /**
     * Clear the _aggroList of the L2Attackable.<BR><BR>
     */
    public void clearAggroList()
    {
    	getAggroList().clear();

        // clear overhit values
	_overhit = false;
	_overhitDamage = 0;
	_overhitAttacker = null;
    }

    /**
     * Return True if a Dwarf use Sweep on the L2Attackable and if item can be spoiled.<BR><BR>
     */
    public boolean isSweepActive()
    {
        return _sweepItems != null;
    }

    /**
     * Return table containing all L2ItemInstance that can be spoiled.<BR><BR>
     */
    public synchronized RewardItem[] takeSweep()
    {
    	RewardItem[] sweep = _sweepItems;

        _sweepItems = null;

        return sweep;
    }

    /**
     * Return table containing all L2ItemInstance that can be harvested.<BR><BR>
     */
    public synchronized RewardItem[] takeHarvest()
    {
    	RewardItem[] harvest = _harvestItems;
         _harvestItems = null;
         return harvest;
    }

    /**
     * Set the over-hit flag on the L2Attackable.<BR><BR>
     *
     * @param status The status of the over-hit flag
     *
     */
    public void overhitEnabled(boolean status)
    {
        _overhit = status;
    }

    /**
     * Set the over-hit values like the attacker who did the strike and the ammount of damage done by the skill.<BR><BR>
     *
     * @param attacker The L2Character who hit on the L2Attackable using the over-hit enabled skill
     * @param damage The ammount of damage done by the over-hit enabled skill on the L2Attackable
     *
     */
    public void setOverhitValues(L2Character attacker, double damage)
    {
        // Calculate the over-hit damage
        // Ex: mob had 10 HP left, over-hit skill did 50 damage total, over-hit damage is 40
        double overhitDmg = ((getCurrentHp() - damage) * (-1));
        if (overhitDmg < 0)
        {
            // we didn't killed the mob with the over-hit strike. (it wasn't really an over-hit strike)
            // let's just clear all the over-hit related values
            overhitEnabled(false);
            _overhitDamage = 0;
            _overhitAttacker = null;
            return;
        }
        overhitEnabled(true);
        _overhitDamage = overhitDmg;
        _overhitAttacker = attacker;
    }

    /**
     * Return the L2Character who hit on the L2Attackable using an over-hit enabled skill.<BR><BR>
     *
     * @return L2Character attacker
     */
    public L2Character getOverhitAttacker()
    {
        return _overhitAttacker;
    }

    /**
     * Return the ammount of damage done on the L2Attackable using an over-hit enabled skill.<BR><BR>
     *
     * @return double damage
     */
    public double getOverhitDamage()
    {
        return _overhitDamage;
    }

    /**
     * Return True if the L2Attackable was hit by an over-hit enabled skill.<BR><BR>
     */
    public boolean isOverhit()
    {
        return _overhit;
    }

    /**
     * Activate the absorbed soul condition on the L2Attackable.<BR><BR>
     */
    public void absorbSoul()
    {
        _absorbed = true;

    }

    /**
     * Return True if the L2Attackable had his soul absorbed.<BR><BR>
     */
    public boolean isAbsorbed()
    {
        return _absorbed;
    }

    /**
     * Adds an attacker that successfully absorbed the soul of this L2Attackable into the _absorbersList.<BR><BR>
     *
     * params:  attacker    - a valid L2PcInstance
     *          condition   - an integer indicating the event when mob dies. This should be:
     *                          = 0     - "the crystal scatters";
     *                          = 1     - "the crystal failed to absorb. nothing happens";
     *                          = 2     - "the crystal resonates because you got more than 1 crystal on you";
     *                          = 3     - "the crystal cannot absorb the soul because the mob level is too low";
     *                          = 4     - "the crystal successfuly absorbed the soul";
     */
    public void addAbsorber(L2PcInstance attacker, int crystalId)
    {
        // This just works for targets like L2MonsterInstance
        if (!(this instanceof L2MonsterInstance))
            return;

        // The attacker must not be null
        if (attacker == null)
            return;

        // This L2Attackable must be of one type in the _absorbingMOBS_levelXX tables.
        // OBS: This is done so to avoid triggering the absorbed conditions for mobs that can't be absorbed.
        if (getAbsorbLevel() == 0)
            return;

        // If we have no _absorbersList initiated, do it
        AbsorberInfo ai = _absorbersList.get(attacker);


        // If the L2Character attacker isn't already in the _absorbersList of this L2Attackable, add it
        if (ai == null)
        {
            ai = new AbsorberInfo(attacker, crystalId, getCurrentHp());
            _absorbersList.put(attacker, ai);
        }
        else
        {
            ai._absorber = attacker;
            ai._crystalId = crystalId;
            ai._absorbedHP = getCurrentHp();
        }

        // Set this L2Attackable as absorbed
        absorbSoul();
    }

    /**
     * Calculate the leveling chance of Soul Crystals based on the attacker that killed this L2Attackable
     *
     * @param attacker The player that last killed this L2Attackable
     * $ Rewrite 06.12.06 - Yesod
     */
    private void levelSoulCrystals(L2Character attacker)
    {
        // Only L2PcInstance can absorb a soul
        if (!(attacker instanceof L2PcInstance) && !(attacker instanceof L2Summon))
        {
            resetAbsorbList(); return;
        }

        int maxAbsorbLevel = getAbsorbLevel();
        int minAbsorbLevel = 0;

        // If this is not a valid L2Attackable, clears the _absorbersList and just return
        if (maxAbsorbLevel == 0)
        {
            resetAbsorbList(); return;
        }
        // All boss mobs with maxAbsorbLevel 13 have minAbsorbLevel of 12 else 10
        if (maxAbsorbLevel > 10)
            minAbsorbLevel = maxAbsorbLevel > 12 ? 12 : 10;

        //Init some useful vars
        boolean isSuccess = true;
        boolean doLevelup = true;
        boolean isBossMob = maxAbsorbLevel > 10 ? true : false;

        L2NpcTemplate.AbsorbCrystalType absorbType = getTemplate().absorbType;

        L2PcInstance killer = (attacker instanceof L2Summon)? ((L2Summon)attacker).getOwner() : (L2PcInstance)attacker;

        // If this mob is a boss, then skip some checkings
        if (!isBossMob)
        {
            // Fail if this L2Attackable isn't absorbed or there's no one in its _absorbersList
            if (!isAbsorbed() /*|| _absorbersList == null*/)
            {
                resetAbsorbList();
                return;
            }

            // Fail if the killer isn't in the _absorbersList of this L2Attackable and mob is not boss
            AbsorberInfo ai = _absorbersList.get(killer);
            if (ai == null || ai._absorber.getObjectId() != killer.getObjectId())
                isSuccess = false;

            // Check if the soul crystal was used when HP of this L2Attackable wasn't higher than half of it
            if (ai != null && ai._absorbedHP > (getMaxHp()/2.0))
                isSuccess = false;

            if (!isSuccess) {
                resetAbsorbList();
                return;
            }
        }

        // ********
        String[] crystalNFO = null;
        String   crystalNME = "";

        int dice = Rnd.get(100);
        int crystalQTY = 0;
        int crystalLVL = 0;
        int crystalOLD = 0;
        int crystalNEW = 0;

        // ********
        // Now we have four choices:
        // 1- The Monster level is too low for the crystal. Nothing happens.
        // 2- Everything is correct, but it failed. Nothing happens. (57.5%)
        // 3- Everything is correct, but it failed. The crystal scatters. A sound event is played. (10%)
        // 4- Everything is correct, the crystal level up. A sound event is played. (32.5%)

        List<L2PcInstance> players = new ArrayList<>();

        if (absorbType == L2NpcTemplate.AbsorbCrystalType.FULL_PARTY && killer.isInParty())
            players = killer.getParty().getPartyMembers();
        else if (absorbType == L2NpcTemplate.AbsorbCrystalType.PARTY_ONE_RANDOM && killer.isInParty())
        {
        	// This is a naive method for selecting a random member.  It gets any random party member and
        	// then checks if the member has a valid crystal.  It does not select the random party member
        	// among those who have crystals, only.  However, this might actually be correct (same as retail).
            players.add(killer.getParty().getPartyMembers().get(Rnd.get(killer.getParty().getMemberCount())));
        }
        else
            players.add(killer);

        for (L2PcInstance player : players)
        {
            if (player == null) continue;
            crystalQTY = 0;

            L2ItemInstance[] inv = player.getInventory().getItems();
            for (L2ItemInstance item : inv)
            {
                int itemId = item.getItemId();
                for (int id : SoulCrystal.SoulCrystalTable)
                {
                    // Find any of the 39 possible crystals.
                    if (id == itemId)
                    {
                        crystalQTY++;
                        // Keep count but make sure the player has no more than 1 crystal
                        if (crystalQTY > 1)
                        {
                            isSuccess = false; break;
                        }

                        // Validate if the crystal has already leveled
                        if(id != SoulCrystal.RED_NEW_CRYSTAL
                        && id != SoulCrystal.GRN_NEW_CYRSTAL
                        && id != SoulCrystal.BLU_NEW_CRYSTAL)
                        {
                            try
                            {
                            	if (item.getItem().getName().contains("Grade"))
                            	{
	                                // Split the name of the crystal into 'name' & 'level'
                            		crystalNFO = item.getItem().getName().trim().replace(" Grade ", "-").split("-");
                            		// Set Level to 13
                            		crystalLVL = 13;
                            		// Get Name
                            		crystalNME = crystalNFO[0].toLowerCase();
                            	} else
                            	{
	                                // Split the name of the crystal into 'name' & 'level'
	                                crystalNFO = item.getItem().getName().trim().replace(" Stage ", "").split("-");
	                                // Get Level
	                                crystalLVL = Integer.parseInt(crystalNFO[1].trim());
	                                // Get Name
	                                crystalNME  = crystalNFO[0].toLowerCase();
                            	}
                                // Allocate current and levelup ids' for higher level crystals
                                if(crystalLVL > 9)
                                {
                                    for(int i = 0; i < SoulCrystal.HighSoulConvert.length; i++)
                                        // Get the next stage above 10 using array.
                                        if(id == SoulCrystal.HighSoulConvert[i][0])
                                        {
                                            crystalNEW = SoulCrystal.HighSoulConvert[i][1]; break;
                                        }
                                }
                                else
                                    crystalNEW = id+1;
                            }
                            catch (NumberFormatException nfe)
                            {
                                _log.log(Level.WARNING, "An attempt to identify a soul crystal failed, " +
                                                        "verify the names have not changed in etcitem "  +
                                                        "table.", nfe);

                                player.sendMessage("There has been an error handling your soul crystal." +
                                                   " Please notify your server admin.");

                                isSuccess = false;
                                break;
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                isSuccess = false;
                                break;
                            }
                        }
                        else
                        {
                            crystalNME = item.getItem().getName().toLowerCase().trim();
                            crystalNEW = id+1;
                        }

                        // Done
                        crystalOLD = id;
                        break;
                    }
                }
                if (!isSuccess) break;
            }

            // If the crystal level is way too high for this mob, say that we can't increase it
            if ((crystalLVL < minAbsorbLevel) || (crystalLVL >= maxAbsorbLevel))
                doLevelup = false;

            // The player doesn't have any crystals with him get to the next player.
            if (crystalQTY < 1 || crystalQTY > 1 || !isSuccess || !doLevelup)
            {
                // Too many crystals in inventory.
                if  (crystalQTY > 1)
                {
                    player.sendPacket(new SystemMessage(SystemMessageId.SOUL_CRYSTAL_ABSORBING_FAILED_RESONATION));
                }
                // The soul crystal stage of the player is way too high
                else if (!doLevelup)
                    player.sendPacket(new SystemMessage(SystemMessageId.SOUL_CRYSTAL_ABSORBING_REFUSED));

                crystalQTY = 0;
                continue;
            }

            /* TODO: Confirm boss chance for crystal level up and for crystal breaking.
             * It is known that bosses with FULL_PARTY crystal level ups have 100% success rate, but this is not
             * the case for the other bosses (one-random or last-hit).
             * While not confirmed, it is most reasonable that crystals leveled up at bosses will never break.
             * Also, the chance to level up is guessed as around 70% if not higher.
             */
            int chanceLevelUp = isBossMob? 70:SoulCrystal.LEVEL_CHANCE;

            // If succeeds or it is a full party absorb, level up the crystal.
            if (((absorbType == L2NpcTemplate.AbsorbCrystalType.FULL_PARTY) && doLevelup) || (dice <= chanceLevelUp))
            {
                // Give staged crystal
                exchangeCrystal(player, crystalOLD, crystalNEW, false);
            }

            // If true and not a last-hit mob, break the crystal.
            else if ((!isBossMob) && dice >= (100.0 - SoulCrystal.BREAK_CHANCE))
            {
                // Remove current crystal an give a broken open.
                if      (crystalNME.startsWith("red"))
                	exchangeCrystal(player, crystalOLD, SoulCrystal.RED_BROKEN_CRYSTAL, true);
                else if (crystalNME.startsWith("gre"))
                    exchangeCrystal(player, crystalOLD, SoulCrystal.GRN_BROKEN_CYRSTAL, true);
                else if (crystalNME.startsWith("blu"))
                    exchangeCrystal(player, crystalOLD, SoulCrystal.BLU_BROKEN_CRYSTAL, true);
                resetAbsorbList();
            }
            else
                player.sendPacket(new SystemMessage(SystemMessageId.SOUL_CRYSTAL_ABSORBING_FAILED));
        }
    }

    private void exchangeCrystal(L2PcInstance player, int takeid, int giveid, boolean broke)
    {
        L2ItemInstance Item = player.getInventory().destroyItemByItemId("SoulCrystal", takeid, 1, player, this);
        if (Item != null)
        {
            // Prepare inventory update packet
            InventoryUpdate playerIU = new InventoryUpdate();
            playerIU.addRemovedItem(Item);

            // Add new crystal to the killer's inventory
            Item = player.getInventory().addItem("SoulCrystal", giveid, 1, player, this);
            playerIU.addItem(Item);

            // Send a sound event and text message to the player
            if(broke)
            {
                player.sendPacket(new SystemMessage(SystemMessageId.SOUL_CRYSTAL_BROKE));
            }
            else
                player.sendPacket(new SystemMessage(SystemMessageId.SOUL_CRYSTAL_ABSORBING_SUCCEEDED));

            // Send system message
            SystemMessage sms = new SystemMessage(SystemMessageId.EARNED_ITEM);
            sms.addItemName(giveid);
            player.sendPacket(sms);

            // Send inventory update packet
            player.sendPacket(playerIU);
        }
    }

    private void resetAbsorbList()
    {
        _absorbed = false;
        _absorbersList.clear();
    }

    /**
     * Calculate the Experience and SP to distribute to attacker (L2PcInstance, L2SummonInstance or L2Party) of the L2Attackable.<BR><BR>
     *
     * @param diff The difference of level between attacker (L2PcInstance, L2SummonInstance or L2Party) and the L2Attackable
     * @param damage The damages given by the attacker (L2PcInstance, L2SummonInstance or L2Party)
     *
     */
    private int[] calculateExpAndSp(int diff, int damage)
    {
        double xp;
        double sp;

        if(diff < -5) diff = -5; // makes possible to use ALT_GAME_EXPONENT configuration
        xp = (double)getExpReward() * damage / getMaxHp();
        if (Config.ALT_GAME_EXPONENT_XP != 0) xp *= Math.pow(2., -diff / Config.ALT_GAME_EXPONENT_XP);

        sp = (double)getSpReward() * damage / getMaxHp();
        if (Config.ALT_GAME_EXPONENT_SP != 0) sp *= Math.pow(2., -diff / Config.ALT_GAME_EXPONENT_SP);

        if (Config.ALT_GAME_EXPONENT_XP == 0 && Config.ALT_GAME_EXPONENT_SP == 0)
        {
            if(diff > 5) // formula revised May 07
            {
            	double pow = Math.pow((double)5/6, diff-5);
            	xp = xp*pow;
            	sp = sp*pow;
            }

            if (xp <= 0)
            {
                xp = 0;
                sp = 0;
            }
            else if (sp <= 0)
            {
                sp = 0;
            }
        }

        int[] tmp = { (int)xp, (int)sp };

        return  tmp;
    }

    public long calculateOverhitExp(long normalExp)
    {
        // Get the percentage based on the total of extra (over-hit) damage done relative to the total (maximum) ammount of HP on the L2Attackable
        double overhitPercentage = ((getOverhitDamage() * 100) / getMaxHp());

        // Over-hit damage percentages are limited to 25% max
        if (overhitPercentage > 25)
            overhitPercentage = 25;

        // Get the overhit exp bonus according to the above over-hit damage percentage
        // (1/1 basis - 13% of over-hit damage, 13% of extra exp is given, and so on...)
        double overhitExp = ((overhitPercentage / 100) * normalExp);

        // Return the rounded ammount of exp points to be added to the player's normal exp reward
        long bonusOverhit = Math.round(overhitExp);
        return bonusOverhit;
    }

    /**
     * Return True.<BR><BR>
     */

    @Override
	public boolean isAttackable()
    {
        return true;
    }


    @Override
	public void onSpawn()
    {
    	super.onSpawn();
    	// Clear mob spoil,seed
        setSpoil(false);
        // Clear all aggro char from list
        clearAggroList();
        // Clear Harvester Rewrard List
    	_harvestItems = null;
    	// Clear mod Seeded stat
    	setSeeded(false);
        // Clear overhit value
        overhitEnabled(false);

        _sweepItems = null;
        resetAbsorbList();

        setWalking();

        // check the region where this mob is, do not activate the AI if region is inactive.
		if(!isInActiveRegion())
		{
			if(this instanceof L2SiegeGuardInstance)
			{
				((L2SiegeGuardAI) getAI()).stopAITask();
			}
			else
			{
				((L2AttackableAI) getAI()).stopAITask();
			}
		}
	}

    /**
     * Sets state of the mob to seeded. Paramets needed to be set before.
     */
    public void setSeeded()
    {
    	if (_seedType != 0 && _seeder != null)
    		setSeeded(_seedType, _seeder.getLevel());
    }

    /**
     * Sets the seed parametrs, but not the seed state
     * @param id  - id of the seed
     * @param seeder - player who is sowind the seed
     */
    public void setSeeded(int id, L2PcInstance seeder)
    {
    	if (!_seeded) 
        {
		_seedType = id;
		_seeder = seeder;
    	}
    }

    public void setSeeded(int id, int seederLvl)
    {
        _seeded = true;
        _seedType = id;
        int count = 1;

        Map<Integer, L2Skill> skills = getTemplate().getSkills();

        if (skills != null)
        {
		    for (int skillId : skills.keySet())
		    {
		    	switch (skillId) {
		    	case 4303: //Strong type x2
		    		count *= 2;
		    		break;
		    	case 4304: //Strong type x3
		    		count *= 3;
		    		break;
		    	case 4305: //Strong type x4
		    		count *= 4;
		    		break;
		    	case 4306: //Strong type x5
		    		count *= 5;
		    		break;
		    	case 4307: //Strong type x6
		    		count *= 6;
		    		break;
		    	case 4308: //Strong type x7
		    		count *= 7;
		    		break;
		    	case 4309: //Strong type x8
		    		count *= 8;
		    		break;
		    	case 4310: //Strong type x9
		    		count *= 9;
		    		break;
		    	}
		    }
        }

        int diff = (getLevel() - (L2Manor.getInstance().getSeedLevel(_seedType) - 5));

        // hi-lvl mobs bonus
        if (diff > 0)
        {
            count += diff;
        }

        List<RewardItem> harvested = new ArrayList<>();

        harvested.add(new RewardItem(L2Manor.getInstance().getCropType(_seedType), count* Config.RATE_DROP_MANOR));

        _harvestItems = harvested.toArray(new RewardItem[harvested.size()]);
    }

    public void setSeeded(boolean seeded)
    {
    	_seeded = seeded;
    }

    public L2PcInstance getSeeder()
    {
    	return _seeder;
    }

    public int getSeedType()
    {
    	return _seedType;
    }

    public boolean isSeeded()
    {
        return _seeded;
    }

    private int getAbsorbLevel()
    {
        return getTemplate().absorbLevel;
    }

    /**
     * Check if the server allows Random Animation.<BR><BR>
     */
    // This is located here because L2Monster and L2FriendlyMob both extend this class. The other non-pc instances extend either L2NpcInstance or L2MonsterInstance.

	@Override
	public boolean hasRandomAnimation()
    {
        return ((Config.MAX_MONSTER_ANIMATION > 0) && !(this instanceof L2GrandBossInstance));
    }


	@Override
	public boolean isMob()
    {
        return true; // This means we use MAX_MONSTER_ANIMATION instead of MAX_NPC_ANIMATION
    }
	protected void setCommandChannelTimer(CommandChannelTimer commandChannelTimer)
	{
		_commandChannelTimer = commandChannelTimer;
	}
	public CommandChannelTimer getCommandChannelTimer()
	{
		return _commandChannelTimer;
	}
	public L2CommandChannel getFirstCommandChannelAttacked()
	{
		return _firstCommandChannelAttacked;
	}
	public void setFirstCommandChannelAttacked(
			L2CommandChannel firstCommandChannelAttacked)
	{
		_firstCommandChannelAttacked = firstCommandChannelAttacked;
	}

	private class CommandChannelTimer implements Runnable
	{
		private L2Attackable _monster;
		private L2CommandChannel _channel;

		public CommandChannelTimer(L2Attackable monster, L2CommandChannel channel)
		{
			_monster = monster;
			_channel = channel;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
	
		public void run()
		{
			_monster.setCommandChannelTimer(null);
			_monster.setFirstCommandChannelAttacked(null);
			for (L2Character player : _monster.getAggroListRP().keySet())
			{
				if (player.isInParty() && player.getParty().isInCommandChannel())
				{
					if (player.getParty().getCommandChannel().equals(_channel))
					{
						// if a player which is in first attacked CommandChannel, restart the timer ;)
						_monster.setCommandChannelTimer(this);
						_monster.setFirstCommandChannelAttacked(_channel);
						ThreadPoolManager.getInstance().scheduleGeneral(this, 300000); // 5 min
						break;
					}
				}
			}
		}
	}
	
	public void returnHome()
	{
		clearAggroList();
		if (hasAI() && getSpawn() != null)
			getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(getSpawn().getLocx(), getSpawn().getLocy(), getSpawn().getLocz(), 0));
	}
}
