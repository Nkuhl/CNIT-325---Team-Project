package edu.purdue.comradesgui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.datatransfer.*; // clip board

public class ComradesFrame implements MouseListener, ActionListener, FocusListener
{
    BoardPanel BOARD_PANEL;
    MovePane MOVE_PANE;
    JPanel BOARD_PANEL_COLLECTIVE, MOVE_PANE_COLLECTIVE; // all
    Font chess_font;
    Font chess_font_small;
    String FONT_FILE_NAME;
    int SIZE;
    int MC_CPUS = 4;
    int MC_DEPTH = 12;
    int MC_LENGTH = 12;
    int MC_VLEN = 0;
    int MC_MAXV = 100;
    int MC_MINV = -100;
    JFrame j_frame;
    JTextArea TEXT_INFO;
    JScrollPane TEXT_INFO_JSP;
    JTextField FEN_AREA;
    JButton FEN_BUTTON, SWITCH_BUTTON;
    boolean POSITION_SEND = false;
    boolean LOAD_INSTANCE = false;
    boolean[] INSTANCES_ON_COPY;
    int NUMBER_OF_MOVES = 0;
    int COMMUNICATOR_COUNT;
    Communicator[] COMMUNICATOR_LIST;
    CommunicatorInstance[] INSTANCES;
    int instances;
    boolean READY_OK = false;
    boolean CHANGING_INSTANCES;
    JButton LOAD_BUTTON, DEFAULTS_BUTTON;
    Box INSTANCE_BOX;
    Color WHITE_PIECES, BLACK_PIECES, LIGHT_SQUARES, DARK_SQUARES;
    Color BACK_GROUND;
    Color CASTLING_COLOR, MOVE_ARROWS, YELLOW_BUTTON, CLICKED_COLOR;
    Color WHITE_MOVE_INDICATOR, BLACK_MOVE_INDICATOR, EN_PASSANT_COLOR;
    Color LAST_MOVE_COLOR;
    boolean WHITE_ALL_OUTLINE, BLACK_ALL_OUTLINE;
    boolean WHITE_HALF_OUTLINE, BLACK_HALF_OUTLINE;
    boolean WHITE_MATERIAL_OUTLINE, BLACK_MATERIAL_OUTLINE;
    Color SMALL_WHITE_PIECES, SMALL_BLACK_PIECES, SMALL_LIGHT_SQUARES, SMALL_DARK_SQUARES;
    Color SMALL_BACK_GROUND, SMALL_WHITE_MOVE_INDICATOR, SMALL_BLACK_MOVE_INDICATOR, SMALL_EN_PASSANT_COLOR;
    boolean SMALL_WHITE_ALL_OUTLINE, SMALL_BLACK_ALL_OUTLINE;
    boolean SMALL_WHITE_HALF_OUTLINE, SMALL_BLACK_HALF_OUTLINE;
    boolean SMALL_WHITE_MATERIAL_OUTLINE, SMALL_BLACK_MATERIAL_OUTLINE;
    File PGN_FILE = null; // recur
    String PIECE_STRING;

    public ComradesFrame ()
    {
	SetColors ();
	OnLoadComradesOptions ();
	SIZE = 40;
	MOVE_PANE = new MovePane (new JPanel ());
	BOARD_PANEL = new BoardPanel (new BoardPosition (this, MOVE_PANE));
	String FEN_STRING = BOARD_PANEL.POS.GetFEN ();
	BOARD_PANEL.setSize (10 * SIZE, 10 * SIZE);
	MakeFrame ();
	FEN_AREA.setText (FEN_STRING);
	redraw();
	COMMUNICATOR_COUNT = 0;
	COMMUNICATOR_LIST = new Communicator[256]; // limit
	instances = 0;
	INSTANCES = new CommunicatorInstance[256]; // limit
	INSTANCES_ON_COPY = new boolean[256];
	for (int i = 0; i < 256; i++)
	    INSTANCES_ON_COPY[i] = false;
	OnLoadCommunicators ();
    }
    
    public void SetColors ()
    {
	WHITE_PIECES = new Color (250, 240, 225);
        BLACK_PIECES = new Color (25, 15, 10);
	LIGHT_SQUARES = new Color (150, 170, 190);
        DARK_SQUARES = new Color (140, 120, 100);
	BACK_GROUND = new Color (180, 180, 180);
	CASTLING_COLOR = new Color (128, 96, 64);
        MOVE_ARROWS = new Color (32, 32, 255);
        YELLOW_BUTTON = Color.yellow;
        CLICKED_COLOR = Color.red;
	LAST_MOVE_COLOR = Color.orange;
        WHITE_MOVE_INDICATOR = Color.white;
        BLACK_MOVE_INDICATOR = Color.black;
        EN_PASSANT_COLOR = Color.orange;
	WHITE_ALL_OUTLINE = BLACK_ALL_OUTLINE = false;
	WHITE_HALF_OUTLINE = BLACK_HALF_OUTLINE = false;
	WHITE_MATERIAL_OUTLINE = BLACK_MATERIAL_OUTLINE = false;
	SMALL_WHITE_PIECES = Color.white;
	SMALL_BLACK_PIECES = Color.black;
	SMALL_LIGHT_SQUARES = Color.white;
	SMALL_DARK_SQUARES = Color.cyan;
	SMALL_BACK_GROUND = new Color (230, 190, 180);
	SMALL_WHITE_MOVE_INDICATOR = Color.white;
	SMALL_BLACK_MOVE_INDICATOR = Color.black;
	SMALL_EN_PASSANT_COLOR = Color.orange;
	SMALL_WHITE_ALL_OUTLINE = true;
	SMALL_BLACK_ALL_OUTLINE = false;
	SMALL_WHITE_HALF_OUTLINE = false;
	SMALL_BLACK_HALF_OUTLINE = false;
	SMALL_WHITE_MATERIAL_OUTLINE = true;
	SMALL_BLACK_MATERIAL_OUTLINE = false;
	FONT_FILE_NAME = new String ("MERIFONT.TTF"); // HACK
	PIECE_STRING = new String ("KQRBNP");
    }

    public void redraw ()
    {
	if (!READY_OK)
	    return;
	BOARD_PANEL.repaint();
	TEXT_INFO.repaint ();
	JScrollBar JSB = TEXT_INFO_JSP.getVerticalScrollBar ();
	JSB.setValue (JSB.getMaximum () - 12); // HACK
	JSB.repaint ();
    }
    

////////////////////////////////////////////////////////////////

    public void AdditionalCommunicator (Communicator COMM)
    {
	COMMUNICATOR_LIST[COMMUNICATOR_COUNT++] = COMM;
	TellInfo ("Additional applied for " + COMM.id);
    }

    public void DeleteUnNeededCommunicator (Communicator COMM)
    {
	int i, j;
	for (i = 0; i < COMMUNICATOR_COUNT; i++)
	    if (COMMUNICATOR_LIST[i].id.equals (COMM.id))
		break;
	for (j = COMMUNICATOR_COUNT - 1; j > i; j--)
	    COMMUNICATOR_LIST[j - 1] = COMMUNICATOR_LIST[j];
	COMMUNICATOR_COUNT--;
	TellInfo ("Delete unneeded " + COMM.id);
    }

    public void CommunicatorLineStartUp (Communicator COMM, String S)
    {
	if (S.startsWith ("id "))
	    {
		COMM.id = new String (S.substring (3));
		return;
	    }
	if (S.equals ("ici"))
	    {
		COMM.ICI = true;
		return;
	    }
	if (S.equals ("can MonteCarlo"))
	    {
		COMM.COMRADES_MONTE_CARLO = true;
		return;
	    }
	if (S.startsWith ("Path "))
	    {
		COMM.path = new String (S.substring (5));
		return;		
	    }
	if (S.startsWith ("RunTimeOptions "))
	    {
		COMM.RunTimeOptions = new String (S.substring (15));
		return;		
	    }
	// uggh, UCI allows spaces in names :( // demur StringTokenizer
	int u;
	String NAME = null, TYPE = null, VALUE = null;
	u = S.indexOf (" check");
	if (u != -1)
	    {
		NAME = S.substring (0, u);
		TYPE = new String ("check");
		VALUE = S.substring (u + 7);
	    }
	u = S.indexOf (" spin");
	if (u != -1)
	    {
		NAME = S.substring (0, u);
		TYPE = new String ("spin");
		VALUE = S.substring (u + 6);
	    }
	u = S.indexOf (" button");
	if (u != -1)
	    {
		NAME = S.substring (0, u);
		TYPE = new String ("button");
		VALUE = S.substring (u + 8);
	    }
	u = S.indexOf (" combo");
	if (u != -1)
	    {
		NAME = S.substring (0, u);
		TYPE = new String ("combo");
		VALUE = S.substring (u + 7);
	    }
	u = S.indexOf (" string");
	if (u != -1)
	    {
		NAME = S.substring (0, u);
		TYPE = new String ("string");
		VALUE = S.substring (u + 8);
	    }
	u = S.indexOf (" file");
	if (u != -1)
	    {
		NAME = S.substring (0, u);
		TYPE = new String ("file");
		VALUE = S.substring (u + 6);
	    }
	u = S.indexOf (" directory-multi-reset");
	if (u != -1)
	    {
		NAME = S.substring (0, u);
		TYPE = new String ("directory-multi-reset");
		VALUE = S.substring (u + 23);
	    }
	else
	    {
		u = S.indexOf (" directory");
		if (u != -1)
		    {
			NAME = S.substring (0, u);
			TYPE = new String ("directory");
			VALUE = S.substring (u + 11);
		    }
	    }
	u = S.indexOf (" binary");
	if (u != -1)
	    {
		NAME = S.substring (0, u);
		TYPE = new String ("binary");
		VALUE = S.substring (u + 8);
	    }
	COMM.OPT_NAME[COMM.opt_count] = new String (NAME);
	COMM.OPT_TYPE[COMM.opt_count] = new String (TYPE);
	COMM.OPT_VALUE[COMM.opt_count++] = new String (VALUE);
    }

    public void CommunicatorDataLoad (String S)
    {
	File file = new File (S);
	if (!file.exists ())
	    {
		TellInfo ("Defunct file: " + S);
		return;
	    }
	try
	    {
		FileReader FR = new FileReader (file);
		BufferedReader BR = new BufferedReader (FR);
                TellInfo ("Reading " + S);
		Communicator COMM = new Communicator (S, this, false);
		while (!BR.ready ())
		    COMM.SleepFor (10); // HACK
		while (BR.ready ())
		    CommunicatorLineStartUp (COMM, BR.readLine ());
		BR.close ();
		AdditionalCommunicator (COMM);
	    }
	catch (IOException io_exc)
	    {
		TellInfo ("No dispensation unto file: " + S);
	    }
    }

    public void OnLoadCommunicators ()
    {
	File file = new File ("Comrades.StartUp");
	if (!file.exists ())
	    {
		TellInfo ("No emergence for Comrades.StartUp");
		return;
	    }
	try
	    {
		FileReader FR = new FileReader (file);
		BufferedReader BR = new BufferedReader (FR);
                TellInfo ("Reading Comrades.StartUp file");
		while (BR.ready ())
		    CommunicatorDataLoad (BR.readLine ());
		BR.close ();
	    }
	catch (IOException io_exc)
	    {
		TellInfo ("No dispensation unto Comrades.StartUp file");
	    }
    }

    public void DoComradesOption (String S)
    {
	StringTokenizer ST = new StringTokenizer (S);
	String N = ST.nextToken ();
	String V = ST.nextToken ();
	if (N.equals ("WhitePieces"))
	    WHITE_PIECES = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("BlackPieces"))
	    BLACK_PIECES = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("LightSquares"))
	    LIGHT_SQUARES = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("DarkSquares"))
	    DARK_SQUARES = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("BackGround"))
	    BACK_GROUND = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("CastlingColor"))
	    CASTLING_COLOR = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("EnPassantColor"))
	    EN_PASSANT_COLOR = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("MoveArrows"))
	    MOVE_ARROWS = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("YellowButton"))
	    YELLOW_BUTTON = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("WhiteMoveIndicator"))
	    WHITE_MOVE_INDICATOR = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("BlackMoveIndicator"))
	    BLACK_MOVE_INDICATOR = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("WhiteAllOutline"))
	    WHITE_ALL_OUTLINE = V.equals ("true");
	if (N.equals ("BlackAllOutline"))
	    BLACK_ALL_OUTLINE = V.equals ("true");
	if (N.equals ("WhiteHalfOutline"))
	    WHITE_HALF_OUTLINE = V.equals ("true");
	if (N.equals ("BlackHalfOutline"))
	    BLACK_HALF_OUTLINE = V.equals ("true");
	if (N.equals ("WhiteMaterialOutline"))
	    WHITE_MATERIAL_OUTLINE = V.equals ("true");
	if (N.equals ("BlackMaterialOutline"))
	    BLACK_MATERIAL_OUTLINE = V.equals ("true");

	if (N.equals ("SmallWhitePieces"))
	    SMALL_WHITE_PIECES = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("SmallBlackPieces"))
	    SMALL_BLACK_PIECES = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("SmallLightSquares"))
	    SMALL_LIGHT_SQUARES = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("SmallDarkSquares"))
	    SMALL_DARK_SQUARES = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("SmallBackGround"))
	    SMALL_BACK_GROUND = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("SmallEnPassantColor"))
	    SMALL_EN_PASSANT_COLOR = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("SmallWhiteMoveIndicator"))
	    SMALL_WHITE_MOVE_INDICATOR = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("SmallBlackMoveIndicator"))
	    SMALL_BLACK_MOVE_INDICATOR = new Color (Integer.valueOf (V).intValue (), true);
	if (N.equals ("SmallWhiteAllOutline"))
	    SMALL_WHITE_ALL_OUTLINE = V.equals ("true");
	if (N.equals ("SmallBlackAllOutline"))
	    SMALL_BLACK_ALL_OUTLINE = V.equals ("true");
	if (N.equals ("SmallWhiteHalfOutline"))
	    SMALL_WHITE_HALF_OUTLINE = V.equals ("true");
	if (N.equals ("SmallBlackHalfOutline"))
	    SMALL_BLACK_HALF_OUTLINE = V.equals ("true");
	if (N.equals ("SmallWhiteMaterialOutline"))
	    SMALL_WHITE_MATERIAL_OUTLINE = V.equals ("true");
	if (N.equals ("SmallBlackMaterialOutline"))
	    SMALL_BLACK_MATERIAL_OUTLINE = V.equals ("true");
	if (N.equals ("FontFile"))
	    FONT_FILE_NAME = new String (V);
	if (N.equals ("PieceString"))
	    PIECE_STRING = new String (V);
    }

    public void OnLoadComradesOptions ()
    {
	File file = new File ("Comrades.Default.Options");
	if (!file.exists ())
	    {
		// TellInfo ("No emergence for Comrades.Default.Options");
		return;
	    }
	try
	    {
		FileReader FR = new FileReader (file);
		BufferedReader BR = new BufferedReader (FR);
                // TellInfo ("Reading Comrades.Default.Options file");
		while (!BR.ready ())
		    Thread.sleep (10);
		while (BR.ready ())
		    DoComradesOption (BR.readLine ());
		BR.close ();
	    }
	catch (IOException io_exc)
	    {
	    }
	catch (InterruptedException int_exc) // HACK
	    {
	    }
    }

    public void SaveApplyStartUp ()
    {
	try
	    {
		FileWriter FW = new FileWriter ("Comrades.StartUp");
		BufferedWriter BW = new BufferedWriter (FW);
		PrintWriter PW = new PrintWriter (BW, true);
		for (int i = 0; i < COMMUNICATOR_COUNT; i++)
		    PW.println (COMMUNICATOR_LIST[i].id);
		PW.close ();
		TellInfo ("Applied to Comrades.StartUp");
	    }
	catch (IOException io_exc)
	    {
		TellInfo ("Error for Comrades.StartUp");
	    }
    }

////////////////////////////////////////////////////////////////

    public void HaltInstances ()
    {
	for (int i = 0; i < instances; i++)
	    if (INSTANCES[i].MONTE_CARLO != null)
		INSTANCES[i].MONTE_CARLO.EndMonteCarlo (); // HACK
	for (int i = 0; i < instances; i++)
	    INSTANCES_ON_COPY[i] = INSTANCES[i].on;	
	for (int i = 0; i < instances; i++)
	    if (INSTANCES[i].on)
		INSTANCES[i].SendHalt ();
    }

    public void EquipInstances ()
    {
	for (int i = 0; i < instances; i++)
	    if (INSTANCES_ON_COPY[i])
		INSTANCES[i].SendGoInfinite ();
    }

    public void NewGameInstances ()
    {
	for (int i = 0; i < instances; i++)
	    INSTANCES[i].UCI_new_game ();
    }

////////////////////////////////////////////////////////////////

    public void mouseEntered (MouseEvent mou_evt)
    {
	if (mou_evt.getComponent () == FEN_AREA)
	    BOARD_PANEL.POS.MOVE_TREE.FOCUS = false;
	FEN_AREA.requestFocus ();
    }
    public void mouseExited (MouseEvent mou_evt) { }
    public void mousePressed (MouseEvent mou_evt) { }
    public void mouseReleased (MouseEvent mou_evt) { }
    public void mouseClicked (MouseEvent mou_evt) { }

    public void BoardPositionChanged ()
    {
	for (int i = 0; i < instances; i++)
	    INSTANCES[i].ClearInformatory ();
	FEN_AREA.setText (BOARD_PANEL.POS.GetFEN ()); // HACK
    }

    public void ParseFEN (String FEN_STRING) // for the BOARD_POSITION ?
    {
	HaltInstances ();
	BOARD_PANEL.POS.SetFEN (new StringTokenizer (FEN_STRING));
	BOARD_PANEL.POS.NewMoveTree ();
	BOARD_PANEL.POS.MOVE_TREE.PaintPanel ();
	BoardPositionChanged ();
	BOARD_PANEL.repaint ();
	NewGameInstances ();
	EquipInstances ();
    }

    public void LoadCommunicatorInstance (Communicator COMM)
    {
	while (LOAD_INSTANCE) // one at time
	    COMM.SleepFor (25);
	LOAD_INSTANCE = true;
	TellInfo ("Loading " + COMM.name);
	new CommunicatorInstance (COMM, this); // terminal (infinite)
    }

    public void SetupNewCommunicator (File FILE)
    {
	Communicator COMM = new Communicator (FILE.getAbsolutePath (), this, true);
	COMM.path = new String (COMM.name); // HACK
	if (!COMM.LoadCommunicator ())
	    return;
	COMM.ModifyDefaults ();
    }

    public void EnsueDefaults (Communicator COMM)
    {
	if (!COMM.LoadCommunicator ())
	    return;
	COMM.ModifyDefaults ();
    }

    public void RegisterNew ()
    {
        JFileChooser JFC;
	JFC = new JFileChooser (System.getProperty ("user.dir")); // HACK
        int Value =  JFC.showOpenDialog (JFC);
        if (Value != JFileChooser.APPROVE_OPTION)
	    return;
        File FILE = JFC.getSelectedFile ();
        if (!FILE.exists())
            {
                TellInfo ("File does not exist");
                return;
            }
	SetupNewCommunicator (FILE);
    }

    public void MakePopUp (String S)
    {
	JPopupMenu POP_UP = new JPopupMenu (S); // S redundant
	for (int i = 0; i < COMMUNICATOR_COUNT; i++)
	    {
		CJMenuItem MENU_ITEM = new CJMenuItem (COMMUNICATOR_LIST[i]);
		MENU_ITEM.setActionCommand (S);
		MENU_ITEM.addActionListener (this);
		POP_UP.add (MENU_ITEM);
	    }
	if (S.equals ("LOAD"))
	    POP_UP.setInvoker (LOAD_BUTTON);
	if (S.equals ("DEFAULTS"))
	    POP_UP.setInvoker (DEFAULTS_BUTTON);
	POP_UP.show (POP_UP.getInvoker (), 30, 10);
    }

    public void DoPastePGN ()
    {
	{
	    Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
	    Transferable contents = clipboard.getContents (null);
	    boolean hasTransferableText =
		(contents != null) &&
		contents.isDataFlavorSupported (DataFlavor.stringFlavor);
	    try
		{
		    String result = (String) contents.getTransferData(DataFlavor.stringFlavor);
		    BufferedReader BR = new BufferedReader (new StringReader (result));
		    boolean value = true;
		    while (value && BR.ready ()) // flakey
			{
			    DataPGN D = new DataPGN (this);
			    if (!D.ParsePGN (BR))
				value = false; // HACk
			    if (!value && D.White == null)
				break;
			    String S = "#" + (BOARD_PANEL_COLLECTIVE.getComponentCount ()) + ": " + D.White + " " + D.Black + " " + D.Result;
			    D.BOARD_PANEL.Name = S;
			    BOARD_PANEL_COLLECTIVE.add (D.BOARD_PANEL, S);
			    MOVE_PANE_COLLECTIVE.add (D.MOVE_PANE, S);
			}
		}
	    catch (UnsupportedFlavorException ex) {}
	    catch (IOException ex) {}
	    DealCards (((BoardPanel) (BOARD_PANEL_COLLECTIVE.getComponent (BOARD_PANEL_COLLECTIVE.getComponentCount () - 1))).Name);
	    redraw ();
	}
    }

    public void DoLoadPGN ()
    {
        JFileChooser JFC;
	if (PGN_FILE != null)
	    JFC = new JFileChooser (PGN_FILE);
	else
	    JFC = new JFileChooser (System.getProperty ("user.dir")); // HACK
        int Value =  JFC.showOpenDialog (JFC);
        if (Value != JFileChooser.APPROVE_OPTION)
	    return;
        PGN_FILE = JFC.getSelectedFile ();
	try
	    {
		BufferedReader BR = new BufferedReader (new FileReader (PGN_FILE));
		while (BR.ready ())
		    {
			DataPGN D = new DataPGN (this);
			if (!D.ParsePGN (BR)) // multiple ?
			    ; // break; //value ?
			String S = "#" + (BOARD_PANEL_COLLECTIVE.getComponentCount ()) + ": " + D.White + " " + D.Black + " " + D.Result;
			D.BOARD_PANEL.Name = S;
			BOARD_PANEL_COLLECTIVE.add (D.BOARD_PANEL, S);
			MOVE_PANE_COLLECTIVE.add (D.MOVE_PANE, S);
		    }
		BR.close (); // ensure
		redraw ();
	    }
	catch (IOException io_exc) { }
    }

    public void DoButtonPGN ()
    {
	new DemandPGN (this);
	if (true)
	    return;
	JPopupMenu POP_UP = (new JPopupMenu ("Games"));
	JMenuItem LOAD_FILE = new JMenuItem ("Load File");
	LOAD_FILE.addActionListener (new ActionListener ()
	    {
		public void actionPerformed (ActionEvent act_evt)
		{
		    DoLoadPGN ();
		}
	    }
	    );
	POP_UP.add (LOAD_FILE);
	POP_UP.addSeparator ();
	for (int i = 0; i < MOVE_PANE_COLLECTIVE.getComponentCount (); i++)
	    {
		final String S = new String (((BoardPanel) (BOARD_PANEL_COLLECTIVE.getComponent (i))).Name);
		JMenuItem JMI = new JMenuItem (S);
		JMI.addActionListener (new ActionListener ()
		    {
			public void actionPerformed (ActionEvent act_evt)
			{
			    DealCards (S);
			}
		    }
		    );
		POP_UP.add (JMI);
	    }
	POP_UP.setInvoker (SWITCH_BUTTON);
	POP_UP.show (POP_UP.getInvoker (), 30, 10);
    }

    public void DealCards (String S)
    {
	HaltInstances ();
	CardLayout CL; // HACK
	CL = (CardLayout) (BOARD_PANEL_COLLECTIVE.getLayout ());
	CL.show (BOARD_PANEL_COLLECTIVE, S);
	CL = (CardLayout) (MOVE_PANE_COLLECTIVE.getLayout ());
	CL.show (MOVE_PANE_COLLECTIVE, S);
	TidyCards ();
	EquipInstances ();
    }

    public void TidyCards () // HACK
    {
	for (int i = 0; i < BOARD_PANEL_COLLECTIVE.getComponentCount (); i++)
	    if (BOARD_PANEL_COLLECTIVE.getComponent (i).isVisible ())
		BOARD_PANEL = (BoardPanel) (BOARD_PANEL_COLLECTIVE.getComponent (i));
	for (int i = 0; i < MOVE_PANE_COLLECTIVE.getComponentCount (); i++)
	    if (MOVE_PANE_COLLECTIVE.getComponent (i).isVisible ())
		MOVE_PANE = (MovePane) (MOVE_PANE_COLLECTIVE.getComponent (i));
	BoardPositionChanged ();
	BOARD_PANEL.POS.MOVE_TREE.FOCUS = true;
	BOARD_PANEL.POS.MOVE_TREE.PaintPanel (); // HACK
	redraw ();	
    }

    public void ForWardCards ()
    {
	HaltInstances ();
	CardLayout CL; // HACK
	CL = (CardLayout) (BOARD_PANEL_COLLECTIVE.getLayout ());
	CL.next (BOARD_PANEL_COLLECTIVE);
	CL = (CardLayout) (MOVE_PANE_COLLECTIVE.getLayout ());
	CL.next (MOVE_PANE_COLLECTIVE);
	TidyCards ();
	EquipInstances ();
    }

    public void BackWardCards ()
    {
	HaltInstances ();
	CardLayout CL; // HACK
	CL = (CardLayout) (BOARD_PANEL_COLLECTIVE.getLayout ());
	CL.previous (BOARD_PANEL_COLLECTIVE);
	CL = (CardLayout) (MOVE_PANE_COLLECTIVE.getLayout ());
	CL.previous (MOVE_PANE_COLLECTIVE);
	TidyCards ();
	EquipInstances ();
    }

    public void DoEmitPGN (boolean DoTree)
    {
        JFrame PGN_FRAME = new JFrame ("PGN of this");
        final JTextArea JTA = new JTextArea (1, 30); /* bind final */
        JTA.setFont (new Font ("Monospaced", 0, 12));
        JTA.setEditable (false);
        JScrollPane JSP = new JScrollPane (JTA);
        JSP.getViewport ().add (JTA);
        JScrollBar VERT = JSP.getVerticalScrollBar ();
        VERT.addAdjustmentListener (new AdjustmentListener()
            { public void adjustmentValueChanged (AdjustmentEvent e) { JTA.repaint (); } }); // HACK // HACK
	String STRING = new String ("");
	for (int i = 0; i < BOARD_PANEL_COLLECTIVE.getComponentCount (); i++)
	    STRING += ((BoardPanel) (BOARD_PANEL_COLLECTIVE.getComponent (i))).POS.MOVE_TREE.EmitPGN (DoTree);
	JTA.setText (STRING);
	PGN_FRAME.add (JSP);
        PGN_FRAME.setBackground (Color.lightGray);
        PGN_FRAME.pack ();
        JSP.setPreferredSize (new Dimension (500, 500));
        PGN_FRAME.setSize (600, 600); // demand
        PGN_FRAME.setResizable (false);
        PGN_FRAME.setVisible (true);	
    }

    public void NewPGN ()
    {
        int c = BOARD_PANEL_COLLECTIVE.getComponentCount ();
        String S = "#" + c + ": White Black *";
        DataPGN D = new DataPGN (this);
        D.BOARD_PANEL.Name = S;
        BOARD_PANEL_COLLECTIVE.add (D.BOARD_PANEL, S);
        MOVE_PANE_COLLECTIVE.add (D.MOVE_PANE, S);
        DealCards (S);
    }

    public void actionPerformed (ActionEvent act_evt)
    {
	String S = act_evt.getActionCommand ();
	if (S.equals ("OPTIONS"))
	    new ComradesOptioner (this); // modal
	if (S.equals ("SetUpPosition"))
	    BOARD_PANEL.StartSetUp ();
	if (S.equals ("FEN"))
	    {
		if ((act_evt.getModifiers() & ActionEvent.SHIFT_MASK) != 0)
		    FEN_AREA.setText ("");
		else
		    ParseFEN (FEN_AREA.getText ());
	    }
	if (S.equals ("Switch")) // old namingery: PGN
	    {
		if ((act_evt.getModifiers() & ActionEvent.SHIFT_MASK) != 0)
		    NewPGN ();
		else
		    DoButtonPGN ();
	    }
	if (S.equals ("Emit"))
	    {
		if ((act_evt.getModifiers() & ActionEvent.SHIFT_MASK) != 0)
		    DoEmitPGN (false);
		else
		    DoEmitPGN (true);
	    }		
	if (S.equals ("RegisterNew"))
	    RegisterNew ();
	if (S.equals ("LOAD"))
	    LoadCommunicatorInstance (((CJMenuItem) (act_evt.getSource ())).COMM);
	if (S.equals ("DEFAULTS"))
	    {
		if ((act_evt.getModifiers () & ActionEvent.CTRL_MASK) != 0 &&
		    (act_evt.getModifiers () & ActionEvent.ALT_MASK) != 0) // HACK
		    {
			DeleteUnNeededCommunicator
			    (((CJMenuItem) (act_evt.getSource ())).COMM);
			SaveApplyStartUp ();
		    }
		else
		    EnsueDefaults (((CJMenuItem) (act_evt.getSource ())).COMM);
	    }
	if (S.equals ("ModifyDefaults"))
	    MakePopUp ("DEFAULTS");
	if (S.equals ("LoadCommunicator"))
	    MakePopUp ("LOAD");
    }

    public void ERROR (String STRING)
    {
	System.out.println (STRING);
	System.exit (123456);
    }
    
    public void InitChessFont (String S, int SIZE)
    {
	FileInputStream FIS = null;
	Font FONT = null;
	try
	    {
		FIS = new FileInputStream (S);
	    }
	catch (IOException io_e)
	    {
		ERROR ("Cannot find font file " + S);
	    }
	try
	    {
		FONT = Font.createFont (Font.TRUETYPE_FONT, FIS);
	    }
	catch (IOException io_e)
	    {
		ERROR ("IOException in font creation");
	    }
	catch (FontFormatException io_e)
	    {
		ERROR("Font format exception");
	    }
	try
	    {
		FIS.close();
	    }
	catch (IOException io_e)
	    {
		ERROR ("Failure to close fonts");
	    }
	FONT_FILE_NAME = new String (S);
	chess_font = FONT.deriveFont ((float) SIZE);
	chess_font_small = chess_font.deriveFont (16.0f); // small
    }

    public void TellInfo (String STRING)
    {
	TEXT_INFO.append (STRING + "\n");
	TEXT_INFO.setCaretPosition (TEXT_INFO.getText ().length ());
	TEXT_INFO.repaint ();
	TEXT_INFO_JSP.repaint ();
	JScrollBar JSB = TEXT_INFO_JSP.getVerticalScrollBar ();
	JSB.setValue (JSB.getMaximum ());
	JSB.repaint ();
    }

    public void focusGained (FocusEvent foc_evt)
    {
	if (BOARD_PANEL.POS.MOVE_TREE.FOCUS)
	    BOARD_PANEL.POS.MOVE_TREE.NOW.LABEL.requestFocus (); // HACK
    }

    public void focusLost (FocusEvent foc_evt) // empty (null)
    {
    }

    public Box Make_FEN_Box ()
    {
	Box FEN_BOX = new Box (BoxLayout.X_AXIS);
	FEN_AREA = new JTextField (50);
	FEN_AREA.setFont (new Font ("Monospaced", 0, 9));
	FEN_AREA.setEditable (true);
	FEN_AREA.addFocusListener (this); // HACK
	FEN_AREA.addMouseListener (this); // HACK
	FEN_AREA.setPreferredSize (new Dimension (410, 20));
	FEN_AREA.setMinimumSize (new Dimension (400, 20));
	FEN_BOX.add (FEN_AREA);
	FEN_AREA.setAlignmentX (0.5f);
	FEN_BUTTON = new JButton ("FEN");
	FEN_BUTTON.setMargin (new Insets (1, 3, 1, 3));
	FEN_BUTTON.setActionCommand ("FEN");
	FEN_BUTTON.addActionListener (this);
	FEN_BOX.add (FEN_BUTTON);
	FEN_BOX.setAlignmentX (0.0f); // query ?
	FEN_BOX.setPreferredSize (new Dimension (430, 20));
	FEN_BOX.setMaximumSize (new Dimension (440, 20));
	return FEN_BOX;
    }

    public JToolBar MakeToolBar ()
    {
	JToolBar TOOL_BAR = new JToolBar ();
	TOOL_BAR.setAlignmentX (0.0f); // HACK ?
	TOOL_BAR.addSeparator (); // fancy
	JButton OPTIONS_BUTTON = new JButton ("OPTIONS");
        Font NEWBUY_FONT = new Font ("SansSerif", Font.BOLD, 11);
        OPTIONS_BUTTON.setFont (NEWBUY_FONT);
	OPTIONS_BUTTON.setActionCommand ("OPTIONS");
	OPTIONS_BUTTON.addActionListener (this);
	TOOL_BAR.add (OPTIONS_BUTTON);
	JButton REGISTER_NEW = new JButton ("RegisterNew");
        REGISTER_NEW.setFont (NEWBUY_FONT);
	REGISTER_NEW.setActionCommand ("RegisterNew");
	REGISTER_NEW.addActionListener (this);
	TOOL_BAR.add (REGISTER_NEW);
	LOAD_BUTTON = new JButton ("Load");
        LOAD_BUTTON.setFont (NEWBUY_FONT);
	LOAD_BUTTON.setActionCommand ("LoadCommunicator");
	LOAD_BUTTON.addActionListener (this);
	TOOL_BAR.add (LOAD_BUTTON);
	DEFAULTS_BUTTON = new JButton ("Defaults");
        DEFAULTS_BUTTON.setFont (NEWBUY_FONT);
	DEFAULTS_BUTTON.setActionCommand ("ModifyDefaults");
	DEFAULTS_BUTTON.addActionListener (this);
	TOOL_BAR.add (DEFAULTS_BUTTON);
	JButton SET_UP_BUTTON = new JButton ("SetBoard");
        SET_UP_BUTTON.setFont (NEWBUY_FONT);
	SET_UP_BUTTON.setActionCommand ("SetUpPosition");
	SET_UP_BUTTON.addActionListener (this);
	TOOL_BAR.add (SET_UP_BUTTON);
	SWITCH_BUTTON = new JButton ("Switch");
        SWITCH_BUTTON.setFont (NEWBUY_FONT);
	SWITCH_BUTTON.setActionCommand ("Switch");
	SWITCH_BUTTON.addActionListener (this);
	TOOL_BAR.add (SWITCH_BUTTON);
	JButton EMIT_BUTTON = new JButton ("Emit");
        EMIT_BUTTON.setFont (NEWBUY_FONT);
	EMIT_BUTTON.setActionCommand ("Emit");
	EMIT_BUTTON.addActionListener (this);
	TOOL_BAR.add (EMIT_BUTTON);
	TOOL_BAR.setFloatable (false);
	//	TOOL_BAR.addSeparator ();
	return TOOL_BAR;
    }

    public void DoGraphics ()
    {
	Box LARGE_BOX = new Box (BoxLayout.X_AXIS);
	Box LEFT_BOX = new Box (BoxLayout.Y_AXIS);
	Box RIGHT_BOX = new Box (BoxLayout.Y_AXIS);
	BOARD_PANEL_COLLECTIVE = new JPanel (new CardLayout ());
	BOARD_PANEL_COLLECTIVE.add (BOARD_PANEL, "#0: White Black *");
	BOARD_PANEL.Name = "#0: White Black *";
	LEFT_BOX.add (BOARD_PANEL_COLLECTIVE);
	MOVE_PANE_COLLECTIVE = new JPanel (new CardLayout ());
	MOVE_PANE_COLLECTIVE.add (MOVE_PANE, "#0: White Black *");
	LEFT_BOX.add (MOVE_PANE_COLLECTIVE);

	TEXT_INFO = new JTextArea (10, 50);
	TEXT_INFO.setFont (new Font("Monospaced", 0, 12));
	TEXT_INFO.setEditable (false);
	TEXT_INFO_JSP = new JScrollPane (TEXT_INFO);
	//	LEFT_BOX.add (TEXT_INFO_JSP); // in the off

	RIGHT_BOX.add (Make_FEN_Box ());
	RIGHT_BOX.add (MakeToolBar ());
	INSTANCE_BOX = new Box (BoxLayout.Y_AXIS);
	INSTANCE_BOX.add (new EmptyPanel ());
	JScrollPane INSTANCE_BOX_PANE = new JScrollPane (INSTANCE_BOX);
	INSTANCE_BOX_PANE.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	INSTANCE_BOX_PANE.setPreferredSize (new Dimension (450, 700));
	INSTANCE_BOX_PANE.setAlignmentX (0.0f);
	RIGHT_BOX.add (INSTANCE_BOX_PANE);
	RIGHT_BOX.setPreferredSize (new Dimension (450, 700));
	LARGE_BOX.add (LEFT_BOX);
	LARGE_BOX.add (RIGHT_BOX);
	j_frame.add (LARGE_BOX);
    }

    public class EmptyPanel extends JPanel // HACK
    {
	public EmptyPanel ()
	{
	}

	public void paintComponent (Graphics G) // Font exemplary
	{
	    G.setColor (Color.cyan);
	    G.fillRect (0, 0, 500, 1000);
	    G.setColor (Color.black);
	    G.setFont (chess_font.deriveFont (20.0f));
	    char[] BUFFER = new char[1];
	    int h = 50, v = 50;
	    for (int i = 0; i < 256; i++)
		{
		    BUFFER[0] = (char) i;
		    G.drawChars (BUFFER, 0, 1, h, v);
		    h += 20;
		    if (h > 300)
			{
			    v += 20;
			    h = 50;
			}
		}
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

    public void MakeFrame ()
    {
	j_frame = new JFrame ("ComradesGUI Proto Type V2");
	j_frame.addWindowListener
	    (new WindowAdapter()
		{
		    public void windowClosing (WindowEvent win_evt)
		    {
			System.exit (0);
		    }
		}
		);
	InitChessFont (FONT_FILE_NAME, SIZE); // void
	DoGraphics ();
	j_frame.setBackground (Color.lightGray);
	j_frame.pack ();
	j_frame.setSize (850, 675);
	j_frame.setResizable (false);
	j_frame.setVisible (true);
	READY_OK = true;
    }
}
