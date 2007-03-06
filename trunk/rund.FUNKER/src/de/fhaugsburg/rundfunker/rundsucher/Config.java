package de.fhaugsburg.rundfunker.rundsucher;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class Config extends Properties {

    private static Config instance = new Config();

    private Config() {
        super();
        try {
            super.load(new FileInputStream("config.properties"));
            String dbDriver = getProperty("dbDriver");
            /* Datenbankverbindung initialisieren */
            Class.forName(dbDriver);
        } catch (IOException ex) {
            System.err.println(
                    "config.properties konnte nicht gefunden werden.");
        } catch (ClassNotFoundException ex) {
            System.err.println("Fehler beim Initialisieren des DB-Treibers: " +
                               ex.getMessage());
        }
    }

    public static Config getInstance() {
        return instance;
    }

    /**
     * Datenbankverbindung herstellen
     * @throws SQLException
     */
    public Connection createCon() throws SQLException {
        Connection c = DriverManager.getConnection(getProperty("dbUrl"),
                getProperty("dbUser"),
                getProperty("dbPass"));
        return c;
    }


}
