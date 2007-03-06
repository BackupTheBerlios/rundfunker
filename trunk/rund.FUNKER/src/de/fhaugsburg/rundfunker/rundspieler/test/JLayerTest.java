package de.fhaugsburg.rundfunker.rundspieler.test;

import javazoom.jl.player.Player;
import java.io.*;
import javazoom.jl.decoder.*;
import jcifs.smb.SmbFile;
import java.net.MalformedURLException;

/**
 * Testklasse für die JL-Library
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: AG RundFunker</p>
 *
 * @author Christian Leberfinger
 * @version 1.0
 */
public class JLayerTest {

    public static void main(String[] args) {

        try {

            File f = new File("001.mp3");
            javazoom.jl.player.Player p = new Player(new FileInputStream(f));
            p.play();

        } catch (FileNotFoundException ex) {
        } catch (JavaLayerException ex) {
        } catch (IOException ex) {
        }
    }

}
