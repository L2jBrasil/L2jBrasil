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
package com.it.br.gameserver.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.LoginServerThread;
import com.it.br.gameserver.lib.Log;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2ManufactureItem;
import com.it.br.gameserver.model.L2ManufactureList;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.TradeList.TradeItem;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.L2GameClient;
import com.it.br.gameserver.network.L2GameClient.GameClientState;

public class OfflineTradeTable
{
	private static Logger _log = Logger.getLogger(OfflineTradeTable.class.getName());

	//SQL DEFINITIONS
	private static final String SAVE_OFFLINE_STATUS = "INSERT INTO character_offline_trade (`charId`,`time`,`type`,`title`) VALUES (?,?,?,?)";
	private static final String SAVE_ITEMS = "INSERT INTO character_offline_trade_items (`charId`,`item`,`count`,`price`,`enchant`) VALUES (?,?,?,?,?)";
	private static final String DELETE_OFFLINE_TABLE_ALL_ITEMS = "delete from character_offline_trade_items where charId=?";
	private static final String DELETE_OFFLINE_TRADER = "DELETE FROM character_offline_trade where charId=?";
	private static final String CLEAR_OFFLINE_TABLE = "DELETE FROM character_offline_trade";
	private static final String CLEAR_OFFLINE_TABLE_ITEMS = "DELETE FROM character_offline_trade_items";
	private static final String LOAD_OFFLINE_STATUS = "SELECT * FROM character_offline_trade";
	private static final String LOAD_OFFLINE_ITEMS = "SELECT * FROM character_offline_trade_items WHERE charId = ?";

	//called when server will go off, different from storeOffliner because
	//of store of normal sellers/buyers also if not in offline mode
	public static void storeOffliners()
	{
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();)
		{
			PreparedStatement stm = con.prepareStatement(CLEAR_OFFLINE_TABLE);
			stm.execute();
			stm.close();
			stm = con.prepareStatement(CLEAR_OFFLINE_TABLE_ITEMS);
			stm.execute();
			stm.close();

			con.setAutoCommit(false); // avoid halfway done
			stm = con.prepareStatement(SAVE_OFFLINE_STATUS);
			PreparedStatement stm_items = con.prepareStatement(SAVE_ITEMS);

			for (L2PcInstance pc : L2World.getInstance().getAllPlayers())
			{
				try
				{
					//without second check, server will store all guys that are in shop mode
					if ((pc.getPrivateStoreType() != L2PcInstance.STORE_PRIVATE_NONE)/* && (pc.isOffline())*/)
					{
						stm.setInt(1, pc.getObjectId()); //Char Id
						stm.setLong(2, pc.getOfflineStartTime());
						stm.setInt(3, pc.getPrivateStoreType()); //store type
						String title = null;

						switch (pc.getPrivateStoreType())
						{
							case L2PcInstance.STORE_PRIVATE_BUY:
								if (!Config.OFFLINE_TRADE_ENABLE)
									continue;
								title = pc.getBuyList().getTitle();
								for (TradeItem i : pc.getBuyList().getItems())
								{
									stm_items.setInt(1, pc.getObjectId());
									stm_items.setInt(2, i.getItem().getItemId());
									stm_items.setLong(3, i.getCount());
									stm_items.setLong(4, i.getPrice());
									stm_items.setLong(5, i.getEnchant());
									stm_items.executeUpdate();
									stm_items.clearParameters();
								}
								break;
							case L2PcInstance.STORE_PRIVATE_SELL:
							case L2PcInstance.STORE_PRIVATE_PACKAGE_SELL:
								if (!Config.OFFLINE_TRADE_ENABLE)
									continue;
								title = pc.getSellList().getTitle();
								pc.getSellList().updateItems();
								for (TradeItem i : pc.getSellList().getItems())
								{
									stm_items.setInt(1, pc.getObjectId());
									stm_items.setInt(2, i.getObjectId());
									stm_items.setLong(3, i.getCount());
									stm_items.setLong(4, i.getPrice());
									stm_items.setLong(5, i.getEnchant());
									stm_items.executeUpdate();
									stm_items.clearParameters();
								}
								break;
							case L2PcInstance.STORE_PRIVATE_MANUFACTURE:
								
								if (!Config.OFFLINE_CRAFT_ENABLE)
									continue;
								title = pc.getCreateList().getStoreName();
								for (L2ManufactureItem i : pc.getCreateList().getList())
								{
									stm_items.setInt(1, pc.getObjectId());
									stm_items.setInt(2, i.getRecipeId());
									stm_items.setLong(3, 0);
									stm_items.setLong(4, i.getCost());
									stm_items.setLong(5, 0);
									stm_items.executeUpdate();
									stm_items.clearParameters();
								}
								break;
							default:
								//_log.info( "OfflineTradersTable[storeTradeItems()]: Error while saving offline trader: " + pc.getObjectId() + ", store type: "+pc.getPrivateStoreType());
								//no save for this kind of shop
								continue;
						}
						stm.setString(4, title);
						stm.executeUpdate();
						stm.clearParameters();
						con.commit(); // flush
					}
				}
				catch (Exception e)
				{
					if(Config.DEBUG)
						e.printStackTrace();
					
					_log.log(Level.WARNING, "OfflineTradersTable[storeTradeItems()]: Error while saving offline trader: " + pc.getObjectId() + " " + e, e);
				}
			}
			stm.close();
			stm_items.close();
			_log.info("Offline traders stored.");
		}
		catch (Exception e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
			
			_log.log(Level.WARNING,"OfflineTradersTable[storeTradeItems()]: Error while saving offline traders: " + e,e);
		}
	}

	public static void restoreOfflineTraders()
	{
		_log.info("Loading offline traders...");

		int nTraders = 0;
		try(Connection con = L2DatabaseFactory.getInstance().getConnection();)
		{
			PreparedStatement stm = con.prepareStatement(LOAD_OFFLINE_STATUS);
			ResultSet rs = stm.executeQuery();
			while (rs.next())
			{
				long time = rs.getLong("time");
				if (Config.OFFLINE_MAX_DAYS > 0)
				{
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(time);
					cal.add(Calendar.DAY_OF_YEAR, Config.OFFLINE_MAX_DAYS);
					if (cal.getTimeInMillis() <= System.currentTimeMillis())
					{
						_log.info("Offline trader with id "+rs.getInt("charId")+" reached OfflineMaxDays, kicked.");
						continue;
					}
				}

				int type = rs.getInt("type");
				if (type == L2PcInstance.STORE_PRIVATE_NONE)
					continue;

				L2PcInstance player = null;

				try
				{
					L2GameClient client = new L2GameClient(null);
					player = L2PcInstance.load(rs.getInt("charId"));
					client.setActiveChar(player);
					client.setAccountName(player.getAccountName());
					client.setState(GameClientState.IN_GAME);
					player.setClient(client);
					player.setOffline(true);
					player.setOfflineStartTime(time);
					if (Config.OFFLINE_SLEEP_EFFECT)
						player.startAbnormalEffect(L2Character.ABNORMAL_EFFECT_SLEEP);
					player.spawnMe(player.getX(), player.getY(), player.getZ());
					LoginServerThread.getInstance().addGameServerLogin(player.getAccountName(), client);
					PreparedStatement stm_items = con.prepareStatement(LOAD_OFFLINE_ITEMS);
					stm_items.setInt(1, player.getObjectId());
					ResultSet items = stm_items.executeQuery();

					switch (type)
					{
						case L2PcInstance.STORE_PRIVATE_BUY:
							while (items.next())
							{
								player.getBuyList().addItemByItemId(items.getInt(2), items.getInt(3), items.getInt(4), items.getInt(5));
							}
							player.getBuyList().setTitle(rs.getString("title"));
							break;
						case L2PcInstance.STORE_PRIVATE_SELL:
						case L2PcInstance.STORE_PRIVATE_PACKAGE_SELL:
							while (items.next())
							{
								player.getSellList().addItem(items.getInt(2), items.getInt(3), items.getInt(4));
							}
							player.getSellList().setTitle(rs.getString("title"));
							player.getSellList().setPackaged(type == L2PcInstance.STORE_PRIVATE_PACKAGE_SELL);
							break;
						case L2PcInstance.STORE_PRIVATE_MANUFACTURE:
							L2ManufactureList createList = new L2ManufactureList();
							while (items.next())
							{
								createList.add(new L2ManufactureItem(items.getInt(2), items.getInt(4)));
							}
							player.setCreateList(createList);
							player.getCreateList().setStoreName(rs.getString("title"));
							break;
						default:
							_log.info("Offline trader "+player.getName()+" finished to sell his items");
					}
					items.close();
					stm_items.close();

					player.sitDown();
					if (Config.OFFLINE_SET_NAME_COLOR){
						player._originalNameColorOffline = player.getAppearance().getNameColor();
						player.getAppearance().setNameColor(Config.OFFLINE_NAME_COLOR);
					}
					player.setPrivateStoreType(type);
					player.setOnlineStatus(true);
					player.restoreEffects();
					player.broadcastUserInfo();
					nTraders++;
				}
				catch (Exception e)
				{
					if(Config.DEBUG)
						e.printStackTrace();

					_log.log(Level.WARNING, "OfflineTradersTable[loadOffliners()]: Error loading trader: ",e);
					if (player != null)
						player.logout();
				}
			}
			rs.close();
			stm.close();
			_log.info("Loaded: " +nTraders+ " offline trader(s)");
		}
		catch (Exception e)
		{
			if(Config.DEBUG)
				e.printStackTrace();

			_log.log(Level.WARNING, "OfflineTradersTable[loadOffliners()]: Error while loading offline traders: ",e);
		}
	}

	public static void storeOffliner(L2PcInstance pc)
	{
		if ((pc.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_NONE) || (!pc.isOffline()))
			return;

		try(Connection con = L2DatabaseFactory.getInstance().getConnection();)
		{
			PreparedStatement stm = con.prepareStatement(DELETE_OFFLINE_TABLE_ALL_ITEMS);
			stm.setInt(1, pc.getObjectId());
			stm.execute();
			stm.clearParameters();
			stm.close();
			stm = con.prepareStatement(DELETE_OFFLINE_TRADER);
			stm.setInt(1, pc.getObjectId());
			stm.execute();
			stm.clearParameters();
			stm.close();

			con.setAutoCommit(false); // avoid halfway done
			stm = con.prepareStatement(SAVE_OFFLINE_STATUS);
			PreparedStatement stm_items = con.prepareStatement(SAVE_ITEMS);

			boolean save = true;

			try
			{
				stm.setInt(1, pc.getObjectId()); //Char Id
				stm.setLong(2, pc.getOfflineStartTime());
				stm.setInt(3, pc.getPrivateStoreType()); //store type
				String title = null;

				switch (pc.getPrivateStoreType())
				{
					case L2PcInstance.STORE_PRIVATE_BUY:
						if (!Config.OFFLINE_TRADE_ENABLE)
							break;
						title = pc.getBuyList().getTitle();
						for (TradeItem i : pc.getBuyList().getItems())
						{
							stm_items.setInt(1, pc.getObjectId());
							stm_items.setInt(2, i.getItem().getItemId());
							stm_items.setLong(3, i.getCount());
							stm_items.setLong(4, i.getPrice());
							stm_items.setLong(5, i.getEnchant());
							stm_items.executeUpdate();
							stm_items.clearParameters();
						}
						break;
					case L2PcInstance.STORE_PRIVATE_SELL:
					case L2PcInstance.STORE_PRIVATE_PACKAGE_SELL:
						if (!Config.OFFLINE_TRADE_ENABLE)
							break;
						title = pc.getSellList().getTitle();
						pc.getSellList().updateItems();
						for (TradeItem i : pc.getSellList().getItems())
						{
							stm_items.setInt(1, pc.getObjectId());
							stm_items.setInt(2, i.getObjectId());
							stm_items.setLong(3, i.getCount());
							stm_items.setLong(4, i.getPrice());
							stm_items.setLong(5, i.getEnchant());
							stm_items.executeUpdate();
							stm_items.clearParameters();
						}
						break;
					case L2PcInstance.STORE_PRIVATE_MANUFACTURE:
						
						if (!Config.OFFLINE_CRAFT_ENABLE)
							break;
						title = pc.getCreateList().getStoreName();
						for (L2ManufactureItem i : pc.getCreateList().getList())
						{
							stm_items.setInt(1, pc.getObjectId());
							stm_items.setInt(2, i.getRecipeId());
							stm_items.setLong(3, 0);
							stm_items.setLong(4, i.getCost());
							stm_items.setLong(5, 0);
							stm_items.executeUpdate();
							stm_items.clearParameters();
						}
						break;
					default:
						//_log.info( "OfflineTradersTable[storeOffliner()]: Error while saving offline trader: " + pc.getObjectId() + ", store type: "+pc.getPrivateStoreType());
						//no save for this kind of shop
						save = false;
				}

				if(save)
				{
					stm.setString(4, title);
					stm.executeUpdate();
					stm.clearParameters();
					con.commit(); // flush
				}
			}
			catch (Exception e)
			{
				if(Config.DEBUG)
					e.printStackTrace();

				_log.log(Level.WARNING, "OfflineTradersTable[storeOffliner()]: Error while saving offline trader: " + pc.getObjectId() + " " + e, e);
			}

			stm.close();
			stm_items.close();
			String text = "Offline trader "+pc.getName()+" stored.";
			Log.add(text, "Offline_trader");
		}
		catch (Exception e)
		{
			if(Config.DEBUG)
				e.printStackTrace();

			_log.log(Level.WARNING,"OfflineTradersTable[storeOffliner()]: Error while saving offline traders: " + e,e);
		}
	}
}