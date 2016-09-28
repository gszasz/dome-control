/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

import java.util.Date;
import java.util.TimeZone;

public class LSTClock {

    private double longitude;

    public LSTClock() {	
	this(0.0);
    }

    public LSTClock(double longitude) {
	this.longitude = Math.toRadians(longitude);
    }

    public double getLongitude() {
	return Math.toDegrees(longitude);
    }

    public void setLongitude(double longitude) {
	this.longitude = Math.toRadians(longitude);
    }

    public double getAngle() {

	Date date;
	double tu;
	double gmst;
	double LST;
	double ut1;
	double dmjd;
	long imjd;
	int offset;
	int hours, day, month, year;

	date = new Date();

	// Get correction in minutes between local and GMT time
	// (e.g. calculate UTC from Civil time)
	offset = date.getTimezoneOffset();
	
	imjd = mjd(date);

	// get ut1 (really UTC) in turns
	ut1 = (date.getSeconds() + (date.getMinutes() + offset) * 60 +
	       date.getHours()*3600)/86400.0;

	// If ut rolled over, means UTC on next day
	if(ut1 > 1.0)  {
	    ut1 -= 1.0;
	    imjd += 1;
	}

	dmjd = (double)imjd + ut1;

	// Julian centuries from fundamental epoch J2000 to this UT
	tu = (dmjd - 51544.5) / 36525.0;

	// tu is in turns
	// GMST at this UT in radians
	gmst = ( ut1 * (2.0*Math.PI) +
	       ( 24110.54841 +
	       ( 8640184.812866 +
	       ( 0.093104 - ( 6.2e-6 * tu ) ) * tu ) * tu ) * Math.PI/43200.0 );

	// correct for observer longitude
	LST = gmst - longitude;

	// assure a positive answer, between 0 and 2PI
	while(LST < 0.0) {
	    LST += 2.0*Math.PI;
	}
	while(LST > 2.0*Math.PI) {
	    LST -= 2.0*Math.PI;
	}

	return Math.toDegrees(LST);
    }

    public Date getTime() {

	Date lstTime, date;
	int offset;
	double LST, imjd, ut1;
	double angle = Math.toRadians(getAngle());
	
	// Convert from radians to seconds
	LST = angle * 43200.0/Math.PI;

	lstTime = new Date();
	lstTime.setSeconds((int)LST%60);
	lstTime.setMinutes(((int)LST/60)%60);
	lstTime.setHours(((int)LST/3600));

	// A true hack but it is useful.
	// We use the year to hold the MJD value, but the formatter
	// will add 1900 to it before display, so offset it here
	date = new Date();
	offset = date.getTimezoneOffset();
	imjd = mjd(date);
	ut1 = (date.getSeconds() + (date.getMinutes() + offset) * 60 +
	       date.getHours()*3600)/86400.0;
	if(ut1 > 1.0)
	    imjd += 1;

	lstTime.setYear((int)imjd - 1900);

	return(lstTime);
    }

    private long mjd(Date date)  {
	long year;
	long month;
	long day;
	long jy, jm, ja, jday;

	// getMonth range is 0..11
	month = 1 + date.getMonth();
	day = date.getDate();
	year = date.getYear() + 1900;

	jy = year;

	// Garbage in, garbage out ...
	if (jy == 0)
	    return(0);

	if (jy < 0)
	    ++jy;
	if (month > 2) {
	    jm=month+1;
	} else {
	    --jy;
	    jm=month+13;
	}
	jday = (long)(Math.floor(365.25*jy)+Math.floor(30.6001*jm)+day+1720995);

	// switch over to gregorian calendar
	if (day+31L*(month+12L*year) >= (15+31L*(10+12L*1582))) {
	    ja=(long)(0.01*jy);
	    jday += 2-ja+(long) (0.25*ja);
	}

	return(jday-2400001);
    }
}
