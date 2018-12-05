package edu.purdue.comradesgui.src;

public class ChessMove {

	private String inputMoveString;

	private ChessGame chessGame;

	private ChessPlayer player;

	private ChessCell fromCell;
	private ChessCell toCell;

	private ChessPiece movingPiece;

	private Character promotion;

	private int turnNumber;

	private boolean isCapture;
	private boolean isCastling;

	/**
	 * Parses a move String to be easier to get the game data
	 * @param rawMove move string in long algebraic notation
	 * @param chessGame game that the move is being played on
	 */
	public ChessMove(String rawMove, ChessGame chessGame) {

		this.chessGame = chessGame;
		this.inputMoveString = rawMove;

		if(isMoveFound()) {

			turnNumber = chessGame.getTurnCount();

			String pos1 = rawMove.substring(0, 2);
			String pos2 = rawMove.substring(2, 4);

			if(rawMove.length() > 4)
				promotion = rawMove.charAt(4);

			int fromCol = getNumFromLetter(pos1.charAt(0));
			int fromRow = Integer.parseInt("" + pos1.charAt(1)) - 1;

			int toCol = getNumFromLetter(pos2.charAt(0));
			int toRow = Integer.parseInt("" + pos2.charAt(1)) - 1;

			fromCell = chessGame.getCells()[fromCol][fromRow];
			toCell = chessGame.getCells()[toCol][toRow];

			movingPiece = fromCell.getChessPiece();

			if(toCell != null && fromCell != null) {
				ChessPiece toPiece = toCell.getChessPiece();
				ChessPiece fromPiece = fromCell.getChessPiece();
				if(toPiece != null && fromPiece != null)
					isCapture = (toPiece.isWhiteTeam() != fromPiece.isWhiteTeam());
			}

			if(!isCaptureMove()) {

				if (toCell != null && fromCell != null) {

					ChessPiece fromPiece = fromCell.getChessPiece();
					if (fromPiece != null) {

						int selRow = -1;

						if (fromPiece.isWhiteTeam())
							selRow = 0;
						else if (fromPiece.isBlackTeam())
							selRow = 7;

						boolean fromCellVerify = (fromCell.getColPos() == 4 && fromCell.getRowPos() == selRow);
						boolean toCellVerify = (toCell.getColPos() == 2 || toCell.getColPos() == 6) && (toCell.getRowPos() == selRow);

						isCastling = fromCellVerify && toCellVerify;
					}
				}
			}
		}
	}

	/**
	 * Parses a move String to be easier to get the game data
	 * @param rawMove move string in long algebraic notation
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

		if(inputMoveString == null)
			hasMove = false;
		else if(inputMoveString.isEmpty())
			hasMove = false;
		else if(inputMoveString.equalsIgnoreCase("(none)"))
			hasMove = false;
		else if(inputMoveString.equalsIgnoreCase("0000"))
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

	/**
	 * Generates a text representation of the ChessMove in long algebraic notation.
	 * e.g. b7b8k
	 * @return generated string
	 */
	public String getMoveString() {

		String move = "";

		if(fromCell != null)
			move = move + fromCell.getCoordString();

		if(toCell != null)
			move = move + toCell.getCoordString();

		if(promotion != null)
			move = move + promotion;

		if(move.isEmpty())
			return "(none)";
		else
			return move;
	}

	public Character getPromotion() {
		return promotion;
	}

	public ChessCell getFromCell() {
		return fromCell;
	}

	public void setFromCell(ChessCell fromCell) {
		this.fromCell = fromCell;
		this.movingPiece = fromCell.getChessPiece();
	}

	public ChessCell getToCell() {
		return toCell;
	}

	public void setToCell(ChessCell toCell) {
		this.toCell = toCell;
	}

	public boolean isCaptureMove() {

		return isCapture;
	}

	public boolean isCastlingMove() {

		return isCastling;
	}

	public boolean isPromotionMove() {

		return (promotion != null);
	}

	public void setPromotion(Character promoChar) {
		promotion = promoChar;
	}

	public int getTurnNumber() {
		return turnNumber;
	}

	public ChessPiece getMovingPiece() {

		return movingPiece;
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
