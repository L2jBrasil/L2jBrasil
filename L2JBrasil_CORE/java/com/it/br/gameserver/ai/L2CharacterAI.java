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
import static com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_CAST;
import static com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_FOLLOW;
import static com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;
import static com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_INTERACT;
import static com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_MOVE_TO;
import static com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_PICK_UP;
import static com.it.br.gameserver.ai.CtrlIntention.AI_INTENTION_REST;

import com.it.br.gameserver.model.L2Attackable;
import com.it.br.gameserver.model.L2CharPosition;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.actor.instance.L2BoatInstance;
import com.it.br.gameserver.model.actor.instance.L2DoorInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.serverpackets.AutoAttackStop;
import com.it.br.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * This class manages AI of L2Character.<BR>
 * <BR>
 * L2CharacterAI :<BR>
 * <BR>
 * <li>L2AttackableAI</li> <li>L2DoorAI</li> <li>L2PlayerAI</li> <li>L2SummonAI</li><BR>
 * <BR>
 */
public class L2CharacterAI extends AbstractAI
{
	private static final int ZONE_PVP = 1;


	@Override
	protected void onEvtAttacked(L2Character attacker)
	{
		clientStartAutoAttack();
	}

	/**
	 * Constructor of L2CharacterAI.<BR>
	 * <BR>
	 * 
	 * @param accessor The AI accessor of the L2Character
	 */
	public L2CharacterAI(L2Character.AIAccessor accessor)
	{
		super(accessor);
	}

	/**
	 * Manage the Idle Intention : Stop Attack, Movement and Stand Up the actor.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Set the AI Intention to AI_INTENTION_IDLE</li> <li>Init cast and attack target</li> <li>Stop the actor
	 * auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li> <li>Stop the actor
	 * movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)</li> <li>
	 * Stand up the actor server side AND client side by sending Server->Client packet ChangeWaitType (broadcast)</li><BR>
	 * <BR>
	 */

	@Override
	protected void onIntentionIdle()
	{
		// Set the AI Intention to AI_INTENTION_IDLE
		changeIntention(AI_INTENTION_IDLE, null, null);

		// Init cast and attack target
		setCastTarget(null);
		setAttackTarget(null);

		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);

		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		clientStopAutoAttack();
	}

	/**
	 * Manage the Active Intention : Stop Attack, Movement and Launch Think Event.<BR>
	 * <BR>
	 * <B><U> Actions</U> : <I>if the Intention is not already Active</I></B><BR>
	 * <BR>
	 * <li>Set the AI Intention to AI_INTENTION_ACTIVE</li> <li>Init cast and attack target</li> <li>Stop the actor
	 * auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li> <li>Stop the actor
	 * movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)</li> <li>
	 * Launch the Think Event</li><BR>
	 * <BR>
	 */
	protected void onIntentionActive(L2Character target)
	{
		if(target instanceof L2PcInstance && _actor instanceof L2PcInstance)
		{
			if(((L2PcInstance) _actor).getKarma() > 0 && _actor.getLevel() - target.getLevel() >= 10 && ((L2PlayableInstance) target).getProtectionBlessing() && !target.isInsideZone(ZONE_PVP))
			{
				//If attacker have karma and have level >= 10 than his target and target have Newbie Protection Buff,
				clientActionFailed();
				return;
			}
		}

		// Check if the Intention is not already Active
		if(getIntention() != AI_INTENTION_ACTIVE)
		{
			// Set the AI Intention to AI_INTENTION_ACTIVE
			changeIntention(AI_INTENTION_ACTIVE, null, null);

			// Init cast and attack target
			setCastTarget(null);
			setAttackTarget(null);

			// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
			clientStopMoving(null);

			// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
			clientStopAutoAttack();

			// Also enable random animations for this L2Character if allowed
			// This is only for mobs - town npcs are handled in their constructor
			if(_actor instanceof L2Attackable)
			{
				((L2NpcInstance) _actor).startRandomAnimationTimer();
			}

			// Launch the Think Event
			onEvtThink();
		}
	}

	/**
	 * Manage the Rest Intention.<BR>
	 * <BR>
	 * <B><U> Actions</U> : </B><BR>
	 * <BR>
	 * <li>Set the AI Intention to AI_INTENTION_IDLE</li><BR>
	 * <BR>
	 */

	@Override
	protected void onIntentionRest()
	{
		// Set the AI Intention to AI_INTENTION_IDLE
		setIntention(AI_INTENTION_IDLE);
	}

	/**
	 * Manage the Attack Intention : Stop current Attack (if necessary), Start a new Attack and Launch Think Event.<BR>
	 * <BR>
	 * <B><U> Actions</U> : </B><BR>
	 * <BR>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li> <li>
	 * Set the Intention of this AI to AI_INTENTION_ATTACK</li> <li>Set or change the AI attack target</li> <li>Start
	 * the actor Auto Attack client side by sending Server->Client packet AutoAttackStart (broadcast)</li> <li>Launch
	 * the Think Event</li><BR>
	 * <BR>
	 * <B><U> Overridden in</U> :</B><BR>
	 * <BR>
	 * <li>L2AttackableAI : Calculate attack timeout</li><BR>
	 * <BR>
	 */

	@Override
	protected void onIntentionAttack(L2Character target)
	{
		if(target == null)
		{
			clientActionFailed();
			return;
		}

		if(getIntention() == AI_INTENTION_REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		if(_actor.isAllSkillsDisabled() || _actor.isAfraid())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		// Check if the Intention is already AI_INTENTION_ATTACK
		if(getIntention() == AI_INTENTION_ATTACK)
		{
			// Check if the AI already targets the L2Character
			if(getAttackTarget() != target)
			{
				// Set the AI attack target (change target)
				setAttackTarget(target);

				stopFollow();

				// Launch the Think Event
				notifyEvent(CtrlEvent.EVT_THINK, null);

			}
			else
			{
				clientActionFailed(); // else client freezes until cancel target
			}
		}
		else
		{
			// Set the Intention of this AbstractAI to AI_INTENTION_ATTACK
			changeIntention(AI_INTENTION_ATTACK, target, null);

			// Set the AI attack target
			setAttackTarget(target);

			stopFollow();

			// Launch the Think Event
			notifyEvent(CtrlEvent.EVT_THINK, null);
		}
	}

	/**
	 * Manage the Cast Intention : Stop current Attack, Init the AI in order to cast and Launch Think Event.<BR>
	 * <BR>
	 * <B><U> Actions</U> : </B><BR>
	 * <BR>
	 * <li>Set the AI cast target</li> <li>Stop the actor auto-attack client side by sending Server->Client packet
	 * AutoAttackStop (broadcast)</li> <li>Cancel action client side by sending Server->Client packet ActionFailed to
	 * the L2PcInstance actor</li> <li>Set the AI skill used by INTENTION_CAST</li> <li>Set the Intention of this AI to
	 * AI_INTENTION_CAST</li> <li>Launch the Think Event</li><BR>
	 * <BR>
	 */

	@Override
	protected void onIntentionCast(L2Skill skill, L2Object target)
	{
		if(getIntention() == AI_INTENTION_REST && skill.isMagic())
		{
			clientActionFailed();
			return;
		}

		if(_actor.isAllSkillsDisabled())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		// can't cast if muted
		if(_actor.isMuted() && skill.isMagic())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}
		if(target instanceof L2PcInstance && _actor instanceof L2PcInstance)
		{
			if(((L2PcInstance) _actor).getKarma() > 0 && _actor.getLevel() - ((L2PcInstance) target).getLevel() >= 10 && ((L2PlayableInstance) target).getProtectionBlessing() && !((L2Character) target).isInsideZone(ZONE_PVP))
			{
				//If attacker have karma and have level >= 10 than his target and target have Newbie Protection Buff,
				clientActionFailed();
				return;
			}
		}

		// Set the AI cast target
		setCastTarget((L2Character) target);

		// Stop actions client-side to cast the skill
		if(skill.getHitTime() > 50)
		{
			// Abort the attack of the L2Character and send Server->Client ActionFailed packet
			_actor.abortAttack();

			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			// no need for second ActionFailed packet, abortAttack() already sent it
			//clientActionFailed();
		}

		// Set the AI skill used by INTENTION_CAST
		_skill = skill;

		// Change the Intention of this AbstractAI to AI_INTENTION_CAST
		changeIntention(AI_INTENTION_CAST, skill, target);

		// Launch the Think Event
		notifyEvent(CtrlEvent.EVT_THINK, null);
	}

	/**
	 * Manage the Move To Intention : Stop current Attack and Launch a Move to Location Task.<BR>
	 * <BR>
	 * <B><U> Actions</U> : </B><BR>
	 * <BR>
	 * <li>Stop the actor auto-attack server side AND client side by sending Server->Client packet AutoAttackStop
	 * (broadcast)</li> <li>Set the Intention of this AI to AI_INTENTION_MOVE_TO</li> <li>Move the actor to Location
	 * (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)</li><BR>
	 * <BR>
	 */

	@Override
	protected void onIntentionMoveTo(L2CharPosition pos)
	{
		if(getIntention() == AI_INTENTION_REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		if(_actor.isAllSkillsDisabled())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		// Set the Intention of this AbstractAI to AI_INTENTION_MOVE_TO
		changeIntention(AI_INTENTION_MOVE_TO, pos, null);

		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		clientStopAutoAttack();

		// Abort the attack of the L2Character and send Server->Client ActionFailed packet
		_actor.abortAttack();

		// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
		moveTo(pos.x, pos.y, pos.z);
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.ai.AbstractAI#onIntentionMoveToInABoat(com.it.br.gameserver.model.L2CharPosition, com.it.br.gameserver.model.L2CharPosition)
	 */

	@Override
	protected void onIntentionMoveToInABoat(L2CharPosition destination, L2CharPosition origin)
	{
		if(getIntention() == AI_INTENTION_REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		if(_actor.isAllSkillsDisabled())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		// Set the Intention of this AbstractAI to AI_INTENTION_MOVE_TO
		//
		//changeIntention(AI_INTENTION_MOVE_TO, new L2CharPosition(((L2PcInstance)_actor).getBoat().getX() - destination.x, ((L2PcInstance)_actor).getBoat().getY() - destination.y, ((L2PcInstance)_actor).getBoat().getZ() - destination.z, 0)  , null);

		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		clientStopAutoAttack();

		// Abort the attack of the L2Character and send Server->Client ActionFailed packet
		_actor.abortAttack();

		// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
		moveToInABoat(destination, origin);
	}

	/**
	 * Manage the Follow Intention : Stop current Attack and Launch a Follow Task.<BR>
	 * <BR>
	 * <B><U> Actions</U> : </B><BR>
	 * <BR>
	 * <li>Stop the actor auto-attack server side AND client side by sending Server->Client packet AutoAttackStop
	 * (broadcast)</li> <li>Set the Intention of this AI to AI_INTENTION_FOLLOW</li> <li>Create and Launch an AI Follow
	 * Task to execute every 1s</li><BR>
	 * <BR>
	 */

	@Override
	protected void onIntentionFollow(L2Character target)
	{
		if(getIntention() == AI_INTENTION_REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		if(_actor.isAllSkillsDisabled())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		if( _actor.isRooted())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		// Dead actors can`t follow
		if(_actor.isDead())
		{
			clientActionFailed();
			return;
		}

		// do not follow yourself
		if(_actor == target)
		{
			clientActionFailed();
			return;
		}

		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		clientStopAutoAttack();

		// Set the Intention of this AbstractAI to AI_INTENTION_FOLLOW
		changeIntention(AI_INTENTION_FOLLOW, target, null);

		// Create and Launch an AI Follow Task to execute every 1s
		startFollow(target);
	}

	/**
	 * Manage the PickUp Intention : Set the pick up target and Launch a Move To Pawn Task (offset=20).<BR>
	 * <BR>
	 * <B><U> Actions</U> : </B><BR>
	 * <BR>
	 * <li>Set the AI pick up target</li> <li>Set the Intention of this AI to AI_INTENTION_PICK_UP</li> <li>Move the
	 * actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)</li><BR>
	 * <BR>
	 */

	@Override
	protected void onIntentionPickUp(L2Object object)
	{
		if(getIntention() == AI_INTENTION_REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		if(_actor.isAllSkillsDisabled())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		clientStopAutoAttack();

		// Set the Intention of this AbstractAI to AI_INTENTION_PICK_UP
		changeIntention(AI_INTENTION_PICK_UP, object, null);

		// Set the AI pick up target
		setTarget(object);
		if(object.getX() == 0 && object.getY() == 0) // TODO: Find the drop&spawn bug
		{
			_log.warning("Object in coords 0,0 - using a temporary fix");
			object.setXYZ(getActor().getX(), getActor().getY(), getActor().getZ() + 5);
		}

		// Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
		moveToPawn(object, 20);
	}

	/**
	 * Manage the Interact Intention : Set the interact target and Launch a Move To Pawn Task (offset=60).<BR>
	 * <BR>
	 * <B><U> Actions</U> : </B><BR>
	 * <BR>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li> <li>
	 * Set the AI interact target</li> <li>Set the Intention of this AI to AI_INTENTION_INTERACT</li> <li>Move the actor
	 * to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)</li><BR>
	 * <BR>
	 */

	@Override
	protected void onIntentionInteract(L2Object object)
	{
		if(getIntention() == AI_INTENTION_REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		if(_actor.isAllSkillsDisabled())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}

		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		clientStopAutoAttack();

		if(getIntention() != AI_INTENTION_INTERACT)
		{
			// Set the Intention of this AbstractAI to AI_INTENTION_INTERACT
			changeIntention(AI_INTENTION_INTERACT, object, null);

			// Set the AI interact target
			setTarget(object);

			// Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
			moveToPawn(object, 60);
		}
	}

	/**
	 * Do nothing.<BR>
	 * <BR>
	 */

	@Override
	protected void onEvtThink()
	{
	// do nothing
	}

	/**
	 * Do nothing.<BR>
	 * <BR>
	 */

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
	// do nothing
	}

	/**
	 * Launch actions corresponding to the Event Stunned then onAttacked Event.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li> <li>
	 * Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation
	 * (broadcast)</li> <li>Break an attack and send Server->Client ActionFailed packet and a System Message to the
	 * L2Character</li> <li>Break a cast and send Server->Client ActionFailed packet and a System Message to the
	 * L2Character</li> <li>Launch actions corresponding to the Event onAttacked (only for L2AttackableAI after the
	 * stunning periode)</li><BR>
	 * <BR>
	 */

	@Override
	protected void onEvtStunned(L2Character attacker)
	{
		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		_actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));

		if(AttackStanceTaskManager.getInstance().getAttackStanceTask(_actor))
		{
			AttackStanceTaskManager.getInstance().removeAttackStanceTask(_actor);
		}

		// Stop Server AutoAttack also
		setAutoAttacking(false);

		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);

		// Launch actions corresponding to the Event onAttacked (only for L2AttackableAI after the stunning periode)
		onEvtAttacked(attacker);
	}

	/**
	 * Launch actions corresponding to the Event Sleeping.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li> <li>
	 * Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation
	 * (broadcast)</li> <li>Break an attack and send Server->Client ActionFailed packet and a System Message to the
	 * L2Character</li> <li>Break a cast and send Server->Client ActionFailed packet and a System Message to the
	 * L2Character</li><BR>
	 * <BR>
	 */

	@Override
	protected void onEvtSleeping(L2Character attacker)
	{
		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		_actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));

		if(AttackStanceTaskManager.getInstance().getAttackStanceTask(_actor))
		{
			AttackStanceTaskManager.getInstance().removeAttackStanceTask(_actor);
		}

		// stop Server AutoAttack also
		setAutoAttacking(false);

		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);
	}

	/**
	 * Launch actions corresponding to the Event Rooted.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation
	 * (broadcast)</li> <li>Launch actions corresponding to the Event onAttacked</li><BR>
	 * <BR>
	 */

	@Override
	protected void onEvtRooted(L2Character attacker)
	{
		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		//_actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));
		//if (AttackStanceTaskManager.getInstance().getAttackStanceTask(_actor))
		//    AttackStanceTaskManager.getInstance().removeAttackStanceTask(_actor);

		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);

		// Launch actions corresponding to the Event onAttacked
		onEvtAttacked(attacker);

	}

	/**
	 * Launch actions corresponding to the Event Confused.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation
	 * (broadcast)</li> <li>Launch actions corresponding to the Event onAttacked</li><BR>
	 * <BR>
	 */

	@Override
	protected void onEvtConfused(L2Character attacker)
	{
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);

		// Launch actions corresponding to the Event onAttacked
		onEvtAttacked(attacker);
	}

	/**
	 * Launch actions corresponding to the Event Muted.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Break a cast and send Server->Client ActionFailed packet and a System Message to the L2Character</li><BR>
	 * <BR>
	 */

	@Override
	protected void onEvtMuted(L2Character attacker)
	{
		// Break a cast and send Server->Client ActionFailed packet and a System Message to the L2Character
		onEvtAttacked(attacker);
	}

	/**
	 * Launch actions corresponding to the Event ReadyToAct.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Launch actions corresponding to the Event Think</li><BR>
	 * <BR>
	 */

	@Override
	protected void onEvtReadyToAct()
	{
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}

	/**
	 * Do nothing.<BR>
	 * <BR>
	 */

	@Override
	protected void onEvtUserCmd(Object arg0, Object arg1)
	{
	// do nothing
	}

	/**
	 * Launch actions corresponding to the Event Arrived.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>If the Intention was AI_INTENTION_MOVE_TO, set the Intention to AI_INTENTION_ACTIVE</li> <li>Launch actions
	 * corresponding to the Event Think</li><BR>
	 * <BR>
	 */

	@Override
	protected void onEvtArrived()
	{
		// Launch an explore task if necessary
		if(_accessor.getActor() instanceof L2PcInstance)
		{
			((L2PcInstance) _accessor.getActor()).revalidateZone(true);
		}
		else
		{
			_accessor.getActor().revalidateZone();
		}

		if(_accessor.getActor().moveToNextRoutePoint())
			return;

		clientStoppedMoving();

		// If the Intention was AI_INTENTION_MOVE_TO, set the Intention to AI_INTENTION_ACTIVE
		if(getIntention() == AI_INTENTION_MOVE_TO)
		{
			setIntention(AI_INTENTION_ACTIVE);
		}

		// Launch actions corresponding to the Event Think
		onEvtThink();

		if(_actor instanceof L2BoatInstance)
		{
			((L2BoatInstance) _actor).evtArrived();
		}
	}

	/**
	 * Launch actions corresponding to the Event ArrivedRevalidate.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Launch actions corresponding to the Event Think</li><BR>
	 * <BR>
	 */

	@Override
	protected void onEvtArrivedRevalidate()
	{
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}

	/**
	 * Launch actions corresponding to the Event ArrivedBlocked.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation
	 * (broadcast)</li> <li>If the Intention was AI_INTENTION_MOVE_TO, set the Intention to AI_INTENTION_ACTIVE</li> <li>
	 * Launch actions corresponding to the Event Think</li><BR>
	 * <BR>
	 */

	@Override
	protected void onEvtArrivedBlocked(L2CharPosition blocked_at_pos)
	{
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(blocked_at_pos);

		// If the Intention was AI_INTENTION_MOVE_TO, tet the Intention to AI_INTENTION_ACTIVE
		if(getIntention() == AI_INTENTION_MOVE_TO)
		{
			setIntention(AI_INTENTION_ACTIVE);
		}

		// Launch actions corresponding to the Event Think
		onEvtThink();
	}

	/**
	 * Launch actions corresponding to the Event ForgetObject.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>If the object was targeted and the Intention was AI_INTENTION_INTERACT or AI_INTENTION_PICK_UP, set the
	 * Intention to AI_INTENTION_ACTIVE</li> <li>If the object was targeted to attack, stop the auto-attack, cancel
	 * target and set the Intention to AI_INTENTION_ACTIVE</li> <li>If the object was targeted to cast, cancel target
	 * and set the Intention to AI_INTENTION_ACTIVE</li> <li>If the object was targeted to follow, stop the movement,
	 * cancel AI Follow Task and set the Intention to AI_INTENTION_ACTIVE</li> <li>If the targeted object was the actor
	 * , cancel AI target, stop AI Follow Task, stop the movement and set the Intention to AI_INTENTION_IDLE</li><BR>
	 * <BR>
	 */

	@Override
	protected void onEvtForgetObject(L2Object object)
	{
		// If the object was targeted  and the Intention was AI_INTENTION_INTERACT or AI_INTENTION_PICK_UP, set the Intention to AI_INTENTION_ACTIVE
		if(getTarget() == object)
		{
			setTarget(null);

			if(getIntention() == AI_INTENTION_INTERACT)
			{
				setIntention(AI_INTENTION_ACTIVE);
			}
			else if(getIntention() == AI_INTENTION_PICK_UP)
			{
				setIntention(AI_INTENTION_ACTIVE);
			}
		}

		// Check if the object was targeted to attack
		if(getAttackTarget() == object)
		{
			// Cancel attack target
			setAttackTarget(null);

			// Set the Intention of this AbstractAI to AI_INTENTION_ACTIVE
			setIntention(AI_INTENTION_ACTIVE);
		}

		// Check if the object was targeted to cast
		if(getCastTarget() == object)
		{
			// Cancel cast target
			setCastTarget(null);

			// Set the Intention of this AbstractAI to AI_INTENTION_ACTIVE
			setIntention(AI_INTENTION_ACTIVE);
		}

		// Check if the object was targeted to follow
		if(getFollowTarget() == object)
		{
			// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
			clientStopMoving(null);

			// Stop an AI Follow Task
			stopFollow();

			// Set the Intention of this AbstractAI to AI_INTENTION_ACTIVE
			setIntention(AI_INTENTION_ACTIVE);
		}

		// Check if the targeted object was the actor
		if(_actor == object)
		{
			// Cancel AI target
			setTarget(null);
			setAttackTarget(null);
			setCastTarget(null);

			// Stop an AI Follow Task
			stopFollow();

			// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
			clientStopMoving(null);

			// Set the Intention of this AbstractAI to AI_INTENTION_IDLE
			changeIntention(AI_INTENTION_IDLE, null, null);
		}
	}

	/**
	 * Launch actions corresponding to the Event Cancel.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Stop an AI Follow Task</li> <li>Launch actions corresponding to the Event Think</li><BR>
	 * <BR>
	 */

	@Override
	protected void onEvtCancel()
	{
		// Stop an AI Follow Task
		stopFollow();

		if(!AttackStanceTaskManager.getInstance().getAttackStanceTask(_actor))
		{
			_actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));
		}

		// Launch actions corresponding to the Event Think
		onEvtThink();
	}

	/**
	 * Launch actions corresponding to the Event Dead.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Stop an AI Follow Task</li> <li>Kill the actor client side by sending Server->Client packet AutoAttackStop,
	 * StopMove/StopRotation, Die (broadcast)</li><BR>
	 * <BR>
	 */

	@Override
	protected void onEvtDead()
	{
		// Stop an AI Follow Task
		stopFollow();

		// Kill the actor client side by sending Server->Client packet AutoAttackStop, StopMove/StopRotation, Die (broadcast)
		clientNotifyDead();

		if(!(_actor instanceof L2PcInstance))
		{
			_actor.setWalking();
		}
	}

	/**
	 * Launch actions corresponding to the Event Fake Death.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Stop an AI Follow Task</li>
	 */

	@Override
	protected void onEvtFakeDeath()
	{
		// Stop an AI Follow Task
		stopFollow();

		// Stop the actor movement and send Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);

		// Init AI
		_intention = AI_INTENTION_IDLE;
		setTarget(null);
		setCastTarget(null);
		setAttackTarget(null);
	}

	/**
	 * Do nothing.<BR>
	 * <BR>
	 */

	@Override
	protected void onEvtFinishCasting()
	{
	// do nothing
	}

	/**
	 * Manage the Move to Pawn action in function of the distance and of the Interact area.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Get the distance between the current position of the L2Character and the target (x,y)</li> <li>If the
	 * distance > offset+20, move the actor (by running) to Pawn server side AND client side by sending Server->Client
	 * packet MoveToPawn (broadcast)</li> <li>If the distance <= offset+20, Stop the actor movement server side AND
	 * client side by sending Server->Client packet StopMove/StopRotation (broadcast)</li><BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>L2PLayerAI, L2SummonAI</li><BR>
	 * <BR>
	 * 
	 * @param target The targeted L2Object
	 * @param offset The Interact area radius
	 * @return True if a movement must be done
	 */
	protected boolean maybeMoveToPawn(L2Object target, int offset)
	{
		// Get the distance between the current position of the L2Character and the target (x,y)
		if(target == null)
		{
			_log.warning("maybeMoveToPawn: target == NULL!");
			return false;
		}

		// skill radius -1
		if(offset < 0)
			return false;

		offset += _actor.getTemplate().collisionRadius;

		if(target instanceof L2Character)
		{
			offset += ((L2Character) target).getTemplate().collisionRadius;
		}

		if(!_actor.isInsideRadius(target, offset, false, false))
		{
			// Caller should be L2Playable and thinkAttack/thinkCast/thinkInteract/thinkPickUp
			if(getFollowTarget() != null)
			{
				// prevent attack-follow into peace zones
				if(getAttackTarget() != null && _actor instanceof L2PlayableInstance && target instanceof L2PlayableInstance)
				{
					if(getAttackTarget() == getFollowTarget())
					{
						// allow GMs to keep following
						boolean isGM = _actor instanceof L2PcInstance ? ((L2PcInstance) _actor).isGM() : false;
						if(L2Character.isInsidePeaceZone(_actor, target) && !isGM)
						{
							stopFollow();
							setIntention(AI_INTENTION_IDLE);
							return true;
						}
					}
				}
				// if the target is too far (maybe also teleported)
				if(!_actor.isInsideRadius(target, 2000, false, false))
				{
					stopFollow();
					setIntention(AI_INTENTION_IDLE);
					return true;
				}
				// allow larger hit range when the target is moving (check is run only once per second)
				if(!_actor.isInsideRadius(target, offset + 100, false, false))
					return true;

				stopFollow();
				return false;
			}

			if(_actor.isMovementDisabled())
				return true;

			// If not running, set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
			if(!_actor.isRunning() && !(this instanceof L2PlayerAI))
			{
				_actor.setRunning();
			}

			stopFollow();

			if(target instanceof L2Character && !(target instanceof L2DoorInstance))
			{
				if(((L2Character) target).isMoving())
				{
					offset -= 100;
				}
				if(offset < 5)
				{
					offset = 5;
				}

				startFollow((L2Character) target, offset);
			}
			else
			{
				// Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
				moveToPawn(target, offset);
			}
			return true;
		}

		if(getFollowTarget() != null)
		{
			stopFollow();
		}

		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		// clientStopMoving(null);
		return false;
	}

	/**
	 * Modify current Intention and actions if the target is lost or dead.<BR>
	 * <BR>
	 * <B><U> Actions</U> : <I>If the target is lost or dead</I></B><BR>
	 * <BR>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li> <li>
	 * Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation
	 * (broadcast)</li> <li>Set the Intention of this AbstractAI to AI_INTENTION_ACTIVE</li><BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>L2PLayerAI, L2SummonAI</li><BR>
	 * <BR>
	 * 
	 * @param target The targeted L2Object
	 * @return True if the target is lost or dead (false if fakedeath)
	 */
	protected boolean checkTargetLostOrDead(L2Character target)
	{
		if(target == null || target.isAlikeDead())
		{
			//check if player is fakedeath
			if(target != null && target.isFakeDeath())
			{
				target.stopFakeDeath(null);
				return false;
			}

			// Set the Intention of this AbstractAI to AI_INTENTION_ACTIVE
			setIntention(AI_INTENTION_ACTIVE);

			return true;
		}
		return false;
	}

	/**
	 * Modify current Intention and actions if the target is lost.<BR>
	 * <BR>
	 * <B><U> Actions</U> : <I>If the target is lost</I></B><BR>
	 * <BR>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li> <li>
	 * Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation
	 * (broadcast)</li> <li>Set the Intention of this AbstractAI to AI_INTENTION_ACTIVE</li><BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>L2PLayerAI, L2SummonAI</li><BR>
	 * <BR>
	 * 
	 * @param target The targeted L2Object
	 * @return True if the target is lost
	 */
	protected boolean checkTargetLost(L2Object target)
	{
		// check if player is fakedeath
		if(target instanceof L2PcInstance)
		{
			L2PcInstance target2 = (L2PcInstance) target; //convert object to chara

			if(target2.isFakeDeath())
			{
				target2.stopFakeDeath(null);
				return false;
			}
			target2 = null;
		}
		if(target == null)
		{
			// Set the Intention of this AbstractAI to AI_INTENTION_ACTIVE
			setIntention(AI_INTENTION_ACTIVE);

			return true;
		}
		return false;
	}

	/**
	 * @see com.it.br.gameserver.ai.AbstractAI#onIntentionActive()
	 */

	@Override
	protected void onIntentionActive()
	{
		// Check if the Intention is not already Active
		if(getIntention() != AI_INTENTION_ACTIVE)
		{
			// Set the AI Intention to AI_INTENTION_ACTIVE
			changeIntention(AI_INTENTION_ACTIVE, null, null);

			// Init cast and attack target
			setCastTarget(null);
			setAttackTarget(null);

			// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
			clientStopMoving(null);

			// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
			clientStopAutoAttack();

			// Also enable random animations for this L2Character if allowed
			// This is only for mobs - town npcs are handled in their constructor
			if(_actor instanceof L2Attackable)
			{
				((L2NpcInstance) _actor).startRandomAnimationTimer();
			}

			// Launch the Think Event
			onEvtThink();
		}
	}

}
