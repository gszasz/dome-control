/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

import java.lang.*;
import java.util.Properties;
import java.util.Locale;
import java.util.ArrayList;
import java.io.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.net.UnknownHostException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


class DomeControlIcons {

    public static final ImageIcon minimizedWindow = loadIcon("DomeControl.gif");
    public static final ImageIcon logo = loadIcon("Logo.gif");
    public static final ImageIcon copy = loadIcon("Copy.gif");
    public static final ImageIcon preferences = loadIcon("Preferences.gif");
    public static final ImageIcon notConnected = loadIcon("NotConnected.gif");
    public static final ImageIcon notResponding = loadIcon("NotResponding.gif");
    public static final ImageIcon error = loadIcon("Error.gif");
    public static final ImageIcon autoMode = loadIcon("Auto.gif");
    public static final ImageIcon slewRight = loadIcon("Right.gif");
    public static final ImageIcon slewLeft = loadIcon("Left.gif");
    public static final ImageIcon fastSlewRight = loadIcon("RightFast.gif");
    public static final ImageIcon fastSlewLeft = loadIcon("LeftFast.gif");
    public static final ImageIcon stop = loadIcon("Stop.gif");
    public static final ImageIcon slewTo = loadIcon("SlewTo.gif");
    public static final ImageIcon park = loadIcon("Park.gif");
    public static final ImageIcon calibration = loadIcon("Calibration.gif");
    
    private static ImageIcon loadIcon(String iconFile) {
	return new ImageIcon(ClassLoader.getSystemResource("Icons/" + iconFile));
    }
}

public class DomeControl {

    public static final String VERSION = "0.1.0";
    public static final String COPYRIGHT_YEAR = "2007";
    public static final String COPYRIGHT = "Hlohovec Observatory";
    public static final String AUTHOR_AND_MAINTAINER = "Gabriel Sz\341sz";
    public static final String SUGGESTIONS_AND_TESTING = "Karol Petr\355k, Marek Chrastina & others";
    public static final String SPECIAL_THANKS = "Milan Wudia, Peter Fabo & Zuzana Fischerov\341";

    public static final String DEFAULT_PROPERTIES_FILE = "defaultProperties";
    public static final String CUSTOM_PROPERTIES_FILE = "customProperties";

    private JPanel mainPane, manualMovePane, autoMovePane, trackingPane;
    private SlewToAzimuthField slewToAzimuthField;
    private DomeStatusLine statusLine;

    private JButton slewLeftButton, slewRightButton, stopButton, fastSlewLeftButton, fastSlewRightButton;
    private JButton slewToButton, parkButton;
    private JButton calibrationButton;

    private JComboBox objectComboBox;
    private JCheckBox trackCheckBox, flipCheckBox;

    private JLabel domePositionLabel = new JLabel("N/A");
    private JLabel statusLabel = new JLabel("Not Connected");

    public static AppProperties defaultProps, customProps;

    private static Dome dome;
    private static Mount mount;
    private static DomeMonitor domeMonitor;
    private static Tracking tracking;

    public static Preferences preferences;
    public static Console console;
    public static Visualizer visualizer;
    public static Calibration calibration;

    private ObjectInfo[] catalogObjects;

    private static boolean running;
   
    private void readCatalogFile(String catalogFile) {
	String line;
	String[] entries = new String[ObjectInfo.ENTRY_COUNT];
	ArrayList objectList = new ArrayList();

	try {
	    BufferedReader in = new BufferedReader(new FileReader(catalogFile));

	    while ((line = in.readLine()) != null) {
		DomeControl.console.print(2, "Line: " + line);
		entries = line.split("\\s+");
		DomeControl.console.print(2, "Entries: " + entries[0] + ", " + entries[1] + ", " + entries[2]);
		objectList.add(new ObjectInfo(entries[0], Double.parseDouble(entries[1]), Double.parseDouble(entries[2])));
	    }

	    in.close();
	}
	catch(IOException e) {	    
	    JOptionPane.showMessageDialog(null, e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
	    DomeControl.console.print(0, "Cannot open file '" + catalogFile + "'.");
	}
	catch(Exception e) {
	    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    e.printStackTrace();
	}

	catalogObjects = new ObjectInfo[objectList.size()];
	objectList.toArray(catalogObjects);
    }

    public void loadAppProperties() {
	Properties defaultProps = new Properties();

	try {
	    // Load default application properties    
	    FileInputStream in = new FileInputStream(DEFAULT_PROPERTIES_FILE);
	    defaultProps.load(in);

	    // Load custom application properties
	    Properties props = new Properties(defaultProps);

	    in = new FileInputStream(CUSTOM_PROPERTIES_FILE);
	    props.load(in);
	    in.close();
	    
	    // Save them to Application properties
	    this.defaultProps = new AppProperties(defaultProps);
	    this.customProps = new AppProperties(props);
	}
	catch(IOException e) {	    
	    JOptionPane.showMessageDialog(null, e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
	    running = false;
	    System.exit(0);
	}
	catch(Exception e) {
	    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    e.printStackTrace();
	    running = false;
	    System.exit(0);
	}
    }

    public static void saveAppProperties() {
	try {
	    Properties props = customProps.toProperties();
	    FileOutputStream out = new FileOutputStream(CUSTOM_PROPERTIES_FILE);
	    props.store(out, "--- Dome Control Parameters ---");
	    out.close();	    
	}
	catch(IOException e) {	    
	    JOptionPane.showMessageDialog(null, e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
	}
	catch(Exception e) {
	    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    e.printStackTrace();
	}
    }

    public static void setFromAppProperties() {

	domeMonitor.setDelay(customProps.domeMonitorDelay);

	dome.setFromAppProperties();
	mount.setFromAppProperties();
	//tracking.setFromAppProperties();	
	console.setFromAppProperties();
    }

    public DomeControl() {

	loadAppProperties();

	dome = new Dome();
	mount = new Mount();
	domeMonitor = new DomeMonitor();

	preferences = new Preferences();
	console = new Console();
	visualizer = new Visualizer();
	calibration = new Calibration();

	setFromAppProperties();

	// Start Dome Monitor
	domeMonitor.setDaemon(true);
        domeMonitor.start();

	// Define Main Panel
	mainPane = new JPanel();
	mainPane.setLayout(new BoxLayout(mainPane,  BoxLayout.Y_AXIS));
	mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

	// Define Status Line
	statusLine = new DomeStatusLine();

	// Define Manual Move Panel
	manualMovePane = new JPanel(new GridBagLayout());
        //Add a border around the select panel.
        manualMovePane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Manual Controls"),
            BorderFactory.createEmptyBorder(5,5,5,5)));

	slewLeftButton = new JButton(DomeControlIcons.slewLeft);
	slewLeftButton.setMargin( new Insets ( 0, 0, 0, 0 ));
	slewLeftButton.setToolTipText("Slew Left");
	slewLeftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
		try {
		    dome.setSpeed(1);
		    dome.slewLeft();
		}
		catch(DomeException e) {
		    JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
		}
		return;
            }
        });

	fastSlewLeftButton = new JButton(DomeControlIcons.fastSlewLeft);
	fastSlewLeftButton.setMargin( new Insets ( 3, 0, 3, 0 ));
	fastSlewLeftButton.setToolTipText("Fast Slew Left");
	fastSlewLeftButton.setMnemonic(KeyEvent.VK_LEFT);
	fastSlewLeftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
		try {
 		    dome.setSpeed(2);
		    dome.slewLeft();
		}
		catch(DomeException e) {
		    e.printStackTrace();
		    JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
		}
		return;
            }
        });

	slewRightButton = new JButton(DomeControlIcons.slewRight);
	slewRightButton.setMargin( new Insets ( 3, 0, 3, 0 ));
	slewRightButton.setToolTipText("Slew Right");
	slewRightButton.addActionListener(new ActionListener() {
	   public void actionPerformed(ActionEvent evt) {
	       try {
		   dome.setSpeed(1);
		   dome.slewRight();
	       } catch(DomeException e) {
		   JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
	       }
	       return;
          }
        });

	fastSlewRightButton = new JButton(DomeControlIcons.fastSlewRight);
	fastSlewRightButton.setMargin( new Insets ( 3, 0, 3, 0 ));
	fastSlewRightButton.setToolTipText("Fast Slew Right");
	fastSlewRightButton.setMnemonic(KeyEvent.VK_RIGHT);
	fastSlewRightButton.addActionListener(new ActionListener() {
	   public void actionPerformed(ActionEvent evt) {
	       try {
		   dome.setSpeed(2);
		   dome.slewRight();
	       } catch(DomeException e) {
		   JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
	       }
	       return;
          }
        });

	stopButton = new JButton(DomeControlIcons.stop);
	stopButton.setMargin( new Insets ( 3, 0, 3, 0 ));
	stopButton.setToolTipText("Stop");
	stopButton.setMnemonic(KeyEvent.VK_SPACE);
	stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
	       try {
		   dome.stop();
	       } catch(DomeException e) {
		   JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
	       }
	       return;
            }
        });

	GridBagConstraints c = new GridBagConstraints();
       	c.fill = GridBagConstraints.BOTH;
	c.weightx = 1.0;
	c.gridheight = 1;
	c.gridx = 0;
	manualMovePane.add(fastSlewLeftButton, c);
	manualMovePane.add(slewLeftButton, c);
	c.gridheight = 2;
	c.gridx = 1;
	manualMovePane.add(stopButton, c);
	c.gridheight = 1;
	c.gridx = 2;
	manualMovePane.add(fastSlewRightButton, c);
	manualMovePane.add(slewRightButton, c);

	// Define Automatic Move Panel
	autoMovePane = new JPanel(new GridBagLayout());
	// autoMovePane.setLayout(new BoxLayout(autoMovePane, BoxLayout.Y_AXIS));
        autoMovePane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Automatic Controls"),
            BorderFactory.createEmptyBorder(5,5,5,5)));

	slewToAzimuthField = new SlewToAzimuthField();
       	c.fill = GridBagConstraints.BOTH;
	c.weightx = 1.0;
	c.gridx = GridBagConstraints.RELATIVE;
	autoMovePane.add(new JLabel("<html><b>Slew to <br>Azimuth </b></html>"), c);
	autoMovePane.add(slewToAzimuthField, c);

	// Define Tracking Panel
	SpringLayout trackingPaneLayout = new SpringLayout();
	trackingPane = new JPanel(trackingPaneLayout);
	trackingPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Tracking Controls"),
            BorderFactory.createEmptyBorder(5,5,5,5)));

	// Read Object Catalog file
	readCatalogFile(customProps.catalogFile);
	// Initialize Dome Tracking Thread
	tracking = new Tracking(dome, mount, catalogObjects[0]);
	tracking.start();

	String[] objectList = new String[catalogObjects.length];
	for(int i = 0; i < objectList.length; i++)
	    objectList[i] = catalogObjects[i].getObjectName();

	objectComboBox = new JComboBox(objectList);
	objectComboBox.setToolTipText("Select Observed Object");
	objectComboBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event) {
		    if ("comboBoxChanged".equals(event.getActionCommand())) {
			int selected = objectComboBox.getSelectedIndex();
			tracking.setObject(catalogObjects[selected]);
		    } 
		}
	    });
	trackCheckBox = new JCheckBox("Track", false);
	trackCheckBox.setToolTipText("Track Dome to Object");
	trackCheckBox.setMnemonic(KeyEvent.VK_T);
	trackCheckBox.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    if(e.getStateChange() == ItemEvent.DESELECTED)
			tracking.pause();
		    else if(e.getStateChange() == ItemEvent.SELECTED)
			tracking.unpause();
		}
	    });

	flipCheckBox = new JCheckBox("Scope Flipped", (mount.getFlipState() == Mount.FLIPPED) ? true : false);
	flipCheckBox.setToolTipText("Telescope in flipped position");
	flipCheckBox.setMnemonic(KeyEvent.VK_F);
	flipCheckBox.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    if(e.getStateChange() == ItemEvent.DESELECTED) {
			mount.setFlipState(Mount.NON_FLIPPED);
		        tracking.refresh();
		     }
		    else if(e.getStateChange() == ItemEvent.SELECTED) {
			mount.setFlipState(Mount.FLIPPED);
			tracking.refresh();
		    }
		}
	    });

	calibrationButton = new JButton(DomeControlIcons.calibration);
	calibrationButton.setMargin( new Insets ( 1, 2, 1, 2 ));
	calibrationButton.setToolTipText("Calibrate Tracking");
	calibrationButton.setMnemonic(KeyEvent.VK_C);
	calibrationButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    if(!calibration.isVisible())
		      calibration.show();
		}
	    });

	trackingPane.add(objectComboBox);
	trackingPane.add(trackCheckBox);	
	trackingPane.add(flipCheckBox);
	trackingPane.add(calibrationButton);

	// Definition of SpringLayout constraints for trackingPane.
	// This code is a mess, but it's the only way how to solve
	// this layout without creating subcontainers for each
	// row. One should encapsulate trackingPane into separated
	// class.

	// flipCheckBox relative position (first row, first)
	trackingPaneLayout.putConstraint(SpringLayout.WEST, objectComboBox, 0, SpringLayout.WEST, trackingPane);
	trackingPaneLayout.putConstraint(SpringLayout.NORTH, objectComboBox, 0, SpringLayout.NORTH, trackingPane);
	// trackCheckBox relative position (first row, last)
	trackingPaneLayout.putConstraint(SpringLayout.WEST, trackCheckBox, 5, SpringLayout.EAST, objectComboBox);
	trackingPaneLayout.putConstraint(SpringLayout.NORTH, trackCheckBox, 0, SpringLayout.NORTH, trackingPane);
	trackingPaneLayout.putConstraint(SpringLayout.EAST, trackingPane, 0, SpringLayout.EAST, trackCheckBox);
	// flipCheckBox relative position (last row, first)
	trackingPaneLayout.putConstraint(SpringLayout.WEST, flipCheckBox, 0, SpringLayout.WEST, trackingPane);
	trackingPaneLayout.putConstraint(SpringLayout.NORTH, flipCheckBox, 5, SpringLayout.SOUTH, objectComboBox);
	trackingPaneLayout.putConstraint(SpringLayout.SOUTH, trackingPane, 0, SpringLayout.SOUTH, flipCheckBox);
	// calibrationButton relative position (last row, last)
	trackingPaneLayout.putConstraint(SpringLayout.NORTH, calibrationButton, 5, SpringLayout.SOUTH, trackCheckBox);
	trackingPaneLayout.putConstraint(SpringLayout.SOUTH, trackingPane, 0, SpringLayout.SOUTH, calibrationButton);
	trackingPaneLayout.putConstraint(SpringLayout.EAST, calibrationButton, 0, SpringLayout.EAST, trackingPane);

        // Display the application window.
	mainPane.add(manualMovePane);
	mainPane.add(autoMovePane);
	mainPane.add(trackingPane);
	mainPane.add(statusLine);

	// Announce that application is running
	running = true;
    }

    class SlewToAzimuthField extends JPanel  {
	
	private JFormattedTextField setAzimuthField;
	private NumberFormat setAzimuthFormat;
	private double azimuth;

	public SlewToAzimuthField() {

	    setAzimuthFormat = NumberFormat.getInstance(Locale.US);
	    ((DecimalFormat)setAzimuthFormat).applyPattern("##0");
	    setAzimuthField = new JFormattedTextField(setAzimuthFormat);
	    //	    setAzimuthField.setFont(setAzimuthField.getFont().deriveFont(12.0f));
	    setAzimuthField.setValue(new Double(0.0));
	    setAzimuthField.setColumns(3);
	    setAzimuthField.setHorizontalAlignment(JTextField.RIGHT);
	    setAzimuthField.setInputVerifier(new SetAzimuthVerifier());
	    setAzimuthField.addPropertyChangeListener("value", new PropertyChangeListener() {
	        public void propertyChange(PropertyChangeEvent evt) {
		     Object source = evt.getSource();

		     if (source == setAzimuthField) {
		 
			 try {
			     azimuth = ((Number)setAzimuthFormat.parse(setAzimuthField.getText())).doubleValue();
			     DomeControl.console.print(2, "Set azimuth: " + azimuth);
			     if(azimuth < 0.0 || azimuth > 360.0)
				 throw new ParseException(null, 0);
			     if(azimuth == 360.0) {
				 azimuth = 0.0;
				 setAzimuthField.setText("0");
			     }				 
			     dome.slewToAzimuth(azimuth);
			 }
			 catch (ParseException e) {
			     azimuth = 0.0;
			     setAzimuthField.setText("0");
			     setAzimuthField.selectAll();
			 }
			 catch (DomeException e)  {
			     JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
			 }	
		     }
		}
	    });

	    slewToButton = new JButton(DomeControlIcons.slewTo);
	    slewToButton.setMargin( new Insets ( 2, 2, 2, 2 ));
	    slewToButton.setToolTipText("Slew to Azimuth");
	    slewToButton.setMnemonic(KeyEvent.VK_ENTER);
	    slewToButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    try {
			dome.slewToAzimuth(azimuth);
		    } 	
		    catch(DomeException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
		    }
		    return;
		}
	    });

	    parkButton = new JButton(DomeControlIcons.park);
	    parkButton.setMargin( new Insets ( 2, 2, 2, 2 ));
	    parkButton.setToolTipText("Park");
	    parkButton.setMnemonic(KeyEvent.VK_P);
	    parkButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent evt) {
			try {
			    dome.park();
			    setAzimuthField.setValue(new Double(dome.getZeroAzimuth()));
			} catch(DomeException e) {
			    JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
			}
			return;
		    }
		});
	    
	    setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.BOTH;
	    c.weightx = 1.0;
	    add(setAzimuthField, c);
	    add(slewToButton, c);
	    c.insets = new Insets(0, 5, 0, 0);
	    add(parkButton, c);
	}

	
	class SetAzimuthVerifier extends InputVerifier {
	    double azimuth = 0.0;
	    
	    public boolean verify(JComponent input) {
		//JFormattedTextField setAzimuthField = (JFormattedTextField) input;
		boolean valueOk = true;
		
		try {			
		    azimuth = ((Number)setAzimuthFormat.parse(setAzimuthField.getText())).doubleValue();
		    DomeControl.console.print(2, "Set azimuth: " + azimuth);
		    if(azimuth < 0.0 || azimuth > 360.0)
			throw new ParseException(null, 0);
		    }	
		catch (ParseException e) {
		    valueOk = false;
		}
		
		return valueOk;
	    }
	    
	    public boolean shouldYieldFocus(JComponent input) {
		boolean valueOk = verify(input);

		if(! valueOk) {
		    setAzimuthField.setText("0");
		    setAzimuthField.selectAll();
		}
		else if(azimuth == 360.0)
		    setAzimuthField.setText("0");

		return valueOk;
	    }
    	}
    }

    class DomeStatusLine extends JPanel {

	private JButton preferencesButton;
	private JLabel statusMessage;
	private JLabel connectionStatus, domeMotorStatus, autoStatus, slewStatus;
	private JLabel actualAzimuth;

      	public DomeStatusLine() {

	    // Status Line Layout
	    this.setLayout(new GridBagLayout());
	    this.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));

	    // Initialize Preferences Button
	    preferencesButton = new JButton(DomeControlIcons.preferences);
	    preferencesButton.setToolTipText("Preferences");
	    preferencesButton.setMargin( new Insets ( -5, 3, -5, 3 ));
	    preferencesButton.addActionListener(new ActionListener()  {
	        public void actionPerformed(ActionEvent evt) {
		    if(!preferences.isVisible())
			preferences.show();
		}
	    });
	    
	    // Status Message Panel
	    JPanel statusMessagePanel = new JPanel(new GridLayout(1,1));
	    statusMessagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
	        BorderFactory.createEmptyBorder(0,5,0,0)));

	    // Initialize Status Message
	    statusMessage = new JLabel(" ");
	    statusMessagePanel.add(statusMessage);

	    // Status Tray
	    JPanel statusTray = new JPanel();
	    statusTray.setBorder(BorderFactory.createCompoundBorder(
 		BorderFactory.createLoweredBevelBorder(),
	        BorderFactory.createEmptyBorder(0,0,0,0)));

	    // Connection Status Indicator
	    connectionStatus = new JLabel(DomeControlIcons.notConnected);
	    connectionStatus.setToolTipText("Not Connected");
	    connectionStatus.setVerticalAlignment(SwingConstants.CENTER);
	    connectionStatus.setVisible(true);
	    statusTray.add(connectionStatus);

	    // Dome Status Indicator
	    domeMotorStatus = new JLabel(DomeControlIcons.error);
	    domeMotorStatus.setToolTipText("Dome Error");
	    domeMotorStatus.setVerticalAlignment(SwingConstants.CENTER);
	    domeMotorStatus.setVisible(false);
	    statusTray.add(domeMotorStatus);

	    // Automatic Mode Indicator
	    autoStatus = new JLabel(DomeControlIcons.autoMode);
	    autoStatus.setToolTipText("Automatic Mode");
	    autoStatus.setVisible(false);
	    statusTray.add(autoStatus);

	    // Slew Status Indicator
	    slewStatus = new JLabel(DomeControlIcons.stop);
	    slewStatus.setToolTipText("Stopped");
	    slewStatus.setVisible(false);
	    statusTray.add(slewStatus);

	    // Actual Azimuth
	    actualAzimuth = new JLabel("N/A");
	    //	    actualAzimuth.setHorizontalAlignment(SwingConstants.CENTER);
	    actualAzimuth.setToolTipText("Actual Azimuth");
	    statusTray.add(actualAzimuth);

	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.BOTH;
	    c.weightx = 0.0;
	    add(preferencesButton, c);
	    c.weightx = 1.0;
	    add(statusMessagePanel, c);
	    c.weightx = 0.0;
	    add(statusTray, c);
	}

	public void setMessage(String message)  {
	    if(message != null) {
		statusMessage.setText(message);
		statusMessage.setToolTipText(message);
	    }
	}

	public void setMessage(String message, String longMessage)  {
	    if(message != null)
		statusMessage.setText(message);
	    if(longMessage != null)
		statusMessage.setToolTipText(longMessage);
	}

	public void setAzimuth(double azimuth)  {
	    if(azimuth < 0.0) {
		actualAzimuth.setText("N/A");
		actualAzimuth.setToolTipText("Azimuth Not Available");
	    }
	    else {
		NumberFormat azimuthFormat = NumberFormat.getInstance(Locale.US);
		((DecimalFormat) azimuthFormat).applyPattern("##0");
		actualAzimuth.setText(azimuthFormat.format(azimuth));
		actualAzimuth.setToolTipText("Actual Azimuth");
	    }
	}

	public void setStatus(DomeStatus domeStatus) {  

	    switch(domeStatus.getConnectionState()) {

	    case DomeStatus.NOT_CONNECTED: 
		statusLine.setMessage("Dome Off Line");
		connectionStatus.setIcon(DomeControlIcons.notConnected);
		connectionStatus.setToolTipText("Not Connected");	       
 		autoStatus.setVisible(false);
 		slewStatus.setVisible(false);
 		connectionStatus.setVisible(true);
		return;

	    case DomeStatus.NETWORK_ERROR:
		statusLine.setMessage("Network Error");
		connectionStatus.setIcon(DomeControlIcons.notResponding);
		connectionStatus.setToolTipText("Connection Error");
 		autoStatus.setVisible(false);
 		slewStatus.setVisible(false);
		connectionStatus.setVisible(true);
		return;

	    default: 
		autoStatus.setVisible(false);
		slewStatus.setVisible(true);
		connectionStatus.setVisible(false);
		break;
	    }

	    switch(domeStatus.getDomeState()) {

	    case DomeStatus.ERROR:
		statusLine.setMessage("Dome Error", "<html><b>Dome Error</b><br>Possible malfunction of Frequency Changer.</html>");
		domeMotorStatus.setVisible(true);
		autoStatus.setVisible(false);
		slewStatus.setVisible(false);
		return;

	    case DomeStatus.READY:
	    default:
		domeMotorStatus.setVisible(false);
		break;
	    }

	    switch(domeStatus.getSlewState()) {

	    case DomeStatus.SLEWING_RIGHT:
		statusLine.setMessage("Slewing...");
		slewStatus.setIcon(DomeControlIcons.slewRight);
		slewStatus.setToolTipText("Slewing Right");
		break;

	    case DomeStatus.FAST_SLEWING_RIGHT:
		statusLine.setMessage("Slewing...");
		slewStatus.setIcon(DomeControlIcons.fastSlewRight);
		slewStatus.setToolTipText("Fast Slewing Right");
 		slewStatus.setVisible(true);
		connectionStatus.setVisible(false);
		break;

	    case DomeStatus.SLEWING_LEFT:
		statusLine.setMessage("Slewing...");
		slewStatus.setIcon(DomeControlIcons.slewLeft);
		slewStatus.setToolTipText("Slewing Left");
		break;

	    case DomeStatus.FAST_SLEWING_LEFT:
		statusLine.setMessage("Slewing...");
		slewStatus.setIcon(DomeControlIcons.fastSlewLeft);
		slewStatus.setToolTipText("Fast Slewing Left");
		break;

	    case DomeStatus.STOPPED:
		statusLine.setMessage("Stopped");
		slewStatus.setIcon(DomeControlIcons.stop);
		slewStatus.setToolTipText("Stopped");
		break;

	    }

	    switch(domeStatus.getSlewMode()) {

	    case DomeStatus.AUTO:
		autoStatus.setVisible(true);
		break;

	    default:
		autoStatus.setVisible(false);
		break;
	    }
	}    
    }

    class DomeMonitor extends Thread {
	
	private static final int DEFAULT_DELAY = 1000;

	private String message = "";
	private String longMessage;
	private double domeAzimuth = -1.0;
	private boolean terminated = false;
	private DomeStatus domeStatus = new DomeStatus(DomeStatus.NOT_CONNECTED);
	private int delay;

	public DomeMonitor() {
	    delay = DEFAULT_DELAY;
	}

	public void setDelay(double delay) {
	    this.delay = (int)(delay*1000);
	}

	public void run()  {
	    while(!terminated) {
		try {
		    Thread.sleep(delay);
		    if(! dome.isConnected())
			dome.connect(customProps.host, customProps.port);
		    domeStatus = dome.getStatus();

		    DomeControl.console.print(2, "Connection State: " + domeStatus.getConnectionState());
		    DomeControl.console.print(2, "Dome State: " + domeStatus.getDomeState());
		    DomeControl.console.print(2, "Dome Slew State: " + domeStatus.getSlewState());
		    DomeControl.console.print(2, "Dome Slew Mode: " + domeStatus.getSlewMode());

		    domeAzimuth = dome.getAzimuth();
		}
		catch(ConnectionFailedException e) {
		    message = "Not Connected";
		    domeStatus.setConnectionState(DomeStatus.NOT_CONNECTED);
		}
		catch (NoResponseException e) {
		    message = "Network Error";
		    longMessage = "<html><b>Network Error:</b><br>Dome is not Responding</html>";
		    domeStatus.setConnectionState(DomeStatus.NETWORK_ERROR);
		}
		catch (InvalidResponseException e) {
		    message = "Network Error";
		    longMessage = "<html><b>Network Error:</b><br>Dome is not Responding</html>";
		    domeStatus.setConnectionState(DomeStatus.NETWORK_ERROR);
		}
 		catch (NetworkException e) {
 		    message = "Network Error";
 		    longMessage = "<html><b>Network Error:</b><br>" + e.getMessage() + "</html>";
 		    domeStatus.setConnectionState(DomeStatus.NETWORK_ERROR);
 		}
		catch (DomeException e) {
		    JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
		    running = false;
		    System.exit(0);
		}
		catch (Exception e) {
   		    JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
		    e.printStackTrace();
		    running = false;
		    System.exit(0);
		}
		finally { 
		    updateGUI();
		}
		
	    }
	}

	public void terminate() {
	    terminated = true;
	}

	private void updateGUI() {
	    SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
			statusLine.setMessage(message, longMessage);
			statusLine.setStatus(domeStatus);
			statusLine.setAzimuth(domeAzimuth);
		    }
		});	    
	}
    }

    public static boolean isRunning() {
	return running;
    }

    /**
     * Define Action for Window Closing
     */
    static class AppJFrame extends JFrame {
	public AppJFrame(String title) {
	    super(title);
	}

	public void saveWindowProperties()  {
	    customProps.mainWindowX = getX();
	    customProps.mainWindowY = getY();
	}

	protected void processWindowEvent(WindowEvent evt) {
	    if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
		try {
		    domeMonitor.terminate();
		    tracking.terminate();
		    dome.disconnect();
		} catch(DomeException e) {
		    JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
		}
		finally {
		    // Save window properties
		    saveWindowProperties();   
		    if(preferences.isVisible())
			preferences.saveWindowProperties();
		    if(console.isVisible())
			console.saveWindowProperties();
		    if(visualizer.isVisible())
			visualizer.saveWindowProperties();
		    if(calibration.isVisible())
			calibration.saveWindowProperties();
		    saveAppProperties();
		    running = false;
		    // Exit application
		    System.exit(0);
		}
	    }
	}
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() throws Exception {
	// JFrame.setDefaultLookAndFeelDecorated(true);

        DomeControl app = new DomeControl();
	
        //Create and set up the window.
        AppJFrame appFrame = new AppJFrame("Dome Control");
        appFrame.setContentPane(app.mainPane);
	appFrame.setIconImage(DomeControlIcons.minimizedWindow.getImage());
	
        //Display the window.
        appFrame.pack();
	if(customProps.mainWindowX < 0 || customProps.mainWindowY < 0)
	    appFrame.setLocationRelativeTo(null);
	else
	    appFrame.setLocation(customProps.mainWindowX, customProps.mainWindowY);
        appFrame.setResizable(false);
        appFrame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
		try {
		    createAndShowGUI();
		} 
		catch(Exception e) {
		    e.printStackTrace();
		}
            }
        });
    }
}
