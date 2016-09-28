/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

public class Status  {

    private int code;
    private String message;

    public Status(int code, String message) {
	this.code = code;
	this.message = message;
    }

    public String getMessage() {
	return message;
    }

    public boolean equals(Object state)  {
	if(state != null && this.code == ((Status)state).code)
	    return true;
	else
	    return false;
    }

    public String toString()  {
	return message;
    }

}
