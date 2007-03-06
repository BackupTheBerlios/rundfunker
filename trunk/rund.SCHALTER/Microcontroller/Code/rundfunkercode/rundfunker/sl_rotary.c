#include "sl_rotary.h"
#include "avrlibdefs.h"
#include "rprintf.h"
#include "uart.h"

void rotaryInit(){
	//Datenrichtung festlegen im DDR
	//Terminal A, Terminal B und INT0 auf Input setzen
	cbi(SL_ROTARY_DDR,SL_ROTARY_A);
	cbi(SL_ROTARY_DDR,SL_ROTARY_B);
	cbi(SL_ROTARY_DDR,SL_ROTARY_INT);

	//Interrupt auf INT0 soll bei fallender Flanke ausgelöst werden.
	//Hierzu muss im EICRA-Register das ISC01-Bit auf high und das 
	//ISC00-Bit auf low gesetzt werden.
	sbi(EICRA,ISC01);
	cbi(EICRA,ISC00);

	//INT0 aktivieren, sicherstellen, dass INT1 deaktiviert ist.
	//Dazu im EIMSK-Register das INT0-Bit auf high und das INT1-Bit auf low setzen.
	//INT1 darf nicht aktiviert sein, da der Pin als I/O-Pin verwendet wird (PD3).
	sbi(EIMSK,INT0);
	cbi(EIMSK,INT1);

	//sei(); //interrupts global aktivieren

}


//über das Rundfunker-Protokoll dem Java-Player mitteilen, dass eine Drehung 
//entdeckt wurde.
void sendRotation(u08 direction){

	//Je nach Übergabewert "JD 1" oder "JD 2" über UART senden.
	
	rprintf("%cJD %d%c",STX,direction,ETX);
	

}



