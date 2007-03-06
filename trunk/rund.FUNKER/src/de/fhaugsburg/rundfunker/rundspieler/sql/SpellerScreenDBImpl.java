package de.fhaugsburg.rundfunker.rundspieler.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.fhaugsburg.rundfunker.rundspieler.view.Display;
import de.fhaugsburg.rundfunker.rundspieler.view.SpellerScreen;
import de.fhaugsburg.rundfunker.rundsucher.Config;
import de.fhaugsburg.rundfunker.rundspieler.Controller;

public class SpellerScreenDBImpl extends SpellerScreen {

    private Connection conn;

    public SpellerScreenDBImpl(Controller controller, Display display) {
        super(controller, display);
        try {
            conn = Config.getInstance().createCon();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public char[] getAvailableChars() {
        try {
            Statement s = conn.createStatement();
            // Leerzeichen muss ersetzt werden, da es sonst durch DISTINCT mit
            // leerem Eintrag ersetzt wird.
            String sql = "select distinct replace( upper(substring("
                    + filterType + ", " + (this.inputChars.length() + 1)
                    + ",1)),\" \",  \"" + (char) Display.SPACE_SPELLER
                    + "\") as nextChar from rf_song where " + filterType
                    + " like '" + inputChars + "%' order by nextChar";

            ResultSet rs = s.executeQuery(sql);
            StringBuffer sb = new StringBuffer();
            while (rs.next()) {
                sb.append(rs.getString("nextChar"));
            }
            sb.append((char) Display.LEFT_ARROW);
            sb.append((char) Display.OK);

            rs.close();
            s.close();
            char[] chars = sb.toString().toCharArray();
            return chars;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public int getResultCount(String nextChar) {
        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT COUNT(DISTINCT " + filterType
                    + ") as resultCount FROM rf_song WHERE " + filterType
                    + " like \"" + inputChars + nextChar + "%\"");
            rs.next();
            return rs.getInt("resultCount");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

}
