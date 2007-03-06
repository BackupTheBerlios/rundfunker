#include "sl_shiftregister.h"
#include "avrlibdefs.h"
#include <avr/io.h>
#include <avr/pgmspace.h>
#include "rprintf.h"


#include "lcd.h" //für Testausgaben auf LCD

u08 readBits = 0;	//unsigned char


void shiftreg_readBit(void){
	static u08 count = 0;
	//rprintf("count:%d",count);

	/* CLK-Line auf High setzen */
	//dadurch sollte jetzt ein Bit aus dem Register geschoben werden...
	//sbi(SL_SHIFTREG_PORT, SL_SHIFTREG_CLK);
	//TODO wie lange muss die CLK-Line auf High bleiben?

	//TODO: Timer steuert Warten für 1ms
//	unsigned long i,k;
//	for(i=0;i<200000;i++){
		//k=i+3;
	//}
	//rprintf(".");

	u08 tempBit = 0;
	u08 readBits = 0;

	while(count<8)
	{
		//CLK auf high
//		sbi(SL_SHIFTREG_PORT, SL_SHIFTREG_CLK);
		PORTB |= _BV(SL_SHIFTREG_CLK);

		//wait
		int i;
		u08 warten = 0;
		//for(i=1; i<100; i++)
		//{
		//	warten = PINB;
		//}

		//einlesen
		tempBit = PINB;
		tempBit &= 0x01; //nur unterstes Bit (PB0) interessiert uns

		if(!tempBit)
		{
			readBits |= 1;
			lightLed(0);
		}
		else lightLed(4);

		readBits << 1;

		//CLK auf low
//		cbi(SL_SHIFTREG_PORT, SL_SHIFTREG_CLK);
		PORTB &= ~_BV(SL_SHIFTREG_CLK);
		PORTB=0x02;
		count++;

	}

	//Bit einlesen von SI-Line
	//u08 tempBit = inb((SL_SHIFTREG_PORT)&SL_SHIFTREG_SI);	//geht das so?
	

	if(tempBit!=0)
	{
		rprintf("tempBits:%d ",tempBit);
	}

//	readBits=readBits<<1;	//readBits um 1 nach links shiften
//	readBits |= tempBit;	//und dann das neue Bit darin aufnehmen

//	rprintf(".");
	if(readBits!=0)
	{
		rprintf("readBits:%d \r\n",readBits);
	}

	count++;							//Aufrufzaehler inkrementieren
	if(count==SL_SHIFTREG_STATIONEN){	//wenn 6 od. 8 Bit eingelesen wurden, sind alle Tasterzstände in readBits enthalten
		count=0;						//zuruecksetzen fuer naechsten Durchgang
		
		

		//fertiges Zustand-Byte muss dem Java-Player mitgeteilt werden
		if(readBits>0){	//aber nur, wenn auch ein Taster gedrückt wurde
			rprintf(".");
			sendTasterzustand(readBits);		
		}
		readBits = 0;	//zuruecksetzen für die nächsten 6 Bits
	}


	/* CLK-Line wieder auf Low */
	
}

void sendTasterzustand(u08 zustand){
	/*
		VORGEHENSWEISE

		Wir haben 6 Taster (Taster 0 bis 5), deren Zustände 
		(gedrückt=1, nicht gedrückt=0) in dem übergebenen 
		byte "zustand" stecken. Das niederwertigste Bit
		entspricht dem Zustand von Taster 0, das zweithöchste
		dem von Taster 1 und so weiter, die höchstwertigen beiden
		Bit sind immer 0 (da wir nur 6 Taster haben).
		Diese Methode wird immer nur mit einem zustand >= 1 aufgerufen,
		d.h. es ist mindestens ein Taster gedrückt. Wenn kein Taster 
		gedrückt ist, muss das dem Java-Player nicht mitgeteilt werden.

		Da bei uns immer nur auf einen Taster reagiert wird (eine Kombination
		von zwei gleichzeitig gedrückten Tastern hat keine sinnvolle Bedeutung),
		betrachten wir im Falle von mehreren gedrückten Tastern nur den Taster mit
		dem niedrigsten Index. Das heißt: wenn z.B. Taster 2 und Taster 4 
		gleichzeitig gedrückt werden, wird dem Java-Player nur mitgeteilt,
		dass Taster 2 gedrückt wurde.

		Um dies zu erreichen, wird zunächst davon ausgegangen, dass Taster 0
		gedrückt wurde (zur Erinnerung: der übergebene zustand ist immer>=1,
		d.h. einer der 6 Taster wurde definitiv gedrückt!).
		Ob dem auch so ist, wird überprüft, indem das hinterste Bit des zustands
		maskiert und betrachtet wird: ist es 1, wurde tatsächlich Taster 0 
		gedrückt. Ist es 0, wird das zustandsbyte um 1 nach rechts geshiftet,
		und als nächstes überprüft, ob Taster 1 gedrückt wurde.	Hierzu wird nun 
		wieder das hinterste Bit betrachtet. Ist es 1 wurde Taster 1 gedrückt, 
		ist es 0, wird wieder geshiftet u.s.w., bis der richtige Taster gefunden
		wurde.
		Dessen Index wird dann dem Java-Player mittels der Nachricht "BT x" 
		mitgeteilt, wobei x der Index des Tasters ist.
	*/
	
	u08 tmp	=	zustand;			//"Arbeitskopie" vom zustand
	//tmp		&=	63;					//63 = 00111111, Maske, zur Sicherheit. Nur die hinteren 6 Bit enthalten Nutzinformationen.

	u08 gedrueckter_taster = 0;	//zustand ist immer >=1 (vgl. shiftreg_readBit)
		
	while((tmp&1) != 1){			//nur niedrigstes Bit von zustand betrachten, daher &1
		tmp = tmp >> 1;			//wenn keine 1 im niedrigsten Bit war, shiften, um nächstes Bit zu betrachten
		gedrueckter_taster++;	//und den index des gedrückten Tasters hochzählen
	}


	//Senden an den JavaPlayer, laut Protokoll, danach ein CRLF
	rprintf("BT %d",gedrueckter_taster); //TODO: STX und ETX??
	//rprintfCRLF();				
//	lcd_gotoxy(0,0);	//TODO einkommentieren
//	lcd_putc((char)gedrueckter_taster);	//TODO einkommentieren
}
