package edu.purdue.comradesgui.javafx;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {

	public enum PlayerType {
		ENGINE,
		HUMAN,
		OTHER
	}

	protected ChessGame chessGame;

	private List<MoveListener> moveListeners;

	private PlayerType playerType;

	public Player(PlayerType playerType) {
		this.playerType = playerType;
		this.moveListeners = new ArrayList<>();
	}

	public void addMoveListener(MoveListener moveListener) {

		this.moveListeners.add(moveListener);
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
	 * Processes the move for all the MoveListeners.
	 * @param move Move for the player to make
	 */
	public void makeMove(ChessMove move) {

		if(move != null) {
			for(MoveListener ml : moveListeners) {
				if(ml.moveEvent(this, move))
					break;
			}
		}
	}

	/**
	 * Tells the player it is time to generate a move
	 */
	public abstract void requestToMakeMove();
}
