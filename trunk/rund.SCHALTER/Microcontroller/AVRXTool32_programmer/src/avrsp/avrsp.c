/*---------------------------------------------------------------------------*/
/* AVRSP - AVR Serial Programming Controller                                 */
/*                                                                           */
/* R0.37 (C)ChaN, 2005                                                       */
/*---------------------------------------------------------------------------*/
/* R0.28  Apr 30, '04  Migration from MS-DOS based AVRSS/AVRXS R0.27         */
/* R0.28b May  1, '04  Fixed some value in the device property table         */
/* R0.28c May  3, '04  Fixed unable to communicate via ByteBlasterMV         */
/* R0.28d May 22, '04  WinNT/2k/XP are supported (GIVEIO is required)        */
/* R0.29  Jun 25, '04  AT90CAN128 is supported                               */
/* R0.30  Sep  1, '04  ATmega165 is supported and some updates...            */
/* R0.31  Nov 11, '04  ATmega325/3250/645/6450 are supported                 */
/* R0.32  Feb 11, '05  90PWM2/3                                              */
/* R0.33  Feb 15, '05  tiny25/45/85                                          */
/* R0.34  Mar 12, '05  mega640/1280/2560/641/1281                            */
/* R0.35  Apr 26, '05  Serial - SPI bridge is supported                      */
/* R0.36  May 20, '05  Read performance on SPI bridge was improved           */
/* R0.37  Aug 10, '05  tiny24/44/84, Fixed some values for tiny25/45/85      */
/*---------------------------------------------------------------------------*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include "avrsp.h"



/*-----------------------------------------------------------------------
  Device properties
-----------------------------------------------------------------------*/

const DEVPROP DevLst[] =	/* Device property list */
{
	/* Name,         ID,    Signature,              FS,  PS,   ES, FW, EW,   PV,   LB, FT, Cals, FuseMasks */
	{ "90S1200",     S1200, {0x1E, 0x90, 0x01},   1024,   0,   64, 11, 11, 0xFF, 0xF9, 0 },
	{ "90S2313",     S2313, {0x1E, 0x91, 0x01},   2048,   0,  128, 11, 11, 0x7F, 0xF9, 0 },
	{ "90S4414",     S4414, {0x1E, 0x92, 0x01},   4096,   0,  256, 11, 11, 0x7F, 0xF9, 0 },
	{ "90S8515",     S8515, {0x1E, 0x93, 0x01},   8192,   0,  512, 11, 11, 0x7F, 0xF9, 0 },
	{ "90S2333",     S2333, {0x1E, 0x91, 0x05},   2048,   0,  128, 11, 11, 0xFF, 0xF9, 2, 0, {0x1F} },
	{ "90S4433",     S4433, {0x1E, 0x92, 0x03},   4096,   0,  256, 11, 11, 0xFF, 0xF9, 2, 0, {0x1F} },
	{ "90S4434",     S4434, {0x1E, 0x92, 0x02},   4096,   0,  256, 11, 11, 0xFF, 0xF9, 1, 0, {0x01} },
	{ "90S8535",     S8535, {0x1E, 0x93, 0x03},   8192,   0,  512, 11, 11, 0xFF, 0xF9, 1, 0, {0x01} },
	{ "90S2323",     S2323, {0x1E, 0x91, 0x02},   2048,   0,  128, 11, 11, 0xFF, 0xF9, 1, 0, {0x01} },
	{ "90S2343",     S2343, {0x1E, 0x91, 0x03},   2048,   0,  128, 11, 11, 0xFF, 0xF9, 1, 0, {0x01} },
	{ "tiny12",      T12,   {0x1E, 0x90, 0x05},   1024,   0,   64,  5,  8, 0xFF, 0xF9, 3, 1, {0xFF} },
	{ "tiny13",      T13,   {0x1E, 0x90, 0x07},   1024,  32,   64,  6,  5, 0xFF, 0xFC, 5, 1, {0x7F, 0x1F} },
	{ "tiny25",      T25,   {0x1E, 0x91, 0x08},   2048,  32,  128,  6,  5, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x01} },
	{ "tiny45",      T45,   {0x1E, 0x92, 0x06},   4096,  64,  256,  6,  5, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x01} },
	{ "tiny85",      T85,   {0x1E, 0x93, 0x0B},   8192,  64,  512,  6,  5, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x01} },
	{ "tiny24",      T24,   {0x1E, 0x91, 0x0B},   2048,  32,  128,  6,  5, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x01} },
	{ "tiny44",      T44,   {0x1E, 0x92, 0x07},   4096,  64,  256,  6,  5, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x01} },
	{ "tiny84",      T84,   {0x1E, 0x93, 0x0C},   8192,  64,  512,  6,  5, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x01} },
	{ "tiny22",      T22,   {0x1E, 0x91, 0x06},   2048,   0,  128, 11, 11, 0xFF, 0xF9, 1, 0, {0x01} },
	{ "tiny26",      T26,   {0x1E, 0x91, 0x09},   2048,  32,  128,  6, 10, 0xFF, 0xFC, 5, 4, {0xFF, 0x17} },
	{ "tiny2313",    T2313, {0x1E, 0x91, 0x0A},   2048,  32,  128,  6,  5, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x01} },
	{ "tiny15",      T15,   {0x1E, 0x90, 0x06},   1024,   0,   64,  6, 11, 0xFF, 0xF9, 3, 1, {0xD3} },
	{ "mega161",     M161,  {0x1E, 0x94, 0x01},  16384, 128,  512, 20,  5, 0xFF, 0xFC, 4, 0, {0x5F} },
	{ "mega162",     M162,  {0x1E, 0x94, 0x04},  16384, 128,  512,  6, 11, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x1E} },
	{ "mega8515",    M8515, {0x1E, 0x93, 0x06},   8192,  64,  512,  6, 11, 0xFF, 0xFC, 5, 4, {0xFF, 0xDF} },
	{ "mega8535",    M8535, {0x1E, 0x93, 0x08},   8192,  64,  512,  6, 11, 0xFF, 0xFC, 5, 4, {0xFF, 0xDF} },
	{ "mega163",     M163,  {0x1E, 0x94, 0x02},  16384, 128,  512, 18,  5, 0xFF, 0xFC, 5, 1, {0xCF, 0x07} },
	{ "mega323",     M323,  {0x1E, 0x95, 0x01},  32768, 128, 1024, 18,  5, 0xFF, 0xFC, 5, 1, {0xCF, 0xCF} },
	{ "mega48",      M48,   {0x1E, 0x92, 0x05},   4096,  64,  256,  6,  5, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x01} },
	{ "mega88",      M88,   {0x1E, 0x93, 0x0A},   8192,  64,  512,  6,  5, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x07} },
	{ "mega168",     M168,  {0x1E, 0x94, 0x06},  16384, 128,  512,  6,  5, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x07} },
	{ "mega8",       M8,    {0x1E, 0x93, 0x07},   8192,  64,  512,  6, 11, 0xFF, 0xFC, 5, 4, {0xFF, 0xDF} },
	{ "mega16",      M16,   {0x1E, 0x94, 0x03},  16384, 128,  512,  6, 11, 0xFF, 0xFC, 5, 4, {0xFF, 0xDF} },
	{ "mega32",      M32,   {0x1E, 0x95, 0x02},  32768, 128, 1024,  6, 11, 0xFF, 0xFC, 5, 4, {0xFF, 0xDF} },
	{ "mega165",     M165,  {0x1E, 0x94, 0x07},  16384, 128,  512,  6, 11, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x0E} },
	{ "mega169",     M169,  {0x1E, 0x94, 0x05},  16384, 128,  512,  6, 11, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x0F} },
	{ "mega325/9",   M325,  {0x1E, 0x95, 0x03},  32768, 128, 1024,  6, 11, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x07} },
	{ "mega3250/90", M3250, {0x1E, 0x95, 0x04},  32768, 128, 1024,  6, 11, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x07} },
	{ "mega645/9",   M645,  {0x1E, 0x96, 0x03},  65536, 256, 2048,  6, 11, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x07} },
	{ "mega6450/90", M6450, {0x1E, 0x96, 0x04},  65536, 256, 2048,  6, 11, 0xFF, 0xFC, 6, 1, {0xFF, 0xDF, 0x07} },
	{ "mega603",     M603,  {0x1E, 0x96, 0x01},  65536, 256, 2048, 60, 11, 0xFF, 0xF9, 2, 0, {0x0B} },
	{ "mega103",     M103,  {0x1E, 0x97, 0x01}, 131072, 256, 4096, 60, 11, 0xFF, 0xF9, 2, 0, {0x0B} },
	{ "mega64",      M64,   {0x1E, 0x96, 0x02},  65536, 256, 2048,  6, 11, 0xFF, 0xFC, 6, 4, {0xFF, 0xDF, 0x03} },
	{ "mega128",     M128,  {0x1E, 0x97, 0x02}, 131072, 256, 4096,  6, 11, 0xFF, 0xFC, 6, 4, {0xFF, 0xDF, 0x03} },
	{ "mega640",     M640,  {0x1E, 0x96, 0x07},  65536, 256, 4096,  6, 11, 0xFF, 0xFC, 6, 4, {0xFF, 0xDF, 0x03} },
	{ "mega1280",    M1280, {0x1E, 0x97, 0x03}, 131072, 256, 4096,  6, 11, 0xFF, 0xFC, 6, 4, {0xFF, 0xDF, 0x03} },
	{ "mega1281",    M1281, {0x1E, 0x97, 0x04}, 131072, 256, 4096,  6, 11, 0xFF, 0xFC, 6, 4, {0xFF, 0xDF, 0x03} },
	{ "mega2560",    M2560, {0x1E, 0x98, 0x01}, 262144, 256, 4096,  6, 11, 0xFF, 0xFC, 6, 4, {0xFF, 0xDF, 0x03} },
	{ "mega2561",    M2561, {0x1E, 0x98, 0x02}, 262144, 256, 4096,  6, 11, 0xFF, 0xFC, 6, 4, {0xFF, 0xDF, 0x03} },
	{ "90CAN128",    CAN128,{0x1E, 0x97, 0x81}, 131072, 256, 4096,  6, 11, 0xFF, 0xFC, 6, 4, {0xFF, 0xDF, 0x0F} },
	{ "90PWM2/3",    PWM2,  {0x1E, 0x93, 0x81},   8192,  64,  512,  6, 11, 0xFF, 0xFC, 6, 4, {0xFF, 0xDF, 0xF7} },
	{ "Locked",      L0000, {0x00, 0x01, 0x02} },	/* Locked Device */
	{ NULL,          N0000 }						/* Unknown */
};

const DEVPROP *Device = NULL;		/* Pointer to the current device property */



/*-----------------------------------------------------------------------
  Global variables (initialized by load_commands())
-----------------------------------------------------------------------*/

BYTE CodeBuff[MAX_FLASH];		/* Program code R/W buffer */
BYTE DataBuff[MAX_EEPROM];		/* EEPROM data R/W buffer */

BYTE CalBuff[4];				/* Calibration bytes read buffer */
BYTE FuseBuff[3];				/* Fuse bytes read buffer */
BYTE SignBuff[3];				/* Device signature read buffer */

/*---------- Command Parameters ------------*/

char Command[2];				/* -r -e -z Read/Erase/Test command (1st,2nd char) */

struct {						/* Code/Data write command (hex file) */
	DWORD CodeSize;				/* Loaded program code size (.hex) */
	DWORD DataSize;				/* Loaded EEPROM data size (.eep) */
	char Verify;				/* -v 1:Verify only (skip programming), 2:Skip verify (programmig only) */
	char CopyCal;				/* -c Copy calibration bytes into end of flash */
} CmdWrite;

struct {						/* Fuse write command */
	union {						/* which fuse? */
		char Flags;
		struct {
			int Low		: 1;	/* -fl */
			int High	: 1;	/* -fh */
			int Extend	: 1;	/* -fx */
			int Lock	: 1;	/* -l */
		} Flag;
	} Cmd;
	BYTE Data[4];				/* fuse bytes to be written {Low,High,Extend,Lock} */
} CmdFuse;

char Pause;						/* -w Pause before exiting program */

char ForcedName[20];			/* -t Forced device type (compared to Device->Name)*/


/*---------- Hardware Control ------------*/

PORTPROP CtrlPort = {	TY_COMM, 1,	115200, /* -p .PortClass .PortNum .Baud */
						3, 					/* -d .Delay (SPI control delay) */
						NULL, NULL };




/*-----------------------------------------------------------------------
  Messages
-----------------------------------------------------------------------*/



void output_usage ()
{
	int n;
	static const char *const MesUsage[] = {
		"AVRSP - AVR Serial Programming tool R0.37 (C)ChaN,2005  http://elm-chan.org/\n\n",
		"Write code and/or data  : <hex file> [<hex file>] ...\n",
		"Verify code and/or data : -v <hex file> [<hex file>] ...\n",
		"Read code, data or fuse : -r{p|e|f}\n",
		"Write fuse byte         : -f{l|h|x}<bin>\n",
		"Lock device             : -l[<bin>]\n",
		"Copy calibration bytes  : -c\n",
		"Erase device            : -e\n",
		"Control port [-pc1]     : -p{c|l|v|b}<n>[:<baud>]\n",
		"SPI control delay [-d3] : -d<n>\n\n",
		"SUPPORTED DEVICE:\n",
		"AT90S 1200,2313,2323,2333,2343,4414,4433,4434,8515,8535\n",
		"ATtiny 12,13,15,22,24,25,26,44,45,84,85,2313\n",
		"ATmega 8,16,32,48,64,88,103,128,161,162,163,165,168,169,323,325/9,3250/90,603,640,645/9,1280,1281,2560,2561,6450/90,8515,8535\n",
		"AT90 CAN128, PWM 2,3\n\n",
		"SUPPORTED ADAPTER:\n",
		"AVRSP adapter (COM/LPT), SPI Bridge (COM), STK200 ISP dongle (LPT)\n",
		"Xilinx JTAG (LPT), Lattice isp (LPT), Altera ByteBlasterMV (LPT)\n",
		NULL
	};


	for(n = 0; MesUsage[n] != NULL; n++)
		MESS(MesUsage[n]);
}




/* Output the device information */

void output_deviceinfo ()
{
	printf("Device Signature  = %02X-%02x-%02X\n",
			Device->Sign[0], Device->Sign[1], Device->Sign[2]);
	printf("Flash Memory Size = %d bytes\n", Device->FlashSize);
	if(Device->FlashPage)
		printf("Flash Memory Page = %d bytes x %d pages\n",
				Device->FlashPage, Device->FlashSize / Device->FlashPage);
	printf("EEPROM Size       = %d bytes\n", Device->EepromSize);
}




/* Output a fuse byte and its description if present */

void put_fuseval (BYTE val, BYTE mask, const char *head, FILE *fp)
{
	int	n;
	char Line[100];


	fputs(head, stdout);
	for(n = 1; n <= 8; n++) {
		putchar((mask & 0x80) ? ((val & 0x80) ? '1' : '0') : '-');
		val <<= 1; mask <<= 1;
	}
	putchar('\n');

	if(fp == NULL) return;
	while (1) {	/* seek to the fuse header */
		if(fgets(Line, sizeof(Line), fp) == NULL) return;
		if(strstr(Line, head) == Line) break;
	}
	do {		/* output fuse bit descriptions */
		if(fgets(Line, sizeof(Line), fp) == NULL) return;
		fputs(Line, stdout);
	} while (Line[0] != '\n');

}




/* Output fuse bytes and calibration byte */

void output_fuse ()
{
	int n;
	FILE *fp;
	char Line[100], *cp;


	if(Device->FuseType == 0) {
		MESS("Fuse bits are not accessible.\n");
		return;
	}

	/* Open FUSE.TXT and seek to the device */
	fp = open_cfgfile(FUSEFILE);
	if(fp == NULL) {
		MESS("WARNING: Missing fuse description file.\n");
	} else {
		while (1) {
			if(fgets(Line, sizeof(Line), fp) == NULL) break;
			if((Line[0] != 'D') || ((cp = strstr(Line, Device->Name)) == NULL)) continue;
			if(strlen(cp) == strlen(Device->Name) + 1) break;
		}
	}

	MESS("\n");
	put_fuseval(FuseBuff[0], Device->FuseMask[0], "Low: ", fp);

	if(Device->FuseType >= 5)
		put_fuseval(FuseBuff[1], Device->FuseMask[1], "High:", fp);

	if(Device->FuseType >= 6)
		put_fuseval(FuseBuff[2], Device->FuseMask[2], "Ext: ", fp);

	/* Output calibration values */
	if(Device->Cals) {
		fputs("Cal:", stdout);
		for(n = 0; n < Device->Cals; n++)
			printf(" %d", CalBuff[n]);
		putchar('\n');
	}

	if(fp != NULL) fclose(fp);	/* Close FUSE.TXT */
}




/*-----------------------------------------------------------------------
  Hex format manupilations
-----------------------------------------------------------------------*/


/* Pick a hexdecimal value from hex record */

DWORD get_valh (char **lp,	/* pointer to line read pointer */
			   int count, 	/* number of digits to get (2,4,6,8) */
			   BYTE *sum)	/* byte check sum */
{
	DWORD val = 0;
	BYTE n;


	do {
		n = *(*lp)++;
		if((n -= '0') >= 10) {
			if((n -= 7) < 10) return(0xFFFFFFFF);
			if(n > 0xF) return(0xFFFFFFFF);
		}
		val = (val << 4) + n;
		if(count & 1) *sum += (BYTE)val;
	} while(--count);
	return(val);
}




/* Load Intel/Motorola hex file into data buffer */ 

long input_hexfile (FILE *fp,			/* input stream */
				   BYTE *buffer,		/* data input buffer */
				   DWORD buffsize,		/* size of data buffer */
				   DWORD *datasize)		/* effective data size in the input buffer */
{
	char line[600];			/* line input buffer */
	char *lp;				/* line read pointer */
	long lnum = 0;			/* input line number */
	WORD seg = 0, hadr = 0;	/* address expantion values for intel hex */
	DWORD addr, count, n;
	BYTE sum;


	while(fgets(line, sizeof(line), fp) != NULL) {
		lnum++;
		lp = &line[1]; sum = 0;

		if(line[0] == ':') {	/* Intel Hex format */
			if((count = get_valh(&lp, 2, &sum)) > 0xFF) return(lnum);	/* byte count */
			if((addr = get_valh(&lp, 4, &sum)) > 0xFFFF) return(lnum);	/* offset */

			switch (get_valh(&lp, 2, &sum)) {	/* block type? */
				case 0x00 :	/* data block */
					addr += (seg << 4) + (hadr << 16);
					while(count--) {
						if((n = get_valh(&lp, 2, &sum)) > 0xFF) return(lnum);
						if(addr >= buffsize) continue;	/* clip by buffer size */
						buffer[addr++] = (BYTE)n;		/* store the data */
						if(addr > *datasize)			/* update data size information */
							*datasize = addr;
					}
					break;

				case 0x01 :	/* end block */
					if(count) return(lnum);
					break;

				case 0x02 :	/* segment block */
					if(count != 2) return(lnum);
					if((seg = (WORD)get_valh(&lp, 4, &sum)) == 0xFFFF) return(lnum);
					break;

				case 0x04 :	/* high address block */
					if(count != 2) return(lnum);
					if((hadr = (WORD)get_valh(&lp, 4, &sum)) == 0xFFFF) return(lnum);
					break;

				default:	/* invalid block */
					return(lnum);
			} /* switch */
			if(get_valh(&lp, 2, &sum) > 0xFF) return(lnum);	/* get check sum */
			if(sum) return(lnum);							/* test check sum */
		} /* if */

		if(line[0] == 'S') {	/* Motorola S format */
			if((*lp >= '1')&&(*lp <= '3')) {

				switch (*lp++) {	/* record type? (S1/S2/S3) */
					case '1' :
						if((count = get_valh(&lp, 2, &sum) - 3) > 0xFF) return(lnum);
						if((addr = get_valh(&lp, 4, &sum)) == 0xFFFFFFFF) return(lnum);
						break;
					case '2' :
						if((count = get_valh(&lp, 2, &sum) - 4) > 0xFF) return(lnum);
						if((addr = get_valh(&lp, 6, &sum)) == 0xFFFFFFFF) return(lnum);
						break;
					default :
						if((count = get_valh(&lp, 2, &sum) - 5) > 0xFF) return(lnum);
						if((addr = get_valh(&lp, 8, &sum)) == 0xFFFFFFFF) return(lnum);
				}
				while(count--) {
					if((n = get_valh(&lp, 2, &sum)) > 0xFF) return(lnum);
					if(addr >= buffsize) continue;	/* clip by buffer size */
					buffer[addr++] = (BYTE)n;		/* store the data */
					if(addr > *datasize)			/* update data size information */
						*datasize = addr;
				}
				if(get_valh(&lp, 2, &sum) > 0xFF) return(lnum);	/* get check sum */
				if(sum != 0xFF) return(lnum);					/* test check sum */
			} /* switch */
		} /* if */

	} /* while */

	return( feof(fp) ? 0 : -1 );
}




/* Put an Intel Hex data block */

void put_hexline (FILE *fp,				/* output stream */
				  const BYTE *buffer,	/* pointer to data buffer */
				  WORD ofs,				/* block offset address */
				  BYTE count,			/* data byte count */
				  BYTE type)			/* block type */
{
	BYTE sum;

	/* Byte count, Offset address and Record type */
	fprintf(fp, ":%02X%04X%02X", count, ofs, type);
	sum = count + (ofs >> 8) + ofs + type;

	/* Data bytes */
	while(count--) {
		fprintf(fp, "%02X", *buffer);
		sum += *buffer++;
	}

	/* Check sum */
	fprintf(fp, "%02X\n", (BYTE)-sum);
}




/* Output data in Intel Hex format */

void output_hexfile (FILE *fp,			/* output stream */
					const BYTE *buffer,	/* pointer to data buffer */
					DWORD datasize,		/* number of bytes to be output */
					BYTE blocksize)		/* HEX block size (1,2,4,..,128) */
{
	WORD seg = 0, ofs = 0;
	BYTE segbuff[2], d, n;
	DWORD bc = datasize;


	while(bc) {
		if((ofs == 0) && (datasize > 0x10000)) {
			segbuff[0] = (BYTE)(seg >> 8); segbuff[1] = (BYTE)seg;
			put_hexline(fp, segbuff, 0, 2, 2);
			seg += 0x1000;
		}
		if(bc >= blocksize) {	/* full data block */
			for(d = 0xFF, n = 0; n < blocksize; n++) d &= *(buffer+n);
			if(d != 0xFF) put_hexline(fp, buffer, ofs, blocksize, 0);
			buffer += blocksize;
			bc -= blocksize;
			ofs += blocksize;
		} else {				/* fractional data block */
			for(d = 0xFF, n = 0; n < bc; n++) d &= *(buffer+n);
			if(d != 0xFF) put_hexline(fp, buffer, ofs, (BYTE)bc, 0);
			bc = 0;
		}
	}

	put_hexline(fp, NULL, 0, 0, 1);	/* End block */
}



/*-----------------------------------------------------------------------
  Command line analysis
-----------------------------------------------------------------------*/


int load_commands (int argc, char **argv)
{
	char *cp, c, *cmdlst[20], cmdbuff[256];
	int cmd;
	FILE *fp;
	DWORD ln;


	/* Clear data buffers */
	memset(CodeBuff, 0xFF, sizeof(CodeBuff));
	memset(DataBuff, 0xFF, sizeof(DataBuff));

	cmd = 0; cp = cmdbuff;

	/* Import ini file as command line parameters */
	fp = open_cfgfile(INIFILE);
	if(fp != NULL) {
		while(fgets(cp, cmdbuff + sizeof(cmdbuff) - cp, fp) != NULL) {
			if(cmd >= (sizeof(cmdlst) / sizeof(cmdlst[0]) - 1)) break;
			if(*cp <= ' ') break;
			cmdlst[cmd++] = cp; cp += strlen(cp) + 1;
		}
		fclose(fp);
	}

	/* Get command line parameters */
	while(--argc && (cmd < (sizeof(cmdlst) / sizeof(cmdlst[0]) - 1)))
		cmdlst[cmd++] = *++argv;
	cmdlst[cmd] = NULL;

	/* Analyze command line parameters... */
	for(cmd = 0; cmdlst[cmd] != NULL; cmd++) {
		cp = cmdlst[cmd];

		if(*cp == '-') {	/* Command switches... */
			cp++;
			switch (tolower(*cp++)) {
				case 'v' :	/* -v, -v- */
					if(*cp == '-') {
						CmdWrite.Verify = 2; cp++;
					} else {
						CmdWrite.Verify = 1;
					}
					break;

				case 'c' :	/* -c */
					CmdWrite.CopyCal = 1; break;

				case 'e' :	/* -e */
					Command[0] = 'e'; break;

				case 'z' :	/* -z */
					Command[0] = 'z'; break;

				case 'r' :	/* -r{p|e|f} */
					Command[0] = 'r';
					if(*cp) Command[1] = tolower(*cp++);
					break;

				case 'f' :	/* -f{l|h|x}<bin> */
					c = tolower(*cp++);
					if(*cp <= ' ') return(RC_SYNTAX);
					ln = strtoul(cp, &cp, 2);
					switch (c) {
						case 'l' :
							CmdFuse.Cmd.Flag.Low = 1;
							CmdFuse.Data[0] = (BYTE)ln;
							break;
						case 'h' :
							CmdFuse.Cmd.Flag.High = 1;
							CmdFuse.Data[1] = (BYTE)ln;
							break;
						case 'x' :
							CmdFuse.Cmd.Flag.Extend = 1;
							CmdFuse.Data[2] = (BYTE)ln;
							break;
						default :
							return(RC_SYNTAX);
					}
					break;

				case 'l' :	/* -l[<bin>] */
					CmdFuse.Cmd.Flag.Lock = 1;
					CmdFuse.Data[3] = (BYTE)strtoul(cp, &cp, 2);
					break;

				case 'p' :	/* -p{c|l|v|b}<num> */
					switch (tolower(*cp++)) {
						case 'c' :
							CtrlPort.PortClass = TY_COMM;
							break;
						case 'l' :
							CtrlPort.PortClass = TY_LPT;
							break;
						case 'v' :
							CtrlPort.PortClass = TY_VCOM;
							break;
						case 'b' :
							CtrlPort.PortClass = TY_BRIDGE;
							break;
						default :
							return(RC_SYNTAX);
					}
					CtrlPort.PortNum = (WORD)strtoul(cp, &cp, 10);
					if(*cp == ':')
						CtrlPort.Baud = strtoul(cp+1, &cp, 10);
					break;

				case 'd' :	/* -d<num> */
					CtrlPort.Delay = (WORD)strtoul(cp, &cp, 10);
					break;

				case 'w' :	/* -w (pause before exit) */
					Pause = 1;
					break;

				case 't' :	/* -t<device> (force device type) */
					for(ln = 0; ln < sizeof(ForcedName); ln++, cp++) {
						if((ForcedName[ln] = *cp) == '\0') break;
					}
					break;

				default :	/* invalid command */
					return(RC_SYNTAX);
			} /* switch */
			if(*cp >= ' ') return(RC_SYNTAX);	/* option trails garbage */
		} /* if */

		else {	/* HEX Files (Write command) */
			if((fp = fopen(cp, "rt")) == NULL) {
				fprintf(stderr, "%s : Unable to open.\n", cp);
				return(RC_FILE);
			}
			/* .eep files are read as EEPROM data, others are read as program code */
			if((strstr(cp, ".EEP") == NULL) && (strstr(cp, ".eep") == NULL)) {
				ln = input_hexfile(fp, CodeBuff, sizeof(CodeBuff), &CmdWrite.CodeSize);
			} else {
				ln = input_hexfile(fp, DataBuff, sizeof(DataBuff), &CmdWrite.DataSize);
			}
			fclose(fp);
			if(ln) {
				if(ln < 0) {
					fprintf(stderr, "%s : File access failure.\n", cp);
				} else {
					fprintf(stderr, "%s (%ld) : Hex format error.\n", cp, ln);
				}
				return(RC_FILE);
			}
		} /* else */

	} /* for */

	return(0);
}



/*-----------------------------------------------------------------------
  Device control functions
-----------------------------------------------------------------------*/


/* Enter the device to ISP mode */

int enter_ispmode ()
{
	BYTE rd;
	int tried, scan;


	spi_reset();	/* Reset device */

	for(tried = 1; tried <= 3; tried++) {
		for(scan = 1; scan <= 32; scan++) {
			spi_xmit(C_EN_PRG1);			/* 1st cmd */
			spi_xmit(C_EN_PRG2);			/* 2nd cmd */
			rd = spi_rcvr(RM_SYNC);			/* 3rd cmd and read echo back */
			spi_xmit(0);					/* 4th cmd */
			if(rd == C_EN_PRG2) return(0);	/* Was 2nd command echoed back? */
			if(tried <= 2) break;			/* first 2 attempts are for 1200, final attempt is for others */
			spi_clk();						/* shift scan point */
		}
	}

	MESS("Device connection failed.\n");
	return(1);
}




/* Read a byte from device */

BYTE read_byte (BYTE src,	/* Read from.. FLASH/EEPROM/SIGNATURE */
				DWORD adr)	/* Address */
{
	BYTE cmd;


	switch (src) {
		case FLASH :
			cmd = (BYTE)((adr & 1) ? C_RD_PRGH : C_RD_PRGL);
			adr >>= 1;
			if((Device->FlashSize > (128*1024))
				&& ((adr & 65535) == 0)) {		/* Load extended address if needed */
				spi_xmit(C_LD_ADRX);			/* extended address command */
				spi_xmit(0);					/* 0 */
				spi_xmit((BYTE)(adr >> 16));	/* address extended */
				spi_xmit(0);					/* 0 */
			}
			break;
		case EEPROM :
			cmd = C_RD_EEP;
			break;
		case SIGNATURE :
			cmd = C_RD_SIG;
			break;
		default :
			return(0xFF);
	}
	spi_xmit(cmd);					/* Eead command */
	spi_xmit((BYTE)(adr >> 8));		/* Address high */
	spi_xmit((BYTE)adr);			/* Address low */
	return(spi_rcvr(RM_SYNC));		/* Receive data */
}




/* Read multiple bytes from device */

void read_multi (BYTE src,		/* Read from.. FLASH/EEPROM/SIGNATURE */
				 DWORD adr,		/* Address */
				 DWORD cnt,		/* Number of bytes to read */
				 BYTE *buff)	/* Read buffer */
{
	BYTE cmd;
	DWORD n, devadr;


	for(n = 0; n < cnt; n++) {
		switch (src) {
			case FLASH :
				cmd = (BYTE)((adr & 1) ? C_RD_PRGH : C_RD_PRGL);
				devadr = adr >> 1;
				if((Device->FlashSize > (128*1024))
					&& ((devadr & 0xFFFF) == 0)) {	/* Load extended address if needed */
					spi_xmit(C_LD_ADRX);			/* Extended address command */
					spi_xmit(0);					/* 0 */
					spi_xmit((BYTE)(devadr >> 16));	/* address extended */
					spi_xmit(0);					/* 0 */
				}
				break;
			case EEPROM :
				cmd = C_RD_EEP;
				devadr = adr;
				break;
			case SIGNATURE :
				cmd = C_RD_SIG;
				devadr = adr;
				break;
			default :
				return;
		}
		spi_xmit(cmd);					/* Eead command */
		spi_xmit((BYTE)(devadr >> 8));	/* Address high */
		spi_xmit((BYTE)devadr);			/* Address low */
		spi_rcvr(RM_ASYNC);				/* Receive data in asinc mode */
		adr++;							/* Next location */
	}
	spi_delayedget(buff, cnt);			/* Get delayed data */
}




/* Read fuse bytes from device into FuseBuff,CalBuff */

void read_fuse ()
{
	int n;


	if(Device->FuseType == 0) return;	/* Type 0 : No fuse */

	if(Device->FuseType == 1) {
		spi_xmit(C_RD_FLB1);			/* Type 1 : Read fuse low */
		spi_xmit(0);
		spi_xmit(0);
		FuseBuff[0] = spi_rcvr(RM_SYNC);
	}
	else {
		spi_xmit(C_RD_FB1);				/* Type 2..6 : Read fuse low */
		spi_xmit(0);
		spi_xmit(0);
		FuseBuff[0] = spi_rcvr(RM_SYNC);

		if(Device->FuseType >= 5) {
			spi_xmit(C_RD_FLB1);		/* Type 5..6 : Read fuse high */
			spi_xmit(0x08);
			spi_xmit(0);
			FuseBuff[1] = spi_rcvr(RM_SYNC);

			if(Device->FuseType >= 6) {
				spi_xmit(C_RD_FB1);		/* Type 6 : Read fuse extend */
				spi_xmit(0x08);
				spi_xmit(0);
				FuseBuff[2] = spi_rcvr(RM_SYNC);
			}
		}
	}

	for(n = 0; n < Device->Cals; n++) {	/* Read calibration bytes if present */
		spi_xmit(C_RD_CAL);
		spi_xmit(0);
		spi_xmit((BYTE)n);
		CalBuff[n] = spi_rcvr(RM_SYNC);
	}
}




/* Write a byte into memory */

int write_byte (char dst,	/* Write to.. FLASH/EEPROM */
				DWORD adr,	/* Address */
				BYTE wd)	/* Data to be written */
{
	int n;


	switch (dst) {
		case FLASH :
			if(wd == 0xFF) return(0);	/* Skip if the value is 0xFF */

		case FLASH_NS :
			spi_xmit((BYTE)((adr & 1) ? C_WR_PRGH : C_WR_PRGL));
			spi_xmit((BYTE)(adr >> 9));
			spi_xmit((BYTE)(adr >> 1));
			spi_xmit(wd);
			if((Device->PollData == wd)				/* Write data is equal to poll data */
				|| (Device->ID == S1200)			/* 90S1200 cannot be polled */
				|| (CtrlPort.PortClass == TY_VCOM))	/* Preventing read access for USB serial */
			{	delay_ms(Device->FlashWait);
				return(0);
			}
			break;

		case EEPROM :
			spi_xmit(C_WR_EEP);
			spi_xmit((BYTE)(adr >> 8));
			spi_xmit((BYTE)adr);
			spi_xmit(wd);
			if((Device->PollData == wd) || (Device->PollData == (BYTE)~wd)
				|| (Device->ID == S1200)
				|| (CtrlPort.PortClass == TY_VCOM))
			{	delay_ms(Device->EepromWait);
				return(0);
			}
			break;

		default:
			return(1);
	}

	for(n = 0; n < 200; n++) {	/* Wait for end of programming (Polling) */
		if(read_byte(dst, adr) == wd) return(0);
	}

	return(1);	/* Polling time out */
}




/* Write a page into flash memory */

void write_page (DWORD adr,			/* Address (must be page boundary) */
				 const BYTE *wd)	/* Pointer to the page data */
{
	BYTE d = 0xFF;
	int n;

	/* Skip page if all data in the page are 0xFF */
	for(n = 0; n < Device->FlashPage; n++)
		d &= wd[n];
	if(d == 0xFF) return;

	/* Load the page data into page buffer */
	for(n = 0; n < Device->FlashPage; n++) {
		spi_xmit((BYTE)((n & 1) ? C_WR_PRGH : C_WR_PRGL));
		spi_xmit((BYTE)((adr + n) >> 9));
		spi_xmit((BYTE)((adr + n) >> 1));
		spi_xmit(wd[n]);
	}

	/* Load extended address if needed */
	if(Device->FlashSize > (128*1024)) {
		spi_xmit(C_LD_ADRX);
		spi_xmit(0);
		spi_xmit((BYTE)(adr >> 17));
		spi_xmit(0);
	}

	/* Start page programming */
	spi_xmit(C_WR_PAGE);
	spi_xmit((BYTE)(adr >> 9));
	spi_xmit((BYTE)(adr >> 1));
	spi_xmit(0);

	delay_ms(Device->FlashWait);	/* Wait for page write time */
}




/* Write Fuse or Lock byte */

void write_fuselock (char dst,		/* which fuse to be written */
					 BYTE val)		/* fuse value */
{
	switch (dst) {
		case F_LOW :	/* Fuse Low byte */
			if(Device->FuseType <= 2) {		/* Type 1,2 */
				spi_xmit(C_WR_FLB);
				spi_xmit((BYTE)(val & 0xBF));
				spi_xmit(0);
				spi_xmit(0);
			} else {						/* Type 3..6 */
				spi_xmit(C_WR_FLB);
				spi_xmit(C_WR_FLBL);
				spi_xmit(0);
				spi_xmit(val);
			}
			break;

		case F_HIGH :	/* Fuse High byte */
			spi_xmit(C_WR_FLB);
			spi_xmit(C_WR_FLBH);
			spi_xmit(0);
			spi_xmit(val);
			break;

		case F_EXTEND :	/* Fuse Extend byte */
			spi_xmit(C_WR_FLB);
			spi_xmit(C_WR_FLBX);
			spi_xmit(0);
			spi_xmit(val);
			break;

		case F_LOCK :	/* Device Lock byte */
			if(Device->FuseType <= 3) {		/* Type 0..3 */
				spi_xmit(C_WR_FLB);
				spi_xmit((BYTE)(C_WR_FLBK | val));
				spi_xmit(0);
				spi_xmit(0);
			} else {						/* Type 4..6 */
				spi_xmit(C_WR_FLB);
				spi_xmit(C_WR_FLBK);
				spi_xmit(0);
				spi_xmit(val);
			}
	} /* switch */
	delay_ms(20);	/* Wait for 20ms */
	spi_flush();
}




/* Issue chip erase command and re-enter ISP mode */

int erase_memory ()
{
	spi_xmit(C_ERASE1);	/* Issue a Chip Erase command */
	spi_xmit(C_ERASE2);
	spi_xmit(0);
	spi_xmit(0);

	delay_ms(40);		/* Wait for 40ms */

	if(Device->ID == T12) {		/* This is to avoid Tiny12's bug */
		if(enter_ispmode()) return(RC_DEV);
		write_byte(FLASH_NS, 0, 0xFF);	/* Dummy write */

		spi_xmit(C_ERASE1);		/* Issue 2nd Chip Erase command */
		spi_xmit(C_ERASE2);
		spi_xmit(0);
		spi_xmit(0);

		delay_ms(40);			/* Wait for 40ms */
	}
	spi_flush();

	/* Re-enter ISP mode and return its status */
	return(enter_ispmode() ? RC_DEV : 0);
}




/* Initialize control port and device if needed */

int init_devices ()
{
	int res;

	/* Execute initialization if not initialized yet */
	if(Device == NULL) {
		res = open_ifport(&CtrlPort);		/* Open interface port and show port information */
		if(CtrlPort.Info1)
			MESS(CtrlPort.Info1);
		if(CtrlPort.Info2)
			MESS(CtrlPort.Info2);
		if(res) return (RC_INIT);			/* return if failed open_ifport() */

		if(enter_ispmode()) return(RC_DEV);

		/* read device signature */
		read_multi(SIGNATURE, 0, 4, SignBuff);

		/* search device */
		for(Device = DevLst; Device->ID != N0000; Device++) {
			if(ForcedName[0]) {
				if(strcmp(ForcedName, Device->Name) == 0) break;
			} else {				
				if(memcmp(SignBuff, Device->Sign, 3) == 0) break;
			}
		}

		/* Show found device type */
		switch (Device->ID) {
			case N0000 :
				fprintf(stderr, "Unknown device (%02X-%02X-%02X).\n",
						SignBuff[0], SignBuff[1], SignBuff[2]);
				break;

			case L0000 :
				fprintf(stderr, "Locked device or Synchronization failed.\n");
				break;

			default :
				fprintf(stderr, "Detected device is AT%s.\n", Device->Name);
		}
	} /* if */

	return(0);
}




/*-----------------------------------------------------------------------
  Programming functions
-----------------------------------------------------------------------*/


/* -e command */

int erase_device ()
{
	int rc;


	if(rc = init_devices()) return(rc);
	if(Device->ID == N0000) return(RC_DEV);		/* Abort if unknown device */

	if(rc = erase_memory()) return(rc);
	MESS("Erased.\n");

	return(0);
}




/* -z command */

int test_ctrlport ()
{
	int n;


	n = open_ifport(&CtrlPort);		/* Open interface port and show port information */
	if(CtrlPort.Info1)
		MESS(CtrlPort.Info1);
	if(CtrlPort.Info2)
		MESS(CtrlPort.Info2);
	if(n) return (RC_INIT);			/* return if failed open_ifport() */

	MESS("1000Hz test pulse on SCK. This takes a time...");
	for (n = 0; n < 10000; n++) {
		delay_ms(1);
		spi_clk();
	}
	spi_flush();

	return(0);
}




/* -r command */

int read_device (char cmd)
{
	DWORD adr;
	int rc;


	if(rc = init_devices()) return(rc);
	if(Device->ID <= L0000) return(RC_DEV);		/* Abort if unknown device or locked device */

	switch (cmd) {
		case 'p' :	/* -rp : read program memory */
			MESS("Reading Flash...");
			for(adr = 0; adr < Device->FlashSize; adr += PIPE_WINDOW)
				read_multi(FLASH, adr, PIPE_WINDOW, &CodeBuff[adr]);
			MESS("Passed.\n");
			output_hexfile(stdout, CodeBuff, Device->FlashSize, 32);
			break;

		case 'e' :	/* -re : read eeprom */
			MESS("Reading EEPROM...");
			for(adr = 0; adr < Device->EepromSize; adr += PIPE_WINDOW)
				read_multi(EEPROM, adr, PIPE_WINDOW, &DataBuff[adr]);
			MESS("Passed.\n");
			output_hexfile(stdout, DataBuff, Device->EepromSize, 32);
			break;

		case 'f' :	/* -rf : read fuses */
			read_fuse();
			output_fuse();
			break;

		default :
			output_deviceinfo();
	}

	return(0);
}




/* .hex file write command */

int write_flash ()
{
	DWORD adr;
	BYTE rd[PIPE_WINDOW];
	int rc, n;


	if(rc = init_devices()) return(rc);
	if(Device->ID <= L0000) return(RC_DEV);	/* Abort if unknown device or locked device */

	MESS("Flash: ");
	if(CmdWrite.CodeSize > Device->FlashSize)	/* Truncate code size by memory size */
		CmdWrite.CodeSize = Device->FlashSize;

	if(CmdWrite.Verify != 1) {	/* -v : Skip programming process when verify only mode */

		MESS("Erasing...");						/* Erase device before programming */
		if(rc = erase_memory()) return(rc);

		if(CmdWrite.CopyCal && Device->Cals) {	/* -c : Copy calibration bytes */
			read_fuse();
			for(n = 0; n < Device->Cals; n++)
				CodeBuff[Device->FlashSize - 1 - n] = CalBuff[n];
			CmdWrite.CodeSize = Device->FlashSize;
		}

		MESS("Writing...");
		if(Device->FlashPage) {		/* Write flash in page mode */
			for(adr = 0; adr < CmdWrite.CodeSize; adr += Device->FlashPage)
				write_page(adr, &CodeBuff[adr]);
		}
		else {						/* Write flash in byte-by-byte mode */
			for(adr = 0; adr < CmdWrite.CodeSize; adr++) {
				if(write_byte(FLASH, adr, CodeBuff[adr])) {
					fprintf(stderr, "Time out at %04X\n", adr);
					return(RC_FAIL);
				}
			}
		}
		spi_flush();
	} /* if */

	if(CmdWrite.Verify != 2) {	/* -v- : Skip verifying process when programming only mode */
		MESS("Verifying...");
		for(adr = 0; adr < CmdWrite.CodeSize; adr += PIPE_WINDOW) {
			read_multi(FLASH, adr, PIPE_WINDOW, rd);
			for(n = 0; n < PIPE_WINDOW; n++) {
				if(rd[n] != CodeBuff[adr+n]) {
					fprintf(stderr, "Failed at %04X:%02X-%02X\n", adr+n, CodeBuff[adr+n], rd[n]);
					return(RC_FAIL);
				}
			}
		}
	} /* if */

	MESS("Passed.\n");

	return(0);
}




/* .eep file write command */

int write_eeprom ()
{
	DWORD adr;
	BYTE rd[PIPE_WINDOW];
	int rc, n;


	if(rc = init_devices()) return(rc);
	if(Device->ID <= L0000) return(RC_DEV);	/* Abort if unknown device or locked device */

	MESS("EEPROM: ");
	if(CmdWrite.DataSize > Device->EepromSize)	/* Truncate data size by memory size */
		CmdWrite.DataSize = Device->EepromSize;

	if(CmdWrite.Verify != 1) {	/* -v : Skip programming process when verify mode */
		MESS("Writing...");
		for(adr = 0; adr < CmdWrite.DataSize; adr++) {	/* Write EEPROM without erase */
			if(write_byte(EEPROM, adr, DataBuff[adr])) {
				fprintf(stderr, "Time out at %04X\n", adr);
				return(RC_FAIL);
			}
		}
		spi_flush();
	}

	if(CmdWrite.Verify != 2) {	/* -v- : Skip verifying process when programming only mode */
		MESS("Verifying...");
		for(adr = 0; adr < CmdWrite.DataSize; adr += PIPE_WINDOW) {
			read_multi(EEPROM, adr, PIPE_WINDOW, rd);
			for(n = 0; n < PIPE_WINDOW; n++) {
				if(rd[n] != DataBuff[adr+n]) {
					fprintf(stderr, "Failed at %04X:%02X-%02X\n", adr+n, DataBuff[adr+n], rd[n]);
					return(RC_FAIL);
				}
			}
		}
	}

	MESS("Passed.\n");

	return(0);
}




/* -f{l|h|x}, -l command */

int write_fuse ()
{
	int	rc;


	if(rc = init_devices()) return(rc);
	if(Device->ID <= L0000) return(RC_DEV);		/* Abort if unknown device or locked device */

	if(CmdFuse.Cmd.Flag.Low && (Device->FuseType > 0)) {
		write_fuselock(F_LOW, (BYTE)(CmdFuse.Data[0] | ~Device->FuseMask[0]));
		MESS("Fuse Low byte was programmed.\n");
	}

	if(CmdFuse.Cmd.Flag.High && (Device->FuseType >= 5)) {
		write_fuselock(F_HIGH, (BYTE)(CmdFuse.Data[1] | ~Device->FuseMask[1]));
		MESS("Fuse High byte was programmed.\n");
	}

	if(CmdFuse.Cmd.Flag.Extend && (Device->FuseType >= 6)) {
		write_fuselock(F_EXTEND, (BYTE)(CmdFuse.Data[2] | ~Device->FuseMask[2]));
		MESS("Fuse Extend byte was programmed.\n");
	}

	if(CmdFuse.Cmd.Flag.Lock) {
		write_fuselock(F_LOCK, (BYTE)(CmdFuse.Data[3] ? CmdFuse.Data[3] : Device->LockData));
		MESS("Lock bits are programmed.\n");
	}

	return(0);
}




/* Terminate process */

void terminate ()
{
	close_ifport();
	Device = NULL;

	if(Pause) {
		MESS("\nType Enter to exit...");
		getchar();
	}
}



/*-----------------------------------------------------------------------
  Main
-----------------------------------------------------------------------*/


int main (int argc, char **argv)
{
	int rc;

	if(rc = load_commands(argc, argv)) { 
		if(rc == RC_SYNTAX) output_usage();
		terminate();
		return(rc);
	}

	/* Read device and terminate if -r{p|e|f} command is specified */
	if(Command[0] == 'r') {
		rc = read_device(Command[1]);
		terminate();
		return(rc);
	}

	/* Erase device and terminate if -e command is specified */
	if(Command[0] == 'e') {
		rc = erase_device();
		terminate();
		return(rc);
	}

	/* Timing test if -e command is specified */
	if(Command[0] == 'z') {
		rc = test_ctrlport();
		terminate();
		return(rc);
	}

	/* Write to device if any file is loaded */
	if(CmdWrite.CodeSize) {
		if(rc = write_flash()) {
			terminate();
			return(rc);
		}
	}
	if(CmdWrite.DataSize) {
		if(rc = write_eeprom()) {
			terminate();
			return(rc);
		}
	}

	/* Write fuse,lock if -f{l|h|x}, -l are specified */
	if(CmdFuse.Cmd.Flags) {		
		if(rc = write_fuse()) {
			terminate();
			return(rc);
		}
	}

	if(Device == NULL) output_usage();
	terminate();
	return (0);
}

