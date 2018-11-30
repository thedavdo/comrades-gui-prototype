package edu.purdue.comradesgui.src;

public class ChessEngineCommand {

	private String cmdStr;

	protected boolean shouldFlush;

	protected ChessEngineCommand() {}

	public ChessEngineCommand(String cmd) {
		this(cmd, true);
	}

	public ChessEngineCommand(String cmd, boolean flush) {
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
