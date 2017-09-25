package com.guillaumepayet.remotenumpad.server;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainController {
	
	@FXML private void exit() {
		Platform.exit();
	}

}
