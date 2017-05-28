package com.GuillaumePayet.RemoteNumpad.server;

public interface IServer {
	
	void open();
	void close();
	void addListener(IServerListener listener);
	void removeListener(IServerListener listener);
	
}
