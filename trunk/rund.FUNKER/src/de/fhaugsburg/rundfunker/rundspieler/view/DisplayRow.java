package de.fhaugsburg.rundfunker.rundspieler.view;

import de.fhaugsburg.rundfunker.rundsucher.Config;

public class DisplayRow {

    private String value;

    private int offset = 0; // für Lauftext

    public static int MAXCOLNUM = 20;

    public static boolean UPPERCASE = true;

    static {
        Config con = Config.getInstance();
        UPPERCASE = con.getProperty("displayAllUppercase").equalsIgnoreCase(
                "true");
        MAXCOLNUM = Integer.parseInt(con.getProperty("displayColCount"));
    }

    public DisplayRow() {
        this.value = " ";
    }

    public DisplayRow(String value) {
        setValue(value);
    }

    public void setValue(String value) {
        if (value == null) {
            this.value = " ";
        } else {
            this.value = value;
        }
        if (UPPERCASE) {
            this.value = this.value.toUpperCase();
        }
        if (isTooLong()) {
            //Erweitere Zeile für Lauftext
            this.value += " *** ";
        }
        offset=0;
    }

    public String getValue() {
        return value;
    }

    public int getLength() {
        return value.length();
    }

    public boolean isTooLong() {
        return value.length() > MAXCOLNUM;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        if (offset >= getLength()) {
            this.offset = 0;
        } else {
            this.offset = offset;
        }
    }

    public String toString() {
        if (value == null) {
            return "";
        }
        if (value.length() > MAXCOLNUM) {
            String view = value + value;
            view = view.substring(offset, offset + MAXCOLNUM);
            return view;
        } else {
            return value;
        }
    }

}
