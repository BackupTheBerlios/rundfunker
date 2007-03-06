package de.fhaugsburg.rundfunker.rundspieler.player;

import de.fhaugsburg.rundfunker.rundspieler.ConnectionException;

import de.fhaugsburg.rundfunker.rundsucher.Song;

public interface Player {

    public static final int PLAYING = 1;
    public static final int PAUSED = 2;
    public static final int STOPPED = 3;
    public static final int CHOOSESONG = 4;
    public static final int SEARCH = 5;
    public static final int OFF = -1;

    public void playSong(Song song) throws ConnectionException;
    public void pause();
    public void stop();
    public int getPlayerStatus();
    public long getPlayTimeMillis();
    public long getSongMillis();
    public void setPlayBackListener(PlaybackListener listener);
    public PlaybackListener getPlayBackListener();
    
    /**
     * Flag dafür, dass der Player aufgrund einer anderen Aufgabe
     * noch nicht das tun kann was er soll.
     * @return das Flag
     */
    public boolean isBusy(); 

}
