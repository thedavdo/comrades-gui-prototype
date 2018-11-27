package edu.purdue.comradesgui.src;

public class ChessEngineOptionCheck extends ChessEngineOption {


	private boolean checkValue;

	public ChessEngineOptionCheck(String toParse, ChessEngine chessEngine) {
		super(toParse, chessEngine);
	}

	protected void parseInputString(String name, String value) {

		if(name.equalsIgnoreCase("check"))
			this.checkValue = Boolean.valueOf(value);
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
