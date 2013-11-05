package edu.purdue.comradesgui;

import javax.swing.tree.*;

public class ComradesNode extends DefaultMutableTreeNode
{
    ComradesNode PARENT; // proper
    TypeMove mv, mainline;
    long hash;
    boolean is_expanded = false;
    ComradesNode transpose, MainLineNode, MainLineParent;
    ComradesLabel LABEL;

    public ComradesNode () // root
    {
	super ();
	PARENT = null;
	mv = new TypeMove ();
	transpose = this; // recur
	MainLineNode = MainLineParent = null;
	LABEL = null;
    }
    
    public ComradesNode (ComradesNode parent, TypeMove move)
    {
	super ();
	PARENT = parent;
	mv = move;
	PARENT.add (this);
	// check transpose // Hashtable ?
	transpose = this;
	MainLineNode = MainLineParent = null;
	LABEL = null;
    }
    
    public String toString ()
    {
	return mv.toString ();
    }

    public ComradesNode getChildAt (int x)
    {
	return (ComradesNode) (super.getChildAt (x));
    }

}
