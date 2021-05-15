package com.sunkpv.communic;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * CommunicClient: The core Client class that connects to the
 * {@link CommunicClient}. Connection is one-one.
 * 
 * @author sunil
 * @since 26/Dec/2017
 *
 */
public final class CommunicClient {

	private Socket socket = null;
	private final String address;
	private final int port;
	private DataInputStream inputStream = null;
	private DataOutputStream outputStream = null;
	
	private final byte DISCONNECTED = -1, CONNECTED = 0;
	private byte state = DISCONNECTED;
	

	// constructor to put ip address and port
	public CommunicClient(String address, int port) {
		this.address = address;
		this.port = port;
	}
	
	public boolean connect() {
		
		// establish a connection
		try {
			socket = new Socket(address, port);
			System.out.println("Connected to server:" + address + "/" + port);
			// takes input from server socket as well
			inputStream = new DataInputStream(socket.getInputStream());
			// sends output to the socket
			outputStream = new DataOutputStream(socket.getOutputStream());
			
			outputStream.write("hello".getBytes());
			System.out.println(inputStream.readUTF());
		} catch (IOException ue) {
			System.out.println(ue);
			return false;
		} 
		
		state = CONNECTED;
		return true;
	}
	
	public void send(byte[] data) 
			throws Exception {
		
		checkConnection();
		
		outputStream.write(data);
	}

	private void checkConnection() throws Exception {
		if(state == DISCONNECTED) {
			throw new Exception("connect() method not called or connection not established successfully");
		}
	}
	
	public byte[] read() throws Exception {
		
		checkConnection();
		
		byte[] raw = new byte[1024*1024]; // 1KB for now //TODO make this configurable
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int read = 0;
		while( (read = inputStream.read(raw)) != -1) {
			buffer.write(raw, 0, read);
		}
		buffer.flush();
		return buffer.toByteArray();
	}
	
	public void disconnect() {
		try {
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException i) {
			System.out.println(i);
		}
	}

}
