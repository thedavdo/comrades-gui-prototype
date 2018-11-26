package edu.purdue.comradesgui.javafx;

public class EngineOption {

	private ChessEngine chessEngine;

	private String rawOptionFeed;

	private String name;
	private String type;

	private boolean checkValue;

	private String strValue;

	private int spinMin, spinMax, spinValue;

	public EngineOption(String toParse, ChessEngine chessEngine) {

		this.chessEngine = chessEngine;
		rawOptionFeed = toParse;

		int nameIndex = toParse.indexOf("name");
		int typeIndex =  toParse.indexOf("type");
		int defaultIndex = toParse.indexOf("default");

		name = toParse.substring(nameIndex + 5, typeIndex - 1);

		if(defaultIndex > 0) {
			type = toParse.substring(typeIndex + 5, defaultIndex - 1);

			if(type.equalsIgnoreCase("check"))
				checkValue = Boolean.valueOf(toParse.substring(typeIndex + 5));

			if(type.equalsIgnoreCase("spin")) {

				int minIndex = toParse.indexOf("min");
				int maxIndex = toParse.indexOf("max");

				spinValue = Integer.valueOf(toParse.substring(defaultIndex + 8, minIndex - 1));
				spinMin = Integer.valueOf(toParse.substring(minIndex + 4, maxIndex - 1));
				spinMax = Integer.valueOf(toParse.substring(maxIndex + 4));
			}
		}
		else
			type = toParse.substring(typeIndex + 5);
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setChecked(boolean check) {

		if(type.equals("check")) {
			this.checkValue = check;
			chessEngine.requestCommand("setoption name " + name + " value " + checkValue, true);
		}
		else
			System.out.println("!! Not a checkValue option !!");
	}

	public void setSpinValue(int value) {

		if(type.equalsIgnoreCase("spin")) {

			if(value <= spinMax && value >= spinMin) {
				this.spinValue = value;
				chessEngine.requestCommand("setoption name " + name + " value " + spinValue, true);
			}
			else
				System.out.println("!! Value out of bounds | Given Value: " + value + " Bounds[" + spinMin + "," + spinMax +"] !!");
		}
		else
			System.out.println("!! Not a spin option !!");
	}

	public int getSpinValue() {

		if(type.equalsIgnoreCase("spin"))
			return spinValue;
		else
			System.out.println("!! Not a spin option !!");

		return -1;
	}

	public boolean getCheckValue() {

		if(type.equalsIgnoreCase("check"))
			return checkValue;
		else
			System.out.println("!! Not a check option !!");

		return false;
	}

	public void setStringValue(String str) {

		if(type.equalsIgnoreCase("string")) {
			this.strValue = str;
			chessEngine.requestCommand("setoption name " + name + " value " + strValue, true);
		}
		else
			System.out.println("!! Not a string option !!");
	}

	public String getStringValue() {

		if(type.equalsIgnoreCase("string"))
			return strValue;
		else
			System.out.println("!! Not a string option !!");

		return null;
	}

	public void pressButton() {

		if(type.equalsIgnoreCase("button"))
			chessEngine.requestCommand("setoption name " + name, true);
		else
			System.out.println("!! Not a button option !!");
	}

	public String toString() {

		String out = "";

		out += "Option Name: " + name;
		out += " | Option Type: " + type;

		if(type.equalsIgnoreCase("check"))
			out += " | Option Check Value: " + checkValue;

		if(type.equalsIgnoreCase("spin")) {
			out += " | Option Int Value: " + spinValue;
			out += " | Option Int Min Value: " + spinMin;
			out += " | Option Int Max Value: " + spinMax;
		}

		if(type.equalsIgnoreCase("string"))
			out += " | Option String Value: " + strValue;

		return out;
	}
}
