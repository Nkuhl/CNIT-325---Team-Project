package edu.purdue.comradesgui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

public class CommunicatorOptionsFrameICI implements ItemListener, ActionListener, ChangeListener, KeyListener
{
    JTextField PATH_AREA;
    JFrame OPTIONS_FRAME;
    Communicator COMM;

    public CommunicatorOptionsFrameICI (Communicator comm)
    {
	COMM = comm;
	ModifyDefaults ();
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

    public void ChangeMultiValue (String NAME, String VALUE, int LOC)
    {
	int i;
	for (i = 0; i < COMM.opt_count; i++)
	    if (NAME.equals (COMM.OPT_NAME[i]))
		break;
	if (i >= COMM.opt_count)
	    return;
	StringTokenizer ST = new StringTokenizer (COMM.OPT_VALUE[i],"|");
	int n = ST.countTokens();
	if (LOC == n)
	    n++;
	String[] S = new String[n];
	n = 0;
	while (ST.hasMoreTokens ())
	    S[n++] = new String (ST.nextToken ());
	S[LOC] = new String (VALUE);
	if (LOC == n)
	    n++;
	String RESULT = new String (S[0]);
	for (int k = 1; k < n; k++)
	    RESULT += "|" + S[k];
	COMM.OPT_VALUE[i] = new String (RESULT);
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

    public void SaveDeleteBox (Box BOX)
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

    public void MenuNameBox (Box BOX)
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

    public void PathBox (Box BOX) // FILE
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

    public void ButtonBox (Box BOX, String NAME, int i)
    {
	Box B = new Box (BoxLayout.X_AXIS);
	JLabel L = new JLabel ("PushOnLoad:");
	L.setForeground (new Color (80, 120, 100));
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

    public void CheckBox (Box BOX, String NAME, StringTokenizer ST, int i)
    {
	JCheckBox B = new JCheckBox (NAME);
	if (COMM.IS_NEW)
	    {
		if (ST.nextToken ().equals ("true"))
		    B.setSelected (true);
		else
		    B.setSelected (false);
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

    public void ComboBox (Box BOX, String NAME, StringTokenizer ST, int i)
    {    
	Box B = new Box (BoxLayout.X_AXIS);
	JLabel L = new JLabel (NAME + ":  ");
	JComboBox J = new JComboBox ();
	L.setBackground (new Color (195, 225, 255));
	B.add (L);
	String DEFAULT = null;
	while (true)
	    {
		String S = ST.nextToken ();
		if (!S.startsWith ("*"))
		    J.addItem (S);
		else
		    {
			if (COMM.IS_NEW)
			    DEFAULT = new String (S.substring (1));
			else
			    DEFAULT = new String (COMM.OPT_VALUE[i]);
			break;
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
		    COMM.CF.TellInfo ("File does not exist");
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
	String DEFAULT;
	if (COMM.IS_NEW)
	    DEFAULT = new String ("NULL");
	else
	    DEFAULT = new String (COMM.OPT_VALUE[i]);
	FileButton FB = new FileButton (DEFAULT, NAME, dir);
	BOX.add (FB.MakeBox ());
	if (COMM.IS_NEW)
	    {
		if (!dir)
		    COMM.OPT_TYPE[i] = new String ("file");
		else
		    COMM.OPT_TYPE[i] = new String ("directory");
		COMM.OPT_VALUE[i] = new String (DEFAULT);
	    }
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
		    COMM.CF.TellInfo ("File does not exist");
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
        if (COMM.IS_NEW)
            DEFAULT = new String ("NULL");
        else
            DEFAULT = new String (COMM.OPT_VALUE[i]);
	if (COMM.IS_NEW)
	    {
		COMM.OPT_TYPE[i] = new String ("directory-multi-reset");
		COMM.OPT_VALUE[i] = new String (DEFAULT);
	    }
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

    public void RunTimeOptionsBox (Box BOX)
    {
	Box B = new Box (BoxLayout.X_AXIS);
	JLabel L = new JLabel ("RunTimeOptions" + ":  ");
	L.setBackground (new Color (235, 225, 215));
	B.add (L);
	String DEFAULT;
	if (COMM.IS_NEW)
	    DEFAULT = new String ("NULL");
	else
	    DEFAULT = new String (COMM.RunTimeOptions);
	JTextField F = new JTextField (DEFAULT);
	F.setBackground (new Color (225, 215, 235));
	F.setPreferredSize (new Dimension (250, 16));
	F.setMaximumSize (new Dimension (250, 16));
	F.setName ("RunTimeOptions");
	F.addKeyListener (this);
	B.add (F);
	B.setAlignmentX (0.0f);
	BOX.add (B);
    }

    public void StringBox (Box BOX, String NAME, int i)
    {
	Box B = new Box (BoxLayout.X_AXIS);
	JLabel L = new JLabel (NAME + ":  ");
	L.setBackground (new Color (235, 225, 215));
	B.add (L);
	String DEFAULT;
	if (COMM.IS_NEW)
	    DEFAULT = new String ("NULL");
	else
	    DEFAULT = new String (COMM.OPT_VALUE[i]);
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

    public void SpinBox (Box BOX, String NAME, StringTokenizer ST, int i, boolean BINARY)
    {
	Box B = new Box (BoxLayout.X_AXIS);
	JLabel L = new JLabel (NAME + ":  ");
	L.setBackground (new Color (205, 195, 235));
	B.add (L);
	Integer min = null, max = null, def = null;
	min = Integer.valueOf (ST.nextToken ());
	max = Integer.valueOf (ST.nextToken ());
	def = Integer.valueOf (ST.nextToken ());
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
	if (BINARY)
	    {
		JLabel N = new JLabel (" (binary)");
		N.setFont (new Font ("Serif", 0, 11));
		B.add (N);
	    }
	B.setAlignmentX (0.0f);
	BOX.add (B);
	if (COMM.IS_NEW)
	    {
		if (BINARY)
		    COMM.OPT_TYPE[i] = new String ("binary");
		else
		    COMM.OPT_TYPE[i] = new String ("spin");
		COMM.OPT_VALUE[i] = new String (def.toString ()); // HACK
	    }
    }

    public void DoTabsICI (JTabbedPane TABBED, Box TOP_BOX)
    {
	SaveDeleteBox (TOP_BOX);
	MenuNameBox (TOP_BOX);
	PathBox (TOP_BOX);
	RunTimeOptionsBox (TOP_BOX);
	for (int i = 0; i < COMM.opt_count; i++)
	    {
		StringTokenizer ST = new StringTokenizer (COMM.options[i]);
		COMM.OPT_NAME[i] = ST.nextToken ();
		if (COMM.IS_NEW)
		    COMM.OPT_VALUE[i] = null;
		String SUB_CLASS = ST.nextToken ();
		String TYPE = ST.nextToken ();
		int w = TABBED.indexOfTab (SUB_CLASS);
		if (w == -1)
		    continue; // HACK
		JScrollPane JSP = (JScrollPane) (TABBED.getComponentAt (w));
		Box BOX = (Box) (JSP.getViewport ().getView ()); // HACK
		if (TYPE.equals ("button"))
		    ButtonBox (BOX, COMM.OPT_NAME[i], i);
		if (TYPE.equals ("check"))
		    CheckBox (BOX, COMM.OPT_NAME[i], ST, i);
		if (TYPE.equals ("combo"))
		    ComboBox (BOX, COMM.OPT_NAME[i], ST, i); // no test
		if (TYPE.equals ("file"))
		    FileBox (BOX, COMM.OPT_NAME[i], i, false);
		if (TYPE.equals ("directory"))
		    FileBox (BOX, COMM.OPT_NAME[i], i, true);
		if (TYPE.equals ("directory-multi-reset"))
		    DirectoryMultiResetBox (BOX, COMM.OPT_NAME[i], i);
		if (TYPE.equals ("string"))
		    StringBox (BOX, COMM.OPT_NAME[i], i);
		if (TYPE.equals ("spin"))
		    SpinBox (BOX, COMM.OPT_NAME[i], ST, i, false);
		if (TYPE.equals ("binary"))
		    SpinBox (BOX, COMM.OPT_NAME[i], ST, i, true);
	    }
    }

    public void AddOptions_ICI (JFrame OPT)
    {
	JTabbedPane TABBED = new JTabbedPane ();
	Box[] BOXES = new Box[COMM.sub_class_count];
	for (int i = 0; i < COMM.sub_class_count; i++)
	    {
		BOXES[i] = new Box (BoxLayout.Y_AXIS);
		JScrollPane JSP = new JScrollPane (BOXES[i]);
		JSP.getViewport ().add (BOXES[i]); // ensure
		TABBED.addTab (COMM.SUB_CLASSES[i], JSP);
	    }
	Box TOP_BOX = new Box (BoxLayout.Y_AXIS);
	DoTabsICI (TABBED, TOP_BOX);
	Box OVER_BOX = new Box (BoxLayout.Y_AXIS);
	OVER_BOX.add (TOP_BOX);
	OVER_BOX.add (TABBED);
	OPT.add (OVER_BOX);
    }

    public void ModifyDefaults ()
    {
	String S;
	if (COMM.IS_NEW)
	    S = "";
	else
	    S = COMM.id;
	OPTIONS_FRAME = new JFrame ("Default Options " + S); // HACK
	AddOptions_ICI (OPTIONS_FRAME);
	OPTIONS_FRAME.setBackground (Color.lightGray);
	OPTIONS_FRAME.pack ();
        OPTIONS_FRAME.setSize (750, 550); // demand
        OPTIONS_FRAME.setResizable (false);
        OPTIONS_FRAME.setVisible (true);       
    }

}
