#include "sl_shiftregister.h"
#include "avrlibdefs.h"
#include <avr/io.h>
#include <avr/pgmspace.h>
#include "rprintf.h"

#include "lcd.h"			//f�r Testausgaben auf LCD
#include "sl_led_3to8.h"	//f�r Test-LED-Ansteuerung
#include "uart.h"

u08 readBits = 0;	//unsigned char
volatile u08 tempBit = 0;
u08 test = 0;

//volatile unsigned long i = 0;
volatile unsigned long timer = 0;

volatile u08 dsCounter = 2;
volatile BOOL dsOn = FALSE;

volatile u08 wertsent = 0;


void shiftreg_readBit(void){
	int i=8;
    u08 wert=0;
   
    // SR initialisieren
    clr(SL_SHIFTREG_PL);
    clr(SL_SHIFTREG_SI);
    clr(SL_SHIFTREG_CLK);
	
	// CLK-Line (bei uns PortB, PB1) auf output setzen	
	sbi(SL_SHIFTREG_DDR,SL_SHIFTREG_CLK);
	// SI-Line (bei uns PortB, PB0) auf output setzen
	sbi(SL_SHIFTREG_DDR,SL_SHIFTREG_SI);
	// PL-Line (bei uns PortB, PB5) auf output setzen
	sbi(SL_SHIFTREG_DDR,SL_SHIFTREG_PL);
	// Q8-Line (bei uns PortB, PB4) auf input setzen
	cbi(SL_SHIFTREG_DDR,SL_SHIFTREG_Q8);

    // zuerst die Daten parallel einlesen
    set(SL_SHIFTREG_PL);
   // waitsl(1);
    clr(SL_SHIFTREG_PL);
	

    // dann die Daten seriell an O7 auslesen
    while(i>0)
    {
        clr(SL_SHIFTREG_CLK);
		waitsl(1);
   		//tempBit=inb(SL_SHIFTREG_PORT)&(1<<SL_SHIFTREG_Q8);
		tempBit = SL_SHIFTREG_PIN & _BV(SL_SHIFTREG_Q8);	//Das vierte Bit heraussuchen
		tempBit = tempBit >> SL_SHIFTREG_Q8;				//rechtsshiften, um die Information an der niederwertigsten Stelle zu haben
        set(SL_SHIFTREG_CLK);
		
        wert |= tempBit;
        if(i>1){				//beim letzten Durchgang ist kein Schieben f�r ein n�chstes Bit n�tig
			wert = wert << 1;
		}
		
		i--;
	
    }

	wert = ~wert;		//invertieren, da taster low-aktiv sind
	wert = wert & 0x3F;	//die beiden h�chstwertigen Bits rausmaskieren, da hier keine Taster anliegen

	if(wertsent!=wert){	//"Entprellen": ein l�ngerer Tastendruck soll nur einmal registriert werden
		wertsent=wert;
		if(wert>0){		//nur, wenn auch ein Taster gedr�ckt wurde, dies dem Java-Player mitteilen
			sendTasterzustand(wert);		
		}
	}
}


void sendTasterzustand(u08 zustand){
	/*
		VORGEHENSWEISE

		Wir haben 6 Taster (Taster 0 bis 5), deren Zust�nde 
		(gedr�ckt=1, nicht gedr�ckt=0) in dem �bergebenen 
		byte "zustand" stecken. Das niederwertigste Bit
		entspricht dem Zustand von Taster 0, das zweith�chste
		dem von Taster 1 und so weiter, die h�chstwertigen beiden
		Bit sind immer 0 (da wir nur 6 Taster haben).
		Diese Methode wird immer nur mit einem zustand >= 1 aufgerufen,
		d.h. es ist mindestens ein Taster gedr�ckt. Wenn kein Taster 
		gedr�ckt ist, muss das dem Java-Player nicht mitgeteilt werden.

		Da bei uns immer nur auf einen Taster reagiert wird (eine Kombination
		von zwei gleichzeitig gedr�ckten Tastern hat keine sinnvolle Bedeutung),
		betrachten wir im Falle von mehreren gedr�ckten Tastern nur den Taster mit
		dem niedrigsten Index. Das hei�t: wenn z.B. Taster 2 und Taster 4 
		gleichzeitig gedr�ckt werden, wird dem Java-Player nur mitgeteilt,
		dass Taster 2 gedr�ckt wurde.

		Um dies zu erreichen, wird zun�chst davon ausgegangen, dass Taster 0
		gedr�ckt wurde (zur Erinnerung: der �bergebene zustand ist immer>=1,
		d.h. einer der 6 Taster wurde definitiv gedr�ckt!).
		Ob dem auch so ist, wird �berpr�ft, indem das hinterste Bit des zustands
		maskiert und betrachtet wird: ist es 1, wurde tats�chlich Taster 0 
		gedr�ckt. Ist es 0, wird das zustandsbyte um 1 nach rechts geshiftet,
		und als n�chstes �berpr�ft, ob Taster 1 gedr�ckt wurde.	Hierzu wird nun 
		wieder das hinterste Bit betrachtet. Ist es 1 wurde Taster 1 gedr�ckt, 
		ist es 0, wird wieder geshiftet u.s.w., bis der richtige Taster gefunden
		wurde.
		Dessen Index wird dann dem Java-Player mittels der Nachricht "BT x" 
		mitgeteilt, wobei x der Index des Tasters ist.
	*/

	u08 tmp	=	zustand;			//"Arbeitskopie" vom zustand

	u08 gedrueckter_taster = 0;	//zustand ist immer >=1 (vgl. shiftreg_readBit)
		
	while((tmp&1) != 1){			//nur niedrigstes Bit von zustand betrachten, daher &1
		tmp = tmp >> 1;			//wenn keine 1 im niedrigsten Bit war, shiften, um n�chstes Bit zu betrachten
		gedrueckter_taster++;	//und den index des gedr�ckten Tasters hochz�hlen
	}


	//Senden an den JavaPlayer, laut Protokoll
	rprintf("%cBT %d%c",STX,gedrueckter_taster,ETX);

}

void waitsl(unsigned long zeit){	//zeit in ms
	timer=zeit*10;		//*10, da Timer2Overflow 10000mal in der sekunde erfolgt
	while(timer)
	{
		//if(timer%10000==0){
		//	rprintf("sek:%d\r\n",timer/10000);
		//}
	}
}

void handle_timer2ov(){
	
	if(timer) timer--;
	
}

void set(u08 port)
{
    SL_SHIFTREG_PORT |= _BV(port);       
}

void clr(u08 port)
{
    SL_SHIFTREG_PORT &= ~_BV(port);
}
