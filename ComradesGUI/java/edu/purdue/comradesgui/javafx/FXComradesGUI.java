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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

	public FXComradesGUI() {
		comradesMain = new ComradesMain();

	}

	private void updateButtons() {

		boolean enableStart = true;

		if(whitePlayerCombo.getValue() == null)
			enableStart = false;

		if(blackPlayerCombo.getValue() == null)
			enableStart = false;

		if(useTimerCheckBox.isSelected()) {
			timerDuration.setDisable(false);
			useTimerDelay.setDisable(false);
		}
		else {
			timerDuration.setDisable(true);
			useTimerDelay.setDisable(true);
		}

		if(useTimerDelay.isSelected() && !useTimerDelay.isDisabled()) {
			useDelayAsBuffer.setDisable(false);
			timerDelayTextField.setDisable(false);
		}
		else {
			useDelayAsBuffer.setDisable(true);
			timerDelayTextField.setDisable(true);
		}

		startGameButton.setDisable(!enableStart);
	}

	private FXChessBoard chessBoard;

	private ComboBox<Player> whitePlayerCombo;
	private ComboBox<Player> blackPlayerCombo;
	private CheckBox useTimerCheckBox;
	private CheckBox useTimerDelay;
	private TextField timerDuration;
	private CheckBox useDelayAsBuffer;
	private TextField timerDelayTextField;
	private Button startGameButton;

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("ComradesGUI - FX!");
		primaryStage.setResizable(false);

		chessBoard = new FXChessBoard(400, comradesMain.getCurrentGame());

		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu("File");

		MenuItem importEngineButton = new MenuItem("Import Engine");
		MenuItem saveAsButton = new MenuItem("Save as");
		MenuItem optionsButton = new MenuItem("Options");

		fileMenu.getItems().add(importEngineButton);
		fileMenu.getItems().add(saveAsButton);
		fileMenu.getItems().add(optionsButton);

		menuBar.getMenus().add(fileMenu);

		Menu editMenu = new Menu("Edit");

		MenuItem setFENButton = new MenuItem("Set FEN");

		editMenu.getItems().add(setFENButton);

		menuBar.getMenus().add(editMenu);

		VBox topBox = new VBox(menuBar);
		Scene scene = new Scene(topBox, 1200, 700);

		GridPane mainGrid = new GridPane();
		mainGrid.setHgap(8);
		mainGrid.setVgap(8);
		mainGrid.setPadding(new Insets(16, 16, 16, 16));
		mainGrid.setAlignment(Pos.TOP_LEFT);

		Text blackComboText = new Text("Black Player: ");
		Text whiteComboText = new Text("White Player: ");
		Text versusText = new Text("vs.");
		Text timerDurationText = new Text("Timer Duration: ");
		Text timerIncrementText = new Text("Timer Delay: ");
		whitePlayerCombo = new ComboBox<>();
		blackPlayerCombo = new ComboBox<>();
		useTimerCheckBox = new CheckBox("Use Timers");
		timerDuration = new TextField();
		useTimerDelay = new CheckBox("Enable Delay");
		useDelayAsBuffer = new CheckBox("Use as Buffer?");
		timerDelayTextField = new TextField();

		startGameButton = new Button("Start Game!");

		useTimerCheckBox.selectedProperty().addListener(((observable, oldValue, newValue) -> {

			if(newValue) {
				timerDuration.textProperty().setValue("" + comradesMain.getCurrentGame().getTimerDuration());
			}

			updateButtons();
		}));

		useTimerDelay.selectedProperty().addListener(((observable, oldValue, newValue) -> {

			if(newValue) {
				timerDelayTextField.textProperty().setValue("" + comradesMain.getCurrentGame().getTimerDelay());
			}

			updateButtons();
		}));

		timerDuration.textProperty().addListener(((observable, oldValue, newValue) -> {

			if(newValue != null) {

				if(!newValue.isEmpty()) {

					String[] split = newValue.split(":");

					if(newValue.matches("\\d*")) {
						long lVal = Long.valueOf(newValue);

						long hours = TimeUnit.MILLISECONDS.toHours(lVal);
						long minutes = TimeUnit.MILLISECONDS.toMinutes(lVal) % 60;
						long seconds = TimeUnit.MILLISECONDS.toSeconds(lVal) % 60;

						timerDuration.setText(hours + ":" + minutes + ":" + seconds);
					}
					else if(split.length == 3) {
						for(int i = 0; i < 3; i++) {
							String str = split[i];
							if(!str.matches("\\d*")) {
								timerDuration.setText(oldValue);
								break;
							}
						}
					}
					else
						timerDuration.setText(oldValue);
				}
				else
					timerDuration.setText(oldValue);
			}
			else
				timerDuration.setText(oldValue);

			updateButtons();
		}));

		timerDelayTextField.textProperty().addListener(((observable, oldValue, newValue) -> {

			if(newValue != null) {

				if(!newValue.isEmpty()) {

					String[] split = newValue.split(":");

					if(newValue.matches("\\d*")) {
						long lVal = Long.valueOf(newValue);

						long hours = TimeUnit.MILLISECONDS.toHours(lVal);
						long minutes = TimeUnit.MILLISECONDS.toMinutes(lVal) % 60;
						long seconds = TimeUnit.MILLISECONDS.toSeconds(lVal) % 60;

						timerDelayTextField.setText(hours + ":" + minutes + ":" + seconds);
					}
					else if(split.length == 3) {
						for(int i = 0; i < 3; i++) {
							String str = split[i];
							if(!str.matches("\\d*")) {
								timerDelayTextField.setText(oldValue);
								break;
							}
						}
					}
					else
						timerDelayTextField.setText(oldValue);
				}
				else
					timerDelayTextField.setText(oldValue);
			}
			else
				timerDelayTextField.setText(oldValue);

			updateButtons();
		}));

		whitePlayerCombo.setMaxWidth(200);
		blackPlayerCombo.setMaxWidth(200);
		whitePlayerCombo.setItems(comradesMain.getPlayerList());
		blackPlayerCombo.setItems(comradesMain.getPlayerList());

		TitledPane gameSetupPane = new TitledPane();
		GridPane gameSetupGrid = new GridPane();
		gameSetupGrid.setHgap(8);
		gameSetupGrid.setVgap(8);
		gameSetupGrid.setPadding(new Insets(16, 16, 16, 16));
		gameSetupGrid.setAlignment(Pos.TOP_LEFT);

		gameSetupPane.setMinWidth(300);
		gameSetupPane.setContent(gameSetupGrid);
		gameSetupPane.setText("Player Setup");
		gameSetupPane.setCollapsible(false);
		gameSetupPane.setAlignment(Pos.TOP_LEFT);

		gameSetupGrid.add(blackComboText, 0, 0);
		gameSetupGrid.add(blackPlayerCombo, 1, 0);
		gameSetupGrid.add(versusText, 0, 1);
		gameSetupGrid.add(whiteComboText, 0, 2);
		gameSetupGrid.add(whitePlayerCombo, 1, 2);
		gameSetupGrid.add(useTimerCheckBox, 0, 3);
		gameSetupGrid.add(timerDurationText, 0, 4);
		gameSetupGrid.add(timerDuration, 1, 4);
		gameSetupGrid.add(useTimerDelay, 0, 5);
		gameSetupGrid.add(useDelayAsBuffer, 1, 5);
		gameSetupGrid.add(timerIncrementText, 0, 6);
		gameSetupGrid.add(timerDelayTextField, 1, 6);
		gameSetupGrid.add(startGameButton, 0, 8);


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

		setFENButton.setOnAction((actionEvent) -> {

			TextInputDialog dialog = new TextInputDialog(comradesMain.getCurrentGame().generateStringFEN());
			dialog.setTitle("Set Board FEN");
			dialog.setHeaderText("Set Board FEN");
			dialog.setContentText("FEN:");
			dialog.getDialogPane().setPrefWidth(350);

			Optional<String> result = dialog.showAndWait();

			result.ifPresent((inFEN) -> comradesMain.getCurrentGame().setBoardFromFEN(inFEN));
		});

		whitePlayerCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateButtons());
		blackPlayerCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateButtons());


		Text blackTimerLabel = new Text("Black Player Clock:");
		Text whiteTimerlabel = new Text("White Player Clock:");
		Text blackTimerFeed = new Text("");
		Text whiteTimerFeed = new Text("");

		TitledPane gameStatusPane = new TitledPane();
		gameStatusPane.setText("Game Info");
		gameStatusPane.setCollapsible(false);
		gameStatusPane.setAlignment(Pos.TOP_LEFT);
		gameStatusPane.setMinWidth(800);

		GridPane gameStatusGrid = new GridPane();
		gameStatusGrid.setHgap(8);
		gameStatusGrid.setVgap(8);
		gameStatusGrid.setPadding(new Insets(16, 16, 16, 16));
		gameStatusGrid.setAlignment(Pos.TOP_LEFT);

		gameStatusGrid.add(blackTimerLabel, 1, 0);
		gameStatusGrid.add(blackTimerFeed, 2, 0);
		gameStatusGrid.add(chessBoard, 0, 1);
		gameStatusGrid.add(whiteTimerlabel, 1, 2);
		gameStatusGrid.add(whiteTimerFeed, 2, 2);

		gameStatusPane.setContent(gameStatusGrid);



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

				if(useTimerCheckBox.isSelected()) {

					String[] durationSplit = timerDuration.getText().split(":");

					long durationMilli = 0;

					durationMilli += TimeUnit.HOURS.toMillis(Long.valueOf(durationSplit[0]));
					durationMilli += TimeUnit.MINUTES.toMillis(Long.valueOf(durationSplit[1]));
					durationMilli += TimeUnit.SECONDS.toMillis(Long.valueOf(durationSplit[2]));

					chessGame.setUseTimers(true);
					chessGame.setTimerDuration(durationMilli);

					if(useTimerDelay.isSelected()) {

						String[] delaySplit = timerDelayTextField.getText().split(":");

						long delayMilli = 0;

						delayMilli += TimeUnit.HOURS.toMillis(Long.valueOf(delaySplit[0]));
						delayMilli += TimeUnit.MINUTES.toMillis(Long.valueOf(delaySplit[1]));
						delayMilli += TimeUnit.SECONDS.toMillis(Long.valueOf(delaySplit[2]));

						chessGame.setTimerDelay(delayMilli);

						if(useDelayAsBuffer.isSelected())
							chessGame.setUseDelayAsBuffer(true);
					}
				}

				//chessGame.setTimerDelay(3000);
				//chessGame.setUseDelayAsIncrement(true);

				chessGame.startGame();

				blackTimerFeed.fillProperty().bind(Bindings.when(chessGame.getBlackTimer().getBufferCountDownProperty()).then(Color.GREEN).otherwise(Color.DODGERBLUE));
				blackTimerFeed.textProperty().bind(chessGame.getBlackTimer().getTimerDisplayProperty());

				whiteTimerFeed.fillProperty().bind(Bindings.when(chessGame.getWhiteTimer().getBufferCountDownProperty()).then(Color.GREEN).otherwise(Color.DODGERBLUE));
				whiteTimerFeed.textProperty().bind(chessGame.getWhiteTimer().getTimerDisplayProperty());

				startGameButton.setText("Started");

				startGameButton.setDisable(true);
			}
		});

		mainGrid.add(gameStatusPane,0,0, 1, 2);
		mainGrid.add(gameSetupPane,1,0);

		topBox.getChildren().add(mainGrid);

		chessBoard.getAnimationTimer().start();
		updateButtons();

		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest((event) -> System.exit(1));
	}
}
