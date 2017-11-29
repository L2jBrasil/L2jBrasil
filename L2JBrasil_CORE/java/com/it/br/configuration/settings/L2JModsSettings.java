package com.it.br.configuration.settings;

import java.util.List;

import com.it.br.configuration.L2Properties;

public class L2JModsSettings implements Settings {

	private boolean championEnabled;
	private int championFrequency;
	private int championMinLevel;
	private int championMaxLevel;
	private int championHp;
	private int championRewards;
	private float championAdenasRewards;
	private float championHpRegen;
	private float championAtk;
	private float championSpdAtk;
	private int championRewardItem;
	private int championRewardItemID;
	private int championRewardItemQty;
	private boolean weddingEnabled;
	private boolean announceWeddingsEnabled;
	private int weddingPrice;
	private boolean weddingPunishInfidelityEnabled;
	private boolean weddingTeleportEnabled;
	private int weddingTeleportPrice;
	private int weddingTeleportDuration;
	private boolean weddingSameSexEnabled;
	private boolean weddingFormalWearEnabled;
	private int weddingDivorceCosts;
	private boolean colorWeddingNameEnabled;
	private int weddingNameColor;
	private int weddingNameGeyColor;
	private int weddingNameLizColor;
	private boolean pcBangPointEnabled;
	private int pcBangPointMinLevel;
	private int pcBangPointMinCount;
	private int pcBangPointMaxCount;
	private int pcBangPointDualChance;
	private int pcBangPointTimeStamp;
	private int pcBangPointId;
	private boolean offlineTradeEnabled;
	private boolean offlineCraftEnabled;
	private boolean offlineNameColorEnabled;
	private int offlineNameColor;
	private boolean restoreOfflinersEnabled;
	private int offlineMaxDays;
	private boolean offlineDisconnectFinished;
	private boolean offlineLogoutEnabled;
	private boolean offlineSleepEffectEnabled;
	private int logoutItemId;
	private int logoutItemCount;
	private boolean checkSkillsOnEnter;
	private List<Integer> allowedSkills;
	private boolean checkHeroSkills;
	private boolean checkNobleSkills;
	private List<Integer> nonCheckSkills;

	@Override
	public void load(L2Properties properties) {
		if (properties == null) {
			return;
		}
		
		championEnabled = properties.getBoolean("ChampionEnable", false);
		if(championEnabled) {
			championFrequency = properties.getInteger("ChampionFrequency", 5);
			championMinLevel = properties.getInteger("ChampionMinLevel", 20);
			championMaxLevel = properties.getInteger("ChampionMaxLevel", 70);
			championHp = properties.getInteger("ChampionHp", 8);
			championRewards = properties.getInteger("ChampionRewards", 8);
			championAdenasRewards = properties.getFloat("ChampionAdenasRewards", 1);
			championHpRegen = properties.getFloat("ChampionHpRegen", 1);
			championAtk = properties.getFloat("ChampionAtk", 1);
			championSpdAtk = properties.getFloat("ChampionSpdAtk", 1);
			championRewardItem = properties.getInteger("ChampionRewardItem", 0);
			championRewardItemID = properties.getInteger("ChampionRewardItemID", 6393);
			championRewardItemQty = properties.getInteger("ChampionRewardItemQty", 1);
		}
		
		weddingEnabled = properties.getBoolean("AllowWedding", false);
		if(weddingEnabled) {
			announceWeddingsEnabled = properties.getBoolean("AnnounceWeddings", true);
			weddingPrice = properties.getInteger("WeddingPrice", 250000000);
			weddingPunishInfidelityEnabled = properties.getBoolean("WeddingPunishInfidelity", true);
			weddingTeleportEnabled = properties.getBoolean("WeddingTeleport", true);
			weddingTeleportPrice =  properties.getInteger("WeddingTeleportPrice", 50000);
			weddingTeleportDuration = properties.getInteger("WeddingTeleportDuration", 60);
			weddingSameSexEnabled = properties.getBoolean("WeddingAllowSameSex", false);
			weddingFormalWearEnabled = properties.getBoolean("WeddingFormalWear", true);
			weddingDivorceCosts = properties.getInteger("WeddingDivorceCosts", 20);
			colorWeddingNameEnabled = properties.getBoolean("ColorWeddingName", false);
			weddingNameColor =  properties.getInteger("WeddingNameColor", 16, 0x62FFFF);
			weddingNameGeyColor = properties.getInteger("WeddingNameGeyColor", 16, 0xFA0000);
			weddingNameLizColor =  properties.getInteger("WeddingNameLizColor", 16, 0xFA70FA);
		}
		
		pcBangPointEnabled = properties.getBoolean("PcBangPointEnable", false);
		if(pcBangPointEnabled) {
			pcBangPointMinLevel = properties.getInteger("PcBangPointMinLevel", 20);
			pcBangPointMinCount =  properties.getInteger("PcBangPointMinCount", 45);
			pcBangPointMaxCount = properties.getInteger("PcBangPointMaxCount", 180);
			pcBangPointDualChance = properties.getInteger("PcBangPointDualChance", 20);
			pcBangPointTimeStamp = properties.getInteger("PcBangPointTimeStamp", 300);
			pcBangPointId =  properties.getInteger("PcBangPointId", 65436);
		}
		
		offlineTradeEnabled = properties.getBoolean("OfflineTradeEnable", false);
		offlineCraftEnabled = properties.getBoolean("OfflineCraftEnable", false);
		offlineNameColorEnabled = properties.getBoolean("OfflineNameColorEnable", false);
		offlineNameColor =  properties.getInteger("OfflineNameColor", 16, 0xff00ff);
		restoreOfflinersEnabled = properties.getBoolean("RestoreOffliners", false);
		offlineMaxDays = properties.getInteger("OfflineMaxDays", 0);
		offlineDisconnectFinished = properties.getBoolean("OfflineDisconnectFinished", false);
		offlineLogoutEnabled = properties.getBoolean("OfflineLogout", false);
		offlineSleepEffectEnabled = properties.getBoolean("OfflineSleepEffect", false);

		logoutItemId =  properties.getInteger("LogoutItemId", 5283);
		logoutItemCount =  properties.getInteger("LogoutItemCount", 2);

		checkSkillsOnEnter = properties.getBoolean("CheckSkillsOnEnter", false);
		if(checkSkillsOnEnter) {
			allowedSkills = properties.getIntegerList("AllowedSkills", ",");
			checkHeroSkills = properties.getBoolean("CheckHeroSkills", true);
			checkNobleSkills = properties.getBoolean("CheckNobleSkills", true);
			nonCheckSkills = properties.getIntegerList("NonCheckSkills", ",");
		}
	}

	public boolean isChampionEnabled() {
		return championEnabled;
	}
	
	public void setChampionEnabled(boolean championEnabled) {
		this.championEnabled = championEnabled;
	}

	public int getChampionFrequency() {
		return championFrequency;
	}
	
	public void setChampionFrequency(int championFrequency) {
		this.championFrequency = championFrequency;
	}

	public int getChampionMinLevel() {
		return championMinLevel;
	}

	public int getChampionMaxLevel() {
		return championMaxLevel;
	}

	public int getChampionHp() {
		return championHp;
	}

	public int getChampionRewards() {
		return championRewards;
	}

	public float getChampionAdenasRewards() {
		return championAdenasRewards;
	}

	public float getChampionHpRegen() {
		return championHpRegen;
	}

	public float getChampionAtk() {
		return championAtk;
	}

	public float getChampionSpdAtk() {
		return championSpdAtk;
	}

	public int getChampionRewardItem() {
		return championRewardItem;
	}

	public int getChampionRewardItemID() {
		return championRewardItemID;
	}

	public int getChampionRewardItemQty() {
		return championRewardItemQty;
	}

	public boolean isWeddingEnabled() {
		return weddingEnabled;
	}
	
	public void setWeddingEnabled(boolean weddingEnabled) {
		this.weddingEnabled = weddingEnabled;
	}

	public boolean isAnnounceWeddingsEnabled() {
		return announceWeddingsEnabled;
	}

	public int getWeddingPrice() {
		return weddingPrice;
	}
	
	public void setWeddingPrice(int weddingPrice) {
		this.weddingPrice = weddingPrice;
	}

	public boolean isWeddingPunishInfidelityEnabled() {
		return weddingPunishInfidelityEnabled;
	}
	
	public void setWeddingPunishInfidelityEnabled(boolean weddingPunishInfidelityEnabled) {
		this.weddingPunishInfidelityEnabled = weddingPunishInfidelityEnabled;
	}

	public boolean isWeddingTeleportEnabled() {
		return weddingTeleportEnabled;
	}
	
	public void setWeddingTeleportEnabled(boolean weddingTeleportEnabled) {
		this.weddingTeleportEnabled = weddingTeleportEnabled;
	}

	public int getWeddingTeleportPrice() {
		return weddingTeleportPrice;
	}
	
	public void setWeddingTeleportPrice(int weddingTeleportPrice) {
		this.weddingTeleportPrice = weddingTeleportPrice;
	}

	public int getWeddingTeleportDuration() {
		return weddingTeleportDuration;
	}
	
	public void setWeddingTeleportDuration(int weddingTeleportDuration) {
		this.weddingTeleportDuration = weddingTeleportDuration;
	}

	public boolean isWeddingSameSexEnabled() {
		return weddingSameSexEnabled;
	}
	
	public void setWeddingSameSexEnabled(boolean weddingSameSexEnabled) {
		this.weddingSameSexEnabled = weddingSameSexEnabled;
	}

	public boolean isWeddingFormalWearEnabled() {
		return weddingFormalWearEnabled;
	}
	
	public void setWeddingFormalWearEnabled(boolean weddingFormalWearEnabled) {
		this.weddingFormalWearEnabled = weddingFormalWearEnabled;
	}

	public int getWeddingDivorceCosts() {
		return weddingDivorceCosts;
	}
	
	public void setWeddingDivorceCosts(int weddingDivorceCosts) {
		this.weddingDivorceCosts = weddingDivorceCosts;
	}

	public boolean isColorWeddingNameEnabled() {
		return colorWeddingNameEnabled;
	}

	public int getWeddingNameColor() {
		return weddingNameColor;
	}

	public int getWeddingNameGeyColor() {
		return weddingNameGeyColor;
	}

	public int getWeddingNameLizColor() {
		return weddingNameLizColor;
	}

	public boolean isPcBangPointEnabled() {
		return pcBangPointEnabled;
	}

	public int getPcBangPointMinLevel() {
		return pcBangPointMinLevel;
	}

	public int getPcBangPointMinCount() {
		return pcBangPointMinCount;
	}

	public int getPcBangPointMaxCount() {
		return pcBangPointMaxCount;
	}

	public int getPcBangPointDualChance() {
		return pcBangPointDualChance;
	}

	public int getPcBangPointTimeStamp() {
		return pcBangPointTimeStamp;
	}

	public int getPcBangPointId() {
		return pcBangPointId;
	}

	public boolean isOfflineTradeEnabled() {
		return offlineTradeEnabled;
	}

	public boolean isOfflineCraftEnabled() {
		return offlineCraftEnabled;
	}

	public boolean isOfflineNameColorEnabled() {
		return offlineNameColorEnabled;
	}

	public int getOfflineNameColor() {
		return offlineNameColor;
	}

	public boolean isRestoreOfflinersEnabled() {
		return restoreOfflinersEnabled;
	}

	public int getOfflineMaxDays() {
		return offlineMaxDays;
	}

	public boolean isOfflineDisconnectFinished() {
		return offlineDisconnectFinished;
	}

	public boolean isOfflineLogoutEnabled() {
		return offlineLogoutEnabled;
	}

	public boolean isOfflineSleepEffectEnabled() {
		return offlineSleepEffectEnabled;
	}

	public int getLogoutItemId() {
		return logoutItemId;
	}

	public int getLogoutItemCount() {
		return logoutItemCount;
	}

	public boolean isCheckSkillsOnEnter() {
		return checkSkillsOnEnter;
	}

	public List<Integer> getAllowedSkills() {
		return allowedSkills;
	}

	public boolean isCheckHeroSkills() {
		return checkHeroSkills;
	}

	public boolean isCheckNobleSkills() {
		return checkNobleSkills;
	}

	public List<Integer> getNonCheckSkills() {
		return nonCheckSkills;
	}

	
}
