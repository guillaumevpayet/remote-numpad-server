package com.guillaumepayet.remotenumpad.server;

public interface INumpadServer {
	void addListener(INumpadServerListener listener);
	void removeListener(INumpadServerListener listener);
	void open();
	void close();
}
