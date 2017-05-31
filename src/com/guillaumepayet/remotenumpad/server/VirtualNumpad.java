package com.guillaumepayet.remotenumpad.server;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import com.guillaumepayet.remotenumpad.server.tcp.TCPServer;

public class VirtualNumpad implements INumpadServerListener {

	public static void main(String[] args) {
		if (!SystemTray.isSupported()) {
			System.err.println("A system tray is required to run this application.");
			return;
		}
		
		INumpadServerListener listener = null;
		
		try {
			listener = new VirtualNumpad();
		} catch (AWTException e) {
			System.err.println("Unable to generate system events.");
			return;
		}

		INumpadServer server = new TCPServer();
		server.addListener(listener);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		URL iconURL = VirtualNumpad.class.getResource("/res/Icon.png");
		Image image = toolkit.getImage(iconURL);

		MenuItem exitItem = new MenuItem("Exit");
		PopupMenu popupMenu = new PopupMenu("Remote Numpad");
		popupMenu.add(exitItem);

		TrayIcon trayIcon = new TrayIcon(image, "Remote Numpad", popupMenu);
		SystemTray systemTray = SystemTray.getSystemTray();
		
		try {
			systemTray.add(trayIcon);
		} catch (AWTException e) {
			System.err.println("Unable to create the system tray icon: " + e.getMessage());
			return;
		}
		
		exitItem.addActionListener((ActionEvent e) -> {
			server.close();
			systemTray.remove(trayIcon);
		});
		
		server.open();
	}
	
	
	private Robot robot;
	
	public VirtualNumpad() throws AWTException {
		robot = new Robot();
	}

	@Override
	public void onStatusChanged(String status) {
		System.out.println("Server status: " + status);
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
