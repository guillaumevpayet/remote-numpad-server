package com.guillaumepayet.remotenumpad.server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashSet;

import com.guillaumepayet.remotenumpad.server.INumpadListener;
import com.guillaumepayet.remotenumpad.server.INumpadServer;
import com.guillaumepayet.remotenumpad.server.IStatusListener;

public class TCPServer extends Thread implements INumpadServer {
	
	public static final int DEFAULT_PORT = 4576;
	
	
	private int port;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private Collection<INumpadListener> numpadListeners;
	private Collection<IStatusListener> statusListeners;
	
	public TCPServer() {
		this.port = DEFAULT_PORT;
		numpadListeners = new HashSet<>();
		statusListeners = new HashSet<>();
	}
	
	public TCPServer(int port) {
		this.port = port;
		numpadListeners = new HashSet<>();
		statusListeners = new HashSet<>();
	}
	
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			changeStatus("TCP.0");
		} catch (IOException e) {
			System.err.println("Unable to start the server: " + e.getMessage());
			return;
		}
		
		while (!serverSocket.isClosed()) {
			try {
				clientSocket = serverSocket.accept();
				
				try (Writer out = new OutputStreamWriter(clientSocket.getOutputStream())) {
					InputStream inputStream = clientSocket.getInputStream();
					
					try (InputStreamReader reader = new InputStreamReader(inputStream)) {
						try (BufferedReader in = new BufferedReader(reader)) {
							changeStatus("TCP.2");
							
							while (true) {
								String input;
								
								try {
									input = in.readLine();
								} catch (SocketException e) { break; }
								
								if (input == null) {
									break;
								} else if (input.toLowerCase().equals("name")) {
									String name = InetAddress.getLocalHost().getHostName();
		                            out.write(name + '\n');
		                            out.flush();
								} else {
									char eventType = input.charAt(0);
									String keyName = input.substring(1);
									
									if (eventType == '+') {
										for (INumpadListener listener : numpadListeners)
											listener.onKeyPressed(keyName);
									} else if (eventType == '-') {
										for (INumpadListener listener : numpadListeners)
											listener.onKeyReleased(keyName);
									}
								}
							}
						}
					}
				}
				
				clientSocket.close();
				clientSocket = null;
				changeStatus("TCP.3");
			} catch (SocketException e) {
			} catch (Exception e) {
				System.err.println("Something went wrong: " + e.getMessage());
			}
		}
	}
	

	@Override
	public void addNumpadListener(INumpadListener listener) {
		numpadListeners.add(listener);
	}

	@Override
	public void removeNumpadListener(INumpadListener listener) {
		numpadListeners.remove(listener);
	}

	@Override
	public void addStatusListener(IStatusListener listener) {
		statusListeners.add(listener);
	}

	@Override
	public void removeStatusListener(IStatusListener listener) {
		statusListeners.remove(listener);
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
			changeStatus("TCP.1");
		} catch (Exception e) {
			System.err.println("Error while closing the server: " + e.getMessage());
		}
	}
	
	
	private void changeStatus(String status) {
		for (IStatusListener listener : statusListeners)
			listener.onStatusChanged(status);
	}
}
