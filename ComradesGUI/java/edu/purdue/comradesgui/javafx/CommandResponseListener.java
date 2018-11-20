package edu.purdue.comradesgui.javafx;

import java.util.EventListener;

public interface CommandResponseListener extends EventListener {

	boolean onResponse(String[] cmdTokens, String cmd);
}
