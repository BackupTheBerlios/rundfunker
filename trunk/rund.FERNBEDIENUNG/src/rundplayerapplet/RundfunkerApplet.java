package rundplayerapplet;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

public class RundfunkerApplet extends Applet {

    public static final int BT_ARTIST = 0;
    public static final int BT_TITLE = 1;
    public static final int BT_ALBUM = 2;
    public static final int BT_GENRE = 3;
    public static final int PUSH_DDS = 4;

    public static final int DDS_RIGHT = 1;
    public static final int DDS_LEFT = 2;

    private String[] listData;
    boolean isStandalone = false;
    BorderLayout borderLayout1 = new BorderLayout();
    private RemoteTCPIPClient tcpip;

    Image img;
    JLabel bt_genre = new JLabel();
    JLabel bt_album = new JLabel();
    JLabel bt_titel = new JLabel();
    JLabel bt_interpret = new JLabel();
    JLabel bt_ok = new JLabel();
    JLabel bt_back = new JLabel();
    JLabel bt_next = new JLabel();
    JLabel led_genre = new JLabel(); //Component initialization

    JLabel led_album = new JLabel();
    JLabel led_titel = new JLabel();
    JLabel led_interpret = new JLabel();
    JList display = new JList();
    JLabel bt_reconnect = new JLabel();
    private String ip;

    //Get a parameter value
//    public String getParameter(String key, String def) {
//        return isStandalone ? System.getProperty(key, def) :
//                (getParameter(key) != null ? getParameter(key) : def);
//    }

    //Construct the applet
    public RundfunkerApplet() {

        listData = new String[] {" ", " ", " ", " "};

    }

    public void setLine(int rowNum, String line) {
        listData[rowNum] = line;
        display.setListData(listData);
    }

    public void setLED(int LEDnum) {
        //TODO: Bringe LED zum Leuchten+alle anderen aus.
    }

    //Initialize the applet
    public void init() {
        System.out.println("codebase:" + getCodeBase());

        this.ip = getParameter("IP");
        System.out.println("IP: " + this.ip);

        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setLine(0, "               -- Loaded --");
        this.connect();
    }

    /**
     * connect
     */
    private void connect() {
        tcpip = new RemoteTCPIPClient(this, ip);
        tcpip.start();
    }

    public void destroy() {
        try {
            tcpip.send("HABEDIEEHRE");
        } catch (IOException ex1) {

        }
        try {
            if (tcpip != null) {
                tcpip.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("exit");
    }

    private void jbInit() throws Exception {
        bt_genre.setBackground(Color.lightGray);
        bt_genre.setForeground(new Color(238, 238, 238));

        bt_genre.setIcon(new ImageIcon(getImage(getDocumentBase(),
                                                "images/bt_simple.png")));

        bt_genre.setBounds(new Rectangle(425, 25, 52, 52));
        bt_genre.addMouseListener(new RundfunkerApplet_bt_genre_mouseAdapter(this));
        this.setLayout(null);
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        img = getImage(getDocumentBase(), "images/background.png");

        //mediatracker blockiert bis alle bilder geladen sind
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(img, 0);
        tracker.waitForAll();
        bt_album.setIcon(new ImageIcon(getImage(getDocumentBase(),
                                                "images/bt_simple.png")));
        bt_album.setBounds(new Rectangle(425, 89, 52, 52));
        bt_album.addMouseListener(new RundfunkerApplet_bt_album_mouseAdapter(this));
        bt_titel.setIcon(new ImageIcon(getImage(getDocumentBase(),
                                                "images/bt_simple.png")));
        bt_titel.setBounds(new Rectangle(425, 153, 52, 52));
        bt_titel.addMouseListener(new RundfunkerApplet_bt_titel_mouseAdapter(this));
        bt_interpret.setIcon(new ImageIcon(getImage(getDocumentBase(),
                "images/bt_simple.png")));
        bt_interpret.setBounds(new Rectangle(425, 217, 52, 52));
        bt_interpret.addMouseListener(new
                                      RundfunkerApplet_bt_interpret_mouseAdapter(this));
        bt_ok.setIcon(new ImageIcon(getImage(getDocumentBase(),
                                             "images/bt_simple.png")));
        bt_ok.setBounds(new Rectangle(125, 153, 52, 52));
        bt_ok.addMouseListener(new RundfunkerApplet_bt_ok_mouseAdapter(this));
        bt_back.setIcon(new ImageIcon(getImage(getDocumentBase(),
                                               "images/bt_backward.png")));
        bt_back.setBounds(new Rectangle(25, 160, 34, 40));
        bt_back.addMouseListener(new RundfunkerApplet_bt_back_mouseAdapter(this));
        bt_next.setIcon(new ImageIcon(getImage(getDocumentBase(),
                                               "images/bt_forward.png")));
        bt_next.setBounds(new Rectangle(244, 160, 34, 40));
        bt_next.addMouseListener(new RundfunkerApplet_bt_next_mouseAdapter(this));
        led_genre.setIcon(new ImageIcon(getImage(getDocumentBase(),
                                                 "images/led_off.png")));
        led_genre.setBounds(new Rectangle(311, 46, 8, 8));
        led_album.setIcon(new ImageIcon(getImage(getDocumentBase(),
                                                 "images/led_off.png")));
        led_album.setBounds(new Rectangle(311, 110, 8, 8));
        led_titel.setIcon(new ImageIcon(getImage(getDocumentBase(),
                                                 "images/led_off.png")));
        led_titel.setBounds(new Rectangle(311, 174, 8, 8));
        led_interpret.setIcon(new ImageIcon(getImage(getDocumentBase(),
                "images/led_off.png")));
        led_interpret.setBounds(new Rectangle(311, 238, 8, 8));
        display.setBackground(new Color(245, 245, 245));
        display.setBounds(new Rectangle(25, 25, 250, 114));
        display.setListData(listData);

        display.setFocusable(false);
        display.setFont(new Font("Verdana", Font.BOLD, 14));
        display.setFixedCellHeight(28);
        bt_reconnect.setIcon(new ImageIcon(getImage(getDocumentBase(),
                "images/bt_reconnect.png")));
        bt_reconnect.setBounds(new Rectangle(223, 268, 56, 8));
        bt_reconnect.addMouseListener(new
                                      RundfunkerApplet_bt_reconnect_mouseAdapter(this));

        this.add(bt_genre);
        this.add(bt_album);
        this.add(bt_titel);
        this.add(bt_interpret);
        this.add(bt_ok);
        this.add(bt_back);
        this.add(bt_next);
        this.add(led_genre);
        this.add(led_album);
        this.add(led_titel);
        this.add(led_interpret);
        this.add(display);
        this.add(bt_reconnect);
    }

    public void paint(Graphics graphics) {
        graphics.drawImage(img, 0, 0, null);
        super.paint(graphics);
    }

    //Get Applet information
    public String getAppletInfo() {
        return "Rundfunker Fernbedienung V1.0";
    }

    //Get parameter info
    public String[][] getParameterInfo() {
        return null;
    }

    //Main method
    public static void main(String[] args) {
        RundfunkerApplet applet = new RundfunkerApplet();
        applet.isStandalone = true;

        Frame frame;
        frame = new Frame();
        frame.setTitle("Rundfunker Fernbedienung");

        frame.add(applet, BorderLayout.CENTER);

        applet.init();
        applet.start();
        frame.setSize(500, 292);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width - frame.getSize().width) / 2,
                          (d.height - frame.getSize().height) / 2);
        frame.setVisible(true);
    }

    public void bt_genre_mouseReleased(MouseEvent e) {
        try {
            if (tcpip != null) {
                tcpip.send("BT" + BT_GENRE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void bt_album_mouseReleased(MouseEvent e) {
        try {
            if (tcpip != null) {
                tcpip.send("BT" + BT_ALBUM);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void bt_titel_mouseReleased(MouseEvent e) {
        try {
            if (tcpip != null) {
                tcpip.send("BT" + BT_TITLE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void bt_interpret_mouseReleased(MouseEvent e) {
        try {
            if (tcpip != null) {
                tcpip.send("BT" + BT_ARTIST);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void bt_ok_mouseReleased(MouseEvent e) {
        try {
            if (tcpip != null) {
                tcpip.send("BT" + PUSH_DDS);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void bt_next_mouseReleased(MouseEvent e) {
        try {
            if (tcpip != null) {
                tcpip.send("JD " + DDS_RIGHT);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void bt_back_mouseReleased(MouseEvent e) {
        try {
            if (tcpip != null) {
                tcpip.send("JD " + DDS_LEFT);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void bt_reconnect_mouseReleased(MouseEvent e) {
        if (tcpip == null) {
            this.connect();
        }
    }

    /**
     * setCursorPos
     */
    public void setCursorPos(int row, int col, boolean show) {
        if (show) {
            String line = listData[row];
            listData[row] = line.substring(0, col + 1) + "#" +
                   line.substring(col + 2, line.length());
//            System.out.println("Row: " + row + "|COL: + " + col + "|Show" + show);
//            System.out.println("Line: " + line);
            display.setListData(listData);
        }
    }

}


class RundfunkerApplet_bt_reconnect_mouseAdapter extends MouseAdapter {
    private RundfunkerApplet adaptee;
    RundfunkerApplet_bt_reconnect_mouseAdapter(RundfunkerApplet adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.bt_reconnect_mouseReleased(e);
    }
}


class RundfunkerApplet_bt_back_mouseAdapter extends MouseAdapter {
    private RundfunkerApplet adaptee;
    RundfunkerApplet_bt_back_mouseAdapter(RundfunkerApplet adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.bt_back_mouseReleased(e);
    }
}


class RundfunkerApplet_bt_next_mouseAdapter extends MouseAdapter {
    private RundfunkerApplet adaptee;
    RundfunkerApplet_bt_next_mouseAdapter(RundfunkerApplet adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.bt_next_mouseReleased(e);
    }
}


class RundfunkerApplet_bt_ok_mouseAdapter extends MouseAdapter {
    private RundfunkerApplet adaptee;
    RundfunkerApplet_bt_ok_mouseAdapter(RundfunkerApplet adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.bt_ok_mouseReleased(e);
    }
}


class RundfunkerApplet_bt_interpret_mouseAdapter extends MouseAdapter {
    private RundfunkerApplet adaptee;
    RundfunkerApplet_bt_interpret_mouseAdapter(RundfunkerApplet adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.bt_interpret_mouseReleased(e);
    }
}


class RundfunkerApplet_bt_titel_mouseAdapter extends MouseAdapter {
    private RundfunkerApplet adaptee;
    RundfunkerApplet_bt_titel_mouseAdapter(RundfunkerApplet adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.bt_titel_mouseReleased(e);
    }
}


class RundfunkerApplet_bt_genre_mouseAdapter extends MouseAdapter {
    private RundfunkerApplet adaptee;
    RundfunkerApplet_bt_genre_mouseAdapter(RundfunkerApplet adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.bt_genre_mouseReleased(e);
    }
}


class RundfunkerApplet_bt_album_mouseAdapter extends MouseAdapter {
    private RundfunkerApplet adaptee;
    RundfunkerApplet_bt_album_mouseAdapter(RundfunkerApplet adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.bt_album_mouseReleased(e);
    }
}
