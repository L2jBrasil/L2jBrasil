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
package com.it.br.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import com.it.br.Config;
import com.it.br.gameserver.datatables.xml.NpcTable;
import com.it.br.gameserver.handler.IAdminCommandHandler;
import com.it.br.gameserver.idfactory.IdFactory;
import com.it.br.gameserver.model.L2Character;
import com.it.br.gameserver.model.actor.instance.L2MonsterInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.NpcHtmlMessage;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.gameserver.skills.Formulas;
import com.it.br.gameserver.templates.L2NpcTemplate;
import com.it.br.util.Rnd;

/**
 * This class handles following admin commands:
 * - gm = turns gm mode on/off
 *
 * @version $Revision: 1.1.2.1 $ $Date: 2005/03/15 21:32:48 $
 */
public class AdminFightCalculator implements IAdminCommandHandler {
	//private static Logger _log = Logger.getLogger(AdminFightCalculator.class.getName());
	private static final String[] ADMIN_COMMANDS = {
		"admin_fight_calculator",
		"admin_fight_calculator_show",
		"admin_fcs",
		};
	private static final int REQUIRED_LEVEL = Config.GM_MIN;

	//TODO: remove from gm list etc etc
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        try
        {
    		//don't check for gm status ;)
            if (!Config.ALT_PRIVILEGES_ADMIN)
                if (!checkLevel(activeChar.getAccessLevel())) return false;

    		if (command.startsWith("admin_fight_calculator_show"))
    			handleShow(command.substring("admin_fight_calculator_show".length()), activeChar);
    		else if (command.startsWith("admin_fcs"))
    			handleShow(command.substring("admin_fcs".length()), activeChar);
    		else if (command.startsWith("admin_fight_calculator"))
    			handleStart(command.substring("admin_fight_calculator".length()), activeChar);
        }
        catch (StringIndexOutOfBoundsException e)
        { }
		return true;
	}

	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}

	private boolean checkLevel(int level) {
		return (level >= REQUIRED_LEVEL);
	}

	private void handleStart(String params, L2PcInstance activeChar) {
		StringTokenizer st = new StringTokenizer(params);
		int lvl1 = 0;
		int lvl2 = 0;
		int mid1 = 0;
		int mid2 = 0;
		while (st.hasMoreTokens())
		{
			String s = st.nextToken();
			if (s.equals("lvl1")) { lvl1 = Integer.parseInt(st.nextToken()); continue; }
			if (s.equals("lvl2")) { lvl2 = Integer.parseInt(st.nextToken()); continue; }
			if (s.equals("mid1")) { mid1 = Integer.parseInt(st.nextToken()); continue; }
			if (s.equals("mid2")) { mid2 = Integer.parseInt(st.nextToken()); continue; }
		}

		L2NpcTemplate npc1 = null;
		if (mid1 != 0)
			npc1 = NpcTable.getInstance().getTemplate(mid1);
		L2NpcTemplate npc2 = null;
		if (mid2 != 0)
			npc2 = NpcTable.getInstance().getTemplate(mid2);


		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuilder replyMSG = new StringBuilder();
		if (npc1 != null && npc2 != null)
		{
			replyMSG.append("<html><title>Selected mobs to fight</title>");
			replyMSG.append("<body>");
			replyMSG.append("<table>");
			replyMSG.append("<tr><td>First</td><td>Second</td></tr>");
			replyMSG.append("<tr><td>level "+lvl1+"</td><td>level "+lvl2+"</td></tr>");
			replyMSG.append("<tr><td>id "+npc1.npcId+"</td><td>id "+npc2.npcId+"</td></tr>");
			replyMSG.append("<tr><td>"+npc1.name+"</td><td>"+npc2.name+"</td></tr>");
	        replyMSG.append("</table>");
	        replyMSG.append("<center><br><br><br>");
	        replyMSG.append("<button value=\"OK\" action=\"bypass -h admin_fight_calculator_show "+npc1.npcId+" "+npc2.npcId+"\"  width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
	        replyMSG.append("</center>");
	        replyMSG.append("</body></html>");
		}
		else if (lvl1 != 0 && npc1 == null)
		{
			replyMSG.append("<html><title>Select first mob to fight</title>");
			replyMSG.append("<body><table>");
			L2NpcTemplate[] npcs = NpcTable.getInstance().getAllOfLevel(lvl1);
			for (L2NpcTemplate n : npcs) {
				replyMSG.append("<tr><td><a action=\"bypass -h admin_fight_calculator lvl1 "+lvl1+" lvl2 "+lvl2+" mid1 "+n.npcId+" mid2 "+mid2+"\">"+n.name+"</a></td></tr>");
			}
	        replyMSG.append("</table></body></html>");
		}
		else if (lvl2 != 0 && npc2 == null)
		{
			replyMSG.append("<html><title>Select second mob to fight</title>");
			replyMSG.append("<body><table>");
			L2NpcTemplate[] npcs = NpcTable.getInstance().getAllOfLevel(lvl2);
			for (L2NpcTemplate n : npcs) {
				replyMSG.append("<tr><td><a action=\"bypass -h admin_fight_calculator lvl1 "+lvl1+" lvl2 "+lvl2+" mid1 "+mid1+" mid2 "+n.npcId+"\">"+n.name+"</a></td></tr>");
			}
	        replyMSG.append("</table></body></html>");
		}
		else {
			replyMSG.append("<html><title>Select mobs to fight</title>");
			replyMSG.append("<body>");
			replyMSG.append("<table>");
			replyMSG.append("<tr><td>First</td><td>Second</td></tr>");
			replyMSG.append("<tr><td><edit var=\"lvl1\" width=80></td><td><edit var=\"lvl2\" width=80></td></tr>");
	        replyMSG.append("</table>");
	        replyMSG.append("<center><br><br><br>");
	        replyMSG.append("<button value=\"OK\" action=\"bypass -h admin_fight_calculator lvl1 $lvl1 lvl2 $lvl2\"  width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
	        replyMSG.append("</center>");
	        replyMSG.append("</body></html>");
		}

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}


	private void handleShow(String params, L2PcInstance activeChar) {
		Formulas f = Formulas.getInstance();
		params = params.trim();

		L2Character npc1 = null;
		L2Character npc2 = null;
		if (params.length() == 0)
		{
			npc1 = activeChar;
			npc2 = (L2Character)activeChar.getTarget();
			if(npc2 == null)
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
				return;
			}
		}
		else
		{
			int mid1 = 0;
			int mid2 = 0;
			StringTokenizer st = new StringTokenizer(params);
			mid1 = Integer.parseInt(st.nextToken());
			mid2 = Integer.parseInt(st.nextToken());

			npc1 = new L2MonsterInstance(IdFactory.getInstance().getNextId(),
					NpcTable.getInstance().getTemplate(mid1));
			npc2 = new L2MonsterInstance(IdFactory.getInstance().getNextId(),
					NpcTable.getInstance().getTemplate(mid2));
		}

		int miss1 = 0;
		int miss2 = 0;
		int shld1 = 0;
		int shld2 = 0;
		int crit1 = 0;
		int crit2 = 0;
		double patk1 = 0;
		double patk2 = 0;
		double pdef1 = 0;
		double pdef2 = 0;
		double dmg1 = 0;
		double dmg2 = 0;


		// ATTACK speed in milliseconds
		int sAtk1 = npc1.calculateTimeBetweenAttacks(npc2, null);
		int sAtk2 = npc2.calculateTimeBetweenAttacks(npc1, null);
		// number of ATTACK per 100 seconds
		sAtk1 = 100000 / sAtk1;
		sAtk2 = 100000 / sAtk2;

		for (int i=0; i < 10000; i++)
		{
			boolean _miss1 = f.calcHitMiss(npc1, npc2);
			if (_miss1) miss1++;
			boolean _shld1 = Formulas.calcShldUse(npc1, npc2);
			if (_shld1) shld1++;
			boolean _crit1 = Formulas.calcCrit(npc1.getCriticalHit(npc2, null));
			if (_crit1) crit1++;

			double _patk1 = npc1.getPAtk(npc2);
			_patk1 += Rnd.nextDouble()* npc1.getRandomDamage(npc2);
			patk1 += _patk1;

			double _pdef1 = npc1.getPDef(npc2);
			pdef1 += _pdef1;

			if (!_miss1) {
				npc1.setAttackingBodypart();
				double _dmg1 = Formulas.calcPhysDam(npc1, npc2, null, _shld1, _crit1, false, false);
				dmg1 += _dmg1;
				npc1.abortAttack();
			}
		}

		for (int i=0; i < 10000; i++)
		{
			boolean _miss2 = f.calcHitMiss(npc2, npc1);
			if (_miss2) miss2++;
			boolean _shld2 = Formulas.calcShldUse(npc2, npc1);
			if (_shld2) shld2++;
			boolean _crit2 = Formulas.calcCrit(npc2.getCriticalHit(npc1, null));
			if (_crit2) crit2++;

			double _patk2 = npc2.getPAtk(npc1);
			_patk2 += Rnd.nextDouble()* npc2.getRandomDamage(npc1);
			patk2 += _patk2;

			double _pdef2 = npc2.getPDef(npc1);
			pdef2 += _pdef2;

			if (!_miss2) {
				npc2.setAttackingBodypart();
				double _dmg2 = Formulas.calcPhysDam(npc2, npc1, null, _shld2, _crit2, false, false);
				dmg2 += _dmg2;
				npc2.abortAttack();
			}
		}

		miss1 /= 100;
		miss2 /= 100;
		shld1 /= 100;
		shld2 /= 100;
		crit1 /= 100;
		crit2 /= 100;
		patk1 /= 10000;
		patk2 /= 10000;
		pdef1 /= 10000;
		pdef2 /= 10000;
		dmg1  /= 10000;
		dmg2  /= 10000;

		// total damage per 100 seconds
		int tdmg1 = (int)(sAtk1 * dmg1);
		int tdmg2 = (int)(sAtk2 * dmg2);
		// HP restored per 100 seconds
		double maxHp1 = npc1.getMaxHp();
		int hp1 = (int)(f.calcHpRegen(npc1) * 100000 / f.getRegeneratePeriod(npc1));

		double maxHp2 = npc2.getMaxHp();
		int hp2 = (int)(f.calcHpRegen(npc2) * 100000 / f.getRegeneratePeriod(npc2));

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuilder replyMSG = new StringBuilder();
		replyMSG.append("<html><title>Selected mobs to fight</title>");
		replyMSG.append("<body>");
		replyMSG.append("<table>");
		if (params.length() == 0) {
			replyMSG.append("<tr><td width=140>Parameter</td><td width=70>me</td><td width=70>target</td></tr>");
		} else {
			replyMSG.append("<tr><td width=140>Parameter</td><td width=70>"+((L2NpcTemplate)npc1.getTemplate()).name+
					"</td><td width=70>"+((L2NpcTemplate)npc2.getTemplate()).name+"</td></tr>");
		}
		replyMSG.append("<tr><td>miss</td><td>"+miss1+"%</td><td>"+miss2+"%</td></tr>");
		replyMSG.append("<tr><td>shld</td><td>"+shld2+"%</td><td>"+shld1+"%</td></tr>");
		replyMSG.append("<tr><td>crit</td><td>"+crit1+"%</td><td>"+crit2+"%</td></tr>");
		replyMSG.append("<tr><td>pAtk / pDef</td><td>"+((int)patk1)+" / "+((int)pdef1)+"</td><td>"+((int)patk2)+" / "+((int)pdef2)+"</td></tr>");
		replyMSG.append("<tr><td>made hits</td><td>"+sAtk1+"</td><td>"+sAtk2+"</td></tr>");
		replyMSG.append("<tr><td>dmg per hit</td><td>"+((int)dmg1)+"</td><td>"+((int)dmg2)+"</td></tr>");
		replyMSG.append("<tr><td>got dmg</td><td>"+tdmg2+"</td><td>"+tdmg1+"</td></tr>");
		replyMSG.append("<tr><td>got regen</td><td>"+hp1+"</td><td>"+hp2+"</td></tr>");
		replyMSG.append("<tr><td>had HP</td><td>"+(int)maxHp1+"</td><td>"+(int)maxHp2+"</td></tr>");
		replyMSG.append("<tr><td>die</td>");
		if (tdmg2 - hp1 > 1)
			replyMSG.append("<td>"+(int)(100*maxHp1/(tdmg2 - hp1))+" sec</td>");
		else
			replyMSG.append("<td>never</td>");
		if (tdmg1 - hp2 > 1)
			replyMSG.append("<td>"+(int)(100*maxHp2/(tdmg1 - hp2))+" sec</td>");
		else
			replyMSG.append("<td>never</td>");
		replyMSG.append("</tr>");
        replyMSG.append("</table>");
        replyMSG.append("<center><br>");
		if (params.length() == 0) {
			replyMSG.append("<button value=\"Retry\" action=\"bypass -h admin_fight_calculator_show\"  width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		} else {
			replyMSG.append("<button value=\"Retry\" action=\"bypass -h admin_fight_calculator_show "+((L2NpcTemplate)npc1.getTemplate()).npcId+" "+((L2NpcTemplate)npc2.getTemplate()).npcId+"\"  width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		}
        replyMSG.append("</center>");
        replyMSG.append("</body></html>");
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);

		if (params.length() != 0)
		{
			((L2MonsterInstance)npc1).deleteMe();
			((L2MonsterInstance)npc2).deleteMe();
		}
	}

}
