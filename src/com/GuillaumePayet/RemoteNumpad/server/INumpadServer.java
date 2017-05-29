package com.GuillaumePayet.RemoteNumpad.server;

public interface INumpadServer {
	void addListener(INumpadListener listener);
	void removeListener(INumpadListener listener);
	void open();
	void close();
}
