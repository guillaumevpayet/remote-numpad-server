package com.GuillaumePayet.RemoteNumpad.server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashSet;

import com.GuillaumePayet.RemoteNumpad.server.INumpadServer;
import com.GuillaumePayet.RemoteNumpad.server.INumpadServerListener;

public class TCPServer extends Thread implements INumpadServer {
	
	public static final int DEFAULT_PORT = 4444;
	
	
	private int port;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private Collection<INumpadServerListener> listeners;
	
	public TCPServer(int port) {
		this.port = port;
		listeners = new HashSet<INumpadServerListener>();
	}
	
	
	@Override
	public void run() {
		super.run();

		try {
			changeStatus("Starting...");
			serverSocket = new ServerSocket(port);
			changeStatus("Started");
		} catch (IOException e) {
			System.err.println("Unable to start the server: " + e.getMessage());
			return;
		}
		
		while (!serverSocket.isClosed()) {
			try {
				clientSocket = serverSocket.accept();
				InputStream stream = clientSocket.getInputStream();
				InputStreamReader reader = new InputStreamReader(stream);
				BufferedReader in = new BufferedReader(reader);
				changeStatus("Client connected");
				
				while (true) {
					String input;
					
					try {
						input = in.readLine();
					} catch (SocketException e) { break; }
					
					if (input == null) {
						break;
					} else {
						char eventType = input.charAt(0);
						String keyName = input.substring(1);
						
						if (eventType == '+') {
							for (INumpadServerListener listener : listeners)
								listener.keyPressed(keyName);
						} else if (eventType == '-') {
							for (INumpadServerListener listener : listeners)
								listener.keyReleased(keyName);
						}
					}
				}
				
				in.close();
				reader.close();
				clientSocket.close();
				clientSocket = null;
				changeStatus("Client disconnected");
			} catch (SocketException e) {
			} catch (Exception e) {
				System.err.println("Something went wrong: " + e.getMessage());
			}
		}
	}
	

	@Override
	public void addListener(INumpadServerListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(INumpadServerListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void open() { start(); }

	@Override
	public void close() {
		try {
			if (clientSocket != null)
				clientSocket.close();
			
			serverSocket.close();
			join();
			changeStatus("Stopped");
		} catch (Exception e) {
			System.err.println("Error while closing the server: " + e.getMessage());
		}
	}
	
	
	private void changeStatus(String status) {
		for (INumpadServerListener listener : listeners)
			listener.onStatusChange(status);
	}
}
