package com.guillaumepayet.remotenumpad.server;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.guillaumepayet.remotenumpad.server.bluetooth.BluetoothServer;
import com.guillaumepayet.remotenumpad.server.tcp.TCPServer;

public class VirtualNumpad implements INumpadServerListener {

	public static void main(String[] args) throws InterruptedException, IOException {
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
		
		INumpadServer tcpServer = new TCPServer();
		tcpServer.addListener(listener);
		
		INumpadServer bluetoothServer = new BluetoothServer();
		bluetoothServer.addListener(listener);
		
		URL imageURL = listener.getClass().getResource("/res/Icon.png");
		Image image = ImageIO.read(imageURL);

		MenuItem exitItem = new MenuItem("Exit");
		PopupMenu popupMenu = new PopupMenu();
		popupMenu.add(exitItem);
		
		TrayIcon trayIcon = new TrayIcon(image, "Remote Numpad Server", popupMenu);
		trayIcon.setImageAutoSize(true);
		SystemTray systemTray = SystemTray.getSystemTray();
		
		try {
			systemTray.add(trayIcon);
		} catch (AWTException e) {
			System.err.println("Unable to create the system tray icon: " + e.getMessage());
			return;
		}
		
		exitItem.addActionListener((ActionEvent e) -> {
			tcpServer.close();
			
			if (BluetoothServer.isBluetoothAvailable())
				bluetoothServer.close();
			
			systemTray.remove(trayIcon);
		});
		
		tcpServer.open();

		if (BluetoothServer.isBluetoothAvailable())
			bluetoothServer.open();
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
