package de.fhaugsburg.rundfunker.rundspieler;

import de.fhaugsburg.rundfunker.rundspieler.player.Player;
import de.fhaugsburg.rundfunker.rundsucher.Config;

public interface Controller {

    public static final String ARTIST = "artist";

    public static final String GENRE = "genre";

    public static final String ALBUM = "album";

    public static final String TITLE = "title";
    
    public void turnDDS(int value);
    
    public void turnDDSLeft();
    
    public void turnDDSRight();

    public void pressDDS();

    public void pressBtArtist();

    public void pressBtGenre();

    public void pressBtTitle();

    public void pressBtAlbum();

    public void pressBtSpell();

    public void powerSwitch();

    public void setPlayer(Player player);

    public Player getPlayer();

//    public void play(int index);
    
    public void play(SearchFilter sf);
    
//    public void skipSongs(int delta);
    
    public void nextSong();
    
    public void prevSong();
    
    public void deltaSong(int delta);

    public void setPlaylist(Playlist playlist);

    public Playlist getPlaylist();

    public void doReturn(String returnMode);
    
    public void startRundspieler();

}
