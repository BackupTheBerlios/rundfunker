package de.fhaugsburg.rundfunker.rundspieler.view;

import de.fhaugsburg.rundfunker.rundspieler.Controller;

public class ErrorScreen extends AbstractScreen {

    private String message;

    public ErrorScreen(Controller controller, Display display) {
        super(controller, display);
        lineNumsToRender = new int[] {0,1,2,3};
    }

    public void setMessage(String message) {
        this.message = message;
        String s = message;
        for (int i = 0; i < displayRows.length; i++) {
            if (s.length() > DisplayRow.MAXCOLNUM) {
                displayRows[i].setValue(s.substring(0,
                        DisplayRow.MAXCOLNUM));
                s = s.substring(DisplayRow.MAXCOLNUM, s.length());
            } else {
                displayRows[i].setValue(" ");
            }
        }

    }

    public void turnDDS(int value) {
        // beim ErrorScreen irrelevant
    }

    public void pressDDS() {
        // beim ErrorScreen irrelevant
    }

    public void updateLines() {
        // beim ErrorScreen irrelevant
    }

}
