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

package com.it.br.gameserver.handler.voicedcommandhandlers;
 
import com.it.br.Config;
import com.it.br.gameserver.handler.IVoicedCommandHandler;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.event.TvTEvent;

/**
 * @author Rayder
 **/
 
public class loc implements IVoicedCommandHandler
{
    private static final String[] VOICED_COMMANDS = {"giran", "aden", "oren", "dion", "goddard", "floran", "gludin", "gludio", "rune", "heine", "dwarvenvillage", "darkelvenvillage", "elvenvillage", "orcvillage", "talkingisland", "schuttgart", "huntersvillage", "antharas", "valakas", "baium", "queenant", "zaken", "orfen", "core"};


	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
    {  
       {
            if(activeChar.atEvent)
            {
            	activeChar.sendMessage("You can't teleport while you are in the event.");
            	return false;
            }
            else if (activeChar.isFestivalParticipant())
            {
            	activeChar.sendMessage("You can't use this command while participating in the Festival!");
            	return false;
            }
            else if(activeChar.isInJail())
            {
            	activeChar.sendMessage("You can't teleport while you are in jail.");
            	return false;
            }
            else if(activeChar.isDead())
            {
            	activeChar.sendMessage("You can't teleport while you are dead.");
            	return false;
            }
            else if(activeChar.isInCombat())
            {
            	activeChar.sendMessage("You can't teleport while you are in combat.");
            	return false;
            }
            else if (activeChar.isInDuel())
            {
            	activeChar.sendMessage("You can't teleport while you are doing a duel.");
            	return false;
            }
            else if (activeChar.isInOlympiadMode())
            {
            	activeChar.sendMessage("You can't teleport while you are in olympiad");
            	return false;
            }
            else if (activeChar.inObserverMode())
            {
            	activeChar.sendMessage("You can't teleport while you are in observer mode");
            	return false;
            }
            else if (Config.TVT_EVENT_ENABLED && TvTEvent.isStarted() && TvTEvent.isPlayerParticipant(activeChar.getObjectId())) 
            {
                activeChar.sendMessage("You can't teleport while you are in the TvTEvent.");
                return false;
            }
            else if (Config.ALLOW_LOC_VOICECOMMAND && !activeChar.inObserverMode() && !activeChar.isInOlympiadMode() && !activeChar.isInDuel() && !activeChar.isInCombat() && !activeChar.isDead() && !activeChar.isInJail()
            		&& !TvTEvent.isPlayerParticipant(activeChar.getObjectId()))
            {

            	if(command.startsWith("giran"))
            	{
            		activeChar.teleToLocation(82337, 148602, -3467);
            	}
            	else if(command.startsWith("dion"))
            	{
            		activeChar.teleToLocation(18492, 145386, -3118);
            	}
            	else if(command.startsWith("oren"))
            	{
            		activeChar.teleToLocation(82769, 53573, -1498);
            	}
            	else if(command.startsWith("gludio"))
            	{
            		activeChar.teleToLocation(-12864, 122716, -3117);
            	}
            	else if(command.startsWith("gludin"))
            	{
            		activeChar.teleToLocation(-80928, 150055, -3044);
            	}
            	else if(command.startsWith("aden"))
            	{
            		activeChar.teleToLocation(147361, 26953, -2205);
            	}
            	else if(command.startsWith("schuttgart"))
            	{
            		activeChar.teleToLocation(87359, -143224, -1293);
            	}
            	else if(command.startsWith("orcvillage"))
            	{
            		activeChar.teleToLocation(-44429, -113596, -220);
            	}
            	else if(command.startsWith("darkelvenvillage"))
            	{
            		activeChar.teleToLocation(11620, 16780, -4662);
            	}
            	else if(command.startsWith("elvenvillage"))
            	{
            		activeChar.teleToLocation(47050, 50767, -2996);
            	}
            	else if(command.startsWith("dwarvenvillage"))
            	{
            		activeChar.teleToLocation(115526, -178660, -945);
            	}
            	else if(command.startsWith("heine"))
            	{
            		activeChar.teleToLocation(111396, 219254, -3546);
            	}
            	else if(command.startsWith("huntersvillage"))
            	{
            		activeChar.teleToLocation(116440, 76320, -2730);
            	}
            	else if(command.startsWith("floran"))
            	{
            		activeChar.teleToLocation(17144, 170156, -3502);
            	}
            	else if(command.startsWith("goddard"))
            	{
            		activeChar.teleToLocation(147720, -55560, -2735);
            	}
            	else if(command.startsWith("rune"))
            	{
            		activeChar.teleToLocation(43848, -48033, -797);
            	}
            	else if(command.startsWith("valakas"))
            	{
            		activeChar.teleToLocation(213604, -114795, -1636);
            	}
            	else if(command.startsWith("baium"))
            	{
            		activeChar.teleToLocation(115213, 16623, 10080);
            	}
            	else if(command.startsWith("queenant"))
            	{
            		activeChar.teleToLocation(-21610, 181594, -5734);
            	}
            	else if(command.startsWith("zaken"))
            	{
            		activeChar.teleToLocation(55312, 219168, -3223);
            	}
            	else if(command.startsWith("orfen"))
            	{
            		activeChar.teleToLocation(43021, 17378, -4394);
            	}
            	else if(command.startsWith("core"))
            	{
            		activeChar.teleToLocation(17682, 111439, -6584);
            	}
            	else if(command.startsWith("antharas"))
            	{
            		activeChar.teleToLocation(185708, 114298, -8221);
            	}
            }
       }
       return true;
    }


	public String[] getVoicedCommandList()
    {
        return VOICED_COMMANDS;
    }
}