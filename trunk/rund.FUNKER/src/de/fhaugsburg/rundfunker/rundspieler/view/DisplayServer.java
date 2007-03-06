package de.fhaugsburg.rundfunker.rundspieler.view;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * 
 * <p>Description: Koordiniert die Ausgaben verschiedener Displays. Displays
 * können sich am DisplayServer registrieren, um bei Änderungen benachrichtigt
 * zu werden.</p> 
 * 
 * <p>Copyright: Copyright (c) 2005</p> 
 * 
 * <p>Company: AG RundFunker</p>
 * 
 * @author Christoph Beckmann
 * @version 1.0
 */
public class DisplayServer implements Display {

    private Vector clients;
    private String[] lines;
    private int ledNUM = 0;
    private int cursorCol = 0;
    private int cursorRow = 0;

    private boolean cursorVisible = false;

    public DisplayServer() {
        clients = new Vector();
        lines = new String[] {" ", " ", " ", " "};
    }

    public void setLine(int rowNum, String row) {
        lines[rowNum] = row;
        Enumeration enumeration= clients();
        while (enumeration.hasMoreElements()) {
            ((DisplayClient) enumeration.nextElement()).setLine(rowNum, row);
        }
    }

    public void setLED(int ledNum) {
        this.ledNUM = ledNum;
        Enumeration enumeration= clients();
        while (enumeration.hasMoreElements()) {
            ((DisplayClient) enumeration.nextElement()).setLED(ledNum);
        }
    }

    public synchronized void addDisplayListener(DisplayClient dl) {
        clients.add(dl);
        for (int i = 0; i < lines.length; i++) {
            dl.setLine(i, lines[i]);
        }
        dl.setLED(ledNUM);
        dl.setCursorPos(cursorRow, cursorCol);
        dl.setCursorVisibility(cursorVisible);

    }

    public void removeDisplayListener(DisplayClient dl) {
        clients.remove(dl);
    }

    public void setCursorPos(int rowNum, int colNum) {
        cursorCol = colNum;
        cursorRow = rowNum;
        Enumeration enumeration= clients();
        while (enumeration.hasMoreElements()) {
            ((DisplayClient) enumeration.nextElement()).setCursorPos(rowNum, colNum);
        }
    }

    public void setCursorVisibility(boolean visible) {
        cursorVisible = visible;
        Enumeration enumeration= clients();
        while (enumeration.hasMoreElements()) {
            ((DisplayClient) enumeration.nextElement()).setCursorVisibility(visible);
        }
    }
    
    public Enumeration clients(){
    	return ((Vector) clients.clone()).elements(); 
    }

}
