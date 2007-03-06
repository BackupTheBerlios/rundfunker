package de.fhaugsburg.rundfunker.rundsucher;

import java.io.*;
import java.sql.*;

/**
 * <p>Description: Repräsentation eines Liedes</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Christian Leberfinger
 * @version 1.0
 */
public class Song {

    private String title;
    private String artist;
    private String filename;
    private String genre;
    private String album;
    private int tracknumber = 0;
    private int readDurationMillis;
    private int lengthSeconds;
    private String errors;
    private long filenameHash = 0;
    private long fileLastModified = 0;
    private boolean alreadyInDB = false;
    private boolean dbEntryUpToDate = false;
    private long filesize = 0;

    /**
     * Standard-Konstruktor
     */
    public Song() {}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getFilename() {
        return filename;
    }

    public String toString() {
        return "Song (" + getFilename() + ") " + getTitle() + " from " +
                getArtist();
    }

    public int getLengthSeconds() {
        return lengthSeconds;
    }

    public void setLengthSeconds(int lengthSeconds) {
        this.lengthSeconds = lengthSeconds;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getReadDurationMillis() {
        return readDurationMillis;
    }

    public void setReadDurationMillis(int readDurationMillis) {
        this.readDurationMillis = readDurationMillis;
    }

    public String getErrors() {
        return errors;
    }

    public long getFilenameHash() {
        return filenameHash;
    }

    public long getFileLastModified() {
        return fileLastModified;
    }

    public boolean isAlreadyInDB() {
        return alreadyInDB;
    }

    public boolean isDbEntryUpToDate() {
        return dbEntryUpToDate;
    }

    public long getFilesize() {
        return filesize;
    }

    public int getTracknumber() {
        return tracknumber;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public void setFilenameHash(long filenameHash) {
        this.filenameHash = filenameHash;
    }

    public void setFileLastModified(long fileLastModified) {
        this.fileLastModified = fileLastModified;
    }

    public void setAlreadyInDB(boolean alreadyInDB) {
        this.alreadyInDB = alreadyInDB;
    }

    public void setDbEntryUpToDate(boolean dbEntryUpToDate) {
        this.dbEntryUpToDate = dbEntryUpToDate;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    /**
     * Versucht, aus dem übergebenen String, z.B.: "   20/ 30" die relevante
     * Zahl "20" zu extrahieren. Leider kommen solche Strings in ID3-Tags
     * manchmal vor.
     * @param tracknumberString String
     */
    public void setTracknumber(String tracknumberString) {
        setTracknumber(extractTracknumberFast(tracknumberString));
    }

    private int extractTracknumberFast(String tracknumberString) {
        int number = 0;
        tracknumberString = tracknumberString.trim();
        int startindex = Integer.MAX_VALUE;
        int endindex = Integer.MIN_VALUE;
        boolean reachedfirstDigit = false;

        for (int i = 0; i < tracknumberString.length(); i++) {
            char aktChar = tracknumberString.charAt(i);
            if (Character.isDigit(aktChar)) {
                reachedfirstDigit = true;
                endindex = Math.max(endindex, i);
                startindex = Math.min(startindex, i);
            } else {
                if (reachedfirstDigit) {
                    break;
                }
            }
        }
        try {
            number = Integer.parseInt(tracknumberString.substring(startindex,
                    Math.min(endindex + 1, tracknumberString.length())));
        } catch (NumberFormatException ex) {}
        return number;
    }

    /**
     * Die schönere Möglichkeit, die Aufgabe zu lösen; leider nur halb so schnell
     * @param s String
     * @return int
     */
    private int extractTracknumberPretty(String s) {
        int number = 0;
        StreamTokenizer st2 = new StreamTokenizer(new StringReader(s));
        try {
            while (st2.nextToken() != StreamTokenizer.TT_EOF) {
                if (st2.ttype == StreamTokenizer.TT_NUMBER) {
                    number = Math.abs((int) st2.nval);
                    break;
                }
            }
        } catch (IOException ex) {
        }
        return number;
    }

    public void setTracknumber(int tracknumber) {
        this.tracknumber = tracknumber;
    }

    /**
     * Führt einen Vergleich auf Basis des Filename-Hashs durch.
     * @param o Object
     * @return boolean
     */
    public boolean equals(Object o) {
        if (!(o instanceof Song)) {
            return false;
        }

        return ((Song) o).filenameHash == filenameHash;
    }

    public void updatePlayTimeStatistic(long secondsPlayed)
    {
        String sql = "UPDATE rf_song SET timesPlayed=timesPlayed+1, secondsPlayed=secondsPlayed+"+secondsPlayed+" WHERE filenameHash="+getFilenameHash()+";";
        Config conf = Config.getInstance();
        java.sql.Connection conn = null;
        try {
            conn = conf.createCon();
            Statement st = conn.createStatement();
//            System.out.println("Statistik-SQL: "+sql);
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException ex)
        {
            System.out.println("Fehler beim Aktualisieren der statistischen Informationen. ");
            ex.printStackTrace();
        } finally
        {
            if(conn!=null)
            {
                try {
                    conn.close();
                } catch (SQLException ex1) {
                }
            }
        }
    }

}
