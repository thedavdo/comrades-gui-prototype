package edu.purdue.comradesgui.src;

public abstract class ChessEngineOption {

	protected ChessEngine chessEngine;

	protected String rawOptionFeed;

	protected String name;
	protected String type;

	public ChessEngineOption(String toParse, ChessEngine chessEngine) {

		this.chessEngine = chessEngine;
		rawOptionFeed = toParse;

		int nameIndex = toParse.indexOf("name");
		int typeIndex =  toParse.indexOf("type");
		int defaultIndex = toParse.indexOf("default");

		name = toParse.substring(nameIndex + 5, typeIndex - 1);

		if(defaultIndex > 0)
			type = toParse.substring(typeIndex + 5, defaultIndex - 1);
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

		return out;
	}
}
