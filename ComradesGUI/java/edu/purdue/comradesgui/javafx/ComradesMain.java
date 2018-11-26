package edu.purdue.comradesgui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ComradesMain {



	private ChessGame currentGame;

	private ObservableList<ChessPlayer> playerList;

	public ComradesMain() {

		playerList = FXCollections.observableArrayList();
		currentGame = new ChessGame();
	}

	public ObservableList<ChessPlayer> getPlayerList() {
		return playerList;
	}

	public void addPlayer(ChessPlayer ply) {
		playerList.add(ply);
	}

	public ChessGame getCurrentGame() {
		return currentGame;
	}
}
