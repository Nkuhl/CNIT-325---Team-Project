package edu.purdue.comradesgui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

public class DemandPGN implements ActionListener, MouseListener
{
    ComradesFrame CF;
    JFrame FRAME;
    JList LIST;
    JScrollPane PANE;
    JButton LOAD_BUTTON, OK_BUTTON, CANCEL_BUTTON, NEW_BUTTON, PASTE_BUTTON;

    public DemandPGN (ComradesFrame cf)
    {
	CF = cf;
	MakeFrame ();
    }

    public void mouseEntered (MouseEvent mou_evt) {}
    public void mouseExited (MouseEvent mou_evt) {}
    public void mousePressed (MouseEvent mou_evt) {}
    public void mouseReleased (MouseEvent mou_evt) {}
    public void mouseClicked (MouseEvent mou_evt)
    {
	if (mou_evt.getClickCount () == 2)
	    {
		int index = LIST.getSelectedIndex ();
		CF.DealCards (((BoardPanel) (CF.BOARD_PANEL_COLLECTIVE.getComponent (index))).Name);
		FRAME.setVisible (false); // viable ?
		FRAME.dispose ();
		FRAME = null;		
	    }
    }

    public void actionPerformed (ActionEvent act_evt)
    {
	if (act_evt.getActionCommand () == "Cancel")
	    {
		FRAME.setVisible (false);
		FRAME.dispose ();
		FRAME = null;
	    }
	if (act_evt.getActionCommand () == "OK")
	    {
		int index = LIST.getSelectedIndex ();
		CF.DealCards (((BoardPanel) (CF.BOARD_PANEL_COLLECTIVE.getComponent (index))).Name);
		FRAME.setVisible (false); // viable ?
		FRAME.dispose ();
		FRAME = null;		
	    }
	if (act_evt.getActionCommand () == "Load")
	    {
		CF.DoLoadPGN ();
		MakeList ();
		PANE.revalidate ();
	    }

	if (act_evt.getActionCommand () == "New")
	    {
		CF.NewPGN ();
		FRAME.setVisible (false); // viable ?
		FRAME.dispose ();
		FRAME = null;		
	    }
	if (act_evt.getActionCommand () == "Paste")
	    {
		CF.DoPastePGN ();
		FRAME.setVisible (false); // viable ?
		FRAME.dispose ();
		FRAME = null;		
	    }
    }

    public void MakeList ()
    {
	LIST = new JList (CF.BOARD_PANEL_COLLECTIVE.getComponents ());
	LIST.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
	LIST.addMouseListener (this);
	PANE.setViewportView (LIST);
	for (int i = 0; i < CF.BOARD_PANEL_COLLECTIVE.getComponentCount (); i++)
            if (CF.BOARD_PANEL_COLLECTIVE.getComponent (i).isVisible ())
		{
		    LIST.setSelectedIndex (i);
		    LIST.ensureIndexIsVisible (i); // EnSure
		}	
    }

    public void MakeButtons ()
    {
	LOAD_BUTTON = new JButton ("Load");
	LOAD_BUTTON.setActionCommand ("Load");
	LOAD_BUTTON.addActionListener (this);
	OK_BUTTON = new JButton ("OK");
	OK_BUTTON.setActionCommand ("OK");
	OK_BUTTON.addActionListener (this);
	NEW_BUTTON = new JButton ("New");
	NEW_BUTTON.setActionCommand ("New");
	NEW_BUTTON.addActionListener (this);
	PASTE_BUTTON = new JButton ("Paste");
	PASTE_BUTTON.setActionCommand ("Paste");
	PASTE_BUTTON.addActionListener (this);
	CANCEL_BUTTON = new JButton ("Cancel");
	CANCEL_BUTTON.setActionCommand ("Cancel");
	CANCEL_BUTTON.addActionListener (this);
    }

    public void MakeBox ()
    {
	PANE = new JScrollPane ();
	MakeList ();
	Box BOX = new Box (BoxLayout.Y_AXIS);
	BOX.add (PANE);
	MakeButtons ();
	Box HBOX = new Box (BoxLayout.X_AXIS);
	HBOX.add (LOAD_BUTTON);
	HBOX.add (OK_BUTTON);
	HBOX.add (NEW_BUTTON);
	HBOX.add (PASTE_BUTTON);
	HBOX.add (CANCEL_BUTTON);
	BOX.add (HBOX);
	FRAME.add (BOX);
    }

    public void MakeFrame ()
    {
	FRAME = new JFrame ("PGN Selector");
	MakeBox ();
	FRAME.setBackground (Color.lightGray);
	FRAME.pack ();
	FRAME.setSize (600, 400);
	FRAME.setResizable (false);
	FRAME.setVisible (true);
    }
}
