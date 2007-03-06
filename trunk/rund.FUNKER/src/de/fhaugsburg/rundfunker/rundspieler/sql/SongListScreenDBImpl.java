package de.fhaugsburg.rundfunker.rundspieler.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.fhaugsburg.rundfunker.rundspieler.Controller;
import de.fhaugsburg.rundfunker.rundspieler.view.Display;

/**
 * Klasse zum Auswählen des nächsten Liedes, beim Drehen des DDS im play-Modus.
 * 
 * @author cbaoth
 * 
 */
public class SongListScreenDBImpl extends ListScreenDBImpl {
    private int delta=0;
    private int size=0;
    private int index=0;

    public SongListScreenDBImpl(Controller controller, Display display) {
        super(controller, display);
    }

    public void init() {
        String firstorderBy=type;
        type="title";
        size=0;
        delta=0;
        index=0;
        try {
            Statement s = conn.createStatement();
            sqlQuery = "SELECT title,filenameHash FROM rf_song ORDER BY " + firstorderBy+", "+PlaylistDBImpl.ORDERBY;

            list = s.executeQuery(sqlQuery);

            // berücksichtige aktuellen Song
            cursorLine = 0;
            while (list.next()) {
                if(list.getLong("filenameHash")==startSong.getFilenameHash()){
                    break;
                }
                index++;
            }

            /*
             * Berechnen der cursorline für counter-werte >size-4 muss die
             * cursorline erhöht werden, damit keine leeren zeilen entstehen
             * Beispiel: ------------------------- | w | x |>y | z
             * -------------------------
             */
            String countSQL = "SELECT COUNT( title )as size FROM rf_song";

            Statement s2 = conn.createStatement();
            ResultSet rs = s2.executeQuery(countSQL);
            rs.next();
            size = rs.getInt("size");
            cursorLine = Math.max(0, index + 4 - size);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
    
    public void turnDDSLeft(){
        super.turnDDSLeft();
        if(delta-1+index>=0){
            delta--;
        }
    }
    
    public void turnDDSRight(){
        super.turnDDSRight();
        if(delta+1+index<size){
            delta++;
        }
    }
    
    public void pressDDS() {
        controller.deltaSong(delta);
    }

}
