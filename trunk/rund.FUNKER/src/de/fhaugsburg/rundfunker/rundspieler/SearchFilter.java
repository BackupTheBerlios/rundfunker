package de.fhaugsburg.rundfunker.rundspieler;

public interface SearchFilter {
    
    /**
     * Setzt den Filter für die Suche:
     * @param filter: z.B. genre, artist, song etc. 
     */
    public void setFilter(String filter);
    
    /**
     * Setzt den Such-String
     * @param searchString
     */
    public void setSearchString(String searchString);
    
    /**
     * Gibt den filter für die Suche zurück
     * @return den filter für die Suche zurück
     */
    public String getFilter();
    
    /**
     * 
     * @return
     */
    public String getSearchString();

}
