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
package com.it.br.gameserver.network.serverpackets;

import com.it.br.gameserver.model.L2Effect;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.skills.effects.EffectCharge;

/* Packet format: F3 XX000000 YY000000 ZZ000000 */

/**
 *
 * @author  Luca Baldi
 */
public class EtcStatusUpdate extends L2GameServerPacket
{
	private static final String _S__F3_ETCSTATUSUPDATE = "[S] F3 EtcStatusUpdate";

	private L2PcInstance _activeChar;
	private EffectCharge _effect;

	public EtcStatusUpdate(L2PcInstance activeChar)
	{
		 _activeChar = activeChar;
		 _effect = (EffectCharge)_activeChar.getFirstEffect(L2Effect.EffectType.CHARGE);
	}

	/**
	 * @see com.it.br.gameserver.network.serverpackets.L2GameServerPacket#writeImpl()
	 */

	@Override
	protected void writeImpl()
	{
		writeC(0xF3); // several icons to a separate line (0 = disabled)
		if (_effect != null)
			writeD(_effect.getLevel()); // 1-7 increase force, lvl
		else
			writeD(0x00); // 1-7 increase force, lvl
		writeD(_activeChar.getWeightPenalty()); // 1-4 weight penalty, lvl (1=50%, 2=66.6%, 3=80%, 4=100%)
		writeD((_activeChar.getMessageRefusal() || _activeChar.isChatBanned()) ? 1 : 0); // 1 = block all chat 
		writeD(0x00); // 1 = danger area
		writeD(Math.min(_activeChar.getExpertisePenalty(),1)); // 1 = grade penalty 
		writeD(_activeChar.getCharmOfCourage() ? 1 : 0); // 1 = charm of courage (no xp loss in siege..)
		writeD(_activeChar.getDeathPenaltyBuffLevel()); // 1-15 death penalty, lvl (combat ability decreased due to death) 
	}

	/**
	 * @see com.it.br.gameserver.network.serverpackets.L2GameServerPacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__F3_ETCSTATUSUPDATE;
	}
}
