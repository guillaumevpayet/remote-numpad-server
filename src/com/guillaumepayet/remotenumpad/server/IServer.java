package com.guillaumepayet.remotenumpad.server;

public interface IServer {
	void addStatusListener(IStatusListener listener);
	void removeStatusListener(IStatusListener listener);
	void open();
	void close();
}
