#include "global.h"
#include "avr/io.h"
#include "avr/signal.h"
#include "avr/interrupt.h"
#include "timerx8.h"


#include "lcd.h"

#include "uart.h"
#include "rprintf.h"
#include "sl_shiftregister.h"

#include "sl_led_3to8.h"
#include "sl_rotary.h"

//#include <stdio.h>
//#include <stdlib.h>

/* Defines */
//Modi, in denen sich der Parser der Java-Player-Befehle befinden kann
#define WAIT_FOR_STX		1		//es wird auf STX (Start of Text, Wert 2) gewartet
#define WAIT_FOR_COMMAND	2		//es wird auf einen Befehl gewartet (Z, L, C, M oder S)
#define WAIT_FOR_LEDNR		3		//es wird auf eine LED-Nr gewartet
#define WAIT_FOR_ZNR		4		//es wird auf eine Zeilen-Nr gewartet
#define WAIT_FOR_ZSPACE		5		//es wird auf das Leerzeichen zwischen Zeilennummer und LCD-Text gewartet
#define WAIT_FOR_LCDCHAR	6		//es wird auf ein Zeichen für das LCD gewartet ODER auch ETX
#define WAIT_FOR_ETX		7		//es wird auf ETX (End of Text, Wert 3) gewartet
#define WAIT_FOR_CURSOREN	8		//es wird auf das Flag zum An-/Ausschalten des Cursors gewartet
#define WAIT_FOR_COLUMN1	9		//es wird auf eine Spaltenangabe (höhere der 2 Stellen) für das Positionieren des Cursors gewartet
#define WAIT_FOR_COLUMN2	10		//es wird auf eine Spaltenangabe (niedrigere der 2 Stellen) für das Positionieren des Cursors gewartet
#define WAIT_FOR_SEPARATOR	11		//es wird auf das Trennzeichen '|' zwischen Spalten- und Zeilenangabe für das Positionieren des Cursors gewartet
#define WAIT_FOR_CURSORLINE	12		//es wird auf eine Zeilenangabe für das Positionieren des Cursors gewartet
#define WAIT_FOR_SECOND_S	13		//es wird nach einem S-Befehl auf das zweite S gewartet, bevor der LCD-Inhalt geleert wird 
//#define WAIT_FOR_SINGLE_H	14		//es wird auf ein 'h' gewartet (ohne STX und ETX). Reaktion: "hello" über UART senden

#define JAVAPLAYER_OK		0x6B	//das Zeichen, das der JavaPlayer über UART sendet, um den Wartescreen zu beenden, 0x6B='k'
#define JAVAPLAYER_HELLO	0x68	//das Zeichen, das der JavaPlayer über UART sendet, um ein "hello" über UART zu erhalten, 0x68='h'



/* Variablen */
volatile BOOL shiftreg_taktflag;
volatile long int timer;
u08 uart_rx_byte;						//das von uart empfangene byte, wird im main() aus dem Uart-Rx-Buffer (uartReceiveByte()) geholt
volatile u08 rotary_rotation;			//Drehung des Drehdrückstellers (KEINE, LINKS oder RECHTS)
//volatile BOOL receivedFirstJavaCommand; //solange noch kein Befehl des JavaPlayers erkannt wurde, Lichtorgel abspielen

//Umsetzungs-Arrays fü Sonderzeichen in Ersatzdarstellung
u08 sonder[] = {'ä',	'ö',	'ü',	'ß',	248,	192,	193,	194,	195,	196,	197,	198,	199,	200,	201,	202,	203,	204,	205,	206,	207,	208,	209,	210,	211,	212,	213,	214,	216,	217,	218,	219,	220,	221,	224,	225,	226,	227,	228,	229,	230,	231,	232,	233,	234,	235,	236,	237,	238,	239,	240,	241,	242,	243,	244,	245,	247,	248,	249,	250,	251,	253,	255	};
u08 ersatz[] = {0xE1,	0xEF,	0xF5,	0xE2,	0x30,	0x41,	0x41,	0x41,	0x41,	0x41,	0x41,	0x41,	0x43,	0x45,	0x45,	0x45,	0x45,	0x49,	0x49,	0x49,	0x49,	0x44,	0xEE,	0x4F,	0x4F,	0x4F,	0x4F,	0xEF,	0xEF,	0x55,	0x55,	0x55,	0xF5,	0x59,	0x61,	0x61,	0x61,	0x61,	0x61,	0x61,	0x61,	0x63,	0x65,	0x65,	0x65,	0x65,	0x69,	0x69,	0x69,	0x69,	0x6F,	0xEE,	0x6F,	0x6F,	0x6F,	0x6F,	0xFD,	0xEF,	0x75,	0x75,	0x75,	0x79,	0x79};

/* Prototypen */
void init_rundfunker();
void handle_shiftreg_interrupt();
//void debugout(char str[]);
u08 getErsatz(u08 sonderzeichen);
void lichtorgel(unsigned long leuchtdauer);

/*
//Falls UART-Rx über Interrupts behandelt werden soll
void handle_uart_rx_interrupt(unsigned char c);
*/

void handle_player_command();		//Verarbeitet einen Befehl des Java-Players (also Z# Titel bzw. LD #)



/* Funktionen */

//ACHTUNG: erst aufrufen, wenn dem Timer2-Overflow die Funktion handle_timer2ov zugewiesen wurde, 
//sonst steht das Programm! Dies erfolgt in main().
void lichtorgel(unsigned long leuchtdauer){
	
	//LED-Lichtorgel
	lightLed(SL_LED0);		//Led0
	waitsl(leuchtdauer);
	lightLed(SL_LED1);		//Led1
	waitsl(leuchtdauer);
	lightLed(SL_LED2);		//Led2
	waitsl(leuchtdauer);
	lightLed(SL_LED3);		//Led3
	waitsl(leuchtdauer);
	lightLed(SL_LED2);		//Led2
	waitsl(leuchtdauer);
	lightLed(SL_LED1);		//Led1
	waitsl(leuchtdauer);
	lightLed(SL_LED0);		//Led0
	waitsl(leuchtdauer);
	lightLed(SL_LEDOFF);	//LEDs aus
}

u08 getErsatz(u08 sonderzeichen){
	int i;
	for(i=0; i<sizeof(sonder);i++){
		if(sonder[i]==sonderzeichen){
			return ersatz[i];
		}
	}
	return 0xB0;	//Wenn es kein vernünftiges Ersatzzeichen gibt, auf dem LCD einen Gedankenstrich ausgeben ('-', Wert = xB0 im LCD, siehe Datenblatt).
}

/*
void debugout(char str[]){
	rprintf("%c",STX);
	rprintfStr(str);
	rprintf("%c",ETX);
}
*/

void init_rundfunker(){
	shiftreg_taktflag	=	FALSE;	//initialisieren des Taktflags für das Schieberegister
	rotary_rotation		=	KEINE;	//initialisieren der Rotary-Encoder-Drehung auf "keine Drehung"
	uart_rx_byte		=	'R';	//dummy-Belegung
//	receivedFirstJavaCommand = FALSE;
//	timer0SetPrescaler(TIMERRTC_CLK_DIV1024); //19531,25 Hz
}


// wird aufgerufen, wenn Timer0 überläuft (--> Interrupt)
// darf keinen Rückgabewert und keine Übergabeparameter besitzen!
volatile unsigned long cnt = 0;
BOOL isLit = FALSE;
void handle_shiftreg_interrupt(){	
	cnt++;	
	if(cnt>10){	
	//	shiftreg_taktflag=!shiftreg_taktflag;
		shiftreg_taktflag = TRUE;
		cnt=0;
	}	
}


void handle_player_command(){

//	rprintf("%chandle_player_command called.\n\r%c",STX,ETX);

	static u08 mode 	= WAIT_FOR_STX;		//Modus des Parsers, standardmäßig auf Start of Text warten
	static u08 lcdZeile = 0;				//LCD-Zeile, in die gerade geschrieben wird
	static u08 lcdText[LCD_LINE_LENGTH+1];	//der Text, der in eine LCD-Zeile soll, um eins länger für 0-termination
	static u08 textIndex = 0;
	static u08 higherCursorPos	= 0;	// Zehnerstelle der Cursorpos.angabe (bei 16 z.B. 10)
	static u08 lowerCursorPos	= 0;	// Einerstelle der Cursorpos.angabe (bei 16 z.B. 6)
	static u08 cursorLine		= 0;	// Zeile, in die der Cursor soll

	u08 tmpP1 = 0;

//	rprintf("%cmode: %d\n\r%c",STX,mode,ETX);
	
	//Getrennte Behandlung des 'h'-Befehls: wartet der Automat gerade nicht auf ein LCD-Zeichen,
	//dann bedeutet ein empfangenes 'h', dass der uC über UART ein "hello" (mit umschließendem 
	//STX und ETX) senden soll.
	if(mode!=WAIT_FOR_LCDCHAR){
		if(uart_rx_byte == JAVAPLAYER_HELLO){
			rprintf("%chello%c",STX,ETX);
		}
	}

	
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
			else if(uart_rx_byte == 'L'){	//L-Befehl (LED-Aktivierung) gelesen
				mode = WAIT_FOR_LEDNR;		//jetzt auf LED-Nummer warten
			}
			else if(uart_rx_byte == 'C'){	//C-Befehl (Cursor-An/Aus) gelesen
				mode = WAIT_FOR_CURSOREN;	//jetzt auf Cursor-Enable_Flag warten
			}
			else if(uart_rx_byte == 'M'){	//M-Befehl (Cursor positionieren) gelesen
		//		rprintf("M gelesen.\r\n");
				mode = WAIT_FOR_COLUMN1;		//jetzt auf Cursor-Enable_Flag warten
			}
			else if(uart_rx_byte == 'S'){	//S-Befehl (LCD-Inhalt leeren) gelesen
				mode = WAIT_FOR_SECOND_S;	//jetzt auf zweites S warten
			}
			else{							//kein Befehl gelesen, also verwerfen
				mode = WAIT_FOR_STX;		//wieder auf STX warten (neuen Befehl)
			}
		break;

		case WAIT_FOR_SECOND_S:
			if(uart_rx_byte=='S'){			//Zweites S gelesen
				lcd_clrscr();				//also LCD-Inhalt leeren (Cursor wird auf home gesetzt)
			}
			mode = WAIT_FOR_STX;			//auf neuen Befehl warten
		break;

		case WAIT_FOR_COLUMN1:
			tmpP1 = uart_rx_byte-'0';
		//	rprintf("Erste Stelle: %d.\r\n",tmpP1);
			if(tmpP1>9)	//Außerhalb des Ziffernbereichs, tmpP1 ist unsigned, also muss auf <0 bicht übeprüft werden
			{ 								
				higherCursorPos = 0;		//zurücksetzen
				tmpP1 = 0;					//zurücksetzen
				mode = WAIT_FOR_STX;		//Befehl verwerfen, auf neuen warten
			}
			else{							//gülige Ziffer gelesen
				higherCursorPos = tmpP1*10;	//Zehnerstelle, also *10
				mode = WAIT_FOR_COLUMN2;
			}
		break;

		case WAIT_FOR_COLUMN2:
			tmpP1 = uart_rx_byte-'0';
		//	rprintf("Zweite Stelle: %d.\r\n",tmpP1);
			if(tmpP1>9)	//Außerhalb des Ziffernbereichs, tmpP1 ist unsigned, also muss auf <0 bicht übeprüft werden
			{ 								
				higherCursorPos = 0;		//zurücksetzen
				lowerCursorPos = 0;			//zurücksetzen
				tmpP1 = 0;					//zurücksetzen
				mode = WAIT_FOR_STX;		//Befehl verwerfen
			}
			else{							//gülige Ziffer gelesen
				lowerCursorPos = tmpP1;		//Einerstelle
				if((higherCursorPos+lowerCursorPos>19) || (higherCursorPos+lowerCursorPos<0)){	//über den Index hinaus
					higherCursorPos = 0;	//zurücksetzen
					lowerCursorPos = 0;		//zurücksetzen
					tmpP1 = 0;				//zurücksetzen
					mode = WAIT_FOR_STX;	//Befehl verwerfen
				}
				else{
					mode = WAIT_FOR_SEPARATOR;	//Jetzt auf Trennzeichen zwischen Spaltenposition und Zeilennummer warten
				}
			}
		break;

		case WAIT_FOR_SEPARATOR:
		//	rprintf("In waitForSep: %c.\r\n",uart_rx_byte);
			if(uart_rx_byte == '|'){		//Trennzeichen gelesen

				mode = WAIT_FOR_CURSORLINE;	//Auf Zeile für Cursorpositionierung warten

			}
			else{
				lowerCursorPos 	= 0;			//zurücksetzen
				higherCursorPos = 0;			//zurücksetzen
				mode = WAIT_FOR_STX;			//halbfertigen Befehl verwerfen
			}				
		break;

		case WAIT_FOR_CURSORLINE:
		//	rprintf("In waitForCursorLine: %c.\r\n",uart_rx_byte);
			if((uart_rx_byte == '0') || (uart_rx_byte == '1') || (uart_rx_byte == '2') || (uart_rx_byte == '3')){
				cursorLine = uart_rx_byte-'0';	//Cursor-Zeile setzen, '0' (wert 48) abziehen, da '0' den ascii-wert 48 hat
				lcd_gotoxy(higherCursorPos+lowerCursorPos, cursorLine);	//Cursor positionieren
			}

			higherCursorPos	=	0;		//zurücksetzen
			lowerCursorPos	=	0;		//zurücksetzen
			cursorLine		=	0;		//zurücksetzen								
			mode = WAIT_FOR_STX;		//auf neuen Befehl warten	
		break;

		case WAIT_FOR_CURSOREN:
			if(uart_rx_byte == '0'){
				lcd_command(LCD_DISP_ON);				// Cursor aus
			}
			else if(uart_rx_byte == '1'){
				lcd_command(LCD_DISP_ON_CURSOR_BLINK);	// Cursor an
			}
										
			mode = WAIT_FOR_STX;		//wieder auf STX warten (neuen Befehl)
			
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
				lcdZeile = uart_rx_byte-'0';	//Zeile setzen, '0' (wert 48) abziehen, da '0' den ascii-wert 48 hat
				mode = WAIT_FOR_ZSPACE;		//als nächstes sollte ein Leerzeichen kommen
			}
			else{							//keine gültige Zeilennummer gelesen
				mode = WAIT_FOR_STX;		//also verwerfen und auf neuen Befehl warten
			}
		break;

		case WAIT_FOR_ZSPACE:
			if(uart_rx_byte == ' '){		//Space gelesen...
				mode = WAIT_FOR_LCDCHAR;	//...also jetzt auf Text fürs LCD warten
			}
			else{
				mode = WAIT_FOR_STX;		//kein Space gelesen, also verwerfen und wieder auf neuen Befehl warten
				lcdZeile = 0;				//und Zeilen-Default wiederherstellen, nachdem der Befehl ungültig war
			}
		break;

		case WAIT_FOR_LCDCHAR:						//Jedes Zeichen kommt ins LCD, es sei denn ETX kommt
			
			if((uart_rx_byte == ETX) || (textIndex>=LCD_LINE_LENGTH)){	//Ende des Textes bzw. des Befehls oder Zeilenende erreicht
				//lcdPrintData(lcdText,textIndex+1);	//also Ausgabe auf LCD
				lcd_gotoxy(0,lcdZeile);
				lcd_puts(lcdText);					//Ausgabe auf LCD, text ist 0-terminated
				
				//Zeilen-Array leeren
				u08 clrind;
				for(clrind=0;clrind<LCD_LINE_LENGTH;clrind++){
					lcdText[clrind] = ' ';			//Leerzeichen schreiben
				}
				lcdText[LCD_LINE_LENGTH]=0;			//vorsichtshalber null-termination neu setzen

				textIndex = 0;						//danach textIndex wieder auf 0, Puffer wird beim nächsten mal überschrieben
				mode = WAIT_FOR_STX;				//auf neuen Befehl warten
			}
			
			else if(textIndex<LCD_LINE_LENGTH){		//es passt noch was in die Zeile
				//Sonderzeichenbehandlung:
				if(uart_rx_byte>127){	//kein Standardzeichen
					uart_rx_byte=getErsatz(uart_rx_byte);
				}
				
				lcdText[textIndex++] = uart_rx_byte;//Zeichen in Array aufnehmen und textIndex inkrementieren
				//modus bleibt unverändert: auf weitere Zeichen für das LCD warten
			}
		break;

		//default:
		//	lcdZeile=0;

	
		/*
		case WAIT_FOR_ETX:				//bisher nicht nötig
			if(uart_rx_byte == ETX){
				
			}
		break;
		*/
	}


}



int main(){
	
	// Initialisierungen
	//sei(); 					// Interrupts aktivieren wird in timerInit() gemacht
	init_rundfunker();			// allgemeine Initialisierungen

	lcd_init(LCD_DISP_ON);		// LCD initialisieren
	
	rotaryInit();				// Drehdrücksteller initialisieren
	timerInit();				// Timer initialisieren

	lightLed(SL_LEDOFF);		// alle LEDs aus am Anfang
	
	
	uartInit();					// UART (serial port) initialisieren
	uartSetBaudRate(9600);		// Baudrate des UART setzen
	rprintfInit(uartSendByte);	// rprintf auf uartSendByte umlenken
	
	
	
	//Timer0-Overflow-Interrupt eine aufzurufende Funktion zuordnen
	timerAttach(TIMER0OVERFLOW_INT, *handle_shiftreg_interrupt);
	//Timer2-Overflow-Interrupt eine aufzurufende Funktion zuordnen
	timerAttach(TIMER2OVERFLOW_INT, *handle_timer2ov);

	
	//Begrüßungsausgaben auf LCD und UART
	lcd_clrscr();
	lcd_gotoxy(5,1);
	lcd_puts("Rundfunker");
	
	
	
	rprintf("Hallo Rundfunker.");


	lichtorgel(200);			//Lichtorgel beim Hochfahren

	

	//Wartebalken anzeigen, bis ein spezielles Zeichen (JAVAPLAYER_OK)
	//über UART empfangen wurde (Bereitmeldung des JavaPlayers).
	//Das empfangene Zeichen kann dann nicht weiter interpretiert werden.
	//Daher sollte der JavaPlayer, wenn er bereit ist, erst das Bereit-Byte 
	//über UART an den uC senden um den Ladescreen zu beenden, und 
	//anschließend die Befehle (eingebettet in STX und ETX) verschicken.
	u16 progress	= 0;
	u16 maxprogress = 255;
	u08 length		= 10;
	BOOL direction	= 1;						//1 inkrementieren, 0 dekrementieren					
	while(1){									//solange nicht das richtige Zeichen über UART kommt
		uartReceiveByte(&uart_rx_byte);
		if(uart_rx_byte == JAVAPLAYER_HELLO){	//kommt ein 'h', so muss auch hier 'hello' gesendet werden
			rprintf("%chello%c",STX,ETX);
		}
		else if(uart_rx_byte == JAVAPLAYER_OK){
			break;								//Ladescreen beenden
		};
		lcd_gotoxy(5,2);						//Cursor positionieren
		waitsl(2);								//etwas warten
		if(direction == 1){
			progress++;
			//lcdProgressBar(pr++, 10, 10);
			if (progress >=maxprogress){
				direction = 0;
			}
		}
		else{
			progress--;
			if (progress <= 1){
				direction = 1;
			}
		}
		lcdProgressBar(progress, maxprogress, length);
	}
	
	lcd_clrscr();	//JavaPlayer hat sich gemeldet, LCD-Inhalt leeren und mit der eigtl. Arbeit anfangen
					//Eventuell könnte der JavaPlayer das 'ClearScreen' übernehmen (Befehl 'SS').
	
	
	// ENDLOSSCHLEIFE
	while(1){

		//Ist Timer0 übergelaufen (d.h. neuer Shiftregister-Takt)?
		if(shiftreg_taktflag){
			shiftreg_readBit();			//Schieberegister auslesen
			shiftreg_taktflag = FALSE;	//flag zuruecksetzen
		}
	
	
		
		
		//Wurde der Drehdrücksteller gedreht?
		if(rotary_rotation != KEINE){
			sendRotation(rotary_rotation);			
			rotary_rotation = KEINE;	//flag zurücksetzen
		}

	

		//Uart-Rx-Puffer auslesen
		if(uartReceiveByte(&uart_rx_byte)){	//enthält der Puffer etwas?
			//	if(!receivedFirstJavaCommand){
			//		receivedFirstJavaCommand = TRUE;
			//	}
			//	if(uart_rx_byte != 0){			//wenn 0-Bytes ignoriert werden sollen, dann hier einkommentieren
				handle_player_command();	//wenn es nicht das 0-Zeichen war, dann verarbeiten	
			//	}
		}

		//if(!receivedFirstJavaCommand){
		//	lichtorgel(200);
		//}

	}

	return 0;
}


/* Interrupt Service Routine für INT0 */
SIGNAL(SIG_INTERRUPT0){
		
		//Es wurde eine Drehung des Drehdrückstellers registriert.
		//Je nachdem, welche Werte Terminal A und B haben,
		//handelte es sich um eine Links- oder Rechtsdrehung.
		//Hier reicht das Überprüfen von Terminal B.
		
		//Flag setzen, um zu signalisieren, in welche Richtung der Drehdrücksteller
		//gedreht wurde.
		if( SL_ROTARY_PIN & BV(SL_ROTARY_B) ){
			rotary_rotation = LINKS;
		}
		else if( SL_ROTARY_PIN & BV(SL_ROTARY_A) ){
			rotary_rotation = RECHTS;
		}
		else{
			rotary_rotation = KEINE;
		}
		
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
