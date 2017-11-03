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
package com.it.br.gameserver.model.quest;

/**
 * @author JoseX56
 *
 * Functions in this class are used in python files
 */
public class State
{
	/** Name of the quest */
        @SuppressWarnings("unused")
		private String _questName;
        private String _name;


	/**
	 * Constructor for the state of the quest.
	 * @param name : String pointing out the name of the quest
	 * @param quest : Quest
	 */
    public State(String name, Quest quest)
    {
        _name = name;
        _questName = quest.getName();
        quest.addState(this);
    }

    /**
     * Return name of the quest
     * @return String
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Return name of the quest
     * @return String
     */

    @Override
	public String toString() 
    {
        return _name;
    }
}
