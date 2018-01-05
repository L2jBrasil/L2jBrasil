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
package com.it.br.gameserver.instancemanager;

import com.it.br.Config;
import com.it.br.gameserver.ThreadPoolManager;
import com.it.br.gameserver.database.L2DatabaseFactory;
import com.it.br.gameserver.datatables.sql.ClanTable;
import com.it.br.gameserver.model.*;
import com.it.br.gameserver.model.actor.instance.L2PcInstance;
import com.it.br.gameserver.model.entity.Castle;
import com.it.br.gameserver.network.SystemMessageId;
import com.it.br.gameserver.network.serverpackets.SystemMessage;
import com.it.br.util.Rnd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

/**
 * Class For Castle Manor Manager Load manor data from DB Update/Reload/Delete
 * Handles all schedule for manor
 * @author l3x
 */

public class CastleManorManager {
	protected static Logger _log = Logger.getLogger(CastleManorManager.class.getName());

	private static CastleManorManager _instance;

	public static final int PERIOD_CURRENT = 0;
	public static final int PERIOD_NEXT = 1;

	private static final String CASTLE_MANOR_LOAD_PROCURE =
											"SELECT * FROM castle_manor_procure WHERE castle_id=?";
	private static final String CASTLE_MANOR_LOAD_PRODUCTION =
											"SELECT * FROM castle_manor_production WHERE castle_id=?";

	private static final int NEXT_PERIOD_APPROVE = Config.ALT_MANOR_APPROVE_TIME;       // 6:00
	private static final int NEXT_PERIOD_APPROVE_MIN = Config.ALT_MANOR_APPROVE_MIN;    //
    private static final int MANOR_REFRESH = Config.ALT_MANOR_REFRESH_TIME;             // 20:00
    private static final int MANOR_REFRESH_MIN = Config.ALT_MANOR_REFRESH_MIN;          //
    protected static final long MAINTENANCE_PERIOD = Config.ALT_MANOR_MAINTENANCE_PERIOD; // 6 mins

    private Calendar _manorRefresh;
    private Calendar _periodApprove;

    private boolean _underMaintenance;
    private boolean _disabled;

    @SuppressWarnings("rawtypes")
	protected ScheduledFuture _scheduledManorRefresh;
    @SuppressWarnings("rawtypes")
	protected ScheduledFuture _scheduledMaintenanceEnd;
    @SuppressWarnings("rawtypes")
	protected ScheduledFuture _scheduledNextPeriodapprove;

	public static final CastleManorManager getInstance() {
		if (_instance == null) {
			_log.info("Initializing CastleManorManager");
			_instance = new CastleManorManager();
		}
		return _instance;
	}

	public class CropProcure {
		int _cropId;
		int _buyResidual;
		int _rewardType;
		int _buy;
		int _price;

		public CropProcure(int id) {
			_cropId      = id;
			_buyResidual = 0;
			_rewardType  = 0;
			_buy         = 0;
			_price       = 0;
		}

		public CropProcure(int id, int amount, int type, int buy, int price) {
			_cropId      = id;
			_buyResidual = amount;
			_rewardType  = type;
			_buy         = buy;
			_price       = price;
		}

		public int getReward() { return _rewardType; }
		public int getId() { return _cropId; }
		public int getAmount() { return _buyResidual; }
		public int getStartAmount() { return _buy; }
		public int getPrice() { return _price; }

		public void setAmount (int amount) { _buyResidual = amount; }
	}

	public class SeedProduction {
		int _seedId;
		int _residual;
		int _price;
		int _sales;

		public SeedProduction(int id) {
			_seedId = id;
			_sales = 0;
			_price = 0;
			_sales = 0;
		}

		public SeedProduction(int id, int amount, int price, int sales) {
			_seedId = id;
			_residual = amount;
			_price = price;
			_sales = sales;
		}

		public int getId() {return _seedId; }
		public int getCanProduce() {return _residual; }
		public int getPrice() {return _price; }
		public int getStartProduce() { return _sales; }

		public void setCanProduce(int amount){ _residual = amount; }
	}

	private CastleManorManager() {
		load(); // load data from database
		init(); // schedule all manor related events
		_underMaintenance = false;
		_disabled = !Config.ALLOW_MANOR;
		boolean isApproved = (_periodApprove.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() &&
							   _manorRefresh.getTimeInMillis() > Calendar.getInstance().getTimeInMillis());
		for (Castle c: CastleManager.getInstance().getCastles()) {
			c.setNextPeriodApproved(isApproved);
		}
	}

	private void load() {
		Connection con = null;
		ResultSet rs;
		PreparedStatement statement;
		try {
			// Get Connection
			con = L2DatabaseFactory.getInstance().getConnection();
			for (Castle castle : CastleManager.getInstance().getCastles()) {
				List<SeedProduction> production = new ArrayList<>();
				List<SeedProduction> productionNext = new ArrayList<>();
				List<CropProcure> procure = new ArrayList<>();
				List<CropProcure> procureNext = new ArrayList<>();

				// restore seed production info
				statement = con.prepareStatement(CASTLE_MANOR_LOAD_PRODUCTION);
				statement.setInt(1, castle.getCastleId());
				rs = statement.executeQuery();
				while(rs.next()) {
					int seedId = rs.getInt("seed_id");
					int canProduce = rs.getInt("can_produce");
					int startProduce = rs.getInt("start_produce");
					int price = rs.getInt("seed_price");
					int period = rs.getInt("period");
					if (period == PERIOD_CURRENT)
						production.add(new SeedProduction(seedId,canProduce,price,startProduce));
					else
						productionNext.add(new SeedProduction(seedId,canProduce,price,startProduce));
				}
				statement.close();
				rs.close();

				castle.setSeedProduction(production, PERIOD_CURRENT);
				castle.setSeedProduction(productionNext, PERIOD_NEXT);

				// restore procure info
				statement = con.prepareStatement(CASTLE_MANOR_LOAD_PROCURE);
				statement.setInt(1, castle.getCastleId());
				rs = statement.executeQuery();
				while(rs.next()) {
					int cropId = rs.getInt("crop_id");
					int canBuy = rs.getInt("can_buy");
					int startBuy = rs.getInt("start_buy");
					int rewardType = rs.getInt("reward_type");
					int price = rs.getInt("price");
					int period = rs.getInt("period");
					if (period == PERIOD_CURRENT)
						procure.add(new CropProcure(cropId, canBuy, rewardType, startBuy, price));
					else
						procureNext.add(new CropProcure(cropId, canBuy, rewardType, startBuy, price));
				}
				statement.close();
				rs.close();

				castle.setCropProcure(procure, PERIOD_CURRENT);
				castle.setCropProcure(procureNext, PERIOD_NEXT);

				if (!procure.isEmpty() || !procureNext.isEmpty() ||
						!production.isEmpty() || !productionNext.isEmpty())
					_log.info(castle.getName() + ": Data loaded");
			}
		} catch (Exception e) {
			_log.info("Error restoring manor data: " + e.getMessage() );
		} finally {
			try { con.close(); } catch (Exception e) {}
		}
	}

	protected void init() {
		_manorRefresh = Calendar.getInstance();
		_manorRefresh.set(Calendar.HOUR_OF_DAY, MANOR_REFRESH);
		_manorRefresh.set(Calendar.MINUTE, MANOR_REFRESH_MIN);

		_periodApprove = Calendar.getInstance();
		_periodApprove.set(Calendar.HOUR_OF_DAY, NEXT_PERIOD_APPROVE);
		_periodApprove.set(Calendar.MINUTE, NEXT_PERIOD_APPROVE_MIN);

		updateManorRefresh();
		updatePeriodApprove();
    }

	public void updateManorRefresh() {
		_log.info("Manor System: Manor refresh updated");
		_scheduledManorRefresh = ThreadPoolManager.getInstance().scheduleGeneral(new Runnable(){
    
			public void run() {
            	if (!isDisabled()) {
	        		setUnderMaintenance(true);
	            	_log.info("Manor System: Under maintenance mode started");

	            	_scheduledMaintenanceEnd = ThreadPoolManager.getInstance().scheduleGeneral(new Runnable(){
	            
						public void run() {
	                        _log.info("Manor System: Next period started");
	                        setNextPeriod();
	                        try {
	                            save();
	                        } catch (Exception e) {
	                            _log.info("Manor System: Failed to save manor data: " + e);
	                        }
	                        setUnderMaintenance(false);
	                    }
	                    }, MAINTENANCE_PERIOD);
            	}
                updateManorRefresh();
            }
            }, getMillisToManorRefresh());


	}

	public void updatePeriodApprove() {
		_log.info("Manor System: Manor period approve updated");
		_scheduledNextPeriodapprove = ThreadPoolManager.getInstance().scheduleGeneral(new Runnable(){
    
			public void run() {
            	if (!isDisabled()) {
            		approveNextPeriod();
            		_log.info("Manor System: Next period approved");
            	}
            	updatePeriodApprove();
            }
            }, getMillisToNextPeriodApprove());
	}

	public long getMillisToManorRefresh() {
		if (_manorRefresh.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
			return (_manorRefresh.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());

		return setNewManorRefresh();
	}

	public long setNewManorRefresh() {
		_manorRefresh = Calendar.getInstance();
		_manorRefresh.set(Calendar.HOUR_OF_DAY, MANOR_REFRESH);
		_manorRefresh.set(Calendar.MINUTE, MANOR_REFRESH_MIN);
		_manorRefresh.add(Calendar.HOUR_OF_DAY, 24);

        _log.info("Manor System: New Schedule for manor refresh @ " + _manorRefresh.getTime());

        return (_manorRefresh.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
    }

	public long getMillisToNextPeriodApprove() {
		if (_periodApprove.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
			return (_periodApprove.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());

		return setNewPeriodApprove();
	}

	public long setNewPeriodApprove() {
		_periodApprove = Calendar.getInstance();
		_periodApprove.set(Calendar.HOUR_OF_DAY, NEXT_PERIOD_APPROVE);
		_periodApprove.set(Calendar.MINUTE, NEXT_PERIOD_APPROVE_MIN);
		_periodApprove.add(Calendar.HOUR_OF_DAY, 24);

        _log.info("Manor System: New Schedule for period approve @ " + _periodApprove.getTime());

        return (_periodApprove.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
    }

	public void setNextPeriod() {
		for (Castle c: CastleManager.getInstance().getCastles()) {
			if (c.getOwnerId() <= 0)
				continue;
			L2Clan clan = ClanTable.getInstance().getClan(c.getOwnerId());
			if (clan == null)
				continue;

			ItemContainer cwh = clan.getWarehouse();
			if (!(cwh instanceof ClanWarehouse)) {
				_log.info("Can't get clan warehouse for clan " + ClanTable.getInstance().getClan(c.getOwnerId()));
				return;
			}

			for (CropProcure crop: c.getCropProcure(PERIOD_CURRENT)) {
				if (crop.getStartAmount() == 0)
					continue;
				// adding bought crops to clan warehouse
				if (crop.getStartAmount() - crop.getAmount() > 0) {
					int count = crop.getStartAmount() - crop.getAmount();
					count = count * 90 / 100;
					if (count < 1) {
						if (Rnd.nextInt(99) < 90)
							count = 1;
					}
					if (count > 0) {
						cwh.addItem("Manor", L2Manor.getInstance().getMatureCrop(crop.getId()), count, null, null);
					}
				}
				// reserved and not used money giving back to treasury
				if (crop.getAmount() > 0) {
					c.addToTreasuryNoTax(crop.getAmount() * crop.getPrice());
				}
			}

			c.setSeedProduction(c.getSeedProduction(PERIOD_NEXT), PERIOD_CURRENT);
			c.setCropProcure(c.getCropProcure(PERIOD_NEXT), PERIOD_CURRENT);

			if (c.getTreasury() < c.getManorCost(PERIOD_CURRENT)) {
				c.setSeedProduction(getNewSeedsList(c.getCastleId()), PERIOD_NEXT);
				c.setCropProcure(getNewCropsList(c.getCastleId()), PERIOD_NEXT);
			} else {
				List<SeedProduction> production = new ArrayList<>();
				for (SeedProduction s : c.getSeedProduction(PERIOD_CURRENT)) {
					s.setCanProduce(s.getStartProduce());
					production.add(s);
				}
				c.setSeedProduction(production, PERIOD_NEXT);

				List<CropProcure> procure = new ArrayList<>();
				for (CropProcure cr : c.getCropProcure(PERIOD_CURRENT)) {
					cr.setAmount(cr.getStartAmount());
					procure.add(cr);
				}
				c.setCropProcure(procure, PERIOD_NEXT);
			}
			if (Config.ALT_MANOR_SAVE_ALL_ACTIONS) {
				c.saveCropData();
				c.saveSeedData();
			}

			// Sending notification to a clan leader
			L2PcInstance clanLeader = null;
			if (clan != null)
				clanLeader = L2World.getInstance().getPlayer(clan.getLeader().getName());
			if (clanLeader != null)
				clanLeader.sendPacket(new SystemMessage(SystemMessageId.THE_MANOR_INFORMATION_HAS_BEEN_UPDATED));

			c.setNextPeriodApproved(false);
		}
	}

	public void approveNextPeriod() {
		for (Castle c: CastleManager.getInstance().getCastles()) {
			boolean notFunc = false;

			if (c.getOwnerId() <= 0 ) {       				 	  // Castle has no owner
				c.setCropProcure(new ArrayList<>(), PERIOD_NEXT);
				c.setSeedProduction(new ArrayList<>(), PERIOD_NEXT);
			} else if (c.getTreasury() < c.getManorCost(PERIOD_NEXT)) {
				notFunc = true;
				c.setSeedProduction(getNewSeedsList(c.getCastleId()), PERIOD_NEXT);
				c.setCropProcure(getNewCropsList(c.getCastleId()), PERIOD_NEXT);
			} else {
				ItemContainer cwh = ClanTable.getInstance().getClan(c.getOwnerId()).getWarehouse();
				if (!(cwh instanceof ClanWarehouse)) {
					_log.info("Can't get clan warehouse for clan " + ClanTable.getInstance().getClan(c.getOwnerId()));
					return;
				}
				int slots = 0;
				for (CropProcure crop : c.getCropProcure(PERIOD_NEXT)) {
					if (crop.getStartAmount() > 0) {
						slots++;
					}
				}
				if (!cwh.validateCapacity(slots)) {
					notFunc = true;
					c.setSeedProduction(getNewSeedsList(c.getCastleId()), PERIOD_NEXT);
					c.setCropProcure(getNewCropsList(c.getCastleId()), PERIOD_NEXT);
				}
			}
			c.setNextPeriodApproved(true);
			c.addToTreasuryNoTax( (-1)*c.getManorCost(PERIOD_NEXT) );

			if (notFunc) {
				L2Clan clan = ClanTable.getInstance().getClan(c.getOwnerId());
				L2PcInstance clanLeader = null;
				if (clan != null)
					 clanLeader = L2World.getInstance().getPlayer(clan.getLeader().getName());
				if (clanLeader != null)
					clanLeader.sendPacket(new SystemMessage(SystemMessageId.THE_AMOUNT_IS_NOT_SUFFICIENT_AND_SO_THE_MANOR_IS_NOT_IN_OPERATION));
			}
		}

	}

	private List<SeedProduction> getNewSeedsList(int castleId) {
		List<SeedProduction> seeds = new ArrayList<>();
		List<Integer> seedsIds = L2Manor.getInstance().getSeedsForCastle(castleId);
		for (int sd : seedsIds) {
			seeds.add(new SeedProduction(sd));
		}
		return seeds;
	}

	private List<CropProcure> getNewCropsList(int castleId) {
		List<CropProcure> crops = new ArrayList<>();
		List<Integer> cropsIds = L2Manor.getInstance().getCropsForCastle(castleId);
		for (int cr : cropsIds) {
			crops.add(new CropProcure(cr));
		}
		return crops;
	}

	public boolean isUnderMaintenance() {
    	return _underMaintenance;
    }

    public void setUnderMaintenance(boolean mode) {
    	_underMaintenance = mode;
    }

    public boolean isDisabled() {
    	return _disabled;
    }

    public void setDisabled(boolean mode) {
    	_disabled = mode;
    }

	public SeedProduction getNewSeedProduction (int id, int amount, int price, int sales) {
		return new SeedProduction(id, amount, price, sales);
	}

	public CropProcure getNewCropProcure (int id, int amount, int type, int price, int buy) {
		return new CropProcure(id, amount, type, buy, price);
	}

	public void save() {
		for (Castle c : CastleManager.getInstance().getCastles()) {
			c.saveSeedData();
			c.saveCropData();
		}
	}
}
