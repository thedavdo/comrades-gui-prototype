package edu.purdue.comradesgui.javafx;

public class ChessMove {

	private String rawMove;

	private ChessGame chessGame;

	private ChessCell fromCell;
	private ChessCell toCell;

	public ChessMove(String rawMove, ChessGame chessGame) {

		this.chessGame = chessGame;
		this.rawMove = rawMove;

		String pos1 = rawMove.substring(0, 2);
		String pos2 = rawMove.substring(2);

		int fromCol = Integer.parseInt("" + pos1.charAt(0), 16) - 10;
		int fromRow = Integer.parseInt("" + pos1.charAt(1)) - 1;

		fromCell = chessGame.getCells()[fromCol][fromRow];

		int toCol = Integer.parseInt("" + pos2.charAt(0), 16) - 10;
		int toRow = Integer.parseInt("" + pos2.charAt(1)) - 1;

		toCell = chessGame.getCells()[toCol][toRow];
	}

	public String getRawMove() {
		return rawMove;
	}

	public ChessCell getFromCell() {
		return fromCell;
	}

	public ChessCell getToCell() {
		return toCell;
	}
}
