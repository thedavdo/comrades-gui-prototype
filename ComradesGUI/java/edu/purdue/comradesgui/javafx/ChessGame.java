package edu.purdue.comradesgui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChessGame {

	private String boardFEN;

	private boolean gamePaused;

	private Player whitePlayer, blackPlayer;

	private ObservableList<Character> deadWhite, deadBlack;

	public ChessGame() {
		boardFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

		deadBlack = FXCollections.observableArrayList();
		deadWhite = FXCollections.observableArrayList();

		gamePaused = false;
	}

	public boolean addPiece() {


		return false;
	}

	public void setBoardFEN(String newFEN) {
		boardFEN = newFEN;
	}

	public String getStringFEN() {
		return boardFEN;
	}

	public char[][] getParsedFEN() {

		char[][] parsed = new char[8][8];

		String[] split = boardFEN.split("/");

		int rowIndex = 0;
		for(String row : split) {

			char[] unparsed = row.toCharArray();

			int colIndex = 0;
			for(char in : unparsed) {

				if(Character.isAlphabetic(in)) {
					parsed[colIndex][rowIndex] = in;
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

		return parsed;
	}
}
