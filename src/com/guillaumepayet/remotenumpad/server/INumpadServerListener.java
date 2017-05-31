package com.guillaumepayet.remotenumpad.server;

public interface INumpadServerListener {
	void onStatusChanged(String status);
	void onKeyPressed(String keyName);
	void onKeyReleased(String keyName);
}
