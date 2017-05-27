package com.GuillaumePayet.RemoteNumpad.server.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
		
		while (!serverSocket.isClosed()) {
			try {
				Socket clientSocket = serverSocket.accept();
				
				InputStream clientStream = clientSocket.getInputStream();
				ObjectInputStream in = new ObjectInputStream(clientStream);
				boolean ok = true;
				
				while (ok) {
					Object input = in.readObject();
					
					if (input == null) {
						ok = false;
					}
					else {
						String keyName = (String)input;
						
						for (IServerListener listener : listeners) {
							listener.keyPressed(keyName);
							listener.keyReleased(keyName);
						}
					}
				}
				
				in.close();
				clientSocket.close();
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
			serverSocket.close();
			join();
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
