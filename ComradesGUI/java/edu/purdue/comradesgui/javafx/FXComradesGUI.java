package edu.purdue.comradesgui.javafx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

	private ComradesMain comradesMain;

	private Font fontChess;
	private int boardSize;

	private int userSelX;
	private int userSelY;

	public FXComradesGUI() {

		boardSize = 500;

		comradesMain = new ComradesMain();

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

					ChessCell[][] parseFEN = comradesMain.getCurrentGame().getCells();
					int checkerSize = getCheckerSize();

					for(int x = 7; x >= 0; x--) {
						for(int y = 7; y >= 0; y--) {

							if((x + y) % 2 == 0)
								graphics.setFill(Color.WHITE);
							else
								graphics.setFill(Color.LIGHTGRAY);

							graphics.fillRect(x * checkerSize, y * checkerSize, checkerSize, checkerSize);


							ChessCell selCell = parseFEN[x][y];

							if(selCell != null) {

								ChessPiece selPiece = selCell.getChessPiece();

								if (selPiece != null) {

									Character selChar = selPiece.getPieceChar();

									if (selChar != null) {

										Font f = fontChess;//Font.font("Consolas", FontWeight.BOLD, 22);

										String str = "" + selChar;

										Text text = new Text(str);
										text.setFont(f);

										int xLoc = (x) * checkerSize;
										int yLoc = checkerSize + (y) * checkerSize;

										xLoc += (int) ((((double) checkerSize) - text.getLayoutBounds().getWidth()) / 2d) - 1;
										yLoc -= (int) ((((double) checkerSize) - text.getLayoutBounds().getHeight()) / 2d) + 1;

										graphics.setFont(f);

										if (Character.isUpperCase(selChar)) {
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
					}
				}
			};

			animationTimer.start();
		}
	}

	private void checkCanStart() {

		boolean enableStart = true;

		if(whitePlayerCombo.getValue() == null)
			enableStart = false;

		if(blackPlayerCombo.getValue() == null)
			enableStart = false;

		startGameButton.setDisable(!enableStart);
	}

	private AnimationTimer animationTimer;
	private Canvas boardCanvas;

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

		boardCanvas = new Canvas(boardSize, boardSize);

		grid.setAlignment(Pos.TOP_LEFT);
		grid.setHgap(8);
		grid.setVgap(8);
		grid.setPadding(new Insets(16, 16, 16, 16));

		Text versusText = new Text("vs.");
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

				if(engine.hasLoaded()) {
					comradesMain.addPlayer(engine);
				}
			}
		});

		whitePlayerCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkCanStart());
		blackPlayerCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkCanStart());

		startGameButton.setOnAction((actionEvent) -> {

			if(whitePlayerCombo.getValue() == blackPlayerCombo.getValue()) {

				Player selected = whitePlayerCombo.getValue();

				if(selected.getPlayerType() == Player.PlayerType.ENGINE) {
					ChessEngine engine = (ChessEngine) selected;
					comradesMain.getCurrentGame().setWhitePlayer(engine);
					comradesMain.getCurrentGame().setBlackPlayer(engine.copyEngine());
				}
			}
			else {
				comradesMain.getCurrentGame().setWhitePlayer(whitePlayerCombo.getValue());
				comradesMain.getCurrentGame().setBlackPlayer(blackPlayerCombo.getValue());
			}

			comradesMain.startNewGame();
		});

		MoveTimer test = new MoveTimer();

		boardCanvas.setOnMouseClicked((event) -> {

			if(!test.isTimerStarted())
				test.start();
			else if(test.isTimerActive())
				test.pause();
			else
				test.resume();

			userSelX = (int) (event.getX() / getCheckerSize());
			userSelY = (int) (event.getY() / getCheckerSize());

			//currentGame.makeMove("a1b1");
	//		currentGame.addPiece('p', userSelX, userSelY);
		});

		whiteTimerText.fillProperty().bind(Bindings.when(test.getBufferCountDownProperty()).then(Color.GREEN).otherwise(Color.DODGERBLUE));
		whiteTimerText.textProperty().bind(test.getRemainingTime());

		//----- End: Add UI elements here

		grid.add(whitePlayerCombo, 0, 0);
		grid.add(versusText, 1, 0);
		grid.add(blackPlayerCombo, 2, 0);

		grid.add(startGameButton, 3, 0);
		grid.add(whiteTimerText, 4, 0);

		grid.add(boardCanvas, 0, 1, 4, 1);

		topBox.getChildren().add(grid);

		this.startAnimationTimer();
		checkCanStart();

		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest((event) -> System.exit(1));
	}
}
