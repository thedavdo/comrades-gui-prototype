package edu.purdue.comradesgui.javafx;

public class EngineCommand {

	private String cmdStr;

	private boolean shouldFlush;

	public EngineCommand(String cmd) {
		this(cmd, true);
	}

	public EngineCommand(String cmd, boolean flush) {
		cmdStr = cmd;
		shouldFlush = flush;
	}

	public String getCommand() {
		return cmdStr;
	}

	public boolean shouldFlush() {
		return shouldFlush;
	}
}
