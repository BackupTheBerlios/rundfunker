#include "global.h"
#include "avr/io.h"
#include "avr/signal.h"
#include "avr/interrupt.h"
#include "timerx8.h"


#include "lcd.h"
#include "lcdconf.h"

//#include "uart.h"
#include "rprintf.h"

//#include "sl_shiftregister.h"
//#include "sl_led_3to8.h"
//#include "sl_rotary.h"

//#include <stdio.h>
//#include <stdlib.h>

/* Defines */
//Modi, in denen sich der Parser der Java-Player-Befehle befinden kann
#define WAIT_FOR_STX		1	//es wird auf STX (Start of Text, Wert 2) gewartet
#define WAIT_FOR_COMMAND	2	//es wird auf einen Befehl gewartet (Z oder L)
#define WAIT_FOR_LEDNR		3	//es wird auf eine LED-Nr gewartet
#define WAIT_FOR_ZNR		4	//es wird auf eine Zeilen-Nr gewartet
#define WAIT_FOR_ZSPACE		5	//es wird auf das Leerzeichen zwischen Zeilennummer und LCD-Text gewartet
#define WAIT_FOR_LCDCHAR	6	//es wird auf ein Zeichen für das LCD gewartet ODER auch ETX
#define WAIT_FOR_ETX		7	//es wird auf ETX (End of Text, Wert 3) gewartet

//Steuerzeichen für UART
#define STX 2					//Start of Text
#define ETX 3					//End of Text


void lcdPrepare();

/*
// Variablen
//volatile BOOL shiftreg_taktflag;


//Falls UART-Rx über Interrupts behandelt werden soll
//volatile BOOL uart_receivedflag;	//zeigt an, dass etwas über uart empfangen wurde
//
u08 uart_rx_byte;					//das von uart empfangene byte, wird im main() aus dem Uart-Rx-Buffer (uartReceiveByte()) geholt


volatile u08 rotary_rotation;		//Drehung des Drehdrückstellers (KEINE, LINKS oder RECHTS)
//BOOL receiving_lcd_string;			//TRUE, wenn ein String zur Ausgabe auf das LCD über UART erwartet wird,
										//in diesem Fall werden die Protokollbefehle Z und LD nicht als Befehle interpretiert


// Prototypen
void init_rundfunker();
void handle_shiftreg_interrupt();


//Falls UART-Rx über Interrupts behandelt werden soll
//void handle_uart_rx_interrupt(unsigned char c);


void handle_player_command();		//Verarbeitet einen Befehl des Java-Players (also Z# Titel bzw. LD #)



// Funktionen

void init_rundfunker(){
	shiftreg_taktflag	=	FALSE;	//initialisieren des Taktflags für das Schieberegister


//Falls UART-Rx über Interrupts behandelt werden soll
//	uart_receivedflag	=	FALSE;

	rotary_rotation		=	KEINE;	//initialisieren der Rotary-Encoder-Drehung auf "keine Drehung"
	//receiving_lcd_string=	FALSE;	//standardmäßig auf Befehl warten
	uart_rx_byte		=	'R';	//dummy-Belegung
}

// wird aufgerufen, wenn Timer0 überläuft (--> Interrupt)
// darf keinen Rückgabewert und keine Übergabeparameter besitzen!
void handle_shiftreg_interrupt(){
	shiftreg_taktflag=TRUE;
}


//Falls UART-Rx über Interrupts behandelt werden soll
//void handle_uart_rx_interrupt(unsigned char c){
//	//TODO
//	//ruba[0]=c;	//dummy
//	uart_receivedflag	=	TRUE;	//anziegen, dass etwas empfangen wurde
//	uart_rx_byte = c;				//empfangenes Zeichen merken
//}


void handle_player_command(){

	static u08 mode 	= WAIT_FOR_STX;		//Modus des Parsers, standardmäßig auf Start of Text warten
	static u08 lcdZeile = 0;				//LCD-Zeile, in die gerade geschrieben wird
	static u08 lcdText[LCD_LINE_LENGTH];	//der Text, der in eine LCD-Zeile soll
	static u08 textIndex= 0;
	
	switch(mode){
		case WAIT_FOR_STX:
			if(uart_rx_byte == STX){		//STX gelesen
				mode = WAIT_FOR_COMMAND;	//jetzt auf einen Befehl warten
			}
		break;
		
		case WAIT_FOR_COMMAND:
			if(uart_rx_byte == 'Z'){		//Z-Befehl (LCD-Textausgabe) gelesen
				mode = WAIT_FOR_ZNR;		//jetzt auf Zeilennummer warten
			}
			else if(uart_rx_byte == 'L'){	//L-Befehl (LED-AKtivierung) gelesen
				mode = WAIT_FOR_LEDNR;		//jetzt auf LED-Nummer warten
			}
			else{							//kein Befehl gelesen, also verwerfen
				mode = WAIT_FOR_STX;		//wieder auf STX warten (neuen Befehl)
			}
		break;

		case WAIT_FOR_LEDNR:
			
			//mode=WAIT_FOR_ETX;			//nach der Ziffer muss als nächstes ETX kommen
			
			if(uart_rx_byte == '0'){		//ganz übel...
				lightLed(SL_LED0);
			}
			else if(uart_rx_byte == '1'){
				lightLed(SL_LED1);
			}
			
			else if(uart_rx_byte == '2'){
				lightLed(SL_LED2);
			}
			
			else if(uart_rx_byte == '3'){
				lightLed(SL_LED3);
			}
			
			else if(uart_rx_byte == '4'){
				lightLed(SL_LED4);
			}
			
			else if(uart_rx_byte == '5'){
				lightLed(SL_LED5);
			}
			
			else if(uart_rx_byte == '6'){
				lightLed(SL_LED6);
			}
			
			else if(uart_rx_byte == '7'){
				lightLed(SL_LEDOFF);
			}
			//else{							
				mode = WAIT_FOR_STX;		//auf neuen Befehl warten
			//}
		
		break;

		case WAIT_FOR_ZNR:
			if((uart_rx_byte == '0') || (uart_rx_byte == '1') || (uart_rx_byte == '2') || (uart_rx_byte == '3')){
				lcdZeile = uart_rx_byte;	//Zeile setzen
				mode = WAIT_FOR_ZSPACE;	//als nächstes sollte ein Leerzeichen kommen
			}
			else{							//keine gültige Zeilennummer gelesen
				mode = WAIT_FOR_STX;		//also verwerfen und auf neuen Befehl warten
			}
		break;

		case WAIT_FOR_ZSPACE:
			if(uart_rx_byte == ' '){		//Space gelesen
				mode = WAIT_FOR_LCDCHAR;	//also jetzt auf Text fürs LCD warten
			}
			else{
				mode = WAIT_FOR_STX;		//kein Space gelesen, also verwerfen und wieder auf neuen Befehl warten
				lcdZeile = 0;				//und Zeilen-Default wiederherstellen, nachdem der Befehl ungültig war
			}
		break;

		case WAIT_FOR_LCDCHAR:						//Jedes Zeichen kommt ins LCD, es sei denn ETX kommt
			if((uart_rx_byte == ETX) || (textIndex>=LCD_LINE_LENGTH)){	//Ende des Textes bzw. des Befehls oder Zeilenende erreicht
				lcdPrintData(lcdText,textIndex+1);	//also Ausgabe auf LCD
				//lcdPrintData(&lcdText,textIndex+1);	//also Ausgabe auf LCD
				textIndex = 0;						//danach textIndex wieder auf 0, Puffer wird beim nächsten mal überschrieben
				mode = WAIT_FOR_STX;				//auf neuen Befehl warten
			}
			
			else if(textIndex<LCD_LINE_LENGTH){			//es passt noch was in die Zeile
				lcdText[textIndex++] = uart_rx_byte;//Zeichen in Array aufnehmen und textIndex inkrementieren
				//modus bleibt unverändert: auf weiter Zeichen für das LCD warten
			}
		break;

	
	//
	//	case WAIT_FOR_ETX:				//bisher nicht nötig
	//		if(uart_rx_byte == ETX){
	//			
	//		}
	//	break;
		
	}


}
*/

/*
void main(){
	
	u08 ausgabetext = 'S';	//Testzweck
	//unsigned int tim0;
	//unsigned int tim1;
	//unsigned int tim2;

	
	// Initialisierungen

	//sei(); //Interrupts aktivieren wird in timerInit() gemacht
	init_rundfunker();	//allgemeine Initialisierungen
	lcdInit();			//LCD initialisieren
	rotaryInit();		//Drehdrücksteller initialisieren
	timerInit();		//Timer initialisieren
	
	// initialize the UART (serial port)
	uartInit();
	// set the baud rate of the UART for our debug/reporting output
	uartSetBaudRate(9600);

	
	//
	//Falls UART-Rx über Interrupts behandelt werden soll
	//Wenn ein Uart-RX-Interrupt kommt, soll handle_uart_rx() aufgerufen werden
	//uartSetRxHandler(&handle_uart_rx_interrupt);
	//

	// initialize the timer system
	// timerInit();

	// initialize rprintf system
	// 	 use uartSendByte as the output for all rprintf statements
	//   this will cause all rprintf library functions to direct their
	//   output to the uart
	rprintfInit(uartSendByte);
	
	
	//rprintfInit(lcdDataWrite);	//rprintf aufs LCD umlenken



	// TESTS

	lcdPrintData(&ausgabetext, 1); //Testausgabe auf LCD

	//LEDs testen, Leds 0 bis 5 möglich,Led 6 ist Reserve (unbenutzt),
	//der Wert 7 schaltet die  Leds aus.
	lightLed(SL_LED1);		//Led1
	lightLed(SL_LED4);		//Led4
	lightLed(SL_LED5);		//Led6
	lightLed(SL_LEDOFF);	//sollte die Leds ausschalten
	lightLed(SL_LED0);		//Led2

	
	//Timer0-Overflow-Interrupt eine aufzurufende Funktion zuordnen
	timerAttach(TIMER0OVERFLOW_INT, *handle_shiftreg_interrupt);


	// ENDLOSSCHLEIFE

	BOOL testblink = TRUE; //Test
	u32 a = 0;             //Test

	while(1){
	//	tim0 = TCNT0;
	//	tim1 = TCNT1;
	//	tim2 = TCNT2;
	
	// Test
		a++;
		if(a>333333){
			testblink = !testblink;
			if(testblink){
				lightLed(SL_LED0);
			}
			else{
				lightLed(SL_LEDOFF);
			}
			a=0;
		}

	// Test Ende
		
		
		//Wurde der Drehdrücksteller gedreht?
		if(rotary_rotation != KEINE)
		{
			sendRotation(rotary_rotation);			
			rotary_rotation = KEINE;	//flag zurücksetzen
		}

		//Uart-Rx-Puffer auslesen
		if(uartReceiveByte(&uart_rx_byte)){	//enthält der Puffer etwas?
		//	if(uart_rx_byte != 0){			//wenn 0-Bytes ignoriert werden sollen, dann hier einkommentieren
				handle_player_command();	//wenn es nicht das 0-Zeichen war, dann verarbeiten	
		//	}
		}

		//
		//Falls UART-Rx über Interrupts behandelt werden soll
		//Wurde etwas über Uart empfangen?
		//if(uart_receivedflag){
			// TODO
				
			//entsprechend des gelesenen Zeichens reagieren 
			//--> Rundfunkerprotokoll	implementieren
			
			
		//	handle_player_command();
		//						
		//	uart_receivedflag = FALSE;	//flag zurücksetzen
		//}
		//
		
		//Ist Timer0 übergelaufen (d.h. neuer Shiftregister-Takt)?
		if(shiftreg_taktflag){
			shiftreg_readBit();
			shiftreg_taktflag = FALSE;	//flag zuruecksetzen
		}
	}
}
*/

BOOL lcdPrepared = FALSE;

void lcdPrepare(){
	if(!lcdPrepared){
		lcdInit();
		lcdPrepared=TRUE;
	}
}


//Main für LCD-Test
int main(void)
{
//u08 a=0;

	// initialize our libraries
	// initialize the UART (serial port)
//	uartInit();
	// make all rprintf statements use uart for output
//	rprintfInit(uartSendByte);
	// turn on and initialize A/D converter
	//a2dInit();
	// initialize the timer system
	timerInit();
	// print a little intro message so we know things are working
//	rprintf("\r\nWelcome to AVRlib!\r\n");
	
	timer1SetPrescaler(4);
	timerAttach(TIMER1OVERFLOW_INT, lcdPrepare);

	// initialize LCD
	//lcdInit();
	// direct printf output to LCD
//	rprintfInit(lcdDataWrite);

	// print message on LCD
	//rprintf("Welcome to AVRlib!");

	//DDRC = 0x00;
	//PORTC = 0x00;

	// display a bargraph of the analog voltages on a2d channels 0,1
	
	while(1)
	{
	//	lcdGotoXY(0,0);
	//	lcdProgressBar(a2dConvert8bit(0), 255, 20);
	//	rprintf(" X: %d", a2dConvert8bit(0));
	//	rprintf(" Sample: %d", a++);
	//	lcdGotoXY(0,1);
	//	lcdProgressBar(a2dConvert8bit(1), 255, 20);
	//	rprintf(" Y: %d", a2dConvert8bit(1));

		if(lcdPrepared){
			lcdClear();
			lcdGotoXY(0,0);
			rprintf("0. Zeile");
			lcdGotoXY(1,1);
			rprintf("1. Zeile %d",'ü');
			lcdGotoXY(2,2);
			rprintf("2. Zeile");
			lcdGotoXY(3,3);
			rprintf("3. Zeile");
		}
	}

	return 0;
}


// Interrupt Service Routine für INT0
SIGNAL(SIG_INTERRUPT0){
		//Es wurde eine Drehung des Drehdrückstellers registriert.
		//Je nachdem, welche Werte Terminal A und B der DDS haben,
		//handelte es sich um eine Links- oder Rechtsdrehung.
		//TODO: welche Werte bewirken was? Übergänge von High nach Low erkennen?

		//TODO: Flag setzen, um zu signalisieren, in welche Richtung der Drehdrücksteller
		//gedreht wurde.
//		rotary_rotation = RECHTS;	//dummy
		
		/*
		sendRotation() direkt hier aufzurufen, funktioniert nicht,
		da die Interrupts bei Aufruf dieser Interrupt Service Routine
		deaktiviert werden. Denn beim Senden der einzelnen Bytes mittels der von
		sendRotation() verwendeten Funktion rprintfChar() wird nach jedem 
		Byte auf einen Interrupt gewartet (TxComplete, TXC0), bevor das nächste 
		Byte gesendet wird. Da dieser ausbleibt, hängt das Programm.
		Erst nach Abarbeitung der ISR werden die Interrupts automatisch wieder
		aktiviert.
		Daher wird hier nur ein Drehrichtungs-Flag gesetzt, das in main() abgefragt wird.
		*/
}


