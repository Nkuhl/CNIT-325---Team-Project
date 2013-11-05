
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

public class DataPGN
{
    BoardPosition BP;
    BoardPanel BOARD_PANEL;
    MovePane MOVE_PANE;
    ComradesFrame CF;
    String Event, Site, Date, Round, White, Black, Result, FEN;

    public DataPGN (ComradesFrame cf)
    {
	CF = cf;
	BP = new BoardPosition (CF.BOARD_PANEL.POS); // HACK
	BP.NewGame ();
	BOARD_PANEL = new BoardPanel (BP);
	MOVE_PANE = new MovePane (new JPanel ());
	BP.MOVE_PANE = MOVE_PANE;
	BP.MOVE_TREE = new MoveTree (BP, new ComradesNode (), MOVE_PANE.PANEL);
	BP.CF = CF;
    }

    public void TagPGN (String S)
    {
	int n = S.length ();
	if (S.startsWith ("[Event "))
	    Event = (new StringTokenizer (S.substring (7),"\"")).nextToken ();
	if (S.startsWith ("[Site "))
	    Site = (new StringTokenizer (S.substring (6),"\"")).nextToken ();
	if (S.startsWith ("[Date "))
	    Date = (new StringTokenizer (S.substring (6),"\"")).nextToken ();
	if (S.startsWith ("[Round "))
	    Round = (new StringTokenizer (S.substring (7),"\"")).nextToken ();
	if (S.startsWith ("[White "))
	    White = (new StringTokenizer (S.substring (7),"\"")).nextToken ();
	if (S.startsWith ("[Black "))
	    Black = (new StringTokenizer (S.substring (7),"\"")).nextToken ();
	if (S.startsWith ("[Result "))
	    Result = (new StringTokenizer (S.substring (8),"\"")).nextToken ();
	if (S.startsWith ("[Variant \"Chess960\"]"))
	    BP.Chess960 = true;
	if (S.startsWith ("[FEN "))
	    {
		FEN = (new StringTokenizer (S.substring (5),"\"")).nextToken ();
		BP.SetFEN (new StringTokenizer (FEN));
	    }
    }

    public void UCIparse (String S)
    {
	StringTokenizer ST = new StringTokenizer (S.substring (24));
	while (ST.hasMoreTokens ())
	    {
		S = ST.nextToken ();
		int w = BP.FindMove (S);
		if (w == -1)
		    {
			System.out.println ("Not found " + S);
			continue;
		    }
		BP.MakeMove (w);
		BP.MakeNormal ();
	    }
	BP.MOVE_TREE.JUMP = false;
	BP.MOVE_TREE.JumpToNode (BP.MOVE_TREE.ROOT);
	BP.MOVE_TREE.PaintPanel ();
	White = new String ("White");
	Black = new String ("Black");
	Result = new String ("*");
	BP.PANEL.White = new String ("White");
	BP.PANEL.Black = new String ("Black");
	BP.PANEL.Result = new String ("*");
	BP.PANEL.repaint ();
    }

    public boolean ParsePGN (BufferedReader BR)
    {
	ComradesNode C = null;
	int re_curse = 0;
	ComradesNode[] RE_CURSE = new ComradesNode[64];
	boolean COMMENT = false;
	String STR = null;
	boolean introRAV = false;
	boolean GAME_IS_ON = true;

	BP.MakeNormal (); // ensure
	BP.MOVE_TREE.ReSet ();
	BP.MOVE_TREE.JUMP = true; // HACK
	while (GAME_IS_ON)
	    {
		try
		    {
			STR = BR.readLine ();
		    }
		catch (IOException io_exc) { }
		if (STR == null)
		    {
			if (White != null)
			    System.out.println ("PGN: Game has not terminate");
			return false; // technic
		    }
		if (STR.startsWith ("position startpos moves "))
		    {
			UCIparse (STR);
			return true;
		    }
		if (!COMMENT && STR.startsWith ("["))
		    {
			TagPGN (STR);
			continue;
		    }
		if (!COMMENT && STR.startsWith ("%")) // ESCape
		    continue;
		StringTokenizer ST = new StringTokenizer (STR, ".() ?!{}", true);
		while (ST.hasMoreTokens ())
		    {
			String S = ST.nextToken ();
			if (S.equals ("{"))
			    COMMENT = true;
			if (S.equals ("}"))
			    COMMENT = false;
			if (COMMENT)
			    continue;
			if (S.equals ("1-0"))
			    GAME_IS_ON = false;
			if (S.equals ("0-1"))
			    GAME_IS_ON = false;
			if (S.equals ("1/2-1/2"))
			    GAME_IS_ON = false;
			if (S.equals ("*")) // HACK
			    GAME_IS_ON = false;
			if (!GAME_IS_ON)
			    break;
			if (S.equals (".") || S.equals ("?") || S.equals ("!") ||
			    S.equals (" ") || S.equals ("{") || S.equals ("}"))
			    continue;
			if (S.equals ("(")) // (( ?
			    {
				BP.DisAttendMove32 (BP.MOVE_TREE.NOW.mv.move);
				BP.MakeNormal ();
				RE_CURSE[re_curse++] = BP.MOVE_TREE.NOW;
				introRAV = true;
				continue;
			    }
			if (S.equals (")"))
			    {
				re_curse--;
				while (BP.MOVE_TREE.NOW != RE_CURSE[re_curse])
				    BP.DisAttendMove32 (BP.MOVE_TREE.NOW.mv.move);
				BP.MakeMove32 (BP.MOVE_TREE.NOW.mainline.move,BP.MOVE_TREE.NOW.mainline.fancy);
				BP.MakeNormal ();
				continue;
			    }
			if (S.equals ("0-0"))
			    S = "O-O"; // corrective
			if (S.equals ("0-0-0"))
			    S = "O-O-O"; // corrective
			if (S.equals ("0-0+"))
			    S = "O-O+"; // corrective
			if (S.equals ("0-0-0+"))
			    S = "O-O-O+"; // corrective
			if ("ONBRQKabcdefgh".indexOf ((new StringBuffer (S)).charAt (0)) == -1)
			    continue;
			S = S.replace ('#', '+');
			int w = BP.NotateComparison (S);
			if (w == -1)
			    continue;
			if (introRAV)
			    C = BP.MOVE_TREE.NOW.MainLineNode;
			BP.MakeMove (w);
			if (introRAV)
			    {
				BP.MOVE_TREE.NOW.MainLineParent.MainLineNode = C;
				BP.MOVE_TREE.NOW.MainLineParent.mainline = C.mv;
			    }
			introRAV = false;
			BP.MakeNormal ();
		    }
	    }
	BP.MOVE_TREE.JUMP = false;
	BP.MOVE_TREE.JumpToNode (BP.MOVE_TREE.ROOT);
	BP.MOVE_TREE.PaintPanel ();
	BP.PANEL.White = new String (White);
	BP.PANEL.Black = new String (Black);
	BP.PANEL.Result = new String (Result);
	BP.PANEL.repaint ();
	return true;
    }
}