#ifndef SL_SHIFTREGISTER_H
#define SL_SHIFTREGISTER_H



// global AVRLIB defines
#include "avrlibdefs.h"
// global AVRLIB types definitions
#include "avrlibtypes.h"


#define SL_SHIFTREG_PORT	PORTB
#define SL_SHIFTREG_DDR		DDRB
#define SL_SHIFTREG_CLK		PB1
#define SL_SHIFTREG_SI		PB0

//Wieviele "Stationen" hat das Schieberegister?
//Bzw. wieviele Taster gibt es?
#define SL_SHIFTREG_STATIONEN 6 //TODO oder doch 8?


void shiftreg_readBit(void);

void sendTasterzustand(u08 zustand);

#endif


