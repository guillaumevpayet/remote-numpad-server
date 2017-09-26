package com.guillaumepayet.remotenumpad.server;

public interface INumpad {
	void addNumpadListener(INumpadListener listener);
	void removeNumpadListener(INumpadListener listener);
}
