#ifndef SL_SHIFTREGISTER_H
#define SL_SHIFTREGISTER_H



// global AVRLIB defines
#include "avrlibdefs.h"
// global AVRLIB types definitions
#include "avrlibtypes.h"


#define SL_SHIFTREG_PORT	PORTB
#define SL_SHIFTREG_PIN		PINB
#define SL_SHIFTREG_DDR		DDRB
#define SL_SHIFTREG_Q8		PB4
#define SL_SHIFTREG_CLK		PB1
#define SL_SHIFTREG_SI		PB0
#define SL_SHIFTREG_PL		PB5

//Wieviele "Stationen" hat das Schieberegister?
#define SL_SHIFTREG_STATIONEN 8


void shiftreg_readBit(void);

void sendTasterzustand(u08 zustand);

void waitsl(unsigned long zeit);

void handle_timer2ov();

void set(u08 port);
void clr(u08 port);

#endif


