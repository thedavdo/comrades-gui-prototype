package edu.purdue.comradesgui.src;

public class ChessCell {

	private int colPos;
	private int rowPos;

	private ChessPiece myPiece;

	public ChessCell(int col, int row) {
		this.colPos = col;
		this.rowPos = row;
	}

	public int getColPos() {
		return colPos;
	}

	public int getRowPos() {
		return rowPos;
	}

	public ChessPiece getChessPiece() {
		return myPiece;
	}

	public void setChessPiece(ChessPiece piece) {

		this.myPiece = piece;
		if(this.myPiece != null) {

			if(myPiece.getCell() != null) {
				if (myPiece.getCell() != this)
					myPiece.getCell().setChessPiece(null);
			}

			this.myPiece.setCell(this);
		}
	}

	public String toString() {

		return "Cell["+ colPos + "," + rowPos+ "] " + myPiece;
	}
}
