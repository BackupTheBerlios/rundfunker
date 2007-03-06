/*!***********************************************************************
 *************************************************************************
 * \file            lcd_driver.h
 * 
 * \author          copyright 1999-2004
 *                  Kai Klenovsek
 *
 * \date            2004-07-27
 * 
 * \b LICENSE: <br>
 *       This code is distributed under the GNU Public License <br>
 *       which can be found at http://www.gnu.org/licenses/gpl.txt <br>
 *
 * \b NOTES:
 *         
 * \todo
 *
 * \bug
 *
 * \b CHANGE \b HISTORY: <br>
 *
 *   <b>- Kai K. / 2005-06-08</b>   
 *           - Changed lib for ATMegas.
 *           - Fucntion lcdputstr_xy() implemented.
 *
***************************************************************************
***************************************************************************/

#ifndef LCDDRIVER_H
#define LCDDRIVER_H


/**************************************************************************
*
* DEFINES
*
***************************************************************************/

/*! \brief Select display typ here  
 *  \param LCD_TYP 0 = 4x40
 *  \param LCD_TYP 1 = 2x40, 1x40
 *  \param LCD_TYP 2 = 2x24, 1x24
 *  \param LCD_TYP 3 = 4x20, 2x20, 1x20          
 *  \param LCD_TYP 4 = 4x16, 2x16, 1x16 */ 
#define LCD_TYP          2


/*! \brief Select lcd controll mode here 
 *  \param LCD_MODE 8 = 8 Bit Mode
 *  \param LCD_MODE 4 = 4 Bit Mode */
#define LCD_MODE         4


#define LCD_INIT                0x30            /*!< Lcd init sequenz */
#define LCD_ON                  0x0C            /*!< Switch on lcd without cursor */
#define LCD_ON_C                0x0E            /*!< Switch on lcd with cursor */
#define LCD_ON_CB               0x0F            /*!< Switch on lcd with blinking cursor */
#define LCD_OFF                 0x08            /*!< Switch off lcd */
#define LCD_CLEAR               0x01            /*!< Clear complete lcd */
#define LCD_ENTRY_4             0x04            /*!< Display shift = OFF and decrement adress */
#define LCD_ENTRY_5             0x05            /*!< Display shift = ON  and decrement adress */
#define LCD_ENTRY_6             0x06            /*!< Display shift = OFF and increment adress */
#define LCD_ENTRY_7             0x07            /*!< Display shift = ON  and increment adress */
#define LCD_SHIFT_CURS_LEFT     0x10            /*!< Shift cursor left */
#define LCD_SHIFT_CURS_RIGHT    0x14            /*!< Shift cursor right */
#define LCD_SHIFT_DISP_LEFT     0x18            /*!< Shift lcd left */
#define LCD_SHIFT_DISP_RIGHT    0x1C            /*!< Shift lcd right */

//! config lines and matrix for 8 bit mode
#define LCD_8_1LINE             0x30
#define LCD_8_2LINE             0x38              

//! config lines and matrix for 4 bit mode
#define LCD_4_1LINE             0x20 
#define LCD_4_2LINE             0x28                   


#define LCD_DDR_PORT    DDRA                    /*!< Lcd data DDR */
#define LCD_DATA_PORT   PORTA                   /*!< Lcd data PORT */
#define LCD_DATA_PIN    PINA                    /*!< Lcd data PORTIN */

#define RW_DDR          DDRA                    /*!< Read / write DDR */
#define RW_PORT         PORTA                   /*!< Read / write PORT */ 
#define RW              PORTA1                  /*!< Read / write BIT */

#define BUSY_PORT       PORTA                   /*!< Busy PORT */
#define BUSY_PIN_PORT   PINA                    /*!< Busy PORTIN */
#define BUSY            PORTA7                  /*!< Busy BIT */

#define RS_DDR          DDRA                    /*!< Data or command DDR */
#define RS_PORT         PORTA                   /*!< Data or command PORT */
#define RS              PORTA2                  /*!< Data or command BIT */

#define ENA_DDR         DDRA                    /*!< Enable DDR */
#define ENA_PORT        PORTA                   /*!< Enable PORT */ 
#define ENA             PORTA0                  /*!< Enable BIT */


#define ENA2_DDR        DDRF                    /*!< Enable 2 DDR */
#define ENA2_PORT       PORTF                   /*!< Enable 2 PORT */
#define ENA2            PORTF3                  /*!< Enable 2 BIT */




#define RS_HIGH()  (RS_PORT|=(1<<RS))
#define RS_LOW()   (RS_PORT&=~(1<<RS))

#define RW_HIGH()  (RW_PORT|=(1<<RW))
#define RW_LOW()   (RW_PORT&=~(1<<RW))

#define ENA_HIGH() (ENA_PORT|=(1<<ENA))
#define ENA_LOW()  (ENA_PORT&=~(1<<ENA))

#define ENA2_HIGH() (ENA2_PORT|=(1<<ENA2))
#define ENA2_LOW()  (ENA2_PORT&=~(1<<ENA2))



/**************************************************************************
*
* GLOBAL PROTOTYPES
*
***************************************************************************/

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/*!\fn extern void lcd_init( void );
 * \brief Init lcd display */
extern void lcd_init( void );
  


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/*!\fn extern void lcd_setcur( unsigned char ypos, unsigned char xpos );
 * \brief Set cursor to special position
 * \param ypos = ypos on lcd
 * \param xpos = xpos on lcd */
extern void lcd_setcur( unsigned char ypos, unsigned char xpos );



//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/*!\fn extern void lcd_sendcom( unsigned char command, unsigned char cntr );
 * \brief Send command to display
 * \param command = Lcd command
 * \param cntr = Lcd controller 1 or 2 second controller only at 4x40 lcd available */
extern void lcd_sendcom( unsigned char command, unsigned char cntr );



//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/*!\fn extern void lcd_putchar( unsigned char data );
 * \brief Send single character to display
 * \param data = Singel byte character */
extern void lcd_putchar( unsigned char data );



//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/*!\fn extern void lcd_putstr( unsigned char *data );
 * \brief Sends a string out of SRAM to display 
 * \param *data = Pointer to string into SRAM */
extern void lcd_putstr( unsigned char *data );



//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/*!\fn extern void lcdputstr_xy( unsigned char *data, unsigned char ypos, unsigned char xpos );
 * \brief Sends a string out of SRAM to display with the additinal feature to set the xy pos on lcd.
 * \param *data = Pointer to string into SRAM */
extern void lcd_putstr_xy( unsigned char *data, unsigned char ypos, unsigned char xpos );



//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/*!\fn extern void lcd_putstr_pgm( const char *data );
 * \brief Send string out of flash memory to display 
 * \param *data = Const pointer into flash memory */
extern void lcd_putstr_pgm( const char *data );



//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/*!\fn extern void lcd_clearline( unsigned char line );
 * \brief Clear a complete line from display
 * \param line = Line number to clear */            
extern void lcd_clearline( unsigned char line );

#endif // LCDDRIVER_H
