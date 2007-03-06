package de.fhaugsburg.rundfunker.rundspieler.test;

import java.io.*;
import java.util.*;

import de.fhaugsburg.rundfunker.rundspieler.comm.*;
import gnu.io.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: AG RundFunker</p>
 *
 * @author Christian Leberfinger
 * @version 1.0
 */
public class CommTest implements SerialPortEventListener {

    private Map ports;
    private InputStream in = null;
    private gnu.io.SerialPort sport = null;
    private OutputStream os = null;

    private void sendToMicroController(byte[] data) throws IOException {
        System.out.println("Sende an Stream: "+new String(data));
        os.write(2);
        os.write(data);
        os.write(3);
        os.flush();
    }

    public CommTest() {
//    	ports=new HashMap();
        Enumeration en = CommPortIdentifier.getPortIdentifiers();
//        Vector vec = new Vector();

        System.out.println("Suche nach Ports.");

        while(en.hasMoreElements()) {
            CommPortIdentifier cpi = (CommPortIdentifier) en.nextElement();

            if(cpi.getPortType() == cpi.PORT_SERIAL)
            {
                System.out.println("Serieller Port gefunden: "+cpi.getName());
//                ports.put(cpi.getName(),cpi);

                try {
                    sport = (SerialPort) cpi.open("Rundfunker", 0);
                    os = sport.getOutputStream();

                    System.out.println("Outputstream: "+os);

                    sendToMicroController("L0".getBytes());
                    sendToMicroController("Z0 ÁÃÄÅÇÈÊËÌÎÏÐÑÒÓÔÕÖØ".getBytes());
                    sendToMicroController("Z1 12345678901234567890".getBytes());

                    byte[] z2 = "Z2 Sonderzeichen:  ".getBytes();
                    z2[z2.length-1] = 0x04;
                    sendToMicroController(z2);

                    sendToMicroController("Z3 áãäåçèêëìîïðñòóôõöø".getBytes());

                    os.flush();

                    os.close();

                    sport.close();


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else
            {
                System.out.println("Nicht-Serieller Port gefunden: "+cpi.getName());
            }
        }
    }

    public Map getPorts(){
    	return ports;
    }

    public CommPortIdentifier getPort(String name){
    	return (CommPortIdentifier)ports.get(name);
    }

    public static void main(String[] args) throws PortInUseException, IOException {
        CommTest commtest = new CommTest();
//    	startTest();


    }

    private static void startTest(){
    	Runnable r= new Runnable(){
			public void run() {
				ServerSerial serConn;
				try {
					serConn = new ServerSerial(null,null);
			    	serConn.start();
			    	try {
						Thread.sleep(20000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	serConn.stopped=true;
				} catch (PortInUseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	};
    	Thread thread = new Thread(r);
    	thread.start();
    }

    public void serialEvent(SerialPortEvent serialPortEvent) {
//        System.out.println("Daten von serieller Schnittstelle empfangen.");
        switch(serialPortEvent.getEventType()) {
//                case SerialPortEvent.BI:
//                case SerialPortEvent.OE:
//                case SerialPortEvent.FE:
//                case SerialPortEvent.PE:
//                case SerialPortEvent.CD:
//                case SerialPortEvent.CTS:
//                case SerialPortEvent.DSR:
//                case SerialPortEvent.RI:
//                case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
//                    break;
                case SerialPortEvent.DATA_AVAILABLE:
                    byte[] readBuffer = new byte[26];
                    try {
                        while (in.available() > 0) {
                            int numBytes = in.read(readBuffer);
                        }
                        System.out.print("data from serialConnection: |" +new String(readBuffer)+"|");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
        }
    }

    private class StopSerConnTimerTask extends TimerTask{

    	private ServerSerial serConn;
    	private StopSerConnTimerTask(ServerSerial serConn){
    		this.serConn=serConn;
    	}
		public void run() {
			serConn.stopped=true;

		}

    }
}
