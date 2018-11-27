package edu.purdue.comradesgui.src;

public class ChessEngineOptionString extends ChessEngineOption {

	private String strValue;

	public ChessEngineOptionString(String toParse, ChessEngine chessEngine) {
		super(toParse, chessEngine);
	}

	public void setStringValue(String str) {
		this.strValue = str;
		chessEngine.requestCommand("setoption name " + name + " value " + strValue, true);
	}

	public String getStringValue() {
		return strValue;
	}

	public String toString() {

		String out = super.toString();

		out += " | Option String Value: " + strValue;

		return out;
	}
}
