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
package com.it.br.gameserver.ai.special;

import java.util.logging.Logger;

import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.ai.special.group.*;
import com.it.br.gameserver.ai.special.individual.Antharas;
import com.it.br.gameserver.ai.special.individual.Baium;
import com.it.br.gameserver.ai.special.individual.Core;
import com.it.br.gameserver.ai.special.individual.Frintezza;
import com.it.br.gameserver.ai.special.individual.Gordon;
import com.it.br.gameserver.ai.special.individual.Orfen;
import com.it.br.gameserver.ai.special.individual.QueenAnt;
import com.it.br.gameserver.ai.special.individual.Valakas;
import com.it.br.gameserver.ai.special.individual.VanHalter;
import com.it.br.gameserver.ai.special.individual.Zaken;

/**
 * @author qwerty
 * @reworked *slayer
 */

public class AiLoader
{
	private static final Logger _log = Logger.getLogger(AiLoader.class.getName());
	public static void init()
	{

        new AncientEgg(-1, "AncientEgg", "ai_grp");
        new CatsEyeBandit(-1, "CatsEyeBandit", "ai_grp");
        new Chests(-1, "chests", "ai_grp");
        new DeluLizardmanSpecialAgent(-1, "DeluLizardmanSpecialAgent", "ai_grp");
        new DeluLizardmanSpecialCommander(-1, "DeluLizardmanSpecialCommander", "ai_grp");
        new FeedableBeasts(-1, "FeedableBeasts", "ai_grp");
        new KarulBugBear(-1, "KarulBugBear", "ai_grp");
        new Monastery(-1, "monastery", "ai_grp");
        new OlMahumGeneral(-1, "OlMahumGeneral", "ai_grp");
        new RetreatOnAttack(-1,"retreatOnAttack","ai_grp");
        new ScarletStokateNoble(-1, "ScarletStokateNoble", "ai_grp");
        new Splendor(-1, "Splendor", "ai_grp");
        new TimakOrcOverlord(-1, "TimakOrcOverlord", "ai_grp");
        new TimakOrcTroopLeader(-1, "TimakOrcTroopLeader", "ai_grp");
        new TurekOrcFootman(-1, "TurekOrcFootman", "ai_grp");
        new TurekOrcSupplier(-1, "TurekOrcSupplier", "ai_grp");
        new TurekOrcWarlord(-1, "TurekOrcWarlord", "ai_grp");

        ThreadPoolManager.getInstance().scheduleAi(new Antharas(-1, "antharas", "ai"), 100);
        ThreadPoolManager.getInstance().scheduleAi(new Baium(-1, "baium", "ai"), 200);
        ThreadPoolManager.getInstance().scheduleAi(new Core(-1, "core", "ai"), 300);
        ThreadPoolManager.getInstance().scheduleAi(new QueenAnt(-1, "queen_ant", "ai"), 400);
        ThreadPoolManager.getInstance().scheduleAi(new VanHalter(-1, "vanhalter", "ai"), 500);
        ThreadPoolManager.getInstance().scheduleAi(new Gordon(-1, "Gordon", "ai"), 600);
        ThreadPoolManager.getInstance().scheduleAi(new Monastery(-1, "monastery", "ai"), 700);
        ThreadPoolManager.getInstance().scheduleAi(new Orfen(-1, "Orfen", "ai"), 800);
        ThreadPoolManager.getInstance().scheduleAi(new Zaken(-1, "Zaken", "ai"), 900);
        ThreadPoolManager.getInstance().scheduleAi(new Frintezza(-1, "Frintezza", "ai"), 1000);
        ThreadPoolManager.getInstance().scheduleAi(new Valakas(-1, "valakas", "ai"), 1200);
        _log.info("All AI loaded.");
    }
}