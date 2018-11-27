package edu.purdue.comradesgui.src;

public abstract class ChessEngineOption {

	protected ChessEngine chessEngine;

	protected String rawOptionFeed;

	protected String name;
	protected String type;

	public ChessEngineOption(String toParse, ChessEngine chessEngine) {

		this.chessEngine = chessEngine;
		rawOptionFeed = toParse;

		String[] split = toParse.split(" ");

		for(int index = 0; index < split.length; index++) {

			String name = split[index];

			if(isKeyword(name)) {

				String value = "";

				if(index != split.length - 1) {
					for(int valIndex = index + 1; valIndex < split.length; valIndex++) {
						String inVal = split[valIndex];
						if(isKeyword(inVal))
							break;
						else
							value += split[valIndex];
					}
				}

				if(name.equalsIgnoreCase("name"))
					this.name = value;
				else if(name.equalsIgnoreCase("type"))
					this.type = value;
				else
					parseInputString(name, value);

			}
		}
	}

	private boolean isKeyword(String inStr) {

		String[] keywords = {"name", "type", "default", "min", "max"};

		for(String keyword : keywords)
			if(inStr.equalsIgnoreCase(keyword))
				return true;

		return false;
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

	protected abstract void parseInputString(String name, String value);
}
