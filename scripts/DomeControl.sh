#!/bin/bash
# Dome Control startup script
prefix=/usr/local

cd $prefix/share/DomeControl
java -jar DomeControl.jar $*
