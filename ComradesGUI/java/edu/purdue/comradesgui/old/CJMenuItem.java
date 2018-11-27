package edu.purdue.comradesgui.old;

import javax.swing.*;

public class CJMenuItem extends JMenuItem {
	Communicator COMM;

	public CJMenuItem(Communicator C) {
		COMM = C;
		setText(C.id); // id
	}

}
