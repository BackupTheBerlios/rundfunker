package de.fhaugsburg.rundfunker.rundsucher;

import java.io.*;
import java.sql.*;
import java.util.*;

import de.vdheide.mp3.*;

/**
 * <p>Title: MetaDataThread</p>
 *
 * <p>Description: Liest Meta-Daten ein.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Christian Leberfinger
 * @version 1.0
 */
public class MetaDataThread extends Thread {

    /* Stack der vom FileSearchThread gefundenen Dateien */
    private Stack foundFiles;

    /* Stack, auf dem eingelesene Songs gelegt werden */
    private Stack songs;

    /* TimeStamp für Performance-Analysen */
    private long lastStartTimestamp = Long.MAX_VALUE;

    /* Das File, das gerade verarbeitet wird */
    private String aktFile = null;
    private boolean isAktFileInDB = false;
    private boolean isAktFileAktuell = false;

    /* Stop-Flag */
    public boolean stopped = false;

    private Connection conn;
    private PreparedStatement pstmtDBLastMod;
    private Config conf = Config.getInstance();
    private static Properties genres = new Properties();

    static {
        try {
            genres.load(new FileInputStream("genres.properties"));
        } catch (IOException ex) {
            System.err.println("Fehler beim Laden der Genres.");
        }
    }

    public MetaDataThread(Stack foundFiles, Stack songs) throws SQLException {
        this.conn = conf.createCon();
        this.foundFiles = foundFiles;
        this.songs = songs;
        this.conn = conn;
        try {
            pstmtDBLastMod = conn.prepareStatement(
                    "SELECT fileLastModified, filesize FROM rf_song WHERE filenameHash = ?");
        } catch (SQLException ex) {
        }
    }

    /**
     * Wird von der Methode start() aufgerufen und arbeitet den foundFiles-Stack
     * ab, solange das Stop-Flag "stopped" nicht auf true gesetzt wurde. Die
     * gefundenen Metadaten werden in Song-Objekte verpackt und auf den Song-Stack
     * gelegt. Um diesen Thread zu starten, rufen Sie bitte die Methode start() auf, die von der
     * Klasse Thread geerbt wurde.
     */
    public void run() {
        while (!stopped) {

            //evtl. synchronized-block um size()-abfrage und pop()???
            while (foundFiles.size() > 0) {

                Song aktSong = (Song) foundFiles.pop();

                if (aktSong == null) {
                    continue;
                }

                getMetaData(aktSong);

                songs.push(aktSong);
            }

            //System.out.println(this.getName()+": FoundFiles-Stack leer. Alle Files verarbeitet.");


            try {
                sleep(500);
            } catch (InterruptedException ex) {
                System.out.println("MetaData-Thread: Interrupted");
            }
        }

        /* Datenbankverbindung schließen */
        try {
            conn.close();
        } catch (SQLException ex) {}

        System.out.println("MetaDataThread stopped.");
    }

    /**
     * Liest die Metadaten der angegebenen Datei ein und gibt ein Song-Objekt
     * zurück. Zum Einlesen der ID3-Tags wird die ID3Lib von Jens Vonderheide
     * (jens@vdheide.de) benützt, die ich nur leicht modifiziert habe.
     * Aufgetretene Fehler logge ich über das Song-Objekt in die Datenbank.
     * @param s Song
     * @return Song
     */
    public Song getMetaData(Song s) {

        lastStartTimestamp = System.currentTimeMillis();

//        Song s = new Song();
//        s.setFilename(file);

        StringBuffer errors = new StringBuffer();

        // Meta-Daten auslesen
        try {

            MP3FileSamba mp3file = new MP3FileSamba(s.getFilename());

            /* Prüfen ob derselbe Eintrag schon in der DB vorliegt */
            long hash = mp3file.hashCode();
            long fileLastMod = mp3file.lastModified();
            long filesize = mp3file.length();

            isAktuellInDB(hash, fileLastMod, filesize);

            /* Eintrag liegt bereits in der DB vor */
            s.setAlreadyInDB(isAktFileInDB);

            /* Eintrag ist aktuell */
            if (isAktFileAktuell) {
                s.setDbEntryUpToDate(true);
                /* Meta-Daten müssen nicht mehr eingelesen werden */
                return s;
            } else {
                s.setDbEntryUpToDate(false);
            }

            s.setFileLastModified(fileLastMod);
            s.setFilenameHash(hash);
            s.setFilesize(filesize);

            String data = null;

            try {
                data = mp3file.getArtist().getTextContent();
                if(data!=null)
                    s.setArtist(data.trim());
            } catch (FrameDamagedException ex) {
                errors.append("FrameDamaged (Artist) |");
            }
            try {
                data = mp3file.getTitle().getTextContent();
                if(data!=null)
                    data = data.trim();

                /* Ist kein ID3-Tag vorhanden, soll als Title der Dateiname
                 * (ohne Fileextension) eingetragen werden */
                if(data==null || data.length()<1)
                {
                    data = mp3file.getName();
                    data = data.substring(0, data.length() -4);
                }
                s.setTitle(data);
            } catch (FrameDamagedException ex) {
                s.setTitle(mp3file.getName().substring(0,-4));
                errors.append("FrameDamaged (Title) |");
            }

            try {
                data = mp3file.getAlbum().getTextContent();
                if(data!=null)
                    s.setAlbum(data.trim());
            } catch (FrameDamagedException ex) {
                errors.append("FrameDamaged (Album) |");
            }

            try {
                // Genre
                String genreString = mp3file.getGenre().getTextContent();
                String genre = convertGenre(genreString);
                s.setGenre(genre);
            } catch (FrameDamagedException ex) {
                errors.append("FrameDamaged (Genre) |");
            }

            try {
                // Tracknumber
                String tracknumber = mp3file.getTrack().getTextContent();
                s.setTracknumber(tracknumber);
            } catch (FrameDamagedException ex1) {
                errors.append("FrameDamaged (Tracknumber) |");
            }

            try {
                // Länge in Sekunden
                s.setLengthSeconds((int) mp3file.getLength());
            } catch (Throwable t) {
                errors.append(t.getCause() + " (getLength) |");
            }

        } catch (ID3v2WrongCRCException e) {
            errors.append("ID3v2WrongCRC | ");
        } catch (ID3v2DecompressionException e) {
            errors.append("ID3v2DecompressionException | ");
            errors.append(e.getStackTrace()[1].toString());
            errors.append(" | ");
        } catch (ID3v2IllegalVersionException e) {
            errors.append("ID3v2IllegalVersionException | ");
        } catch (IOException e) {
            errors.append("IOException | ");
        } catch (NoMP3FrameException e) {
            errors.append("NoMP3FrameException | ");
        } catch (Throwable t) {
            errors.append("RuntimeException: ");
            errors.append(t.getMessage());
            errors.append(" | ");
            StackTraceElement[] stacktrace = t.getStackTrace();
            for (int i = 0; i < stacktrace.length; i++) {
                errors.append(stacktrace[i].toString());
                errors.append(" | ");
            }
        }

        /* Dauer des Einlesevorgangs speichern */
        s.setReadDurationMillis((int) (System.currentTimeMillis() -
                                       lastStartTimestamp));

        /* Aufgetretene Fehler speichern */
        if (errors.length() > 0) {
            s.setErrors(errors.toString());
        }

        return s;
    }

    public String getAktFileString() {
        return aktFile;
    }

    public long getLastStartTimestamp() {
        return lastStartTimestamp;
    }

    /**
     * Prüft ob eine Datei schon in der Datenbank vorhanden ist und ob
     * sie dort bereits mit den aktuellen Daten eingetragen ist. Diese
     * Entscheidung wird aufgrund der Dateigröße und des Datei-Timestamps
     * getroffen.
     * @param filenameHash long Hash der Datei, die überprüft werden
     * @param fileLastModified long Timestamp der Datei
     * @param filesize long Dateigröße
     */
    public void isAktuellInDB(long filenameHash, long fileLastModified,
                              long filesize) {

        isAktFileAktuell = false;
        isAktFileInDB = false;

        try {

            /* filesize und fileLastModified für die Datei aus Datenbank holen */
            pstmtDBLastMod.setLong(1, filenameHash);
            ResultSet rs = pstmtDBLastMod.executeQuery();

            /* ist die Datei schon in der Datenbank abgelegt? */
            if (rs.next()) {

                isAktFileInDB = true;

                long fileDBLastModified = rs.getLong(1);
                long fileDBSize = rs.getLong(2);

                if ((fileDBLastModified == fileLastModified) &&
                    (fileDBSize == filesize)) {
                    isAktFileAktuell = true;
                }
            }
            /* File noch nicht in der Datenbank */
            else {
                isAktFileInDB = false;
            }
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
            isAktFileInDB = false;
        }
    }

    /**
     * Konvertiert eine eventuell vorhandene ID3v1-Genrenummer in die textuelle
     * Darstellung wie bei ID3v2-Tags.
     * @param string String
     * @return String
     * @throws IOException
     */
    public String convertGenre(String string) throws IOException {

        if(string==null)
            return null;

        if(string.length()<2)
            return null;

        string = string.trim();

        /* nur Strings mit Format (xxx) verarbeiten */
        if (!string.startsWith("(") || !string.endsWith(")") ) {
            return string;
        }

        try {
            string = string.substring(1, string.length()-2);
            return genres.getProperty(string);
        } catch (Throwable ex) {
            System.out.println("Fehler bei: "+string);
        }

        return null;
    }

}
