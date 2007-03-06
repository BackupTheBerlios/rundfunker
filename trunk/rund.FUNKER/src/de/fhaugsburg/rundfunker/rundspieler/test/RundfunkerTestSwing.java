package de.fhaugsburg.rundfunker.rundspieler.test;

import gnu.io.PortInUseException;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Arc2D;

import java.io.IOException;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.ListModel;

import javax.swing.JFrame;

import de.fhaugsburg.rundfunker.rundspieler.Controller;
import de.fhaugsburg.rundfunker.rundspieler.ControllerImpl;
import de.fhaugsburg.rundfunker.rundspieler.Playlist;
import de.fhaugsburg.rundfunker.rundspieler.ServerTCPIP;
import de.fhaugsburg.rundfunker.rundspieler.comm.ServerSerial;
import de.fhaugsburg.rundfunker.rundspieler.player.Player;
import de.fhaugsburg.rundfunker.rundspieler.view.DisplayServer;
import de.fhaugsburg.rundfunker.rundspieler.view.DisplayClientSwing;
import de.fhaugsburg.rundfunker.rundspieler.player.JLayerPlayerImpl;
import de.fhaugsburg.rundfunker.rundspieler.sql.PlaylistDBImpl;
import de.fhaugsburg.rundfunker.rundspieler.view.Display;
import de.fhaugsburg.rundfunker.rundspieler.view.DisplayClient;

//import net.leberfinger.rundsucher.Song;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder,
 * which is free for non-commercial use. If Jigloo is being used commercially
 * (ie, by a corporation, company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo. Please visit
 * www.cloudgarden.com for details. Use of Jigloo implies acceptance of these
 * licensing terms. ************************************* A COMMERCIAL LICENSE
 * HAS NOT BEEN PURCHASED for this machine, so Jigloo or this code cannot be
 * used legally for any corporate or commercial purpose.
 * *************************************
 */
public class RundfunkerTestSwing extends JFrame {

    {
        // Set Look & Feel
        try {
            javax.swing.UIManager
                    .setLookAndFeel(
                            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JList jList;

    private JPanel jPControls;

    private JPanel jPanel1;

    private JToggleButton jTB_Power;

    private JButton jB_Titel;

    private JPanel jpButtons;

    private JButton jB_spell;

    private JButton jB_Album;

    private JButton jB_Genre;

    private DDSCanvas dds;

    private JButton jB_Artist;

    private int colNum = 4;

    private Player player;

    private Controller controller;

    private ServerSerial serConn;

    private ServerTCPIP tcpipServer;

    private PlaylistDBImpl pl;

    public static void main(String[] args) {

        try {
            RundfunkerTestSwing dt = new RundfunkerTestSwing();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PortInUseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public RundfunkerTestSwing() throws SQLException, PortInUseException,
            NumberFormatException, IOException {

        initGUI();
        this.validate();
        this.repaint();

        player = new JLayerPlayerImpl();
        // player = new PlayerImpl();
        // player=new NoPlayer();
        // Display display = new DisplaySwingImpl(jList,4);

        DisplayServer display = new DisplayServer();
        pl = new PlaylistDBImpl();
        controller = new ControllerImpl(player, display, pl);

        DisplayClient swingImplDisplay = new DisplayClientSwing(jList, 4);
        System.out.println("Swing-Display-Listener: " + swingImplDisplay);

        display.addDisplayListener(swingImplDisplay);
//		serConn = new ServerSerial(display, controller);
//		serConn.start();
        controller.startRundspieler();
//        tcpipServer = new ServerTCPIP(display, controller);
//        tcpipServer.start();

    }

    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            this.getContentPane().setLayout(thisLayout);
            // super.setPreferredSize(new java.awt.Dimension(400, 300));
            this.setSize(new java.awt.Dimension(400, 300));
            this.setResizable(false);
            this.setLocation(new java.awt.Point(400, 400));
            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    rootWindowClosing(evt);
                }
            });
            this.setSize(400, 300);
            {
                jPanel1 = new JPanel();
                FlowLayout jPanel1Layout = new FlowLayout();
                jPanel1.setLayout(jPanel1Layout);
                this.getContentPane().add(jPanel1, BorderLayout.NORTH);
                {
                    ListModel jList1Model = new DefaultComboBoxModel(
                            new String[] {" ", " ", " ", " "});
                    jList = new JList();
                    jPanel1.add(jList);
                    jList.setModel(jList1Model);
                    jList.setFont(new java.awt.Font("Monospaced", 1, 8));
                    jList.setBackground(new java.awt.Color(0, 0, 255));
                    jList.setForeground(new java.awt.Color(255, 255, 255));
                    jList.setPreferredSize(new java.awt.Dimension(320, 80));
                    jList
                            .setBorder(BorderFactory.createEmptyBorder(5, 5, 5,
                            5));
                }
            }
            {
                jPControls = new JPanel();
                this.getContentPane().add(jPControls, BorderLayout.CENTER);
                jPControls.setPreferredSize(new java.awt.Dimension(300, 100));
                {
                    jpButtons = new JPanel();
                    jPControls.add(jpButtons);
                    BoxLayout jpButtonsLayout = new BoxLayout(jpButtons,
                            javax.swing.BoxLayout.Y_AXIS);
                    jpButtons.setLayout(jpButtonsLayout);
                    jpButtons
                            .setPreferredSize(new java.awt.Dimension(100, 100));
                    {
                        jB_Artist = new JButton();
                        jpButtons.add(jB_Artist);
                        jB_Artist.setText("Artist");
                        jB_Artist
                                .setMaximumSize(new java.awt.Dimension(100, 25));
                        jB_Artist.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent evt) {
                                controller.pressBtArtist();
                            }
                        });
                    }
                    {
                        jB_Genre = new JButton();
                        jpButtons.add(jB_Genre);
                        jB_Genre.setText("Genre");
                        jB_Genre
                                .setMaximumSize(new java.awt.Dimension(100, 25));
                        jB_Genre.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent evt) {
                                controller.pressBtGenre();
                            }
                        });
                    }
                    {
                        jB_Titel = new JButton();
                        jpButtons.add(jB_Titel);
                        jB_Titel.setText("Titel");
                        jB_Titel
                                .setPreferredSize(new java.awt.Dimension(80, 10));
                        jB_Titel.setMinimumSize(new java.awt.Dimension(50, 10));
                        jB_Titel
                                .setMaximumSize(new java.awt.Dimension(100, 25));
                        jB_Titel.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent evt) {
                                controller.pressBtTitle();
                            }
                        });

                    }
                    {
                        jB_Album = new JButton();
                        jpButtons.add(jB_Album);
                        jB_Album.setText("Album");
                        jB_Album
                                .setMaximumSize(new java.awt.Dimension(100, 25));
                        jB_Album.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent evt) {
                                controller.pressBtAlbum();
                            }
                        });
                    }
                }
                {
                    jB_spell = new JButton();
                    jPControls.add(jB_spell);
                    jB_spell.setText("ABC");
                    jB_spell.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            controller.pressBtSpell();
                        }
                    });
                }
                {
                    jTB_Power = new JToggleButton();
                    jPControls.add(jTB_Power);
                    jTB_Power.setText("Power");
                    jTB_Power.setPreferredSize(new java.awt.Dimension(74, 25));
                    jTB_Power.setOpaque(false);
                }
                {
                    dds = new DDSCanvas();
                    dds.addMouseWheelListener(new MouseWheelListener() {

                        public void mouseWheelMoved(MouseWheelEvent e) {
                            dds.setDegree(dds.getDegree() + 20
                                          * e.getWheelRotation());
                            dds.repaint();
                            controller.turnDDS(e.getWheelRotation());
                        }

                    });
                    dds.addMouseListener(new MouseListener() {

                        public void mouseClicked(MouseEvent arg0) {
                            // TODO Auto-generated method stub

                        }

                        public void mouseEntered(MouseEvent arg0) {
                            // TODO Auto-generated method stub

                        }

                        public void mouseExited(MouseEvent arg0) {
                            // TODO Auto-generated method stub

                        }

                        public void mousePressed(MouseEvent arg0) {
                            // TODO Auto-generated method stub

                        }

                        public void mouseReleased(MouseEvent arg0) {
                            controller.pressDDS();
                        }

                    });

                    this.setVisible(true);
                    System.out.println(this.getGraphics());
                    dds.paint(this.getGraphics());
                    jPControls.add(dds);
                    dds.setSize(new java.awt.Dimension(50, 50));
                    dds.setBackground(new java.awt.Color(238, 238, 238));
                    dds.setForeground(new java.awt.Color(255, 255, 255));

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rootWindowClosing(WindowEvent evt) {
        if (serConn != null) {
            serConn.stopped = true;
        }
        if (tcpipServer != null) {
            tcpipServer.stopped = true;
        }
        if (pl != null) {
            try {
                pl.disconnet();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.exit( -1);
    }

    private class DDSCanvas extends Canvas {
        private Graphics2D g2D;

        private int degree = 90;

        public void setDegree(int degree) {
            this.degree = degree;
        }

        public int getDegree() {
            return degree;
        }

        public void paint(Graphics g) {
            super.paint(g);

            g2D = (Graphics2D) g;
            Color c = g2D.getColor();
            g2D.setColor(Color.white);
            // Arc2D.Float arc= Arc2D.Float(Arc2D.OPEN);
            g2D.fill(new Arc2D.Float(0, 0, 48, 48, degree, 360, Arc2D.PIE));
            g2D.setColor(Color.black);
            g2D.draw(new Arc2D.Float(0, 0, 48, 48, degree, 360, Arc2D.PIE));
            g2D.setColor(c);
        }
    }

}
