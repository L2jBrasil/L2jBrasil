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
package com.it.br.gameserver.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.L2DatabaseFactory;
import com.it.br.gameserver.cache.CrestCache;
import com.it.br.gameserver.cache.CrestCache.CrestType;
import com.it.br.gameserver.communitybbs.BB.Forum;
import com.it.br.gameserver.communitybbs.Manager.ForumsBBSManager;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.instancemanager.CrownManager;
import com.it.br.gameserver.instancemanager.SiegeManager;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.ActionFailed;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.network.serverpackets.L2GameServerPacket;
import com.it.br.gameserver.network.serverpackets.MagicSkillUser;
import com.it.br.gameserver.network.serverpackets.PledgeReceiveSubPledgeCreated;
import com.it.br.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import com.it.br.gameserver.network.serverpackets.PledgeShowMemberListAll;
import com.it.br.gameserver.network.serverpackets.PledgeShowMemberListDeleteAll;
import com.it.br.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import com.it.br.gameserver.network.serverpackets.PledgeSkillListAdd;
import com.it.br.gameserver.network.serverpackets.StatusUpdate;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.network.serverpackets.UserInfo;
import com.it.br.gameserver.util.Util;

public class L2Clan
{
	private static final Logger _log = Logger.getLogger(L2Clan.class.getName());

	private String _name;
	private int _clanId;
	private L2ClanMember _leader;
	private Map<String, L2ClanMember> _members = new HashMap<>();

	private String _allyName;
	private int _allyId;
	private int _level;
	private int _hasCastle;
	private int _hasFort;
	private int _hasHideout;
    private boolean _hasCrest;
    private int _hiredGuards;
    private int _crestId;
    private int _crestLargeId;
    private int _allyCrestId;
    private int _auctionBiddedAt = 0;
    private long _allyPenaltyExpiryTime;
    private int _allyPenaltyType;
    private long _charPenaltyExpiryTime;
    private long _dissolvingExpiryTime;
    // Ally Penalty Types
    /** Clan leaved ally */
    public static final int PENALTY_TYPE_CLAN_LEAVED = 1;
    /** Clan was dismissed from ally */
    public static final int PENALTY_TYPE_CLAN_DISMISSED = 2;
    /** Leader clan dismiss clan from ally */
    public static final int PENALTY_TYPE_DISMISS_CLAN = 3;
    /** Leader clan dissolve ally */
    public static final int PENALTY_TYPE_DISSOLVE_ALLY = 4;

    private ItemContainer _warehouse = new ClanWarehouse(this);
    private List<Integer> _atWarWith = new ArrayList<>();
    private List<Integer> _atWarAttackers = new ArrayList<>();

	private boolean _hasCrestLarge;
	private Forum _forum;
	private List<L2Skill> _skillList = new ArrayList<>();

	//  Clan Privileges
    /** No privilege to manage any clan activity */
    public static final int CP_NOTHING = 0;
    /** Privilege to join clan */
    public static final int CP_CL_JOIN_CLAN = 2;
    /** Privilege to give a title */
    public static final int CP_CL_GIVE_TITLE = 4;
    /** Privilege to view warehouse content */
    public static final int CP_CL_VIEW_WAREHOUSE = 8;
    /** Privilege to manage clan ranks */
    public static final int CP_CL_MANAGE_RANKS = 16;
    public static final int CP_CL_PLEDGE_WAR = 32;
    public static final int CP_CL_DISMISS = 64;
    /** Privilege to register clan crest */
    public static final int CP_CL_REGISTER_CREST = 128;
    public static final int CP_CL_MASTER_RIGHTS = 256;
    public static final int CP_CL_MANAGE_LEVELS = 512;
    /** Privilege to open a door */
    public static final int CP_CH_OPEN_DOOR = 1024;
    public static final int CP_CH_OTHER_RIGHTS = 2048;
    public static final int CP_CH_AUCTION = 4096;
    public static final int CP_CH_DISMISS = 8192;
    public static final int CP_CH_SET_FUNCTIONS = 16384;
    public static final int CP_CS_OPEN_DOOR = 32768;
    public static final int CP_CS_MANOR_ADMIN = 65536;
    public static final int CP_CS_MANAGE_SIEGE = 131072;
    public static final int CP_CS_USE_FUNCTIONS = 262144;
    public static final int CP_CS_DISMISS = 524288;
    public static final int CP_CS_TAXES =1048576;
    public static final int CP_CS_MERCENARIES =2097152;
    public static final int CP_CS_SET_FUNCTIONS =4194304;
    /** Privilege to manage all clan activity */
    public static final int CP_ALL = 8388606;

    // Sub-unit types
    /** Clan subunit type of Academy */
    public static final int SUBUNIT_ACADEMY = -1;
    /** Clan subunit type of Royal Guard A */
    public static final int SUBUNIT_ROYAL1 = 100;
    /** Clan subunit type of Royal Guard B */
    public static final int SUBUNIT_ROYAL2 = 200;
    /** Clan subunit type of Order of Knights A-1 */
    public static final int SUBUNIT_KNIGHT1 = 1001;
    /** Clan subunit type of Order of Knights A-2 */
    public static final int SUBUNIT_KNIGHT2 = 1002;
    /** Clan subunit type of Order of Knights B-1 */
    public static final int SUBUNIT_KNIGHT3 = 2001;
    /** Clan subunit type of Order of Knights B-2 */
    public static final int SUBUNIT_KNIGHT4 = 2002;

    /** FastMap(Integer, L2Skill) containing all skills of the L2Clan */
    protected final Map<Integer, L2Skill> _skills = new HashMap<>();
    protected final Map<Integer, RankPrivs> _privs = new HashMap<>();
    protected final Map<Integer, SubPledge> _subPledges = new HashMap<>();

    private int _reputationScore = 0;
    private int _rank = 0;

    private String _notice;
    
    @SuppressWarnings("unused")
    private boolean _noticeEnabled = true;
    
    /**
     * Called if a clan is referenced only by id.
     * In this case all other data needs to be fetched from db
     *
     * @param clanId A valid clan Id to create and restore
     */
    public L2Clan(int clanId)
    {
        _clanId = clanId;
        initializePrivs();
        insertNotice();
        restore();
        getWarehouse().restore();
        checkCrests();
    }
    
    	// at the end of the file, before the last '}' that ends the L2Clan class, add the following codes:
    	 public void insertNotice()
    	 {
    	 Connection con = null;
    	 try
    	 {
    		 con = L2DatabaseFactory.getInstance().getConnection();
    		 PreparedStatement statement = con.prepareStatement("INSERT INTO clan_notices (clanID, notice, enabled) values (?,?,?)");
    		 statement.setInt(1, this.getClanId());
    		 statement.setString(2, "Change me");
    		 statement.setString(3, "false");
    		 statement.execute();
    		 statement.close();
    		 con.close();
    		 
    	 }
    	 catch(Exception e)
    	 {
    		 if (Config.DEBUG)
    		 System.out.println("BBS: Error while creating clan notice for clan "+ this.getClanId() + "");
    		 if(e.getMessage()!=null)
    			 if (Config.DEBUG)
    			 System.out.println("BBS: Exception = "+e.getMessage()+"");
    	      }
    	 }
    
    	 /**
    	  * @return Returns the clan notice.
    	  */
    	 public String getNotice()
    	 {
    		 Connection con = null;
    		 try
    		 {
    			 con = L2DatabaseFactory.getInstance().getConnection();
    			 PreparedStatement statement = con.prepareStatement("SELECT notice FROM clan_notices WHERE clanID=?");
    			 statement.setInt(1, getClanId());
    			 ResultSet rset = statement.executeQuery();
    
    			 while (rset.next())
    			 {
    				 _notice = rset.getString("notice");
    			 }
    
    			 rset.close();
    			 statement.close();
    			 con.close();
    
    		 } catch (Exception e)
    		 {
    			 if (Config.DEBUG)
    			 System.out.println("BBS: Error while getting notice from DB for clan "+ this.getClanId() + "");
    			 if(e.getMessage()!=null)
    				 if (Config.DEBUG)
    				 System.out.println("BBS: Exception = "+e.getMessage()+"");
    		 }
    		 return _notice;
    	 }
    
    	 public String getNoticeForBBS()
    	 {
    		 String notice="";
    		 Connection con = null;
    		 try
    		 {
    			 con = L2DatabaseFactory.getInstance().getConnection();
    			 PreparedStatement statement = con.prepareStatement("SELECT notice FROM clan_notices WHERE clanID=?");
    			 statement.setInt(1, getClanId());
    			 ResultSet rset = statement.executeQuery();
    
    			 while (rset.next())
    			 {
    				 notice = rset.getString("notice");
    			 }
    
    			 rset.close();
    			 statement.close();
    			 con.close();
    
    		 } catch (Exception e)
    		 {
    			 if (Config.DEBUG)
    			 System.out.println("BBS: Error while getting notice from DB for clan "+ this.getClanId() + "");
    			 if(e.getMessage()!=null)
    				 if (Config.DEBUG)
    				 System.out.println("BBS: Exception = "+e.getMessage()+"");
    		 }
    		 return notice.replaceAll("<br>", "\n");
    	 }
    	 /**
    	  * @param notice The new clan notice.
    	  */
    	 public void setNotice(String notice)
    	 {
    
    		 notice = notice.replaceAll("\n", "<br>");
    
    		 Connection con = null;
    		 try
    		 {
    			 con = L2DatabaseFactory.getInstance().getConnection();
    
    			 con = L2DatabaseFactory.getInstance().getConnection();
    			 PreparedStatement statement = con.prepareStatement("UPDATE clan_notices SET notice=? WHERE clanID=?");
    
    			 statement.setString(1, notice);
    			 statement.setInt(2, this.getClanId());
    			 statement.execute();
    			 statement.close();
    			 con.close();
    
    			 _notice = notice;
    		 } catch (Exception e)
    		 {
    			 if (Config.DEBUG)
    			 System.out.println("BBS: Error while saving notice for clan "+ this.getClanId() + "");
    			 if(e.getMessage()!=null)
    				 if (Config.DEBUG)
    				 System.out.println("BBS: Exception = "+e.getMessage()+"");
    		 }
    	 }
    	 /**
    	  * @return Returns the noticeEnabled.
    	  */
    	 public boolean isNoticeEnabled()
    	 {
    		 String result="";
    		 Connection con = null;
    		 try
    		 {
    
    			 con = L2DatabaseFactory.getInstance().getConnection();
    			 PreparedStatement statement = con.prepareStatement("SELECT enabled FROM clan_notices WHERE clanID=?");
    			 statement.setInt(1, getClanId());
    			 ResultSet rset = statement.executeQuery();
    
    			 while (rset.next())
    			 {
    				 result = rset.getString("enabled");
    			 }
    
    			 rset.close();
    			 statement.close();
    			 con.close();
    
    		 } catch (Exception e)
    		 {
    			 if (Config.DEBUG)
    			 System.out.println("BBS: Error while reading _noticeEnabled for clan "+ this.getClanId() + "");
    			 if(e.getMessage()!=null)
    				 if (Config.DEBUG) 
    				 System.out.println("BBS: Exception = "+e.getMessage()+"");
    		 }
    		 if (result.isEmpty())
    		 {
    			 insertNotice();
    			 return false;
    		 }
    		 else if(result.compareToIgnoreCase("true")==0)
    			 return true;
    		 else
    			 return false;
    	 }
    	 /**
    	  * @param noticeEnabled The noticeEnabled to set.
    	  */
    	 public void setNoticeEnabled(boolean noticeEnabled)
    	 {
    
    		 Connection con = null;
    		 try
    		 {
    			 con = L2DatabaseFactory.getInstance().getConnection();
    			 PreparedStatement statement = con.prepareStatement("UPDATE clan_notices SET enabled=? WHERE clanID=?");
    			 if(noticeEnabled)
    				 statement.setString(1, "true");
    			 else
    				 statement.setString(1, "false");
    			 statement.setInt(2, this.getClanId());
    			 statement.execute();
    			 statement.close();
    			 con.close();
    
    		 } catch (Exception e)
    		 {
    			 if (Config.DEBUG)
    			 System.out.println("BBS: Error while updating notice status for clan "+ this.getClanId() + "");
    			 if(e.getMessage()!=null)
    				 if (Config.DEBUG)
    				 System.out.println("BBS: Exception = "+e.getMessage()+"");
    		 }
    
    		 _noticeEnabled = noticeEnabled;
    
    	 }

    /**
     * Called only if a new clan is created
     *
     * @param clanId  A valid clan Id to create
     * @param clanName  A valid clan name
     */
    public L2Clan(int clanId, String clanName)
    {
        _clanId = clanId;
        _name = clanName;
        initializePrivs();
    }

	/**
	 * @return Returns the clanId.
	 */
	public int getClanId()
	{
		return _clanId;
	}
	/**
	 * @param clanId The clanId to set.
	 */
	public void setClanId(int clanId)
	{
		_clanId = clanId;
	}
	/**
	 * @return Returns the leaderId.
	 */
	public int getLeaderId()
	{
		return (_leader != null ? _leader.getObjectId() : 0);
	}
    /**
     * @return L2ClanMember of clan leader.
     */
    public L2ClanMember getLeader()
    {
        return _leader;
    }
    
	/**
	 * @param member The leaderId to set.
	 */
	public boolean setLeader(L2ClanMember member)
	{
		if(member == null)
			return false;

		L2ClanMember old_leader = _leader;
		_leader = member;
		_members.put(member.getName(), member);

		//refresh oldleader and new leader info
		if(old_leader != null)
		{

			L2PcInstance exLeader = old_leader.getPlayerInstance();
			exLeader.setClan(this);
			exLeader.getClan().getClanMember(exLeader.getObjectId());
			exLeader.setPledgeClass(L2ClanMember.calculatePledgeClass(exLeader));
			exLeader.setClanPrivileges(L2Clan.CP_NOTHING);

			exLeader.broadcastUserInfo();

			CrownManager.getInstance().checkCrowns(exLeader);

		}

		updateClanInDB();

		if(member.getPlayerInstance() != null)
		{

			L2PcInstance newLeader = member.getPlayerInstance();
			newLeader.setClan(this);
			newLeader.setPledgeClass(L2ClanMember.calculatePledgeClass(newLeader));
			newLeader.setClanPrivileges(L2Clan.CP_ALL);

			newLeader.broadcastUserInfo();

		}

		broadcastClanStatus();

		CrownManager.getInstance().checkCrowns(member.getPlayerInstance());

		return true;
	}

	//public void setNewLeader(L2ClanMember member) 
	public void setNewLeader(L2ClanMember member, L2PcInstance activeChar)
	{
		if(activeChar.isRiding() || activeChar.isFlying())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		if(!getLeader().isOnline())
			return;

		if(member == null)
			return;

		if(!member.isOnline())
			return;

		//L2PcInstance exLeader = getLeader().getPlayerInstance();
		if(setLeader(member))
		{

			SystemMessage sm = new SystemMessage(SystemMessageId.CLAN_LEADER_PRIVILEGES_HAVE_BEEN_TRANSFERRED_TO_S1);
			sm.addString(member.getName());
			broadcastToOnlineMembers(sm);
			sm = null;

		}

		//SiegeManager.getInstance().removeSiegeSkills(exLeader);

		/*
		if(getLevel() >= 4)
		{
			SiegeManager.getInstance().addSiegeSkills(newLeader);
		}
		*/

	}

	/**
	 * @return Returns the leaderName.
	 */
	public String getLeaderName()
	{
		return (_leader != null ? _leader.getName() : "");
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return _name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		_name = name;
	}

	private void addClanMember(L2ClanMember member)
	{
		_members.put(member.getName(), member);
	}

	public void addClanMember(L2PcInstance player)
	{
		L2ClanMember member = new L2ClanMember(this,player.getName(), player.getLevel(), player.getClassId().getId(), player.getObjectId(), player.getPledgeType(), player.getPowerGrade(), player.getTitle());
        // store in memory
		addClanMember(member);
		member.setPlayerInstance(player);
		player.setClan(this);
		player.rewardSkills();
		player.setPledgeClass(L2ClanMember.calculatePledgeClass(player));
		player.sendPacket(new PledgeShowMemberListUpdate(player));
		player.sendPacket(new UserInfo(player));
    }

	public void updateClanMember(L2PcInstance player)
	{
		L2ClanMember member = new L2ClanMember(player);

		addClanMember(member);
	}

	public L2ClanMember getClanMember(String name)
	{
		return _members.get(name);
	}

    public L2ClanMember getClanMember(int objectID)
    {
        for (L2ClanMember temp : _members.values())
        {
            if (temp.getObjectId() == objectID) return temp;
        }
        return null;
    }

    public void removeClanMember(String name, long clanJoinExpiryTime)
	{
		L2ClanMember exMember = _members.remove(name);
		if(exMember == null)
		{
			_log.warning("Member "+name+" not found in clan while trying to remove");
			return;
		}
		int leadssubpledge = getLeaderSubPledge(name);
		if (leadssubpledge != 0)
		{
			// Sub-unit leader withdraws, position becomes vacant and leader
			// should appoint new via NPC
			getSubPledge(leadssubpledge).setLeaderName("");
			updateSubPledgeInDB(leadssubpledge);
		}

		if(exMember.getApprentice() != 0)
		{
			L2ClanMember apprentice = getClanMember(exMember.getApprentice());
			if(apprentice != null)
			{
				 if (apprentice.getPlayerInstance() != null)
					 apprentice.getPlayerInstance().setSponsor(0);
				 else
					 apprentice.initApprenticeAndSponsor(0, 0);

				 apprentice.saveApprenticeAndSponsor(0, 0);
			}
		}
		if(exMember.getSponsor() != 0)
		{
			L2ClanMember sponsor = getClanMember(exMember.getSponsor());
			if(sponsor != null)
			{
				 if (sponsor.getPlayerInstance() != null)
					 sponsor.getPlayerInstance().setApprentice(0);
				 else
					 sponsor.initApprenticeAndSponsor(0, 0);

				 sponsor.saveApprenticeAndSponsor(0, 0);
			}
		}
		exMember.saveApprenticeAndSponsor(0, 0);
		if (Config.REMOVE_CASTLE_CIRCLETS)
		{
			CastleManager.getInstance().removeCirclet(exMember,getHasCastle());
		}
		if (exMember.isOnline())
		{
			L2PcInstance player = exMember.getPlayerInstance();
            player.setTitle(""); 
			player.setApprentice(0);
			player.setSponsor(0);

			if (player.isClanLeader())
			{
		        SiegeManager.getInstance().removeSiegeSkills(player);
		        player.setClanCreateExpiryTime(System.currentTimeMillis() + Config.CLAN_CREATE_DAYS * 86400000L); //24*60*60*1000 = 86400000
			}
		    // remove Clanskills from Player  
            for (L2Skill skill : player.getClan().getAllSkills())  
            player.removeSkill(skill, false);  
	        player.setClan(null);
            player.setTitle(null);
            // players leaving from clan academy have no penalty 
            if (exMember.getPledgeType() != -1) 
            player.setClanJoinExpiryTime(clanJoinExpiryTime); 
         	player.setPledgeClass(L2ClanMember.calculatePledgeClass(player));
			player.broadcastUserInfo();
			// disable clan tab
			player.sendPacket(new PledgeShowMemberListDeleteAll());
		}
		else
		{
			removeMemberInDatabase(exMember, clanJoinExpiryTime,
					getLeaderName().equalsIgnoreCase(name) ? System.currentTimeMillis() + Config.CLAN_CREATE_DAYS * 86400000L : 0);
		}
	}

	public L2ClanMember[] getMembers()
	{
		return _members.values().toArray(new L2ClanMember[_members.size()]);
	}

	public int getMembersCount()
	{
		return _members.size();
	}

	public int getSubPledgeMembersCount(int subpl)
	{
		int result = 0;
		for (L2ClanMember temp : _members.values())
		{
			if (temp.getPledgeType() == subpl) result++;
		}
		return result;
	}

	public int getMaxNrOfMembers(int pledgetype)
	{
		int limit = 0;

		switch (pledgetype)
		{
		case 0:
			switch (getLevel())
			{
			case 4:
				limit   = 40;
				break;
			case 3:
				limit   = 30;
				break;
			case 2:
				limit   = 20;
				break;
			case 1:
				limit   = 15;
				break;
			case 0:
				limit   = 10;
				break;
			default:
				limit   = 40;
			break;
			}
			break;
		case -1:
		case 100:
		case 200:
			limit   = 20;
			break;
		case 1001:
		case 1002:
		case 2001:
		case 2002:
			limit   = 10;
			break;
		default:
			break;
        }

        return limit;
	}

	public L2PcInstance[] getOnlineMembers(String exclude)
	{
		List<L2PcInstance> result = new ArrayList<>();
		for (L2ClanMember temp : _members.values())
		{
			try	{
				if (temp.isOnline() && !temp.getName().equals(exclude))
					result.add(temp.getPlayerInstance());
			} catch (NullPointerException e) {}
		}

		return result.toArray(new L2PcInstance[result.size()]);
	}

	/**
	 * @return
	 */
	public int getAllyId()
	{
		return _allyId;
	}
	/**
	 * @return
	 */
	public String getAllyName()
	{
		return _allyName;
	}

    public void setAllyCrestId(int allyCrestId)
    {
        _allyCrestId = allyCrestId;
    }

	/**
	 * @return
	 */
	public int getAllyCrestId()
	{
		return _allyCrestId;
	}
	/**
	 * @return
	 */
	public int getLevel()
	{
		return _level;
	}
	/**
	 * @return
	 */
	public int getHasCastle()
	{
		return _hasCastle;
	}

	/**
	 * @return hasFort
	 */
	public int getHasFort()
	{
		return _hasFort;
	}
	
	/**
	 * @return
	 */
	public int getHasHideout()
	{
		return _hasHideout;
	}

    /**
     * @param crestId The id of pledge crest.
     */
    public void setCrestId(int crestId)
    {
        _crestId = crestId;
    }

	/**
	 * @return Returns the clanCrestId.
	 */
	public int getCrestId()
	{
		return _crestId;
	}

    /**
     * @param crestLargeId The id of pledge LargeCrest.
     */
    public void setCrestLargeId(int crestLargeId)
    {
        _crestLargeId = crestLargeId;
    }

	/**
	 * @return Returns the clan CrestLargeId
	 */
	public int getCrestLargeId()
	{
		return _crestLargeId;
	}

	/**
	 * @param allyId The allyId to set.
	 */
	public void setAllyId(int allyId)
	{
		_allyId = allyId;
	}
	/**
	 * @param allyName The allyName to set.
	 */
	public void setAllyName(String allyName)
	{
		_allyName = allyName;
	}
	
	/**
	 * @param hasCastle The hasCastle to set.
	 */
	public void setHasCastle(int hasCastle)
	{
		_hasCastle = hasCastle;
	}

	/**
	 * @param hasFort
	 */
	public void setHasFort(int hasFort)
	{
		_hasFort = hasFort;
	}
	/**
	 * @param hasHideout The hasHideout to set.
	 */
	public void setHasHideout(int hasHideout)
	{
		_hasHideout = hasHideout;
	}
	
	/**
	 * @param level The level to set.
	 */
	public void setLevel(int level)
	{
	    _level = level;
	    if(_forum == null)
	    {
	    	if(_level >= 2)
	    	{
	    		_forum = ForumsBBSManager.getInstance().getForumByName("ClanRoot").getChildByName(_name);
            	if(_forum == null)
            	{
            		_forum = ForumsBBSManager.getInstance().createNewForum(_name,ForumsBBSManager.getInstance().getForumByName("ClanRoot"),Forum.CLAN,Forum.CLANMEMBERONLY,getClanId());
            	}
	    	}
	    }
	}

	/**
	 * @param name player name
	 * @return
	 */
	public boolean isMember(String name)
	{
		return (name == null ? false :_members.containsKey(name));
	}

	public void updateClanInDB()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET leader_id=?,ally_id=?,ally_name=?,reputation_score=?,ally_penalty_expiry_time=?,ally_penalty_type=?,char_penalty_expiry_time=?,dissolving_expiry_time=? WHERE clan_id=?");
			statement.setInt(1, getLeaderId());
			statement.setInt(2, getAllyId());
			statement.setString(3, getAllyName());
			statement.setInt(4, getReputationScore());
			statement.setLong(5, getAllyPenaltyExpiryTime());
			statement.setInt(6, getAllyPenaltyType());
			statement.setLong(7, getCharPenaltyExpiryTime());
			statement.setLong(8, getDissolvingExpiryTime());
			statement.setInt(9, getClanId());
			statement.execute();
			statement.close();
			if (Config.DEBUG) _log.fine("New clan leader saved in db: "+getClanId());
		}
		catch (Exception e)
		{
			_log.warning("error while saving new clan leader to db "+e);
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
		}
	}

	public void store()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO clan_data (clan_id,clan_name,clan_level,hasCastle,ally_id,ally_name,leader_id,crest_id,crest_large_id,ally_crest_id) values (?,?,?,?,?,?,?,?,?,?)");
			statement.setInt(1, getClanId());
			statement.setString(2, getName());
			statement.setInt(3, getLevel());
			statement.setInt(4, getHasCastle());
			statement.setInt(5, getAllyId());
			statement.setString(6, getAllyName());
			statement.setInt(7, getLeaderId());
            statement.setInt(8, getCrestId());
            statement.setInt(9,getCrestLargeId());
            statement.setInt(10,getAllyCrestId());
			statement.execute();
			statement.close();

			if (Config.DEBUG) _log.fine("New clan saved in db: "+getClanId());
		}
		catch (Exception e)
		{
			_log.warning("error while saving new clan to db "+e);
		}
		finally
		{
			try { con.close(); } catch (Exception e) {}
		}
	}

    private void removeMemberInDatabase(L2ClanMember member, long clanJoinExpiryTime, long clanCreateExpiryTime)
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("UPDATE characters SET clanid=0, title=?, clan_join_expiry_time=?, clan_create_expiry_time=?, clan_privs=0, wantspeace=0, subpledge=0, lvl_joined_academy=0, apprentice=0, sponsor=0 WHERE obj_Id=?");
            statement.setString(1, "");
            statement.setLong(2, clanJoinExpiryTime);
            statement.setLong(3, clanCreateExpiryTime);
            statement.setInt(4, member.getObjectId());
            statement.execute();
            statement.close();
            if (Config.DEBUG) _log.fine("clan member removed in db: "+getClanId());

            statement = con.prepareStatement("UPDATE characters SET apprentice=0 WHERE apprentice=?");
            statement.setInt(1, member.getObjectId());
            statement.execute();
            statement.close();

            statement = con.prepareStatement("UPDATE characters SET sponsor=0 WHERE sponsor=?");
            statement.setInt(1, member.getObjectId());
            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            _log.warning("error while removing clan member in db "+e);
        }
        finally
        {
            try { con.close(); } catch (Exception e) {}
        }
    }

    @SuppressWarnings("unused")
    private void updateWarsInDB()
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement;
            statement = con.prepareStatement("UPDATE clan_wars SET wantspeace1=? WHERE clan1=?");
            statement.setInt(1, 0);
            statement.setInt(2, 0);

            // TODO: deprecated? no execute? :o

            statement.close();
        }
        catch (Exception e)
        {
            _log.warning("could not update clans wars data:" + e);
        }
        finally
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
            }
        }
    }

    private void restore()
    {
        //restorewars();
    	Connection con = null;
        try
        {
            L2ClanMember member;

            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT clan_name,clan_level,hasCastle,ally_id,ally_name,leader_id,crest_id,crest_large_id,ally_crest_id,reputation_score,auction_bid_at,ally_penalty_expiry_time,ally_penalty_type,char_penalty_expiry_time,dissolving_expiry_time FROM clan_data where clan_id=?");
            statement.setInt(1, getClanId());
            ResultSet clanData = statement.executeQuery();

            if (clanData.next())
            {
            	setName(clanData.getString("clan_name"));
                setLevel(clanData.getInt("clan_level"));
                setHasCastle(clanData.getInt("hasCastle"));
                setAllyId(clanData.getInt("ally_id"));
                setAllyName(clanData.getString("ally_name"));
                setAllyPenaltyExpiryTime(clanData.getLong("ally_penalty_expiry_time"), clanData.getInt("ally_penalty_type"));
                if (getAllyPenaltyExpiryTime() < System.currentTimeMillis())
                {
                	setAllyPenaltyExpiryTime(0, 0);
                }
                setCharPenaltyExpiryTime(clanData.getLong("char_penalty_expiry_time"));
                if (getCharPenaltyExpiryTime() + Config.CLAN_JOIN_DAYS * 86400000L < System.currentTimeMillis()) //24*60*60*1000 = 86400000
                {
                	setCharPenaltyExpiryTime(0);
                }
                setDissolvingExpiryTime(clanData.getLong("dissolving_expiry_time"));

                setCrestId(clanData.getInt("crest_id"));
                if (getCrestId() != 0)
                {
                    setHasCrest(true);
                }

                setCrestLargeId(clanData.getInt("crest_large_id"));
                if (getCrestLargeId() != 0)
                {
                	setHasCrestLarge(true);
                }

                setAllyCrestId(clanData.getInt("ally_crest_id"));
                setReputationScore(clanData.getInt("reputation_score"), false);
                setAuctionBiddedAt(clanData.getInt("auction_bid_at"), false);

                int leaderId = (clanData.getInt("leader_id"));

                PreparedStatement statement2 = con.prepareStatement("SELECT char_name,level,classid,obj_Id,title,power_grade,subpledge,apprentice,sponsor FROM characters WHERE clanid=?");
                statement2.setInt(1, getClanId());
                ResultSet clanMembers = statement2.executeQuery();

                while (clanMembers.next())
                {
                	member = new L2ClanMember(this, clanMembers.getString("char_name"), clanMembers.getInt("level"), clanMembers.getInt("classid"), clanMembers.getInt("obj_id"),clanMembers.getInt("subpledge"), clanMembers.getInt("power_grade"), clanMembers.getString("title"));
                    if (member.getObjectId() == leaderId)
                    	setLeader(member);
                    else
                        addClanMember(member);
                    member.initApprenticeAndSponsor(clanMembers.getInt("apprentice"), clanMembers.getInt("sponsor"));
                }
                clanMembers.close();
                statement2.close();
            }

            clanData.close();
            statement.close();

            if (Config.DEBUG && getName() != null)
            	_log.config("Restored clan data for \"" + getName() + "\" from database.");
            restoreSubPledges();
            restoreRankPrivs();
            restoreSkills();
        }
        catch (Exception e)
        {
            _log.warning("error while restoring clan "+e);
        }
        finally
        {
            try { con.close(); } catch (Exception e) {}
        }
    }

    private void restoreSkills()
    {
        Connection con = null;

        try
        {
            // Retrieve all skills of this L2PcInstance from the database
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT skill_id,skill_level FROM clan_skills WHERE clan_id=?");
            statement.setInt(1, getClanId());

            ResultSet rset = statement.executeQuery();

            // Go though the recordset of this SQL query
            while (rset.next())
            {
                int id = rset.getInt("skill_id");
                int level = rset.getInt("skill_level");
                // Create a L2Skill object for each record
                L2Skill skill = SkillTable.getInstance().getInfo(id, level);
                // Add the L2Skill object to the L2Clan _skills
                _skills.put(skill.getId(), skill);
            }

            rset.close();
            statement.close();
        }
        catch (Exception e)
        {
            _log.warning("Could not restore clan skills: " + e);
        }
        finally
        {
            try { con.close(); } catch (Exception e) {}
        }
    }

    /** used to retrieve all skills */
    public final L2Skill[] getAllSkills()
    {
        if (_skills == null)
            return new L2Skill[0];

        return _skills.values().toArray(new L2Skill[_skills.values().size()]);
    }


    /** used to add a skill to skill list of this L2Clan */
    public L2Skill addSkill(L2Skill newSkill)
    {
        L2Skill oldSkill    = null;

        if (newSkill != null)
        {
            // Replace oldSkill by newSkill or Add the newSkill
            oldSkill = _skills.put(newSkill.getId(), newSkill);
        }

        return oldSkill;
    }

    /** used to add a new skill to the list, send a packet to all online clan members, update their stats and store it in db*/
    public L2Skill addNewSkill(L2Skill newSkill)
    {
        L2Skill oldSkill    = null;
        Connection con = null;

        if (newSkill != null)
        {

            // Replace oldSkill by newSkill or Add the newSkill
            oldSkill = _skills.put(newSkill.getId(), newSkill);


            try
            {
                con = L2DatabaseFactory.getInstance().getConnection();
                PreparedStatement statement;

                if (oldSkill != null)
                {
                    statement = con.prepareStatement("UPDATE clan_skills SET skill_level=? WHERE skill_id=? AND clan_id=?");
                    statement.setInt(1, newSkill.getLevel());
                    statement.setInt(2, oldSkill.getId());
                    statement.setInt(3, getClanId());
                    statement.execute();
                    statement.close();
                }
                else
                {
                    statement = con.prepareStatement("INSERT INTO clan_skills (clan_id,skill_id,skill_level,skill_name) VALUES (?,?,?,?)");
                    statement.setInt(1, getClanId());
                    statement.setInt(2, newSkill.getId());
                    statement.setInt(3, newSkill.getLevel());
                    statement.setString(4, newSkill.getName());
                    statement.execute();
                    statement.close();
                }
            }
            catch (Exception e)
            {
                _log.warning("Error could not store char skills: " + e);
            }
            finally
            {
                try { con.close(); } catch (Exception e) {}
            }


            for (L2ClanMember temp : _members.values())
            {
                try {
                	if (temp != null && temp.isOnline())
                	{
                		if (newSkill.getMinPledgeClass() <= temp.getPlayerInstance().getPledgeClass())
                		{
                			temp.getPlayerInstance().addSkill(newSkill, false); // Skill is not saved to player DB
                			temp.getPlayerInstance().sendPacket(new PledgeSkillListAdd(newSkill.getId(), newSkill.getLevel()));
                		}
                	}
                } catch (NullPointerException e) 
                  {
                          _log.log(Level.WARNING, e.getMessage(),e);
                  }
            }
        }

        return oldSkill;
    }


    public void addSkillEffects()
    {
        for(L2Skill skill : _skills.values())
        {
            for (L2ClanMember temp : _members.values())
            {
                try{
                	if (temp != null && temp.isOnline())
                	{
                		if (skill.getMinPledgeClass() <= temp.getPlayerInstance().getPledgeClass())
                			temp.getPlayerInstance().addSkill(skill, false); // Skill is not saved to player DB
                	}
                } catch (NullPointerException e) 
                  {
                          _log.log(Level.WARNING, e.getMessage(),e);
                  }
            }
        }
    }

    public void addSkillEffects(L2PcInstance cm)
    {
        if (cm == null)
            return;

        for(L2Skill skill : _skills.values())
        {
            //TODO add skills according to members class( in ex. don't add Clan Agillity skill's effect to lower class then Baron)
            if (skill.getMinPledgeClass() <= cm.getPledgeClass())
            	cm.addSkill(skill, false); // Skill is not saved to player DB
        }
    }

	public void broadcastToOnlineAllyMembers(L2GameServerPacket packet)
	{
		if (getAllyId() == 0)
		{
			return;
		}
		for (L2Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getAllyId() == getAllyId())
			{
				clan.broadcastToOnlineMembers(packet);
			}
		}
	}

	public void broadcastToOnlineMembers(L2GameServerPacket packet)
	{
		for (L2ClanMember member : _members.values())
		{
			try {
				if (member.isOnline())
					member.getPlayerInstance().sendPacket(packet);
			} catch (NullPointerException e) {}
		}
	}

	public void broadcastToOtherOnlineMembers(L2GameServerPacket packet, L2PcInstance player)
	{
		for (L2ClanMember member : _members.values())
		{
			try {
				if (member.isOnline() && member.getPlayerInstance() != player)
					member.getPlayerInstance().sendPacket(packet);
			} catch (NullPointerException e) {}
		}
	}


	public String toString()
	{
		return getName();
	}

    /**
     * @return
     */
    public boolean hasCrest()
    {
        return _hasCrest;
    }

    public boolean hasCrestLarge()
	{
		return _hasCrestLarge;
	}

    public void setHasCrest(boolean flag)
    {
        _hasCrest = flag;
    }
	
    public void setHasCrestLarge(boolean flag)
    {
        _hasCrestLarge = flag;
    }

    public ItemContainer getWarehouse()
    {
        return _warehouse;
    }
    public boolean isAtWarWith(Integer id)
    {
    	if ((_atWarWith != null)&&(_atWarWith.size() > 0))
    		if (_atWarWith.contains(id)) return true;
    	return false;
    }
    public boolean isAtWarAttacker(Integer id)
    {
    	if ((_atWarAttackers != null)&&(_atWarAttackers.size() > 0))
    		if (_atWarAttackers.contains(id)) return true;
    	return false;
    }
    public void setEnemyClan(L2Clan clan)
    {
    	Integer id = clan.getClanId();
    	_atWarWith.add(id);
    }
    public void setEnemyClan(Integer clan)
    {
    	_atWarWith.add(clan);
    }
    public void setAttackerClan(L2Clan clan)
    {
    	Integer id = clan.getClanId();
    	_atWarAttackers.add(id);
    }
    public void setAttackerClan(Integer clan)
    {
    	_atWarAttackers.add(clan);
    }
    public void deleteEnemyClan(L2Clan clan)
    {
    	Integer id = clan.getClanId();
    	_atWarWith.remove(id);
    }
    public void deleteAttackerClan(L2Clan clan)
    {
    	Integer id = clan.getClanId();
    	_atWarAttackers.remove(id);
    }
    public int getHiredGuards(){ return _hiredGuards; }
    public void incrementHiredGuards(){ _hiredGuards++; }

    public int isAtWar()
    {
       if ((_atWarWith != null)&&(_atWarWith.size() > 0))
           return 1;
       return 0;
    }

    public List<Integer> getWarList()
    {
    	return _atWarWith;
    }

    public List<Integer> getAttackerList()
    {
    	return _atWarAttackers;
    }

    public void broadcastClanStatus()
    {
        for(L2PcInstance member: getOnlineMembers(""))
        {
        	member.sendPacket(new PledgeShowMemberListDeleteAll());
        	member.sendPacket(new PledgeShowMemberListAll(this, member));
        }
    }

    public void removeSkill(int id)
    {
    	L2Skill deleteSkill = null;
    	for(L2Skill sk : _skillList)
    	{
    		if(sk.getId() == id)
    		{
    			deleteSkill = sk;
    			return;
    		}
    	}
    	_skillList.remove(deleteSkill);
    }

    public void removeSkill(L2Skill deleteSkill)
    {
    	_skillList.remove(deleteSkill);
    }

	/**
	 * @return
	 */
	public List<L2Skill> getSkills()
	{
		return _skillList;
	}

	public class SubPledge
    {
       private int _id;
       private String _subPledgeName;
       private String _leaderName;

       public SubPledge(int id, String name, String leaderName)
       {
           _id = id;
           _subPledgeName = name;
           _leaderName = leaderName;
       }

       public int getId()
       {
           return _id;
       }
       public String getName()
       {
           return _subPledgeName;
       }
       public void setName(String newName) 
       { 
           _subPledgeName = newName; 
       } 
       public String getLeaderName()
       {
           return _leaderName;
       }

       public void setLeaderName(String leaderName)
       {
           _leaderName = leaderName;
       }
    }

    public class RankPrivs
    {
       private int _rankId;
       private int _party;// TODO find out what this stuff means and implement it
       private int _rankPrivs;

       public RankPrivs(int rank, int party, int privs)
       {
    	   _rankId = rank;
           _party = party;
           _rankPrivs = privs;
       }

       public int getRank()
       {
           return _rankId;
       }
       
       public int getParty()
       {
           return _party;
       }
       
       public int getPrivs()
       {
           return _rankPrivs;
       }
       
       public void setPrivs(int privs)
       {
    	   _rankPrivs = privs;
       }
    }

    private void restoreSubPledges()
    {
        Connection con = null;

        try
        {
            // Retrieve all subpledges of this clan from the database
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT sub_pledge_id,name,leader_name FROM clan_subpledges WHERE clan_id=?");
            statement.setInt(1, getClanId());
            ResultSet rset = statement.executeQuery();

            while (rset.next())
            {
                int id = rset.getInt("sub_pledge_id");
                String name = rset.getString("name");
                String leaderName = rset.getString("leader_name");
                // Create a SubPledge object for each record
                SubPledge pledge = new SubPledge(id, name, leaderName);
                _subPledges.put(id, pledge);
            }

            rset.close();
            statement.close();
        }
        catch (Exception e)
        {
            _log.warning("Could not restore clan sub-units: " + e);
        }
        finally
        {
            try { con.close(); } catch (Exception e) {}
        }
    }

    /** used to retrieve subPledge by type */
    public final SubPledge getSubPledge(int pledgeType)
    {
        if (_subPledges == null)
            return null;

        return _subPledges.get(pledgeType);
    }

    /** used to retrieve subPledge by type */
    public final SubPledge getSubPledge(String pledgeName)
    {
        if (_subPledges == null)
            return null;

        for (SubPledge sp : _subPledges.values())
    	{
    		if (sp.getName().equalsIgnoreCase(pledgeName))
    		{
    			return sp;
    		}
    	}
        return null;
    }

    /** used to retrieve all subPledges */
    public final SubPledge[] getAllSubPledges()
    {
        if (_subPledges == null)
            return new SubPledge[0];

        return _subPledges.values().toArray(new SubPledge[_subPledges.values().size()]);
    }

    public SubPledge createSubPledge(L2PcInstance player, int pledgeType, String leaderName, String subPledgeName)
    {
    	SubPledge subPledge = null;
        pledgeType = getAvailablePledgeTypes(pledgeType);
        if (pledgeType == 0)
        {
          	if (pledgeType == L2Clan.SUBUNIT_ACADEMY)
                player.sendPacket(new SystemMessage(SystemMessageId.CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY));
        	else
        		player.sendMessage("You can't create any more sub-units of this type");
        	return null;
        }
        if (_leader.getName().equals(leaderName))
        {
        	player.sendMessage("Leader is not correct");
        	return null;
        }

        // Royal Guard 5000 points per each
        // Order of Knights 10000 points per each
        if(pledgeType != -1	&& (getReputationScore() < 5000 && pledgeType < L2Clan.SUBUNIT_KNIGHT1) || getReputationScore() < 10000 && pledgeType > L2Clan.SUBUNIT_ROYAL2)
        {
        	SystemMessage sp = new SystemMessage(SystemMessageId.CLAN_REPUTATION_SCORE_IS_TOO_LOW);
        	player.sendPacket(sp);
        	return null;
        }
        else
        {
	        Connection con = null;
	        try
	        {
	            con = L2DatabaseFactory.getInstance().getConnection();
	            PreparedStatement statement = con.prepareStatement("INSERT INTO clan_subpledges (clan_id,sub_pledge_id,name,leader_name) values (?,?,?,?)");
	            statement.setInt(1, getClanId());
	            statement.setInt(2, pledgeType);
	            statement.setString(3, subPledgeName);
	            if (pledgeType != -1)
	                statement.setString(4, leaderName);
	            else
	                statement.setString(4, "");
	            statement.execute();
	            statement.close();

	            subPledge = new SubPledge(pledgeType, subPledgeName, leaderName);
	            _subPledges.put(pledgeType, subPledge);

	            if(pledgeType != -1)
	            {
	            	setReputationScore(getReputationScore() - 2500, true);
	            }

	            if (Config.DEBUG) _log.fine("New sub_clan saved in db: "+getClanId()+"; "+pledgeType);
	        }
	        catch (Exception e)
	        {
	            _log.warning("error while saving new sub_clan to db "+e);
	        }
	        finally
	        {
	            try { con.close(); } catch (Exception e) {}
	        }
        }
        broadcastToOnlineMembers(new PledgeShowInfoUpdate(_leader.getClan()));
        broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(subPledge));
        return subPledge;
    }

    public int getAvailablePledgeTypes(int pledgeType)
    {
    	if (_subPledges.get(pledgeType) != null)
    	{
    		//_log.warning("found sub-unit with id: "+pledgeType);
    		switch(pledgeType)
    		{
    			case SUBUNIT_ACADEMY:
    				return 0;
    			case SUBUNIT_ROYAL1:
    				pledgeType = getAvailablePledgeTypes(SUBUNIT_ROYAL2);
    				break;
    			case SUBUNIT_ROYAL2:
    				return 0;
    			case SUBUNIT_KNIGHT1:
    				pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT2);
    				break;
    			case SUBUNIT_KNIGHT2:
    				pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT3);
    				break;
                case SUBUNIT_KNIGHT3:
                	pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT4);
                	break;
                case SUBUNIT_KNIGHT4:
                	return 0;
    		}
    	}
        return pledgeType;
    }

    public void updateSubPledgeInDB(int pledgeType)
    {
 	   Connection con = null;
 	   try
 	   {
 		   con = L2DatabaseFactory.getInstance().getConnection();
 		   PreparedStatement statement = con.prepareStatement("UPDATE clan_subpledges SET leader_name=? WHERE clan_id=? AND sub_pledge_id=?");
 		   statement.setString(1, getSubPledge(pledgeType).getLeaderName());
 		   statement.setInt(2, getClanId());
 		   statement.setInt(3, pledgeType);
 		   statement.execute();
           statement = con.prepareStatement("UPDATE clan_subpledges SET name=? WHERE clan_id=? AND sub_pledge_id=?"); 
           statement.setString(1, getSubPledge(pledgeType).getName()); 
           statement.setInt(2, getClanId()); 
           statement.setInt(3, pledgeType); 
           statement.execute();
 		   statement.close();
 		   if (Config.DEBUG)
 			   _log.fine("New subpledge leader saved in db: "+getClanId());
 	   }
 	   catch (Exception e)
 	   {
 		   _log.warning("error while saving new clan leader to db "+e);
 	   }
 	   finally
 	   {
 		   try { con.close(); } catch (Exception e) {}
 	   }
    }

    private void restoreRankPrivs()
    {
        Connection con = null;

        try
        {
            // Retrieve all skills of this L2PcInstance from the database
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT privs,rank,party FROM clan_privs WHERE clan_id=?");
            statement.setInt(1, getClanId());
            //_log.warning("clanPrivs restore for ClanId : "+getClanId());
            ResultSet rset = statement.executeQuery();

            // Go though the recordset of this SQL query
            while (rset.next())
            {
                int rank = rset.getInt("rank");
                //int party = rset.getInt("party");
                int privileges = rset.getInt("privs");
                // Create a SubPledge object for each record
                if (rank == -1) 
                      continue;
                
                _privs.get(rank).setPrivs(privileges);
            }

            rset.close();
            statement.close();
        }
        catch (Exception e)
        {
            _log.warning("Could not restore clan privs by rank: " + e);
        }
        finally
        {
            try { con.close(); } catch (Exception e) {}
        }
    }

    public void initializePrivs()
    {
    	RankPrivs privs;
    	for (int i=1; i < 10; i++)
    	{
    		privs = new RankPrivs(i, 0, CP_NOTHING);
    		_privs.put(i, privs);
    	}

    }

    public int getRankPrivs(int rank)
    {
        if (_privs.get(rank) != null)
            return _privs.get(rank).getPrivs();
        else
            return CP_NOTHING;
    }
    public void setRankPrivs(int rank, int privs)
    {
        if (_privs.get(rank)!= null)
        {
            _privs.get(rank).setPrivs(privs);


            Connection con = null;

            try
            {
                //_log.warning("requested store clan privs in db for rank: "+rank+", privs: "+privs);
                // Retrieve all skills of this L2PcInstance from the database
                con = L2DatabaseFactory.getInstance().getConnection();
                PreparedStatement statement = con.prepareStatement("INSERT INTO clan_privs (clan_id,rank,party,privs) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE privs = ?");
                statement.setInt(1, getClanId());
                statement.setInt(2, rank);
                statement.setInt(3, 0);
                statement.setInt(4, privs);
                statement.setInt(5, privs);

                statement.execute();
                statement.close();
            }
            catch (Exception e)
            {
                _log.warning("Could not store clan privs for rank: " + e);
            }
            finally
            {
                try { con.close(); } catch (Exception e) {}
            }
            for (L2ClanMember cm : getMembers())
            {
                if (cm.isOnline())
                    if (cm.getPowerGrade() == rank)
                        if (cm.getPlayerInstance() != null)
                        {
                            cm.getPlayerInstance().setClanPrivileges(privs);
                            cm.getPlayerInstance().sendPacket(new UserInfo(cm.getPlayerInstance()));
                        }
            }
            broadcastClanStatus();
        }
        else
        {
            _privs.put(rank, new RankPrivs(rank, 0, privs));

            Connection con = null;

            try
            {
                //_log.warning("requested store clan new privs in db for rank: "+rank);
                // Retrieve all skills of this L2PcInstance from the database
                con = L2DatabaseFactory.getInstance().getConnection();
                PreparedStatement statement = con.prepareStatement("INSERT INTO clan_privs (clan_id,rank,party,privs) VALUES (?,?,?,?)");
                statement.setInt(1, getClanId());
                statement.setInt(2, rank);
                statement.setInt(3, 0);
                statement.setInt(4, privs);
                statement.execute();
                statement.close();
            }
            catch (Exception e)
            {
                _log.warning("Could not create new rank and store clan privs for rank: " + e);
            }
            finally
            {
                try { con.close(); } catch (Exception e) {}
            }
        }
    }

    /** used to retrieve all RankPrivs */
    public final RankPrivs[] getAllRankPrivs()
    {
        if (_privs == null)
            return new RankPrivs[0];

        return _privs.values().toArray(new RankPrivs[_privs.values().size()]);
    }

    public int getLeaderSubPledge(String name)
    {
        int id = 0;
        for (SubPledge sp : _subPledges.values())
        {
        	if (sp.getLeaderName() == null) continue;
        	if (sp.getLeaderName().equals(name))
                id = sp.getId();
        }
        return id;
    }
    
	public synchronized void addReputationScore(int value, boolean save)
	{
		setReputationScore(getReputationScore() + value, save);
	}
	
	public synchronized void takeReputationScore(int value, boolean save)
	{
		setReputationScore(getReputationScore() - value, save);
	}
	
    public void setReputationScore(int value, boolean save)
    {
    	if(_reputationScore >= 0 && value < 0)
    	{
    		broadcastToOnlineMembers(new SystemMessage(SystemMessageId.REPUTATION_POINTS_0_OR_LOWER_CLAN_SKILLS_DEACTIVATED));
    		L2Skill[] skills = getAllSkills();
    		for (L2ClanMember member : _members.values())
    		{
    			if (member.isOnline() && member.getPlayerInstance() != null)
    			{
    				for (L2Skill sk : skills)
    					member.getPlayerInstance().removeSkill(sk, false);
    			}
    		}
    	}
    	else if(_reputationScore < 0 && value >= 0)
    	{
    		broadcastToOnlineMembers(new SystemMessage(SystemMessageId.CLAN_SKILLS_WILL_BE_ACTIVATED_SINCE_REPUTATION_IS_0_OR_HIGHER));
    		L2Skill[] skills = getAllSkills();
    		for (L2ClanMember member : _members.values())
    		{
    			if (member.isOnline() && member.getPlayerInstance() != null)
    			{
    				for (L2Skill sk : skills)
    				{
    					if(sk.getMinPledgeClass() <= member.getPlayerInstance().getPledgeClass())
    						member.getPlayerInstance().addSkill(sk, false);
    				}
    			}
    		}
    	}

    	_reputationScore = value;
    	if(_reputationScore > 100000000) _reputationScore = 100000000;
    	if(_reputationScore < -100000000) _reputationScore = -100000000;
    	broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
    	if (save) updateClanInDB();
    }

    public int getReputationScore()
    {
    	return _reputationScore;
    }

    public void setRank(int rank)
    {
    	_rank = rank;
    }

    public int getRank()
    {
    	return _rank;
    }
    public int getAuctionBiddedAt()
    {
        return _auctionBiddedAt;
    }

    public void setAuctionBiddedAt(int id, boolean storeInDb)
    {
        _auctionBiddedAt = id;

        if(storeInDb)
        {
        	Connection con = null;
        	try
        	{
        		con = L2DatabaseFactory.getInstance().getConnection();
        		PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET auction_bid_at=? WHERE clan_id=?");
        		statement.setInt(1, id);
        		statement.setInt(2, getClanId());
        		statement.execute();
        		statement.close();
        	}
        	catch (Exception e)
        	{
        		_log.warning("Could not store auction for clan: " + e);
        	}
        	finally
        	{
        		try { con.close(); } catch (Exception e) {}
        	}
        }
    }

    /**
     * Checks if activeChar and target meet various conditions to join a clan
     *
     * @param activeChar
     * @param target
     * @param pledgeType
     * @return
     */
    public boolean checkClanJoinCondition(L2PcInstance activeChar, L2PcInstance target, int pledgeType)
    {
		if (activeChar == null)
		{
		    return false;
		}
		if ((activeChar.getClanPrivileges() & L2Clan.CP_CL_JOIN_CLAN) != L2Clan.CP_CL_JOIN_CLAN)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
			return false;
		}
		if (target == null)
        {
        	activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET));
            return false;
        }
		if (activeChar.getObjectId() == target.getObjectId())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_INVITE_YOURSELF));
			return false;
		}
		if (getCharPenaltyExpiryTime() > System.currentTimeMillis())
		{
        	SystemMessage sm = new SystemMessage(SystemMessageId.YOU_MUST_WAIT_BEFORE_ACCEPTING_A_NEW_MEMBER);
        	sm.addString(target.getName());
        	activeChar.sendPacket(sm);
        	sm = null;
			return false;
		}
		if (target.getClanId() != 0)
		{
        	SystemMessage sm = new SystemMessage(SystemMessageId.S1_WORKING_WITH_ANOTHER_CLAN);
        	sm.addString(target.getName());
        	activeChar.sendPacket(sm);
        	sm = null;
			return false;
		}
		if (target.getClanId() != 0)
		{
        	SystemMessage sm = new SystemMessage(SystemMessageId.S1_WORKING_WITH_ANOTHER_CLAN);
        	sm.addString(target.getName());
        	activeChar.sendPacket(sm);
        	sm = null;
			return false;
		}
		if (target.getClanJoinExpiryTime() > System.currentTimeMillis())
		{
        	SystemMessage sm = new SystemMessage(SystemMessageId.S1_MUST_WAIT_BEFORE_JOINING_ANOTHER_CLAN);
        	sm.addString(target.getName());
        	activeChar.sendPacket(sm);
        	sm = null;
			return false;
		}
		if ((target.getLevel() > 40 || target.getClassId().level() >= 2) && pledgeType == -1)
		{
        	SystemMessage sm = new SystemMessage(SystemMessageId.S1_DOESNOT_MEET_REQUIREMENTS_TO_JOIN_ACADEMY);
        	sm.addString(target.getName());
        	activeChar.sendPacket(sm);
        	sm = null;
			activeChar.sendPacket(new SystemMessage(SystemMessageId.ACADEMY_REQUIREMENTS));
			return false;
		}
		if (getSubPledgeMembersCount(pledgeType) >= getMaxNrOfMembers(pledgeType))
		{
        	if (pledgeType == 0)
        	{
            	SystemMessage sm = new SystemMessage(SystemMessageId.S1_CLAN_IS_FULL);
            	sm.addString(getName());
            	activeChar.sendPacket(sm);
            	sm = null;
        	}
        	else
        	{
            	activeChar.sendPacket(new SystemMessage(SystemMessageId.SUBCLAN_IS_FULL));
        	}
        	return false;
		}
    	return true;
    }

    /**
     * Checks if activeChar and target meet various conditions to join a clan
     *
     * @param activeChar
     * @param target
     * @return
     */
    public boolean checkAllyJoinCondition(L2PcInstance activeChar, L2PcInstance target)
    {
		if (activeChar == null)
		{
			return false;
		}
		if (activeChar.getAllyId() == 0 || !activeChar.isClanLeader() || activeChar.getClanId() != activeChar.getAllyId())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.FEATURE_ONLY_FOR_ALLIANCE_LEADER));
			return false;
		}
        L2Clan leaderClan = activeChar.getClan();
		if (leaderClan.getAllyPenaltyExpiryTime() > System.currentTimeMillis())
		{
			if (leaderClan.getAllyPenaltyType() == PENALTY_TYPE_DISMISS_CLAN)
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.CANT_INVITE_CLAN_WITHIN_1_DAY));
				return false;
			}
		}
		if (target == null)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET));
			return false;
		}
		if (activeChar.getObjectId() == target.getObjectId())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_INVITE_YOURSELF));
			return false;
		}
		if (target.getClan() == null)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.TARGET_MUST_BE_IN_CLAN));
			return false;
		}
		if (!target.isClanLeader())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER);
			sm.addString(target.getName());
			activeChar.sendPacket(sm);
			sm = null;
			return false;
		}
        L2Clan targetClan = target.getClan();
		if (target.getAllyId() != 0)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_CLAN_ALREADY_MEMBER_OF_S2_ALLIANCE);
			sm.addString(targetClan.getName());
			sm.addString(targetClan.getAllyName());
			activeChar.sendPacket(sm);
			sm = null;
			return false;
		}
		if (targetClan.getAllyPenaltyExpiryTime() > System.currentTimeMillis())
		{
			if (targetClan.getAllyPenaltyType() == PENALTY_TYPE_CLAN_LEAVED)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANT_ENTER_ALLIANCE_WITHIN_1_DAY);
				sm.addString(target.getClan().getName());
				sm.addString(target.getClan().getAllyName());
				activeChar.sendPacket(sm);
				sm = null;
				return false;
			}
			if (targetClan.getAllyPenaltyType() == PENALTY_TYPE_CLAN_DISMISSED)
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.CANT_ENTER_ALLIANCE_WITHIN_1_DAY));
				return false;
			}
		}
        if (activeChar.isInsideZone(L2Character.ZONE_SIEGE) && target.isInsideZone(L2Character.ZONE_SIEGE))
        {
        	activeChar.sendPacket(new SystemMessage(SystemMessageId.OPPOSING_CLAN_IS_PARTICIPATING_IN_SIEGE));
            return false;
        }
        if (leaderClan.isAtWarWith(targetClan.getClanId()))
        {
        	activeChar.sendPacket(new SystemMessage(SystemMessageId.MAY_NOT_ALLY_CLAN_BATTLE));
            return false;
        }

		int numOfClansInAlly = 0;
		for (L2Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getAllyId() == activeChar.getAllyId())
			{
				++numOfClansInAlly;
			}
		}
		if (numOfClansInAlly >= Config.MAX_NUM_OF_CLANS_IN_ALLY)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_THE_LIMIT));
			return false;
		}

		return true;
    }

    public long getAllyPenaltyExpiryTime()
    {
    	return _allyPenaltyExpiryTime;
    }

    public int getAllyPenaltyType()
    {
    	return _allyPenaltyType;
    }

    public void setAllyPenaltyExpiryTime(long expiryTime, int penaltyType)
    {
    	_allyPenaltyExpiryTime = expiryTime;
    	_allyPenaltyType = penaltyType;
    }

    public long getCharPenaltyExpiryTime()
    {
    	return _charPenaltyExpiryTime;
    }

    public void setCharPenaltyExpiryTime(long time)
    {
    	_charPenaltyExpiryTime = time;
    }

    public long getDissolvingExpiryTime()
    {
    	return _dissolvingExpiryTime;
    }

    public void setDissolvingExpiryTime(long time)
    {
    	_dissolvingExpiryTime = time;
    }
    public void createAlly(L2PcInstance player, String allyName)
    {
    	if (null == player)
    		return;

        if (Config.DEBUG)
            _log.fine(player.getObjectId() + "(" + player.getName() + ") requested ally creation from ");

        if (!player.isClanLeader())
        {
            player.sendPacket(new SystemMessage(SystemMessageId.ONLY_CLAN_LEADER_CREATE_ALLIANCE));
            return;
        }
        if (getAllyId() != 0)
        {
            player.sendPacket(new SystemMessage(SystemMessageId.ALREADY_JOINED_ALLIANCE));
            return;
        }
        if (getLevel() < 5)
        {
            player.sendPacket(new SystemMessage(SystemMessageId.TO_CREATE_AN_ALLY_YOU_CLAN_MUST_BE_LEVEL_5_OR_HIGHER));
            return;
        }
		if (getAllyPenaltyExpiryTime() > System.currentTimeMillis())
		{
			if (getAllyPenaltyType() == L2Clan.PENALTY_TYPE_DISSOLVE_ALLY)
			{
				player.sendPacket(new SystemMessage(SystemMessageId.CANT_CREATE_ALLIANCE_10_DAYS_DISOLUTION));
				return;
			}
		}
        if (getDissolvingExpiryTime() > System.currentTimeMillis())
        {
            player.sendPacket(new SystemMessage(SystemMessageId.YOU_MAY_NOT_CREATE_ALLY_WHILE_DISSOLVING));
            return;
        }
        if (!Util.isAlphaNumeric(allyName))
        {
            player.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_ALLIANCE_NAME));
            return;
        }
        if (allyName.length() > 16 || allyName.length() < 2)
        {
            player.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_ALLIANCE_NAME_LENGTH));
            return;
        }
		if (!Config.CLAN_ALLY_NAME_PATTERN.matcher(allyName).matches())
		{
			player.sendPacket(SystemMessageId.INCORRECT_ALLIANCE_NAME);
			return;
		}
        if (ClanTable.getInstance().isAllyExists(allyName))
        {
            player.sendPacket(new SystemMessage(SystemMessageId.ALLIANCE_ALREADY_EXISTS));
            return;
        }

        setAllyId(getClanId());
        setAllyName(allyName.trim());
        setAllyPenaltyExpiryTime(0, 0);
        updateClanInDB();

        player.sendPacket(new UserInfo(player));
        player.rewardSkills();
       
        player.sendMessage("Alliance " + allyName + " has been created.");
    }

    public void dissolveAlly(L2PcInstance player)
    {
		if (getAllyId() == 0)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.NO_CURRENT_ALLIANCES));
			return;
		}
		if (!player.isClanLeader() || getClanId() != getAllyId())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.FEATURE_ONLY_FOR_ALLIANCE_LEADER));
			return;
		}
        if (player.isInsideZone(L2Character.ZONE_SIEGE))
        {
            player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_DISSOLVE_ALLY_WHILE_IN_SIEGE));
            return;
        }

		broadcastToOnlineAllyMembers(new SystemMessage(SystemMessageId.ALLIANCE_DISOLVED));

		long currentTime = System.currentTimeMillis();
		for (L2Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getAllyId() == getAllyId() && clan.getClanId() != getClanId())
			{
				clan.setAllyId(0);
				clan.setAllyName(null);
				clan.setAllyPenaltyExpiryTime(0, 0);
				clan.updateClanInDB();
			}
		}

		setAllyId(0);
        setAllyName(null);
        setAllyPenaltyExpiryTime(currentTime + Config.CREATE_ALLY_DAYS_WHEN_DISSOLVED * 86400000L,L2Clan.PENALTY_TYPE_DISSOLVE_ALLY); //24*60*60*1000 = 86400000
		updateClanInDB();

        // The clan leader should take the XP penalty of a full death.
        player.deathPenalty(false);
    }

    public void levelUpClan(L2PcInstance player)
    {
    	if (!player.isClanLeader())
    	{
    		player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
    		return;
    	}
    	if (System.currentTimeMillis() < getDissolvingExpiryTime())
    	{
    		player.sendPacket(new SystemMessage(SystemMessageId.CANNOT_RISE_LEVEL_WHILE_DISSOLUTION_IN_PROGRESS));
    		return;
    	}

    	boolean increaseClanLevel = false;

    	switch (getLevel())
   		{
   			case 0:
    		{
    			// upgrade to 1
    			if (player.getSp() >= 20000 && player.getAdena() >= 650000)
    			{
    				if (player.reduceAdena("ClanLvl", 650000, player.getTarget(), true))
    				{
    					player.setSp(player.getSp() - 20000);
						player.sendPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
						player.sendPacket(new SystemMessage(SystemMessageId.SP_DECREASED_S1).addNumber(20000));
						increaseClanLevel = true;
    				}
    			}
    			break;
    		}
   			case 1:
   			{
   				if(player.getSp() >= 100000 && player.getAdena() >= 2500000)
   				{
   					if(player.reduceAdena("ClanLvl", 2500000, player.getTarget(), true))
   					{
   						player.setSp(player.getSp() - 100000);
   						player.sendPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
   						player.sendPacket(new SystemMessage(SystemMessageId.SP_DECREASED_S1).addNumber(100000));
   						increaseClanLevel = true;
   					}
   				}
   				break;
   			}
   			case 2:
   			{
   				if(player.getSp() >= 350000 && player.getInventory().getItemByItemId(1419) != null)
   				{
   					if(player.destroyItemByItemId("ClanLvl", 1419, 1, player.getTarget(), false))
   					{
   						player.setSp(player.getSp() - 350000);
   						player.sendPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
   						player.sendPacket(new SystemMessage(SystemMessageId.SP_DECREASED_S1).addNumber(350000));
   						player.sendPacket(new SystemMessage(SystemMessageId.DISSAPEARED_ITEM).addItemName(1419).addNumber(1));
   						increaseClanLevel = true;
   					}
   				}
   				break;
   			}
   			case 3:
   			{
   				if(player.getSp() >= 1000000 && player.getInventory().getItemByItemId(3874) != null)
   				{
   					if(player.destroyItemByItemId("ClanLvl", 3874, 1, player.getTarget(), false))
   					{
   						player.setSp(player.getSp() - 1000000);
   						player.sendPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
   						player.sendPacket(new SystemMessage(SystemMessageId.SP_DECREASED_S1).addNumber(1000000));
   						player.sendPacket(new SystemMessage(SystemMessageId.DISSAPEARED_ITEM).addItemName(3874).addNumber(1));
   						increaseClanLevel = true;
   					}
   				}
   				break;
   			}
   			case 4:
   			{
   				if(player.getSp() >= 2500000 && player.getInventory().getItemByItemId(3870) != null)
   				{
   					if(player.destroyItemByItemId("ClanLvl", 3870, 1, player.getTarget(), false))
   					{
   						player.setSp(player.getSp() - 2500000);
   						player.sendPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
   						player.sendPacket(new SystemMessage(SystemMessageId.SP_DECREASED_S1).addNumber(2500000));
   						player.sendPacket(new SystemMessage(SystemMessageId.DISSAPEARED_ITEM).addItemName(3870).addNumber(1));
   						increaseClanLevel = true;
   					}
   				}
   				break;
   			}
   			case 5:
   			{
   				if(getReputationScore() >= 10000 && getMembersCount() >= 30)
   				{
   					setReputationScore(getReputationScore() - 10000, true);
   					player.sendPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
   					player.sendPacket(new SystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP).addNumber(10000));
   					increaseClanLevel = true;
   				}
   				break;
   			}
   			case 6:
   			{
   				if(getReputationScore() >= 20000 && getMembersCount() >= 80)
   				{
   					setReputationScore(getReputationScore() - 20000, true);
   					player.sendPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
   					player.sendPacket(new SystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP).addNumber(20000));
   					increaseClanLevel = true;
   				}
   				break;
   			}
   			case 7:
   			{
   				if (getReputationScore() >= 40000 && getMembersCount() >= Config.CLAN_LEVEL_8_MEMBERS)
   				{
   					setReputationScore(getReputationScore() - Config.CLAN_LEVEL_6_COST, true);
   					player.sendPacket(new MagicSkillUser(player, 5103, 1, 1000, 0));
   					player.sendPacket(new SystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP).addNumber(Config.CLAN_LEVEL_8_COST));
   					increaseClanLevel = true;
   				}
   				break;
   			}
   			default:
   				return;
   		}

    	if (!increaseClanLevel)
    	{
    		SystemMessage sm = new SystemMessage(SystemMessageId.FAILED_TO_INCREASE_CLAN_LEVEL);
    		player.sendPacket(sm);
    		return;
    	}

    	// the player should know that he has less sp now :p
    	StatusUpdate su = new StatusUpdate(player.getObjectId());
    	su.addAttribute(StatusUpdate.SP, player.getSp());
    	player.sendPacket(su);

    	ItemList il = new ItemList(player, false);
    	player.sendPacket(il);

    	changeLevel(getLevel() + 1);
    }

    public void changeLevel(int level)
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET clan_level = ? WHERE clan_id = ?");
            statement.setInt(1, level);
            statement.setInt(2, getClanId());
            statement.execute();
            statement.close();

            con.close();
        }
        catch (Exception e)
        {
            _log.warning("could not increase clan level:" + e);
        }
        finally
        {
	        try { con.close(); } catch (Exception e) {}
        }

        setLevel(level);

        if (getLeader().isOnline())
        {
        	L2PcInstance leader = getLeader().getPlayerInstance();
            if (level > 3)
            {
            	SiegeManager.getInstance().addSiegeSkills(leader);
            }
            else if (level < 4)
            {
            	SiegeManager.getInstance().removeSiegeSkills(leader);
            }
            if (level > 4)
            {
                leader.sendPacket(new SystemMessage(SystemMessageId.CLAN_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS));
            }
        }

        // notify all the members about it
        broadcastToOnlineMembers(new SystemMessage(SystemMessageId.CLAN_LEVEL_INCREASED));
        broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
        /*
         * Micht :
         * 	- use PledgeShowInfoUpdate instead of PledgeStatusChanged
         * 		to update clan level ingame
         * 	- remove broadcastClanStatus() to avoid members duplication
         */
        //clan.broadcastToOnlineMembers(new PledgeStatusChanged(clan));
        //clan.broadcastClanStatus();
    }
//    
//    public void setAllyCrest(int crestId)
//	{
//		Connection con = null;
//		try
//		{
//			setAllyCrestId(crestId);
//			con = L2DatabaseFactory.getInstance().getConnection();
//			PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET ally_crest_id = ? WHERE clan_id = ?");
//			statement.setInt(1, crestId);
//			statement.setInt(2, getClanId());
//			statement.executeUpdate();
//			statement.close();
//
//			statement = null;
//		}
//		catch(SQLException e)
//		{
//			_log.warning("could not update the ally crest id:" + e.getMessage());
//		}
//		finally
//		{
//			L2DatabaseFactory.close(con);
//			con = null;
//		}
//	}
//    
//	
	/**
	 * Change the clan crest. If crest id is 0, crest is removed. New crest id is saved to database.
	 * @param crestId if 0, crest is removed, else new crest id is set and saved to database
	 */
	public void changeClanCrest(int crestId)
	{
		if (crestId == 0)
			CrestCache.removeCrest(CrestType.PLEDGE, _crestId);
		
		_crestId = crestId;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET crest_id = ? WHERE clan_id = ?");
			statement.setInt(1, crestId);
			statement.setInt(2, _clanId);
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "Could not update crest for clan " + _name + " [" + _clanId + "] : " + e.getMessage(), e);
		}
		
		for (L2PcInstance member : getOnlineMembers(""))
			member.broadcastUserInfo();
	}
	
	/**
	 * Change the ally crest. If crest id is 0, crest is removed. New crest id is saved to database.
	 * @param crestId if 0, crest is removed, else new crest id is set and saved to database
	 * @param onlyThisClan Do it for the ally aswell.
	 */
	public void changeAllyCrest(int crestId, boolean onlyThisClan)
	{
		String sqlStatement = "UPDATE clan_data SET ally_crest_id = ? WHERE clan_id = ?";
		int allyId = _clanId;
		if (!onlyThisClan)
		{
			if (crestId == 0)
				CrestCache.removeCrest(CrestType.ALLY, _allyCrestId);
			
			sqlStatement = "UPDATE clan_data SET ally_crest_id = ? WHERE ally_id = ?";
			allyId = _allyId;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(sqlStatement);
			statement.setInt(1, crestId);
			statement.setInt(2, allyId);
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "Could not update ally crest for ally/clan id " + allyId + " : " + e.getMessage(), e);
		}
		
		if (onlyThisClan)
		{
			_allyCrestId = crestId;
			for (L2PcInstance member : getOnlineMembers(""))
				member.broadcastUserInfo();
		}
		else
		{
			for (L2Clan clan : ClanTable.getInstance().getClans())
			{
				if (clan.getAllyId() == _allyId)
				{
					clan.setAllyCrestId(crestId);
					for (L2PcInstance member : clan.getOnlineMembers(""))
						member.broadcastUserInfo();
				}
			}
		}
	}
	
	/**
	 * Change the large crest. If crest id is 0, crest is removed. New crest id is saved to database.
	 * @param crestId if 0, crest is removed, else new crest id is set and saved to database
	 */
	public void changeLargeCrest(int crestId)
	{
		if (crestId == 0)
			CrestCache.removeCrest(CrestType.PLEDGE_LARGE, _crestLargeId);
		
		_crestLargeId = crestId;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET crest_large_id = ? WHERE clan_id = ?");
			statement.setInt(1, crestId);
			statement.setInt(2, _clanId);
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "Could not update large crest for clan " + _name + " [" + _clanId + "] : " + e.getMessage(), e);
		}
		
		for (L2PcInstance member : getOnlineMembers(""))
			member.broadcastUserInfo();
	}
	
	private void checkCrests()
	{
		if (_crestId != 0)
		{
			if (CrestCache.getCrest(CrestType.PLEDGE, _crestId) == null)
			{
				_log.log(Level.INFO, "Removing non-existent crest for clan " + _name + " [" + _clanId + "], crestId:" + _crestId);
				changeClanCrest(0);
			}
		}
		
		if (_crestLargeId != 0)
		{
			if (CrestCache.getCrest(CrestType.PLEDGE_LARGE, _crestLargeId) == null)
			{
				_log.log(Level.INFO, "Removing non-existent large crest for clan " + _name + " [" + _clanId + "], crestLargeId:" + _crestLargeId);
				changeLargeCrest(0);
			}
		}
		
		if (_allyCrestId != 0)
		{
			if (CrestCache.getCrest(CrestType.ALLY, _allyCrestId) == null)
			{
				_log.log(Level.INFO, "Removing non-existent ally crest for clan " + _name + " [" + _clanId + "], allyCrestId:" + _allyCrestId);
				changeAllyCrest(0, true);
			}
		}
	}
}
