package de.fhaugsburg.rundfunker.rundspieler.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.fhaugsburg.rundfunker.rundspieler.view.Display;
import de.fhaugsburg.rundfunker.rundspieler.view.ListScreen;
import de.fhaugsburg.rundfunker.rundsucher.Config;
import de.fhaugsburg.rundfunker.rundspieler.Controller;

public class ListScreenDBImpl extends ListScreen {

    protected Connection conn;

    protected ResultSet list;

    protected String sqlQuery;

    public ListScreenDBImpl(Controller controller, Display display) {
        try {
            setController(controller);
            setDisplay(display);
            conn = Config.getInstance().createCon();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void init() {
        try {
            Statement s = conn.createStatement();
            sqlQuery = "SELECT DISTINCT " + type
                    + " FROM rf_song WHERE NOT ISNULL(" + type
                    + ") AND LENGTH(" + type + ") <>0 ORDER BY " + type;

            list = s.executeQuery(sqlQuery);
            
            // berücksichtige aktuellen Song
            int counter = 0;
            cursorLine=0;
            while (list.next()) {
                if (type.equals(Controller.ALBUM)) {
                    if(startSong.getAlbum()==null||startSong.getAlbum().length()==0){
                        return;
                    }
                    if (list.getString(type).equals(startSong.getAlbum())) {
                        break;
                    }
                } else if (type.equals(Controller.ARTIST)) {
                    if(startSong.getArtist()==null||startSong.getArtist().length()==0){
                        return;
                    }
                    if (list.getString(type).equals(startSong.getArtist())) {
                        break;
                    }
                } else if (type.equals(Controller.GENRE)) {
                    if(startSong.getGenre()==null||startSong.getGenre().length()==0){
                        return;
                    }
                    if (list.getString(type).equals(startSong.getGenre())) {
                        break;
                    }
                } else if (type.equals(Controller.TITLE)) {
                    if(startSong.getTitle()==null||startSong.getTitle().length()==0){
                        return;
                    }
                    if (list.getString(type).equals(startSong.getTitle())) {
                        break;
                    }
                }

                counter++;
            }
            
            /* 
             * Berechnen der cursorline
             * für counter-werte >size-4  muss die cursorline erhöht werden,
             * damit keine leeren zeilen entstehen
             * Beispiel:
             * -------------------------
             * | w
             * | x
             * |>y
             * | z
             * -------------------------
             */
            String countSQL = "SELECT COUNT( DISTINCT " + type
            + ")as size FROM rf_song WHERE NOT ISNULL(" + type
            + ") AND LENGTH(" + type + ") <>0";

            Statement s2 = conn.createStatement();
            ResultSet rs = s2.executeQuery(countSQL);
            rs.next();
            int size = rs.getInt("size");
            cursorLine = Math.max(0, counter + 4 - size);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public String getSelectedItem() {
        String selectedItem = displayRows[cursorLine].getValue();
        selectedItem = selectedItem.substring(1, selectedItem.length());
        return selectedItem;
    }

    public void updateLines() {
        try {
            // Optimierung für Flash-Karte, da sql Anweisungen mit ORDER BY
            // auf dem Rundfunker unter Umständen zu langsam sind.
            for (int i = 0; i <= cursorLine; i++) {
                list.previous();

            }

            for (int i = 0; i < lineCount; i++) {
                if (list.next()) {
                    displayRows[i].setValue(" " + list.getString(type));
                }
            }

            for (int i = 0; i < lineCount - 1 - cursorLine; i++) {
                list.previous();
            }

            displayRows[cursorLine].setValue((char) Display.RIGHT_ARROW
                    + displayRows[cursorLine].toString().trim());

            // Statement s = conn.createStatement();
            // ResultSet rs = s.executeQuery(sqlQuery + " LIMIT " + startIndex +
            // ", " + lineCount);
            //
            // for (int i = 0; (i < lineCount) && rs.next(); i++) {
            // displayRows[i].setValue(" " + rs.getString(type));
            // }
            // displayRows[cursorLine].setValue((char) Display.RIGHT_ARROW +
            // displayRows[cursorLine].toString().
            // trim());
            // rs.close();
            // s.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public boolean prevItem() {
        try {
            return list.previous();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

    }

    public boolean nextItem() {
        try {
            return list.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
