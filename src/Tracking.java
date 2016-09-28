/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

import javax.vecmath.*;
import javax.swing.*;        // For JPanel, etc.

class Tracking extends Thread  {
    
    // Inicialization from Properties
    private AppProperties ap = DomeControl.customProps;

    private boolean suspended = true;
    private boolean terminated = false;
    
    private int domeTrackingMode = ap.domeTrackingMode;
    private double domeTrackingDelay = ap.domeTrackingDelay;

    // Optimized Dome Tracking Variables
    private boolean centerTransit = false;
    private double prevScopeRelativeAzimuth = 180;

    // Vectors
    private Vector3d legAxisPosition, legAxis, polarAxis, decAxis;
    private Vector3d scopeOffset, scopeDirection, scopePosition;
    private Vector3d[] sensorPositions;
    // Mount Parameters
    private double hourAngle, rightAscension, declination, latitude, domeRadius, slitWidth;
    private double wallHeight, excentricDistance, decAxisLength, polarAxisLength, legAxisLength, scopeOffsetLength;
    private double scopePositionAngle, scopeDiameter;
    private int sensorCount;

    // Azimuth
    private double domeAzimuth, scopeAzimuth, objectAzimuth;

    // Intersections
    private Point3d scopeAxisIntersection;
    private Point3d[] sensorIntersections;
    
    // Siderial Time Clock
    private LSTClock siderialClock = new LSTClock(ap.longitude);

    // References to Dome and Mount objects
    private Dome dome;
    private Mount mount;

    public Tracking(Dome dome, Mount mount, ObjectInfo object) {

    // Initialize references to external Dome and Mount objects
	this.dome = dome;
	this.mount = mount;

        // Initialize object coordinates
	rightAscension = object.getRightAscension();
	declination = object.getDeclination();
	mount.setDeclination(declination);

	// Initialize Siderial Clock
	hourAngle = siderialClock.getAngle() - rightAscension;
	if(hourAngle < 0)
	    hourAngle += 360;
	mount.setHourAngle(hourAngle);

	// Initialize Vectors
	legAxisPosition = mount.getLegAxisPosition();
	legAxis = mount.getLegAxis();
	polarAxis = mount.getPolarAxis();
	decAxis = mount.getDecAxis();
	scopeOffset = mount.getScopeOffset();
	scopeDirection = mount.getScopeDirection();
	scopePosition = mount.getScopePosition();
	sensorPositions = mount.getSensorPositions();
	
	// Initialize parameters
	hourAngle = mount.getHourAngle();
	declination = mount.getDeclination();
	latitude = mount.getLatitude();
	domeRadius = dome.getEffectiveRadius();
	slitWidth = dome.getSlitWidth();
	wallHeight = -legAxisPosition.z;
	excentricDistance = legAxisPosition.x;
	legAxisLength = legAxis.length();
	polarAxisLength = polarAxis.length();
	decAxisLength = decAxis.length();	    
	scopeOffsetLength = scopeOffset.length();
	scopePositionAngle = mount.getScopePositionAngle();
	scopeDiameter = mount.getScopeDiameter();
	sensorCount = mount.getSensorCount();
	
	// Initialize Scope and Sensor's Intersections
	computeIntersections();
	
	// Compute Scope and Object Azimuth
	scopeAzimuth = computeAzimuth(scopeAxisIntersection);
	objectAzimuth = computeAzimuth(mount.getScopeDirection());
    }

    public void setFromAppProperties() {
	siderialClock.setLongitude(ap.longitude);
	domeTrackingMode = ap.domeTrackingMode;
	domeTrackingDelay = ap.domeTrackingDelay;
	refresh();
    }

    public void setObject(ObjectInfo object) {
	rightAscension = object.getRightAscension();
	declination = object.getDeclination();

	hourAngle = siderialClock.getAngle() - rightAscension;
	if(hourAngle < 0)
	    hourAngle += 360;

	mount.setHourAngle(hourAngle);
	mount.setDeclination(declination);

	refresh();
    }
    
    public void refresh() {
	// Update Parameters
	hourAngle = mount.getHourAngle();
	decAxis = mount.getDecAxis();
	scopeOffset = mount.getScopeOffset();
	scopeDirection = mount.getScopeDirection();
	scopePosition = mount.getScopePosition();
	sensorPositions = mount.getSensorPositions();
	
	// Recompute Scope Axis and Sensors' Intersections
	computeIntersections();
		
	// Compute Scope Azimuth
	scopeAzimuth = computeAzimuth(scopeAxisIntersection);		       
		
	// Compute Object Azimuth
	objectAzimuth = computeAzimuth(mount.getScopeDirection());

	if(! suspended) {
	    try {
		trackDome();
	    } 
	    catch(DomeException e) { }
	    catch(NetworkException e) { }
	}	
    }

    public void computeIntersections() {
	// Compute Scope Intersection
	scopeAxisIntersection = computeIntersection(scopePosition, scopeDirection, domeRadius);
	
	// Compute Sensors' Intersections
	sensorIntersections = new Point3d[sensorCount];
	for(int i=0; i<sensorPositions.length; i++)
	    sensorIntersections[i] = computeIntersection(sensorPositions[i], scopeDirection, domeRadius);	    
	}	

    private Point3d computeIntersection(Vector3d r0, Vector3d n, double R)  {
	Vector3d r = new Vector3d();
	
	double N = n.dot(r0);
	double c = -N + Math.sqrt(Math.pow(N,2) - r0.lengthSquared() + Math.pow(R,2));
	
	r.scale(c, n);
	r.add(r0);
	    
	return new Point3d(r);
    }

    public double computeAzimuth(Tuple3d r) {
	return computeAzimuth(new Vector2d(r.x, r.y));
    }

    public double computeAzimuth(Tuple2d r) {
	    
	Vector2d rDir = new Vector2d(r);
	rDir.normalize();
	    
	double azimuth = 0;
	if(rDir.x > 0 && rDir.y > 0) 
	    azimuth = Math.acos(rDir.x);
	
	else if(rDir.x < 0 && rDir.y > 0)
	    azimuth = Math.acos(rDir.x);
	
	else if(rDir.x < 0 && rDir.y < 0)
	    azimuth = 2*Math.PI - Math.acos(rDir.x);
	
	else if(rDir.x > 0 && rDir.y < 0)
	    azimuth = 2*Math.PI - Math.acos(rDir.x);
	
	return Math.toDegrees(azimuth);
    }

    public double computeRelativeAzimuth(Tuple3d r) {
	return computeRelativeAzimuth(new Vector2d(r.x, r.y));
    }

    public double computeRelativeAzimuth(Tuple2d r) {
	double azimuth = computeAzimuth(r);
	if(azimuth > 180.0)
	    azimuth -= 360;
	
	return azimuth;
    }

    public double computeSlitBorderRelativeAzimuth(double z) {
	return computeSlitBorderRelativeAzimuth(z, 0.0);
    }
    
    public double computeSlitBorderRelativeAzimuth(double z, double treshold) {
	double w = slitWidth/2.0 - treshold;
	double maxZ = Math.sqrt(Math.pow(domeRadius,2) - Math.pow(w,2));
	if(z == maxZ)
	    return 0.0;
	else if(z > maxZ)
	    return -1.0;
	
	double A = Math.atan(w / Math.sqrt(Math.pow(maxZ, 2) - Math.pow(z,2)));
	
	return Math.toDegrees(A);
    }
    
    public void trackDome() throws NetworkException, DomeException {
	
	switch(domeTrackingMode) {
	    
	case AppProperties.FOLLOW_OBJECT: 
	    System.out.println("Following object... " + Math.round(objectAzimuth));
	    dome.slewToAzimuth((double)Math.round(objectAzimuth));
	    break;

	case AppProperties.FOLLOW_SCOPE:
	    System.out.println("Following scope... " + Math.round(scopeAzimuth));
	    dome.slewToAzimuth((double)Math.round(scopeAzimuth));
	    break;
		
	case AppProperties.OPTIMIZED:
	    final double TRESHOLD = 0.3;
	    // If Scope or Any Sensor is Below Horizon, do not Track
	    if(scopeAxisIntersection.z < 0)
		return;
	    for(int i=0; i < sensorIntersections.length; i++) {
		if(sensorIntersections[i].z < 0)
		    return;
		}
	    Point3d relScopeAxisIntersection = new Point3d();
	    Point3d[] relSensorIntersections = new Point3d[sensorIntersections.length];
	    
	    double scopeRelativeAzimuth, sensorRelativeAzimuth, borderRelativeAzimuth, tresholdRelativeAzimuth = 90;
	    
	    Matrix3d domeCoordinatesMatrix = new Matrix3d();
	    try {
		domeAzimuth = dome.getAzimuth();
	    }
	    catch(NetworkException e) {
		e.printStackTrace();
	    }
	    domeCoordinatesMatrix.rotZ(Math.toRadians(-domeAzimuth));
	    
	    // Compute Relative Scope Intersection Coordinates
	    domeCoordinatesMatrix.transform(scopeAxisIntersection, relScopeAxisIntersection);
	    // Check if Scope is inside Slit Boundaries
	    scopeRelativeAzimuth = computeRelativeAzimuth(relScopeAxisIntersection);
	    borderRelativeAzimuth = computeSlitBorderRelativeAzimuth(relScopeAxisIntersection.z);
	    // If Scope is out of Slit, center Slit to Scope
	    if(borderRelativeAzimuth > 0 && Math.abs(scopeRelativeAzimuth) > borderRelativeAzimuth) {
		System.out.println("Centering Slit to Scope... " + Math.round(scopeAzimuth));
		dome.slewToAzimuth((double)Math.round(scopeAzimuth));
		return;
	    }
	    // If Scope went through center
	    if(!centerTransit &&
	       (prevScopeRelativeAzimuth < 0 && scopeRelativeAzimuth > 0 ||
		prevScopeRelativeAzimuth > 0 && scopeRelativeAzimuth < 0)) {
		centerTransit = true;
	    }
	    boolean moveToRightEdge = false, moveToLeftEdge = false, moveToCenter = false;
	    double rightDistance = 180, leftDistance = 180;
	    // Compute Relative Sensor Intersections' Coordinates
	    for(int i=0; i < sensorIntersections.length; i++) {
		relSensorIntersections[i] = new Point3d();
		domeCoordinatesMatrix.transform(sensorIntersections[i], relSensorIntersections[i]);
		// Check if Sensor is inside Slit Boundaries
		sensorRelativeAzimuth = computeRelativeAzimuth(relSensorIntersections[i]);
		borderRelativeAzimuth = computeSlitBorderRelativeAzimuth(relSensorIntersections[i].z);
		tresholdRelativeAzimuth = computeSlitBorderRelativeAzimuth(relSensorIntersections[i].z, TRESHOLD);
		// If Sensor is out of Slit, center Slit to Scope
		if(borderRelativeAzimuth > 0 && Math.abs(sensorRelativeAzimuth) > borderRelativeAzimuth) {
		    System.out.println("Centering Slit to Scope... " + Math.round(scopeAzimuth));
		    dome.slewToAzimuth((double)Math.round(scopeAzimuth));
		    return;
		}
		// If Sensor meet Zenith Zone Boundary, center Slit to Sensor's Azimuth
		else if(tresholdRelativeAzimuth >= 0 && Math.abs(sensorRelativeAzimuth) > 90) {
		    System.out.println("Centering Slit to Sensor's Azimuth... " + Math.round(computeAzimuth(sensorIntersections[i])));
		    dome.slewToAzimuth((double)Math.round(computeAzimuth(sensorIntersections[i])));
		    centerTransit = false;
		    return;
		}
		// If Sensor approaching Slit Zone Boundary
		else if(tresholdRelativeAzimuth > 0 && Math.abs(sensorRelativeAzimuth) > tresholdRelativeAzimuth) {
		    if(centerTransit) {
			if(sensorRelativeAzimuth >= scopeRelativeAzimuth)
			    moveToLeftEdge = true;
			else
			    moveToRightEdge = true;
			centerTransit = false;
		    }
		    else
			moveToCenter = true;
		    }

		// Find distances between Sensor Intersections and Treshold Boundaries
		if(tresholdRelativeAzimuth > 0) {
		    double distance = 90;
		    if(sensorRelativeAzimuth >= scopeRelativeAzimuth) {
			distance = Math.abs(tresholdRelativeAzimuth - sensorRelativeAzimuth);
			if(distance < rightDistance)
			    rightDistance = distance;
		    }
		    else {
			distance = Math.abs(-tresholdRelativeAzimuth - sensorRelativeAzimuth);
			if(distance < leftDistance)
			    leftDistance = distance;
		    }
		}
	    }

	    if(moveToLeftEdge) {
		System.out.println("Moving to left edge... " + Math.round(domeAzimuth + leftDistance));
		dome.slewToAzimuth((double)Math.round(domeAzimuth + leftDistance));
	    }
	    else if(moveToRightEdge) {
		System.out.println("Moving to right edge... " + Math.round(domeAzimuth - rightDistance));
		dome.slewToAzimuth((double)Math.round(domeAzimuth - rightDistance));
	    }
	    else if(moveToCenter) {
		System.out.println("Moving to center... " + Math.round(scopeAzimuth));
		dome.slewToAzimuth((double)Math.round(scopeAzimuth));
		centerTransit = true;
	    }
	    
	    if(moveToRightEdge || moveToLeftEdge || moveToCenter) {
		domeAzimuth = dome.getAzimuth();
		domeCoordinatesMatrix.rotZ(Math.toRadians(-domeAzimuth));
		domeCoordinatesMatrix.transform(scopeAxisIntersection, relScopeAxisIntersection);
		scopeRelativeAzimuth = computeRelativeAzimuth(relScopeAxisIntersection);
	    }		    
	    prevScopeRelativeAzimuth = scopeRelativeAzimuth;
	    break;
	}
    }
       
    public void run()  {
	
	while(!terminated) {
	    try {
		if (suspended) {
		    synchronized (this) {
			while (suspended)
			    wait();
		    }	
		}	

		// Update Hour Angle
		hourAngle = siderialClock.getAngle() - rightAscension;
		if(hourAngle < 0)
		    hourAngle += 360;
		mount.setHourAngle(hourAngle);
		    
		// Update Parameters
		hourAngle = mount.getHourAngle();
		decAxis = mount.getDecAxis();
		scopeOffset = mount.getScopeOffset();
		scopeDirection = mount.getScopeDirection();
		scopePosition = mount.getScopePosition();
		sensorPositions = mount.getSensorPositions();

		// Recompute Scope Axis and Sensors' Intersections
		computeIntersections();
		
		// Compute Scope Azimuth
		scopeAzimuth = computeAzimuth(scopeAxisIntersection);		       
		
		// Compute Object Azimuth
		objectAzimuth = computeAzimuth(mount.getScopeDirection());

		// Perform Dome Tracking
		trackDome();

		Thread.sleep((int)(domeTrackingDelay*1000));
	    }  
	    catch(InterruptedException e) {
		terminated = true;
		break;
	    }
	    catch(NoResponseException e) {
		continue;
	    }
	    catch(DomeException e) {
		JOptionPane.showMessageDialog(null, e.getMessage(), "Dome Communication Error", JOptionPane.ERROR_MESSAGE);
	    }
	    catch (Exception e) { 
		e.printStackTrace(); 	    
	    }
	}
    }

    public synchronized void unpause() {
	if(suspended) {
	    suspended = false;
	    notifyAll();
	}
    }
    
    public synchronized void pause() {
	if(!suspended)
	    suspended = true;
    }	
    
    public synchronized void terminate() {
	terminated = true;
    }

    // Main method for testing purposes
    public static void main(String[] args) {
	Tracking tracking = new Tracking(new Dome(), new Mount(), new ObjectInfo("test", 0.0, 0.0));
    }
}

// Class to hold information about object
class ObjectInfo {

    public static final int ENTRY_COUNT = 3;
    
    private String objectName;
    private double rightAscension;
    private double declination;
    
    public ObjectInfo(String objectName, double rightAscension, double declination) {
	this.objectName = objectName.replace('_', ' ');
	this.rightAscension = rightAscension;
	this.declination = declination;
    }
    public String getObjectName() {
	return objectName;
    }
    public double getRightAscension() {
	return rightAscension;
    }
    public double getDeclination() {
	return declination;
    }
    public void setObjectName(String objectName) {
	this.objectName = objectName.replace('_', ' ');
    }
    
    public void getRightAscension(double rightAscension) {
	this.rightAscension = rightAscension;
    }
    public void getDeclination(double declination) {
	this.declination = declination;
    }
    public String toString() {
	return objectName + ": RA = " + rightAscension + ", Dec = " + declination;
    }
}
