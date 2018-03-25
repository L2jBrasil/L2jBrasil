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
package com.it.br.gameserver.script.faenor;

import com.it.br.gameserver.script.*;
import org.apache.bsf.BSFManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * @author Luis Arias
 *
 */
public class FaenorEventParser extends FaenorParser
{
    static Logger _log = LoggerFactory.getLogger(FaenorEventParser.class);
    private DateRange _eventDates = null;


	@Override
	public void parseScript(Node eventNode, BSFManager context)
    {
        String ID = attribute(eventNode, "ID");

        if (DEBUG) _log.debug("Parsing Event \""+ID+"\"");

        _eventDates = DateRange.parse(attribute(eventNode, "Active"), DATE_FORMAT);

        Date currentDate = new Date();
        if (_eventDates.getEndDate().before(currentDate))
        {
            _log.warn("Event ID: (" + ID + ") has passed... Ignored.");
            return;
        }

        for (Node node = eventNode.getFirstChild(); node != null; node = node.getNextSibling()) {

            if (isNodeName(node, "DropList"))
            {
                parseEventDropList(node);
            }
            else if (isNodeName(node, "Message"))
            {
                parseEventMessage(node);
            }
        }
    }

    private void parseEventMessage(Node sysMsg)
    {
        if (DEBUG) _log.debug("Parsing Event Message.");

        try
        {
            String type         = attribute(sysMsg, "Type");
            String[] message    = attribute(sysMsg, "Msg").split("\n");

            if (type.equalsIgnoreCase("OnJoin"))
            {
                _bridge.onPlayerLogin(message, _eventDates);
            }
        }
        catch (Exception e)
        {
            _log.warn("Error in event parser.");
            e.printStackTrace();
        }
    }

    private void parseEventDropList(Node dropList)
    {
        if (DEBUG) _log.debug("Parsing Droplist.");

        for (Node node = dropList.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (isNodeName(node, "AllDrop"))
            {
                parseEventDrop(node);
            }
        }
    }

    private void parseEventDrop(Node drop)
    {
        if (DEBUG) _log.debug("Parsing Drop.");

        try
        {
            int[] items         = IntList.parse(attribute(drop, "Items"));
            int[] count         = IntList.parse(attribute(drop, "Count"));
            double chance       = getPercent(attribute(drop, "Chance"));

            _bridge.addEventDrop(items, count, chance, _eventDates);
        }
        catch (Exception e)
        {
            System.err.println("ERROR(parseEventDrop):" + e.getMessage());
        }
    }

    static class FaenorEventParserFactory extends ParserFactory
    {

		@Override
		public Parser create()
        {
            return(new FaenorEventParser());
        }
    }

    static
    {
        ScriptEngine.parserFactories.put(getParserName("Event"), new FaenorEventParserFactory());
    }
}
