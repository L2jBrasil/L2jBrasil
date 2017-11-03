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
package com.it.br.gameserver.handler.itemhandlers;

import com.it.br.gameserver.handler.IItemHandler;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PlayableInstance;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;

/**
 * This class ...
 *
 * @version $Revision: 1.2.4.4 $ $Date: 2005/03/27 15:30:07 $
 */

public class CrystalCarol implements IItemHandler
{
	private static final int[] ITEM_IDS = { 5562, 5563, 5564, 5565, 5566, 5583, 5584, 5585, 5586, 5587,
									 4411, 4412, 4413, 4414, 4415, 4416, 4417, 5010, 6903, 7061, 7062, 8555};


	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		L2PcInstance activeChar = (L2PcInstance)playable;
	    int itemId = item.getItemId();
		if (itemId == 5562) { //crystal_carol_01
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2140, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_carol_01");
		}
		else if (itemId == 5563) { //crystal_carol_02
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2141, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_carol_02");
		}
		else if (itemId == 5564) { //crystal_carol_03
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2142, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_carol_03");
		}
		else if (itemId == 5565) { //crystal_carol_04
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2143, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_carol_04");
		}
		else if (itemId == 5566) { //crystal_carol_05
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2144, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_carol_05");
		}
		else if (itemId == 5583) { //crystal_carol_06
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2145, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_carol_06");
		}
		else if (itemId == 5584) { //crystal_carol_07
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2146, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_carol_07");
		}
		else if (itemId == 5585) { //crystal_carol_08
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2147, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_carol_08");
		}
		else if (itemId == 5586) { //crystal_carol_09
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2148, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_carol_09");
		}
		else if (itemId == 5587) { //crystal_carol_10
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2149, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_carol_10");
		}
		else if (itemId == 4411) { //crystal_journey
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2069, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_journey");
		}
		else if (itemId == 4412) { //crystal_battle
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2068, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_battle");
		}
		else if (itemId == 4413) { //crystal_love
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2070, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_love");
		}
		else if (itemId == 4414) { //crystal_solitude
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2072, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_solitude");
		}
		else if (itemId == 4415) { //crystal_festival
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2071, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_festival");
		}
		else if (itemId == 4416) { //crystal_celebration
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2073, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_celebration");
		}
		else if (itemId == 4417) { //crystal_comedy
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2067, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_comedy");
		}
		else if (itemId == 5010) { //crystal_victory
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2066, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_victory");
		}
		else if (itemId == 6903) { //music_box_m
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2187, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"EtcSound.battle");
		}
		else if (itemId == 7061) { //crystal_birthday
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2073, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound2.crystal_celebration");
		}
		else if (itemId == 7062) { //crystal_wedding
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2230, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"SkillSound5.wedding");
		}
		else if (itemId == 8555) { //VVKorea
			MagicSkillUser MSU = new MagicSkillUser(playable, activeChar, 2272, 1, 1, 0);
			activeChar.broadcastPacket(MSU);
			//playCrystalSound(activeChar,"EtcSound.VVKorea");
		}
		activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
	}


	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
