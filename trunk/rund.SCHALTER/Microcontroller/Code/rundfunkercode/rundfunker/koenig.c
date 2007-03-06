
/* modifizierter Code, Original aus kwrx.c von Stefan König*/

#include <stdio.h>
#include <stdlib.h>
#include <avr/io.h>
#include <stdint.h>
#include <avr/signal.h>
#include <avr/interrupt.h>
#include <string.h>
#include "lcd.c"

#define TIMER1_H 0xb1
#define TIMER1_L 0xdf
#define TIMER0 180

/* Port A */
#define SDIO 0
#define IO_RESET 1
#define MA_RESET 2
#define CSB 3
#define SCLK 4
#define IO_UPDATE 5

#define AGC 6
#define MUTE 7

/* Port B */
#define PIEZO 3
#define DEBUG_LED 4

/* Port C */
/* Display: 4 Bit Daten und 3 Bit Steuerung */
/* Definiert in lcd.h */

/* Port D */
#define Taste1 0
#define Taste2 1
#define Taste3 2
#define Taste4 3
#define Taste5 4
#define Taste6 5
#define Taste7 6
#define Taste8 7
#define up Taste6
#define down Taste5

#define on 0xff
#define off 0x00;

#define MAXFREQUENZ 30000
#define MINFREQUENZ 0

volatile char i,ausgabe,toggle,T0frequenz,sound;
volatile long int timer,frequenz;

char *Frequenz=(char)6000;

void wait(int);
void init_Display(void);
void init_System(void);
void startsound(void);
void tastensound(void);


void init_System(void)
{
	/* Datenrichtung der Ports festlegen: */
	DDRA=0xbf;

	DDRB=0x18;

	DDRC=0xff;

	DDRD=0xff;
	PORTD=0xff; /* pull up's einschalten */

	/* SLEEP MODES deaktivieren */
	MCUCR=0x40;

	/* MCU Control Register */
	MCUCR=0x00;

	TCCR0=0x03;
	TCCR1A=0x00;
	TCCR1B=0x01; /* Prescaler = 1 */
	TIMSK=_BV(TOIE1) | _BV(TOIE0);

        /* Timer neu setzen */
        TCNT1H = TIMER1_H;
        TCNT1L = TIMER1_L;
	TCNT0 = TIMER0;

	frequenz=6000;

	
	/* Groessen des TXD- und RXD-Buffers (UART, serielle Schnittstelle) */
	UART_RX_BUFFER_SIZE = 32; /* in Byte */
	UART_TX_BUFFER_SIZE = 2;  /* in Byte */


}

void init_Display(void)
{
	lcd_init(LCD_DISP_ON);
	lcd_clrscr();
	lcd_puts("KWRX Version 0.1\n");
	lcd_puts("(c) 2005 by Stefan Koenig");
	startsound();
	wait(1000);
	lcd_clrscr();
}

void write_frequ(void)
{
	lcd_gotoxy(10,0);
	itoa(frequenz,Frequenz,10);
	lcd_puts("      ");
	lcd_gotoxy(10,0);
	lcd_puts(Frequenz);
}

void out_Display(void)
{
	lcd_home();
	lcd_puts("Frequenz:");
	lcd_gotoxy(16,0);
	lcd_puts("kHz     Mod: AM\n");
	lcd_puts("Pegel: -20dbuV          AGC: off");
	write_frequ();
}

void tastensound()
{
/*	T0frequenz=50;
	sound=on;
	wait(5);
	sound=off;*/
	wait(100);
}

void startsound()
{
	T0frequenz=200;
	sound=on;
	wait(50);
	sound=off;

	T0frequenz=170;
	sound=on;
	wait(50);
	sound=off;
}

void in_Taste(void)
{
	char temp;
	temp=~(PIND & 0xff);
	lcd_gotoxy(33,1);
	switch(temp)
	{
		case 0x08:
		lcd_puts("x1     ");
		break;
		case 0x04:
		lcd_puts("x10    ");
		break;
		case 0x02:
		lcd_puts("x100   ");
		break;
		case 0x01:
		lcd_puts("x1000  ");
		break;

		case 0x28:
		if(frequenz-1>MINFREQUENZ) frequenz--;
		write_frequ();
		tastensound();
		break;
		case 0x24:
		if(frequenz-10>MINFREQUENZ) frequenz=frequenz-10;
		write_frequ();
		tastensound();
		break;
		case 0x22:
		if(frequenz-100>MINFREQUENZ) frequenz=frequenz-100;
		write_frequ();
		tastensound();
		break;
		case 0x21:
		if(frequenz-1000>MINFREQUENZ) frequenz=frequenz-1000;
		write_frequ();
		tastensound();
		break;

		case 0x48:
		if(frequenz+1<MAXFREQUENZ) frequenz++;
		write_frequ();
		tastensound();
		break;
		case 0x44:
		if(frequenz+10<MAXFREQUENZ) frequenz=frequenz+10;
		write_frequ();
		tastensound();
		break;
		case 0x42:
		if(frequenz+100<MAXFREQUENZ) frequenz=frequenz+100;
		write_frequ();
		tastensound();
		break;
		case 0x41:
		if(frequenz+1000<MAXFREQUENZ) frequenz=frequenz+1000;
		write_frequ();
		tastensound();
		break;

		case 0x60: /* up und down gleichzeitig gedrÃ¼ckt */
		frequenz=6000;
		tastensound();
		break;

		case 0x00:
		lcd_puts("       ");
		break;
	}
}

void wait(int Zeit)
{
	/* Zeit in ms */
	timer=Zeit;
	while(timer)
	{}	
}


int main(void)
{
	init_System();

	sei();

	init_Display();

	while(1)
	{
		in_Taste();
		out_Display();
	}
	return 0;
}

/******************************/
/* Interrupt Service Routinen */
/******************************/
SIGNAL( SIG_OVERFLOW0 )
{
	/* Pulsdauer: 500us */
	TCNT0 = T0frequenz;
	toggle=~toggle;
	if(sound) PORTB = (_BV(PIEZO) & toggle);
}		

SIGNAL( SIG_OVERFLOW1 )
{
	/* Pulsdauer: 1ms */
	if(timer) timer--;
	
        /* Timer neu setzen */
        TCNT1H = TIMER1_H;
        TCNT1L = TIMER1_L;
}


