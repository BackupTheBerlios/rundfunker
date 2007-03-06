package de.fhaugsburg.rundfunker.rundspieler.view;

public interface Display {

    public static final int OK = 6;

    public static final int RIGHT_ARROW = 0x7e;

    public static final int LEFT_ARROW = 0x7f;

//    public static final int SPACE_SPELLER = 222;
    public static final int SPACE_SPELLER = 0x07;

    public static final int JD_TURN_LEFT = 1;
    public static final int JD_TURN_RIGHT = -1;

//    public void setChar(int row, int col, char c);

    public void setLine(int rowNum, String row);

    public void setLED(int ledNum);

    public void setCursorPos(int rowNum, int colNum);

    public void setCursorVisibility(boolean visible);

//    public void setLines(String[] lines);

//    public void setCursor(int row, int col);

//    public void setCursorVisibility(boolean isVisible);

}
