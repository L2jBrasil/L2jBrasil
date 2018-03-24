package com.it.br.configuration.settings;

import java.util.List;
import java.util.Map;

import com.it.br.configuration.L2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSettings implements Settings {
	
	private static final Logger _log = LoggerFactory.getLogger(EventSettings.class);

	private boolean tvTEventEnabled;
	private List<String> tvTEventInterval;
	private int tvTEventParticipationTime;
	private int tvTEventRunningTime;
	private int tvTEventParticipationNpcId;
	private int[] tvTEventParticipationNpcCoordinates;
	private int[] tvTEventParticipationFee;
	private int tvTEventMinPlayersInTeams;
	private int tvTEventMaxPlayersInTeams;
	private int tvTEventMinPlayerLevel;
	private int tvTEventMaxPlayerLevel;
	private int tvTEventRespawnTeleportDelay;
	private int tvTEventStartLeaveTeleportDelay;
	private String tvTEventTeam1Name;
	private int[] tvTEventTeam1Coordinates;
	private String tvTEventTeam2Name;
	private int[] tvTEventTeam2Coordinates;
	private Map<Integer, Integer> tvTEventReward;
	private boolean tvTEventTargetTeamMembersAllowed;
	private boolean tvTEventScrollsAllowed;
	private boolean tvTEventPotionsAllowed;
	private boolean tvTEventSummonByItemAllowed;
	private List<Integer> tvTDoorsToOpen;
	private List<Integer> tvTDoorsToClose;
	private boolean tvTRewardTeamTie;
	private int tvTEventEffectsRemoval;
	private Map<Integer, Integer> tvTEventFighterBuffs;
	private Map<Integer, Integer> tvTEventMageBuffs;
	private String tvTEventOnKill;
	private boolean tvTRestorePlayerOldPosition;
	private boolean tvTRewardOnlyKillers;
	private int tvTEventRewardKill;
	private boolean tvTAllowPeaceAttack;
	private boolean tvTAllowFlag;
	private boolean tvTRestoreCPHPMP;

	@Override
	public void load(L2Properties properties) {
		if (properties == null) {
			return;
		}
		
		loadTvtEventSettings(properties);
	
	}

	private void loadTvtEventSettings(L2Properties properties) {
		tvTEventEnabled = properties.getBoolean("TvTEventEnabled", false);
		if(tvTEventEnabled) {
			tvTEventTeam1Coordinates = properties.getIntegerArray("TvTEventTeam1Coordinates", ",");
			tvTEventTeam2Coordinates =  properties.getIntegerArray("TvTEventTeam2Coordinates", ",");

			if(tvTEventTeam2Coordinates.length < 3 || tvTEventTeam1Coordinates.length < 3) {
				tvTEventEnabled = false;
				_log.warn("TvTEventEngine: invalid config property -> TvTEventTeamCoordinates");
				return;
			}
			
			tvTEventParticipationNpcId = properties.getInteger("TvTEventParticipationNpcId", 70010);
			if(tvTEventParticipationNpcId == 0) {
				tvTEventEnabled = false;
				_log.warn("TvTEventEngine: invalid config property -> TvTEventParticipationNpcId");
				return;
			}
			
			
			tvTEventParticipationNpcCoordinates = properties.getIntegerArray("TvTEventParticipationNpcCoordinates", ",");
			if(tvTEventParticipationNpcCoordinates.length < 3) {
				tvTEventEnabled = false;
				_log.warn("TvTEventEngine: invalid config property -> TvTEventParticipationNpcCoordinates");
			}
			
			
			tvTEventTeam1Name = properties.getString("TvTEventTeam1Name", "Blue");
			tvTEventTeam2Name = properties.getString("TvTEventTeam2Name", "Red"); 
			
			tvTEventInterval = properties.getStringList("TvTEventInterval", "4:00,8:00,12:00,16:00,20:00,00:00", ",");
			tvTEventParticipationTime = properties.getInteger("TvTEventParticipationTime", 8);
			tvTEventRunningTime = properties.getInteger("TvTEventRunningTime", 12);
			
			tvTEventParticipationFee = properties.getIntegerArray("TvTEventParticipationFee", ",");
			
			tvTEventMinPlayersInTeams = properties.getInteger("TvTEventMinPlayersInTeams", 1);
			tvTEventMaxPlayersInTeams = properties.getInteger("TvTEventMaxPlayersInTeams", 20);
			tvTEventMinPlayerLevel = properties.getInteger("TvTEventMinPlayerLevel", 1);
			tvTEventMaxPlayerLevel = properties.getInteger("TvTEventMaxPlayerLevel", 80);
	
			tvTEventRespawnTeleportDelay = properties.getInteger("TvTEventRespawnTeleportDelay", 10);
			tvTEventStartLeaveTeleportDelay = properties.getInteger("TvTEventStartLeaveTeleportDelay", 10);
	
			tvTEventReward = properties.getIntegerMap("TvTEventReward", ";", ",");
	
			tvTEventTargetTeamMembersAllowed = properties.getBoolean("TvTEventTargetTeamMembersAllowed", true);
			tvTEventScrollsAllowed = properties.getBoolean("TvTEventScrollsAllowed", false);
			tvTEventPotionsAllowed = properties.getBoolean("TvTEventPotionsAllowed", false);
			tvTEventSummonByItemAllowed = properties.getBoolean("TvTEventSummonByItemAllowed", false);
	
			tvTDoorsToOpen = properties.getIntegerList("TvTDoorsToOpen", ",");
			tvTDoorsToClose = properties.getIntegerList("TvTDoorsToClose", ",");
	
			tvTRewardTeamTie = properties.getBoolean("TvTRewardTeamTie", false);
			tvTEventEffectsRemoval = properties.getInteger("TvTEventEffectsRemoval", 7683);
			
			tvTEventFighterBuffs = properties.getIntegerMap("TvTEventFighterBuffs", ";", ",");
			tvTEventMageBuffs = properties.getIntegerMap("TvTEventMageBuffs", ";", ",");
	
			tvTEventOnKill = properties.getString("TvTEventOnKill", "pmtitle");
			tvTRestorePlayerOldPosition = properties.getBoolean("TvTRestorePlayerOldPosition", true);
			tvTRewardOnlyKillers = properties.getBoolean("TvTRewardOnlyKillers", true);
			tvTEventRewardKill = properties.getInteger("TvTEventRewardKill", 3);
			tvTAllowPeaceAttack = properties.getBoolean("TvTAllowPeaceAttack", true);
			tvTAllowFlag = properties.getBoolean("TvTAllowFlag", true);
			tvTRestoreCPHPMP = properties.getBoolean("TvTRestoreCPHPMP", false);
		}
	}

	public boolean isTvTEventEnabled() {
		return tvTEventEnabled;
	}
	
	public void setTvTEventEnabled(boolean tvTEventEnabled) {
		this.tvTEventEnabled = tvTEventEnabled;
	}

	public List<String> getTvTEventInterval() {
		return tvTEventInterval;
	}
	
	public void setTvTEventInterval(List<String> tvTEventInterval) {
		this.tvTEventInterval = tvTEventInterval;
	}

	public int getTvTEventParticipationTime() {
		return tvTEventParticipationTime;
	}

	public void setTvTEventParticipationTime(int tvTEventParticipationTime) {
		this.tvTEventParticipationTime = tvTEventParticipationTime;
	}
	
	public int getTvTEventRunningTime() {
		return tvTEventRunningTime;
	}
	
	public void setTvTEventRunningTime(int tvTEventRunningTime) {
		this.tvTEventRunningTime = tvTEventRunningTime;
	}

	public int getTvTEventParticipationNpcId() {
		return tvTEventParticipationNpcId;
	}
	
	public void setTvTEventParticipationNpcId(int tvTEventParticipationNpcId) {
		this.tvTEventParticipationNpcId = tvTEventParticipationNpcId;
	}

	public int[] getTvTEventParticipationNpcCoordinates() {
		return tvTEventParticipationNpcCoordinates;
	}

	public int[] getTvTEventParticipationFee() {
		return tvTEventParticipationFee;
	}

	public int getTvTEventMinPlayersInTeams() {
		return tvTEventMinPlayersInTeams;
	}

	public int getTvTEventMaxPlayersInTeams() {
		return tvTEventMaxPlayersInTeams;
	}

	public int getTvTEventMinPlayerLevel() {
		return tvTEventMinPlayerLevel;
	}

	public int getTvTEventMaxPlayerLevel() {
		return tvTEventMaxPlayerLevel;
	}

	public int getTvTEventRespawnTeleportDelay() {
		return tvTEventRespawnTeleportDelay;
	}

	public int getTvTEventStartLeaveTeleportDelay() {
		return tvTEventStartLeaveTeleportDelay;
	}

	public String getTvTEventTeam1Name() {
		return tvTEventTeam1Name;
	}

	public int[] getTvTEventTeam1Coordinates() {
		return tvTEventTeam1Coordinates;
	}

	public String getTvTEventTeam2Name() {
		return tvTEventTeam2Name;
	}

	public int[] getTvTEventTeam2Coordinates() {
		return tvTEventTeam2Coordinates;
	}

	public Map<Integer, Integer> getTvTEventReward() {
		return tvTEventReward;
	}

	public boolean isTvTEventTargetTeamMembersAllowed() {
		return tvTEventTargetTeamMembersAllowed;
	}

	public boolean isTvTEventScrollsAllowed() {
		return tvTEventScrollsAllowed;
	}

	public boolean isTvTEventPotionsAllowed() {
		return tvTEventPotionsAllowed;
	}

	public boolean isTvTEventSummonByItemAllowed() {
		return tvTEventSummonByItemAllowed;
	}

	public List<Integer> getTvTDoorsToOpen() {
		return tvTDoorsToOpen;
	}

	public List<Integer> getTvTDoorsToClose() {
		return tvTDoorsToClose;
	}

	public boolean isTvTRewardTeamTie() {
		return tvTRewardTeamTie;
	}

	public int getTvTEventEffectsRemoval() {
		return tvTEventEffectsRemoval;
	}

	public Map<Integer, Integer> getTvTEventFighterBuffs() {
		return tvTEventFighterBuffs;
	}

	public Map<Integer, Integer> getTvTEventMageBuffs() {
		return tvTEventMageBuffs;
	}

	public String getTvTEventOnKill() {
		return tvTEventOnKill;
	}

	public boolean isTvTRestorePlayerOldPosition() {
		return tvTRestorePlayerOldPosition;
	}

	public boolean isTvTRewardOnlyKillers() {
		return tvTRewardOnlyKillers;
	}

	public int getTvTEventRewardKill() {
		return tvTEventRewardKill;
	}

	public boolean isTvTAllowPeaceAttack() {
		return tvTAllowPeaceAttack;
	}

	public boolean isTvTAllowFlag() {
		return tvTAllowFlag;
	}

	public boolean isTvTRestoreCPHPMP() {
		return tvTRestoreCPHPMP;
	}
	
}
