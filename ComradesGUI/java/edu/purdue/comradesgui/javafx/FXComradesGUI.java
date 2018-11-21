package edu.purdue.comradesgui.javafx;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

	private ComradesMain comradesMain;

	private int userSelX;
	private int userSelY;

	public FXComradesGUI() {

		comradesMain = new ComradesMain();
	}

	private void checkCanStart() {

		boolean enableStart = true;

		if(whitePlayerCombo.getValue() == null)
			enableStart = false;

		if(blackPlayerCombo.getValue() == null)
			enableStart = false;

		startGameButton.setDisable(!enableStart);
	}

	private FXChessBoard chessBoard;

	private ComboBox<Player> whitePlayerCombo;
	private ComboBox<Player> blackPlayerCombo;
	private Button startGameButton;

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

		chessBoard = new FXChessBoard(500, comradesMain.getCurrentGame());

		grid.setAlignment(Pos.TOP_LEFT);
		grid.setHgap(8);
		grid.setVgap(8);
		grid.setPadding(new Insets(16, 16, 16, 16));

		Text versusText = new Text("vs.");
		Text blackTimerText = new Text("-");
		Text whiteTimerText = new Text("-");
		whitePlayerCombo = new ComboBox<>();
		blackPlayerCombo = new ComboBox<>();

		startGameButton = new Button("Start Game!");

		whitePlayerCombo.setMaxWidth(200);
		blackPlayerCombo.setMaxWidth(200);
		whitePlayerCombo.setItems(comradesMain.getPlayerList());
		blackPlayerCombo.setItems(comradesMain.getPlayerList());

		importEngineButton.setOnAction((actionEvent) -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select engine executable...");
			File file = fileChooser.showOpenDialog(primaryStage);

			if(file != null) {
				ChessEngine engine = new ChessEngine();
				engine.loadFromPath(file.getAbsolutePath());

				if(engine.hasLoadedFromFile()) {
					comradesMain.addPlayer(engine);
				}
			}
		});

		whitePlayerCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkCanStart());
		blackPlayerCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkCanStart());

		startGameButton.setOnAction((actionEvent) -> {

			ChessGame chessGame = comradesMain.getCurrentGame();

			if(!chessGame.isGameStarted()) {

				if(whitePlayerCombo.getValue() == blackPlayerCombo.getValue()) {

					Player selected = whitePlayerCombo.getValue();

					if(selected.getPlayerType() == Player.PlayerType.ENGINE) {
						ChessEngine engine = (ChessEngine) selected;
						chessGame.setWhitePlayer(engine);
						chessGame.setBlackPlayer(engine.copyEngine());
					}
				}
				else {
					chessGame.setWhitePlayer(whitePlayerCombo.getValue());
					chessGame.setBlackPlayer(blackPlayerCombo.getValue());
				}

				chessGame.startGame();

				blackTimerText.fillProperty().bind(Bindings.when(chessGame.getBlackTimer().getBufferCountDownProperty()).then(Color.GREEN).otherwise(Color.DODGERBLUE));
				blackTimerText.textProperty().bind(chessGame.getBlackTimer().getTimerDisplayProperty());

				whiteTimerText.fillProperty().bind(Bindings.when(chessGame.getWhiteTimer().getBufferCountDownProperty()).then(Color.GREEN).otherwise(Color.DODGERBLUE));
				whiteTimerText.textProperty().bind(chessGame.getWhiteTimer().getTimerDisplayProperty());

				startGameButton.setText("Loading");
				//startGameButton.setDisable(true);
			}
			else {
				if(chessGame.isGamePaused()) {
					chessGame.setGamePaused(false);
					startGameButton.setText("Pause");
				}
				else {
					chessGame.setGamePaused(true);
					startGameButton.setText("Resume");
				}
			}
		});

		//TextField tx = new TextField();

		chessBoard.setOnMouseClicked((event) -> {
			userSelX = (int) (event.getX() / chessBoard.getCheckerSize());
			userSelY = (int) (event.getY() / chessBoard.getCheckerSize());

			//((ChessEngine) comradesMain.getCurrentGame().getWhitePlayer()).requestCommand("isready", true);
		});

		//----- End: Add UI elements here

		grid.add(whitePlayerCombo, 0, 0);
		grid.add(versusText, 1, 0);
		grid.add(blackPlayerCombo, 2, 0);

		grid.add(startGameButton, 3, 0);
		grid.add(whiteTimerText, 4, 0);
		grid.add(blackTimerText, 4, 1);

		grid.add(chessBoard, 0, 1, 4, 1);

		topBox.getChildren().add(grid);

		chessBoard.getAnimationTimer().start();
		checkCanStart();

		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest((event) -> System.exit(1));
	}
}
