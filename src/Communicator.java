/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

// TODO: Revise whole schema of Exceptions

// This is supposed to be independent low-level communication layer.
//
// Possible schemas:
//
//  1. Communicator class supplies all dome control methods and overrides contemporary
//     Dome class.
//
//  2. Communicator covers just low-lever command handling and Dome sustains as 
//     intermediate layer. 
//
// Anyway, I would like to encapsulate all low-level communication exceptions inside this 
// class.  This basicaly suggests 2 as the best scenario, allowing to keep abstract Dome
// class to handle all the low-level UDP negotiations in scope of high-level interactions.

//import java.io.*;
//import java.net.*;
//import java.text.*;
//import java.util.regex.*;
//import java.util.Locale;

class Communicator extends Thread {

    // Application properties reference
    private AppProperties ap = DomeControl.customProps;

    // Network Connection internal Properties
    private String host;
    private int port; 
    private int packetTimeout;
    // Alerts internal Properties
    private boolean showDomeErrorAlert;
    private Alert domeErrorAlert;
    private boolean showNetErrorAlert;
    private Alert netErrorAlert;
    private boolean showErrorAlert;
    private Alert errorAlert;

    // State indicators
    private Status connectionState;
    private Status domeState;
    private Status domeSlewState;

    private int status = NOT_CONNECTED;

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

    // UDP Connection
    private UDPConnection domeConnection;
  
    public Communicator() {

	// Inicialization from given application properties
	this.ap = ap;

	// Network Connection
	host = ap.host;
	port = ap.port;
	packetTimeout = ap.packetTimeout;

	// Alerts
	showDomeErrorAlert = ap.domeErrorAlert;
	domeErrorAlert = new Alert("Dome Error", ap.domeErrorAlertFile);
	showDomeErrorAlert = ap.netErrorAlert;
	netErrorAlert = new Alert("Network Error", ap.netErrorAlertFile);
	showErrorAlert = ap.errorAlert;
	errorAlert = new Alert("Error", ap.errorAlertFile);

	// Inicialize UDP Connection
	try {
	  domeConnection = new UDPConnection();
        }
        catch(Exception e) {

	}
    }

    // Set from Application Properties
    public void setFromAppProperties() {

	// If packet length changed
	if(packetLength != ap.packetLength) {
	    domeConnection.setPacketLength(packetLength);
	    packetLength = ap.packetLength;
	}
	// If packet timeout changed
	if (packetTimeout != ap.packetTimeout) {
	    domeConnection.setPacketTimeout(packetTimeout);
	    packetTimeout = ap.packetTimeout;
	}
	// If socket address changed
	if (!host.equal(ap.host) || port != ap.port) {
	    domeConnection.establish(hostname, port);
	    host = ap.host;
	    port = ap.port;
	}
	// If alert changed
	if(showDomeErrorAlert != ap.domeErrorAlert)
	    showDomeErrorAlert = ap.domeErrorAlert;

	// If dome error alert file changed
	if(domeErrorAlert.getAudioFile() != ap.domeErrorAlertFile)
	    netErrorAlert.loadAudoFile(ap.domeErrorAlertFile);
	if(networkErrorAlert.getAudioFile() != ap.networkErrorAlertFile)
	    netErrorAlert.loadAudoFile(ap.domeErrorAlertFile);
	if(errorAlert.getAudioFile() != ap.errorAlertFile)
	    errorAlert.loadAudoFile(ap.errorAlertFile);
	    
	showDomeErrorAlert = ap.domeErrorAlert;
	 = new Alert("Dome Error", ap.domeErrorAlertFile);
	showDomeErrorAlert = ap.netErrorAlert;
	netErrorAlert = new Alert("Network Error", ap.netErrorAlertFile);
	showErrorAlert = ap.errorAlert;
	errorAlert = new Alert("Error", ap.errorAlertFile);
    }
    
    // Attempt to Establish a Connection
    public void connect(String host, int port) throws NetworkException, DomeException, UnknownHostException  {
	try  {
	    domeConnection.setTimeout(packetTimeout);
	    domeConnection.establish(host, port);
	    connectionState = DomeStatus.CONNECTED;
	    this.host = host;
	    this.port = port;
	}
	catch(UnknownHostException e)  {	    
	    throw new UnknownHostException("Unknown Host: '" + host + "'");
	}
	catch(SocketException e) {
	    throw new ConnectionFailedException("Cannot Create UDP Socket", e);
	}
	catch(SecurityException e)  {
	    throw new ConnectionFailedException("Connection to '" + host + ":" + port + "' not Allowed.\nDetails: " + e.getMessage(), e);
	}
	catch(Exception e)  {
	    throw new CommunicatorException(e);
	}
    }	


    public void run()  {
	while(!terminated) {
	    try {
		// Delay
		Thread.sleep(ap.communicatorDelay);

		if(! dome.isConnected())
		    dome.connect(customProps.host, customProps.port);
		domeStatus = dome.getStatus();

		System.out.println("Connection State: " + domeStatus.getConnectionState());
		System.out.println("Dome State: " + domeStatus.getDomeState());
		System.out.println("Dome Slew State: " + domeStatus.getSlewState());
		System.out.println("Dome Slew Mode: " + domeStatus.getSlewMode());

		domeAzimuth = dome.getAzimuth();
	    }
	    catch(ConnectionFailedException e) {
		message = "Not Connected";
		domeStatus.setConnectionState(DomeStatus.NOT_CONNECTED);
	    }
	    catch(NoResponseException e) {
		message = "Network Error";
		longMessage = "<html><b>Network Error:</b><br>Dome is not Responding</html>";
		domeStatus.setConnectionState(DomeStatus.NETWORK_ERROR);
	    }
	    catch(InvalidResponseException e) {
		message = "Network Error";
		longMessage = "<html><b>Network Error:</b><br>Dome is not Responding</html>";
		domeStatus.setConnectionState(DomeStatus.NETWORK_ERROR);
	    }
	    catch(NetworkException e) {
		message = "Network Error";
		longMessage = "<html><b>Network Error:</b><br>" + e.getMessage() + "</html>";
		domeStatus.setConnectionState(DomeStatus.NETWORK_ERROR);
	    }
	    catch(CommunicatorException e) {
		JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	    }
	    catch(Exception e) {
		JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
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

    // Constructors
    public Dome() {
	super();
    }

    // Connect to the dome method
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
	// implementation is not done yet
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


    /*   ConnectionException
	 |- InvalidResponseException
	 |- LinkNotEstablishedException
	 

    */

    class DomeCommand throws ConnectionException {

	private String command;
	private String response;
	private int attempt;

	public DomeCommand(String command, String response) {
	    this.command = command;
	    this.response = response;
	}

        public void send() {
	    
	    int attempt = 0;

	    while(attempt < 3) {

		try {
		    if(domeConnection != null) {
			domeConnection.send(command);
			String sentence = domeConnection.receive();
			if(! Pattern.matches(response, sentence))
			    throw new InvalidResponseException(sentence.trim());
			return;
		    }
		    else
			throw new LinkNotEstablishedException();
		}
		catch (LinkNotEstablishedException e) {
		    throw e;
		}
		catch (PortUnreachableException e) {		
		    attempt++;
		    if(attempt>3) {
			domeConnection.close();
			domeConnection = null;
		    }
		}
		catch (SecurityException e)  {
		    attempt++;
		    Console.
		    throw new ConnectionFailedException("Connection not Allowed. " + e.getMessage(), e);
		}
		catch (SocketTimeoutException e)  {
		    throw new NoResponseException(e);
		}
		catch (InvalidResponseException e) {
		    throw e;
		}
		catch (Exception e) {
		    throw new ConnectionException(e);
		}
	    }

	    /* If connection failed on third attempt terminate UDP socket */
	    terminateConnection();
	    throw new ConnectionFailedException("Connection Terminated", e);
	}

	private void terminateConnection() throws ConnectionFailedException {
	    domeConnection.close();
	    domeConnection = null;
	    throw new ConnectionFailedException("Connection Terminated", e);
	}

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


ConnectionState connectionState 
    
    println("This is the message" + p
    


public enum ConnectionState {

    NOT_CONNECTED ("Dome is not Connected"),
    CONNECTED     ("Dome is Connected"),
    
    private final String message;

    public ConnectionStates(String message) {
	this.message = message;
    }

    public String toString() {

	return message;
    }    
}

	    
class DomeStatus {

    public static final Status UNKNOWN = new Status(-10, "Unknown");
    public static final Status UNKNOWN = new Status(0, "Unknown");
    public static final Status UNKNOWN = new Status(0, "Unknown");
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

    String toString() {
	return 
    }
}

//
// Communicator Exceptions
//
class CommunicatorException extends Exception {
    public CommunicatorException(Exception e) {
	super(e);
    }
    public CommunicatorException(String message) {
	super(message);
    }
    public CommunicatorException(String message, Exception e) {
	super(message, e);
    }
}

class ConnectionFailedException extends CommunicatorException {
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

class ConnectionErrorException extends CommunicatorException {
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

class NoResponseException extends CommunicatorException {
    public NoResponseException() {
	super("Dome not Responding.");
    }
    public NoResponseException(Exception e) {
	super("Details: " + e.getMessage(), e);
    }
}

class InvalidResponseException extends CommunicatorException {
    public InvalidResponseException() {
	super("Invalid Response.");
    }
    public InvalidResponseException(String sentence) {
	super("Invalid Response: '" + sentence + "'");
    }
}


// Probably better as "DomeException" child... We will see later.
class SpeedOutOfRangeException extends CommunicatorException {
    public SpeedOutOfRangeException(int speed) {
	super("Speed Out of Range.");
    }
}

// Probably better as "DomeException" child... We will see later.
class AzimuthOutOfRangeException extends CommunicatorException {
    public AzimuthOutOfRangeException(double azimuth) {
	super("Azimuth Out of Range.");
    }
}
