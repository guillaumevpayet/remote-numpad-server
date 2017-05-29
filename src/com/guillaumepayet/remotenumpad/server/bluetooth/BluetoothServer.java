package com.guillaumepayet.remotenumpad.server.bluetooth;

import com.guillaumepayet.remotenumpad.server.INumpadServer;
import com.guillaumepayet.remotenumpad.server.INumpadServerListener;

public class BluetoothServer implements INumpadServer {

	@Override
	public native void addListener(INumpadServerListener listener);

	@Override
	public native void removeListener(INumpadServerListener listener);

	@Override
	public native void open();

	@Override
	public native void close();
}
