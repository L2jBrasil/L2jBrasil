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
package com.it.br.gameserver.model.Olympiad;

import java.util.Arrays;
import java.util.Map;

import com.it.br.Config;
import com.it.br.gameserver.datatables.HeroSkillTable;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.instancemanager.OlympiadStadiaManager;
import com.it.br.gameserver.model.Inventory;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Party;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.model.L2Summon;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.CharInfo;
import com.it.br.gameserver.network.serverpackets.EtcStatusUpdate;
import com.it.br.gameserver.network.serverpackets.ExAutoSoulShot;
import com.it.br.gameserver.network.serverpackets.ExOlympiadMode;
import com.it.br.gameserver.network.serverpackets.ExShowScreenMessage;
import com.it.br.gameserver.network.serverpackets.InventoryUpdate;
import com.it.br.gameserver.network.serverpackets.ItemList;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.network.serverpackets.UserInfo;
import com.it.br.gameserver.templates.StatsSet;
import com.it.br.util.L2FastList;

class OlympiadGame extends Olympiad
{
	protected COMP_TYPE _type;
	public boolean _aborted;
	public boolean _gamestarted;
	public boolean _playerOneDisconnected;
	public boolean _playerTwoDisconnected;
	public String _playerOneName;
	public String _playerTwoName;
	public int _playerOneID = 0;
	public int _playerTwoID = 0;

	public L2PcInstance _playerOne;
	public L2PcInstance _playerTwo;
	//public L2Spawn _spawnOne;
	//public L2Spawn _spawnTwo;
	private L2FastList<L2PcInstance> _players;
	private int[] _stadiumPort;
	private int x1, y1, z1, x2, y2, z2;
	public int _stadiumID;
	public L2FastList<L2PcInstance> _spectators;
	private SystemMessage _sm;
	private SystemMessage _sm2;
	private SystemMessage _sm3;

	protected OlympiadGame(int id, COMP_TYPE type, L2FastList<L2PcInstance> list, int[] stadiumPort)
	{
		_aborted = false;
		_gamestarted = false;
		_stadiumID = id;
		_playerOneDisconnected = false;
		_playerTwoDisconnected = false;
		_type = type;
		_stadiumPort = stadiumPort;
		_spectators = new L2FastList<L2PcInstance>();

		if(list != null)
		{
			_players = list;
			_playerOne = list.get(0);
			_playerTwo = list.get(1);

			try
			{
				_playerOneName = _playerOne.getName();
				_playerTwoName = _playerTwo.getName();
				_playerOne.setOlympiadGameId(id);
				_playerTwo.setOlympiadGameId(id);
				_playerOneID = _playerOne.getObjectId();
				_playerTwoID = _playerTwo.getObjectId();
				if (!Config.ALT_OLY_SAME_IP)
    			{
    				String _playerOneIp = _playerOne.getClient().getConnection().getInetAddress().getHostAddress();
    				String _playerTwoIp = _playerTwo.getClient().getConnection().getInetAddress().getHostAddress();
    				if (_playerOneIp.equals(_playerTwoIp))
    				{
    					switch (_type)
    					{
    						case CLASSED:
							break;
    					}
    					_playerOne.sendMessage("Match aborted due to same ip-address of your enemy.");
    					_playerTwo.sendMessage("Match aborted due to same ip-address of your enemy.");
    					_aborted = true;
    					clearPlayers();
    				}
    			}
			}
			catch(Exception e)
			{
				_aborted = true;
				clearPlayers();
			}
			if(Config.DEBUG){
			_log.info("Olympiad System: Game - " + id + ": " + _playerOne.getName() + " Vs " + _playerTwo.getName());}
		}
		else
		{
			_aborted = true;
			clearPlayers();
			return;
		}
	}

	/*public boolean isAborted()
	{
		return _aborted;
	}*/

	protected void clearPlayers()
	{
		_playerOne = null;
		_playerTwo = null;
		_players = null;
		_playerOneName = "";
		_playerTwoName = "";
		_playerOneID = 0;
		_playerTwoID = 0;
	}

	protected void handleDisconnect(L2PcInstance player)
	{
		if(player == _playerOne)
		{
			_playerOneDisconnected = true;
		}
		else if(player == _playerTwo)
		{
			_playerTwoDisconnected = true;
		}
	}

	protected void removals()
	{
		if(_aborted)
			return;

		if(_playerOne == null || _playerTwo == null)
			return;
		if(_playerOneDisconnected || _playerTwoDisconnected)
			return;

		if(_playerOne.isDead())
			_playerOne.doRevive();
		if(_playerTwo.isDead())
			_playerTwo.doRevive();

		for(L2PcInstance player : _players)
		{
			try
			{
				if (Config.OLY_SKILL_PROTECT)
				{
					for(L2Skill skill : player.getAllSkills())
					{
						if (Config.OLY_SKILL_LIST.contains(skill.getId()))
							player.disableSkill(skill.getId());
						player.sendPacket(new ExShowScreenMessage ("This skill can not be used", 4000));
					}
				}
				
				//Remove Clan Skills
				if(player.getClan() != null)
				{
					for(L2Skill skill : player.getClan().getAllSkills())
						player.removeSkill(skill, false);
				}
				
				//Abort casting if player casting  
				if(player.isCastingNow())
					player.abortCast();

				//disable hero skills
				if(player.isHero())
				{					
					for(L2Skill actual: HeroSkillTable.GetHeroSkills())					
						player.disableSkill(actual.getId());									
				}
				
				// Heal Player fully
				player.setCurrentCp(player.getMaxCp());
				player.setCurrentHp(player.getMaxHp());
				player.setCurrentMp(player.getMaxMp());

				// Remove Buffs
				player.stopAllEffects();

				// Like L2OFF buffs on summon are removed
				// C4 patch note: All buffs on a summon are removed before the start of a match.
				if(player.getPet() != null)
				{
					L2Summon summon = player.getPet();
					summon.stopAllEffects();

					if(summon instanceof L2PetInstance)
						summon.unSummon(player);
				}
			
				// C4 patch note: Summons/Cubics do not disappear but are moved to the tournament arena together with their masters.

				//Remove Tamed Beast
				if(player.getTrainedBeast() != null)
					player.getTrainedBeast().doDespawn();

				//Remove player from his party
				if(player.getParty() != null)
				{
					L2Party party = player.getParty();
					party.removePartyMember(player);
				}

				L2ItemInstance wpn;

				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LRHAND) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LRHAND);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_UNDER) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_UNDER);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_BACK) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_BACK);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FACE) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FACE);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HAIR) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HAIR);
					checkWeaponArmor(player, wpn);
				}
				if(player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_DHAIR) != null)
				{
					wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_DHAIR);
					checkWeaponArmor(player, wpn);
				}

				//Remove shot automation
				Map<Integer, Integer> activeSoulShots = player.getAutoSoulShot();
				for(int itemId : activeSoulShots.values())
				{
					player.removeAutoSoulShot(itemId);
					ExAutoSoulShot atk = new ExAutoSoulShot(itemId, 0);
					player.sendPacket(atk);
				}
				
				// Skill recharge is a Gracia Final feature, but we have it configurable ;)
				if (Config.ALT_OLY_RECHARGE_SKILLS)
				{
					for (L2Skill skill : player.getAllSkills())
						if(skill.getId() != 1324)
							player.enableSkill(skill.getId());
					
					player.updateEffectIcons();
				}
				
				player.sendSkillList();				
				player.setPvpFlag(0);
			}
			catch(Exception e)
			{
				if(Config.DEBUG)
					e.printStackTrace();
			}
		}
	}

	protected boolean portPlayersToArena()
	{
		boolean _playerOneCrash = _playerOne == null || _playerOneDisconnected;
		boolean _playerTwoCrash = _playerTwo == null || _playerTwoDisconnected;

		if(_playerOneCrash || _playerTwoCrash || _aborted)
		{
			_playerOne = null;
			_playerTwo = null;
			_aborted = true;
			return false;
		}
		
		if(_playerOne.inObserverMode() || _playerTwo.inObserverMode()){
			
			_playerOne = null;
			_playerTwo = null;
			_aborted = true;
			return false;
			
		}

		try
		{
			x1 = _playerOne.getX();
			y1 = _playerOne.getY();
			z1 = _playerOne.getZ();

			x2 = _playerTwo.getX();
			y2 = _playerTwo.getY();
			z2 = _playerTwo.getZ();

			OlympiadStadiaManager.getInstance().getStadiumByLoc(_stadiumPort[0], _stadiumPort[1], _stadiumPort[2]).oustAllPlayers();

			// Check if player one is in private store or sitting
			if(_playerOne.getPrivateStoreType() == 1 || _playerOne.getPrivateStoreType() == 8 || _playerOne.getPrivateStoreType() == 3 || _playerOne.getPrivateStoreType() == 5 ||_playerOne.isSitting())
			{
				_playerOne.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
				_playerOne.broadcastUserInfo();
				_playerOne.standUp();
			}

			// Check if player two is in private store or sitting
			if(_playerTwo.getPrivateStoreType() == 1 || _playerTwo.getPrivateStoreType() == 8 || _playerTwo.getPrivateStoreType() == 3 || _playerTwo.getPrivateStoreType() == 5 || _playerTwo.isSitting())
			{
				_playerTwo.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
				_playerTwo.broadcastUserInfo();
				_playerTwo.standUp();
			}

			_playerOne.setTarget(null);
			_playerTwo.setTarget(null);

			_playerOne.sendPacket(new ExOlympiadMode(2));
			_playerTwo.sendPacket(new ExOlympiadMode(2));

			// Players are teleporting to Arena but not at the center
			_playerOne.teleToLocation(_stadiumPort[0] + 900, _stadiumPort[1], _stadiumPort[2], true);
			_playerOne.setOlympiadPosition(_stadiumPort);
			
			_playerTwo.teleToLocation(_stadiumPort[0] - 900, _stadiumPort[1], _stadiumPort[2], true);
			_playerTwo.setOlympiadPosition(_stadiumPort);
			
			_playerOne.setIsInOlympiadMode(true);
			_playerOne.setIsOlympiadStart(false);
			_playerOne.setOlympiadSide(1);

			_playerTwo.setIsInOlympiadMode(true);
			_playerTwo.setIsOlympiadStart(false);
			_playerTwo.setOlympiadSide(2);

			_gamestarted = true;
		}
		catch(NullPointerException e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
			
			return false;
		}
		return true;
	}

	protected void sendMessageToPlayers(boolean toBattleBegin, int nsecond)
	{
		if(!toBattleBegin)
		{
			_sm = new SystemMessage(SystemMessageId.YOU_WILL_ENTER_THE_OLYMPIAD_STADIUM_IN_S1_SECOND_S);
		}
		else
		{
			_sm = new SystemMessage(SystemMessageId.THE_GAME_WILL_START_IN_S1_SECOND_S);
		}

		_sm.addNumber(nsecond);
		try
		{
			for(L2PcInstance player : _players)
			{
				player.sendPacket(_sm);
			}
		}
		catch(Exception e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
		}
	}

	protected void portPlayersBack()
	{
		if(_playerOne != null)
		{
			if(x1 != 0 && y1 != 0 && z1 != 0)
			{
				_playerOne.teleToLocation(x1, y1, z1, true);
			}
		}
		else
		{
			_log.info("OlympiadPlayersBack: _playerOne is null!!!");
		}

		if(_playerTwo != null)
		{
			if(x2 != 0 && y2 != 0 && z2 != 0)
			{
				_playerTwo.teleToLocation(x2, y2, z2, true);
			}
		}
		else
		{
			_log.info("OlympiadPlayersBack: _playerTwo is null!!!");
		}
	}

	protected void PlayersStatusBack()
	{
		for(L2PcInstance player : _players)
		{
			try
			{
				if (Config.OLY_SKILL_PROTECT)
				{
					for(L2Skill skill : player.getAllSkills())
					{
						if (Config.OLY_SKILL_LIST.contains(skill.getId()))
							player.enableSkill(skill.getId());
						player.updateEffectIcons();
						player.sendPacket(new ExShowScreenMessage ("His skill can be used normally", 5000));
					}
				}
				
				player.setIsInOlympiadMode(false);
				player.setIsOlympiadStart(false);
				player.setOlympiadSide(-1);
				player.setOlympiadGameId(-1);
				player.sendPacket(new ExOlympiadMode(0));
				player.getStatus().startHpMpRegeneration();
				player.setCurrentCp(player.getMaxCp());
				player.setCurrentHp(player.getMaxHp());
				player.setCurrentMp(player.getMaxMp());

				//Add Clan Skills
				if(player.getClan() != null)
				{
					for(L2Skill skill : player.getClan().getAllSkills())
					{
						if(skill.getMinPledgeClass() <= player.getPledgeClass())
						{
							player.addSkill(skill, false);
						}
					}
				}

				//enable hero skills
				if(player.isHero()){
					
					for(L2Skill actual: HeroSkillTable.GetHeroSkills())
					{
						player.enableSkill(actual.getId());
					}
				}
				player.broadcastStatusUpdate();
			}
			catch(Exception e)
			{
				if(Config.DEBUG)
					e.printStackTrace();
			}
		}
	}

	protected boolean haveWinner()
	{
		boolean retval = false;
		if(_aborted || _playerOne == null || _playerTwo == null)
			return true;

		double playerOneHp = 0;

		try
		{
			if(_playerOne != null && _playerOne.getOlympiadGameId() != -1)
			{
				playerOneHp = _playerOne.getCurrentHp();
			}
		}
		catch(Exception e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
			
			playerOneHp = 0;
		}

		double playerTwoHp = 0;
		try
		{
			if(_playerTwo != null && _playerTwo.getOlympiadGameId() != -1)
			{
				playerTwoHp = _playerTwo.getCurrentHp();
			}
		}
		catch(Exception e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
			
			playerTwoHp = 0;
		}

		if(playerTwoHp == 0 || playerOneHp == 0)
		{
			if(_playerOne.getPet() != null)
			{
				L2Summon summon = _playerOne.getPet();
				summon.stopAllEffects();
				summon.unSummon(_playerOne);
			}
			if(_playerTwo.getPet() != null)
			{
				L2Summon summon = _playerTwo.getPet();
				summon.stopAllEffects();
				summon.unSummon(_playerTwo);
			}
			return true;
		}
		return retval;
	}

	protected void validateWinner()
	{
		if(_aborted || _playerOne == null || _playerTwo == null || _playerOneDisconnected || _playerTwoDisconnected)
			return;

		StatsSet playerOneStat;
		StatsSet playerTwoStat;

		playerOneStat = _nobles.get(_playerOneID);
		playerTwoStat = _nobles.get(_playerTwoID);

		int _div;
		int _gpreward;

		int playerOnePlayed = playerOneStat.getInteger(COMP_DONE);
		int playerTwoPlayed = playerTwoStat.getInteger(COMP_DONE);

		int playerOnePoints = playerOneStat.getInteger(POINTS);
		int playerTwoPoints = playerTwoStat.getInteger(POINTS);

		double playerOneHp = 0;
		try
		{
			if(_playerOne != null && !_playerOneDisconnected)
			{
				if(!_playerOne.isDead())
				{
					playerOneHp = _playerOne.getCurrentHp() + _playerOne.getCurrentCp();
				}
			}
		}
		catch(Exception e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
			
			playerOneHp = 0;
		}

		double playerTwoHp = 0;
		try
		{
			if(_playerTwo != null && !_playerTwoDisconnected)
			{
				if(!_playerTwo.isDead())
				{
					playerTwoHp = _playerTwo.getCurrentHp() + _playerTwo.getCurrentCp();
				}
			}
		}
		catch(Exception e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
			
			playerTwoHp = 0;
		}

		_sm = new SystemMessage(SystemMessageId.S1_HAS_WON_THE_GAME);
		_sm2 = new SystemMessage(SystemMessageId.S1_HAS_GAINED_S2_OLYMPIAD_POINTS);
		_sm3 = new SystemMessage(SystemMessageId.S1_HAS_LOST_S2_OLYMPIAD_POINTS);

		String result = "";

		// if players crashed, search if they've relogged
		L2PcInstance new_one = L2World.getInstance().getPlayer(_playerOneName);
		if(new_one!=null)
			_playerOne = new_one;
		_players.set(0, _playerOne);
		
		L2PcInstance new_two = L2World.getInstance().getPlayer(_playerTwoName);
		if(new_two!=null)
			_playerTwo = new_two;
		_players.set(1, _playerTwo);
		
		switch(_type)
		{
			case NON_CLASSED:
				_div = 5;
				_gpreward = Config.ALT_OLY_NONCLASSED_RITEM_C;
				break;
			default:
				_div = 3;
				_gpreward = Config.ALT_OLY_CLASSED_RITEM_C;
				break;
		}

		if(_playerOne !=null && (_playerTwo == null) || ((_playerTwo!=null && _playerTwo.isOnline() == 0 && _playerOne.isOnline() == 1) || (playerTwoHp == 0 && playerOneHp != 0) || (_playerTwo!=null && _playerOne.dmgDealt > _playerTwo.dmgDealt && playerTwoHp != 0 && playerOneHp != 0)))
		{
			int pointDiff;
			if(playerOnePoints > playerTwoPoints)
			{
				pointDiff = playerTwoPoints / _div;
			}
			else
			{
				pointDiff = playerOnePoints / _div;
			}
			playerOneStat.set(POINTS, playerOnePoints + pointDiff);
			playerTwoStat.set(POINTS, playerTwoPoints - pointDiff);

			_sm.addString(_playerOneName);
			broadcastMessage(_sm, true);
			_sm2.addString(_playerOneName);
			_sm2.addNumber(pointDiff);
			broadcastMessage(_sm2, true);
			_sm3.addString(_playerTwoName);
			_sm3.addNumber(pointDiff);
			broadcastMessage(_sm3, true);

			try
			{
				result = " (" + playerOneHp + "hp vs " + playerTwoHp + "hp - " + _playerOne.dmgDealt + "dmg vs " + _playerTwo.dmgDealt + "dmg) " + _playerOneName + " win " + pointDiff + " points";
				L2ItemInstance item = _playerOne.getInventory().addItem("Olympiad", Config.ALT_OLY_BATTLE_REWARD_ITEM, _gpreward, _playerOne, null);
				InventoryUpdate iu = new InventoryUpdate();
				iu.addModifiedItem(item);
				_playerOne.sendPacket(iu);

				SystemMessage sm = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
				sm.addItemName(item.getItemId());
				sm.addNumber(_gpreward);
				_playerOne.sendPacket(sm);
			}
			catch(Exception e)
			{
				if(Config.DEBUG)
					e.printStackTrace();
			}
		}
		else if(_playerTwo !=null && (_playerOne == null) || ((_playerOne!=null && _playerOne.isOnline() == 0 && _playerTwo.isOnline() == 1) || (playerOneHp == 0 && playerTwoHp != 0) || (_playerOne!=null && _playerTwo.dmgDealt > _playerOne.dmgDealt && playerOneHp != 0 && playerTwoHp != 0)))
		{
			int pointDiff;
			if(playerTwoPoints > playerOnePoints)
			{
				pointDiff = playerOnePoints / _div;
			}
			else
			{
				pointDiff = playerTwoPoints / _div;
			}
			playerTwoStat.set(POINTS, playerTwoPoints + pointDiff);
			playerOneStat.set(POINTS, playerOnePoints - pointDiff);

			_sm.addString(_playerTwoName);
			broadcastMessage(_sm, true);
			_sm2.addString(_playerTwoName);
			_sm2.addNumber(pointDiff);
			broadcastMessage(_sm2, true);
			_sm3.addString(_playerOneName);
			_sm3.addNumber(pointDiff);
			broadcastMessage(_sm3, true);

			try
			{
				result = " (" + playerOneHp + "hp vs " + playerTwoHp + "hp - " + _playerOne.dmgDealt + "dmg vs " + _playerTwo.dmgDealt + "dmg) " + _playerTwoName + " win " + pointDiff + " points";
				L2ItemInstance item = _playerTwo.getInventory().addItem("Olympiad", Config.ALT_OLY_BATTLE_REWARD_ITEM, _gpreward, _playerTwo, null);
				InventoryUpdate iu = new InventoryUpdate();
				iu.addModifiedItem(item);
				_playerTwo.sendPacket(iu);

				SystemMessage sm = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
				sm.addItemName(item.getItemId());
				sm.addNumber(_gpreward);
				_playerTwo.sendPacket(sm);
			}
			catch(Exception e)
			{
				if(Config.DEBUG)
					e.printStackTrace();
			}
		}
		else
		{
			result = " tie";
			_sm = new SystemMessage(SystemMessageId.THE_GAME_ENDED_IN_A_TIE);
			broadcastMessage(_sm, true);
		}
		if(Config.DEBUG){
		_log.info("Olympia Result: " + _playerOneName + " vs " + _playerTwoName + " ... " + result);}

		playerOneStat.set(COMP_DONE, playerOnePlayed + 1);
		playerTwoStat.set(COMP_DONE, playerTwoPlayed + 1);

		_nobles.remove(_playerOneID);
		_nobles.remove(_playerTwoID);

		_nobles.put(_playerOneID, playerOneStat);
		_nobles.put(_playerTwoID, playerTwoStat);

		for(int i = 20; i > 10; i -= 10)
		{
			_sm = new SystemMessage(SystemMessageId.YOU_WILL_GO_BACK_TO_THE_VILLAGE_IN_S1_SECOND_S);
			_sm.addNumber(10);
			broadcastMessage(_sm, false);
			try
			{
				Thread.sleep(5000);
			}
			catch(InterruptedException e)
			{
				if(Config.DEBUG)
					e.printStackTrace();
			}
		}
		for(int i = 5; i > 0; i--)
		{
			_sm = new SystemMessage(SystemMessageId.YOU_WILL_GO_BACK_TO_THE_VILLAGE_IN_S1_SECOND_S);
			_sm.addNumber(i);
			broadcastMessage(_sm, false);
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				if(Config.DEBUG)
					e.printStackTrace();
			}
		}
	}

	protected void additions()
	{
		for(L2PcInstance player : _players)
		{
			try
			{
				if (Config.OLY_SKILL_PROTECT)
				{
					for(L2Skill skill : player.getAllSkills())
					{
						if (Config.OLY_SKILL_LIST.contains(skill.getId()))
							player.enableSkill(skill.getId());
						player.updateEffectIcons();
						player.sendPacket(new ExShowScreenMessage ("You can use your skill", 3000));
					}
				}

				player.dmgDealt = 0;

				L2Skill skill;
				SystemMessage sm;

				skill = SkillTable.getInstance().getInfo(1204, 2);
				skill.getEffects(player, player);
				sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
				sm.addSkillName(1204);
				player.sendPacket(sm);

				if(!player.isMageClass())
				{
					skill = SkillTable.getInstance().getInfo(1086, 1);
					skill.getEffects(player, player);
					sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
					sm.addSkillName(1086);
					player.sendPacket(sm);
				}
				else
				{
					skill = SkillTable.getInstance().getInfo(1085, 1);
					skill.getEffects(player, player);
					sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
					sm.addSkillName(1085);
					player.sendPacket(sm);
				}
				player.setCurrentCp(player.getMaxCp());
				player.setCurrentHp(player.getMaxHp());
				player.setCurrentMp(player.getMaxMp());
			}
			catch(Exception e)
			{
				if(Config.DEBUG)
					e.printStackTrace();
			}
		}
	}

	/*protected boolean makePlayersVisible()
	{
		_sm = new SystemMessage(SystemMessageId.STARTS_THE_GAME);
		try
		{
	    	for (L2PcInstance player : _players)
	    	{
	    		player.getAppearance().setVisible();
	    		player.broadcastUserInfo();
	    		player.sendPacket(_sm);
	    		if (player.getPet() != null)
	    			player.getPet().updateAbnormalEffect();
	    	}
	    }
		catch (NullPointerException e)
		{
			_aborted = true;
			return false;
		}
	    return true;
	}*/

	protected boolean makeCompetitionStart()
	{
		if(_aborted)
			return false;

		_sm = new SystemMessage(SystemMessageId.STARTS_THE_GAME);
		broadcastMessage(_sm, true);
		try
		{
			
			for(L2PcInstance player : _players)
			{
				player.setIsOlympiadStart(true);
				if (Config.OLY_SKILL_PROTECT)
				{
					for(L2Skill skill : player.getAllSkills())
					{
						if (Config.OLY_SKILL_LIST.contains(skill.getId()))
							player.enableSkill(skill.getId());
						player.updateEffectIcons();
					}
				}

			}
		}
		catch(Exception e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
			
			_aborted = true;
			return false;
		}
		return true;
	}

	protected String getTitle()
	{
		String msg = "";
		msg += _playerOneName + " : " + _playerTwoName;
		return msg;
	}

	protected L2PcInstance[] getPlayers()
	{
		L2PcInstance[] players = new L2PcInstance[2];

		if(_playerOne == null || _playerTwo == null)
			return null;

		players[0] = _playerOne;
		players[1] = _playerTwo;

		return players;
	}

	protected L2FastList<L2PcInstance> getSpectators()
	{
		return _spectators;
	}

	protected void addSpectator(L2PcInstance spec)
	{
		_spectators.add(spec);
	}

	protected void removeSpectator(L2PcInstance spec)
	{
		if(_spectators != null && _spectators.contains(spec))
		{
			_spectators.remove(spec);
		}
	}

	/*protected void clearSpectators()
	{ 
	    if (_spectators != null)
	    {
	        for (L2PcInstance pc : _spectators)
	        {
	            try
				{
	            	if(!pc.inObserverMode())
						continue;
	            	pc.leaveOlympiadObserverMode();
	            }
				catch (NullPointerException e)
				{
					//null
				}
	        }
	        _spectators.clear();
	    }
	}*/

	private void broadcastMessage(SystemMessage sm, boolean toAll)
	{
		try
		{
			_playerOne.sendPacket(sm);
			_playerTwo.sendPacket(sm);
		}
		catch(Exception e)
		{
			if(Config.DEBUG)
				e.printStackTrace();
		}

		if(toAll && _spectators != null)
		{
			for(L2PcInstance spec : _spectators)
			{
				try
				{
					spec.sendPacket(sm);
				}
				catch(NullPointerException e)
				{
					if(Config.DEBUG)
						e.printStackTrace();
				}
			}
		}
	}

	private void checkWeaponArmor(L2PcInstance player, L2ItemInstance wpn)
	{
		if(wpn != null && (wpn.getItemId() >= 6611 && wpn.getItemId() <= 6621 || wpn.getItemId() == 6842) || wpn.isOlyRestrictedGradS() || wpn.isOlyRestrictedItem())
		{
			SystemMessage sm = null;
			
			if(wpn.getEnchantLevel() > 0)
			{
				sm = new SystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
				sm.addNumber(wpn.getEnchantLevel());
				sm.addItemName(wpn.getItemId());
			}
			else
			{
				sm = new SystemMessage(SystemMessageId.S1_DISARMED);
				sm.addItemName(wpn.getItemId());
			}

			player.sendPacket(sm);

			// Remove augementation bonus on unequipment
			if(wpn.isAugmented())
			{
				wpn.getAugmentation().removeBonus(player);
			}
			
			//items = activeChar.getInventory().unEquipItemInBodySlotAndRecord(bodyPart);
			
			L2ItemInstance[] unequiped = player.getInventory().unEquipItemInBodySlotAndRecord(wpn.getItem().getBodyPart());
			
			sm = null;

			player.refreshExpertisePenalty();
			
			/*
			if(wpn.getItem().getType2() == L2Item.TYPE2_WEAPON)
			{
				player.checkIfWeaponIsAllowed();
			}
			*/

			player.abortAttack();

			player.sendPacket(new EtcStatusUpdate(player));
			
			int location = wpn.getEquipSlot();

			switch(location)
			{
				case Inventory.PAPERDOLL_UNDER:
				case Inventory.PAPERDOLL_LEAR:
				case Inventory.PAPERDOLL_REAR:
				case Inventory.PAPERDOLL_NECK:
					player.sendPacket(new UserInfo(player));
					player.sendPacket(new CharInfo(player));
					player.sendPacket(new ItemList(player, true));
					break;
				case Inventory.PAPERDOLL_RFINGER:
				case Inventory.PAPERDOLL_LFINGER:
				case Inventory.PAPERDOLL_HAIR:
				case Inventory.PAPERDOLL_FACE:
				case Inventory.PAPERDOLL_DHAIR:
				case Inventory.PAPERDOLL_HEAD:
				case Inventory.PAPERDOLL_RHAND:
				case Inventory.PAPERDOLL_LHAND:
				case Inventory.PAPERDOLL_GLOVES:
				case Inventory.PAPERDOLL_CHEST:
				case Inventory.PAPERDOLL_LEGS:
				case Inventory.PAPERDOLL_BACK:
				case Inventory.PAPERDOLL_FEET:
				case Inventory.PAPERDOLL_LRHAND:
					player.broadcastUserInfo();
					break;
					
			}
			
			InventoryUpdate iu = new InventoryUpdate();
			iu.addItems(Arrays.asList(unequiped));
			player.sendPacket(iu);
			
			/*
			// if an "invisible" item has changed (Jewels, helmet),
			// we dont need to send broadcast packet to all other users
			if(!((wpn.getItem().getBodyPart() & L2Item.SLOT_HEAD) > 0 
					|| (wpn.getItem().getBodyPart() & L2Item.SLOT_NECK) > 0 
					|| (wpn.getItem().getBodyPart() & L2Item.SLOT_L_EAR) > 0 
					|| (wpn.getItem().getBodyPart() & L2Item.SLOT_R_EAR) > 0 
					|| (wpn.getItem().getBodyPart() & L2Item.SLOT_L_FINGER) > 0 
					|| (wpn.getItem().getBodyPart() & L2Item.SLOT_R_FINGER) > 0))
			{
				player.broadcastUserInfo();
				InventoryUpdate iu = new InventoryUpdate();
				iu.addItems(Arrays.asList(unequiped));
				player.sendPacket(iu);
			}
			else if((wpn.getItem().getBodyPart() & L2Item.SLOT_HEAD) > 0)
			{
				InventoryUpdate iu = new InventoryUpdate();
				iu.addItems(Arrays.asList(unequiped));
				player.sendPacket(iu);
				player.sendPacket(new UserInfo(player));
			}
			else
			{
				// because of complicated jewels problem i'm forced to resend the item list :(
				player.sendPacket(new ItemList(player, true));
				player.sendPacket(new UserInfo(player));
			}
			*/
			
			/*
			InventoryUpdate iu = new InventoryUpdate();
			for(L2ItemInstance element : unequiped)
			{
				iu.addModifiedItem(element);
			}
			player.sendPacket(iu);
			player.abortAttack();
			player.broadcastUserInfo();

			if(unequiped.length > 0)
			{
				if(unequiped[0].isWear())
					return;
				SystemMessage sm = null;
				if(unequiped[0].getEnchantLevel() > 0)
				{
					sm = new SystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
					sm.addNumber(unequiped[0].getEnchantLevel());
					sm.addItemName(unequiped[0].getItemId());
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.S1_DISARMED);
					sm.addItemName(unequiped[0].getItemId());
				}
				player.sendPacket(sm);
			}
			*/
		}
	}
}