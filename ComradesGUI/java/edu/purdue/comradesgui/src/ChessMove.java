package edu.purdue.comradesgui.src;

public class ChessMove {

	private String rawMove;

	private ChessGame chessGame;

	private ChessPlayer player;

	private ChessCell fromCell;
	private ChessCell toCell;

	private String leftover;

	/**
	 * Parses a move String to be easier to get the game data
	 * @param rawMove move string
	 * @param chessGame game that the move is being played on
	 */
	public ChessMove(String rawMove, ChessPlayer player, ChessGame chessGame) {

		this.chessGame = chessGame;
		this.player = player;
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

	/**
	 * Convert the letter coordinate to a number so we can reference the col in the game code.
	 * @param c Letter between [a, h]
	 * @return Matching column number
	 */
	private int getNumFromLetter(Character c) {

		return Character.toLowerCase(c) - 97;
	}

	/**
	 * Checks to make sure there is a not null (but not verified legal) move stored.
	 */
	public boolean isMoveFound() {

		boolean hasMove = true;

		if(rawMove == null)
			hasMove = false;
		else if(rawMove.isEmpty())
			hasMove = false;
		else if(rawMove.equalsIgnoreCase("(none)"))
			hasMove = false;
		else if(rawMove.equalsIgnoreCase("0000"))
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
