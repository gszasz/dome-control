//
// fcpu4.c                      Last Update: 25.2.2006
//
// Author: Ing. Milan Wudia     Copyright (C) 2004 AstroLab Brno
// 
// This code is licensed under the terms found in the COPYING file.
//
// Modifikace 21.12.04  - doplnena inicializace serv po canu
// Modifikace 22.10.05  - cteni tastru   
// Modifikace 25.2.06 - verze pro Hlohovec 
// doplneno cteni stavu kopule - prikaz /TA0 vraci KREG kop kopa \r\n
// v bloku 1pps je mozno aktivovat sekvenci pro autonomni test kopule  
// ledka B sviti pri pruchodu blokem zpracovani prikazu - odezva na prichozi platny paket 

// IP adresa se nastavuje v souboru defsets.c !!!!!!!!!

#include <sample\_ffmc16.h>
#include "hw.h"

#include "rtl8019.h"
#include "arp.h"
#include "internet.h"

#include "rtc.h"
#include "can.h"
#include <string.h>

#define HSK  438.1028776
#define HKRR 39321600		// hodin na krok v rektascenzi      
#define SKRD 1820444.444444 // stupnu na krok v deklinaci
#define SKRA 1.01408   //stupnu na krok azimutu nutno upravit, pro TA3 je 2.4953 


#define MKRR25 1747.63
#define MKRD25 1213.63 
 
#define mrra0  5000
#define mrra1  20000	
#define mrra2  125000
 	
#define mrde0  5000
#define mrde1  20000
#define mrde2  105000

#define kopdr2 15			//diference azimutu pro rychlost 2
#define kopdr  1  			//diference azimutu 
	
 
void app_poll(void);

char ma,*pu,errp;
unsigned char stavp0,sbo,sba,kop=0,kopr,kopa=0;
int azc,azd,az;
double par1,hs=HSK;
long ra,de,rad,ded,hu;
long rra,rde,mrra=mrra2,mrde=mrde2;

void main(void){
	char tmp1,tmp2;
	long int poloha=0;
	double st=0;
	inithw();
	initinternet();
	initrtc();
	uinit();
	
	candivider=4;			//inicializace CANu
	canphase=0;canstate=0;
	caninit();

	IO_ICR15.bit.ISE=0;  //povoleni preruseni delayed pro can...
	IO_ICR15.bit.IL=6;
		
	IO_DDR8.bit.D80=1;//nastaveni portu pro vystup
	IO_DDR8.bit.D81=1;
	IO_DDR8.bit.D82=1;	
	IO_DDR8.bit.D83=1;
	IO_DDR7.bit.D70=1;//nastaveni portu pro vystup    
	IO_DDR7.bit.D71=1;
	IO_DDR7.bit.D72=1;	
	IO_DDR7.bit.D73=1;

	IO_ADER.byte=0x00;
	
	stavp0=IO_PDR0.byte;
			
	__EI();
 
 	Delay_10ms(250);   
 	Delay_10ms(250); 
 	Delay_10ms(250); 
 	Delay_10ms(250);
 		
	canwr(2,0x01ED,5,1);  //reset chyby ra serva po zapnuti
	IO_TREQR0.bit.TREQ2=1;
	canwr(3,0x01ED,5,1);  //reset chyby de serva po zapnuti
	IO_TREQR0.bit.TREQ3=1;

	Delay_1ms(50);
	
	canwr(2,0x01ED,2,1);  //zapnuti ra serva po zapnuti
	IO_TREQR0.bit.TREQ2=1;
	canwr(3,0x01ED,2,1);  //zapnuti de serva po zapnuti
	IO_TREQR0.bit.TREQ3=1;

	Delay_1ms(50);
	
	canwr(2,0x01ED,7,1);  //reset resolveru ra serva po zapnuti  
	IO_TREQR0.bit.TREQ2=1;
	canwr(3,0x01ED,7,1);  //reset resolveru de serva po zapnuti 
	IO_TREQR0.bit.TREQ3=1;

	Delay_1ms(50);
	
//	mrtc_init(0x00040706,0x00155000);		



		
	while(1){
			
//		app_poll();
		dointernet();
	
	IO_PDR8.bit.P80=~IO_PDR8.bit.P80;

	
	if(flag1pps) {				flag1pps=0;
	  							IO_PDR8.bit.P83=~IO_PDR8.bit.P83;		
						// pouze pro test ---- zrusit !!!!!!!!!!
						 	/*		switch(kop){
									     			  		case 0x08 : {
									     			  					azc++;
									     			  			   		}break;
									     			  		case 0x0A : {
									     			  					azc+=2;	
									     			  			   		}break;
															case 0x04 : {
									     			  					azc--;	
									     			  			   		}break;
									     			  		case 0x06: {
									     			  				     azc-=2;
									     			  			   		}break;
											}*/				
						// pouze pro test ---- zrusit !!!!!!!!!!
		
						//	ma=sprintf(txbuff,"%0lX %0lX\r\n",IO_CANID0.DTR[5].BYTE0_3,IO_CANID0.DTR[5].BYTE4_7);
							//sendudp(ipdest,portdest, 1999,&txbuff[0],ma); 
						//	IO_TREQR0.bit.TREQ2=1;
						//	ma=sprintf(txbuff,"%lx %lx \r\n",mmrtc_time(),mmrtc_date());
					//	 	ma=sprintf(txbuff,"%ld %08lX \r\n",canrdp(0),canrds(1));

				     //       print (txbuff,ma);
				            
        					//uprint (txbuff,ma);
        				//	ma=uread(txbuff);
						//	if(ma)uprint (txbuff,ma);
		
				} //konec 1pps
	if(flag25pps) {	
								

							flag25pps=0;
							IO_TREQR0.bit.TREQ0=1;
							IO_TREQR0.bit.TREQ1=1;

							if(!(IO_CANID0.DTR[0].BYTE0_3&0x00000020))
							{
	  							st=st+hs;
	  							ra=ra+rra;
	  					    	hu=(long)st-ra;  
								canwr(2,0x0192,-hu,2);
								IO_TREQR0.bit.TREQ2=1;
								de=de-rde; 
								canwr(3,0x0192,de,2);
								IO_TREQR0.bit.TREQ3=1;
							}
							else 
							{
							 	rra=0;rde=0;
							 	hu=-canrdp(0);de=canrdp(1);
							 	ra=(long)st-hu;						
							}
							
							if (kop&&!IO_PDR6.bit.P63)
							 {   
							 	IO_PDR7.byte=kop;	
							 }
							 else
							 {
 	 							IO_PDR7.byte=0;
							 }	
			                 
			                if(kopa)
			                {
			                 azd=az-azc;
			                 if(azd>kopdr2)kop=0x0A;
			                 else 
			                 	if(azd>kopdr)kop=0x08;
			                 		else  
			                 			if(azd<-kopdr2)kop=0x06;
							 				else 
			                 					if(azd<-kopdr)kop=0x04;
			                 						else 
			                 							{ 
			                 								kop=0x00;
			                 								kopa=0;	
			                 							}		                 
			                }
				
				if (stavp0!=IO_PDR0.byte)
					{
					
					  if(!IO_PDR0.bit.P04){
					  						mrra=mrra0;mrde=mrde0;
					  						kopr=0;
					  					   }
				      	else
				      if(!IO_PDR0.bit.P05){mrra=mrra2;mrde=mrde2;kopr=2;}
				      	else
				      	{mrra=mrra1;mrde=mrde1;kopr=2;}
	
					  if( (stavp0&0x01)&&!IO_PDR0.bit.P00)rra=mrra;
					  if(!(stavp0&0x01)&&IO_PDR0.bit.P00)rra=0;
					  if( (stavp0&0x02)&&!IO_PDR0.bit.P01)rra=-mrra;
					  if(!(stavp0&0x02)&&IO_PDR0.bit.P01)rra=0;
					  if( (stavp0&0x04)&&!IO_PDR0.bit.P02)rde=mrde;
					  if(!(stavp0&0x04)&&IO_PDR0.bit.P02)rde=0;
					  if( (stavp0&0x08)&&!IO_PDR0.bit.P03)rde=-mrde;
					  if(!(stavp0&0x08)&&IO_PDR0.bit.P03)rde=0;
				      if( (stavp0&0x40)&&!IO_PDR0.bit.P06){kop=0x04|kopr;kopa=0;}
					  if(!(stavp0&0x40)&&IO_PDR0.bit.P06){kop=0;kopa=0;}
					  if( (stavp0&0x80)&&!IO_PDR0.bit.P07){kop=0x08|kopr;kopa=0;}
					  if(!(stavp0&0x80)&&IO_PDR0.bit.P07){kop=0;kopa=0;}

					      				      		
					  stavp0=IO_PDR0.byte;
					  

					}

	sba=IO_PDR6.byte&0x03;
   	switch(sba)
      {
        case 0x00:
		  if(sbo==0x02)azc++;
		  if(sbo==0x01)azc--;	
           	  break;
        case 0x02:
		  if(sbo==0x03)azc++;
		  if(sbo==0x00)azc--;	
           	  break;
        case 0x03:
		  if(sbo==0x01)azc++;
		  if(sbo==0x02)azc--;	
           	  break;
        case 0x01:
		  if(sbo==0x00)azc++;
		  if(sbo==0x03)azc--;	
           	  break;
      }
  sbo=sba;
  
	if(!IO_PDR6.bit.P62)azc=0;										
		
				} //konec 25pps
	
	else
		
	if(rxflag) {
				errp=1;
				pu=memchr(&rxbuff[0],'/',rxpoint);
				if(pu++!=NULL)
				{
					IO_PDR8.bit.P81=1;
			 if(*pu=='G') {
			 				pu++;
							switch(*pu++){
										 case 'R' : {
										 			ma=sprintf(txbuff,"R %lf\r\n",((double)( st+canrdp(0)+rad))/HKRR);
										 			sendudp(ipdest,portdest, 1999,&txbuff[0],ma); 
										 			}break;
										 case 'D' : {
										 			ma=sprintf(txbuff,"D %lf\r\n",((double)(canrdp(1)+ded))/SKRD);
										 			sendudp(ipdest,portdest, 1999,&txbuff[0],ma); 
										 			}break;
										 case 'r' : {
										 			ma=sprintf(txbuff,"r %lf\r\n",(double)rad/HKRR);
										 			sendudp(ipdest,portdest, 1999,&txbuff[0],ma); 
										 			}break;
										 case 'd' : {
										 			ma=sprintf(txbuff,"d %lf\r\n",(double)ded/SKRD);
										 			sendudp(ipdest,portdest, 1999,&txbuff[0],ma); 
										 			}break;
										 case 'A' : { 
										 			ma=sprintf(txbuff,"A %lf\r\n",(double)azc/SKRA);
										 			sendudp(ipdest,portdest, 1999,&txbuff[0],ma); 
										 			}break;

										 case 'H' : {
										 			ma=sprintf(txbuff,"H %lf\r\n",hs);
										 			sendudp(ipdest,portdest, 1999,&txbuff[0],ma); 
										 			}break;
      							 		 case 'S' : {
										 			ma=sprintf(txbuff,"S R %X D %X A %x \r\n",canrds(0),canrds(1),(char)!IO_PDR6.bit.P63);
										 			sendudp(ipdest,portdest, 1999,&txbuff[0],ma); 
										 			}break;
									     case 'L' : {
									     			ma=sprintf(txbuff,"L %lx %lx \r\n",mmrtc_time(),mmrtc_date());
										 			sendudp(ipdest,portdest, 1999,&txbuff[0],ma);
													}break;
										 case 'T' : {
										 			//PRIDANO PRO TEST TASTRU												
										 			ma=sprintf(txbuff,"C %X \r\n",(char)stavp0);
										 			sendudp(ipdest,portdest, 1999,&txbuff[0],ma);							
										 			}break;
				
										 } 
						   }
			 else
			 {	
			 	par1=0;
			 	errp=sscanf(pu+2,"%lf",&par1);
			switch(*pu++){
										
				case 'S' : {
							if (errp&&errp!=255)
							switch(*pu++){
										 case 'R' : {
										 			 ra=(long)(par1*HKRR)-rad;
													}break;
									     case 'D' : {
									     			 de=(long)(par1*SKRD)-ded;
													}break;
										 case 'r' : {
										 			 rad=(long)(par1*HKRR);
										 			 ra=0;st=0;rra=0;
										 			 canwr(2,0x01ED,7,1);  //reset de serva 
													 IO_TREQR0.bit.TREQ2=1;
													}break;
									     case 'd' : {
									     			 ded=(long)(par1*SKRD);
									     			 de=0;rde=0;
									     			 canwr(3,0x01ED,7,1);  //reset de serva 
													 IO_TREQR0.bit.TREQ3=1;
													}break;			
										 case 'H' : {
									     			 hs=par1;
													}break;				
										 case 'A' : {
									     			 az=(int)(par1*SKRA);
									     			 kopa=1;
													}break;				
										 default: errp=0;
										 } 
						   }break;
							
				case 'M' : {
							if (errp&&errp!=255)
							switch(*pu++){
										 case 'R' : {
										 			rra=(long)(par1*MKRR25);
													}break;
									     case 'D' : {
									     			rde=-(long)(par1*MKRD25);
													}break;
										 case 'A' : {
									     			  switch((int)(par1)){
									     			  		case 1 : {
									     			  					kop=0x08;	
									     			  			   		}break;
									     			  		case 2 : {
									     			  					kop=0x0A;	
									     			  			   		}break;
															case -1 : {
									     			  					kop=0x04;	
									     			  			   		}break;
									     			  		case -2 : {
									     			  					kop=0x06;	
									     			  			   		}break;
															default: kop=0;


															}			
													}break;

										 default: errp=0;
										 } 

							}break;				
			
				case 'Q' : {
							errp=1;
							switch(*pu++){
										 case 'R' : {
										 			rra=0;
													}break;
									     case 'D' : {
									     			rde=0;
													}break;
										 case 'A' : {
									     			kop=0;
													}break;

										 default: errp=0;
										 } 

							}break;	
				case 'T' : {
							if (errp&&errp!=255)
							switch(*pu++){
											case 'R' : {
      							 		 			canwrd(2,(int)par1,1);
      							 		 			IO_TREQR0.bit.TREQ2=1;
													Delay_1ms(4);
										 			ma=sprintf(txbuff,"RREG %d %lX \r\n",(int)par1,canrdr(5));
										 			sendudp(ipdest,portdest, 1999,&txbuff[0],ma); 
										 			}break;
											case 'D' : {
      							 		 			canwrd(3,(int)par1,1);
      							 		 			IO_TREQR0.bit.TREQ3=1;
													Delay_1ms(4);
										 			ma=sprintf(txbuff,"DREG %d %lX \r\n",(int)par1,canrdr(6));
										 			sendudp(ipdest,portdest, 1999,&txbuff[0],ma); 
										 			}break;
											case 'A' : {
      							 		 				ma=sprintf(txbuff,"KREG %d %d \r\n",kop,kopa);
										 				sendudp(ipdest,portdest, 1999,&txbuff[0],ma); 
										 			   }break;

										 case 'C' : {
										 					
										 			}break;
									     case 'O' : {
									     			
													}break;
										 default: errp=0;
										 } 

							}break;						
				
				default: errp=0;
				}
				if (!errp||errp==255)sendudp(ipdest,portdest, 1999,"ERR-PAR\x0D\x0A",9);
					else
							{
								
									sendudp(ipdest,portdest, 1999,"OK\r\n",4);
							}

				}
			} else sendudp(ipdest,portdest, 1999,"ERR-URC\x0D\x0A",9);
						
			rxpoint=0;
			rxflag=0;
		 	IO_PDR8.bit.P81=0;
			}
			 else
			{
				
			}
						 
			}
	}
