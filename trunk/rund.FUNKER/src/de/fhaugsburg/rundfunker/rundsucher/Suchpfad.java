package de.fhaugsburg.rundfunker.rundsucher;

import java.sql.*;


public class Suchpfad {

    private long dbid;
    private String pfad;
    private long smbHashCode;
    private Timestamp lastDBChange;
    private boolean wirdGeradeDurchsucht = false;
    private boolean forceSearch = false;

    public Suchpfad(){}

    public Suchpfad(String pfad)
    {
        this.pfad = pfad;
    }

    public long getDbid() {
        return dbid;
    }

    public String getPfad() {
        return pfad;
    }

    public long getSmbHashCode() {
        return smbHashCode;
    }

    public Timestamp getLastDBChange() {
        return lastDBChange;
    }

    public boolean isWirdGeradeDurchsucht() {
        return wirdGeradeDurchsucht;
    }

    public boolean isForceSearch() {
        return forceSearch;
    }

    public void setDbid(long dbid) {
        this.dbid = dbid;
    }

    public void setPfad(String pfad) {
        this.pfad = pfad;
    }

    public void setSmbHashCode(long smbHashCode) {
        this.smbHashCode = smbHashCode;
    }

    public void setLastDBChange(Timestamp lastDBChange) {
        this.lastDBChange = lastDBChange;
    }

    public void setWirdGeradeDurchsucht(boolean wirdGeradeDurchsucht) {
        this.wirdGeradeDurchsucht = wirdGeradeDurchsucht;
    }

    public void setForceSearch(boolean forceSearch) {
        this.forceSearch = forceSearch;
    }

    /**
     * Vergleicht zwei Suchpfade aufgrund ihres SmbHashCodes
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj) {
        if(!(obj instanceof Suchpfad))
            return false;
        Suchpfad other = (Suchpfad)obj;
        return (other.getSmbHashCode()==this.getSmbHashCode());
    }

    public String toString()
    {
        return pfad;
    }

}
