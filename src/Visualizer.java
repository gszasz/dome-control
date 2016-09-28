/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Visualizer extends JFrame {

    private AppProperties ap = DomeControl.customProps;

    private JPanel mainPane;

    public Visualizer() {
	super("Visualizer");

	mainPane = new JPanel();
        mainPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	mainPane.add(new JLabel("Visualizer not implemented yet"));

	setContentPane(mainPane);
	setIconImage(DomeControlIcons.minimizedWindow.getImage());

	pack();
	if(ap.visualizerWindowX < 0 || ap.visualizerWindowY < 0)
	    setLocationRelativeTo(null);
	else
	    setLocation(ap.visualizerWindowX, ap.visualizerWindowY);

	if(ap.showVisualizer)
	    show();
    }

    public void saveWindowProperties() {
	ap.visualizerWindowX = getX();
	ap.visualizerWindowY = getY();
    }

    // Define action on closing of the Visualizer Window
    protected void processWindowEvent(WindowEvent evt) {
	if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
	    if(DomeControl.isRunning()) {
		ap.showVisualizer = false;
		saveWindowProperties();
		DomeControl.saveAppProperties();
		DomeControl.preferences.setShowVisualizerCheckBox(false);
	    }
	    if(DomeControl.preferences.isVisible())
		hide();
	    else



		dispose();
	}
    }
}
