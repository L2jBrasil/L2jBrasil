package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;

import java.util.List;

public class OlympiadSettings implements Settings {

	private int startHour;
	private int startMinute;
	private long competitionPeriod;
	private long battlePeriod;
	private long battleWaitTime;
	private long initialWaitTime;
	private long weeklyPeriod;
	private long validationPeriod;
	private int minimumClassedParticipants;
	private int minimumNonClassedParticipants;
	private int battleRewardItem;
	private int classedRewardCount;
	private int nonClassedRewardCount;
	private int minimumPointForExchange;
	private int compRewardItem;
	private int gatePassPerPoint;
	private int heroPoints;
	private List<Integer> restrictedItems;
	private boolean equipmentGradeSEnabled;
	private int maximumEnchant;
	private String period;
	private int periodMultiplier;
	private boolean eventsDuringOlympiadEnabled;
	private boolean rechargeSkillsEnabled;
	private boolean skillProtectionEnabled;
	private List<Integer> skillProtectionIds;

	@Override
	public void load(L2Properties properties) {
		if (properties == null) {
			return;
		}
		
		period =  properties.getString("AltOlyPeriod", "MONTH");
		periodMultiplier = properties.getInteger("AltOlyPeriodMultiplier", 1);
		startHour = properties.getInteger("AltOlyStartHour", 18);
		startMinute = properties.getInteger("AltOlyStartMin", 00);
		competitionPeriod = properties.getLong("AltOlyCPeriod", 21600000L);
		battlePeriod = properties.getLong("AltOlyBattle", 360000L);
		battleWaitTime = properties.getLong("AltOlyBWait", 600000L);
		initialWaitTime = properties.getLong("AltOlyIWait", 300000L);
		weeklyPeriod = properties.getLong("AltOlyWPeriod", 604800000L);
		validationPeriod = properties.getLong("AltOlyVPeriod", 86400000L);

		battleRewardItem = properties.getInteger("AltOlyBattleRewItem", 6651);
		classedRewardCount = properties.getInteger("AltOlyClassedRewItemCount", 50);
		nonClassedRewardCount = properties.getInteger("AltOlyNonClassedRewItemCount", 30);
		minimumPointForExchange = properties.getInteger("AltOlyMinPointForExchange", 50);
		compRewardItem = properties.getInteger("AltOlyCompRewItem", 6651);
		gatePassPerPoint = properties.getInteger("AltOlyGPPerPoint", 1000);
		heroPoints = properties.getInteger("AltOlyHeroPoints", 300);
		
		minimumClassedParticipants = properties.getInteger("AltOlyClassedParticipants", 5); 
		minimumNonClassedParticipants = properties.getInteger("AltOlyNonClassedParticipants", 9);
		restrictedItems = properties.getIntegerList("OlyRestrictedItems", ",");
		equipmentGradeSEnabled = properties.getBoolean("AllowOlyGradS", false);
		maximumEnchant = properties.getInteger("OlyMaxEnchant", -1);
		eventsDuringOlympiadEnabled = properties.getBoolean("AllowEventsDuringOly", true);
		rechargeSkillsEnabled = properties.getBoolean("AltOlyRechargeSkills", false);
		skillProtectionEnabled = properties.getBoolean("OlySkillProtect", true);
		skillProtectionIds = properties.getIntegerList("OllySkillId", ","); 
	}

	public int getStartHour() {
		return startHour;
	}

	public int getStartMinute() {
		return startMinute;
	}

	public long getCompetitionPeriod() {
		return competitionPeriod;
	}

	public long getBattlePeriod() {
		return battlePeriod;
	}

	public long getBattleWaitTime() {
		return battleWaitTime;
	}

	public long getInitialWaitTime() {
		return initialWaitTime;
	}

	public long getWeeklyPeriod() {
		return weeklyPeriod;
	}

	public long getValidationPeriod() {
		return validationPeriod;
	}

	public int getMinimumClassedParticipants() {
		return minimumClassedParticipants;
	}

	public int getMinimumNonClassedParticipants() {
		return minimumNonClassedParticipants;
	}

	public int getBattleRewardItem() {
		return battleRewardItem;
	}

	public int getClassedRewardCount() {
		return classedRewardCount;
	}

	public int getNonClassedRewardCount() {
		return nonClassedRewardCount;
	}

	public int getMinimumPointForExchange() {
		return minimumPointForExchange;
	}

	public int getCompetitionRewardItem() {
		return compRewardItem;
	}

	public int getGatePassPerPoint() {
		return gatePassPerPoint;
	}

	public int getHeroPoints() {
		return heroPoints;
	}

	public List<Integer> getRestrictedItems() {
		return restrictedItems;
	}

	public boolean isEquipmentGradeSEnabled() {
		return equipmentGradeSEnabled;
	}

	public int getMaximumEnchant() {
		return maximumEnchant;
	}

	public String getPeriod() {
		return period;
	}

	public int getPeriodMultiplier() {
		return periodMultiplier;
	}

	public boolean isEventsDuringOlympiadEnabled() {
		return eventsDuringOlympiadEnabled;
	}

	public boolean isRechargeSkillsEnabled() {
		return rechargeSkillsEnabled;
	}

	public boolean isSkillProtectionEnabled() {
		return skillProtectionEnabled;
	}

	public List<Integer> getSkillProtectionIds() {
		return skillProtectionIds;
	}
}
