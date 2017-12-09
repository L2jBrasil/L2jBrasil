package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;

public class SevensignsSettings implements Settings {

	private int minimumPlayers;
	private int maximumPlayerContribuition;
	private int managerStart;
	private int duration;
	private int cycleDuration;
	private int firstSpawn;
	private int firstSwarm;
	private int secondSpawn;
	private int secondSwarm;
	private int chestSpawn;

	@Override
	public void load(L2Properties properties) {
		if (properties == null) {
			return;
		}
		
		minimumPlayers = properties.getInteger("AltFestivalMinPlayer", 5);
		maximumPlayerContribuition = properties.getInteger("AltMaxPlayerContrib", 1000000);  
		managerStart = properties.getInteger("AltFestivalManagerStart", 2);
		duration = properties.getInteger("AltFestivalLength", 18);
		cycleDuration = properties.getInteger("AltFestivalCycleLength", 38);
		firstSpawn = properties.getInteger("AltFestivalFirstSpawn", 2);
		firstSwarm = properties.getInteger("AltFestivalFirstSwarm", 5);
		secondSpawn = properties.getInteger("AltFestivalSecondSpawn", 9);
		secondSwarm = properties.getInteger("AltFestivalSecondSwarm", 12);
		chestSpawn = properties.getInteger("AltFestivalChestSpawn", 15);
	}

	public int getMinimumPlayers() {
		return minimumPlayers;
	}

	public int getMaximumPlayerContribuition() {
		return maximumPlayerContribuition;
	}

	public int getManagerStart() {
		return managerStart;
	}

	public int getDuration() {
		return duration;
	}

	public int getCycleDuration() {
		return cycleDuration;
	}

	public int getFirstSpawn() {
		return firstSpawn;
	}

	public int getFirstSwarm() {
		return firstSwarm;
	}

	public int getSecondSpawn() {
		return secondSpawn;
	}

	public int getSecondSwarm() {
		return secondSwarm;
	}

	public int getChestSpawn() {
		return chestSpawn;
	}

}
