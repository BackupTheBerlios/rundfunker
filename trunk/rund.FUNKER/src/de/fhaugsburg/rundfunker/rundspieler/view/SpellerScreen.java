package de.fhaugsburg.rundfunker.rundspieler.view;

import de.fhaugsburg.rundfunker.rundspieler.SearchFilter;
import de.fhaugsburg.rundfunker.rundspieler.SearchFilterImpl;
import de.fhaugsburg.rundfunker.rundspieler.Controller;

public abstract class SpellerScreen extends AbstractScreen {

    public SpellerScreen(Controller controller, Display display) {
        super(controller, display);
    }

    protected String inputChars;

    protected int resultCount;

    protected String filterType;

    protected char[] availableChars;

    protected int currentCharIndex;

    private int cursorCol;

    public void init(String filterType) {
        this.filterType = filterType;
        inputChars = "";
        availableChars = getAvailableChars();
        resultCount = getResultCount();
        currentCharIndex = 0;
        cursorCol = 0;
        lineNumsToRender = new int[] { 0, 1, 2, 3 };
        display.setCursorVisibility(true);
        display.setCursorPos(2, currentCharIndex);
    }

    public void turnDDS(int value) {
        if (value == Display.JD_TURN_RIGHT) {
            turnDDSRight();
        } else if (value == Display.JD_TURN_LEFT) {
            turnDDSLeft();
        }
        display.setCursorPos(2, cursorCol);
        lineNumsToRender = new int[] { 2, 3 };
        render();
    }

    public void turnDDSRight() {
        if (++currentCharIndex < availableChars.length) {
            cursorCol++;
            if (cursorCol >= DisplayRow.MAXCOLNUM) {
                cursorCol = DisplayRow.MAXCOLNUM - 1;
                displayRows[2].setOffset(currentCharIndex + 1
                        - DisplayRow.MAXCOLNUM);
            }
        } else {
            cursorCol = 0;
            currentCharIndex = 0;
            displayRows[2].setOffset(0);
        }
    }

    public void turnDDSLeft() {
        if (--currentCharIndex >= 0) {
            cursorCol--;
            if (cursorCol < 0) {
                cursorCol = 0;
                displayRows[2].setOffset(currentCharIndex);
            }
        } else {
            cursorCol = Math.min(availableChars.length - 1,
                    DisplayRow.MAXCOLNUM - 1);
            currentCharIndex = availableChars.length - 1;
            displayRows[2].setOffset(Math.max(availableChars.length
                    - DisplayRow.MAXCOLNUM, 0));
        }

    }

    public void pressDDS() {
        int selectedCharacter = availableChars[currentCharIndex];
        switch (selectedCharacter) {
        case Display.OK:
            SearchFilter sf = new SearchFilterImpl();
            sf.setFilter(filterType);
            sf.setSearchString(inputChars);
            controller.play(sf);
            display.setCursorVisibility(false);
            return;
        case Display.LEFT_ARROW:
            if (inputChars.length() > 0) {
                inputChars = inputChars.substring(0, inputChars.length() - 1);
            }
            break;
        case Display.SPACE_SPELLER:
            inputChars += " ";
            break;
        default:
            inputChars += (char) selectedCharacter;
        }
        availableChars = getAvailableChars();
        cursorCol = 0;
        currentCharIndex = 0;
        display.setCursorPos(2, cursorCol);
        displayRows[2].setOffset(0);
        lineNumsToRender = new int[] { 1, 2, 3 };
        render();
    }

    public abstract char[] getAvailableChars();

    public int getResultCount() {
        return getResultCount("");
    }

    public abstract int getResultCount(String nextChar);

    public void updateLines() {

        displayRows[0].setValue("suche: " + filterType);
        displayRows[1].setValue("abc: " + inputChars);

        displayRows[2].setValue(new String(availableChars));
        displayRows[3].setValue("results: " + resultCount);

    }

    public void render() {
        super.render();
        lineNumsToRender = new int[0];
    }

}
