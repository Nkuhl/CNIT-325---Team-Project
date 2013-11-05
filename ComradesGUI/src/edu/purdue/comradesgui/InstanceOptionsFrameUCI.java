package edu.purdue.comradesgui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

public class InstanceOptionsFrameUCI extends InstanceOptionsFrame
{
    public InstanceOptionsFrameUCI (CommunicatorInstance ci)
    {
	super (ci);
	DoInstanceOptions ();
    }

    public void AttendOptions_UCI ()
    {
        opt_count = 0;
        if (CI.on)
            CI.DoHalt ();
        CI.SendTo ("uci", true);
        while (!CI.IsReady ())
            CI.SleepFor (10);
        while (true)
            {
                String S = CI.DemandLine ();
                if (S.startsWith ("id"))
                    continue;
                if (S.startsWith ("option"))
		    options[opt_count++] = new String (S); // copy
                if (S.equals ("uciok"))
                    break;
                while (!CI.IsReady ())
                    CI.SleepFor (10);
            }
    }

    public void BoxFill_UCI (Box BOX)
    {
        AttendOptions_UCI (); // HACK
	TopOptions (BOX);
        COPY_NAME = new String[opt_count];
        COPY_TYPE = new String[opt_count];
        COPY_VALUE = new String[opt_count];
        for (int i = 0; i < opt_count; i++)
            {
                COPY_NAME[i] = new String (CI.OPT_NAME[i]);
                COPY_TYPE[i] = new String (CI.OPT_TYPE[i]);
                COPY_VALUE[i] = new String (CI.OPT_VALUE[i]);
            }
	for (int i = 0; i < opt_count; i++)
            {
                StringBuffer SB = new StringBuffer (options[i]);
                int n = SB.indexOf (" name ");
                int t = SB.indexOf (" type ");
                String NAME = SB.substring (n + 6, t);
                StringTokenizer ST = new StringTokenizer (SB.substring (t + 6));
                String TYPE = ST.nextToken ();
		if (TYPE.equals ("button"))
		    ButtonBox (BOX, NAME);
		if (TYPE.equals ("check"))
		    CheckBox (BOX, NAME, i);
		if (TYPE.equals ("combo"))
		    ComboBox (BOX, NAME, options[i].substring (options[i].indexOf ("combo ")), i);
		if (TYPE.equals ("string"))
		    StringBox (BOX, NAME, i);
		if (TYPE.equals ("spin"))
		    SpinBox (BOX, NAME, ST, i, false);
	    }
    }

    public void AddOptions_UCI (JFrame OPT)
    {
        Box BOX = new Box (BoxLayout.Y_AXIS);
        BoxFill_UCI (BOX);
        JScrollPane OPT_JSP = new JScrollPane (BOX);
        OPT_JSP.getViewport ().add (BOX);
        OPT.add (OPT_JSP);
    }

    public void DoInstanceOptions ()
    {
        OPTIONS_FRAME = new JFrame ("Instance Options " + CI.COMM.id);
        OPTIONS_FRAME.addWindowListener
	    (new WindowAdapter()
		{
		    public void windowClosing (WindowEvent win_evt)
		    {
			CI.IOF_UCI = null;
			OPTIONS_FRAME = null;
			for (int i = 0; i < opt_count; i++)
                            CI.OPT_VALUE[i] = new String (COPY_VALUE[i]);
		    }
		}
		);

        AddOptions_UCI (OPTIONS_FRAME);
        OPTIONS_FRAME.setBackground (Color.lightGray);
        OPTIONS_FRAME.pack ();
        OPTIONS_FRAME.setSize (750, 550); // demand
        OPTIONS_FRAME.setResizable (false);
        OPTIONS_FRAME.setVisible (true);
    }
}
