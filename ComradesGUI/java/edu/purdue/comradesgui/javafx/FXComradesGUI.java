package edu.purdue.comradesgui.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;

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

	private ObservableList<ChessEngine> chessEngines;

	public FXComradesGUI() {

		chessEngines = FXCollections.observableArrayList();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("ComradesGUI - FX!");
		primaryStage.setResizable(false);

		GridPane grid = new GridPane();

		Scene scene = new Scene(grid, 900, 700);

		//----- Start: Add UI elements here

		Button loadEngineButton = new Button("Load Engine");

		ComboBox<ChessEngine> engineComboBox = new ComboBox<>();

		engineComboBox.setItems(chessEngines);

//		loadEngine


		loadEngineButton.setOnAction((actionEvent) -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select engine executable...");
			File file = fileChooser.showOpenDialog(primaryStage);

			if(file != null) {
				ChessEngine engine = new ChessEngine();
				engine.loadFromPath(file.getAbsolutePath());

				if(engine.hasLoaded()) {

					chessEngines.add(engine);
				}
			}
		});

		//----- End: Add UI elements here

		grid.add(loadEngineButton, 0, 0);
		grid.add(engineComboBox, 1, 0);

		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				System.exit(1);
			}
		});
	}
}
