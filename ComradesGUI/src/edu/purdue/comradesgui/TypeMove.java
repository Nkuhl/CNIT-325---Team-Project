package edu.purdue.comradesgui;

import java.lang.*;
import javax.swing.*;

public class TypeMove
{
    int move;
    String plain, fancy;
    
    public TypeMove (int m, String S)
    {
	fancy = S; // valid ?
	move = m;
	plain = GetDirect (m);
    }
    
    public TypeMove () // empty
    {
	move = 0;
	plain = "";
	fancy = "ROOT";
    }
    
    public String toString ()
    {
	return fancy; // HACK
    }

    public String GetDirect (int MOVE)
    {
        int mv = MOVE & 0xffff;
        int prom = (MOVE >> 17) & 7;
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
}
