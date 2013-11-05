import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

public class Communicator extends CommIO
{
    String name;
    String path;
    String RunTimeOptions;
    String id;
    String[] options;
    int opt_count;
    String[] OPT_NAME;
    String[] OPT_VALUE;
    String[] OPT_TYPE;
    String[] SUB_CLASSES;
    int sub_class_count;
    boolean IS_NEW;
    boolean ICI = false;
    boolean Has_Chess_960 = false;
    boolean COMRADES_MONTE_CARLO = false;

    public Communicator (String NAME, ComradesFrame cf, boolean NEW)
    {
	super (cf);
	CF = cf;
	name = new String (NAME); // copy
	options = new String[1024]; // limit
	OPT_NAME = new String[1024];
	OPT_VALUE = new String[1024];
	OPT_TYPE = new String[1024];
	SUB_CLASSES = new String[16];
	opt_count = 0;
	sub_class_count = 0;
	IS_NEW = NEW;
	RunTimeOptions = "NULL";
	LOGGING = false; // HACK
    }

    public void OptionLine (String S)
    {
        options[opt_count++] = new String (S); // copy                
    }

    public void DoCanICI (String S)
    {
	if (S.equals ("MonteCarlo"))
	    COMRADES_MONTE_CARLO = true;
    }

    public void SetUpICI ()
    {
	ICI = true;
        while (true)
            {
                String S = DemandLine ();
                if (IS_NEW && S.startsWith ("id "))
                    id = new String (S.substring (3));
                if (S.startsWith ("opt "))
                    options[opt_count++] = new String (S.substring (4));
                if (S.startsWith ("sc "))
                    SUB_CLASSES[sub_class_count++] = new String (S.substring (3));
		if (S.startsWith ("can "))
		    DoCanICI (S.substring (4));
                if (S.equals ("iciok"))
                    break;
                if (!IsReady ())
                    {
                        SleepFor (100);
                        if (!IsReady ())
                            {
				CF.TellInfo ("Communicator decayed?");
                                return;
                            }
                    }
            }
    }

    public void IdLine (String S)
    {
        if (IS_NEW && S.startsWith ("id name "))
	    id = new String (S.substring (8));
    }

    public boolean TryUCI (Process process)
    {
	SendTo ("uci", true);
        int x = 0;
        while (true)
            {
                x++;
                SleepFor (25);
                if (IsReady ())
                    break;
                if (x == 10)
                    {
                        CF.TellInfo ("No communications for " + path);
                        return false;
                    }
            }
        while (true)
            {
                String S = DemandLine ();
                if (S.startsWith ("id"))
                    IdLine (S);
                if (S.startsWith ("option"))
                    OptionLine (S);
                if (S.equals ("uciok"))
                    break;
                if (!IsReady ())
                    {
                        SleepFor (100);
                        if (!IsReady ())
                            {
				CF.TellInfo ("Communicator decayed?");
				SendTo ("quit", true);
				SleepFor (100);
                                process.destroy ();
                                return false;
                            }
                    }
            }
	SendTo ("quit", true);
	SleepFor (100);
	process.destroy (); // HACK
        return true;
    }

    public boolean LoadCommunicator ()
    {
	opt_count = 0;
	sub_class_count = 0;
        Process exterior = null;
	try
            {
                exterior = Runtime.getRuntime ().exec (path);
            }
        catch (IOException io_exc)
            {
                CF.TellInfo ("Not found: " + path);
                return false;
            }
	    READER = new BufferedReader (new InputStreamReader (exterior.getInputStream ()));
	    WRITER = new PrintWriter (new OutputStreamWriter (exterior.getOutputStream ()));
        SleepFor (100);
        while (IsReady ())
	    DemandLine (); // HACK  
	SendTo ("ici", true);
	if (WaitForThroughPut ("ici-echo", 1000, false))
	    {
		SetUpICI ();
		SendTo ("quit", true);
		SleepFor (100);
		exterior.destroy ();
		return true;
	    }
	return TryUCI (exterior);
    }

    public void SaveCommunicator (JFrame OPTIONS_FRAME)
    {
        try
            {
                FileWriter FW = new FileWriter (id);
                BufferedWriter BW = new BufferedWriter (FW);
		PrintWriter PW = new PrintWriter (BW, true);
                PW.println ("id " + id);
		if (ICI) // additional
		    PW.println ("ici");
		if (COMRADES_MONTE_CARLO)
		    PW.println ("can MonteCarlo");
                PW.println ("Path " + path);
                PW.println ("RunTimeOptions " + RunTimeOptions);
                for (int i = 0; i < opt_count; i++)
                    PW.println (OPT_NAME[i] + " " + OPT_TYPE[i] + " " + OPT_VALUE[i]);
		PW.flush (); // ensure 
                PW.close ();
            }
        catch (IOException io_exc)
            {
                CF.TellInfo ("Bad try to write on disk, filename bad?");
                return;
            }
        OPTIONS_FRAME.setVisible (false);
        OPTIONS_FRAME.dispose (); // null
        if (IS_NEW)
            CF.AdditionalCommunicator (this);
        CF.SaveApplyStartUp ();
	IS_NEW = false;
    }

    public void DeleteCommunicator (JFrame OPTIONS_FRAME)
    {
        OPTIONS_FRAME.setVisible (false);
        OPTIONS_FRAME.dispose (); // null
	if (IS_NEW)
            CF.AdditionalCommunicator (this);
        CF.DeleteUnNeededCommunicator (this);
        CF.SaveApplyStartUp ();
    }

    public void ModifyDefaults ()
    {
	if (ICI)
	    new CommunicatorOptionsFrameICI (this);
	else
	    new CommunicatorOptionsFrameUCI (this); // HACK
    }

}
