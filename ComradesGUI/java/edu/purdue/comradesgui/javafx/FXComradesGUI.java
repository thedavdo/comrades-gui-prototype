package edu.purdue.comradesgui.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FXComradesGUI extends Application {


//	public static void displayWindow() {
//
//		try {
//			FXComradesGUI.launch(new String[1]);
//		}
//		catch (IllegalStateException e) {
//			e.printStackTrace();
//
//			try {
//				new FXComradesGUI().start(new Stage());
//			}
//			catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		}
//	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("ComradesGUI - FX!");
		primaryStage.setResizable(false);

		GridPane grid = new GridPane();

		Scene scene = new Scene(grid, 900, 700);

		//----- Start: Add UI elements here

		//----- End: Add UI elements here

		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				Platform.exit();
			}
		});
	}
}
