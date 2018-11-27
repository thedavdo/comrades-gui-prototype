package edu.purdue.comradesgui.src;

public class ChessMove {

	private String rawMove;

	private ChessGame chessGame;

	private ChessCell fromCell;
	private ChessCell toCell;

	private String leftover;

	public ChessMove(String rawMove, ChessGame chessGame) {

		this.chessGame = chessGame;
		this.rawMove = rawMove;

		if(isMoveFound()) {

			String pos1 = rawMove.substring(0, 2);
			String pos2 = rawMove.substring(2, 4);

			leftover = rawMove.substring(4);

			int fromCol = getNumFromLetter(pos1.charAt(0));
			int fromRow = Integer.parseInt("" + pos1.charAt(1)) - 1;

			fromCell = chessGame.getCells()[fromCol][fromRow];

			int toCol = getNumFromLetter(pos2.charAt(0));
			int toRow = Integer.parseInt("" + pos2.charAt(1)) - 1;

			toCell = chessGame.getCells()[toCol][toRow];
		}
	}

	private int getNumFromLetter(Character c) {

		int num = (int) Character.toLowerCase(c) - 97;

		return num;
	}

	public boolean isMoveFound() {

		boolean hasMove = true;

		if(rawMove == null)
			hasMove = false;

		if(rawMove.isEmpty())
			hasMove = false;

		if(rawMove.equalsIgnoreCase("(none)"))
			hasMove = false;

		return hasMove;
	}

	public String getRawMove() {
		return rawMove;
	}

	public String getLeftover() {
		return leftover;
	}

	public ChessCell getFromCell() {
		return fromCell;
	}

	public ChessCell getToCell() {
		return toCell;
	}
}