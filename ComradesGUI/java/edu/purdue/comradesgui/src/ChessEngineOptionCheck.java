package edu.purdue.comradesgui.src;

public class ChessEngineOptionCheck extends ChessEngineOption {

	private boolean checkValue;

	public ChessEngineOptionCheck(String toParse, ChessEngine chessEngine) {
		super(toParse, chessEngine);
	}

	@Override
	protected void parseInputString(String name, String value) {

		if(name.equalsIgnoreCase("check"))
			this.checkValue = Boolean.valueOf(value);
	}

	/**
	 * Sets the Engine's check option to true/false.
	 * @param check
	 */
	public void setChecked(boolean check) {

		this.checkValue = check;
		chessEngine.requestCommand("setoption name " + name + " value " + checkValue, true);
	}

	/**
	 * Whether or not this option is checked.
	 * @return checkValue
	 */
	public boolean isChecked() {

		return checkValue;
	}

	public String toString() {

		String out = super.toString();

		out += " | Option Check Value: " + checkValue;

		return out;
	}
}
