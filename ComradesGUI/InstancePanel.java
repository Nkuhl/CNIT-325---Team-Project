import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.math.BigInteger;

public class InstancePanel extends JPanel implements ActionListener
{
    CommunicatorInstance CI;
    private Dimension Dim;
    private Graphics Gr;
    private Image image;
    int CLICKED_x = 0;
    int CLICKED_y = 0;
    int CLICKED_b = 0;
    boolean CLICKED = false;
    Color TEXT_COLOR;
    boolean IS_PAINTING = false;
    boolean PERIODIC = true; // demand
    int HORIZONTAL = 345;
    int VERT_ORIG = 1200; // HACK
    int VERTICAL = VERT_ORIG;

    int SIZE = 16;
    int HORZ_OFF_SET = 16;
    int VERT_OFF_SET = 52 + 16;
    int OFF_SET = 16;
    long LAST_DRAW, THIS_DRAW;

    JLabel NAME_LABEL;
    JButton MULTI_PV_BUTTON;

    Color WHITE_PIECES, BLACK_PIECES, LIGHT_SQUARES, DARK_SQUARES;
    Color BACK_GROUND, WHITE_MOVE_INDICATOR, BLACK_MOVE_INDICATOR, EN_PASSANT_COLOR;
    boolean WHITE_ALL_OUTLINE, BLACK_ALL_OUTLINE;
    boolean WHITE_HALF_OUTLINE, BLACK_HALF_OUTLINE;
    boolean WHITE_MATERIAL_OUTLINE, BLACK_MATERIAL_OUTLINE;

    public InstancePanel (CommunicatorInstance ci)
    {
	super ();
	CI = ci;
	SetColors ();
	TEXT_COLOR = new Color (30, 30, 30);
	setLayout (null); // HACK
	LAST_DRAW = new Date ().getTime () - 1000;
	NameLabel ();
	if (CI.MultiPV != 0)
	    MultiPVButton ();
	NewGameButton ();
	AllOptionsButton ();
	DestroyButton ();
    }

    public void SetColors ()
    {
	WHITE_PIECES = CI.CF.SMALL_WHITE_PIECES;
	BLACK_PIECES = CI.CF.SMALL_BLACK_PIECES;
	LIGHT_SQUARES = CI.CF.SMALL_LIGHT_SQUARES;
	DARK_SQUARES = CI.CF.SMALL_DARK_SQUARES;
	BACK_GROUND = CI.CF.SMALL_BACK_GROUND;
	WHITE_MOVE_INDICATOR = CI.CF.SMALL_WHITE_MOVE_INDICATOR;
	BLACK_MOVE_INDICATOR = CI.CF.SMALL_BLACK_MOVE_INDICATOR;
	EN_PASSANT_COLOR = CI.CF.SMALL_EN_PASSANT_COLOR;
	WHITE_ALL_OUTLINE = CI.CF.SMALL_WHITE_ALL_OUTLINE;
	BLACK_ALL_OUTLINE = CI.CF.SMALL_BLACK_ALL_OUTLINE;
	WHITE_HALF_OUTLINE = CI.CF.SMALL_WHITE_HALF_OUTLINE;
	BLACK_HALF_OUTLINE = CI.CF.SMALL_BLACK_HALF_OUTLINE;
	WHITE_MATERIAL_OUTLINE = CI.CF.SMALL_WHITE_MATERIAL_OUTLINE;
	BLACK_MATERIAL_OUTLINE = CI.CF.SMALL_BLACK_MATERIAL_OUTLINE;
    }

    public Dimension getPreferredSize ()
    {
	return new Dimension (HORIZONTAL, VERTICAL); // attend
    }

    public Dimension getMinimumSize ()
    {
	return new Dimension (HORIZONTAL, VERTICAL);
    }

    public Dimension getMaximumSize ()
    {
	return new Dimension (HORIZONTAL, VERTICAL);
    }

    public void NameLabel ()
    {
	NAME_LABEL = new JLabel (CI.COMM.id);
	NAME_LABEL.setFont (new Font ("Serif", Font.BOLD, 14));
	Dimension D = NAME_LABEL.getPreferredSize ();
	if (D.width > 150)
	    NAME_LABEL.setBounds (42, 5, 150, D.height);
	else
	    NAME_LABEL.setBounds (42, 5, D.width, D.height);
	add (NAME_LABEL);
    }

    public void actionPerformed (ActionEvent act_evt)
    {
	String S = act_evt.getActionCommand ();
	if (S.equals ("NEW"))
	    {
		if (CI.on)
		    CI.SendHalt ();
		CI.UCI_new_game ();
	    }
	if (S.equals ("KILL"))
	    {
		int i;
		if (CI.on)
		    CI.SendHalt ();
		CI.SendTo ("quit", true);
		CI.SleepFor (100);
		CI.process.destroy ();
		CI.process = null; // ensure
		for (i = 0; i < CI.CF.instances; i++)
		    if (CI == CI.CF.INSTANCES[i])
			break;
		for (int j = i; j < CI.CF.instances - 1; j++)
		    CI.CF.INSTANCES[j] = CI.CF.INSTANCES[j + 1];
		CI.DisMissInstance ();
	    }
	if (S.equals ("OPT"))
	    {
                if (CI.on)
                    CI.SendHalt ();
                if (!CI.ICI)
		    {
			if (CI.IOF_UCI == null)
			    CI.IOF_UCI = new InstanceOptionsFrameUCI (CI);
		    }
		else
                    {
			if (CI.IOF_ICI == null)
			    CI.IOF_ICI = new InstanceOptionsFrameICI (CI);
		    }
	    }
	if (S.equals ("MultiPV"))
	    CI.doPopUpMultiPV ();
    }

    public void RenewInstancePanel ()
    {
	NAME_LABEL.setText (CI.COMM.id);
	NAME_LABEL.revalidate ();
	NAME_LABEL.repaint ();
	String S = "Multi " + (CI.MultiPV > 9 ? "*" : CI.MultiPV) + "/" +
	    ((CI.MultiPV_Centi_Pawn >= 1000) ? "--" : CI.MultiPV_Centi_Pawn);
	MULTI_PV_BUTTON.setText (S);
	MULTI_PV_BUTTON.repaint ();
	revalidate ();
	repaint ();
    }

    public void MultiPVButton ()
    {
	String S = "Multi " + (CI.MultiPV > 9 ? "*" : CI.MultiPV) + "/" +
	    ((CI.MultiPV_Centi_Pawn >= 1000) ? "--" : CI.MultiPV_Centi_Pawn);
	MULTI_PV_BUTTON = new JButton ("Multi 9/999");
	MULTI_PV_BUTTON.setMargin (new Insets (0, 2, 0, 2));
	MULTI_PV_BUTTON.setForeground (Color.white);
	MULTI_PV_BUTTON.setBackground (Color.blue);
	MULTI_PV_BUTTON.setFont (new Font ("Serif", 0, 13));
	Dimension D = MULTI_PV_BUTTON.getPreferredSize ();
	MULTI_PV_BUTTON.setBounds (195, 2, D.width, D.height);
	MULTI_PV_BUTTON.setText (S);
	MULTI_PV_BUTTON.setActionCommand ("MultiPV");
	MULTI_PV_BUTTON.addActionListener (this);
	add (MULTI_PV_BUTTON);
    }

    public void NewGameButton ()
    {
	JButton B = new JButton ("NEW");
	B.setMargin (new Insets (0, 2, 0, 2));
	B.setForeground (Color.black);
	B.setBackground (Color.green);
	B.setFont (new Font ("Monospaced", Font.BOLD, 12));
	Dimension D = B.getPreferredSize ();
	B.setBounds (281, 2, D.width, D.height);
	B.setActionCommand ("NEW");
	B.addActionListener (this);
	add (B);
    }

    public void AllOptionsButton ()
    {
	JButton B = new JButton ("O");
	B.setMargin (new Insets (-3, 1, -3, 1));
	B.setForeground (Color.red);
	B.setBackground (Color.white);
	B.setFont (new Font ("Serif", Font.BOLD, 16));
	Dimension D = B.getPreferredSize ();
	B.setBounds (314, 2, D.width, D.height);
	B.setActionCommand ("OPT");
	B.addActionListener (this);
	add (B);
    }

    public void DestroyButton ()
    {
	JButton B = new JButton ("X");
	B.setMargin (new Insets (-2, 2, -2, 2));
	B.setForeground (Color.yellow);
	B.setBackground (Color.black);
	B.setFont (new Font ("Serif", Font.BOLD, 16));
	Dimension D = B.getPreferredSize ();
	B.setBounds (338, 1, D.width, D.height);
	B.setActionCommand ("KILL");
	B.addActionListener (this);
	add (B);
    }

    public String NumberThree (long i)
    {
	if (i < 1000)
	    return "" + i;
	long n = 0, b = i; // COPY
	int D = 0;
	char[] SUFFIX = new char [6];
	new StringBuffer (" KMGTP").getChars (0, 6, SUFFIX, 0);
	while (b >= 1000)
	    {
		n = b % 1000;
		b = b / 1000;
		D++;
	    }
	int c = (int) (n / 100);
	int t = (int) ((n / 10) % 10);
	int o = (int) (n % 10); // HACK
	String S;
	if (b < 10)
	    S = "" + b + "." + c + "" + t + "" + SUFFIX[D];
	else if (b < 100)
	    S = "" + b + "." + c + "" + SUFFIX[D];
	else
	    S = "" + b +  "" + SUFFIX[D];
	return S;
    }

    public void HeadLines (Graphics G, int s)
    {	
	G.setColor (TEXT_COLOR);
	Font FT12 = new Font ("SansSerif", 0, 12);
	FontMetrics FM12 = G.getFontMetrics (FT12);
	G.setFont (FT12);
	int v = 37;
	int h = 75;
	String S = "Depth " + CI.DEPTH + "/" + CI.SELDEPTH;
	G.drawString (S, h - FM12.stringWidth (S) / 2, v);
	S = CI.CURR_MOVE_STR;
	if (S != null)
	    G.drawString (S, h - FM12.stringWidth (S) / 2, v + 13);
	h = 140;
	long t = CI.TIME;
	if (t < 10000) // 10s
	    S = "" + (t / 1000) + "." + ((t / 100) % 10) + "" + ((t / 10) % 10) + "s";
	else
	    S = "" + (t / 60000) + ":" + (((t / 1000) % 60) / 10) + "" + (((t / 1000) % 60) % 10);
	G.drawString (S, h - FM12.stringWidth (S) / 2, v);
	long nps;
	if (CI.TIME > 10)
	    nps = CI.NODES / CI.TIME;
	else
	    nps = 0;
	S = NumberThree (nps * 1000) + "/s";
	G.drawString (S, h - FM12.stringWidth (S) / 2, v + 13);
	h = 190;
	S = "Nodes";
	G.drawString (S, h - FM12.stringWidth (S) / 2, v);
	S = NumberThree (CI.NODES);
	G.drawString (S, h - FM12.stringWidth (S) / 2, v + 13);
	h = 230;
	S = "Hash";
	G.drawString (S, h - FM12.stringWidth (S) / 2, v);
	if (CI.HASH_FULL == 1000)
	    S = "FULL";
	else
	    S = "" + (CI.HASH_FULL / 10) + "%";
	G.drawString (S, h - FM12.stringWidth (S) / 2, v + 13);
	h = 265;
	S = "CPU";
	G.drawString (S, h - FM12.stringWidth (S) / 2, v);
	S = "" + (CI.CPU_LOAD / 10) + "%";
	G.drawString (S, h - FM12.stringWidth (S) / 2, v + 13);
	h = 305;
	S = "TB";
	G.drawString (S, h - FM12.stringWidth (S) / 2, v);
	S = NumberThree (CI.TB_HITS);
	G.drawString (S, h - FM12.stringWidth (S) / 2, v + 13);
    }

    public void PaintPV (Graphics G)
    {
	if (CI.DEPTH < 10 && CI.NODES < 100000 && CI.TIME < 100 && CI.on && CI.DEPTH > 0 && !PERIODIC && THIS_DRAW - LAST_DRAW < 100)
	    return; // HACK, elude overage for X
	G.setColor (TEXT_COLOR);
	Font FT11 = new Font ("SansSerif", 0, 11);
	FontMetrics FM11 = G.getFontMetrics (FT11);
	G.setFont (FT11);
	int v = 75;
	for (int i = 1; i < 256; i++)
	    {
		String S = "";
		if (CI.PV[i][0] == null)
		    break;
		int s = CI.SCORE[i];
		int h = 150;
		if (CI.UPPER[i])
		    {
			S += CI.CI_BOARD_POSITION.WTM ? "\u2264" : "\u2265";
                        h -= FM11.stringWidth("\u2264");
		    }
		if (CI.LOWER[i])
		    {
			S += CI.CI_BOARD_POSITION.WTM ? "\u2265" : "\u2264";
                        h -= FM11.stringWidth ("\u2265");
		    }
                if (s < 0)
                    {
                        S += "-";
                        h += FM11.stringWidth ("+") - FM11.stringWidth ("-");
                    }
		if (s > 0)
		    S += "+";
                if (s == 0)
                    h += FM11.stringWidth("+"); // HACK
		if (s < 0)
		    s = -s; // reverse
		if (CI.MATE[i])
		    G.drawString (S + "#" + s, h, v);
		else
		    {
			if (s >= 10000)
			    G.drawString (S + ((s + 50) / 100), h, v);
			else if (s >= 1000)
			    G.drawString (S + (s / 100) + "." + ((s / 10) % 10), h, v);
			else
			    G.drawString (S + (s / 100) + "." + ((s / 10) % 10) + "" + (s % 10), h, v);
		    }
                int LEFT_LIMIT = 150 + FM11.stringWidth ("+0.00") + 4;
		int RIGHT_LIMIT = getSize ().width - 1;
		h = LEFT_LIMIT;
		StringTokenizer ST = new StringTokenizer (CI.PV_STRING[i]);
		while (ST.hasMoreTokens ())
		    {
			S = ST.nextToken ();
			int w = FM11.stringWidth (S + " ");
			if (h + w > RIGHT_LIMIT)
			    {
				h = LEFT_LIMIT;
				v += 12;
			    }
			G.drawString (S, h, v);
			h += w;
		    }
		v += 20;
	    }
	v -= 20;
	v += 3; // acquire
	if (v < VERT_ORIG)
	    v = VERT_ORIG;
	if (v > VERTICAL)
	    {
		VERTICAL += VERTICAL / 4; // HACK
		revalidate ();
	    }
    }

    public void DoMaterialImbalance (BoardPosition BP, Graphics G)
    {
	char buffer[] = new char[1];
        int PIECES[] = { 0, 0, 0, 0, 0, 0, 0 };
        char TABLE[] = { 0, 79, 77, 86, 84, 87, 76, 80, 78, 66, 82, 81, 75 };
        G.setFont (CI.CF.chess_font_small);
        for (int i = 1; i <= 8; i++)
            for (int j = 1; j <= 8; j++)
                {
                    if (BP.AT[i][j] > 0)
                        PIECES[BP.AT[i][j]]++;
                    else
                        PIECES[-BP.AT[i][j]]--;
                }
        int h = OFF_SET;
        G.setColor (WHITE_MATERIAL_OUTLINE ? BLACK_PIECES : WHITE_PIECES);
        for (int p = 5; p > 0; p--)
            for (int i = 0; i < PIECES[p]; i++)
                {
                    buffer[0] = (char) (TABLE[p + (WHITE_MATERIAL_OUTLINE ? 6 : 0)] +  32);
		    if (!CI.REVERSE)
			G.drawChars (buffer, 0, 1, h, 9 * SIZE + VERT_OFF_SET);
		    else
			G.drawChars (buffer, 0, 1, h, VERT_OFF_SET);
		    h += SIZE;
		}
	G.setColor (BLACK_MATERIAL_OUTLINE ? WHITE_PIECES : BLACK_PIECES);
	h = OFF_SET;
        for (int p = 5; p > 0; p--)
            for (int i = 0; i > PIECES[p]; i--)
                {
                    buffer[0] = (char) (TABLE[p + (BLACK_MATERIAL_OUTLINE ? 6 : 0)] +  32);
		    if (!CI.REVERSE)
			G.drawChars (buffer, 0, 1, h, VERT_OFF_SET);
		    else
			G.drawChars (buffer, 0, 1, h, 9 * SIZE + VERT_OFF_SET);
		    h += SIZE;
		}
    }

    public void DoBoardDraw (BoardPosition BP, Graphics G)
    {
	G.setFont (CI.CF.chess_font_small);
	char buffer[] = new char[1];
	for (int i = 1; i <= 8; i++)
	    for (int j = 1; j <= 8; j++)
		{
                    int s = BP.AT[i][j];
		    if (s > 0)
                        s = -s; // HACK
                    if (BLACK_ALL_OUTLINE && BP.AT[i][j] < 0)
                        s = -s;
                    if (WHITE_ALL_OUTLINE && BP.AT[i][j] > 0)
                        s = -s;
                    if (BLACK_HALF_OUTLINE && BP.AT[i][j] < 0 && ((i + j) % 2) == 0)
                        s = -s;
                    if (WHITE_HALF_OUTLINE && BP.AT[i][j] > 0 && ((i + j) % 2) != 0)
                        s = -s;
		    switch (s)
			{
			case 1: buffer[0] = 80 + 32; break;
			case 2: buffer[0] = 78 + 32; break;
			case 3: buffer[0] = 66 + 32; break;
			case 4: buffer[0] = 82 + 32; break;
			case 5: buffer[0] = 81 + 32; break;
			case 6: buffer[0] = 75 + 32; break;
			case -1: buffer[0] = 79 + 32; break;
			case -2: buffer[0] = 77 + 32; break;
			case -3: buffer[0] = 86 + 32; break;
			case -4: buffer[0] = 84 + 32; break;
			case -5: buffer[0] = 87 + 32; break;
			case -6: buffer[0] = 76 + 32; break;
			case 0: default: continue;
			}
		    int horz, vert;
		    if (!CI.REVERSE)
			{
			    horz = SIZE * i;
			    vert = SIZE * (9 - j);
			}
		    else // reverse
			{
			    horz = SIZE * (9 - i);
			    vert = SIZE * j;
			}
		    if (BP.AT[i][j] > 0)
                        G.setColor (WHITE_PIECES);
                    else
                        G.setColor (BLACK_PIECES);
                    if (WHITE_ALL_OUTLINE && BP.AT[i][j] > 0)
			G.setColor (BLACK_PIECES);
                    if (BLACK_ALL_OUTLINE && BP.AT[i][j] < 0)
			G.setColor (WHITE_PIECES);
                    if (WHITE_HALF_OUTLINE && BP.AT[i][j] > 0 && ((i + j) % 2) != 0)
                        G.setColor (BLACK_PIECES);
                    if (BLACK_HALF_OUTLINE && BP.AT[i][j] < 0 && ((i + j) % 2) == 0)
                        G.setColor (WHITE_PIECES);
                    G.drawChars (buffer, 0, 1, horz, vert + VERT_OFF_SET);
		}
    }

    public void DoInstanceBoard (Graphics G)
    {
	if (CI.DEPTH < 10 && CI.NODES < 100000 && CI.TIME < 100 && CI.on && CI.DEPTH > 0 && !PERIODIC && THIS_DRAW - LAST_DRAW < 100) 
	    return; // much
	String S = new String ("");
	G.setFont (CI.CF.chess_font_small);
	BoardPosition TEMP = new BoardPosition (CI.CI_BOARD_POSITION);
	int pv_num = CI.DISPLAY_PV;
	int k = 0;
	while (CI.PV[pv_num][k] != null) // advance PV
	    {
		CI.AttendForm (CI.PV[pv_num][k], TEMP, k);
		k++;
	    }
	DoMaterialImbalance (TEMP, G);
	DoBoardDraw (TEMP, G);
	DoColorIndicator (TEMP, G);
	if (TEMP.EnPassant != 0)
	    {
		G.setColor (EN_PASSANT_COLOR);
		int h = (CI.REVERSE) ? 9 - TEMP.EnPassant : TEMP.EnPassant;
		int v = ((CI.REVERSE ^ TEMP.WTM) ? 3 : 6);
		G.fillArc (h * SIZE + 3, VERT_OFF_SET + (v - 1) * SIZE + 3,
			   SIZE - 7, SIZE - 7, 0, 360);
	    }
    }

    public void DoColorIndicator (BoardPosition TEMP, Graphics G)
    {
	if (TEMP.WTM)
	    {
		G.setColor (Color.white);
		if (!CI.REVERSE)
		    G.fillArc (3, 8 * SIZE + 3 + VERT_OFF_SET - SIZE,
			       SIZE - 7, SIZE - 7, 0, 360);
		else
		    G.fillArc (3, SIZE + 3 + VERT_OFF_SET - SIZE,
			       SIZE - 7, SIZE - 7, 0, 360);
	    }
	else
	    {
		G.setColor (Color.black);
		if (!CI.REVERSE)
		    G.fillArc (3, SIZE + 3 + VERT_OFF_SET - SIZE,
			       SIZE - 7, SIZE - 7, 0, 360);
		else
		    G.fillArc (3, 8 * SIZE + 3 + VERT_OFF_SET - SIZE,
			       SIZE - 7, SIZE - 7, 0, 360);
	    }
    }

    public void paintComponent (Graphics G) // override
    {
	// entry conditional
	THIS_DRAW = new Date ().getTime ();
	if (THIS_DRAW - LAST_DRAW < 50)
	    return;
	IS_PAINTING = true;
	BoardPosition BP = CI.CI_BOARD_POSITION; // pointer

	if (!PERIODIC && !CI.PV_CHANGE)
	    {
		G.setColor (BACK_GROUND);
		G.fillRect(0, 0, 1000, VERT_OFF_SET - 1);
		G.setColor (CI.on ? Color.green : Color.red);
		G.fillArc (5, 5, 30, 30, 0, 360); // on/off
		HeadLines (G, SIZE);
		CI.PV_CHANGE = false;
		return;
	    }

	G.setColor (BACK_GROUND);
	G.fillRect(0, 0, 1000, VERTICAL);
	G.setColor (CI.on ? Color.green : Color.red);
	G.fillArc (5, 5, 30, 30, 0, 360); // on/off
	HeadLines (G, SIZE);

	for (int i = 0; i < 8; i++)
	    for (int j = 0; j < 8; j++)
		{
		    if ((i + j) % 2 == 0)
			G.setColor (LIGHT_SQUARES);
		    else
			G.setColor (DARK_SQUARES);
		    G.fillRect (SIZE * i + HORZ_OFF_SET, SIZE * j + VERT_OFF_SET, SIZE, SIZE);
		}
	G.setColor (Color.black);
	G.drawLine (HORZ_OFF_SET - 1, VERT_OFF_SET - 1,
		    HORZ_OFF_SET - 1, VERT_OFF_SET + SIZE * 8);
	G.drawLine (HORZ_OFF_SET - 1, VERT_OFF_SET - 1,
		    HORZ_OFF_SET + SIZE * 8, VERT_OFF_SET - 1);
	G.drawLine (HORZ_OFF_SET + SIZE * 8, VERT_OFF_SET + SIZE * 8,
		    HORZ_OFF_SET + SIZE * 8, VERT_OFF_SET - 1);
	G.drawLine (HORZ_OFF_SET + SIZE * 8, VERT_OFF_SET + SIZE * 8,
		    HORZ_OFF_SET - 1, VERT_OFF_SET + SIZE * 8);
	// VERT_OFF_SET -= SIZE; // HACK ? 
	DoInstanceBoard (G);
	PaintPV (G);
	IS_PAINTING = false;
	PERIODIC = false; // HACK
	LAST_DRAW = THIS_DRAW;
    }
}
