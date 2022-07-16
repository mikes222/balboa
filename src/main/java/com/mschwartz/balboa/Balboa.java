package com.mschwartz.balboa;

import java.io.IOException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.choice.RangeArgumentChoice;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Balboa {

	private static StatusModel lastStatusModel;

	public static void main(String[] args) throws IOException {

		ArgumentParser parser = ArgumentParsers.newFor("Balboa").build().defaultHelp(true)
				.description("Balboa water spa command line controller");
		parser.addArgument("-l", "--lights").choices("ON", "OFF", "TOGGLE")// .setDefault("TOGGLE")
				.help("Control the lights");
		parser.addArgument("-s", "--settemp").type(Integer.class).choices(new RangeArgumentChoice<Integer>(10, 104))
				.help("Set the desired temperature (in celsius or fahrenheit depending on the current setting)");
		parser.addArgument("-c", "--connect").help("Skip discovery and directly contact the SPA with the given IP");
		parser.addArgument("--heatingmode").choices("READY", "REST", "TOGGLE").help("Toggle heating mode");
		parser.addArgument("-tm", "--temperatures").choices("NO", "PRINT")
				.help("Comma-separated: Current temperature, Set temperature");
		Namespace ns = null;
		try {
			ns = parser.parseArgs(args);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}

		String ip = null;
		if (ns.getString("connect") == null) {
			DiscoveryClient discoveryClient = new DiscoveryClient(null);
			discoveryClient.send();
			int count = 1;
			while (ip == null) {
				ip = discoveryClient.receive();
				System.out.println(count + ". IP of spa is '" + ip + "'");
				++count;
			}
			discoveryClient.close();
		} else {
			ip = ns.getString("connect");
		}

		ConnectClient connectClient = new ConnectClient(ip);
		read(connectClient);

		if (ns.getString("lights") != null) {
			if (ns.getString("lights").equalsIgnoreCase("ON")) {
				if (lastStatusModel != null && lastStatusModel.getLight1() == 0) {
					printContent(lastStatusModel);
					sendPayload(new byte[] { 0x0a, (byte) 0xbf, 0x11 }, new byte[] { 0x11, 0x00 }, connectClient);
					read(connectClient);
					printContent(lastStatusModel);
				}
			} else if (ns.getString("lights").equalsIgnoreCase("OFF")) {
				if (lastStatusModel != null && lastStatusModel.getLight1() != 0) {
					printContent(lastStatusModel);
					sendPayload(new byte[] { 0x0a, (byte) 0xbf, 0x11 }, new byte[] { 0x11, 0x00 }, connectClient);
					read(connectClient);
					printContent(lastStatusModel);
				}
			} else {
				printContent(lastStatusModel);
				sendPayload(new byte[] { 0x0a, (byte) 0xbf, 0x11 }, new byte[] { 0x11, 0x00 }, connectClient);
				read(connectClient);
				printContent(lastStatusModel);
			}
		}

		if (ns.getString("heatingmode") != null) {
			if (ns.getString("heatingmode").equalsIgnoreCase("READY")) {
				if (lastStatusModel != null && lastStatusModel.getHeatingMode() != 0) {
					printContent(lastStatusModel);
					sendPayload(new byte[] { 0x0a, (byte) 0xbf, 0x11 }, new byte[] { 0x51, 0x00 }, connectClient);
					read(connectClient);
					printContent(lastStatusModel);
				}
			} else if (ns.getString("heatingmode").equalsIgnoreCase("REST")) {
				if (lastStatusModel != null && lastStatusModel.getHeatingMode() == 0) {
					printContent(lastStatusModel);
					sendPayload(new byte[] { 0x0a, (byte) 0xbf, 0x11 }, new byte[] { 0x51, 0x00 }, connectClient);
					read(connectClient);
					printContent(lastStatusModel);
				}
			} else {
				printContent(lastStatusModel);
				sendPayload(new byte[] { 0x0a, (byte) 0xbf, 0x11 }, new byte[] { 0x51, 0x00 }, connectClient);
				read(connectClient);
				printContent(lastStatusModel);
			}
		}

		if (lastStatusModel != null && ns.getString("settemp") != null) {
			int temp = ns.getInt("settemp");
			if (lastStatusModel.isCelsius())
				temp = temp * 2;
			printContent(lastStatusModel);
			sendPayload(new byte[] { 0x0a, (byte) 0xbf, 0x20 }, new byte[] { (byte) temp }, connectClient);
			read(connectClient);
			printContent(lastStatusModel);
		}
		if (lastStatusModel != null && ns.getString("temperatures") != null) {
			if (ns.getString("temperatures").equalsIgnoreCase("PRINT")) {
				System.out.println(lastStatusModel.getCurrentTemperatureString() + "," + lastStatusModel.getSetTemperatureString());
			}
		}
		connectClient.close();
	}

	private static void sendPayload(byte[] type, byte[] payload, ConnectClient connectClient) throws IOException {

		int length = 5 + payload.length;
		byte[] lengthTypePayload = new byte[1 + type.length + payload.length];
		lengthTypePayload[0] = (byte) (length & 0xff);
		StringHelper.copyToArray(lengthTypePayload, 1, type);
		StringHelper.copyToArray(lengthTypePayload, 1 + type.length, payload);

		int checksum = computeChecksum(lengthTypePayload);

		byte[] telegram = new byte[1 + lengthTypePayload.length + 1 + 1];
		// startbyte
		telegram[0] = 0x7e;
		// length (1 byte), type of message (3 byte), payload (variable)
		StringHelper.copyToArray(telegram, 1, lengthTypePayload);
		// checksum (CRC8, 1 byte)
		telegram[telegram.length - 2] = (byte) checksum;
		// endbyte
		telegram[telegram.length - 1] = 0x7e;

		connectClient.write(telegram);
	}

	private static int computeChecksum(byte[] content) {
		Crc81 crc8 = new Crc81();
		int calculatedChecksum = crc8.calculate(0x02, content);
		calculatedChecksum = calculatedChecksum ^ 0x02;
		// System.out.println("Checksum for " + StringHelper.asHexColon(content) + " is
		// " + Integer.toHexString(calculatedChecksum));
		return calculatedChecksum & 0xff;
	}

	private static void read(ConnectClient connectClient) throws IOException {
		byte[] content = connectClient.read();

		// https://github.com/ccutrer/balboa_worldwide_app/blob/main/doc/protocol.md

		// https://github.com/ccutrer/balboa_worldwide_app/wiki

		// https://github.com/vincedarley/homebridge-plugin-bwaspa/blob/master/src/spaClient.ts

		if (content.length < 5) {
			System.out.println("Received content which is too short " + StringHelper.asHexColon(content));
			return;
		}
		if (content[0] != 0x7e) {
			// Message start
			System.out.println("Received content without message start " + StringHelper.asHexColon(content));
			return;
		}
		int length = content[1];
		if (content.length != length + 2) {
			System.out.println("Received content with length mismatch " + StringHelper.asHexColon(content));
			return;
		}
		if (content[content.length - 1] != 0x7e) {
			// Message end
			System.out.println("Received content without message end " + StringHelper.asHexColon(content));
			return;
		}
		int checksum = content[content.length - 2] & 0xff;
		int calculatedChecksum = computeChecksum(StringHelper.copyFromArray(content, 1, content.length - 3));
		if (checksum != calculatedChecksum) {
			System.out.println("Received content with invalid checksum (" + Integer.toHexString(checksum) + " vs "
					+ Integer.toHexString(calculatedChecksum) + ")" + StringHelper.asHexColon(content));
			return;
		}

		int idx = StringHelper.indexOf(content, 0, content.length, new byte[] { (byte) 0xFF, (byte) 0xAF, 0x13 });
		if (idx > 0) {
			lastStatusModel = statusUpdate(content, idx);
		} else {
			System.out.println("Received " + StringHelper.asHexColon(content));

		}
	}

	private static StatusModel statusUpdate(byte[] content, int idx) {
		// 2 byte (7e, 20) before the signature
		byte[] frame = StringHelper.copyFromArray(content, idx + 3, content.length - idx - 3);

		StatusModel statusModel = new StatusModel(frame);

		return statusModel;
	}

	private static void printContent(StatusModel statusModel) {
		System.out.println("----");
		System.out.println("Current time: " + statusModel.getCurrentHour() + ":" + statusModel.getCurrentMinute());
		System.out.println("Current temperature: " + statusModel.getCurrentTemperatureString() + ", Set temperature: "
				+ statusModel.getSetTemperatureString() + ", Sensor A: " + statusModel.getSensorAString()
				+ ", Sensor B: " + statusModel.getSensorBString());
		System.out.println("Heating mode: " + statusModel.getHeatingModeString() + ", TemperatureRange: "
				+ (statusModel.getTemperatureRange() > 0 ? "High" : "Low") + ", Needs heat: "
				+ (statusModel.getNeedsHeat() > 0 ? "Yes" : "No") + ", Heating: " + statusModel.getHeatingString()
				+ ", A/B Temperatures on: " + (statusModel.isTempsOn() ? "Yes" : "No"));
		System.out.println("Pump statuses: " + statusModel.getPumpStatus1() + "/" + statusModel.getPumpStatus2() + "/"
				+ statusModel.getPumpStatus3() + "/" + statusModel.getPumpStatus4() + "/" + statusModel.getPumpStatus5()
				+ "/" + statusModel.getPumpStatus6() + ", CirculationPump: "
				+ (statusModel.getCirculationPump() > 0 ? "Yes" : "No") + ", Blower: "
				+ (statusModel.getBlower() > 0 ? "Yes" : "No") + ", Mister: "
				+ (statusModel.getMister() > 0 ? "Yes" : "No"));
		System.out.println("Light 1: " + (statusModel.getLight1() > 0 ? "On" : "Off") + ", Light 2: "
				+ (statusModel.getLight2() > 0 ? "On" : "Off"));
		System.out.println("Hold mode: " + (statusModel.isHoldMode() ? "Yes" : "No") + ", Test mode: "
				+ (statusModel.isTestMode() ? "Yes" : "No") + ", Initializing: "
				+ (statusModel.isInitializing() ? "Yes" : "No") + ", Priming: "
				+ (statusModel.getPriming() > 0 ? "Yes" : "No"));
		System.out.println("TemperatureScale: " + (statusModel.isCelsius() ? "Celsius" : "Fahrenheit") + ", HourMode: "
				+ (statusModel.isH24() ? "24" : "12") + ", M8 Cycle time: " + statusModel.getM8cycleTime() + " minutes"
				+ ", Panel locked: " + (statusModel.isPanelLocked() ? "Yes" : "No"));
		System.out.println(
				"FilterMode: " + statusModel.getFilterModeString() + ", Reminder: " + statusModel.getReminderString());

	}
}
