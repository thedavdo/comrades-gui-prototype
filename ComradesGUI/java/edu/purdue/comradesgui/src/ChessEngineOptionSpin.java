package edu.purdue.comradesgui.src;

public class ChessEngineOptionSpin extends ChessEngineOption {


	private int spinMin, spinMax, spinValue;

	public ChessEngineOptionSpin(String toParse, ChessEngine chessEngine) {
		super(toParse, chessEngine);
	}

	@Override
	protected void parseInputString(String name, String value) {

		if(name.equalsIgnoreCase("min"))
			this.spinMin = Integer.valueOf(value);
		else if(name.equalsIgnoreCase("max"))
			this.spinMax = Integer.valueOf(value);
		else if(name.equalsIgnoreCase("default"))
			this.spinValue = Integer.valueOf(value);
	}

	/**
	 * Set the current value of the Spin option. Must be between SpinMin and SpinMax bounds.
	 * @param value newSpin
	 */
	public void setSpinValue(int value) {

		if(value <= spinMax && value >= spinMin) {
			this.spinValue = value;
			chessEngine.requestCommand("setoption name " + name + " value " + spinValue, true);
		}
		else
			System.out.println("!! Value out of bounds | Given Value: " + value + " Bounds[" + spinMin + "," + spinMax +"] !!");
	}

	public int getSpinValue() {

		return spinValue;
	}

	/**
	 * Lower bound of the spin value
	 * @return spinMin
	 */
	public int getMinValue() {
		return spinMin;
	}

	/**
	 * Upper bound of the spin value
	 * @return spinMax
	 */
	public int getMaxValue() {
		return spinMax;
	}

	public String toString() {

		String out = super.toString();

		out += " | Option Int Value: " + spinValue;
		out += " | Option Int Min Value: " + spinMin;
		out += " | Option Int Max Value: " + spinMax;

		return out;
	}
}
