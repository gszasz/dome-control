/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

import javax.vecmath.*;

class Mount {

    // Inicialization from Properties
    private AppProperties ap = DomeControl.customProps;

    // GEM Flip States
    public static final int NON_FLIPPED = 0;
    public static final int FLIPPED = 1;

    // Variables
    private double hourAngle = 0;
    private double declination = 0;
    private double latitude = 0;
    private double positionAngle = 0;
    private int flipState = NON_FLIPPED;
    private double scopeDiameter = 1;
    private int sensorCount = ap.sensorCount;

    // Vectors
    private Vector3d legAxisPosition = new Vector3d();
    private Vector3d legAxis = new Vector3d();

    private Vector3d polarAxis = new Vector3d();
    private Vector3d polarAxisDirection = new Vector3d();

    private Vector3d decAxis = new Vector3d();
    private Vector3d decAxisPosition = new Vector3d();
    private Vector3d decAxisDirection = new Vector3d();

    private Vector3d scopeOffset = new Vector3d();
    private Vector3d scopeOffsetDirection = new Vector3d();

    private Vector3d scopePosition = new Vector3d();
    private Vector3d scopeDirection = new Vector3d();

    private Vector3d[] sensorOffsets = new Vector3d[sensorCount];
    private Vector3d[] sensorPositions = new Vector3d[sensorCount];

    // Transformation Matrices
    private Matrix3d polarAxisTransform = new Matrix3d();
    private Matrix3d decAxisTransform = new Matrix3d();

    // Tracking Motor
    private TrackingMotor trackingMotor;

    // Constructor
    public Mount() {

	// Initialize Leg Axis Position
	legAxisPosition.add(new Vector3d(0, 0, -ap.wallHeight));
	legAxisPosition.add(new Vector3d(ap.excentricDistance, 0, 0));
	
	// Initialize Leg Axis
	legAxis.set(0, 0, ap.legAxisLength);

	// Initialize Latitude
	latitude = Math.toRadians(ap.latitude);
	
	// Initialize Polar Axis, Polar Axis Direction and Polar Axis Transform
	computePolarAxis(ap.polarAxisLength);

	// Initialize Dec Axis Position
	computeDecAxisPosition();

	// Initialize Dec Axis and Dec Axis Direction
	computeDecAxis(ap.decAxisLength);

	// Initialize Scope Direction
	computeScopeDirection();

	// Initialize Scope Position Angle
        positionAngle = Math.toRadians(ap.scopePA);

	// Initialize Scope Offset, Scope Offset Direction and Dec Axis Transform
	computeScopeOffset(ap.scopeOffset);

	// Initialize Scope Position
	computeScopePosition();

	// Initialize Scope Diameter
	scopeDiameter = ap.scopeDiameter;

	// Initialize Sensor Offsets and Sensor Positions
	for(int i=0; i<sensorPositions.length; i++) {
	    sensorOffsets[i] = new Vector3d();
	    sensorPositions[i] = new Vector3d();
	}
	computeSensorPositions();

	// Initialze Tracking Motor
	initTrackingMotor();
    }

    private void computePolarAxis() {
	computePolarAxis(polarAxis.length());
    }

    private void computePolarAxis(double length) {

	polarAxisDirection.set(-Math.cos(latitude), 0, Math.sin(latitude));
	if(latitude < 0)
	    polarAxisDirection.negate();
	if(length != 0)
	    polarAxis.scale(length, polarAxisDirection);

	polarAxisTransform.rotY(-(Math.PI/2-latitude));
    }

    private void computeDecAxis() {
	computeDecAxis(decAxis.length());
    }

    private void computeDecAxis(double length) {

	decAxisDirection.set(Math.sin(hourAngle), -Math.cos(hourAngle), 0);
	if(flipState == FLIPPED)
	    decAxisDirection.negate();

	polarAxisTransform.transform(decAxisDirection);
	if(length != 0)
	    decAxis.scale(length, decAxisDirection);
    }

    private void computeScopeDirection() {
	scopeDirection.set(Math.cos(hourAngle)*Math.cos(declination),
                           Math.sin(hourAngle)*Math.cos(declination), 
                           Math.sin(declination));
        polarAxisTransform.transform(scopeDirection);
    }

    private void computeScopeOffset() {
	computeScopeOffset(scopeOffset.length());
    }

    private void computeScopeOffset(double length) {
	Vector3d orthogonalDirection = new Vector3d();
	orthogonalDirection.cross(scopeDirection, decAxisDirection);
	decAxisTransform.setColumn(0, decAxisDirection) ;
	decAxisTransform.setColumn(1, orthogonalDirection);
	decAxisTransform.setColumn(2, scopeDirection);

	if(positionAngle == 0)
	    scopeOffsetDirection.set(decAxisDirection);	
	else {
	    scopeOffsetDirection.set(Math.cos(positionAngle), Math.sin(positionAngle), 0);
	    decAxisTransform.transform(scopeOffsetDirection);
	}
	if(length != 0)
	    scopeOffset.scale(length, scopeOffsetDirection);
    }

    private void computeDecAxisPosition() {
	decAxisPosition.scale(0);
	decAxisPosition.add(legAxisPosition);
	decAxisPosition.add(legAxis);
	decAxisPosition.add(polarAxis);
    }

    private void computeScopePosition() {
	scopePosition.scale(0);
	scopePosition.add(decAxisPosition);
	scopePosition.add(decAxis);
	scopePosition.add(scopeOffset);
    }

    private void computeSensorPositions() {

	double step = Math.toRadians(360.0/sensorCount);

	for(int i=0; i<sensorOffsets.length; i++) {
	    sensorOffsets[i].set(Math.cos(i*step), Math.sin(i*step), 0);
	    decAxisTransform.transform(sensorOffsets[i]);
	    sensorOffsets[i].scale(scopeDiameter/2.0);
	    sensorPositions[i].add(sensorOffsets[i], scopePosition);
	}

    }

    public Vector3d getLegAxisPosition() {
	return legAxisPosition;
    }

    public Vector3d getLegAxis() {
	return legAxis;
    }

    public Vector3d getPolarAxis() {
	return polarAxis;
    }

    public Vector3d getDecAxis() {
	return decAxis;
    }

    public Vector3d getScopeOffset() {
	return scopeOffset;
    }

    public Vector3d getScopeDirection() {
	return scopeDirection;
    }

    public Vector3d getScopePosition() {
	return scopePosition;
    }

    public Vector3d[] getSensorOffsets() {
	return sensorOffsets;
    }

    public Vector3d[] getSensorPositions() {
	return sensorPositions;
    }

    public double getHourAngle() {
	return Math.toDegrees(hourAngle);
    }

    public double getDeclination() {
	return Math.toDegrees(declination);
    }

    public double getLatitude() {
	return Math.toDegrees(latitude);
    }

    public int getFlipState() {
	return flipState;
    }

    public double getScopePositionAngle() {
	return Math.toDegrees(positionAngle);
    }

    public int getSensorCount() {
	return sensorCount;
    }

    public double getScopeDiameter() {
	return scopeDiameter;
    }

    // Set Leg Axis Position
    public void setLegAxisPosition(double wallHeight, double excentricDistance) {

	Vector3d floorCenter = new Vector3d(0, 0, -wallHeight);
	Vector3d legOffset = new Vector3d(excentricDistance, 0, 0);
	
	legAxisPosition.add(floorCenter, legOffset);

	computeDecAxisPosition();
	computeDecAxis();
	computeScopeOffset();
	computeScopePosition();
	computeSensorPositions();
    }

    // Set Leg Axis Length
    public void setLegAxisLength(double legAxisLength) {

	if(legAxis.length() == legAxisLength)
	    return;

	legAxis.set(0, 0, legAxisLength);

	computeDecAxisPosition();
	computeDecAxis();
	computeScopeOffset();
	computeScopePosition();
	computeSensorPositions();
    }

    // Set Polar Axis Length
    public void setPolarAxisLength(double polarAxisLength) {

	if(polarAxis.length() == polarAxisLength)
	    return;

	polarAxis.scale(polarAxisLength, polarAxisDirection);

	computeDecAxisPosition();
	computeDecAxis();
	computeScopeOffset();
	computeScopePosition();
	computeSensorPositions();
    }

    // Set Declination Axis Length
    public void setDecAxisLength(double decAxisLength) {

	if(decAxis.length() == decAxisLength)
	    return;

	decAxis.scale(decAxisLength, decAxisDirection);
	
	computeDecAxis();
	computeScopeOffset();
	computeScopePosition();
	computeSensorPositions();
    }

    // Set Scope Offset Length
    public void setScopeOffsetLength(double scopeOffsetLength) {

	if(scopeOffset.length() == scopeOffsetLength)
	    return;

	scopeOffset.scale(scopeOffsetLength, scopeOffsetDirection);

	computeScopeOffset();
	computeScopePosition();
	computeSensorPositions();
    }

    // Set Scope Position Angle
    public void setScopePositionAngle(double positionAngle) {

	if(positionAngle < -360 || positionAngle > 360)
	    return;

	this.positionAngle = Math.toRadians(positionAngle);
// 	double pa = Math.toRadians(positionAngle);
// 	double length = scopeOffset.length();
// 	scopeOffsetDirection.set(Math.cos(pa), Math.sin(pa), 0);
// 	decAxisTransform.transform(scopeOffsetDirection);
// 	scopeOffset.scale(length, scopeOffsetDirection);

	computeScopeOffset();
	computeScopePosition();
	computeSensorPositions();
    }

    // Set Latitude
    public void setLatitude(double latitude) {
	
	this.latitude = Math.toRadians(latitude);

	computePolarAxis();
	computeDecAxisPosition();
	computeDecAxis();
	computeScopeDirection();
	computeScopeOffset();
	computeScopePosition();
	computeSensorPositions();
	trackingMotor.computeRotationMatrix();
    }

    // Set Hour Angle
    public void setHourAngle(double hourAngle) {

	if(hourAngle < 0 || hourAngle > 360)
	    return;

	this.hourAngle = Math.toRadians(hourAngle);

	computeScopeDirection();
	computeDecAxis();
	computeScopeOffset();
	computeScopePosition();
	computeSensorPositions();
    }

    public void setDeclination(double declination) {

	if(declination < -90 || hourAngle > 90)
	    return;

	this.declination = Math.toRadians(declination);

	computeScopeDirection();
	computeScopeOffset();
	computeScopePosition();
	computeSensorPositions();
    }

    // Set GEM Flip State
    public void setFlipState(int flipState) {

	if(flipState != NON_FLIPPED && flipState != FLIPPED)
	    return;

	if(flipState != this.flipState) {
	    this.flipState = flipState;
	    decAxisDirection.negate();
	    decAxis.negate();
	    computeScopeOffset();
	    computeScopePosition();
	    computeSensorPositions();
	}
    }

    // Set Scope Diameter
    public void setScopeDiameter(double scopeDiameter) {

	if(scopeDiameter < 0)
	    return;

	this.scopeDiameter = scopeDiameter;

	computeScopeOffset();
	computeScopePosition();
	computeSensorPositions();
    }

    // Set Number of Sensors
    public void setSensorCount(int sensorCount) {

	if(sensorCount < 0)
	    return;

	this.sensorCount = sensorCount;
	sensorOffsets = new Vector3d[sensorCount];
	sensorPositions = new Vector3d[sensorCount];

	for(int i=0; i < sensorPositions.length; i++) {
	    sensorOffsets[i] = new Vector3d();
	    sensorPositions[i] = new Vector3d();
	}
	computeScopeOffset();
	computeScopePosition();
	computeSensorPositions();
    }

    public void setFromAppProperties() {
//     private double hourAngle = 0;
//     private double declination = 0;
//     private double latitude = 0;
//     private double positionAngle = 0;
//     private int flipState = NON_FLIPPED;
//     private double scopeDiameter = 1;
//     private int sensorCount = ap.sensorCount;

	setLegAxisPosition(ap.wallHeight, ap.excentricDistance);
	setLegAxisLength(ap.legAxisLength);
	setLatitude(ap.latitude);	
	setPolarAxisLength(ap.polarAxisLength);
	setDecAxisLength(ap.decAxisLength);
	setScopeOffsetLength(ap.scopeOffset);
	setSensorCount(ap.sensorCount);
	

    }

    // Tracking Motor
    public void initTrackingMotor() {
	if(trackingMotor == null) {
	    trackingMotor = new TrackingMotor();
	    trackingMotor.start();
	}
    }

    public void startTrackingMotor() {
	initTrackingMotor();
	trackingMotor.switchOn();
    }

    public void startTrackingMotor(double speed) {
	initTrackingMotor();
	trackingMotor.setSpeed(speed);
	trackingMotor.switchOn();
    }

    public boolean trackingMotorRunning() {
	return trackingMotor.isRunning();
    }

    public void setTrackingMotorSpeed(double speed) {
	trackingMotor.setSpeed(speed);
    }

    public void stopTrackingMotor() {
	trackingMotor.switchOff();
    }

    public void destroyTrackingMotor() {
	trackingMotor.terminate();
	trackingMotor = null;
    }

    // Tracking Motor Simulator
    class TrackingMotor extends Thread {

	public final double INCREMENT = Math.toRadians(360.0/864000.0);

	private Matrix3d polarAxisInverseTransform = new Matrix3d();
	private Matrix3d rotationMatrix = new Matrix3d();
	private double speed;
	private boolean terminated = false;
	private boolean suspended = true;

	public TrackingMotor() {
	    this(1);
	}

	public TrackingMotor(double speed) {
	    this.speed = speed;
	    computeRotationMatrix();
	}

	public void computeRotationMatrix()  {
	    polarAxisInverseTransform.invert(polarAxisTransform);
	    rotationMatrix.rotZ(INCREMENT*speed);
	    rotationMatrix.mul(polarAxisTransform, rotationMatrix);
	    rotationMatrix.mul(polarAxisInverseTransform);
	}

	public void setSpeed(double speed) {
	    this.speed = speed;
	    rotationMatrix.rotZ(INCREMENT*speed);
	    rotationMatrix.mul(polarAxisTransform, rotationMatrix);
	    rotationMatrix.mul(polarAxisInverseTransform);
	}

	public boolean isRunning() {
	    return (! suspended);
	}
	
	public void run() {
	    while(!terminated) {
		try {
		    if (suspended) {
			synchronized (this) {
			    while (suspended)
				wait();
			}
		    }

		    Thread.sleep(100);
		    // Rotate Declination Axis
		    rotationMatrix.transform(decAxisDirection);
		    decAxis.scale(decAxis.length(), decAxisDirection);
		    // Rotate Scope Offset
		    rotationMatrix.transform(scopeOffsetDirection);
		    scopeOffset.scale(scopeOffset.length(), scopeOffsetDirection);
		    rotationMatrix.transform(scopeDirection);
		    // Compute Scope position
		    computeScopePosition();
		    // Rotate Sensor Offsets and Compute Sensor Positions
		    for(int i=0; i<sensorPositions.length; i++) {
		    	rotationMatrix.transform(sensorOffsets[i]);
			sensorPositions[i].add(sensorOffsets[i], scopePosition);
		    }
		    // Set Hour Angle
		    hourAngle += INCREMENT*speed;
		    if(hourAngle >= Math.toRadians(360))
			hourAngle -= Math.toRadians(360);
		}
		catch(InterruptedException e) {
		    terminated = true;
		    break;
		}
		catch(Exception e) {
		    e.printStackTrace();
		}
	    }
	}

	public synchronized void switchOn() {
	    if(suspended) {
		suspended = false;
                notifyAll();
	    }
	}

	public synchronized void switchOff() {
	    if(!suspended)
		suspended = true;
	}

	public synchronized void terminate() {
	    terminated = true;
	    this.interrupt();
	}

    }

    // Main Method for Testing purposes
    public static void main(String[] args) {
	Mount mount = new Mount();

	System.out.println(mount.getScopePosition());
	System.out.println(mount.getScopeDirection());

    }
}
