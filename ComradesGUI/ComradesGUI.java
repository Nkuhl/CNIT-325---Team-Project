import java.applet.Applet;
import java.io.*;
import java.util.StringTokenizer;
import java.lang.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

public class ComradesGUI extends Applet
{
    String PATH;
    static ComradesFrame RF;
    int POLL_DELAY = 1; // 1 ms

    public void ProcessConfig (String STRING)
    {
	StringTokenizer ST = new StringTokenizer (STRING);
	if (!ST.hasMoreTokens ()) // HACK
	    return;
	String S = ST.nextToken ();
    }

    public void ReadConfigFile (File file)
    {
	BufferedReader BR;
	try
	    {
		FileReader FR = new FileReader (file);
		BR = new BufferedReader (FR);
		RF.TellInfo ("Reading ComradesGUI.config file");
		while (BR.ready ())
		    {
			ProcessConfig (BR.readLine ());
		    }
		    BR.close ();
	    }
	catch (IOException io_e)
	    {

	    }
    }

    public void LoadStartup ()
    {
	PATH = getClass ().getProtectionDomain ().getCodeSource ().
	    getLocation ().toString ();
	File file = new File ("ComradesGUI.config");
	if (!file.exists ())
	    RF.TellInfo ("No ComradesGUI.config file noticed");
	else
	    ReadConfigFile (file);
    }

    public void RunLoop ()
    {
        Timer SECOND = new Timer (250, new ActionListener () // HACK 0.25s
            {
                public void actionPerformed (ActionEvent evt)
                {
                    for (int i = 0; i < RF.instances; i++)
			{
			    RF.INSTANCES[i].IP.PERIODIC = true; // HACK
			    RF.INSTANCES[i].IP.repaint (); // queue
			}
                }
            }
            );
	SECOND.start (); // attends necessary

	Timer T = new Timer (POLL_DELAY, new ActionListener ()
	    {
		public void actionPerformed (ActionEvent evt)
		{
		    for (int i = 0; i < RF.instances; i++)
			RF.INSTANCES[i].InstanceThread ();		     
		    ((Timer) (evt.getSource ())).restart ();
		}
	    }
	    );
	T.start (); // nominal
    }

    public void MakeComradesFrame ()
    {
	RF = new ComradesFrame ();
    }

    /* Copyright (c) Igor Igorovich Igoronov (Muleteer) plus
                     Ivan Skavinsky Skavar (Brave Musketeer) plus
                     Decembrists (all), PUBLICDOMAIN (workers) */

    public static void LookAndFeel ()
    {
	try 
	    {
		UIManager.setLookAndFeel (UIManager.getCrossPlatformLookAndFeelClassName ()); // uniformed
	    }
	catch (Exception exc)
	    {
	    }
    }

    public static void main (String[] Arguments)
    {
	LookAndFeel (); // uniformed
	ComradesGUI RE = new ComradesGUI ();
	RE.MakeComradesFrame ();
	//	RE.LoadStartup (); // for the off
	RE.RunLoop ();
    }

}
