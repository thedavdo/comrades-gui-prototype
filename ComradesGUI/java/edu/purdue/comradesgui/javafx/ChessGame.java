package edu.purdue.comradesgui.javafx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChessGame {

	private ChessCell[][] chessCells;
	private ObservableList<ChessPiece> deadWhite, deadBlack;

	private Player whitePlayer, blackPlayer;

	private MoveTimer whiteTimer, blackTimer;

	private BooleanProperty whiteReadyToStart, blackReadyToStart;
	private BooleanProperty gameStarted;
	private BooleanProperty gamePaused;
	private BooleanProperty useTimers;

	private BooleanProperty whiteTurn, blackTurn;

	private MoveListener moveListener;
	private CommandResponseListener engineInitListener;

	public ChessGame() {

		setBoardFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
		//setBoardFromFEN("N7/P3pk1p/3p2p1/r4p2/8/4b2B/4P1KP/R7");

		System.out.println(generateStringFEN());

		whiteTimer = new MoveTimer();
		blackTimer = new MoveTimer();

		gameStarted = new SimpleBooleanProperty();
		gamePaused = new SimpleBooleanProperty();

		whiteReadyToStart = new SimpleBooleanProperty();
		blackReadyToStart = new SimpleBooleanProperty();

		whiteTurn = new SimpleBooleanProperty();
		blackTurn = new SimpleBooleanProperty();

		useTimers = new SimpleBooleanProperty();

		deadBlack = FXCollections.observableArrayList();
		deadWhite = FXCollections.observableArrayList();

		gameStarted.setValue(false);
		gamePaused.setValue(true);
		useTimers.setValue(true);

		whiteReadyToStart.setValue(false);
		blackReadyToStart.setValue(false);

		whiteTurn.setValue(false);
		blackTurn.setValue(false);

		moveListener = (player, move) -> {

			if(player == getCurrentTurnsPlayer()) {
				ChessGame.this.makeMove(move);
				cycleTurns();
			}
		};

		engineInitListener = ((cmdTokens, cmd, engine) -> {
			//Useful for something?
		});
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

		if(ply instanceof ChessEngine) {
			ChessEngine plyEngine = (ChessEngine) ply;
			plyEngine.addResponseListener(engineInitListener);
		}
		ply.addMoveListener(this.moveListener);
		ply.setGame(this);
	}

	public void setPlayerReady(Player ply, boolean isReady) {

		if(whitePlayer == ply)
			whiteReadyToStart.setValue(isReady);
		else if(blackPlayer == ply)
			blackReadyToStart.setValue(isReady);

		if(isGameStarted()) {
			if(this.isReadyToStart()) {
				gamePaused.setValue(false);
				cycleTurns();
			}
		}
	}

	public boolean isReadyToStart() {
		return whitePlayer.isReadyForGame() && blackPlayer.isReadyForGame();
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
		System.out.println("Starting Game...");
		this.whitePlayer.prepareForGame();
		this.blackPlayer.prepareForGame();
		gameStarted.setValue(true);
	}

	public boolean isGameStarted() {
		return gameStarted.getValue();
	}

	public BooleanProperty getGameStartedBooleanProperty() {
		return gameStarted;
	}

	public boolean isGamePaused() {
		return gamePaused.getValue();
	}

	public void setGamePaused(boolean paused) {

		this.gamePaused.setValue(paused);

		if(useTimers.getValue()) {

			if(isGamePaused()) {
				whiteTimer.pause();
				blackTimer.pause();
			}
			else {
				if(whiteTurn.getValue()) {
					whiteTimer.resume();
					blackTimer.pause();
				}
				else if(blackTurn.getValue()) {
					whiteTimer.pause();
					blackTimer.resume();
				}
			}
		}
	}

	public BooleanProperty getGamePausedBooleanProperty() {
		return gamePaused;
	}

	public void cycleTurns() {

		if(!isGamePaused() && isGameStarted()) {
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

	public boolean addPiece(Character piece, int col, int row) {

		if(chessCells != null) {
			if(Character.isAlphabetic(piece)) {
				chessCells[col][row].setChessPiece(new ChessPiece(piece));
			}
		}

		return false;
	}

	public boolean makeMove(ChessMove move) {

		ChessCell fromCell = move.getFromCell();

		if(fromCell != null) {

			ChessPiece piece = fromCell.getChessPiece();
			ChessCell toCell = move.getToCell();

			if(piece != null) {
				if(toCell != null) {
					fromCell.setChessPiece(null);
					toCell.setChessPiece(piece);
					return true;
				}
			}
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

		//N7/P3pk1p/3p2p1/r4p2/8/4b2B/4P1KP/R7
		ChessCell[][] parsed = new ChessCell[8][8];

		String[] splitFEN = strFEN.split("/");

		for(int row = 0; row < 8; row++) {
			for(int col = 0; col < 8; col++) {
				parsed[col][row] = new ChessCell(col, row);
			}
		}

		for(int row = 7; row >= 0; row--) {

			String rowFEN = splitFEN[7-row];

			char[] rowCharArray = rowFEN.toCharArray();

			int colIndex = 7;

			for(Character pieceChar : rowCharArray) {
				if(Character.isDigit(pieceChar))
					colIndex -= Character.digit(pieceChar, 10);
				else {
					parsed[7-colIndex][row].setChessPiece(new ChessPiece(pieceChar));
					colIndex--;
				}
			}
		}

		chessCells = parsed;
	}

	public String generateStringFEN() {

		String boardFEN = null;

		if(chessCells != null) {
			boardFEN = "";

			for(int row = 7; row >= 0; row--) {

				int emptyCount = 0;

				String rowBuild = "";
				for(int col = 7; col >= 0; col--) {

					boolean isEmpty = true;
					ChessCell cell = chessCells[col][row];

					if(cell != null) {
						ChessPiece chessPiece = cell.getChessPiece();
						if(chessPiece != null) {
							Character pieceChar = chessPiece.getPieceChar();
							if(emptyCount > 0) {
								rowBuild = emptyCount + rowBuild;
								emptyCount = 0;
							}
							rowBuild = pieceChar + rowBuild;
							isEmpty = false;
						}
					}
					if(isEmpty)
						emptyCount++;
				}

				if(emptyCount > 0)
					rowBuild = emptyCount + rowBuild;

				if(row != 7)
					rowBuild = "/" + rowBuild;

				boardFEN = boardFEN + rowBuild;
			}
		}

		return boardFEN;
	}
}
