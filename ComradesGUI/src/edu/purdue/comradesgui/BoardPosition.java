package edu.purdue.comradesgui;

import java.util.*;

public class BoardPosition
{
    int AT[][];
    int WHITE_KingFile, WHITE_KingRank, BLACK_KingFile, BLACK_KingRank;
    int MOVE_NUMBER, EnPassant, ReversibleCount;
    int COUNT_OF_LEGAL_MOVES;
    boolean WTM, WhiteOOO, WhiteOO, BlackOOO, BlackOO, Chess960;
    int White_KR_file, White_QR_file, Black_KR_file, Black_QR_file;
    int Chess960_WK_File, Chess960_BK_File;
    int move_list[];
    String move_list_annotated[];
    ComradesFrame CF;
    MoveTree MOVE_TREE = null;
    BoardPanel PANEL = null;
    MovePane MOVE_PANE = null;
    int LAST_FROM_x = 0;
    int LAST_FROM_y = 0;
    int LAST_TO_x = 0;
    int LAST_TO_y = 0;

    public BoardPosition (BoardPosition BP) // copy
    {
	AT = new int[10][10];
	for (int i = 1; i <= 8; i++)
	    for (int j = 1; j <= 8; j++)
		AT[i][j] = BP.AT[i][j];
	WHITE_KingFile = BP.WHITE_KingFile;
	WHITE_KingRank = BP.WHITE_KingRank;
	BLACK_KingFile = BP.BLACK_KingFile;
	BLACK_KingRank = BP.BLACK_KingRank;
	MOVE_NUMBER = BP.MOVE_NUMBER;
	EnPassant = BP.EnPassant;
	ReversibleCount = BP.ReversibleCount;
	COUNT_OF_LEGAL_MOVES = BP.COUNT_OF_LEGAL_MOVES;
	WTM = BP.WTM;
	Chess960 = BP.Chess960;
	White_KR_file = BP.White_KR_file;
	White_QR_file = BP.White_QR_file;
	Black_KR_file = BP.Black_KR_file;
	Black_QR_file = BP.Black_QR_file;
	Chess960_WK_File = BP.Chess960_WK_File;
	Chess960_BK_File = BP.Chess960_BK_File;
	WhiteOOO = BP.WhiteOOO;
	WhiteOO = BP.WhiteOO;
	BlackOOO = BP.BlackOOO;
	BlackOO = BP.BlackOO;
	move_list = new int[256];
	move_list_annotated = new String[256];
	for (int i = 0; i < 256; i++)
	    {
		move_list[i] = BP.move_list[i];
		move_list_annotated[i] = new String (BP.move_list_annotated[i]);
	    }		    
	CF = BP.CF;
	MOVE_TREE = null; // ?
	MOVE_PANE = null;
    }

    public BoardPosition (ComradesFrame cf, MovePane mp)
    {
	super ();
	CF = cf;
	MOVE_PANE = mp;
	AT = new int[10][10];
	for (int i = 0; i < 10; i++)
	    for (int j = 0; j < 10; j++)
		AT[i][j] = 0;
	AT[1][1] = 4;
	AT[2][1] = 2;
	AT[3][1] = 3;
	AT[4][1] = 5;
	AT[5][1] = 6;
	AT[6][1] = 3;
	AT[7][1] = 2;
	AT[8][1] = 4;
	for (int i = 1; i <= 8; i++)
	    AT[i][8] = -AT[i][1];
	for (int i = 1; i <= 8; i++)
	    AT[i][2] = 1;
	for (int i = 1; i <= 8; i++)
	    AT[i][7] = -1;

	WTM = true;
	MOVE_NUMBER = 1;
	Chess960 = false;
	Chess960_WK_File = Chess960_BK_File = 5;
	White_KR_file = Black_KR_file = 8;
	White_QR_file = Black_QR_file = 1;
	WhiteOOO = true;
	WhiteOO = true;
	BlackOOO = true;
	BlackOO = true;
	EnPassant = 0;
	ReversibleCount = 0;
	move_list = new int[256];
	move_list_annotated = new String[256];
	for (int i = 0; i < 256; i++)
	    {
		move_list[i] = 0;
		move_list_annotated[i] = new String ("");
	    }
	MakeNormal ();
	NewMoveTree ();
    }

    public void NewMoveTree ()
    {
	MOVE_TREE = new MoveTree (this, new ComradesNode (), MOVE_PANE.PANEL);
    }

    public void ClearBoard ()
    {
	for (int i = 1; i <= 8; i++)
	    for (int j = 1; j <= 8; j++)
		AT[i][j] = 0;
	EnPassant = 0;
	WTM = true;
	WhiteOO = WhiteOOO = BlackOOO = BlackOO = false;
	ReversibleCount = 0;
	MOVE_NUMBER = 1;
    }

    public void Chess960_New_Game ()
    {
	int c = 0;
	for (int i = 1; i <= 8; i++)
	    for (int j = 1; j <= 8; j++)
		AT[i][j] = 0;
        for (int i = 1; i <= 8; i++)
            AT[i][2] = 1;
        for (int i = 1; i <= 8; i++)
            AT[i][7] = -1;
	Random gen = new Random ();
	int s1 = 2 * (1 + (gen.nextInt (4))) - 1;
	AT[s1][1] = 3;
	int s2 = 2 * (1 + (gen.nextInt (4)));
	AT[s2][1] = 3;
	int s3 = 1 + (gen.nextInt (6));
	c = 0;
	for (int i = 1; i <= 8; i++)
	    {
		if (AT[i][1] == 0)
		    {
			c++;
			if (c == s3)
			    {
				AT[i][1] = 5;
				break;
			    }
		    }
	    }
	int s4 = 1 + (gen.nextInt (5));
	c = 0;
	for (int i = 1; i <= 8; i++)
	    {
		if (AT[i][1] == 0)
		    {
			c++;
			if (c == s4)
			    {
				AT[i][1] = 2;
				break;
			    }
		    }
	    }
	int s5 = 1 + (gen.nextInt (4));
	c = 0;
	for (int i = 1; i <= 8; i++)
	    {
		if (AT[i][1] == 0)
		    {
			c++;
			if (c == s5)
			    {
				AT[i][1] = 2;
				break;
			    }
		    }
	    }
	for (int i = 1; i <= 8; i++)
	    {
		if (AT[i][1] == 0)
		    {
			AT[i][1] = 4;
			White_QR_file = i;
			break;
		    }
	    }
	for (int i = 1; i <= 8; i++)
	    {
		if (AT[i][1] == 0)
		    {
			AT[i][1] = 6;
			Chess960_WK_File = i;
			break;
		    }
	    }
	for (int i = 1; i <= 8; i++)
	    {
		if (AT[i][1] == 0)
		    {
			AT[i][1] = 4;
			White_KR_file = i;
			break;
		    }
	    }
	for (int i = 1; i <= 8; i++)
            AT[i][8] = -AT[i][1];
        WTM = true;
	MOVE_NUMBER = 1;
	Chess960 = true;
        WhiteOOO = WhiteOO = true;
	Chess960_BK_File = Chess960_WK_File;
	Black_QR_file = White_QR_file;
	Black_KR_file = White_KR_file;
        BlackOOO = BlackOO = true;
        EnPassant = 0;
        ReversibleCount = 0;
    }

    public void NewGame ()
    {
	if (Chess960)
	    {
		Chess960_New_Game ();
		return;
	    }
	for (int i = 1; i <= 8; i++)
	    for (int j = 1; j <= 8; j++)
		AT[i][j] = 0;
        AT[1][1] = 4;
        AT[2][1] = 2;
        AT[3][1] = 3;
	AT[4][1] = 5;
        AT[5][1] = 6;
        AT[6][1] = 3;
        AT[7][1] = 2;
        AT[8][1] = 4;
	for (int i = 1; i <= 8; i++)
            AT[i][8] = -AT[i][1];
        for (int i = 1; i <= 8; i++)
            AT[i][2] = 1;
        for (int i = 1; i <= 8; i++)
            AT[i][7] = -1;

        WTM = true;
	MOVE_NUMBER = 1;
	Chess960 = false;
	White_KR_file = Black_KR_file = 8;
	White_QR_file = Black_QR_file = 1;
	Chess960_WK_File = Chess960_BK_File = 5;
        WhiteOOO = WhiteOO = true;
        BlackOOO = BlackOO = true;
        EnPassant = 0;
        ReversibleCount = 0;
    }

    public void SetFEN (StringTokenizer ST)
    {
	String S, n;
	S = ST.nextToken ();
	StringBuffer SB = new StringBuffer (S);
	int rank = 8;
	int file = 1;
	Chess960_WK_File = Chess960_BK_File = 0;

	for (int i = 1; i <= 8; i++)
	    for (int j = 1; j <= 8; j++)
		AT[i][j] = 0;
	for (int w = 0; w < S.length(); w++)
	    {
		char c = SB.charAt (w);
		if (rank < 1)
		    {
			CF.TellInfo ("Bad FEN? Too many ranks");
			return;
		    }
		if (c == '/')
		    {
			rank--;
			file = 1;
		    }
		else if (file > 8)
		    {
			CF.TellInfo ("Bad FEN? Too many files in rank " + rank);
			return;
		    }
		if (c == '1')
		    file++;
		if (c == '2')
		    file += 2;
		if (c == '3')
		    file += 3;
		if (c == '4')
		    file += 4;
		if (c == '5')
		    file += 5;
		if (c == '6')
		    file += 6;
		if (c == '7')
		    file += 7;
		if (c == '8')
		    file += 8;
		if (c == 'K')
		    {
			WHITE_KingFile = file;
			Chess960_WK_File = file;
			WHITE_KingRank = rank;
			AT[file++][rank] = 6;
		    }
		if (c == 'Q')
		    AT[file++][rank] = 5;
		if (c == 'R')
		    {
			if (rank == 1)
			    {
				if (Chess960_WK_File == 0)
				    White_QR_file = file;
				else
				    White_KR_file = file;
			    }
			AT[file++][rank] = 4;
		    }
		if (c == 'B')
		    AT[file++][rank] = 3;
		if (c == 'N')
		    AT[file++][rank] = 2;
		if (c == 'P')
		    AT[file++][rank] = 1;
		if (c == 'k')
		    {
			BLACK_KingFile = file;
			Chess960_BK_File = file;
			BLACK_KingRank = rank;
			AT[file++][rank] = -6;
		    }
		if (c == 'q')
		    AT[file++][rank] = -5;
		if (c == 'r')
		    {
			if (rank == 8)
			    {
				if (Chess960_BK_File == 0)
				    Black_QR_file = file;
				else
				    Black_KR_file = file;
			    }
			AT[file++][rank] = -4;
		    }
		if (c == 'b')
		    AT[file++][rank] = -3;
		if (c == 'n')
		    AT[file++][rank] = -2;
		if (c == 'p')
		    AT[file++][rank] = -1;
		if ((new String ("/12345678KQRBNPkqrnbp").indexOf (c)) == -1)
		    {
			CF.TellInfo ("Bad FEN? character " + c + " at rank " + rank + " and file " + file);
			return;
		    }
	    }
	WTM = true;
	EnPassant = 0;
	MOVE_NUMBER = 1;
	WhiteOO = WhiteOOO = BlackOO = BlackOOO = true;
	if (ST.hasMoreTokens ())
	    {
		n = ST.nextToken ();
		if (n.equals ("b"))
		    WTM = false;
	    }
	if (ST.hasMoreTokens ())
	    {
		n = ST.nextToken();
		char ch = n.charAt (0);
		if ((new String ("ABCDEFGHabcdefgh").indexOf (ch)) != -1)
		    {   // Chess960
			WhiteOOO = BlackOOO = WhiteOO = BlackOO = false;
			for (int i = 0; i < n.length (); i++)
			    {
				int c = n.charAt (i) - 'A' + 1;
				if (c >= 1 && c <= 8 && AT[c][1] == 4)
				    {
					if (WHITE_KingFile > c)
					    {
						WhiteOOO = true;
						White_QR_file = c;
					    }
					if (WHITE_KingFile < c)
					    {
						WhiteOO = true;
						White_KR_file = c;
					    }
				    }
				c = n.charAt (i) - 'a' + 1;
				if (c >= 1 && c <= 8 && AT[c][8] == -4)
				    {
					if (BLACK_KingFile > c)
					    {
						BlackOOO = true;
						Black_QR_file = c;
					    }
					if (BLACK_KingFile < c)
					    {
						BlackOO = true;
						Black_KR_file = c;
					    }
				    }
			    }
		    }
		if ((new String ("KQkq-").indexOf (ch)) != -1)
		    {
			if (n.equals ("KQk"))
			    BlackOOO = false;
			if (n.equals ("KQq"))
			    BlackOO = false;
			if (n.equals ("KQ"))
			    {
				BlackOOO = false;
				BlackOO = false;
			    }
			if (n.equals ("Kkq"))
			    WhiteOOO = false;
			if (n.equals ("Qkq"))
			    WhiteOO = false;
			if (n.equals ("kq"))
			    {
				WhiteOOO = false;
				WhiteOO = false;
			    }
			if (n.equals ("K"))
			    {
				BlackOOO = false;
				BlackOO = false;
				WhiteOOO = false;
			    }
			if (n.equals ("Q"))
			    {
				BlackOOO = false;
				BlackOO = false;
				WhiteOO = false;
			    }
			if (n.equals ("k"))
			    {
				BlackOOO = false;
				WhiteOO = false;
				WhiteOOO = false;
			    }
			if (n.equals ("q"))
			    {
				WhiteOOO = false;
				BlackOO = false;
				WhiteOO = false;
			    }
			if (n.equals ("Kq"))
			    {
				BlackOO = false;
				WhiteOOO = false;
			    }
			if (n.equals ("Kk"))
			    {
				BlackOOO = false;
				WhiteOOO = false;
			    }
			if (n.equals ("Qk"))
			    {
				BlackOOO = false;
				WhiteOO = false;
			    }
			if (n.equals ("Qq"))
			    {
				BlackOO = false;
				WhiteOO = false;
			    }
			if (n.equals ("-"))
			    {
				BlackOO = false;
				WhiteOO = false;
				WhiteOOO = false;
				BlackOOO = false;
			    }
		    }
	    }
	if (ST.hasMoreTokens ())
	    {
		n = ST.nextToken();
		SB = new StringBuffer (n);
		if (n.equals ("-"))
		    EnPassant = 0;
		else
		    EnPassant = ((int) (SB.charAt (0) - 'a')) + 1;
	    }
	if (ST.hasMoreTokens ())
	    {
		n = ST.nextToken ();
		ReversibleCount = Integer.valueOf (n).intValue ();
	    }
	if (ST.hasMoreTokens ())
	    {
		n = ST.nextToken ();
		MOVE_NUMBER = Integer.valueOf (n).intValue ();
	    }
	MakeNormal ();
	if (MOVE_TREE != null)
	    MOVE_TREE.ReSet ();
    }

    public String GetPositionalFEN ()
    {
	String R = new String ("");
	int rank = 8, file = 1, c = 0;
	while (true)
	    {
		while (AT[file][rank] == 0)
		    {
			file++;
			c++;
			if (file == 9)
			    {
				R += "" + c + "";
				if (rank != 1)
				    R += "/";
				else
				    return R;
				rank--;
				file = 1;
				c = 0;
			    }
		    }
		if (c != 0)
		    R = R + c;
		switch (AT[file][rank])
		    {
		    case 1: R = R + "P"; break;
		    case 2: R = R + "N"; break;
		    case 3: R = R + "B"; break;
		    case 4: R = R + "R"; break;
		    case 5: R = R + "Q"; break;
		    case 6: R = R + "K"; break;
		    case -1: R = R + "p"; break;
		    case -2: R = R + "n"; break;
		    case -3: R = R + "b"; break;
		    case -4: R = R + "r"; break;
		    case -5: R = R + "q"; break;
		    case -6: R = R + "k"; break;
		    }
		file++;
		c = 0;
		if (file == 9)
		    {
			if (rank != 1)
			    R += "/";
			else
			    return R;
			rank--;
			file = 1;
			c = 0;
		    }
	    }
    }

    public String GetFEN ()
    {
	String R = GetPositionalFEN ();
	if (WTM)
	    R += " w ";
	else
	    R += " b ";
	if (!WhiteOOO && !WhiteOO && !BlackOOO && !BlackOO)
	    R += "-";
	if (!Chess960)
	    {
		if (WhiteOO)
		    R += "K";
		if (WhiteOOO)
		    R += "Q";
		if (BlackOO)
		    R += "k";
		if (BlackOOO)
		    R += "q";
	    }
	else
	    {
		String S[] = { "", "A", "B", "C", "D", "E", "F", "G", "H" };
		if (WhiteOO)
		    R += S[White_KR_file];
		if (WhiteOOO)
		    R += S[White_QR_file];
		String T[] = { "", "a", "b", "c", "d", "e", "f", "g", "h" };
		if (BlackOO)
		    R += T[Black_KR_file];
		if (BlackOOO)
		    R += T[Black_QR_file];
	    }
	R += " ";
	if (EnPassant == 0)
	    R += "-";
	else
	    {
		if (!WTM)
		    R += (char) (((char) EnPassant) + 'a' - (char) 1) + "" + 3;
		else
		    R += (char) (((char) EnPassant) + 'a' - (char) 1) + "" + 6;
	    }
	R += " " + ReversibleCount + " " + MOVE_NUMBER;
	return R;
    }

    public int FindMove (String S)
    {
	StringBuffer SB = new StringBuffer (S);
	int u = 0;
	while (!IsLetter (SB.charAt (u)))
	    u++;
	int from_file = SB.charAt (u) - 'a';
	u++;
	while (!IsDigit (SB.charAt (u)))
	    u++;
	int from_rank = SB.charAt (u) - '1';
	u++;
	while (!IsLetter (SB.charAt (u)))
	    u++;
	int to_file = SB.charAt (u) - 'a';
	u++;
	while (!IsDigit (SB.charAt (u)))
	    u++;
	int to_rank = SB.charAt (u) - '1';
	u++;
	boolean prom = false;
	char pr = 'a';
	int v = from_file + (from_rank << 3) + (to_file << 6) + (to_rank << 9);
	if (S.length () > u)
	    {
		prom = true;
		while (SB.charAt (u) == '=') u++;
		pr = SB.charAt (u);
		if (pr < 'a')
		    pr += 'a' - 'A';
	    }
	for (int i = 0; i < COUNT_OF_LEGAL_MOVES; i++)
	    {
		if (v == (move_list[i] & 0xfff))
		    {
			if (!prom)
			    return i;
			int w = (move_list[i] >> 17) & 7;
			if (pr == 'q' && w == 5)
			    return i;
			if (pr == 'r' && w == 4)
			    return i;
			if (pr == 'b' && w == 3)
			    return i;
			if (pr == 'n' && w == 2)
			    return i;
		    }
	    }
	return -1;
    }

    public boolean AttacksWhite (int file, int rank)
    {
	int piece, i;
	i = 1;
	while (rank + i <= 8) // forward
	    {
		piece = AT[file][rank + i];
		if (piece < 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == 4 || (piece == 6 && i == 1) || piece == 5))
		 return true;
	     break;
	 }
	i = 1;
	while (rank - i >= 1) // behind
	    {
		piece = AT[file][rank - i];
		if (piece < 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == 4 || (piece == 6 && i == 1) || piece == 5))
		    return true;
		break;
	    }
	i = 1;
	while (file - i >= 1) // to left
	    {
		piece = AT[file - i][rank];
		if (piece < 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == 4 || (piece == 6 && i == 1) || piece == 5))
		    return true;
		break;
	    }
	i = 1;
	while (file + i <= 8) // to right
	    {
		piece = AT[file + i][rank];
		if (piece < 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == 4 || (piece == 6 && i == 1) || piece == 5))
		    return true;
		break;
	    }
	i = 1;
	while ((rank + i <= 8) && (file + i <= 8)) // forward right
	    {
		piece = AT[file + i][rank + i];
		if (piece < 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == 3 || (piece == 6 && i == 1) || piece == 5))
		    return true;
		break;
	    }
	i = 1;
	while ((rank + i <= 8) && (file - i >= 1)) // forward left
	    {
		piece = AT[file - i][rank + i];
		if (piece < 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == 3 || (piece == 6 && i == 1) || piece == 5))
		    return true;
		break;
	    }
	i = 1;
	while ((rank - i >= 1) && (file + i <= 8)) // behind right
	    {
		piece = AT[file + i][rank - i];
		if (piece < 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == 3 || (piece == 6 && i == 1) || piece == 5 ||
		     (piece == 1 && i == 1)))
		    return true;
		break;
	    }
	i = 1;
	while ((rank - i >= 1) && (file - i >= 1)) // behind left
	    {
		piece = AT[file - i][rank - i];
		if (piece < 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == 3 || (piece == 6 && i == 1) || piece == 5 || 
		     (piece == 1 && i == 1)))
		    return true;
		break;
	    }
	if ((((file + 2) <= 8) && ((rank + 1) <= 8) &&
	     ((file + 2) >= 1) && ((rank + 1) >= 1) &&
	     (AT[(file + 2)][(rank + 1)] == 2)))
	    return true;
	if ((((file + 1) <= 8) && ((rank + 2) <= 8) &&
	     ((file + 1) >= 1) && ((rank + 2) >= 1) &&
	     (AT[(file + 1)][(rank + 2)] == 2)))
	    return true;
	if ((((file - 2) <= 8) && ((rank - 1) <= 8) &&
	     ((file - 2) >= 1) && ((rank - 1) >= 1) &&
	     (AT[(file - 2)][(rank - 1)] == 2)))
	    return true;
	if ((((file - 1) <= 8) && ((rank - 2) <= 8) &&
	     ((file - 1) >= 1) && ((rank - 2) >= 1) &&
	     (AT[(file - 1)][(rank - 2)] == 2)))
	    return true;
	if ((((file - 2) <= 8) && ((rank + 1) <= 8) &&
	     ((file - 2) >= 1) && ((rank + 1) >= 1) &&
	     (AT[(file - 2)][(rank + 1)] == 2)))
	    return true;
	if ((((file + 1) <= 8) && ((rank - 2) <= 8) &&
	     ((file + 1) >= 1) && ((rank - 2) >= 1) &&
	     (AT[(file + 1)][(rank - 2)] == 2)))
	    return true;
	if ((((file + 2) <= 8) && ((rank - 1) <= 8) &&
	     ((file + 2) >= 1) && ((rank - 1) >= 1) &&
	     (AT[(file + 2)][(rank - 1)] == 2)))
	    return true;
	if ((((file - 1) <= 8) && ((rank + 2) <= 8) &&
	     ((file - 1) >= 1) && ((rank + 2) >= 1) &&
	     (AT[(file - 1)][(rank + 2)] == 2)))
	    return true;
	return false;
    }

    public boolean AttacksBlack (int file, int rank)
    {
	int piece, i;
	i = 1;
	while (rank + i <= 8)
	    {
		piece = AT[file][rank + i];
		if (piece > 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == -4 || (piece == -6 && i == 1) || piece == -5))
		    return true;
		break;
	    }
	i = 1;
	while (rank - i >= 1)
	    {
		piece = AT[file][rank - i];
		if (piece > 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == -4 || (piece == -6 && i == 1) || piece == -5))
		    return true;
		break;
	    }
	i = 1;
	while (file - i >= 1)
	    {
		piece = AT[file - i][rank];
		if (piece > 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == -4 || (piece == -6 && i == 1) || piece == -5))
		    return true;
		break;
	    }
	i = 1;
	while (file + i <= 8)
	    {
		piece = AT[file + i][rank];
		if (piece > 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == -4 || (piece == -6 && i == 1) || piece == -5))
		    return true;
		break;
	    }
	i = 1;
	while (rank + i <= 8 && file + i <= 8)
	    {
		piece = AT[file + i][rank + i];
		if (piece > 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == -3 || (piece == -6 && i == 1) || piece == -5
		     || (piece == -1 && i == 1)))
		    return true;
		break;
	    }
	i = 1;
	while (rank + i <= 8 && file - i >= 1)
	    {
		piece = AT[file - i][rank + i];
		if (piece > 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == -3 || (piece == -6 && i == 1) || piece == -5
		     || (piece == -1 && i == 1)))
		    return true;
		break;
	    }
	i = 1;
	while (rank - i >= 1 && file + i <= 8)
	    {
		piece = AT[file + i][rank - i];
		if (piece > 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == -3 || (piece == -6 && i == 1) || piece == -5))
		    return true;
		break;
	    }
	i = 1;
	while (rank - i >= 1 && file - i >= 1)
	    {
		piece = AT[file - i][rank - i];
		if (piece > 0)
		    break;
		if (piece == 0)
		    {
			i++;
			continue;
		    }
		if ((piece == -3 || (piece == -6 && i == 1) || piece == -5))
		    return true;
		break;
	    }
	if (file + 2 <= 8 && rank + 1 <= 8 && file + 2 >= 1 && rank + 1 >= 1
	    && AT[file + 2][rank + 1] == -2)
	    return true;
	if ((((file + 1) <= 8) && ((rank + 2) <= 8) && ((file + 1) >= 1)
	     && ((rank + 2) >= 1) && (AT[(file + 1)][(rank + 2)] == -2)))
	    return true;
	if ((((file - 2) <= 8) && ((rank - 1) <= 8) && ((file - 2) >= 1)
	     && ((rank - 1) >= 1) && (AT[(file - 2)][(rank - 1)] == -2)))
	    return true;
	if ((((file - 1) <= 8) && ((rank - 2) <= 8) && ((file - 1) >= 1)
	     && ((rank - 2) >= 1) && (AT[(file - 1)][(rank - 2)] == -2)))
	    return true;
	if ((((file - 2) <= 8) && ((rank + 1) <= 8) && ((file - 2) >= 1)
	     && ((rank + 1) >= 1) && (AT[(file - 2)][(rank + 1)] == -2)))
	    return true;
	if ((((file + 1) <= 8) && ((rank - 2) <= 8) && ((file + 1) >= 1)
	     && ((rank - 2) >= 1) && (AT[(file + 1)][(rank - 2)] == -2)))
	    return true;
	if ((((file + 2) <= 8) && ((rank - 1) <= 8) && ((file + 2) >= 1)
	     && ((rank - 1) >= 1) && (AT[(file + 2)][(rank - 1)] == -2)))
	    return true;
	if ((((file - 1) <= 8) && ((rank + 2) <= 8) && ((file - 1) >= 1)
	     && ((rank + 2) >= 1) && (AT[(file - 1)][(rank + 2)] == -2)))
	    return true;
	return false;
    }

    public void AddThisMove (int v)
    {
	int c = 0; // is_check
	if (WTM && AttacksWhite (BLACK_KingFile, BLACK_KingRank))
	    c = 1;
	if (!WTM && AttacksBlack (WHITE_KingFile, WHITE_KingRank))
	    c = 1;
	move_list[COUNT_OF_LEGAL_MOVES++] = v + (c << 16);
    }
    
    public boolean IsLetter (char x)
    {
	return ((x >= 'a') && (x <= 'h'));
    }
    
    public boolean IsDigit (char x)
    {
	return ((x >= '1') && (x <= '8'));
    }

    public void AddMove
	(int from_file, int from_rank, int to_file, int to_rank, int type)
    {
	int v = (from_file - 1) + ((from_rank - 1) << 3) +
	    ((to_file - 1) << 6) + ((to_rank - 1) << 9) + (type << 12);
	if (WTM && AT[to_file][to_rank] == 1 && from_rank == 7)
	    {
		for (int i = 5; i >= 2; i--)
		    {
			AT[to_file][to_rank] = i;
			AddThisMove (v + (i << 17));
		    }
		AT[to_file][to_rank] = 1;
		return;
	    }
	if (!WTM && AT[to_file][to_rank] == -1 && from_rank == 2)
	    {
		for (int i = 5; i >= 2; i--)
		    {
			AT[to_file][to_rank] = -i;
			AddThisMove (v + (i << 17));
		    }
		AT[to_file][to_rank] = -1;
		return;
	    }
	AddThisMove (v);
    }

    public void LegalWhite
	(int from_file, int from_rank, int to_file, int to_rank, int type)
    {
	int piece = AT[to_file][to_rank];
	if (Chess960 && type == 6)
	    {
		for (int i = Chess960_WK_File; i <= 7; i++)
		    if (AttacksBlack (i, 1))
			return;
		AT[Chess960_WK_File][1] = 0;
		AT[White_KR_file][1] = 0;
		AT[6][1] = 4;
		AT[7][1] = 6;
		AddMove (Chess960_WK_File, 1, White_KR_file, 1, 6);
		AT[6][1] = 0;
		AT[7][1] = 0;
		AT[Chess960_WK_File][1] = 6;
		AT[White_KR_file][1] = 4;
		return;
	    }
	if (Chess960 && type == 7)
	    {
		if (Chess960_WK_File == 2 && AttacksBlack (2, 1))
		    return;
		if (AttacksBlack (3, 1))
		    return;
		for (int i = Chess960_WK_File; i > 3; i--)
		    if (AttacksBlack (i, 1))
			return;
		AT[Chess960_WK_File][1] = 0;
		AT[White_QR_file][1] = 0;
		AT[4][1] = 4;
		AT[3][1] = 6;
		AddMove (Chess960_WK_File, 1, White_QR_file, 1, 7);
		AT[4][1] = 0;
		AT[3][1] = 0;
		AT[Chess960_WK_File][1] = 6;
		AT[White_QR_file][1] = 4;
		return;
	    }
	if (!Chess960 && type == 6)
	    {
		if (AttacksBlack (5, 1) || AttacksBlack (6, 1) || AttacksBlack (7, 1))
		    return;
		AT[5][1] = 0;
		AT[6][1] = 4;
		AT[7][1] = 6;
		AT[8][1] = 0;
		AddMove (5, 1, 7, 1, 6);
		AT[5][1] = 6;
		AT[6][1] = 0;
		AT[7][1] = 0;
		AT[8][1] = 4;
		return;
	    }
	if (!Chess960 && type == 7)
	    {
		if (AttacksBlack (5, 1) || AttacksBlack (4, 1) || AttacksBlack (3, 1))
		    return;
		AT[5][1] = 0;
		AT[4][1] = 4;
		AT[3][1] = 6;
		AT[1][1] = 0;
		AddMove (5, 1, 3, 1, 7);
		AT[5][1] = 6;
		AT[4][1] = 0;
		AT[3][1] = 0;
		AT[1][1] = 4;
		return;
	    }
	AT[to_file][to_rank] = AT[from_file][from_rank];
	AT[from_file][from_rank] = 0;
	if (type == 8)
	    AT[to_file][to_rank - 1] = 0; // en passant
	if (AT[to_file][to_rank] == 6)
	    {
		if (!AttacksBlack (to_file, to_rank))
		    AddMove (from_file, from_rank, to_file, to_rank, type);
	    }
	else
	    {
		if (!AttacksBlack (WHITE_KingFile, WHITE_KingRank))
		    AddMove (from_file, from_rank, to_file, to_rank, type);
	    }
	AT[from_file][from_rank] = AT[to_file][to_rank];
	AT[to_file][to_rank] = piece;
	if (type == 8)
	    AT[to_file][to_rank - 1] = -1; // en passant
    }
    
    public void LegalBlack
	(int from_file, int from_rank, int to_file, int to_rank, int type)
    {
	int piece = AT[to_file][to_rank];
	if (Chess960 && type == 6)
	    {
		for (int i = Chess960_BK_File; i <= 7; i++)
		    if (AttacksWhite (i, 8))
			return;
		AT[Chess960_BK_File][8] = 0;
		AT[Black_KR_file][8] = 0;
		AT[6][8] = -4;
		AT[7][8] = -6;
		AddMove (Chess960_BK_File, 8, Black_KR_file, 8, 6);
		AT[6][8] = 0;
		AT[7][8] = 0;
		AT[Chess960_BK_File][8] = -6;
		AT[Black_KR_file][8] = -4;
		return;
	    }
	if (Chess960 && type == 7)
	    {
		if (Chess960_BK_File == 2 && AttacksWhite (2, 8))
		    return;
		if (AttacksWhite (3, 8))
		    return;
		for (int i = Chess960_BK_File; i > 3; i--)
		    if (AttacksWhite (i, 8))
			return;
		AT[Chess960_BK_File][8] = 0;
		AT[Black_QR_file][8] = 0;
		AT[4][8] = -4;
		AT[3][8] = -6;
		AddMove (Chess960_BK_File, 8, Black_QR_file, 8, 7);
		AT[3][8] = 0;
		AT[4][8] = 0;
		AT[Chess960_BK_File][8] = -6;
		AT[Black_QR_file][8] = -4;
		return;
	    }
	if (!Chess960 && type == 6)
	    {
		if (AttacksWhite (5, 8) || AttacksWhite (6, 8) || AttacksWhite (7, 8))
		    return;
		AT[5][8] = 0;
		AT[6][8] = -4;
		AT[7][8] = -6;
		AT[8][8] = 0;
		AddMove (5, 8, 7, 8, 6);
		AT[5][8] = -6;
		AT[6][8] = 0;
		AT[7][8] = 0;
		AT[8][8] = -4;
		return;
	    }
	if (!Chess960 && type == 7)
	    {
		if (AttacksWhite (5, 8) || AttacksWhite (4, 8) || AttacksWhite (3, 8)) 
		    return;
		AT[5][8] = 0;
		AT[4][8] = -4;
		AT[3][8] = -6;
		AT[1][8] = 0;
		AddMove(5, 8, 3, 8, 7);
		AT[5][8] = -6;
		AT[4][8] = 0;
		AT[3][8] = 0;
		AT[1][8] = -4;
		return;
	    }
	AT[to_file][to_rank] = AT[from_file][from_rank];
	AT[from_file][from_rank] = 0;
	if (type == 8)
	    AT[to_file][to_rank + 1] = 0; // en passant
	if (AT[to_file][to_rank] == -6)
	    {
		if (!AttacksWhite (to_file, to_rank))
		    AddMove (from_file, from_rank, to_file, to_rank, type);
	    }
	else
	    {
		if (!AttacksWhite (BLACK_KingFile, BLACK_KingRank))
		    AddMove (from_file, from_rank, to_file, to_rank, type);
	    }
	AT[from_file][from_rank] = AT[to_file][to_rank];
	AT[to_file][to_rank] = piece;
	if (type == 8)
	    AT[to_file][to_rank + 1] = 1; // en passant
    }

    public boolean CheckMove
	(int from_file, int from_rank, int to_file, int to_rank)
    {
	if (WTM)
	    {
		if (AT[to_file][to_rank] == 0)
		    {
			LegalWhite (from_file, from_rank, to_file, to_rank, 0);
			return false;
		    }
		if (AT[to_file][to_rank] < 0 && AT[to_file][to_rank] != -6)
		    LegalWhite (from_file, from_rank, to_file, to_rank, -AT[to_file][to_rank]);
		return true;
	    }
	else
	    {
		if (AT[to_file][to_rank] == 0)
		    {
			LegalBlack (from_file, from_rank, to_file, to_rank, 0);
			return false;
		    }
		if (AT[to_file][to_rank] > 0 && AT[to_file][to_rank] != 6)
		    LegalBlack (from_file, from_rank, to_file, to_rank, AT[to_file][to_rank]);
		return true;
	    }
    }

    public void GenerateMoves (int file, int rank)
    {
	int i, j, piece = AT[file][rank];
	if (!WTM)
	    piece = -piece;
	if (piece == 6 || piece == 5 || piece == 4)
	    {
		i = 1;
		while (file + i <= 8)
		    {
			if (CheckMove (file, rank, file + i, rank) || piece == 6)
			    break;
			i++;
		    }
		i = 1;
		while (file - i >= 1)
		    {
			if (CheckMove (file, rank, file - i, rank) || piece == 6)
			    break;
			i++;
		    }
		j = 1;
		while (rank + j <= 8)
		    {
			if (CheckMove (file, rank, file, rank + j) || piece == 6)
			    break;
			j++;
		    }
		j = 1;
		while (rank - j >= 1)
		    {
			if (CheckMove (file, rank, file, rank - j) || piece == 6)
			    break;
			j++;
		    }
	    }
	if (piece == 6 || piece == 5 ||  piece == 3)
	    {
		i = 1;
		while (file + i <= 8 && rank + i <= 8)
		    {
			if (CheckMove (file, rank, file + i, rank + i) || piece == 6)
			    break;
			i++;
		    }
		i = 1;
		while (file - i >= 1 && rank - i >= 1)
		    {
			if (CheckMove (file ,rank, file - i, rank - i) || piece == 6)
			    break;
			i++;
		    }
		i = 1;
		while (file + i <= 8 && rank - i >= 1)
		    {
			if (CheckMove (file, rank, file + i, rank - i) || piece == 6)
			    break;
			i++;
		    }
		i = 1;
		while (file - i >= 1 && rank + i <= 8)
		    {
			if (CheckMove (file, rank, file - i, rank + i) || piece == 6)
			    break;
			i++;
		    }
	    }
	if (piece == 2)
	    {
		if (file + 2 <= 8 && rank + 1 <= 8)
		    CheckMove (file, rank, file + 2, rank + 1);
		if (file + 1 <= 8 && rank + 2 <= 8)
		    CheckMove (file, rank, file + 1, rank + 2);
		if (file - 2 >= 1 && rank + 1 <= 8)
		    CheckMove (file, rank, file - 2, rank + 1);
		if (file - 1 >= 1 && rank + 2 <= 8)
		    CheckMove (file, rank, file - 1, rank + 2);
		if (file - 2 >= 1 && rank - 1 >= 1)
		    CheckMove (file, rank, file - 2, rank - 1);
		if (file - 1 >= 1 && rank - 2 >= 1)
		    CheckMove (file, rank, file - 1, rank - 2);
		if (file + 2 <= 8 && rank - 1 >= 1)
		    CheckMove (file, rank, file + 2, rank - 1);
		if (file + 1 <= 8 && rank - 2 >= 1)
		    CheckMove (file, rank, file + 1, rank - 2);
	    }
	if (piece == 1)
	    {
		if (WTM)
		    {
			if (file < 8 && AT[file + 1][rank + 1] < 0 && AT[file + 1][rank + 1] != -6)
			    LegalWhite (file, rank, file + 1, rank + 1, -AT[file + 1][rank + 1]);
			if (file > 1 && AT[file - 1][rank + 1] < 0 && AT[file - 1][rank + 1] != -6)
			    LegalWhite (file, rank, file - 1, rank + 1, -AT[file - 1][rank + 1]);
			if (AT[file][rank + 1] == 0)
			    LegalWhite (file, rank, file, rank + 1, 0);
			if (rank == 2 && AT[file][3] == 0 && AT[file][4] == 0)
			    LegalWhite (file, 2, file, 4, 0);
		    }
		else
		    {
			if (file < 8 && AT[file + 1][rank - 1] > 0 && AT[file + 1][rank - 1] != 6)
			    LegalBlack (file, rank, file + 1,rank - 1, AT[file + 1][rank - 1]);
			if (file > 1 && AT[file - 1][rank - 1] > 0 && AT[file - 1][rank - 1] != 6)
			    LegalBlack (file, rank, file - 1,rank - 1, AT[file - 1][rank - 1]);
			if (AT[file][rank - 1] == 0)
			    LegalBlack (file, rank, file, rank - 1, 0);
			if (rank == 7 && AT[file][6] == 0 && AT[file][5] == 0)
			    LegalBlack (file, 7, file, 5, 0);
		    }
	    }
    }

    public void Handle_Chess960_White_Castle ()
    {
	if (WhiteOOO && AT[White_QR_file][1] == 4 &&
	    AT[Chess960_WK_File][1] == 6)
	    {
		boolean b = true;
		for (int i = White_QR_file + 1; i <= 4; i++)
		    {
			if (i == Chess960_WK_File)
			    continue;
			if (AT[i][1] != 0)
			    b = false;
		    }
		for (int i = White_QR_file - 1; i >= 4; i--)
		    {
			if (i == Chess960_WK_File)
			    continue;
			if (AT[i][1] != 0)
			    b = false;
		    }
		for (int i = Chess960_WK_File + 1; i <= 3; i++)
		    {
			if (i == White_QR_file)
			    continue;
			if (AT[i][1] != 0)
			    b = false;
		    }
		for (int i = Chess960_WK_File - 1; i >= 3; i--)
		    {
			if (i == White_QR_file)
			    continue;
			if (AT[i][1] != 0)
			    b = false;
		    }
		if (b)
		    LegalWhite (Chess960_WK_File, 1, White_QR_file, 1, 7);
	    }	    
	if (WhiteOO && AT[White_KR_file][1] == 4 &&
	    AT[Chess960_WK_File][1] == 6)
	    {
		boolean b = true;
		for (int i = White_KR_file + 1; i <= 6; i++)
		    {
			if (i == Chess960_WK_File)
			    continue;
			if (AT[i][1] != 0)
			    b = false;
		    }
		for (int i = White_KR_file - 1; i >= 6; i--)
		    {
			if (i == Chess960_WK_File)
			    continue;
			if (AT[i][1] != 0)
			    b = false;
		    }
		for (int i = Chess960_WK_File + 1; i <= 7; i++)
		    {
			if (i == White_KR_file)
			    continue;
			if (AT[i][1] != 0)
			    b = false;
		    }
		if (b)
		    LegalWhite (Chess960_WK_File, 1, White_KR_file, 1, 6);
	    }	    
    }

    public void Handle_Chess960_Black_Castle ()
    {
	if (BlackOOO && AT[Black_QR_file][8] == -4 &&
	    AT[Chess960_BK_File][8] == -6)
	    {
		boolean b = true;
		for (int i = Black_QR_file + 1; i <= 4; i++)
		    {
			if (i == Chess960_BK_File)
			    continue;
			if (AT[i][8] != 0)
			    b = false;
		    }
		for (int i = Black_QR_file - 1; i >= 4; i--)
		    {
			if (i == Chess960_BK_File)
			    continue;
			if (AT[i][8] != 0)
			    b = false;
		    }
		for (int i = Chess960_BK_File + 1; i <= 3; i++)
		    {
			if (i == Black_QR_file)
			    continue;
			if (AT[i][8] != 0)
			    b = false;
		    }
		for (int i = Chess960_BK_File - 1; i >= 3; i--)
		    {
			if (i == Black_QR_file)
			    continue;
			if (AT[i][8] != 0)
			    b = false;
		    }
		if (b)
		    LegalBlack (Chess960_BK_File, 1, Black_QR_file, 1, 7);
	    }	    
	if (BlackOO && AT[Black_KR_file][8] == -4 &&
	    AT[Chess960_BK_File][8] == -6)
	    {
		boolean b = true;
		for (int i = Black_KR_file + 1; i <= 6; i++)
		    {
			if (i == Chess960_BK_File)
			    continue;
			if (AT[i][8] != 0)
			    b = false;
		    }
		for (int i = Black_KR_file - 1; i >= 6; i--)
		    {
			if (i == Chess960_BK_File)
			    continue;
			if (AT[i][8] != 0)
			    b = false;
		    }
		for (int i = Chess960_BK_File + 1; i <= 7; i++)
		    {
			if (i == Black_KR_file)
			    continue;
			if (AT[i][8] != 0)
			    b = false;
		    }
		if (b)
		    LegalBlack (Chess960_BK_File, 1, Black_KR_file, 1, 6);
	    }	    
    }

    public void GenerateAllMoves ()
    {
	int rank, file;
	if (EnPassant != 0)
	    {
		if (WTM)
		    {
			if (EnPassant < 8 && AT[EnPassant + 1][5] == 1)
			    LegalWhite (EnPassant + 1, 5, EnPassant, 6, 8);
			if (EnPassant > 1 && AT[EnPassant - 1][5] == 1)
			    LegalWhite (EnPassant - 1, 5, EnPassant, 6, 8);
		    }
		else
		    {
			if (EnPassant < 8 && AT[EnPassant + 1][4] == -1)
			    LegalBlack (EnPassant + 1, 4, EnPassant, 3, 8);
			if (EnPassant > 1 && AT[EnPassant - 1][4] == -1)
			    LegalBlack (EnPassant - 1, 4, EnPassant, 3, 8);
		    }
	    }
	if (Chess960 && WTM)
	    Handle_Chess960_White_Castle ();
	if (Chess960 && !WTM)
	    Handle_Chess960_Black_Castle ();
	if (!Chess960)
	    {
		if (WTM)
		    {
			if (WhiteOOO && AT[1][1] == 4 && AT[5][1] == 6 &&
			    AT[2][1] == 0 && AT[3][1] == 0 && AT[4][1] == 0)
			    LegalWhite (5, 1, 3, 1, 7);
			if (WhiteOO && AT[5][1] == 6 && AT[6][1] == 0 &&
			    AT[7][1] == 0 && AT[8][1] == 4)
			    LegalWhite (5, 1, 7, 1, 6);
		    }
		else
		    {
			if (BlackOOO && AT[1][8] == -4 && AT[5][8] == -6 &&
			    AT[2][8] == 0 && AT[3][8] == 0 && AT[4][8] == 0)
			    LegalBlack (5, 8, 3, 8, 7);
			if (BlackOO && AT[5][8] == -6 && AT[6][8] == 0 &&
			    AT[7][8] == 0 && AT[8][8] == -4)
			    LegalBlack (5, 8, 7, 8, 6);
		    }
	    }
	if (WTM)
	    {
		for (rank = 1; rank <= 8; rank++)
		    for (file = 1; file <= 8; file++)
			if (AT[file][rank] > 0)
			    GenerateMoves (file, rank);
	    }
	else
	    {
		for (rank = 1; rank <= 8; rank++)
		    for (file = 1; file <= 8; file++)
			if (AT[file][rank] < 0)
			    GenerateMoves(file, rank);
	    }
	MakeNotation();
    }

    public void MakeNormal ()
    {
	for (int i = 0; i < COUNT_OF_LEGAL_MOVES; i++)
	    {
		move_list[i] = 0;
		move_list_annotated[i] = new String ("");
	    }
	for (int rank = 1; rank <= 8; rank++)
	    for (int file = 1; file <= 8; file++)
		{
		    if (AT[file][rank] == 6)
			{
			    WHITE_KingFile = file;
			    WHITE_KingRank = rank;
			}
		    if (AT[file][rank] == -6)
			{
			    BLACK_KingFile = file;
			    BLACK_KingRank = rank;
			}
		}
	if (Chess960)
	    {
		if (AT[Chess960_WK_File][1] != 6)
		    WhiteOOO = WhiteOO = false;
		if (AT[White_KR_file][1] != 4)
		    WhiteOO = false;
		if (AT[White_QR_file][1] != 4)
		    WhiteOOO = false;
		if (AT[Chess960_BK_File][8] != -6)
		    BlackOOO = BlackOO = false;
		if (AT[Black_KR_file][8] != -4)
		    BlackOO = false;
		if (AT[Black_QR_file][8] != -4)
		    BlackOOO = false;
	    }
	else
	    {
		if (AT[5][1] != 6)
		    WhiteOOO = WhiteOO = false;
		if (AT[1][1] != 4)
		    WhiteOOO = false;
		if (AT[8][1] != 4)
		    WhiteOO = false;
		if (AT[5][8] != -6)
		    BlackOOO = BlackOO = false;
		if (AT[1][8] != -4)
		    BlackOOO = false;
		if (AT[8][8] != -4)
		    BlackOO = false;
	    }
	if (EnPassant != 0)
	    {
		if (WTM && AT[EnPassant][5] != -1)
		    EnPassant = 0;
		else if (!WTM && AT[EnPassant][4] != 1)
		    EnPassant = 0;
	    }
	if (EnPassant != 0)
	    {
		if (WTM && AT[EnPassant + 1][5] != 1 && AT[EnPassant - 1][5] !=
 1)
		    EnPassant = 0;
		else if (!WTM && AT[EnPassant + 1][4] != -1 && AT[EnPassant - 1][4] != -1)
		    EnPassant = 0;
	    }
	COUNT_OF_LEGAL_MOVES = 0;
	GenerateAllMoves ();
	ComplyLastMove ();
    }

    public void MakeNotation ()
    { 
	int move, check, prom, type, piece;
	boolean cap;
	String S;
	for (int i = 0; i < COUNT_OF_LEGAL_MOVES; i++)
	    {
		move = move_list[i];
		type = (move >> 12) & 0xf;
		check = (move >> 16) & 1;
		prom = (move >> 17) & 7;
		S = "";
		if (type == 6)
		    S = "O-O";
		else if (type == 7)
		    S = "O-O-O";
		else
		    {
			int from_file = 1 + (move & 7),
			    from_rank = 1 + ((move >> 3) & 7),
			    to_file = 1 + ((move >> 6) & 7),
			    to_rank = 1 + ((move >> 9) & 7);
			cap = ((type >= 1 && type <= 5) || (type == 8));
			piece = AT[from_file][from_rank];
			if (piece == 1 || piece == -1)
			    {
				if (cap)
				    S = "" + (char) (from_file + 'a' - 1) + "x";
				S += "" + (char) (to_file + 'a' - 1) + "" + to_rank;
				if (prom >= 2)
				    S += "=" + CF.PIECE_STRING.substring (6 - prom, 7 - prom);
			    }
			else if (piece == 6 || piece == -6)
			    {
				S = CF.PIECE_STRING.substring (0, 1);
				if (cap)
				    S += "x";
				S += (char) (to_file + 'a' - 1) + "" + to_rank;
			    }
			else
			    {
				if (piece == 5 || piece == -5)
				    S = CF.PIECE_STRING.substring (1, 2);
				if (piece == 4 || piece == -4)
				    S = CF.PIECE_STRING.substring (2, 3);
				if (piece == 3 || piece == -3)
				    S = CF.PIECE_STRING.substring (3, 4);
				if (piece == 2 || piece == -2)
				    S = CF.PIECE_STRING.substring (4, 5);
				int u = (move_list[i] >> 6) & 63;
				boolean same_file = false, same_rank = false, yes = false;
				for (int j = 0; j < COUNT_OF_LEGAL_MOVES && (!same_rank || !same_file);j++)
				    {
					if (j == i)
					    continue;
					int v = move_list[j];
					if (((v >> 6) & 63) == u && AT[1 + (v & 7)][1 + ((v >> 3) & 7)] == piece)
					    {
						yes = true;
						if ((1 + (v & 7)) == from_file)
						    same_file = true;
						if ((1 + ((v >> 3) & 7)) == from_rank)
						    same_rank = true;
					    }
				    }
				if (yes)
				    {
					if (same_rank || !same_file)
					    S += (char) (from_file + 'a' - 1);
					if (same_file)
					    S += from_rank;
				    }
				if (cap)
				    S += "x";
				S += (char) (to_file + 'a' - 1) + "" + to_rank;
			    }
		    }
		if (check == 1)
		    S = S + "+";
		move_list_annotated[i] = new String (S);
	    }
    }

    public boolean IsOK ()
    {
	int[] COUNT = new int[13];
	int file, rank;
	for (int i = 0; i < 13; i++)
	    COUNT[i] = 0;
	for (rank = 1; rank <= 8; rank++)
	    for (file = 1; file <= 8; file++)
		COUNT[AT[file][rank] + 6]++;
	if (COUNT[5] > 8 || COUNT[7] > 8 || COUNT[0] != 1 || COUNT[12] != 1)
	    return false;
	if (COUNT[1] > 10 || COUNT[11] > 10 || COUNT[2] > 10 || COUNT[10] > 9)
	    return false;
	if (COUNT[3] > 9 || COUNT[9] > 9 || COUNT[4] > 10 || COUNT[8] > 10)
	    return false;
	for (file = 1; file <= 8; file++)
	    if (AT[file][1] == 1 || AT[file][1] == -1 ||
		AT[file][8] == 1 || AT[file][8] == -1)
		return false;
	if (WTM && AttacksWhite (BLACK_KingFile, BLACK_KingRank))
	    return false;
        if (!WTM && AttacksBlack (WHITE_KingFile, WHITE_KingRank))
	    return false;
	return true;
    }

    public int NotateComparison (String S)
    {
	for (int i = 0; i < COUNT_OF_LEGAL_MOVES; i++)
	    if (S.equals (move_list_annotated[i]))
		return i;
	for (int i = 0; i < COUNT_OF_LEGAL_MOVES; i++)
	    {
		int w = FindMove(S);
		if (w != -1)
		    return w;
	    }
	for (int i = 0; i < COUNT_OF_LEGAL_MOVES; i++)
	    {
		int w = FindMove (S.substring (1));
		if (w != -1)
		    return w;
	    }
	
	return -1;
    }

    public void Chess960_OO ()
    {
	if (WTM)
	    {
		WhiteOO = WhiteOOO = false;
		AT[WHITE_KingFile][1] = AT[White_KR_file][1] = 0;
		AT[6][1] = 4;
		AT[7][1] = 6;
		WHITE_KingFile = 7;
		EnPassant = 0;
	    }
	else
	    {
		BlackOO = BlackOOO = false;
		AT[BLACK_KingFile][8] = AT[Black_KR_file][8] = 0;
		AT[6][8] = -4;
		AT[7][8] = -6;
		BLACK_KingFile = 7;
		EnPassant = 0;
	    }
    }

    public void Chess960_Un_OO ()
    {
	if (WTM)
	    {
		WHITE_KingFile = Chess960_WK_File;
		AT[6][1] = AT[7][1] = 0;
		AT[Chess960_WK_File][1] = 6;
		AT[White_KR_file][1] = 4;
	    }
	else
	    {
		BLACK_KingFile = Chess960_BK_File;
		AT[6][8] = AT[7][8] = 0;
		AT[Chess960_BK_File][8] = -6;
		AT[Black_KR_file][8] = -4;
	    }
    }

    public void CastleOO ()
    {
	if (Chess960)
	    {
		Chess960_OO ();
		return;
	    }
	if (WTM)
	    {
		WhiteOO = WhiteOOO = false;
		WHITE_KingFile = 7;
		AT[5][1] = 0;
		AT[6][1] = 4;
		AT[7][1] = 6;
		AT[8][1] = 0;
		EnPassant = 0;
	    }
	else
	    {
		BlackOO = BlackOOO = false;
		BLACK_KingFile = 7;
		AT[5][8] = 0;
		AT[6][8] = -4;
		AT[7][8] = -6;
		AT[8][8] = 0;
		EnPassant = 0;
	    }
    }

    public void UnCastleOO ()
    {
	if (Chess960)
	    {
		Chess960_Un_OO ();
		return;
	    }
	if (WTM)
	    {
		WHITE_KingFile = 5;
		AT[5][1] = 6;
		AT[6][1] = 0;
		AT[7][1] = 0;
		AT[8][1] = 4;
	    }
	else
	    {
		BLACK_KingFile = 5;
		AT[5][8] = -6;
		AT[6][8] = 0;
		AT[7][8] = 0;
		AT[8][8] = -4;
	    }
    }

    public void Chess960_OOO ()
    {
	if (WTM)
	    {
		WhiteOO = WhiteOOO = false;
		AT[WHITE_KingFile][1] = AT[White_QR_file][1] = 0;
		AT[4][1] = 4;
		AT[3][1] = 6;
		WHITE_KingFile = 3;
		EnPassant = 0;
	    }
	else
	    {
		BlackOO = BlackOOO = false;
		AT[BLACK_KingFile][8] = AT[Black_QR_file][8] = 0;
		AT[4][8] = -4;
		AT[3][8] = -6;
		BLACK_KingFile = 3;
		EnPassant = 0;
	    }
    }

    public void Chess960_Un_OOO ()
    {
	if (WTM)
	    {
		WHITE_KingFile = Chess960_WK_File;
		AT[3][1] = AT[4][1] = 0;
		AT[Chess960_WK_File][1] = 6;
		AT[White_QR_file][1] = 4;
	    }
	else
	    {
		BLACK_KingFile = Chess960_BK_File;
		AT[3][8] = AT[4][8] = 0;
		AT[Chess960_BK_File][8] = -6;
		AT[Black_QR_file][8] = -4;
	    }
    }

    public void CastleOOO ()
    {
	if (Chess960)
	    {
		Chess960_OOO ();
		return;
	    }
	if (WTM)
	    {
		WhiteOO = WhiteOOO = false;
		WHITE_KingFile = 3;
		AT[5][1] = 0;
		AT[4][1] = 4;
		AT[3][1] = 6;
		AT[1][1] = 0;
		EnPassant = 0;
	    }
	else
	    {
		BlackOO = BlackOOO = false;
		BLACK_KingFile = 3;
		AT[5][8] = 0;
		AT[4][8] = -4;
		AT[3][8] = -6;
		AT[1][8] = 0;
		EnPassant = 0;
	    }
    }

    public void UnCastleOOO ()
    {
	if (Chess960)
	    {
		Chess960_Un_OOO ();
		return;
	    }
	if (WTM)
	    {
		WHITE_KingFile = 5;
		AT[5][1] = 6;
		AT[4][1] = 0;
		AT[3][1] = 0;
		AT[1][1] = 4;
	    }
	else
	    {
		BLACK_KingFile = 5;
		AT[5][8] = -6;
		AT[4][8] = 0;
		AT[3][8] = 0;
		AT[1][8] = -4;
	    }
    }

    public void MakeEP (int from_file, int to_file)
    {
	if (WTM)
	    {
		AT[from_file][5] = 0;
		AT[to_file][6] = 1;
		AT[to_file][5] = 0;
	    }
	else
	    {
		AT[from_file][4] = 0;
		AT[to_file][3] = -1;
		AT[to_file][4] = 0;
	    }
    }

    public String GetDirect (int MOVE)
    {
        int mv = MOVE & 0xffff;
        int prom = (MOVE >> 17) & 0x7;
        int from_file = 1 + (mv & 7), from_rank = 1 + ((mv >> 3) & 7),
            to_file = 1 + ((mv >> 6) & 7), to_rank = 1 + ((mv >> 9) & 7);
        String direct = new String ("");
        direct += (char) ('a' + (char) (from_file - 1));
        direct += (char) ('1' + (char) (from_rank - 1));
        direct += (char) ('a' + (char) (to_file - 1));
        direct += (char) ('1' + (char) (to_rank - 1));
        if (prom > 0)
            {
                if (prom == 2)
                    direct += 'n';
                if (prom == 3)
                    direct += 'b';
                if (prom == 4)
                    direct += 'r';
                if (prom == 5)
                    direct += 'q';
            }
	return direct;
    }

    public void MakeMove (int w)
    {
	MakeMove32 (move_list[w], move_list_annotated[w]);
    }

    public void DisAttendMove32 (int mv)
    {
	UnMakeMove32 (mv);
	MOVE_TREE.NOW = MOVE_TREE.NOW.MainLineParent;
	MakeNormal ();
    }

    public void ComplyLastMove ()
    {
	if (MOVE_TREE != null && MOVE_TREE.NOW.MainLineParent != null)
	    {
		int c = MOVE_TREE.NOW.MainLineParent.mainline.move;
		LAST_FROM_x = 1 + (c & 07);
		LAST_FROM_y = 1 + ((c >> 3) & 07);
		LAST_TO_x = 1 + ((c >> 6) & 07);
		LAST_TO_y = 1 + ((c >> 9) & 07);
	    }
	else
	    LAST_FROM_x = LAST_FROM_y = LAST_TO_x = LAST_TO_y = 0;
    }

    public void UnMakeMove32 (int mv)
    {
	int move = mv & 07777;
	int from_file = 1 + (move & 7), from_rank = 1 + ((move >> 3) & 7),
	    to_file = 1 + ((move >> 6) & 7), to_rank = 1 + ((move >> 9) & 7);
	int piece = AT[to_file][to_rank];
	int type = (mv >> 12) & 0xf;
	int prom = (mv >> 17) & 7;
	int OoOoO = (mv >> 20) & 0xf;
	int ct = (mv >> 24) & 127;
	WTM = !WTM;
	if (type == 6)
	    UnCastleOO ();
	else if (type == 7)
	    UnCastleOOO ();
	else
	    {
		AT[from_file][from_rank] = piece;
		if (prom != 0)
		    AT[from_file][from_rank] = WTM ? 1 : -1;
		AT[to_file][to_rank] = 0;
		if (type >= 1 && type <= 5)
		    AT[to_file][to_rank] = WTM ? -type : type;
		if (type == 8)
		    {
			if (WTM)
			    AT[to_file][to_rank - 1] = -1;
			else
			    AT[to_file][to_rank + 1] = 1;
		    }
		if (piece == 6)
		    {
			WHITE_KingFile = from_file;
			WHITE_KingRank = from_rank;
		    }
		if (piece == -6)
		    {
			BLACK_KingFile = from_file;
			BLACK_KingRank = from_rank;
		    }
	    }
	if (ct >= 110)
	    {
		EnPassant = ct - 110;
		ReversibleCount = 0;
	    }
	else
	    {
		EnPassant = 0;
		ReversibleCount = ct;
	    }
	WhiteOO = ((OoOoO & 0x1) == 1);
	WhiteOOO = ((OoOoO & 0x2) == 2);
	BlackOO = ((OoOoO & 0x4) == 4);
	BlackOOO = ((OoOoO & 0x8) == 8);
	if (!WTM)
	    MOVE_NUMBER--;
    }

    public void MakeMove32 (int mv, String S)
    {
	int move = mv & 0xffff;
	int prom = (mv >> 17) & 7;
	int from_file = 1 + (move & 7), from_rank = 1 + ((move >> 3) & 7),
	    to_file = 1 + ((move >> 6) & 7), to_rank = 1 + ((move >> 9) & 7);
	int piece = AT[from_file][from_rank];
	boolean oo = (((move >> 12) & 7) == 6);
	boolean ooo = (((move >> 12) & 7) == 7);
	boolean ep = (((move >> 12) & 8) == 8);
	int OoOoO = (WhiteOO ? 1 : 0) | (WhiteOOO ? 2 : 0) |
	            (BlackOO ? 4 : 0) | (BlackOOO ? 8 : 0);
	int ct = (EnPassant > 0) ? (EnPassant + 110) :
	                    ((ReversibleCount > 100) ? 100 : ReversibleCount);
	if (MOVE_TREE != null)
	    MOVE_TREE.NodeMove (mv | (ct << 24) | (OoOoO << 20), S);
	if (piece == 1 || piece == -1 || AT[to_file][to_rank] != 0)
	    ReversibleCount = 0;
	else
	    ReversibleCount++;
	if (oo)
	    CastleOO ();
	else if (ooo)
	    CastleOOO ();
	else
	    {
		if (ep)
		    {
			MakeEP (from_file, to_file);
			piece = WTM ? 1 : -1;
		    }
		else
		    {
			piece = AT[from_file][from_rank];
			AT[to_file][to_rank] = AT[from_file][from_rank];
			AT[from_file][from_rank] = 0;
		    }
		if (piece == 6)
		    {
			WhiteOO = false;
			WhiteOOO = false;
			WHITE_KingFile = to_file;
			WHITE_KingRank = to_rank;
		    }
		if (piece == -6)
		    {
			BlackOO = false;
			BlackOOO = false;
			BLACK_KingFile = to_file;
			BLACK_KingRank = to_rank;
		    }
		EnPassant = 0;
		if (!Chess960)
		    {
			if (from_file == 1 && from_rank == 1)
			    WhiteOOO = false;
			if (from_file == 8 && from_rank == 1)
			    WhiteOO = false;
			if (from_file == 1 && from_rank == 8)
			    BlackOOO = false;
			if (from_file == 8 && from_rank == 8)
			    BlackOO = false;
			if (to_file == 1 && to_rank == 1)
			    WhiteOOO = false;
			if (to_file == 8 && to_rank == 1)
			    WhiteOO = false;
			if (to_file == 1 && to_rank == 8)
			    BlackOOO = false;
			if (to_file == 8 && to_rank == 8)
			    BlackOO = false;
		    }
		else
		    {
			if (from_file == White_QR_file && from_rank == 1)
			    WhiteOOO = false;
			if (from_file == White_KR_file && from_rank == 1)
			    WhiteOO = false;
			if (from_file == Black_QR_file && from_rank == 8)
			    BlackOOO = false;
			if (from_file == Black_KR_file && from_rank == 8)
			    BlackOO = false;
			if (to_file == White_QR_file && to_rank == 1)
			    WhiteOOO = false;
			if (to_file == White_KR_file && to_rank == 1)
			    WhiteOO = false;
			if (to_file == Black_QR_file && to_rank == 8)
			    BlackOOO = false;
			if (to_file == Black_KR_file && to_rank == 8)
			    BlackOO = false;
		    }
		if (prom > 0)
		    {
			if (WTM)
			    AT[to_file][to_rank] = prom;
			else
			    AT[to_file][to_rank] = -prom;
		    }
		if (piece == 1 && to_rank == 4 && from_rank == 2)
		    {
			if (from_file > 1 && AT[from_file -1][4] == -1)
			    EnPassant = from_file;
			if (from_file < 8 && AT[from_file + 1][4] == -1)
			    EnPassant = from_file;
		    }
		if (piece == -1 && to_rank == 5 && from_rank == 7)
		    {
			if (from_file > 1 && AT[from_file - 1][5] == 1)
			    EnPassant = from_file;
			if (from_file < 8 && AT[from_file + 1][5] == 1)
			    EnPassant = from_file;
		    }
	    }
	WTM = !WTM;
	if (WTM)
	    MOVE_NUMBER++;	
    }
 }
 
// 0-11 to-from, 12-15 type/ep, 16 is_check, 17-19 prom
// 20-23 prior castling, 24-30 prior count