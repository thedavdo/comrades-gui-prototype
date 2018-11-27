package edu.purdue.comradesgui.src;

public class ChessEngineOptionButton extends ChessEngineOption {

	public ChessEngineOptionButton(String toParse, ChessEngine chessEngine) {
		super(toParse, chessEngine);
	}

	protected void parseInputString(String name, String value) {

	}

	public void pressButton() {
		chessEngine.requestCommand("setoption name " + name, true);
	}
}
