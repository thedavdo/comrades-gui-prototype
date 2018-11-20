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

				double boardSize = FXChessBoard.this.getWidth();

				GraphicsContext graphics = FXChessBoard.this.getGraphicsContext2D();
				graphics.clearRect(0, 0, boardSize, boardSize);

				ChessCell[][] parseFEN = chessGame.getCells();
				double checkerSize = FXChessBoard.this.getCheckerSize();

				for(int x = 7; x >= 0; x--) {
					for(int y = 7; y >= 0; y--) {

						if((x + y) % 2 == 0)
							graphics.setFill(Color.WHITE);
						else
							graphics.setFill(Color.LIGHTGRAY);

						graphics.fillRect(x * checkerSize, y * checkerSize, checkerSize, checkerSize);

						if(!chessGame.isGamePaused()) {

							ChessCell selCell = parseFEN[x][y];

							if (selCell != null) {

								ChessPiece selPiece = selCell.getChessPiece();

								if (selPiece != null) {

									Character selChar = selPiece.getPieceChar();

									if (selChar != null) {

										Font f = fontChess;//Font.font("Consolas", FontWeight.BOLD, 22);

										String str = "" + selChar;

										Text text = new Text(str);
										text.setFont(f);

										double xLoc = (x) * checkerSize;
										double yLoc = checkerSize + (y) * checkerSize;

										xLoc += (((checkerSize) - text.getLayoutBounds().getWidth()) / 2d) - 1d;
										yLoc -= (((checkerSize) - text.getLayoutBounds().getHeight()) / 2d) + 1d;

										graphics.setFont(f);

										if (Character.isUpperCase(selChar)) {
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
