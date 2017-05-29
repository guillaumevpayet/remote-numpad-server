package com.GuillaumePayet.RemoteNumpad.server;

public interface INumpadServer {
	void addListener(INumpadServerListener listener);
	void removeListener(INumpadServerListener listener);
	void open();
	void close();
}
