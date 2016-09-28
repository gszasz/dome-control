PREFIX = /usr/local
DESTDIR = $(PREFIX)/share/DomeControl

JAVAC = javac
JAR = jar
CLASSES = Dome.class Mount.class Tracking.class Preferences.class Visualizer.class Console.class AppProperties.class LSTClock.class UDPConnection.class SpringUtilities.class

all: DomeControl.jar

DomeControl.jar: DomeControl.class
	$(JAR) cmf mainClass DomeControl.jar *.class Icons

DomeControl.class: DomeControl.java $(CLASSES)
	$(JAVAC) DomeControl.java

Dome.class: Dome.java
	$(JAVAC) Dome.java

Mount.class: Mount.java
	$(JAVAC) Mount.java

Tracking.class: Tracking.java
	$(JAVAC) Tracking.java

Preferences.class: Preferences.java
	$(JAVAC) Preferences.java

Visualizer.class: Visualizer.java
	$(JAVAC) Visualizer.java

Console.class: Console.java
	$(JAVAC) Console.java

AppProperties.class: AppProperties.java
	$(JAVAC) AppProperties.java

LSTClock.class: LSTClock.java
	$(JAVAC) LSTClock.java

UDPConnection.class: UDPConnection.java
	$(JAVAC) UDPConnection.java

SpringUtilities.class: SpringUtilities.java
	$(JAVAC) SpringUtilities.java

clean:
	rm -f DomeControl.jar *.class
