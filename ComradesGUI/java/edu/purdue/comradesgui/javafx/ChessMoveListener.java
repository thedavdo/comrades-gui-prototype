package edu.purdue.comradesgui.javafx;

import java.util.EventListener;

public interface ChessMoveListener extends EventListener {

	void moveEvent(ChessPlayer ply, ChessMove move);
}
