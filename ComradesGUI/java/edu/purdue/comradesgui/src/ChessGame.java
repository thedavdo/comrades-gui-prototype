package edu.purdue.comradesgui.src;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChessGame {

	private ChessCell[][] chessCells;
	private ObservableList<ChessPiece> deadWhite, deadBlack;


	private IntegerProperty turnCount;

	private ChessPlayer whitePlayer, blackPlayer;

	private ChessPlayerTimer whiteTimer, blackTimer;

	private BooleanProperty whiteReadyToStart, blackReadyToStart;
	private BooleanProperty gameStarted;
	//private BooleanProperty gamePaused;

	private BooleanProperty useTimers;

	private BooleanProperty useTimerIncrement;
	private BooleanProperty useTimerBuffer;

	private long timerDuration;
	private long timerDelay;

	private BooleanProperty whiteTurn, blackTurn;

	private ChessMoveListener chessMoveListener;
	private ChessEngineResponseListener engineInitListener;

	public ChessGame() {
		//setBoardFromFEN("8/8/2K5/2N1B3/2k5/8/5Q2/8");
		setBoardFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
		//setBoardFromFEN("8/1p6/8/8/8/8/8/8");
		//setBoardFromFEN("N7/P3pk1p/3p2p1/r4p2/8/4b2B/4P1KP/R7");

		//System.out.println(generateStringFEN());

		whiteTimer = new ChessPlayerTimer();
		blackTimer = new ChessPlayerTimer();

		gameStarted = new SimpleBooleanProperty();

		turnCount = new SimpleIntegerProperty();

		whiteReadyToStart = new SimpleBooleanProperty();
		blackReadyToStart = new SimpleBooleanProperty();

		whiteTurn = new SimpleBooleanProperty();
		blackTurn = new SimpleBooleanProperty();

		useTimers = new SimpleBooleanProperty();

		useTimerIncrement = new SimpleBooleanProperty();
		useTimerBuffer = new SimpleBooleanProperty();

		deadBlack = FXCollections.observableArrayList();
		deadWhite = FXCollections.observableArrayList();

		gameStarted.setValue(false);

		turnCount.set(0);

		useTimers.setValue(false);
		useTimerIncrement.setValue(false);
		useTimerBuffer.setValue(false);

		timerDuration = 300 * 1000;
		timerDelay = 5000;

		whiteReadyToStart.setValue(false);
		blackReadyToStart.setValue(false);

		whiteTurn.setValue(false);
		blackTurn.setValue(false);

		chessMoveListener = (player, move) -> {

			if(player == getCurrentTurnsPlayer()) {
				makeMove(move);
				cycleTurns();
			}
		};

		engineInitListener = ((cmdTokens, cmd, engine) -> {
			//Useful for something?
		});
	}

	public IntegerProperty getTurnCountProperty() {
		return turnCount;
	}

	public int getTurnCount() {
		return turnCount.getValue();
	}

	public void setUseTimers(boolean useTimers) {
		this.useTimers.setValue(useTimers);
	}

	public BooleanProperty getUseTimers() {
		return useTimers;
	}

	public boolean isUsingTimers() {

		return this.useTimers.getValue();
	}

	public void setTimerDuration(long duration) {
		this.timerDuration = duration;
		whiteTimer.setDurationLength(timerDuration);
		blackTimer.setDurationLength(timerDuration);
	}

	public long getTimerDuration() {
		return timerDuration;
	}

	public ChessPlayerTimer getWhiteTimer() {
		return whiteTimer;
	}

	public ChessPlayerTimer getBlackTimer() {
		return blackTimer;
	}

	public void setUseDelayAsIncrement(boolean useIncrement) {

		if(timerDelay > -1) {
			useTimerIncrement.setValue(useIncrement);

			if(useIncrement) {
				useTimerBuffer.setValue(false);
				whiteTimer.setBufferTime(-1);
				blackTimer.setBufferTime(-1);
			}
		}
	}

	public void setUseDelayAsBuffer(boolean useBuffer) {

		if(timerDelay > -1) {
			useTimerBuffer.setValue(useBuffer);
			if(useBuffer) {
				useTimerIncrement.setValue(false);
				whiteTimer.setBufferTime(timerDelay);
				blackTimer.setBufferTime(timerDelay);
			}
			else {
				whiteTimer.setBufferTime(-1);
				blackTimer.setBufferTime(-1);
			}
		}
	}

	public boolean isDelayAsBuffer() {
		return useTimerBuffer.getValue();
	}

	public boolean isDelayAsIncrement() {
		return useTimerIncrement.getValue();
	}

	public boolean isUsingTimerDelay() {
		return isDelayAsBuffer() || isDelayAsIncrement();
	}

	public void setUseTimerDelay(boolean useDelay) {

		if(useDelay) {
			setUseDelayAsIncrement(true);
			setUseDelayAsBuffer(false);
		}
		else {
			setUseDelayAsIncrement(false);
			setUseDelayAsBuffer(false);
		}
	}

	public void setTimerDelay(long delay) {
		this.timerDelay = delay;

		if(!useTimerIncrement.getValue() && !useTimerBuffer.getValue())
			setUseDelayAsIncrement(true);
	}

	public long getTimerDelay() {
		return timerDelay;
	}

	public void setWhitePlayer(ChessPlayer ply) {
		this.whitePlayer = ply;
		initPlayer(ply);
	}

	public void setBlackPlayer(ChessPlayer ply) {
		this.blackPlayer = ply;
		initPlayer(ply);
	}

	private void initPlayer(ChessPlayer ply) {

		if(ply != null) {
			if(ply instanceof ChessEngine) {
				ChessEngine plyEngine = (ChessEngine) ply;
				plyEngine.addResponseListener(engineInitListener);
			}
			ply.addMoveListener(this.chessMoveListener);
			ply.setGame(this);
		}
	}

	public void setPlayerReady(ChessPlayer ply, boolean isReady) {

		if(whitePlayer == ply)
			whiteReadyToStart.setValue(isReady);
		else if(blackPlayer == ply)
			blackReadyToStart.setValue(isReady);

		if(isGameStarted()) {
			if(this.isReadyToStart()) {
				//gamePaused.setValue(false);
				cycleTurns();
			}
		}
	}

	public boolean isReadyToStart() {
		return whitePlayer.isReadyForGame() && blackPlayer.isReadyForGame();
	}

	public ChessPlayer getWhitePlayer() {
		return whitePlayer;
	}

	public ChessPlayer getBlackPlayer() {
		return blackPlayer;
	}

	public ChessPlayer getCurrentTurnsPlayer() {

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


	public void cycleTurns() {

		if(isGameStarted()) {
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

			if(whiteTurn.getValue()) {
				turnCount.set(turnCount.get() + 1);
				whitePlayer.requestToMakeMove();
			}
			else if(blackTurn.getValue())
				blackPlayer.requestToMakeMove();

			if(useTimers.getValue()) {
				if(whiteTurn.getValue()) {
					if(useTimerIncrement.getValue())
						whiteTimer.incrementTime(timerDelay);
					whiteTimer.resume();
					blackTimer.pause();
				}
				else {
					if(useTimerIncrement.getValue())
						blackTimer.incrementTime(timerDelay);
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

		if(move.isMoveFound()) {

			if(fromCell != null) {

				ChessPiece fromPiece = fromCell.getChessPiece();
				ChessCell toCell = move.getToCell();

				if(fromPiece != null) {
					if(toCell != null) {

						ChessPiece toPiece = toCell.getChessPiece();

						if(toPiece != null) {
							if(toPiece.isWhiteTeam() != fromPiece.isWhiteTeam()) {
								if(toPiece.isWhiteTeam())
									deadWhite.add(toPiece);
								else if(toPiece.isBlackTeam())
									deadBlack.add(toPiece);
							}
							else {
								System.out.println("same team...");
								return false;
							}
						}

						if(!move.getLeftover().isEmpty()) {
							if(move.getLeftover().equals("q"))
								fromPiece.setPieceType('q', true);
						}

						fromCell.setChessPiece(null);
						toCell.setChessPiece(fromPiece);
						fromPiece.incrementMoveCount();

						return true;
					}
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

		return true;
	}

	public ChessCell[][] getCells() {
		return chessCells;
	}

	public boolean isValidFEN(String testFEN) {

		if(testFEN == null)
			return false;

		if(testFEN.isEmpty())
			return false;

		return true;
	}

	public void setBoardFromFEN(String strFEN) {


		if(isValidFEN(strFEN)) {

			ChessCell[][] parsed = new ChessCell[8][8];

			String[] splitFEN = strFEN.split("/");

			for(int row = 0; row < 8; row++) {
				for(int col = 0; col < 8; col++) {
					parsed[col][row] = new ChessCell(col, row);
				}
			}

			for(int row = 7; row >= 0; row--) {

				String rowFEN = splitFEN[7 - row];

				char[] rowCharArray = rowFEN.toCharArray();

				int colIndex = 7;

				for(Character pieceChar : rowCharArray) {
					if(Character.isDigit(pieceChar))
						colIndex -= Character.digit(pieceChar, 10);
					else {
						parsed[7 - colIndex][row].setChessPiece(new ChessPiece(pieceChar));
						colIndex--;
					}
				}
			}

			chessCells = parsed;
		}
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

		if(whiteTurn.getValue())
			boardFEN = boardFEN + " w";
		else if(blackTurn.getValue())
			boardFEN = boardFEN + " b";


		boardFEN = boardFEN + " KQkq - 0 " + turnCount.getValue();

		return boardFEN;
	}
}
