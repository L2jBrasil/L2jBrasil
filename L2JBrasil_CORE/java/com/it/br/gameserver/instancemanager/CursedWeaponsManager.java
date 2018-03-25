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

import com.it.br.Config;
import com.it.br.configuration.Configurator;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.actor.instance.*;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CursedWeaponsManager
{
	private static final Logger _log = LoggerFactory.getLogger(CursedWeaponsManager.class);

	private static CursedWeaponsManager _instance;

	public static final CursedWeaponsManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new CursedWeaponsManager();
		}
		return _instance;
	}

	private Map<Integer, CursedWeapon> _cursedWeapons;

	public CursedWeaponsManager()
	{
		_cursedWeapons = new HashMap<>();

		if (!Config.ALLOW_CURSED_WEAPONS)
			return;

		load();
		restore();
		controlPlayers();
		_log.info("Loaded : "+_cursedWeapons.size() + " cursed weapon(s).");
	}

	public final void reload()
	{
		_instance = new CursedWeaponsManager();
	}
	private final void load()
	{
		if (Config.DEBUG)
    		System.out.print("Parsing ...");
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            
            ServerSettings serverSettings = Configurator.getSettings(ServerSettings.class);
            
            File file = new File(serverSettings.getDatapackDirectory() +"/data/xml/cursedWeapons.xml");
            if (!file.exists())
            {
        		if (Config.DEBUG)
            		System.out.println("NO FILE");
            	return;
            }

            Document doc = factory.newDocumentBuilder().parse(file);

            for (Node n=doc.getFirstChild(); n != null; n = n.getNextSibling())
            {
                if ("list".equalsIgnoreCase(n.getNodeName()))
                {
                    for (Node d=n.getFirstChild(); d != null; d = d.getNextSibling())
                    {
                        if ("item".equalsIgnoreCase(d.getNodeName()))
                        {
                    		NamedNodeMap attrs = d.getAttributes();
                        	int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                        	int skillId = Integer.parseInt(attrs.getNamedItem("skillId").getNodeValue());
                        	String name = attrs.getNamedItem("name").getNodeValue();

                        	CursedWeapon cw = new CursedWeapon(id, skillId, name);

                        	int val;
                            for (Node cd=d.getFirstChild(); cd != null; cd = cd.getNextSibling())
                            {
                                if ("dropRate".equalsIgnoreCase(cd.getNodeName()))
                                {
                            		attrs = cd.getAttributes();
                            		val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                            		cw.setDropRate(val);
                                }
                                else if ("duration".equalsIgnoreCase(cd.getNodeName()))
                                {
                            		attrs = cd.getAttributes();
                            		val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                            		cw.setDuration(val);
                                }
                                else if ("durationLost".equalsIgnoreCase(cd.getNodeName()))
                                {
                            		attrs = cd.getAttributes();
                            		val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                            		cw.setDurationLost(val);
                                }
                                else if ("disapearChance".equalsIgnoreCase(cd.getNodeName()))
                                {
                            		attrs = cd.getAttributes();
                            		val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                            		cw.setDisapearChance(val);
                                }
                                else if ("stageKills".equalsIgnoreCase(cd.getNodeName()))
                                {
                            		attrs = cd.getAttributes();
                            		val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                            		cw.setStageKills(val);
                                }
                            }
                            _cursedWeapons.put(id, cw);
                        }
                    }
                }
            }

        	if (Config.DEBUG)
        		System.out.println("OK");
        }
        catch (Exception e)
        {
            _log.error( "Error parsing cursed weapons file.", e);

            if (Config.DEBUG)
        		System.out.println("ERROR");
            return ;
        }
	}

	private final void restore()
	{
    	if (Config.DEBUG)
    		System.out.print("  Restoring ... ");

		Connection con = null;
		try
		{
			// Retrieve the L2PcInstance from the characters table of the database
			con = L2DatabaseFactory.getInstance().getConnection();

			PreparedStatement statement = con.prepareStatement("SELECT itemId, playerId, playerKarma, playerPkKills, nbKills, endTime FROM cursed_weapons");
			ResultSet rset = statement.executeQuery();

			if (rset.next())
			{
				int itemId        = rset.getInt("itemId");
				int playerId      = rset.getInt("playerId");
				int playerKarma   = rset.getInt("playerKarma");
				int playerPkKills = rset.getInt("playerPkKills");
				int nbKills       = rset.getInt("nbKills");
				long endTime      = rset.getLong("endTime");

				CursedWeapon cw = _cursedWeapons.get(itemId);
				cw.setPlayerId(playerId);
				cw.setPlayerKarma(playerKarma);
				cw.setPlayerPkKills(playerPkKills);
				cw.setNbKills(nbKills);
				cw.setEndTime(endTime);
				cw.reActivate();
			}

			rset.close();
			statement.close();

	    	if (Config.DEBUG)
	    		System.out.println("OK");
		}
		catch (Exception e)
		{
			_log.warn("Could not restore CursedWeapons data: " + e);

	    	if (Config.DEBUG)
	    		System.out.println("ERROR");
			return;
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
		}
	}

	private final void controlPlayers()
	{
    	if (Config.DEBUG)
    		System.out.print("Checking players ...");

		Connection con = null;
		try
		{
			// Retrieve the L2PcInstance from the characters table of the database
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = null;
			ResultSet rset = null;

			// TODO: See comments below...
			// This entire for loop should NOT be necessary, since it is already handled by
			// CursedWeapon.endOfLife().  However, if we indeed *need* to duplicate it for safety,
			// then we'd better make sure that it FULLY cleans up inactive cursed weapons!
			// Undesired effects result otherwise, such as player with no zariche but with karma
			// or a lost-child entry in the cursedweapons table, without a corresponding one in items...
			for (CursedWeapon cw : _cursedWeapons.values())
			{
				if (cw.isActivated()) continue;

				// Do an item check to be sure that the cursed weapon isn't hold by someone
				int itemId = cw.getItemId();
				try
				{
					statement = con.prepareStatement("SELECT owner_id FROM items WHERE item_id=?");
					statement.setInt(1, itemId);
					rset = statement.executeQuery();

					if (rset.next())
					{
						// A player has the cursed weapon in his inventory ...
						int playerId = rset.getInt("owner_id");
						_log.info("PROBLEM : Player "+playerId+" owns the cursed weapon "+itemId+" but he shouldn't.");

						// Delete the item
						statement = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?");
						statement.setInt(1, playerId);
						statement.setInt(2, itemId);
						if (statement.executeUpdate() != 1)
						{
							_log.warn("Error while deleting cursed weapon "+itemId+" from userId "+playerId);
						}
						statement.close();

						// Restore the player's old karma and pk count
		    			statement = con.prepareStatement("UPDATE characters SET karma=?, pkkills=? WHERE obj_id=?");
		    			statement.setInt(1, cw.getPlayerKarma());
		    			statement.setInt(2, cw.getPlayerPkKills());
		    			statement.setInt(3, playerId);
		    			if (statement.executeUpdate() != 1)
		    			{
		    				_log.warn("Error while updating karma & pkkills for userId "+cw.getPlayerId());
		    			}

		    			removeFromDb(itemId);
					}
					rset.close();
					statement.close();
				} catch (SQLException sqlE)
				{}
				// close the statement to avoid multiply prepared statement errors in following iterations.
    			try { con.close(); } catch (Exception e) {}
			}
		}
		catch (Exception e)
		{
			_log.warn("Could not check CursedWeapons data: " + e);

	    	if (Config.DEBUG)
	    		System.out.println("ERROR");
			return;
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
		}

    	if (Config.DEBUG)
    		System.out.println("DONE");
	}

	public synchronized void checkDrop(L2Attackable attackable, L2PcInstance player)
	{
		if (attackable instanceof L2SiegeGuardInstance
			|| attackable instanceof L2RiftInvaderInstance
			|| attackable instanceof L2FestivalMonsterInstance
			|| attackable instanceof L2GrandBossInstance   
            || attackable instanceof L2GuardInstance  
            || attackable instanceof L2FeedableBeastInstance)
			return;

		if (player.isCursedWeaponEquipped())
			return;

		for (CursedWeapon cw : _cursedWeapons.values())
		{
			if (cw.isActive()) continue;

			if (cw.checkDrop(attackable, player)) break;
		}
	}

	public void activate(L2PcInstance player, L2ItemInstance item)
	{
		CursedWeapon cw = _cursedWeapons.get(item.getItemId());
		if (player.isCursedWeaponEquipped()) // cannot own 2 cursed swords
		{
			CursedWeapon cw2 = _cursedWeapons.get(player.getCursedWeaponEquipedId());
			/* TODO: give the bonus level in a more appropriate manner.
			 *  The following code adds "_stageKills" levels.  This will also show in the char status.
			 * I do not have enough info to know if the bonus should be shown in the pk count, or if it
			 * should be a full "_stageKills" bonus or just the remaining from the current count till the
			 * of the current stage...
			 * This code is a TEMP fix, so that the cursed weapon's bonus level can be observed with as
			 * little change in the code as possible, until proper info arises.
			 */
			cw2.setNbKills(cw2.getStageKills()-1);
			cw2.increaseKills();

			// erase the newly obtained cursed weapon
			cw.setPlayer(player);  // NECESSARY in order to find which inventory the weapon is in!
			cw.endOfLife();        // expire the weapon and clean up.
		}
		else cw.activate(player, item);
	}

	public void drop(int itemId, L2Character killer)
	{
		CursedWeapon cw = _cursedWeapons.get(itemId);

		cw.dropIt(killer);
	}

	public void increaseKills(int itemId)
	{
		CursedWeapon cw = _cursedWeapons.get(itemId);

		cw.increaseKills();
	}

	public int getLevel(int itemId)
	{
		CursedWeapon cw = _cursedWeapons.get(itemId);

		return cw.getLevel();
	}


	public static void announce(SystemMessage sm)
	{
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			if (player == null) continue;

			player.sendPacket(sm);
		}
		if (Config.DEBUG)
			_log.info("MessageID: "+sm.getMessageID());
	}

	public void checkPlayer(L2PcInstance player)
	{
		if (player == null)
			return;

		for (CursedWeapon cw : _cursedWeapons.values())
		{
			if (cw.isActivated() && player.getObjectId() == cw.getPlayerId())
			{
				cw.setPlayer(player);
				cw.setItem(player.getInventory().getItemByItemId(cw.getItemId()));
				cw.giveSkill();
				player.setCursedWeaponEquipedId(cw.getItemId());

				SystemMessage sm = new SystemMessage(SystemMessageId.S2_MINUTE_OF_USAGE_TIME_ARE_LEFT_FOR_S1);
				sm.addString(cw.getName());
				//sm.addItemName(cw.getItemId());
				sm.addNumber((int)((cw.getEndTime() - System.currentTimeMillis()) / 60000));
				player.sendPacket(sm);
			}
		}
	}

    public static void removeFromDb(int itemId)
    {
    	Connection con = null;
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();

        	// Delete datas
        	PreparedStatement statement = con.prepareStatement("DELETE FROM cursed_weapons WHERE itemId = ?");
            statement.setInt(1, itemId);
            statement.executeUpdate();

            statement.close();
        	con.close();
        }
        catch (SQLException e)
        {
        	_log.error("CursedWeaponsManager: Failed to remove data: " + e);
        }
        finally
        {
        	try { con.close(); } catch (Exception e) {}
        }
    }

    public void saveData()
    {
		for (CursedWeapon cw : _cursedWeapons.values())
		{
			cw.saveData();
		}
    }


	// =========================================================
    public boolean isCursed(int itemId)
    {
		return _cursedWeapons.containsKey(itemId);
    }

    public Collection<CursedWeapon> getCursedWeapons()
    {
    	return _cursedWeapons.values();
    }

    public Set<Integer> getCursedWeaponsIds()
    {
    	return _cursedWeapons.keySet();
    }

    public CursedWeapon getCursedWeapon(int itemId)
    {
    	return _cursedWeapons.get(itemId);
    }

    public void givePassive(int itemId)
    {
    	try { _cursedWeapons.get(itemId).giveSkill(); } catch (Exception e) {/***/}
    }
}
