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

import com.it.br.gameserver.model.actor.instance.L2PcInstance;

/**
 *
 * @author  Luca Baldi
 */
public class RelationChanged extends L2GameServerPacket
{
	public static final int RELATION_PVP_FLAG     = 0x00002; // pvp ???
	public static final int RELATION_HAS_KARMA    = 0x00004; // karma ???
	public static final int RELATION_LEADER 	  = 0x00080; // leader
    public static final int RELATION_HAS_PARTY    = 0x00008; // true if in party 
    public static final int RELATION_PARTYLEADER  = 0x00010; // true if is party leader 
    public static final int RELATION_PARTY_MEMBER = 0x00020; // party member (even party leader need it) 
    public static final int RELATION_CLAN_MEMBER  = 0x00040; // true if is in clan 
	public static final int RELATION_INSIEGE   	  = 0x00200; // true if in siege
	public static final int RELATION_ATTACKER     = 0x00400; // true when attacker
	public static final int RELATION_ALLY         = 0x00800; // blue siege icon, cannot have if red
	public static final int RELATION_ENEMY        = 0x01000; // true when red icon, doesn't matter with blue
	public static final int RELATION_MUTUAL_WAR   = 0x08000; // double fist
	public static final int RELATION_1SIDED_WAR   = 0x10000; // single fist

	private static final String _S__CE_RELATIONCHANGED = "[S] CE RelationChanged";

	private int _objId, _relation, _autoAttackable, _karma, _pvpFlag;

	public RelationChanged(L2PcInstance cha, int relation, boolean autoattackable)
	{
		_objId = cha.getObjectId();
		_relation = relation;
		_autoAttackable = autoattackable ? 1 : 0;
		_karma = cha.getKarma();
		_pvpFlag = cha.getPvpFlag();
	}

	/**
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#writeImpl()
	 */

	@Override
	protected final void writeImpl()
	{
		// TODO Auto-generated method stub
		writeC(0xce);
		writeD(_objId);
		writeD(_relation);
		writeD(_autoAttackable);
		writeD(_karma);
		writeD(_pvpFlag);
	}

	/**
	 * @see com.it.br.gameserver.BasePacket#getType()
	 */

	@Override
	public String getType()
	{
		// TODO Auto-generated method stub
		return _S__CE_RELATIONCHANGED;
	}

}
