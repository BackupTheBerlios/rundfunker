package de.fhaugsburg.rundfunker.rundspieler;

import gnu.io.PortInUseException;

import java.io.IOException;
import java.sql.SQLException;

import de.fhaugsburg.rundfunker.rundspieler.comm.ServerSerial;
import de.fhaugsburg.rundfunker.rundspieler.player.JLayerPlayerImpl;
import de.fhaugsburg.rundfunker.rundspieler.player.Player;
import de.fhaugsburg.rundfunker.rundspieler.sql.PlaylistDBImpl;
import de.fhaugsburg.rundfunker.rundspieler.view.DisplayServer;

public class Rundspieler {

	private Player player;

	private PlaylistDBImpl playlist;

	private Controller controller;

	private ServerSerial comServer;

	private ServerTCPIP tcpipServer;

	private DisplayServer display;

	public Rundspieler() throws PortInUseException{
		try {
			display = new DisplayServer();
			player = new JLayerPlayerImpl();
			playlist = new PlaylistDBImpl();
			controller = new ControllerImpl(player, display, playlist);
			tcpipServer = new ServerTCPIP(display, controller);
                        comServer = new ServerSerial(display, controller);
		}  catch (SQLException e) {
			display.setLine(1,"Datenbankfehler.");
			e.printStackTrace();
		} catch (IOException e) {
			display.setLine(1,"Fehler beim Starten");
			display.setLine(2,"des TCPIP-Servers.");
			e.printStackTrace();
		}

	}

	public void start(){
		comServer.start();
		tcpipServer.start();
		controller.startRundspieler();
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Rundspieler rundspieler= new Rundspieler();
			rundspieler.start();
		} catch (PortInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
