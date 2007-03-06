#ifndef SL_LED_3TO8_H
#define SL_LED_3TO8_H

// global AVRLIB defines
#include "avrlibdefs.h"
// global AVRLIB types definitions
#include "avrlibtypes.h"

#define SL_LED_3TO8_PORT	PORTD
#define SL_LED_3TO8_DDR		DDRD
#define SL_LED_3TO8_A		PD7
#define SL_LED_3TO8_B		PD6
#define SL_LED_3TO8_C		PD5

//LED-Codes definieren
//dies ist leider nötig, da auf dem uC die Wertigkeitsreihenfolge der Lines A-B-C lautet,
//im 3o8-Encoder jedoch genau umgekehrt, C-B-A.
//Folglich kann z.B. Led3 nicht mit lightLed(3) zum leuchten gebracht werden,
//sondern nur mit lightLed(SL_LED3)
#define SL_LED0		0	//000	bleibt	000
#define SL_LED1		4	//001	wird	100
#define SL_LED2		2	//010	bleibt	010
#define SL_LED3		6	//011	wird	110
#define SL_LED4		1	//100	wird	001
#define SL_LED5		5	//101	bleibt	101
#define SL_LED6		3	//110	wird	011, reserviert
#define SL_LEDOFF	7	//111	bleibt	111, alle Leds aus

void lightLed(u08 abc);		//das Byte abc enthält in den hinteren 3 Bit die Zustaende
								//fuer die Pins A, B und C. Sollen z.B. A und B aktiviert 
								//und C deaktiviert sein, muesste abc = 6 (also 00000110) sein


#endif
