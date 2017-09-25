package com.guillaumepayet.remotenumpad.server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class MainController implements INumpadServerListener {
	
	@FXML private Text tcpStatusText;
	@FXML private Text bthStatusText;
	
	
	@Override
	public void onKeyPressed(String keyName) {
		// TODO Implement a visualization
	}

	@Override
	public void onKeyReleased(String keyName) {
		// TODO Implement a visualization
	}

	@Override
	public void onStatusChanged(String status) {
		String uppercase = status.toUpperCase();
		String lowercase = status.toLowerCase();
		
		tcpStatusText.setText(uppercase);
		bthStatusText.setText(lowercase);
	}
	
	
	@FXML private void exit() { Platform.exit(); }
}
