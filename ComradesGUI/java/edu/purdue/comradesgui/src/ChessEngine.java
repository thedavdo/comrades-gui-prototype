package edu.purdue.comradesgui.src;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChessEngine extends ChessPlayer {

	private String path;

	private Process fileProcess;
	private BufferedReader bufReader;
	private PrintWriter bufWriter;

	private StringBuffer logBuffer;

	private List<ChessEngineResponseListener> responseListeners;
	private ObservableList<ChessEngineCommand> pushCmdList;
	private ObservableList<ChessEngineCommand> reserveCmdList;

	private String engineAuthor;
	private ObservableList<ChessEngineOption> optionList;

	private boolean loadedFromFile = false;
	private boolean initialized = false;

	private boolean pushCmdThroughInit = false;

	private boolean shouldLog = true;

	private boolean isReady = true;
	private boolean waitingForReady = false;

	private boolean attemptingICI = true;
	private boolean useUCI = true;

	private boolean attemptingNewGame = false;

	private ChessEngineGoBuilder goCommand;

	public ChessEngine() {

		super(PlayerType.ENGINE);

		responseListeners = new ArrayList<>();
		logBuffer = new StringBuffer();
		reserveCmdList = FXCollections.observableArrayList();
		pushCmdList = FXCollections.observableArrayList();
		optionList = FXCollections.observableArrayList();

		goCommand = new ChessEngineGoBuilder();

		initListener();
	}

	public boolean hasLoadedFromFile() {
		return loadedFromFile;
	}

	public void setLogging(boolean shouldLog) {
		this.shouldLog = shouldLog;
	}

	public boolean isLogging() {
		return shouldLog;
	}

	public StringBuffer getLogBuffer() {
		return logBuffer;
	}

	public ChessEngineOption getOption(String name) {

		for(ChessEngineOption opt : optionList) {
			if(opt.getName().equalsIgnoreCase(name))
				return opt;
		}

		return null;
	}

	public ObservableList<ChessEngineOption> getOptionList() {
		return optionList;
	}

	public void addResponseListener(ChessEngineResponseListener rl) {
		responseListeners.add(rl);
	}

	/**
	 * Add ChesEngine's Listener to process the command responses
	 */
	private void initListener() {

		addResponseListener((cmdTokens, cmd, engine) -> {

			//Listen for Response from 'isready'
			if(cmdTokens[0].equals("readyok")) {
				isReady = true;
				waitingForReady = false;

				if(attemptingICI) {
					if(useUCI) {
						pushCmdThroughInit = true;
						requestCommand("uci", true);
						pushCmdThroughInit = false;
					}
					attemptingICI = false;
				}

				if(attemptingNewGame) {
					this.setReadyForGame(true);
					attemptingNewGame = false;
				}
			}

			if(cmdTokens[0].equals("ici-echo")) {
				logInfo("eng < ICI Ready");
				useUCI = false;
				finishInit();
			}

		//Listen for last Response from 'uci'
			if(cmdTokens[0].equals("uciok")) {
				logInfo("eng < UCI Ready");
				finishInit();
			}

		//Listen for one of the Responses from 'uci'
			if(cmdTokens[0].equals("id")) {

				if (cmdTokens[1].equals("name")) {

					if(getPlayerName().equalsIgnoreCase("unset playername"))
						setPlayerName(cmd.substring(cmd.indexOf(cmdTokens[2])));

					logInfo("eng < Engine Name: " + getPlayerName());
				}

				if (cmdTokens[1].equals("author")) {
					engineAuthor = cmd.substring(cmd.indexOf(cmdTokens[2]));
					logInfo("eng < Author(s): " + engineAuthor);
				}
			}

		//Listen for one of the Responses from 'uci'
			if(cmdTokens[0].equals("option")) {
				ChessEngineOption engOption = generateOption(cmd);
				optionList.add(engOption);
			}

			if(cmdTokens[0].equals("bestmove")) {
				logInfo("eng < Best Move: " + cmdTokens[1]);
				this.makeMove(new ChessMove(cmdTokens[1], chessGame));
			}
		});
	}

	/**
	 * Attempts to load a Chess Engine from the given prath
	 * @param path chess engine exe location
	 */
	public void loadFromPath(String path) {

		try {
			logInfo("Attempting to load from path: " + path);
			fileProcess = Runtime.getRuntime().exec(path);

			bufReader = new BufferedReader(new InputStreamReader(fileProcess.getInputStream()));
			bufWriter = new PrintWriter(new OutputStreamWriter(fileProcess.getOutputStream()));

			logInfo("Path loaded successfully");
			this.path = path;

			initializeEngine();

			loadedFromFile = true;
		}
		catch (Exception e) {
			logInfo("!! Error Loading Path !!");
			e.printStackTrace();
		}
	}

	public String getEnginePath() {
		return path;
	}

	/**
	 * Setup communications loop with the provided engine
	 */
	private void initializeEngine() {

		logInfo("Initializing Engine...");


		Thread readerThread = new Thread(() -> {
			AnimationTimer readerLoop = new AnimationTimer() {

				public void handle(long now) {
					try {

						String state = null;

						if(bufReader.ready())
							state = bufReader.readLine();

						if(state != null) {
							if(!state.isEmpty()) {
								processResponse(state);
							}
						}
					}
					catch(Exception e) {
						logInfo("!! Error Reading from bufReader !!");
						e.printStackTrace();
					}
				}
			};
			readerLoop.start();
		});
		readerThread.start();

		AnimationTimer writerLoop = new AnimationTimer() {

			public void handle(long now) {
				try {
					pushCommands();
				}
				catch(Exception e) {
					logInfo("!! Error Writing from to Buffer !!");
					e.printStackTrace();
				}
			}
		};
		writerLoop.start();

		pushCmdThroughInit = true;
		requestCommand("ici", true);
		pushCmdThroughInit = false;
	}

	private void finishInit() {
		pushCmdList.addAll(reserveCmdList);
		initialized = true;
		logInfo("...Initialized");
	}

	@Override
	public void prepareForGame() {
		if(this.chessGame != null) {
			requestCommand("ucinewgame", true);
			attemptingNewGame = true;
		}
	}

	/**
	 * This is called when it is this Engine's turn to play. The engine is notified by the POSITION and GO command
	 */
	@Override
	public void requestToMakeMove() {

		//FEN string is generated by the currently active ChessGame object.
		String buildFEN = "position fen " + chessGame.generateStringFEN();

		requestCommand(buildFEN, true);

		//This is the GO command, which is configured by the GoBuilder
		requestCommand(goCommand.getCommand(chessGame), true);
	}

	/**
	 * Method that enables ResponseListeners to capture data from the Engine
	 * @param in data from engine
	 */
	private void processResponse(String in) {

		String[] cmdTokens = in.split(" ");

		if(cmdTokens.length > 0) {
			for(ChessEngineResponseListener rl : responseListeners) {
				rl.onResponse(cmdTokens, in, this);
			}
		}
	}

	/**
	 * Adds a command to the queue to be sent to the engine
	 * @param cmd The command you want to send
	 * @param flush Should the writer be flushed after command is sent?
	 */
	public void requestCommand(String cmd, boolean flush) {

		ChessEngineCommand engCmd = new ChessEngineCommand(cmd, flush);

		//If the engine is not done processing the initializing commands, or if this command is not part of the init, save it to the reserve list until the init is finished.
		boolean choose = initialized || pushCmdThroughInit;

		if(choose)
			pushCmdList.add(engCmd);
		else
			reserveCmdList.add(engCmd);
	}

	/**
	 * Directly sends a command to the engine --ONLY USE WITH ISREADY--
	 * All other commands should use requestCommand
	 */
	private void sendCommand(String cmd, boolean flush) {
		logInfo("gui > " + cmd);
		bufWriter.println(cmd);
		if(flush)
			flushWriter();
	}

	/**
	 * Method that processes all requested commands to the engine
	 */
	private void pushCommands() {

		//If the engine is ready for another command...
		if(isReady) {
			//If we have a command to send...
			if(!pushCmdList.isEmpty()) {
				sendCommand(pushCmdList.get(0).getCommand(), pushCmdList.get(0).shouldFlush());
				pushCmdList.remove(0);
				isReady = false;
			}
		}

		//If we just sent a command...
		if(!isReady) {
			//If we aren't already waiting for an "isready" statement from the engine...
			if(!waitingForReady) {
				sendCommand("isready", true);
				waitingForReady = true;
			}
		}
	}

	/**
	 * Flushes the BufferedWriter
	 */
	private void flushWriter() {
		bufWriter.flush();
	}

	/**
	 * Verbose logging of the Engine's activity
	 * @param input Message to be logged
	 */
	private void logInfo(String input) {

		if(logBuffer != null) {
			if (isLogging()) {

				LocalDateTime curDateTime = LocalDateTime.now();
				String time = curDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);

				String logLine = time.substring(0,10) + " | " + getPlayerName() + " | "  + input;

				System.out.println(logLine);

				logBuffer.append(logLine);
				logBuffer.append(System.lineSeparator());
			}
		}
	}

	private ChessEngineOption generateOption(String cmd) {

		ChessEngineOption option = null;

		//Split option information by the individual spaces
		String[] split = cmd.split(" ");

		String type = "";

		//Find the type of option we just got... if any
		for(int i = 0; i < split.length; i++) {
			if(split[i].equalsIgnoreCase("type")) {
				type = split[i + 1];
				break;
			}
		}

		//Find out the type of option we found, and make the proper EngineOption object
		if(type.equalsIgnoreCase("check"))
			option = new ChessEngineOptionCheck(cmd, this);
		else if(type.equalsIgnoreCase("spin"))
			option = new ChessEngineOptionSpin(cmd, this);
		else if(type.equalsIgnoreCase("string"))
			option = new ChessEngineOptionString(cmd, this);
		else if(type.equalsIgnoreCase("button"))
			option = new ChessEngineOptionButton(cmd, this);

		return option;
	}

	/**
	 * Makes a generic copy of this engine.
	 * @return Copy of Engine
	 */
	public ChessEngine copyEngine() {

		ChessEngine copy = new ChessEngine();
		copy.setPlayerName(this.getPlayerName() + "(Copy)");
		copy.loadFromPath(path);

		return copy;
	}
}
