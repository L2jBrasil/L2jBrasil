package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;

public class ClanHallSiegeSettings implements Settings {

	private int devastatedDay;
	private int devastatedHour;
	private int devastatedMinutes;
	private int partisanDay;
	private int partisanHour;
	private int partisanMinutes;

	@Override
	public void load(L2Properties properties) {
		if (properties == null) {
			return;
		}
		
		devastatedDay = properties.getInteger("DevastatedDay", 1);
		devastatedHour = properties.getInteger("DevastatedHour", 18);
		devastatedMinutes = properties.getInteger("DevastatedMinutes", 0);

		partisanDay = properties.getInteger("PartisanDay", 5);
		partisanHour = properties.getInteger("PartisanHour", 21);
		partisanMinutes = properties.getInteger("PartisanMinutes", 0);
	}

	public int getDevastatedDay() {
		return devastatedDay;
	}

	public int getDevastatedHour() {
		return devastatedHour;
	}

	public int getDevastatedMinutes() {
		return devastatedMinutes;
	}

	public int getPartisanDay() {
		return partisanDay;
	}

	public int getPartisanHour() {
		return partisanHour;
	}

	public int getPartisanMinutes() {
		return partisanMinutes;
	}

	
}
