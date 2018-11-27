package edu.purdue.comradesgui.old;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;

public class CommunicatorInstance extends CommIO implements MouseListener, ChangeListener, ActionListener {
	String name;
	Communicator COMM;
	Process process;
	String[] OPT_NAME;
	String[] OPT_VALUE;
	String[] OPT_TYPE;
	boolean IS_NEW;
	private boolean HALTING = false;
	private boolean PRE_SENT = false;
	private boolean THREAD_INPUT = false;
	boolean on = false;
	private boolean COMRADES_MONTE_CARLO = false;
	private boolean PARSE_LINE = false;
	private long START_TIME = 0;
	private long LAST_INPUT = 0;
	InstancePanel instancePanel = null;
	JScrollPane scrollPane = null;
	BoardPosition CI_BOARD_POSITION;
	MonteCarlo MONTE_CARLO = null;
	InstanceOptionsFrameUCI IOF_UCI = null;
	InstanceOptionsFrameICI IOF_ICI = null;
	int MultiPV = 0;
	int MultiPV_Centi_Pawn = 987652;
	private int num_MPV = -1;
	private int num_CP = -1;
	private String CP_str;
	private JFrame POP_UP = null;

	boolean ICI;
	boolean REVERSE;
	private int MULTI_PV = 0;
	int DEPTH = 0;
	int SELDEPTH = 0;
	long TIME = 0;
	int DISPLAY_PV = 1;
	long NODES;
	boolean[] LOWER;
	boolean[] UPPER;
	boolean[] MATE;
	int[] SCORE;
	private String CURR_MOVE;
	String CURR_MOVE_STR = null;
	private int CURR_MOVE_NUMBER = 0;
	int HASH_FULL = 0;
	private int NPS = 0;
	long TB_HITS;
	int CPU_LOAD = 0;
	String[] PV_STRING;
	String[][] PV;
	boolean PV_CHANGE = false;
	private boolean MAKE_NEXT_MOVE = false;

	public CommunicatorInstance(Communicator comm, ComradesFrame cf) {
		super(cf);
		frame = cf;
		COMM = comm;
		ICI = COMM.ICI;
		COMRADES_MONTE_CARLO = COMM.COMRADES_MONTE_CARLO;
		name = COMM.id; // copy
		OPT_NAME = new String[1024];
		OPT_VALUE = new String[1024];
		OPT_TYPE = new String[1024];
		NODES = 0;
		TB_HITS = 0;
		LOWER = new boolean[256];
		UPPER = new boolean[256];
		MATE = new boolean[256];
		SCORE = new int[256];
		PV = new String[256][]; // dynamic ?
		PV_STRING = new String[256];
		REVERSE = false;
		for (int i = 1; i < 256; i++) {
			PV[i] = new String[256]; // dynamic ?
			SCORE[i] = 0;
			for (int j = 0; j < 256; j++)
				PV[i][j] = null;
			PV_STRING[i] = null;
		}
		CopyMyDefaults();
		LAST_INPUT = new Date().getTime();
		StartInstance();
	}

	public void AttendMonteCarlo() {
		MONTE_CARLO = new MonteCarlo(this);
	}

	public void mouseEntered(MouseEvent evt) {
	}

	public void mouseExited(MouseEvent evt) {
	}

	public void mousePressed(MouseEvent evt) {
	}

	public void mouseReleased(MouseEvent evt) {
	}

	/*
	 * public void mouseClicked (MouseEvent evt) { if (MONTE_CARLO != null &&
	 * MONTE_CARLO.WORKING) return;  int x = evt.getX (); int y = evt.getY
	 * (); int b = evt.getButton (); int n = evt.getClickCount (); if (x < 35 && y <
	 * 35 && !frame.BOARD_PANEL.SET_UP) { if (b != 1 && COMRADES_MONTE_CARLO) { if (on)
	 * SendHalt (); if (MONTE_CARLO == null) AttendMonteCarlo (); return; } if (on)
	 * SendHalt (); else SendGo(); return; } if (y < 20 && x > 50 && ((!ICI &&
	 * IOF_UCI == null) || (ICI && IOF_ICI == null))) { if (on) SendHalt (); if (b
	 * != 1) { UCI_new_game (); return; } if (!ICI) IOF_UCI = new
	 * InstanceOptionsFrameUCI (this); else IOF_ICI = new InstanceOptionsFrameICI
	 * (this); return; } }
	 */

	public void mouseClicked(MouseEvent evt) {
		if (MONTE_CARLO != null && MONTE_CARLO.WORKING)
			return;
		int x = evt.getX();
		int y = evt.getY();
		int b = evt.getButton();
		int n = evt.getClickCount();
		if (x < 35 && y < 35 && !frame.BOARD_PANEL.SET_UP) {
			if (b != 1 && COMRADES_MONTE_CARLO) {
				if (on)
					SendHalt();
				if (MONTE_CARLO == null)
					AttendMonteCarlo();
				return;
			}
			if (on)
				SendHalt();
			else
				SendGoInfinite();
			return;
		}
		if (y < 20 && x > 50 && ((!ICI && IOF_UCI == null) || (ICI && IOF_ICI == null))) {
			if (on)
				SendHalt();
			if (b != 1) {
				UCI_new_game();
				return;
			}
			if (!ICI)
				IOF_UCI = new InstanceOptionsFrameUCI(this);
			else
				IOF_ICI = new InstanceOptionsFrameICI(this);
		}
	}

/////////////////// /////////////////// /////////////////// ///////////////////

	public void CopyMyDefaults() {
		for (int i = 0; i < COMM.opt_count; i++) {
			OPT_NAME[i] = COMM.OPT_NAME[i];
			OPT_VALUE[i] = COMM.OPT_VALUE[i];
			OPT_TYPE[i] = COMM.OPT_TYPE[i];
			if (OPT_NAME[i].equals("UCI_Chess960"))
				COMM.Has_Chess_960 = true;
			if (OPT_NAME[i].equals("MultiPV")) {
				MultiPV = Integer.valueOf(OPT_VALUE[i]);
				num_MPV = i;
			}
			if (OPT_NAME[i].equals("MultiCentiPawnPV")) {
				MultiPV_Centi_Pawn = Integer.valueOf(OPT_VALUE[i]);
				CP_str = "MultiCentiPawnPV";
				num_CP = i;
			}
			if (OPT_NAME[i].equals("MultiPV_cp")) {
				MultiPV_Centi_Pawn = Integer.valueOf(OPT_VALUE[i]);
				CP_str = "MultiPV_cp";
				num_CP = i;
			}
		}
	}

	public void DisMissInstance() {
		if (on)
			DoHalt();
		if (instancePanel != null)
			frame.INSTANCE_BOX.remove(scrollPane); // upon so scrollPane
		if (frame.instances == 0)
			frame.INSTANCE_BOX.add(new EmptyPanel());
		if (IOF_ICI != null) {
			IOF_ICI.OPTIONS_FRAME.setVisible(false);
			IOF_ICI.OPTIONS_FRAME.dispose(); // null
			IOF_ICI.OPTIONS_FRAME = null;
			IOF_ICI = null;
		}
		if (IOF_UCI != null) {
			IOF_UCI.OPTIONS_FRAME.setVisible(false);
			IOF_UCI.OPTIONS_FRAME.dispose(); // null
			IOF_UCI.OPTIONS_FRAME = null;
			IOF_UCI = null;
		}
		RemovePopUp();
		frame.INSTANCE_BOX.revalidate(); // "re"
		frame.INSTANCE_BOX.repaint(); // and too ?
		frame.TellInfo("Forbid instance: " + COMM.id);
	}

	public void RemovePopUp() {
		if (POP_UP != null) {
			POP_UP.setVisible(false);
			POP_UP.dispose();
			POP_UP = null;
		}
	}

	public void DoHalt() {
		SendTo("stop", false);
		SendTo("isready", true);
		if (!WaitForThroughPut("readyok", 10000, false)) // 10s
		{
			DisMissInstance();
			return;
		}
		on = false;
		instancePanel.repaint();
		HALTING = false;
	}

	public void UCI_new_game() {
		while (THREAD_INPUT)
			SleepFor(10);
		if (on)
			DoHalt();
		SendTo("ucinewgame", false);
		SendTo("isready", true);
		if (!WaitForThroughPut("readyok", 10000, false)) // 10s
		{
			DisMissInstance();
			return;
		}

	}

	public String GetFenMoves() {
		MoveTree moveTree = frame.BOARD_PANEL.POS.MOVE_TREE;
		int revCount = CI_BOARD_POSITION.ReversibleCount;
		ComradesNode nowNode = moveTree.NOW;
		for (int i = 0; i < revCount && nowNode.getParent() != null; i++)
			nowNode = nowNode.MainLineParent;
		BoardPosition BP = new BoardPosition(moveTree.START); // copy
		ComradesNode rootNode = moveTree.ROOT;
		while (rootNode != nowNode) {
			BP.MakeMove32(rootNode.mainline.move, rootNode.mainline.fancy);
			rootNode = rootNode.MainLineNode;
		}
		String S = "position fen " + BP.GetFEN() + " moves";
		while (rootNode != moveTree.NOW) {
			BP.MakeMove32(rootNode.mainline.move, rootNode.mainline.fancy);
			S += " " + BP.GetDirect(rootNode.mainline.move);
			rootNode = rootNode.MainLineNode;
		}
		return S;
	}

	public void ClearInformatory() {
		NODES = 0;
		DEPTH = 0;
		SELDEPTH = 0;
		TIME = 0;
		CURR_MOVE_STR = null;
		CPU_LOAD = 0;
		HASH_FULL = 0;
		TB_HITS = 0;
		for (int i = 1; i < 256; i++)
			PV[i][0] = null;
		REVERSE = frame.BOARD_PANEL.REVERSE;
		CI_BOARD_POSITION = new BoardPosition(frame.BOARD_PANEL.POS);
		PV_CHANGE = true;
		instancePanel.repaint();
	}

	public void GoInfinite() {
		while (HALTING)
			SleepFor(10);
		while (THREAD_INPUT)
			SleepFor(10);
		ClearInformatory();
		CI_BOARD_POSITION.MakeNormal(); // ensure
		if (CI_BOARD_POSITION.COUNT_OF_LEGAL_MOVES == 0) {
			frame.TellInfo("No Legal moves.");
			return;
		}
		if (!CI_BOARD_POSITION.IsOK()) {
			frame.TellInfo("BoardPosition is not OK!");
			return;
		}
		START_TIME = new Date().getTime();
		if (ICI)
			SendTo("ici-age " +
					(2 * CI_BOARD_POSITION.MOVE_NUMBER +
							(CI_BOARD_POSITION.WTM ? 0 : 1)), false);
		if (CI_BOARD_POSITION.Chess960 && !COMM.Has_Chess_960) {
			frame.TellInfo("Communicator buys not the Chess960!");
			return;
		}
		if (COMM.Has_Chess_960)
			SendTo("setoption name UCI_Chess960 value " + CI_BOARD_POSITION.Chess960, false);
		SendTo(GetFenMoves(), false);
		SendTo("go infinite", true);
		on = true;
		instancePanel.repaint();
		PRE_SENT = false;
		// oblige
	}

	public void Go() {
		while (HALTING)
			SleepFor(10);
		while (THREAD_INPUT)
			SleepFor(10);
		ClearInformatory();
		CI_BOARD_POSITION.MakeNormal(); // ensure
		if (CI_BOARD_POSITION.COUNT_OF_LEGAL_MOVES == 0) {
			frame.TellInfo("No Legal moves.");
			return;
		}
		if (!CI_BOARD_POSITION.IsOK()) {
			frame.TellInfo("BoardPosition is not OK!");
			return;
		}
		START_TIME = new Date().getTime();
		if (ICI)
			SendTo("ici-age " + (2 * CI_BOARD_POSITION.MOVE_NUMBER + (CI_BOARD_POSITION.WTM ? 0 : 1)), false);
		if (CI_BOARD_POSITION.Chess960 && !COMM.Has_Chess_960) {
			frame.TellInfo("Communicator buys not the Chess960!");
			return;
		}
		if (COMM.Has_Chess_960)
			SendTo("setoption name UCI_Chess960 value " + CI_BOARD_POSITION.Chess960, false);
		SendTo(GetFenMoves(), false);

		if (frame.gameMode == 1)
			frame.blackTime.resume();

		SendTo("go wtime " + (frame.whiteTime.getTime()) + " btime " + (frame.blackTime.getTime()), true);

		on = true;
		instancePanel.repaint();
		PRE_SENT = false;
		// oblige
	}

	public void SendHalt() {
		HALTING = true;
		DoHalt();
	}

	public void SendGoInfinite() {
		PRE_SENT = true;
		GoInfinite();
	}

	public void SendGo() {
		PRE_SENT = true;
		Go();
	}

////////////////////////////////      ////////////////////////////////

	public boolean DirectCommunicatory() {
		process = null;
		try {
			if (COMM.RunTimeOptions.equals("NULL"))
				process = Runtime.getRuntime().exec(COMM.path);
			else
				process = Runtime.getRuntime().exec(COMM.path + " " + COMM.RunTimeOptions);
		}
		catch (IOException io_exc) {
			frame.TellInfo("Not found: " + COMM.path);
			return false;
		}
		READER = new BufferedReader(new InputStreamReader(process.getInputStream()));
		WRITER = new PrintWriter(new OutputStreamWriter(process.getOutputStream()));
		Runtime.getRuntime().addShutdownHook(new KillProcess(process, this));
		SleepFor(100); // 1/10 second delay
		while (IsReady()) {
			String S = DemandLine();
			frame.TellInfo("On StartUp: " + S);
		}
		SendTo("ici", true);
		if (WaitForThroughPut("ici-echo", 1000, false))
			return true;
		SendTo("uci", true);
		if (!WaitForThroughPut("uciok", 1000, false)) // 1 second
		{
			process.destroy();
			return false;
		}
		return true;
	}

	public void AttendMyDefaults() {
		for (int i = 0; i < COMM.opt_count; i++) {
			if (OPT_TYPE[i].equals("string") && OPT_VALUE[i].equals("NULL"))
				continue;
			if (OPT_TYPE[i].equals("button") && OPT_VALUE[i].equals("false"))
				continue;
			if (OPT_TYPE[i].equals("directory-multi-reset")) {
				StringTokenizer ST = new StringTokenizer(OPT_VALUE[i], "|");
				while (ST.hasMoreTokens())
					SendTo("setoption name " + OPT_NAME[i] + " value " + ST.nextToken(), false);
				OPT_VALUE[i] = "NULL";
				continue;
			}
			SendTo("setoption name " + OPT_NAME[i] + " value " + OPT_VALUE[i], false);
		}
		ReadyOK(true); // user dis miss ?
	}

	public void BelongInstance() {
		frame.INSTANCES[frame.instances++] = this;
	}

	public void DisplayInstance() {
		if (frame.instances == 0)
			frame.INSTANCE_BOX.removeAll();
		instancePanel = new InstancePanel(this);
		// instancePanel.addMouseListener (this);
		scrollPane = new JScrollPane(instancePanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setMinimumSize(new Dimension(360, 200));
		scrollPane.setPreferredSize(new Dimension(360, 200));
		frame.INSTANCE_BOX.add(scrollPane);
		frame.INSTANCE_BOX.revalidate(); // attend // For the necessary: "re"
		frame.INSTANCE_BOX.repaint(); // and too ?
	}

////////////////////////////////      ////////////////////////////////

	public int NextInt(StringTokenizer ST) {
		return Integer.valueOf(ST.nextToken());
	}

	public long NextLong(StringTokenizer ST) {
		return Long.valueOf(ST.nextToken());
	}

	public void DoBestMove(String S) // battle ?
	{
		engineMove(S);


		instancePanel.repaint();
	}

	public void engineMove(String S) {
		if (frame.gameMode == 1 && frame.BOARD_PANEL.POS.WTM) {
			return;
		}

		if (frame.gameMode == 3 && frame.INSTANCES[0] == this && !frame.BOARD_PANEL.POS.WTM) {
			return;
		}

		if (frame.gameMode == 3 && frame.INSTANCES[1] == this && frame.BOARD_PANEL.POS.WTM) {
			return;
		}

		SendHalt();
		int moveNum = frame.BOARD_PANEL.POS.FindMove(S);
		frame.BOARD_PANEL.AttendMove(moveNum, true, true);
		// frame.BOARD_PANEL.BoardPositionChanged ();
		frame.BOARD_PANEL.repaint();

		if (frame.gameMode != 0) {
			frame.switchTurn();
			MAKE_NEXT_MOVE = true;
		}
	}

	public String AttendForm(String strIn, BoardPosition boardPos, int i) {

		String newStr = new String("");
		if ((i == 0) && !boardPos.WTM)
			newStr += boardPos.MOVE_NUMBER + "...";
		if (boardPos.WTM)
			newStr += "" + boardPos.MOVE_NUMBER + ".";
		int w = boardPos.FindMove(strIn);
		if (w == -1) // do err ?
			return newStr;
		boolean IS_CHECK = (boardPos.move_list_annotated[w].indexOf("+") != -1);
		newStr += boardPos.move_list_annotated[w];
		boardPos.MakeMove(w);
		boardPos.MakeNormal();
		if (boardPos.COUNT_OF_LEGAL_MOVES == 0)
			newStr += (IS_CHECK) ? "#" : "=";
		return newStr;
	}

	public void PV_do_string(int w) {
		int i = 0;
		PV_STRING[w] = "";
		BoardPosition TEMP = new BoardPosition(CI_BOARD_POSITION); // defer !
		while (PV[w][i] != null) {
			PV_STRING[w] += AttendForm(PV[w][i], TEMP, i) + " ";
			i++;
		}
	}

	public void DoPV(StringTokenizer ST) {
		for (int i = 0; i < 256; i++)
			PV[MULTI_PV][i] = null;
		PV_CHANGE = true; // intend ?
		int n = 0;
		while (ST.hasMoreTokens())
			PV[MULTI_PV][n++] = ST.nextToken();
		PV_do_string(MULTI_PV);
	}

	public void UCI_Parser(String S) {
		StringTokenizer ST = new StringTokenizer(S);
		if (!ST.hasMoreTokens())
			return;
		String nextToken = ST.nextToken();
		if (nextToken.equals("bestmove")) {
			DoBestMove(ST.nextToken());
			return;
		}
		if (!nextToken.equals("info")) // not info (UCI)
		{
			frame.TellInfo("From " + COMM.name + ": " + S);
			return;
		}
		MULTI_PV = 1; // ensure
		int Score = -123456;
		boolean lower = false, upper = false, mate = false;
		while (ST.hasMoreTokens()) {
			nextToken = ST.nextToken();
			if (nextToken.equals("string")) {
				frame.TellInfo("From " + COMM.name + ": " + S);
				return;
			}
			if (nextToken.equals("depth")) {
				int u = NextInt(ST);
				if (u > DEPTH)
					DEPTH = u;
			}
			if (nextToken.equals("seldepth")) {
				int u = NextInt(ST);
				if (u > SELDEPTH)
					SELDEPTH = u;
			}
			if (nextToken.equals("time")) {
				int u = NextInt(ST);
				if (u > TIME)
					TIME = u;
			}
			if (nextToken.equals("nodes")) {
				long u = NextLong(ST);
				if (u > NODES)
					NODES = u;
			}
			if (nextToken.equals("multipv")) {
				MULTI_PV = NextInt(ST);
				PV[MULTI_PV + 1][0] = null;
			}
			if (nextToken.equals("pv"))
				DoPV(ST);
			// fails valid, upon MULTI_PV delaying (StockFish)
			if (nextToken.equals("lowerbound"))
				lower = true;
			if (nextToken.equals("upperbound"))
				upper = true;
			if (nextToken.equals("score")) {
				nextToken = ST.nextToken();

				mate = nextToken.equals("mate"); // cp
				lower = upper = false;
				nextToken = ST.nextToken();
				if (nextToken.equals("lowerbound")) {
					lower = true;
					nextToken = ST.nextToken();
				}
				if (nextToken.equals("upperbound")) {
					upper = true;
					nextToken = ST.nextToken();
				}
				int score = Integer.valueOf(nextToken);
				if (!CI_BOARD_POSITION.WTM)
					score = -score; // invert score upon black
				Score = score;
			}
			if (nextToken.equals("currmove"))
				CURR_MOVE = ST.nextToken();
			if (nextToken.equals("currmovenumber")) {
				CURR_MOVE_NUMBER = Integer.valueOf(ST.nextToken());
				CURR_MOVE_STR = "" + CURR_MOVE_NUMBER + "/" + CI_BOARD_POSITION.COUNT_OF_LEGAL_MOVES;
				int w = CI_BOARD_POSITION.FindMove(CURR_MOVE);  // order
				if (w != -1)
					CURR_MOVE_STR += " " + CI_BOARD_POSITION.move_list_annotated[w];
			}
			if (nextToken.equals("hashfull"))
				HASH_FULL = NextInt(ST);
			if (nextToken.equals("nps"))
				NPS = NextInt(ST);
			if (nextToken.equals("tbhits"))
				TB_HITS = NextLong(ST);
			if (nextToken.equals("cpuload"))
				CPU_LOAD = NextInt(ST);
		}
		if (Score != -123456) {
			SCORE[MULTI_PV] = Score;
			MATE[MULTI_PV] = mate;
			LOWER[MULTI_PV] = lower;
			UPPER[MULTI_PV] = upper;
		}
		instancePanel.repaint();
	}

	public void ParseLine(String S) {
		// while frame repaints attend SleepFor ?
		PARSE_LINE = true;
		if (MONTE_CARLO != null && MONTE_CARLO.WORKING)
			MONTE_CARLO.ParseLineMC(S);
		else
			UCI_Parser(S); // switch parser : xboard ?
		PARSE_LINE = false;
	}

	public void ThreadInput() {
		if (HALTING || PRE_SENT)
			return;
		THREAD_INPUT = true;
		while (IsReady())
			ParseLine(DemandLine());
		LAST_INPUT = new Date().getTime();
		THREAD_INPUT = false;

		if (MAKE_NEXT_MOVE) {
			MAKE_NEXT_MOVE = false;

			if (frame.checkGameOver())
				return;

			if (frame.gameMode == 2) {
				frame.INSTANCES[0].SendGo();
			}
			else if (frame.gameMode == 3) {
				if (frame.INSTANCES[0] == this) {
					frame.INSTANCES[1].SendGo();
				}
				if (frame.INSTANCES[1] == this) {
					frame.INSTANCES[0].SendGo();
				}
			}
		}
	}

	public void InstanceThread() /* internal ? */ {
		if (IsReady())
			ThreadInput();
	}

////////////////////////  ////////////////////////  ////////////////////////

	public void StartInstance() {
		if (!DirectCommunicatory())
			return;
		AttendMyDefaults();
		DisplayInstance();
		BelongInstance();
		frame.TellInfo("Has loaded " + COMM.id);
		frame.LOAD_INSTANCE = false;
		CI_BOARD_POSITION = new BoardPosition(frame.BOARD_PANEL.POS);
	}

	public void OldMultiPV_values() {
		OPT_VALUE[num_MPV] = "" + MultiPV;
		if (MultiPV_Centi_Pawn != 987652)
			OPT_VALUE[num_CP] = "" + MultiPV_Centi_Pawn;
	}

	public void actionPerformed(ActionEvent act_evt) {
		String S = act_evt.getActionCommand();
		RemovePopUp();
		if (S.equals("Cancel")) {
			OldMultiPV_values();
			return;
		}
		MultiPV = Integer.valueOf(OPT_VALUE[num_MPV]);
		SendTo("setoption name MultiPV value " + MultiPV, true);
		if (MultiPV_Centi_Pawn != 987652) {
			MultiPV_Centi_Pawn = Integer.valueOf(OPT_VALUE[num_CP]);
			SendTo("setoption name " + CP_str + " value " + MultiPV_Centi_Pawn, true);
		}
		instancePanel.RenewInstancePanel();
	}

	public void stateChanged(ChangeEvent chg_evt) {
		JSpinner J = (JSpinner) (chg_evt.getSource());
		if (J.getName().equals("MultiPV"))
			OPT_VALUE[num_MPV] = J.getValue().toString();
		if (J.getName().equals("MultiPV_Centi_Pawn"))
			OPT_VALUE[num_CP] = J.getValue().toString();
	}

	public void doPopUpMultiPV() {
		if (MultiPV == 0 || POP_UP != null)
			return;
		if (on)
			SendHalt();
		POP_UP = new JFrame("MultiPV: " + COMM.id);
		Box boxLayoutMain = new Box(BoxLayout.Y_AXIS);
		Dimension D = new Dimension(5, 7);
		boxLayoutMain.add(new Box.Filler(D, D, D));
		boxLayoutMain.add(new Box.Filler(D, D, D));
		Box boxLayout_MultiPV = new Box(BoxLayout.X_AXIS);
		SpinnerNumberModel MODEL = new SpinnerNumberModel(MultiPV, 1, 250, 1);
		JLabel L = new JLabel("MultiPV:  ");
		boxLayout_MultiPV.add(L);
		JSpinner spiinerMultiPV = new JSpinner(MODEL);
		spiinerMultiPV.setName("MultiPV");
		spiinerMultiPV.addChangeListener(this);
		spiinerMultiPV.setPreferredSize(new Dimension(60, 16));
		spiinerMultiPV.setMaximumSize(new Dimension(60, 16));
		boxLayout_MultiPV.add(spiinerMultiPV);
		boxLayoutMain.add(boxLayout_MultiPV);
		Dimension minSize = new Dimension(5, 100);
		Dimension prefSize = new Dimension(5, 100);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
		boxLayoutMain.add(new Box.Filler(D, D, D));
		if (MultiPV_Centi_Pawn != 987652) {
			Box boxLayout_centiPawn = new Box(BoxLayout.X_AXIS);
			SpinnerNumberModel centiPawnFactory = new SpinnerNumberModel(MultiPV_Centi_Pawn, 0, 65535, 1);
			JLabel centiPawnLabel = new JLabel("CentiPawn: ");
			boxLayout_centiPawn.add(centiPawnLabel);
			JSpinner spinnerCentiPawn = new JSpinner(centiPawnFactory);
			spinnerCentiPawn.setName("MultiPV_Centi_Pawn");
			spinnerCentiPawn.addChangeListener(this);
			spinnerCentiPawn.setPreferredSize(new Dimension(65, 16));
			spinnerCentiPawn.setMaximumSize(new Dimension(65, 16));
			boxLayout_centiPawn.add(spinnerCentiPawn);
			boxLayoutMain.add(boxLayout_centiPawn);
			boxLayoutMain.add(new Box.Filler(D, D, D));
		}
		POP_UP.add(boxLayoutMain);
		Box boxLayout_Buttons = new Box(BoxLayout.X_AXIS);
		JButton buttonOK = new JButton("OK");
		buttonOK.setActionCommand("OK");
		buttonOK.addActionListener(this);
		boxLayout_Buttons.add(buttonOK);
		boxLayout_Buttons.add(new Box.Filler(D, D, D));
		JButton buttonCancel = new JButton("Cancel");
		buttonCancel.setActionCommand("Cancel");
		buttonCancel.addActionListener(this);
		boxLayout_Buttons.add(buttonCancel);
		boxLayoutMain.add(boxLayout_Buttons);
		POP_UP.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent win_evt) {
				OldMultiPV_values();
				POP_UP = null;
			}
		});
		POP_UP.setBackground(Color.lightGray);
		POP_UP.pack();
		POP_UP.setSize(250, 125); // demand
		POP_UP.setResizable(false);
		POP_UP.setVisible(true);
	}

	public class EmptyPanel extends JPanel {
		public EmptyPanel() {
		}

		public Dimension getPreferredSize() {
			return new Dimension(400, 400);
		}

		public Dimension getMinimumSize() {
			return new Dimension(400, 400);
		}
	}
}
