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
package com.it.br.gameserver.handler.skillhandlers;

import com.it.br.Config;
import com.it.br.gameserver.handler.ISkillHandler;
import com.it.br.gameserver.instancemanager.FishingZoneManager;
import com.it.br.gameserver.model.Inventory;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Object;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Skill.SkillType;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.zone.type.L2FishingZone;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.templates.L2Weapon;
import com.it.br.gameserver.templates.L2WeaponType;
import com.it.br.gameserver.util.Util;
import com.it.br.util.Rnd;

public class Fishing implements ISkillHandler
{
    //private static Logger _log = Logger.getLogger(SiegeFlag.class.getName());
	//protected SkillType[] _skillIds = {SkillType.FISHING};
	private static final SkillType[] SKILL_IDS = {SkillType.FISHING};


	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
    {
        if (activeChar == null || !(activeChar instanceof L2PcInstance)) return;

        L2PcInstance player = (L2PcInstance)activeChar;

        /* If fishing is disabled, there isn't much point in doing anything else, unless you are GM.
         * so this got moved up here, before anything else.
         */
		if (!Config.ALLOWFISHING && !player.isGM())
		{
			player.sendMessage("Not Working Yet");
			return;
		}

		/* If fishing is enabled, here is the code that was striped from startFishing() in L2PcInstance.
		 * Decide now where will the hook be cast...*/
        int rnd = Rnd.get(200) + 200;
        double angle = Util.convertHeadingToDegree(player.getHeading());
        //this.sendMessage("Angel: "+angle+" Heading: "+getHeading());
        double radian = Math.toRadians(angle - 90);
        double sin = Math.sin(radian);
        double cos = Math.cos(radian);
        int x1 = -(int)(sin * rnd); //Somthing wrong with L2j Heding calculation o_0?
        int y1 = (int)(cos * rnd); //Somthing wrong with L2j Heding calculation o_0?
        int x = player.getX()+x1;
        int y = player.getY()+y1;
        int z = player.getZ()-30;

        /* ...and if the spot is in a fishing zone. If it is, it will then position the hook on the water
         * surface. If not, you have to be GM to proceed past here... in that case, the hook will be
         * positioned using the old Z lookup method.
         */
        L2FishingZone aimingTo = FishingZoneManager.getInstance().isInsideFishingZone(x, y, z);
		if (aimingTo != null)
		{
	        z = aimingTo.getWaterZ();
			//player.sendMessage("Hook x,y: " + x + "," + y + " - Water Z, Player Z:" + z + ", " + player.getZ()); //debug line, shows hook landing related coordinates. Uncoment if needed.
		}
		else
		{
            //You can't fish here
			player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_FISH_HERE));
			if (!player.isGM())
			{
				return;
			}
		}

		if (player.isFishing())
		{
			if (player.GetFishCombat() != null) player.GetFishCombat().doDie(false);
			else player.EndFishing(false);
			//Cancels fishing
			player.sendPacket(new SystemMessage(SystemMessageId.FISHING_ATTEMPT_CANCELLED));
			return;
		}
        if (player.isInBoat())
		{
			//You can't fish while you are on boat
        	player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_FISH_ON_BOAT));
			if (!player.isGM())
				return;
		}

        /* Of course since you can define fishing water volumes of any height, the function needs to be
         * changed to cope with that. Still, this is assuming that fishing zones water surfaces, are
         * always above "sea level".
         */
		if (player.getZ() <= -3800 || player.getZ() < (z - 32))
		{
            //You can't fish in water
			player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_FISH_UNDER_WATER));
			if (!player.isGM())
				return;
		}
		if (player.isInCraftMode() || player.isInStoreMode()) {
			player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_FISH_WHILE_USING_RECIPE_BOOK));
			if (!player.isGM())
				return;
		}
		L2Weapon weaponItem = player.getActiveWeaponItem();
		if ((weaponItem==null || weaponItem.getItemType() != L2WeaponType.ROD))
		{
			//Fishing poles are not installed
			player.sendPacket(new SystemMessage(SystemMessageId.FISHING_POLE_NOT_EQUIPPED));
			return;
		}
		L2ItemInstance lure = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if (lure == null)
		{
		    //Bait not equiped.
			player.sendPacket(new SystemMessage(SystemMessageId.BAIT_ON_HOOK_BEFORE_FISHING));
            return;
		}
		player.SetLure(lure);
		L2ItemInstance lure2 = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);

		if (lure2 == null || lure2.getCount() < 1) //Not enough bait.
		{
		    player.sendPacket(new SystemMessage(SystemMessageId.NOT_ENOUGH_BAIT));
			player.sendPacket(new ItemList(player,false));
		}
		else //Has enough bait, consume 1 and update inventory. Start fishing follows.
		{
			lure2 = player.getInventory().destroyItem("Consume", player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1, player, null);
			InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(lure2);
			player.sendPacket(iu);
		}

		// If everything else checks out, actually cast the hook and start fishing... :P
		player.startFishing(x, y, z);

    }


	public SkillType[] getSkillIds()
    {
        return SKILL_IDS;
    }

}
