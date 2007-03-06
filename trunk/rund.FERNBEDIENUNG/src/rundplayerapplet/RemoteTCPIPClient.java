package rundplayerapplet;

import java.net.ServerSocket;
import java.io.*;
import java.net.Socket;
import java.net.*;

public class RemoteTCPIPClient extends Thread {

    private Socket socket = null;
    public static final int STARTBYTE = 2;

    public static final int ENDBYTE = 3;

    private static int threadCount = 0;

    private char[] charBuffer = new char[50];

    private byte charBufferIndex = 0;

    private boolean isContent = false;

    private RundfunkerApplet ra;

    private OutputStream os;

    private InputStream in;

    public boolean running = true;

    private String ip;

    boolean show_cursor = false;

    public RemoteTCPIPClient(RundfunkerApplet ra, String ip) {
        this.ra = ra;
        this.ip = ip;
    }

    public void send(String message) throws IOException {
        os.write(2);
        os.write(message.getBytes());
        os.write(3);
        os.flush();
    }

    public void close() throws IOException {
        running = false;
        if (socket != null) {
            os.close();
            socket.close();
        }
    }


    private char[] getCharBufferContent() {
        char[] ret = new char[charBufferIndex];
        System.arraycopy(charBuffer, 0, ret, 0, charBufferIndex);
        return ret;
    }

    private void resetCharBuffer() {

        charBuffer = new char[50];
        charBufferIndex = 0;
    }

    private void parseData() {
        char[] data = getCharBufferContent();
        String s = new String(data);
        if (s.startsWith("L")) {
            int LEDnum = data[1] - 0x30;
            //TODO: LED leuchten lassen RundfunkerApplet Methode: setLED(...)
        } else if (s.startsWith("Z")) {
            int row = data[1] - 0x30;
            String line = s.substring(2, s.length());
            //TODO: setze Display Zeile RundfunkerApplet Methode: setLine(...)
            line = line.replace('~', '>');
            line = line.replace((char)6, '!');
            line = line.replace((char)126, '>');
            line = line.replace((char)127, '<');
            ra.setLine(row, line);
        } else if (s.equalsIgnoreCase("SERVUS")) {
            ra.setLine(0, "Handshake");
        } else if (s.startsWith("C")) {
            if (s.substring(1, 2).equalsIgnoreCase("0")) {
                show_cursor = false;
            }
            if (s.substring(1, 2).equalsIgnoreCase("1")) {
                show_cursor = true;
            }
        } else if (s.startsWith("M")) {
            System.out.println("M: " + s);
            int row = new Integer(s.substring(4, 5));
            int col = new Integer(s.substring(1, 3));
            ra.setCursorPos(row, col, show_cursor);
        }
    }

    public void run() {

        // Verbindung Aufbauen

        int counter = 1;
        while (socket == null) {
            ra.setLine(1, "Verbindungsversuch " + counter++ +" ...");
            ra.setLine(2, "");
            try {

                socket = new Socket(this.ip, 9090);
                System.out.println("connected to " + socket.getInetAddress() +
                                   " at port " + socket.getPort());
            } catch (UnknownHostException ex) {
                ra.setLine(0, "Unknown Host");
                ex.printStackTrace();
            } catch (IOException ex) {
                ra.setLine(2, "Verbinden fehlgeschlagen");
                if (socket == null) {
                    try {
                        this.sleep(1500);
                    } catch (InterruptedException ex2) {
                        ex2.printStackTrace();
                    }
                }
            }

        }

        //Outputstream holen + Handshake

        try {
            os = socket.getOutputStream();
            in = socket.getInputStream();
            this.send("SERVUS");
        } catch (IOException ex1) {
            ra.setLine(1, "Handshake: IO Exception");
            ex1.printStackTrace();
        }

        //Inputstream holen
        try {
            while (running && socket.isConnected()) {
                int read = in.read();
                if (read == STARTBYTE) {
                    isContent = true;
                    continue;
                }

                if (read == ENDBYTE) {
                    isContent = false;
                    parseData();
                    resetCharBuffer();
                    continue;
                }

                if (isContent) {
                    charBuffer[charBufferIndex++] = (char) read;
                }
            }
            in.close();
        } catch (IOException e) {
            ra.setLine(3, "Read: " + e.getMessage());
        }

        ra.setLine(1, "Verbindung beendet");
        ra.setLine(2, "");

    }
}
