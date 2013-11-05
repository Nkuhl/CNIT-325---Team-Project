package edu.purdue.comradesgui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class RobboInformatoryFrame extends JFrame
{
    CommunicatorInstance CI;
    Object[][] DATA;

    public RobboInformatoryFrame (CommunicatorInstance ci)
    {
	super ();
	CI = ci;
	JTable TABLE = MakeTable ();
	if (TABLE == null)
	    return;
	JScrollPane JSP = new JScrollPane (TABLE);
        JSP.setPreferredSize (new Dimension (400, 400));
        add (JSP); // this
	setBackground (Color.lightGray);
	setSize (400, 400);
	setVisible (true);
	pack ();
    }

    public JTable MakeTable ()
    {
	CI.SendTo ("isready", true);
	while (!CI.IsReady ())
	    CI.SleepFor (10);
	String S = CI.DemandLine ();
	if (!S.equals ("RobboInformatory"))
	    return null;
	String[] STR = new String[3000]; // HACK
	int i = 0;
	while (true)
	    {
		STR[i] = new String (CI.DemandLine ());
		if (STR[i].equals ("DesistRobboInformatory"))
		    break;
		i++;
	    }
	JTable TABLE = new JTable (new TableModel (STR, i));
	TABLE.setAutoCreateRowSorter (true); // valid ?
	if (TABLE.getRowCount () > 0)
	    {
		TABLE.getRowSorter().toggleSortOrder (3);
		TABLE.getRowSorter().toggleSortOrder (3); // in twice (3)
		TABLE.getRowSorter().toggleSortOrder (2);
		TABLE.getRowSorter().toggleSortOrder (2); // in twice (2)
	    }
	CI.WaitForThroughPut ("readyok", -1, false); // HACK
	return TABLE;
    }

    public class TableModel extends AbstractTableModel
    {
        String[] NAMES = { "Direct", "Name", "Weak", "Hit" };
        int row_count;
	public TableModel (String[] S, int n)
	{
	    row_count = n;
	    DATA = new Object[n][5];
	    for (int i = 0; i < n; i++)
		{
		    StringTokenizer ST = new StringTokenizer (S[i]);
		    DATA[i][1] = new String (ST.nextToken ());
		    DATA[i][0] = new Boolean (ST.nextToken ());
		    DATA[i][2] = new Long (ST.nextToken ()); // Long
		    DATA[i][3] = new Long (ST.nextToken ());	
		    DATA[i][4] = new Boolean (ST.nextToken ()); // HACK
		}
	}

        public Object getValueAt (int row, int col) { return DATA[row][col]; }
        public String getColumnName (int col) { return NAMES[col]; }
        public int getColumnCount() { return NAMES.length; }
        public int getRowCount() { return row_count; }
        public Class getColumnClass (int c)
	{
	    if (row_count == 0)
		return new String ("").getClass (); // HACK
	    return getValueAt(0, c).getClass ();
	}
        public boolean isCellEditable (int row, int col)
	{ return (col == 0) && (Boolean) (DATA[row][4]); } // ensure [4]
        public void setValueAt (Object obj, int row, int col)
	{
	    DATA[row][col] = obj;
	    Boolean b = (Boolean) obj;
	    if (b)
		CI.SendTo ("setoption name RobboTripleBulkLoadThisName value " + DATA[row][1], true);
	    else
		CI.SendTo ("setoption name RobboTripleBulkDetachThisName value " + DATA[row][1], true);
	    CI.ReadyOK (false);
	    fireTableDataChanged ();
	}
    }

}