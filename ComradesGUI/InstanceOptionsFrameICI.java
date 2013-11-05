import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

public class InstanceOptionsFrameICI extends InstanceOptionsFrame
{
    public InstanceOptionsFrameICI (CommunicatorInstance ci)
    {
	super (ci);
	DoInstanceOptions ();
    }

    public void AttendOptionsICI ()
    {
	sub_class_count = 0;
        opt_count = 0;
        if (CI.on)
            CI.DoHalt ();
        CI.SendTo ("ici", true);
        while (!CI.IsReady ())
            CI.SleepFor (10);
        while (true)
            {
                String S = CI.DemandLine ();
                if (S.startsWith ("id "))
                    continue;
                if (S.startsWith ("opt "))
		    options[opt_count++] = new String (S.substring (4));
		if (S.startsWith ("sc "))
		    SUB_CLASSES[sub_class_count++] = new String (S.substring (3));
                if (S.equals ("iciok"))
                    break;
                while (!CI.IsReady ())
                    CI.SleepFor (10);
            }
    }

////////////////////////////////////////

    public class FileButton extends JButton implements ActionListener
    {
        JTextField TEXT_FIELD;
        String DEFAULT;
        String NAME;
        boolean DIR;

        public FileButton (String d, String n, boolean dir)
        {
            super (d);
            DEFAULT = new String (d);
            NAME = new String (n);
            DIR = dir;
        }

        public void actionPerformed (ActionEvent act_evt) // JComboBox
        {
            JFileChooser JFC;
            if (act_evt.getActionCommand ().equals ("NULL"))
                {
                    ChangeValue (NAME, "NULL");
                    TEXT_FIELD.setText ("NULL");
                    TEXT_FIELD.repaint (); // ensure
                    return;
                }
            if (DEFAULT.equals ("NULL"))
                JFC = new JFileChooser (System.getProperty ("user.dir"));
            else
                JFC = new JFileChooser (new File (DEFAULT));
            if (DIR)
                JFC.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
            int Value =  JFC.showOpenDialog (JFC);
            if (Value != JFileChooser.APPROVE_OPTION)
                return;
            File FILE = JFC.getSelectedFile ();
            if (!FILE.exists())
                {
                    CI.CF.TellInfo ("File does not exist");
                    return;
                }
            ChangeValue (NAME, FILE.getAbsolutePath ());
            DEFAULT = new String (FILE.getAbsolutePath ());
            TEXT_FIELD.setText (FILE.getAbsolutePath ());
            TEXT_FIELD.repaint (); // ensure
        }

        public Box MakeBox ()
        {
            Box B = new Box (BoxLayout.X_AXIS);
            JLabel L = new JLabel (NAME + ":  ");
            L.setBackground (new Color (235, 225, 215));
            B.add (L);
            TEXT_FIELD = new JTextField (DEFAULT);
            TEXT_FIELD.setBackground (new Color (225, 215, 235));
            TEXT_FIELD.setPreferredSize (new Dimension (300, 16));
            TEXT_FIELD.setMaximumSize (new Dimension (300, 16));
            TEXT_FIELD.setName (NAME);
            TEXT_FIELD.setEditable (false);
            B.add (TEXT_FIELD);
            JButton X = new JButton ("Change");
            X.setActionCommand ("Change");
            X.addActionListener (this);
            X.setMargin (new Insets (1, 5, 1, 5)); // HACK
            B.add (X);
            JButton Y = new JButton ("NULL");
            Y.setActionCommand ("NULL");
            Y.addActionListener (this);
            Y.setMargin (new Insets (1, 5, 1, 5)); // HACK
            B.add (Y);
            B.setAlignmentX (0.0f);
            return B;
        }
    }

    public void FileBox (Box BOX, String NAME, int i, boolean dir)
    {
        String DEFAULT = new String (CI.OPT_VALUE[i]);
        FileButton FB = new FileButton (DEFAULT, NAME, dir);
        BOX.add (FB.MakeBox ());
    }

////////////////////////////////////////

    public class MultiButton extends JButton implements ActionListener
    {
	JTextField TEXT_FIELD;
	String DEFAULT;
	String NAME;
	Box BOX;
	int LOC;
	
	public MultiButton (String d, String n, int loc)
	{
	    super (d);
	    DEFAULT = new String (d);
	    NAME = new String (n);
	    LOC = loc;
	}
	
	public void actionPerformed (ActionEvent act_evt) // JComboBox
	{
	    JFileChooser JFC;
	    if (act_evt.getActionCommand ().equals ("NULL"))
		{
		    ChangeMultiValue (NAME, "NULL", LOC);
		    TEXT_FIELD.setText ("NULL");
		    TEXT_FIELD.repaint (); // ensure
		    return;
		}
	    if (act_evt.getActionCommand ().equals ("ADD"))
		{
		    Box B = (Box) (BOX.getParent ()); // HACK
		    B.add (new MultiButton ("NULL", NAME, B.getComponentCount ()).MakeBox (false));
		    B.revalidate ();
		    B.repaint ();
		    ChangeMultiValue (NAME, "NULL", B.getComponentCount () - 1);
		    return;
		}
	    if (DEFAULT.equals ("NULL"))
		JFC = new JFileChooser (System.getProperty ("user.dir"));
	    else
		JFC = new JFileChooser (new File (DEFAULT));
	    JFC.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
	    int Value =  JFC.showOpenDialog (JFC);
	    if (Value != JFileChooser.APPROVE_OPTION)
		return;
	    File FILE = JFC.getSelectedFile ();
	    if (!FILE.exists())
		{
		    CI.CF.TellInfo ("File does not exist");
		    return;
		}
	    ChangeMultiValue (NAME, FILE.getAbsolutePath (), LOC);
	    DEFAULT = new String (FILE.getAbsolutePath ());
	    TEXT_FIELD.setText (FILE.getAbsolutePath ());
	    TEXT_FIELD.repaint (); // ensure
	}
	
	public Box MakeBox (boolean ADD)
	{
	    Box B = new Box (BoxLayout.X_AXIS);
	    JLabel L = new JLabel (NAME + ":  ");
	    L.setBackground (new Color (235, 225, 215));
	    B.add (L);
	    TEXT_FIELD = new JTextField (DEFAULT);
	    TEXT_FIELD.setBackground (new Color (225, 215, 235));
	    TEXT_FIELD.setPreferredSize (new Dimension (300, 16));
	    TEXT_FIELD.setMaximumSize (new Dimension (300, 16));
	    TEXT_FIELD.setName (NAME);
	    TEXT_FIELD.setEditable (false);
	    B.add (TEXT_FIELD);
	    JButton X = new JButton ("Change");
	    X.setActionCommand ("Change");
	    X.addActionListener (this);
	    X.setMargin (new Insets (1, 5, 1, 5)); // HACK
	    B.add (X);
	    JButton Y = new JButton ("NULL");
	    Y.setActionCommand ("NULL");
	    Y.addActionListener (this);
	    Y.setMargin (new Insets (1, 5, 1, 5)); // HACK
	    B.add (Y);
	    if (ADD)
		{
		    JButton Z = new JButton ("ADD");
		    Z.setActionCommand ("ADD");
		    Z.addActionListener (this);
		    Z.setMargin (new Insets (1, 5, 1, 5)); // HACK
		    B.add (Z);
		}
	    B.setAlignmentX (0.0f);
	    BOX = B;
	    return B;
	}
    }

    public void DirectoryMultiResetBox (Box BOX, String NAME, int i)
    {
        Box BY = new Box (BoxLayout.Y_AXIS);
        String DEFAULT;
	DEFAULT = new String ("NULL"); // reset
        StringTokenizer ST = new StringTokenizer (DEFAULT, "|"); // HACK
        int k = 0;
        MultiButton MB = new MultiButton (ST.nextToken (), NAME, k++);
        BY.add (MB.MakeBox (true));
        while (ST.hasMoreTokens ())
            {
                MB = new MultiButton (ST.nextToken (), NAME, k++);
                BY.add (MB.MakeBox (false));
            }
        BOX.add (BY);
    }

////////////////////////////////////////

    public void DoTabsICI (JTabbedPane TABBED, Box TOP_BOX)
    {
	TopOptions (TOP_BOX);
        for (int i = 0; i < opt_count; i++)
            {
                StringTokenizer ST = new StringTokenizer (options[i]);
                CI.OPT_NAME[i] = ST.nextToken (); // assert ?
                String SUB_CLASS = ST.nextToken ();
                String TYPE = ST.nextToken ();
                int w = TABBED.indexOfTab (SUB_CLASS);
                if (w == -1)
                    continue; // HACK
                JScrollPane JSP = (JScrollPane) (TABBED.getComponentAt (w));
                Box BOX = (Box) (JSP.getViewport ().getView ()); // HACK
                if (TYPE.equals ("button"))
                    ButtonBox (BOX, CI.OPT_NAME[i]);
                if (TYPE.equals ("check"))
                    CheckBox (BOX, CI.OPT_NAME[i], i);
		/*
                if (TYPE.equals ("combo"))
                    ComboBox (BOX, CI.OPT_NAME[i], ST, i);
		*/
                if (TYPE.equals ("combo"))
                    ComboBox (BOX, CI.OPT_NAME[i], options[i].substring (options[i].indexOf ("combo ")), i);
                if (TYPE.equals ("file"))
                    FileBox (BOX, CI.OPT_NAME[i], i, false);
                if (TYPE.equals ("directory"))
                    FileBox (BOX, CI.OPT_NAME[i], i, true);
                if (TYPE.equals ("directory-multi-reset"))
                    DirectoryMultiResetBox (BOX, CI.OPT_NAME[i], i);
                if (TYPE.equals ("string"))
                    StringBox (BOX, CI.OPT_NAME[i], i);
                if (TYPE.equals ("spin"))
                    SpinBox (BOX, CI.OPT_NAME[i], ST, i, false);
                if (TYPE.equals ("binary"))
                    SpinBox (BOX, CI.OPT_NAME[i], ST, i, true);
            }
    }

    public void AddOptions_ICI (JFrame OPT)
    {
        JTabbedPane TABBED = new JTabbedPane ();
	AttendOptionsICI (); // HACK
        COPY_NAME = new String[opt_count];
        COPY_TYPE = new String[opt_count];
        COPY_VALUE = new String[opt_count];
        for (int i = 0; i < opt_count; i++)
            {
                COPY_NAME[i] = new String (CI.OPT_NAME[i]);
                COPY_TYPE[i] = new String (CI.OPT_TYPE[i]);
                COPY_VALUE[i] = new String (CI.OPT_VALUE[i]);
            }
        Box[] BOXES = new Box[sub_class_count];
        for (int i = 0; i < sub_class_count; i++)
            {
                BOXES[i] = new Box (BoxLayout.Y_AXIS);
                JScrollPane JSP = new JScrollPane (BOXES[i]);
                JSP.getViewport ().add (BOXES[i]); // ensure
                TABBED.addTab (SUB_CLASSES[i], JSP);
            }
        Box TOP_BOX = new Box (BoxLayout.Y_AXIS);
        DoTabsICI (TABBED, TOP_BOX);
        Box OVER_BOX = new Box (BoxLayout.Y_AXIS);
        OVER_BOX.add (TOP_BOX);
        OVER_BOX.add (TABBED);
        OPT.add (OVER_BOX);
    }

    public void DoInstanceOptions ()
    {
        OPTIONS_FRAME = new JFrame ("Instance Options " + CI.COMM.id);
        OPTIONS_FRAME.addWindowListener
	    (new WindowAdapter()
		{
		    public void windowClosing (WindowEvent win_evt)
		    {
			CI.IOF_ICI = null;
			OPTIONS_FRAME = null;
			for (int i = 0; i < opt_count; i++)
			    CI.OPT_VALUE[i] = new String (COPY_VALUE[i]);
		    }
		}
		);

        AddOptions_ICI (OPTIONS_FRAME);
        OPTIONS_FRAME.setBackground (Color.lightGray);
        OPTIONS_FRAME.pack ();
        OPTIONS_FRAME.setSize (750, 550); // demand
        OPTIONS_FRAME.setResizable (false);
        OPTIONS_FRAME.setVisible (true);
    }
}
