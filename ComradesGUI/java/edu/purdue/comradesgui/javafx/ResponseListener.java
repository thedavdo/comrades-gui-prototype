package edu.purdue.comradesgui.javafx;

import java.util.EventListener;

public interface ResponseListener extends EventListener {

	boolean onResponse(String[] cmdTokens, String cmd);
}
