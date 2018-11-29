package edu.purdue.comradesgui.javafx;

import edu.purdue.comradesgui.src.ChessEngine;
import edu.purdue.comradesgui.src.ChessGame;
import edu.purdue.comradesgui.src.ChessPlayer;
import edu.purdue.comradesgui.src.ComradesMain;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class FXComradesGUI extends Application {

	private ComradesMain comradesMain;

	private FXChessBoard chessBoard;

	private ComboBox<ChessPlayer> whitePlayerCombo;
	private ComboBox<ChessPlayer> blackPlayerCombo;
	private CheckBox useTimerCheckBox;
	private CheckBox useTimerDelay;
	private TextField timerDurationTextField;
	private CheckBox useDelayAsBuffer;
	private TextField timerDelayTextField;
	private Button startGameButton;

	private Text blackTimerLabel = new Text("Black ChessPlayer Clock:");
	private Text whiteTimerlabel = new Text("White ChessPlayer Clock:");
	private Text blackTimerFeed = new Text("");
	private Text whiteTimerFeed = new Text("");

	private TitledPane gameInfoPane;

	private Stage optionsStage;

	public FXComradesGUI() {
		comradesMain = new ComradesMain();

	}

	private void updateButtons() {

		if(!comradesMain.getCurrentGame().isGameStarted()) {

			boolean enableStart = true;

			if(whitePlayerCombo.getValue() == null)
				enableStart = false;

			if(blackPlayerCombo.getValue() == null)
				enableStart = false;

			if(useTimerCheckBox.isSelected()) {
				timerDurationTextField.setDisable(false);
//				useTimerDelay.setDisable(false);
//				blackTimerFeed.setVisible(true);
//				whiteTimerFeed.setVisible(true);
//				blackTimerLabel.setVisible(true);
//				whiteTimerlabel.setVisible(true);
				gameInfoPane.setVisible(true);
			}
			else {
				timerDurationTextField.setDisable(true);
				useTimerDelay.setDisable(true);
//				blackTimerFeed.setVisible(false);
//				whiteTimerFeed.setVisible(false);
//				blackTimerLabel.setVisible(false);
//				whiteTimerlabel.setVisible(false);
				gameInfoPane.setVisible(false);
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
		else {
			useTimerCheckBox.setDisable(true);
			timerDurationTextField.setDisable(true);
			useTimerDelay.setDisable(true);
			useDelayAsBuffer.setDisable(true);
			timerDelayTextField.setDisable(true);
			startGameButton.setDisable(true);
			whitePlayerCombo.setDisable(true);
			blackPlayerCombo.setDisable(true);
		}
	}

	private boolean updatePlayerSelection(ComboBox<ChessPlayer> inCombo, ComboBox<ChessPlayer> otherCombo) {

		boolean success = true;

		ChessGame chessGame = comradesMain.getCurrentGame();

		if(inCombo.getValue() != null) {

			if(otherCombo.getValue() != null) {

				if(inCombo.getValue() == otherCombo.getValue()) {

					ChessPlayer selected = inCombo.getValue();

					if(selected.getPlayerType() == ChessPlayer.PlayerType.ENGINE) {
						inCombo.getSelectionModel().clearSelection();
						String msg = "You selected the same engine to play against itself, a copy of it must be added to continue.";
						Alert promptDuplicate = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK, ButtonType.CANCEL);
						promptDuplicate.setHeaderText("Duplicate Engine?");
						promptDuplicate.setTitle("Duplicate Engine?");
						promptDuplicate.showAndWait();

						if(promptDuplicate.getResult() == ButtonType.OK) {
							ChessEngine engine = (ChessEngine) selected;
							ChessEngine engineCopy = engine.copyEngine();

							comradesMain.addPlayer(engineCopy);
							inCombo.setValue(engineCopy);
						}
						else
							success = false;
					}
				}

			}
		}

		if(inCombo == blackPlayerCombo)
			chessGame.setBlackPlayer(inCombo.getValue());
		else
			chessGame.setWhitePlayer(inCombo.getValue());

		return success;
	}

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("ComradesGUI - FX!");
		primaryStage.setResizable(false);

		chessBoard = new FXChessBoard(600, comradesMain.getCurrentGame());

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

		HBox listBox = new HBox();
		listBox.setSpacing(4);
		listBox.setPadding(new Insets(4, 4, 4, 4));

		GridPane mainGrid = new GridPane();
		mainGrid.setHgap(8);
		mainGrid.setVgap(8);
		mainGrid.setPadding(new Insets(16, 16, 16, 16));
		mainGrid.setAlignment(Pos.TOP_LEFT);

		Text blackComboText = new Text("Black ChessPlayer: ");
		Text whiteComboText = new Text("White ChessPlayer: ");
		Text versusText = new Text("vs.");
		Text timerDurationText = new Text("Timer Duration: ");
		Text timerIncrementText = new Text("Timer Delay: ");
		whitePlayerCombo = new ComboBox<>();
		blackPlayerCombo = new ComboBox<>();
		useTimerCheckBox = new CheckBox("Use Timers");
		timerDurationTextField = new TextField();
		useTimerDelay = new CheckBox("Enable Delay");
		useDelayAsBuffer = new CheckBox("Use as Buffer?");
		timerDelayTextField = new TextField();
		blackTimerFeed = new Text("");
		whiteTimerFeed = new Text("");
		blackTimerLabel = new Text("Black ChessPlayer Clock:");
		whiteTimerlabel = new Text("White ChessPlayer Clock:");

		startGameButton = new Button("Start Game!");

		useTimerCheckBox.selectedProperty().addListener(((observable, oldValue, newValue) -> {

			if(newValue) {
				blackTimerFeed.fillProperty().bind(Bindings.when(comradesMain.getCurrentGame().getBlackTimer().getBufferCountDownProperty()).then(Color.GREEN).otherwise(Color.DODGERBLUE));
				blackTimerFeed.textProperty().bind(comradesMain.getCurrentGame().getBlackTimer().getTimerDisplayProperty());
				whiteTimerFeed.fillProperty().bind(Bindings.when(comradesMain.getCurrentGame().getWhiteTimer().getBufferCountDownProperty()).then(Color.GREEN).otherwise(Color.DODGERBLUE));
				whiteTimerFeed.textProperty().bind(comradesMain.getCurrentGame().getWhiteTimer().getTimerDisplayProperty());

				timerDurationTextField.textProperty().setValue("" + comradesMain.getCurrentGame().getTimerDuration());
			}
			comradesMain.getCurrentGame().setUseTimers(newValue);
			updateButtons();
		}));

		useTimerDelay.selectedProperty().addListener(((observable, oldValue, newValue) -> {

			if(newValue) {
				timerDelayTextField.textProperty().setValue("" + comradesMain.getCurrentGame().getTimerDelay());
			}
			comradesMain.getCurrentGame().setUseTimerDelay(newValue);
			updateButtons();
		}));

		useDelayAsBuffer.selectedProperty().addListener(((observable, oldValue, newValue) -> {
			comradesMain.getCurrentGame().setUseDelayAsBuffer(newValue);
			updateButtons();
		}));

		timerDurationTextField.textProperty().addListener(((observable, oldValue, newValue)  -> {

			if(newValue != null) {
				if(!newValue.isEmpty()) {

					String[] split = newValue.split(":");

					if(newValue.matches("\\d*")) {
						long lVal = Long.valueOf(newValue);

						long hours = TimeUnit.MILLISECONDS.toHours(lVal);
						long minutes = TimeUnit.MILLISECONDS.toMinutes(lVal) % 60;
						long seconds = TimeUnit.MILLISECONDS.toSeconds(lVal) % 60;

						timerDurationTextField.setText(hours + ":" + minutes + ":" + seconds);
					}
					else if(split.length == 3) {

						boolean goodFormat = true;

						long hoursVal = -1;
						long minutesVal = -1;
						long secondsVal = -1;

						if(!split[0].matches("\\d*"))
							goodFormat = false;
						else
							hoursVal = TimeUnit.HOURS.toMillis(Long.parseLong(split[0]));

						if(!split[1].matches("\\d*"))
							goodFormat = false;
						else
							minutesVal = TimeUnit.MINUTES.toMillis(Long.parseLong(split[1]));

						if(!split[2].matches("\\d*"))
							goodFormat = false;
						else
							secondsVal = TimeUnit.SECONDS.toMillis(Long.parseLong(split[2]));

						if(goodFormat) {

							long totalValue = hoursVal + minutesVal + secondsVal;

							long minutesNew = TimeUnit.MILLISECONDS.toMinutes(minutesVal);
							long secondsNew = TimeUnit.MILLISECONDS.toSeconds(secondsVal);

							if(secondsNew < 60 && minutesNew < 60)
								comradesMain.getCurrentGame().setTimerDuration(totalValue);
							else
								timerDurationTextField.setText(totalValue + "");
						}
						else
							timerDurationTextField.setText(oldValue);
					}
					else
						timerDurationTextField.setText(oldValue);
				}
				else
					timerDurationTextField.setText(oldValue);
			}
			else
				timerDurationTextField.setText(oldValue);

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

						boolean goodFormat = true;

						long hoursVal = -1;
						long minutesVal = -1;
						long secondsVal = -1;

						if(!split[0].matches("\\d*"))
							goodFormat = false;
						else
							hoursVal = TimeUnit.HOURS.toMillis(Long.parseLong(split[0]));

						if(!split[1].matches("\\d*"))
							goodFormat = false;
						else
							minutesVal = TimeUnit.MINUTES.toMillis(Long.parseLong(split[1]));

						if(!split[2].matches("\\d*"))
							goodFormat = false;
						else
							secondsVal = TimeUnit.SECONDS.toMillis(Long.parseLong(split[2]));

						if(goodFormat) {
							long totalValue = hoursVal + minutesVal + secondsVal;

							long minutesNew = TimeUnit.MILLISECONDS.toMinutes(minutesVal);
							long secondsNew = TimeUnit.MILLISECONDS.toSeconds(secondsVal);

							if(secondsNew < 60 && minutesNew < 60)
								comradesMain.getCurrentGame().setTimerDelay(totalValue);
							else
								timerDelayTextField.setText(totalValue + "");
						}
						else
							timerDelayTextField.setText(oldValue);
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
		gameSetupPane.setText("ChessPlayer Setup");
		gameSetupPane.setCollapsible(false);
		gameSetupPane.setAlignment(Pos.TOP_LEFT);

		gameSetupGrid.add(blackComboText, 0, 0);
		gameSetupGrid.add(blackPlayerCombo, 1, 0);
		gameSetupGrid.add(versusText, 0, 1);
		gameSetupGrid.add(whiteComboText, 0, 2);
		gameSetupGrid.add(whitePlayerCombo, 1, 2);
		gameSetupGrid.add(useTimerCheckBox, 0, 3);
		gameSetupGrid.add(timerDurationText, 0, 4);
		gameSetupGrid.add(timerDurationTextField, 1, 4);
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

				if(engine.hasLoadedFromFile())
					comradesMain.addPlayer(engine);
			}
		});

		optionsButton.setOnAction((actionEvent) -> {

			if(optionsStage != null) {
				try {
					optionsStage.show();
					optionsStage.requestFocus();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			else {
				optionsStage = new Stage();
				FXChessOptions optionsPanel = new FXChessOptions(this);
				optionsPanel.start(optionsStage);
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

		whitePlayerCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

			boolean success = updatePlayerSelection(whitePlayerCombo, blackPlayerCombo);
			if(!success)
				whitePlayerCombo.setValue(oldValue);
			else
				updateButtons();
		});

		blackPlayerCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

			boolean success = updatePlayerSelection(blackPlayerCombo, whitePlayerCombo);
			if(!success)
				blackPlayerCombo.setValue(oldValue);
			else
				updateButtons();
		});

		whitePlayerCombo.setPromptText("<none selected>");
		blackPlayerCombo.setPromptText("<none selected>");

		TitledPane boardPane = new TitledPane();
		boardPane.setText("Game Board");
		boardPane.setCollapsible(false);
		boardPane.setAlignment(Pos.TOP_LEFT);
		boardPane.setContent(chessBoard);

		GridPane gameInfoGrid = new GridPane();
		gameInfoGrid.setHgap(8);
		gameInfoGrid.setVgap(8);
		gameInfoGrid.setPadding(new Insets(16, 16, 16, 16));
		gameInfoGrid.setAlignment(Pos.TOP_LEFT);

		gameInfoGrid.add(blackTimerLabel, 1, 0);
		gameInfoGrid.add(blackTimerFeed, 2, 0);
		gameInfoGrid.add(whiteTimerlabel, 1, 2);
		gameInfoGrid.add(whiteTimerFeed, 2, 2);

		gameInfoPane = new TitledPane();
		gameInfoPane.setText("Game Info");
		gameInfoPane.setContent(gameInfoGrid);
		gameInfoPane.setCollapsible(false);
		gameInfoPane.setAlignment(Pos.TOP_LEFT);


		startGameButton.setOnAction((actionEvent) -> {

			ChessGame chessGame = comradesMain.getCurrentGame();

			if(!chessGame.isGameStarted()) {

				chessGame.startGame();

				startGameButton.setText("Started");
				startGameButton.setDisable(true);
			}
			updateButtons();
		});

		listBox.getChildren().addAll(boardPane, gameInfoPane, gameSetupPane);

		mainGrid.add(listBox,0,0);

		topBox.getChildren().add(mainGrid);

		chessBoard.getAnimationTimer().start();
		updateButtons();

		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest((event) -> System.exit(1));
	}

	public ComradesMain getComradesMain() {
		return comradesMain;
	}

	public FXChessBoard getChessBoard() {
		return chessBoard;
	}
}
