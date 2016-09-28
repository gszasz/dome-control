/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

import java.lang.*;
import java.util.Locale;
import java.io.File;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

class Preferences extends JFrame {    

    private static final String GENERAL_TAB = "General";
    private static final String TRACKING_TAB = "Tracking";

    private AppProperties ap = DomeControl.customProps;
    private AppProperties customProps;

    private JPanel mainPane = new JPanel();
    private JPanel genPane = new JPanel();
    private JPanel trackPane = new JPanel();
    private NetworkPanel networkPane;
    private AlertPanel alertPane;
    private MonitorPanel monitorPane;
    private MiscPanel miscPane;
    private CreditsPanel creditsPane;
    private ObjectParametersPanel objectParPane;
    private CoordinatesPanel coordPane;
    private DomeParametersPanel domeParPane;
    private MountParametersPanel mountParPane;
    private ScopeParametersPanel scopeParPane;
    private TrackingParametersPanel trackParPane;
    private JPanel buttonPane = new JPanel();

    private JButton okButton, applyButton, cancelButton;

    public Preferences() {

	super("Preferences");

	// Inicialization of application properties
	customProps = new AppProperties(DomeControl.customProps);
	networkPane = new NetworkPanel();
	monitorPane = new MonitorPanel();
	alertPane = new AlertPanel();
	miscPane = new MiscPanel();
	creditsPane = new CreditsPanel();
	objectParPane = new ObjectParametersPanel();
	coordPane = new CoordinatesPanel();
	domeParPane = new DomeParametersPanel();
	mountParPane = new MountParametersPanel();
	scopeParPane = new ScopeParametersPanel();
	trackParPane = new TrackingParametersPanel();

	JTabbedPane tabbedPane = new JTabbedPane();

	okButton = new JButton("OK");
	// okButton.setMargin( new Insets ( 0, 0, 0, 0 ));
	okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
		try {
		    saveCustomProperties();
		    DomeControl.console.print(2, "Aplication properties: " + ap);
		    disposeHiddenWindows();
		    dispose();
		}
		catch(Exception e) {		   
		    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return;
            }
        });

	applyButton = new JButton("Apply");
	// applyButton.setMargin( new Insets ( 0, 0, 0, 0 ));
	applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
		try {
		    saveCustomProperties();
		    DomeControl.console.print(2, "Aplication properties: " + ap);
		    disposeHiddenWindows();
		}
		catch(Exception e) {		   
		    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return;
            }
        });

	cancelButton = new JButton("Close");
	// cancelButton.setMargin( new Insets ( 0, 0, 0, 0 ));
	cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
		try {		  
		    saveWindowProperties();
		    DomeControl.saveAppProperties();
		    disposeHiddenWindows();
		    dispose();
		}
		catch(Exception e) {		   
		    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return;
            }
        });

	buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	buttonPane.add(Box.createHorizontalGlue());
	buttonPane.add(okButton);
        buttonPane.add(Box.createRigidArea(new Dimension(5, 0)));
	buttonPane.add(applyButton);
        buttonPane.add(Box.createRigidArea(new Dimension(5, 0)));
	buttonPane.add(cancelButton);

	genPane = new JPanel(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	c.anchor = GridBagConstraints.PAGE_START;
	c.fill = GridBagConstraints.BOTH;
	c.weightx = 1.0;
	c.gridy = 0;
	c.gridwidth = 2;
	genPane.add(networkPane, c);
	c.gridy = 1;
	genPane.add(alertPane, c);
	c.gridy = 2;
	c.gridwidth = 1;
	genPane.add(monitorPane, c);
	genPane.add(miscPane, c);
	c.gridy = 3;
	c.gridwidth = 2;
	genPane.add(creditsPane, c);

	trackPane = new JPanel(new GridBagLayout());
	c = new GridBagConstraints();
	c.fill = GridBagConstraints.BOTH;
	c.weightx = 1.0;
	c.gridwidth = GridBagConstraints.REMAINDER;
	trackPane.add(objectParPane, c);
	trackPane.add(trackParPane, c);
	c.gridwidth = GridBagConstraints.RELATIVE;
	trackPane.add(scopeParPane, c);
	c.gridwidth = GridBagConstraints.REMAINDER;
	trackPane.add(coordPane, c);
	c.gridwidth = GridBagConstraints.RELATIVE;
	trackPane.add(domeParPane, c);
	c.gridwidth = GridBagConstraints.REMAINDER;
	trackPane.add(mountParPane, c);

	tabbedPane.add(GENERAL_TAB, genPane);
        tabbedPane.add(TRACKING_TAB, trackPane);

	mainPane.setLayout(new BorderLayout());
	mainPane.add(tabbedPane, BorderLayout.PAGE_START);
	mainPane.add(buttonPane, BorderLayout.PAGE_END);

	setContentPane(mainPane);
	setIconImage(DomeControlIcons.minimizedWindow.getImage());
		
	// Display the window
	pack();
	if(ap.preferencesWindowX < 0 || ap.preferencesWindowY < 0)
	    setLocationRelativeTo(null);
	else
	    setLocation(ap.preferencesWindowX, ap.preferencesWindowY);
	setResizable(false);
	setVisible(false);
    }

    public void saveCustomProperties() {

	saveWindowProperties();

	// Get custom preferences from particular panels
	networkPane.setAppProperties(customProps);
	alertPane.setAppProperties(customProps);
	monitorPane.setAppProperties(customProps);
	miscPane.setAppProperties(customProps);
        objectParPane.setAppProperties(customProps);
	coordPane.setAppProperties(customProps);
	domeParPane.setAppProperties(customProps);
	mountParPane.setAppProperties(customProps);
	scopeParPane.setAppProperties(customProps);
        trackParPane.setAppProperties(customProps);
	
	// Update global application properties
	ap.setFromAppProperties(customProps);
	// Refresh application
	DomeControl.setFromAppProperties();
	// Save properties to file
	DomeControl.saveAppProperties();
    }

    public void setShowVisualizerCheckBox(boolean selected) {
	miscPane.showVisualizerCheckBox.setSelected(selected);
    }

    public void setShowConsoleCheckBox(boolean selected) {
	miscPane.showConsoleCheckBox.setSelected(selected);
    }

    class LoadFileField extends JPanel {
	
	private File file;
	private JFileChooser fileChooser = new JFileChooser();
	private JTextField fileInputField;
	private JButton browseButton;

	public LoadFileField() {
	    this("");
	}

	public LoadFileField(String defaultPath) {
	    
	    fileInputField = new JTextField(defaultPath);
	    fileInputField.setColumns(20);
	    
	    browseButton = new JButton("Browse");
	    // browseButton.setMargin( new Insets ( 0, 0, 0, 0 ));
	    browseButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent evt) {
			try {
			    if(fileChooser.showOpenDialog(LoadFileField.this) == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();
				fileInputField.setText(file.getPath());
			    }
			}
			catch(Exception e) {		   
			    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			return;
		    }
		});

	    setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.BOTH;

	    add(fileInputField, c);
	    add(browseButton, c);
	}

	String getPath() {

	    return fileInputField.getText();
	}
    }

    class ObjectParametersPanel extends JPanel {

	private LoadFileField catalogFileField;

	public ObjectParametersPanel() {
	    setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    setBorder(BorderFactory.createCompoundBorder(
	      BorderFactory.createTitledBorder("Object Parameters"),
              BorderFactory.createEmptyBorder(3,5,5,0)));

	    catalogFileField = new LoadFileField(customProps.catalogFile);

	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.weightx = 1.0;
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    add(new JLabel("Catalog File"), c);
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    add(catalogFileField, c);
	}

	public void setAppProperties(AppProperties appProps) {

	    appProps.catalogFile = catalogFileField.getPath();
	}
    }

    class CoordinatesPanel extends JPanel {

	private CoordinatesField longitudeField, latitudeField;

	public CoordinatesPanel() {
	    setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    setBorder(BorderFactory.createCompoundBorder(
	      BorderFactory.createTitledBorder("Observatory Coordinates"),
              BorderFactory.createEmptyBorder(3,5,0,0)));

	    longitudeField = new CoordinatesField(customProps.longitude, -180.0, 180.0);
	    latitudeField = new CoordinatesField(customProps.latitude, -90.0, 90.0);

	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.weightx = 1.0;
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    add(new JLabel("Longitude"), c);
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    add(longitudeField, c);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    add(new JLabel("Latitude"), c);
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    add(latitudeField, c);
	}

	public void setAppProperties(AppProperties appProps) {
	    try {
		appProps.longitude = longitudeField.getValue();
		appProps.latitude = latitudeField.getValue();
	    }
	    catch (ParseException e) {
		JOptionPane.showMessageDialog(null, "Invalid entry in Observatory Coordinates", "Parse Error", JOptionPane.ERROR_MESSAGE);
	    }
	}
    }

    class CoordinatesField extends JPanel implements PropertyChangeListener {

	private JFormattedTextField degField, minField, secField;
	private NumberFormat degFormat, minFormat, secFormat;
	private Coordinate value, defaultValue;
	private double minValue, maxValue;
	private boolean noMinValue = false;
	private boolean noMaxValue = false;
	private CoordinatesFieldVerifier degFieldVerifier, minFieldVerifier, secFieldVerifier;
	private Color normalColor, errorColor;


	public CoordinatesField() {
	    this(0.0, 0.0, 0.0);    
	}

	public CoordinatesField(double value) {
	    this(value, 0.0, 0.0);	    
	}

	public CoordinatesField(double value, double minValue) {
	    this(value, minValue, 0.0);
       	    noMaxValue = true;
	    maxValue = minValue+1;
	}


	public CoordinatesField(double value, double minValue, double maxValue) {

	    this.defaultValue = new Coordinate(value);
	    this.value = new Coordinate(value);
	    this.minValue = minValue;
	    this.maxValue = maxValue;

	    degFormat = NumberFormat.getInstance(Locale.US);
	    degFormat.setParseIntegerOnly(true);
	    ((DecimalFormat)degFormat).applyPattern("#00");

	    degField = new JFormattedTextField(degFormat);
	    degField.setText(degFormat.format(this.value.deg));
	    degField.setColumns(3);
	    degField.addPropertyChangeListener("value", this);
	    degFieldVerifier = new CoordinatesFieldVerifier();
	    degField.setInputVerifier(degFieldVerifier);

	    normalColor = degField.getSelectionColor();
	    errorColor = new Color(255, 203, 203);

	    minFormat = NumberFormat.getInstance(Locale.US);
	    minFormat.setParseIntegerOnly(true);
	    minFormat.setMaximumIntegerDigits(2);

	    minField = new JFormattedTextField(minFormat);
	    minField.setText(minFormat.format(this.value.min));
	    minField.setColumns(2);
	    minField.addPropertyChangeListener("value", this);
	    minFieldVerifier = new CoordinatesFieldVerifier();
	    minField.setInputVerifier(minFieldVerifier);

	    secFormat = NumberFormat.getInstance(Locale.US);
	    ((DecimalFormat)secFormat).applyPattern("#0.00#");

	    secField = new JFormattedTextField(secFormat);
	    secField.setText(secFormat.format(this.value.sec));
	    secField.setColumns(4);
	    secField.addPropertyChangeListener("value", this);
	    secFieldVerifier = new CoordinatesFieldVerifier();
	    secField.setInputVerifier(secFieldVerifier);

	    add(degField);
	    add(minField);
	    add(secField);
	}

	public double getValue() throws ParseException {

	    return value.toDegrees();
	}

	public void propertyChange(PropertyChangeEvent evt) {

	    Object source = evt.getSource();
	    double degValue = 0.0;

	    if (source != degField && source != minField && source != secField)
		return;

	    try {

		if (source == degField) {
		    value.deg = ((Number)degFormat.parse(degField.getText())).intValue();
		    degField.setText(degFormat.format(value.deg));
		}
		else if (source == minField) {
		    value.min = ((Number)minFormat.parse(minField.getText())).intValue();
		    minField.setText(minFormat.format(value.min));
		}
		else if (source == secField) {
		    value.sec = ((Number)secFormat.parse(secField.getText())).doubleValue();
		    secField.setText(secFormat.format(value.sec));
		}

		degValue = value.toDegrees();

		boolean valueOk;

		if(noMinValue)
		    valueOk = (degValue > maxValue) ? false : true;
		else if(noMaxValue)
		    valueOk = (degValue < minValue) ? false : true;
		else
		    valueOk = (degValue < minValue || degValue > maxValue) ? false : true;
		
		if(!valueOk)
		    throw new ParseException(null, 0);

		if (source == degField && degField.getSelectionColor() == errorColor)
		    degField.setSelectionColor(normalColor);
		else if (source == minField && degField.getSelectionColor() == errorColor)
		    minField.setSelectionColor(normalColor);
		else if (source == secField && degField.getSelectionColor() == errorColor) 
		    secField.setSelectionColor(normalColor);
	    }
	    catch (ParseException e) {
		if (source == degField) {
		    degField.setSelectionColor(errorColor);
		    degField.selectAll();
		}
		else if (source == minField) {
		    minField.setSelectionColor(errorColor);
		    minField.selectAll();
		}
		else if (source == secField) {
		    secField.setSelectionColor(errorColor);
		    secField.selectAll();
		}

		Coordinate maxCoordinate = new Coordinate(maxValue);
		Coordinate minCoordinate = new Coordinate(minValue);
		
		if(noMinValue)
		    JOptionPane.showMessageDialog(null, "Please enter a value below " + maxCoordinate + ".", "Parse Error", JOptionPane.ERROR_MESSAGE);
		else if(noMaxValue)
		    JOptionPane.showMessageDialog(null, "Please enter a value above " + minCoordinate + ".", "Parse Error", JOptionPane.ERROR_MESSAGE);
		else
		    JOptionPane.showMessageDialog(null, "Please enter a value between " + minCoordinate + " and " + maxCoordinate +".", "Parse Error", JOptionPane.ERROR_MESSAGE);

		if (source == degField)
		    degField.setText(degFormat.format(defaultValue.deg));
		else if (source == minField)
		    minField.setText(minFormat.format(defaultValue.min));
		else if (source == secField)
		    secField.setText(secFormat.format(defaultValue.sec));
		
	    }
	    catch (Exception e)  {
		e.printStackTrace();
		JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}

	class CoordinatesFieldVerifier extends InputVerifier {

	    private Coordinate value;
	    private JFormattedTextField degField, minField, secField;

	    public CoordinatesFieldVerifier() {

		super();
		value = new Coordinate(CoordinatesField.this.value);
		degField = CoordinatesField.this.degField;
		minField = CoordinatesField.this.minField;
		secField = CoordinatesField.this.secField;
		degFormat = CoordinatesField.this.degFormat;
		minFormat = CoordinatesField.this.minFormat;
		secFormat = CoordinatesField.this.secFormat;
	    }


	    public boolean verify(JComponent input) {

		boolean valueOk = true;
		double degValue = 0.0;

		if(minValue < maxValue) {
		    try {			
			
			value = new Coordinate(CoordinatesField.this.value);

			if(this == CoordinatesField.this.degFieldVerifier) {
			    value.deg = ((Number)degFormat.parse(degField.getText())).intValue();
			    degField.setText(degFormat.format(value.deg));
			}
			else if(this == CoordinatesField.this.minFieldVerifier) {
			    value.min = ((Number)minFormat.parse(minField.getText())).intValue();
			    minField.setText(minFormat.format(value.min));
			}
			else if(this == CoordinatesField.this.secFieldVerifier) {
			    value.sec = ((Number)secFormat.parse(secField.getText())).doubleValue();
			    secField.setText(secFormat.format(value.sec));
		}

			degValue = value.toDegrees();
			
			if(noMinValue)
			    valueOk = (degValue > maxValue) ? false : true;
			else if(noMaxValue)
			    valueOk = (degValue < minValue) ? false : true;
			else
			    valueOk = (degValue < minValue || degValue > maxValue) ? false : true;

		    }	
		    catch (ParseException e) {
			valueOk = false;
		    }
		}
		
		return valueOk;
	    }
	    
	    public boolean shouldYieldFocus(JComponent input) {

		boolean valueOk = verify(input);
	       
		if(! valueOk) {
		    if (this == CoordinatesField.this.degFieldVerifier) {
			degField.setSelectionColor(CoordinatesField.this.errorColor);
			degField.selectAll();
		    }
		    else if (this == CoordinatesField.this.minFieldVerifier) {
			minField.setSelectionColor(CoordinatesField.this.errorColor);
			minField.selectAll();
		    }
		    else if (this == CoordinatesField.this.secFieldVerifier) {
			secField.setSelectionColor(CoordinatesField.this.errorColor);
			secField.selectAll();
		    }		    
		}
		else {
		    if (this == CoordinatesField.this.degFieldVerifier && 
                        degField.getSelectionColor() == CoordinatesField.this.errorColor)
			degField.setSelectionColor(CoordinatesField.this.normalColor);
		    else if (this == CoordinatesField.this.minFieldVerifier && 
			     minField.getSelectionColor() == CoordinatesField.this.errorColor)
			minField.setSelectionColor(CoordinatesField.this.normalColor);
		    else if (this == CoordinatesField.this.secFieldVerifier &&
			     secField.getSelectionColor() == CoordinatesField.this.errorColor)
			secField.setSelectionColor(CoordinatesField.this.normalColor);		   
		}		
		return valueOk;
	    }
    	}
    }

    class NetworkPanel extends JPanel {
	
	private JPanel hostPortPane, leftColumnPane, middleColumnPane, rightColumnPane;
	private JTextField hostField;
	private ParameterField portField, packetLengthField, packetTimeoutField;
	private JCheckBox simulatorCheckBox;

	public NetworkPanel() {

	    setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    setBorder(BorderFactory.createTitledBorder("Network Connection"));

	    NumberFormat parameterFormat = NumberFormat.getInstance(Locale.US);
	    ((DecimalFormat)parameterFormat).applyPattern("#0");

	    hostField = new JTextField(customProps.host);
	    hostField.setColumns(20);
	    portField = new ParameterField((double) customProps.port, parameterFormat);
	    portField.setColumns(4);

	    hostPortPane = new JPanel(new GridBagLayout());
	    hostPortPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 6));
	    c.weightx = 0.3;
	    c.fill = GridBagConstraints.BOTH;
	    c.gridx = 0;
	    hostPortPane.add(new JLabel("Host"), c);
	    c.gridx = 1;
	    c.weightx = 1.0;
	    hostPortPane.add(hostField, c);
	    c.gridx = 2;
	    c.weightx = 0.2;
	    c.insets = new Insets(0, 10, 0, 0);
	    hostPortPane.add(new JLabel("Port"), c);
	    c.gridx = 3;
	    c.insets = new Insets(0, 0, 0, 0);
	    hostPortPane.add(portField, c);

	    simulatorCheckBox = new JCheckBox("Simulator");
	    simulatorCheckBox.setSelected(customProps.simulator);
	    packetLengthField = new ParameterField((double)customProps.packetLength, parameterFormat, 0.0);
	    packetLengthField.setColumns(4);
	    packetTimeoutField = new ParameterField((double)customProps.packetTimeout, parameterFormat, 0.0);
	    packetTimeoutField.setColumns(4);

    	    leftColumnPane = new JPanel(); 
	    leftColumnPane.setBorder(BorderFactory.createEmptyBorder(3, -11, 0, 0));
	    leftColumnPane.add(simulatorCheckBox);

	    int numPairs;

	    middleColumnPane = new JPanel(new SpringLayout());
	    middleColumnPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
	    middleColumnPane.add(new JLabel("Packet Length "));
	    middleColumnPane.add(packetLengthField);

	    numPairs = middleColumnPane.getComponentCount() / 2;
	    SpringUtilities.makeCompactGrid(middleColumnPane, numPairs, 2, 5, 8, 7, 4);

	    rightColumnPane = new JPanel(new SpringLayout());	    
	    rightColumnPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
	    rightColumnPane.add(new JLabel("Packet Timeout "));
	    rightColumnPane.add(packetTimeoutField);

	    numPairs = rightColumnPane.getComponentCount() / 2;
	    SpringUtilities.makeCompactGrid(rightColumnPane, numPairs, 2, 5, 8, 7, 4);

	    c.fill = GridBagConstraints.BOTH;
	    c.gridy = 0;
	    c.gridx = 0;
	    c.gridwidth = 3;
	    add(hostPortPane, c);
	    c.gridwidth = 1;
	    c.gridy = 1;
	    c.insets = new Insets(0, 0, 0, 0);
	    add(leftColumnPane, c);
	    c.gridx = 1;
	    c.insets = new Insets(0, 12, 0, 0);
	    add(middleColumnPane, c);
	    c.gridx = 2;
	    c.insets = new Insets(0, 17, 0, 0);
	    add(rightColumnPane, c);
	}

	public void setAppProperties(AppProperties appProps) {
 	    appProps.host = hostField.getText();
 	    appProps.port = ((Number)portField.getValue()).intValue();
	    appProps.simulator = simulatorCheckBox.isSelected();
 	    appProps.packetLength = ((Number)packetLengthField.getValue()).intValue();
 	    appProps.packetTimeout = ((Number)packetTimeoutField.getValue()).intValue();
	}
    }

    class AlertPanel extends JPanel {
	private JCheckBox domeErrorAlertCheckBox, netErrorAlertCheckBox, errorAlertCheckBox;
	private LoadFileField domeErrorAlertFileField, netErrorAlertFileField, errorAlertFileField;

	public AlertPanel() {

	    setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    setBorder(BorderFactory.createTitledBorder("Alerts"));

	    domeErrorAlertCheckBox = new JCheckBox("Dome Error Alert");
	    domeErrorAlertCheckBox.setSelected(customProps.domeErrorAlert);
	    domeErrorAlertFileField = new LoadFileField();
	    netErrorAlertCheckBox = new JCheckBox("Network Error Alert");
	    netErrorAlertCheckBox.setSelected(customProps.netErrorAlert);
	    netErrorAlertFileField = new LoadFileField();
	    errorAlertCheckBox = new JCheckBox("Error Alert");
	    errorAlertCheckBox.setSelected(customProps.errorAlert);
	    errorAlertFileField = new LoadFileField();

	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.weightx = 1.0;
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    add(domeErrorAlertCheckBox, c);
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    add(domeErrorAlertFileField, c);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    add(netErrorAlertCheckBox, c);
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    add(netErrorAlertFileField, c);
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    add(errorAlertCheckBox, c);
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    add(errorAlertFileField, c);
	}

	public void setAppProperties(AppProperties appProps) {
	    appProps.domeErrorAlert = domeErrorAlertCheckBox.isSelected();
	    appProps.domeErrorAlertFile = domeErrorAlertFileField.getPath();
	    appProps.netErrorAlert = netErrorAlertCheckBox.isSelected();
	    appProps.netErrorAlertFile = netErrorAlertFileField.getPath();
            appProps.errorAlert = errorAlertCheckBox.isSelected();
            appProps.errorAlertFile = errorAlertFileField.getPath();
	}

    }

    class MonitorPanel extends JPanel {
	
	private ParameterField domeMonitorDelayField, zeroAzimuthField;

	public MonitorPanel() {

	    setLayout(new SpringLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    setBorder(BorderFactory.createTitledBorder("Dome Monitor"));

	    NumberFormat parameterFormat = NumberFormat.getInstance(Locale.US);
	    ((DecimalFormat)parameterFormat).applyPattern("#0.00#");
	    domeMonitorDelayField = new ParameterField(customProps.domeMonitorDelay, parameterFormat, 0.0);
	    domeMonitorDelayField.setColumns(3);
	    NumberFormat intParameterFormat = NumberFormat.getInstance(Locale.US);
	    ((DecimalFormat)intParameterFormat).applyPattern("#0");
	    zeroAzimuthField = new ParameterField((double) customProps.zeroAzimuth, intParameterFormat, 0.0, 359.0);
	    zeroAzimuthField.setColumns(3);

	    add(new JLabel("Monitor Loop Delay         "));
	    add(domeMonitorDelayField);

	    add(new JLabel("Reference Azimuth          "));
	    add(zeroAzimuthField);

	    int numPairs = this.getComponentCount() / 2;
	    SpringUtilities.makeCompactGrid(this, numPairs, 2, 5, 8, 7, 4);
	}

	public void setAppProperties(AppProperties appProps) {
 	    appProps.domeMonitorDelay = ((Number)domeMonitorDelayField.getValue()).doubleValue();
 	    appProps.zeroAzimuth = ((Number)zeroAzimuthField.getValue()).intValue();
	}
    }

    class MiscPanel extends JPanel {
	
	public JCheckBox showVisualizerCheckBox, showConsoleCheckBox;

	public MiscPanel() {

	    setLayout(new GridLayout(2, 1));
	    GridBagConstraints c = new GridBagConstraints();
	    setBorder(BorderFactory.createTitledBorder("Miscelaneous"));

	    showVisualizerCheckBox = new JCheckBox("Show Visualizer Window     ");
	    showVisualizerCheckBox.setSelected(customProps.showVisualizer);
	    showVisualizerCheckBox.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			boolean isSelected = showVisualizerCheckBox.isSelected();
			DomeControl.visualizer.setVisible(isSelected);
			ap.showVisualizer = isSelected;
		    }
		});

	    showConsoleCheckBox = new JCheckBox("Show Console Window        ");
	    showConsoleCheckBox.setSelected(customProps.showConsole);
	    showConsoleCheckBox.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			boolean isSelected = showConsoleCheckBox.isSelected();
			DomeControl.console.setVisible(isSelected);
			ap.showConsole = isSelected;
		    }
		});

	    add(showVisualizerCheckBox);
	    add(showConsoleCheckBox);
	}

	public void setAppProperties(AppProperties appProps) {

 	    appProps.showVisualizer = showVisualizerCheckBox.isSelected();
 	    appProps.showConsole = showConsoleCheckBox.isSelected();
	}
    }

    class CreditsPanel extends JPanel {
	
	public CreditsPanel() {

	    setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    setBorder(BorderFactory.createCompoundBorder(
	      BorderFactory.createTitledBorder("Credits"),
              BorderFactory.createEmptyBorder(3,5,5,5)));

	    c.gridy = 0;
	    c.gridx = 0;
	    c.gridwidth = 1;
	    c.gridheight = 2;
	    c.anchor = GridBagConstraints.LINE_START;
	    c.insets = new Insets(0, 20, 3, 20);
	    add(new JLabel(DomeControlIcons.logo), c);
	    c.insets = new Insets(0, 0, 0, 0);
	    c.gridx = 1;
	    c.gridwidth = 2;
	    c.gridheight = 1;
	    add(new JLabel("Dome Control " + DomeControl.VERSION), c);
	    c.gridy = 1;
	    add(new JLabel("Copyright \251 " + DomeControl.COPYRIGHT_YEAR + " " + DomeControl.COPYRIGHT), c);

	    c.gridy = 2;
	    c.gridx = 0;
	    c.gridwidth=2;
	    add(new JLabel("Author & Maintainer:"), c);

	    c.gridx = 2;
	    c.gridwidth=1;
	    add(new JLabel(DomeControl.AUTHOR_AND_MAINTAINER), c);

	    c.gridy = 3;
	    c.gridx = 0;
	    c.gridwidth=2;
	    add(new JLabel("Suggestions & Testing:  "), c);
	    c.gridx = 2;
	    c.gridwidth=1;
	    add(new JLabel(DomeControl.SUGGESTIONS_AND_TESTING), c);

	    c.gridy = 4;
	    c.gridx = 0;
	    c.gridwidth=2;
	    add(new JLabel("Special Thanks:"), c);
	    c.gridx = 2;
	    c.gridwidth=1;
	    add(new JLabel(DomeControl.SPECIAL_THANKS), c);
	}
    }

    class DomeParametersPanel extends JPanel {

	private ParameterField wallHeightField, domeRadiusField, slitWidthField, slitDepthField;

	public DomeParametersPanel() {

	    setLayout(new SpringLayout());
	    setBorder(BorderFactory.createTitledBorder("Dome Parameters"));

	    NumberFormat parameterFormat = NumberFormat.getInstance(Locale.US);
	    ((DecimalFormat)parameterFormat).applyPattern("##0.00#");

	    wallHeightField = new ParameterField(customProps.wallHeight, parameterFormat, 0.0);
	    domeRadiusField = new ParameterField(customProps.domeRadius, parameterFormat, 0.0);
	    slitWidthField = new ParameterField(customProps.slitWidth, parameterFormat, 0.0);
	    slitDepthField = new ParameterField(customProps.slitDepth, parameterFormat, 0.0);

	    add(new JLabel("Wall Height                       "));
	    add(wallHeightField);
	    add(new JLabel("Dome Radius"));
	    add(domeRadiusField);
	    add(new JLabel("Slit Width"));
	    add(slitWidthField);
	    add(new JLabel("Slit Depth"));
	    add(slitDepthField);

	    int numPairs = getComponentCount() / 2;
	    SpringUtilities.makeCompactGrid(this, numPairs, 2, 5, 8, 5, 4);
	}

	void setAppProperties(AppProperties appProps) {
	    appProps.wallHeight = ((Number)wallHeightField.getValue()).doubleValue();
	    appProps.domeRadius = ((Number)domeRadiusField.getValue()).doubleValue();
	    appProps.slitWidth = ((Number)slitWidthField.getValue()).doubleValue();
	    appProps.slitDepth = ((Number)slitDepthField.getValue()).doubleValue();
	}
    }

    class MountParametersPanel extends JPanel {

        private ParameterField excentricDistanceField, legAxisLengthField, polarAxisLengthField, decAxisLengthField;

	public MountParametersPanel() {

	    setLayout(new SpringLayout());
	    setBorder(BorderFactory.createTitledBorder("Mount Parameters"));

	    NumberFormat parameterFormat = NumberFormat.getInstance(Locale.US);
	    ((DecimalFormat)parameterFormat).applyPattern("##0.00#");

	    excentricDistanceField = new ParameterField(customProps.excentricDistance, parameterFormat);
	    legAxisLengthField = new ParameterField(customProps.legAxisLength, parameterFormat, 0.0);
	    polarAxisLengthField = new ParameterField(customProps.polarAxisLength, parameterFormat, 0.0);
	    decAxisLengthField = new ParameterField(customProps.decAxisLength, parameterFormat, 0.0);

	    add(new JLabel("Excentric Distance"));
	    add(excentricDistanceField);
	    add(new JLabel("Leg Axis Length"));
	    add(legAxisLengthField);
	    add(new JLabel("Polar Axis Length"));
	    add(polarAxisLengthField);
	    add(new JLabel("Declination Axis Length"));
	    add(decAxisLengthField);

	    int numPairs = getComponentCount() / 2;
	    SpringUtilities.makeCompactGrid(this, numPairs, 2, 5, 8, 5, 4);
	}

	void setAppProperties(AppProperties appProps) {

	    appProps.excentricDistance = ((Number)excentricDistanceField.getValue()).doubleValue();
	    appProps.legAxisLength = ((Number)legAxisLengthField.getValue()).doubleValue();
	    appProps.polarAxisLength = ((Number)polarAxisLengthField.getValue()).doubleValue();
	    appProps.decAxisLength = ((Number)decAxisLengthField.getValue()).doubleValue();
	}
    }

    class ScopeParametersPanel extends JPanel {

	private ParameterField scopeOffsetField, scopePositionAngleField, scopeDiameterField;

	public ScopeParametersPanel() {

	    setLayout(new SpringLayout());
	    setBorder(BorderFactory.createTitledBorder("Telescope Parameters"));

	    NumberFormat parameterFormat = NumberFormat.getInstance(Locale.US);
	    ((DecimalFormat)parameterFormat).applyPattern("##0.00#");

	    scopeOffsetField = new ParameterField(customProps.scopeOffset, parameterFormat, 0.0);
	    scopePositionAngleField = new ParameterField(customProps.scopePA, parameterFormat, 0.0, 360.0);
	    scopeDiameterField = new ParameterField(customProps.scopeDiameter, parameterFormat, 0.0);

	    add(new JLabel("Scope Offset"));
	    add(scopeOffsetField);
	    add(new JLabel("Scope Position Angle        "));
	    add(scopePositionAngleField);
	    add(new JLabel("Scope Diameter"));
	    add(scopeDiameterField);

	    int numPairs = getComponentCount() / 2;
	    SpringUtilities.makeCompactGrid(this, numPairs, 2, 5, 8, 5, 4);
	}

	void setAppProperties(AppProperties appProps) {

	    appProps.scopeOffset = ((Number)scopeOffsetField.getValue()).doubleValue();
	    appProps.scopePA = ((Number)scopePositionAngleField.getValue()).doubleValue();
	    appProps.scopeDiameter = ((Number)scopeDiameterField.getValue()).doubleValue();
	}
   }

    class TrackingParametersPanel extends JPanel {

	private JPanel leftColumnPane, rightColumnPane, trackingModeRadioButtonPane;
	private ParameterField domeTrackingDelayField, slitBoundaryTresholdField;
	private ParameterSpinner sensorCountSpinner;
	private JRadioButton followObjectRadioButton, followScopeRadioButton, optimizedRadioButton;
	private ButtonGroup trackingModeButtonGroup;

	public TrackingParametersPanel() {
   
	    setLayout(new GridLayout(1, 2));

	    setBorder(BorderFactory.createTitledBorder("Tracking Parameters"));

	    leftColumnPane = new JPanel(new GridBagLayout());
	    leftColumnPane.setBorder(BorderFactory.createEmptyBorder(3,5,0,0));
	    trackingModeRadioButtonPane = new JPanel(new GridBagLayout());
	    followObjectRadioButton = new JRadioButton("Follow Object");
	    followScopeRadioButton = new JRadioButton("Follow Scope");
	    optimizedRadioButton = new JRadioButton("Optimized");

	    if(customProps.domeTrackingMode == AppProperties.FOLLOW_OBJECT)
		followObjectRadioButton.setSelected(true);
	    else if(customProps.domeTrackingMode == AppProperties.FOLLOW_SCOPE)
		followScopeRadioButton.setSelected(true);
	    else if(customProps.domeTrackingMode == AppProperties.OPTIMIZED)
		optimizedRadioButton.setSelected(true);

	    trackingModeButtonGroup = new ButtonGroup();
	    trackingModeButtonGroup.add(followObjectRadioButton);
	    trackingModeButtonGroup.add(followScopeRadioButton);
	    trackingModeButtonGroup.add(optimizedRadioButton);

	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.BOTH;
	    c.weightx = 1.0;
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    trackingModeRadioButtonPane.add(followObjectRadioButton, c);
	    trackingModeRadioButtonPane.add(followScopeRadioButton, c);
	    trackingModeRadioButtonPane.add(optimizedRadioButton, c);

	    c.gridwidth = GridBagConstraints.RELATIVE;
	    leftColumnPane.add(new JLabel("Tracking mode"), c);
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    leftColumnPane.add(trackingModeRadioButtonPane, c);

	    rightColumnPane = new JPanel(new SpringLayout());
	    rightColumnPane.setBorder(BorderFactory.createEmptyBorder(3, 13, 5, 0));

	    NumberFormat parameterFormat = NumberFormat.getInstance(Locale.US);
	    ((DecimalFormat)parameterFormat).applyPattern("##0.00#");

	    domeTrackingDelayField = new ParameterField(customProps.domeTrackingDelay, parameterFormat, 0.0);
	    slitBoundaryTresholdField = new ParameterField(customProps.sensorCount, parameterFormat, 0.0);
	    sensorCountSpinner = new ParameterSpinner(customProps.sensorCount, 3, 20);
	    
	    rightColumnPane.add(new JLabel("Tracking Loop Delay "));
	    rightColumnPane.add(domeTrackingDelayField);
	    rightColumnPane.add(new JLabel("Slit Boundary Treshold "));
	    rightColumnPane.add(slitBoundaryTresholdField);
	    rightColumnPane.add(new JLabel("Sensor Count"));
	    rightColumnPane.add(sensorCountSpinner);

	    int numPairs = rightColumnPane.getComponentCount() / 2;
	    SpringUtilities.makeCompactGrid(rightColumnPane, numPairs, 2, 5, 8, 7, 4);

	    add(leftColumnPane);
	    add(rightColumnPane);
	}

	void setAppProperties(AppProperties appProps) {

	    if(followObjectRadioButton.isSelected())
		appProps.domeTrackingMode = AppProperties.FOLLOW_OBJECT;
	    else if(followScopeRadioButton.isSelected())
		appProps.domeTrackingMode = AppProperties.FOLLOW_SCOPE;
	    else if(optimizedRadioButton.isSelected())
		appProps.domeTrackingMode = AppProperties.OPTIMIZED;

	    appProps.domeTrackingDelay = ((Number)domeTrackingDelayField.getValue()).doubleValue();
	    appProps.slitBoundaryTreshold = ((Number)slitBoundaryTresholdField.getValue()).doubleValue();
	    appProps.sensorCount = ((Number)sensorCountSpinner.getValue()).intValue();

	}
    }

    class ParameterSpinner extends JSpinner implements ChangeListener  {
	
	private SpinnerNumberModel model;

	public ParameterSpinner(int value, int min, int max) {
	    this(value, min, max, 1);
	}

	public ParameterSpinner(int value, int min, int max, int step) {
  
	    model = new SpinnerNumberModel(value, min, max, step);
	    setModel(model);
	    setEditor(new JSpinner.NumberEditor(this, "#0"));
	    setPreferredSize(new Dimension(60, 18));
	    addChangeListener(this);
	}

	public void setValue(int value) {
	    model.setValue(new Integer(value));
	}

	public void spinValue(int steps) {
	    int value = model.getNumber().intValue();
	    int min = ((Number)model.getMinimum()).intValue();
	    int max = ((Number)model.getMaximum()).intValue();
	    int step = model.getStepSize().intValue();

	    if(steps < 0 && value == min || steps > 0 && value == max)
		return;

	    int newValue = value + steps*step;
	    if(newValue < min)
		newValue = min;
	    else if(newValue > max)
		newValue = max;

	    model.setValue(new Integer(newValue));
	}

	public void stateChanged(ChangeEvent e) {
	    return;
	}
    }

    class ParameterField extends JFormattedTextField implements PropertyChangeListener {

	private double value;
	private double defaultValue;
	private double minValue;
	private double maxValue;
	private boolean noMinValue = false;
	private boolean noMaxValue = false;
	private NumberFormat parameterFormat;
	private Color normalColor, errorColor;

	public ParameterField(NumberFormat format) {
	    this(0.0, format, 0.0, 0.0);    
	}

	public ParameterField(double value, NumberFormat format) {
	    this(value, format, 0.0, 0.0);	    
	}

	public ParameterField(double value, NumberFormat format, double minValue) {
	    this(value, format, minValue, 0.0);
       	    noMaxValue = true;
	    maxValue = minValue+1;
	}

	public ParameterField(double value, NumberFormat format, double minValue, double maxValue) {
	    
	    super(format);

	    setValue(new Double(value));
	    setColumns(3);
	    this.value = value;
	    this.defaultValue = value;
	    this.parameterFormat = format;
	    this.minValue = minValue;
	    this.maxValue = maxValue;
	    setInputVerifier(new ParameterFieldVerifier());
	    addPropertyChangeListener("value", this);

	    normalColor = getSelectionColor();
	    errorColor = new Color(255, 203, 203);

	    if(minValue > maxValue)
		noMinValue = true;
	}

	public double getDefaultValue() {

	    return defaultValue;
	}

	public void propertyChange(PropertyChangeEvent evt) {

	    Object source = evt.getSource();

	    if (source == this) {
		 
		try {
		    value = ((Number)parameterFormat.parse(getText())).doubleValue();

		    boolean valueOk;

		    if(noMinValue)
			valueOk = (value > maxValue) ? false : true;
		    else if(noMaxValue)
			valueOk = (value < minValue) ? false : true;
		    else
			valueOk = (value < minValue || value > maxValue) ? false : true;

		    if(!valueOk)
			throw new ParseException(null, 0);

		    if(getSelectionColor() == errorColor)
			setSelectionColor(normalColor);
		}
		catch (ParseException e) {
		    //setText(new Double(defaultValue).toString());
		    setSelectionColor(errorColor);
		    selectAll();

		    if(noMinValue)
			JOptionPane.showMessageDialog(null, "Please enter a value below " + maxValue + ".", "Parse Error", JOptionPane.ERROR_MESSAGE);
		    else if(noMaxValue)
			JOptionPane.showMessageDialog(null, "Please enter a value above " + minValue + ".", "Parse Error", JOptionPane.ERROR_MESSAGE);
		    else
			JOptionPane.showMessageDialog(null, "Please enter a value between " + minValue + " and " + maxValue +".", "Parse Error", JOptionPane.ERROR_MESSAGE);

		    setText(parameterFormat.format(defaultValue));
		}
		catch (Exception e)  {
		    e.printStackTrace();
		    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
	    }
	}

	class ParameterFieldVerifier extends InputVerifier {

	    private double value = 0.0;

	    public boolean verify(JComponent input) {

		ParameterField parameterField = (ParameterField)input;
		boolean valueOk = true;
		
		if(minValue < maxValue) {
		    try {			
			value = ((Number)parameterField.parameterFormat.parse(parameterField.getText())).doubleValue();

			if(noMinValue)
			    valueOk = (value > maxValue) ? false : true;
			else if(noMaxValue)
			    valueOk = (value < minValue) ? false : true;
			else
			    valueOk = (value < minValue || value > maxValue) ? false : true;
		    }	
		    catch (ParseException e) {
			valueOk = false;
		    }
		}

		return valueOk;
	    }
	    
	    public boolean shouldYieldFocus(JComponent input) {

		ParameterField parameterField = (ParameterField)input;
		boolean valueOk = verify(input);
	       
		if(! valueOk) {
		    //ParameterField.this.setValue(new Double(getDefaultValue()));
		    parameterField.setSelectionColor(parameterField.errorColor);
		    parameterField.selectAll();
		}
		else if(parameterField.getSelectionColor() == parameterField.errorColor) {
		    parameterField.setSelectionColor(parameterField.normalColor);
		}

		return valueOk;
	    }
    	}
    }

    public void disposeHiddenWindows() {
	if(DomeControl.visualizer.isShowing() == false)
	    DomeControl.visualizer.dispose();
	if(DomeControl.console.isShowing() == false)
	    DomeControl.console.dispose();
    }

    public void saveWindowProperties() {
	ap.preferencesWindowX = getX();
	ap.preferencesWindowY = getY();
    }

    // Define action on closing of the Preferenes Window
    protected void processWindowEvent(WindowEvent evt) {
	if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
	    saveWindowProperties();
	    DomeControl.saveAppProperties();
	    disposeHiddenWindows();
	    dispose();
	}
    }

    public static void main(String[] args) {
	Preferences dialog = new Preferences();
	dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
