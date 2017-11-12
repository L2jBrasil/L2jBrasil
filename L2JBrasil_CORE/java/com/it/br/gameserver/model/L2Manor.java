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
package com.it.br.gameserver.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.it.br.Config;
import com.it.br.configuration.Configurator;
import com.it.br.configuration.settings.ServerSettings;
import com.it.br.gameserver.datatables.sql.ItemTable;
import com.it.br.gameserver.templates.L2Item;

/**
 * Service class for manor
 * @author l3x
 */

public class L2Manor
{
	private static Logger _log = Logger.getLogger(L2Manor.class.getName());
    private static L2Manor _instance;

    private static Map<Integer,SeedData> _seeds;

    public L2Manor() {
		_seeds = new ConcurrentHashMap<>();
		parseData();
	}

    public static L2Manor getInstance() {
		if (_instance == null) {
			_instance = new L2Manor();
		}
		return _instance;
	}

    public List<Integer> getAllCrops() {
    	List<Integer> crops = new ArrayList<>();

    	for (SeedData seed: _seeds.values()) {
    		if (!crops.contains(seed.getCrop()) && seed.getCrop() != 0 && !crops.contains(seed.getCrop())) {
    			crops.add(seed.getCrop());
    		}
    	}

    	return crops;
    }

    public int getSeedBasicPrice (int seedId) {
    	L2Item seedItem = ItemTable.getInstance().getTemplate(seedId);

    	if (seedItem != null) {
    		return seedItem.getReferencePrice();
    	} else {
    		return 0;
    	}
    }

    public int getSeedBasicPriceByCrop (int cropId) {
    	for (SeedData seed: _seeds.values()) {
    		if (seed.getCrop() == cropId)
    			return getSeedBasicPrice(seed.getId());
    	}
    	return 0;
    }

    public int getCropBasicPrice (int cropId) {
    	L2Item cropItem = ItemTable.getInstance().getTemplate(cropId);

    	if (cropItem != null)
    		return cropItem.getReferencePrice();
    	else
    		return 0;
    }

    public int getMatureCrop (int cropId) {
    	for (SeedData seed: _seeds.values()) {
    		if (seed.getCrop() == cropId)
    			return seed.getMature();
    	}
    	return 0;
    }

    /**
     * Returns price which lord pays to buy one seed
     * @param seedId
     * @return seed price
     */
    public int getSeedBuyPrice (int seedId) {
    	int buyPrice = getSeedBasicPrice(seedId) / 10;
    	return (buyPrice > 0?buyPrice:1);
    }

    public int getSeedMinLevel(int seedId)
    {
		SeedData seed = _seeds.get(seedId);

		if (seed != null)
			return seed.getLevel() - 5;
		return -1;
	}

	public int getSeedMaxLevel(int seedId) {
		SeedData seed = _seeds.get(seedId);

		if (seed != null)
			return seed.getLevel() + 5;
		return -1;
	}

	public int getSeedLevelByCrop (int cropId) {
		for (SeedData seed: _seeds.values()) {
    		if (seed.getCrop() == cropId) {
    			return seed.getLevel();
    		}
    	}
    	return 0;
	}

    public int getSeedLevel(int seedId) {
		SeedData seed = _seeds.get(seedId);

		if (seed != null) {
			return seed.getLevel();
		}
		return -1;
	}

    public boolean isAlternative(int seedId) {
    	for (SeedData seed: _seeds.values()) {
    		if (seed.getId() == seedId) {
    			return seed.isAlternative();
    		}
    	}
    	return false;
    }

    public int getCropType(int seedId) {
		SeedData seed = _seeds.get(seedId);

		if (seed != null)
			return seed.getCrop();
		return -1;
	}

    public synchronized int getRewardItem(int cropId, int type) {
		for (SeedData seed : _seeds.values()) {
			if (seed.getCrop() == cropId) {
				return seed.getReward(type); // there can be several
												// seeds with same crop, but
												// reward should be the same for
												// all
			}
		}
		return -1;
	}

    public synchronized int getRewardItemBySeed (int seedId, int type) {
    	SeedData seed = _seeds.get(seedId);

		if (seed != null) {
			return seed.getReward(type);
		}
		return 0;
    }

    /**
	 * Return all crops which can be purchased by given castle
	 *
	 * @param castleId
	 * @return
	 */
    public List<Integer> getCropsForCastle(int castleId) {
		List<Integer> crops = new ArrayList<>();

		for (SeedData seed : _seeds.values()) {
			if (seed.getManorId() == castleId && !crops.contains(seed.getCrop())) {
				crops.add(seed.getCrop());
			}
		}

		return crops;
	}
    /**
     * Return list of seed ids, which belongs to castle with given id
     * @param castleId - id of the castle
     * @return seedIds - list of seed ids
     */
    public List<Integer> getSeedsForCastle(int castleId) {
		List<Integer> seedsID = new ArrayList<>();

		for (SeedData seed : _seeds.values()) {
			if (seed.getManorId() == castleId && !seedsID.contains(seed.getId())) {
				seedsID.add(seed.getId());
			}
		}

		return seedsID;
	}

    /**
     * Returns castle id where seed can be sowned<br>
     * @param seedId
     * @return castleId
     */
    public int getCastleIdForSeed(int seedId) {
		SeedData seed = _seeds.get(seedId);

		if (seed != null) {
			return seed.getManorId();
		}
		return 0;
	}

    public int getSeedSaleLimit(int seedId) {
    	SeedData seed = _seeds.get(seedId);

    	if (seed != null) {
    		return seed.getSeedLimit();
    	}
    	return 0;
    }

    public int getCropPuchaseLimit(int cropId) {
    	for (SeedData seed : _seeds.values()) {
    		if (seed.getCrop() == cropId){
    			return seed.getCropLimit();
    		}
    	}
    	return 0;
    }



    private class SeedData {
        private int _id;
        private int _level;      // seed level
        private int _crop;           // crop type
        private int _mature;           // mature crop type
        private int _type1;
        private int _type2;
        private int _manorId;             // id of manor (castle id) where seed can be farmed
        private int _isAlternative;
        private int _limitSeeds;
        private int _limitCrops;

        public SeedData(int level ,int crop, int mature) {
        	this._level = level;
            this._crop = crop;
            this._mature = mature;
        }

        public void setData(int id, int t1, int t2, int manorId, int isAlt, int lim1, int lim2) {
            this._id = id;
            _type1 = t1;
            _type2 = t2;
            _manorId = manorId;
            _isAlternative = isAlt;
            _limitSeeds = lim1;
            _limitCrops = lim2;
        }

        public int getManorId () {
        	return _manorId;
        }

        public int getId() {
        	return _id;
        }


        public int getCrop() {
        	return _crop;
        }

        public int getMature() {
        	return _mature;
        }

        public int getReward(int type) {
        	return (type == 1?_type1:_type2);
        }

        public int getLevel() {
        	return _level;
        }

        public boolean isAlternative() {
        	return (_isAlternative == 1);
        }

        public int getSeedLimit() {
        	return _limitSeeds*Config.RATE_DROP_MANOR;
        }

        public int getCropLimit() {
        	return _limitCrops*Config.RATE_DROP_MANOR;
        }
    }


    private void parseData() {
		LineNumberReader lnr = null;
		try {
			ServerSettings serverSettings = Configurator.getSettings(ServerSettings.class);
			File seedData = new File(serverSettings.getDatapackDirectory(), "data/csv/seeds.csv");
			lnr = new LineNumberReader(new BufferedReader(new FileReader(
					seedData)));

			String line = null;
			while ((line = lnr.readLine()) != null) {
				if (line.trim().length() == 0 || line.startsWith("#")) {
					continue;
				}
				SeedData seed = parseList(line);
				_seeds.put(seed.getId(), seed);
			}

			_log.info("ManorManager: Loaded " + _seeds.size() + " seeds");
		} catch (FileNotFoundException e) {
			_log.info("seeds.csv is missing in data folder");
		} catch (Exception e) {
			_log.info("error while loading seeds: " + e.getMessage());
		} finally {
			try {
				lnr.close();
			} catch (Exception e1) {
			}
		}
	}

    private SeedData parseList(String line) {
        StringTokenizer st = new StringTokenizer(line, ";");

        int seedId     = Integer.parseInt(st.nextToken());  	// seed id
        int level      = Integer.parseInt(st.nextToken());		// seed level
        int cropId     = Integer.parseInt(st.nextToken());  	// crop id
        int matureId   = Integer.parseInt(st.nextToken());  	// mature crop id
        int type1R     = Integer.parseInt(st.nextToken());  	// type I reward
        int type2R     = Integer.parseInt(st.nextToken());  	// type II reward
        int manorId    = Integer.parseInt(st.nextToken());		// id of manor, where seed can be farmed
        int isAlt      = Integer.parseInt(st.nextToken());		// alternative seed
        int limitSeeds = Integer.parseInt(st.nextToken());		// limit for seeds
        int limitCrops = Integer.parseInt(st.nextToken());		// limit for crops

        SeedData seed = new SeedData(level, cropId, matureId);
        seed.setData(seedId, type1R, type2R, manorId, isAlt, limitSeeds, limitCrops);

        return seed;
    }
}
