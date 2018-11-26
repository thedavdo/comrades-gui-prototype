package edu.purdue.comradesgui.javafx;

import java.util.EventListener;

public interface ChessEngineResponseListener extends EventListener {

	void onResponse(String[] cmdTokens, String cmd, ChessEngine engine);
}
