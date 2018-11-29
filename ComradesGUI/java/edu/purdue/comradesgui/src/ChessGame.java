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
	private IntegerProperty halfTurnCount;

	private ChessPlayer whitePlayer, blackPlayer;

	private BooleanProperty whiteKingCastle, whiteQueenCastle;
	private BooleanProperty blackKingCastle, blackQueenCastle;

	private ChessPlayerTimer whiteTimer, blackTimer;

	private BooleanProperty whiteReadyToStart, blackReadyToStart;
	private BooleanProperty gameStarted;

	private BooleanProperty useTimers;

	private BooleanProperty useTimerIncrement;
	private BooleanProperty useTimerBuffer;

	private long timerDuration;
	private long timerDelay;

	private BooleanProperty whiteTurn, blackTurn;

	private ChessMoveListener chessMoveListener;

	public ChessGame() {

		chessCells = new ChessCell[8][8];

		whiteTimer = new ChessPlayerTimer();
		blackTimer = new ChessPlayerTimer();

		gameStarted = new SimpleBooleanProperty();

		turnCount = new SimpleIntegerProperty();
		halfTurnCount = new SimpleIntegerProperty();

		whiteKingCastle = new SimpleBooleanProperty();
		whiteQueenCastle = new SimpleBooleanProperty();
		blackKingCastle = new SimpleBooleanProperty();
		blackQueenCastle = new SimpleBooleanProperty();

		whiteReadyToStart = new SimpleBooleanProperty();
		blackReadyToStart = new SimpleBooleanProperty();

		whiteTurn = new SimpleBooleanProperty();
		blackTurn = new SimpleBooleanProperty();

		useTimers = new SimpleBooleanProperty();

		useTimerIncrement = new SimpleBooleanProperty();
		useTimerBuffer = new SimpleBooleanProperty();

		deadBlack = FXCollections.observableArrayList();
		deadWhite = FXCollections.observableArrayList();

		whiteKingCastle.setValue(true);
		whiteQueenCastle.setValue(true);
		blackKingCastle.setValue(true);
		blackQueenCastle.setValue(true);

		gameStarted.setValue(false);

		turnCount.set(0);

		useTimers.setValue(false);
		useTimerIncrement.setValue(false);
		useTimerBuffer.setValue(false);

		timerDuration = 300 * 1000;
		timerDelay = 5000;

		whiteReadyToStart.setValue(false);
		blackReadyToStart.setValue(false);

		whiteTurn.setValue(true);
		blackTurn.setValue(false);

		chessMoveListener = (player, move) -> {
			if(player == getCurrentTurnsPlayer()) {
				makeMove(move);
				endTurnCycle();
			}
		};

		setBoardFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPP1P/RNBQKBNR");
	}

	public ChessCell[][] getCells() {
		return chessCells;
	}

	public ChessPlayer getWhitePlayer() {
		return whitePlayer;
	}

	public ChessPlayer getBlackPlayer() {
		return blackPlayer;
	}

	/**
	 * Set the given player to be the White Player, and initialize the player.
	 * @param ply
	 */
	public void setWhitePlayer(ChessPlayer ply) {
		this.whitePlayer = ply;
		initPlayer(ply);
	}

	/**
	 * Set the given player to be the Black Player, and initialize the player.
	 * @param ply
	 */
	public void setBlackPlayer(ChessPlayer ply) {
		this.blackPlayer = ply;
		initPlayer(ply);
	}

	/**
	 * Adds engineResponse(if ChessEngine) and ChessMove Listener to the provided player instance
	 */
	private void initPlayer(ChessPlayer ply) {

		if(ply != null) {
			ply.addMoveListener(this.chessMoveListener);
			ply.setGame(this);
		}
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

	/**
	 * If the delay is a separate count down before the timer begins every turn
	 */
	public boolean isDelayAsBuffer() {
		return useTimerBuffer.getValue();
	}

	/**
	 * If the delay is being added to the timer's value every turn
	 */
	public boolean isDelayAsIncrement() {
		return useTimerIncrement.getValue();
	}

	/**
	 * Is the game using a delay(Incremental or Buffered) for the timers
	 * @return
	 */
	public boolean isUsingTimerDelay() {
		return isDelayAsBuffer() || isDelayAsIncrement();
	}

	/**
	 * Enable or disable the timer delay. If set to true, default will be incremental
	 * @param useDelay should use delay
	 */
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

	/**
	 * Set the duration of the timer delay
	 * @param delay
	 */
	public void setTimerDelay(long delay) {
		this.timerDelay = delay;

		if(!useTimerIncrement.getValue() && !useTimerBuffer.getValue())
			setUseDelayAsIncrement(true);
	}

	/**
	 * Duration of the timer delay
	 * @return duration
	 */
	public long getTimerDelay() {
		return timerDelay;
	}

	/**
	 * Set the provided player to be ready for the game to begin. Most useful for engines, as they can take a while to
	 * finish initializing after being told a new game has begun.
	 * @param ply Player to be readied / unreadier
	 * @param isReady
	 */
	public void setPlayerReady(ChessPlayer ply, boolean isReady) {

		if(whitePlayer == ply)
			whiteReadyToStart.setValue(isReady);
		else if(blackPlayer == ply)
			blackReadyToStart.setValue(isReady);

		if(isGameStarted()) {
			if(this.isReadyToStart())
				startTurnCycle();
		}
	}

	/**
	 * If both players are ready to start playing the game
	 */
	public boolean isReadyToStart() {
		return whitePlayer.isReadyForGame() && blackPlayer.isReadyForGame();
	}

	/**
	 * Get the current turn's player
	 * @return
	 */
	public ChessPlayer getCurrentTurnsPlayer() {

		if(whiteTurn.getValue())
			return whitePlayer;
		else if(blackTurn.getValue())
			return blackPlayer;
		else
			return null;
	}

	public boolean isGameStarted() {
		return gameStarted.getValue();
	}

	public BooleanProperty getGameStartedBooleanProperty() {
		return gameStarted;
	}

	/**
	 * Starts the game, notifies the players the game is starting, and then waits if the players are not ready.
	 */
	public void startGame() {
		System.out.println("Starting Game...");
		this.whitePlayer.prepareForGame();
		this.blackPlayer.prepareForGame();
		gameStarted.setValue(true);
	}

	private void startTurnCycle() {


		if(!(whiteTurn.getValue() || blackTurn.getValue())) {
			whiteTurn.setValue(true);
			blackTurn.setValue(false);
		}

		ChessPlayer activePlayer = getCurrentTurnsPlayer();

		if(useTimers.getValue()) {
			whiteTimer.initialize();
			blackTimer.initialize();
			activePlayer.getMoveTimer().resume();
			activePlayer.getOpponentMoveTimer().pause();
		}

		activePlayer.requestToMakeMove();
	}

	/**
	 * Called mainly when a player ends their move. Will process all the timer logic and turnCount information.
	 */
	public void endTurnCycle() {

		if(isGameStarted()) {
			if(whiteTurn.getValue()) {
				whiteTurn.setValue(false);
				blackTurn.setValue(true);
			}
			else if(blackTurn.getValue()) {
				whiteTurn.setValue(true);
				blackTurn.setValue(false);
			}

			ChessPlayer currentPlayer = getCurrentTurnsPlayer();

			if(currentPlayer == whitePlayer)
				turnCount.set(turnCount.get() + 1);

			if(useTimers.getValue()) {

				currentPlayer.getOpponentMoveTimer().pause();

				if(useTimerIncrement.getValue())
					currentPlayer.getMoveTimer().incrementTime(timerDelay);

				currentPlayer.getMoveTimer().resume();
			}


			currentPlayer.requestToMakeMove();

//			if(useTimers.getValue()) {
//				if(whiteTurn.getValue()) {
//					if(useTimerIncrement.getValue())
//						whiteTimer.incrementTime(timerDelay);
//					whiteTimer.resume();
//					blackTimer.pause();
//				}
//				else {
//					if(useTimerIncrement.getValue())
//						blackTimer.incrementTime(timerDelay);
//					whiteTimer.pause();
//					blackTimer.resume();
//				}
//			}
		}
	}

	/**
	 * Tries to add the provided piece to the provided location.
	 *
	 * @param piece ChessPiece
	 * @param col col location
	 * @param row row location
	 * @return true if successfully added piece
	 */
	public boolean addPiece(ChessPiece piece, int col, int row) {

		if(chessCells != null) {
			if(piece != null) {
				chessCells[col][row].setChessPiece(piece);
				return true;
			}
		}

		return false;
	}

	/**
	 * Method to process provided ChessMove, usually called from a ChessPlayer's ChessMoveListener.
	 * @param move Move to process
	 * @return true if successfully processed move
	 */
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

	/**
	 * Method to process provided move from string
	 * @param move Move to interpret
	 * @return true if successfully processed move
	 */
	public boolean makeMove(String move) {

		return makeMove(new ChessMove(move, this));
	}

	/**
	 * Method to verify provided move from string is a legal move.
	 * @param move raw move
	 * @return true if legal
	 */
	public boolean isMoveLegal(String move) {

		return isMoveLegal(new ChessMove(move, this));
	}

	/**
	 * Method to verify provided move is a legal move.
	 * @param move move
	 * @return true if legal
	 */
	public boolean isMoveLegal(ChessMove move) {

		//TODO: Needed for when human's play.
		return true;
	}

	//rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1

	/**
	 * Set the gamestate from a provided FEN string.
	 * @param strFEN FEN to parse
	 */
	public void setBoardFromFEN(String strFEN) {

		if(this.isValidFEN(strFEN)) {

			ChessCell[][] parsed = new ChessCell[8][8];

			String[] splitSpace = strFEN.split(" ");

			String[] splitFEN = splitSpace[0].split("/");

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

			if(splitSpace.length > 1) {
				if(splitSpace[1].charAt(0) == 'b') {
					blackTurn.setValue(true);
					whiteTurn.setValue(false);
				}
				else if(splitSpace[1].charAt(0) == 'w') {
					whiteTurn.setValue(true);
					blackTurn.setValue(false);
				}
			}

			if(splitSpace.length > 2) {

				if(splitSpace[2].contains("q"))
					this.blackQueenCastle.setValue(true);
				else
					this.blackQueenCastle.setValue(false);

				if(splitSpace[2].contains("k"))
					this.blackKingCastle.setValue(true);
				else
					this.blackKingCastle.setValue(false);

				if(splitSpace[2].contains("Q"))
					this.whiteQueenCastle.setValue(true);
				else
					this.whiteQueenCastle.setValue(false);

				if(splitSpace[2].contains("K"))
					this.whiteKingCastle.setValue(true);
				else
					this.whiteKingCastle.setValue(false);

			}

			if(splitSpace.length > 3) {
				if(isNumber(splitSpace[4]))
					halfTurnCount.setValue(Integer.parseInt(splitSpace[4]));
			}

			if(splitSpace.length > 4) {
				if(isNumber(splitSpace[5]))
					turnCount.setValue(Integer.parseInt(splitSpace[5]));
			}

			chessCells = parsed;
		}
	}

	/**
	 * Generate a FEN string to capture the game state.
	 * @return FEN string
	 */
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

		boardFEN = boardFEN + " ";

		if(whiteTurn.getValue())
			boardFEN = boardFEN + "w";
		else if(blackTurn.getValue())
			boardFEN = boardFEN + "b";

		boardFEN = boardFEN + " ";

		String castling = "";

		if(whiteKingCastle.getValue())
			castling = castling + "K";
		if(whiteQueenCastle.getValue())
			castling = castling + "Q";
		if(blackKingCastle.getValue())
			castling = castling + "k";
		if(blackQueenCastle.getValue())
			castling = castling + "q";

		if(!castling.isEmpty()) {
			boardFEN = boardFEN + castling + " ";
		}
		else
			boardFEN = boardFEN + "- ";

		boardFEN = boardFEN + "-"; //en passant

		boardFEN = boardFEN + " ";

		boardFEN = boardFEN + halfTurnCount.getValue();

		boardFEN = boardFEN + " ";

		boardFEN = boardFEN + turnCount.getValue();

		return boardFEN;
	}

	private boolean isNumber(String in) {

		try {
			int num = Integer.parseInt(in);
			return true;
		}
		catch(Exception e) { }

		return false;
	}

	/**
	 * Verify provided raw FEN string is valid.
	 * @param testFEN string to test
	 * @return true if valid
	 */
	public boolean isValidFEN(String testFEN) {

		if(testFEN == null)
			return false;
		else if(testFEN.isEmpty())
			return false;
		else {
			String[] splitSpace = testFEN.split(" ");
			if(splitSpace.length == 0)
				return false;
		}
		return true;
	}
}
