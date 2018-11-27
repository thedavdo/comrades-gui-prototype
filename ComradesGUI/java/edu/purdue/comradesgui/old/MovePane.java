package edu.purdue.comradesgui.old;

import javax.swing.*;
import java.awt.*;

public class MovePane extends JScrollPane {
	JPanel PANEL;

	public MovePane(JPanel panel) {
		super(panel);
		PANEL = panel;
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		PANEL.setPreferredSize(new Dimension(380, 6000));
		PANEL.setMinimumSize(new Dimension(380, 6000));
		PANEL.setMaximumSize(new Dimension(380, 6000));
		setPreferredSize(new Dimension(400, 250));
		setMinimumSize(new Dimension(400, 250));
		InputMap IM = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		IM.put(KeyStroke.getKeyStroke("UP"), "none");
		IM.put(KeyStroke.getKeyStroke("DOWN"), "none");
		IM.put(KeyStroke.getKeyStroke("LEFT"), "none");
		IM.put(KeyStroke.getKeyStroke("RIGHT"), "none");
		setFocusTraversalKeysEnabled(false); // value, for the plus!
		// IM.put (KeyStroke.getKeyStroke ("TAB"), "none");
	}

}
