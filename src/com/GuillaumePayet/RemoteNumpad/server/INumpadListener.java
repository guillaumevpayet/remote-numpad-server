package com.GuillaumePayet.RemoteNumpad.server;

public interface INumpadListener {
	void keyPressed(String keyName);
	void keyReleased(String keyName);
}
