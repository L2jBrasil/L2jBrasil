package com.it.br.gameserver;

import com.it.br.configuration.Configurator;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.cache.HtmCache;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.clientpackets.Say2;
import com.it.br.gameserver.network.serverpackets.CreatureSay;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.script.DateRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class Announcements
{
	private static Logger _log = LoggerFactory.getLogger(Announcements.class);
	private static Announcements _instance;
	private List<String> _announcements = new ArrayList<>();
	private List<List<Object>> _eventAnnouncements = new ArrayList<>();

	public Announcements()
	{
		loadAnnouncements();
	}

	public static Announcements getInstance()
	{
		if(_instance == null)
		{
			_instance = new Announcements();
		}

		return _instance;
	}

	public void loadAnnouncements()
	{
		ServerSettings serverSettings = Configurator.getSettings(ServerSettings.class);
		_announcements.clear();
		File file = new File(serverSettings.getDatapackDirectory(), "data/announcements.txt");
		if (file.exists())
		{
			readFromDisk(file);
		}
		else
		{
			_log.info("data/announcements.txt doesn't exist");
		}
	}

	public void showAnnouncements(L2PcInstance activeChar)
	{
		for(int i = 0; i < _announcements.size(); i++)
		{
			CreatureSay cs = new CreatureSay(0, Say2.ANNOUNCEMENT, activeChar.getName(), _announcements.get(i));
			activeChar.sendPacket(cs);
			cs = null;
		}

		for(int i = 0; i < _eventAnnouncements.size(); i++)
		{
			List<Object> entry = _eventAnnouncements.get(i);
			DateRange validDateRange = (DateRange) entry.get(0);
			String[] msg = (String[]) entry.get(1);
			Date currentDate = new Date();

			if(!validDateRange.isValid() || validDateRange.isWithinRange(currentDate))
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_S2);

				for(String element : msg)
				{
					sm.addString(element);
				}

				activeChar.sendPacket(sm);
				sm = null;
			}

			entry = null;
			validDateRange = null;
			msg = null;
			currentDate = null;
		}
	}

	public void addEventAnnouncement(DateRange validDateRange, String[] msg)
	{
	    List<Object> entry = new ArrayList<>();
	    entry.add(validDateRange);
	    entry.add(msg);
	    _eventAnnouncements.add(entry);

		entry = null;
	}

	public void listAnnouncements(L2PcInstance activeChar)
	{
        String content = HtmCache.getInstance().getHtmForce("data/html/admin/announce.htm");
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        adminReply.setHtml(content);
        StringBuilder replyMSG = new StringBuilder("<br>");
		
		for (int i = 0; i < _announcements.size(); i++)
		{
			replyMSG.append("<table width=260><tr><td width=220>" + _announcements.get(i) + "</td><td width=40>");
			replyMSG.append("<button value=\"Delete\" action=\"bypass -h admin_del_announcement " + i + "\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr></table>");
		}
        adminReply.replace("%announces%", replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	public void addAnnouncement(String text)
	{
		_announcements.add(text);
		saveToDisk();
	}

	public void delAnnouncement(int line)
	{
		_announcements.remove(line);
		saveToDisk();
	}

	private void readFromDisk(File file)
	{
		LineNumberReader lnr = null;
		try
		{
			int i=0;
			String line = null;
			lnr = new LineNumberReader(new FileReader(file));
			while ( (line = lnr.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line,"\n\r");
				if (st.hasMoreTokens())
				{
					String announcement = st.nextToken();
					_announcements.add(announcement);
					i++;
				}
			}
			_log.info("Announcements: Loaded " + i + " Announcements.");
		}
		catch (IOException e1)
		{
			_log.error( "Error reading announcements", e1);
		}
		finally
		{
			try
			{
				lnr.close();
			}
			catch (Exception e2){}
		}
	}

	private void saveToDisk()
	{
		File file = new File("data/announcements.txt");
		FileWriter save = null;

		try
		{
			save = new FileWriter(file);
			for (int i = 0; i < _announcements.size(); i++)
			{
				save.write(_announcements.get(i));
				save.write("\r\n");
			}
			save.flush();
			save.close();
			save = null;
		}
		catch (IOException e)
		{
			_log.warn("saving the announcements file has failed: " + e);
		}
	}

	public void announceToAll(String text)
	{
		CreatureSay cs = new CreatureSay(0, Say2.ANNOUNCEMENT, "", text);
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			player.sendPacket(cs);
		}
	}

	public void announceToAll(SystemMessage sm)
	{
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			player.sendPacket(sm);
		}
	}

	// Method fo handling announcements from admin
	public void handleAnnounce(String command, int lengthToTrim)
	{
		try
		{
			// Announce string to everyone on server
			String text = command.substring(lengthToTrim);
			Announcements.getInstance().announceToAll(text);
		}

		// No body cares!
		catch (StringIndexOutOfBoundsException e)
		{
			// empty message.. ignore
		}
	}

	public static void announceToPlayers(String message)
	{
		for (L2PcInstance player : L2World.getInstance().getAllPlayers()) 
		{
			   player.sendMessage(message);
		}
	}
	public void announceToPlayers(SystemMessage sm)
	{
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			player.sendPacket(sm);
		}
	}
	
	// Colored Announcements 8D
	public void gameAnnounceToAll(String text)
	{
		CreatureSay cs = new CreatureSay(0, 18, "", " "+text);

		for(L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			if(player != null)
				if(player.isOnline()!=0)
					player.sendPacket(cs);
		}

		cs = null;
	}
}