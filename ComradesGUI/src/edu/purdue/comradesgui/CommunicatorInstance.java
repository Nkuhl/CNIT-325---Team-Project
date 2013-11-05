package edu.purdue.comradesgui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

public class CommunicatorInstance extends CommIO implements MouseListener, ChangeListener, ActionListener
{
    String name;
    Communicator COMM;
    Process process;
    String[] OPT_NAME;
    String[] OPT_VALUE;
    String[] OPT_TYPE;
    boolean IS_NEW;
    boolean HALTING = false;
    boolean PRE_SENT = false;
    boolean THREAD_INPUT = false;
    boolean on = false;
    boolean COMRADES_MONTE_CARLO = false;
    boolean PARSE_LINE = false;
    long START_TIME = 0;
    long LAST_INPUT = 0;
    InstancePanel IP = null;
    JScrollPane JSP = null;
    BoardPosition CI_BOARD_POSITION;
    MonteCarlo MONTE_CARLO = null;
    InstanceOptionsFrameUCI IOF_UCI = null;
    InstanceOptionsFrameICI IOF_ICI = null;
    int MultiPV = 0;
    int MultiPV_Centi_Pawn = 987652;
    int num_MPV = -1;
    int num_CP = -1;
    String CP_str;
    JFrame POP_UP = null;
    
    boolean ICI;
    boolean REVERSE;
    int MULTI_PV = 0;
    int DEPTH = 0;
    int SELDEPTH = 0;
    long TIME = 0;
    int DISPLAY_PV = 1;
    long NODES;
    boolean[] LOWER;
    boolean[] UPPER;
    boolean[] MATE;
    int[] SCORE;
    String CURR_MOVE;
    String CURR_MOVE_STR = null;
    int CURR_MOVE_NUMBER = 0;
    int HASH_FULL = 0;
    int NPS = 0;
    long TB_HITS;
    int CPU_LOAD = 0;
    String[] PV_STRING;
    String[][] PV;
    boolean PV_CHANGE = false;

    public CommunicatorInstance (Communicator comm, ComradesFrame cf)
    {
	super (cf);
	CF = cf;
	COMM = comm;
	ICI = COMM.ICI;
	COMRADES_MONTE_CARLO = COMM.COMRADES_MONTE_CARLO;
	name = new String (COMM.id); // copy
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
	for (int i = 1; i < 256; i++)
	    {
		PV[i] = new String[256]; // dynamic ?
		SCORE[i] = 0;
		for (int j = 0; j < 256; j++)
		    PV[i][j] = null;
		PV_STRING[i] = null;
	    }
	CopyMyDefaults ();
	LAST_INPUT = new Date ().getTime (); // HACK
	StartInstance ();
    }

    public void AttendMonteCarlo ()
    {
	MONTE_CARLO = new MonteCarlo (this);
    }

    public void mouseEntered (MouseEvent evt) {}
    public void mouseExited (MouseEvent evt) {}
    public void mousePressed (MouseEvent evt) {}
    public void mouseReleased (MouseEvent evt) {}
    public void mouseClicked (MouseEvent evt)
    {
	if (MONTE_CARLO != null && MONTE_CARLO.WORKING)
	    return; // HACK
	int x = evt.getX ();
	int y = evt.getY ();
	int b = evt.getButton ();
	int n = evt.getClickCount ();
	if (x < 35 && y < 35 && !CF.BOARD_PANEL.SET_UP) 
	    {
		if (b != 1 && COMRADES_MONTE_CARLO)
		    {
			if (on)
			    SendHalt ();
			if (MONTE_CARLO == null)
			    AttendMonteCarlo ();
			return;
		    }
		if (on)
		    SendHalt ();
		else
		    SendGoInfinite ();
		return;
	    }
	if (y < 20 && x > 50 && ((!ICI && IOF_UCI == null) || (ICI && IOF_ICI == null)))
	    {
		if (on)
		    SendHalt ();
		if (b != 1)
		    {
			UCI_new_game ();
			return;
		    }
		if (!ICI)
		    IOF_UCI = new InstanceOptionsFrameUCI (this);
		else
		    IOF_ICI = new InstanceOptionsFrameICI (this);
		return;
	    }
    }
		
/////////////////// /////////////////// /////////////////// ///////////////////

    public void CopyMyDefaults ()
    {
	for (int i = 0; i < COMM.opt_count; i++)
	    {
		OPT_NAME[i] = new String (COMM.OPT_NAME[i]);
		OPT_VALUE[i] = new String (COMM.OPT_VALUE[i]);
		OPT_TYPE[i] = new String (COMM.OPT_TYPE[i]);
		if (OPT_NAME[i].equals ("UCI_Chess960"))
		    COMM.Has_Chess_960 = true;
		if (OPT_NAME[i].equals ("MultiPV"))
		    {
			MultiPV = Integer.valueOf (OPT_VALUE[i]).intValue ();
			num_MPV = i;
		    }
		if (OPT_NAME[i].equals ("MultiCentiPawnPV"))
		    {
			MultiPV_Centi_Pawn = Integer.valueOf (OPT_VALUE[i]).intValue ();
			CP_str = "MultiCentiPawnPV";
			num_CP = i;
		    }
		if (OPT_NAME[i].equals ("MultiPV_cp"))
		    {
			MultiPV_Centi_Pawn = Integer.valueOf (OPT_VALUE[i]).intValue ();
			CP_str = "MultiPV_cp";
			num_CP = i;
		    }
	    }
    }

    public void DisMissInstance ()
    {
	if (on)
	    DoHalt ();
	if (IP != null)
	    CF.INSTANCE_BOX.remove (JSP); // upon so JSP
	if (CF.instances == 0)
	    CF.INSTANCE_BOX.add (new EmptyPanel ());
	if (IOF_ICI != null)
	    {
		IOF_ICI.OPTIONS_FRAME.setVisible (false);
		IOF_ICI.OPTIONS_FRAME.dispose (); // null
		IOF_ICI.OPTIONS_FRAME = null;
		IOF_ICI = null;
	    }
	if (IOF_UCI != null)
	    {
		IOF_UCI.OPTIONS_FRAME.setVisible (false);
		IOF_UCI.OPTIONS_FRAME.dispose (); // null
		IOF_UCI.OPTIONS_FRAME = null;
		IOF_UCI = null;
	    }
	RemovePopUp ();
	CF.INSTANCE_BOX.revalidate (); // "re"
	CF.INSTANCE_BOX.repaint (); // and too ?
	CF.TellInfo ("Forbid instance: " + COMM.id);
    }

    public void RemovePopUp ()
    {
	if (POP_UP != null)
	    {
		POP_UP.setVisible (false);
		POP_UP.dispose ();
		POP_UP = null;
	    }
    }

    public void DoHalt ()
    {
	SendTo ("stop", false);
	SendTo ("isready", true);
	if (!WaitForThroughPut ("readyok", 10000, false)) // 10s
	    {
		DisMissInstance ();
		return; // HACK
	    }
	on = false;
	IP.repaint ();
	HALTING = false;
    }

    public void UCI_new_game ()
    {
	while (THREAD_INPUT)
	    SleepFor (10);
	if (on)
	    DoHalt ();
	SendTo ("ucinewgame", false);
	SendTo ("isready", true);
	if (!WaitForThroughPut ("readyok", 10000, false)) // 10s
	    {
		DisMissInstance ();
		return; // HACK
	    }
    }

    public String GetFenMoves () 
    {
	MoveTree MT = CF.BOARD_PANEL.POS.MOVE_TREE;
	int R = CI_BOARD_POSITION.ReversibleCount;
	ComradesNode T = MT.NOW;
	for (int i = 0; i < R && T.getParent () != null; i++)
	    T = T.MainLineParent;
	BoardPosition BP = new BoardPosition (MT.START); // copy
	ComradesNode N = MT.ROOT;
	while (N != T) // HACK
	    {
		BP.MakeMove32 (N.mainline.move, N.mainline.fancy);
		N = N.MainLineNode;
	    }
	String S = "position fen " + BP.GetFEN () + " moves";
	while (N != MT.NOW)
	    {
		BP.MakeMove32 (N.mainline.move, N.mainline.fancy);
		S += " " + BP.GetDirect (N.mainline.move);
		N = N.MainLineNode;
	    }
	return S;
    }

    public void ClearInformatory ()
    {
	NODES = 0;
	DEPTH = 0;
	SELDEPTH = 0;
	TIME = 0; // HACK
	CURR_MOVE_STR = null;
	CPU_LOAD = 0;
	HASH_FULL = 0;
	TB_HITS = 0;
	for (int i = 1; i < 256; i++)
	    PV[i][0] = null;
	REVERSE = CF.BOARD_PANEL.REVERSE;
	CI_BOARD_POSITION = new BoardPosition (CF.BOARD_PANEL.POS);
	PV_CHANGE = true;
	IP.repaint ();
    }

    public void GoInfinite ()
    {
	while (HALTING)
	    SleepFor (10);
	while (THREAD_INPUT)
	    SleepFor (10);
	ClearInformatory ();
	CI_BOARD_POSITION.MakeNormal (); // ensure
	if (CI_BOARD_POSITION.COUNT_OF_LEGAL_MOVES == 0)
	    {
		CF.TellInfo ("No Legal moves.");
		return;
	    }
	if (!CI_BOARD_POSITION.IsOK ())
	    {
		CF.TellInfo ("BoardPosition is not OK!");
		return;
	    }
	START_TIME = new Date ().getTime ();
	if (ICI)
	    SendTo ("ici-age " + 
		    (2 * CI_BOARD_POSITION.MOVE_NUMBER +
		     (CI_BOARD_POSITION.WTM ? 0 : 1)), false);
	if (CI_BOARD_POSITION.Chess960 && !COMM.Has_Chess_960)
	    {
		CF.TellInfo ("Communicator buys not the Chess960!");
		return;
	    }
	if (COMM.Has_Chess_960)
	    SendTo ("setoption name UCI_Chess960 value " + CI_BOARD_POSITION.Chess960, false);
	SendTo (GetFenMoves (), false);
        SendTo ("go infinite", true);
	on = true;
	IP.repaint ();
	PRE_SENT = false;
	// oblige
    }

    public void SendHalt ()
    {
	HALTING = true;
	DoHalt ();
    }

    public void SendGoInfinite ()
    {
	PRE_SENT = true;
	GoInfinite ();
    }

////////////////////////////////      ////////////////////////////////

    public boolean DirectCommunicatory ()
    {
	process = null;
	try
            {
		if (COMM.RunTimeOptions.equals ("NULL"))
		    process = Runtime.getRuntime ().exec (COMM.path);
		else
		    process = Runtime.getRuntime ().exec (COMM.path + " " + COMM.RunTimeOptions); // HACK
            }
        catch (IOException io_exc)
            {
                CF.TellInfo ("Not found: " + COMM.path);
                return false;
            }
        READER = new BufferedReader (new InputStreamReader (process.getInputStream ()));
        WRITER = new PrintWriter (new OutputStreamWriter (process.getOutputStream ()));
	Runtime.getRuntime().addShutdownHook (new KillProcess (process, this));
        SleepFor (100); // 1/10 second delay
        while (IsReady ())
            {
                String S = DemandLine (); // HACK  
		CF.TellInfo ("On StartUp: " + S);
            }
        SendTo ("ici", true);
        if (WaitForThroughPut ("ici-echo", 1000, false))
	    return true;
        SendTo ("uci", true);
	if (!WaitForThroughPut ("uciok", 1000, false)) // 1 second
	    {
		process.destroy ();
		return false;
	    }
        return true;
    }

    public void AttendMyDefaults ()
    {
	for (int i = 0; i < COMM.opt_count; i++)
	    {
		if (OPT_TYPE[i].equals ("string") && OPT_VALUE[i].equals ("NULL"))
		    continue;
		if (OPT_TYPE[i].equals ("button") && OPT_VALUE[i].equals ("false"))
		    continue;
		if (OPT_TYPE[i].equals ("directory-multi-reset"))
		    {
			StringTokenizer ST = new StringTokenizer (OPT_VALUE[i],"|");
			while (ST.hasMoreTokens ())
			    SendTo ("setoption name " + OPT_NAME[i] +
				    " value " + ST.nextToken (), false);
			OPT_VALUE[i] = new String ("NULL");
			continue;
		    }
		SendTo ("setoption name " + OPT_NAME[i] + " value " + OPT_VALUE[i], false);
	    }
        ReadyOK (true); // user dis miss ?
    }

    public void BelongInstance ()
    {
	CF.INSTANCES[CF.instances++] = this;
    }

    public void DisplayInstance ()
    {
	if (CF.instances == 0)
	    CF.INSTANCE_BOX.removeAll ();
	IP = new InstancePanel (this);
	IP.addMouseListener (this);
	JSP = new JScrollPane (IP);
        JSP.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	JSP.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);	
	JSP.setMinimumSize (new Dimension (360, 200));
	JSP.setPreferredSize (new Dimension (360, 200));
	CF.INSTANCE_BOX.add (JSP);
	CF.INSTANCE_BOX.revalidate (); // attend // For the necessary: "re"
	CF.INSTANCE_BOX.repaint (); // and too ?
    }

////////////////////////////////      ////////////////////////////////

    public int NextInt (StringTokenizer ST)
    {
	return Integer.valueOf (ST.nextToken ()).intValue ();
    }

    public long NextLong (StringTokenizer ST)
    {
	return Long.valueOf (ST.nextToken ()).longValue ();
    }

    public void DoBestMove (String S) // battle ?
    {
	SendHalt (); // HACK
    }
    
    public String AttendForm (String S, BoardPosition BP, int i)
    {
	String R = new String ("");
	if ((i == 0) && !BP.WTM)
	    R += BP.MOVE_NUMBER + "...";
	if (BP.WTM)
	    R += "" + BP.MOVE_NUMBER + ".";
	int w = BP.FindMove (S);
	if (w == -1) // do err ?
	    return R;
	boolean IS_CHECK = (BP.move_list_annotated[w].indexOf ("+") != -1);
	R += BP.move_list_annotated[w];
	BP.MakeMove (w);
	BP.MakeNormal ();
	if (BP.COUNT_OF_LEGAL_MOVES == 0)
	    R += (IS_CHECK) ? "#" : "=";
	return R;
    }

    public void PV_do_string (int w)
    {
	int i = 0;
	PV_STRING[w] = new String ("");
	BoardPosition TEMP = new BoardPosition (CI_BOARD_POSITION); // defer !
	while (PV[w][i] != null)
	    {
		PV_STRING[w] += AttendForm (PV[w][i], TEMP, i) + " ";
		i++;
	    }
    }

    public void DoPV (StringTokenizer ST)
    {
	for (int i = 0; i < 256; i++)
	    PV[MULTI_PV][i] = null;
	PV_CHANGE = true; // intend ?
	int n = 0;
	while (ST.hasMoreTokens ())
	    PV[MULTI_PV][n++] = ST.nextToken ();
	PV_do_string (MULTI_PV);
    }
    
    public void UCI_Parser (String S)
    {
	StringTokenizer ST = new StringTokenizer (S);
	if (!ST.hasMoreTokens ())
	    return;
	String x = ST.nextToken ();
	if (x.equals ("bestmove"))
	    {
		DoBestMove (ST.nextToken ());
		return;
	    }
	if (!x.equals ("info")) // not info (UCI)
	    {
		CF.TellInfo ("From " + COMM.name + ": " + S);
		return;
	    }
	MULTI_PV = 1; // ensure
	int Score = -123456;
	boolean Lower = false, Upper = false, Mate = false;
	while (ST.hasMoreTokens ())
	    {
		x = ST.nextToken ();
		if (x.equals ("string"))
		    {
			CF.TellInfo ("From " + COMM.name + ": " + S);
			return;
		    }
		if (x.equals ("depth"))
		    {
			int u = NextInt (ST);
			if (u > DEPTH)
			    DEPTH = u;
		    }
		if (x.equals ("seldepth"))
		    {
			int u = NextInt (ST);
			if (u > SELDEPTH)
			    SELDEPTH = u;
		    }
		if (x.equals ("time"))
		    {
			int u = NextInt (ST);
			if (u > TIME)
			    TIME = u;
		    }
		if (x.equals ("nodes"))
		    {
			long u = NextLong (ST);
			if (u > NODES)
			    NODES = u;
		    }
		if (x.equals ("multipv"))
		    {
			MULTI_PV = NextInt (ST);
			PV[MULTI_PV + 1][0] = null; // HACK  UCI orders multipv
		    }
		if (x.equals ("pv"))
		    DoPV (ST);
		// fails valid, upon MULTI_PV delaying (StockFish)
		if (x.equals ("lowerbound"))
		    Lower = true;
		if (x.equals ("upperbound"))
		    Upper = true;
		if (x.equals ("score"))
		    {
			x = ST.nextToken ();
			
			Mate = x.equals ("mate"); // cp
			Lower = Upper = false;
			x = ST.nextToken ();
			if (x.equals ("lowerbound"))
			    {
				Lower = true;
				x = ST.nextToken ();
			    }
			if (x.equals ("upperbound"))
			    {
				Upper = true;
				x = ST.nextToken ();
			    }
			int score =  Integer.valueOf (x).intValue ();
			if (!CI_BOARD_POSITION.WTM)
			    score = -score; // invert score upon black
			Score = score;
		    }
		if (x.equals ("currmove"))
		    CURR_MOVE = ST.nextToken ();
		if (x.equals ("currmovenumber"))
		    {
			CURR_MOVE_NUMBER = Integer.valueOf (ST.nextToken ()).intValue ();
			CURR_MOVE_STR = "" + CURR_MOVE_NUMBER + "/" + CI_BOARD_POSITION.COUNT_OF_LEGAL_MOVES;
			int w = CI_BOARD_POSITION.FindMove (CURR_MOVE); // HACK // order
			if (w != -1)
			    CURR_MOVE_STR += " " + CI_BOARD_POSITION.move_list_annotated[w];
		    }			
		if (x.equals ("hashfull"))
		    HASH_FULL = NextInt (ST);
		if (x.equals ("nps"))
		    NPS = NextInt (ST);
		if (x.equals ("tbhits"))
		    TB_HITS = NextLong (ST);
		if (x.equals ("cpuload"))
		    CPU_LOAD = NextInt (ST);
	    }
	if (Score != -123456)
	    {
		SCORE[MULTI_PV] = Score;
		MATE[MULTI_PV] = Mate;
		LOWER[MULTI_PV] = Lower;
		UPPER[MULTI_PV] = Upper;
	    }
	IP.repaint ();
    }

    public void ParseLine (String S)
    {
	// while CF repaints attend SleepFor ?
	PARSE_LINE = true;
	if (MONTE_CARLO != null && MONTE_CARLO.WORKING)
	    MONTE_CARLO.ParseLineMC (S);
	else
	    UCI_Parser (S);	// switch parser : xboard ?
	PARSE_LINE = false;
    }

    public void ThreadInput ()
    {	
	if (HALTING || PRE_SENT)
	    return;
	THREAD_INPUT = true;
	while (IsReady ())
	    ParseLine (DemandLine ());
	LAST_INPUT = new Date ().getTime ();
	THREAD_INPUT = false;
    }

    public void InstanceThread () /* internal ? */
    {
        if (IsReady ())
            ThreadInput ();
    }

////////////////////////  ////////////////////////  ////////////////////////  

    public void StartInstance ()
    {
	if (!DirectCommunicatory ())
	    return;
	AttendMyDefaults ();
	DisplayInstance ();
	BelongInstance ();
	CF.TellInfo ("Has loaded " + COMM.id);
	CF.LOAD_INSTANCE = false;
	CI_BOARD_POSITION = new BoardPosition (CF.BOARD_PANEL.POS); // HACK
    }

    public void OldMultiPV_values ()
    {
	OPT_VALUE[num_MPV] = "" + MultiPV;
	if (MultiPV_Centi_Pawn != 987652)
	    OPT_VALUE[num_CP] = "" + MultiPV_Centi_Pawn;
    }

    public void actionPerformed (ActionEvent act_evt)
    {
	String S = act_evt.getActionCommand ();
	RemovePopUp ();
	if (S.equals ("Cancel"))
	    {
		OldMultiPV_values();
		return;
	    }
	MultiPV = Integer.valueOf (OPT_VALUE[num_MPV]).intValue ();
	SendTo ("setoption name MultiPV value " + MultiPV, true);
	if (MultiPV_Centi_Pawn != 987652)
	    {
		MultiPV_Centi_Pawn = Integer.valueOf (OPT_VALUE[num_CP]).intValue ();
		SendTo ("setoption name " + CP_str + " value " + MultiPV_Centi_Pawn, true);
	    }
	IP.RenewInstancePanel ();
    }

    public void stateChanged (ChangeEvent chg_evt)
    {
	JSpinner J = (JSpinner) (chg_evt.getSource ());
	if (J.getName ().equals ("MultiPV"))
	    OPT_VALUE[num_MPV] = J.getValue ().toString ();
	if (J.getName ().equals ("MultiPV_Centi_Pawn"))
	    OPT_VALUE[num_CP] = J.getValue ().toString ();
    }

    public void doPopUpMultiPV ()
    {
	if (MultiPV == 0 || POP_UP != null)
	    return; // HACK
	if (on)
	    SendHalt ();
	POP_UP = new JFrame ("MultiPV: " + COMM.id);	
	Box B = new Box (BoxLayout.Y_AXIS);
	Dimension D = new Dimension (5, 7);
	B.add (new Box.Filler (D, D, D));
	B.add (new Box.Filler (D, D, D));
	Box B1 = new Box (BoxLayout.X_AXIS);
	SpinnerNumberModel MODEL = new SpinnerNumberModel (MultiPV, 1, 250, 1);
	JLabel L = new JLabel ("MultiPV:  ");
	B1.add (L);
	JSpinner J = new JSpinner (MODEL);
	J.setName ("MultiPV");
        J.addChangeListener (this);
        J.setPreferredSize (new Dimension (60, 16));
        J.setMaximumSize (new Dimension (60, 16));
	B1.add (J);
	B.add (B1);
	Dimension minSize = new Dimension(5, 100);
	Dimension prefSize = new Dimension(5, 100);
	Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
	B.add (new Box.Filler (D, D, D));
	if (MultiPV_Centi_Pawn != 987652)
	    {
		Box B2 = new Box (BoxLayout.X_AXIS);
		SpinnerNumberModel M2 = new SpinnerNumberModel (MultiPV_Centi_Pawn, 0, 65535, 1);
		JLabel L2 = new JLabel ("CentiPawn: ");
		B2.add (L2);
		JSpinner J2 = new JSpinner (M2);
		J2.setName ("MultiPV_Centi_Pawn");
		J2.addChangeListener (this);
		J2.setPreferredSize (new Dimension (65, 16));
		J2.setMaximumSize (new Dimension (65, 16));
		B2.add (J2);
		B.add (B2);
		B.add (new Box.Filler (D, D, D));
	    }
	POP_UP.add (B);
	Box BX = new Box (BoxLayout.X_AXIS);
	JButton OK = new JButton ("OK");
	OK.setActionCommand ("OK");
	OK.addActionListener (this);
	BX.add (OK);
	BX.add (new Box.Filler (D, D, D));
	JButton CANCEL = new JButton ("Cancel");
	CANCEL.setActionCommand ("Cancel");
	CANCEL.addActionListener (this);
	BX.add (CANCEL);
	B.add (BX);
	POP_UP.addWindowListener (new WindowAdapter ()
            {
                public void windowClosing (WindowEvent win_evt)
                {
		    OldMultiPV_values ();
		    POP_UP = null;
                }
            }
            );
        POP_UP.setBackground (Color.lightGray);
        POP_UP.pack ();
        POP_UP.setSize (250, 125); // demand
        POP_UP.setResizable (false);
        POP_UP.setVisible (true);
    }

    public class EmptyPanel extends JPanel // HACK
    {
        public EmptyPanel ()
        {
        }

        public Dimension getPreferredSize ()
        {
            return new Dimension (400, 400);
        }

        public Dimension getMinimumSize ()
        {
            return new Dimension (400, 400);
        }
    }
}
