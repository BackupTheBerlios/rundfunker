package de.fhaugsburg.rundfunker.rundspieler;

import java.io.*;
import java.net.*;

import de.fhaugsburg.rundfunker.rundspieler.view.*;
import de.fhaugsburg.rundfunker.rundsucher.*;

public class ServerTCPIP implements Runnable {

    private Socket s = null;

    private ServerSocket ss = null;

    private Config conf = Config.getInstance();

    public boolean stopped = false;

    private Controller controller;

    private DisplayServer display;

    private OutputStream os;

    public ServerTCPIP(DisplayServer display, Controller controller) throws
            NumberFormatException, IOException {
        this.controller = controller;
        this.display = display;
        ss = new ServerSocket(Integer.parseInt(conf
                                               .getProperty("RemoteTCPIPPort")));
        System.out.println("Server wird gestartet an Port "
                           + conf.getProperty("RemoteTCPIPPort"));
    }

    public void run() {
        try {
            while (!stopped) {
                s = ss.accept();
                TCPIPThread t = new TCPIPThread(this, s);
                System.out.println("Neuen Client von Thread #" +
                                   t.getThreadCount()
                                   + " angenommen. " + s.getInetAddress());
                os = s.getOutputStream();

                t.start();
            }
            os.flush();
            os.close();
            ss.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void start() {
        Thread t = new Thread(this);
        t.start();

    }

    protected void handleTCPIPCommand(char[] data, TCPIPThread tcpipthread) {
        // char[] data= getCharBufferContent();
        String s = new String(data);
        if (s.equalsIgnoreCase("servus")) {
            try {
                String hallo = "servus";
                os.write(hallo.getBytes());
                DisplayClientRemote displaylistener = new DisplayClientRemote(
                        os, display);
                display.addDisplayListener(displaylistener);
                tcpipthread.setDisplay(display);
                tcpipthread.setDisplayListener(displaylistener);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (s.startsWith("JD ")) {
            int val = data[3] - 0x30;
            if (val == 1) {
                controller.turnDDSLeft();
            } else if (val == 2) {
                controller.turnDDSRight();
            }
        } else if (s.startsWith("BT")) {
            int btNr = data[2] - 0x30;
            switch (btNr) {
            case 0:

                // Interpret
                controller.pressBtArtist();
                break;
            case 1:

                // Titel
                controller.pressBtTitle();
                break;
            case 2:

                // Album
                controller.pressBtAlbum();
                break;
            case 3:

                // Genre
                controller.pressBtGenre();
                break;
            case 4:

                // Drücken von Drehdrücksteller
                controller.pressDDS();
                break;

            }
        } else if (s.startsWith("L")) {
            // int row= data[1]-0x30;
            // display.setLine(row,s.substring(2,s.length()));
            ;
        } else if (s.startsWith("D")) {
            // DEBUG:
            System.out.println(new String(data));
        } else if (s.equalsIgnoreCase("HABEDIEEHRE")) {
            System.out.println("HabeDieEhre wurde geschickt.");
            display.removeDisplayListener(tcpipthread.getDisplayListener());
            tcpipthread.clStopped = true;
        }
    }


}


class TCPIPThread extends Thread {

    public static final int STARTBYTE = 2;

    public static final int ENDBYTE = 3;

    private static int threadCount = 0;

    private DisplayServer display;

    private DisplayClientRemote displayListener;

    private Socket socket;

    ServerTCPIP remote;

    private char[] charBuffer = new char[50];

    private byte charBufferIndex = 0;

    private boolean isContent = false; // markiert den content zwischen start-

    public boolean clStopped = false;

    // und endezeichen

    public int getThreadCount() {
        return threadCount;
    }

    public DisplayServer getDisplay() {
        return display;
    }

    public DisplayClientRemote getDisplayListener() {
        return displayListener;
    }

    TCPIPThread(ServerTCPIP remote, Socket s) {
        threadCount++;
        this.remote = remote;
        this.socket = s;
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

    public void run() {
        try {
            // InputStreamReader(socket.getInputStream()));
            InputStream in = socket.getInputStream();
            try {
                while (socket.isConnected() && !clStopped) {
                    int read = in.read();
                    if (read == STARTBYTE) {
                        isContent = true;
                        continue;
                    }

                    if (read == ENDBYTE) {
                        isContent = false;
                        remote.handleTCPIPCommand(getCharBufferContent(), this);
                        resetCharBuffer();
                        continue;
                    }

                    if (isContent) {
                        charBuffer[charBufferIndex++] = (char) read;
                    }
                }
                in.close();
//                                display.removeDisplayListener();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setDisplay(DisplayServer display) {
        this.display = display;
    }

    public void setDisplayListener(DisplayClientRemote displayListener) {
        this.displayListener = displayListener;
    }

}
