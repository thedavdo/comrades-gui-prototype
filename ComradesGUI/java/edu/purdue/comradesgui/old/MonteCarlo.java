package edu.purdue.comradesgui.old;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class MonteCarlo implements ChangeListener, ActionListener {
	CommunicatorInstance commInst;
	JFrame MC_FRAME = null;
	boolean WORKING = false;
	boolean SPICEING = false;
	boolean BESTMOVE = false;
	JSpinner CPU_SPINNER, DEPTH_SPINNER, LENGTH_SPINNER;
	JSpinner VERBOSE_SPINNER, MAX_SPINNER, MIN_SPINNER;
	JButton ON_OFF, CLEAR, ALL_IN, ALL_OUT, SPICE;
	Object[][] DATA;
	Long[] EVAL;
	JTable TABLE;
	int[] SORT;
	int LEGAL_COUNT;
	long UP_DATE;
	int CENTI_PAWN_PARSE;
	ComradesFrame commFrame;
	ArrayList[] SCORES;

	public MonteCarlo(CommunicatorInstance ci) {
		super();
		commInst = ci;
		commFrame = commInst.frame;
		EVAL = new Long[256];
		SORT = new int[256];
		for (int i = 0; i < 256; i++)
			EVAL[i] = 0L;
		for (int i = 0; i < 256; i++)
			SORT[i] = i;
		LEGAL_COUNT = commInst.COMM.frame.BOARD_PANEL.POS.COUNT_OF_LEGAL_MOVES;
		UP_DATE = new Date().getTime();
		StartMonteCarlo();
	}

	public void GoMonteCarlo() {
		if (commInst.on)
			commInst.SendHalt();
		ON_OFF.setBackground(Color.green);
		ON_OFF.repaint();
		commInst.SendTo(commInst.GetFenMoves(), false);
		String S = "go montecarlo";
		S += " length " + LENGTH_SPINNER.getValue();
		S += " depth " + DEPTH_SPINNER.getValue();
		S += " cpus " + CPU_SPINNER.getValue();
		S += " verbose vlength " + VERBOSE_SPINNER.getValue();
		S += " max " + MAX_SPINNER.getValue();
		S += " min " + MIN_SPINNER.getValue();
		S += " moves";
		commFrame.MC_CPUS = (Integer) CPU_SPINNER.getValue();
		commFrame.MC_DEPTH = (Integer) DEPTH_SPINNER.getValue();
		commFrame.MC_VLEN = (Integer) VERBOSE_SPINNER.getValue();
		commFrame.MC_MAXV = (Integer) MAX_SPINNER.getValue();
		commFrame.MC_MINV = (Integer) MIN_SPINNER.getValue();
		commFrame.MC_LENGTH = (Integer) LENGTH_SPINNER.getValue();
		BoardPosition BP = commInst.COMM.frame.BOARD_PANEL.POS;
		for (int i = 0; i < BP.COUNT_OF_LEGAL_MOVES; i++)
			if ((Boolean) (DATA[i][0]))
				S += " " + BP.GetDirect(BP.move_list[i]);
		commInst.SendTo(S, true);
		WORKING = true;
	}

	public void HaltMonteCarlo() {
		if (!WORKING)
			return;
		commInst.SendTo("stop", false);
		commInst.SendTo("isready", true);
		ON_OFF.setBackground(Color.red);
		ON_OFF.repaint();
		commInst.WaitForThroughPut("readyok", -1, false);
		WORKING = false;
	}

	public void stateChanged(ChangeEvent chg_evt) {
		if (WORKING)
			HaltMonteCarlo();
	}

	public void actionPerformed(ActionEvent act_evt) {
		if (act_evt.getActionCommand() == "On/Off") {
			if (WORKING)
				HaltMonteCarlo();
			else
				GoMonteCarlo();
		}
		if (act_evt.getActionCommand() == "Clear")
			ClearDataMC();
		if (act_evt.getActionCommand() == "All-In")
			AllInMC();
		if (act_evt.getActionCommand() == "All-Out")
			AllOutMC();
		if (act_evt.getActionCommand() == "Spice")
			SpiceMC();
	}

	public JSpinner GetSpinner(String S, int def, int min, int max, Box BOX) {
		Box B = new Box(BoxLayout.X_AXIS);
		JLabel L = new JLabel(S + ":  ");
		L.setBackground(new Color(205, 195, 235));
		B.add(L);
		SpinnerNumberModel MODEL = new SpinnerNumberModel(def, min, max, 1);
		JSpinner J = new JSpinner(MODEL);
		J.setName(S);
		J.addChangeListener(this);
		J.setPreferredSize(new Dimension(80, 16));
		J.setMaximumSize(new Dimension(80, 16));
		B.add(J);
		JLabel M = new JLabel("  min " + min + " max " + max);
		M.setFont(new Font("Monospaced", 0, 12));
		B.add(M);
		B.setAlignmentX(0.05f);
		BOX.add(B);
		return J;
	}

	public class RenderFloat extends DefaultTableCellRenderer {
		public RenderFloat() {
			super();
			setHorizontalAlignment(SwingConstants.RIGHT);
		}

		public void setValue(Object obj) {
			String S = String.format("%03.3g%%", (Float) obj);
			super.setValue(S);
		}
	}

	public class RenderDouble extends DefaultTableCellRenderer {
		public RenderDouble() {
			super();
			setHorizontalAlignment(SwingConstants.RIGHT);
		}

		public void setValue(Object obj) {
			Double D = (Double) obj / 100.0d;
			if (D > 99.9d)
				D = 99.9d;
			if (D < -99.9d)
				D = -99.9d;
			String S = String.format("%0+3.2f", D);
			super.setValue(S);
		}
	}

	public void DoFrameMC(JFrame F) {
		Box BOX = new Box(BoxLayout.Y_AXIS);
		Box HBOX = new Box(BoxLayout.X_AXIS);
		ON_OFF = new JButton("On/Off");
		ON_OFF.setBackground(Color.red);
		ON_OFF.setActionCommand("On/Off");
		ON_OFF.addActionListener(this);
		HBOX.add(ON_OFF);
		CLEAR = new JButton("Clear");
		CLEAR.setActionCommand("Clear");
		CLEAR.addActionListener(this);
		HBOX.add(CLEAR);
		ALL_IN = new JButton("All-In");
		ALL_IN.setActionCommand("All-In");
		ALL_IN.addActionListener(this);
		HBOX.add(ALL_IN);
		HBOX.setAlignmentX(0.0f);
		BOX.add(HBOX);
		ALL_OUT = new JButton("All-Out");
		ALL_OUT.setActionCommand("All-Out");
		ALL_OUT.addActionListener(this);
		HBOX.add(ALL_OUT);
		SPICE = new JButton("Spice");
		SPICE.setActionCommand("Spice");
		SPICE.addActionListener(this);
		HBOX.add(SPICE);
		BOX.add(HBOX);
		// BOX.add (new JSeparator (SwingConstants.VERTICAL)); // value ?
		CPU_SPINNER = GetSpinner("CPUs", commFrame.MC_CPUS, 1, 64, BOX);
		DEPTH_SPINNER = GetSpinner("Depth", commFrame.MC_DEPTH, 5, 20, BOX);
		LENGTH_SPINNER = GetSpinner("Length", commFrame.MC_LENGTH, 5, 65535, BOX);
		VERBOSE_SPINNER = GetSpinner("VerboseLength", commFrame.MC_VLEN, 0, 65535, BOX);
		MAX_SPINNER = GetSpinner("MaxValue", commFrame.MC_MAXV, -9000, 10000, BOX);
		MIN_SPINNER = GetSpinner("MinValue", commFrame.MC_MINV, -10000, 9000, BOX);
		// BOX.add (new JSeparator (SwingConstants.VERTICAL)); // value ?
		TABLE = new JTable(new TableModel(commInst.COMM.frame.BOARD_PANEL.POS));
		TABLE.setDefaultRenderer(Float.class, new RenderFloat());
		TABLE.setDefaultRenderer(Double.class, new RenderDouble());
		JScrollPane JSP = new JScrollPane(TABLE);
		int r = TABLE.getColumnModel().getColumnIndex("Hide");
		TableColumn C = TABLE.getColumnModel().getColumn(r);
		TABLE.removeColumn(C);
		BOX.add(JSP);
		F.add(BOX);
	}

	public void AllInMC() {
		if (WORKING)
			HaltMonteCarlo();
		for (int i = 0; i < DATA.length; i++)
			DATA[i][0] = true;
		ReSort();
		TABLE.repaint();
		UP_DATE = new Date().getTime();
	}

	public void AllOutMC() {
		if (WORKING)
			HaltMonteCarlo();
		for (int i = 0; i < DATA.length; i++)
			DATA[i][0] = false;
		ReSort();
		TABLE.repaint();
		UP_DATE = new Date().getTime();
	}

	public void ClearDataMC() {
		if (WORKING)
			HaltMonteCarlo();
		for (int i = 0; i < DATA.length; i++) {
			DATA[i][0] = false;
			DATA[i][2] = 0;
			DATA[i][3] = 0;
			DATA[i][4] = 0;
			DATA[i][5] = 0;
			DATA[i][6] = 0;
			DATA[i][7] = 0;
			DATA[i][9] = 0;
			EVAL[i] = 0L;
		}
		ReSort();
		TABLE.repaint();
		UP_DATE = new Date().getTime();
	}

	public class TableModel extends AbstractTableModel {
		String[] NAMES = {"on", "Move", "Trials", "Wins", "Loss", "Draw", "Eval", "Perc", "Hide", "Sigma"};
		int row_count;

		public TableModel(BoardPosition BP) {
			BP.MakeNormal(); // ensure
			row_count = BP.COUNT_OF_LEGAL_MOVES;
			SCORES = new ArrayList[row_count];
			DATA = new Object[BP.COUNT_OF_LEGAL_MOVES][10];
			for (int i = 0; i < BP.COUNT_OF_LEGAL_MOVES; i++) {
				DATA[i][0] = false;
				DATA[i][1] = BP.move_list_annotated[i];
				DATA[i][2] = 0;
				DATA[i][3] = 0;
				DATA[i][4] = 0;
				DATA[i][5] = 0;
				DATA[i][6] = 0;
				DATA[i][7] = 0f;
				DATA[i][8] = 0;
				DATA[i][9] = 0d;
				SCORES[i] = new ArrayList(100);
			}

			addTableModelListener(new TableModelListener() {
				public void tableChanged(TableModelEvent tab_mod_evt) {
					if (WORKING) {
						HaltMonteCarlo();
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								GoMonteCarlo();
							}
						});
					}
				}
			});
		}

		public Object getValueAt(int row, int col) {
			return DATA[SORT[row]][col];
		}

		public String getColumnName(int col) {
			return NAMES[col];
		}

		public int getColumnCount() {
			return NAMES.length;
		}

		public int getRowCount() {
			return row_count;
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			return (col == 0);
		}

		public void setValueAt(Object obj, int row, int col) {
			DATA[SORT[row]][col] = obj;
			fireTableDataChanged();
		}
	}

	public void StartMonteCarlo() {
		MC_FRAME = new JFrame("MonteCarlo " + commInst.COMM.id);
		DoFrameMC(MC_FRAME);
		MC_FRAME.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent win_evt) {
				HaltMonteCarlo();
				commInst.MONTE_CARLO = null;
			}
		});
		MC_FRAME.setBackground(Color.lightGray);
		MC_FRAME.pack();
		MC_FRAME.setSize(420, 300 + 10 * commInst.COMM.frame.BOARD_PANEL.POS.COUNT_OF_LEGAL_MOVES); // demand
		MC_FRAME.setResizable(false);
		MC_FRAME.setVisible(true);
	}

	public void ReSort() {
		int[] PRIORITY = new int[LEGAL_COUNT];
		int[] SWIVEL = new int[LEGAL_COUNT];
		for (int i = 0; i < LEGAL_COUNT; i++) {
			SWIVEL[i] = i;
			PRIORITY[i] = ((Boolean) DATA[i][0]) ? 1000000000 : 0;
			PRIORITY[i] += (Integer) DATA[i][8]; // pre EVAL
			PRIORITY[i] += (Integer) DATA[i][2] * 1; // Trials
			if ((Integer) DATA[i][2] == 0)
				PRIORITY[i] -= 1000000;
			PRIORITY[i] += (int) ((Double) DATA[i][6] * 1000.0d); // eval (cp)
			if (10 * ((Integer) DATA[i][3] + (Integer) DATA[i][4]) > (Integer) DATA[i][5])
				PRIORITY[i] += (int) ((Float) DATA[i][7] * 10000000.0d); // win perc
		}
		for (int j = LEGAL_COUNT - 1; j >= 0; j--) {
			int P = PRIORITY[j];
			int S = SWIVEL[j];
			for (int i = j + 1; i < LEGAL_COUNT; i++) {
				if (P < PRIORITY[i]) {
					PRIORITY[i - 1] = PRIORITY[i];
					SWIVEL[i - 1] = SWIVEL[i];
					PRIORITY[i] = P;
					SWIVEL[i] = S;
				}
				else {
					PRIORITY[i - 1] = P;
					SWIVEL[i - 1] = S;
					break;
				}
			}
		}
		for (int i = 0; i < LEGAL_COUNT; i++)
			SORT[i] = SWIVEL[i];
	}

	public void SpiceMC() {
		BoardPosition BP = commInst.COMM.frame.BOARD_PANEL.POS;
		for (int i = 0; i < BP.COUNT_OF_LEGAL_MOVES; i++) {
			WORKING = true;
			SPICEING = true;
			BESTMOVE = false;
			commInst.SendTo(commInst.GetFenMoves(), false);
			commInst.SendTo("go depth 10 searchmoves " + BP.GetDirect(BP.move_list[i]), true);
			while (!BESTMOVE)
				try {
					Thread.sleep(1);
					commInst.ThreadInput();
				}
				catch (InterruptedException io) {
				}
			DATA[i][8] = CENTI_PAWN_PARSE;
			SPICEING = false;
			WORKING = false;
		}
		ReSort();
		TABLE.repaint();
	}

	public void ParseSpice(String S) {
		StringTokenizer ST = new StringTokenizer(S);
		while (ST.hasMoreTokens()) {
			String A = ST.nextToken();
			if (A.equals("bestmove")) {
				BESTMOVE = true;
				return;
			}
			if (A.equals("cp")) {
				CENTI_PAWN_PARSE = Integer.valueOf(ST.nextToken()).intValue();
			}
			if (A.equals("mate")) {
				CENTI_PAWN_PARSE = 25000 * Integer.valueOf(ST.nextToken()).intValue();
			}
		}
	}

	public void ResultMC(String S) {
		BoardPosition BP = commInst.COMM.frame.BOARD_PANEL.POS;
		StringTokenizer ST = new StringTokenizer(S);
		String MOVE = ST.nextToken();
		Long VALUE = Long.valueOf(ST.nextToken());
		int i;
		for (i = 0; i < BP.COUNT_OF_LEGAL_MOVES; i++)
			if ((Boolean) (DATA[i][0]) && MOVE.equals(BP.GetDirect(BP.move_list[i])))
				break;
		if (i >= BP.COUNT_OF_LEGAL_MOVES)
			return;
		if (VALUE > (Integer) MAX_SPINNER.getValue()) {
			DATA[i][3] = (Integer) DATA[i][3] + 1; // Wins
			VALUE = (Long) MAX_SPINNER.getValue();
		}
		else if (VALUE < (Integer) MIN_SPINNER.getValue()) {
			DATA[i][4] = (Integer) DATA[i][4] + 1; // Loss
			VALUE = ((Long) MIN_SPINNER.getValue());
		}
		else
			DATA[i][5] = (Integer) DATA[i][5] + 1; // Data
		DATA[i][2] = (Integer) DATA[i][2] + 1; // Trials
		SCORES[i].add(VALUE);
		EVAL[i] += VALUE;
		double avg = ((double) EVAL[i]) / ((double) (Integer) (DATA[i][2]));
		DATA[i][6] = avg;

		double SD = 0.0;
		for (int j = 0; j < (Integer) DATA[i][2]; j++)
			SD += (avg - (double) (Long) SCORES[i].get(j)) * (avg - (double) (Long) SCORES[i].get(j));
		DATA[i][7] = (50.0f * (float) (2 * (Integer) DATA[i][3] + (Integer) DATA[i][5]))
				/ ((float) (Integer) DATA[i][2]);
		if ((Integer) DATA[i][2] > 1)
			DATA[i][9] = Math.sqrt(SD / (double) ((Integer) DATA[i][2] - 1));
		else
			DATA[i][9] = 999.0;
		ReSort();
		if (new Date().getTime() - UP_DATE > 200) // elapse 0.2 ms
		{
			TABLE.repaint();
			UP_DATE = new Date().getTime();
		}
	}

	public void ParseLineMC(String S) {
		if (SPICEING) {
			ParseSpice(S);
		}
		if (S.equals("MonteCarlo finds completion"))
			HaltMonteCarlo();
		if (S.startsWith("MCresult"))
			ResultMC(S.substring(9));
	}

	public void EndMonteCarlo() {
		HaltMonteCarlo();
		commInst.MONTE_CARLO = null;
		MC_FRAME.setVisible(false);
		MC_FRAME.dispose(); // null
	}
}