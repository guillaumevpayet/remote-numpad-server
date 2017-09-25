package com.guillaumepayet.remotenumpad.server;

public interface INumpadListener {
	void onKeyPressed(String keyName);
	void onKeyReleased(String keyName);
}
