package edu.purdue.comradesgui.javafx;

import javafx.concurrent.Task;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChessEngine extends Player {

	//private Process fileProcess;
	private BufferedReader bufReader;
	private PrintWriter bufWriter;

	private StringBuffer logBuffer;

	private List<ResponseListener> responseListeners;
	private List<EngineCommand> pushCmdList;

	private String engineName;
	private String engineAuthor;
	private List<String> rawOptions;

	private boolean hasLoaded = false;

	private boolean shouldLog = true;

	private boolean isReady = false;
	private boolean waitingForReady = false;

	private boolean attemptingICI = true;
	private boolean useUCI = true;

	public ChessEngine() {

		super(PlayerType.ENGINE);

		responseListeners = new ArrayList<>();
		logBuffer = new StringBuffer();
		pushCmdList = new ArrayList<>();
		rawOptions = new ArrayList<>();

		engineName = "nil";

		initListener();
	}

	public boolean hasLoaded() {
		return hasLoaded;
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

	public void addResponseListener(ResponseListener rl) {
		responseListeners.add(rl);
	}

	/**
	 * Add our Listener to the ChessEngine to process the main items
	 */
	private void initListener() {

		addResponseListener((cmdTokens, cmd) -> {

			//Listen for Response from 'isready'
			if(cmdTokens[0].equals("readyok")) {
				logInfo("eng < Ready for Cmd");
				isReady = true;
				waitingForReady = false;

				if(attemptingICI) {
					if(useUCI)
						requestCommand("uci", true);
					attemptingICI = false;
				}
				return true;
			}

			if(cmdTokens[0].equals("ici-echo")) {
				logInfo("eng < ICI Ready");
				useUCI = false;
				return true;
			}

		//Listen for last Response from 'uci'
			if(cmdTokens[0].equals("uciok")) {
				logInfo("eng < UCI Ready");
				return true;
			}

		//Listen for one of the Responses from 'uci'
			if(cmdTokens[0].equals("id")) {

				if (cmdTokens[1].equals("name")) {
					engineName = cmd.substring(cmd.indexOf(cmdTokens[2]));
					logInfo("eng < Engine Name: " + engineName);
					return true;
				}

				if (cmdTokens[1].equals("author")) {
					engineAuthor = cmd.substring(cmd.indexOf(cmdTokens[2]));
					logInfo("eng < Author(s): " + engineAuthor);
					return true;
				}
			}

		//Listen for one of the Responses from 'uci'
			if(cmdTokens[0].equals("option")) {
				rawOptions.add(cmd);
				logInfo("eng < Option Imported: " + cmd);
				return true;
			}
			return false;
		});
	}

	/**
	 * Attempts to load a Chess Engine from the given prath
	 * @param path chess engine exe location
	 */
	public void loadFromPath(String path) {

		try {
			logInfo("Attempting to load from path: " + path);
			Process fileProcess = Runtime.getRuntime().exec(path);

			bufReader = new BufferedReader(new InputStreamReader(fileProcess.getInputStream()));
			bufWriter = new PrintWriter(new OutputStreamWriter(fileProcess.getOutputStream()));

			hasLoaded = true;

			logInfo("Path loaded successfully");

			initializeEngine();
		}
		catch (Exception e) {
			logInfo("!! Error Loading Path !!");
			e.printStackTrace();
		}
	}

	/**
	 * Setup communications loop with the provided engine
	 */
	private void initializeEngine() {

		logInfo("Initializing Engine...");

		Task readerLoop = new Task() {
			@Override
			protected Void call() {

				boolean runLoop = true;

				try {
					while(runLoop) {

						pushCommands();

						String state = bufReader.readLine();

						if(state != null) {
							if(!state.isEmpty()) {
								processResponse(state);
							}
						}
					}
				}
				catch(Exception e) {
					logInfo("!! Error Reading from bufReader !!");
					e.printStackTrace();
				}

				return null;
			}
		};
		new Thread(readerLoop).start();

		logInfo("...Initialized");

		requestCommand("ici", true);
		//requestCommand("uci", true);
	}

	@Override
	public void setGame(ChessGame chessGame) {
		super.setGame(chessGame);
		requestCommand("ucinewgame", true);
	}

	@Override
	public void requestMove() {
		requestCommand("position fen " + chessGame.getStringFEN(), true);
		requestCommand("go ", true);
	}

	public void setGoType() {

	}

	/**
	 * Method that enables ResponseListeners to capture data from the Engine
	 * @param in data from engine
	 */
	private void processResponse(String in) {

		boolean consumed = false;

		String[] cmdTokens = in.split(" ");

		if(cmdTokens.length > 0) {
			for(ResponseListener rl : responseListeners) {
				if(rl.onResponse(cmdTokens, in)) {
					consumed = true;
					break;
				}
			}
		}

		if(!consumed)
			logInfo("eng < " + in);
	}

	/**
	 * Adds a command to the queue to be sent to the engine
	 * @param cmd The command you want to send
	 * @param flush Should the writer be flushed after command is sent?
	 */
	private void requestCommand(String cmd, boolean flush) {
		pushCmdList.add(new EngineCommand(cmd, flush));
	}

	/**
	 * Directly sends a command to the engine --ONLY USE WITH ISREADY--
	 * All other commands should use requestCommand
	 */
	private void sendCommand(String cmd, boolean flush) {
		logInfo("cmd > " + cmd);
		bufWriter.println(cmd);
		if(flush)
			flushWriter();
	}

	/**
	 * Method that processes all requested commands to the engine
	 */
	private void pushCommands() {

		if(isReady) {
			if(!pushCmdList.isEmpty()) {
				sendCommand(pushCmdList.get(0).getCommand(), pushCmdList.get(0).shouldFlush());
				pushCmdList.remove(0);
				isReady = false;
			}
		}

		if(!isReady) {
			if(!waitingForReady) {
				sendCommand("isready", true);
				logInfo("Waiting for engine ready response");
				waitingForReady = true;
			}
		}
	}

	/**
	 * Flushes the BufferedWriter
	 */
	private void flushWriter() {
		//logInfo("cmd > Flushing Writer...");
		bufWriter.flush();
		//logInfo("cmd < Flush finished.");
	}

	/**
	 * Verbose logging of the Engine's activity
	 * @param input Message to be logged
	 */
	public void logInfo(String input) {

		if(logBuffer != null) {
			if (isLogging()) {

				LocalDateTime curDateTime = LocalDateTime.now();
				String time = curDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);

				String logLine = time.substring(0,10) + " | " + input;

				System.out.println(logLine);

				logBuffer.append(logLine);
				logBuffer.append(System.lineSeparator());
			}
		}
	}


	public String toString() {
		return this.engineName;
	}
}
