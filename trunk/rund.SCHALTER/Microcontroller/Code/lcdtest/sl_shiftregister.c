#include "sl_shiftregister.h"
#include "avrlibdefs.h"
#include <avr/io.h>
#include <avr/pgmspace.h>
#include "rprintf.h"

u08 readBits = 0;	//unsigned char



void shiftreg_readBit(void){
	static u08 count = 0;
	
	/* CLK-Line (bei uns PortB, PB1) auf output setzen */	
	sbi(SL_SHIFTREG_DDR,SL_SHIFTREG_CLK);	//geht das so?

	/* SI-Line (bei uns PortB, PB0) auf input setzen */
	cbi(SL_SHIFTREG_DDR,SL_SHIFTREG_SI);	//geht das so?

	//TODO das setzen der Lines auf In- bzw. Output muss eigtl. nicht jedesmal neu erfolgen?

	/* CLK-Line auf High setzen */
	//dadurch sollte jetzt ein Bit aus dem Register geschoben werden...
	sbi(SL_SHIFTREG_PORT, SL_SHIFTREG_CLK);
	//TODO wie lange muss die CLK-Line auf High bleiben?


	//Bit einlesen von SI-Line
	u08 tempBit = inb((SL_SHIFTREG_PORT)&SL_SHIFTREG_SI);	//geht das so?
	readBits=readBits<<1;	//readBits um 1 nach links shiften
	readBits |= tempBit;	//und dann das neue Bit darin aufnehmen

	count++;							//Aufrufzaehler inkrementieren
	if(count==SL_SHIFTREG_STATIONEN){	//wenn 6 Bit eingelesen wurden, sind alle 6 Tasterzst�nde in readBits enthalten
		count=0;						//zuruecksetzen fuer naechsten Durchgang

		//fertiges Zustand-Byte muss dem Java-Player mitgeteilt werden
		if(readBits>0){	//aber nur, wenn auch ein Taster gedr�ckt wurde
			sendTasterzustand(readBits);		
		}
		readBits = 0;	//zuruecksetzen f�r die n�chsten 6 Bits
	}


	/* CLK-Line wieder auf Low */
	cbi(SL_SHIFTREG_PORT, SL_SHIFTREG_CLK);
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
	tmp		&=	63;					//63 = 00111111, Maske, zur Sicherheit. Nur die hinteren 6 Bit enthalten Nutzinformationen.

	u08 gedrueckter_taster = 0;	//zustand ist immer >=1 (vgl. shiftreg_readBit)
		
	while((tmp&1) != 1){			//nur niedrigstes Bit von zustand betrachten, daher &1
		tmp = tmp >> 1;			//wenn keine 1 im niedrigsten Bit war, shiften, um n�chstes Bit zu betrachten
		gedrueckter_taster++;	//und den index des gedr�ckten Tasters hochz�hlen
	}


	//Senden an den JavaPlayer, laut Protokoll, danach ein CRLF
	rprintf("BT %d",gedrueckter_taster);
	rprintfCRLF();				
}
