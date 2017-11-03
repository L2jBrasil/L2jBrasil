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
package com.it.br.gameserver.datatables;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.it.br.Config;
import com.it.br.gameserver.datatables.sql.SkillTable;
import com.it.br.gameserver.model.L2Augmentation;
import com.it.br.gameserver.model.L2ItemInstance;
import com.it.br.gameserver.model.L2Skill;
import com.it.br.gameserver.skills.Stats;
import com.it.br.util.Rnd;

/**
 * This class manages the augmentation data and can also create new augmentations.
 *
 * @author durgus
 */
public class AugmentationData
{
	private static final Logger _log = Logger.getLogger(AugmentationData.class.getName());

	// =========================================================
	private static AugmentationData _instance;

	public static final AugmentationData getInstance()
	{
		if (_instance == null)
		{
		       _instance = new AugmentationData();
		}
		return _instance;
	}

	// =========================================================
	// Data Field

	// chances
	//private static final int CHANCE_STAT = 88;
	private static final int CHANCE_SKILL = Config.AUGSKILL_CHANCE;
	private static final int CHANCE_BASESTAT = 1;

	// stats
	private static final int STAT_START = 1;
	private static final int STAT_END = 14560;
	private static final int STAT_BLOCKSIZE = 3640;
	//private static final int STAT_NUMBEROF_BLOCKS = 4;
	private static final int STAT_SUBBLOCKSIZE = 91;
	//private static final int STAT_NUMBEROF_SUBBLOCKS = 40;

	// basestats
	private static final int BASESTAT_STR = 16341;
	private static final int BASESTAT_CON = 16342;
	private static final int BASESTAT_INT = 16343;
	private static final int BASESTAT_MEN = 16344;

	private List<?> _augmentationStats[];
	private List<augmentationSkill> _activeSkills;
	private List<augmentationSkill> _passiveSkills;
	private List<augmentationSkill> _chanceSkills;

	// =========================================================
	// Constructor
	public AugmentationData()
	{
		_log.info("Initializing AugmentationData.");

		_augmentationStats = new ArrayList[4];
		_augmentationStats[0] = new ArrayList<>();
		_augmentationStats[1] = new ArrayList<>();
		_augmentationStats[2] = new ArrayList<>();
		_augmentationStats[3] = new ArrayList<>();

		_activeSkills = new ArrayList<>();
		_passiveSkills = new ArrayList<>();
		_chanceSkills = new ArrayList<>();

		load();

		// Use size*4: since theres 4 blocks of stat-data with equivalent size
		_log.info("AugmentationData: Loaded: "+(_augmentationStats[0].size()*4)+" augmentation stats.");
		_log.info("AugmentationData: Loaded: "+_activeSkills.size()+" active, "+_passiveSkills.size()+" passive and "+_chanceSkills.size()+" chance skills");
	}

	// =========================================================
	// Nested Class

	public class augmentationSkill
	{
		private int _skillId;
		private int _maxSkillLevel;
		private int _augmentationSkillId;

		public augmentationSkill(int skillId, int maxSkillLevel, int augmentationSkillId)
		{
			_skillId = skillId;
			_maxSkillLevel = maxSkillLevel;
			_augmentationSkillId = augmentationSkillId;
		}

		public L2Skill getSkill(int level)
		{
			if (level > _maxSkillLevel)
			return SkillTable.getInstance().getInfo(_skillId, _maxSkillLevel);
			return SkillTable.getInstance().getInfo(_skillId, level);
		}

		public int getAugmentationSkillId()
		{
			return _augmentationSkillId;
		}
	}

	public class augmentationStat
	{
		private Stats _stat;
		private int _singleSize;
		private int _combinedSize;
		private float _singleValues[];
		private float _combinedValues[];

		public augmentationStat(Stats stat, float sValues[], float cValues[])
		{
			_stat = stat;
			_singleSize = sValues.length;
			_singleValues = sValues;
			_combinedSize = cValues.length;
			_combinedValues = cValues;
		}

		public int getSingleStatSize()
		{
			return _singleSize;
		}
		public int getCombinedStatSize()
		{
			return _combinedSize;
		}

		public float getSingleStatValue(int i)
		{
			if (i >= _singleSize || i < 0) return _singleValues[_singleSize-1];
			return _singleValues[i];
		}
		public float getCombinedStatValue(int i)
		{
			if (i >= _combinedSize || i < 0) return _combinedValues[_combinedSize-1];
			return _combinedValues[i];
		}

		public Stats getStat()
		{
			return _stat;
		}
	}

	// =========================================================
	// Method - Private

	@SuppressWarnings("unchecked") private final void load()
	{
		// Load the skillmap
		// Note: the skillmap data is only used when generating new augmentations
		// the client expects a different id in order to display the skill in the
		// items description...
		try
		{
			SkillTable st = SkillTable.getInstance();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);

			File file = new File(Config.DATAPACK_ROOT+"/data/stats/augmentation/augmentation_skillmap.xml");
			if (!file.exists())
			{
				if (Config.DEBUG)
				System.out.println("The augmentation skillmap file is missing.");
				return;
			}

			Document doc = factory.newDocumentBuilder().parse(file);

			for (Node n=doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n.getNodeName()))
				{
					for (Node d=n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if ("augmentation".equalsIgnoreCase(d.getNodeName()))
						{
							NamedNodeMap attrs = d.getAttributes();
							int skillId=0, augmentationId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
							String type="passive";

							for (Node cd=d.getFirstChild(); cd != null; cd = cd.getNextSibling())
							{
								if ("skillId".equalsIgnoreCase(cd.getNodeName()))
								{
									attrs = cd.getAttributes();
                            		skillId = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
								}
								else if ("type".equalsIgnoreCase(cd.getNodeName()))
								{
									attrs = cd.getAttributes();
                            		type = attrs.getNamedItem("val").getNodeValue();
								}
							}

							if (type.equalsIgnoreCase("active")) _activeSkills.add(new augmentationSkill(skillId, st.getMaxLevel(skillId, 1), augmentationId));
							else if (type.equalsIgnoreCase("passive")) _passiveSkills.add(new augmentationSkill(skillId, st.getMaxLevel(skillId, 1), augmentationId));
							else _chanceSkills.add(new augmentationSkill(skillId, st.getMaxLevel(skillId, 1), augmentationId));
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Error parsing augmentation_skillmap.xml.", e);
			return ;
		}



		// Load the stats from xml
		for (int i=1; i<5; i++)
		{
			try
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setValidating(false);
				factory.setIgnoringComments(true);

				File file = new File(Config.DATAPACK_ROOT+"/data/stats/augmentation/augmentation_stats"+i+".xml");
				if (!file.exists())
				{
					if (Config.DEBUG)
					System.out.println("The augmentation stat data file "+i+" is missing.");
					return;
				}

				Document doc = factory.newDocumentBuilder().parse(file);

				for (Node n=doc.getFirstChild(); n != null; n = n.getNextSibling())
				{
					if ("list".equalsIgnoreCase(n.getNodeName()))
					{
						for (Node d=n.getFirstChild(); d != null; d = d.getNextSibling())
						{
							if ("stat".equalsIgnoreCase(d.getNodeName()))
							{
								NamedNodeMap attrs = d.getAttributes();
								String statName = attrs.getNamedItem("name").getNodeValue();
								float soloValues[]=null, combinedValues[]=null;

								for (Node cd=d.getFirstChild(); cd != null; cd = cd.getNextSibling())
								{
									if ("table".equalsIgnoreCase(cd.getNodeName()))
									{
										attrs = cd.getAttributes();
										String tableName = attrs.getNamedItem("name").getNodeValue();

										StringTokenizer data = new StringTokenizer(cd.getFirstChild().getNodeValue());
										List<Float> array = new ArrayList<>();
										while (data.hasMoreTokens())
											array.add(Float.parseFloat(data.nextToken()));

										if (tableName.equalsIgnoreCase("#soloValues"))
										{
											soloValues = new float[array.size()];
											int x=0;
											for (float value : array)
												soloValues[x++] = value;
										}
										else
										{
											combinedValues = new float[array.size()];
											int x=0;
											for (float value : array)
												combinedValues[x++] = value;
										}
									}
								}
								// store this stat
								((List<augmentationStat>)_augmentationStats[(i-1)]).add(new augmentationStat(Stats.valueOfXml(statName), soloValues, combinedValues));
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error parsing augmentation_stats"+i+".xml.", e);
				return ;
			}
		}
	}

	// =========================================================
	// Properties - Public

	/**
	 * Generate a new random augmentation
	 * @param item
	 * @param lifeStoneLevel
	 * @param lifeStoneGrade
	 * @return L2Augmentation
	 */
	public L2Augmentation generateRandomAugmentation(L2ItemInstance item, int lifeStoneLevel, int lifeStoneGrade)
	{
		// Note that stat12 stands for stat 1 AND 2 (same for stat34 ;p )
		// this is because a value can contain up to 2 stat modifications
		// (there are two short values packed in one integer value, meaning 4 stat modifications at max)
		// for more info take a look at getAugStatsById(...)

		// Note: lifeStoneGrade: (0 means low grade, 3 top grade)
		// First: decide which grade the augmentation result is going to have:
		// 0:yellow, 1:blue, 2:purple, 3:red
		int resultColor = 0;
		// The chances used here are most likely custom,
		// whats known is: u can also get a red result from a normal grade lifeStone
		// however I will make it so that a higher grade lifeStone will more likely result in a
		// higher grade augmentation... and the augmentation result will at least have the grade
		// of the life stone
		resultColor = Rnd.get(0, 100);
		if (lifeStoneGrade == 3 || resultColor <= (15*lifeStoneGrade)+10) resultColor = 3;
		else if (lifeStoneGrade == 2 || resultColor <= (15*lifeStoneGrade)+20) resultColor = 2;
		else if (lifeStoneGrade == 1 || resultColor <= (15*lifeStoneGrade)+30) resultColor = 1;
		else resultColor = 0;

		// Second: Calculate the subblock offset for the choosen color,
		// and the level of the lifeStone
		int colorOffset = (resultColor*(STAT_SUBBLOCKSIZE*10))
							+((lifeStoneLevel-1)*STAT_SUBBLOCKSIZE);

		int offset = ((3-lifeStoneGrade)*STAT_BLOCKSIZE)+colorOffset;

		int stat12 = Rnd.get(offset, offset+STAT_SUBBLOCKSIZE);
		int stat34 = 0;
		boolean generateSkill=false;

		// use a chance to determine whether we will add a skill or not
		if (Rnd.get(1, 100) <= CHANCE_SKILL) generateSkill = true;
		// only if no skill is going to be applyed
		else if (Rnd.get(1, 100) <= CHANCE_BASESTAT) stat34 = Rnd.get(BASESTAT_STR, BASESTAT_MEN);

		// is neither a skill nor basestat used for stat34? then generate a normal stat
		if (stat34 == 0 && !generateSkill)
		{
			offset = (lifeStoneGrade*STAT_BLOCKSIZE)+colorOffset;

			stat34 = Rnd.get(offset, offset+STAT_SUBBLOCKSIZE);
		}

		// generate a skill if neccessary
		L2Skill skill = null;
		if (generateSkill)
		{
			augmentationSkill temp=null;
			switch (Rnd.get(1,3))
			{
				case 1:	// chance skill
					temp = _chanceSkills.get(Rnd.get(0,_chanceSkills.size()-1));
					skill = temp.getSkill(lifeStoneLevel);
					stat34 = temp.getAugmentationSkillId();
					break;
				case 2: // active skill
					temp = _activeSkills.get(Rnd.get(0,_activeSkills.size()-1));
					skill = temp.getSkill(lifeStoneLevel);
					stat34 = temp.getAugmentationSkillId();
					break;
				case 3: // passive skill
					temp = _passiveSkills.get(Rnd.get(0,_passiveSkills.size()-1));
					skill = temp.getSkill(lifeStoneLevel);
					stat34 = temp.getAugmentationSkillId();
					break;
			}
		}

		return new L2Augmentation(item, ((stat34<<16)+stat12), skill, true);
	}

	public class AugStat
	{
		private Stats _stat;
		private float _value;
		public AugStat(Stats stat, float value)
		{
			_stat = stat; _value = value;
		}
		public Stats getStat() { return _stat; }
		public float getValue() { return _value; }
	}
	/**
	 * Returns the stat and basestat boni for a given augmentation id
	 * @param augmentationId
	 * @return
	 */
	public List<AugStat> getAugStatsById(int augmentationId)
	{
		List <AugStat> temp = new ArrayList<>();
		// An augmentation id contains 2 short vaues so we gotta seperate them here
		// both values contain a number from 1-16380, the first 14560 values are stats
		// the 14560 stats are devided into 4 blocks each holding 3640 values
		// each block contains 40 subblocks holding 91 stat values
		// the first 13 values are so called Solo-stats and they have the highest stat increase possible
		// after the 13 Solo-stats come 78 combined stats (thats every possible combination of the 13 solo stats)
		// the first 12 combined stats (14-26) is the stat 1 combined with stat 2-13
		// the next 11 combined stats then are stat 2 combined with stat 3-13 and so on...
		// to get the idea have a look @ optiondata_client-e.dat - thats where the data came from :)
		int stats[] = new int[2];
		stats[0] = 0x0000FFFF&augmentationId;
		stats[1] = (augmentationId>>16);

		for (int i=0; i<2; i++)
		{
			// its a stat
			if (stats[i] >= STAT_START && stats[i] <= STAT_END)
			{
				int block=0;
				while (stats[i] > STAT_BLOCKSIZE)
				{
					stats[i]-=STAT_BLOCKSIZE;
					block++;
				}

				int subblock=0;
				while (stats[i] > STAT_SUBBLOCKSIZE)
				{
					stats[i]-=STAT_SUBBLOCKSIZE;
					subblock++;
				}

				if (stats[i] < 14) // solo stat
				{
					augmentationStat as = ((augmentationStat)_augmentationStats[block].get((stats[i]-1)));
					temp.add(new AugStat(as.getStat(), as.getSingleStatValue(subblock)));
				}
				else // twin stat
				{
					stats[i]-=13;		// rescale to 0 (if first of first combined block)
					int x = 12;			// next combi block has 12 stats
					int rescales = 0;	// number of rescales done

					while (stats[i] > x)
					{
						stats[i]-=x;
						x--;
						rescales++;
					}
					// get first stat
					augmentationStat as = ((augmentationStat)_augmentationStats[block].get(rescales));
					if (rescales == 0)
						temp.add(new AugStat(as.getStat(), as.getCombinedStatValue(subblock)));
					else
						temp.add(new AugStat(as.getStat(), as.getCombinedStatValue((subblock*2)+1)));

					// get 2nd stat
					as = ((augmentationStat)_augmentationStats[block].get(rescales+stats[i]));
					if (as.getStat() == Stats.CRITICAL_DAMAGE)
						temp.add(new AugStat(as.getStat(), as.getCombinedStatValue(subblock)));
					else
						temp.add(new AugStat(as.getStat(), as.getCombinedStatValue(subblock*2)));
				}
			}
			// its a base stat
			else if (stats[i] >= BASESTAT_STR && stats[i] <= BASESTAT_MEN)
			{
				switch (stats[i])
				{
					case BASESTAT_STR:
						temp.add(new AugStat(Stats.STAT_STR, 1.0f));
						break;
					case BASESTAT_CON:
						temp.add(new AugStat(Stats.STAT_CON, 1.0f));
						break;
					case BASESTAT_INT:
						temp.add(new AugStat(Stats.STAT_INT, 1.0f));
						break;
					case BASESTAT_MEN:
						temp.add(new AugStat(Stats.STAT_MEN, 1.0f));
						break;
				}
			}
		}

		return temp;
	}
}