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
package com.it.br.gameserver.model;

import com.it.br.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GMAudit {
	private static final Logger _log = LoggerFactory.getLogger("gmaudit");

	public static void auditGMAction(String gmName, String action, String target, String params){
		if (Config.GMAUDIT){
            String today;
            SimpleDateFormat formatter;
            formatter = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
            today = formatter.format(new Date());

			_log.info( today + ">" + gmName + ">" + action + ">" +target + ">" + params);
		}
	}
}





