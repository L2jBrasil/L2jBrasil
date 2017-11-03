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
package com.it.br.gameserver.model;

import java.util.ArrayList;
import java.util.List;

import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.ai.L2ControllableMobAI;
import com.it.br.gameserver.datatables.sql.SpawnTable;
import com.it.br.gameserver.model.actor.instance.L2ControllableMobInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.util.Rnd;

/**
 * @author littlecrow
 *
 */
public final class MobGroup
{
    private L2NpcTemplate _npcTemplate;
    private int _groupId;
    private int _maxMobCount;

    private List<L2ControllableMobInstance> _mobs;

    public MobGroup(int groupId, L2NpcTemplate npcTemplate, int maxMobCount)
    {
        _groupId = groupId;
        _npcTemplate = npcTemplate;
        _maxMobCount = maxMobCount;
    }

    public int getActiveMobCount()
    {
        return getMobs().size();
    }

    public int getGroupId()
    {
        return _groupId;
    }

    public int getMaxMobCount()
    {
        return _maxMobCount;
    }

    public List<L2ControllableMobInstance> getMobs()
    {
        if (_mobs == null)
            _mobs = new ArrayList<>();

        return _mobs;
    }

    public String getStatus()
    {
        try {
            L2ControllableMobAI mobGroupAI = (L2ControllableMobAI)getMobs().get(0).getAI();

            switch (mobGroupAI.getAlternateAI())
            {
                case L2ControllableMobAI.AI_NORMAL:
                    return "Idle";
                case L2ControllableMobAI.AI_FORCEATTACK:
                    return "Force Attacking";
                case L2ControllableMobAI.AI_FOLLOW:
                    return "Following";
                case L2ControllableMobAI.AI_CAST:
                    return "Casting";
                case L2ControllableMobAI.AI_ATTACK_GROUP:
                    return "Attacking Group";
                default:
                    return "Idle";
            }
        }
        catch (Exception e) {
            return "Unspawned";
        }
    }

    public L2NpcTemplate getTemplate()
    {
        return _npcTemplate;
    }

    public boolean isGroupMember(L2ControllableMobInstance mobInst)
    {
        for (L2ControllableMobInstance groupMember : getMobs())
        {
        	if (groupMember == null) continue;

            if (groupMember.getObjectId() == mobInst.getObjectId())
                return true;
        }

        return false;
    }

    public void spawnGroup(int x, int y, int z)
    {
        if (getActiveMobCount() > 0) // can't spawn mob if already done
            return;

        try
        {
            for (int i = 0; i < getMaxMobCount(); i++)
            {
                L2GroupSpawn spawn = new L2GroupSpawn(getTemplate());

                int signX = (Rnd.nextInt(2) == 0) ? -1 : 1;
                int signY = (Rnd.nextInt(2) == 0) ? -1 : 1;
                int randX = Rnd.nextInt(MobGroupTable.RANDOM_RANGE);
                int randY = Rnd.nextInt(MobGroupTable.RANDOM_RANGE);

                spawn.setLocx(x + signX * randX);
                spawn.setLocy(y + signY * randY);
                spawn.setLocz(z);
                spawn.stopRespawn();

                SpawnTable.getInstance().addNewSpawn(spawn, false);
                getMobs().add((L2ControllableMobInstance)spawn.doGroupSpawn());
            }
        }
        catch (ClassNotFoundException e) {}
        catch (NoSuchMethodException e2) {}
    }

    public void spawnGroup(L2PcInstance activeChar)
    {
        spawnGroup(activeChar.getX(), activeChar.getY(), activeChar.getZ());
    }

    public void teleportGroup(L2PcInstance player)
    {
        removeDead();

        for (L2ControllableMobInstance mobInst : getMobs())
        {
        	if (mobInst == null) continue;

            if (!mobInst.isDead())
            {
                int x = player.getX() + Rnd.nextInt(50);
                int y = player.getY() + Rnd.nextInt(50);

                mobInst.teleToLocation(x, y, player.getZ(), true);
                L2ControllableMobAI ai = (L2ControllableMobAI)mobInst.getAI();
                ai.follow(player);
            }
        }
    }

    public L2ControllableMobInstance getRandomMob()
    {
        removeDead();

        if (getActiveMobCount() == 0)
            return null;

        int choice = Rnd.nextInt(getActiveMobCount());
        return getMobs().get(choice);
    }

    public void unspawnGroup()
    {
        removeDead();

        if (getActiveMobCount() == 0)
            return;

        for (L2ControllableMobInstance mobInst : getMobs())
        {
        	if (mobInst == null) continue;

            if (!mobInst.isDead())
                mobInst.deleteMe();

            SpawnTable.getInstance().deleteSpawn(mobInst.getSpawn(), false);
        }

        getMobs().clear();
    }

    public void killGroup(L2PcInstance activeChar)
    {
        removeDead();

        for (L2ControllableMobInstance mobInst : getMobs())
        {
        	if (mobInst == null) continue;

            if (!mobInst.isDead())
                mobInst.reduceCurrentHp(mobInst.getMaxHp() + 1, activeChar);

            SpawnTable.getInstance().deleteSpawn(mobInst.getSpawn(), false);
        }

        getMobs().clear();
    }

    public void setAttackRandom()
    {
        removeDead();

        for (L2ControllableMobInstance mobInst : getMobs())
        {
        	if (mobInst == null) continue;

            L2ControllableMobAI ai = (L2ControllableMobAI)mobInst.getAI();
            ai.setAlternateAI(L2ControllableMobAI.AI_NORMAL);
            ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }

    public void setAttackTarget(L2Character target)
    {
        removeDead();

        for (L2ControllableMobInstance mobInst : getMobs())
        {
        	if (mobInst == null) continue;

            L2ControllableMobAI ai = (L2ControllableMobAI)mobInst.getAI();
            ai.forceAttack(target);
        }
    }

    public void setIdleMode()
    {
        removeDead();

        for (L2ControllableMobInstance mobInst : getMobs())
        {
        	if (mobInst == null) continue;

            L2ControllableMobAI ai = (L2ControllableMobAI)mobInst.getAI();
            ai.stop();
        }
    }

    public void returnGroup(L2Character activeChar)
    {
        setIdleMode();

        for (L2ControllableMobInstance mobInst : getMobs())
        {
        	if (mobInst == null) continue;

            int signX = (Rnd.nextInt(2) == 0) ? -1 : 1;
            int signY = (Rnd.nextInt(2) == 0) ? -1 : 1;
            int randX = Rnd.nextInt(MobGroupTable.RANDOM_RANGE);
            int randY = Rnd.nextInt(MobGroupTable.RANDOM_RANGE);

            L2ControllableMobAI ai = (L2ControllableMobAI)mobInst.getAI();
            ai.move(activeChar.getX() + signX * randX, activeChar.getY() + signY * randY, activeChar.getZ());
        }
    }

    public void setFollowMode(L2Character character)
    {
        removeDead();

        for (L2ControllableMobInstance mobInst : getMobs())
        {
        	if (mobInst == null) continue;

            L2ControllableMobAI ai = (L2ControllableMobAI)mobInst.getAI();
            ai.follow(character);
        }
    }

    public void setCastMode()
    {
        removeDead();

        for (L2ControllableMobInstance mobInst : getMobs())
        {
        	if (mobInst == null) continue;

            L2ControllableMobAI ai = (L2ControllableMobAI)mobInst.getAI();
            ai.setAlternateAI(L2ControllableMobAI.AI_CAST);
        }
    }

    public void setNoMoveMode(boolean enabled)
    {
        removeDead();

        for (L2ControllableMobInstance mobInst : getMobs())
        {
        	if (mobInst == null) continue;

            L2ControllableMobAI ai = (L2ControllableMobAI)mobInst.getAI();
            ai.setNotMoving(enabled);
        }
    }

    protected void removeDead()
    {
        List<L2ControllableMobInstance> deadMobs = new ArrayList<>();

        for (L2ControllableMobInstance mobInst : getMobs())
            if (mobInst != null && mobInst.isDead())
                deadMobs.add(mobInst);

        getMobs().removeAll(deadMobs);
    }

    public void setInvul(boolean invulState)
    {
        removeDead();

        for (L2ControllableMobInstance mobInst : getMobs())
        	if (mobInst != null)
        		mobInst.setInvul(invulState);
    }

    public void setAttackGroup(MobGroup otherGrp)
    {
        removeDead();

        for (L2ControllableMobInstance mobInst : getMobs())
        {
        	if (mobInst == null) continue;

            L2ControllableMobAI ai = (L2ControllableMobAI)mobInst.getAI();
            ai.forceAttackGroup(otherGrp);
            ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }
}