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
import com.GuillaumePayet.RemoteNumpad.server.INumpadListener;

public class TCPServer extends Thread implements INumpadServer {
	
	public static final int DEFAULT_PORT = 4444;
	
	
	private int port;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private Collection<INumpadListener> listeners;
	
	public TCPServer(int port) {
		this.port = port;
		listeners = new HashSet<INumpadListener>();
	}
	
	@Override
	public void run() {
		super.run();

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Unable to start the server: " + e.getMessage());
			return;
		}
		
		System.out.println("Server started");
		
		while (!serverSocket.isClosed()) {
			try {
				clientSocket = serverSocket.accept();
				InputStream stream = clientSocket.getInputStream();
				InputStreamReader reader = new InputStreamReader(stream);
				BufferedReader in = new BufferedReader(reader);
				System.out.println("Client connected");
				
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
							for (INumpadListener listener : listeners)
								listener.keyPressed(keyName);
						} else if (eventType == '-') {
							for (INumpadListener listener : listeners)
								listener.keyReleased(keyName);
						}
					}
				}
				
				in.close();
				reader.close();
				clientSocket.close();
				clientSocket = null;
				System.out.println("Client disconnected");
			} catch (SocketException e) {
			} catch (Exception e) {
				System.err.println("Something went wrong: " + e.getMessage());
			}
		}
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
			System.out.println("Server stopped");
		} catch (Exception e) {
			System.err.println("Error while closing the server: " + e.getMessage());
		}
	}

	@Override
	public void addListener(INumpadListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(INumpadListener listener) {
		listeners.remove(listener);
	}
}
