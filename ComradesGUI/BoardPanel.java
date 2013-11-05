
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

public class BoardPanel extends JPanel implements MouseListener
{
    ComradesFrame CF;
    int CLICKED_x = 0;
    int CLICKED_y = 0;
    int CLICKED_b = 0;
    boolean CLICKED = false;
    Color WHITE_PIECES, BLACK_PIECES, LIGHT_SQUARES, DARK_SQUARES;
    Color CASTLING_COLOR, BACK_GROUND, MOVE_ARROWS, YELLOW_BUTTON, CLICKED_COLOR;
    Color WHITE_MOVE_INDICATOR, BLACK_MOVE_INDICATOR, EN_PASSANT_COLOR;
    Color LAST_MOVE_COLOR;
    boolean WHITE_ALL_OUTLINE, BLACK_ALL_OUTLINE, WHITE_HALF_OUTLINE, BLACK_HALF_OUTLINE, WHITE_MATERIAL_OUTLINE, BLACK_MATERIAL_OUTLINE;
    int SIZE;
    BoardPosition POS;
    boolean REVERSE = false, SET_UP = false;
    String Name = null;
    String White = null, Black = null, Result = null;

    public BoardPanel (BoardPosition pos)
    {
	super ();
	CF = pos.CF;
	SIZE = CF.SIZE; 
	POS = pos;
	POS.PANEL = this;
	ImportColors ();
	addMouseListener (this);
   }

    public String toString ()
    {
	return Name;
    }

////////////////////////////////

    public void ExitSetUp ()
    {
	SET_UP = false;
        POS.MakeNormal (); // attend
        POS.NewMoveTree ();
        POS.MOVE_TREE.PaintPanel ();
        BoardPositionChanged ();
        CLICKED = false;
	repaint ();
	CF.NewGameInstances ();
        CF.EquipInstances ();
    }

    public void StartSetUp ()
    {
        if (SET_UP)
            return; // HACK  
        POS.NewMoveTree ();
        POS.MOVE_TREE.PaintPanel ();
        REVERSE = false; // in simple        
	CF.HaltInstances ();
        SET_UP = true;
        CLICKED = false;
        BoardPositionChanged ();
        repaint ();
    }

    public void BoardPositionChanged ()
    {
        for (int i = 0; i < CF.instances; i++)
            CF.INSTANCES[i].ClearInformatory ();
        CF.FEN_AREA.setText (POS.GetFEN ()); // HACK
    }

    public void BoardPanelSetUp (int x, int y, int b, int c)
    {
        if (x == 9 && y == 9)
            ExitSetUp ();
        if (x == 0 && y == 9 && c == 1)
            {
                POS.ClearBoard ();
                repaint ();
                return;
            }
        if (x == 0 && y == 9 && c == 2)
            {
                POS.NewGame ();
                repaint ();
                return;
            }
	if (x == 0 && y == 7)
	    {
		POS.Chess960 = !POS.Chess960;
		repaint ();
		return;
	    }
        if ((y == 0 || y == 9) && (x >= 1 && x <= 7))
            {
                CLICKED_x = x;
                CLICKED_y = y;
                CLICKED = true;
                repaint ();
                return;
            }
        if (x == 0 && y == 4)
            {
                POS.WTM = true;
                repaint ();
                return;
            }
        if (x == 0 && y == 5)
            {
                POS.WTM = false;
                repaint ();
                return;
            }
        if (x >= 1 && x <= 8 && y >= 1 && y <= 8)
            {
                if (CLICKED && x == CLICKED_x && y == CLICKED_y)
                    {
                        CLICKED = false;
			if (b != 1)
                            POS.AT[x][y] = 0;
                        repaint ();
                        return;
                    }
                if (b != 1 && POS.AT[x][y] != 0)
                    {
                        CLICKED = true;
                        CLICKED_x = x;
                        CLICKED_y = y;
                        repaint ();
                        return;
                    }
                if (!CLICKED)
                    return;
                int cx = CLICKED_x;
                int cy = CLICKED_y;
                if (cy == 0)
                    POS.AT[x][y] = cx - 1;
                if (cy == 9)
                    POS.AT[x][y] = 1 - cx; // HACK                   
                if (cx >= 1 && cx <= 8 && cy >= 1 && cy <= 8)
                    {
                        POS.AT[x][y] = POS.AT[cx][cy];
                        POS.AT[cx][cy] = 0;
                        CLICKED_x = x;
                        CLICKED_y = y;
                    }
		repaint ();
                return;
	    }
    }

    public void BoardPanelPushed (MouseEvent mou_evt)
    {
        int x = mou_evt.getX () / SIZE;
        int y = 9 - (mou_evt.getY () / SIZE); // HACK
        int b = mou_evt.getButton ();
        int c = mou_evt.getClickCount ();
	if (SET_UP)
            {
                BoardPanelSetUp (x, y, b, c);
                return;
            }
        if (x == 0 && y == 8 && c == 2)
            {
                REVERSE = !REVERSE;
		repaint ();
                for (int i = 0; i < CF.instances; i++)
		    {
			CF.INSTANCES[i].REVERSE = !CF.INSTANCES[i].REVERSE;
			CF.INSTANCES[i].IP.repaint ();
		    }
		return;
            }
        if (x == 0 && y == 0)
            {
                if (POS.MOVE_TREE.NOW != POS.MOVE_TREE.ROOT)
                    {
                        AbruptBack ();
                        CLICKED = false;
                    }
                return;
            }
	if (x == 9 && y == 0)
            {
                if (POS.MOVE_TREE.NOW.getChildCount () > 0)
                    {
                        AbruptForward ();
                        CLICKED = false;
                    }
                return;
            }
        if (x < 1 || x > 8 || y < 1 || y > 8)
            return;
        if (REVERSE)
            {
                x = 9 - x;
                y = 9 - y;
            }
	if (CLICKED)
            {
                CLICKED = false;
                TryMove (CLICKED_x, CLICKED_y, CLICKED_b, x, y, b);
                repaint ();
            }
        else
            {
                int p = POS.AT[x][y];
                if ((POS.WTM && p > 0) ||
                    (!POS.WTM && p < 0))
                    {
                        CLICKED_x = x;
                        CLICKED_y = y;
                        CLICKED_b = b;
                        CLICKED = true;
                        repaint ();
                    }
            }
    }

    public void mouseEntered (MouseEvent mou_evt) { }
    public void mouseExited (MouseEvent mou_evt) { }
    public void mousePressed (MouseEvent mou_evt) { }
    public void mouseReleased (MouseEvent mou_evt) { }
    public void mouseClicked (MouseEvent mou_evt)
    {
	BoardPanelPushed (mou_evt);
    }
////////////////////////////////
    public void AttendMove (int w, boolean advance, boolean ReDraw)
    {
        if (w > POS.COUNT_OF_LEGAL_MOVES || w < 0)
            return;
        POS.MakeMove (w);
        POS.MakeNormal ();
        if (ReDraw)
            CF.FEN_AREA.setText (POS.GetFEN ());
    }

    public void AttendMoveNew (TypeMove tm, boolean ReDraw)
    {
        POS.MakeMove32 (tm.move, tm.fancy);
	POS.MakeNormal ();
        if (ReDraw)
            CF.FEN_AREA.setText (POS.GetFEN ());
    }

    public void TryMove (int cx, int cy, int cb, int x, int y, int b)
    {
        if (cx > 8 || cx < 1 || cy > 8 || cy < 1 ||
            x < 1 || x > 8 || y < 1 || y > 8)
            return;
        if (cx == x && cy == y)
            return; // HACK
	cx--;
        cy--;
        x--; // morph
        y--;
        int m = cx + (cy << 3) + (x << 6) + (y << 9);
        for (int i = 0; i < POS.COUNT_OF_LEGAL_MOVES; i++)
            {
                int mv = POS.move_list[i];
                if (m == (mv & 07777))
                    {
                        if ((mv >> 17) != 0 && cb != 1 && (mv >> 17) != (b + 1))
                            continue;
                        CF.HaltInstances ();
                        AttendMove (i, true, true);
                        BoardPositionChanged ();
                        CF.EquipInstances ();
                        return;
                    }
            }
    }



    public void AbruptBack ()
    {
        MoveTree MT = POS.MOVE_TREE;
        if (MT.NOW == MT.ROOT)
            return;
	if (true)
	{
	    CF.HaltInstances ();
	    POS.DisAttendMove32 (MT.NOW.mv.move);
	    /*
	      POS.UnMakeMove32 (MT.NOW.mv.move);
	      POS.MakeNormal ();
	      MT.NOW = MT.NOW.MainLineParent;
	    */
	    MT.FOCUS = true;
	    MT.PaintPanel ();
	    repaint ();
	    BoardPositionChanged ();
	    CF.EquipInstances ();
	}
	else
	    MT.JumpToNode (MT.NOW.MainLineParent); // value for Instances 
    }

    public void AbruptForward ()
    {
        MoveTree MT = POS.MOVE_TREE;
        if (MT.NOW.getChildCount () == 0)
            return;
        CF.HaltInstances ();
        AttendMoveNew (MT.NOW.mainline, true);
        repaint ();
        BoardPositionChanged ();
        CF.EquipInstances ();
    }


////////////////////////////////
    public Dimension getPreferredSize ()
    {
	return new Dimension (400, 400);
    }

    public Dimension getMinimumSize ()
    {
	return new Dimension (400, 400);
    }

    public Dimension getMaximumSize ()
    {
	return new Dimension (405, 400); // valid ?
    }

    public void ImportColors ()
    {
	WHITE_PIECES = CF.WHITE_PIECES;
	BLACK_PIECES = CF.BLACK_PIECES;
	LIGHT_SQUARES = CF.LIGHT_SQUARES;
	DARK_SQUARES = CF.DARK_SQUARES;
	BACK_GROUND = CF.BACK_GROUND;
	CASTLING_COLOR = CF.CASTLING_COLOR;
	LAST_MOVE_COLOR = CF.LAST_MOVE_COLOR;
	MOVE_ARROWS = CF.MOVE_ARROWS;
	YELLOW_BUTTON = CF.YELLOW_BUTTON;
	CLICKED_COLOR = CF.CLICKED_COLOR;
	WHITE_MOVE_INDICATOR = CF.WHITE_MOVE_INDICATOR;
	BLACK_MOVE_INDICATOR = CF.BLACK_MOVE_INDICATOR;
	EN_PASSANT_COLOR = CF.EN_PASSANT_COLOR;
        WHITE_ALL_OUTLINE = CF.WHITE_ALL_OUTLINE;
        BLACK_ALL_OUTLINE = CF.BLACK_ALL_OUTLINE;
        WHITE_HALF_OUTLINE = CF.WHITE_HALF_OUTLINE;
        BLACK_HALF_OUTLINE = CF.BLACK_HALF_OUTLINE;
        WHITE_MATERIAL_OUTLINE = CF.WHITE_MATERIAL_OUTLINE;
        BLACK_MATERIAL_OUTLINE = CF.BLACK_MATERIAL_OUTLINE;
    }

    public void DrawCastling (Graphics G)
    {
	G.setColor (CASTLING_COLOR);
	int u = (!REVERSE ? POS.Chess960_WK_File : 9 - POS.Chess960_WK_File);
	if ((!REVERSE && POS.WhiteOOO) || (REVERSE && POS.BlackOO))
	    {
		G.drawLine (u * SIZE + SIZE / 2, 9 * SIZE + SIZE / 4,
			    u * SIZE + 7, 9 * SIZE + SIZE / 4);
		G.drawLine (u * SIZE + SIZE / 2, 9 * SIZE + SIZE / 4 - 1,
			    u * SIZE + 7, 9 * SIZE + SIZE / 4 - 1);
		G.drawLine (u * SIZE + 7, 9 * SIZE + SIZE / 4,
			    u * SIZE + 12, 9 * SIZE + 5);
		G.drawLine (u * SIZE + 7, 9 * SIZE + SIZE / 4 - 1,
			    u * SIZE + 12, 9 * SIZE + 6);
		G.drawLine (u * SIZE + 7, 9 * SIZE + SIZE / 4 - 1,
			    u * SIZE + 12, 9 * SIZE + SIZE / 2 - 6);
		G.drawLine (u * SIZE + 7, 9 * SIZE + SIZE / 4,
			    u * SIZE + 12, 9 * SIZE + SIZE / 2 - 7);
		if (POS.Chess960)
		    {
			if (!REVERSE)
			    G.fillArc (POS.White_QR_file * SIZE + 7,
				       9 * SIZE + 4, 7, 7, 0, 360);
			else
			    G.fillArc ((9 - POS.Black_KR_file) * SIZE + 7,
				       9 * SIZE + 4, 7, 7, 0, 360);
		    }
	    }	    
	if ((!REVERSE && POS.WhiteOO) || (REVERSE && POS.BlackOOO))
	    {
		G.drawLine (u * SIZE + SIZE / 2, 9 * SIZE + SIZE / 4,
			    (u + 1) * SIZE - 7, 9 * SIZE + SIZE / 4);
		G.drawLine (u * SIZE + SIZE / 2, 9 * SIZE + SIZE / 4 - 1,
			    (u + 1) * SIZE - 7, 9 * SIZE + SIZE / 4 - 1);
		G.drawLine ((u + 1) * SIZE - 7, 9 * SIZE + SIZE / 4,
			    (u + 1) * SIZE - 12, 9 * SIZE + 5);
		G.drawLine ((u + 1) * SIZE - 7, 9 * SIZE + SIZE / 4 - 1,
			    (u + 1) * SIZE - 12, 9 * SIZE + 6);
		G.drawLine ((u + 1) * SIZE - 7, 9 * SIZE + SIZE / 4 - 1,
			    (u + 1) * SIZE - 12, 9 * SIZE + SIZE / 2 - 6);
		G.drawLine ((u + 1) * SIZE - 7, 9 * SIZE + SIZE / 4,
			    (u + 1) * SIZE - 12, 9 * SIZE + SIZE / 2 - 7);
		if (POS.Chess960)
		    {
			if (!REVERSE)
			    G.fillArc (POS.White_KR_file * SIZE + 28,
				       9 * SIZE + 4, 7, 7, 0, 360);
			else
			    G.fillArc ((9 - POS.Black_QR_file) * SIZE + 28,
				       9 * SIZE + 4, 7, 7, 0, 360);
		    }
	    }	    
	if ((!REVERSE && POS.BlackOOO) || (REVERSE && POS.WhiteOO))
	    {
		G.drawLine (u * SIZE + SIZE / 2, 3 * SIZE / 4 + 1,
			    u * SIZE + 7, 3 * SIZE / 4 + 1);
		G.drawLine (u * SIZE + SIZE / 2, 3 * SIZE / 4,
			    u * SIZE + 7, 3 * SIZE / 4);
		G.drawLine (u * SIZE + 7, 3 * SIZE / 4,
			    u * SIZE + 12, SIZE - 5);
		G.drawLine (u * SIZE + 7, 3 * SIZE / 4 + 1,
			    u * SIZE + 12,  SIZE - 6);
		G.drawLine (u * SIZE + 7, 3 * SIZE / 4 + 1,
			    u * SIZE + 12, SIZE / 2 + 6);
		G.drawLine (u * SIZE + 7, 3 * SIZE / 4,
			    u * SIZE + 12, SIZE / 2 + 7);
		if (POS.Chess960)
		    {
			if (!REVERSE)
			    G.fillArc (POS.White_QR_file * SIZE + 7,
				       SIZE - 11, 7, 7, 0, 360);
			else
			    G.fillArc ((9 - POS.Black_KR_file) * SIZE + 7,
				       SIZE - 11, 7, 7, 0, 360);
		    }
	    }	    
	if ((!REVERSE && POS.BlackOO) || (REVERSE && POS.WhiteOOO))
	    {
		G.drawLine (u * SIZE + SIZE / 2, 3 * SIZE / 4 + 1,
			    (u + 1) * SIZE - 7, 3 * SIZE / 4 + 1);
		G.drawLine (u * SIZE + SIZE / 2, 3 * SIZE / 4,
			    (u + 1) * SIZE - 7, 3 * SIZE / 4);
		G.drawLine ((u + 1) * SIZE - 7, 3 * SIZE / 4,
			    (u + 1) * SIZE - 12, SIZE - 5);
		G.drawLine ((u + 1) * SIZE - 7, 3 * SIZE / 4 + 1,
			    (u + 1) * SIZE - 12,  SIZE - 6);
		G.drawLine ((u + 1) * SIZE - 7, 3 * SIZE / 4 + 1,
			    (u + 1) * SIZE - 12, SIZE / 2 + 6);
		G.drawLine ((u + 1) * SIZE - 7, 3 * SIZE / 4,
			    (u + 1) * SIZE - 12, SIZE / 2 + 7);
		if (POS.Chess960)
		    {
			if (!REVERSE)
			    G.fillArc (POS.White_KR_file * SIZE + 28,
				       SIZE - 11, 7, 7, 0, 360);
			else
			    G.fillArc ((9 - POS.Black_QR_file) * SIZE + 28,
				       SIZE - 11, 7, 7, 0, 360);
		    }
	    }	    
    }

    public void DrawMoveArrows (Graphics G)
    {
	G.setColor (MOVE_ARROWS);
	MoveTree MT = POS.MOVE_TREE;
	if (MT.NOW != MT.ROOT)
	    {
		G.drawLine (SIZE - 5, 9 * SIZE + SIZE / 2, 5, 9 * SIZE + SIZE / 2);
		G.drawLine (5, 9 * SIZE + SIZE / 2, SIZE / 2, 9 * SIZE + 7);
		G.drawLine (5, 9 * SIZE + SIZE / 2, SIZE / 2, 10 * SIZE - 7);
	    }
	if (MT.NOW.getChildCount () > 0)
	    {
		G.drawLine (10 * SIZE - 5, 9 * SIZE + SIZE / 2, 9 * SIZE + 5, 9 * SIZE + SIZE / 2);
		G.drawLine (10 * SIZE - 5, 9 * SIZE + SIZE / 2, 9 * SIZE + SIZE / 2, 9 * SIZE + 7);
		G.drawLine (10 * SIZE - 5, 9 * SIZE + SIZE / 2, 9 * SIZE + SIZE / 2, 10 * SIZE - 7);
	    }
    }

    public void DrawSquares (Graphics G, BoardPosition BP)
    {
	int OFF_SET = CF.SIZE;
	for (int i = 0; i < 8; i++)
	    for (int j = 0; j < 8; j++)
		{
		    if ((i + j) % 2 == 0)
			G.setColor (LIGHT_SQUARES);
		    else
			G.setColor (DARK_SQUARES);
		    G.fillRect (SIZE * i + OFF_SET, SIZE * j + OFF_SET, SIZE, SIZE);
		}
	G.setColor (Color.black); // verity ?
	G.drawLine (OFF_SET - 1, OFF_SET - 1, OFF_SET - 1, OFF_SET + SIZE * 8);
	G.drawLine (OFF_SET - 1, OFF_SET - 1, OFF_SET + SIZE * 8, OFF_SET - 1);
	G.drawLine (OFF_SET + SIZE * 8, OFF_SET + SIZE * 8,
		    OFF_SET + SIZE * 8, OFF_SET - 1);
	G.drawLine (OFF_SET + SIZE * 8, OFF_SET + SIZE * 8,
		    OFF_SET - 1, OFF_SET + SIZE * 8);
	if (POS.EnPassant != 0)
	    {
		G.setColor (EN_PASSANT_COLOR);
		if (REVERSE)
		    {
			int u = 9 - POS.EnPassant;
			if (POS.WTM)
			    G.fillArc (u * SIZE + 8, 6 * SIZE + 8,
				       SIZE - 17, SIZE - 17, 0, 360);
			else
			    G.fillArc (u * SIZE + 8, 3 * SIZE + 8,
				       SIZE - 17, SIZE - 17, 0, 360);
		    }
		else
		    {
			if (POS.WTM)
			    G.fillArc (POS.EnPassant * SIZE + 8, 3 * SIZE + 8,
				       SIZE - 17, SIZE - 17, 0, 360);
			else
			    G.fillArc (POS.EnPassant * SIZE + 8, 6 * SIZE + 8,
				       SIZE - 17, SIZE - 17, 0, 360);
		    }
	    }
    }

    public void DrawWhiteBlackIndicator (Graphics G, BoardPosition BP)
    {
        if (!SET_UP)
	    {
		if (POS.WTM)
		    {
			G.setColor (WHITE_MOVE_INDICATOR);
			if (!REVERSE)
			    G.fillArc (3, 8 * SIZE + 3, SIZE - 7, SIZE - 7, 0, 360);
			else
			    G.fillArc (3, SIZE + 3, SIZE - 7, SIZE - 7, 0, 360);
		    }
		else
		    {
			G.setColor (BLACK_MOVE_INDICATOR);
			if (!REVERSE)
			    G.fillArc (3, SIZE + 3, SIZE - 7, SIZE - 7, 0, 360);
			else
			    G.fillArc (3, 8 * SIZE + 3, SIZE - 7, SIZE - 7, 0, 360);
		    }
	    }
	else
	    {
		if (POS.WTM)
		    G.setColor (Color.white);
		else
		    G.setColor (new Color (160, 160, 160));
		G.fillArc (3, 5 * SIZE + 3, SIZE - 7, SIZE - 7, 0, 360);
		if (!POS.WTM)
		    G.setColor (Color.black);
		else
		    G.setColor (new Color (160, 160, 160));
		G.fillArc (3, 4 * SIZE + 3, SIZE - 7, SIZE - 7, 0, 360);
	    }
	
    }
    
    public void DrawMaterialImbalance (Graphics G, BoardPosition BP)
    {
	char buffer[] = new char[1];
	int PIECES[] = { 0, 0, 0, 0, 0, 0, 0 };
        char TABLE[] = { 0, 79, 77, 86, 84, 87, 76, 80, 78, 66, 82, 81, 75 };
	G.setFont (CF.chess_font_small);
	for (int i = 1; i <= 8; i++)
	    for (int j = 1; j <= 8; j++)
		{
		    if (POS.AT[i][j] > 0)
			PIECES[POS.AT[i][j]]++;
		    else
			PIECES[-POS.AT[i][j]]--;
		}
	int h = 0, v = (REVERSE ? 2 * SIZE - 2 * SIZE / 3 : 9 * SIZE);
        G.setColor (WHITE_MATERIAL_OUTLINE ? BLACK_PIECES : WHITE_PIECES);
	for (int p = 5; p > 0; p--)
	    for (int i = 0; i < PIECES[p]; i++)
		{
                    buffer[0] = (char) (TABLE[p + (WHITE_MATERIAL_OUTLINE ? 6 : 0)] +  32);
		    G.drawChars (buffer, 0, 1, 9 * SIZE + h, v);
		    h += SIZE / 3;
		    if (SIZE - h < 3)
			{
			    h = 0;
			    if (REVERSE)
				v += SIZE / 3;
			    else
				v -= SIZE / 3;
			}
		}
	G.setColor (BLACK_MATERIAL_OUTLINE ? WHITE_PIECES : BLACK_PIECES);
	h = 0;
	v = (!REVERSE ? 2 * SIZE - 2 * SIZE / 3 : 9 * SIZE);
	for (int p = 5; p > 0; p--)
	    for (int i = 0; i > PIECES[p]; i--)
		{
                    buffer[0] = (char) (TABLE[p + (BLACK_MATERIAL_OUTLINE ? 6 : 0)] +  32);
		    G.drawChars (buffer, 0, 1, 9 * SIZE + h, v);
		    h += SIZE / 3;
		    if (SIZE - h < 3)
			{
			    h = 0;
			    if (!REVERSE)
				v += SIZE / 3;
			    else
				v -= SIZE / 3;
			}
		}
    }
    
    public void DrawMajorBoard (Graphics G, BoardPosition BP)
    {
	int OFF_SET = CF.SIZE;
	char buffer[] = new char[1];
	int ilo = 1, ihi = 8, jlo =1, jhi = 8;
	if (SET_UP)
	    {
		ilo--;
		jlo--;
		ihi++;
		jhi++;
		for (int i = 2; i <= 7; i++)
		    BP.AT[i][0] = i - 1;
		for (int i = 2; i <= 7; i++)
		    BP.AT[i][9] = 1 - i;
		G.setColor (YELLOW_BUTTON);
		G.fillArc (9 * SIZE + 3, 3, SIZE - 7, SIZE - 7, 0, 360);
	    }
	else
	    DrawMaterialImbalance (G, BP);
	G.setFont (CF.chess_font);
	for (int i = ilo; i <= ihi; i++)
	    for (int j = jlo; j <= jhi; j++)
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
		    if (!REVERSE) // attend ?
			{
			    horz = SIZE * i;
			    vert = SIZE * (9 - j);
			}
		    else
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

		    G.drawChars (buffer, 0, 1, horz, vert + OFF_SET);
		}
	if (CLICKED)
	    {
		G.setColor (CLICKED_COLOR);
		int x, y;
		if (REVERSE) // HACK
		    {
			x = OFF_SET + SIZE * (9 - CLICKED_x) - 1;
			y = OFF_SET + SIZE * CLICKED_y - 1;
		    }
		else
		    {
			x = OFF_SET + SIZE * CLICKED_x - 1;
			y = OFF_SET + SIZE * (9 - CLICKED_y) - 1;
		    }
		G.drawLine (x, y, x - SIZE + 1, y);
		G.drawLine (x, y, x, y - SIZE + 1);
		G.drawLine (x - SIZE + 1, y - SIZE + 1, x - SIZE + 1, y);
		G.drawLine (x - SIZE + 1, y - SIZE + 1, x, y - SIZE + 1);
	    }
	if (LAST_MOVE_COLOR != null && POS.LAST_FROM_x != 0)
	    {
		G.setColor (LAST_MOVE_COLOR);
		int x, y;
		if (REVERSE) // HACK
		    {
			x = OFF_SET + SIZE * (9 - POS.LAST_FROM_x) - 1;
			y = OFF_SET + SIZE * POS.LAST_FROM_y - 1;
		    }
		else
		    {
			x = OFF_SET + SIZE * POS.LAST_FROM_x - 1;
			y = OFF_SET + SIZE * (9 - POS.LAST_FROM_y) - 1;
		    }
		G.drawLine (x, y, x - SIZE + 1, y);
		G.drawLine (x, y, x, y - SIZE + 1);
		G.drawLine (x - SIZE + 1, y - SIZE + 1, x - SIZE + 1, y);
		G.drawLine (x - SIZE + 1, y - SIZE + 1, x, y - SIZE + 1);
		if (REVERSE) // HACK
		    {
			x = OFF_SET + SIZE * (9 - POS.LAST_TO_x) - 1;
			y = OFF_SET + SIZE * POS.LAST_TO_y - 1;
		    }
		else
		    {
			x = OFF_SET + SIZE * POS.LAST_TO_x - 1;
			y = OFF_SET + SIZE * (9 - POS.LAST_TO_y) - 1;
		    }
		G.drawLine (x, y, x - SIZE + 1, y);
		G.drawLine (x, y, x, y - SIZE + 1);
		G.drawLine (x - SIZE + 1, y - SIZE + 1, x - SIZE + 1, y);
		G.drawLine (x - SIZE + 1, y - SIZE + 1, x, y - SIZE + 1);
	    }
    }

    public void paintComponent (Graphics G)
    {
	G.setColor (BACK_GROUND);
	G.fillRect(0, 0, 1000, 1000);
	DrawSquares (G, POS); // HACK ep
	DrawWhiteBlackIndicator (G, POS);

	char buffer[] = new char[4];
	if (POS.Chess960 || SET_UP)
	    {
		buffer[0] = '9';
		buffer[1] = '6';
		buffer[2] = '0';
		G.setColor (new Color (0, 180, 180));
		G.setFont (new Font ("SansSerif", Font.BOLD, 15));
		G.drawChars (buffer, 0, 3, 5, 105);
	    }
	if (SET_UP)
	    {
		G.setColor (new Color (65, 105, 205));
		G.setFont (new Font ("SansSerif", Font.BOLD, 35));
		buffer[0] = 'C';
		G.drawChars (buffer, 0, 1, 9, 35);
		if (!POS.Chess960)
		    {
			G.setColor (Color.red);
			G.drawLine (3, 85, 36, 115);
			G.drawLine (3, 115, 36, 85);
		    }
	    }
	DrawMajorBoard (G, POS);
	if (SET_UP)
	    return;
	DrawCastling (G);
	DrawMoveArrows (G);
    }
}

