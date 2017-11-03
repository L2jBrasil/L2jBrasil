/*
 * $Header: EventData.java
 *
 * $Author: SarEvoK
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

package com.it.br.gameserver.util;

import java.util.LinkedList;
import java.util.List;

public class EventData
{
	public int eventX;
	public int eventY;
	public int eventZ;
	public int eventKarma;
	public int eventPvpKills;
	public int eventPkKills;
	public String eventTitle;
	public List<String> kills = new LinkedList<String>();
	public boolean eventSitForced = false;

	public EventData(int pEventX, int pEventY, int pEventZ, int pEventkarma, int pEventpvpkills, int pEventpkkills, String pEventTitle, List<String> pKills, boolean pEventSitForced)
	{
		eventX = pEventX;
		eventY = pEventY;
		eventZ = pEventZ;
		eventKarma = pEventkarma;
		eventPvpKills = pEventpvpkills;
		eventPkKills = pEventpkkills;
		eventTitle = pEventTitle;
		kills = pKills;
		eventSitForced = pEventSitForced;
	}
}
