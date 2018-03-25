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
package com.it.br.gameserver.model.actor.instance;

import com.it.br.Config;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.ai.CtrlIntention;
import com.it.br.gameserver.ai.L2CharacterAI;
import com.it.br.gameserver.ai.L2DoorAI;
import com.it.br.gameserver.instancemanager.CastleManager;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.actor.knownlist.DoorKnownList;
import com.it.br.gameserver.model.actor.stat.DoorStat;
import com.it.br.gameserver.model.actor.status.DoorStatus;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.model.entity.ClanHall;
import com.it.br.gameserver.network.L2GameClient;
import com.it.br.gameserver.network.serverpackets.*;
import com.it.br.gameserver.templates.L2CharTemplate;
import com.it.br.gameserver.templates.L2Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class L2DoorInstance extends L2Character
{
	protected static final Logger log = LoggerFactory.getLogger(L2DoorInstance.class);

	/** The castle index in the array of L2Castle this L2NpcInstance belongs to */
	private int _castleIndex = -2;
	private int _mapRegion = -1;

	// when door is closed, the dimensions are
	private int _rangeXMin = 0;
	private int _rangeYMin = 0;
	private int _rangeZMin = 0;
	private int _rangeXMax = 0;
	private int _rangeYMax = 0;
	private int _rangeZMax = 0;

	private int _A = 0;
	private int _B = 0;
	private int _C = 0;
	private int _D = 0;

	protected final int _doorId;
	protected final String _name;
	private boolean _open;
	private boolean _unlockable;

	private ClanHall _clanHall;

	protected int _autoActionDelay = -1;
	private ScheduledFuture<?> _autoActionTask;

	@Override
	public L2CharacterAI getAI()
	{
		if(_ai == null)
		{
			synchronized (this)
			{
				if(_ai == null)
				{
					_ai = new L2DoorAI(new AIAccessor());
				}
			}
		}
		return _ai;
	}

	@Override
	public boolean hasAI()
	{
		return _ai != null;
	}

	class CloseTask implements Runnable
	{
	
		public void run()
		{
			try
			{
				onClose();
			}
			catch(Throwable e)
			{
				log.error( "", e);
			}
		}
	}

	class AutoOpenClose implements Runnable
	{
	
		public void run()
		{
			try
			{
				String doorAction;

				if(!getOpen())
				{
					doorAction = "opened";
					openMe();
				}
				else
				{
					doorAction = "closed";
					closeMe();
				}

				if(Config.DEBUG)
				{
					log.info("Auto " + doorAction + " door ID " + _doorId + " (" + _name + ") for " + _autoActionDelay / 60000 + " minute(s).");
				}
			}
			catch(Exception e)
			{
				log.warn("Could not auto open/close door ID " + _doorId + " (" + _name + ")");
			}
		}
	}

	public L2DoorInstance(int objectId, L2CharTemplate template, int doorId, String name, boolean unlockable)
	{
		super(objectId, template);
		getKnownList(); // init knownlist
		getStat(); // init stats
		getStatus(); // init status
		_doorId = doorId;
		_name = name;
		_unlockable = unlockable;
	}

	@Override
	public final DoorKnownList getKnownList()
	{
		if(super.getKnownList() == null || !(super.getKnownList() instanceof DoorKnownList))
		{
			setKnownList(new DoorKnownList(this));
		}

		return (DoorKnownList) super.getKnownList();
	}

	@Override
	public final DoorStat getStat()
	{
		if(super.getStat() == null || !(super.getStat() instanceof DoorStat))
		{
			setStat(new DoorStat(this));
		}

		return (DoorStat) super.getStat();
	}

	@Override
	public final DoorStatus getStatus()
	{
		if(super.getStatus() == null || !(super.getStatus() instanceof DoorStatus))
		{
			setStatus(new DoorStatus(this));
		}

		return (DoorStatus) super.getStatus();
	}

	public final boolean isUnlockable()
	{
		return _unlockable;
	}

	@Override
	public final int getLevel()
	{
		return 1;
	}

	public int getDoorId()
	{
		return _doorId;
	}

	public boolean getOpen()
	{
		return _open;
	}

	public void setOpen(boolean open)
	{
		_open = open;
	}

	/**
	 * Sets the delay in milliseconds for automatic opening/closing of this door instance. <BR>
	 * <B>Note:</B> A value of -1 cancels the auto open/close task.
	 * 
	 * @param actionDelay
	 */
	public void setAutoActionDelay(int actionDelay)
	{
		if(_autoActionDelay == actionDelay)
			return;

		if(actionDelay > -1)
		{
			AutoOpenClose ao = new AutoOpenClose();
			ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(ao, actionDelay, actionDelay);
			ao = null;
		}
		else
		{
			if(_autoActionTask != null)
			{
				_autoActionTask.cancel(false);
			}
		}

		_autoActionDelay = actionDelay;
	}

	public int getDamage()
	{
		int dmg = 6 - (int) Math.ceil(getCurrentHp() / getMaxHp() * 6);
		if(dmg > 6)
			return 6;
		if(dmg < 0)
			return 0;
		return dmg;
	}

	public final Castle getCastle()
	{
		if(_castleIndex < 0)
		{
			_castleIndex = CastleManager.getInstance().getCastleIndex(this);
		}

		if(_castleIndex < 0)
			return null;

		return CastleManager.getInstance().getCastles().get(_castleIndex);
	}

	public void setClanHall(ClanHall clanhall)
	{
		_clanHall = clanhall;
	}

	public ClanHall getClanHall()
	{
		return _clanHall;
	}

	public boolean isEnemyOf(L2Character cha)
	{
		return true;
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		if(isUnlockable())
			return true;

		// Doors can`t be attacked by NPCs
		if(attacker == null || !(attacker instanceof L2PlayableInstance))
			return false;

		L2PcInstance activePlayer;
		if(attacker instanceof L2Summon)
			activePlayer = ((L2Summon)attacker).getOwner();
		else
			activePlayer = (L2PcInstance)attacker;
		// Attackable during siege by attacker only
		boolean isCastle = getCastle() != null && getCastle().getCastleId() > 0 && getCastle().getSiege().getIsInProgress() && getCastle().getSiege().checkIsAttacker(activePlayer.getClan());

		if(isCastle)
		{
			if(attacker instanceof L2SummonInstance)
			{
				L2Clan clan = ((L2SummonInstance) attacker).getOwner().getClan();
				if(clan != null && clan.getClanId() == getCastle().getOwnerId())
				{
					clan = null;
					return false;
				}
			}
			else if(attacker instanceof L2PcInstance)
			{
				L2Clan clan = ((L2PcInstance) attacker).getClan();
				if(clan != null && clan.getClanId() == getCastle().getOwnerId())
				{
					clan = null;
					return false;
				}
			}
		}
		return isCastle;
	}

	public boolean isAttackable(L2Character attacker)
	{
		return isAutoAttackable(attacker);
	}

	@Override
	public void updateAbnormalEffect()
	{}

	public int getDistanceToWatchObject(L2Object object)
	{
		if(!(object instanceof L2PcInstance))
			return 0;
		return 2000;
	}

	/**
	 * Return the distance after which the object must be remove from _knownObject according to the type of the object.<BR>
	 * <BR>
	 * <B><U> Values </U> :</B><BR>
	 * <BR>
	 * <li>object is a L2PcInstance : 4000</li> <li>object is not a L2PcInstance : 0</li><BR>
	 * <BR>
	 */
	public int getDistanceToForgetObject(L2Object object)
	{
		if(!(object instanceof L2PcInstance))
			return 0;

		return 4000;
	}

	@Override
	public L2ItemInstance getActiveWeaponInstance()
	{
		return null;
	}

	@Override
	public L2Weapon getActiveWeaponItem()
	{
		return null;
	}

	@Override
	public L2ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}

	@Override
	public L2Weapon getSecondaryWeaponItem()
	{
		return null;
	}

	@Override
	public void onAction(L2PcInstance player)
	{
		if(player == null)
			return;

		// Check if the L2PcInstance already target the L2NpcInstance
		if(this != player.getTarget())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(this);

			// Send a Server->Client packet MyTargetSelected to the L2PcInstance player
			MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
			player.sendPacket(my);
			my = null;

			DoorStatusUpdate su = new DoorStatusUpdate(this);
			player.sendPacket(su);
			su = null;

			// Send a Server->Client packet ValidateLocation to correct the L2NpcInstance position and heading on the client
			player.sendPacket(new ValidateLocation(this));
		}
		else
		{
			if(isAutoAttackable(player))
			{
				if(Math.abs(player.getZ() - getZ()) < 400) // this max heigth difference might need some tweaking
				{
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
				}
			}
			else if(player.getClan() != null && getClanHall() != null && player.getClanId() == getClanHall().getOwnerId())
			{
				if(!isInsideRadius(player, L2NpcInstance.INTERACTION_DISTANCE, false, false))
				{
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
				}
				else
				{
					//need find serverpacket which ask open/close gate. now auto
					if(!getOpen())
					{
						openMe();
					}
					else
					{
						closeMe();
					}
				}
			}
		}
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}

    @Override
	public void onActionShift(L2GameClient client)
    {
        L2PcInstance player = client.getActiveChar();
        if (player == null) return;

        if (player.getAccessLevel() >= Config.GM_ACCESSLEVEL)
        {
            player.setTarget(this);
            MyTargetSelected my = new MyTargetSelected(getObjectId(), player
                    .getLevel());
            player.sendPacket(my);

            if (isAutoAttackable(player)) 
            {
                DoorStatusUpdate su = new DoorStatusUpdate(this);
                player.sendPacket(su);
            }

            NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            StringBuilder html1 = new StringBuilder("<html><body><table border=0>");
            html1.append("<tr><td>S.Y.L. Says:</td></tr>");
            html1.append("<tr><td>Current HP  "+getCurrentHp()+ "</td></tr>");
            html1.append("<tr><td>Max HP "+getMaxHp()+"</td></tr>");

            html1.append("<tr><td>Object ID: " + getObjectId() + "</td></tr>");
            html1.append("<tr><td>Door ID:<br>"+getDoorId()+"</td></tr>");
            html1.append("<tr><td><br></td></tr>");

            html1.append("<tr><td>Class: " + getClass().getName() + "</td></tr>");
            html1.append("<tr><td><br></td></tr>");
            html1.append("</table>");

            html1.append("<table><tr>");
            html1.append("<td><button value=\"Open\" action=\"bypass -h admin_open "+getDoorId()+"\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            html1.append("<td><button value=\"Close\" action=\"bypass -h admin_close "+getDoorId()+"\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            html1.append("<td><button value=\"Kill\" action=\"bypass -h admin_kill\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            html1.append("<td><button value=\"Delete\" action=\"bypass -h admin_delete\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            html1.append("</tr></table></body></html>");

            html.setHtml(html1.toString());
            player.sendPacket(html);
        }
        else {}

        player.sendPacket(new ActionFailed());
    }

	@Override
	public void broadcastStatusUpdate()
	{
		Collection<L2PcInstance> knownPlayers = getKnownList().getKnownPlayers().values();

		if(knownPlayers == null || knownPlayers.isEmpty())
			return;

		DoorStatusUpdate su = new DoorStatusUpdate(this);

		for(L2PcInstance player : knownPlayers)
		{
			player.sendPacket(su);
		}
	}

	public void onOpen()
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new CloseTask(), 60000);
	}

	public void onClose()
	{
		closeMe();
	}

	public final void closeMe()
	{
		synchronized (this)
		{
			if(!getOpen())
				return;

			setOpen(false);
		}

		broadcastStatusUpdate();
	}

	public final void openMe()
	{
		synchronized (this)
		{
			if(getOpen())
				return;
			setOpen(true);
		}

		broadcastStatusUpdate();
	}


	@Override
	public String toString()
	{
		return "door " + _doorId;
	}

	public String getDoorName()
	{
		return _name;
	}

	public int getXMin()
	{
		return _rangeXMin;
	}

	public int getYMin()
	{
		return _rangeYMin;
	}

	public int getZMin()
	{
		return _rangeZMin;
	}

	public int getXMax()
	{
		return _rangeXMax;
	}

	public int getYMax()
	{
		return _rangeYMax;
	}

	public int getZMax()
	{
		return _rangeZMax;
	}

	public void setRange(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax)
	{
		_rangeXMin = xMin;
		_rangeYMin = yMin;
		_rangeZMin = zMin;

		_rangeXMax = xMax;
		_rangeYMax = yMax;
		_rangeZMax = zMax;

		_A = _rangeYMax * (_rangeZMax - _rangeZMin) + _rangeYMin * (_rangeZMin - _rangeZMax);
		_B = _rangeZMin * (_rangeXMax - _rangeXMin) + _rangeZMax * (_rangeXMin - _rangeXMax);
		_C = _rangeXMin * (_rangeYMax - _rangeYMin) + _rangeXMin * (_rangeYMin - _rangeYMax);
		_D = -1	* (_rangeXMin * (_rangeYMax * _rangeZMax - _rangeYMin * _rangeZMax) + _rangeXMax * (_rangeYMin * _rangeZMin - _rangeYMin * _rangeZMax)
				+ _rangeXMin * (_rangeYMin * _rangeZMax - _rangeYMax * _rangeZMin));
	}

	public int getA()
	{
		return _A;
	}

	public int getB()
	{
		return _B;
	}

	public int getC()
	{
		return _C;
	}

	public int getD()
	{
		return _D;
	}

	public int getMapRegion()
	{
		return _mapRegion;
	}

	public void setMapRegion(int region)
	{
		_mapRegion = region;
	}

	public Collection<L2SiegeGuardInstance> getKnownSiegeGuards()
	{
		List<L2SiegeGuardInstance> result = new ArrayList<>();

		for(L2Object obj : getKnownList().getKnownObjects().values())
		{
			if(obj instanceof L2SiegeGuardInstance)
			{
				result.add((L2SiegeGuardInstance) obj);
			}
		}

		return result;
	}

	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake)
	{
		if(this.isAutoAttackable(attacker) || (attacker instanceof L2PcInstance && ((L2PcInstance) attacker).isGM()))
		{
			super.reduceCurrentHp(damage, attacker, awake);
		}
		else
		{
			super.reduceCurrentHp(0, attacker, awake);
		}
	}

	@Override
	public boolean doDie(L2Character killer)
	{
		if(!super.doDie(killer))
			return false;

		return true;
	}
}