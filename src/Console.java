/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

class Console extends JFrame {

    // Types of Messages
    public static final int NORMAL_MESSAGE = 0;
    public static final int WARNING = 1;
    public static final int ERROR_MESSAGE = 2;
    public static final int POSITIVE_RESPONSE = 3;
    
    private AppProperties ap = DomeControl.customProps;

    private JPanel mainPane, bottomToolbarPane, debugLevelRadioButtonPane;
    private JScrollPane messageScrollPane;
    private JTextPane messagePane;
    private StyledDocument messageDocument;
    private JButton copyButton;
    private JRadioButton debugLevel0RadioButton, debugLevel1RadioButton, debugLevel2RadioButton;
    private ButtonGroup debugLevelButtonGroup;
    private int debugLevel = 0;

    public Console() {

	super("Console");

	mainPane = new JPanel(new GridBagLayout());
	mainPane.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

	messagePane = new JTextPane();
	messagePane.setEditable(false);

	messageDocument = messagePane.getStyledDocument();
        addStylesToDocument(messageDocument);	

	messageScrollPane = new JScrollPane(messagePane,
		    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	messageScrollPane.setBorder(BorderFactory.createEmptyBorder(4,4,4,2));
	
	bottomToolbarPane = new JPanel(new BorderLayout());
	bottomToolbarPane.setBorder(BorderFactory.createEmptyBorder(4,4,2,4));

	copyButton = new JButton(new CopyToClipboardAction());
	copyButton.setText(null);
	copyButton.setIcon(DomeControlIcons.copy);
	copyButton.setToolTipText("Copy to Clipboard");
	copyButton.setMargin(new Insets ( -5, 3, -5, 3 ));

	debugLevelRadioButtonPane = new JPanel();

	debugLevel0RadioButton = new JRadioButton("0");
	debugLevel0RadioButton.setToolTipText("Basic Messages");
	debugLevel0RadioButton.setSelected(true);
	debugLevel0RadioButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      debugLevel = 0;
   	   }
	});
	debugLevel1RadioButton = new JRadioButton("1");
	debugLevel1RadioButton.setToolTipText("Advanced Messages");
	debugLevel1RadioButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      debugLevel = 1;
   	   }
	});
	debugLevel2RadioButton = new JRadioButton("2");
	debugLevel2RadioButton.setToolTipText("All Debugging Messages");
	debugLevel2RadioButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      debugLevel = 2;
   	   }
	});

	debugLevelButtonGroup = new ButtonGroup();
	debugLevelButtonGroup.add(debugLevel0RadioButton);
        debugLevelButtonGroup.add(debugLevel1RadioButton);
	debugLevelButtonGroup.add(debugLevel2RadioButton);

	debugLevelRadioButtonPane.add(new JLabel("Debug Level"));
	debugLevelRadioButtonPane.add(debugLevel0RadioButton);
	debugLevelRadioButtonPane.add(debugLevel1RadioButton);
	debugLevelRadioButtonPane.add(debugLevel2RadioButton);

	bottomToolbarPane.add(copyButton, BorderLayout.LINE_START);
	bottomToolbarPane.add(debugLevelRadioButtonPane, BorderLayout.LINE_END);

	GridBagConstraints c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.fill = GridBagConstraints.BOTH;
	c.weightx = 1.0;
	c.weighty = 1.0;
	mainPane.add(messageScrollPane, c);
	c.weighty = 0.0;
	mainPane.add(bottomToolbarPane, c);

	mainPane.setOpaque(true);
	setContentPane(mainPane);
	setIconImage(DomeControlIcons.minimizedWindow.getImage());

	// Generate the window
	pack();
	if(ap.consoleWindowWidth > 0 || ap.consoleWindowHeight > 0)
	    setSize(ap.consoleWindowWidth, ap.consoleWindowHeight);
	if(ap.consoleWindowX < 0 || ap.consoleWindowY < 0)
	    setLocationRelativeTo(null);
	else
	    setLocation(ap.consoleWindowX, ap.consoleWindowY);

	if(ap.showConsole)
	    show();
    }

    protected void addStylesToDocument(StyledDocument document)  {

        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style regular = document.addStyle("regular.black", def);

        Style bold = document.addStyle("bold.black", regular);
        StyleConstants.setBold(bold, true);

	Style style = document.addStyle("regular.blue", regular);
        StyleConstants.setForeground(style, Color.BLUE);

	style = document.addStyle("regular.red", regular);
        StyleConstants.setForeground(style, Color.RED);

	style = document.addStyle("regular.green", regular);
        StyleConstants.setForeground(style, Color.GREEN.darker());

	style = document.addStyle("bold.blue", bold);
        StyleConstants.setForeground(style, Color.BLUE);

	style = document.addStyle("bold.red", bold);
        StyleConstants.setForeground(style, Color.RED);

	style = document.addStyle("bold.green", bold);
        StyleConstants.setForeground(style, Color.GREEN.darker());
    }

    public void setFromAppProperties() {
	setVisible(ap.showConsole);
    }

    public void print(String message) {
	print(0, message, Console.NORMAL_MESSAGE);
    }

    public void print(String message, int type) {
	print(0, message, type);
    }

    public void print(int debugLevel, String message) {
	print(debugLevel, message, Console.NORMAL_MESSAGE);
    }

    public void print(int debugLevel, String message, int type) {
	if(debugLevel <= this.debugLevel) {
	    Date date = new Date();
	    SimpleDateFormat df = new SimpleDateFormat("MMM dd hh:mm:ss:", Locale.US);

	    try {

		String foregroundColorString = "black";

		if(type == Console.WARNING)
		    foregroundColorString = "blue";
		else if(type == Console.ERROR_MESSAGE)
		    foregroundColorString = "red";
		else if(type == Console.POSITIVE_RESPONSE)
		    foregroundColorString = "green";
	
		Style style = messageDocument.getStyle("regular." + foregroundColorString);

		if(messageDocument.getLength() > 0)
		    messageDocument.insertString(messageDocument.getLength(), "\n", style);

		messageDocument.insertString(messageDocument.getLength(), df.format(date), style);

		if(debugLevel < 2)
		    style = messageDocument.getStyle("bold." + foregroundColorString);

		messageDocument.insertString(messageDocument.getLength(), " " + message, style);

		messagePane.setCaretPosition(messageDocument.getLength());

	    } catch (BadLocationException e) {
		System.err.println("Could not display message on Console.");
		e.printStackTrace();
	    }
	}
    }

    public int getDebugMode() {
	return debugLevel;
    }

    public void setDebugMode(int debugLevel) {
	this.debugLevel = debugLevel;
    }

    class CopyToClipboardAction extends DefaultEditorKit.CopyAction {

 	public void actionPerformed(ActionEvent e) {
	    int selectionStart = messagePane.getSelectionStart();
	    int selectionEnd = messagePane.getSelectionEnd();

	    if(selectionStart == selectionEnd)
		messagePane.selectAll();

	    super.actionPerformed(e);

	    messagePane.setSelectionStart(selectionStart);
	    messagePane.setSelectionEnd(selectionEnd);
	}

    }

    public void saveWindowProperties() {
	ap.consoleWindowX = getX();
	ap.consoleWindowY = getY();
	ap.consoleWindowWidth = getWidth();
	ap.consoleWindowHeight = getHeight();
    }

    // Define action on closing of the Console Window
    protected void processWindowEvent(WindowEvent evt) {
	if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
	    if(DomeControl.isRunning()) {
		ap.showConsole = false;
		saveWindowProperties();
		DomeControl.saveAppProperties();
		DomeControl.preferences.setShowConsoleCheckBox(false);
	    }
	    if(DomeControl.preferences.isVisible())
		hide();
	    else
		dispose();
	}
    }

    // Doesn't work.  Console class is not standalone anymore.
    public static void main(String[] args) {

	Console console = new Console();
	console.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	console.show();
	
	try {
	    for(int i=0; i<100; i++) {
		console.print(1, i +": Message");
		Thread.sleep(500);
	    }
	}
	catch(Exception e) {  }
    }
}
