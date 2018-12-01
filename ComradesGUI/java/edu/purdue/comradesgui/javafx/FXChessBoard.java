package edu.purdue.comradesgui.javafx;

import edu.purdue.comradesgui.src.ChessCell;
import edu.purdue.comradesgui.src.ChessGame;
import edu.purdue.comradesgui.src.ChessPiece;
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

	private Color oddTileColor, evenTileColor;
	private Font boardFont;

	private boolean fillPieces, framePieces;

	private double boardSize;

	public FXChessBoard(double containerSize, ChessGame chessGame) {
		super(containerSize, containerSize);
		this.chessGame = chessGame;

		boardSize = containerSize - 30;

		try {
			FileInputStream fileInputStream = new FileInputStream(new File("MERIFONT.TTF"));
			boardFont = Font.loadFont(fileInputStream, getCheckerSize() - 4);
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}

		evenTileColor = new Color(1, 205d/255d, 160d/255d, 1);
		oddTileColor = new Color(209d/255d, 140d/255d, 70d/255d, 1);

		fillPieces = true;
		framePieces = false;

		animationTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				drawChessBoard();
			}
		};
	}

	private void drawChessBoard() {

		GraphicsContext graphics = getGraphicsContext2D();
		graphics.clearRect(0, 0, getWidth(), getWidth());

		ChessCell[][] cells = chessGame.getCells();

		for(int row = 0; row < 8; row++) {

			double yLoc = getBoardPosY() + ((8 - row) * getCheckerSize());
			for(int col = 0; col < 8; col++) {

				double xLoc = getBoardPosX() + (col * getCheckerSize());

				if((col + row) % 2 == 0)
					graphics.setFill(evenTileColor);
				else
					graphics.setFill(oddTileColor);

				graphics.fillRect(xLoc, yLoc - getCheckerSize(), getCheckerSize(), getCheckerSize());

				ChessCell cell = cells[col][row];

				if(cell != null)
					if(cell.getChessPiece() != null) {
						Character ch = cell.getChessPiece().getPieceChar();

						graphics.setFont(boardFont);
						String str = "" + getSkinnedPiece(ch);

						Text text = new Text(str);
						text.setFont(boardFont);

						double pieceX = xLoc + (((getCheckerSize()) - text.getLayoutBounds().getWidth()) / 2d);
						double pieceY = yLoc - (((getCheckerSize()) - text.getLayoutBounds().getHeight()) / 2d);

						if (Character.isUpperCase(ch)) {
							graphics.setFill(Color.GRAY);

							graphics.fillText(str, pieceX + 1, pieceY + 1);
							graphics.setFill(Color.BEIGE);
							graphics.fillText(str, pieceX, pieceY);
						}
						else {
							graphics.setFill(Color.BEIGE);
							graphics.fillText(str, pieceX - 1, pieceY - 1);
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
			double rowX = getBoardSize() + getBoardPosX() + text.getLayoutBounds().getWidth() + 2;
			double rowY = yLoc - (((getCheckerSize()) - text.getLayoutBounds().getHeight()) / 2d);

			graphics.fillText(rowNum, rowX, rowY);

			String colNum = "" + String.valueOf((char) (row + 97));

			text.setText(colNum);

			double xLoc = getBoardPosX() + ((row + 1) * getCheckerSize());
			double colX = xLoc - ((getCheckerSize() + text.getLayoutBounds().getWidth()) / 2d);
			double colY = getBoardPosY() + getBoardSize() + text.getLayoutBounds().getHeight() + 2;

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

	public Color getOddTileColor() {
		return oddTileColor;
	}

	public Color getEvenTileColor() {
		return evenTileColor;
	}

	public void setOddTileColor(Color colorIn) {
		if(colorIn != null)
			this.oddTileColor = colorIn;
	}

	public Character getSkinnedPiece(Character inChar) {

		Character outChar = Character.toLowerCase(inChar);

		if(fillPieces) {
			if(outChar == 'q')
				outChar = 'w';
			else if(outChar == 'r')
				outChar = 't';
			else if(outChar == 'p')
				outChar = 'o';
			else if(outChar == 'k')
				outChar = 'l';
			else if(outChar == 'b')
				outChar = 'v';
			else if(outChar == 'n')
				outChar = 'm';
		}

		if(framePieces)
			outChar = Character.toUpperCase(outChar);

		return outChar;
	}

	public void setEvenTileColor(Color colorIn) {
		if(colorIn != null)
			this.evenTileColor = colorIn;
	}

	public Font getBoardFont() {
		return boardFont;
	}

	public void setBoardFont(Font font) {
		if(font != null)
			this.boardFont = font;
	}

	public void setFillPieces(boolean fill) {
		fillPieces = fill;
	}

	public boolean getFillPieces() {
		return fillPieces;
	}

	public void setFramePieces(boolean frame) {
		framePieces = frame;
	}

	public boolean getFramePieces() {
		return framePieces;
	}

	public double getBoardPosX() {

		return 0;//(getContainerSize() - boardSize) / 2;
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
