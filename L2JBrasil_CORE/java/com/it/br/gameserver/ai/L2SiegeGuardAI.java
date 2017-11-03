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
package com.it.br.gameserver.ai;

import static com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import static com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;

import java.util.concurrent.Future;

import com.it.br.Config;
import com.it.br.gameserver.GameTimeController;
import com.it.br.gameserver.GeoData;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Summon;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.actor.instance.L2FolkInstance;
import com.it.br.gameserver.model.actor.instance.L2MonsterInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2SiegeGuardInstance;
import com.it.br.util.Rnd;

/**
 * This class manages AI of L2Attackable.<BR>
 * <BR>
 */
public class L2SiegeGuardAI extends L2CharacterAI implements Runnable
{

	//protected static final Logger _log = Logger.getLogger(L2SiegeGuardAI.class.getName());

	private static final int MAX_ATTACK_TIMEOUT = 300; // int ticks, i.e. 30 seconds

	/** The L2Attackable AI task executed every 1s (call onEvtThink method) */
	private Future<?> _aiTask;

	/** The delay after wich the attacked is stopped */
	private int _attackTimeout;

	/** The L2Attackable aggro counter */
	private int _globalAggro;

	/** The flag used to indicate that a thinking action is in progress */
	private boolean _thinking; // to prevent recursive thinking

	private int _attackRange;

	/**
	 * Constructor of L2AttackableAI.<BR>
	 * <BR>
	 * 
	 * @param accessor The AI accessor of the L2Character
	 */
	public L2SiegeGuardAI(L2Character.AIAccessor accessor)
	{
		super(accessor);

		_attackTimeout = Integer.MAX_VALUE;
		_globalAggro = -10; // 10 seconds timeout of ATTACK after respawn

		_attackRange = ((L2Attackable) _actor).getPhysicalAttackRange();
	}


	public void run()
	{
		// Launch actions corresponding to the Event Think
		onEvtThink();

	}

	/**
	 * Return True if the target is autoattackable (depends on the actor type).<BR>
	 * <BR>
	 * <B><U> Actor is a L2GuardInstance</U> :</B><BR>
	 * <BR>
	 * <li>The target isn't a Folk or a Door</li> <li>The target isn't dead, isn't invulnerable, isn't in silent moving
	 * mode AND too far (>100)</li> <li>The target is in the actor Aggro range and is at the same height</li> <li>The
	 * L2PcInstance target has karma (=PK)</li> <li>The L2MonsterInstance target is aggressive</li><BR>
	 * <BR>
	 * <B><U> Actor is a L2SiegeGuardInstance</U> :</B><BR>
	 * <BR>
	 * <li>The target isn't a Folk or a Door</li> <li>The target isn't dead, isn't invulnerable, isn't in silent moving
	 * mode AND too far (>100)</li> <li>The target is in the actor Aggro range and is at the same height</li> <li>A
	 * siege is in progress</li> <li>The L2PcInstance target isn't a Defender</li><BR>
	 * <BR>
	 * <B><U> Actor is a L2FriendlyMobInstance</U> :</B><BR>
	 * <BR>
	 * <li>The target isn't a Folk, a Door or another L2NpcInstance</li> <li>The target isn't dead, isn't invulnerable,
	 * isn't in silent moving mode AND too far (>100)</li> <li>The target is in the actor Aggro range and is at the same
	 * height</li> <li>The L2PcInstance target has karma (=PK)</li><BR>
	 * <BR>
	 * <B><U> Actor is a L2MonsterInstance</U> :</B><BR>
	 * <BR>
	 * <li>The target isn't a Folk, a Door or another L2NpcInstance</li> <li>The target isn't dead, isn't invulnerable,
	 * isn't in silent moving mode AND too far (>100)</li> <li>The target is in the actor Aggro range and is at the same
	 * height</li> <li>The actor is Aggressive</li><BR>
	 * <BR>
	 * 
	 * @param target The targeted L2Object
	 */
	private boolean autoAttackCondition(L2Character target)
	{
		// Check if the target isn't another guard, folk or a door
		if(target == null || target instanceof L2SiegeGuardInstance || target instanceof L2FolkInstance || target instanceof L2DoorInstance || target.isAlikeDead() || target.isInvul())
			return false;

		// Get the owner if the target is a summon
		if(target instanceof L2Summon)
		{
			L2PcInstance owner = ((L2Summon) target).getOwner();
			if(_actor.isInsideRadius(owner, 1000, true, false))
			{
				target = owner;
			}

			owner = null;
		}

		// Check if the target is a L2PcInstance
		if(target instanceof L2PcInstance)
		{
			// Check if the target isn't in silent move mode AND too far (>100)
			if(((L2PcInstance) target).isSilentMoving() && !_actor.isInsideRadius(target, 250, false, false))
				return false;
		}
		// Los Check Here
		return _actor.isAutoAttackable(target) && GeoData.getInstance().canSeeTarget(_actor, target);

	}

	/**
	 * Set the Intention of this L2CharacterAI and create an AI Task executed every 1s (call onEvtThink method) for this
	 * L2Attackable.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : If actor _knowPlayer isn't EMPTY, AI_INTENTION_IDLE will be change in
	 * AI_INTENTION_ACTIVE</B></FONT><BR>
	 * <BR>
	 * 
	 * @param intention The new Intention to set to the AI
	 * @param arg0 The first parameter of the Intention
	 * @param arg1 The second parameter of the Intention
	 */

	@Override
	synchronized void changeIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		if(Config.DEBUG)
		{
			_log.info("L2SiegeAI.changeIntention(" + intention + ", " + arg0 + ", " + arg1 + ")");
		}

		((L2Attackable) _actor).setisReturningToSpawnPoint(false);

		if(intention == AI_INTENTION_IDLE /*|| intention == AI_INTENTION_ACTIVE*/) // active becomes idle if only a summon is present
		{
			// Check if actor is not dead
			if(!_actor.isAlikeDead())
			{
				L2Attackable npc = (L2Attackable) _actor;

				// If its _knownPlayer isn't empty set the Intention to AI_INTENTION_ACTIVE
				if(npc.getKnownList().getKnownPlayers().size() > 0)
				{
					intention = AI_INTENTION_ACTIVE;
				}
				else
				{
					intention = AI_INTENTION_IDLE;
				}

				npc = null;
			}

			if(intention == AI_INTENTION_IDLE)
			{
				// Set the Intention of this L2AttackableAI to AI_INTENTION_IDLE
				super.changeIntention(AI_INTENTION_IDLE, null, null);

				// Stop AI task and detach AI from NPC
				if(_aiTask != null)
				{
					_aiTask.cancel(true);
					_aiTask = null;
				}

				// Cancel the AI
				_accessor.detachAI();

				return;
			}
		}

		// Set the Intention of this L2AttackableAI to intention
		super.changeIntention(intention, arg0, arg1);

		// If not idle - create an AI task (schedule onEvtThink repeatedly)
		if(_aiTask == null)
		{
			_aiTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(this, 1000, 1000);
		}
	}

	/**
	 * Manage the Attack Intention : Stop current Attack (if necessary), Calculate attack timeout, Start a new Attack
	 * and Launch Think Event.<BR>
	 * <BR>
	 * 
	 * @param target The L2Character to attack
	 */

	@Override
	protected void onIntentionAttack(L2Character target)
	{
		// Calculate the attack timeout
		_attackTimeout = MAX_ATTACK_TIMEOUT + GameTimeController.getGameTicks();

		// Manage the Attack Intention : Stop current Attack (if necessary), Start a new Attack and Launch Think Event
		//if (_actor.getTarget() != null)
		super.onIntentionAttack(target);
	}

	/**
	 * Manage AI standard thinks of a L2Attackable (called by onEvtThink).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Update every 1s the _globalAggro counter to come close to 0</li> <li>If the actor is Aggressive and can
	 * attack, add all autoAttackable L2Character in its Aggro Range to its _aggroList, chose a target and order to
	 * attack it</li> <li>If the actor can't attack, order to it to return to its home location</li>
	 */
	private void thinkActive()
	{
		L2Attackable npc = (L2Attackable) _actor;

		// Update every 1s the _globalAggro counter to come close to 0
		if(_globalAggro != 0)
		{
			if(_globalAggro < 0)
			{
				_globalAggro++;
			}
			else
			{
				_globalAggro--;
			}
		}

		// Add all autoAttackable L2Character in L2Attackable Aggro Range to its _aggroList with 0 damage and 1 hate
		// A L2Attackable isn't aggressive during 10s after its spawn because _globalAggro is set to -10
		if(_globalAggro >= 0)
		{
			for(L2Character target : npc.getKnownList().getKnownCharactersInRadius(_attackRange))
			{
				if(target == null)
				{
					continue;
				}

				if(autoAttackCondition(target)) // check aggression
				{
					// Get the hate level of the L2Attackable against this L2Character target contained in _aggroList
					int hating = npc.getHating(target);

					// Add the attacker to the L2Attackable _aggroList with 0 damage and 1 hate
					if(hating == 0)
					{
						npc.addDamageHate(target, 0, 1);
					}
				}
			}

			// Chose a target from its aggroList
			L2Character hated;

			// Force mobs to attak anybody if confused
			if(_actor.isConfused())
			{
				hated = _attackTarget;
			}
			else
			{
				hated = npc.getMostHated();
			}

			// Order to the L2Attackable to attack the target
			if(hated != null)
			{
				// Get the hate level of the L2Attackable against this L2Character target contained in _aggroList
				int aggro = npc.getHating(hated);

				if(aggro + _globalAggro > 0)
				{
					// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
					if(!_actor.isRunning())
					{
						_actor.setRunning();
					}

					// Set the AI Intention to AI_INTENTION_ATTACK
					setIntention(CtrlIntention.AI_INTENTION_ATTACK, hated, null);
				}

				return;
			}

		}

		npc = null;

		// Order to the L2SiegeGuardInstance to return to its home location because there's no target to attack
		((L2SiegeGuardInstance) _actor).returnHome();

		return;

	}

	private void attackPrepare()
	{
		// Get all information needed to chose between physical or magical attack
		L2Skill[] skills = null;
		double dist_2 = 0;
		int range = 0;
		L2SiegeGuardInstance sGuard = (L2SiegeGuardInstance) _actor;

		try
		{
			_actor.setTarget(_attackTarget);
			skills = _actor.getAllSkills();
			dist_2 = _actor.getPlanDistanceSq(_attackTarget.getX(), _attackTarget.getY());
			range = _actor.getPhysicalAttackRange() + _actor.getTemplate().collisionRadius + _attackTarget.getTemplate().collisionRadius;
		}
		catch(NullPointerException e)
		{
			//_log.warning("AttackableAI: Attack target is NULL.");
			_actor.setTarget(null);
			setIntention(AI_INTENTION_IDLE, null, null);
			return;
		}

		// never attack defenders
		if(_attackTarget instanceof L2PcInstance && sGuard.getCastle().getSiege().checkIsDefender(((L2PcInstance) _attackTarget).getClan()))
		{
			// Cancel the target
			sGuard.stopHating(_attackTarget);
			_actor.setTarget(null);
			setIntention(AI_INTENTION_IDLE, null, null);
			return;
		}

		if(!GeoData.getInstance().canSeeTarget(_actor, _attackTarget))
		{
			// Siege guards differ from normal mobs currently:
			// If target cannot seen, don't attack any more
			sGuard.stopHating(_attackTarget);
			_actor.setTarget(null);
			setIntention(AI_INTENTION_IDLE, null, null);
			return;
		}

		// Check if the actor isn't muted and if it is far from target
		if(!_actor.isMuted() && dist_2 > (range + 20) * (range + 20))
		{
			// check for long ranged skills and heal/buff skills
			if(!Config.ALT_GAME_MOB_ATTACK_AI || _actor instanceof L2MonsterInstance && Rnd.nextInt(100) <= 5)
			{
				for(L2Skill sk : skills)
				{
					int castRange = sk.getCastRange();

					if((sk.getSkillType() == L2Skill.SkillType.BUFF || sk.getSkillType() == L2Skill.SkillType.HEAL || dist_2 >= castRange * castRange / 9 && dist_2 <= castRange * castRange && castRange > 70) && !_actor.isSkillDisabled(sk.getId()) && _actor.getCurrentMp() >= _actor.getStat().getMpConsume(sk) && !sk.isPassive())
					{
						if(sk.getSkillType() == L2Skill.SkillType.BUFF || sk.getSkillType() == L2Skill.SkillType.HEAL)
						{
							boolean useSkillSelf = true;

							if(sk.getSkillType() == L2Skill.SkillType.HEAL && _actor.getCurrentHp() > (int) (_actor.getMaxHp() / 1.5))
							{
								useSkillSelf = false;
								break;
							}
							if(sk.getSkillType() == L2Skill.SkillType.BUFF)
							{
								L2Effect[] effects = _actor.getAllEffects();

								for(int i = 0; effects != null && i < effects.length; i++)
								{
									L2Effect effect = effects[i];

									if(effect.getSkill() == sk)
									{
										useSkillSelf = false;
										break;
									}
								}

								effects = null;
							}
							if(useSkillSelf)
							{
								_actor.setTarget(_actor);
							}
						}

						L2Object OldTarget = _actor.getTarget();

						clientStopMoving(null);
						_accessor.doCast(sk);
						_actor.setTarget(OldTarget);

						OldTarget = null;

						return;
					}
				}
			}

			// Check if the L2SiegeGuardInstance is attacking, knows the target and can't run
			if(!_actor.isAttackingNow() && _actor.getRunSpeed() == 0 && _actor.getKnownList().knowsObject(_attackTarget))
			{
				// Cancel the target
				_actor.getKnownList().removeKnownObject(_attackTarget);
				_actor.setTarget(null);
				setIntention(AI_INTENTION_IDLE, null, null);
			}
			else
			{
				double dx = _actor.getX() - _attackTarget.getX();
				double dy = _actor.getY() - _attackTarget.getY();
				double dz = _actor.getZ() - _attackTarget.getZ();
				double homeX = _attackTarget.getX() - sGuard.getHomeX();
				double homeY = _attackTarget.getY() - sGuard.getHomeY();

				// Check if the L2SiegeGuardInstance isn't too far from it's home location
				if(dx * dx + dy * dy > 10000 && homeX * homeX + homeY * homeY > 3240000 && _actor.getKnownList().knowsObject(_attackTarget))
				{
					// Cancel the target
					_actor.getKnownList().removeKnownObject(_attackTarget);
					_actor.setTarget(null);
					setIntention(AI_INTENTION_IDLE, null, null);
				}
				else
				// Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
				{
					// Temporary hack for preventing guards jumping off towers,
					// before replacing this with effective geodata checks and AI modification
					if(dz * dz < 170 * 170)
					{
						moveToPawn(_attackTarget, range);
					}
				}
			}

			return;

		}
		// Else, if the actor is muted and far from target, just "move to pawn"
		else if(_actor.isMuted() && dist_2 > (range + 20) * (range + 20))
		{
			// Temporary hack for preventing guards jumping off towers,
			// before replacing this with effective geodata checks and AI modification
			double dz = _actor.getZ() - _attackTarget.getZ();

			// normally 130 if guard z coordinates correct
			if(dz * dz < 170 * 170)
			{
				moveToPawn(_attackTarget, range);
			}

			return;
		}
		// Else, if this is close enough to attack
		else if(dist_2 <= (range + 20) * (range + 20))
		{
			// Force mobs to attak anybody if confused
			L2Character hated = null;

			if(_actor.isConfused())
			{
				hated = _attackTarget;
			}
			else
			{
				hated = ((L2Attackable) _actor).getMostHated();
			}

			if(hated == null)
			{
				setIntention(AI_INTENTION_ACTIVE, null, null);
				return;
			}

			if(hated != _attackTarget)
			{
				_attackTarget = hated;
			}

			hated = null;

			_attackTimeout = MAX_ATTACK_TIMEOUT + GameTimeController.getGameTicks();

			// check for close combat skills && heal/buff skills
			if(!_actor.isMuted() && Rnd.nextInt(100) <= 5)
			{
				for(L2Skill sk : skills)
				{
					int castRange = sk.getCastRange();

					if(castRange * castRange >= dist_2 && castRange <= 70 && !sk.isPassive() && _actor.getCurrentMp() >= _actor.getStat().getMpConsume(sk) && !_actor.isSkillDisabled(sk.getId()))
					{
						if(sk.getSkillType() == L2Skill.SkillType.BUFF || sk.getSkillType() == L2Skill.SkillType.HEAL)
						{
							boolean useSkillSelf = true;

							if(sk.getSkillType() == L2Skill.SkillType.HEAL && _actor.getCurrentHp() > (int) (_actor.getMaxHp() / 1.5))
							{
								useSkillSelf = false;
								break;
							}

							if(sk.getSkillType() == L2Skill.SkillType.BUFF)
							{
								L2Effect[] effects = _actor.getAllEffects();

								for(int i = 0; effects != null && i < effects.length; i++)
								{
									L2Effect effect = effects[i];

									if(effect.getSkill() == sk)
									{
										useSkillSelf = false;
										break;
									}
								}
							}
							if(useSkillSelf)
							{
								_actor.setTarget(_actor);
							}
						}

						L2Object OldTarget = _actor.getTarget();

						clientStopMoving(null);
						_accessor.doCast(sk);
						_actor.setTarget(OldTarget);

						OldTarget = null;

						return;
					}
				}
			}
			// Finally, do the physical attack itself
			_accessor.doAttack(_attackTarget);

			skills = null;
			sGuard = null;
		}
	}

	/**
	 * Manage AI attack thinks of a L2Attackable (called by onEvtThink).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Update the attack timeout if actor is running</li> <li>If target is dead or timeout is expired, stop this
	 * attack and set the Intention to AI_INTENTION_ACTIVE</li> <li>Call all L2Object of its Faction inside the Faction
	 * Range</li> <li>Chose a target and order to attack it with magic skill or physical attack</li><BR>
	 * <BR>
	 * TODO: Manage casting rules to healer mobs (like Ant Nurses)
	 */
	private void thinkAttack()
	{
		if(Config.DEBUG)
		{
			_log.info("L2SiegeGuardAI.thinkAttack(); timeout=" + (_attackTimeout - GameTimeController.getGameTicks()));
		}

		if(_attackTimeout < GameTimeController.getGameTicks())
		{
			// Check if the actor is running
			if(_actor.isRunning())
			{
				// Set the actor movement type to walk and send Server->Client packet ChangeMoveType to all others L2PcInstance
				_actor.setWalking();

				// Calculate a new attack timeout
				_attackTimeout = MAX_ATTACK_TIMEOUT + GameTimeController.getGameTicks();
			}
		}

		// Check if target is dead or if timeout is expired to stop this attack
		if(_attackTarget == null || _attackTarget.isAlikeDead() || _attackTimeout < GameTimeController.getGameTicks())
		{
			// Stop hating this target after the attack timeout or if target is dead
			if(_attackTarget != null)
			{
				L2Attackable npc = (L2Attackable) _actor;

				npc.stopHating(_attackTarget);

				npc = null;
			}

			// Cancel target and timeout
			_attackTimeout = Integer.MAX_VALUE;
			_attackTarget = null;

			// Set the AI Intention to AI_INTENTION_ACTIVE
			setIntention(AI_INTENTION_ACTIVE, null, null);

			_actor.setWalking();
			return;
		}

		attackPrepare();
		factionNotify();
	}

	private final void factionNotify()
	{
		// Call all L2Object of its Faction inside the Faction Range
		if(((L2NpcInstance) _actor).getFactionId() == null || _attackTarget == null || _actor == null)
			return;

		if(_attackTarget.isInvul())
			return;

		// Go through all L2Object that belong to its faction
		for(L2Character cha : _actor.getKnownList().getKnownCharactersInRadius(1000))
		{
			if(cha == null)
			{
				continue;
			}

			if(!(cha instanceof L2NpcInstance))
			{
				continue;
			}

			L2NpcInstance npc = (L2NpcInstance) cha;

			String faction_id = ((L2NpcInstance) _actor).getFactionId();

			if(faction_id != npc.getFactionId())
			{
				continue;
			}

			faction_id = null;

			// Check if the L2Object is inside the Faction Range of the actor
			if((npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE || npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) && _actor.isInsideRadius(npc, npc.getFactionRange(), false, true) && npc.getTarget() == null && _attackTarget.isInsideRadius(npc, npc.getFactionRange(), false, true))
			{
				if(Config.GEODATA > 0)
				{
					if(GeoData.getInstance().canSeeTarget(npc, _attackTarget))
					{
						// Notify the L2Object AI with EVT_AGGRESSION
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _attackTarget, 1);
					}
				}
				else
				{
					if(!npc.isDead() && Math.abs(_attackTarget.getZ() - npc.getZ()) < 600)
					{
						// Notify the L2Object AI with EVT_AGGRESSION
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _attackTarget, 1);
					}
				}
			}
			npc = null;
		}
	}

	/**
	 * Manage AI thinking actions of a L2Attackable.<BR>
	 * <BR>
	 */

	@Override
	protected void onEvtThink()
	{
		// Check if the actor can't use skills and if a thinking action isn't already in progress
		if(_thinking || _actor.isAllSkillsDisabled())
			return;

		// Start thinking action
		_thinking = true;

		try
		{
			// Manage AI thinks of a L2Attackable
			if(getIntention() == AI_INTENTION_ACTIVE)
			{
				thinkActive();
			}
			else if(getIntention() == AI_INTENTION_ATTACK)
			{
				thinkAttack();
			}
		}
		finally
		{
			// Stop thinking action
			_thinking = false;
		}
	}

	/**
	 * Launch actions corresponding to the Event Attacked.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Init the attack : Calculate the attack timeout, Set the _globalAggro to 0, Add the attacker to the actor
	 * _aggroList</li> <li>Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all
	 * others L2PcInstance</li> <li>Set the Intention to AI_INTENTION_ATTACK</li><BR>
	 * <BR>
	 * 
	 * @param attacker The L2Character that attacks the actor
	 */

	@Override
	protected void onEvtAttacked(L2Character attacker)
	{
		// Calculate the attack timeout
		_attackTimeout = MAX_ATTACK_TIMEOUT + GameTimeController.getGameTicks();

		// Set the _globalAggro to 0 to permit attack even just after spawn
		if(_globalAggro < 0)
		{
			_globalAggro = 0;
		}

		// Add the attacker to the _aggroList of the actor
		((L2Attackable) _actor).addDamageHate(attacker, 0, 1);

		// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
		if(!_actor.isRunning())
		{
			_actor.setRunning();
		}

		// Set the Intention to AI_INTENTION_ATTACK
		if(getIntention() != AI_INTENTION_ATTACK)
		{
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker, null);
		}

		super.onEvtAttacked(attacker);
	}

	/**
	 * Launch actions corresponding to the Event Aggression.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Add the target to the actor _aggroList or update hate if already present</li> <li>Set the actor Intention to
	 * AI_INTENTION_ATTACK (if actor is L2GuardInstance check if it isn't too far from its home location)</li><BR>
	 * <BR>
	 * 
	 * @param attacker The L2Character that attacks
	 * @param aggro The value of hate to add to the actor against the target
	 */

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
		if(_actor == null)
			return;

		L2Attackable me = (L2Attackable) _actor;

		if(target != null)
		{
			// Add the target to the actor _aggroList or update hate if already present
			me.addDamageHate(target, 0, aggro);

			// Get the hate of the actor against the target
			aggro = me.getHating(target);

			if(aggro <= 0)
			{
				if(me.getMostHated() == null)
				{
					_globalAggro = -25;
					me.clearAggroList();
					setIntention(AI_INTENTION_IDLE, null, null);
				}
				return;
			}

			// Set the actor AI Intention to AI_INTENTION_ATTACK
			if(getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
			{
				// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
				if(!_actor.isRunning())
				{
					_actor.setRunning();
				}

				L2SiegeGuardInstance sGuard = (L2SiegeGuardInstance) _actor;

				double homeX = target.getX() - sGuard.getHomeX();
				double homeY = target.getY() - sGuard.getHomeY();

				// Check if the L2SiegeGuardInstance is not too far from its home location
				if(homeX * homeX + homeY * homeY < 3240000)
				{
					setIntention(CtrlIntention.AI_INTENTION_ATTACK, target, null);
				}

				sGuard = null;
			}
		}
		else
		{
			// currently only for setting lower general aggro
			if(aggro >= 0)
				return;

			L2Character mostHated = me.getMostHated();
			if(mostHated == null)
			{
				_globalAggro = -25;
				return;
			}
			for(L2Character aggroed : me.getAggroListRP().keySet())
			{
				me.addDamageHate(aggroed, 0, aggro);
			}

			aggro = me.getHating(mostHated);
			if(aggro <= 0)
			{
				_globalAggro = -25;
				me.clearAggroList();
				setIntention(AI_INTENTION_IDLE, null, null);
			}
			mostHated = null;
		}
		me = null;
	}


	@Override
	protected void onEvtDead()
	{
		stopAITask();
		super.onEvtDead();
	}

	public void stopAITask()
	{
		if(_aiTask != null)
		{
			_aiTask.cancel(false);
			_aiTask = null;
		}
		_accessor.detachAI();
	}

}
