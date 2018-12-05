package edu.purdue.comradesgui.src.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FXTabChessBoardOptions extends Tab {

	private FXComradesGUI comradesGUI;

	public FXTabChessBoardOptions(FXComradesGUI comradesGUI) {

		this.comradesGUI = comradesGUI;

		FXChessBoard chessBoard = comradesGUI.getChessBoard();

		BorderPane boardPane = new BorderPane();
		this.setContent(boardPane);
		this.setClosable(false);

		GridPane centerGridPane = new GridPane();
		centerGridPane.setAlignment(Pos.TOP_LEFT);
		centerGridPane.setHgap(8);
		centerGridPane.setVgap(8);
		centerGridPane.setPadding(new Insets(8, 8, 8, 8));

		boardPane.setCenter(centerGridPane);

		ColorPicker oddTileColorPicker = new ColorPicker();
		oddTileColorPicker.setValue(chessBoard.getOddTileColor());
		oddTileColorPicker.valueProperty().addListener(((observable, oldValue, newValue) -> {
			chessBoard.setOddTileColor(newValue);
		}));

		ColorPicker evenTileColorPicker = new ColorPicker();
		evenTileColorPicker.setValue(chessBoard.getEvenTileColor());
		evenTileColorPicker.valueProperty().addListener(((observable, oldValue, newValue) -> {
			chessBoard.setEvenTileColor(newValue);
		}));

		Label fontNameField = new Label();
		fontNameField.setText(chessBoard.getBoardFont().getName());

		Button fontChooseButton = new Button("Browse...");

		fontChooseButton.setOnAction((action) -> {

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select engine executable...");
			File file = fileChooser.showOpenDialog(null);

			if(file != null) {
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					Font chooseFont = Font.loadFont(fileInputStream, chessBoard.getCheckerSize() - 4);
					chessBoard.setBoardFont(chooseFont);
					fontNameField.setText(chooseFont.getName());
				}
				catch(FileNotFoundException e) {
					//e.printStackTrace();
				}
			}
		});

		CheckBox fillPiecesCheckBox = new CheckBox("Fill Pieces");
		fillPiecesCheckBox.setSelected(chessBoard.getFillPieces());
		fillPiecesCheckBox.selectedProperty().addListener(((observable, oldValue, newValue) -> chessBoard.setFillPieces(newValue)));

		CheckBox framePiecesCheckBox = new CheckBox("Frame Pieces");
		framePiecesCheckBox.setSelected(chessBoard.getFramePieces());
		framePiecesCheckBox.selectedProperty().addListener(((observable, oldValue, newValue) -> chessBoard.setFramePieces(newValue)));

		Label oddTileLabel = new Label("Odd Tile Color:");
		oddTileLabel.setGraphic(oddTileColorPicker);
		oddTileLabel.setContentDisplay(ContentDisplay.RIGHT);

		centerGridPane.add(oddTileLabel, 0, 0);

		Label evenTileLabel = new Label("Even Tile Color:");
		evenTileLabel.setGraphic(evenTileColorPicker);
		evenTileLabel.setContentDisplay(ContentDisplay.RIGHT);

		centerGridPane.add(evenTileLabel, 0, 1);

		fontNameField.setGraphic(fontChooseButton);
		fontNameField.setContentDisplay(ContentDisplay.RIGHT);
		fontNameField.setFont(Font.font("Arial", FontPosture.ITALIC, 12));
		Label fontLabel = new Label("Board Font:");
		fontLabel.setGraphic(fontNameField);
		fontLabel.setContentDisplay(ContentDisplay.RIGHT);

		centerGridPane.add(fontLabel, 0, 2);

		centerGridPane.add(fillPiecesCheckBox, 0, 3);
		centerGridPane.add(framePiecesCheckBox, 0, 4);
	}
}
