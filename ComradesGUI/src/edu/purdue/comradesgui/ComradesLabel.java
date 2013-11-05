package edu.purdue.comradesgui;

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class ComradesLabel extends JButton // for ComradesNode.java ?
{
    ComradesNode N;
    MoveTree MT;
    boolean END;

    public MouseAdapter ComradesLabelMouseAdapter () // adapt
    {
	MouseAdapter MA = new MouseAdapter ()
	    {
		public void mouseClicked (MouseEvent mou_evt)
		{
		    if (END)
			N.is_expanded = false; // un do // focus ?
		    else if (mou_evt.getClickCount () == 2)
			{
			    MT.JumpToNode (N);
			    N.is_expanded = false;
			}
		    else
			{
			    MT.FOCUS = true;
			    if (N.getChildCount () > 1)
				N.is_expanded = true;
			}
		    MT.PaintPanel ();
		}
	    };
	    return MA;    
    }

    public KeyAdapter ComradesLabelKeyAdapter ()
    {
	KeyAdapter KA = new KeyAdapter ()
	    {
		public void ChangeVariation (int dir) // + 1 or - 1 (value)
		{
		    ComradesNode T = N;
		    while (!T.is_expanded && T.getParent () != null)
			T = T.MainLineParent;
		    if (T.is_expanded)
			{
			    for (int i = 0; i < T.getChildCount (); i++)
				if (T.MainLineNode  == T.getChildAt (i))
				    {
					int w = i + dir;
					if (w < T.getChildCount () && w >= 0)
					    MT.JumpToNode (T.getChildAt (w));
					if (w < 0)
					    {
						T.is_expanded = false;
						MT.JumpToNode (T);
					    }
					return;
				    }
			}
		}

		public void keyReleased (KeyEvent key_evt)
		{
		    if (MT.NOW != N)
			return; // HACK
		    if (key_evt.getKeyCode () == key_evt.VK_RIGHT)
			MT.BP.PANEL.AbruptForward ();
		    if (key_evt.getKeyCode () == key_evt.VK_LEFT)
			MT.BP.PANEL.AbruptBack ();
		    if (key_evt.getKeyCode () == key_evt.VK_UP)
			ChangeVariation (-1); // switch
		    if (key_evt.getKeyCode () == key_evt.VK_DOWN)
			ChangeVariation (+1); // switch
		    if (key_evt.getKeyCode () == key_evt.VK_SPACE)
			{
			    if (!N.is_expanded && N.getChildCount () > 1)
				{
				    N.is_expanded = true;
				    MT.BP.PANEL.AbruptForward ();
				}
			}
		    if (key_evt.getKeyCode () == key_evt.VK_TAB)
			{
			    int mod = key_evt.getModifiersEx ();
			    if ((mod & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK)
				MT.BP.CF.ForWardCards ();
			    if ((mod & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK)
				MT.BP.CF.BackWardCards ();
			    return;
			}
		}
	    };
	    return KA;
    }

    public ComradesLabel (TreeNode n, MoveTree mt, boolean end, int ht)
    {
	super ();
	MT = mt;
	END = end;
	if (END)
	    setText ("\u25bc");
	else if (ht == 0)
	    setText (n.toString ());
	else if ((ht % 2) == 0)
	    setText ("" + (ht / 2) + ". " + n.toString ());
	else if ((ht % 2) == 1)
	    setText ("" + (ht / 2) + "... " + n.toString ());
	if (n.getParent () == null && !END)
	    setText ("R");
	setBorder (BorderFactory.createEmptyBorder ()); // save
	//	setMargin (new Insets (1, 1, 1, 1)); // save
	if (!END)
	    setFont (new Font ("Serif", 0, 12)); // save
	else
	    setFont (new Font ("Monospaced", 0, 12)); // save
	setBackground (Color.white);
	if (END)
	    setForeground (Color.black);
	else if (n.getParent () == null)
	    setForeground (Color.blue);
	else
	    setForeground ((n.getParent ().getChildCount () > 1) // transpose ?
			   ? Color.red : Color.black);
	N = (ComradesNode) n; // involve
	addMouseListener (ComradesLabelMouseAdapter ()); // in direct
	addKeyListener (ComradesLabelKeyAdapter ());
        setFocusTraversalKeysEnabled (false); // value, for the plus!
	setFocusable (true); // value ?
	if (!END)
	    N.LABEL = this;
    }
}
