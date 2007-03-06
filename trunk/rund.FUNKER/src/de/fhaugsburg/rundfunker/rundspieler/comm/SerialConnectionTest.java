package de.fhaugsburg.rundfunker.rundspieler.comm;

import gnu.io.*;
import java.io.*;

public class SerialConnectionTest {

    ServerSerial scon;

    public SerialConnectionTest() {
        try {
        	
            scon = new ServerSerial(null,null);
            scon.start();

            scon.writeToSerial("Z1test");

            scon.stopped=true;

        } catch (PortInUseException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) {
        SerialConnectionTest serialconnectiontest = new SerialConnectionTest();
    }
}
