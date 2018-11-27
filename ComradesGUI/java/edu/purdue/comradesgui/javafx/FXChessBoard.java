package edu.purdue.comradesgui.javafx;

import edu.purdue.comradesgui.src.ChessCell;
import edu.purdue.comradesgui.src.ChessGame;
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

	private double boardSize;

	public FXChessBoard(double containerSize, ChessGame chessGame) {
		super(containerSize, containerSize);
		this.chessGame = chessGame;

		boardSize = containerSize * 0.94;

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
				FXChessBoard.this.drawChessBoard();
			}
		};
	}

	public void drawChessBoard() {

		GraphicsContext graphics = getGraphicsContext2D();
		graphics.clearRect(0, 0, getWidth(), getWidth());

		//graphics.setFill(Color.LIGHTGRAY);
		//graphics.fillRect(0, 0, getWidth(), getWidth());

		ChessCell[][] cells = chessGame.getCells();

		Color lightTile = new Color(1, 205d/255d, 160d/255d, 1);
		Color darkTile = new Color(209d/255d, 140d/255d, 70d/255d, 1);

		for(int row = 0; row < 8; row++) {

			double yLoc = getBoardPosY() + ((8 - row) * getCheckerSize());
			for(int col = 0; col < 8; col++) {

				double xLoc = getBoardPosX() + (col * getCheckerSize());


				if((col + row) % 2 == 0)
					graphics.setFill(lightTile);
				else
					graphics.setFill(darkTile);

				graphics.fillRect(xLoc, yLoc - getCheckerSize(), getCheckerSize(), getCheckerSize());

				ChessCell cell = cells[col][row];

				if(cell.getChessPiece() != null) {
					Character ch = cell.getChessPiece().getPieceChar();

					graphics.setFont(fontChess);
					String str = "" + ch;

					Text text = new Text(str);
					text.setFont(fontChess);

					double pieceX = xLoc + (((getCheckerSize()) - text.getLayoutBounds().getWidth()) / 2d);
					double pieceY = yLoc - (((getCheckerSize()) - text.getLayoutBounds().getHeight()) / 2d);

					if (Character.isUpperCase(ch)) {
						graphics.setFill(Color.GRAY);

						graphics.fillText(str, pieceX + 1, pieceY + 1);
						graphics.setFill(Color.BEIGE);
						graphics.fillText(str, pieceX, pieceY);
					}
					else {
						graphics.setFill(Color.BLACK);
						graphics.fillText(str, pieceX, pieceY);
					}
				}
			}

			Font gridFont = Font.font("Consolas", 18);
			Text text = new Text();
			text.setFont(gridFont);

			graphics.setFont(gridFont);
			graphics.setFill(Color.BLACK);

			String rowNum = "" + (row + 1);

			text.setText(rowNum);
			double rowX = getBoardPosX() - getCheckerSize()/3;
			double rowY = yLoc - (((getCheckerSize()) - text.getLayoutBounds().getHeight()) / 2d);

			graphics.fillText(rowNum, rowX, rowY);

			String colNum = "" + String.valueOf((char) (row + 97));

			text.setText(colNum);
			double colX = (getContainerSize() - yLoc) + (((getCheckerSize()) - text.getLayoutBounds().getWidth()) / 2d);
			double colY = getBoardPosY() + getBoardSize() + getCheckerSize()/3;

			graphics.fillText(colNum, colX, colY);
		}
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

	public double getBoardPosX() {

		return getContainerSize() - getBoardSize();//(getContainerSize() - boardSize) / 2;
	}

	public double getBoardPosY() {
		return 0;//(getContainerSize() - boardSize) / 2;
	}

	public double getBoardSize() {
		return boardSize;
	}

	public double getContainerSize(){
		return this.getWidth();
	}

	public double getCheckerSize() {
		return (boardSize / 8);
	}
}
