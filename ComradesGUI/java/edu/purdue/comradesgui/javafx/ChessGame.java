package edu.purdue.comradesgui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChessGame {

	private boolean gamePaused;

	private Player whitePlayer, blackPlayer;

	private ChessCell[][] chessCells;
	private ObservableList<ChessPiece> deadWhite, deadBlack;

	public ChessGame() {

		setBoardFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");

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

	public void startGame() {

	}

	public boolean addPiece(Character piece, int col, int row) {

		if(chessCells != null) {
			if(Character.isAlphabetic(piece)) {
				chessCells[col][row].setChessPiece(new ChessPiece(piece));
			}
		}

		return false;
	}

	public boolean makeMove(String move) {

		if(isMoveLegal(move)) {
			ChessMove moveParsed = new ChessMove(move, this);
			if(move.length() > 3) {

				ChessCell fromCell = moveParsed.getFromCell();
				ChessCell toCell = moveParsed.getToCell();
				ChessPiece piece = fromCell.getChessPiece();
				if(piece != null) {
					fromCell.setChessPiece(null);
					toCell.setChessPiece(piece);
				}
			}
		}
		else {
			System.out.println("not valid move");
		}

		return false;
	}

	public boolean isMoveLegal(String move) {

		ChessMove moveParsed = new ChessMove(move, this);


		return true;
	}

	public ChessCell[][] getCells() {
		return chessCells;
	}

	public void setBoardFromFEN(String strFEN) {

		ChessCell[][] parsed = new ChessCell[8][8];

		String[] split = strFEN.split("/");

		int rowIndex = 7;
		for(String row : split) {

			char[] unparsed = row.toCharArray();

			int colIndex = 7;
			for(Character in : unparsed) {
				if(Character.isAlphabetic(in)) {
					parsed[colIndex][rowIndex] = new ChessCell(colIndex, rowIndex);
					parsed[colIndex][rowIndex].setChessPiece(new ChessPiece(in));
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

		chessCells = parsed;
	}

	public String getStringFEN() {

		String boardFEN = null;

		if(chessCells != null) {
			boardFEN = "";

			for(int row = 7; row >= 0; row--) {

				int emptyCount = 0;
				for(int col = 7; col >= 0; col--) {

					Character loc = chessCells[col][row].getChessPiece().getPieceChar();

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
