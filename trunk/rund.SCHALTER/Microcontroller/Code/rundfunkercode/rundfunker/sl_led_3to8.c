#include "sl_led_3to8.h"

#include "avrlibdefs.h"
#include <avr/io.h>
#include <avr/pgmspace.h>



void lightLed(u08 abc){

	//Lines auf Output setzen - muss eigtl. nicht jedesmal hier passiern, TODO auslagern
	/* A-, B- und C-Line (bei uns PortD, PD7, PD6 und PD5) auf output setzen */	
	sbi(SL_LED_3TO8_DDR,SL_LED_3TO8_A);	
	sbi(SL_LED_3TO8_DDR,SL_LED_3TO8_B);	
	sbi(SL_LED_3TO8_DDR,SL_LED_3TO8_C);	

	//entsprechend des uebergebenen abc-Bytes die Lines auf High oder Low setzen

	u08 shifttemp = abc<<5;	//Wert von abc um 5 nach links shiften, 
							//damit die Bits direkt auf die Port-Pins gemappt 
							//werden können. 
							//A ist der "oberste" Pin (PD7),
							//B ist der zweite Pin von oben (PD6),
							//C ist der dritte Pin von oben (PD5),
							//also sollte da so passen
							//z.B.: aus 00000110 wird 11000000
							//ACHTUNG: da im 3to8-Encoder die Reihenfolge
							//CBA statt ABC ist (also C ist das höchste Bit)
							//lässt ein Aufruf dieser Funktion mit dem Wert 4 (100)
							//Led1 leuchten (001), bzw. Wert 3 (011) Led6 (110) usw.
							//Deswegen die defines als Parameter für diese
							//Funktion verwenden (SL_LED0 ... SL_LEDOFF)

	
	//zunächst erstmal die ABC-Lines auf low setzen, damit vorherige Zustände 
	//verworfen werden
	cbi(SL_LED_3TO8_PORT, SL_LED_3TO8_A);
	cbi(SL_LED_3TO8_PORT, SL_LED_3TO8_B);
	cbi(SL_LED_3TO8_PORT, SL_LED_3TO8_C);
	
	//jetzt das geshiftete byte auf den Port (PORTD) mappen, so dass Line A, B und C
	//korrekt gesetzt werden (Bsp.: wenn abc=SL_LED3=6 war, also 00000110, wurde durch 
	//das shiften 11000000 daraus. Die obersten 3 Bit von PORTD sind genau mit den 
	//Lines A, B und C verbunden. Also würde eine |-Verknüpfung die Pins genau richtig 
	//setzen.
	//Dazu werden die höchsten 3 Bit von shifttemp mit dem bisherigen Inhalt des
	//SL_LED_3TO8_PORT |-Verknüpft. Das Ergebnis wird dann der neue Inhalt des
	//SL_LED_3TO8_PORTs.

	outb(SL_LED_3TO8_PORT, (inb(SL_LED_3TO8_PORT)|(224&shifttemp)));	//224: Maske 11100000, zur Sicherheit, damit auch sicher nur die höchsten 3 Bit aus shifttemp einfließen
	
}
