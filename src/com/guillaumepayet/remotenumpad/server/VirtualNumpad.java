package com.guillaumepayet.remotenumpad.server;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class VirtualNumpad implements INumpadListener {
	
	private Robot robot;
	
	public VirtualNumpad() throws AWTException {
		robot = new Robot();
	}

	@Override
	public void onKeyPressed(String keyName) {
		robot.keyPress(keycode(keyName));
	}

	@Override
	public void onKeyReleased(String keyName) {
		robot.keyRelease(keycode(keyName));
	}
	
	
	private int keycode(String keyName) {
		try {
			return KeyEvent.VK_NUMPAD0 + Integer.parseInt(keyName);
		} catch (NumberFormatException e) {
			switch (keyName.toLowerCase()) {
			case "enter": return KeyEvent.VK_ENTER;
			case "/": return KeyEvent.VK_DIVIDE;
			case "*": return KeyEvent.VK_MULTIPLY;
			case "-": return KeyEvent.VK_SUBTRACT;
			case "+": return KeyEvent.VK_ADD;
			case ".": return KeyEvent.VK_DECIMAL;
			default: return KeyEvent.VK_NUM_LOCK;
			}
		}
	}
}
