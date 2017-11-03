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

import java.util.ArrayList;
import java.util.List;

import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.instance.L2PetInstance;
import com.it.br.gameserver.model.actor.instance.L2SummonInstance;

/**
 * This class ...
 *
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PartySpelled extends L2GameServerPacket
{
    private static final String _S__EE_PartySpelled = "[S] EE PartySpelled";
    private List<Effect> _effects;
    private L2Character _activeChar;

    private class Effect
    {
    	protected int _skillId;
    	protected int _dat;
    	protected int _duration;

        public Effect(int pSkillId, int pDat, int pDuration)
        {
            _skillId = pSkillId;
            _dat = pDat;
            _duration = pDuration;
        }
    }

    public PartySpelled(L2Character cha)
    {
        _effects = new ArrayList<>();
        _activeChar = cha;
    }


    @Override
	protected final void writeImpl()
    {
        if (_activeChar == null) return;
        writeC(0xee);
        writeD(_activeChar instanceof L2SummonInstance ? 2 : _activeChar instanceof L2PetInstance ? 1 : 0);
        writeD(_activeChar.getObjectId());
        writeD(_effects.size());
        for (Effect temp : _effects)
        {
            writeD(temp._skillId);
            writeH(temp._dat);
            writeD(temp._duration / 1000);
        }

    }

    public void addPartySpelledEffect(int skillId, int dat, int duration)
    {
        _effects.add(new Effect(skillId, dat, duration));
    }

    /* (non-Javadoc)
     * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
     */

    @Override
	public String getType()
    {
        return _S__EE_PartySpelled;
    }
}
