package de.fhaugsburg.rundfunker.rundspieler.player;

import java.io.*;
import java.net.*;
import javazoom.jl.decoder.*;
import javazoom.jl.player.*;

import de.fhaugsburg.rundfunker.rundspieler.*;
import de.fhaugsburg.rundfunker.rundsucher.*;
import jcifs.smb.*;

/**
 *
 * @author Christian Leberfinger
 * @version 1.0
 */
public class JLayerPlayerImpl implements Player, Runnable {
    /** The MPEG audio bitstream. */
    private Bitstream bitstream;

    /** The MPEG audio decoder. */
    private Decoder decoder;

    /** The AudioDevice the audio samples are written to. */
    private AudioDevice audio;

    /** Has the player been closed? */
    private boolean closed = false;

    /** Has the player played back all frames from the stream? */
    private boolean complete = false;

    private int lastPosition = 0;

    /** Listener for the playback process */
    private PlaybackListener listener;

    private boolean paused = false;

    private boolean playing = false;

    private Song song = null;

    private SmbFile file = null;

    private InputStream istream;

    private long songBeginTime;

    private long pausedTime;

    private boolean isBusy;

    private boolean statisticsWereUpdated = false;

    /**
     * Spielt eine festgelegte Anzahl an Frames. Diese Methode übernimmt die
     * eigentliche Abspiellogik.
     *
     * @return true if the last frame was played, or false if there are more
     *         frames.
     */
    private boolean play() throws JavaLayerException {
        songBeginTime = System.currentTimeMillis();
        // Listener beim Start benachrichtigen
        if (listener != null) {
            listener.playbackStarted(createEvent(PlaybackEvent.STARTED));
        }

        playing = true;

        Thread playThread = new Thread(this);
        playThread.start();

        return playing;
    }

    /**
     * Closes this player. Any audio currently playing is stopped immediately.
     */
    private synchronized void close() {
        playing = false;
        AudioDevice out = audio;

        updateStatistics();

        if (out != null) {
            closed = true;
            audio = null;
            // this may fail, so ensure object state is set up before
            // calling this method.
            out.close();
            lastPosition = out.getPosition();
        }
        try {
            if (bitstream != null) {
                bitstream.close();
            }
        } catch (BitstreamException ex) {
        }
        try {
            if (istream != null)
                istream.close();
        } catch (IOException ex1) {
        }

    }

    /**
     * Decodes a single frame.
     *
     * @return true if there are no more frames to decode, false otherwise.
     */
    protected boolean decodeFrame() throws JavaLayerException {
        try {
            AudioDevice out = audio;
            if (out == null) {
                return false;
            }

            Header h = bitstream.readFrame();
            if (h == null) {
                return false;
            }

            // hier kann teilweise eine ArrayIndexOutOfBoundsException auftreten!
            SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h,
                    bitstream);

            synchronized (this) {
                out = audio;
                if (out != null) {
                    out.write(output.getBuffer(), 0, output.getBufferLength());
                }
            }

            bitstream.closeFrame();
        } catch (RuntimeException ex) {
            throw new JavaLayerException("Exception decoding audio frame", ex);
        }
        return true;
    }

    /**
     * skips over a single frame
     *
     * @return false if there are no more frames to decode, true otherwise.
     */
    protected boolean skipFrame() throws JavaLayerException {
        Header h = bitstream.readFrame();
        if (h == null) {
            return false;
        }
        bitstream.closeFrame();
        return true;
    }

    /**
     * Constructs a <code>PlaybackEvent</code>
     */
    private PlaybackEvent createEvent(int id) {
        return createEvent(audio, id);
    }

    /**
     * Constructs a <code>PlaybackEvent</code>
     */
    private PlaybackEvent createEvent(AudioDevice dev, int id) {
        return new PlaybackEvent(this, id, dev.getPosition());
    }

    /**
     * sets the <code>PlaybackListener</code>
     */
    public void setPlayBackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    /**
     * gets the <code>PlaybackListener</code>
     */
    public PlaybackListener getPlayBackListener() {
        return listener;
    }

    /**
     * closes the player and notifies <code>PlaybackListener</code>
     */
    public void stop() {

        // Statistische Informationen aktualisieren
        updateStatistics();

        // Listener benachrichtigen
        if (listener != null)
            listener.playbackFinished(createEvent(PlaybackEvent.STOPPED));

        // Streams schließen
        close();
    }

    /**
     * Spielt einen Song
     *
     * @param song
     *            Song
     */
    public void playSong(Song song) throws ConnectionException {
        try {
            // Setzen des Busy-Flags. Wird für die Lade-Info benötigt,
            // da der Verbindungsvorgang manchmal länger dauert.
            isBusy = true;

            close();

            if (this.song != null && this.song.equals(song) && playing) {
                istream = file.getInputStream();
            } else {
                this.song = song;
                statisticsWereUpdated = false;
                file = new SmbFile(song.getFilename());
                istream = file.getInputStream();
            }

            bitstream = new Bitstream(istream);

            audio = FactoryRegistry.systemRegistry().createAudioDevice();
            audio.open(decoder = new Decoder());

            play();

            // Zurücksetzen des Busy-Flags
            isBusy = false;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            throw new ConnectionException("Verbindungsfehler. "
                    + song.getFilename() + " kann nicht gefunden werden");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new ConnectionException("Verbindungsfehler beim Lesen von "
                    + song.getFilename());
        } catch (JavaLayerException ex) {
            throw new ConnectionException("Decoding-Fehler bei "
                    + song.getFilename());
        }

    }

    public void pause() {
        paused = !paused;
        if (paused) {
            pausedTime = System.currentTimeMillis();
        }else{
        	songBeginTime+=System.currentTimeMillis()-pausedTime;
        }
    }

    public int getPlayerStatus() {
        return 0;
    }

    public void run() {
        try {
            while (playing) {
                while (paused) {
                    Thread.sleep(500);
                }
                playing = decodeFrame();
            }
        } catch (JavaLayerException ex) {
            ex.printStackTrace();
            listener.errorOcurred(createEvent(PlaybackEvent.ERROROCURRED));
        } catch (InterruptedException ex) {
        }

        // last frame, ensure all data flushed to the audio device.
        AudioDevice out = audio;
        if (out != null) {
            // System.out.println(audio.getPosition());
            out.flush();
            // System.out.println(audio.getPosition());
            synchronized (this) {
                complete = (!closed);
                close();
            }

            // Statistische Informationen aktualisieren
            updateStatistics();

            // report to listener
            if (listener != null) {
                listener.playbackFinished(createEvent(out,
                        PlaybackEvent.STOPPED));
            }
        }
    }

    /**
     * Aktualisiert die statistischen Einträge des gespielten Songs
     */
    private void updateStatistics()
    {
        if(song!=null && !statisticsWereUpdated)
        {
            // nur positive Werte in DB eintragen!
            song.updatePlayTimeStatistic(Math.max(0, getPlayTimeMillis() / 1000));
            statisticsWereUpdated = true;
        }
    }

    public long getPlayTimeMillis() {
        if (paused) {
            return pausedTime - songBeginTime;
        } else
            return Math.max(System.currentTimeMillis() - songBeginTime,0);
    }

    public long getSongMillis() {
        /** @todo implementieren */
        return 0L;
    }

    public boolean isBusy() {
        return isBusy;
    }
}
