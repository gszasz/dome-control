/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.regex.*;
import java.util.Locale;

class Dome {
    // Inicialization from Properties
    private AppProperties ap = DomeControl.customProps;
    private int packetTimeout = ap.packetTimeout;
    private double domeRadius = ap.domeRadius;
    private double slitWidth = ap.slitWidth;
    private double slitDepth = ap.slitWidth;
    private double zeroAzimuth = ap.zeroAzimuth;

    private UDPConnection domeConnection = new UDPConnection();
    private String host;
    private int port;
    private double azimuth;    
    private int speed = 2;

    private double previousSlewToAzimuth = -1.0;

    private int connectionState = DomeStatus.UNKNOWN;
    private int domeState = DomeStatus.UNKNOWN;
    private int slewState = DomeStatus.UNKNOWN;
    private int slewMode = DomeStatus.UNKNOWN;
    private String message = "";
    private String longMessage = null;

    // Constructors
    public Dome() {
	super();
    }

    public Dome(String host, int port) throws NetworkException, DomeException, UnknownHostException {
	connect(host, port);
    }

    public double getEffectiveRadius() {
	
	return domeRadius + slitDepth;	
    }

    public double getRadius() {

	return domeRadius;

    }

    public double getSlitWidth() {

	return slitWidth;
    }

    public double getSlitDepth() {

	return slitDepth;
    }

    public void setRadius(double domeRadius) {

	if(domeRadius > 0)
	    this.domeRadius = domeRadius;
    }

    public void setSlitWidth(double slitWidth) {

	if(slitWidth > 0 && slitWidth < 2*domeRadius)
	    this.slitWidth = slitWidth;
    }

    public void setSlitDepth(double slitDepth) {

	if(slitDepth > 0)
	    this.slitDepth = slitDepth;
    }

    // Connect to the dome method
    public void connect(String host, int port) throws NetworkException, DomeException, UnknownHostException  {
	try  {
	    domeConnection.setTimeout(packetTimeout);
	    domeConnection.establish(host, port);
	    connectionState = DomeStatus.CONNECTED;
	    this.host = host;
	    this.port = port;
	}
	catch (UnknownHostException e)  {	    
	    throw new UnknownHostException("Unknown Host: '" + host + "'");
	}
	catch (SocketException e) {
	    throw new ConnectionFailedException("Cannot Create UDP Socket", e);
	}
	catch (SecurityException e)  {
	    throw new ConnectionFailedException("Connection to '" + host + ":" + port + "' not Allowed.\nDetails: " + e.getMessage(), e);
	}
	catch (Exception e)  {
	    throw new DomeException(e);
	}
    }	

    public void setTimeout(int packetTimeout) throws NetworkException, DomeException {
	try {
	    domeConnection.setTimeout(packetTimeout);
	    this.packetTimeout = packetTimeout;
	}
	catch (Exception e)  {
	    throw new DomeException(e);
	}
    }

    public void setHost(String host) throws NetworkException, DomeException, UnknownHostException {
	try  {
	    if(domeConnection != null) {
	        domeConnection.setTimeout(packetTimeout);
		domeConnection.establish(host, port);
		connectionState = DomeStatus.CONNECTED;
		this.host = host;
	    }
	}
	catch (UnknownHostException e)  {	    
	    throw new UnknownHostException("Unknown Host: '" + host + "'");
	}
	catch (SocketException e) {
	    throw new ConnectionFailedException("Cannot Create UDP Socket", e);
	}
	catch (SecurityException e)  {
	    throw new ConnectionFailedException("Connection to '" + host + ":" + port + "' not Allowed.\nDetails: " + e.getMessage(), e);
	}
	catch (Exception e)  {
	    throw new DomeException(e);
	}
    }

    public void setPort(int port) throws NetworkException, DomeException, UnknownHostException {
	try  {
	    if(domeConnection != null) {
	        domeConnection.setTimeout(packetTimeout);
		domeConnection.establish(host, port);
		connectionState = DomeStatus.CONNECTED;
		this.port = port;
	    }
	}
	catch (UnknownHostException e)  {	    
	    throw new UnknownHostException("Unknown Host: '" + host + "'");
	}
	catch (SocketException e) {
	    throw new ConnectionFailedException("Cannot Create UDP Socket", e);
	}
	catch (SecurityException e)  {
	    throw new ConnectionFailedException("Connection to '" + host + ":" + port + "' not Allowed.\nDetails: " + e.getMessage(), e);
	}
	catch (Exception e)  {
	    throw new DomeException(e);
	}
    }

    public boolean isConnected() {
	if(domeConnection != null)
	    return true;
	else
	    return false;
    }

    public void setZeroAzimuth(double zeroAzimuth)  {
	while(zeroAzimuth < 0.0)
	    zeroAzimuth += 360.0;
	while(zeroAzimuth >= 360.0)
	    zeroAzimuth -= 360.0;

	this.zeroAzimuth = zeroAzimuth;
    }

    public void setFromAppProperties() {
	setZeroAzimuth(ap.zeroAzimuth);
    }

    public double getZeroAzimuth() {
	return zeroAzimuth;
    }

    public synchronized double getAzimuth() throws NetworkException, DomeException {

	if(! isConnected())
	    return -1.0;

	double azimuth = getHardwareAzimuth() + zeroAzimuth;

	if(azimuth < 0.0)
	    return azimuth + 360.0;
	else if (azimuth > 360.0)
	    return azimuth - 360.0;
	else
	    return azimuth;
    }

    private synchronized double getHardwareAzimuth() throws NetworkException, DomeException {

	try {
	    domeConnection.send("/GA\r\n");
	    String sentence = domeConnection.receive();
	    
// 	    System.out.print("/GA\\r\\n -> ");
// 	    for(int i=0 ; i< sentence.length(); i++) {
// 		if(sentence.charAt(i) == '\n') 
// 		    System.out.print("\\n");
// 		else if(sentence.charAt(i) == '\r') 
// 		    System.out.print("\\r");
// 		else 
// 		    System.out.print(sentence.charAt(i));
// 		}	
// 	    System.out.println("");
		
	    if(! Pattern.matches("A [+-]?[0-9]+\\.[0-9]+\r\n.*", sentence))
		throw new InvalidResponseException(sentence.trim());
		
	    sentence = sentence.substring(1).trim();

	    //System.out.println(sentence);

	    if(sentence.startsWith("+"))
		sentence = sentence.substring(1);
	    double hardwareAzimuth = Double.parseDouble(sentence);
	    
	    return hardwareAzimuth;
	}
	catch (LinkNotEstablishedException e) {
	    throw e;
	}
	catch (PortUnreachableException e) {
	    throw new ConnectionFailedException("Destination Port Unreachable", e);
	}
	catch (SecurityException e)  {
	    throw new ConnectionErrorException("Connection not Allowed. " + e.getMessage(), e);
	}
	catch (SocketTimeoutException e)  {
	    throw new NoResponseException(e);
	}
	catch (InvalidResponseException e) {
	    throw e;
	}
	catch (Exception e) {
	    throw new DomeException(e);
	}

    }

    public synchronized DomeStatus getStatus() throws NetworkException, DomeException {
	connectionState = DomeStatus.UNKNOWN;	
	domeState = DomeStatus.UNKNOWN;
	slewState = DomeStatus.UNKNOWN;	
	slewMode = DomeStatus.UNKNOWN;

	try {
	    domeConnection.send("/GS\r\n");
	    String sentence = domeConnection.receive();
// 	    System.out.print("/GS\\r\\n -> ");	    
// 	    for(int i=0 ; i< sentence.length(); i++) {
// 		if(sentence.charAt(i) == '\n') 
// 		    System.out.print("\\n");
// 		else if(sentence.charAt(i) == '\r') 
// 		    System.out.print("\\r");
// 		else 
// 		    System.out.print(sentence.charAt(i));
// 	    }
// 	    System.out.println("");

	    if(!  Pattern.matches("S R [0-9A-F]+ D [0-9A-F]+ A [01] \r\n.*", sentence)) {
		System.out.println("/GS No match");
		throw new InvalidResponseException();
	    }
	    domeState = Integer.parseInt(sentence.split("\\s+")[6]);

	    domeConnection.send("/TA0\r\n");
	    sentence = domeConnection.receive();

// 	    System.out.print("/TA0\\r\\n -> ");	    
// 	    for(int i=0 ; i< sentence.length(); i++) {
// 		if(sentence.charAt(i) == '\n') 
// 		    System.out.print("\\n");
// 		else if(sentence.charAt(i) == '\r') 
// 		    System.out.print("\\r");
// 		else 
// 		    System.out.print(sentence.charAt(i));
// 	    }
// 	    System.out.println("");
	    if(!  Pattern.matches("KREG [0-9A-F]+ [0-9A-F]+ \r\n.*", sentence)) {
		System.out.println("/TA0 No match");
		throw new InvalidResponseException();
	    }
	    slewState = Integer.parseInt(sentence.split("\\s+")[1], 16);
	    slewMode = Integer.parseInt(sentence.split("\\s+")[2]);

	    connectionState = DomeStatus.CONNECTED;
	}
	catch (LinkNotEstablishedException e) {
	    connectionState = DomeStatus.NOT_CONNECTED;
	    message = "Not Connected";
	    longMessage = "Dome is Not Connected.";
	}
	catch (PortUnreachableException e) {
	    connectionState = DomeStatus.NOT_CONNECTED;
	    message = "Not Connected";
	    longMessage = "Dome is Not Connected.";
	}
	catch (SecurityException e)  {
	    connectionState = DomeStatus.NOT_ALLOWED;
	    message = "Not Allowed";
	    longMessage = "Connection to Dome is Not Allowed.";
	}
	catch (SocketTimeoutException e)  {
	    connectionState = DomeStatus.NOT_RESPONDING;
	    message = "No Response";
	    longMessage = "Dome is Not Responding.";
	}
	catch (InvalidResponseException e) {
	    connectionState = DomeStatus.ERROR;
	    message = "Error";
	    longMessage = "Invalid Response from Dome.";
	    throw e;
	}
	catch (Exception e) {	   
	    throw new DomeException(e);
	}
	return(new DomeStatus(connectionState, domeState, slewState, slewMode));
    }

    public void setSpeed(int speed) throws DomeException {
	if(speed == 1 || speed == 2)
	    this.speed = speed;
	else
	    throw new SpeedOutOfRangeException(speed);
    }

    public synchronized void slewLeft() throws DomeException {
	try {
	    interruptAutoMode();
	    domeConnection.send("/MA -" + speed + "\r\n");
	    System.out.println("Slewing left");
	    String sentence = domeConnection.receive();
	    System.out.println(sentence.trim());
	    if(! sentence.startsWith("OK\r\n"))
		throw new NoResponseException();
	}
	catch (PortUnreachableException e) {
	    throw new ConnectionFailedException("Destination Port Unreachable", e);
	}
	catch (SecurityException e)  {
	    throw new ConnectionErrorException("Connection Not Allowed. " + e.getMessage(), e);
	}
	catch (SocketTimeoutException e)  {
	    throw new NoResponseException(e);
	}
	catch (InvalidResponseException e) {
	    throw e;
	}
	catch (Exception e) {
	    throw new DomeException(e);
	}
    }	

    public synchronized void slewRight() throws DomeException {
	try {		
	    interruptAutoMode();
	    domeConnection.send("/MA +" + speed + "\r\n");
	    System.out.println("Slewing right");
	    String sentence = domeConnection.receive();
	    System.out.println(sentence.trim());
	    if(! sentence.startsWith("OK\r\n"))
		throw new NoResponseException();
	}
	catch (PortUnreachableException e) {
	    throw new ConnectionFailedException("Destination Port Unreachable", e);
	}
	catch (SecurityException e)  {
	    throw new ConnectionErrorException("Connection not Allowed. " + e.getMessage(), e);
	}
	catch (SocketTimeoutException e)  {
	    throw new NoResponseException(e);
	}
	catch (InvalidResponseException e) {
	    throw e;
	}
	catch (Exception e) {
	    throw new DomeException(e);
	}
    }

    public synchronized void stop() throws DomeException {
	try {
	    interruptAutoMode();
	    domeConnection.send("/QA\r\n");
	    System.out.println("Stopping");
	    String sentence = domeConnection.receive();
	    if(! sentence.startsWith("OK\r\n"))
		throw new NoResponseException();
	}
	catch (PortUnreachableException e) {
	    throw new ConnectionFailedException("Destination Port Unreachable", e);
	}
	catch (SecurityException e)  {
	    throw new ConnectionErrorException("Connection not Allowed. " + e.getMessage(), e);
	}
	catch (SocketTimeoutException e)  {
	    throw new NoResponseException(e);
	}
	catch (InvalidResponseException e) {
	    throw e;
	}
	catch (Exception e) {
	    throw new DomeException(e);
	}
    }

    public synchronized void interruptAutoMode() throws DomeException {
	if(slewMode == DomeStatus.AUTO) {
	    try {
		double actualAzimuth = getHardwareAzimuth();

		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		((DecimalFormat)nf).applyPattern("+##0.00000;-##0.00000");

		domeConnection.send("/SA " + nf.format(actualAzimuth) + "\r\n");
		System.out.println("/SA " + nf.format(actualAzimuth));
		String sentence = domeConnection.receive();
		if(! sentence.startsWith("OK\r\n"))
		    throw new NoResponseException();
		Thread.sleep(100);
	    }
	    catch (PortUnreachableException e) {
		throw new ConnectionFailedException("Destination Port Unreachable", e);
	    }
	    catch (SecurityException e)  {
		throw new ConnectionErrorException("Connection not Allowed. " + e.getMessage(), e);
	    }
	    catch (SocketTimeoutException e)  {
		throw new NoResponseException(e);
	    }
	    catch (InvalidResponseException e) {
		throw e;
	    }
	    catch (Exception e) {
		throw new DomeException(e);
	    }
	}
    }

    public synchronized void slewToAzimuth(double azimuth) throws DomeException {	
	if(slewMode == DomeStatus.AUTO && azimuth == previousSlewToAzimuth)
	    return;

	try {
      	    interruptAutoMode();
	    double actualHardwareAzimuth = getHardwareAzimuth();
	    double actualAzimuth = actualHardwareAzimuth + zeroAzimuth;
	    double difference;

	    if(actualAzimuth < 0.0)
		actualAzimuth += 360.0;
	    else if (actualAzimuth > 360.0)
		actualAzimuth -= 360.0;

	    difference = azimuth - actualAzimuth;

	    if(Math.abs(difference) > 180) {
		if(difference > 0)
		    difference -= 360;
		else
		    difference += 360;
	    }

	    double hardwareAzimuth = actualHardwareAzimuth + difference;

	    NumberFormat nf = NumberFormat.getInstance(Locale.US);
	    ((DecimalFormat)nf).applyPattern("+##0.00000;-##0.00000");

	    domeConnection.send("/SA " + nf.format(hardwareAzimuth) + "\r\n");

	    System.out.println("Command: " + "/SA " + nf.format(hardwareAzimuth) + "\r\n");
	    String sentence = domeConnection.receive();
	    if(! sentence.startsWith("OK\r\n"))
		throw new NoResponseException();
	}
	catch (PortUnreachableException e) {
	    throw new ConnectionFailedException("Destination Port Unreachable", e);
	}
	catch (SecurityException e)  {
	    throw new ConnectionErrorException("Connection not Allowed. " + e.getMessage(), e);
	}
	catch (SocketTimeoutException e)  {
	    throw new NoResponseException(e);
	}
	catch (InvalidResponseException e) {
	    throw e;
	}
	catch (Exception e) {
	    throw new DomeException(e);
	}

	previousSlewToAzimuth = azimuth;
    }

    public synchronized void park() throws DomeException {
	slewToAzimuth(zeroAzimuth);
    }

    public void disconnect() throws DomeException {
	try {
	    domeConnection.close();    
	}
	catch (SocketException e)  {
	    throw new ConnectionErrorException("Cannot Close UDP Socket", e);
	}
	catch (Exception e) {
	    throw new DomeException(e);
	}
    }
}

class DomeStatus {

    // Common Constants
    public static final int UNKNOWN = -10;
    // Connection State Constants
    public static final int NETWORK_ERROR = -5;
    public static final int NOT_CONNECTED = -4;
    public static final int NOT_ALLOWED = -3;
    public static final int NOT_RESPONDING = -1;
    public static final int CONNECTED = 0;
    // Dome State Constants
    public static final int ERROR = 0;
    public static final int READY = 1;
    // Dome Slew State Constants
    public static final int STOPPED = 0x00;
    public static final int SLEWING_RIGHT = 0x08;
    public static final int SLEWING_LEFT = 0x04;
    public static final int FAST_SLEWING_RIGHT = 0x10;
    public static final int FAST_SLEWING_LEFT = 0x06;
    // Dome Slew Mode Constants
    public static final int MANUAL = 0;
    public static final int AUTO = 1;

    private int connectionState, domeState, slewState, slewMode; 

    public DomeStatus() {
	this.connectionState = UNKNOWN; 
	this.domeState = UNKNOWN;
	this.slewState = UNKNOWN;
	this.slewMode = UNKNOWN;
    }

    public DomeStatus(int connectionState) {
	this.connectionState = connectionState;
	this.domeState = UNKNOWN;
	this.slewState = UNKNOWN;
	this.slewMode = UNKNOWN;
    }

    public DomeStatus(int connectionState, int domeState, int slewState, int slewMode) {
	this.connectionState = connectionState;
	this.domeState = domeState;
	this.slewState = slewState;
	this.slewMode = slewMode;
    }

    public int getConnectionState() {
	return connectionState;
    }

    public int getDomeState() {
	return domeState;
    }

    public int getSlewState()  {
	return slewState;
    }

    public int getSlewMode()  {
	return slewMode;
    }

    public void setConnectionState(int connectionState) {
	this.connectionState = connectionState;
    }

    public void setDomeState(int domeState) {
	this.domeState = domeState;
    }

    public void setSlewState(int slewState)  {
	this.slewState = slewState;
    }

    public void setSlewMode(int slewMode)  {
	this.slewMode = slewMode;
    }
}

class DomeException extends Exception {
    public DomeException(Exception e) {
	super(e);
    }
    public DomeException(String message) {
	super(message);
    }
    public DomeException(String message, Exception e) {
	super(message, e);
    }
}

class SpeedOutOfRangeException extends DomeException {
    public SpeedOutOfRangeException(int speed) {
	super("Speed Out of Range.");
    }
}

class AzimuthOutOfRangeException extends DomeException {
    public AzimuthOutOfRangeException(double azimuth) {
	super("Azimuth Out of Range.");
    }
}

class ConnectionFailedException extends DomeException {
    public ConnectionFailedException(Exception e) {
	super(e);
    }
    public ConnectionFailedException(String message) {
	super(message);
    }
    public ConnectionFailedException(String message, Exception e) {
	super(message, e);
    }
}

class ConnectionErrorException extends DomeException {
    public ConnectionErrorException(Exception e) {
	super(e);
    }
    public ConnectionErrorException(String message) {
	super(message);
    }
    public ConnectionErrorException(String message, Exception e) {
	super(message, e);
    }
}

class NoResponseException extends DomeException {
    public NoResponseException() {
	super("Dome not Responding.");
    }
    public NoResponseException(Exception e) {
	super("Details: " + e.getMessage(), e);
    }
}

class InvalidResponseException extends DomeException {
    public InvalidResponseException() {
	super("Invalid Response.");
    }
    public InvalidResponseException(String sentence) {
	super("Invalid Response: '" + sentence + "'");
    }
}
