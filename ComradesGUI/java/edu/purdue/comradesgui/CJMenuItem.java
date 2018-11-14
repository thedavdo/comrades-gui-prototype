package edu.purdue.comradesgui;

import javax.swing.*;

public class CJMenuItem extends JMenuItem {
	Communicator COMM;

	public CJMenuItem(Communicator C) {
		COMM = C;
		setText(C.id); // id
	}

}
