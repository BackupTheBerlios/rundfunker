package de.fhaugsburg.rundfunker.rundsucher;

import java.util.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import jcifs.smb.SmbFile;
import java.net.MalformedURLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Christian Leberfinger
 * @version 1.0
 */
public class RundSuche {

    private Stack foundFiles = new Stack();
    private Stack songs = new Stack();

    private Config conf = Config.getInstance();
    private Connection conn = null;
    private Connection conn2 = null;

    /* Vectoren, in denen die Threads abgelegt werden */
    private Vector fileSearchThreads = new Vector();
    private Vector metaDataThreads = new Vector();
    private Vector dbThreads = new Vector();

    /* Singleton */
    private static RundSuche rsinstanz = new RundSuche();

    public static RundSuche getInstance() {
        return rsinstanz;
    }

    private RundSuche() {
        try {
            conn = conf.createCon();
            /* Threads erstellen */
            initThreads();
        } catch (Exception ex) {
            System.err.println("Fehler beim Instantiieren der rund.SUCHE.");
        }
    }

    /**
     * Durchsucht den angegebenen Pfad nach abspielbaren Media-Files.
     * Dabei werden auch die Metadaten eingelesen und in der Datenbank abgelegt.
     * @param suchpfad Suchpfad-Objekt, das den Suchpfad im Attribut pfad beinhaltet.
     * @throws MalformedURLException
     */
    public void addSearchPath(Suchpfad suchpfad) throws
            MalformedURLException {
        /* FileSearch-Thread initialiseren und zur Threadliste hinzufügen */
        FileSearchThread fst = new FileSearchThread(foundFiles, suchpfad);
        fileSearchThreads.add(fst);

        System.out.println("Suchpfad hinzufügen: "+suchpfad);

        /* Neuen Thread starten */
        fst.start();
    }

    public synchronized void removeSearchPath(Suchpfad suchpfad)
    {
        /**@todo removeSearchPath Implementieren! */

        /* Durch FileSearchThreads laufen */
        Iterator it = fileSearchThreads.iterator();
        while(it.hasNext())
        {

        }

        /* Bearbeitet der FileSearchThread den entspr. Suchpfad? */

        /* FileSearchThread stoppen */

        /* FileSearchThread aus fst-Liste entfernen */
    }

    private int getSearchPathID(String searchPath) throws SQLException,
            MalformedURLException {

        int spID = 0;

        SmbFile searchFile = new SmbFile(searchPath);
        long pathHash = searchFile.hashCode();

        Connection c = conf.createCon();
        Statement s = c.createStatement();

        ResultSet rs = s.executeQuery("SELECT Id FROM rf_setter_suchpfade WHERE smbHashCode="+pathHash);

        /* Eintrag bereits in der Datenbank */
        if(rs.next())
        {
            spID = rs.getInt(1);
        }
        /* Für den Suchpfad muss erst noch ein Hashcode eingetragen werden */
        else
        {
            s.execute("UPDATE rf_setter_suchpfade SET smbHashCode="+pathHash+" WHERE pfad="+searchPath+";");

            ResultSet keys = s.getGeneratedKeys();
            while(keys.next())
            {
                int key = keys.getInt(1);
                System.err.println("updatekey: "+key);
            }
        }

        /* Setze Pfad auf 'WirdGeradeDurchsucht'
         * Dabei wird auch der TimeStamp aktualisiert */
        s.execute("UPDATE rf_setter_suchpfade SET wirdGeradeDurchsucht=1 WHERE Id="+spID+";");

        /* Datenbankverbindung schließen */
        c.close();

        return spID;
    }

    /**
     * Initialisiert die MetadataThreads (die MP3-Files auf ID3Tags untersuchen),
     * einen WatcherThread, der tote Threads killt und gegen neue ersetzt, sowie
     * einen DatenbankThread, der die gefundenen Metadaten in die Datenbank schreibt.
     */
    private void initThreads() {

        /* Aufpasser-Thread mit eingestellen Optionen initialisieren und starten
         * Ist kein Timeout angegeben, werden als Defaultwert 30 s angenommen.*/
        int metaTimeOutMillis = Integer.parseInt(conf.getProperty(
                "metaDataThreadTimeOutMillis", "30000"));
        WatcherThread wt = new WatcherThread(this, metaDataThreads,
                                             metaTimeOutMillis);
        wt.start();

        /* Gewünschte Anzahl an MetadataThreads einrichten (Default = 5) */
        int metaThreads = Integer.parseInt(conf.getProperty("metaDataThreads",
                "5"));

        for (int i = 0; i < metaThreads; i++) {
            newMetaDataThread();
        }

        /* DBThread initialisieren */
        DBThread dbt = new DBThread(songs, conn);
        dbt.start();

        dbThreads.add(dbt);
    }

    /**
     * Erzeugt einen neuen MetaDataThread, der nach MetaDaten in den gefundenen
     * MP3-Files sucht.
     */
    public void newMetaDataThread() {
        MetaDataThread mdt = null;
        try {
            mdt = new MetaDataThread(foundFiles, songs);
            mdt.start();
            metaDataThreads.add(mdt);
        } catch (SQLException ex) {
            System.err.println("Fehler beim Erstellen eine DBConn für einen MDT: "+ex.getMessage());
        }
    }

    /**
     * Teilt allen Threads mit, dass sie nach der aktuellen Operation ihre
     * Aktivität einstellen sollen.
     */
    public void stopThreads() {

        /* FileSearchThreads stoppen */
        Iterator it = fileSearchThreads.iterator();
        while (it.hasNext()) {
            ((FileSearchThread) it.next()).stopped = true;
        }

        /* MetaDataThreads stoppen */
        it = metaDataThreads.iterator();
        while (it.hasNext()) {
            ((MetaDataThread) it.next()).stopped = true;
        }

        /* DBThreads stoppen */
        it = dbThreads.iterator();
        while (it.hasNext()) {
            ((DBThread) it.next()).stopped = true;
        }
    }



    /**
     * Fügt ein File zum Stack hinzu, das weiterverarbeitet werden soll
     * @param f File
     */
    public void addFoundFile(File f) {

        foundFiles.push(f);
    }

    /**
     * Fügt einen Song hinzu, der vom DBThread in die Datenbank eingefügt werden soll
     * @param s Song
     */
    public void addSong(Song s) {
        songs.push(s);
    }


}
