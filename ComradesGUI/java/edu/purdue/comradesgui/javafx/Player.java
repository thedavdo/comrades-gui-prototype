package edu.purdue.comradesgui.javafx;

public abstract class Player {

	public enum PlayerType {
		ENGINE,
		HUMAN,
		OTHER
	}

	private String boardFEN;
	private PlayerType playerType;

	public Player(PlayerType playerType) {

		this.playerType = playerType;
	}

	public void setBoardFEN(String boardFEN) {
		this.boardFEN = boardFEN;
	}

	public String getBoardFEN() {
		return boardFEN;
	}

	/**
	 * Tells the player it is time to make a move
	 */
	public abstract void requestMove();
}
