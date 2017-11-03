/*
 * $Header: SpawnListener.java, 7/09/2005 23:44:50 luisantonioa Exp $
 *
 * $Author: luisantonioa $
 * $Date: 7/09/2005 23:44:50 $
 * $Revision: 1 $
 * $Log: SpawnListener.java,v $
 * Revision 1  7/09/2005 23:44:50  luisantonioa
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

import com.it.br.gameserver.model.actor.instance.L2NpcInstance;

/**
 * This class ...
 *
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */

public interface SpawnListener
{
    public void npcSpawned(L2NpcInstance npc);
}
