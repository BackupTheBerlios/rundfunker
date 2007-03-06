package de.fhaugsburg.rundfunker.rundspieler;

import de.fhaugsburg.rundfunker.rundspieler.player.*;
import de.fhaugsburg.rundfunker.rundspieler.sql.*;
import de.fhaugsburg.rundfunker.rundspieler.util.*;
import de.fhaugsburg.rundfunker.rundspieler.view.*;
import de.fhaugsburg.rundfunker.rundsucher.*;

public class ControllerImpl implements Controller, PlaybackListener {

    private boolean showListWhenDDSTurned = false;

    /* hier wäre ein ENUM schöner gewesen, aber wir haben nur Java 1.4 */
    private String currentMode;

    private Player player;

    private Display display;

    private Screen currentScreen;

    private ListScreen listScreen;

    private PlayScreen playScreen;

    private SpellerScreen spellerScreen;

    private ErrorScreen errorScreen;

    private ListScreen nextSongScreen;

    private Playlist playlist;

    private ReturnTimerHandler returnTimerHandler;

    private RenderTimerHandler renderTimerHandler;

    private Song currentSong;

    private DynamicTimerHandler nextSongDelayTimer;

    private Config conf = Config.getInstance();

    public ControllerImpl(Player player, Display display, Playlist playlist) {
        String s = conf.getProperty("showListWhenDDSTurned", "false");
        showListWhenDDSTurned = Boolean.valueOf(s).booleanValue();
        this.player = player;
        this.display = display;
        this.playlist = playlist;
        initScreens();
        setCurrentMode(playlist.getFilter().getFilter());
        returnTimerHandler = new ReturnTimerHandler(currentMode, this);
        renderTimerHandler = new RenderTimerHandler(playScreen, 400);
        player.setPlayBackListener(this);
        try {
            nextSongDelayTimer = new DynamicTimerHandler(this,
                    "playCurrentSong", null);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void startRundspieler() {
        try {
            currentSong = playlist.getCurrentSong();
            playScreen.init(currentSong, player.getPlayerStatus());
            setScreen(playScreen);
            playCurrentSong();
        } catch (DataException e) {
            errorScreen.setMessage(e.getMessage());
            setScreen(errorScreen);
            e.printStackTrace();
        }
    }

    public void stop() {
        errorScreen.setMessage("GESTOPPT.");
        player.stop();
        renderTimerHandler.stopRenderTimer();
        returnTimerHandler.stopTimer();
        setScreen(errorScreen);
    }

    private void pressButton(String name) {
        // wenn knopf zweimal hintereinander gedrückt Speller starten
        if (name.equals(currentMode) && returnTimerHandler.isRunning()
                && currentScreen != nextSongScreen) {
            pressBtSpell();
        } else {
            showList(name);
            returnTimerHandler.startTimer(currentMode);
            setCurrentMode(name);
        }
    }

    public void turnDDS(int value) {
        if (currentScreen == playScreen && showListWhenDDSTurned) {
            // display.setCursorVisibility(false);
            nextSongScreen.init(currentMode, currentSong);
            this.setScreen(nextSongScreen);
            returnTimerHandler.startTimer(currentMode);
            return;
        }
        if (player.isBusy()) {
            // do nothing
            returnTimerHandler.resetTimer();
            return;
        }
        returnTimerHandler.resetTimer();
        currentScreen.turnDDS(value);
    }

    public void turnDDSLeft() {
        turnDDS(-1);
    }

    public void turnDDSRight() {
        turnDDS(1);
    }

    public void pressDDS() {
        if (player.isBusy()) {
            // do nothing
            returnTimerHandler.resetTimer();
            return;
        }
        returnTimerHandler.resetTimer();
        currentScreen.pressDDS();
    }

    public void pressBtArtist() {
        pressButton(ARTIST);
    }

    public void pressBtGenre() {
        pressButton(GENRE);
    }

    public void pressBtAlbum() {
        pressButton(ALBUM);

    }

    public void pressBtTitle() {
        pressButton(TITLE);

    }

    public void pressBtSpell() {
        // falls speller schon aktiv, zurück zum ListScreen
        if (currentScreen == spellerScreen) {
            showList(currentMode);
            returnTimerHandler.startTimer(currentMode);
        } else {
            spellerScreen.init(currentMode);
            setScreen(spellerScreen);
            returnTimerHandler.startTimer(currentMode);

        }
    }

    public void powerSwitch() {
        // TODO: close all connections and System Exit

    }

    private void setCurrentMode(String mode) {
        currentMode = mode;
        int lednumber = Integer.parseInt(conf.getProperty("LED_" + mode, "0"));
        display.setLED(lednumber);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private void setScreen(Screen screen) {
        renderTimerHandler.stopRenderTimer();
        if (screen == playScreen) {
            renderTimerHandler.startRenderTimer();
        }
        currentScreen = screen;
        currentScreen.render();
    }

    private void initScreens() {

        System.out.println("Screens werden initialisiert");

        listScreen = new ListScreenDBImpl(this, display);

        playScreen = new PlayScreen(this, display);

        spellerScreen = new SpellerScreenDBImpl(this, display);

        errorScreen = new ErrorScreen(this, display);

        nextSongScreen = new SongListScreenDBImpl(this, display);

    }

    private void showList(String type) {
        display.setCursorVisibility(false);
        listScreen.init(type, currentSong);
        this.setScreen(listScreen);

    }

    public void play(SearchFilter sf) {
        try {
            returnTimerHandler.stopTimer();
            display.setCursorVisibility(false);
            playlist.play(sf);
            currentSong = playlist.getCurrentSong();
            playScreen.init(currentSong, player.getPlayerStatus());
            setScreen(playScreen);
            playCurrentSong();
        } catch (DataException e) {
            errorScreen.setMessage(e.getMessage());
            setScreen(errorScreen);
            e.printStackTrace();
        }

    }

    public void nextSong() {
        try {
            playlist.nextSong();
            currentSong = playlist.getCurrentSong();
            if (currentScreen == playScreen) {
                playScreen.init(currentSong, player.getPlayerStatus());
            }
            playScreen.setNextSongStarted(false);
            nextSongDelayTimer.start(2000);
        } catch (DataException e) {
            errorScreen.setMessage(e.getMessage());
            setScreen(errorScreen);
            e.printStackTrace();
        } catch (EndOfPlaylistException e) {
            // einfach weiterspielen
        }

    }

    public void prevSong() {
        try {
            playlist.prevSong();
            currentSong = playlist.getCurrentSong();
            if (currentScreen == playScreen) {
                playScreen.init(currentSong, player.getPlayerStatus());
            }
            playScreen.setNextSongStarted(false);
            nextSongDelayTimer.start(2000);

        } catch (DataException e) {
            errorScreen.setMessage(e.getMessage());
            setScreen(errorScreen);
            e.printStackTrace();
        } catch (EndOfPlaylistException e) {
            // einfach weiterspielen
        }

    }

    public void deltaSong(int delta) {
        try {
            if (delta < 0) {
                for (int i = 0; i < Math.abs(delta); i++) {
                    playlist.prevSong();
                }
            } else {
                for (int i = 0; i < Math.abs(delta); i++) {
                    playlist.nextSong();
                }
            }
            returnTimerHandler.stopTimer();
            display.setCursorVisibility(false);
            currentSong = playlist.getCurrentSong();
            playScreen.init(currentSong, player.getPlayerStatus());
            setScreen(playScreen);
            playCurrentSong();

        } catch (DataException e) {
            errorScreen.setMessage(e.getMessage());
            setScreen(errorScreen);
            e.printStackTrace();
        } catch (EndOfPlaylistException e) {
            // einfach weiterspielen
        }

    }

    public void playCurrentSong() {
        try {
            playScreen.setNextSongStarted(true);
            player.playSong(currentSong);
        } catch (ConnectionException e) {
            errorScreen.setMessage(e.getMessage());
            setScreen(errorScreen);
            e.printStackTrace();
        }
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;

    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void doReturn(String returnMode) {
        display.setCursorVisibility(false);
        setCurrentMode(returnMode);
        playScreen.init(currentSong, player.getPlayerStatus());
        setScreen(playScreen);

    }

    public Player getPlayer() {
        return player;
    }

    public void playbackStarted(PlaybackEvent evt) {
        // TODO Auto-generated method stub

    }

    public void playbackFinished(PlaybackEvent evt) {
        // Spiele nächsten Song
        nextSong();
    }

    public void errorOcurred(PlaybackEvent evt) {
        errorScreen.setMessage("Fehler beim lesen von"
                + currentSong.getFilename());
        setScreen(errorScreen);
    }

}
