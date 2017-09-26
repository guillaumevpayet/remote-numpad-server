package com.guillaumepayet.remotenumpad.server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

public class MainController implements IStatusListener {
	
	private static final Paint[] paints = {
			Paint.valueOf("green"),
			Paint.valueOf("grey"),
			Paint.valueOf("blue"),
			Paint.valueOf("green"),
			Paint.valueOf("orange")
	};
	
	
	@FXML private Text tcpStatusText;
	@FXML private Text bthStatusText;
	
	private Thread thread;
	

	@Override
	public void onStatusChanged(String status) {
		String[] parts = status.split("\\.");
		int code = Integer.parseInt(parts[1]);
		String message = interpretCode(code);
		
		if (parts.length == 3) {
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
		} else if (code == 3) {
			thread = new Thread(() -> {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				
				Platform.runLater(() -> onStatusChanged(parts[0] + ".0.0"));
			});
			
			thread.start();
		}
		
		if (parts[0].equals("TCP")) {
			tcpStatusText.setText(message);
			tcpStatusText.setFill(paints[code]);
		} else if (parts[0].equals("BTH")) {
			bthStatusText.setText(message);
			bthStatusText.setFill(paints[code]);
		}
	}
	
	
	@FXML private void exit() { Platform.exit(); }
	
	
	private String interpretCode(int code) {
		switch (code) {
		case 0: return "Server ready";
		case 1: return "Server stopped";
		case 2: return "Client connected";
		case 3: return "Client disconnected";
		case 4: return "Error";
		default: return "Unknown";
		}
	}
}
