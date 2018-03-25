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
package com.it.br.gameserver.instancemanager;

import com.it.br.gameserver.database.dao.CastleDao;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Castle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SiegeGuardManager 
{

	private static Logger _log = LoggerFactory.getLogger(SiegeGuardManager.class);

    // =========================================================
    // Data Field
    private Castle _castle;
    private List<L2Spawn> _siegeGuardSpawn  = new ArrayList<>();

    // =========================================================
    // Constructor
    public SiegeGuardManager(Castle castle)
    {
        _castle = castle;
    }

    // =========================================================
    // Method - Public
    /**
     * Add guard.<BR><BR>
     */
    public void addSiegeGuard(L2PcInstance activeChar, int npcId)
    {
        if (activeChar == null) return;
        addSiegeGuard(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getHeading(), npcId);
    }

    /**
     * Add guard.<BR><BR>
     */
    public void addSiegeGuard(int x, int y, int z, int heading, int npcId)
    {
        CastleDao.saveSiegeGuard(getCastle(), x, y, z, heading, npcId, 0);
    }

    /**
     * Hire merc.<BR><BR>
     */
    public void hireMerc(L2PcInstance activeChar, int npcId)
    {
        if (activeChar == null) return;
        hireMerc(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getHeading(), npcId);
    }

    /**
     * Hire merc.<BR><BR>
     */
    public void hireMerc(int x, int y, int z, int heading, int npcId)
    {
        CastleDao.saveSiegeGuard(getCastle(), x, y, z, heading, npcId, 0);
    }

    /**
     * Spawn guards.<BR><BR>
     * Added cheat proof check by Bian. Some players found a way to spawn more than the allowed 
     * amount of siege guards. This check for max hired guards should stop this. 
     */
    public void spawnSiegeGuard()
    {
        try 
        { 
            int     hiredCount  = 0,  
                    hiredMax    = MercTicketManager.getInstance().getMaxAllowedMerc(_castle.getCastleId());  
            boolean isHired     = (getCastle().getOwnerId() > 0) ? true : false; 
            {

                List<L2Spawn> spawns = CastleDao.loadSiegeGuard(getCastle());
                _siegeGuardSpawn.addAll(spawns);
                for (L2Spawn spawn: getSiegeGuardSpawn())
                { 
                    if (spawn != null)  
                        { 
                            spawn.init(); 
                            if (isHired) 
                            { 
                                hiredCount++; 
                                if (hiredCount > hiredMax) 
                                    return; 
                            } 
                        } 
                } 
            } 
        } 
        catch (Throwable t) 
        { 
            _log.warn("Error spawning siege guards for castle " + getCastle().getName() + ":" + t.toString());}
    }

    /**
     * Unspawn guards.<BR><BR>
     */
    public void unspawnSiegeGuard()
    {
        for (L2Spawn spawn: getSiegeGuardSpawn())
        {
            if (spawn == null)
                continue;

            spawn.stopRespawn();
            spawn.getLastSpawn().doDie(spawn.getLastSpawn());
        }

        getSiegeGuardSpawn().clear();
    }

    public final Castle getCastle()
    {
        return _castle;
    }

    public final List<L2Spawn> getSiegeGuardSpawn()
    {
        return _siegeGuardSpawn;
    }
}
