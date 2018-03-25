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
package com.it.br.loginserver;

import com.it.br.loginserver.serverpackets.Init;
import com.it.br.util.IPv4Filter;
import com.l2jserver.mmocore.network.*;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author KenM
 */
public class SelectorHelper implements IMMOExecutor<L2LoginClient>, IClientFactory<L2LoginClient>, IAcceptFilter
{
	private ThreadPoolExecutor _generalPacketsThreadPool;
        private IPv4Filter _ipv4filter;
    
	public SelectorHelper()
	{
	        _generalPacketsThreadPool = new ThreadPoolExecutor(4, 6, 15L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
                _ipv4filter = new IPv4Filter(); 
        }

	/**
	 * @see com.l2jserver.mmocore.network.IMMOExecutor#execute(com.l2jserver.mmocore.network.ReceivablePacket)
	 */
	public void execute(ReceivablePacket<L2LoginClient> packet)
	{
		_generalPacketsThreadPool.execute(packet);
	}

	/**
	 * @see com.l2jserver.mmocore.network.IClientFactory#create(com.l2jserver.mmocore.network.MMOConnection)
	 */
	public L2LoginClient create(MMOConnection<L2LoginClient> con)
	{
		L2LoginClient client = new L2LoginClient(con);
		client.sendPacket(new Init(client));
		return client;
	}

	/**
	 * @see com.l2jserver.mmocore.network.IAcceptFilter#accept(java.nio.channels.SocketChannel)
	 */
	public boolean accept(SocketChannel sc)
	{
		return _ipv4filter.accept(sc) && !LoginController.getInstance().isBannedAddress(sc.socket().getInetAddress());
	}

}
