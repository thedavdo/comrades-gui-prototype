package edu.purdue.comradesgui.src;

public class ChessEngineOptionButton extends ChessEngineOption {

	public ChessEngineOptionButton(String toParse, ChessEngine chessEngine) {
		super(toParse, chessEngine);
	}

	@Override
	protected void parseInputString(String name, String value) { }

	/**
	 * Sends command notifying Engine that this button has been pressed.
	 * OptionButton apparently doesn't need any arguments in the setoption command.
	 */
	public void pressButton() {
		chessEngine.requestCommand("setoption name " + name, true);
	}
}
