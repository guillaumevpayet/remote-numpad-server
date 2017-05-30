package com.guillaumepayet.remotenumpad.server.bluetooth;

import com.guillaumepayet.remotenumpad.server.INumpadServer;
import com.guillaumepayet.remotenumpad.server.INumpadServerListener;

public class BluetoothServer implements INumpadServer {
	
	static {
		System.loadLibrary("BluetoothServer");
	}
	
	
	public BluetoothServer() { init(); }
	
	private native void init();
	

	@Override
	public native void addListener(INumpadServerListener listener);

	@Override
	public native void removeListener(INumpadServerListener listener);

	@Override
	public native void open();

	@Override
	public native void close();
}
