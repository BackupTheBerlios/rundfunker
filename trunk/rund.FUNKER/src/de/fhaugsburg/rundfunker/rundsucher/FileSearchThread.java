package de.fhaugsburg.rundfunker.rundsucher;

import java.util.Stack;
import java.net.MalformedURLException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * <p>Title: FileSearchThread</p>
 *
 * <p>Description: Durchsucht den angegebenen Pfad nach MP3-Files und pusht
 * gefundene Dateien auf den übergebenen Stack.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Christian Leberfinger
 * @version 1.0
 */
public class FileSearchThread extends Thread {

    /* Stack, auf dem die gefundenen Files liegen */
    private Stack foundFiles;

    /* interner Counter, für statistische Zwecke o.ä. */
    private int filecounter = 0;

    /* Pfad, in dem der Thread sucht */
    private final SmbFile searchRoot;

    private final Suchpfad suchpfad;

    /* Stop-Flag */
    public boolean stopped = false;

    public FileSearchThread(Stack foundFiles, Suchpfad suchpfad) throws
            MalformedURLException {
        this.foundFiles = foundFiles;
        this.suchpfad = suchpfad;
        this.searchRoot = new SmbFile(suchpfad.getPfad());
    }

    /**
     * Wird von der Methode start() aufgerufen und arbeitet den Pfad rekursiv ab,
     * solange das Stop-Flag "stopped" nicht auf true gesetzt wurde. Um diesen
     * Thread zu starten, rufen Sie bitte die Methode start() auf, die von der
     * Klasse Thread geerbt wurde.
     */
    public void run() {
        try {
            search(searchRoot);
            System.out.println(filecounter + " Dateien im Pfad " +
                               searchRoot + " gefunden. Meta-Daten werden eingelesen...");
        } catch (SmbException e) {
            System.err.println("SambaException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void search(SmbFile searchPath) throws SmbException {

        if (stopped) {
            return;
        }

        if (searchPath.isDirectory()) {
            SmbFile[] files = searchPath.listFiles();
            for (int i = 0; i < files.length; i++) {
                search(files[i]);
            }
        } else {
            if (searchPath.getName().endsWith("mp3")) {

                Song newSong = new Song();
                newSong.setFilename(searchPath.getPath());

                /* Counter erhöhen */
                filecounter++;

                /* File auf FoundFiles-Stack pushen */
                foundFiles.push(newSong);
            }
        }
    }

    /**
     * Gibt den Suchpfad dieses FileSearchThreads zurück
     * @return Suchpfad
     */
    public Suchpfad getSuchpfad()
    {
        return suchpfad;
    }
}
