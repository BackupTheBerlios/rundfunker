package de.fhaugsburg.rundfunker.rundspieler.view;

import de.fhaugsburg.rundfunker.rundspieler.SearchFilter;
import de.fhaugsburg.rundfunker.rundspieler.SearchFilterImpl;
import de.fhaugsburg.rundfunker.rundsucher.Song;

public abstract class ListScreen extends AbstractScreen {

    protected Song startSong;

    protected int cursorLine;

    protected String type;


    public void init(String type, Song startSong) {
        this.startSong = startSong;
        cursorLine=0;
        this.type=type;
        init();
        updateLines();
        super.render();
    }

    public void turnDDS(int value) {
        if (value == Display.JD_TURN_RIGHT) {
            turnDDSRight();
        } else if (value == Display.JD_TURN_LEFT) {
            turnDDSLeft();
        }

        super.render();

    }

    public void turnDDSRight() {
        if (nextItem()) {
            cursorLine++;
            if (cursorLine >= lineCount) {
                cursorLine = lineCount - 1;
            }
        } else {
            prevItem();
        }
    }

    public void turnDDSLeft() {
        if (prevItem()) {
            cursorLine--;
            if (cursorLine < 0) {
                cursorLine = 0;
            }
        } else {
            nextItem();
        }
    }


    public abstract boolean prevItem();

    public abstract boolean nextItem();

    public abstract void init();


    public void pressDDS() {
        SearchFilter sf = new SearchFilterImpl();
        sf.setFilter(type);
        sf.setSearchString(getSelectedItem());
        controller.play(sf);
    }

    public abstract String getSelectedItem();


    public void render() {

    }

}
