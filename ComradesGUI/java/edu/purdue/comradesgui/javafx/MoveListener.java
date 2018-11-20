package edu.purdue.comradesgui.javafx;

import java.util.EventListener;

public interface MoveListener extends EventListener {

	boolean moveEvent(Player ply, ChessMove move);
}
