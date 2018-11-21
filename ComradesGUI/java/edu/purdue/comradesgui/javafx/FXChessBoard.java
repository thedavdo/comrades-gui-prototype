package edu.purdue.comradesgui.javafx;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FXChessBoard extends Canvas {

	private ChessGame chessGame;
	private AnimationTimer animationTimer;

	private Font fontChess;

	public FXChessBoard(double boardSize, ChessGame chessGame) {
		super(boardSize, boardSize);
		this.chessGame = chessGame;

		try {
			FileInputStream fileInputStream = new FileInputStream(new File("MERIFONT.TTF"));
			fontChess = Font.loadFont(fileInputStream, getCheckerSize() - 4);
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}

		animationTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {

				GraphicsContext graphics = FXChessBoard.this.getGraphicsContext2D();
				graphics.clearRect(0, 0, boardSize, boardSize);

				ChessCell[][] cells = chessGame.getCells();

				for(int row = 7; row >= 0; row--) {
					for(int col = 0; col < 8; col++) {

						double xLoc = (7-col) * getCheckerSize();
						double yLoc = (row + 1) * getCheckerSize();

						if((col + row) % 2 == 0)
							graphics.setFill(Color.WHITE);
						else
							graphics.setFill(Color.LIGHTGRAY);

						graphics.fillRect(xLoc, row *getCheckerSize(), getCheckerSize(), getCheckerSize());

						ChessCell cell = cells[col][row];

						if(cell.getChessPiece() != null) {
							Character ch = cells[col][row].getChessPiece().getPieceChar();

							graphics.setFont(fontChess);
							String str = "" + ch;

							Text text = new Text(str);
							text.setFont(fontChess);

							xLoc += ((( getCheckerSize()) - text.getLayoutBounds().getWidth()) / 2d) - 1d;
							yLoc -= ((( getCheckerSize()) - text.getLayoutBounds().getHeight()) / 2d) + 1d;

							if (Character.isUpperCase(ch)) {
								graphics.setFill(Color.GRAY);

								graphics.fillText(str, xLoc + 1, yLoc + 1);
								graphics.setFill(Color.BEIGE);
								graphics.fillText(str, xLoc, yLoc);
							}
							else {
								graphics.setFill(Color.BLACK);
								graphics.fillText(str, xLoc, yLoc);
							}
						}
					}
				}
			}
		};
	}

	public AnimationTimer getAnimationTimer() {
		return animationTimer;
	}

	public void setChessGame(ChessGame chessGame) {
		this.chessGame = chessGame;
	}

	public ChessGame getChessGame() {
		return chessGame;
	}

	public double getBoardSize(){
		return this.getWidth();
	}

	public double getCheckerSize() {
		return (getBoardSize() / 8d);
	}
}
