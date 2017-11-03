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
package custom.NpcLocationInfo;

import com.it.br.gameserver.datatables.sql.SpawnTable;
import com.it.br.gameserver.model.actor.instance.L2NpcInstance;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.quest.Quest;
import com.it.br.gameserver.model.quest.QuestState;
import com.it.br.gameserver.model.L2Spawn;
import com.it.br.gameserver.util.Util;

public class NpcLocationInfo extends Quest
{
	private static final String qn = "NpcLocationInfo";

	private static final int[] NPC =
	{
		30598, 30599, 30600, 30601, 30602
	};

	private static final int[] NPCRADAR = 
	{
		// Talking Island
		30006,	//Gatekeeper Roxxy
		30039,	//Captain Gilbert
		30040,	//Guard Leon
		30041,	//Guard Arnold
		30042,	//Guard Abellos
		30043,	//Guard Johnstone
		30044,	//Guard Chiperan
		30045,	//Guard Kenyos
		30046,	//Guard Hanks
		30283,	//Blacksmith Altran
		30003,	//Trader Silvia
		30004,	//Trader Katerina
		30001,	//Trader Lector
		30002,	//Trader Jackson
		30031,	//High Priest Biotin
		30033,	//Magister Baulro
		30035,	//Magister Harrys
		30032,	//Priest Yohanes
		30036,	//Priest Petron
		30026,	//Grand Master Bitz
		30027,	//Master Gwinter
		30029,	//Master Minia
		30028,	//Master Pintage
		30054,	//Warehouse Keeper Rant
		30055,	//Warehouse Keeper Rolfe
		30005,	//Warehouse Keeper Wilford
		30048,	//Darin
		30312,	//Lighthouse Keeper Rockswell
		30368,	//Lilith
		30049,	//Bonnie
		30047,	//Wharf Manager Firon
		30497,	//Edmond
		30050,	//Elias
		30311,	//Sir Collin Windawood
		30051,	//Cristel

		// Dark Elf Village
		30134,	//Gatekeeper Jasmine
		30224,	//Sentry Knight Rayla
		30348,	//Sentry Nelsya
		30355,	//Sentry Roselyn
		30347,	//Sentry Marion
		30432,	//Sentry Irene
		30356,	//Sentry Altima
		30349,	//Sentry Jenna
		30346,	//Sentry Kayleen
		30433,	//Sentry Kathaway
		30357,	//Sentry Kristin
		30431,	//Sentry Eriel
		30430,	//Sentry Trionell
		30307,	//Blacksmith Karrod
		30138,	//Trader Minaless
		30137,	//Trader Vollodos
		30135,	//Trader Iria
		30136,	//Trader Payne
		30143,	//Master Trudy
		30360,	//Master Harant
		30145,	//Master Vlasty
		30135,	//Magister Harne
		30144,	//Tetrarch Vellior
		30358,	//Tetrarch Thifiell
		30359,	//Tetrarch Kaitar
		30141,	//Tetrarch Talloth
		30139,	//Warehouse Keeper Dorankus 
		30140,	//Warehouse Keeper Erviante 
		30350,	//Warehouse Freightman Carlon
		30421,	//Varika
		30419,	//Arkenia
		30130,	//Abyssal Celebrant Undrias
		30351,	//Astaron
		30353,	//Jughead
		30354,	//Jewel

		// Elven Village
		30146,	//Gatekeeper Mirabel
		30285,	//Sentinel Gartrandell
		30284,	//Sentinel Knight Alberius
		30221,	//Sentinel Rayen
		30217,	//Sentinel Berros
		30219,	//Sentinel Veltress
		30220,	//Sentinel Starden
		30218,	//Sentinel Kendell
		30216,	//Sentinel Wheeler
		30363,	//Blacksmith Aios
		30149,	//Trader Creamees
		30150,	//Trader Herbiel
		30148,	//Trader Ariel
		30147,	//Trader Unoren
		30155,	//Master Ellenia
		30156,	//Master Cobendell
		30157,	//Magister Greenis
		30158,	//Magister Esrandell
		30154,	//Hierarch Asterios
		30153,	//Warehouse Keeper Markius
		30152,	//Warehouse Keeper Julia
		30151,	//Warehouse Freightman Chad
		30423,	//Northwind
		30414,	//Rosella
		31853,	//Treant Bremec
		30223,	//Arujien
		30362,	//Andellia
		30222,	//Alshupes
		30371,	//Thalia
		31852,	//Pixy Murika

		// Dwarven Village
		30540,	//Gatekeeper Wirphy
		30541,	//Protector Paion
		30542,	//Defender Runant
		30543,	//Defender Ethan
		30544,	//Defender Cromwell
		30545,	//Defender Proton
		30546,	//Defender Dinkey
		30547,	//Defender Tardyon
		30548,	//Defender Nathan
		30531,	//Iron Gate's Lockirin
		30532,	//Golden Wheel's Spiron
		30533,	//Silver Scale's Balanki
		30534,	//Bronze Key's Keef
		30535,	//Filaur of the Gray Pillar
		30536,	//Black Anvil's Arin
		30525,	//Head Blacksmith Bronk
		30526,	//Blacksmith Brunon
		30527,	//Blacksmith Silvera
		30518,	//Trader Garita
		30519,	//Trader Mion
		30516,	//Trader Reep
		30517,	//Trader Shari
		30520,	//Warehouse Chief Reed
		30521,	//Warehouse Freightman Murdoc
		30522,	//Warehouse Keeper Airy
		30523,	//Collector Gouph
		30524,	//Collector Pippi
		30537,	//Daichir, Priest of the Eart
		30650,	//Priest of the Earth Gerald
		30538,	//Priest of the Earth Zimenf
		30539,	//Priestess of the Earth Chichirin
		30671,	//Captain Croto
		30651,	//Wanderer Dorf
		30550,	//Gauri Twinklerock
		30554,	//Miner Bolter
		30553,	//Maryse Redbonnet

		// Orc Village
		30576,	//Gatekeeper Tamil
		30577,	//Praetorian Rukain
		30578,	//Centurion Nakusin
		30579,	//Centurion Tamai
		30580,	//Centurion Parugon
		30581,	//Centurion Orinak
		30582,	//Centurion Tiku
		30583,	//Centurion Petukai
		30584,	//Centurion Vapook
		30569,	//Prefect Brukurse
		30570,	//Prefect Karukia
		30571,	//Seer Tanapi
		30572,	//Seer Livina
		30564,	//Blacksmith Sumari
		30560,	//Trader Uska
		30561,	//Trader Papuma
		30558,	//Trader Jakal
		30559,	//Trader Kunai
		30562,	//Warehouse Keeper Grookin
		30563,	//Warehouse Keeper Imantu
		30565,	//Flame Lord Kakai
		30566,	//Atuba Chief Varkees
		30567,	//Neruga Chief Tantus
		30568,	//Urutu Chief Hatos
		30585,	//Tataru Zu Hestui
		30587,	//Gantaki Zu Urutu
	};

	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);

		if(st == null)
			return htmltext;

		if(Util.isDigit(event))
		{
			htmltext = null;
			int npcId = Integer.parseInt(event);

			if(Util.contains(NPCRADAR, npcId))
			{
				int x = 0, y = 0, z = 0;
				for(L2Spawn spawn : SpawnTable.getInstance().getSpawnTable().values())
				{
					if(npcId == spawn.getNpcid())
					{
						x = spawn.getLocx();
						y = spawn.getLocy();
						z = spawn.getLocz();
						break;
					}
				}
				st.addRadar(x, y,z);
				htmltext = "MoveToLoc.htm";
			}
			st.exitQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		int npcId = npc.getNpcId();

		if(Util.contains(NPC, npcId))
			htmltext = String.valueOf(npcId) + ".htm";

		return htmltext;
	}

	public NpcLocationInfo(int id, String name, String descr)
	{
		super(id, name, descr);

		for(int i : NPC)
		{
			addStartNpc(i);
			addTalkId(i);
		}
	}

	public static void main(String args[])
	{
		new NpcLocationInfo(-1, qn, "custom");
	}
}