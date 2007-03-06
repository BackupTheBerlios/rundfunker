package de.fhaugsburg.rundfunker.rundspieler;

public interface SearchFilter {
    
    /**
     * Setzt den Filter f�r die Suche:
     * @param filter: z.B. genre, artist, song etc. 
     */
    public void setFilter(String filter);
    
    /**
     * Setzt den Such-String
     * @param searchString
     */
    public void setSearchString(String searchString);
    
    /**
     * Gibt den filter f�r die Suche zur�ck
     * @return den filter f�r die Suche zur�ck
     */
    public String getFilter();
    
    /**
     * 
     * @return
     */
    public String getSearchString();

}
