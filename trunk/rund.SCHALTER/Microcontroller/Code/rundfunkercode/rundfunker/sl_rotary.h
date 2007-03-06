#ifndef SL_ROTARY_H
#define SL_ROTARY_H

// global AVRLIB defines
#include "avrlibdefs.h"
// global AVRLIB types definitions
#include "avrlibtypes.h"

#include <avr/io.h>

#define SL_ROTARY_PORT		PORTD	//Port des Drehdr�ckstellers
#define SL_ROTARY_PIN		PIND	//PortIn des Drehdr�ckstellers
#define SL_ROTARY_DDR		DDRD	//DDR des Drehdr�ckstellers
#define SL_ROTARY_INT		INT0	//Pin f�r externen Interrupt
#define SL_ROTARY_A			PD3		//Terminal A des Drehdr�ckstellers
#define SL_ROTARY_B			PD4		//Terminal B des Drehdr�ckstellers

#define KEINE		0	//"keine Drehung" des Drehdr�ckstellers entspricht 0
#define LINKS		1	//"Linksdrehung" des Drehdr�ckstellers entspricht 1
#define RECHTS		2	//"Rechtsdrehung" des Drehdr�ckstellers entspricht 2

void rotaryInit();					//Initialiserungen von verwendeten Registern etc.
void sendRotation(u08 direction);	//�ber das Rundfunker-Protokoll dem Java-Player 
									//mitteilen, dass eine Drehung entdeckt wurde


#endif
