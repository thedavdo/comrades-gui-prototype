package edu.purdue.comradesgui.src;

public class ChessEngineOptionButton extends ChessEngineOption {

	public ChessEngineOptionButton(String toParse, ChessEngine chessEngine) {
		super(toParse, chessEngine);
	}

	public void pressButton() {
		chessEngine.requestCommand("setoption name " + name, true);
	}
}
