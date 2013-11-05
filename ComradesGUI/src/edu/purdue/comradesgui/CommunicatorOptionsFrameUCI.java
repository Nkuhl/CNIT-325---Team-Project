package edu.purdue.comradesgui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

public class CommunicatorOptionsFrameUCI implements ItemListener, ActionListener, ChangeListener, KeyListener
{
    JTextField PATH_AREA;
    JFrame OPTIONS_FRAME;
    Communicator COMM;

    public CommunicatorOptionsFrameUCI (Communicator comm)
    {
	COMM = comm;
	ModifyDefaults ();
    }

    public void IdLine (String S)
    {
        if (COMM.IS_NEW && S.startsWith ("id name"))
	    COMM.id = new String (S.substring (8));
    }

    public void ChangePath ()
    {
	JFileChooser JFC = new JFileChooser (System.getProperty ("user.dir")); // HACK
        int Value =  JFC.showOpenDialog (JFC);
        if (Value != JFileChooser.APPROVE_OPTION)
	    return;
        File FILE = JFC.getSelectedFile ();
	ChangeValue ("AbsolutePath", FILE.getAbsolutePath ());
    }

    public void ChangeValue (String NAME, String VALUE)
    {
	int i;
	if (NAME.equals ("AbsolutePath"))
	    {
		PATH_AREA.setText (VALUE);
		COMM.path = new String (VALUE);
		return;
	    }
	if (NAME.equals ("MenuName"))
	    {
		COMM.id = new String (VALUE);
		return;
	    }
	if (NAME.equals ("RunTimeOptions"))
	    {
		COMM.RunTimeOptions = new String (VALUE);
		return;
	    }
	for (i = 0; i < COMM.opt_count; i++)
	    if (NAME.equals (COMM.OPT_NAME[i]))
		break;
	if (i >= COMM.opt_count)
	    return;
	COMM.OPT_VALUE[i] = new String (VALUE);
    }

    public void itemStateChanged (ItemEvent item_evt)
    {
	JCheckBox J = (JCheckBox) (item_evt.getItemSelectable ());
	ChangeValue (J.getText (), new String ("" + J.isSelected ()));
    }

    public void actionPerformed (ActionEvent act_evt) // JComboBox
    {
	String S = act_evt.getSource ().getClass ().getName ();
	if (S.equals ("javax.swing.JButton"))
	    {
		JButton B = (JButton) (act_evt.getSource ()); // incur ?
		if (act_evt.getActionCommand () == "ChangePath")
		    ChangePath ();
		if (act_evt.getActionCommand () == "SAVE")
		    COMM.SaveCommunicator (OPTIONS_FRAME);
		if (act_evt.getActionCommand () == "DELETE")
		    COMM.DeleteCommunicator (OPTIONS_FRAME);
	    }
	if (S.equals ("javax.swing.JComboBox"))
	    {
		JComboBox B = (JComboBox) (act_evt.getSource ());
		ChangeValue (act_evt.getActionCommand (), (String) (B.getSelectedItem ()));
	    }
    }

    public void keyTyped (KeyEvent key_evt) { }
    public void keyPressed (KeyEvent key_evt) { }
    public void keyReleased (KeyEvent key_evt) // unto "Released" (order)
    {
	JTextField J = (JTextField) (key_evt.getSource ());
	ChangeValue (J.getName (), J.getText ());
    }

    public void stateChanged (ChangeEvent chg_evt)
    {
	JSpinner J = (JSpinner) (chg_evt.getSource ());
	ChangeValue (J.getName (), J.getValue ().toString ());
    }

    public void BoxFill_UCI (Box BOX) // pained for parsage (UCI)
    {
	{
	    Box B = new Box (BoxLayout.X_AXIS);
	    JButton B1 = new JButton ("Save as Default");
	    B1.setActionCommand ("SAVE");
	    B1.addActionListener (this);
	    B1.setFont (B1.getFont ().deriveFont (20.0f));
	    B1.setAlignmentX (0.0f);
	    B.add (B1);
	    JLabel L = new JLabel ("                    ");
	    B.add (L);
	    JButton B2 = new JButton ("Delete Communicator");
	    B2.setActionCommand ("DELETE");
	    B2.addActionListener (this);
	    B2.setFont (B2.getFont ().deriveFont (16.0f));
	    B2.setAlignmentX (1.0f);
	    B.add (B2);
	    B.setAlignmentX (0.0f);
	    BOX.add (B);
	}

	{
	    Box B = new Box (BoxLayout.X_AXIS);
	    JLabel J = new JLabel ("MenuName:  ");
	    B.add (J);
	    JTextField F = new JTextField (COMM.id);
	    F.setBackground (new Color (225, 215, 235));
	    F.setPreferredSize (new Dimension (250, 16));
	    F.setMaximumSize (new Dimension (250, 16));
	    F.setName ("MenuName");
	    F.addKeyListener (this);
	    B.add (F);
	    B.setAlignmentX (0.0f);
	    BOX.add (B);
	}

	{
	    Box B = new Box (BoxLayout.X_AXIS);
	    JLabel J = new JLabel ("Path: ");
	    B.add (J);
	    PATH_AREA = new JTextField (COMM.path);
	    B.add (PATH_AREA);
	    PATH_AREA.setBackground (new Color (225, 215, 235));
	    PATH_AREA.setPreferredSize (new Dimension (400, 16));
	    PATH_AREA.setMaximumSize (new Dimension (400, 16));
	    PATH_AREA.setEditable (false);
	    JButton T = new JButton ("Change");
	    T.setActionCommand ("ChangePath");
	    T.addActionListener (this);
	    B.add (T);
	    B.setAlignmentX (0.0f);
	    BOX.add (B);
	}			       

	{
	    Box B = new Box (BoxLayout.X_AXIS);
	    JLabel J = new JLabel ("RunTimeOptions: ");
	    B.add (J);
	    JTextField F = new JTextField (COMM.RunTimeOptions);
	    F.setBackground (new Color (225, 215, 235));
	    F.setPreferredSize (new Dimension (250, 16));
	    F.setMaximumSize (new Dimension (250, 16));
	    F.setName ("RunTimeOptions");
	    F.addKeyListener (this);
	    B.add (F);
	    B.setAlignmentX (0.0f);
	    BOX.add (B);
	}

	for (int i = 0; i < COMM.opt_count; i++)
	    {
		StringBuffer SB = new StringBuffer (COMM.options[i]);
		int n = SB.indexOf (" name ");
		int t = SB.indexOf (" type ");
		String NAME = SB.substring (n + 6, t);
		StringTokenizer ST = new StringTokenizer (SB.substring (t + 6));
		COMM.OPT_NAME[i] = new String (NAME);
		String TYPE = ST.nextToken ();
		if (TYPE.equals ("button"))
		    {
			Box B = new Box (BoxLayout.X_AXIS);
			JLabel L = new JLabel ("PushOnLoad:  ");
			L.setBackground (new Color (235, 225, 215));
			B.add (L);
			JCheckBox C = new JCheckBox (NAME);
			if (COMM.IS_NEW)
			    C.setSelected (false);
			else
			    C.setSelected (COMM.OPT_VALUE[i].equals ("true"));
			C.addItemListener (this);
			B.add (C);
			B.setAlignmentX (0.0f);
			BOX.add (B);
			if (COMM.IS_NEW)
			    {
				COMM.OPT_TYPE[i] = new String ("button");
				COMM.OPT_VALUE[i] = new String ("false");
			    }
		    }
		if (TYPE.equals ("check"))
		    {
			JCheckBox B = new JCheckBox (NAME);
			if (COMM.IS_NEW)
			    {
				if (ST.countTokens () < 2)
				    B.setSelected (false);
				else
				    {
					ST.nextToken ();
					if (ST.nextToken ().equals ("true"))
					    B.setSelected (true);
					else
					    B.setSelected (false);
				    }
			    }
			else
			    B.setSelected (COMM.OPT_VALUE[i].equals ("true"));
			B.addItemListener (this);
			B.setAlignmentX (0.0f);
			BOX.add (B);
			if (COMM.IS_NEW)
			    {
				COMM.OPT_TYPE[i] = new String ("check");
				if (B.isSelected ())
				    COMM.OPT_VALUE[i] = new String ("true");
				else
				    COMM.OPT_VALUE[i] = new String ("false");
			    }
		    }
		if (TYPE.equals ("combo")) // ?
		    {
			Box B = new Box (BoxLayout.X_AXIS);
			JLabel L = new JLabel (NAME + ":  ");
			JComboBox J = new JComboBox ();
			L.setBackground (new Color (195, 225, 255));
			B.add (L);
			String DEFAULT = null;

			String X = new String (COMM.options[i]);
			int s = X.indexOf ("var ");
			int u = X.indexOf ("default ");
			if (s > u)
			    s = u;
			X = X.substring (s);
			while (s != -1)
			    {
				X = X.substring (s);
				if (X.startsWith ("var "))
				    X = X.substring (4);
				else
				    {
					s = X.indexOf ("var "); // HACK
					String Y;
					if (s == -1)
					    Y = X;
					else
					    Y = X.substring (0, s - 1);
					if (COMM.IS_NEW)
					    DEFAULT = new String (Y);
					else
					    DEFAULT = new String (COMM.OPT_VALUE[i]);
				    }
				s = X.indexOf ("var ");
				u = X.indexOf ("default ");
				if (s == -1 && u == -1)
				    J.addItem (X);
				if (s == -1 && u != -1)
				    J.addItem (X.substring (0, u - 1));
				if (s != -1 && u == -1)
				    J.addItem (X.substring (0, s - 1));
				if (s != -1 && u != -1)
				    {
					if (s > u)
					    s = u;
					J.addItem (X.substring (0, s - 1));
				    }
			    }
			J.setActionCommand (NAME);
			J.addActionListener (this);
			J.setSelectedItem (DEFAULT);
			J.setPreferredSize (new Dimension (250, 20));
			J.setMaximumSize (new Dimension (250, 20));
			B.add (J);
			B.setAlignmentX (0.0f);
			BOX.add (B);
			if (COMM.IS_NEW)
			    {
				COMM.OPT_TYPE[i] = new String ("combo");
				COMM.OPT_VALUE[i] = new String (DEFAULT);
			    }
		    }
		if (TYPE.equals ("string"))
		    {
			Box B = new Box (BoxLayout.X_AXIS);
			JLabel L = new JLabel (NAME + ":  ");
			L.setBackground (new Color (235, 225, 215));
			B.add (L);
			String DEFAULT;
			if (COMM.IS_NEW)
			    {
				if (ST.countTokens () < 2)
				    DEFAULT = new String ("NULL");
				else
				    {
					ST.nextToken ();
					DEFAULT = new String (ST.nextToken ());
				    }
			    }
			else
			    {
				ST.nextToken ();
				ST.nextToken ();
				DEFAULT = new String (COMM.OPT_VALUE[i]);
			    }
			JTextField F = new JTextField (DEFAULT);
			F.setBackground (new Color (225, 215, 235));
			F.setPreferredSize (new Dimension (250, 16));
			F.setMaximumSize (new Dimension (250, 16));
			F.setName (NAME);
			F.addKeyListener (this);
			B.add (F);
			B.setAlignmentX (0.0f);
			BOX.add (B);
			if (COMM.IS_NEW)
			    {
				COMM.OPT_TYPE[i] = new String ("string");
				COMM.OPT_VALUE[i] = new String (DEFAULT);
			    }
		    }
		if (TYPE.equals ("spin"))
		    {
			if (ST.countTokens () < 6)
			    continue; // HACK
			Box B = new Box (BoxLayout.X_AXIS);
			JLabel L = new JLabel (NAME + ":  ");
			L.setBackground (new Color (205, 195, 235));
			B.add (L);
			Integer min = null, max = null, def = null;
			while (ST.hasMoreTokens ())
			    {
				String S = ST.nextToken ();
				Integer m = Integer.valueOf (ST.nextToken ());
				if (S.equals ("max"))
				    max = m;
				if (S.equals ("min"))
				    min = m;
				if (S.equals ("default"))
				    def = m;
			    }
			if (!COMM.IS_NEW)
			    def = Integer.valueOf (COMM.OPT_VALUE[i]);
			if (def > max)
			    def = new Integer (max); // HACK
			if (def < min)
			    def = new Integer (min); // HACK
			SpinnerNumberModel MODEL =  new SpinnerNumberModel (def, min, max, new Integer (1));
			JSpinner J = new JSpinner (MODEL);
			J.setName (NAME);
			J.addChangeListener (this);
			J.setPreferredSize (new Dimension (60, 16));
			J.setMaximumSize (new Dimension (60, 16));
			B.add (J);
			JLabel M = new JLabel ("  min " + min + " max " + max);
			M.setFont (new Font ("Monospaced", 0, 12));
			B.add (M);
			B.setAlignmentX (0.0f);
			BOX.add (B);
			if (COMM.IS_NEW)
			    {
				COMM.OPT_TYPE[i] = new String ("spin");
				COMM.OPT_VALUE[i] = new String (def.toString ()); // HACK
			    }
		    }
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

    public void ModifyDefaults ()
    {
	String S;
	if (COMM.IS_NEW)
	    S = "";
	else
	    S = COMM.id;
	OPTIONS_FRAME = new JFrame ("Default Options " + S); // HACK
	AddOptions_UCI (OPTIONS_FRAME);
	OPTIONS_FRAME.setBackground (Color.lightGray);
	OPTIONS_FRAME.pack ();
        OPTIONS_FRAME.setSize (600, 600); // demand
        OPTIONS_FRAME.setResizable (false);
        OPTIONS_FRAME.setVisible (true);       
    }

}
