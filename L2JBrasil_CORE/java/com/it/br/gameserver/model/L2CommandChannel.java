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

import com.it.br.gameserver.model.actor.instance.L2GrandBossInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2RaidBossInstance;
import com.it.br.gameserver.network.serverpackets.ExCloseMPCC;
import com.it.br.gameserver.network.serverpackets.ExOpenMPCC;
import com.it.br.gameserver.network.serverpackets.L2GameServerPacket;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

/**
 *
 * @author  chris_00
 */
public class L2CommandChannel
{
	private List<L2Party> _partys = null;
	private L2PcInstance _commandLeader = null;
	private int _channelLvl;

	/**
	 * Creates a New Command Channel and Add the Leaders party to the CC
	 *
	 * @param leader
	 *
	 */
	public L2CommandChannel(L2PcInstance leader)
	{
		_commandLeader = leader;
		_partys = new ArrayList<>();
		_partys.add(leader.getParty());
		_channelLvl = leader.getParty().getLevel();
		leader.getParty().setCommandChannel(this);
		leader.getParty().broadcastToPartyMembers(new ExOpenMPCC());
	}

	/**
	 * Adds a Party to the Command Channel
	 * @param party
	 */
	public void addParty(L2Party party)
	{
		_partys.add(party);
		if (party.getLevel() > _channelLvl)
			_channelLvl = party.getLevel();
		party.setCommandChannel(this);
		party.broadcastToPartyMembers(new ExOpenMPCC());
	}

	/**
	 * Removes a Party from the Command Channel
	 * @param party
	 */
	public void removeParty(L2Party party)
	{
		_partys.remove(party);
		_channelLvl = 0;
		for (L2Party pty : _partys)
		{
			if (pty.getLevel() > _channelLvl)
				_channelLvl = pty.getLevel();
		}
		party.setCommandChannel(null);
		party.broadcastToPartyMembers(new ExCloseMPCC());
		if(_partys.size() < 2)
		{
			SystemMessage sm = SystemMessage.sendString("The Command Channel was disbanded.");
    		        broadcastToChannelMembers(sm);
			disbandChannel();
		}
	}

	/**
	 * disbands the whole Command Channel
	 */
	public void disbandChannel()
	{
		if (_partys != null)
		{
			for (L2Party party : _partys)
			{
				if(party != null)
					removeParty(party);
			}
		}
		_partys = null;
	}

	/**
	 * @return overall membercount of the Command Channel
	 */
	public int getMemberCount()
	{
		int count = 0;
		for (L2Party party : _partys)
		{
			if(party != null)
				count += party.getMemberCount();
		}
		return count;
	}

	/**
	 * Broadcast packet to every channelmember
	 * @param gsp
	 */
	public void broadcastToChannelMembers(L2GameServerPacket gsp)
	{
		if (!_partys.isEmpty())
		{
			for (L2Party party : _partys)
			{
				if(party != null)
					party.broadcastToPartyMembers(gsp);
			}
		}
	}


	/**
	 * @return list of Parties in Command Channel
	 */
	public List<L2Party> getPartys()
	{
		return _partys;
	}

	/**
	 * @return list of all Members in Command Channel
	 */
	public List<L2PcInstance> getMembers()
	{
		List<L2PcInstance> members = new ArrayList<>();
		for (L2Party party : getPartys())
		{
			members.addAll(party.getPartyMembers());
		}
		return members;
	}

	/**
	 *
	 * @return Level of CC
	 */
	public int getLevel() { return _channelLvl; }

	/**
	 * @param leader the leader of the Command Channel
	 */
	public void setChannelLeader(L2PcInstance leader)
	{
		_commandLeader = leader;
	}

	/**
	 * @return the leader of the Command Channel
	 */
	public L2PcInstance getChannelLeader()
	{
		return _commandLeader;
	}

	/**
	 * Queen Ant, Core, Orfen, Zaken: MemberCount > 36<br>
	 * Baium: MemberCount > 56<br>
	 * Antharas: MemberCount > 225<br>
	 * Valakas: MemberCount > 99<br>
	 * normal RaidBoss: MemberCount > 18
	 *
	 * @param obj
	 * @return true if proper condition for RaidWar
	 */
	public boolean meetRaidWarCondition(L2Object obj)
	{
		if (!(obj instanceof L2RaidBossInstance) || !(obj instanceof L2GrandBossInstance))
			return false;
		int npcId = ((L2Attackable)obj).getNpcId();
		switch(npcId)
		{
	    	case 29001: // Queen Ant
	    	case 29006: // Core
	    	case 29014: // Orfen
	    	case 29022: // Zaken
	    		return (getMemberCount() > 36);
	    	case 29020: // Baium
	    		return (getMemberCount() > 56);
	    	case 29019: // Antharas
	    		return (getMemberCount() > 225);
	    	case 29028: // Valakas
	    		return (getMemberCount() > 99);
                case 29045: // Frintezza
	    		return (getMemberCount() > 35);
	    	default: // normal Raidboss
	    		return (getMemberCount() > 18);
		}
	}
}
