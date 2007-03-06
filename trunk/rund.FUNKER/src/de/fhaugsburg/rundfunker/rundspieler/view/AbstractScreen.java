package de.fhaugsburg.rundfunker.rundspieler.view;

import java.util.HashSet;
import java.util.Set;

import de.fhaugsburg.rundfunker.rundspieler.Controller;

public abstract class AbstractScreen implements Screen {

    protected Controller controller;

    protected Display display;

    protected int lineCount = 4;

//    protected String[] currentLines;

    protected DisplayRow[] displayRows;

    protected int[] lineNumsToRender;

    public AbstractScreen(Controller controller, Display display) {
        this();
        setController(controller);
        setDisplay(display);
    }

    public AbstractScreen() {
        //        currentLines=new String[lineCount];
        displayRows = new DisplayRow[lineCount];
        lineNumsToRender = new int[lineCount];
        for (int i = 0; i < lineCount; i++) {
//            currentLines[i]="";
            displayRows[i] = new DisplayRow();
            lineNumsToRender[i] = i;
        }
    }

    public abstract void updateLines();

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public void render() {
        updateLines();
        int line;
        for (int i = 0; i < lineNumsToRender.length; i++) {
            line = lineNumsToRender[i];
            display.setLine(line, displayRows[line].toString());
        }

    }

}
