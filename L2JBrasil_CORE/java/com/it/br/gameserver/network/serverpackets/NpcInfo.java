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
package com.it.br.gameserver.network.serverpackets;

import com.it.br.Config;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.instancemanager.TownManager;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.L2Clan;
import com.it.br.gameserver.model.L2Summon;
import com.it.br.gameserver.model.actor.instance.L2MonsterInstance;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.model.zone.type.L2TownZone;

public class NpcInfo extends L2GameServerPacket
{
	private static final String _S__22_NPCINFO = "[S] 16 NpcInfo";
	private L2Character _activeChar;
	private int _x, _y, _z, _heading;
	private int _idTemplate;
	private boolean _isAttackable, _isSummoned;
	private int _mAtkSpd, _pAtkSpd;
	private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd;
	private int _rhand, _lhand;
    private int _collisionHeight, _collisionRadius;
    private String _name = "";
    private String _title = "";
    int _clanCrest = 0, _allyCrest = 0, _allyId = 0, _clanId = 0;

	public NpcInfo(L2NpcInstance cha, L2Character attacker)
	{
		_activeChar = cha;
		_idTemplate = cha.getTemplate().idTemplate;
		_isAttackable = cha.isAutoAttackable(attacker);
		_rhand = cha.getRightHandItem(); 
		_lhand = cha.getLeftHandItem(); 
		_isSummoned = false;
        _collisionHeight = cha.getCollisionHeight();
        _collisionRadius = cha.getCollisionRadius();
        if (cha.getTemplate().serverSideName)
        	_name = cha.getTemplate().name;

        if(Config.L2JMOD_CHAMPION_ENABLE && cha.isChampion())
            _title = ("Champion");
        else if (cha.getTemplate().serverSideTitle)
    		_title = cha.getTemplate().title;
    	else
    		_title = cha.getTitle();

        if (Config.SHOW_NPC_LVL && _activeChar instanceof L2MonsterInstance)
	    {
			String t = "Lv " + cha.getLevel() + (cha.getAggroRange() > 0 ? "*" : "");
			if (_title != null)
				t += " " + _title;

			_title = t;
	    }
        
        if(Config.SHOW_NPC_CREST)
        {
        	if(cha != null && cha.isInsideZone(L2Character.ZONE_PEACE) && cha.getCastle().getOwnerId() != 0)
        	{
        		_x = cha.getX();
        		_y = cha.getY();
        		_z = cha.getZ();
        		L2TownZone Town;
        		Town = TownManager.getInstance().getTown(_x, _y, _z);
        		if(Town != null)
        		{
        			int townId = Town.getTownId();
        			if(townId != 33 && townId != 22)
        			{
        				L2Clan clan;
        				clan = ClanTable.getInstance().getClan(cha.getCastle().getOwnerId());
        				_clanCrest = clan.getCrestId();
        				_clanId = clan.getClanId();
        				_allyCrest = clan.getAllyCrestId();
        				_allyId = clan.getAllyId();
        			}
        		}
        	}
        }

        _x = _activeChar.getX();
		_y = _activeChar.getY();
		_z = _activeChar.getZ();
		_heading = _activeChar.getHeading();
		_mAtkSpd = _activeChar.getMAtkSpd();
		_pAtkSpd = _activeChar.getPAtkSpd();
		_runSpd = _activeChar.getRunSpeed();
		_walkSpd = _activeChar.getWalkSpeed();
		_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
		_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
	}

	public NpcInfo(L2Summon cha, L2Character attacker)
	{
		_activeChar = cha;
		_idTemplate = cha.getTemplate().idTemplate;
		_isAttackable = cha.isAutoAttackable(attacker); //(cha.getKarma() > 0);
		_rhand = 0;
		_lhand = 0;
		_isSummoned = cha.isShowSummonAnimation();
        _collisionHeight = _activeChar.getTemplate().collisionHeight;
        _collisionRadius = _activeChar.getTemplate().collisionRadius;
        if (cha.getTemplate().serverSideName || cha instanceof L2PetInstance)
    	{
            _name = _activeChar.getName();
    		_title = cha.getTitle();
    	}

        _x = _activeChar.getX();
		_y = _activeChar.getY();
		_z = _activeChar.getZ();
		_heading = _activeChar.getHeading();
		_mAtkSpd = _activeChar.getMAtkSpd();
		_pAtkSpd = _activeChar.getPAtkSpd();
		_runSpd = _activeChar.getRunSpeed();
		_walkSpd = _activeChar.getWalkSpeed();
		_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
		_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
	}


	@Override
	protected final void writeImpl()
	{
        if (_activeChar instanceof L2Summon)
            if (((L2Summon)_activeChar).getOwner() != null
                    && ((L2Summon)_activeChar).getOwner().getAppearance().getInvisible())
                return;
		writeC(0x16);
		writeD(_activeChar.getObjectId());
		writeD(_idTemplate+1000000);  // npctype id
		writeD(_isAttackable ? 1 : 0);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_heading);
		writeD(0x00);
		writeD(_mAtkSpd);
		writeD(_pAtkSpd);
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimRunSpd/*0x32*/);  // swimspeed
		writeD(_swimWalkSpd/*0x32*/);  // swimspeed
		writeD(_flRunSpd);
		writeD(_flWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);
		writeF(1.1/*_activeChar.getProperMultiplier()*/);
		//writeF(1/*_activeChar.getAttackSpeedMultiplier()*/);
		writeF(_pAtkSpd/277.478340719);
		writeF(_collisionRadius);
		writeF(_collisionHeight);
		writeD(_rhand); // right hand weapon
		writeD(0);
		writeD(_lhand); // left hand weapon
		writeC(1);	// name above char 1=true ... ??
		writeC(_activeChar.isRunning() ? 1 : 0);
		writeC(_activeChar.isInCombat() ? 1 : 0);
		writeC(_activeChar.isAlikeDead() ? 1 : 0);
		writeC(_isSummoned ? 2 : 0); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
		writeS(_name);
		writeS(_title);
		writeD(0);
		writeD(0);
		writeD(0000);  // hmm karma ??

		writeD(_activeChar.getAbnormalEffect());  // C2
	    if(Config.SHOW_NPC_CREST)
	    {
	    	writeD(_clanId);
	    	writeD(_clanCrest);
	    	writeD(_allyId);
	    	writeD(_allyCrest);
	    }
	    else
	    {
	    	writeD(0000);
	    	writeD(0000);
	    	writeD(0000);
	    	writeD(0000);
	    	writeC(0000);
		}

		writeC(0x00);  // C3  team circle 1-blue, 2-red
		writeF(_collisionRadius);
		writeF(_collisionHeight);
		writeD(0x00);  // C4
		writeD(0x00);  // C6
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__22_NPCINFO;
	}
}
