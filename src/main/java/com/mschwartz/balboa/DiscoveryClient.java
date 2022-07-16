package com.mschwartz.balboa;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DiscoveryClient {
	private DatagramSocket socket;

	/**
	 * Creates a new Discovery client.
	 * 
	 * @param ownIp own network adapter IP or null to use the default adapter
	 * @throws IOException
	 */
	public DiscoveryClient(String ownIp) throws IOException {
		InetAddress address = ownIp == null ? InetAddress.getLocalHost() : InetAddress.getByName(ownIp);
		socket = new DatagramSocket(0, address);
		socket.setSoTimeout(10000);
		// socket.setOption(StandardSocketOptions.SO_BROADCAST, Boolean.TRUE);
		// socket.setBroadcast(true);
		// socket.setOption(StandardSocketOptions.MUL, null);
	}

	public void send() throws IOException {

		byte[] data = "Discovery: Who is out there?".getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), 30303);
		socket.send(packet);
//		System.out.println("Discovery package sent. " + socket.getLocalSocketAddress() + " to " + packet.getAddress()
//				+ ":" + packet.getPort());

	}

	public String receive() throws IOException {
		// Discovery response command. Answer should be BWGSPA
		// <0d0a>00-15-27-E4-06-E8<0d0a>
		byte[] response = new byte[1000];
		DatagramPacket responsePacket = new DatagramPacket(response, response.length);
		socket.receive(responsePacket);
//		System.out
//				.println("Discovery response received!" + responsePacket.getAddress() + ":" + responsePacket.getPort());
		String responseData = new String(responsePacket.getData()).trim();
		if (responseData.contains("BWGSPA")) {
			System.out.println("Found Balboa SPA at " + responsePacket.getAddress() + ":" + responsePacket.getPort() + "   '"
					+ responseData + "'");
			String[] split = responseData.split("\n");
			if (split.length < 2)
				return null;
			return responsePacket.getAddress().getHostAddress();
		} else {
			return null;
		}
	}

	public void close() {
		socket.close();
	}
}
