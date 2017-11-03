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

import com.it.br.gameserver.model.L2Character;

/**
 * Interface of AI and client state. To correctly send messages to client we need it's state. For example, if we've sent
 * 'StartAutoAttack' message, we need to send 'StopAutoAttack' message before any other action. Or if we've sent
 * 'MoveToPawn', we need to send 'StopMove' when the movement of a character is canceled (by Root spell or any other
 * reason). Thus, we need to know the state of client, i.e. which messages we've sent and how the client will show the
 * scene. Close to this task is the task of AI. If a player's character is attacking a mob, his ATTACK may be iterrupted
 * by an event, that temporary disable attacking. But when the possibility to ATTACK will be enabled, the character must
 * continue the ATTACK. For mobs it may be more complex, since we want them to decide when to use magic, or when to
 * follow the player for physical combat, or when to escape, to help another mob, etc. This interface is hiding
 * complexity of server<->client interaction and multiple states of a character. It allows to set a desired, simple
 * "wish" of a character, and the implementation of this interface will take care about the rest. The goal of a
 * character may be like "ATTACK", "random walk" and so on. To reach the goal inplementation will split it into several
 * small actions, several steps (possibly repeatable). Like "run to target" then "hit it", then if target is not dead -
 * repeat. This flow of simplier steps may be interrupted by incoming events. Like a character's movement was disabled
 * (by Root spell, for instance). Depending on character's ability AI may choose to wait, or to use magic ATTACK and so
 * on. Additionally incoming events are compared with client's state of the character, and required network messages are
 * sent to client's, i.e. if we have incoming event that character's movement was disabled, it causes changing if its
 * behavour, and if client's state for the character is "moving" we send messages to clients to stop the avatar/mob.
 */
public interface Ctrl
{
	/** the character this AI serves */
	L2Character getActor();

	/** get current intention */
	CtrlIntention getIntention();

	/** get current ATTACK target */
	L2Character getAttackTarget();

	/** Set general state/intention for AI, with optional data */
	void setIntention(CtrlIntention intention);

	void setIntention(CtrlIntention intention, Object arg0);

	void setIntention(CtrlIntention intention, Object arg0, Object arg1);

	/**
	 * Event, that notifies about previous step result, or user command, that does not change current general intention
	 */
	void notifyEvent(CtrlEvent evt);

	void notifyEvent(CtrlEvent evt, Object arg0);

	void notifyEvent(CtrlEvent evt, Object arg0, Object arg1);
}
