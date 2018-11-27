package edu.purdue.comradesgui.src;

public class ChessEngineOptionSpin extends ChessEngineOption {


	private int spinMin, spinMax, spinValue;

	public ChessEngineOptionSpin(String toParse, ChessEngine chessEngine) {
		super(toParse, chessEngine);

		int defaultIndex = toParse.indexOf("default");

		if(defaultIndex > 0) {
			if(type.equalsIgnoreCase("spin")) {

				int minIndex = toParse.indexOf("min");
				int maxIndex = toParse.indexOf("max");

				spinValue = Integer.valueOf(toParse.substring(defaultIndex + 8, minIndex - 1));
				spinMin = Integer.valueOf(toParse.substring(minIndex + 4, maxIndex - 1));
				spinMax = Integer.valueOf(toParse.substring(maxIndex + 4));
			}
		}
	}

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

	public String toString() {

		String out = super.toString();

		out += " | Option Int Value: " + spinValue;
		out += " | Option Int Min Value: " + spinMin;
		out += " | Option Int Max Value: " + spinMax;

		return out;
	}
}
