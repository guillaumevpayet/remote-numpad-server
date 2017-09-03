package com.guillaumepayet.remotenumpad.server.bluetooth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;

import com.guillaumepayet.remotenumpad.server.INumpadServer;
import com.guillaumepayet.remotenumpad.server.INumpadServerListener;

public class BluetoothServer implements INumpadServer {
	
	private static File extractResource(String path, String to) throws IOException {
		String prefix = path.substring(1, path.lastIndexOf('.'));
		String suffix = path.substring(path.lastIndexOf('.'));

		File file;
		
		if (to != null) {
			file = new File(to);
			file.createNewFile();
		} else {
			file = File.createTempFile(prefix, suffix);
		}
		
		if (!file.exists())
			throw new FileNotFoundException();
		
		file.deleteOnExit();
		
		try (InputStream in = BluetoothServer.class.getResourceAsStream(path)) {
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			
			try (OutputStream out = new FileOutputStream(file)) {
				out.write(buffer);
			}
		}
		
		return file;
	}
	
	static {
		String libraryPath = "/" + System.mapLibraryName("BluetoothServer");
		File library = null;
		
		try {
			library = extractResource(libraryPath, null);
		} catch (IOException e) {
			System.err.println("Unable to extract native libraries.");
			System.exit(1);
		}
		
		try {
			System.load(library.getPath());
			bluetoothAvailable = true;
		} catch (UnsatisfiedLinkError e) {
			System.out.println("Bluetooth is not available.");
			bluetoothAvailable = false;
		}
	}
	
	
	private static boolean bluetoothAvailable;
	
	public static boolean isBluetoothAvailable() { return bluetoothAvailable; }
	
	
	private Collection<INumpadServerListener> listeners;
	
	public BluetoothServer() {
		listeners = new HashSet<>();
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
	public native void open();

	@Override
	public native void close();
	

	private void changeStatus(String status) {
		for (INumpadServerListener listener : listeners)
			listener.onStatusChanged(status);
	}
	
	private void dataReceived(String data) {
		if (data.startsWith("+")) {
			for (INumpadServerListener listener : listeners)
				listener.onKeyPressed(data.substring(1));
		} else if (data.startsWith("-")) {
			for (INumpadServerListener listener : listeners)
				listener.onKeyReleased(data.substring(1));
		}
	}
}
