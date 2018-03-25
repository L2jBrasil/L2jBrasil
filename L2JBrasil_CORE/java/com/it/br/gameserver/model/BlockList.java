/*
 * $Header: BlockList.java, 21/11/2005 14:53:53 luisantonioa Exp $
 *
 * $Author: luisantonioa $
 * $Date: 21/11/2005 14:53:53 $
 * $Revision: 1 $
 * $Log: BlockList.java,v $
 * Revision 1  21/11/2005 14:53:53  luisantonioa
 * Added copyright notice
 *
 *
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

import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * This class ...
 *
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */

public class BlockList
{
    private final Set<String> _blockSet;
    private boolean _blockAll;

    public BlockList()
    {
        _blockSet    = new HashSet<>();
        _blockAll    = false;
    }

    private void addToBlockList(L2PcInstance character)
    {
        if(character != null)
        {
            _blockSet.add(character.getName());
        }
    }

    private void removeFromBlockList(L2PcInstance character)
    {
        if(character != null)
        {
            _blockSet.remove(character.getName());
        }
    }

    private boolean isInBlockList(L2PcInstance character)
    {
        return _blockSet.contains(character.getName());
    }

    private boolean isBlockAll()
    {
        return _blockAll;
    }

    public static boolean isBlocked(L2PcInstance listOwner, L2PcInstance character)
    {
        BlockList blockList = listOwner.getBlockList();
        return blockList.isBlockAll() || blockList.isInBlockList(character);
    }

    private void setBlockAll(boolean state)
    {
        _blockAll = state;
    }

    private Set<String> getBlockList()
    {
        return _blockSet;
    }

    public static void addToBlockList(L2PcInstance listOwner, L2PcInstance character)
    {
        listOwner.getBlockList().addToBlockList(character);

        SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_ADDED_YOU_TO_IGNORE_LIST);
        sm.addString(listOwner.getName());
        character.sendPacket(sm);

        sm = new SystemMessage(SystemMessageId.S1_WAS_ADDED_TO_YOUR_IGNORE_LIST);
        sm.addString(character.getName());
        listOwner.sendPacket(sm);
    }

    public static void removeFromBlockList(L2PcInstance listOwner, L2PcInstance character)
    {
        listOwner.getBlockList().removeFromBlockList(character);

        SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_REMOVED_FROM_YOUR_IGNORE_LIST);
        sm.addString(character.getName());
        listOwner.sendPacket(sm);
    }

    public static boolean isInBlockList(L2PcInstance listOwner, L2PcInstance character)
    {
        return listOwner.getBlockList().isInBlockList(character);
    }

    public static boolean isBlockAll(L2PcInstance listOwner)
    {
        return listOwner.getBlockList().isBlockAll();
    }

    public static void setBlockAll(L2PcInstance listOwner, boolean newValue)
    {
        listOwner.getBlockList().setBlockAll(newValue);
    }

    public static void sendListToOwner(L2PcInstance listOwner)
    {
        for (String playerName : listOwner.getBlockList().getBlockList())
        {
            listOwner.sendPacket(new SystemMessage(SystemMessageId.S1_S2).addString(playerName));
        }
    }
}
