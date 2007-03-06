package de.fhaugsburg.rundfunker.rundspieler.test;

import java.awt.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.fhaugsburg.rundfunker.rundspieler.ConnectionException;
import de.fhaugsburg.rundfunker.rundsucher.Song;
import de.fhaugsburg.rundfunker.rundspieler.player.*;

public class JLayerTestFrame extends JFrame {

    JPanel contentPane;
    JTextField tbFilename = new JTextField();
    JButton btPlay = new JButton();
    JButton btStop = new JButton();
    JButton btPause = new JButton();
    private Player p = new JLayerPlayerImpl();

    public JLayerTestFrame() {
        try {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Component initialization.
     *
     * @throws java.lang.Exception
     */
    private void jbInit() throws Exception {
        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(null);
        setSize(new Dimension(400, 300));
        setTitle("JLayerTest");
        tbFilename.setText(
                "smb://nobody:nobody@tsat2410-404/newMP3s/2proof/Blunt,James - You\'re " +
                "Beautiful.mp3");
        tbFilename.setBounds(new Rectangle(18, 16, 365, 21));
        btPlay.setBounds(new Rectangle(18, 73, 73, 25));
        btPlay.setText("Play");
        btPlay.addActionListener(new JLayerTestFrame_btPlay_actionAdapter(this));
        btStop.setBounds(new Rectangle(232, 73, 73, 25));
        btStop.setText("Stop");
        btStop.addActionListener(new JLayerTestFrame_btStop_actionAdapter(this));
        btPause.setBounds(new Rectangle(125, 73, 73, 25));
        btPause.setText("Pause");
        btPause.addActionListener(new JLayerTestFrame_btPause_actionAdapter(this));
        contentPane.add(tbFilename);
        contentPane.add(btPlay);
        contentPane.add(btStop);
        contentPane.add(btPause);
    }

    public void btPlay_actionPerformed(ActionEvent e) {
        Song s = new Song();
        s.setFilename(tbFilename.getText());
        try {
            p.playSong(s);
        } catch (ConnectionException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public void btPause_actionPerformed(ActionEvent e) {
        p.pause();
    }

    public void btStop_actionPerformed(ActionEvent e) {
        p.stop();
    }
}


class JLayerTestFrame_btStop_actionAdapter implements ActionListener {
    private JLayerTestFrame adaptee;
    JLayerTestFrame_btStop_actionAdapter(JLayerTestFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btStop_actionPerformed(e);
    }
}


class JLayerTestFrame_btPause_actionAdapter implements ActionListener {
    private JLayerTestFrame adaptee;
    JLayerTestFrame_btPause_actionAdapter(JLayerTestFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btPause_actionPerformed(e);
    }
}


class JLayerTestFrame_btPlay_actionAdapter implements ActionListener {
    private JLayerTestFrame adaptee;
    JLayerTestFrame_btPlay_actionAdapter(JLayerTestFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btPlay_actionPerformed(e);
    }
}
