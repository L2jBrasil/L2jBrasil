package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;

public class SepulchersSettings implements Settings {
	
	private int attackTime;
	private int coolDownTime;
	private int entryTime;
	private int warmUpTime;
	private int minimunPartyMembers;

	@Override
	public void load(L2Properties properties) {
		if (properties == null) {
			return;
		}
		
		attackTime = properties.getInteger("TimeOfAttack", 50);
		if(attackTime <= 0) {
			attackTime = 50;
		}
		
		coolDownTime = properties.getInteger("TimeOfCoolDown", 5);
		if(coolDownTime <= 0) {
			coolDownTime = 5;
		}
		
		entryTime = properties.getInteger("TimeOfEntry", 3);
		if(entryTime <= 0) {
			entryTime = 3;
		}
		
		warmUpTime = properties.getInteger("TimeOfWarmUp", 2);
		minimunPartyMembers = properties.getInteger("NumberOfNecessaryPartyMembers", 4);
	}

	public int getAttackTime() {
		return attackTime;
	}

	public int getCoolDownTime() {
		return coolDownTime;
	}

	public int getEntryTime() {
		return entryTime;
	}

	public int getWarmUpTime() {
		return warmUpTime;
	}

	public int getMinimunPartyMembers() {
		return minimunPartyMembers;
	}
}
