package edu.purdue.comradesgui.javafx;

import edu.purdue.comradesgui.src.ChessEngine;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FXTabChessBoardOptions extends Tab {

	private FXChessBoard chessBoard;

	public FXTabChessBoardOptions(FXChessBoard chessBoard) {

		this.chessBoard = chessBoard;

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

		TextField fontNameField = new TextField();
		fontNameField.setText(chessBoard.getBoardFont().getName());
		fontNameField.setDisable(true);

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

		centerGridPane.add(new Label("Odd Tile Color:"), 0, 0);
		centerGridPane.add(oddTileColorPicker, 1, 0);

		centerGridPane.add(new Label("Even Tile Color:"), 0, 1);
		centerGridPane.add(evenTileColorPicker, 1, 1);

		centerGridPane.add(new Label("Board Font:"), 0, 2);
		centerGridPane.add(fontNameField, 1, 2);
		centerGridPane.add(fontChooseButton, 2, 2);

		centerGridPane.add(fillPiecesCheckBox, 0, 3);
		centerGridPane.add(framePiecesCheckBox, 0, 4);
	}
}
