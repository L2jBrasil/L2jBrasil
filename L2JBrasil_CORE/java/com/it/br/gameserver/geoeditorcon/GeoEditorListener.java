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
package com.it.br.gameserver.geoeditorcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Dezmond
 */
public class GeoEditorListener extends Thread
{
	private static GeoEditorListener _instance;
	private static final int PORT = 9011;
	private static Logger _log = LoggerFactory.getLogger(GeoEditorListener.class);
	private ServerSocket _serverSocket;
	private static GeoEditorThread _geoEditor;

	public static GeoEditorListener getInstance()
	{
		if (_instance == null)
			try
			{
				_instance = new GeoEditorListener();
				_instance.start();
				_log.info("GeoEditorListener Initialized.");
			} catch (IOException e)
			{
				_log.error("Error creating geoeditor listener! "
						+ e.getMessage());
				System.exit(1);
			}
		return _instance;
	}

	private GeoEditorListener() throws IOException
	{
		_serverSocket = new ServerSocket(PORT);
	}

	public GeoEditorThread getThread()
	{
		return _geoEditor;
	}

	public String getStatus()
	{
		if (_geoEditor != null && _geoEditor.isWorking())
		{
			return "Geoeditor connected.";
		}
		return "Geoeditor not connected.";
	}


	@Override
	public void run()
	{
		Socket connection = null;
		try
		{
			while (true)
			{
				connection = _serverSocket.accept();
				if (_geoEditor != null && _geoEditor.isWorking())
				{
					_log.warn("Geoeditor already connected!");
					connection.close();
					continue;
				}
				_log.info("Received geoeditor connection from: "
						+ connection.getInetAddress().getHostAddress());
				_geoEditor = new GeoEditorThread(connection);
				_geoEditor.start();
			}
		} catch (Exception e)
		{
			_log.info("GeoEditorListener: " + e.getMessage());
			try
			{
				connection.close();
			} catch (Exception e2)
			{
			}
		} finally
		{
			try
			{
				_serverSocket.close();
			} catch (IOException io)
			{
				_log.info( "", io);
			}
			_log.warn("GeoEditorListener Closed!");
		}
	}
}
