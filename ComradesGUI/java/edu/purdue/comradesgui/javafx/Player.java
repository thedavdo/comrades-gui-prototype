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

	private String playerName;

	public Player(PlayerType playerType) {
		this.playerType = playerType;
		this.moveListeners = new ArrayList<>();
		readyForGame = new SimpleBooleanProperty();
		playerName = "unset playername";
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}

	public boolean isWhitePlayer() {
		return (chessGame.getWhitePlayer() == this);
	}

	public boolean isBlackPlayer() {
		return (chessGame.getBlackPlayer() == this);
	}

	public MoveTimer getMoveTimer() {

		if(isWhitePlayer())
			return chessGame.getWhiteTimer();
		else if(isBlackPlayer())
			return chessGame.getBlackTimer();

		return null;
	}

	public MoveTimer getOpponentMoveTimer() {

		if(isBlackPlayer())
			return chessGame.getWhiteTimer();
		else if(isWhitePlayer())
			return chessGame.getBlackTimer();

		return null;
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


	public String toString() {
		return getPlayerName();
	}

	/**
	 * Tells the player it is time to generate a move
	 */
	public abstract void requestToMakeMove();

	public abstract void prepareForGame();
}
