package edu.purdue.comradesgui.javafx;

import javafx.concurrent.Task;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class ChessEngine {


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

	public ChessEngine() {

		logBuffer = new StringBuffer();
		rawOptions = new ArrayList<>();

		responseListeners = new ArrayList<>();

		pushCmdList = new ArrayList<>();

		engineName = "nil";

		initListener();
	}

	private void initListener() {

		addResponseListener((cmd) -> {
			//Listen for Response from 'isready'
			if(cmd.equals("readyok")) {
				logInfo("eng < Ready");
				isReady = true;
				waitingForReady = false;
				return true;
			}

		//Listen for last Response from 'uci'
			if (cmd.equals("uciok")) {
				logInfo("UCI Ready.");
				return true;
			}

		//Listen for one of the Responses from 'uci'
			if (cmd.startsWith("id name")) {
				engineName = cmd.substring(8);
				logInfo("Engine Name: " + engineName);
				return true;
			}

		//Listen for one of the Responses from 'uci'
			if (cmd.startsWith("id author")) {
				engineAuthor = cmd.substring(10);
				logInfo("Author(s): " + engineAuthor);
				return true;
			}

		//Listen for one of the Responses from 'uci'
			if(cmd.startsWith("option")) {
				rawOptions.add(cmd);
				logInfo("Option Raw: " + cmd);
				return true;
			}
			return false;
		});
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

	public boolean hasLoaded() {
		return hasLoaded;
	}

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

	private void initializeEngine() {

		logInfo("Initializing Engine...");

		Task readerLoop = new Task() {
			@Override
			protected Void call() {

				boolean runLoop = true;

				while(runLoop) {
					String state;
					try {
						state = bufReader.readLine();

						if(state != null) {
							if(!state.isEmpty()) {
								processResponse(state);
							}
						}
					}
					catch(Exception e) {
						runLoop = false;
						logInfo("!! Error Reading from bufReader !!");
						e.printStackTrace();
					}

					if(!isReady) {
						if(!waitingForReady) {
							sendCommand("isready", true);
							logInfo("Waiting for engine ready");
							waitingForReady = true;
						}
					}

					pushCommands();
				}

				return null;
			}
		};
		new Thread(readerLoop).start();

		logInfo("...Initialized");

		requestCommand("uci", true);
	}

	public void startNewGame() {

		requestCommand("ucinewgame", true);
	}

	private void processResponse(String in) {

		boolean consumed = false;

		for(ResponseListener rl : responseListeners) {
			if(rl.onResponse(in)) {
				consumed = true;
				break;
			}
		}

		if(!consumed)
			logInfo("eng < " + in);
	}

	/**
	 * Send a command to the chess engine
	 * @param cmd command to be sent
	 */
	public void requestCommand(String cmd, boolean flush) {
		pushCmdList.add(new EngineCommand(cmd, flush));
	}

	private void sendCommand(String cmd, boolean flush) {
		logInfo("cmd > " + cmd);
		bufWriter.println(cmd);
		if(flush)
			flushWriter();
	}

	private void pushCommands() {

		if(isReady) {
			if(!pushCmdList.isEmpty()) {

				for(EngineCommand cmd : pushCmdList)
					sendCommand(cmd.getCommand(), cmd.shouldFlush());

				pushCmdList.clear();

				isReady = false;
			}
		}
	}

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

	/**
	 * Flushes the BufferedWriter
	 */
	private void flushWriter() {
		logInfo("> > Flushing Writer...");
		bufWriter.flush();
		logInfo("< < Flush finished.");
	}

	public String toString() {
		return this.engineName;
	}
}
