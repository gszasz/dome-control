/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

import java.io.*;
import java.util.*;
import java.text.*;

class AppProperties {

    // Properties' Names
    public static final String MAIN_WINDOW_X_PROP = "window.main.position.x";
    public static final String MAIN_WINDOW_Y_PROP = "window.main.position.y";
    public static final String PREFERENCES_WINDOW_X_PROP = "window.preferences.position.x";
    public static final String PREFERENCES_WINDOW_Y_PROP = "window.preferences.position.y";
    public static final String CONSOLE_WINDOW_X_PROP = "window.console.position.x";
    public static final String CONSOLE_WINDOW_Y_PROP = "window.console.position.y";
    public static final String CONSOLE_WINDOW_WIDTH_PROP = "window.console.size.width";
    public static final String CONSOLE_WINDOW_HEIGHT_PROP = "window.console.size.height";
    public static final String VISUALIZER_WINDOW_X_PROP = "window.visualizer.position.x";
    public static final String VISUALIZER_WINDOW_Y_PROP = "window.visualizer.position.y";
    public static final String CALIBRATION_WINDOW_X_PROP = "window.calibration.position.x";
    public static final String CALIBRATION_WINDOW_Y_PROP = "window.calibration.position.y";
    public static final String HOST_PROP = "general.network.host";
    public static final String PORT_PROP = "general.network.port";
    public static final String SIMULATOR_PROP = "general.network.simulator";
    public static final String PACKET_LENGTH_PROP = "general.network.packet-length";    
    public static final String PACKET_TIMEOUT_PROP = "general.network.packet-timeout";
    public static final String DOME_MONITOR_DELAY_PROP = "general.network.dome-monitoring-delay";
    public static final String ZERO_AZIMUTH_PROP = "general.calibration.zero-azimuth";
    public static final String DOME_ERROR_ALERT_PROP = "general.alerts.dome-error";
    public static final String DOME_ERROR_ALERT_FILE_PROP = "general.alerts.dome-error.file";
    public static final String NET_ERROR_ALERT_PROP = "general.alerts.network-error";
    public static final String NET_ERROR_ALERT_FILE_PROP = "general.alerts.network-error.file";
    public static final String ERROR_ALERT_PROP = "general.alerts.error";
    public static final String ERROR_ALERT_FILE_PROP = "general.alerts.error.file";
    public static final String SHOW_VISUALIZER_PROP = "general.misc.show-visualizer";
    public static final String SHOW_CONSOLE_PROP = "general.misc.show-console";
    public static final String CATALOG_FILE_PROP = "tracking.catalog.file";
    public static final String LONGITUDE_PROP = "tracking.coordinates.longitude";
    public static final String LATITUDE_PROP = "tracking.coordinates.latitude";
    public static final String DOME_RADIUS_PROP = "tracking.parameters.dome-radius";
    public static final String SLIT_WIDTH_PROP = "tracking.parameters.slit-width";
    public static final String SLIT_DEPTH_PROP = "tracking.parameters.slit-depth";
    public static final String WALL_HEIGHT_PROP = "tracking.parameters.wall-height";
    public static final String EXCENTRIC_DISTANCE_PROP = "tracking.parameters.excentric-distance";
    public static final String LEG_AXIS_LENGTH_PROP = "tracking.parameters.leg-axis-length";
    public static final String POLAR_AXIS_LENGTH_PROP = "tracking.parameters.polar-axis-length";
    public static final String DEC_AXIS_LENGTH_PROP = "tracking.parameters.dec-axis-length";
    public static final String SCOPE_OFFSET_PROP = "tracking.parameters.scope-offset"; 
    public static final String SCOPE_PA_PROP = "tracking.parameters.scope-position-angle";
    public static final String SCOPE_DIAMETER_PROP = "tracking.parameters.scope-diameter";
    public static final String SENSOR_COUNT_PROP = "tracking.parameters.sensor-count";
    public static final String DOME_TRACKING_DELAY_PROP = "tracking.parameters.tracking-delay";
    public static final String SLIT_BOUNDARY_TRESHOLD_PROP = "tracking.parameters.slit-boundary-treshold";
    public static final String DOME_TRACKING_MODE_PROP = "tracking.parameters.tracking-mode";

    // Default Values

    // Default Window Positions (-1 = System Default)
    public static final int MAIN_WINDOW_X = -1;         // DomeControl
    public static final int MAIN_WINDOW_Y = -1;         // DomeControl
    public static final int PREFERENCES_WINDOW_X = -1;  // Preferences
    public static final int PREFERENCES_WINDOW_Y = -1;  // Preferences
    public static final int CONSOLE_WINDOW_X = -1;      // Console
    public static final int CONSOLE_WINDOW_Y = -1;      // Console
    public static final int CONSOLE_WINDOW_WIDTH = -1;  // Console
    public static final int CONSOLE_WINDOW_HEIGHT = -1; // Console
    public static final int VISUALIZER_WINDOW_X = -1;   // Visualizer
    public static final int VISUALIZER_WINDOW_Y = -1;   // Visualizer
    public static final int CALIBRATION_WINDOW_X = -1;  // Calibration
    public static final int CALIBRATION_WINDOW_Y = -1;  // Calibration

    // General Tab

    // Default Network Properties
    public static final String HOST = "dome";	           // DomeControl
    public static final int PORT = 1999;	           // DomeControl
    public static final boolean SIMULATOR = false;	   // DomeControl
    public static final int PACKET_LENGTH = 1024;          // UDPConnection
    public static final int PACKET_TIMEOUT = 500;          // Dome
    public static final double DOME_MONITOR_DELAY = 1.0;   // DomeControl

    // Default Zero Azimuth
    public static final int ZERO_AZIMUTH = 102;            // DomeControl

    // Default Alerts
    public static final boolean DOME_ERROR_ALERT = false;  // DomeControl
    public static final String DOME_ERROR_ALERT_FILE = ""; // DomeControl
    public static final boolean NET_ERROR_ALERT = false;   // DomeControl
    public static final String NET_ERROR_ALERT_FILE = "";  // DomeControl
    public static final boolean ERROR_ALERT = false;       // DomeControl
    public static final String ERROR_ALERT_FILE = "";      // DomeControl

    // Miscelaneous
    public static final boolean SHOW_VISUALIZER = false;   // DomeControl
    public static final boolean SHOW_CONSOLE = false;      // DomeControl

    // Tracking Tab

    // Catalog File
    public static final String CATALOG_FILE = "catalog.dat"; // DomeControl

    // Observatory Coordinates
    public static final double LONGITUDE = -17.79833;  // LSTClock
    public static final double LATITUDE = 48.41972;    // Mount

    // Default Dome parameters
    public static final double DOME_RADIUS = 2.3;  // Dome
    public static final double SLIT_WIDTH = 2.0;   // Dome
    public static final double SLIT_DEPTH = 0.55;  // Dome
    public static final double WALL_HEIGHT = 2.10; // Mount

    // Default Mount parameters
    public static final double EXCENTRIC_DISTANCE = 0.2; // Mount 
    public static final double LEG_AXIS_LENGTH = 1.93;   // Mount
    public static final double POLAR_AXIS_LENGTH = 0.25; // Mount 
    public static final double DEC_AXIS_LENGTH = 0.94;   // Mount 
    public static final double SCOPE_OFFSET = 0;         // Mount 
    public static final double SCOPE_PA = 0;             // Mount 
    public static final double SCOPE_DIAMETER = 0.7;     // Mount

    // Dome Tracking Modes
    public static final int FOLLOW_OBJECT = 0;		 // Tracking
    public static final int FOLLOW_SCOPE = 1;            // Tracking
    public static final int OPTIMIZED = 2;               // Tracking

    // Default Dome Tracking Parameters
    public static final int SENSOR_COUNT = 20;                 // Mount
    public static final double DOME_TRACKING_DELAY = 10.0;     // Tracking
    public static final double SLIT_BOUNDARY_TRESHOLD = 0.3;   // Tracking
    public static final int DOME_TRACKING_MODE = FOLLOW_SCOPE; // Tracking

    // Variables

    public int mainWindowX = MAIN_WINDOW_X;
    public int mainWindowY = MAIN_WINDOW_Y;
    public int preferencesWindowX = PREFERENCES_WINDOW_X;
    public int preferencesWindowY = PREFERENCES_WINDOW_Y;
    public int consoleWindowX = CONSOLE_WINDOW_X;
    public int consoleWindowY = CONSOLE_WINDOW_Y;
    public int consoleWindowWidth = CONSOLE_WINDOW_WIDTH;
    public int consoleWindowHeight = CONSOLE_WINDOW_HEIGHT;
    public int visualizerWindowX = VISUALIZER_WINDOW_X;
    public int visualizerWindowY = VISUALIZER_WINDOW_Y;
    public int calibrationWindowX = CALIBRATION_WINDOW_X;
    public int calibrationWindowY = CALIBRATION_WINDOW_Y;
    public String host = HOST;
    public int port = PORT;	
    public boolean simulator = SIMULATOR;
    public int packetLength = PACKET_LENGTH;
    public int packetTimeout = PACKET_TIMEOUT;
    public double domeMonitorDelay = DOME_MONITOR_DELAY;
    public int zeroAzimuth = ZERO_AZIMUTH;
    public boolean domeErrorAlert = DOME_ERROR_ALERT;
    public String domeErrorAlertFile = DOME_ERROR_ALERT_FILE;
    public boolean netErrorAlert = NET_ERROR_ALERT;
    public String netErrorAlertFile = NET_ERROR_ALERT_FILE;
    public boolean errorAlert = ERROR_ALERT;
    public String errorAlertFile = ERROR_ALERT_FILE;
    public boolean showVisualizer = SHOW_VISUALIZER;
    public boolean showConsole = SHOW_CONSOLE;
    public String catalogFile = CATALOG_FILE;
    public double longitude = LONGITUDE;
    public double latitude = LATITUDE;
    public double domeRadius = DOME_RADIUS;
    public double slitWidth = SLIT_WIDTH;
    public double slitDepth = SLIT_DEPTH;
    public double wallHeight = WALL_HEIGHT;
    public double excentricDistance = EXCENTRIC_DISTANCE;
    public double legAxisLength = LEG_AXIS_LENGTH;
    public double polarAxisLength = POLAR_AXIS_LENGTH;
    public double decAxisLength = DEC_AXIS_LENGTH;
    public double scopeOffset = SCOPE_OFFSET;
    public double scopePA = SCOPE_PA;
    public double scopeDiameter = SCOPE_DIAMETER;
    public int domeTrackingMode = DOME_TRACKING_MODE;
    public double domeTrackingDelay = DOME_TRACKING_DELAY;
    public double slitBoundaryTreshold = SLIT_BOUNDARY_TRESHOLD;
    public int sensorCount = SENSOR_COUNT;


    // Initialize AppProperties with default values
    public AppProperties() {
	return;
    }

    // Initialize AppProperties from another AppProperties object
    public AppProperties(AppProperties appProps) {
	setFromAppProperties(appProps);
    }

    // Inicialize AppProperties directly from Properties object
    public AppProperties(Properties props) {
	setFromProperties(props);
    }

    public Properties toProperties() {

	Properties props = new Properties();
	
	props.put(MAIN_WINDOW_X_PROP, new Integer(mainWindowX).toString());
	props.put(MAIN_WINDOW_Y_PROP, new Integer(mainWindowY).toString());
	props.put(PREFERENCES_WINDOW_X_PROP, new Integer(preferencesWindowX).toString());
	props.put(PREFERENCES_WINDOW_Y_PROP, new Integer(preferencesWindowY).toString());
	props.put(CONSOLE_WINDOW_X_PROP, new Integer(consoleWindowX).toString());
	props.put(CONSOLE_WINDOW_Y_PROP, new Integer(consoleWindowY).toString());
	props.put(CONSOLE_WINDOW_WIDTH_PROP, new Integer(consoleWindowWidth).toString());
	props.put(CONSOLE_WINDOW_HEIGHT_PROP, new Integer(consoleWindowHeight).toString());
	props.put(VISUALIZER_WINDOW_X_PROP, new Integer(visualizerWindowX).toString());
	props.put(VISUALIZER_WINDOW_Y_PROP, new Integer(visualizerWindowY).toString());
	props.put(CALIBRATION_WINDOW_X_PROP, new Integer(calibrationWindowX).toString());
	props.put(CALIBRATION_WINDOW_Y_PROP, new Integer(calibrationWindowY).toString());
	props.put(HOST_PROP, host);
	props.put(PORT_PROP, new Integer(port).toString());
	props.put(SIMULATOR_PROP, new Boolean(simulator).toString());
	props.put(PACKET_LENGTH_PROP, new Integer(packetLength).toString());
	props.put(PACKET_TIMEOUT_PROP, new Integer(packetTimeout).toString());
	props.put(DOME_MONITOR_DELAY_PROP, new Double(domeMonitorDelay).toString());
	props.put(ZERO_AZIMUTH_PROP, new Integer(zeroAzimuth).toString());
	props.put(DOME_ERROR_ALERT_PROP, new Boolean(domeErrorAlert).toString());
	props.put(DOME_ERROR_ALERT_FILE_PROP, domeErrorAlertFile);
	props.put(NET_ERROR_ALERT_PROP, new Boolean(netErrorAlert).toString());
	props.put(NET_ERROR_ALERT_FILE_PROP, netErrorAlertFile);
	props.put(ERROR_ALERT_PROP, new Boolean(errorAlert).toString());
	props.put(ERROR_ALERT_FILE_PROP, errorAlertFile);
	props.put(SHOW_VISUALIZER_PROP, new Boolean(showVisualizer).toString());
	props.put(SHOW_CONSOLE_PROP, new Boolean(showConsole).toString());
	props.put(CATALOG_FILE_PROP, catalogFile);
	props.put(DOME_RADIUS_PROP, new Double(domeRadius).toString());
	props.put(SLIT_WIDTH_PROP, new Double(slitWidth).toString());
	props.put(SLIT_DEPTH_PROP, new Double(slitDepth).toString());
	props.put(WALL_HEIGHT_PROP, new Double(wallHeight).toString());
	props.put(EXCENTRIC_DISTANCE_PROP, new Double(excentricDistance).toString());
	props.put(LEG_AXIS_LENGTH_PROP, new Double(legAxisLength).toString());
	props.put(POLAR_AXIS_LENGTH_PROP, new Double(polarAxisLength).toString());
	props.put(DEC_AXIS_LENGTH_PROP, new Double(decAxisLength).toString());
	props.put(SCOPE_OFFSET_PROP, new Double(scopeOffset).toString());
	props.put(SCOPE_PA_PROP, new Double(scopePA).toString());
	props.put(SCOPE_DIAMETER_PROP, new Double(scopeDiameter).toString());
	props.put(DOME_TRACKING_MODE_PROP, new Integer(domeTrackingMode).toString());
	props.put(DOME_TRACKING_DELAY_PROP, new Double(domeTrackingDelay).toString());
	props.put(SLIT_BOUNDARY_TRESHOLD_PROP, new Double(sensorCount).toString());
	props.put(SENSOR_COUNT_PROP, new Integer(sensorCount).toString());

	return props;
    }

    public void setFromAppProperties(AppProperties appProps) {
	mainWindowX = appProps.mainWindowX;
	mainWindowY = appProps.mainWindowY;
	preferencesWindowX = appProps.preferencesWindowX;
	preferencesWindowY = appProps.preferencesWindowY;
	consoleWindowX = appProps.consoleWindowX;
	consoleWindowY = appProps.consoleWindowY;
	consoleWindowHeight = appProps.consoleWindowHeight;
	consoleWindowWidth = appProps.consoleWindowWidth;
	visualizerWindowX = appProps.visualizerWindowX;
	visualizerWindowY = appProps.visualizerWindowY;
	calibrationWindowX = appProps.calibrationWindowX;
	calibrationWindowY = appProps.calibrationWindowY;
	host = appProps.host;
	port = appProps.port;
	simulator = appProps.simulator;
	packetLength = appProps.packetLength;
	packetTimeout = appProps.packetTimeout;
	domeMonitorDelay = appProps.domeMonitorDelay;
	zeroAzimuth = appProps.zeroAzimuth;
	domeErrorAlert = appProps.domeErrorAlert;
	domeErrorAlertFile = appProps.domeErrorAlertFile;
	netErrorAlert = appProps.netErrorAlert;
	netErrorAlertFile = appProps.netErrorAlertFile;
	errorAlert = appProps.errorAlert;
	errorAlertFile = appProps.errorAlertFile;
	showVisualizer = appProps.showVisualizer;
	showConsole = appProps.showConsole;
	catalogFile = appProps.catalogFile;
	domeRadius = appProps.domeRadius;
	slitWidth = appProps.slitWidth;
	slitDepth = appProps.slitDepth;
	wallHeight = appProps.wallHeight;
	excentricDistance = appProps.excentricDistance;
	legAxisLength = appProps.legAxisLength;
	polarAxisLength = appProps.polarAxisLength;
	decAxisLength = appProps.decAxisLength;
	scopeOffset = appProps.scopeOffset;
	scopePA = appProps.scopePA;
	scopeDiameter = appProps.scopeDiameter;
	domeTrackingMode = appProps.domeTrackingMode;
	domeTrackingDelay = appProps.domeTrackingDelay;
	slitBoundaryTreshold = appProps.slitBoundaryTreshold;
	sensorCount = appProps.sensorCount;
    }

    public void setFromProperties(Properties props)  {
	if(props.getProperty(MAIN_WINDOW_X_PROP) != null)
	    mainWindowX = Integer.parseInt(props.getProperty(MAIN_WINDOW_X_PROP));
	if(props.getProperty(MAIN_WINDOW_Y_PROP) != null)
	    mainWindowY = Integer.parseInt(props.getProperty(MAIN_WINDOW_Y_PROP));
	if(props.getProperty(PREFERENCES_WINDOW_X_PROP) != null)
	    preferencesWindowX = Integer.parseInt(props.getProperty(PREFERENCES_WINDOW_X_PROP));
	if(props.getProperty(PREFERENCES_WINDOW_Y_PROP) != null)
	    preferencesWindowY = Integer.parseInt(props.getProperty(PREFERENCES_WINDOW_Y_PROP));
	if(props.getProperty(CONSOLE_WINDOW_X_PROP) != null)
	    consoleWindowX = Integer.parseInt(props.getProperty(CONSOLE_WINDOW_X_PROP));
	if(props.getProperty(CONSOLE_WINDOW_Y_PROP) != null)
	    consoleWindowY = Integer.parseInt(props.getProperty(CONSOLE_WINDOW_Y_PROP));
	if(props.getProperty(CONSOLE_WINDOW_WIDTH_PROP) != null)
	    consoleWindowWidth = Integer.parseInt(props.getProperty(CONSOLE_WINDOW_WIDTH_PROP));
	if(props.getProperty(CONSOLE_WINDOW_HEIGHT_PROP) != null)
	    consoleWindowHeight = Integer.parseInt(props.getProperty(CONSOLE_WINDOW_HEIGHT_PROP));
	if(props.getProperty(VISUALIZER_WINDOW_X_PROP) != null)
	    visualizerWindowX = Integer.parseInt(props.getProperty(VISUALIZER_WINDOW_X_PROP));
	if(props.getProperty(VISUALIZER_WINDOW_Y_PROP) != null)
	    visualizerWindowY = Integer.parseInt(props.getProperty(VISUALIZER_WINDOW_Y_PROP));
	if(props.getProperty(CALIBRATION_WINDOW_X_PROP) != null)
	    calibrationWindowX = Integer.parseInt(props.getProperty(CALIBRATION_WINDOW_X_PROP));
	if(props.getProperty(CALIBRATION_WINDOW_Y_PROP) != null)
	    calibrationWindowY = Integer.parseInt(props.getProperty(CALIBRATION_WINDOW_Y_PROP));
	if(props.getProperty(HOST_PROP) != null)
	    host = props.getProperty(HOST_PROP);
	if(props.getProperty(PORT_PROP) != null)
	    port = Integer.parseInt(props.getProperty(PORT_PROP));
	if(props.getProperty(SIMULATOR_PROP) != null)
	    simulator = props.getProperty(SIMULATOR_PROP).equals("true") ? true : false;
	if(props.getProperty(PACKET_LENGTH_PROP) != null)
	    packetLength = Integer.parseInt(props.getProperty(PACKET_LENGTH_PROP));
	if(props.getProperty(PACKET_TIMEOUT_PROP) != null)
	    packetTimeout = Integer.parseInt(props.getProperty(PACKET_TIMEOUT_PROP));
	if(props.getProperty(DOME_MONITOR_DELAY_PROP) != null)
	    domeMonitorDelay = Double.parseDouble(props.getProperty(DOME_MONITOR_DELAY_PROP));
	if(props.getProperty(ZERO_AZIMUTH_PROP) != null)
	    zeroAzimuth = Integer.parseInt(props.getProperty(ZERO_AZIMUTH_PROP));
	if(props.getProperty(DOME_ERROR_ALERT_PROP) != null)
	    domeErrorAlert = props.getProperty(DOME_ERROR_ALERT_PROP).equals("true") ? true : false;
	if(props.getProperty(DOME_ERROR_ALERT_FILE_PROP) != null)
	    domeErrorAlertFile = props.getProperty(DOME_ERROR_ALERT_FILE_PROP);
	if(props.getProperty(NET_ERROR_ALERT_PROP) != null)
	    netErrorAlert = props.getProperty(NET_ERROR_ALERT_PROP).equals("true") ? true : false;
	if(props.getProperty(NET_ERROR_ALERT_FILE_PROP) != null)
	    netErrorAlertFile = props.getProperty(NET_ERROR_ALERT_FILE_PROP);
	if(props.getProperty(ERROR_ALERT_PROP) != null)
	    errorAlert = props.getProperty(ERROR_ALERT_PROP).equals("true") ? true : false;
	if(props.getProperty(ERROR_ALERT_FILE_PROP) != null)
	    errorAlertFile = props.getProperty(ERROR_ALERT_FILE_PROP);
	if(props.getProperty(SHOW_VISUALIZER_PROP) != null)
	    showVisualizer = props.getProperty(SHOW_VISUALIZER_PROP).equals("true") ? true : false;
	if(props.getProperty(SHOW_CONSOLE_PROP) != null)
	    showConsole = props.getProperty(SHOW_CONSOLE_PROP).equals("true") ? true : false;
	if(props.getProperty(CATALOG_FILE_PROP) != null)
	    catalogFile = props.getProperty(CATALOG_FILE_PROP);
	if(props.getProperty(DOME_RADIUS_PROP) != null)
	    domeRadius = Double.parseDouble(props.getProperty(DOME_RADIUS_PROP));
	if(props.getProperty(SLIT_WIDTH_PROP) != null)
	    slitWidth = Double.parseDouble(props.getProperty(SLIT_WIDTH_PROP));
	if(props.getProperty(SLIT_DEPTH_PROP) != null)
	    slitDepth = Double.parseDouble(props.getProperty(SLIT_DEPTH_PROP));
	if(props.getProperty(WALL_HEIGHT_PROP) != null)
	    wallHeight = Double.parseDouble(props.getProperty(WALL_HEIGHT_PROP));
	if(props.getProperty(EXCENTRIC_DISTANCE_PROP) != null)
	    excentricDistance = Double.parseDouble(props.getProperty(EXCENTRIC_DISTANCE_PROP));
	if(props.getProperty(LEG_AXIS_LENGTH_PROP) != null)
	    legAxisLength = Double.parseDouble(props.getProperty(LEG_AXIS_LENGTH_PROP));
	if(props.getProperty(POLAR_AXIS_LENGTH_PROP) != null)
	    polarAxisLength = Double.parseDouble(props.getProperty(POLAR_AXIS_LENGTH_PROP));
	if(props.getProperty(DEC_AXIS_LENGTH_PROP) != null)
	    decAxisLength = Double.parseDouble(props.getProperty(DEC_AXIS_LENGTH_PROP));
	if(props.getProperty(SCOPE_OFFSET_PROP) != null)
	    scopeOffset = Double.parseDouble(props.getProperty(SCOPE_OFFSET_PROP));
	if(props.getProperty(SCOPE_PA_PROP) != null)
	    scopePA = Double.parseDouble(props.getProperty(SCOPE_PA_PROP));
	if(props.getProperty(SCOPE_DIAMETER_PROP) != null)
	    scopeDiameter = Double.parseDouble(props.getProperty(SCOPE_DIAMETER_PROP));
	if(props.getProperty(DOME_TRACKING_MODE_PROP) != null)
	    domeTrackingMode = Integer.parseInt(props.getProperty(DOME_TRACKING_MODE_PROP));
	if(props.getProperty(DOME_TRACKING_DELAY_PROP) != null)
	    domeTrackingDelay = Double.parseDouble(props.getProperty(DOME_TRACKING_DELAY_PROP));
	if(props.getProperty(SLIT_BOUNDARY_TRESHOLD_PROP) != null)
	    slitBoundaryTreshold = Double.parseDouble(props.getProperty(SLIT_BOUNDARY_TRESHOLD_PROP));
	if(props.getProperty(SENSOR_COUNT_PROP) != null)
	    sensorCount = Integer.parseInt(props.getProperty(SENSOR_COUNT_PROP));
    }

    public String toString() {

	String latitudeString, longitudeString, domeTrackingString;

	longitudeString = new Coordinate(longitude).toString();
	latitudeString = new Coordinate(latitude).toString();

	switch(domeTrackingMode) {
	case FOLLOW_OBJECT: 
	    domeTrackingString = "Follow Object";
	    break;
	case FOLLOW_SCOPE: 
	    domeTrackingString = "Follow Scope";
	    break;
	case OPTIMIZED: 
	    domeTrackingString = "Optimized";
	    break;
	default: 
	    domeTrackingString = "Not Set";
	    break;
	}

	String outputString = "\nMain Window Position: " + mainWindowX + ":" + mainWindowY +
	                      "\nPreferences Window Position: " + preferencesWindowX + ":" + preferencesWindowY +
	                      "\nConsole Window Position: " + consoleWindowX + ":" + consoleWindowY +
	                      "\nConsole Window Size: " + consoleWindowWidth + ":" + consoleWindowHeight +
	                      "\nVisualizer Window Position: " + visualizerWindowX + ":" + visualizerWindowY +
                              "\nCalibration Window Position: " + calibrationWindowX + ":" + calibrationWindowY +
	                      "\nHost: " + host + ":" + port +
                              "\nSimulator: " + simulator +
	                      "\nPacket Length: " + packetLength +
	                      "\nPacket Timeout: " + packetTimeout +
	                      "\nDome Monitor Delay: " + domeMonitorDelay +
	                      "\nZero Azimuth: " + zeroAzimuth +
	                      "\nDome Error Alert: " + domeErrorAlert +
	                      "\nDome Error Alert File: " + domeErrorAlertFile +
	                      "\nNetwork Error Alert: " + netErrorAlert +
	                      "\nNetwork Error Alert File: " + netErrorAlertFile +
	                      "\nError Alert: " + errorAlert +
	                      "\nError Alert File: " + errorAlertFile +
	                      "\nShow Visualizer: " + showVisualizer +
	                      "\nShow Console: " + showConsole +
	                      "\nCatalog File: " + catalogFile +
	                      "\nLongitude: " + longitudeString +
	                      "\nLatitude: " + latitudeString +
	                      "\nDome Radius: " + domeRadius +
	                      "\nSlit Width: " + slitWidth +
	                      "\nSlit Depth: " + slitDepth +
	                      "\nWall Height: " + wallHeight +
	                      "\nExcentric Distance: " + excentricDistance +
	                      "\nLeg Axis Length: " + legAxisLength +
	                      "\nPolar Axis Length: " + polarAxisLength +
	                      "\nScope Offset: " + scopeOffset +
	                      "\nScope Position Angle: " + scopePA +
	                      "\nScope Diameter: " + scopeDiameter +
	                      "\nDome Tracking Mode: " + domeTrackingString +
	                      "\nDome Tracking Delay: " + domeTrackingDelay +
	                      "\nSensor Count: " + sensorCount +
	                      "\nSlit Boundary Treshold: " + slitBoundaryTreshold;

	return outputString;
    }

    public static void main(String[] args) {

	// Load default properties from file
	Properties defaultProps = new Properties();
	try {
	    FileInputStream in = new FileInputStream("defaultProperties");
	    defaultProps.load(in);

	    // Load custom application properties
	    Properties props = new Properties(defaultProps);

	    in = new FileInputStream("customProperties");
	    props.load(in);
	    in.close();
	    
	    // Save them to Application properties
	    AppProperties defaultAppProps = new AppProperties(defaultProps);
	    AppProperties appProps = new AppProperties(props);

	    appProps.wallHeight = 14.0;

	    props = appProps.toProperties();
	    FileOutputStream out = new FileOutputStream("customProperties");
	    props.store(out, "--- Dome Control Parameters ---");
	    out.close();
	} catch(Exception e) { e.printStackTrace(); }
    }
}

// Class to hold Coordinates types
class Coordinate  {

    public int deg;
    public int min;
    public double sec;

    public Coordinate(int deg, int min, double sec) {
	this.deg = deg;
	this.min = min;
	this.sec = sec;
    } 
    public Coordinate(double degrees) {
	int sign = (degrees < 0) ? -1 : 1;	
	degrees = Math.abs(degrees);
	this.deg = (int)Math.floor(degrees);
	this.min = (int)Math.floor((degrees-deg)*60);
	this.sec = ((degrees-deg)*60 - min)*60;
	this.deg *= sign;
    }

    public Coordinate(Coordinate coordinate) {
	this.deg = coordinate.deg;
	this.min = coordinate.min;
	this.sec = coordinate.sec;
    }

    public double toDegrees() throws ParseException {
	int sign = (deg<0) ? -1 : 1;
	if(min < 0 || min >= 60 || sec < 0.0 || sec >= 60.0)
	    throw new ParseException(null, 0);
        return (Math.abs(deg) + min/60.0 + sec/3600.0)*sign;
    }

    public String toString()  {

	NumberFormat f = NumberFormat.getInstance(Locale.US);
	((DecimalFormat) f).applyPattern("#0.000");

	if (min == 0 && sec == 0.0)
	    return  deg + "d";
	else if (sec == 0.0)
	    return  deg + "d " + min + "m";
	else
	    return deg + "d " + min + "' " + f.format(sec) + "\"";
    }
}

class HourCoordinate  {

    public int hr;
    public int min;
    public double sec;

    public HourCoordinate(int hr, int min, double sec) {
	this.hr = hr;
	this.min = min;
	this.sec = sec;
    } 
    public HourCoordinate(double degrees) {
	int sign = (degrees < 0) ? -1 : 1;
	degrees = Math.abs(degrees);
	double hours = degrees/15.0;
	this.hr = (int)Math.floor(hours);
	this.min = (int)Math.floor((hours-hr)*60);
	this.sec = ((hours-hr)*60 - min)*60;
	this.hr *= sign;
    }  
    public HourCoordinate(HourCoordinate hourCoordinate) {
	this.hr = hourCoordinate.hr;
	this.min = hourCoordinate.min;
	this.sec = hourCoordinate.sec;
    }

    public double toDegrees() throws ParseException {
	int sign = (hr<0) ? -1 : 1;
	if(min < 0 || min >= 60 || sec < 0.0 || sec >= 60.0)
	    throw new ParseException(null, 0);
	return (Math.abs(hr) + min/60.0 + sec/3600.0)*15*sign;
    }

    public String toString() {
	NumberFormat f = NumberFormat.getInstance(Locale.US);
	((DecimalFormat) f).applyPattern("#0.00#");
	
	if (min == 0 && sec == 0.0)
	    return  hr + "h";
	else if (sec == 0.0)
	    return  hr + "h " + min + "m";
	else
	    return  hr + "h " + min + "m " + f.format(sec) + "s";
    }

}
