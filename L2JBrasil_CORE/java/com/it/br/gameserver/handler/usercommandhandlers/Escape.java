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
package com.it.br.gameserver.handler.usercommandhandlers;

import com.it.br.Config;
import com.it.br.gameserver.GameTimeController;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.datatables.xml.MapRegionTable;
import com.it.br.gameserver.handler.IUserCommandHandler;
import com.it.br.gameserver.instancemanager.GrandBossManager;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;
import com.it.br.gameserver.network.serverpackets.SetupGauge;
import com.it.br.gameserver.util.Broadcast;

public class Escape implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS = { 52 };
    private static final int REQUIRED_LEVEL = Config.GM_ESCAPE;

    /* (non-Javadoc)
     * @see com.it.br.gameserver.handler.IUserCommandHandler#useUserCommand(int, com.it.br.gameserver.model.L2PcInstance)
     */

	public boolean useUserCommand(int id, L2PcInstance activeChar)
    {
    	// Thanks nbd
    	if (!TvTEvent.onEscapeUse(activeChar.getObjectId()))
    	{
    		activeChar.sendPacket(new ActionFailed());
    		return false;
    	}

        if (activeChar.isCastingNow() || activeChar.isMovementDisabled() || activeChar.isMuted() || activeChar.isAlikeDead() ||
                activeChar.isInOlympiadMode() || activeChar.inObserverMode() || activeChar.isRiding() || activeChar.isDead() ||
 activeChar.isFestivalParticipant() || activeChar.isEnchanting() || activeChar.isTeleporting() || activeChar.isAfraid() || activeChar.isInJail()) 
            return false;

        int unstuckTimer = (activeChar.getAccessLevel() >=REQUIRED_LEVEL? 5000 : Config.UNSTUCK_INTERVAL*1000 );
   
        if (GrandBossManager.getInstance().getZone(activeChar) != null && !activeChar.isGM()) 
        { 
            activeChar.sendMessage("You may not use an escape command in a Boss Zone.");
            return false;
        }

        activeChar.sendMessage("You are stuck. You will be transported to the nearest village in " + Config.UNSTUCK_INTERVAL);

        if (activeChar.getAccessLevel() >= Config.GM_ESCAPE)
        {
            activeChar.sendMessage("You use Fast Escape: 5 seconds.");
        }
        else if(Config.UNSTUCK_INTERVAL > 100)
        {
            activeChar.sendMessage("You use Escape: " + unstuckTimer/60000 + " minutes.");
        }
        else
            activeChar.sendMessage("You use Escape: " + unstuckTimer/1000 + " seconds.");

        activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        //SoE Animation section
        activeChar.setTarget(activeChar);
        activeChar.disableAllSkills();

        MagicSkillUser msk = new MagicSkillUser(activeChar, 1050, 1, unstuckTimer, 0);
        Broadcast.toSelfAndKnownPlayersInRadius(activeChar, msk, 810000);
        SetupGauge sg = new SetupGauge(0, unstuckTimer);
        activeChar.sendPacket(sg);
        //End SoE Animation section

        EscapeFinalizer ef = new EscapeFinalizer(activeChar);
        // continue execution later
        activeChar.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(ef, unstuckTimer));
        activeChar.setSkillCastEndTime(10+GameTimeController.getGameTicks()+unstuckTimer/GameTimeController.MILLIS_IN_TICK);

        return true;
    }

    static class EscapeFinalizer implements Runnable
    {
        private L2PcInstance _activeChar;

        EscapeFinalizer(L2PcInstance activeChar)
        {
            _activeChar = activeChar;
        }


		public void run()
        {
            if (_activeChar.isDead())
                return;

            _activeChar.setIsIn7sDungeon(false);

            _activeChar.enableAllSkills();

            try
            {
                _activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
            } catch (Throwable e) { if (Config.DEBUG) e.printStackTrace(); }
        }
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.handler.IUserCommandHandler#getUserCommandList()
     */

	public int[] getUserCommandList()
    {
        return COMMAND_IDS;
    }
}
