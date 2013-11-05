package edu.purdue.comradesgui;

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.*;

public class MoveTree extends JTree // JPanel ?
{
    ComradesNode ROOT, NOW;
    JPanel PANEL;
    BoardPosition BP, START;
    String START_FEN;
    int SPACE = 5; // prefer
    boolean WTM, FOCUS;
    int MOVE_NUMBER;
    int v = 0;
    boolean JUMP = false;
    boolean RESET = true;

    public MoveTree (BoardPosition bp, ComradesNode root, JPanel panel)
    {
	super (new DefaultTreeModel (root));
	ROOT = root;
	BP = bp;
	START = new BoardPosition (BP); // copy
	START_FEN = new String (BP.GetFEN ());
	PANEL = panel; // copy
	PANEL.removeAll ();
	PANEL.setLayout (null); // absolute
	NOW = ROOT;
	WTM = BP.WTM;
	MOVE_NUMBER = BP.MOVE_NUMBER;
	FOCUS = false;
    }

    public void ReSet ()
    {
	if (!RESET)
	    return;
	START = new BoardPosition (BP); // copy
	START_FEN = new String (BP.GetFEN ());
	ROOT.removeAllChildren ();
	PANEL.removeAll ();
	PANEL.setLayout (null); // absolute
	NOW = ROOT;
	WTM = BP.WTM;
	MOVE_NUMBER = BP.MOVE_NUMBER;
	FOCUS = false;
    }

    public void JumpToNode (ComradesNode N)
    {
	ComradesNode T = N;
	BP.CF.HaltInstances ();
	JUMP = true;
	while (T.MainLineParent != null)
	    {
		T.MainLineParent.MainLineNode = T;
		T.MainLineParent.mainline = T.mv; // valid ?
		T = T.MainLineParent;
	    }
	RESET = false;
	BP.SetFEN (new StringTokenizer (START_FEN));
	RESET = true; // HACK
	NOW = ROOT;
	while (N != NOW) // attend
	    BP.MakeMove32 (NOW.mainline.move, NOW.mainline.fancy);
	BP.MakeNormal ();
	BP.CF.redraw ();
	BP.CF.BoardPositionChanged (); // ensure
	PaintPanel ();
	BP.CF.EquipInstances ();
	JUMP = false;
    }

    public void NodeMove (int mv, String S)
    {
        int i, n = NOW.getChildCount ();
        ComradesNode PREV = NOW;
        PREV.mainline = new TypeMove (mv, S);
        for (i = 0; i < n; i++)
            {
                if ((PREV.getChildAt (i).mv.move & 0xfffff) == (mv & 0xfffff))
                    {
                        NOW = PREV.getChildAt (i);
                        break;
                    }
            }
        if (i >= n)
            NOW = new ComradesNode (PREV, PREV.mainline);
        PREV.MainLineNode = NOW;
        NOW.MainLineParent = PREV;
	FOCUS = true;
	if (!JUMP)
	    PaintPanel ();
    }

    public void NodeExpansion (int LEFT, ComradesNode N, int ht)
    {
	int h = LEFT;
	ComradesLabel C;
	Dimension D;
	if (LEFT > 5)
	    {
		JLabel ARROW = new JLabel ("\u25cf");
		setBorder (BorderFactory.createEmptyBorder ());
		ARROW.setFont (new Font ("Monospaced", 0, 10));
		D = ARROW.getPreferredSize ();
		ARROW.setBounds (h - 9, v, D.width, D.height);
		PANEL.add (ARROW);
	    }
	C = new ComradesLabel (N, this, false, ht);
	D = C.getPreferredSize ();
	C.setBounds (h, v, D.width, D.height);
	h += D.width + SPACE;
	if (N == NOW)
	    C.setBackground (Color.orange);
	PANEL.add (C);
	ht++;

	while (N.getChildCount () > 0)
	    {
		ComradesNode F = N.MainLineNode;
		if (!N.is_expanded || N.getChildCount() <= 1)
		    {
			C = new ComradesLabel (F, this, false, (ht % 2 == 0 || N == ROOT) ? ht : 0);
			if (F == NOW) // HACK // valid ?
			    C.setBackground (Color.orange); // partial ?
			D = C.getPreferredSize ();
			if (h + D.width > 360)
			    {
				h = LEFT; // LIMIT
				v += 15;
			    }
			C.setBounds (h, v, D.width, D.height);
			h += D.width + SPACE;
			PANEL.add (C);
		    }
		else
		    {
			C = new ComradesLabel (N, this, true, 0);
			D = C.getPreferredSize ();
			C.setBounds (h, v, D.width, D.height);
			PANEL.add (C);
			int n = N.getChildCount ();
			for (int i = 0; i < n; i++)
			    {
				v += 15;
				NodeExpansion (LEFT + 10, N.getChildAt (i), ht);
			    }
			break;
		    }
		N = F.transpose; // HACK
		ht++;
	    }
    }

    public void PaintPanel () // manual
    {
	v = 0; // attend
	PANEL.removeAll (); // HACK
	JLabel L = new JLabel (BP.PANEL.Name);
	Dimension D = L.getPreferredSize ();
	L.setBounds (2, 0, D.width, D.height);
	PANEL.add (L);
	v = 15; // vertical
	NodeExpansion (2, ROOT, (WTM ? 0 : 1) + 2 * MOVE_NUMBER - 1);
	PANEL.repaint ();
	Rectangle R = NOW.LABEL.getBounds ();
	R = new Rectangle (R.x, R.y - 15, R.width, R.height + 30);
	PANEL.scrollRectToVisible (R); // ensure
	if (FOCUS)
	    NOW.LABEL.requestFocus ();
    }

    public String LineSplit (String STRING, int w)
    {
	String S = new String ("");
	StringTokenizer ST = new StringTokenizer (STRING);
	int u = 0;
	while (ST.hasMoreTokens ())
	    {
		String A = ST.nextToken ();
		u += A.length ();
		String B = A;
		while (B.indexOf (".") != -1)
		    {
			B = ST.nextToken ();
			A += " " + B;
			u += 1 + B.length ();
		    }		    
		if (u > w)
		    {
			S += "\n";
			u = A.length ();
		    }
		S += A + " ";
		u += 1;
	    }
	return S + "\n";
    }

    public String Indicator (int ht, ComradesNode N, boolean b)
    {
	String S = new String (" ");
	if (b || (ht % 2) == 0)
	    {
		S += "" + (ht / 2) + ((ht % 2) == 0 ? ". " : "... ");
	    }
	return S;
    }

    public String SubPGN (ComradesNode N, int ht, boolean b)
    {
	String S = new String ("");
	int c = N.getChildCount ();
	if (c > 0)
	    S += Indicator (ht, N, b) + N.getChildAt (0).toString ();
	if (c > 1)
	    for (int i = 1; i < c; i++)
		{
		    S += " " + "(" + (ht / 2) + ((ht % 2) == 0 ? ". " : "... ")
			+ N.getChildAt (i). toString () +
			SubPGN (N.getChildAt (i), ht + 1, false) + ")";
		}
	if (c > 0)
	    S += SubPGN (N.getChildAt (0), ht + 1, c > 1);
	return S;
    }

    public String EmitPGNTree ()
    {
	String S = new String ("");
	S += "[White \"" + BP.PANEL.White + "\"]\n";
	S += "[Black \"" + BP.PANEL.Black + "\"]\n";
	if (BP.PANEL.Result == null)
	    BP.PANEL.Result = "*";
	S += "[Result \"" + BP.PANEL.Result + "\"]\n";
	if (BP.Chess960)
	    S += "[Variant \"Chess960\"]\n";
	if (!START_FEN.equals
	    ("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"))
	    S += "[FEN \"" + START_FEN + "\"]\n";
	S += "\n" +
	    LineSplit (SubPGN (ROOT, 2 * MOVE_NUMBER + (WTM ? 0 : 1), false)
		       + " " + BP.PANEL.Result, 75);
	return S + "\n";
    }

    public String MainLinePGN (int ht)
    {
	ComradesNode N = ROOT;
	String S = new String ("");
	while (N.MainLineNode != null)
	    {
		if ((ht % 2) == 0)
		    S += "" + (ht / 2) + ". ";
		S += N.mainline.toString () + " ";
		N = N.MainLineNode;
		ht++;
	    }
	return S;
    }

    public String EmitPGNMainLine ()
    {
	String S = new String ("");
	S += "[White \"" + BP.PANEL.White + "\"]\n";
	S += "[Black \"" + BP.PANEL.Black + "\"]\n";
	if (BP.PANEL.Result == null)
	    BP.PANEL.Result = "*";
	S += "[Result \"" + BP.PANEL.Result + "\"]\n";
	if (BP.Chess960)
	    S += "[Variant \"Chess960\"]\n";
	if (!START_FEN.equals
	    ("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"))
	    S += "[FEN \"" + START_FEN + "\"]\n";
	S += "\n";
	if (!WTM)
	    S += "" + MOVE_NUMBER + "... ";
	S += LineSplit (MainLinePGN (2 * MOVE_NUMBER + (WTM ? 0 : 1))
			+ " " + BP.PANEL.Result, 75);
	return S + "\n";
    }

    public String EmitPGN (boolean DoTree)
    {
	return DoTree ? EmitPGNTree () : EmitPGNMainLine ();
    }

}
