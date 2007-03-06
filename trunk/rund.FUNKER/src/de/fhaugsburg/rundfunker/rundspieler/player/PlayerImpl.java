package de.fhaugsburg.rundfunker.rundspieler.player;

import java.io.IOException;
import java.util.Properties;
import de.fhaugsburg.rundfunker.rundsucher.Song;

import de.fhaugsburg.rundfunker.rundspieler.Controller;



public class PlayerImpl implements Player {

    private int status;
    private Properties playerProps;
    private String execString;


    public PlayerImpl() {
        status= OFF;
//        playerProps = new Properties();
//        try {
//            FileInputStream fis = new FileInputStream(
//                    "src/chbeckmann/rundplayer/model/playerWinamp.properties");
//            playerProps.load(fis);
//            fis.close();
//            execString = playerProps.getProperty("path")
//            + playerProps.getProperty("playername");
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }


    public void playSong(Song song) {

    }

    public void pause() {
        // TODO Auto-generated method stub

    }

    private void execCommand(String[] options) {
        StringBuffer execCommand = new StringBuffer();
        execCommand.append(execString);
        if (options != null) {
            for (int i = 0; i < options.length; i++) {
                execCommand.append(" ");
                execCommand.append(playerProps.getProperty("optionchar"));
                execCommand.append(playerProps.getProperty(options[i]));
            }
        }
        System.out.println(execCommand);
        try {
            Process p = Runtime.getRuntime().exec(execCommand.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stop() {
        // TODO Auto-generated method stub

    }

    public int getPlayerStatus() {
        return status;
    }

    public long getPlayTimeMillis() {
        return 0L;
    }

    public long getSongMillis() {
        return 0L;
    }


    public boolean isBusy() {
        // TODO Auto-generated method stub
        return false;
    }


    public void setController(Controller c) {
        // TODO Auto-generated method stub
        
    }


    public void setPlayBackListener(PlaybackListener listener) {
        // TODO Auto-generated method stub
        
    }


    public PlaybackListener getPlayBackListener() {
        // TODO Auto-generated method stub
        return null;
    }


}
