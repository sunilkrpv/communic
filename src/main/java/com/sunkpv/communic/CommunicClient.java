package com.sunkpv.communic;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * CommunicClient: The core Client class that connects to the
 * {@link CommunicClient}. Connection is one-one.
 * 
 * @author sunil
 * @since 26/Dec/2017
 *
 */
public final class CommunicClient {

	private DataReceiver dataReceiver;

	// constructor to put ip address and port
	public CommunicClient(String address, int port) {
		dataReceiver = new DataReceiver(address, port);
	}

	/**
	 * runs the client in synchronous (blocking) mode
	 */
	public void sync() {
		dataReceiver.process();
	}

	/**
	 * runs the client in asynchronous (non-blocking) mode
	 */
	public void async() {
		Thread t = new Thread(dataReceiver);
		t.start();
	}

	private class DataReceiver implements Runnable {

		// initialize socket and input output streams
		private Socket socket = null;
		private BufferedReader in = null;
		private DataInputStream inputStream = null;
		private DataOutputStream outputStream = null;
		private String address;
		private int port;

		DataReceiver(String address, int port) {
			this.address = address;
			this.port = port;
		}

		public void run() {
			process();
		}

		public void process() {
			// establish a connection
			try {
				socket = new Socket(address, port);
				System.out.println("Connected to server:" + address + "/" + port);
				// takes input from terminal
				in = new BufferedReader(new InputStreamReader(System.in));
				// takes input from server socket as well
				inputStream = new DataInputStream(socket.getInputStream());
				// sends output to the socket
				outputStream = new DataOutputStream(socket.getOutputStream());
				
				outputStream.writeUTF("hello");
				System.out.println(inputStream.readUTF());
			} catch (UnknownHostException ue) {
				System.out.println(ue);
			} catch (IOException ioe) {
				System.out.println(ioe);
			}

			// string to read message from input
			String line = "";

			// keep reading until "^C" is input
			while (!line.equals("^C")) {
				try {
					line = in.readLine();
					outputStream.writeUTF(line);
				} catch (IOException i) {
					System.out.println(i);
				}
			}

			// close the connection
			try {
				in.close();
				outputStream.close();
				socket.close();
			} catch (IOException i) {
				System.out.println(i);
			}
		}
	}
}
