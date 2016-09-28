Control system of the dome and paralactic mount 
=====================================================================
                                   (Copyright (C) 2004 AstroLab Brno)

Control unit is connected to the computer network via 10BASE-T
interface.  System can be operated from any computer conntected to the
network.  Access to the unit is provided via reserved UDP port and one
can operate the unit using appropriate terminal program.

Requirements
------------
Unit has to be associated with uniqe static IP address, accessible by
the operating computer.  The computer should be connected within the
same subnet as the unit, if possible.

UDP Datagram Structure
----------------------

/XX YYY.YYY \r\n
|--|--------|---|
| |  param.   \ 
|  \            end of sequence (character 0Dh optionally followed by 0Ah)
|   \ 
 \     two letter codeword
  \    
    beginning of sequence

For complete reference (in Czech Language) see Manual-CZ.doc.


Firmware Source Code
-------------------- 

For further reference see file 'fcpu4.c', which contains source code
of the communication part of the firmware of control unit in Hlohovec
Observatory dome building.
