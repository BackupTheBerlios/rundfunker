package de.fhaugsburg.rundfunker.rundsucher;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Stack;
import java.sql.Connection;

/**
 * <p>Title: DBThread</p>
 *
 * <p>Description: Legt die Daten aus dem Song-Stack in der Datenbank ab.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Christian Leberfinger
 * @version 1.0
 */
public class DBThread extends Thread {

    /* Stack, aus dem der Thread die Daten bezieht */
    private Stack songs;

    /* Datenbankverbindung */
    private Connection conn;
    private PreparedStatement pstmtInsert;
    private PreparedStatement pstmtUpdate;

    /* Stop-Flag */
    public boolean stopped = false;

    public DBThread(Stack songs, Connection conn) {
        this.songs = songs;
        this.conn = conn;

        try {
            pstmtInsert = conn.prepareStatement(
                    "INSERT INTO rf_song SET artist = ?, album = ?, "
                    + "title = ?, songSeconds = ?, genre = ?, filename = ?, "
                    + "fileLastModified = ?, readDurationMillis = ?, "
                    + "errors = ?, filesize = ?, tracknumber = ?, filenameHash = ?");

            pstmtUpdate = conn.prepareStatement(
                    "UPDATE rf_song SET artist = ?, album = ?, "
                    + "title = ?, songSeconds = ?, genre = ?, filename = ?, "
                    + "fileLastModified = ?, readDurationMillis = ?, "
                    + "errors = ?, filesize = ?, tracknumber = ? WHERE filenameHash = ?");

        } catch (SQLException ex) {
            System.err.println(
                    "Fehler beim Anlegen der PreparedStatements in DBThread: " +
                    ex.getMessage());
        }

    }

    /**
     * Wird von der Methode start() aufgerufen und arbeitet den Song-Stack ab,
     * solange das Stop-Flag "stopped" nicht auf true gesetzt wurde. Um diesen
     * Thread zu starten, rufen Sie bitte die Methode start() auf, die von der
     * Klasse Thread geerbt wurde.
     */
    public void run() {
        while (!stopped) {
            while (songs.size() > 0) {
                Song s = (Song) songs.pop();
                addToDataBase(s);
            }

            /* Ist der Songs-Stack leer, so wartet der Thread ein wenig */
            try {
                sleep(2000);
            } catch (InterruptedException ex) {
                //System.out.println("DBThread interrupted.");
            }
        }

        /* Datenbankverbindung schließen */
        try {
            conn.close();
        } catch (SQLException ex) {}

        System.out.println("DBThread stopped.");
    }

    /**
     * Fügt den übergebenen Song in die Datenbank ein
     * @param s Song
     */
    public void addToDataBase(Song s) {

        if (null == s) {
            return;
        }
        if (0 == s.getFilenameHash()) {
            return;
        }

        /* aktuelle Version bereits in der DB */
        if (s.isDbEntryUpToDate()) {
//            System.out.println("schon aktuell: "+s.getFilename());
            return;
        }

        /* INSERT oder UPDATE? */
        PreparedStatement stmt = null;
        if(s.isAlreadyInDB())
        {
            stmt = pstmtUpdate;
//            System.err.println("UPDATE: "+s.getFilename());
        }
        else
        {
            stmt = pstmtInsert;
//            System.out.println("INSERT: "+s.getFilename());
        }

        try {

            stmt.setString(1, s.getArtist());
            stmt.setString(2, s.getAlbum());
            stmt.setString(3, s.getTitle());
            stmt.setInt(4, s.getLengthSeconds());
            stmt.setString(5, s.getGenre());
            stmt.setString(6, s.getFilename());
            stmt.setLong(7, s.getFileLastModified());
            stmt.setInt(8, s.getReadDurationMillis());
            stmt.setString(9, s.getErrors());
            stmt.setLong(10, s.getFilesize());
            stmt.setInt(11, s.getTracknumber());
            stmt.setLong(12, s.getFilenameHash());

            stmt.executeUpdate();

        } catch (SQLException ex) {
            System.err.println(s + " : " + ex.getMessage());
        }
    }

}
