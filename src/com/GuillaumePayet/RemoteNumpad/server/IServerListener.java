package com.GuillaumePayet.RemoteNumpad.server;

public interface IServerListener {
	
	void keyPressed(String keyName);
	void keyReleased(String keyName);
	
}
