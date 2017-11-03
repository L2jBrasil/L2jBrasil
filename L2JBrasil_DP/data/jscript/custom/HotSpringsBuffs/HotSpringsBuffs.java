/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package custom.HotSpringsBuffs;

import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.State;
import com.it.br.util.Rnd;

public class HotSpringsBuffs extends Quest
{
	private static final int[] HSMOBS = {21316, 21321, 21314, 21319};

	private State CREATED;
	
	public HotSpringsBuffs(int questid, String name, String descr)
	{
		super(questid, name, descr);
		CREATED = new State("Start", this);
		this.setInitialState(CREATED);
		for(int NPC_ID : HSMOBS)
		{
			addAttackId(NPC_ID);
		}
	}

	public String onAttack(L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		int npcId = npc.getNpcId();
		for(int NPC_ID : HSMOBS)
		{
			if(npcId == NPC_ID)
			{
				if(Rnd.get(2) == 1)
				{
					if(Rnd.get(2) == 1)
					{
						if(attacker.getFirstEffect(4552) != null)
						{
							int holera = attacker.getFirstEffect(4552).getLevel();
							if(Rnd.get(100) < 30)
								if(holera < 10)
								{
									int newholera = holera + 1;
									npc.setTarget(attacker);
									npc.doCast(SkillTable.getInstance().getInfo(4552,newholera));
								}
						}
						else
						{
							npc.setTarget(attacker);
							npc.doCast(SkillTable.getInstance().getInfo(4552,1));
						}
					}
					else
					{
						if(attacker.getFirstEffect(4554) != null)
						{
							int malaria = attacker.getFirstEffect(4554).getLevel();
							if(Rnd.get(100) < 15)
								if(malaria < 10)
								{
									int newmalaria = malaria + 1;
									npc.setTarget(attacker);
									npc.doCast(SkillTable.getInstance().getInfo(4554,newmalaria));
								}
						}
						else
						{
							npc.setTarget(attacker);
							npc.doCast(SkillTable.getInstance().getInfo(4554,1));
						}
					}
				}
				else
				{
					if(Rnd.get(2) == 1)
					{
						if(attacker.getFirstEffect(4551) != null)
						{
							int rheumatism = attacker.getFirstEffect(4551).getLevel();
							if(Rnd.get(100) < 30)
								if(rheumatism < 10)
								{
									int newrheumatism = rheumatism + 1;
									npc.setTarget(attacker);
									npc.doCast(SkillTable.getInstance().getInfo(4551,newrheumatism));
								}
						}
						else
						{
							npc.setTarget(attacker);
							npc.doCast(SkillTable.getInstance().getInfo(4551,1));
						}
					}
					else
					{
						if(attacker.getFirstEffect(4553) != null)
						{
							int flu = attacker.getFirstEffect(4553).getLevel();
							if(Rnd.get(100) < 15)
								if(flu < 10)
								{
									int newflu = flu + 1;
									npc.setTarget(attacker);
									npc.doCast(SkillTable.getInstance().getInfo(4553,newflu));
								}
						}
						else
						{
							npc.setTarget(attacker);
							npc.doCast(SkillTable.getInstance().getInfo(4553,1));
						}
					}
				}
			}
			else
				return null;
		}
		return null;
	}

	public static void main(String[] args)
	{
		new HotSpringsBuffs(8009, "HotSpringsBuffs", "custom");
	}
}