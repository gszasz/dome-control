/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

import java.net.*;
import java.io.*;

class UDPConnection {

    public static final int DEFAULT_PACKET_LENGTH = 1024;

    private boolean established;
    private DatagramSocket clientSocket;
    private int packetLength;
    private int packetTimeout;

    public UDPConnection()  {

	packetLength = DEFAULT_PACKET_LENGTH;
	established = false;
	packetTimeout = 0;
    }

    public void establish(String hostname, int port) throws Exception {

	if(established)
	    close();

	clientSocket = new DatagramSocket();
	clientSocket.connect(InetAddress.getByName(hostname), port);
	established = true;
    }

    public void setPacketLength(int packetLength) {
	if(packetLength > 0)
	    this.packetLength = packetLength;
    }

    public void setTimeout(int timeout) throws Exception {	
	clientSocket.setSoTimeout(timeout);
	packetTimeout = timeout;
    }

    public int getPacketLength() {
	return packetLength;
    }

    public int getTimeout() {
	return packetTimeout;
    }

    public void send(String sentence) throws Exception  {
	if(!established)
	    throw new LinkNotEstablishedException("Network Link Not Established");

	byte[] data = new byte[packetLength];
	data = sentence.getBytes();
	DatagramPacket packet;
	packet = new DatagramPacket(data, data.length);
	clientSocket.send(packet);
    }    

    public String receive() throws Exception  {
	if(!established)
	    throw new LinkNotEstablishedException("Network Link Not Established");

	byte[] data = new byte[packetLength];
	DatagramPacket packet;
	packet = new DatagramPacket(data, data.length);
	clientSocket.receive(packet);
	String sentence = new String(packet.getData());
	return sentence;
    }	

    public boolean isEstablished() {
        return established;
    }
    
    public void close() throws Exception {

	if (!established)
	    throw new LinkNotEstablishedException("Network Link Not Established");

	clientSocket.disconnect();
	clientSocket.close();	
    }
}

class NetworkException extends Exception {
    public NetworkException(Exception e) {
	super(e);
    }
    public NetworkException(String message) {
	super(message);
    }
    public NetworkException(String message, Exception e) {
	super(message, e);
    }
}

class LinkNotEstablishedException extends NetworkException {
    public LinkNotEstablishedException(Exception e) {
	super(e);
    }
    public LinkNotEstablishedException(String message) {
	super(message);
    }
    public LinkNotEstablishedException(String message, Exception e) {
	super(message, e);
    }
}
