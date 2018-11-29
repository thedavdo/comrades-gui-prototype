package edu.purdue.comradesgui.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;


public class FXChessOptions extends Application {

	private FXComradesGUI comradesGUI;

	public FXChessOptions(FXComradesGUI comradesGUI) {
		this.comradesGUI = comradesGUI;
	}

	public FXComradesGUI getComradesGUI() {

		return comradesGUI;
	}

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("Options");
		primaryStage.setResizable(false);

		TabPane tabPane = new TabPane();
		Scene scene = new Scene(tabPane, 450, 550);

		Tab boardOptionsTab = new FXTabChessBoardOptions(comradesGUI);
		boardOptionsTab.setText("Board");
		tabPane.getTabs().add(boardOptionsTab);

		Tab engineOptionTab = new FXTabChessEngineOptions(comradesGUI);
		engineOptionTab.setText("Engines");
		tabPane.getTabs().add(engineOptionTab);

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
