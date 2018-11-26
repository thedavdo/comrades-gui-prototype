package edu.purdue.comradesgui.javafx;

import java.util.ArrayList;
import java.util.List;

public class GoCommandBuilder {

	private List<ChessMove> searchMoves;

	private boolean ponder;

	private int depth;
	private int nodes;
	private int mate;

	private long moveTime;

	private boolean infinite;

	public GoCommandBuilder() {

		ponder = false;

		searchMoves = new ArrayList<>();
		depth = -1;
		nodes = -1;
		mate = -1;
		moveTime = -1;

		infinite = false;
	}

	public void setSearchMoves(List<ChessMove> moves) {
		this.searchMoves.clear();
		this.searchMoves.addAll(moves);
	}

	public void addSearchMove(ChessMove move) {
		this.searchMoves.add(move);
	}

	public List<ChessMove> getSearchMoves() {
		return searchMoves;
	}

	public void setPonder(boolean ponder) {
		this.ponder = ponder;
	}

	public boolean getPonder() {
		return ponder;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getDepth() {
		return depth;
	}

	public void setNodes(int nodes) {
		this.nodes = nodes;
	}

	public int getNodes() {
		return nodes;
	}

	public void setMate(int mate) {
		this.mate = mate;
	}

	public int getMate() {
		return mate;
	}

	public void setMoveTime(long moveTime) {
		this.moveTime = moveTime;
	}

	public long getMoveTime() {
		return moveTime;
	}

	public void setInfinite(boolean infinite) {
		this.infinite = infinite;
	}

	public boolean isInfinite() {
		return infinite;
	}

	public String getCommand(ChessGame chessGame) {

		String cmd = "go";

		if(!searchMoves.isEmpty()) {
			cmd += " searchmoves";

			for(ChessMove move : searchMoves)
				cmd += " " + move.getRawMove();
		}
		else if(ponder) {
			cmd += " ponder";
		}
		else if(infinite) {
			cmd += " infinite";
		}
		else {
			if(chessGame.isUsingTimers()) {
				cmd += " wtime " + chessGame.getWhiteTimer().getRemainingTime();
				cmd += " btime " + chessGame.getBlackTimer().getRemainingTime();

				if(chessGame.isUsingTimerDelay()) {
					cmd += " winc " + chessGame.getTimerDelay();
					cmd += " binc " + chessGame.getTimerDelay();
				}

				if(depth > 0)
					cmd += " depth " + depth;

				if(nodes > 0)
					cmd += " nodes " + nodes;

				if(mate > 0)
					cmd += " mate " + mate;

				if(moveTime > 0)
					cmd += " movetime " + moveTime;
			}
		}

		return cmd;
	}
}
