package de.fhaugsburg.rundfunker.rundspieler.view;

import java.awt.font.NumericShaper;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.NumberFormatter;

import de.fhaugsburg.rundfunker.rundspieler.player.PlayerImpl;
import de.fhaugsburg.rundfunker.rundsucher.Song;
import de.fhaugsburg.rundfunker.rundspieler.Controller;

public class PlayScreen extends AbstractScreen {

    private int status;

    private Song song;

    private Map stati;

    private boolean isPlaying;

    private int frameCounter=0;

    private final String[] bussyAniFrames = {" ",".","..","...","...."};

    private boolean nextSongStartet;

    public PlayScreen(Controller controller, Display display) {
        super(controller, display);
    }

    public void init(Song song, int status) {
        this.song = song;
        this.status = status;
        stati = new HashMap();
        stati.put(new Integer(PlayerImpl.PAUSED), "||");
        stati.put(new Integer(PlayerImpl.PLAYING), ">");
        stati.put(new Integer(PlayerImpl.STOPPED), "\u1555");
        isPlaying=true;
        displayRows[0].setValue(song.getArtist());
        displayRows[1].setValue(song.getAlbum());
        displayRows[2].setValue(song.getTitle());
        displayRows[3].setValue("");
        display.setLine(0,displayRows[0].toString());
        display.setLine(1,displayRows[1].toString());
        display.setLine(2,displayRows[2].toString());
        display.setLine(3,displayRows[3].toString());
        int[] tempArray=new int[4];
        int counter=0;
        for (int i = 0; i < tempArray.length; i++) {
			if(displayRows[i].isTooLong()){
				tempArray[counter++]=i;
			}
		}
        lineNumsToRender=new int[counter];
        System.arraycopy(tempArray,0,lineNumsToRender,0,counter);
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public void turnDDS(int value) {
    	if(value==Display.JD_TURN_RIGHT){
    		controller.nextSong();
    	}else if(value==Display.JD_TURN_LEFT){
    		controller.prevSong();
    	}

    }

    public void setNextSongStarted(boolean b){
    	nextSongStartet=b;
    }

    public void pressDDS() {
        controller.getPlayer().pause();
    }

    public void updateLines() {
    	if(!nextSongStartet){
    		displayRows[3].setValue(" ");
    		return;
    	}
        if(controller.getPlayer().isBusy()){
        	//connecting-Animation
            frameCounter= (frameCounter+1)%bussyAniFrames.length;
            displayRows[3].setValue("connecting"+bussyAniFrames[frameCounter]);
        }else{
//        	Zeitanzeige
            displayRows[3].setValue(milisToTimeString(controller.getPlayer()
                    .getPlayTimeMillis())
                    + "/" + milisToTimeString(song.getLengthSeconds() * 1000));
        }

        // displayRows[2].setValue((String)stati.get(new Integer(status)));
    }

    public void render() {
        updateLines();
        display.setLine(3,displayRows[3].toString());

//      Lauftext. Bei jedem Rendervorgang wird die Zeile eins weiter geschoben.
        DisplayRow dr;
        int rowNum;
        for (int i = 0; i < lineNumsToRender.length; i++) {
        	rowNum=lineNumsToRender[i];
        	dr=displayRows[rowNum];
        	dr.setOffset(dr.getOffset()+1);
        	display.setLine(rowNum,dr.toString());
		}
    }

    public static String milisToTimeString(long milis) {
        StringBuffer s = new StringBuffer();
        long value;
        // minuten
        value = milis / (60 * 1000);
        if (value / 10 == 0) {
            s.append("0");
        }
        s.append(value);
        s.append(":");
        milis -= (value * 60 * 1000);
        // sekunden
        value = milis / 1000;
        if (value / 10 == 0) {
            s.append("0");
        }
        s.append(value);
        return s.toString();
    }



}
