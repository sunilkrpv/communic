package com.sunkpv.communic;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * CommunicServer: The core Server class that accepts connections from
 * {@link CommunicClient} instances. Connection is one-many.
 * 
 * @author sunil
 * @since 26/Dec/2017
 *
 */
public final class CommunicServer {

	// initialize socket and input stream
	private ServerSocket serverSocket = null;
	private int port;
	

	private class DataReceiver implements Runnable {

		private DataInputStream inputStream = null;
		private DataOutputStream outputStream = null;
		private Socket connectedSocket = null;

		private DataReceiver(Socket connectedSocket) {
			this.connectedSocket = connectedSocket;
		}


		public void run() {
			
			try {

				// takes input from the client socket
				//inputStream = new DataInputStream(new BufferedInputStream(connectedSocket.getInputStream()));
				inputStream = new DataInputStream(connectedSocket.getInputStream());
				outputStream = new DataOutputStream(connectedSocket.getOutputStream());

				byte[] line = read();
				System.out.println(line);
				
				//outputStream.writeUTF(String.format("hello client, your id is: %d", connectedSocket.hashCode()));
				outputStream.write(("hello client, your id is: " + connectedSocket.hashCode()).getBytes());

				// reads message from client until "^C" is sent
				while (!line.equals("^C")) {
					try {
						
						line = read();
						System.out.println(connectedSocket.hashCode() + ": " + line);
					} catch (IOException i) {
						System.out.println(i);
						break;
					}
				}
				System.out.println(connectedSocket.hashCode() + " client aborted connection");

				// close connection
				connectedSocket.close();
				inputStream.close();
			} catch (IOException e) {
				System.out.println(e);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		
		public byte[] read() throws Exception {
			
			byte[] raw = new byte[1024*1024]; // 1KB for now //TODO make this configurable
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int read = 0;
			while( (read = inputStream.read(raw)) != -1) {
				buffer.write(raw, 0, read);
			}
			buffer.flush();
			return buffer.toByteArray();
		}

	}

	// constructor with port
	public CommunicServer(int port) {
		this.port = port;
	}

	private void run(Socket socket) {

		Thread client = new Thread(new DataReceiver(socket));
		client.start();
	}

	public void asyncStart() {

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Server started: " + serverSocket);

		Thread t = new Thread(new Runnable() {

			public void run() {

				syncStart();
			}
		});
		t.start();
	}

	public void syncStart() {

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Server started: " + serverSocket);

		while (true) {

			System.out.println("waiting for a new client connection...");

			try {
				Socket socket = serverSocket.accept();
				System.out.println("new client connection accepted: " + socket);
				CommunicServer.this.run(socket);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public void stop() {

		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
