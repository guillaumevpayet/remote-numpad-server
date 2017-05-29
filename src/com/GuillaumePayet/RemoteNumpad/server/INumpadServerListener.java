package com.GuillaumePayet.RemoteNumpad.server;

public interface INumpadServerListener {
	void onStatusChange(String status);
	void keyPressed(String keyName);
	void keyReleased(String keyName);
}
