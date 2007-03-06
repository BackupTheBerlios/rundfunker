package de.fhaugsburg.rundfunker.rundsucher;

import java.net.*;
import java.sql.*;
import jcifs.smb.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Der Thread, der schaut, welche Ordner durchsucht werden müssen und für diese
 * Die Suchthreads startet.
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: AG RundSucher</p>
 *
 * @author Christian Leberfinger
 * @version 1.0
 */
public class BossThread extends Thread {

    private RundSuche rs;
    public boolean stopped = false;
    private Connection conn;
    private Config conf = Config.getInstance();

    /* Pfade, die von der RundSuche durchsucht werden müssen */
    private List suchpfade = new LinkedList();
//    private Map suchpfadMap = new HashMap();

    /* Prepared Statements */
    private PreparedStatement psSuchpfade = null;
    private PreparedStatement psFehlerUpdate = null;
    private PreparedStatement psHashCodeUpdate = null;

    public BossThread() {
        rs = RundSuche.getInstance();
    }

    public void run() {
        /* Datenbankverbindung erstellen */
        try {
            conn = conf.createCon();
            String suchpfadTable = conf.getProperty("tableSuchpfade");
            psSuchpfade = conn.prepareStatement("SELECT Id, pfad, smbHashCode, lastDBChange, wirdGeradeDurchsucht, forceSearch FROM " +
                                                suchpfadTable +
                    " WHERE ISNULL(fehler) ORDER BY smbHashCode");
            psFehlerUpdate = conn.prepareStatement("UPDATE " + suchpfadTable +
                    " SET fehler=? WHERE Id=?;");
            psHashCodeUpdate = conn.prepareStatement("UPDATE " + suchpfadTable +
                    " SET smbHashCode=? WHERE Id=?;");
        } catch (SQLException ex) {
            System.err.println(
                    "Fehler beim Erstellen der Datenbankverbindung für BossThread.");
        }

        while (!stopped) {
            try {
                checkSearchPathes();

            } catch (SQLException ex) {
                System.err.println("Fehler in BossThread: " +
                                   ex.getLocalizedMessage());
            }

            try {
                Thread.sleep(20000);
            } catch (InterruptedException ex1) {
            }
        }

        /* Datenbankverbindung schließen */
        try {
            conn.close();
        } catch (SQLException ex) {}
    }

    /**
     * Läuft durch die in der Datenbank angegebenen Suchpfade und prüft, ob Sie
     * von der RundSuche durchsucht werden müssen
     * @throws SQLException
     */
    private void checkSearchPathes() throws SQLException {
        Map fehlerMap = new HashMap();
        //Map suchpfadMap = new HashMap();
        List suchpfade = new LinkedList();

        /* Pfade aus Datenbank holen */
        ResultSet rsPfade = psSuchpfade.executeQuery();

        while (rsPfade.next()) {
            long id = rsPfade.getLong("Id");
            String pfad = rsPfade.getString("pfad");
            long smbHashCode = rsPfade.getLong("smbHashCode");

            /* Daten in Suchpfad-Objekt ablegen */
            Suchpfad suchpfad = new Suchpfad();
            suchpfad.setDbid(id);
            suchpfad.setPfad(pfad);

            /* Ist noch kein HashCode eingetragen? */
            if (rsPfade.wasNull()) {
                /* Hashcode des Pfades berechnen */
                try {
                    SmbFile f = new SmbFile(pfad);
                    smbHashCode = f.hashCode();

                    /* HashCode in Datenbank eintragen */
                    recordHashCode(id, smbHashCode);

                    /** @todo: Evtl. Prüfen, ob Elternpfad schon vorhanden ist */

                } catch (MalformedURLException ex) {
                    fehlerMap.put(new Long(id), ex.getMessage());
                }
            }

            suchpfad.setSmbHashCode(smbHashCode);

            /** @todo: Kombination aus DBID und HashCode zur identifizierung
             * des suchpfadeintrags */

            /* Vergleichen, ob Pfad bereits vorhanden */
            if (suchpfade.contains(suchpfad)) {
                fehlerMap.put(new Long(id), "Pfad bereits vorhanden");
            } else {
                /* Pfad zur Map hinzufügen, deren Elemente durchsucht werden */
                suchpfade.add(suchpfad);
            }

        } //endwhile

        /* Fehler in DB eintragen */
        recordErrors(fehlerMap);

        /* ResultSet schließen */
        rsPfade.close();

        updateSuchpfade(suchpfade);
    }

    private void updateSuchpfade(List neueSuchpfade) {
        System.out.println("Neue Suchpfade: "+neueSuchpfade);
        ListIterator lit = neueSuchpfade.listIterator();
        while(lit.hasNext())
        {
            Suchpfad aktPfad = (Suchpfad) lit.next();
            if(!this.suchpfade.contains(aktPfad))
            {
                System.out.println("+ neu zu suchen: " + aktPfad);
                this.suchpfade.add(aktPfad);
            }
        }
    }

    private void recordErrors(Map fehlerMap) throws SQLException {
        Iterator it = fehlerMap.keySet().iterator();
        while (it.hasNext()) {
            Long key = (Long) it.next();
            psFehlerUpdate.setString(1, (String) fehlerMap.get(key));
            psFehlerUpdate.setLong(2, key.longValue());
            psFehlerUpdate.executeUpdate();
        }
    }

    private void recordHashCode(long dbid, long hashCode) throws SQLException {
        psHashCodeUpdate.setLong(1, hashCode);
        psHashCodeUpdate.setLong(2, dbid);
        psHashCodeUpdate.executeUpdate();
    }

    /* Für Testzwecke */
    public static void main(String[] args) {
        BossThread bt = new BossThread();
        bt.start();
    }

}
