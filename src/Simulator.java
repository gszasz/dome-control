/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

// TODO: This file is under development... Not checked-in yet.

import java.io.*;
import java.net.*;
import java.lang.*;
import java.text.*;
import java.util.Locale;

public class Simulator extends Thread {

    public static final int PORT = 1999;

    DatagramSocket serverSocket;
    DatagramPacket receivePacket;

    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];

    VirDome virDome;
    double virDomeAzimuth;  // DEPRECATED

    public Simulator() {
	
	// Initialize UDP Socket
	try {
	    serverSocket = new DatagramSocket(PORT);
	}
	catch(SocketException e) {
	    throw new SimulatorException("Cannot establish UDP socket on port " + port + ".\nDetails: " + e.getMessage(), e);
	}
	catch(SecurityException e) {
	    throw new SimulatorException("Port " + port + " not allowed for UDP socket.\nDetails: " + e.getMessage(), e);
	}
	catch(Exception e) {
	    throw new SimulatorException("Initialization of UDP socket on port " + port + " failed.\nDetails: " + e.getMessage(), e);
	}

	// Reset virtual dome azimuth (DEPRECATED)
	virDomeAzimuth = 0;

	// Start virtual dome thread
	virDome = new VirDome();
	virDome.start();
    }

    public void run()  {

	int autoMode = 0;

	while(true) {

	    System.out.println("Starting Simulator thread...");

	    try {

		// Allocate space for received datagram
		receivePacket = new DatagramPacket(receiveData, receiveData.length);

		// Receive datagram
		serverSocket.receive(receivePacket);

		String command = new String(receivePacket.getData());

		// Get IP address of sender
		InetAddress IPAddress = receivePacket.getAddress();

		// Get port number of sender
		int port = receivePacket.getPort();

		command = command.substring(0,command.indexOf("\r\n")+2);
	    }
	    catch(Exception e) {
		// TODO: datagram receive catch block
	    }

	    // Output the received datagram
	    System.out.println("Simulator: Packet received: " + command.replaceAll("\r\n","\\r\\n"));    
    
	    if(command.equals("/GA\r\n"))  {
		System.out.println("Command received: Get azimuth");
		virDomeAzimuth = virDome.getAzimuth();

		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		((DecimalFormat)nf).applyPattern("##0.00000;-##0.00000");

		String sendValue = "A " + nf.format(virDomeAzimuth) + "\r\n";
		sendData = sendValue.getBytes();
		System.out.println("Dome Azimuth: " + sendValue);
		System.out.println("Dome Speed: " + virDome.getSpeed());
		// Create datagram to send to client
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		// Write out datagram to socket
		serverSocket.send(sendPacket);
		System.out.println("Packet sent: " + sendValue.replaceAll("\r\n", "\\r\\n"));
	    }	
	    else if(command.startsWith("/SA"))  {
		command = command.substring(3).trim();
		if(command.startsWith("+"))
		   command = command.substring(1);
		double azimuth = Double.parseDouble(command);
		virDome.slewToAzimuth(azimuth);
		System.out.println("Command received: Slew to Azimuth" + azimuth);
		String sendValue = "OK\r\n";
		sendData = sendValue.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		serverSocket.send(sendPacket);
		System.out.println("Packet sent: " + sendValue.replaceAll("\r\n", "\\r\\n"));
	    }
	    else if(command.startsWith("/MA"))  {
		command = command.substring(3).trim();
		if(command.startsWith("+"))
		   command = command.substring(1);
		int speed = Integer.parseInt(command);
		System.out.println("Command received: Manual Slew " + speed);
		virDome.manualControl(speed);
		String sendValue = "OK\r\n";
		sendData = sendValue.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		serverSocket.send(sendPacket);
		System.out.println("Packet sent: " + sendValue.replaceAll("\r\n", "\\r\\n"));
	    }
	    else if(command.equals("/QA\r\n"))  {
		System.out.println("Command received: Manual Stop");
		virDome.manualControl(0);
		String sendValue = "OK\r\n";
		sendData = sendValue.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		serverSocket.send(sendPacket);
		System.out.println("Packet sent: " + sendValue.replaceAll("\r\n", "\\r\\n"));
	    }
	    else if(command.equals("/GS\r\n"))  {
		System.out.println("Command received: Get Dome State");
		String sendValue = "S R 0 D 0 A " + virDome.getStatus() + "\r\n";
		sendData = sendValue.getBytes();
		// Create datagram to send to client
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		// Write out datagram to socket
		serverSocket.send(sendPacket);
		System.out.println("Packet sent: " + sendValue.replaceAll("\r\n", "\\r\\n"));
	    }
	    else if(command.equals("/TA0\r\n"))  {
		System.out.println("Command received: Get Register State");
		String state = Integer.toString(virDome.getSlewStatus(),16).toUpperCase();
		String sendValue = "KREG " + state + " " + virDome.getSlewMode() +" \r\n";
		System.out.println(sendValue);
		sendData = sendValue.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		serverSocket.send(sendPacket);
		System.out.println("Packet sent: " + sendValue.replaceAll("\r\n", "\\r\\n"));
	    } 
	//	    else if(command.substring(0,3).equals("/SA"))
	    try {
		Thread.sleep(50);
	    } catch (InterruptedException e) { }
	}
    }

    class VirDome extends Thread {

	public final static int STOPPED = 0x00;
	public final static int SLEWING_RIGHT = 0x08;
	public final static int SLEWING_LEFT = 0x04;
	public final static int FAST_SLEWING_RIGHT = 0x10;
	public final static int FAST_SLEWING_LEFT = 0x06;

	public final static int MANUAL = 0x00;
	public final static int AUTO = 0x01;

	public final static int READY = 0x01;

	private int speed = 0;
	private double setAzimuth = 0.0;
	private double azimuth = 0.0;
	private int slewStatus = STOPPED;
	private int slewMode = MANUAL;
	private int status = READY;

	public VirDome() {
	    super("VirDome");
	}

	public void manualControl(int speed)  {
	    slewMode = MANUAL;
	    if(speed > 2 || speed < -2)
		this.speed = 0;	    
	    else
		this.speed = speed;
	}

	public void slewToAzimuth(double azimuth) {
	    this.slewMode = AUTO;
	    this.setAzimuth = azimuth;
	}

	public double getAzimuth() {
	    return azimuth;
	}

	public int getSpeed() {
	    return speed;
	}

	public int getSlewStatus() {
	    return slewStatus;
	}

	public int getSlewMode() {
	    return slewMode;
	}

	public int getStatus() {
	    return status;
	}

	public void run() {
	    double absAzimuth;
	    double absSetAzimuth;
    
	    System.out.println("Starting VirDome thread...");
    
	    while (true) {
		while (speed != 0 || slewMode == AUTO) {
		    if(slewMode == AUTO) {
			absAzimuth = ((azimuth < 0) ? azimuth + 360 : azimuth);
			absSetAzimuth = ((setAzimuth < 0) ? setAzimuth + 360 : setAzimuth);

			if(Math.abs(absAzimuth - absSetAzimuth) == 0) {
			    speed = 0;
			    slewMode = MANUAL;
			}
			else if(Math.abs(absAzimuth - absSetAzimuth) <= 10)
			    speed = 1;
			else
			    speed = 2;

			if(setAzimuth < 0)
			    speed *= -1;
		    }

		    switch(speed) {
		    case  2: slewStatus = FAST_SLEWING_RIGHT; break;
		    case  1: slewStatus = SLEWING_RIGHT;      break;
		    case -1: slewStatus = SLEWING_LEFT;       break;
		    case -2: slewStatus = FAST_SLEWING_LEFT;  break;
		    default: break;
		    }

		    System.out.print("Slewing...");
		    if(speed > 0)
			azimuth++;
		    else if (speed < 0)
			azimuth --;

		    if(Math.abs(azimuth) == 360)
			azimuth = 0.0;

		    System.out.println(" Speed: " + speed + " Azimuth: " + azimuth);

		    try {
			if(Math.abs(speed) == 1)
			    Thread.sleep(1000);
			else if(Math.abs(speed) == 2)
			    Thread.sleep(500);
		    } catch (InterruptedException e){ }	  
		}

		slewStatus = STOPPED;

		try {
		    Thread.sleep(100);
		} catch (InterruptedException e) { }
	    }
	}
    }

    public static void main(String args[]) throws Exception {

        Simulator simulator = new Simulator();

        simulator.start();
    }


//    public void move(direction = left) {


//     public void stepRight() {
// 	virDomeAzimuth += (moveSpeed == 1) ? 5 : 10;
// 	if(virDomeAzimuth > 360.0)
// 	    virDomeAzimuth -= 360.0;
//     }

//     public void stepLeft() {
// 	virDomeAzimuth -= (moveSpeed == 1) ? 5 : 10;
// 	if(virDomeAzimuth < -360.0)
// 	    virDomeAzimuth += 360.0;
//     }
}


//
// Simulator Exceptions
//
class SimulatorException extends Exception {
    public SimulatorException(Exception e) {
	super(e);
    }
    public SimulatorException(String message) {
	super(message);
    }
    public SimulatorException(String message, Exception e) {
	super(message, e);
    }
}
