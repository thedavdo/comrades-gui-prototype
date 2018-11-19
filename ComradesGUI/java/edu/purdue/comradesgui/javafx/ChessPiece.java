package edu.purdue.comradesgui.javafx;

public class ChessPiece {

	private Character pieceChar;

	private ChessCell currentCell;

	private int moveCount;

	private boolean isWhiteTeam;
	private boolean isBlackTeam;

	public ChessPiece(Character pieceChar) {

		this.setPieceType(pieceChar, false);
		moveCount = 0;
	}

	/**
	 * Returns the letter corresponding to the piece type. Case will reflect team.
	 * @return Character (e.g. r, K, N, q c...)
	 */
	public Character getPieceChar() {

		return pieceChar;
	}

	public boolean isWhiteTeam() {
		return isWhiteTeam;
	}

	public boolean isBlackTeam() {
		return isBlackTeam;
	}

	/**
	 * Will change piece to given piece without affecting team.
	 * @param inputType Char representing the new piece type
	 * @param ignoreTeam Set true to let current team persist
	 */
	public void setPieceType(Character inputType, boolean ignoreTeam) {

		if(ignoreTeam) {
			if(isWhiteTeam)
				this.pieceChar = Character.toUpperCase(inputType);
			else
				this.pieceChar = Character.toLowerCase(inputType);
		}
		else {
			this.pieceChar = inputType;
			this.setTeam(inputType);
		}
	}


	/**
	 * Change team of current piece to match provided character
	 * @param inTeam
	 */
	public void setTeam(Character inTeam) {
		isWhiteTeam = Character.isUpperCase(inTeam);
		isBlackTeam = !isWhiteTeam;

		if(isWhiteTeam)
			this.pieceChar = Character.toUpperCase(pieceChar);
		else
			this.pieceChar = Character.toLowerCase(pieceChar);
	}



	/**
	 * Only use ChessCell.setPiece, you can try this but it won't work for you!
	 * @param cell Cell that has this piece already
	 */
	public void setCell(ChessCell cell) {

		if(cell.getChessPiece() == this)
			this.currentCell = cell;
	}

	public ChessCell getCell() {
		return currentCell;
	}

	/**
	 * How many times this piece has been moved in the game
	 * @return count
	 */
	public int getMoveCount() {
		return moveCount;
	}
}
