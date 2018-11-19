package edu.purdue.comradesgui.javafx;

public abstract class Player {

	public enum PlayerType {
		ENGINE,
		HUMAN,
		OTHER
	}

	protected ChessGame chessGame;

	private PlayerType playerType;

	public Player(PlayerType playerType) {
		this.playerType = playerType;
	}

	public PlayerType getPlayerType() {
		return playerType;
	}

	public void setGame(ChessGame chessGame) {
		this.chessGame = chessGame;
	}

	public ChessGame getGame() {
		return chessGame;
	}


	/**
	 * Tells the player it is time to make a move
	 */
	public abstract void requestMove();
}
