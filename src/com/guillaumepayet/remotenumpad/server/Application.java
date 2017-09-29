package com.guillaumepayet.remotenumpad.server;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import com.guillaumepayet.remotenumpad.server.bluetooth.BluetoothServer;
import com.guillaumepayet.remotenumpad.server.tcp.TCPServer;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {
	
	private static INumpadServer tcpServer;
	private static INumpadServer bluetoothServer;
	
	private static boolean ready = false;
	
	public static void main(String[] args) throws InterruptedException {
		tcpServer = new TCPServer();
		bluetoothServer = new BluetoothServer();
		
		Thread jfxThread = new Thread(() -> Application.launch(args));
		jfxThread.start();
		
		while (!ready)
			Thread.yield();

		tcpServer.open();
		
		if (BluetoothServer.isBluetoothAvailable())
			bluetoothServer.open();
		
		jfxThread.join();
	}
	
	
	private Stage stage = null;
	
	private SystemTray systemTray = null;
	private TrayIcon trayIcon = null;
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		IStatusListener statusListener = initStage(primaryStage);
		INumpadListener numpadListener = null;
		
		try {
			numpadListener = new VirtualNumpad();
		} catch (AWTException e) {
			System.err.println("Unable to generate system events.");
			return;
		}
		
		tcpServer.addNumpadListener(numpadListener);
		tcpServer.addStatusListener(statusListener);
		
		bluetoothServer.addNumpadListener(numpadListener);
		bluetoothServer.addStatusListener(statusListener);
		
		ready = true;
		
		initSystemTrayIcon();
	}
	
	@Override
	public void stop() throws Exception {
		if (BluetoothServer.isBluetoothAvailable())
			bluetoothServer.close();
		
		tcpServer.close();
		SwingUtilities.invokeLater(() -> systemTray.remove(trayIcon));
		super.stop();
	}
	
	
	private IStatusListener initStage(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root, 300, 100);
		
		primaryStage.setTitle("Remote Numpad");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		stage = primaryStage;
		
		return loader.getController();
	}
	
	private void initSystemTrayIcon() {
		if (!SystemTray.isSupported()) {
			System.out.println("No system tray found, not using system tray icon.");
			Platform.runLater(stage::show);
			return;
		}
		
		Image image;
		PopupMenu popupMenu = new PopupMenu();
		systemTray = SystemTray.getSystemTray();
		
		try {
			image = ImageIO.read(getClass().getResource("/res/Icon.png"));
			trayIcon = new TrayIcon(image, "Remote Numpad", popupMenu);
			systemTray.add(trayIcon);
		} catch (IOException e) {
			System.out.println("Could not load icon, not using system tray icon.");
			Platform.runLater(stage::show);
			return;
		} catch (AWTException e) {
			System.out.println("Could not add icon to system tray, not using system tray icon.");
			Platform.runLater(stage::show);
			return;
		}
		
		MenuItem openItem = new MenuItem("Open");
		openItem.addActionListener(this::showStage);
		
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(event -> Platform.exit());
		
		popupMenu.add(openItem);
		popupMenu.add(exitItem);
		
		trayIcon.addActionListener(this::showStage);
		trayIcon.setImageAutoSize(true);
		Platform.setImplicitExit(false);
	}
	
	
	private void showStage(ActionEvent event) {
		if (!stage.isShowing())
			Platform.runLater(stage::show);
	}
}
