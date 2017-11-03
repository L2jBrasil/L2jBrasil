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
package com.it.br.gameserver.network.clientpackets;

import java.nio.BufferUnderflowException;

import com.it.br.Config;
import com.it.br.gameserver.TaskPriority;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.model.L2CharPosition;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.PartyMemberPosition;
import com.it.br.gameserver.templates.L2WeaponType;
import com.it.br.gameserver.util.IllegalPlayerAction;
import com.it.br.gameserver.util.Util;

public class MoveBackwardToLocation extends L2GameClientPacket
{
	//private static Logger _log = Logger.getLogger(MoveBackwardToLocation.class.getName());

	private int _targetX,_targetY,_targetZ;

	@SuppressWarnings("unused")
    private int _originX,_originY,_originZ;

	private int _moveMovement;

    //For geodata
    private int _curX,_curY;
    @SuppressWarnings("unused")
    private int _curZ;

	public TaskPriority getPriority() { return TaskPriority.PR_HIGH; }

	private static final String _C__01_MOVEBACKWARDTOLOC = "[C] 01 MoveBackwardToLoc";

	@Override
	protected void readImpl()
	{
		_targetX  = readD();
		_targetY  = readD();
		_targetZ  = readD();
		_originX  = readD();
		_originY  = readD();
		_originZ  = readD();
		try
		{
			_moveMovement = readD(); // is 0 if cursor keys are used  1 if mouse is used
		}
		catch (BufferUnderflowException e)
		{
			// ignore for now
            if(Config.L2WALKER_PROTECTION)   
            {   
            	L2PcInstance activeChar = getClient().getActiveChar();   
            	activeChar.sendPacket(SystemMessageId.HACKING_TOOL);   
            	Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " Tried to Use L2Walker And Got Kicked", IllegalPlayerAction.PUNISH_KICK);   
            }   
		 }
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		// don't do anything if player is Afraid
		if(activeChar.isAfraid())
		{
			getClient().sendPacket(new ActionFailed());
			return;
		}

		_curX = activeChar.getX();
		_curY = activeChar.getY();
		_curZ = activeChar.getZ();

		if(activeChar.isInBoat())
		{
			activeChar.setInBoat(false);
		}
		if (activeChar.getTeleMode() > 0)
		{
			if (activeChar.getTeleMode() == 1)
				activeChar.setTeleMode(0);
			activeChar.sendPacket(new ActionFailed());
			activeChar.teleToLocation(_targetX, _targetY, _targetZ, false);
			return;
		}
		if (_moveMovement == 0)
		{
			activeChar.sendPacket(new ActionFailed());
		}
		else if (_moveMovement == 0 && Config.GEODATA < 1) // cursor movement without geodata is disabled
		{
			activeChar.sendPacket(new ActionFailed());
		}
		else if (activeChar.isAttackingNow() && activeChar.getActiveWeaponItem() != null && (activeChar.getActiveWeaponItem().getItemType() == L2WeaponType.BOW))
		{
			activeChar.sendPacket(new ActionFailed());
		}
		else
		{
			double dx = _targetX-_curX;
			double dy = _targetY-_curY;
			// Can't move if character is confused, or trying to move a huge distance
			if (activeChar.isOutOfControl() || ((dx*dx+dy*dy) > 98010000)) // 9900*9900
			{
				activeChar.sendPacket(new ActionFailed());
				return;
			}
			activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO,
					new L2CharPosition(_targetX, _targetY, _targetZ, 0));

			if(activeChar.getParty() != null)
				activeChar.getParty().broadcastToPartyMembers(activeChar,new PartyMemberPosition(activeChar));
		}
	}

	@Override
	public String getType()
	{
		return _C__01_MOVEBACKWARDTOLOC;
	}
}