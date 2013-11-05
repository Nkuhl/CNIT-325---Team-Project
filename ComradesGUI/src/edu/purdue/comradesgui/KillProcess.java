package edu.purdue.comradesgui;

 public class KillProcess extends Thread
 {
     Process process;
     CommunicatorInstance CI;

     public KillProcess (Process process, CommunicatorInstance ci)
     {
	 super ();
	 this.process = process;
	 CI = ci;
     }

     public void run ()
     {
	 CI.SendTo ("quit", true);
	 CI.SleepFor (100);
	 process.destroy (); // HACK
     }

 }
