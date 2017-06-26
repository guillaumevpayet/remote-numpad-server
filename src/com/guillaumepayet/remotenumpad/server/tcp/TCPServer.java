package com.guillaumepayet.remotenumpad.server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashSet;

import com.guillaumepayet.remotenumpad.server.INumpadServer;
import com.guillaumepayet.remotenumpad.server.INumpadServerListener;

public class TCPServer extends Thread implements INumpadServer {
	
	public static final int DEFAULT_PORT = 4444;
	
	
	private int port;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private Collection<INumpadServerListener> listeners;
	
	public TCPServer() {
		this.port = DEFAULT_PORT;
		listeners = new HashSet<>();
	}
	
	public TCPServer(int port) {
		this.port = port;
		listeners = new HashSet<>();
	}
	
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			changeStatus("Listening to port " + port);
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
				changeStatus("Client connected via TCP");
				
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
								listener.onKeyPressed(keyName);
						} else if (eventType == '-') {
							for (INumpadServerListener listener : listeners)
								listener.onKeyReleased(keyName);
						}
					}
				}
				
				in.close();
				reader.close();
				clientSocket.close();
				clientSocket = null;
				changeStatus("Client disconnected from TCP");
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
			changeStatus("Stopped listening");
		} catch (Exception e) {
			System.err.println("Error while closing the server: " + e.getMessage());
		}
	}
	
	
	private void changeStatus(String status) {
		for (INumpadServerListener listener : listeners)
			listener.onStatusChanged(status);
	}
}
