package com.mschwartz.balboa;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectClient {

	private Socket clientSocket;

	private DataOutputStream outToServer;

	ConnectClient(String ip) throws UnknownHostException, IOException {

		clientSocket = new Socket(ip, 4257);
		// create output stream attached to socket
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
	}

	public void write(byte[] content) throws IOException {
		outToServer.write(content);
		outToServer.flush();
	}

	public byte[] read() throws IOException {

		// create input stream attached to socket
		DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());

		// read line from server
		// displayBytes = inFromServer.readLine();

		byte[] content = new byte[100];
		int count = inFromServer.read(content);
		return StringHelper.copyFromArray(content, 0, count);
	}
	
	public void close() throws IOException {
		outToServer.close();
		clientSocket.close();
	}
}
