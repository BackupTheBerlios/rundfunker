package de.fhaugsburg.rundfunker.rundspieler.view;

public interface DisplayClient {

    public void setLine(int rowNum, String line);

    public void setLED(int ledNum);

    public void setCursorPos(int rowNum, int colNum);

    public void setCursorVisibility(boolean visible);

}
