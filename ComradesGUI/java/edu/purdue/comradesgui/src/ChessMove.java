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

	public boolean isCaptureMove() {

		if(toCell != null && fromCell != null) {
			ChessPiece toPiece = toCell.getChessPiece();
			ChessPiece fromPiece = fromCell.getChessPiece();
			if(toPiece != null && fromPiece != null)
				return (toPiece.isWhiteTeam() != fromPiece.isWhiteTeam());
		}

		return false;
	}

	public boolean isCastlingMove() {

		if(isCaptureMove())
			return false;

		if(toCell != null && fromCell != null) {

			ChessPiece fromPiece = fromCell.getChessPiece();
			if(fromPiece != null) {

				int selRow = -1;

				if(fromPiece.isWhiteTeam())
					selRow = 0;
				else if(fromPiece.isBlackTeam())
					selRow = 7;

				boolean fromCellVerify = (fromCell.getColPos() == 4 && fromCell.getRowPos() == selRow);
				boolean toCellVerify = (toCell.getColPos() == 2 || toCell.getColPos() == 6) && (toCell.getRowPos() == selRow);

				return fromCellVerify && toCellVerify;
			}
		}

		return false;
	}

	public boolean isPromotionMove() {

		if(!getLeftover().isEmpty())
			return getLeftover().equals("q");

		return false;
	}

	/**
	 * Method to verify provided move from string is a legal move.
	 * @return true if legal
	 */
	public boolean isLegalMove() {

		boolean legal = true;

		if(!isMoveFound())
			legal = false;
		else if(isCastlingMove()) {

			ChessPiece fromPiece = fromCell.getChessPiece();
			if(fromPiece.isWhiteTeam()) {

				if(toCell.getColPos() == 6) {
					if(!chessGame.canCastleWhiteKingSide())
						legal = false;
				}
				else if(toCell.getColPos() == 2) {
					if(!chessGame.canCastleWhiteQueenSide())
						legal = false;
				}
			}
			else if(fromPiece.isBlackTeam()) {

				if(toCell.getColPos() == 6) {
					if(!chessGame.canCastleBlackKingSide())
						legal = false;
				}
				else if(toCell.getColPos() == 2) {
					if(!chessGame.canCastleBlackQueenSide())
						legal = false;
				}
			}
		}

		return legal;
	}
}
