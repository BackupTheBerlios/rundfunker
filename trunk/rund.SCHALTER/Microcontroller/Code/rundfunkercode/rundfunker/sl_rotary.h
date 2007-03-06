#ifndef SL_ROTARY_H
#define SL_ROTARY_H

// global AVRLIB defines
#include "avrlibdefs.h"
// global AVRLIB types definitions
#include "avrlibtypes.h"

#include <avr/io.h>

#define SL_ROTARY_PORT		PORTD	//Port des Drehdrückstellers
#define SL_ROTARY_PIN		PIND	//PortIn des Drehdrückstellers
#define SL_ROTARY_DDR		DDRD	//DDR des Drehdrückstellers
#define SL_ROTARY_INT		INT0	//Pin für externen Interrupt
#define SL_ROTARY_A			PD3		//Terminal A des Drehdrückstellers
#define SL_ROTARY_B			PD4		//Terminal B des Drehdrückstellers

#define KEINE		0	//"keine Drehung" des Drehdrückstellers entspricht 0
#define LINKS		1	//"Linksdrehung" des Drehdrückstellers entspricht 1
#define RECHTS		2	//"Rechtsdrehung" des Drehdrückstellers entspricht 2

void rotaryInit();					//Initialiserungen von verwendeten Registern etc.
void sendRotation(u08 direction);	//über das Rundfunker-Protokoll dem Java-Player 
									//mitteilen, dass eine Drehung entdeckt wurde


#endif
