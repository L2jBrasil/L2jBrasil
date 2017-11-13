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
package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;

/**
 *
 * @author  Alisson Oliveira
 */
public class MmoCoreSettings implements Settings{


	private int maxReadPerPass;
	private int maxSendPerPass;
	private int sleepTime;
	private int helperBufferCount;

	@Override
	public void load(L2Properties properties) {
		if(properties == null) { 
			return;
		}
		
		maxReadPerPass = properties.getInteger("maxReadPerPass", 12);
		maxSendPerPass = properties.getInteger("maxSendPerPass", 12);
		sleepTime = properties.getInteger("sleepTime", 20);
		helperBufferCount = properties.getInteger("maxHelperBuffer", 20);
	}
	
	public int getMaxReadPerPass() {
		return maxReadPerPass;
	}
	
	public int getSleepTime() {
		return sleepTime;
	}
	
	public int getMaxSendPerPass() {
		return maxSendPerPass;
	}
	
	public int getHelperBufferCount() {
		return helperBufferCount;
	}
	

}

