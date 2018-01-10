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
package com.it.br.gameserver.network;

import com.it.br.Config;
import com.it.br.configuration.settings.L2JModsSettings;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.LoginServerThread;
import com.it.br.gameserver.LoginServerThread.SessionKey;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.communitybbs.Manager.RegionBBSManager;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.database.dao.OfflineTradeDao;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.datatables.xml.MapRegionTable;
import com.it.br.gameserver.instancemanager.AwayManager;
import com.it.br.gameserver.lib.Log;
import com.it.br.gameserver.model.CharSelectInfoPackage;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.Olympiad.Olympiad;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.L2Event;
import com.it.br.gameserver.model.entity.event.TvTEvent;
import com.it.br.gameserver.network.serverpackets.L2GameServerPacket;
import com.it.br.gameserver.network.serverpackets.ServerClose;
import com.it.br.gameserver.network.serverpackets.UserInfo;
import com.it.br.gameserver.util.FloodProtectors;
import com.it.br.util.EventData;
import com.l2jserver.mmocore.network.MMOClient;
import com.l2jserver.mmocore.network.MMOConnection;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.it.br.configuration.Configurator.getSettings;

public final class L2GameClient extends MMOClient<MMOConnection<L2GameClient>>
{
	protected static final Logger _log = Logger.getLogger(L2GameClient.class.getName());

	/**
	 * CONNECTED	- client has just connected
	 * AUTHED		- client has authed but doesnt has character attached to it yet
	 * IN_GAME		- client has selected a char and is in game
	 * @author  KenM
	 */
	public static enum GameClientState { CONNECTED, AUTHED, IN_GAME };

	public GameClientState state;

	// Info
	public String accountName;
	private String _adress;
	public SessionKey sessionId;
	public L2PcInstance activeChar;
	private ReentrantLock _activeCharLock = new ReentrantLock();

	private boolean _isAuthedGG;
	private long _connectionStartTime;
	private List<Integer> _charSlotMapping = new ArrayList<>();

	// floodprotectors 
	private final FloodProtectors _floodProtectors = new FloodProtectors(this);
	
	// Task
	@SuppressWarnings("rawtypes")
	protected /*final*/ ScheduledFuture _autoSaveInDB;

	// Crypt
	public GameCrypt crypt;

	// Flood protection
	public byte packetsSentInSec = 0;
	public int packetsSentStartTick = 0;
	
	private int[][] trace;
	
	public boolean _isDetached = false;
	protected boolean _forcedToClose = false;
	
	// UnknownPacket protection
	private int unknownPacketCount = 0;
	private boolean _isBeingProcessed = false;
	protected ScheduledFuture<?> _cleanupTask = null;

	public L2GameClient(MMOConnection<L2GameClient> con)
	{
		super(con);
		if (con != null)
			_adress = con.getInetAddress().getHostAddress();
		state = GameClientState.CONNECTED;
		_connectionStartTime = System.currentTimeMillis();
		crypt = new GameCrypt();
		_autoSaveInDB = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new AutoSaveTask(), 300000L, 900000L);
	}

	public byte[] enableCrypt()
	{
		byte[] key = BlowFishKeygen.getRandomKey();
		crypt.setKey(key);
		return key;
	}

	public GameClientState getState()
	{
		return state;
	}

	public void setState(GameClientState pState)
	{
		state = pState;
	}

	public long getConnectionStartTime()
	{
		return _connectionStartTime;
	}

	@Override
	public boolean decrypt(ByteBuffer buf, int size)
	{
		crypt.decrypt(buf.array(), buf.position(), size);
		return true;
	}

	@Override
	public boolean encrypt(final ByteBuffer buf, final int size)
	{
		crypt.encrypt(buf.array(), buf.position(), size);
		buf.position(buf.position() + size);
		return true;
	}

	public L2PcInstance getActiveChar()
	{
		return activeChar;
	}

	public void setActiveChar(L2PcInstance pActiveChar)
	{
		activeChar = pActiveChar;
		if (activeChar != null)
		{
			L2World.getInstance().storeObject(getActiveChar());
		}
	}

	public ReentrantLock getActiveCharLock()
	{
		return _activeCharLock;
	}

	public boolean isAuthedGG()
	{
		return _isAuthedGG;
	}
	
	public void setGameGuardOk(boolean val)
	{
		_isAuthedGG = val;
	}

	public void setAccountName(String pAccountName)
	{
		accountName = pAccountName;
	}

	public String getAccountName()
	{
		return accountName;
	}
	
	public String getAdress()
	{
		return _adress;
	}
	
	public void setSessionId(SessionKey sk)
	{
		sessionId = sk;
	}

	public SessionKey getSessionId()
	{
		return sessionId;
	}

	public void sendPacket(L2GameServerPacket gsp)
	{
		if (_isDetached) 
			return;
		
		if(getConnection()!=null)
		{
			
			if(Config.DEBUG)
			{
				
				Log.add("[ServerPacket] SendingGameServerPacket, Client: "+this.toString()+" Packet:"+gsp.getType(), "GameServerPacketsLog");
				
			}
			
			getConnection().sendPacket(gsp);
			gsp.runImpl();
		}
	}
	
	public boolean isDetached()
	{
		return _isDetached;
	}
	
	public void setDetached(boolean b)
	{
		_isDetached = b;
	}

	public L2PcInstance markToDeleteChar(int charslot) throws Exception
	{
		//have to make sure active character must be nulled
		/*if (getActiveChar() != null)
		{
			saveCharToDisk(getActiveChar());
			if (Config.DEBUG)
			{
				_log.fine("active Char saved");
			}
			this.setActiveChar(null);
		}*/

		int objid = getObjectIdForSlot(charslot);
		if (objid < 0)
		    return null;

		L2PcInstance character = L2PcInstance.load(objid);
		if (character.getClanId() != 0)
			return character;

		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=? WHERE obj_id=?");
			statement.setLong(1, System.currentTimeMillis() + Config.DELETE_DAYS*86400000L); // 24*60*60*1000 = 86400000
			statement.setInt(2, objid);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Data error on update delete time of char: " + e);
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
		}
	    return null;
	}

	public L2PcInstance deleteChar(int charslot) throws Exception
	{
		//have to make sure active character must be nulled
		/*if (getActiveChar() != null)
		{
			saveCharToDisk (getActiveChar());
			if (Config.DEBUG) _log.fine("active Char saved");
			this.setActiveChar(null);
		}*/

		int objid = getObjectIdForSlot(charslot);
		if (objid < 0)
    	    return null;

		L2PcInstance character = L2PcInstance.load(objid);
		if (character.getClanId() != 0)
			return character;

		deleteCharByObjId(objid);
		return null;
	}

	/**
	 * Save the L2PcInstance to the database.
	 */
	public static void saveCharToDisk(L2PcInstance cha)
	{
        try
        {
            cha.store();
        }
        catch(Exception e)
        {
            _log.severe("Error saving player character: "+e);
        }
	}

	public void markRestoredChar(int charslot) throws Exception
	{
		//have to make sure active character must be nulled
		/*if (getActiveChar() != null)
		{
			saveCharToDisk (getActiveChar());
			if (Config.DEBUG) _log.fine("active Char saved");
			this.setActiveChar(null);
		}*/

		int objid = getObjectIdForSlot(charslot);
    		if (objid < 0)
    		    return;
		Connection con = null;
		try
		{
		con = L2DatabaseFactory.getInstance().getConnection();
		PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE obj_id=?");
		statement.setInt(1, objid);
		statement.execute();
		statement.close();
		}
		catch (Exception e)
		{
			_log.severe("Data error on restoring char: " + e);
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
		}
	}

	public static void deleteCharByObjId(int objid)
	{
	    if (objid < 0)
	        return;

	    Connection con = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement ;

        	statement = con.prepareStatement("DELETE FROM character_friends WHERE char_id=? OR friend_id=?");
			statement.setInt(1, objid);
			statement.setInt(2, objid);
			statement.execute();
			statement.close();

            statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=?");
            statement.setInt(1, objid);
            statement.execute();
            statement.close();

			statement = con.prepareStatement("DELETE FROM character_macroses WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_recipebook WHERE char_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

            statement = con.prepareStatement("DELETE FROM heroes WHERE char_id=?");
            statement.setInt(1, objid);
            statement.execute();
            statement.close();

            statement = con.prepareStatement("DELETE FROM olympiad_nobles WHERE char_id=?");
            statement.setInt(1, objid);
            statement.execute();
            statement.close();

            statement = con.prepareStatement("DELETE FROM seven_signs WHERE char_obj_id=?");
            statement.setInt(1, objid);
            statement.execute();
            statement.close();

        	statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id IN (SELECT object_id FROM items WHERE items.owner_id=?)");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM augmentations WHERE item_id IN (SELECT object_id FROM items WHERE items.owner_id=?)");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM items WHERE owner_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();

			statement = con.prepareStatement("DELETE FROM merchant_lease WHERE player_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();


			statement = con.prepareStatement("DELETE FROM characters WHERE obj_Id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Data error on deleting char: " + e);
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
		}
	}

	public L2PcInstance loadCharFromDisk(int charslot)
	{
		L2PcInstance character = L2PcInstance.load(getObjectIdForSlot(charslot));

		if (character != null)
		{
			//restoreInventory(character);
			//restoreSkills(character);
                	//character.restoreSkills();
			//restoreShortCuts(character);
			//restoreWarehouse(character);

			// preinit some values for each login
			character.setRunning();	// running is default
			character.standUp();	// standing is default

			character.refreshOverloaded();
			character.refreshExpertisePenalty();
			character.sendPacket(new UserInfo(character));
			character.broadcastKarma();
			character.setOnlineStatus(true);
		}
		else
		{
			_log.severe("could not restore in slot: "+ charslot);
		}

		//setCharacter(character);
		return character;
	}

	/**
     * @param chars
     */
    public void setCharSelection(CharSelectInfoPackage[] chars)
    {
        _charSlotMapping.clear();

        for (int i = 0; i < chars.length; i++)
        {
            int objectId = chars[i].getObjectId();
            _charSlotMapping.add(Integer.valueOf(objectId));
        }
    }

    public void close(L2GameServerPacket gsp)
    {
    	if(getConnection()!=null)
    		getConnection().close(gsp);
    }

    /**
     * @param charslot
     * @return
     */
    private int getObjectIdForSlot(int charslot)
    {
        if (charslot < 0 || charslot >= _charSlotMapping.size())
        {
            _log.warning(toString()+" tried to delete Character in slot "+charslot+" but no characters exits at that slot.");
            return -1;
        }
        Integer objectId = _charSlotMapping.get(charslot);
        return objectId.intValue();
    }


    @Override
	protected void onForcedDisconnection()
    {
    	_log.log(Level.WARNING, "Client " + toString() + " disconnected abnormally.");
		L2PcInstance player = null;
		if((player = getActiveChar()) !=null)
		{

			_log.log(Level.WARNING, "Character disconnected at Loc X:"+getActiveChar().getX()+" Y:"+getActiveChar().getY()+" Z:"+getActiveChar().getZ());
			_log.log(Level.WARNING, "Character disconnected in (closest) zone: "+MapRegionTable.getInstance().getClosestTownName(getActiveChar()));

			if(player.isFlying())
				player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));

			if(player.isInParty())
			{
				player.getParty().removePartyMember(player);
			}

			//Decrease boxes number
			if(player._active_boxes!=-1)
				player.decreaseBoxes();

			if(Olympiad.getInstance().isRegistered(player))
			{
				Olympiad.getInstance().unRegisterNoble(player);
			}

			player.deleteMe();
		}
    }

    @Override
    protected void onDisconnection()
    {
    	// no long running tasks here, do it async
    	try
    	{
    		ThreadPoolManager.getInstance().executeTask(new DisconnectTask());
    	}
    	catch (RejectedExecutionException e)
    	{
    		// server is closing
    	}
    }
    
    public boolean checkUnknownPackets()
    {
    	ServerSettings serverSettings = getSettings(ServerSettings.class);
    	if (this.getActiveChar() != null && !activeChar.getFloodProtectors().getPacket().tryPerformAction("packet"))
    	{
    		unknownPacketCount++;
    		if (unknownPacketCount >= serverSettings.getMaxUnknownPacket())
    		{
    			return true;
    		}
    		else
    			return false;
    	}
    	else
    	{
    		unknownPacketCount = 0;
    		return false;
    	}
    }

    /**
     * Produces the best possible string representation of this client.
     */

	@Override
	public String toString()
	{
		try
		{
			InetAddress address = getConnection().getInetAddress();
			switch (getState())
			{
				case CONNECTED:
					return "[IP: "+(address == null ? "disconnected" : address.getHostAddress())+"]";
				case AUTHED:
					return "[Account: "+getAccountName()+" - IP: "+(address == null ? "disconnected" : address.getHostAddress())+"]";
				case IN_GAME:
					return "[Character: "+(getActiveChar() == null ? "disconnected" : getActiveChar().getName())+" - Account: "+getAccountName()+" - IP: "+(address == null ? "disconnected" : address.getHostAddress())+"]";
				default:
					throw new IllegalStateException("Missing state on switch");
			}
		}
		catch (NullPointerException e)
		{
			return "[Character read failed due to disconnect]";
		}
	}

	class DisconnectTask implements Runnable
	{
		public void run()
		{
			boolean fast = true;

			try
			{
				// Update BBS
				try
				{
					RegionBBSManager.getInstance().changeCommunityBoard();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				if(_autoSaveInDB != null)
					_autoSaveInDB.cancel(true);

				L2PcInstance player = L2GameClient.this.getActiveChar();
				if (player != null)
				{
					if(Olympiad.getInstance().isRegistered(player))
					{
						Olympiad.getInstance().unRegisterNoble(player);
					}

					if(player.isFlying())
					{
						player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
					}

					if(player.isInParty())
					{
						player.getParty().removePartyMember(player);
					}
					
					if(player.isAway())
					{
						AwayManager.getInstance().extraBack(player);
					}

					L2JModsSettings l2jModsSettings = getSettings(L2JModsSettings.class);
					if(!Olympiad.getInstance().isRegistered(player) && !player.isInOlympiadMode() && !player.isInFunEvent() && !TvTEvent.isPlayerParticipant(player.getObjectId())
							&& ((player.isInStoreMode() && l2jModsSettings.isOfflineTradeEnabled()) || (player.isInCraftMode() && l2jModsSettings.isOfflineCraftEnabled())))
					{
						player.setOffline(true);
						player.leaveParty();
						player.store();

						if(l2jModsSettings.isOfflineNameColorEnabled())
						{
							player._originalNameColorOffline=player.getAppearance().getNameColor();
							player.getAppearance().setNameColor(l2jModsSettings.getOfflineNameColor());
							player.broadcastUserInfo();
						}

						if (player.getOfflineStartTime() == 0)
							player.setOfflineStartTime(System.currentTimeMillis());

						OfflineTradeDao.storeOffliner(player);

						return;
					}

					if(player._active_boxes!=-1)
						player.decreaseBoxes();

					if (player.isInCombat())
					{
						fast = false;
					}
				}
				cleanMe(fast);
			}
			catch (Exception e1)
			{
				_log.log(Level.WARNING, "Error while disconnecting client.", e1);
			}
		}
	}

	public void cleanMe(boolean fast)
	{
		try
		{
			synchronized(this)
			{
				if (_cleanupTask == null)
				{
					_cleanupTask = ThreadPoolManager.getInstance().scheduleGeneral(new CleanupTask(), fast ? 5 : 15000L);
				}			
			}
		}
		catch (Exception e1)
		{
			_log.log(Level.WARNING, "Error during cleanup.", e1);			
		}
	}

	class CleanupTask implements Runnable
	{
		public void run()
		{
			try
			{
				// Update BBS
				try
				{
					RegionBBSManager.getInstance().changeCommunityBoard();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				// we are going to mannually save the char bellow thus we can force the cancel
				if (_autoSaveInDB != null)
				{
					_autoSaveInDB.cancel(true);
				}

	            L2PcInstance player = L2GameClient.this.getActiveChar();
				if (player != null)  // this should only happen on connection loss
				{
					//Decrease boxes number
					if(player._active_boxes!=-1)
						player.decreaseBoxes();

	                // we store all data from players who are disconnected while in an event in order to restore it in the next login
	                if (player.atEvent)
	                {
	                	EventData data = new EventData(player.eventX, player.eventY, player.eventZ, player.eventkarma, player.eventpvpkills, player.eventpkkills, player.eventTitle, player.kills, player.eventSitForced);
	                    L2Event.connectionLossData.put(player.getName(), data);
	                }
	                if (player.isFlying())
	                {
	                	player.removeSkill(SkillTable.getInstance().getInfo(4289, 1));
	                }

	                // prevent closing again
					player.setClient(null);
					player.deleteMe();
					try
					{
						player.store(_forcedToClose);
					}
					catch(Exception e2)
					{
						if(Config.DEBUG)
							e2.printStackTrace();
					}
						

					try
	                {
						saveCharToDisk(player);
					}
	                catch (Exception e2) { /* ignore any problems here */ }
				}
				L2GameClient.this.setActiveChar(null);
			}
			catch (Exception e1)
			{
				_log.log(Level.WARNING, "error while disconnecting client", e1);
			}
			finally
			{
				LoginServerThread.getInstance().sendLogout(L2GameClient.this.getAccountName());
			}
		}
	}

	class AutoSaveTask implements Runnable
	{
	
		public void run()
		{
			try
			{
				L2PcInstance player = L2GameClient.this.getActiveChar();
				if (player != null)
				{
					saveCharToDisk(player);
					if (player.getPet() != null)
						player.getPet().store();
				}
			}
			catch (Throwable e)
			{
				_log.severe(e.toString());
			}
		}
	}

	public void setClientTracert(int[][] tracert)
	{
		trace = tracert;
	}

	public int[][] getTrace()
	{
		return trace;
	}
	
	public boolean isBeingProcessed()
	{
		return _isBeingProcessed;
	}
		
	public void setIsBeingProcessed(boolean val)
	{
		_isBeingProcessed = val;
	}
	
	public void closeNow()
	{
		_isDetached = true; // prevents more packets execution
		close(new ServerClose());
		synchronized (this)
		{
			if (_cleanupTask != null)
				cancelCleanup();
			_cleanupTask = ThreadPoolManager.getInstance().scheduleGeneral(new CleanupTask(), 0); //instant
		}
	}
	
	public FloodProtectors getFloodProtectors()
	{
		return _floodProtectors;
	}
	
	private boolean cancelCleanup()
	{
		Future<?> task = _cleanupTask;
		if (task != null)
		{
			_cleanupTask = null;
			return task.cancel(true);
		}
		return false;
	}
	
	/**
	 * @return the _forcedToClose
	 */
	public boolean is_forcedToClose()
	{
		return _forcedToClose;
	}
	}
	

