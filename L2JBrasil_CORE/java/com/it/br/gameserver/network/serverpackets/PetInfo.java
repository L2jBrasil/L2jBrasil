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

import com.it.br.gameserver.model.L2Summon;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;

/**
 * This class ...
 *
 * @version $Revision: 1.6.2.5.2.12 $ $Date: 2005/03/31 09:19:16 $
 */
public class PetInfo extends L2GameServerPacket
{
	//private static Logger _log = LoggerFactory.getLogger(PetInfo.class);

	private static final String _S__CA_PETINFO = "[S] b1 PetInfo";
	private L2Summon _summon;
	private int _x, _y, _z, _heading;
	private boolean _isSummoned;
	private int _mAtkSpd, _pAtkSpd;
	private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd;
	private int _maxHp, _maxMp;
	private int _maxFed, _curFed;

	/**
	 * rev 478  dddddddddddddddddddffffdddcccccSSdddddddddddddddddddddddddddhc
	 * @param _characters
	 */
	public PetInfo(L2Summon summon)
	{
        _summon = summon;
        _isSummoned = _summon.isShowSummonAnimation();
		_x = _summon.getX();
		_y = _summon.getY();
		_z = _summon.getZ();
		_heading = _summon.getHeading();
		_mAtkSpd = _summon.getMAtkSpd();
		_pAtkSpd = _summon.getPAtkSpd();
		_runSpd = _summon.getRunSpeed();
		_walkSpd = _summon.getWalkSpeed();
		_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
		_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
		_maxHp = _summon.getMaxHp();
		_maxMp = _summon.getMaxMp();
		if (_summon instanceof L2PetInstance)
        {
			L2PetInstance pet = (L2PetInstance)_summon;
			_curFed = pet.getCurrentFed(); // how fed it is
			_maxFed = pet.getMaxFed(); //max fed it can be
		}
	}


	@Override
	protected final void writeImpl()
	{
		writeC(0xb1);
		writeD(_summon.getSummonType());
		writeD(_summon.getObjectId());
		writeD(_summon.getTemplate().idTemplate+1000000);
		writeD(0);    // 1=attackable

		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_heading);
		writeD(0);
		writeD(_mAtkSpd);
		writeD(_pAtkSpd);
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimRunSpd);
		writeD(_swimWalkSpd);
		writeD(_flRunSpd);
		writeD(_flWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);

		writeF(1/*_cha.getProperMultiplier()*/);
		writeF(1/*_cha.getAttackSpeedMultiplier()*/);
		writeF(_summon.getTemplate().collisionRadius);
		writeF(_summon.getTemplate().collisionHeight);
		writeD(0); // right hand weapon
		writeD(0);
		writeD(0); // left hand weapon
		writeC(1);	// name above char 1=true ... ??
		writeC(_summon.isRunning() ? 1 : 0);	// running=1
		writeC(_summon.isInCombat() ? 1 : 0);	// attacking 1=true
		writeC(_summon.isAlikeDead() ? 1 : 0);  // dead 1=true
		writeC(_isSummoned ? 2 : 0); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
		 writeS(_summon.getTemplate().serverSideName ? _summon.getName() : ""); // Summon name.
		writeS(_summon.getTitle());
		writeD(1);
		writeD(_summon.getPvpFlag());	//0 = white,2= purpleblink, if its greater then karma = purple
		writeD(_summon.getKarma());  // hmm karma ??
		writeD(_curFed); // how fed it is
		writeD(_maxFed); //max fed it can be
		writeD((int)_summon.getCurrentHp());//current hp
		writeD(_maxHp);// max hp
		writeD((int)_summon.getCurrentMp());//current mp
		writeD(_maxMp);//max mp
		writeD(_summon.getStat().getSp()); //sp
		writeD(_summon.getLevel());// lvl
		writeQ(_summon.getStat().getExp());
		writeQ(_summon.getExpForThisLevel());// 0%  absolute value
		writeQ(_summon.getExpForNextLevel());// 100% absoulte value
		writeD(_summon instanceof L2PetInstance ? _summon.getInventory().getTotalWeight() : 0);//weight
		writeD(_summon.getMaxLoad());//max weight it can carry
		writeD(_summon.getPAtk(null));//patk
		writeD(_summon.getPDef(null));//pdef
		writeD(_summon.getMAtk(null,null));//matk
		writeD(_summon.getMDef(null,null));//mdef
		writeD(_summon.getAccuracy());//accuracy
		writeD(_summon.getEvasionRate(null));//evasion
		writeD(_summon.getCriticalHit(null,null));//critical
		writeD(_runSpd);//speed
		writeD(_summon.getPAtkSpd());//atkspeed
		writeD(_summon.getMAtkSpd());//casting speed

		writeD(0);//c2  abnormal visual effect... bleed=1; poison=2; poison & bleed=3; flame=4;

		final int npcId = _summon.getNpcId(); 
		if (npcId >= 12526 && npcId <= 12528)
			writeH(1);//c2    ride button
		else
			writeH(0);

        writeC(0); // c2

        // Following all added in C4.
        writeH(0); // ??
        writeC(0); // team aura (1 = blue, 2 = red)
		writeD(_summon.getSoulShotsPerHit()); // How many soulshots this servitor uses per hit
        writeD(_summon.getSpiritShotsPerHit()); // How many spiritshots this servitor uses per hit
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__CA_PETINFO;
	}

}
