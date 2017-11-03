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

import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.gameserver.cache.HtmCache;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.clientpackets.RequestBypassToServer;

/**
 *
 * the HTML parser in the client knowns these standard and non-standard tags and attributes
 * VOLUMN
 * UNKNOWN
 * UL
 * U
 * TT
 * TR
 * TITLE
 * TEXTCODE
 * TEXTAREA
 * TD
 * TABLE
 * SUP
 * SUB
 * STRIKE
 * SPIN
 * SELECT
 * RIGHT
 * PRE
 * P
 * OPTION
 * OL
 * MULTIEDIT
 * LI
 * LEFT
 * INPUT
 * IMG
 * I
 * HTML
 * H7
 * H6
 * H5
 * H4
 * H3
 * H2
 * H1
 * FONT
 * EXTEND
 * EDIT
 * COMMENT
 * COMBOBOX
 * CENTER
 * BUTTON
 * BR
 * BODY
 * BAR
 * ADDRESS
 * A
 * SEL
 * LIST
 * VAR
 * FORE
 * READONL
 * ROWS
 * VALIGN
 * FIXWIDTH
 * BORDERCOLORLI
 * BORDERCOLORDA
 * BORDERCOLOR
 * BORDER
 * BGCOLOR
 * BACKGROUND
 * ALIGN
 * VALU
 * READONLY
 * MULTIPLE
 * SELECTED
 * TYP
 * TYPE
 * MAXLENGTH
 * CHECKED
 * SRC
 * Y
 * X
 * QUERYDELAY
 * NOSCROLLBAR
 * IMGSRC
 * B
 * FG
 * SIZE
 * FACE
 * COLOR
 * DEFFON
 * DEFFIXEDFONT
 * WIDTH
 * VALUE
 * TOOLTIP
 * NAME
 * MIN
 * MAX
 * HEIGHT
 * DISABLED
 * ALIGN
 * MSG
 * LINK
 * HREF
 * ACTION
 *
 *
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class NpcHtmlMessage extends L2GameServerPacket
{
	// d S
	// d is usually 0, S is the html text starting with <html> and ending with </html>
	//
	private static final String _S__1B_NPCHTMLMESSAGE = "[S] 0f NpcHtmlMessage";
	private static Logger _log = Logger.getLogger(RequestBypassToServer.class.getName());
	private int _npcObjId;
	private String _html;
        private boolean _validate = true;
	/**
	 * @param _characters
	 */
	public NpcHtmlMessage(int npcObjId, String text)
	{
		_npcObjId = npcObjId;
		setHtml(text);
	}

	public NpcHtmlMessage(int npcObjId)
	{
		_npcObjId = npcObjId;
	}


	@Override
	public void runImpl()
	{
		if (Config.BYPASS_VALIDATION && _validate)
		{
 			buildBypassCache(getClient().getActiveChar());
			buildLinksCache(getClient().getActiveChar());
	     }
	 } 
	public void setHtml(String text)
	{
                if (text.length() > 8192)
		{
			_log.warning("Html is too long! this will crash the client!");
			_html = "<html><body>Html was too long</body></html>";
			return;
		}
		_html = text; // html code must not exceed 8192 bytes
	}

	public boolean setFile(String path)
	{
           String content = HtmCache.getInstance().getHtm(path);

		if (content == null)
		{
			setHtml("<html><body>My Text is missing:<br>"+path+"</body></html>");
			_log.warning("missing html page "+path);
			return false;
		}

           setHtml(content);
           return true;
	}

	public void replace(String pattern, String value)
	{
		_html = _html.replaceAll(pattern, value);
	}

	private final void buildBypassCache(L2PcInstance activeChar)
	{
        if (activeChar == null)
            return;

        activeChar.clearBypass();
		int len = _html.length();
		for(int i=0; i<len; i++)
		{
			int start = _html.indexOf("bypass -h", i);
			int finish = _html.indexOf("\"", start);

			if(start < 0 || finish < 0)
				break;

			start += 10;
			i = start;
			int finish2 = _html.indexOf("$",start);
			if (finish2 < finish && finish2 > 0)
                activeChar.addBypass2(_html.substring(start, finish2));
			else
                activeChar.addBypass(_html.substring(start, finish));
			//System.err.println("["+_html.substring(start, finish)+"]");
		}
	}

        private final void buildLinksCache(L2PcInstance activeChar)
	{
        if (activeChar == null)
            return;
        
        activeChar.clearLinks();
		int len = _html.length();
		for(int i=0; i<len; i++)
		{
			int start = _html.indexOf("\"link ", i);
			if (start != -1) start++;

			int finish = _html.indexOf("\"", start);
			
			if(start < 0 || finish < 0)
				break;
			
			i = start;
			
			activeChar.addLink(_html.substring(start + 5, finish).trim());
		}
	}



	@Override
	protected final void writeImpl()
	{
		writeC(0x0f);

		writeD(_npcObjId);
		writeS(_html);
		writeD(0x00);
	}

	/* (non-Javadoc)
	 * @see com.it.br.gameserver.network.serverpackets.ServerBasePacket#getType()
	 */

	@Override
	public String getType()
	{
		return _S__1B_NPCHTMLMESSAGE;
	}

}
