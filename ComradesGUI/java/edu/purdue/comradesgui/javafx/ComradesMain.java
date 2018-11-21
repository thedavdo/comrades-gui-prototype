package edu.purdue.comradesgui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ComradesMain {



	private ChessGame currentGame;

	private ObservableList<Player> playerList;

	public ComradesMain() {

		playerList = FXCollections.observableArrayList();
		currentGame = new ChessGame();
	}

	public ObservableList<Player> getPlayerList() {
		return playerList;
	}

	public void addPlayer(Player ply) {
		playerList.add(ply);
	}

	public ChessGame getCurrentGame() {
		return currentGame;
	}
}
