package edu.purdue.comradesgui.src;

public class ChessMove {

	/**
	 * Method to verify provided move from string is a legal move.
	 * @param move  ChessMove object
	 * @return true if legal
	 */
	public static boolean isLegalMove(ChessMove move) {

		boolean legal = true;

		if(!move.isMoveFound())
			legal = false;

		return legal;
	}

	/**
	 * Method to verify provided move from string is a legal move.
	 * @param rawMove
	 * @return true if legal
	 */
	public static boolean isLegalMove(String rawMove) {
		return isLegalMove(new ChessMove(rawMove, (ChessGame) null));
	}

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
	public ChessMove(String rawMove, ChessGame chessGame) {

		this.chessGame = chessGame;
		this.rawMove = rawMove;

		if(isMoveFound()) {
			String pos1 = rawMove.substring(0, 2);
			String pos2 = rawMove.substring(2, 4);

			leftover = rawMove.substring(4);

			int fromCol = getNumFromLetter(pos1.charAt(0));
			int fromRow = Integer.parseInt("" + pos1.charAt(1)) - 1;

			int toCol = getNumFromLetter(pos2.charAt(0));
			int toRow = Integer.parseInt("" + pos2.charAt(1)) - 1;

			if(chessGame != null) {
				fromCell = chessGame.getCells()[fromCol][fromRow];
				toCell = chessGame.getCells()[toCol][toRow];
			}
		}
	}

	/**
	 * Parses a move String to be easier to get the game data
	 * @param rawMove move string
	 * @param player the player making the move
	 */
	public ChessMove(String rawMove, ChessPlayer player) {
		this(rawMove, player.getGame());
		this.player = player;
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

	/**
	 * Method to process the ChessMove, usually called from a ChessPlayer's ChessMoveListener.
	 * @return true if successfully processed move
	 */
	public boolean performMove() {

		if(isLegalMove(this)) {

			ChessCell fromCell = getFromCell();
			if(fromCell != null) {

				ChessPiece fromPiece = fromCell.getChessPiece();
				if(fromPiece != null) {

					ChessCell toCell = getToCell();
					if(toCell != null) {

						ChessPiece toPiece = toCell.getChessPiece();

						if(toPiece != null)
							if(toPiece.isWhiteTeam() != fromPiece.isWhiteTeam())
								chessGame.setPieceAsDead(toPiece);

						if(!getLeftover().isEmpty()) {
							if(getLeftover().equals("q"))
								fromPiece.setPieceType('q', true);
						}

						toCell.setChessPiece(fromPiece);
						fromPiece.incrementMoveCount();
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * If the move was made by a player, this will be them
	 */
	public ChessPlayer getPlayer() {
		return player;
	}

	/**
	 * Sets the player that is making the move
	 * @param player
	 */
	public void setPlayer(ChessPlayer player) {
		this.player = player;
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
