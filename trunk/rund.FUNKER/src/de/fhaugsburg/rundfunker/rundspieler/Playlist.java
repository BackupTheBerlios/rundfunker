package de.fhaugsburg.rundfunker.rundspieler;

import de.fhaugsburg.rundfunker.rundsucher.Song;

public interface Playlist{
    
//    public void skipSongs(int delta)throws EndOfPlaylistException,DataException;
    
    public void nextSong()throws EndOfPlaylistException, DataException;
    
    public void prevSong()throws EndOfPlaylistException, DataException;

    public Song getCurrentSong()throws DataException;

    public SearchFilter getFilter();
    
    public void play(SearchFilter sf)throws DataException;

}
