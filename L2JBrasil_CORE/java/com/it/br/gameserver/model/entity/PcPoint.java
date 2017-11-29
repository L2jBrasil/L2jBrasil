package com.it.br.gameserver.model.entity;

import static com.it.br.configuration.Configurator.getSettings;

import java.util.logging.Logger;

import com.it.br.configuration.settings.L2JModsSettings;
import com.it.br.gameserver.model.L2World;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.util.Rnd;

/**
 * @Rewarked *Slayer
 */

public class PcPoint implements Runnable
{
	Logger _log = Logger.getLogger(PcPoint.class.getName());
	private static PcPoint _instance;

	public static PcPoint getInstance()
	{
		if(_instance == null)
		{
			_instance = new PcPoint();
		}

		return _instance;
	}

	private PcPoint()
	{
		_log.info("PcBang point event started.");
	}

	public void run()
	{

		int score = 0;
        boolean duble = false;
		for(L2PcInstance activeChar: L2World.getInstance().getAllPlayers())
		{
			L2JModsSettings l2jModsSettings = getSettings(L2JModsSettings.class);
			if(activeChar.getLevel() > l2jModsSettings.getPcBangPointMinLevel() && !activeChar.isOffline())
			{
				score = Rnd.get(l2jModsSettings.getPcBangPointMinCount(), l2jModsSettings.getPcBangPointMaxCount());
				if(Rnd.get(100) <= l2jModsSettings.getPcBangPointDualChance())
				{
					duble = true;
					score *= 2;
				}
				activeChar.addPcBangScore(score);
				activeChar.sendPacket((new SystemMessage(duble ? 1708 : 1707)).addNumber(score));
				activeChar.updatePcBangWnd(score, true, duble);
			}
		} 
	}
}
