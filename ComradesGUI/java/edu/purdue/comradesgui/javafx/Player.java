package edu.purdue.comradesgui.javafx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

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

	private BooleanProperty readyForGame;

	public Player(PlayerType playerType) {
		this.playerType = playerType;
		this.moveListeners = new ArrayList<>();
		readyForGame = new SimpleBooleanProperty();
	}

	public void addMoveListener(MoveListener moveListener) {

		this.moveListeners.add(moveListener);
	}

	public BooleanProperty getReadyForGameProperty() {
		return readyForGame;
	}

	public boolean isReadyForGame() {
		return readyForGame.getValue();
	}

	public void setReadyForGame(boolean isReady) {
		readyForGame.setValue(isReady);
		chessGame.setPlayerReady(this, isReady);
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
				ml.moveEvent(this, move);
			}
		}
	}

	/**
	 * Tells the player it is time to generate a move
	 */
	public abstract void requestToMakeMove();
}
