import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import java.lang.*;

public class CJMenuItem extends JMenuItem
{
    Communicator COMM;

    public CJMenuItem (Communicator C)
    {
	COMM = C;
	setText (C.id); // id
    }

}
