package com.mschwartz.balboa;

import lombok.Data;

@Data
public class StatusModel {

	/**
	 * not zero if the spa is in holding mode (for example to clean the spa you can
	 * activate holding mode for one hour)
	 */
	private boolean holdMode;
	
	private boolean testMode;
	
	private boolean initializing;
	
	private boolean tempsOn;

	/**
	 * Priminig mode. Not zero if the spa is switched on the first time
	 */
	private int priming;

	/**
	 * The current temperature in integer value (divide by 2 for celcius)
	 */
	private int currentTemperature;

	/**
	 * A human readable string for the current temperature
	 */
	private String currentTemperatureString;

	private int currentHour;

	private int currentMinute;

	/**
	 * The heating mode (Ready , Rest, Ready in Rest)
	 */
	private int heatingMode;

	private String heatingModeString;

	/**
	 * Reminder Type 0x00=None, 0x04=Clean filter, 0x0A=Check the pH, 0x09=Check the
	 * sanitizer
	 */
	private int reminder;

	private String reminderString;

	/**
	 * Minutes if Hold Mode else Temperature (scaled by Temperature Scale) if A/B
	 * Temps else 0x01 if Test Mode else 0x00
	 */
	private int sensorA;

	private String sensorAString;

	/**
	 * Temperature (scaled by Temperature Scale) if A/B Temps else 0x00
	 */
	private int sensorB;

	private String sensorBString;
	
	/**
	 * 0x00=Running, 0x01=Initializing, 0x05=Hold Mode, ?0x14=A/B Temps ON?, 0x17=Test Mode
	 */
	private int flag0;

	private int flag3;

	private int flag4;

	/**
	 * true if celsius, false if fahrenheit
	 */
	private boolean celsius;

	/**
	 * true if 24 hour time, false for 12
	 */
	private boolean h24;

	/**
	 * 0=OFF, 1=Cycle 1, 2=Cycle 2, 3=Cycle 1 and 2
	 */
	private int filterMode;
	
	private String filterModeString;
	
	private boolean panelLocked;

	/**
	 * 0x04 = Temperature Range (0 = Low, 1 = High)
	 */
	private int temperatureRange;

	private int needsHeat;

	/**
	 * 0=OFF, 1=Heating, 2=Heat Waiting 0x30 = Heating (seems it can be 0, 1, or 2)
	 */
	private int heating;

	private String heatingString;

	private int pumpStatus;

	private int pumpStatus12;

	private int pumpStatus1;

	private int pumpStatus2;

	private int pumpStatus3;

	private int pumpStatus4;

	private int pumpStatus5;

	private int pumpStatus6;

	private int flag5;

	private int circulationPump;

	private int blower;
	
	private int lightFlag;
	
	private int light1;
	
	private int light2;
	
	private int mister;

	private int setTemperature;
	
	private String setTemperatureString;
	
	private int m8cycleTime;
	
	public StatusModel(byte[] frame) {

		flag0 = frame[0];
		priming = frame[1];
		currentTemperature = frame[2] & 0xff;
		currentHour = frame[3];
		currentMinute = frame[4];
		heatingMode = frame[5];
		reminder = frame[6];
		sensorA = frame[7];
		sensorB = frame[8];
		flag3 = frame[9];
		flag4 = frame[10];
		pumpStatus = frame[11];
		pumpStatus12 = frame[12];
		flag5 = frame[13];
		lightFlag = frame[14];
		mister = frame[15];
		setTemperature = frame[20] & 0xff;
		m8cycleTime = frame[24];
		
		holdMode = (flag0 & 0x05) > 0;
		testMode = (flag0 & 0x17) > 0;
		initializing = (flag0 & 0x01) > 0;
		tempsOn = (flag0 & 0x14) > 0;

		celsius = (flag3 & 0x01) > 0;
		h24 = (flag3 & 0x02) > 0;
		filterMode = (flag3 & 0x0c) >> 2;
		panelLocked = (flag3 & 0x20) > 0;

		temperatureRange = flag4 & 0x04;
		needsHeat = flag4 & 0x08;
		heating = (flag4 & 0x30) >> 4;

		pumpStatus1 = pumpStatus & 0x03;
		pumpStatus2 = (pumpStatus >> 2) & 0x03;
		pumpStatus3 = (pumpStatus >> 4) & 0x03;
		pumpStatus4 = (pumpStatus >> 6) & 0x03;
		pumpStatus5 = pumpStatus12 & 0x03;
		pumpStatus6 = (pumpStatus12 >> 2) & 0x03;

		circulationPump = flag5 & 0x02;
		blower = flag5 & 0x0c;

		light1 = lightFlag & 0x03;
		light2 = (lightFlag >> 2) & 0x03;
		
		double tempScale = 1.0;
		if (celsius) tempScale = 2.0;

		//System.out.println("current: " + Integer.toHexString(currentTemperature));
		currentTemperatureString = currentTemperature == 0xff ? "Unknown" : String.valueOf(currentTemperature / tempScale);
		heatingModeString = heatingMode == 0 ? "Ready"
				: heatingMode == 1 ? "Rest" : heatingMode == 3 ? "Ready in rest" : "unknown";
		reminderString = reminder == 0 ? "None"
				: reminder == 0x04 ? "Clean filter"
						: reminder == 0x0a ? "Check PH" : reminder == 0x09 ? "Check sanitizer" : "Unknown";
		sensorAString = holdMode ? ("" + sensorA + " minutes in hold mode") : String.valueOf(sensorA / tempScale);
		sensorBString = String.valueOf(sensorB / tempScale);
		heatingString = heating == 0 ? "OFF" : heating == 1 ? "Heating" : heating == 2 ? "Heat waiting" : "unknown";
		setTemperatureString = setTemperature == 0xff ? "Unknown" : String.valueOf(setTemperature / tempScale);
		filterModeString = filterMode == 0 ? "OFF" : filterMode == 1 ? "Cycle 1" : filterMode == 2 ? "Cylcle 2" : filterMode == 3 ? "Cycle 1&2" : "unknown";
	}

}
