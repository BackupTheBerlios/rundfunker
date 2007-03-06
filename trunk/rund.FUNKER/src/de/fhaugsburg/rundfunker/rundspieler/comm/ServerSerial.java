package de.fhaugsburg.rundfunker.rundspieler.comm;

import java.io.*;
import java.util.*;

import de.fhaugsburg.rundfunker.rundspieler.*;
import de.fhaugsburg.rundfunker.rundspieler.view.*;
import de.fhaugsburg.rundfunker.rundsucher.*;
import gnu.io.*;

/**
 *
 * @author Christian Leberfinger
 * @version 1.0
 */
public class ServerSerial implements SerialPortEventListener, Runnable {

    public static final int STARTBYTE = 2;

    public static final int ENDBYTE = 3;

    private static Config conf = Config.getInstance();

    /** Der Port f¸r die Ansteuerung des Microcontrollers */
    private SerialPort uCPort = null;

    private InputStream in = null;

    private OutputStream out = null;

    private static final int PORT_TIMEOUT = 2000;

    public boolean stopped = false;

    private Thread readThread = null;

    private Controller controller;

    private char[] charBuffer = new char[50];

    private byte charBufferIndex = 0;

    private boolean helloReceived = false;

    /** isContent markiert den Befehl zwischen STX und ETX */
    private boolean isContent = false;

    public ServerSerial(DisplayServer display, Controller controller) throws
            PortInUseException {

        this.controller = controller;

        /* Nach dem richtigen Port f¸r Microcontroller-Ansteuerung suchen */
        Enumeration en = CommPortIdentifier.getPortIdentifiers();
        while (en.hasMoreElements()) {
            CommPortIdentifier cpi = (CommPortIdentifier) en.nextElement();

            // uns interessieren nur serielle Ports
            if (cpi.getPortType() == cpi.PORT_SERIAL) {

                System.out.println("Serieller Port gefunden: " + cpi.getName());

                SerialPort tempPort = (SerialPort) cpi.open("RundSpieler",
                        PORT_TIMEOUT);

                System.out.println("Bereite Port f¸r Ping vor.");
                preparePort(tempPort);

                System.out.println("Versuche uC auf "+cpi.getName()+" zu pingen.");
                try {
                    // ping absetzen
                    out.write('h');
                    out.flush();
                    // dem uC etwas Zeit zum antworten geben
                    Thread.sleep(250);
                } catch (Exception ex) {
                    System.out.println("Pingen ist fehlgeschlagen.");
                }

                // Microcontroller h‰ngt an aktuellem Port und sendet ACK
                if (isMicroControllerOnPort()) {
                    System.out.println("Micro-Controller an Port "+cpi.getName()+" gefunden.");
                    // Port merken und das Iterieren ¸ber die Ports abbrechen
                    uCPort = tempPort;
                    break;
                } else {
                    System.out.println("Keine Antwort auf Port "+cpi.getName()+".");
                    // Aktuellen Port wieder schlieﬂen
                    tempPort.close();
                }

            }
            // nicht serielle ports zur information anzeigen
            else {
                System.out.println("Nicht-Serieller Port gefunden: " +
                                   cpi.getName());
            }
        }

        try {
            //Begr¸ﬂung senden
            out.write('k');
            out.flush();

            //Neues RemoteDisplay als Listener beim Display registrieren
            display.addDisplayListener(new DisplayClientRemote(out,
                    display));

        } catch (IOException ex) {
        }

    }

    /**
     * Setzt die Parameter des ¸bergebenen Ports, ˆffnet Input- und Output-
     * Stream, meldet den Event-Listener f¸r Callbacks an.
     * @param port SerialPort
     */
    public void preparePort(SerialPort port) {
        /* Port f¸r Input ˆffnen */
        try {
            in = port.getInputStream();
        } catch (IOException ex1) {
            System.out.println("Fehler beim ÷ffnen des Outputstreams.");
        }

        /* Port f¸r Output ˆffnen */
        try {
            out = port.getOutputStream();

        } catch (IOException ex1) {
            System.out.println("Fehler beim ÷ffnen des Outputstreams.");
        }

        /* Schnittstellenparameter einstellen */
        try {
            port.setSerialPortParams(9600, SerialPort.DATABITS_8,
                                     SerialPort.STOPBITS_1,
                                     SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException ex2) {
            System.out.println("Fehler beim Setzen der Port-Parameter.");
        }

        /* Ich will bei empfangenen Daten benachrichtigt werden */
        try {
            port.addEventListener(this);
            port.notifyOnDataAvailable(true);
        } catch (TooManyListenersException ex) {
            System.out
                    .println("Da lauschen schon zu viele Leut an diesem Port.");
        }
    }

    /**
     * Gibt zur¸ck, ob der MicroController sich auf dem aktuellen Port befindet
     * @return boolean
     */
    public boolean isMicroControllerOnPort() {
        return helloReceived;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void start() {
        readThread = new Thread(this);
        readThread.start();
    }

    /**
     * Schickt den angegebenen String an den Microcontroller. STX und ETX werden
     * automatisch hinzugef¸gt.
     * @param string String
     * @throws IOException
     */
    public void writeToSerial(String string) throws IOException {
        writeToSerial(string.getBytes());
    }

    /**
     * Schickt das angegebene byte-Array an den Microcontroller. STX und ETX
     * werden automatisch hinzugef¸gt.
     * @param byteArray byte[]
     * @throws IOException
     */
    public void writeToSerial(byte[] byteArray) throws IOException {
        out.write(STARTBYTE);
        out.write(byteArray);
        out.write(ENDBYTE);
    }

    /**
     * Behandelt die empfangenen Daten
     */
    private void parseData() {
        char[] data = getCharBufferContent();

        String s = new String(data);
//        System.out.println(s+" wurde empfangen.");

        resetCharBuffer(); //zur¸cksetzen des Puffers

        if (s.startsWith("JD ")) {
            int val = data[3] - 0x30;
            if (val == 1) {
                controller.turnDDS(Display.JD_TURN_LEFT);
            } else if (val == 2) {
                controller.turnDDS(Display.JD_TURN_RIGHT);
            }
        } else if (s.startsWith("BT ")) {
            int btNr = data[3] - 0x30;
            if (btNr == 0) {
                return;
            }
            switch (btNr) {
            case 0:
                break;
            case 1:
                // Dr¸cken des Drehdr¸ckstellers
                controller.pressDDS();
                System.out.println("BT_DDS pressed");
                break;
            case 2:
                // Interpret
                controller.pressBtArtist();
                System.out.println("BT_Interpret pressed");
                break;
            case 3:
                // Titel
                controller.pressBtTitle();
                System.out.println("BT_Titel pressed");
                break;
            case 4:
                // Album
                controller.pressBtAlbum();
                System.out.println("BT_Album pressed");
                break;
            case 5:
                // Genre
                controller.pressBtGenre();
                System.out.println("BT_Genre pressed");
                break;
            }
        } else if (s.startsWith("L")) {
//			int row= data[1]-0x30;
//			display.setLine(row,s.substring(2,s.length()));
            ;
        } else if (s.startsWith("D")) {
            //DEBUG:
            System.out.println(new String(data));
        } else if (s.startsWith("hello")) {
            helloReceived = true;
        }
    }

    /**
     * Setzt den Eingang-Puffer zur¸ck
     */
    private void resetCharBuffer() {
        charBuffer = new char[50];
        charBufferIndex = 0;
    }

    /**
     * Gibt den Inhalt des Eingang-Puffers zur¸ck
     * @return char[]
     */
    private char[] getCharBufferContent() {
        char[] ret = new char[charBufferIndex];
        System.arraycopy(charBuffer, 0, ret, 0, charBufferIndex);
        return ret;
    }

    /**
     * Callback-Methode f¸r Events an der seriellen Schnittstelle.
     * Uns interessiert dabei nur der Event "SerialPortEvent.DATA_AVAILABLE"
     * @param serialPortEvent SerialPortEvent
     */
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                while (in.available() > 0) {

                    // Zeichen einlesen
                    int read = in.read();

                    // War Zeichen Beginn eines Befehls?
                    if (read == STARTBYTE) {
                        isContent = true;
                        continue;
                    }

                    // War das Zeichen das Ende eines Befehls?
                    if (read == ENDBYTE) {
                        isContent = false;
                        parseData();
                        resetCharBuffer();
                        continue;
                    }

                    // Zwischen STARTBYTE und ENDBYTE befindet sich der Befehl
                    if (isContent) {
                        charBuffer[charBufferIndex++] = (char) read;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void run() {
        while (!stopped) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }

        /*
         * Schlieﬂe den Port wieder, damit nicht das passiert, was 1954 in
         * Chicago passiert ist
         */
        uCPort.close();
    }

}
