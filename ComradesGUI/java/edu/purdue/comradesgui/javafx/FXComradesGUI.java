package edu.purdue.comradesgui.javafx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

	Font fontChess;
	private int boardSize;

	private int userSelX;
	private int userSelY;

	private ChessGame currentGame;

	public FXComradesGUI() {

		boardSize = 600;

		chessEngines = FXCollections.observableArrayList();
		currentGame = new ChessGame();

		try {
			FileInputStream fileInputStream = new FileInputStream(new File("MERIFONT.TTF"));
			fontChess = Font.loadFont(fileInputStream, getCheckerSize() - 4);
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public int getBoardSize(){
		return boardSize;
	}

	public int getCheckerSize() {
		return (int) (boardSize / 8d);
	}

	private void startAnimationTimer() {

		if(animationTimer == null) {
			animationTimer = new AnimationTimer() {
				@Override
				public void handle(long now) {
					GraphicsContext graphics = boardCanvas.getGraphicsContext2D();
					graphics.clearRect(0, 0, boardSize, boardSize);

					char[][] parseFEN = currentGame.getParsedFEN();
					int checkerSize = getCheckerSize();

					for(int x = 0; x < 8; x++) {
						for(int y = 0; y < 8; y++) {

							if((x + y) % 2 == 0)
								graphics.setFill(Color.WHITE);
							else
								graphics.setFill(Color.LIGHTGRAY);

							graphics.fillRect(x * checkerSize, y * checkerSize, checkerSize, checkerSize);

							char selChar = parseFEN[x][y];

							if(Character.isLetter(selChar)) {

								Font f = fontChess;//Font.font("Consolas", FontWeight.BOLD, 22);

								String str = "" + selChar;

								Text text = new Text(str);
								text.setFont(f);

								int xLoc = (x) * checkerSize;
								int yLoc = checkerSize + (y) * checkerSize;

								xLoc += (int) ((((double) checkerSize) - text.getLayoutBounds().getWidth()) / 2d) - 1;
								yLoc -= (int) ((((double) checkerSize) - text.getLayoutBounds().getHeight()) / 2d) + 1;

								graphics.setFont(f);

								if(Character.isUpperCase(selChar)) {
									graphics.setFill(Color.GRAY);

									graphics.fillText(str, xLoc + 1, yLoc + 1);
									graphics.setFill(Color.BEIGE);
									graphics.fillText(str, xLoc, yLoc);
								}
								else {
									graphics.setFill(Color.BLACK);
									graphics.fillText(str, xLoc, yLoc);
								}
							}
						}
					}
				}
			};

			animationTimer.start();
		}
	}

	private AnimationTimer animationTimer;
	private Canvas boardCanvas;

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("ComradesGUI - FX!");
		primaryStage.setResizable(false);

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

		boardCanvas = new Canvas(boardSize, boardSize);

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

		boardCanvas.setOnMouseClicked((event) -> {
			userSelX = (int) (event.getX() / getCheckerSize());
			userSelY = (int) (event.getY() / getCheckerSize());
		});

		//----- End: Add UI elements here

		grid.add(engineComboBox, 0, 0);
		grid.add(boardCanvas, 0, 1);

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
