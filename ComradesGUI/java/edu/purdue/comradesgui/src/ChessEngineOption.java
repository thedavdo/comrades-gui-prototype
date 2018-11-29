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

	/**
	 * Tests to see if the given input string matches any of the keywords used in option strings
	 * @param inStr String to test
	 * @return true if inStr is a keyword
	 */
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

	/**
	 * Method to process information given in the option string recieved from the Engine
	 * @param name Name of the option information
	 * @param value Value of the option information
	 */
	protected abstract void parseInputString(String name, String value);
}
