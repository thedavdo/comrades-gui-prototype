package edu.purdue.comradesgui.javafx;

public class EngineOption {

	private String rawOptionFeed;

	private String name;
	private String type;

	private boolean check;

	private int min, max;

	private int value;

	public EngineOption(String toParse) {

		rawOptionFeed = toParse;

		int nameIndex = toParse.indexOf("name");
		int typeIndex =  toParse.indexOf("type");
		int defaultIndex = toParse.indexOf("default");

		name = toParse.substring(nameIndex + 5, typeIndex - 1);

		if(defaultIndex > 0) {
			type = toParse.substring(typeIndex + 5, defaultIndex - 1);

			if(type.equals("check"))
				check = Boolean.valueOf(toParse.substring(typeIndex + 5));

			if(type.equals("spin")) {

				int minIndex = toParse.indexOf("min");
				int maxIndex = toParse.indexOf("max");

				value = Integer.valueOf(toParse.substring(defaultIndex + 8, minIndex - 1));
				min = Integer.valueOf(toParse.substring(minIndex + 4, maxIndex - 1));
				max = Integer.valueOf(toParse.substring(maxIndex + 4));
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

	public String toString() {

		String out = "";

		out += "Option Name: " + name;
		out += " | Option Type: " + type;
		out += " | Option Check Value: " + check;
		out += " | Option Int Value: " + value;
		out += " | Option Int Min Value: " + min;
		out += " | Option Int Max Value: " + max;

		return out;
	}
}
