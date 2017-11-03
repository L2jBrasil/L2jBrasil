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
package com.it.br.gameserver.network.loginserverpackets;


public class LoginServerFail extends LoginServerBasePacket
{

	private static final String[] REASONS = {"None" ,
	                                        "Reason: ip banned",
	                                        "Reason: ip reserved",
	                                        "Reason: wrong hexid",
	                                        "Reason: id reserved",
	                                        "Reason: no free ID",
	                                        "Not authed",
	                                        "Reason: already logged in"};
	private int _reason;
	/**
	 * @param decrypt
	 */
	public LoginServerFail(byte[] decrypt)
	{
		super(decrypt);
		_reason = readC();
	}

	public String getReasonString()
	{
		return REASONS[_reason];
	}

	public int getReason()
	{
		return _reason;
	}

}