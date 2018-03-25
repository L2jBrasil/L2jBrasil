package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;

import java.util.List;
import java.util.Map;

public class L2JBrasilSettings implements Settings {
	
	private int levelOnEnter;
	private int sPOnEnter;
	private int startingAdena;
	private boolean customStarterItemsEnabled;
	private Map<Integer, Integer> startingItemsFighter;
	private Map<Integer, Integer> startingItemsMage;
	private int startingGBCount;
	private int startingGBId;
	private boolean customSpawnEnabled;
	private int customSpawnX;
	private int customSpawnY;
	private int customSpawnZ;
	private boolean customReSpawnEnabled;
	private int respawnLocationX;
	private int respawnLocationY;
	private int respawnLocationZ;
	private boolean newCharTitleEnabled;
	private String charTitle;
	private int manaPotionMPRes;
	private int altSubClassLevel;
	private boolean gradePenaltyDisabled;
	private boolean anyClassUseHeavyEnabled;
	private List<Integer> notAllowedUseHeavy;
	private boolean anyClassUseLightEnabled;
	private List<Integer> notAllowedUseLight;
	private boolean daggersUseHeavyEnabled;
	private boolean showNpcCrest;
	private boolean welcomeHtmEnabled;
	private boolean messageOnEnterEnabled;
	private String messageOnEnter;
	private boolean showPlayersOnlineOnLoginEnabled;
	private int onlinePlayerAdd;
	private boolean showWelcomePM;
	private String pMFrom;
	private String pMText1;
	private String pMText2;
	private boolean announceGMLoginEnabled;
	private boolean announceCastleLordsEnabled;
	private boolean announceVipLoginEnabled;
	private boolean announceAioLoginEnabled;
	private boolean announceSpawnRaidEnabled;
	private String serverName;
	private boolean announceBanChat;
	private boolean announceUnbanChat;
	private boolean modifySkillDurationEnabled;
	private Map<Integer, Integer> skillDurationMap;
	private boolean modifySkillReuseEnabled;
	private Map<Integer, Integer> skillReuseMap;
	private boolean noRemoveBuffsOnDie;
	private boolean heroSkillsOnSubEnabled;
	private boolean restoreEffectsOnSubEnabled;
	private boolean keepSubClassSkillsEnabled;
	private boolean dualBoxEnabled;
	private int allowedBoxes;
	private boolean dualBoxInOlyEnabled;
	private boolean dualBoxInEventEnabled;
	private boolean lowLvlProtectEnabled;
	private boolean l2WalkerProtectionEnabled;
	private boolean sameIPDontGivePvPPointEnabled;
	private boolean guardSystemEnabled;
	private boolean partyTradeEnabled;
	private List<String> forbiddenNames;
	private int altPlayerProtectionLevel;
	private int maxRunSpeed;
	private int runSpeedBoost;
	private int maxPAtkSpeed;
	private int maxMAtkSpeed;
	private int maxEvasion;
	private int maxPCritRate;
	private int maxMCritRate;
	private double multipleMCrit;
	private boolean gmSpawnOnCustomEnabled;
	private boolean deleteGmSpawnOnCustom;
	private int partyDuelSpawnX;
	private int partyDuelSpawnY;
	private int partyDuelSpawnZ;
	private boolean nobleCustomItemEnabled;
	private int nobleItemId;
	private boolean activeSubNeededToUseNobleItem;
	private int levelNeededToUseNobleCustomItem;
	private boolean heroCustomItemEnabled;
	private int heroCustomItemID;
	private boolean nobleStatusNeededToUseHeroItem;
	private boolean raidBossPetrificationDisabled;
	private int playerSpawnProtection;
	private boolean playerSpawnEffectEnabled;
	private int playerEffectId;
	private int altWarehouseDepositFee;
	private boolean argumentsRetailLike;
	private boolean sellByItemEnabled;
	private int sellItem;
	private int pvPToUseStore;

	@Override
	public void load(L2Properties properties) {
		if(properties == null) {
			return;
		}
		
		levelOnEnter = properties.getInteger("LevelOnEnter", 1);
		sPOnEnter =  properties.getInteger("SPOnEnter", 0);
		startingAdena =  properties.getInteger("StartingAdena", 0);

		customStarterItemsEnabled = properties.getBoolean("CustomStarterItemsEnabled", false);
		if(customStarterItemsEnabled) {
			startingItemsFighter = properties.getIntegerMap("StartingItemsFighter", ";", ",");
			startingItemsMage = properties.getIntegerMap("StartingItemsMage", ";", ",");
		}

		startingGBCount = properties.getInteger("StartingGBCount", 0);
		startingGBId =  properties.getInteger("StartingGBId", 3470);

		customSpawnEnabled = properties.getBoolean("CustomSpawnEnabled", false);
		if(customSpawnEnabled) {
			customSpawnX =  properties.getInteger("CustomSpawnX", 149319);
			customSpawnY =  properties.getInteger("CustomSpawnY", 46710);
			customSpawnZ =  properties.getInteger("CustomSpawnZ", -3414);
		}
		
		customReSpawnEnabled = properties.getBoolean("CustomReSpawn", false);
		if(customReSpawnEnabled) {
			respawnLocationX =  properties.getInteger("RespawnLocationX", 81236);
			respawnLocationY =  properties.getInteger("RespawnLocationY", 148638); 
			respawnLocationZ =  properties.getInteger("RespawnLocationZ", -3469);
		}

		newCharTitleEnabled = properties.getBoolean("NewCharTitle", false);
		if(newCharTitleEnabled) {
			charTitle =  properties.getString("CharTitle", "L2JBrasil 3.0");
		}
		
		manaPotionMPRes =  properties.getInteger("ManaPotionMPRes", 400);
		altSubClassLevel =  properties.getInteger("AltSubClassLevel", 40);
		gradePenaltyDisabled = properties.getBoolean("DisableGradePenalty", false);
		showNpcCrest =  properties.getBoolean("ShowNpcCrest", false);
		welcomeHtmEnabled =  properties.getBoolean("WelcomeHtm", false);
		
		anyClassUseHeavyEnabled =  properties.getBoolean("AllowAnyClassUseHeavy", true);
		if(!anyClassUseHeavyEnabled) {
			notAllowedUseHeavy =  properties.getIntegerList("NotAllowedUseHeavy", ",");  
		}
		
		anyClassUseLightEnabled = properties.getBoolean("AllowAnyClassUseLight", true);
		if(!anyClassUseLightEnabled) { 
			notAllowedUseLight = properties.getIntegerList("NotAllowedUseLight", ",");
		}
		
		daggersUseHeavyEnabled =  properties.getBoolean("AllowDaggersUseHeavy", true);
		messageOnEnterEnabled = properties.getBoolean("AllowMessageOnEnter", true);
		if(messageOnEnterEnabled) {
			messageOnEnter =  properties.getString("MessageOnEnter", "L2JBrasil 3.0 Project Interlude!");
		}

		showPlayersOnlineOnLoginEnabled =  properties.getBoolean("OnlineOnLogin", false);
		onlinePlayerAdd = properties.getInteger("OnlinePlayerAdd", 0);

		showWelcomePM =  properties.getBoolean("ShowWelcomePM", true);
		if(showWelcomePM) {
			pMFrom =  properties.getString("PMFrom", "Server");
			pMText1 =  properties.getString("PMText1", "Welcome to our server!"); 
			pMText2 = properties.getString("PMText2", " Visit our web http://www.l2jbrasil.com");
		}

		announceGMLoginEnabled =  properties.getBoolean("AnnounceGMLogin", false);
		announceCastleLordsEnabled = properties.getBoolean("AnnounceCastleLords", false);
		announceVipLoginEnabled =  properties.getBoolean("AnnounceVipLogin", false);
		announceAioLoginEnabled = properties.getBoolean("AnnounceAioLogin", false);
		announceSpawnRaidEnabled = properties.getBoolean("AnnounceSpawnRaid", false);

		serverName =  properties.getString("ServerName", "L2JBrasil 3.0");
		announceBanChat =  properties.getBoolean("AnnounceBanChat", false);
		announceUnbanChat = properties.getBoolean("AnnounceUnbanChat", false);

		modifySkillDurationEnabled = properties.getBoolean("EnableModifySkillDuration", false);
		if(modifySkillDurationEnabled) {
			skillDurationMap = properties.getIntegerMap("SkillDurationList", ";", ",");
		}
		
		modifySkillReuseEnabled =  properties.getBoolean("EnableModifySkillReuse", false);
		if(modifySkillReuseEnabled) {
			skillReuseMap =  properties.getIntegerMap("SkillReuseList", ";", ",");
		}

		noRemoveBuffsOnDie =  properties.getBoolean("NoRemoveBuffsOnDie", false);

		heroSkillsOnSubEnabled = properties.getBoolean("AllowHeroSkillsOnSub", false);
		
		restoreEffectsOnSubEnabled = properties.getBoolean("RestoreEffectsOnSub", false);

		keepSubClassSkillsEnabled = properties.getBoolean("KeepSubClassSkills", false);

		dualBoxEnabled = properties.getBoolean("AllowDualBox", true);
		allowedBoxes =  properties.getInteger("AllowedBoxes", 2);
		dualBoxInOlyEnabled = properties.getBoolean("AllowDualBoxInOly", true);
		dualBoxInEventEnabled = properties.getBoolean("AllowDualBoxInEvent", true);

		lowLvlProtectEnabled = properties.getBoolean("AllowLowLvlProtect", false);
		l2WalkerProtectionEnabled = properties.getBoolean("L2WalkerProtection", true);
		sameIPDontGivePvPPointEnabled = properties.getBoolean("AllowSameIPDontGivePvPPoint", false);
		guardSystemEnabled = properties.getBoolean("AllowSameIPDontGivePvPPoint", true);
		partyTradeEnabled = properties.getBoolean("AllowPartyTrade", false);
		forbiddenNames =  properties.getStringList("ForbiddenNames", "annou,ammou,amnou,anmou,anou,amou", ",");
		altPlayerProtectionLevel = properties.getInteger("AltPlayerProtectionLevel", 0);
		
		maxRunSpeed =  properties.getInteger("MaxRunSpeed", 500);
		runSpeedBoost = properties.getInteger("RunSpeedBoost", 0);
		maxPAtkSpeed =  properties.getInteger("MaxPAtkSpeed", 1500);
		maxMAtkSpeed =  properties.getInteger("MaxMAtkSpeed", 1999);
		maxEvasion =  properties.getInteger("MaxEvasion", 200);
		maxPCritRate =  properties.getInteger("MaxPCritRate", 500);
		maxMCritRate =  properties.getInteger("MaxMCritRate", 300);
		multipleMCrit = properties.getDouble("MultipleMCrit", 4.0);

		gmSpawnOnCustomEnabled =  properties.getBoolean("GmSpawnOnCustom", true);
		deleteGmSpawnOnCustom = properties.getBoolean("DeleteGmSpawnOnCustom", false);
		
		partyDuelSpawnX =  properties.getInteger("PartyDuelSpawnX", 149319);
		partyDuelSpawnY =  properties.getInteger("PartyDuelSpawnY", 46710);
		partyDuelSpawnZ =  properties.getInteger("PartyDuelSpawnZ", -3413);

		nobleCustomItemEnabled = properties.getBoolean("AllowNobleCustomItem", false);
		nobleItemId =  properties.getInteger("NobleItemId", 6673);
		
		activeSubNeededToUseNobleItem = properties.getBoolean("ActiveSubNeededToUseNobleItem", true);
		levelNeededToUseNobleCustomItem =  properties.getInteger("LevelNeededToUseNobleCustomItem", 76);
		heroCustomItemEnabled = properties.getBoolean("AllowHeroCustomItem", false);
		heroCustomItemID =  properties.getInteger("HeroCustomItemID", 7196);
		nobleStatusNeededToUseHeroItem = properties.getBoolean("NobleStatusNeededToUseHeroItem", true);

		raidBossPetrificationDisabled = properties.getBoolean("DisableRaidBossPetrification", false);
		
		playerSpawnProtection = properties.getInteger("PlayerSpawnProtection", 0); 
		
		playerSpawnEffectEnabled = properties.getBoolean("PlayerSpawnEffect", false);
		if(playerSpawnEffectEnabled) {
			playerEffectId =  properties.getInteger("PlayerEffectId", 2097152);
		}
		
		altWarehouseDepositFee =  properties.getInteger("AltWarehouseDepositFee", 30);
		argumentsRetailLike = properties.getBoolean("ArgumentsRetailLike", true);
		
		sellByItemEnabled = properties.getBoolean("SellByItem", false);
		sellItem =  properties.getInteger("SellItem", 3470);
		pvPToUseStore = properties.getInteger("PvPToUseStore", 0);
	}
	
	
	public int getLevelOnEnter() {
		return levelOnEnter;
	}

	public int getsPOnEnter() {
		return sPOnEnter;
	}

	public int getStartingAdena() {
		return startingAdena;
	}
	
	public void setStartingAdena(int startingAdena) {
		this.startingAdena = startingAdena;
	}

	public boolean isCustomStarterItemsEnabled() {
		return customStarterItemsEnabled;
	}

	public Map<Integer, Integer> getStartingItemsFighter() {
		return startingItemsFighter;
	}

	public Map<Integer, Integer> getStartingItemsMage() {
		return startingItemsMage;
	}

	public int getStartingGBCount() {
		return startingGBCount;
	}

	public int getStartingGBId() {
		return startingGBId;
	}

	public boolean isCustomSpawnEnabled() {
		return customSpawnEnabled;
	}

	public int getCustomSpawnX() {
		return customSpawnX;
	}

	public int getCustomSpawnY() {
		return customSpawnY;
	}

	public int getCustomSpawnZ() {
		return customSpawnZ;
	}

	public boolean isCustomReSpawnEnabled() {
		return customReSpawnEnabled;
	}

	public int getRespawnLocationX() {
		return respawnLocationX;
	}

	public int getRespawnLocationY() {
		return respawnLocationY;
	}

	public int getRespawnLocationZ() {
		return respawnLocationZ;
	}

	public boolean isNewCharTitleEnabled() {
		return newCharTitleEnabled;
	}

	public String getCharTitle() {
		return charTitle;
	}

	public int getManaPotionMPRes() {
		return manaPotionMPRes;
	}

	public int getAltSubClassLevel() {
		return altSubClassLevel;
	}

	public boolean isGradePenaltyDisabled() {
		return gradePenaltyDisabled;
	}

	public boolean isAnyClassUseHeavyEnabled() {
		return anyClassUseHeavyEnabled;
	}

	public List<Integer> getNotAllowedUseHeavy() {
		return notAllowedUseHeavy;
	}

	public boolean isAnyClassUseLightEnabled() {
		return anyClassUseLightEnabled;
	}

	public List<Integer> getNotAllowedUseLight() {
		return notAllowedUseLight;
	}

	public boolean isDaggersUseHeavyEnabled() {
		return daggersUseHeavyEnabled;
	}

	public boolean isShowNpcCrest() {
		return showNpcCrest;
	}

	public boolean isWelcomeHtmEnabled() {
		return welcomeHtmEnabled;
	}
	
	public boolean isMessageOnEnterEnabled() {
		return messageOnEnterEnabled;
	}
	
	public String getMessageOnEnter() {
		return messageOnEnter;
	}
	
	public boolean isShowPlayerOnLoginEnabled() {
		return showPlayersOnlineOnLoginEnabled;
	}
	
	public int getOnlinePlayerAdd() {
		return onlinePlayerAdd;
	}
	
	public boolean isShowWelcomePM() {
		return showWelcomePM;
	}
	
	public String getpMFrom() {
		return pMFrom;
	}
	
	public String getpMText1() {
		return pMText1;
	}
	
	public String getpMText2() {
		return pMText2;
	}
	
	public boolean isAnnounceGMLoginEnabled() {
		return announceGMLoginEnabled;
	}
	
	public boolean isAnnounceCastleLordsEnabled() {
		return announceCastleLordsEnabled;
	}
	
	public boolean isAnnounceVipLoginEnabled() {
		return announceVipLoginEnabled;
	}
	
	public boolean isAnnounceAioLoginEnabled() {
		return announceAioLoginEnabled;
	}
	
	public boolean isAnnounceSpawnRaidEnabled() {
		return announceSpawnRaidEnabled;
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public boolean isAnnounceUnbanChat() {
		return announceUnbanChat;
	}
	
	public boolean isAnnounceBanChat() {
		return announceBanChat;
	}
	
	public boolean isModifySkillDurationEnabled() {
		return modifySkillDurationEnabled;
	}
	
	public Map<Integer, Integer> getSkillDurationMap() {
		return skillDurationMap;
	}
	
	public boolean isModifySkillReuseEnabled() {
		return modifySkillReuseEnabled;
	}
	
	public Map<Integer, Integer> getSkillReuseMap() {
		return skillReuseMap;
	}
	
	
	public boolean isNoRemoveBuffsOnDie() {
		return noRemoveBuffsOnDie;
	}
	
	public boolean isHeroSkillsOnSubEnabled() {
		return heroSkillsOnSubEnabled;
	}
	
	public boolean isRestoreEffectsOnSubEnabled() {
		return restoreEffectsOnSubEnabled;
	}
	
	public boolean isKeepSubClassSkillsEnabled() {
		return keepSubClassSkillsEnabled;
	}
	
	
	public boolean isDualBoxEnabled() {
		return dualBoxEnabled;
	}
	
	public int getAllowedBoxes() {
		return allowedBoxes;
	}
	
	public boolean isDualBoxInOlyEnabled() {
		return dualBoxInOlyEnabled;
	}
	
	public boolean isDualBoxInEventEnabled() {
		return dualBoxInEventEnabled;
	}
	
	public boolean isLowLvlProtectEnabled() {
		return lowLvlProtectEnabled;
	}
	
	public boolean isL2WalkerProtectionEnabled() {
		return l2WalkerProtectionEnabled;
	}
	
	public boolean isSameIPDontGivePvPPointEnabled() {
		return sameIPDontGivePvPPointEnabled;
	}
	
	public boolean isGuardSystemEnabled() {
		return guardSystemEnabled;
	}
	
	public boolean isPartyTradeEnabled() {
		return partyTradeEnabled;
	}
	
	public List<String> getForbiddenNames() {
		return forbiddenNames;
	}
	
	public int getAltPlayerProtectionLevel() {
		return altPlayerProtectionLevel;
	}
	
	public int getMaxRunSpeed() {
		return maxRunSpeed;
	}
	
	public int getRunSpeedBoost() {
		return runSpeedBoost;
	}
	
	public int getMaxPAtkSpeed() {
		return maxPAtkSpeed;
	}
	
	public void setMaxPAtkSpeed(int maxPAtkSpeed) {
		this.maxPAtkSpeed = maxPAtkSpeed;
	}
	
	public int getMaxMAtkSpeed() {
		return maxMAtkSpeed;
	}
	
	public void setMaxMAtkSpeed(int maxMAtkSpeed) {
		this.maxMAtkSpeed = maxMAtkSpeed;
	}
	
	public int getMaxEvasion() {
		return maxEvasion;
	}
	
	public int getMaxPCritRate() {
		return maxPCritRate;
	}
	
	public int getMaxMCritRate() {
		return maxMCritRate;
	}
	
	public double getMultipleMCrit() {
		return multipleMCrit;
	}
	
	public boolean isGmSpawnOnCustomEnabled() {
		return gmSpawnOnCustomEnabled;
	}
	
	public boolean isDeleteGmSpawnOnCustom() {
		return deleteGmSpawnOnCustom;
	}
	
	public int getPartyDuelSpawnX() {
		return partyDuelSpawnX;
	}
	
	public int getPartyDuelSpawnY() {
		return partyDuelSpawnY;
	}
	
	public int getPartyDuelSpawnZ() {
		return partyDuelSpawnZ;
	}
	
	public boolean isNobleCustomItemEnabled() {
		return nobleCustomItemEnabled;
	}
	
	public int getNobleItemId() {
		return nobleItemId;
	}
	
	public boolean isActiveSubNeededToUseNobleItem() {
		return activeSubNeededToUseNobleItem;
	}
	
	public int getLevelNeededToUseNobleCustomItem() {
		return levelNeededToUseNobleCustomItem;
	}
	
	public boolean isHeroCustomItemEnabled() {
		return heroCustomItemEnabled;
	}
	
	public int getHeroCustomItemID() {
		return heroCustomItemID;
	}
	
	public boolean isNobleStatusNeededToUseHeroItem() {
		return nobleStatusNeededToUseHeroItem;
	}
	
	public boolean isRaidBossPetrificationDisabled() {
		return raidBossPetrificationDisabled;
	}
	
	public int getPlayerSpawnProtection() {
		return playerSpawnProtection;
	}
	
	public void setPlayerSpawnProtection(int playerSpawnProtection) {
		this.playerSpawnProtection = playerSpawnProtection;
	}
	
	public boolean isPlayerSpawnEffectEnabled() {
		return playerSpawnEffectEnabled;
	}
	
	public int getPlayerEffectId() {
		return playerEffectId;
	}
	
	public int getAltWarehouseDepositFee() {
		return altWarehouseDepositFee;
	}
	
	public boolean isArgumentsRetailLike() {
		return argumentsRetailLike;
	}
	
	public boolean isSellByItemEnabled() {
		return sellByItemEnabled;
	}
	
	public int getSellItem() {
		return sellItem;
	}
	
	public int getPvPToUseStore() {
		return pvPToUseStore;
	}
	
	
}
