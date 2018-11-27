package edu.purdue.comradesgui.src;

public class ChessEngineOptionCheck extends ChessEngineOption {


	private boolean checkValue;

	public ChessEngineOptionCheck(String toParse, ChessEngine chessEngine) {
		super(toParse, chessEngine);

		int defaultIndex = toParse.indexOf("default");

		if(defaultIndex > 0) {
			if(type.equalsIgnoreCase("check")) {

				int typeIndex = toParse.indexOf("type");
				checkValue = Boolean.valueOf(toParse.substring(typeIndex + 5));
			}
		}
	}

	public void setChecked(boolean check) {

		this.checkValue = check;
		chessEngine.requestCommand("setoption name " + name + " value " + checkValue, true);
	}

	public boolean isChecked() {

		return checkValue;
	}

	public String toString() {

		String out = super.toString();

		out += " | Option Check Value: " + checkValue;

		return out;
	}
}
