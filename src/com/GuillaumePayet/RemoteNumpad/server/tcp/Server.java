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

import com.GuillaumePayet.RemoteNumpad.server.IServer;
import com.GuillaumePayet.RemoteNumpad.server.IServerListener;

public class Server extends Thread implements IServer {
	
	public static final int DEFAULT_PORT = 4444;
	
	
	private int port;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private Collection<IServerListener> listeners;
	
	public Server(int port) {
		this.port = port;
		listeners = new HashSet<IServerListener>();
	}
	
	@Override
	public void run() {
		super.run();

		try {
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e) {
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
					String keyName;
					
					try {
						keyName = in.readLine();
					}
					catch (Exception e) {
						System.out.println(e.getClass().getSimpleName());
						break;
					}
					
					if (keyName == null) break;
					
					for (IServerListener listener : listeners) {
						listener.keyPressed(keyName);
						listener.keyReleased(keyName);
					}
				}
				
				in.close();
				reader.close();
				clientSocket.close();
				clientSocket = null;
				System.out.println("Client disconnected");
			}
			catch (SocketException e) {}
			catch (Exception e) {
				System.err.println("Something went wrong: " + e.getMessage());
			}
		}
	}

	@Override
	public void open() { start(); }

	@Override
	public void close() {
		try {
			if (clientSocket != null) {
				clientSocket.close();
				System.out.println("Client connection interrupted");
			}
			
			serverSocket.close();
			join();
			System.out.println("Server stopped");
		}
		catch (Exception e) {
			System.err.println("Error while closing the server: " + e.getMessage());
		}
	}

	@Override
	public void addListener(IServerListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(IServerListener listener) {
		listeners.remove(listener);
	}

}
