package edu.purdue.comradesgui.javafx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChessGame {

	private BooleanProperty gamePaused;

	private Player whitePlayer, blackPlayer;
	private BooleanProperty whiteTurn, blackTurn;

	private MoveTimer whiteTimer, blackTimer;
	private BooleanProperty useTimers;

	private ChessCell[][] chessCells;
	private ObservableList<ChessPiece> deadWhite, deadBlack;

	private MoveListener moveListener;

	public ChessGame() {

		setBoardFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");

		whiteTimer = new MoveTimer();
		blackTimer = new MoveTimer();

		gamePaused = new SimpleBooleanProperty();

		whiteTurn = new SimpleBooleanProperty();
		blackTurn = new SimpleBooleanProperty();

		useTimers = new SimpleBooleanProperty();

		deadBlack = FXCollections.observableArrayList();
		deadWhite = FXCollections.observableArrayList();

		gamePaused.setValue(true);
		useTimers.setValue(true);

		whiteTurn.setValue(false);
		blackTurn.setValue(false);

		moveListener = (player, move) -> {

			if(player == getCurrentTurnsPlayer()) {
				ChessGame.this.makeMove(move);
				cycleTurns();
			}

			return false;
		};
	}

	public void setUseTimers(boolean useTimers) {
		this.useTimers.setValue(useTimers);
	}

	public MoveTimer getWhiteTimer() {
		return whiteTimer;
	}

	public MoveTimer getBlackTimer() {
		return blackTimer;
	}

	public void setWhitePlayer(Player ply) {
		this.whitePlayer = ply;
		initPlayer(ply);
	}

	public void setBlackPlayer(Player ply) {
		this.blackPlayer = ply;
		initPlayer(ply);
	}

	private void initPlayer(Player ply) {
		ply.setGame(this);
		ply.addMoveListener(this.moveListener);
	}

	public Player getWhitePlayer() {
		return whitePlayer;
	}

	public Player getBlackPlayer() {
		return blackPlayer;
	}

	public Player getCurrentTurnsPlayer() {

		if(whiteTurn.getValue())
			return whitePlayer;
		else if(blackTurn.getValue())
			return blackPlayer;
		else
			return null;
	}

	public void startGame() {
		gamePaused.setValue(false);
		cycleTurns();
	}

	public void setGamePaused(boolean paused) {

		this.gamePaused.setValue(paused);

		if(useTimers.getValue() && gamePaused.getValue()) {
			whiteTimer.pause();
			blackTimer.pause();
		}
	}

	public void cycleTurns() {

		if(!gamePaused.getValue()) {
			if(whiteTurn.getValue()) {
				whiteTurn.setValue(false);
				blackTurn.setValue(true);
			}
			else if(blackTurn.getValue()) {
				whiteTurn.setValue(true);
				blackTurn.setValue(false);
			}
			else {
				whiteTurn.setValue(true);
				blackTurn.setValue(false);

				if(useTimers.getValue()) {
					whiteTimer.initialize();
					blackTimer.initialize();
				}
			}

			if(whiteTurn.getValue())
				whitePlayer.requestToMakeMove();
			else if(blackTurn.getValue())
				blackPlayer.requestToMakeMove();

			if(useTimers.getValue()) {
				if(whiteTurn.getValue()) {
					whiteTimer.resume();
					blackTimer.pause();
				}
				else {
					whiteTimer.pause();
					blackTimer.resume();
				}
			}
		}
	}

	public BooleanProperty isGamePaused() {
		return gamePaused;
	}

	public boolean addPiece(Character piece, int col, int row) {

		if(chessCells != null) {
			if(Character.isAlphabetic(piece)) {
				chessCells[col][row].setChessPiece(new ChessPiece(piece));
			}
		}

		return false;
	}

	public boolean makeMove(ChessMove move) {

		if(isMoveLegal(move)) {
			if(move.getRawMove().length() > 3) {

				ChessCell fromCell = move.getFromCell();
				ChessCell toCell = move.getToCell();
				ChessPiece piece = fromCell.getChessPiece();
				if(piece != null) {
					fromCell.setChessPiece(null);
					toCell.setChessPiece(piece);
				}
			}
		}
		else {
			System.out.println("not valid move");
		}

		return false;
	}

	public boolean makeMove(String move) {

		return makeMove(new ChessMove(move, this));
	}

	public boolean isMoveLegal(String move) {

		return isMoveLegal(new ChessMove(move, this));
	}

	public boolean isMoveLegal(ChessMove move) {

		//ChessMove moveParsed = new ChessMove(move, this);


		return true;
	}

	public ChessCell[][] getCells() {
		return chessCells;
	}

	public void setBoardFromFEN(String strFEN) {

		ChessCell[][] parsed = new ChessCell[8][8];

		String[] split = strFEN.split("/");

		int rowIndex = 7;
		for(String row : split) {

			char[] unparsed = row.toCharArray();

			int colIndex = 7;
			for(Character in : unparsed) {
				if(Character.isAlphabetic(in)) {
					parsed[colIndex][rowIndex] = new ChessCell(colIndex, rowIndex);
					parsed[colIndex][rowIndex].setChessPiece(new ChessPiece(in));
					colIndex--;
				}
				else if(Character.isDigit(in)) {

					int numEmpty = Integer.parseInt(""+in);

					for(int i = 0; i < numEmpty; i++)
						colIndex--;
				}
			}
			rowIndex--;
		}

		chessCells = parsed;
	}

	public String getStringFEN() {

		String boardFEN = null;

		if(chessCells != null) {
			boardFEN = "";

			for(int row = 7; row >= 0; row--) {

				int emptyCount = 0;
				for(int col = 7; col >= 0; col--) {

					Character loc = chessCells[col][row].getChessPiece().getPieceChar();

					if(loc != null) {
						if(emptyCount > 0) {
							boardFEN += emptyCount;
							emptyCount = 0;
						}
						boardFEN += loc;
					}
					else {
						emptyCount++;
					}
				}

				if(emptyCount > 0)
					boardFEN += emptyCount;

				if(row != 0)
					boardFEN += "/";
			}
		}
		return boardFEN;
	}
}
