package de.fhaugsburg.rundfunker.rundspieler.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.ListModel;

public class DisplayClientSwing implements DisplayClient {

    private JList jList;

    private String[] lines;

    private int maxRowCount = 4;

    private int maxColCount = 20;

    private boolean cursorVisible = false;

    private int cursorRow = 0;

    private int cursorCol = 0;

    public DisplayClientSwing(JList jList, int colNum) {
        // this.frame=frame;
        this.jList = jList;
        lines = new String[maxRowCount];
        ListModel jList1Model = new DefaultComboBoxModel(new String[colNum]);
        jList.setModel(jList1Model);
        jList.setFont(new Font("Monospaced", 1, 12));
    }

//	public void setChar(int row, int col, char c) {
//		// TODO Auto-generated method stub
//	}

    private String replaceSpecialChars(String s) {
        s = s.replace((char) Display.RIGHT_ARROW, '>');
        s = s.replace((char) Display.OK, '#');
        s = s.replace((char) Display.LEFT_ARROW, '-');
        s = s.replace((char) Display.SPACE_SPELLER, ' ');
        return s;
    }

    public void setLine(int rowNum, String row) {

        lines[rowNum] = replaceSpecialChars(row);
        if (cursorVisible && rowNum == cursorRow) {
            StringBuffer charChooser = new StringBuffer(lines[cursorRow]);
            charChooser.insert(cursorCol, "_");
            charChooser.insert(cursorCol + 2, "_");
            lines[cursorRow] = charChooser.toString();
        }
        drawLines();
    }

    public void drawLines() {
//		this.lines = lines;
        jList.setListData(lines);

        // sehr unschön, aber für testzwecke muss der screen sofort
        // neugezeichnet werden.
        // sonst wird der repaint() zu spät aufgerufen.
        jList.paint(jList.getGraphics());

    }

    public void setCursorVisibility(boolean isVisible) {
        cursorVisible = isVisible;
    }

    public void setLED(int ledNum) {
        // betrifft Swing-Implementierung nicht
    }

    public void setCursorPos(int rowNum, int colNum) {
        cursorCol = colNum;
        cursorRow = rowNum;
    }
}
