package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;

public class CommandSettings implements Settings {
	
	private boolean resCommandEnabled;
	private int resCommandConsumeId;
	private boolean buyRecEnabled;
	private int recItemID;
	private int recItemCount;
	private int recReward;
	private boolean locVoiceCommandEnabled;
	private boolean tradeOffCommandEnabled;
	private boolean vipTeleportEnabled;
	private boolean onlinePlayersCommandEnabled;
	private boolean statViewEnabled;
	private boolean infoViewEnabled;
	private boolean statsCommandEnabled;
	private boolean castleCommandEnabled;
	private boolean setCommandEnabled;
	private boolean bankingEnabled;
	private int bankingGoldbarCount;
	private int bankingAdenaCount;
	private int bankingGoldbarId;
	private boolean awayStatusEnabled;
	private boolean awayOnlyInPeaceZone;
	private boolean awayInterferenceEnabled;
	private boolean awayPlayerTakeAggro;
	private int awayTitleColor;
	private int awayTimer;
	private int awayBackTimer;
	
	@Override
	public void load(L2Properties properties) {
		if(properties == null) {
			return;
		}
		
		resCommandEnabled = properties.getBoolean("AllowResCommand", false); 
		resCommandConsumeId = properties.getInteger("ResCommandConsumeId", 3470);

		buyRecEnabled = properties.getBoolean("AllowBuyRec", false);
		recItemID =  properties.getInteger("RecItemID", 57);
		recItemCount =  properties.getInteger("RecItemCount", 1000000000);
		recReward =  properties.getInteger("RecReward", 1);

		locVoiceCommandEnabled =  properties.getBoolean("LocVoiceCommand", false);
		tradeOffCommandEnabled =  properties.getBoolean("TradeOffCommand", false);
		vipTeleportEnabled =  properties.getBoolean("VipTeleport", false); 
		onlinePlayersCommandEnabled =  properties.getBoolean("EnableOnlinePlayersCommand", false);

		statViewEnabled =  properties.getBoolean("AllowStatView", false);
		infoViewEnabled =  properties.getBoolean("AllowinfoView", false); 
		statsCommandEnabled =  properties.getBoolean("StatsCommandEnabled", false);
		castleCommandEnabled =  properties.getBoolean("CastleCommandEnabled", false);
		setCommandEnabled =  properties.getBoolean("SetCommandEnabled", false);

		bankingEnabled =  properties.getBoolean("BankingEnabled", false);
		bankingGoldbarCount =  properties.getInteger("BankingGoldbarCount", 1);
		bankingAdenaCount =  properties.getInteger("BankingAdenaCount", 500000000);
		bankingGoldbarId =  properties.getInteger("BankingGoldbarId", 3470);

		awayStatusEnabled = properties.getBoolean("AllowAwayStatus", false);
		awayOnlyInPeaceZone =  properties.getBoolean("AwayOnlyInPeaceZone", true); 
		awayInterferenceEnabled =  properties.getBoolean("AwayAllowInterference", false);
		awayPlayerTakeAggro =  properties.getBoolean("AwayPlayerTakeAggro", false);
		awayTitleColor =  properties.getInteger("AwayTitleColor", 16, 0x0000FF);
		awayTimer =  properties.getInteger("AwayTimer", 10);
		awayBackTimer =  properties.getInteger("BackTimer", 10);
	}
	
	
	public boolean isResCommandEnabled() {
		return resCommandEnabled;
	}
	
	public int getResCommandConsumeId() {
		return resCommandConsumeId;
	}
	
	public boolean isBuyRecEnabled() {
		return buyRecEnabled;
	}
	
	public int getRecItemID() {
		return recItemID;
	}
	
	public int getRecItemCount() {
		return recItemCount;
	}
	
	public int getRecReward() {
		return recReward;
	}
	
	public boolean isLocVoiceCommandEnabled() {
		return locVoiceCommandEnabled;
	}
	
	public boolean isTradeOffCommandEnabled() {
		return tradeOffCommandEnabled;
	}
	
	public boolean isVipTeleportEnabled() {
		return vipTeleportEnabled;
	}
	
	public boolean isOnlinePlayersCommandEnabled() {
		return onlinePlayersCommandEnabled;
	}
	
	public boolean isStatViewEnabled() {
		return statViewEnabled;
	}
	
	public boolean isInfoViewEnabled() {
		return infoViewEnabled;
	}
	
	public boolean isStatsCommandEnabled() {
		return statsCommandEnabled;
	}
	
	public boolean isCastleCommandEnabled() {
		return castleCommandEnabled;
	}
	
	public boolean isSetCommandEnabled() {
		return setCommandEnabled;
	}
	
	public boolean isBankingEnabled() {
		return bankingEnabled;
	}
	
	public int getBankingGoldbarCount() {
		return bankingGoldbarCount;
	}
	
	public int getBankingAdenaCount() {
		return bankingAdenaCount;
	}
	
	public int getBankingGoldbarId() {
		return bankingGoldbarId;
	}
	
	public boolean isAwayStatusEnabled() {
		return awayStatusEnabled;
	}
	
	public boolean isAwayOnlyInPeaceZone() {
		return awayOnlyInPeaceZone;
	}
	
	public boolean isAwayInterferenceEnabled() {
		return awayInterferenceEnabled;
	}
	
	public boolean isAwayPlayerTakeAggro() {
		return awayPlayerTakeAggro;
	}
	
	public int getAwayTitleColor() {
		return awayTitleColor;
	}
	
	public int getAwayTimer() {
		return awayTimer;
	}
	
	public int getAwayBackTimer() {
		return awayBackTimer;
	}
}
