package de.fhaugsburg.rundfunker.rundspieler.test;

import java.util.Timer;
import java.util.TimerTask;

import de.fhaugsburg.rundfunker.rundspieler.player.PlaybackListener;
import de.fhaugsburg.rundfunker.rundspieler.player.Player;
import de.fhaugsburg.rundfunker.rundsucher.Song;

public class NoPlayer implements Player {
    
    private Timer t = new Timer();
    private TimerTask tt;
    
    private long playTimeMilis;
    
    private long playTimeStarted;

    public NoPlayer(){
        tt= new TimerTask(){

            public void run() {
                playTimeMilis=System.currentTimeMillis()-playTimeStarted;
            }
            
        };
    }
    
    public void playSong(Song song) {
        playTimeStarted=System.currentTimeMillis();
        t.scheduleAtFixedRate(tt,0,100);
    }

    public void pause() {
        // TODO Auto-generated method stub
        
    }

    public void stop() {
        // TODO Auto-generated method stub
        
    }

    public int getPlayerStatus() {
        // TODO Auto-generated method stub
        return 0;
    }

    public long getPlayTimeMillis() {
        return playTimeMilis;
    }

    public long getSongMillis() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isBusy() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setPlayBackListener(PlaybackListener listener) {
        // TODO Auto-generated method stub
        
    }

    public PlaybackListener getPlayBackListener() {
        // TODO Auto-generated method stub
        return null;
    }

}
