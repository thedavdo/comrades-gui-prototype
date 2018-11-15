package edu.purdue.comradesgui.javafx;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChessEngine {


	private Process fileProcess;
	private BufferedReader bufReader;
	private PrintWriter bufWriter;

	private StringBuffer logBuffer;

	private boolean shouldLog = true;

	public ChessEngine() {
		logBuffer = new StringBuffer();
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

	public void loadFromPath(String path) {

		try {
			logInfo("Attempting to load from path: " + path);
			fileProcess = Runtime.getRuntime().exec(path);

			bufReader = new BufferedReader(new InputStreamReader(fileProcess.getInputStream()));
			bufWriter = new PrintWriter(new OutputStreamWriter(fileProcess.getOutputStream()));

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

		this.sendCommand("ici");
		this.flushWriter();
		this.readResponse();
	}

	private void logInfo(String input) {

		if(logBuffer != null) {
			if (isLogging()) {

				LocalDateTime curDateTime = LocalDateTime.now();
				String time = curDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);

				String logLine = time + " | " + input;

				System.out.println(logLine);

				logBuffer.append(logLine);
				logBuffer.append(System.lineSeparator());
			}
		}
	}

	/**
	 * Send a command to the chess engine
	 * @param cmd command to be sent
	 */
	public void sendCommand(String cmd) {
		logInfo("cmd > " + cmd);
		bufWriter.println(cmd);
	}

	public String readResponse() {

		String state = "DEBUG | uninitialized";

		//logInfo("Attempting to read from bufReader");
		try {
			state = bufReader.readLine();
			logInfo(state);
		}
		catch (IOException e) {
			logInfo("!! Error Reading from bufReader !!");
			e.printStackTrace();
		}

		return state;
	}

	/**
	 * Flushes the BufferedWriter
	 */
	public void flushWriter() {
		logInfo("Flushing Writer...");
		bufWriter.flush();
		logInfo("Flush finished.");
	}
}
