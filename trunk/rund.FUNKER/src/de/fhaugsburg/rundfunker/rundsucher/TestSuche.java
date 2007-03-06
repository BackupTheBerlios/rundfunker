package de.fhaugsburg.rundfunker.rundsucher;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Christian Leberfinger
 * @version 1.0
 */
public class TestSuche {

    public static void main(String[] args) {

        try {
            if (args.length > 0) {
                RundSuche rs = RundSuche.getInstance();
                Suchpfad sp = new Suchpfad(args[0]);
                rs.addSearchPath(sp);
            } else {
                System.out.println("Bitte mit Übergabeparameter (Suchpfad) aufrufen, z.B.: smb://user:pass@sambahost/path/to/mp3s");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
