package de.fhaugsburg.rundfunker.rundspieler;

/**
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
public interface RemoteControl {

    public void turnDDS(int value);

    public void pressDDS();

    public void pressBtArtist();

    public void pressBtGenre();

    public void pressBtTitle();

    public void pressBtAlbum();

    public void pressBtSpell();

}
