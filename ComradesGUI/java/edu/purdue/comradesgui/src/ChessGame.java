package edu.purdue.comradesgui.src;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChessGame {

	private ChessCell[][] chessCells;
	private ObservableList<ChessPiece> deadWhite, deadBlack;

	private List<ChessMove> chessMoveList;

	private IntegerProperty turnCount;
	private IntegerProperty halfTurnCount;

	private ChessCell enPassantCell;

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

		chessMoveList = new ArrayList<>();

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

		turnCount.setValue(1);

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
				performMove(move);
				chessMoveList.add(move);
				endTurn();
			}
		};

		setBoardFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
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

			if(useIncrement)
				useTimerBuffer.setValue(false);
		}
	}

	public void setUseDelayAsBuffer(boolean useBuffer) {

		if(timerDelay > -1) {
			useTimerBuffer.setValue(useBuffer);

			if(useBuffer)
				useTimerIncrement.setValue(false);
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

	public BooleanProperty getWhiteTurnProperty() {
		return whiteTurn;
	}

	public BooleanProperty getBlackTurnProperty() {
		return blackTurn;
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

	public ObservableList<ChessPiece> getDeadWhitePieces() {
		return deadWhite;
	}

	public ObservableList<ChessPiece> getDeadBlackPieces() {
		return deadBlack;
	}

	public boolean canCastleWhiteKingSide() {
		return whiteKingCastle.getValue();
	}

	public boolean canCastleWhiteQueenSide() {
		return whiteQueenCastle.getValue();
	}

	public boolean canCastleBlackKingSide() {
		return blackKingCastle.getValue();
	}

	public boolean canCastleBlackQueenSide() {
		return blackQueenCastle.getValue();
	}

	public BooleanProperty getCastleWhiteKingSide() {
		return whiteKingCastle;
	}

	public BooleanProperty getCastleWhiteQueenSide() {
		return whiteQueenCastle;
	}

	public BooleanProperty getCastleBlackKingSide() {
		return blackKingCastle;
	}

	public BooleanProperty getCastleBlackQueenSide() {
		return blackQueenCastle;
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

	/**
	 * Called to set off the turn cycle for the first time.
	 */
	private void startTurnCycle() {

		if(!(whiteTurn.getValue() || blackTurn.getValue())) {
			whiteTurn.setValue(true);
			blackTurn.setValue(false);
		}

		ChessPlayer activePlayer = getCurrentTurnsPlayer();

		if(useTimers.getValue()) {

			if(useTimerBuffer.getValue()) {
				whiteTimer.setBufferTime(timerDelay);
				blackTimer.setBufferTime(timerDelay);
			}
			else if (useTimerIncrement.getValue())
				activePlayer.getMoveTimer().incrementTime(timerDelay);

			whiteTimer.initialize();
			blackTimer.initialize();

			activePlayer.getMoveTimer().resume();
			activePlayer.getOpponentMoveTimer().pause();
		}

		activePlayer.requestToMakeMove();
	}

	/**
	 * Called mainly when a player ends their turn. Will process all the timer logic and turnCount information.
	 */
	public void endTurn() {

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
		}
	}

	/**
	 * Method to process the ChessMove, usually called from a ChessPlayer's ChessMoveListener.
	 */
	public void performMove(ChessMove chessMove) {

		if(chessMove.isLegalMove()) {

			ChessCell fromCell = chessMove.getFromCell();

			if(fromCell != null) {
				ChessPiece fromPiece = fromCell.getChessPiece();
				if(fromPiece != null) {

					ChessCell toCell = chessMove.getToCell();
					if(toCell != null) {

						ChessPiece toPiece = toCell.getChessPiece();

						if(chessMove.isPromotionMove())
							fromPiece.setPieceType('q', true);

						if(chessMove.isCaptureMove())
							setPieceAsDead(toPiece);
						else if(chessMove.isCastlingMove()) {

							int selFromColSide = -1;
							int selToColSide = -1;

							if(toCell.getColPos() < fromCell.getColPos()) {
								selFromColSide = fromCell.getColPos();
								selToColSide =  toCell.getColPos() + 1;
							}
							else if(toCell.getColPos() > fromCell.getColPos()) {
								selFromColSide = toCell.getColPos() + 1;
								selToColSide =  toCell.getColPos() - 1;
							}

							ChessCell castleFromCell = chessCells[selFromColSide][toCell.getRowPos()];
							ChessCell castleToCell = chessCells[selToColSide][toCell.getRowPos()];

							ChessPiece castlePiece = castleFromCell.getChessPiece();

							castleToCell.setChessPiece(castlePiece);
							castlePiece.incrementMoveCount();
						}

						Character pieceChar = Character.toLowerCase(fromPiece.getPieceChar());

						if(pieceChar == 'k') {
							if(fromPiece.isWhiteTeam()) {
								whiteKingCastle.setValue(false);
								whiteQueenCastle.set(false);
							}
							else {
								blackQueenCastle.setValue(false);
								blackKingCastle.setValue(false);
							}
						}
						else if(pieceChar == 'r') {
							if(fromPiece.isWhiteTeam()) {
								if(fromPiece.getCell().getColPos() == 0)
									whiteQueenCastle.setValue(false);
								else
									whiteKingCastle.setValue(false);
							}
							else {
								if(fromPiece.getCell().getColPos() == 0)
									blackQueenCastle.setValue(false);
								else
									blackKingCastle.setValue(false);
							}
						}
						else if(pieceChar == 'p') {
							if(Math.abs(fromCell.getRowPos() - toCell.getRowPos()) == 2) {
								if(fromPiece.isWhiteTeam())
									enPassantCell = chessCells[toCell.getColPos()][toCell.getRowPos() - 1];
								else
									enPassantCell = chessCells[toCell.getColPos()][toCell.getRowPos() + 1];
							}
						}
						else
							enPassantCell = null;

						if(chessMove.isCaptureMove() || (pieceChar == 'p'))
							halfTurnCount.setValue(0);
						else
							halfTurnCount.setValue(halfTurnCount.getValue() + 1);


						toCell.setChessPiece(fromPiece);
						fromPiece.incrementMoveCount();
					}
				}
			}
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
	public boolean addPieceToCell(ChessPiece piece, int col, int row) {


		if(chessCells != null) {
			if(col < chessCells.length && row < chessCells[0].length) {
				if (piece != null) {
					chessCells[col][row].setChessPiece(piece);
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Removes the piece from the cell it resides in.
	 * @param piece
	 */
	public void removePieceFromCell(ChessPiece piece) {

		if(piece != null) {
			ChessCell cell = piece.getCell();
			if(cell != null)
				cell.setChessPiece(null);
		}
	}

	/**
	 * Removes the piece from play and adds it to it's respective team dead array.
	 * @param piece
	 */
	public void setPieceAsDead(ChessPiece piece) {

		this.removePieceFromCell(piece);

		if(piece.isWhiteTeam())
			deadWhite.add(piece);
		else if(piece.isBlackTeam())
			deadBlack.add(piece);
	}

	/**
	 * Set the gamestate from a provided FEN string.
	 * @param strFEN FEN to parse
	 */
	public void setBoardFromFEN(String strFEN) {

		if(this.isValidFEN(strFEN)) {

			String[] splitSpace = strFEN.split(" ");

			String[] splitFEN = splitSpace[0].split("/");

			for(int row = 0; row < 8; row++) {
				for(int col = 0; col < 8; col++) {
					chessCells[col][row] = new ChessCell(col, row);
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
						addPieceToCell(new ChessPiece(pieceChar), 7 - colIndex, row);
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
				this.blackQueenCastle.setValue(splitSpace[2].contains("q"));
				this.blackKingCastle.setValue(splitSpace[2].contains("k"));
				this.whiteQueenCastle.setValue(splitSpace[2].contains("Q"));
				this.whiteKingCastle.setValue(splitSpace[2].contains("K"));
			}

			if(splitSpace.length > 3) {

				String pos = splitSpace[3];

				if(pos.length() == 2) {

					int cellCol = Character.toLowerCase(pos.charAt(0)) - 97;
					int cellRow = -1;

					if(isNumber("" + pos.charAt(1)))
						cellRow = Integer.parseInt("" + pos.charAt(1)) - 1;

					if(cellCol < 8 && cellCol >= 0 && cellRow < 8 && cellRow >= 0)
						enPassantCell = chessCells[cellCol][cellRow];
					else
						enPassantCell = null;
				}
				else
					enPassantCell = null;
			}

			if(splitSpace.length > 4) {
				if(isNumber(splitSpace[4]))
					halfTurnCount.setValue(Integer.parseInt(splitSpace[4]));
			}

			if(splitSpace.length > 5) {
				if(isNumber(splitSpace[5]))
					turnCount.setValue(Integer.parseInt(splitSpace[5]));
			}
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


		if(enPassantCell != null)
			boardFEN = boardFEN + enPassantCell.getCoordString();
		else
			boardFEN = boardFEN + "-";

		boardFEN = boardFEN + " ";

		boardFEN = boardFEN + halfTurnCount.getValue();

		boardFEN = boardFEN + " ";

		boardFEN = boardFEN + turnCount.getValue();

		return boardFEN;
	}

	public String generateStringPGN() {

		String out = "";

		out += "[Event " + '"' + "ComradesGUI Match" + '"' + "]" + System.lineSeparator();
		out += "[Site " + '"' + "Simulation" + '"' + "]" + System.lineSeparator();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		LocalDateTime now = LocalDateTime.now();

		out += "[Date " + '"' + dtf.format(now) + '"' + "]" + System.lineSeparator();
		out += "[Round " + '"' + turnCount.getValue() + '"' + "]" + System.lineSeparator();


		if(whitePlayer != null)
			out += "[White " + '"' + whitePlayer.getPlayerName() + '"' + "]" + System.lineSeparator();

		if(blackPlayer != null)
			out += "[Black " + '"' + blackPlayer.getPlayerName() + '"' + "]" + System.lineSeparator();
		out += "[Result " + '"' + "TBD" + '"' + "]" + System.lineSeparator();

		out += System.lineSeparator();

		String buildMoves = "";

		int curMove = -1;
		for(int i = 0; i < chessMoveList.size(); i++) {

			ChessMove move = chessMoveList.get(i);

			if(curMove != move.getTurnNumber()) {
				curMove = move.getTurnNumber();
				buildMoves += curMove + ". ";
			}

			if(move.isCastlingMove()) {
				buildMoves += "O-O";
			}
			else {

				if(move.isMoveFound()) {
					if(Character.toLowerCase(move.getMovingPiece().getPieceChar()) != 'p')
						buildMoves += move.getMovingPiece().getPieceChar();

					if(move.isCaptureMove())
						buildMoves += "x";

					buildMoves += move.getToCell().getCoordString();
				}
				else
					buildMoves += "0000";
			}

			buildMoves += " ";
		}

		out += buildMoves;

		return out;
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

			String[] splitFEN = splitSpace[0].split("/");

			if(splitFEN.length != 8)
				return false;
		}
		return true;
	}
}
