
#ifndef _WINDEF_
typedef unsigned char   BYTE;
typedef unsigned short	WORD;
typedef unsigned long	DWORD;
#endif /* _WINDEF_ */



#define MESS(str)	fputs(str, stderr)
#define FUSEFILE "fuse.txt"
#define INIFILE "avrsp.ini"


/* Buffer size for flash/eeprom */

#define	MAX_FLASH	(256*1024)	/* Max Flash size */
#define	MAX_EEPROM	(  4*1024)	/* Max EEPROM size */
#define	PIPE_WINDOW	32			/* Pipe window for SPI bridge */


/* Device property structure */

enum _devid {	/* device identifier */
	N0000, L0000,	/* Unknown, Locked */
	S1200, S2313, S4414, S8515, S2333, S4433, S4434, S8535, S2323, S2343,
	T12, T13, T22, T25, T45, T85, T24, T44, T84, T26, T2313, T15,
	M161, M162, M8515, M8535, M163, M323, M48, M88, M168, M8, M16, M32, M325, M3250, M165, M169, M603, M645, M6450, M103, M64, M128, M640, M1280, M1281, M2560, M2561,
	CAN128, PWM2
};


typedef struct _DEVPROP {
	char	*Name;			/* Device name */
	char	ID;				/* Device ID */
	BYTE	Sign[3];		/* Device signature bytes */
	DWORD	FlashSize;		/* Flash memory size in unit of byte */
	WORD	FlashPage;		/* Flash page size (0 is byte-by-byte) */
	DWORD	EepromSize;		/* EEPROM size in unit of byte */
	WORD	FlashWait;		/* Wait time for flash write */
	WORD	EepromWait;		/* Wait time for EEPROM write */
	BYTE	PollData;		/* Polling data value */
	BYTE	LockData;		/* Default lock byte (program LB1 and LB2) */
	char	FuseType;		/* Device specific fuse type */
	char	Cals;			/* Number of calibration bytes */
	BYTE	FuseMask[3];	/* Valid fuse bit mask [low, high, ext] */
} DEVPROP;




/* Device programming commands */

#define C_EN_PRG1	0xAC
#define C_EN_PRG2	0x53
#define C_ERASE1	0xAC
#define C_ERASE2	0x80
#define C_LD_ADRX	0x4D
#define C_WR_PRGL	0x40
#define C_WR_PRGH	0x48
#define C_WR_PAGE	0x4C
#define C_RD_PRGL	0x20
#define C_RD_PRGH	0x28
#define C_WR_EEP	0xC0
#define C_RD_EEP	0xA0
#define C_WR_FLB	0xAC
#define C_WR_FLBL	0xA0
#define C_WR_FLBH	0xA8
#define C_WR_FLBX	0xA4
#define C_WR_FLBK	0xE0
#define C_RD_FLB1	0x58
#define C_RD_FB1	0x50
#define C_RD_SIG	0x30
#define C_RD_CAL	0x38



/* Program return codes */

#define	RC_FAIL		1
#define	RC_FILE		2
#define	RC_INIT		3
#define RC_DEV		4
#define	RC_SYNTAX	5



/* Byte read/write identifire */

#define FLASH		0
#define FLASH_NS	1
#define EEPROM		2
#define SIGNATURE	3


/* Fuse write identifire */

#define F_LOW		0
#define	F_HIGH		1
#define	F_EXTEND	2
#define	F_LOCK		3


/* spi_rcvr() argument */

#define	RM_SYNC		0
#define	RM_ASYNC	1


/* Physical port properties */

typedef struct {
	WORD	PortClass;		/* Port class */
	WORD	PortNum;		/* Port number (1..)  */
	DWORD	Baud;			/* Baud rate (for SPI bridge) */
	WORD	Delay;			/* I/O delay */
	char	*Info1, *Info2;	/* Information strings, returned by open_ifport() */
} PORTPROP;

enum _portclass {	/* Port class */
	TY_LPT, TY_COMM, TY_VCOM, TY_BRIDGE, TY_AVRSP, TY_STK200, TY_XILINX, TY_LATTICE, TY_ALTERA
};




/* Prototypes for platform depending module */

int open_ifport (PORTPROP *);
void close_ifport ();
void spi_reset ();
void spi_clk ();
void spi_xmit (BYTE);
BYTE spi_rcvr (BYTE);
void spi_delayedget (BYTE *, DWORD);
void delay_ms (WORD);
int spi_flush ();
FILE *open_cfgfile(char *);

