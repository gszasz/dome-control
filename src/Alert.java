/* 
 * Dome Control - A Dome Control Client for Java platform
 * Copyright (C) 2007 Hlohovec Observatory
 *
 * This program is licensed under the terms found in the COPYING file.
 */

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.sound.sampled.*;

class Alert extends Thread {

    private boolean shown;
    private String message;
    private String audioFile;
    private Clip audioClip;

    public Alert(String message) {
	this(message, null);
    }

    public Alert(String message, String audioFile) {
	this.message = message;
	this.audioFile = audioFile;
    }

    public void loadAudioFile(String filename)  {
	File soundFile = new File(filename);

	try  {
	    AudioInputStream source = AudioSystem.getAudioInputStream(soundFile);
	    DataLine.Info clipInfo = new DataLine.Info(Clip.class, source.getFormat());
	    if(AudioSystem.isLineSupported(clipInfo))  {
		audioClip = (Clip)AudioSystem.getLine(clipInfo);
		audioClip.open(source);
	    }
	}
	catch(Exception e) {
	    audioClip = null;
	}
    }

    public String getAudioFile() {
	return audioFile;
    }

    public boolean audioFileOk() {
	if(audioFile != null && audioClip == null)
	    loadAudioFile(audioFile);
	
	if(audioClip != null)
	    return true;
	else
	    return false;
    }

    public void show()  {

	if(!shown) {

	    shown=true;

	    if(audioFile != null && audioClip == null)
		loadAudioFile(audioFile);

	    if(audioClip != null)
		audioClip.loop(audioClip.LOOP_CONTINUOUSLY);

	    JOptionPane.showMessageDialog(null, message, "Dome Control Alert", JOptionPane.WARNING_MESSAGE);

	    if(audioFile != null)
		audioClip.loop(0);

	    shown=false;
	}
    }

    public static void main(String[] args) {

	Alert alert = new Alert("Message", "/home/sg/src/DomeControl/src/alert.wav");

	alert.show();
    }
}
