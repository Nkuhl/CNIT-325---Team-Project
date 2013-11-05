import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

public class InstanceOptionsFrame implements KeyListener, ItemListener, ActionListener, ChangeListener
{
    CommunicatorInstance CI;
    JFrame OPTIONS_FRAME;
    String[] COPY_NAME;
    String[] COPY_TYPE;
    String[] COPY_VALUE;
    int opt_count;
    String[] options;
    String[] SUB_CLASSES;
    int sub_class_count;

    public InstanceOptionsFrame (CommunicatorInstance ci)
    {
	CI = ci;
	options = new String[1024];
	SUB_CLASSES = new String[64];
    }

    public void TransactText ()
    {
	JFrame J = new JFrame ("ThroughPut " + CI.COMM.id);
	JTextArea JTA = new JTextArea (80, 40);
        JTA.setText (CI.LOG_SB.toString ());
	JScrollPane JSP = new JScrollPane (JTA);
	JSP.setPreferredSize (new Dimension (700, 550));
	J.add (JSP);
	J.setBackground (Color.lightGray);
	J.setSize (700, 550);
	J.setVisible (true);
	J.pack ();
    }

    public void RejectChanges ()
    {
        for (int i = 0; i < opt_count; i++)
            CI.OPT_VALUE[i] = new String (COPY_VALUE[i]);
        OPTIONS_FRAME.setVisible (false);
        OPTIONS_FRAME.dispose (); // null
        OPTIONS_FRAME = null;
        CI.IOF_ICI = null;
        CI.IOF_UCI = null;
        CI.SendTo ("isready", true);
        CI.WaitForThroughPut ("readyok", -1, false);
    }

    public void ConfirmChanges ()
    {
        for (int i = 0; i < opt_count; i++)
            if (!CI.OPT_TYPE[i].equals ("button") && !CI.OPT_VALUE[i].equals (COPY_VALUE[i]))
		{
		    if (CI.OPT_TYPE[i].equals ("directory-multi-reset"))
			{
			    StringTokenizer ST = new StringTokenizer (CI.OPT_VALUE[i],"|");
			    while (ST.hasMoreTokens ())
				CI.SendTo ("setoption name " + CI.OPT_NAME[i] +
					   " value " + ST.nextToken (), false);
			}
		    else
			CI.SendTo ("setoption name " +  CI.OPT_NAME[i] +
				   " value " + CI.OPT_VALUE[i], false);
		}
        for (int i = 0; i < opt_count; i++)
            {
                if (CI.OPT_NAME[i].equals ("MultiPV"))
                    CI.MultiPV = Integer.valueOf (CI.OPT_VALUE[i]).intValue ();
                if (CI.OPT_NAME[i].equals ("MultiCentiPawnPV") ||
		    CI.OPT_NAME[i].equals ("MultiPV_cp"))
                    CI.MultiPV_Centi_Pawn = Integer.valueOf (CI.OPT_VALUE[i]).intValue ();
            }
        CI.IP.RenewInstancePanel ();
        CI.SendTo ("isready", true);
        CI.WaitForThroughPut ("readyok", -1, true);
        OPTIONS_FRAME.setVisible (false);
        OPTIONS_FRAME.dispose (); // null
        CI.IOF_ICI = null;
        CI.IOF_UCI = null;
        OPTIONS_FRAME = null;
    }

    public void ForbidInstance ()
    {
        int i;
        OPTIONS_FRAME.setVisible (false);
        OPTIONS_FRAME.dispose (); // null
	CI.SendTo ("quit", true);
	CI.SleepFor (100);
        CI.process.destroy ();
        CI.process = null; // ensure
        for (i = 0; i < CI.CF.instances; i++)
            if (CI == CI.CF.INSTANCES[i])
                break;
        for (int j = i; j < CI.CF.instances - 1; j++)
            CI.CF.INSTANCES[j] = CI.CF.INSTANCES[j + 1];
        CI.DisMissInstance ();
    }

    public void ButtonPush (String S)
    {
        CI.SendTo ("setoption name " + S + " value true", true); // HACK
	if (S.equals ("RobboInformatory"))
	    new RobboInformatoryFrame (CI);
    }

    public void ChangeMultiValue (String NAME, String VALUE, int LOC)
    {
        int i;
        for (i = 0; i < opt_count; i++)
            if (NAME.equals (CI.OPT_NAME[i]))
                break;
        if (i >= opt_count)
            return;
        StringTokenizer ST = new StringTokenizer (CI.OPT_VALUE[i],"|");
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
        CI.OPT_VALUE[i] = new String (RESULT);
    }

    public void ChangeValue (String NAME, String VALUE)
    {
        int i;
        for (i = 0; i < opt_count; i++)
            if (NAME.equals (CI.OPT_NAME[i]))
                break;
        if (i >= opt_count)
            return;
        CI.OPT_VALUE[i] = new String (VALUE);
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
                if (act_evt.getActionCommand () == "CONFIRM")
                    ConfirmChanges ();
                else if (act_evt.getActionCommand () == "REJECT")
                    RejectChanges ();
                else if (act_evt.getActionCommand () == "FORBID")
                    ForbidInstance ();
                else if (act_evt.getActionCommand () == "TRANSACT")
                    TransactText ();
                else
                    ButtonPush (act_evt.getActionCommand ());
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

    public void TopOptions (Box BOX)
    {
	Box B = new Box (BoxLayout.X_AXIS);
	JButton B1 = new JButton ("Confirm Instance Options");
	B1.setActionCommand ("CONFIRM");
	B1.addActionListener (this);
	B1.setFont (B1.getFont ().deriveFont (16.0f));
	B.add (B1);
	JLabel L = new JLabel (" ");
	B.add (L);
	JButton B2 = new JButton ("Reject Changes");
	B2.setActionCommand ("REJECT");
	B2.addActionListener (this);
	B2.setFont (B2.getFont ().deriveFont (14.0f));
	B.add (B2);
	JLabel LL = new JLabel (" ");
	B.add (LL);
	JButton B3 = new JButton ("Forbid Instance");
	B3.setActionCommand ("FORBID");
	B3.addActionListener (this);
	B3.setFont (B3.getFont ().deriveFont (12.0f));
	B.add (B3);
	JLabel LLL = new JLabel (" ");
	B.add (LLL);
	JButton B4 = new JButton ("Show Transactions");
	B4.setActionCommand ("TRANSACT");
	B4.addActionListener (this);
	B4.setFont (B4.getFont ().deriveFont (12.0f));
	B.add (B4);
	B.setAlignmentX (0.0f);
	BOX.add (B);
    }

    public void ButtonBox (Box BOX, String NAME)
    {
	JButton B = new JButton (NAME);
	B.setActionCommand (NAME);
	B.addActionListener (this);
	B.setAlignmentX (0.0f);
	BOX.add (B);
    }

    public void CheckBox (Box BOX, String NAME, int i)
    {
	JCheckBox B = new JCheckBox (NAME);
	B.setSelected (CI.OPT_VALUE[i].equals ("true"));
	B.addItemListener (this);
	B.setAlignmentX (0.0f);
	BOX.add (B);
    }

    public void ComboBox (Box BOX, String NAME, String STR, int i)
    {
	StringTokenizer ST = new StringTokenizer (STR);
	ST.nextToken (); // combo
	Box B = new Box (BoxLayout.X_AXIS);
	JLabel L = new JLabel (NAME + ":  ");
	JComboBox J = new JComboBox ();
	L.setBackground (new Color (195, 225, 255));
	B.add (L);
	String DEFAULT;
	DEFAULT = new String (CI.OPT_VALUE[i]);
	if (CI.COMM.ICI)
	    {
		while (true)
		    {
			String S = ST.nextToken ();
			if (!S.startsWith ("*"))
			    J.addItem (S);
			else
			    break;
		    }
	    }
	else
	    {
		String X = new String (STR);
		int s = X.indexOf ("var ");
		int t = X.indexOf ("default ");
		if (s > t)
		    s = t;
		X = X.substring (s);
		while (s != -1)
		    {
			X = X.substring (s);
			if (X.startsWith ("var "))
			    X = X.substring (4);
			else
			    {
				s = X.indexOf ("var ");			    
				continue;
			    }
			s = X.indexOf ("var ");			    
			t = X.indexOf ("default ");
			if (s == -1 && t == -1)
			    J.addItem (X);
			if (s == -1 && t != -1)
			    J.addItem (X.substring (0, t - 1));
			if (s != -1 && t == -1)
			    J.addItem (X.substring (0, s - 1));
			if (s != -1 && t != -1)
			    {
				if (s > t)
				    s = t;
				J.addItem (X.substring (0, s - 1));
			    }
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
    }

    public void StringBox (Box BOX, String NAME, int i)
    {
	Box B = new Box (BoxLayout.X_AXIS);
	JLabel L = new JLabel (NAME + ":  ");
	L.setBackground (new Color (235, 225, 215));
	B.add (L);
	String DEFAULT = new String (CI.OPT_VALUE[i]);
	JTextField F = new JTextField (DEFAULT);
	F.setBackground (new Color (225, 215, 235));
	F.setPreferredSize (new Dimension (250, 16));
	F.setMaximumSize (new Dimension (250, 16));
	F.setName (NAME);
	F.addKeyListener (this);
	B.add (F);
	B.setAlignmentX (0.0f);
	BOX.add (B);
    }

    public void SpinBox (Box BOX, String NAME, StringTokenizer ST, int i, boolean BINARY)
    {
	Box B = new Box (BoxLayout.X_AXIS);
	JLabel L = new JLabel (NAME + ":  ");
	L.setBackground (new Color (205, 195, 235));
	B.add (L);
	Integer min = null, max = null, def = null;
	if (CI.COMM.ICI)
	    {
		min = Integer.valueOf (ST.nextToken ());
		max = Integer.valueOf (ST.nextToken ());
		def = Integer.valueOf (ST.nextToken ());
	    }
	else
	    {
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
	    }
	def = Integer.valueOf (CI.OPT_VALUE[i]);
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
    }
}
