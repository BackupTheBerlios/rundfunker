/*-----------------------------------------------------------------------*/
/*  Hardware control functions for AVRSP  R0.37                          */
/*-----------------------------------------------------------------------*/

#include <stdio.h>
#include <string.h>
#include <conio.h>
#include <windows.h>
#include "avrsp.h"
#include "hwctrl.h"


#define COM_DAT	((WORD)(PortBase + C_DAT))
#define COM_IMR	((WORD)(PortBase + C_IMR))
#define COM_ISR	((WORD)(PortBase + C_ISR))
#define COM_FCR	((WORD)(PortBase + C_FCR))
#define COM_LCR	((WORD)(PortBase + C_LCR))
#define COM_MCR	((WORD)(PortBase + C_MCR))
#define COM_LSR	((WORD)(PortBase + C_LSR))
#define COM_MSR	((WORD)(PortBase + C_MSR))
#define COM_SCR	((WORD)(PortBase + C_SCR))
#define LPT_DAT	((WORD)(PortBase + L_DAT))
#define LPT_STA	((WORD)(PortBase + L_STA))
#define LPT_CTL	((WORD)(PortBase + L_CTL))



/*----------------------------------------------------------------------
  Control Variables
----------------------------------------------------------------------*/

static WORD PortBase, PortType, PortDly;
static HANDLE hComm = INVALID_HANDLE_VALUE;
static DWORD ModemStat;
static BOOL sig_break;
static char str_info[100];

static struct {
	DWORD	Rp;
	DWORD	Wp;
	DWORD	Ctr;
	BYTE	Buff[100];
} RcvrFifo;




/*----------------------------------------------------------------------
  Module Private Functions
----------------------------------------------------------------------*/


/* Initialize GIVEIO */

static int init_driver ()
{
	int ls = 0;
	HANDLE hdev;
	SC_HANDLE hsc, hsv;
	char filepath[_MAX_PATH], *cp;
	BOOL res;


	while (1) {
		ls++;

		if(ls >= 4)		/* Could not open, start or register giveio due to any reason. */
			return (-1);

		if(ls >= 3) {	/* Not registered. Register GIVEIO.SYS to the SCM database */
			if(SearchPath(NULL, "giveio.sys", NULL, sizeof(filepath), filepath, &cp) == 0) continue;
			if((hsc = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS)) != NULL) {
				if((hsv = CreateService(hsc,
										"giveio", "giveio", 
										SERVICE_ALL_ACCESS, SERVICE_KERNEL_DRIVER, SERVICE_DEMAND_START, SERVICE_ERROR_IGNORE,
										filepath,
										NULL, NULL, NULL, NULL, NULL)) != NULL) {
					CloseServiceHandle(hsv);
				} else {
					if((hsv = OpenService(hsc, "giveio", SERVICE_ALL_ACCESS)) != NULL) {
						DeleteService(hsv);
						CloseServiceHandle(hsv);
						hsv = NULL;
					}
				}
				CloseServiceHandle(hsc);
			}
			if((hsc == NULL) || (hsv == NULL)) continue;
		}

		if(ls >= 2) {	/* Not started. Start GIVEIO */
			if((hsc = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS)) != NULL) {
				if((hsv = OpenService(hsc, "giveio", SERVICE_ALL_ACCESS)) != NULL) {
					res = StartService(hsv, 0, NULL);
					CloseServiceHandle(hsv);
				}
				CloseServiceHandle(hsc);
			}
			if((hsc == NULL) || (hsv == NULL) || (res == FALSE)) continue;
		}

		/* Open GIVEIO to clear IOPM of this process */
		hdev = CreateFile("\\\\.\\giveio", GENERIC_READ, 0, NULL,
							  OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
		if(hdev == INVALID_HANDLE_VALUE) continue;
		CloseHandle(hdev);
		break;
	} /* while */

	return (0);

}




/* Check if the COM/LPT port is present */

static int check_comport ()
{
	_outp(COM_LCR, 0x0C);
	if((BYTE)_inp(COM_LCR) != 0x0C) return (-1);
	_outp(COM_LCR, 0x03);
	if((BYTE)_inp(COM_LCR) != 0x03) return (-1);
	return (0);
}




static int check_lptport ()
{
	_outp(LPT_CTL, 0);
	_outp(LPT_DAT, 0x08);
	if((BYTE)_inp(LPT_DAT) != 0x08) return (-1);
	_outp(LPT_DAT, 0x40);
	if((BYTE)_inp(LPT_DAT) != 0x40) return (-1);
	return (0);
}




/* SPI delayed read FIFO */

static void fifo_put (BYTE d)
{
	if(RcvrFifo.Ctr < sizeof(RcvrFifo.Buff)) {
		RcvrFifo.Buff[RcvrFifo.Wp++] = d;
		RcvrFifo.Ctr++;
		if(RcvrFifo.Wp >= sizeof(RcvrFifo.Buff))
			RcvrFifo.Wp = 0;
	}
}




static BYTE fifo_get ()
{
	BYTE d = 0xFF;


	if(RcvrFifo.Ctr > 0) {
		d = RcvrFifo.Buff[RcvrFifo.Rp++];
		RcvrFifo.Ctr--;
		if(RcvrFifo.Rp >= sizeof(RcvrFifo.Buff))
			RcvrFifo.Rp = 0;
	}
	return d;
}




/* I/O control delay */

static void iodly()
{
	WORD d;
	LARGE_INTEGER val1, val2;


	if(PortType == TY_VCOM) {
		QueryPerformanceCounter(&val1);
		QueryPerformanceFrequency(&val2);
		val1.QuadPart += val2.QuadPart / 1000000 * PortDly;
		do
			QueryPerformanceCounter(&val2);
		while(val2.QuadPart < val1.QuadPart);
	}
	else {
		for(d = PortDly; d > 0; d--)
			_inp(PortBase);
	}
}




/* Bufferred data transmission and reception for SPI bridge */

static void send_bridge (BYTE *buffer, DWORD count)
{
	static BYTE outbuff[400];
	static int wp = 0;
	DWORD cnt;


	if(count == 0) {	/* Zero means flush transmission buffer */
		if(wp) {
			WriteFile(hComm, outbuff, wp, &cnt, NULL);
			wp = 0;
		}
		return;
	}

	do {
		outbuff[wp++] = *buffer++;
		if(wp >= sizeof(outbuff)) {
			WriteFile(hComm, outbuff, wp, &cnt, NULL);
			wp = 0;
		}
	} while(--count);
}




static BOOL read_bridge (BYTE *buffer, DWORD count)
{
	DWORD cnt = 0;


	send_bridge(NULL, 0);	/* flush left data in the transmission buffer */
	ReadFile(hComm, buffer, count, &cnt, NULL);
	return (cnt == count) ? TRUE : FALSE;
}




/*----------------------------------------------------------------------
  Public Functions
----------------------------------------------------------------------*/


/* Search and Open configuration file */

FILE *open_cfgfile(char *filename)
{
	FILE *fp;
	char filepath[256], *dmy;


	if((fp = fopen(filename, "rt")) != NULL) 
		return fp;
	if(SearchPath(NULL, filename, NULL, sizeof(filepath), filepath, &dmy)) {
		if((fp = fopen(filepath, "rt")) != NULL) 
			return fp;
	}
	return NULL;
}




/* Wait for dly msec */

void delay_ms (WORD dly)
{
	LARGE_INTEGER val1, val2;
	BYTE spicmd[3] = { FLAG, SPI_WAIT, 0 };


	if(PortType == TY_BRIDGE) {		/* Issue wait commadnd for bridge */
		spicmd[2] = (BYTE)dly;
		send_bridge(spicmd, 3);
	}
	else {							/* Make wait using Win32 API */
		QueryPerformanceCounter(&val1);
		QueryPerformanceFrequency(&val2);
		val1.QuadPart += val2.QuadPart / 1000 * dly;
		do
			QueryPerformanceCounter(&val2);
		while(val2.QuadPart < val1.QuadPart);
	}
}




/* Flush command pipeline for SPI bridge */

int spi_flush ()
{
	BYTE spicmd[2] = { FLAG, SPI_NOP };
	int n;


	if(PortType != TY_BRIDGE) return 0;	/* Exit if no bridge */

	send_bridge(spicmd, 2);		/* Send SPI_NOP command */

	n = 50;
	do {						/* Wait until SPI_NOP is processed in timeout of 5 sec */
		read_bridge(spicmd, 1);
		if(spicmd[0] == SPI_NOP) break;
	} while(--n);

	return n ? 1 : 0;
}




/* Initialize control port and return port status */

int open_ifport (PORTPROP *pc)
{
	OSVERSIONINFO vinfo = { sizeof(OSVERSIONINFO) };
	LARGE_INTEGER val1;
	BYTE cmdspi[6];
	static const WORD PortsCom[] = { COM1ADR, COM2ADR, COM3ADR, COM4ADR };
	static const WORD PortsLpt[] = { LPT1ADR, LPT2ADR, LPT3ADR };
	char sComm[16];
	DCB dcb = { sizeof(DCB),
				9600, TRUE, FALSE, TRUE, FALSE,
				DTR_CONTROL_DISABLE, FALSE,
				TRUE, FALSE, FALSE, FALSE, FALSE,
				RTS_CONTROL_DISABLE, FALSE, 0, 0,
				10, 10,
				8, NOPARITY, ONESTOPBIT, '\x11', '\x13', '\xFF', '\xFF', 0 };
	COMMTIMEOUTS commtimeouts = { 0, 1, 100, 1, 300};


	/* Check if high resolution timer is supported */
	QueryPerformanceFrequency(&val1);
	if (val1.QuadPart == 0) {
		pc->Info1 = "Incompatible envilonment.\n";
		return 1;
	}

	PortDly = pc->Delay;		/* I/O delay for direct I/O control */
	dcb.BaudRate = pc->Baud;	/* Bit rate for SPI bridge */

	/* Open direct I/O driver if needed */
	if(GetVersionEx(&vinfo) == FALSE) {
		pc->Info1 = "Incompatible envilonment.\n";
		return 1;
	}
	if((vinfo.dwPlatformId == VER_PLATFORM_WIN32_NT)
	    && ((pc->PortClass == TY_COMM)||(pc->PortClass == TY_LPT))) {
		if(init_driver()) {
			pc->Info1 = "I/O driver initialization failed.\n";
			return 1;
		}
	}

	/* Use COM port in direct I/O */
	if(pc->PortClass == TY_COMM) {
		if((pc->PortNum < 1)||(pc->PortNum > 4)) {
			pc->Info1 = "Invalid Port#.\n";
			return 1;
		}
		PortBase = PortsCom[pc->PortNum - 1];
		sprintf(str_info, "No COM%u(0x%X) port.\n", pc->PortNum, PortBase);
		pc->Info1 = str_info;
		if(check_comport()) {
			if(vinfo.dwPlatformId != VER_PLATFORM_WIN32_NT) return 1;
			sprintf(sComm, "\\\\.\\COM%u", pc->PortNum);
			hComm = CreateFile(sComm, GENERIC_READ, 0, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
			if(hComm == INVALID_HANDLE_VALUE) return 1;
			if(check_comport()) return 1;
		}

		_outp(COM_IMR, 0x00);		/* Mask interrupts */
		pc->Info1 = NULL;
		PortType = TY_COMM;
		return 0;
	}

	/* Use COM port via API */
	if(pc->PortClass == TY_VCOM) {
		sprintf(sComm, "\\\\.\\COM%u", pc->PortNum);
		hComm = CreateFile(sComm, GENERIC_READ|GENERIC_WRITE, 0, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
		if(hComm == INVALID_HANDLE_VALUE) {
			sprintf(str_info, "%s could not be opened.\n", sComm);
			pc->Info1 = str_info;
			return 1;
		}
		SetCommState(hComm, &dcb);
		EscapeCommFunction(hComm, CLRRTS);
		EscapeCommFunction(hComm, CLRDTR);
		PortType = TY_VCOM;
		return 0;
	}

	/* Use SPI bridge attached on COM port */
	if(pc->PortClass == TY_BRIDGE) {
		sprintf(sComm, "\\\\.\\COM%u", pc->PortNum);
		hComm = CreateFile(sComm, GENERIC_READ|GENERIC_WRITE, 0, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
		if(hComm == INVALID_HANDLE_VALUE) {
			sprintf(str_info, "%s could not be opened.\n", sComm);
			pc->Info1 = str_info;
			return 1;
		}
		dcb.fOutxCtsFlow = TRUE;
		dcb.fRtsControl = RTS_CONTROL_HANDSHAKE;
		SetCommState(hComm, &dcb);
		SetCommTimeouts(hComm, &commtimeouts);
		EscapeCommFunction(hComm, CLRDTR);
		delay_ms(10);
		while(read_bridge(cmdspi, sizeof(cmdspi)));
		cmdspi[0] = FLAG-1;
		cmdspi[1] = FLAG; cmdspi[2] = SPI_ENABLE;	/* Enable Bridge */
		cmdspi[3] = FLAG; cmdspi[4] = SPI_SETDLY; cmdspi[5] = (BYTE)PortDly; /* Set SPI delay */
		send_bridge(cmdspi, 6);		/* Send bridge initialization commands */
		read_bridge(cmdspi, 2);		/* Check if the commands are accepted */
		if((cmdspi[0] != SPI_ENABLE) || (cmdspi[1] != SPI_SETDLY)) {
			sprintf(str_info, "No SPI bridge on the %s.\n", sComm);
			pc->Info1 = str_info;
			return 1;
		}
		PortType = TY_BRIDGE;
		return 0;
	}

	/* Use LPT port in direct I/O */
	if(pc->PortClass == TY_LPT) {
		if((pc->PortNum < 1)||(pc->PortNum > 3)) {
			pc->Info1 = "Invalid Port#.\n";
			return 1;
		}
		PortBase = PortsLpt[pc->PortNum - 1];
		if(check_lptport()) {
			sprintf(str_info, "No LPT%u(0x%X) port.\n", pc->PortNum, PortBase);
			pc->Info1 = str_info;
			return 1;
		}

		_outp(LPT_DAT, 0x20);	/* Check if the adapter is ByteBlasterMV (D5-ACK) */
		if(_inp(LPT_STA) & S_ACK) {
			_outp(LPT_DAT, 0);
			if((_inp(LPT_STA) & S_ACK) == 0) {
				_outp(LPT_CTL, BA_ENA);
				pc->Info1 = "Altera ByteBlasterMV was found.\n";
				PortType = TY_ALTERA;
				if(_inp(LPT_STA) & S_ERR) return 0;	/* Check target power */
				pc->Info2 = "But target power is OFF.\n";
				return 1;
			}
		}
		_outp(LPT_DAT, 0x80);	/* Check if the adapter is AVRSP (D7-PE) */
		if(_inp(LPT_STA) & S_PE) {
			_outp(LPT_DAT, 0);
			if((_inp(LPT_STA) & S_PE) == 0) {
				_outp(LPT_DAT, B_ENA);
				pc->Info1 = "AVRSP adapter was found.\n";
				PortType = TY_AVRSP;
				if(_inp(LPT_STA) & S_ERR) return 0;	/* Check target power */
				pc->Info2 = "But target power is OFF.\n";
				return 1;
			}
		}
		_outp(LPT_DAT, 0x40);	/* Check if the adapter is Xilinx JTAG (D6-BUSY-PE) */
		if((_inp(LPT_STA) & (S_PE | S_BUSY)) == S_PE) {
			_outp(LPT_DAT, 0);
			if((_inp(LPT_STA) & (S_PE | S_BUSY)) == S_BUSY) {
				pc->Info1 = "Xilinx JTAG adapter was found.\n";
				PortType = TY_XILINX;
				if(_inp(LPT_STA) & S_ERR) return 0;	/* Check target power */
				pc->Info2 = "But target power is OFF.\n";
				return 1;
			}
		}
		_outp(LPT_DAT, 0x40);	/* Check if the adapter is Lattice ISP (D6-PE) */
		if(_inp(LPT_STA) & S_PE) {
			_outp(LPT_DAT, 0);
			if((_inp(LPT_STA) & S_PE) == 0) {
				pc->Info1 = "Lattice ISP adapter was found.\n";
				PortType = TY_LATTICE;
				if(_inp(LPT_STA) & S_ERR) return 0;	/* Check target power */
				pc->Info2 = "But target power is OFF.\n";
				return 1;
			}
		}
		_outp(LPT_DAT, 0x01);	/* Check if the adapter is STK200 dongle (D0-PE) */
		if(_inp(LPT_STA) & S_PE) {
			_outp(LPT_DAT, 0);
			if((_inp(LPT_STA) & S_PE) == 0) {
				pc->Info1 = "STK200 ISP dongle was found.\n";
				PortType = TY_STK200;
				if(_inp(LPT_STA) & S_ERR) return 0;	/* Check target power */
				pc->Info2 = "But target power is OFF.\n";
				return 1;
			}
		}
		sprintf(str_info, "No ISP adapter on the LPT%u.\n", pc->PortNum);
		pc->Info1 = str_info;
		return 1;
	}

	pc->Info1 = "Invalid port class.\n";
	return 1;
}




/* Close control port */

void close_ifport ()
{
	BYTE spicmd[2] = {FLAG, SPI_DISABLE};


	switch (PortType) {
		case TY_COMM :
			_outp(COM_MCR, 0);
			_outp(COM_LCR, 3);
			break;

		case TY_VCOM :
			break;

		case TY_BRIDGE :
			send_bridge(spicmd, 2);
			read_bridge(spicmd, 1);
			break;

		case TY_AVRSP :
			_outp(LPT_DAT, 0);
			break;

		case TY_STK200 :
			_outp(LPT_DAT, BS_DIS);
			break;

		case TY_XILINX :
			_outp(LPT_DAT, BX_DIS1|BX_DIS2);
			break;

		case TY_LATTICE :
			_outp(LPT_DAT, BL_DIS);
			break;

		case TY_ALTERA :
			_outp(LPT_CTL, 0);
			break;
	}

	if(hComm != INVALID_HANDLE_VALUE)
		CloseHandle(hComm);

	PortType = 0;
}




/* Apply a reset sequence */

void spi_reset ()
{
	BYTE spicmd[2] = { FLAG, SPI_RESET };


	switch (PortType) {
		case TY_COMM :
			_outp(COM_MCR, 0);		/* RESET,SCK = "L" */
			delay_ms(10);			/* 10msec */
			_outp(COM_MCR, C_RES);	/* RESET = "H" */
			iodly(); iodly();		/* delay */
			_outp(COM_MCR, 0);		/* RESET = "L" */
			break;

		case TY_VCOM :
			EscapeCommFunction(hComm, CLRDTR);
			EscapeCommFunction(hComm, CLRRTS);
			delay_ms(10);
			EscapeCommFunction(hComm, SETDTR);
			iodly(); iodly();
			EscapeCommFunction(hComm, CLRDTR);
			break;

		case TY_BRIDGE :
			delay_ms(10);
			send_bridge(spicmd, 2);
			break;

		case TY_AVRSP :
			_outp(LPT_DAT, B_ENA);
			delay_ms(10);
			_outp(LPT_DAT, B_ENA|B_RES);
			iodly(); iodly();
			_outp(LPT_DAT, B_ENA);
			break;

		case TY_STK200 :
			_outp(LPT_DAT, 0);
			delay_ms(10);
			_outp(LPT_DAT, BS_RES);
			iodly(); iodly();
			_outp(LPT_DAT, 0);
			break;

		case TY_XILINX :
			_outp(LPT_DAT, BX_DIS2);
			delay_ms(10);
			_outp(LPT_DAT, BX_DIS2|BX_RES);
			iodly(); iodly();
			_outp(LPT_DAT, BX_DIS2);
			break;

		case TY_LATTICE :
			_outp(LPT_DAT, 0);
			delay_ms(10);
			_outp(LPT_DAT, BL_RES);
			iodly(); iodly();
			_outp(LPT_DAT, 0);
			break;

		case TY_ALTERA :
			_outp(LPT_DAT, 0);
			delay_ms(10);
			_outp(LPT_DAT, BA_RES);
			iodly(); iodly();
			_outp(LPT_DAT, 0);
			break;

	}
	RcvrFifo.Rp = 0;					/* Flush FIFO */
	RcvrFifo.Wp = 0;
	RcvrFifo.Ctr = 0;
	delay_ms(30);						/* 30msec */
}




/* Apply an SCK pulse (for command synchronization) */

void spi_clk ()
{
	BYTE spicmd[2] = { FLAG, SPI_SCK };


	switch (PortType) {
		case TY_COMM :
			iodly();
			_outp(COM_MCR, C_SCK);		/* SCK = "H" */
			iodly();
			_outp(COM_MCR, 0);			/* SCK = "L" */
			break;

		case TY_VCOM :
			iodly();
			EscapeCommFunction(hComm, SETRTS);
			iodly();
			EscapeCommFunction(hComm, CLRRTS);
			break;

		case TY_BRIDGE :
			send_bridge(spicmd, 2);
			break;

		case TY_AVRSP :
			iodly();
			_outp(LPT_DAT, B_ENA|B_SCK);
			iodly();
			_outp(LPT_DAT, B_ENA);
			break;

		case TY_STK200 :
			iodly();
			_outp(LPT_DAT, BS_SCK);
			iodly();
			_outp(LPT_DAT, 0);
			break;

		case TY_XILINX :
			iodly();
			_outp(LPT_DAT, BX_DIS2|BX_SCK);
			iodly();
			_outp(LPT_DAT, BX_DIS2);
			break;

		case TY_LATTICE :
			iodly();
			_outp(LPT_DAT, BL_SCK);
			iodly();
			_outp(LPT_DAT, 0);
			break;

		case TY_ALTERA :
			_outp(LPT_DAT, BA_SCK);
			iodly();
			_outp(LPT_DAT, 0);
			break;
	}

}




/* Send a byte to the device */

void spi_xmit (BYTE td)
{
	BYTE a;
	BYTE spicmd[2];
	int n = 8;


	switch (PortType) {
		case TY_COMM :
			do {
				a = ((td & 0x80) ? C_MOSI : 0);		/* MOSI(TD) = data */
				_outp(COM_LCR, a);
				iodly();							/* delay */
				_outp(COM_MCR, C_SCK);				/* SCK(RS) = "H" */
				iodly();							/* delay */
				_outp(COM_MCR, 0);					/* SCK(RS) = "L" */
				td <<= 1;
			} while (--n);
			break;

		case TY_VCOM :
			do {
				if(td & 0x80) {						/* MOSI(TD) = data */
					if(!sig_break) {
						EscapeCommFunction(hComm, SETBREAK);
						sig_break = TRUE;
					}
				} else {
					if(sig_break) {
						EscapeCommFunction(hComm, CLRBREAK);
						sig_break = FALSE;
					}
				}
				iodly();							/* delay */
				EscapeCommFunction(hComm, SETRTS);	/* SCK(RS) = "H" */
				iodly();							/* delay */
				EscapeCommFunction(hComm, CLRRTS);	/* SCK(RS) = "L" */
				td <<= 1;
			} while (--n);
			break;

		case TY_BRIDGE :
			spicmd[0] = spicmd[1] = td;
			n = (td == FLAG) ? 2 : 1;
			send_bridge(spicmd, n);					/* Send data exchange command */
			break;

		case TY_AVRSP :
			do {
				a = ((td & 0x80) ? B_ENA | B_MOSI : B_ENA);	/* MOSI(D1) = data, SCK(D0) = "L" */
				_outp(LPT_DAT, a);
				iodly();									/* delay */
				_outp(LPT_DAT, a | B_SCK);					/* SCK(D0) = "H" */
				iodly();									/* delay */
				td <<= 1;
			} while (--n);
			_outp(LPT_DAT, a);								/* SCK(D0) = "L" */
			break;

		case TY_STK200 :
			do {
				a = ((td & 0x80) ? BS_MOSI : 0);	/* MOSI(D5) = data, SCK(D4) = "L" */
				_outp(LPT_DAT, a);
				iodly();							/* delay */
				_outp(LPT_DAT, a | BS_SCK);			/* SCK(D4) = "H" */
				iodly();							/* delay */
				td <<= 1;
			} while (--n);
			_outp(LPT_DAT, a);						/* SCK(D4) = "L" */
			break;

		case TY_XILINX :
			do {
				a = ((td & 0x80) ? BX_DIS2 | BX_MOSI : BX_DIS2); /* MOSI(D0) = data, SCK(D1) = "L" */
				_outp(LPT_DAT, a);
				iodly();									/* delay */
				_outp(LPT_DAT, a | BX_SCK);					/* SCK(D1) = "H" */
				iodly();									/* delay */
				td <<= 1;
			} while (--n);
			_outp(LPT_DAT, a);								/* SCK(D1) = "L" */
			break;

		case TY_LATTICE :
			do {
				a = ((td & 0x80) ? BL_MOSI : 0);	/* MOSI(D0) = data, SCK(D1) = "L" */
				_outp(LPT_DAT, a);
				iodly();							/* delay */
				_outp(LPT_DAT, a | BL_SCK);			/* SCK(D1) = "H" */
				iodly();							/* delay */
				td <<= 1;
			} while (--n);
			_outp(LPT_DAT, a);						/* SCK(D1) = "L" */
			break;

		case TY_ALTERA :
			do {
				a = ((td & 0x80) ? BA_MOSI : 0);	/* MOSI(D6) = data, SCK(D0) = "L" */
				_outp(LPT_DAT, a);
				iodly();							/* delay */
				_outp(LPT_DAT, a | BA_SCK);			/* SCK(D0) = "H" */
				iodly();							/* delay */
				td <<= 1;
			} while (--n);
			_outp(LPT_DAT, a);						/* SCK(D0) = "L" */
			break;
	}

}




/* Send zero and Receive a byte from device */

BYTE spi_rcvr (BYTE mode)
{
	BYTE rd = 0;
	BYTE spicmd[2] = { FLAG, SPI_RCVZ };
	int n = 8;


	switch (PortType) {
		case TY_COMM :
			_outp(COM_LCR, 0);						/* MOSI(TD) = "L" */
			iodly();
			do {
				rd <<= 1;							/* Read MISO(DR) */
				if(_inp(COM_MSR) & C_MISO) rd++;
				_outp(COM_MCR, C_SCK);				/* SCK(RS) = "H" */
				iodly();							/* delay */
				_outp(COM_MCR, 0);					/* SCK(RS) = "L" */
				iodly();							/* delay */
			} while (--n);
			if(mode == RM_ASYNC)
				fifo_put(rd);
			break;

		case TY_VCOM :
			if(!sig_break) {
				EscapeCommFunction(hComm, CLRBREAK);/* MOSI(TD) = "L" */
				sig_break = FALSE;
			}
			iodly();
			do {
				rd <<= 1;							/* Read MISO(DR) */
				GetCommModemStatus(hComm, &ModemStat);
				if(ModemStat & MS_DSR_ON) rd++;
				EscapeCommFunction(hComm, SETRTS);	/* SCK(RS) = "H" */
				iodly();							/* delay */
				EscapeCommFunction(hComm, CLRRTS);	/* SCK(RS) = "L" */
				iodly();							/* delay */
			} while (--n);
			if(mode == RM_ASYNC)
				fifo_put(rd);
			break;

		case TY_BRIDGE :
			send_bridge(spicmd, 2);					/* Send SPI_RCVZ command */
			if(mode == RM_SYNC) {
				if(!read_bridge(&rd, 1))			/* Get a read data from pipeline */
					rd = 0xFF;
			}
			break;

		case TY_AVRSP :
			_outp(LPT_DAT, B_ENA);					/* MOSI(D0) = "L" */
			iodly();
			do {
				rd <<= 1;							/* Read MISO(BUSY) */
				if((_inp(LPT_STA) & S_BUSY) == 0) rd++;
				_outp(LPT_DAT, B_ENA | B_SCK);		/* SCK(D1) = "H" */
				iodly();							/* delay */
				_outp(LPT_DAT, B_ENA);				/* SCK(D1) = "L" */
				iodly();							/* delay */
			} while (--n);
			if(mode == RM_ASYNC)
				fifo_put(rd);
			break;

		case TY_STK200 :
			_outp(LPT_DAT, 0);						/* MOSI(D5) = "L" */
			iodly();
			do {
				rd <<= 1;							/* Read MISO(ACK) */
				if(_inp(LPT_STA) & S_ACK) rd++;
				_outp(LPT_DAT, BS_SCK);				/* SCK(D4) = "H" */
				iodly();							/* delay */
				_outp(LPT_DAT, 0);					/* SCK(D4) = "L" */
				iodly();							/* delay */
			} while (--n);
			if(mode == RM_ASYNC)
				fifo_put(rd);
			break;

		case TY_XILINX :
			_outp(LPT_DAT, BX_DIS2);				/* MOSI(D0) = "L" */
			iodly();
			do {
				rd <<= 1;							/* Read MISO(SEL) */
				if(_inp(LPT_STA) & S_SEL) rd++;
				_outp(LPT_DAT, BX_DIS2 | BX_SCK);	/* SCK(D1) = "H" */
				iodly();							/* delay */
				_outp(LPT_DAT, BX_DIS2);			/* SCK(D1) = "L" */
				iodly();							/* delay */
			} while (--n);
			if(mode == RM_ASYNC)
				fifo_put(rd);
			break;

		case TY_LATTICE :
			_outp(LPT_DAT, 0);						/* MOSI(D0) = "L" */
			iodly();
			do {
				rd <<= 1;							/* Read MISO(ACK) */
				if(_inp(LPT_STA) & S_ACK) rd++;
				_outp(LPT_DAT, BL_SCK);				/* SCK(D1) = "H" */
				iodly();							/* delay */
				_outp(LPT_DAT, 0);					/* SCK(D1) = "L" */
				iodly();							/* delay */
			} while (--n);
			if(mode == RM_ASYNC)
				fifo_put(rd);
			break;

		case TY_ALTERA :
			_outp(LPT_DAT, 0);						/* MOSI(D6) = "L" */
			iodly();
			do {
				rd <<= 1;							/* Read MISO(BUSY) */
				if((_inp(LPT_STA) & S_BUSY) == 0) rd++;
				_outp(LPT_DAT, BA_SCK);				/* SCK(D0) = "H" */
				iodly();							/* delay */
				_outp(LPT_DAT, 0);					/* SCK(D0) = "L" */
				iodly();							/* delay */
			} while (--n);
			if(mode == RM_ASYNC)
				fifo_put(rd);
			break;
	}

	return(rd);
}




/* Get delayed receiving data */

void spi_delayedget (BYTE *ptr, DWORD cnt)
{
	if(PortType == TY_BRIDGE) {
		if(!read_bridge(ptr, cnt))			/* Get data from pipeline */
			memset(ptr, 0xFF, cnt);
	}
	else {
		while (cnt--)
			*ptr++ = fifo_get();		/* Get data form receiving fifo */
	}
}
