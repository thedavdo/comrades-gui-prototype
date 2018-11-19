package edu.purdue.comradesgui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.effect.Light;

public class ChessGame {

	private boolean gamePaused;

	private Player whitePlayer, blackPlayer;

	private Character[][] arrayFEN;
	private ObservableList<Character> deadWhite, deadBlack;

	public ChessGame() {

		setBoardFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");

		deadBlack = FXCollections.observableArrayList();
		deadWhite = FXCollections.observableArrayList();

		gamePaused = false;
	}

	public void setWhitePlayer(Player ply) {
		this.whitePlayer = ply;
		ply.setGame(this);
	}

	public void setBlackPlayer(Player ply) {
		this.blackPlayer = ply;
		ply.setGame(this);
	}

	public Player getWhitePlayer() {
		return whitePlayer;
	}

	public Player getBlackPlayer() {
		return blackPlayer;
	}

	public boolean addPiece(Character piece, int col, int row) {

		if(arrayFEN != null) {
			if(Character.isAlphabetic(piece)) {
				arrayFEN[col][row] = piece;
			}
		}

		return false;
	}

	private int[][] parseMove(String move) {

		int[][] moveArray = new int[2][2];

		String pos1 = move.substring(0, 2);
		String pos2 = move.substring(2);

		moveArray[0][0] = Integer.parseInt("" + pos1.charAt(0), 16) - 10;
		moveArray[0][1] = Integer.parseInt("" + pos1.charAt(1)) - 1;

		moveArray[1][0] = Integer.parseInt("" + pos2.charAt(0), 16) - 10;
		moveArray[1][1] = Integer.parseInt("" + pos2.charAt(1)) - 1;

		return moveArray;
	}

	public boolean requestMove(String move) {

		if(isMoveLegal(move)) {
			int[][] moveArray = parseMove(move);
			if(move.length() > 3) {

				Character piece = arrayFEN[moveArray[0][0]][moveArray[0][1]];
				if(piece != null) {
					arrayFEN[moveArray[0][0]][moveArray[0][1]] = null;
					arrayFEN[moveArray[1][0]][moveArray[1][1]] = piece;
				}
			}
		}
		else {
			System.out.println("not valid move");
		}

		return false;
	}

	public boolean isMoveLegal(String move) {

		int[][] moveArray = parseMove(move);

		int fromX = moveArray[0][0];
		int fromY = moveArray[0][1];

		int toX = moveArray[1][0];
		int toY = moveArray[1][1];

		Character curPiece = arrayFEN[fromX][fromY];
		Character toPiece = arrayFEN[toX][toY];


		return true;
	}

	private int[][] getPieceMovements(Character piece) {

		int[][] moves = null;

		if(Character.toLowerCase(piece) == 'k') {
			moves = new int[4][4];
		}

		return moves;
	}

	public boolean isMatchingTeam(Character piece1, Character piece2) {
		return Character.isUpperCase(piece1) == Character.isUpperCase(piece2);
	}

	public Character[][] getArrayFEN() {
		return arrayFEN;
	}

	public void setBoardFEN(String strFEN) {

		Character[][] parsed = new Character[8][8];

		String[] split = strFEN.split("/");

		int rowIndex = 7;
		for(String row : split) {

			char[] unparsed = row.toCharArray();

			int colIndex = 7;
			for(Character in : unparsed) {
				if(Character.isAlphabetic(in)) {
					parsed[colIndex][rowIndex] = in;
					colIndex--;
				}
				else if(Character.isDigit(in)) {

					int numEmpty = Integer.parseInt(""+in);

					for(int i = 0; i < numEmpty; i++)
						colIndex--;
				}
			}
			rowIndex--;
		}

		arrayFEN = parsed;
	}

	public String getStringFEN() {

		String boardFEN = null;

		if(arrayFEN != null) {
			boardFEN = "";

			for(int row = 7; row >= 0; row--) {

				int emptyCount = 0;
				for(int col = 7; col >= 0; col--) {

					Character loc = arrayFEN[col][row];

					if(loc != null) {
						if(emptyCount > 0) {
							boardFEN += emptyCount;
							emptyCount = 0;
						}
						boardFEN += loc;
					}
					else {
						emptyCount++;
					}
				}

				if(emptyCount > 0)
					boardFEN += emptyCount;

				if(row != 0)
					boardFEN += "/";
			}
		}
		return boardFEN;
	}
}
