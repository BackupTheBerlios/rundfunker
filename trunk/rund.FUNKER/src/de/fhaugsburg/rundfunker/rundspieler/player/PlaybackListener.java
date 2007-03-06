package de.fhaugsburg.rundfunker.rundspieler.player;


import de.fhaugsburg.rundfunker.rundspieler.*;

public interface PlaybackListener {
    public void playbackStarted(PlaybackEvent evt);
    public void playbackFinished(PlaybackEvent evt);
    public void errorOcurred(PlaybackEvent evt);
}
