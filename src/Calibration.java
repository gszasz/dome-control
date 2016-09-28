/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Calibration extends JFrame {

    private AppProperties ap = DomeControl.customProps;

    private JPanel mainPane;

    public Calibration() {
	super("Calibration");

	mainPane = new JPanel();
        mainPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	mainPane.add(new JLabel("Calibration not implemented yet"));

	setContentPane(mainPane);
	setIconImage(DomeControlIcons.minimizedWindow.getImage());

	pack();
	if(ap.calibrationWindowX < 0 || ap.calibrationWindowY < 0)
	    setLocationRelativeTo(null);
	else
	    setLocation(ap.calibrationWindowX, ap.calibrationWindowY);
	setResizable(false);
	setVisible(false);
    }

    public void saveWindowProperties() {
	ap.calibrationWindowX = getX();
	ap.calibrationWindowY = getY();
    }

    // Define action on closing of the Visualizer Window
    protected void processWindowEvent(WindowEvent evt) {
	if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
	    if(DomeControl.isRunning()) {
		saveWindowProperties();
		DomeControl.saveAppProperties();
	    }
	    dispose();
	}
    }
}
