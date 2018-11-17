package edu.purdue.comradesgui.javafx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

	private char[][] parseFenString(String fen) {

		char[][] parsed = new char[8][8];

		String[] split = fen.split("/");

		int rowIndex = 0;
		for(String row : split) {

			char[] unparsed = row.toCharArray();

			int colIndex = 0;
			for(char in : unparsed) {

				if(Character.isAlphabetic(in)) {
					parsed[rowIndex][colIndex] = in;
					colIndex++;
				}
				else if(Character.isDigit(in)) {

					int numEmpty = Integer.parseInt(""+in);

					for(int i = 0; i < numEmpty; i++)
						colIndex++;
				}
			}
			rowIndex++;
		}

//		for(int i = 0; i < parsed.length; i++) {
//
//			char[] charArray = parsed[i];
//			String row = "";
//			for(int ii = 0; ii < charArray.length; ii++) {
//
//				char ch = charArray[ii];
//				if(Character.isLetter(ch))
//					row += ch;
//				else
//					row += "-";
//			}
//			System.out.println(row);
//		}

		return parsed;
	}

	private void startAnimationTimer() {

		if(animationTimer == null) {
			animationTimer = new AnimationTimer() {
				@Override
				public void handle(long now) {
					GraphicsContext graphics = chessCanvas.getGraphicsContext2D();
					graphics.clearRect(0, 0, chessCanvas.getWidth(), chessCanvas.getHeight());

					int checkerSize = (int) (chessCanvas.getWidth() / 8d);

					for(int x = 0; x < 8; x++) {
						for(int y = 0; y < 8; y++) {

							if((x + y) % 2 == 0)
								graphics.setFill(Color.BEIGE);
							else
								graphics.setFill(Color.LIGHTGRAY);

							graphics.fillRect(x * checkerSize, y * checkerSize, checkerSize, checkerSize);
						}
					}
				}
			};

			animationTimer.start();
		}
	}

	private AnimationTimer animationTimer;
	private Canvas chessCanvas;

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("ComradesGUI - FX!");
		primaryStage.setResizable(false);


		//----- Start: Add UI elements here

		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu("File");

		MenuItem importEngineButton = new MenuItem("Import Engine");
		MenuItem saveAsButton = new MenuItem("Save as");
		MenuItem optionsButton = new MenuItem("Options");

		fileMenu.getItems().add(importEngineButton);
		fileMenu.getItems().add(saveAsButton);
		fileMenu.getItems().add(optionsButton);

		menuBar.getMenus().add(fileMenu);

		VBox topBox = new VBox(menuBar);
		Scene scene = new Scene(topBox, 900, 700);

		GridPane grid = new GridPane();

		chessCanvas = new Canvas(550, 550);

		grid.setAlignment(Pos.TOP_LEFT);
		grid.setHgap(8);
		grid.setVgap(8);
		grid.setPadding(new Insets(16, 16, 16, 16));

		ComboBox<ChessEngine> engineComboBox = new ComboBox<>();

		engineComboBox.setItems(chessEngines);

		importEngineButton.setOnAction((actionEvent) -> {
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

		grid.add(engineComboBox, 0, 0);
		grid.add(chessCanvas, 0, 1);

		topBox.getChildren().add(grid);

		this.startAnimationTimer();

		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				System.exit(1);
			}
		});
	}
}
