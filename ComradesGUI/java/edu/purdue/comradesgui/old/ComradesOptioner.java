package edu.purdue.comradesgui.old;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ComradesOptioner implements ActionListener {
	ComradesFrame CF;
	JDialog DIALOG;
	ActionListener AL_ColorMain, AL_ColorSmall;
	File FILE = null;
	Color COPY_WHITE_PIECES, COPY_BLACK_PIECES, COPY_LIGHT_SQUARES, COPY_DARK_SQUARES;
	Color COPY_BACK_GROUND;
	Color COPY_CASTLING_COLOR, COPY_MOVE_ARROWS, COPY_YELLOW_BUTTON, COPY_CLICKED_COLOR;
	Color COPY_WHITE_MOVE_INDICATOR, COPY_BLACK_MOVE_INDICATOR, COPY_EN_PASSANT_COLOR;
	boolean COPY_WHITE_ALL_OUTLINE, COPY_BLACK_ALL_OUTLINE;
	boolean COPY_WHITE_HALF_OUTLINE, COPY_BLACK_HALF_OUTLINE;
	boolean COPY_WHITE_MATERIAL_OUTLINE, COPY_BLACK_MATERIAL_OUTLINE;
	Color COPY_SMALL_WHITE_PIECES, COPY_SMALL_BLACK_PIECES, COPY_SMALL_LIGHT_SQUARES, COPY_SMALL_DARK_SQUARES;
	Color COPY_SMALL_BACK_GROUND, COPY_SMALL_WHITE_MOVE_INDICATOR, COPY_SMALL_BLACK_MOVE_INDICATOR,
			COPY_SMALL_EN_PASSANT_COLOR;
	Color COPY_LAST_MOVE_COLOR;
	boolean COPY_SMALL_WHITE_ALL_OUTLINE, COPY_SMALL_BLACK_ALL_OUTLINE;
	boolean COPY_SMALL_WHITE_HALF_OUTLINE, COPY_SMALL_BLACK_HALF_OUTLINE;
	boolean COPY_SMALL_WHITE_MATERIAL_OUTLINE, COPY_SMALL_BLACK_MATERIAL_OUTLINE;
	Font COPY_FONT;
	String COPY_FONT_FILE_NAME, COPY_PIECE_STRING;
	String COPY_INITIAL_TIMER;
	String COPY_INITIAL_INCREMENT;

	public ComradesOptioner(ComradesFrame cf) {
		super();
		CF = cf;
		AL_ColorMain = ColorMainActionListener();
		AL_ColorSmall = ColorSmallActionListener();
		OptionDialog();
	}

	public ActionListener ColorMainActionListener() {

		ActionListener AL = new ActionListener() {
			public void actionPerformed(ActionEvent act_evt) {

				String actionString = act_evt.getActionCommand();

				if (actionString.equals("PieceFullOutline")) {
					JComboBox comboBox = (JComboBox) (act_evt.getSource());
					String comboBoxSelectedString = (String) (comboBox.getSelectedItem());
					if (comboBoxSelectedString.equals("None")) {
						CF.WHITE_ALL_OUTLINE = false;
						CF.BLACK_ALL_OUTLINE = false;
					}
					if (comboBoxSelectedString.equals("White")) {
						CF.WHITE_ALL_OUTLINE = true;
						CF.BLACK_ALL_OUTLINE = false;
					}
					if (comboBoxSelectedString.equals("Black")) {
						CF.WHITE_ALL_OUTLINE = false;
						CF.BLACK_ALL_OUTLINE = true;
					}
					if (comboBoxSelectedString.equals("Both")) {
						CF.WHITE_ALL_OUTLINE = true;
						CF.BLACK_ALL_OUTLINE = true;
					}
					ReDrawMainBoard();
					return;
				}
				if (actionString.equals("PieceHalfOutline")) {
					JComboBox comboBox = (JComboBox) (act_evt.getSource());
					String comboBoxSelectedString = (String) (comboBox.getSelectedItem());
					if (comboBoxSelectedString.equals("None")) {
						CF.WHITE_HALF_OUTLINE = false;
						CF.BLACK_HALF_OUTLINE = false;
					}
					if (comboBoxSelectedString.equals("White")) {
						CF.WHITE_HALF_OUTLINE = true;
						CF.BLACK_HALF_OUTLINE = false;
					}
					if (comboBoxSelectedString.equals("Black")) {
						CF.WHITE_HALF_OUTLINE = false;
						CF.BLACK_HALF_OUTLINE = true;
					}
					if (comboBoxSelectedString.equals("Both")) {
						CF.WHITE_HALF_OUTLINE = true;
						CF.BLACK_HALF_OUTLINE = true;
					}
					ReDrawMainBoard();
					return;
				}
				if (actionString.equals("MaterialOutline")) {
					JComboBox comboBox = (JComboBox) (act_evt.getSource());
					String comboBoxSelectedString = (String) (comboBox.getSelectedItem());
					if (comboBoxSelectedString.equals("None")) {
						CF.WHITE_MATERIAL_OUTLINE = false;
						CF.BLACK_MATERIAL_OUTLINE = false;
					}
					if (comboBoxSelectedString.equals("White")) {
						CF.WHITE_MATERIAL_OUTLINE = true;
						CF.BLACK_MATERIAL_OUTLINE = false;
					}
					if (comboBoxSelectedString.equals("Black")) {
						CF.WHITE_MATERIAL_OUTLINE = false;
						CF.BLACK_MATERIAL_OUTLINE = true;
					}
					if (comboBoxSelectedString.equals("Both")) {
						CF.WHITE_MATERIAL_OUTLINE = true;
						CF.BLACK_MATERIAL_OUTLINE = true;
					}
					ReDrawMainBoard();
					return;
				}
				JButton buttonColorChooser = (JButton) (act_evt.getSource());
				Color colorChosen = JColorChooser.showDialog(DIALOG, "Choose Color", buttonColorChooser.getBackground());
				if (colorChosen == null)
					return;
				buttonColorChooser.setBackground(colorChosen);
				buttonColorChooser.repaint();
				if (actionString.equals("WhitePieces"))
					CF.WHITE_PIECES = colorChosen;
				if (actionString.equals("WhiteMoveIndicator"))
					CF.WHITE_MOVE_INDICATOR = colorChosen;
				if (actionString.equals("BlackPieces"))
					CF.BLACK_PIECES = colorChosen;
				if (actionString.equals("BlackMoveIndicator"))
					CF.BLACK_MOVE_INDICATOR = colorChosen;
				if (actionString.equals("LightSquares"))
					CF.LIGHT_SQUARES = colorChosen;
				if (actionString.equals("BackGround"))
					CF.BACK_GROUND = colorChosen;
				if (actionString.equals("DarkSquares"))
					CF.DARK_SQUARES = colorChosen;
				if (actionString.equals("EnPassantColor"))
					CF.EN_PASSANT_COLOR = colorChosen;
				if (actionString.equals("YellowButton"))
					CF.YELLOW_BUTTON = colorChosen;
				if (actionString.equals("CastlingColor"))
					CF.CASTLING_COLOR = colorChosen;
				if (actionString.equals("MoveArrows"))
					CF.MOVE_ARROWS = colorChosen;
				if (actionString.equals("ClickedColor"))
					CF.CLICKED_COLOR = colorChosen;
				if (actionString.equals("LastMoveColor"))
					CF.LAST_MOVE_COLOR = colorChosen;
				ReDrawMainBoard();
			}
		};
		return AL;
	}

	public ActionListener ColorSmallActionListener() {
		ActionListener AL = new ActionListener() {
			public void actionPerformed(ActionEvent act_evt) {
				String actionString = act_evt.getActionCommand();
				if (actionString.equals("PieceFullOutline")) {
					JComboBox comboBox = (JComboBox) (act_evt.getSource());
					String comboBoxSelectedString = (String) (comboBox.getSelectedItem());
					if (comboBoxSelectedString.equals("None")) {
						CF.SMALL_WHITE_ALL_OUTLINE = false;
						CF.SMALL_BLACK_ALL_OUTLINE = false;
					}
					if (comboBoxSelectedString.equals("White")) {
						CF.SMALL_WHITE_ALL_OUTLINE = true;
						CF.SMALL_BLACK_ALL_OUTLINE = false;
					}
					if (comboBoxSelectedString.equals("Black")) {
						CF.SMALL_WHITE_ALL_OUTLINE = false;
						CF.SMALL_BLACK_ALL_OUTLINE = true;
					}
					if (comboBoxSelectedString.equals("Both")) {
						CF.SMALL_WHITE_ALL_OUTLINE = true;
						CF.SMALL_BLACK_ALL_OUTLINE = true;
					}
					ReDrawInstances();
					return;
				}
				if (actionString.equals("PieceHalfOutline")) {
					JComboBox comboBox = (JComboBox) (act_evt.getSource());
					String comboBoxSelectedString = (String) (comboBox.getSelectedItem());
					if (comboBoxSelectedString.equals("None")) {
						CF.SMALL_WHITE_HALF_OUTLINE = false;
						CF.SMALL_BLACK_HALF_OUTLINE = false;
					}
					if (comboBoxSelectedString.equals("White")) {
						CF.SMALL_WHITE_HALF_OUTLINE = true;
						CF.SMALL_BLACK_HALF_OUTLINE = false;
					}
					if (comboBoxSelectedString.equals("Black")) {
						CF.SMALL_WHITE_HALF_OUTLINE = false;
						CF.SMALL_BLACK_HALF_OUTLINE = true;
					}
					if (comboBoxSelectedString.equals("Both")) {
						CF.SMALL_WHITE_HALF_OUTLINE = true;
						CF.SMALL_BLACK_HALF_OUTLINE = true;
					}
					ReDrawInstances();
					return;
				}
				if (actionString.equals("MaterialOutline")) {
					JComboBox comboBox = (JComboBox) (act_evt.getSource());
					String comboBoxSelectedString = (String) (comboBox.getSelectedItem());
					if (comboBoxSelectedString.equals("None")) {
						CF.SMALL_WHITE_MATERIAL_OUTLINE = false;
						CF.SMALL_BLACK_MATERIAL_OUTLINE = false;
					}
					if (comboBoxSelectedString.equals("White")) {
						CF.SMALL_WHITE_MATERIAL_OUTLINE = true;
						CF.SMALL_BLACK_MATERIAL_OUTLINE = false;
					}
					if (comboBoxSelectedString.equals("Black")) {
						CF.SMALL_WHITE_MATERIAL_OUTLINE = false;
						CF.SMALL_BLACK_MATERIAL_OUTLINE = true;
					}
					if (comboBoxSelectedString.equals("Both")) {
						CF.SMALL_WHITE_MATERIAL_OUTLINE = true;
						CF.SMALL_BLACK_MATERIAL_OUTLINE = true;
					}
					ReDrawInstances();
					return;
				}
				JButton chooseColorButton = (JButton) (act_evt.getSource());
				Color chosenColor = JColorChooser.showDialog(DIALOG, "Choose Color", chooseColorButton.getBackground());
				if (chosenColor == null)
					return;
				chooseColorButton.setBackground(chosenColor);
				chooseColorButton.repaint();
				if (actionString.equals("WhitePieces"))
					CF.SMALL_WHITE_PIECES = chosenColor;
				if (actionString.equals("WhiteMoveIndicator"))
					CF.SMALL_WHITE_MOVE_INDICATOR = chosenColor;
				if (actionString.equals("BlackPieces"))
					CF.SMALL_BLACK_PIECES = chosenColor;
				if (actionString.equals("BlackMoveIndicator"))
					CF.SMALL_BLACK_MOVE_INDICATOR = chosenColor;
				if (actionString.equals("LightSquares"))
					CF.SMALL_LIGHT_SQUARES = chosenColor;
				if (actionString.equals("BackGround"))
					CF.SMALL_BACK_GROUND = chosenColor;
				if (actionString.equals("DarkSquares"))
					CF.SMALL_DARK_SQUARES = chosenColor;
				ReDrawInstances();
			}
		};
		return AL;
	}

	public void ReDrawMainBoard() {
		CF.BOARD_PANEL.ImportColors();
		CF.redraw();
	}

	public void ReDrawInstances() {
		for (int i = 0; i < CF.instances; i++)
			CF.INSTANCES[i].instancePanel.SetColors();
		for (int i = 0; i < CF.instances; i++)
			CF.INSTANCES[i].instancePanel.repaint();
	}

	public void CopyOnEntry() {
		COPY_FONT = CF.chess_font;
		COPY_FONT_FILE_NAME = new String(CF.FONT_FILE_NAME);
		COPY_WHITE_PIECES = CF.WHITE_PIECES;
		COPY_BLACK_PIECES = CF.BLACK_PIECES;
		COPY_LIGHT_SQUARES = CF.LIGHT_SQUARES;
		COPY_DARK_SQUARES = CF.DARK_SQUARES;
		COPY_BACK_GROUND = CF.BACK_GROUND;
		COPY_CASTLING_COLOR = CF.CASTLING_COLOR;
		COPY_MOVE_ARROWS = CF.MOVE_ARROWS;
		COPY_YELLOW_BUTTON = CF.YELLOW_BUTTON;
		COPY_CLICKED_COLOR = CF.CLICKED_COLOR;
		COPY_WHITE_MOVE_INDICATOR = CF.WHITE_MOVE_INDICATOR;
		COPY_BLACK_MOVE_INDICATOR = CF.BLACK_MOVE_INDICATOR;
		COPY_LAST_MOVE_COLOR = CF.LAST_MOVE_COLOR;
		COPY_EN_PASSANT_COLOR = CF.EN_PASSANT_COLOR;
		COPY_WHITE_ALL_OUTLINE = CF.WHITE_ALL_OUTLINE;
		COPY_BLACK_ALL_OUTLINE = CF.BLACK_ALL_OUTLINE;
		COPY_WHITE_HALF_OUTLINE = CF.WHITE_HALF_OUTLINE;
		COPY_BLACK_HALF_OUTLINE = CF.BLACK_HALF_OUTLINE;
		COPY_WHITE_MATERIAL_OUTLINE = CF.WHITE_MATERIAL_OUTLINE;
		COPY_BLACK_MATERIAL_OUTLINE = CF.BLACK_MATERIAL_OUTLINE;
		COPY_SMALL_WHITE_PIECES = CF.SMALL_WHITE_PIECES;
		COPY_SMALL_BLACK_PIECES = CF.SMALL_BLACK_PIECES;
		COPY_SMALL_LIGHT_SQUARES = CF.SMALL_LIGHT_SQUARES;
		COPY_SMALL_DARK_SQUARES = CF.SMALL_DARK_SQUARES;
		COPY_SMALL_BACK_GROUND = CF.SMALL_BACK_GROUND;
		COPY_SMALL_WHITE_MOVE_INDICATOR = CF.SMALL_WHITE_MOVE_INDICATOR;
		COPY_SMALL_BLACK_MOVE_INDICATOR = CF.SMALL_BLACK_MOVE_INDICATOR;
		COPY_SMALL_EN_PASSANT_COLOR = CF.SMALL_EN_PASSANT_COLOR;
		COPY_SMALL_WHITE_ALL_OUTLINE = CF.SMALL_WHITE_ALL_OUTLINE;
		COPY_SMALL_BLACK_ALL_OUTLINE = CF.SMALL_BLACK_ALL_OUTLINE;
		COPY_SMALL_WHITE_HALF_OUTLINE = CF.SMALL_WHITE_HALF_OUTLINE;
		COPY_SMALL_BLACK_HALF_OUTLINE = CF.SMALL_BLACK_HALF_OUTLINE;
		COPY_SMALL_WHITE_MATERIAL_OUTLINE = CF.SMALL_WHITE_MATERIAL_OUTLINE;
		COPY_SMALL_BLACK_MATERIAL_OUTLINE = CF.SMALL_BLACK_MATERIAL_OUTLINE;
		COPY_PIECE_STRING = new String(CF.PIECE_STRING);
		COPY_INITIAL_TIMER = new String(CF.INITIAL_TIMER);
		COPY_INITIAL_INCREMENT = new String(CF.INITIAL_INCREMENT);

	}

	public void UnDoChanges() {
		CF.WHITE_PIECES = COPY_WHITE_PIECES;
		CF.BLACK_PIECES = COPY_BLACK_PIECES;
		CF.LIGHT_SQUARES = COPY_LIGHT_SQUARES;
		CF.DARK_SQUARES = COPY_DARK_SQUARES;
		CF.BACK_GROUND = COPY_BACK_GROUND;
		CF.CASTLING_COLOR = COPY_CASTLING_COLOR;
		CF.MOVE_ARROWS = COPY_MOVE_ARROWS;
		CF.YELLOW_BUTTON = COPY_YELLOW_BUTTON;
		CF.CLICKED_COLOR = COPY_CLICKED_COLOR;
		CF.LAST_MOVE_COLOR = COPY_LAST_MOVE_COLOR;
		CF.WHITE_MOVE_INDICATOR = COPY_WHITE_MOVE_INDICATOR;
		CF.BLACK_MOVE_INDICATOR = COPY_BLACK_MOVE_INDICATOR;
		CF.EN_PASSANT_COLOR = COPY_EN_PASSANT_COLOR;
		CF.WHITE_ALL_OUTLINE = COPY_WHITE_ALL_OUTLINE;
		CF.BLACK_ALL_OUTLINE = COPY_BLACK_ALL_OUTLINE;
		CF.WHITE_HALF_OUTLINE = COPY_WHITE_HALF_OUTLINE;
		CF.BLACK_HALF_OUTLINE = COPY_BLACK_HALF_OUTLINE;
		CF.WHITE_MATERIAL_OUTLINE = COPY_WHITE_MATERIAL_OUTLINE;
		CF.BLACK_MATERIAL_OUTLINE = COPY_BLACK_MATERIAL_OUTLINE;
		CF.SMALL_WHITE_PIECES = COPY_SMALL_WHITE_PIECES;
		CF.SMALL_BLACK_PIECES = COPY_SMALL_BLACK_PIECES;
		CF.SMALL_LIGHT_SQUARES = COPY_SMALL_LIGHT_SQUARES;
		CF.SMALL_DARK_SQUARES = COPY_SMALL_DARK_SQUARES;
		CF.SMALL_BACK_GROUND = COPY_SMALL_BACK_GROUND;
		CF.SMALL_WHITE_MOVE_INDICATOR = COPY_SMALL_WHITE_MOVE_INDICATOR;
		CF.SMALL_BLACK_MOVE_INDICATOR = COPY_SMALL_BLACK_MOVE_INDICATOR;
		CF.SMALL_EN_PASSANT_COLOR = COPY_SMALL_EN_PASSANT_COLOR;
		CF.SMALL_WHITE_ALL_OUTLINE = COPY_SMALL_WHITE_ALL_OUTLINE;
		CF.SMALL_BLACK_ALL_OUTLINE = COPY_SMALL_BLACK_ALL_OUTLINE;
		CF.SMALL_WHITE_HALF_OUTLINE = COPY_SMALL_WHITE_HALF_OUTLINE;
		CF.SMALL_BLACK_HALF_OUTLINE = COPY_SMALL_BLACK_HALF_OUTLINE;
		CF.SMALL_WHITE_MATERIAL_OUTLINE = COPY_SMALL_WHITE_MATERIAL_OUTLINE;
		CF.SMALL_BLACK_MATERIAL_OUTLINE = COPY_SMALL_BLACK_MATERIAL_OUTLINE;
		CF.FONT_FILE_NAME = new String(COPY_FONT_FILE_NAME);
		CF.PIECE_STRING = new String(COPY_PIECE_STRING);
		CF.chess_font = COPY_FONT;
		CF.chess_font_small = COPY_FONT.deriveFont(16.0f);
		CF.INITIAL_TIMER = new String(COPY_INITIAL_TIMER);
		CF.INITIAL_INCREMENT = new String(COPY_INITIAL_INCREMENT);
	}

	public void ColorPair(String S, Color C, PrintWriter PW) // utile
	{
		PW.println(S + " " + C.getRGB());
	}

	public void BooleanPair(String S, boolean b, PrintWriter PW) // utile
	{
		PW.println(S + " " + b);
	}

	public void SaveAsDefault() {
		try {
			FileWriter FW = new FileWriter("Comrades.Default.Options");
			BufferedWriter BW = new BufferedWriter(FW);
			PrintWriter PW = new PrintWriter(BW, true);
			ColorPair("WhitePieces", CF.WHITE_PIECES, PW);
			ColorPair("BlackPieces", CF.BLACK_PIECES, PW);
			ColorPair("LightSquares", CF.LIGHT_SQUARES, PW);
			ColorPair("DarkSquares", CF.DARK_SQUARES, PW);
			ColorPair("BackGround", CF.BACK_GROUND, PW);
			ColorPair("CastlingColor", CF.CASTLING_COLOR, PW);
			ColorPair("MoveArrows", CF.MOVE_ARROWS, PW);
			ColorPair("YellowButton", CF.YELLOW_BUTTON, PW);
			ColorPair("ClickedColor", CF.CLICKED_COLOR, PW);
			ColorPair("WhiteMoveIndicator", CF.WHITE_MOVE_INDICATOR, PW);
			ColorPair("BlackMoveIndicator", CF.BLACK_MOVE_INDICATOR, PW);
			ColorPair("EnPassantColor", CF.EN_PASSANT_COLOR, PW);
			ColorPair("LastMoveColor", CF.LAST_MOVE_COLOR, PW);
			BooleanPair("WhiteAllOutline", CF.WHITE_ALL_OUTLINE, PW);
			BooleanPair("BlackAllOutline", CF.BLACK_ALL_OUTLINE, PW);
			BooleanPair("WhiteHalfOutline", CF.WHITE_HALF_OUTLINE, PW);
			BooleanPair("BlackHalfOutline", CF.BLACK_HALF_OUTLINE, PW);
			BooleanPair("WhiteMaterialOutline", CF.WHITE_MATERIAL_OUTLINE, PW);
			BooleanPair("BlackMaterialOutline", CF.BLACK_MATERIAL_OUTLINE, PW);
			ColorPair("SmallWhitePieces", CF.SMALL_WHITE_PIECES, PW);
			ColorPair("SmallBlackPieces", CF.SMALL_BLACK_PIECES, PW);
			ColorPair("SmallLightSquares", CF.SMALL_LIGHT_SQUARES, PW);
			ColorPair("SmallDarkSquares", CF.SMALL_DARK_SQUARES, PW);
			ColorPair("SmallBackGround", CF.SMALL_BACK_GROUND, PW);
			ColorPair("SmallEnPassantColor", CF.SMALL_EN_PASSANT_COLOR, PW);
			ColorPair("SmallWhiteMoveIndicator", CF.SMALL_WHITE_MOVE_INDICATOR, PW);
			ColorPair("SmallBlackMoveIndicator", CF.SMALL_BLACK_MOVE_INDICATOR, PW);
			BooleanPair("SmallWhiteAllOutline", CF.SMALL_WHITE_ALL_OUTLINE, PW);
			BooleanPair("SmallBlackAllOutline", CF.SMALL_BLACK_ALL_OUTLINE, PW);
			BooleanPair("SmallWhiteHalfOutline", CF.SMALL_WHITE_HALF_OUTLINE, PW);
			BooleanPair("SmallBlackHalfOutline", CF.SMALL_BLACK_HALF_OUTLINE, PW);
			BooleanPair("SmallWhiteMaterialOutline", CF.SMALL_WHITE_MATERIAL_OUTLINE, PW);
			BooleanPair("SmallBlackMaterialOutline", CF.SMALL_BLACK_MATERIAL_OUTLINE, PW);
			PW.println("FontFile " + CF.FONT_FILE_NAME);
			PW.println("PieceString " + CF.PIECE_STRING);
			PW.println("InitialTimer " + CF.INITIAL_TIMER);
			PW.println("InitialIncrement " + CF.INITIAL_INCREMENT);
			PW.close();
			CF.TellInfo("Applied to Comrades.Default.Options");
		}
		catch (IOException io_exc) {
			CF.TellInfo("Error for Comrades.Default.Options");
		}

	}

	public void actionPerformed(ActionEvent act_evt) {
		String actionString = act_evt.getActionCommand();
		if (actionString.equals("OK")) {
			DIALOG.setVisible(false);
			DIALOG.dispose();
			DIALOG = null;
		}
		else if (actionString.equals("Save as Default")) {
			DIALOG.setVisible(false);
			DIALOG.dispose();
			SaveAsDefault();
			DIALOG = null;
		}
		else if (actionString.equals("Cancel")) {
			DIALOG.setVisible(false);
			DIALOG.dispose();
			UnDoChanges();
			ReDrawMainBoard();
			ReDrawInstances();
			DIALOG = null;
		}
	}

	public JPanel ColorMe(String S, Color C, String T) {
		JPanel P = new JPanel();
		P.setLayout(null);
		JLabel L = new JLabel(S + " ");
		L.setBounds(2, 3, 300, 20);
		L.setFont(new Font("SansSerif", 0, 15));
		P.add(L);
		JButton J = new JButton("");
		J.setMargin(new Insets(4, 4, 4, 4));
		J.setBounds(160, 5, 20, 20);
		J.setActionCommand(S);
		if (T.equals("Main"))
			J.addActionListener(AL_ColorMain);
		if (T.equals("Small"))
			J.addActionListener(AL_ColorSmall);
		J.setBackground(C);
		P.add(J);
		P.setAlignmentX(0.0f);
		return P;
	}

	public Box OutLineBox(String S, String T, int n) {
		Box B = new Box(BoxLayout.X_AXIS);
		JLabel L = new JLabel(S + " ");
		L.setMinimumSize(new Dimension(120, 20));
		L.setMaximumSize(new Dimension(120, 20));
		L.setPreferredSize(new Dimension(120, 20));
		B.add(L);
		JComboBox C = new JComboBox();
		C.addItem("None");
		C.addItem("White");
		C.addItem("Black");
		C.addItem("Both");
		C.setActionCommand(S);
		if (T.equals("Main"))
			C.addActionListener(AL_ColorMain);
		if (T.equals("Small"))
			C.addActionListener(AL_ColorSmall);
		C.setSelectedIndex(n);
		C.setMaximumSize(new Dimension(80, 20));
		B.add(C);
		B.setAlignmentX(0.0f);
		return B;
	}

	public JPanel MainColorBox() {
		JPanel P = new JPanel();
		P.setLayout(new GridLayout(0, 2));
		P.add(ColorMe("WhitePieces", CF.WHITE_PIECES, "Main"));
		P.add(ColorMe("WhiteMoveIndicator", CF.WHITE_MOVE_INDICATOR, "Main"));
		P.add(ColorMe("BlackPieces", CF.BLACK_PIECES, "Main"));
		P.add(ColorMe("BlackMoveIndicator", CF.BLACK_MOVE_INDICATOR, "Main"));
		P.add(ColorMe("LightSquares", CF.LIGHT_SQUARES, "Main"));
		P.add(ColorMe("BackGround", CF.BACK_GROUND, "Main"));
		P.add(ColorMe("LastMoveColor", CF.LAST_MOVE_COLOR, "Main"));
		P.add(ColorMe("DarkSquares", CF.DARK_SQUARES, "Main"));
		P.add(ColorMe("EnPassantColor", CF.EN_PASSANT_COLOR, "Main"));
		P.add(ColorMe("YellowButton", CF.YELLOW_BUTTON, "Main"));
		P.add(ColorMe("CastlingArrows", CF.CASTLING_COLOR, "Main"));
		P.add(OutLineBox("PieceFullOutline", "Main", (CF.WHITE_ALL_OUTLINE ? 1 : 0) + (CF.BLACK_ALL_OUTLINE ? 2 : 0)));
		P.add(ColorMe("MoveArrows", CF.MOVE_ARROWS, "Main"));
		P.add(OutLineBox("PieceHalfOutline", "Main",
				(CF.WHITE_HALF_OUTLINE ? 1 : 0) + (CF.BLACK_HALF_OUTLINE ? 2 : 0)));
		P.add(ColorMe("ClickedColor", CF.CLICKED_COLOR, "Main"));
		P.add(OutLineBox("MaterialOutline", "Main",
				(CF.WHITE_MATERIAL_OUTLINE ? 1 : 0) + (CF.BLACK_MATERIAL_OUTLINE ? 2 : 0)));
		P.setAlignmentX(0.0f);
		return P;
	}

	public JPanel SmallColorBox() {
		JPanel P = new JPanel();
		P.setLayout(new GridLayout(0, 1));
		P.add(ColorMe("WhitePieces", CF.SMALL_WHITE_PIECES, "Small"));
		P.add(ColorMe("BlackPieces", CF.SMALL_BLACK_PIECES, "Small"));
		P.add(ColorMe("LightSquares", CF.SMALL_LIGHT_SQUARES, "Small"));
		P.add(ColorMe("DarkSquares", CF.SMALL_DARK_SQUARES, "Small"));
		P.add(ColorMe("WhiteMoveIndicator", CF.SMALL_WHITE_MOVE_INDICATOR, "Small"));
		P.add(ColorMe("BlackMoveIndicator", CF.SMALL_BLACK_MOVE_INDICATOR, "Small"));
		P.add(ColorMe("BackGround", CF.SMALL_BACK_GROUND, "Small"));
		P.add(ColorMe("EnPassantColor", CF.SMALL_EN_PASSANT_COLOR, "Small"));
		P.add(OutLineBox("PieceFullOutline", "Small",
				(CF.SMALL_WHITE_ALL_OUTLINE ? 1 : 0) + (CF.SMALL_BLACK_ALL_OUTLINE ? 2 : 0)));
		P.add(OutLineBox("PieceHalfOutline", "Small",
				(CF.SMALL_WHITE_HALF_OUTLINE ? 1 : 0) + (CF.SMALL_BLACK_HALF_OUTLINE ? 2 : 0)));
		P.add(OutLineBox("MaterialOutline", "Small",
				(CF.SMALL_WHITE_MATERIAL_OUTLINE ? 1 : 0) + (CF.SMALL_BLACK_MATERIAL_OUTLINE ? 2 : 0)));
		P.setAlignmentX(0.0f);
		return P;
	}

	public Box DoFontBox() {
		Box B = new Box(BoxLayout.Y_AXIS);
		JButton J = new JButton("Change Chess Font");
		J.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent act_evt) {
				JFileChooser JFC;
				if (FILE == null)
					JFC = new JFileChooser(System.getProperty("user.dir"));
				else
					JFC = new JFileChooser(FILE);
				int Value = JFC.showOpenDialog(JFC);
				if (Value != JFileChooser.APPROVE_OPTION)
					return;
				FILE = JFC.getSelectedFile();
				if (!FILE.exists())
					return;
				CF.InitChessFont(FILE.getAbsolutePath(), 40); // void
				ReDrawMainBoard();
				ReDrawInstances();
			}
		});
		B.add(J);
		return B;
	}

	/**
	 * This method creates a Panel component to hold the timer options components
	 *
	 * @return A Panel component containing the timer options
	 */
	public Box DoTimerBox() {
		// Create Main box
		Box B = new Box(BoxLayout.Y_AXIS);
		B.setAlignmentX(0.0f);

		// Create box for current line
		Box lineBox = new Box(BoxLayout.X_AXIS);
		lineBox.setAlignmentX(0.0f);

		// Add Label to current line
		JLabel L = new JLabel("Timer start time: ");
		L.setMinimumSize(new Dimension(100, 20));
		L.setMaximumSize(new Dimension(100, 20));
		L.setPreferredSize(new Dimension(100, 20));
		lineBox.add(L);

		// Add formatted text box to current line for time
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		JFormattedTextField T = new JFormattedTextField(df);
		try {
			T.setValue(df.parse(CF.INITIAL_TIMER));
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			T.setValue(5000);
		}
		T.setColumns(8);
		T.setMaximumSize(new Dimension(80, 20));

		T.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JTextField text = (JTextField) evt.getSource();
				CF.INITIAL_TIMER = text.getText();
			}
		});

		lineBox.add(T);


		// Create box for current line
		Box incrLineBox = new Box(BoxLayout.X_AXIS);
		incrLineBox.setAlignmentX(0.0f);

		// Add Label to current line
		JLabel incrL = new JLabel("Increment Value: ");
		incrL.setMinimumSize(new Dimension(100, 20));
		incrL.setMaximumSize(new Dimension(100, 20));
		incrL.setPreferredSize(new Dimension(100, 20));
		incrLineBox.add(incrL);

		// Add formatted text box to current line for time
		JFormattedTextField incrT = new JFormattedTextField(df);

		try {
			incrT.setValue(df.parse(CF.INITIAL_INCREMENT));
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			incrT.setValue(3000);
		}

		incrT.setColumns(8);
		incrT.setMaximumSize(new Dimension(80, 20));

		incrT.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				JTextField text = (JTextField) evt.getSource();
				CF.INITIAL_INCREMENT = text.getText();
			}
		});

		incrLineBox.add(incrT);

		// Add current line to main box
		B.add(lineBox);
		B.add(incrLineBox);

		// Return main box
		return B;
	}

	public void MakeDialog() {
		JTabbedPane TABBED = new JTabbedPane();
		JTabbedPane COLOR_TAB = new JTabbedPane();
		TABBED.addTab("Font", DoFontBox());
		COLOR_TAB.addTab("MainBoard", MainColorBox());
		COLOR_TAB.addTab("SmallBoard", SmallColorBox());
		TABBED.addTab("Colors", COLOR_TAB);
		TABBED.addTab("Timer", DoTimerBox());
		Box OVER_BOX = new Box(BoxLayout.Y_AXIS);
		OVER_BOX.add(TABBED);
		Dimension D = new Dimension(5, 5);
		OVER_BOX.add(new Box.Filler(D, D, D));
		Box H = new Box(BoxLayout.X_AXIS);
		JButton J1 = new JButton("OK");
		J1.setActionCommand("OK");
		J1.addActionListener(this);
		H.add(J1);
		JButton J2 = new JButton("Cancel");
		J2.setActionCommand("Cancel");
		J2.addActionListener(this);
		H.add(J2);
		JButton J3 = new JButton("Save as Default");
		J3.setActionCommand("Save as Default");
		J3.addActionListener(this);
		H.add(J3);
		OVER_BOX.add(H);
		DIALOG.add(OVER_BOX);

	}

	public void OptionDialog() {
		DIALOG = new JDialog(CF.j_frame, "Comrades Options", true); // modal
		CopyOnEntry();
		MakeDialog();
		DIALOG.setBackground(Color.lightGray);
		DIALOG.pack();
		DIALOG.setSize(500, 410);
		DIALOG.setResizable(false);
		DIALOG.setVisible(true);
	}
}
