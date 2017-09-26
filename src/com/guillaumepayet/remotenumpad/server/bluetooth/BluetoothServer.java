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
import com.guillaumepayet.remotenumpad.server.INumpadListener;
import com.guillaumepayet.remotenumpad.server.IStatusListener;

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
	

	private Collection<INumpadListener> numpadListeners;
	private Collection<IStatusListener> statusListeners;
	
	public BluetoothServer() {
		numpadListeners = new HashSet<>();
		statusListeners = new HashSet<>();
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
	public native void open();

	@Override
	public native void close();
	

	private void changeStatus(String status) {
		for (IStatusListener listener : statusListeners)
			listener.onStatusChanged(status);
	}
	
	private void dataReceived(String data) {
		if (data.startsWith("+")) {
			for (INumpadListener listener : numpadListeners)
				listener.onKeyPressed(data.substring(1));
		} else if (data.startsWith("-")) {
			for (INumpadListener listener : numpadListeners)
				listener.onKeyReleased(data.substring(1));
		}
	}
}
