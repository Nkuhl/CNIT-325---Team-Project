VERSION = ProtoType

JAVAC = javac
JAR = jar

CP = cp
MKDIR = mkdir
RM = rm
TAR = tar

FONTS = MERIFONT.TTF LEIPFONT.TTF KINGFONT.TTF
SOURCE = BoardPanel.java \
         ComradesGUI.java \
         BoardPosition.java \
         CJMenuItem.java \
         MonteCarlo.java \
         CommIO.java \
         Communicator.java \
         CommunicatorOptionsFrameICI.java \
         CommunicatorOptionsFrameUCI.java \
         CommunicatorInstance.java \
         ComradesFrame.java \
         DemandPGN.java \
         RobboInformatoryFrame.java \
         ComradesOptioner.java \
         InstanceOptionsFrame.java \
         InstanceOptionsFrameUCI.java \
         InstanceOptionsFrameICI.java \
         InstancePanel.java \
         KillProcess.java \
         TypeMove.java \
         DataPGN.java \
         MovePane.java \
         ComradesNode.java \
         ComradesLabel.java \
         MoveTree.java

CLASSES = $(SOURCE:.java=.class)
WORKING_DIR = ComradesGUI-$(VERSION)
INNER = #

ALL = $(FONTS) $(INNER) $(SOURCE) Makefile MANIFEST READ_ME

all: ComradesGUI.jar
ComradesGUI.jar: $(CLASSES) Makefile
	$(JAR) cfm ComradesGUI.jar MANIFEST $(CLASSES) $(INNER)
%.class: %.java Makefile
	$(JAVAC) $<
archive:
	$(RM) -rf $(WORKING_DIR) *.tar # ensure
	$(MKDIR) $(WORKING_DIR)
	$(CP) $(ALL) $(WORKING_DIR) # inner classes ?
	$(TAR) cf $(WORKING_DIR).tar $(WORKING_DIR)/*
	$(RM) -rf $(WORKING_DIR)
clean:
	$(RM) -f $(CLASSES) $(INNER) *.class
