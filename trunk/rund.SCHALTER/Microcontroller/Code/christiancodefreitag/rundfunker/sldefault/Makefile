###############################################################################
# Makefile for the project rundfunker
###############################################################################

## General Flags
PROJECT = rundfunker
MCU = atmega168
TARGET = rundfunker.elf
CC = avr-gcc

## Options common to compile, link and assembly rules
COMMON = -mmcu=$(MCU)

## Compile options common for all C compilation units.
CFLAGS = $(COMMON)
CFLAGS += -Wall -gdwarf-2  -DF_CPU=20000000  -Os -fsigned-char
CFLAGS += -Wp,-M,-MP,-MT,$(*F).o,-MF,dep/$(@F).d 

## Assembly specific flags
ASMFLAGS = $(COMMON)
ASMFLAGS += -x assembler-with-cpp -Wa,-gdwarf2

## Linker flags
LDFLAGS = $(COMMON)
LDFLAGS += 


## Intel Hex file production flags
HEX_FLASH_FLAGS = -R .eeprom

HEX_EEPROM_FLAGS = -j .eeprom
HEX_EEPROM_FLAGS += --set-section-flags=.eeprom="alloc,load"
HEX_EEPROM_FLAGS += --change-section-lma .eeprom=0


## Include Directories
INCLUDES = -I"C:\Dokumente und Einstellungen\rundfunker\Eigene Dateien\rundfunker\rund.SCHALTER\trunk\Microcontroller\Code\AVRlib" 

## Objects that must be built in order to link
OBJECTS = rundfunker.o timerx8.o sl_shiftregister.o sl_led_3to8.o uart.o rprintf.o buffer.o sl_rotary.o lcd.o 

## Build
all: $(TARGET) rundfunker.hex rundfunker.eep size

## Compile
rundfunker.o: ../rundfunker.c
	$(CC) $(INCLUDES) $(CFLAGS) -c  $<

timerx8.o: ../timerx8.c
	$(CC) $(INCLUDES) $(CFLAGS) -c  $<

sl_shiftregister.o: ../sl_shiftregister.c
	$(CC) $(INCLUDES) $(CFLAGS) -c  $<

sl_led_3to8.o: ../sl_led_3to8.c
	$(CC) $(INCLUDES) $(CFLAGS) -c  $<

uart.o: ../uart.c
	$(CC) $(INCLUDES) $(CFLAGS) -c  $<

rprintf.o: ../rprintf.c
	$(CC) $(INCLUDES) $(CFLAGS) -c  $<

buffer.o: ../buffer.c
	$(CC) $(INCLUDES) $(CFLAGS) -c  $<

sl_rotary.o: ../sl_rotary.c
	$(CC) $(INCLUDES) $(CFLAGS) -c  $<

lcd.o: ../lcd.c
	$(CC) $(INCLUDES) $(CFLAGS) -c  $<

##Link
$(TARGET): $(OBJECTS)
	 $(CC) $(LDFLAGS) $(OBJECTS) $(LIBDIRS) $(LIBS) -o $(TARGET)

%.hex: $(TARGET)
	avr-objcopy -O ihex $(HEX_FLASH_FLAGS)  $< $@

%.eep: $(TARGET)
	avr-objcopy $(HEX_EEPROM_FLAGS) -O ihex $< $@

%.lss: $(TARGET)
	avr-objdump -h -S $< > $@

size: ${TARGET}
	@echo
	@sh avr-mem.sh ${TARGET} ${MCU}

## Clean target
.PHONY: clean
clean:
	-rm -rf $(OBJECTS) rundfunker.elf dep/ rundfunker.hex rundfunker.eep

## Other dependencies
-include $(shell mkdir dep 2>/dev/null) $(wildcard dep/*)

