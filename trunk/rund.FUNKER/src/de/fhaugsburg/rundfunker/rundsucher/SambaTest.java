package de.fhaugsburg.rundfunker.rundsucher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.vdheide.mp3.FrameDamagedException;
import de.vdheide.mp3.ID3v2DecompressionException;
import de.vdheide.mp3.ID3v2IllegalVersionException;
import de.vdheide.mp3.ID3v2WrongCRCException;
import de.vdheide.mp3.MP3FileSamba;
import de.vdheide.mp3.NoMP3FrameException;

public class SambaTest {

	/**
	 * @param args
	 * @throws NoMP3FrameException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws ID3v2IllegalVersionException 
	 * @throws ID3v2DecompressionException 
	 * @throws ID3v2WrongCRCException 
	 * @throws FrameDamagedException 
	 */
	public static void main(String[] args) throws ID3v2WrongCRCException, ID3v2DecompressionException, ID3v2IllegalVersionException, MalformedURLException, IOException, NoMP3FrameException, FrameDamagedException {
		// TODO Auto-generated method stub
		
//		long t1 = System.currentTimeMillis();
//		MP3FileSamba m = new MP3FileSamba("smb://nobody:nobody@tsat2410-404/newMP3s/okay/01 - Schwinger.mp3");
//		System.out.println(m.getArtist().getTextContent());
//		long t2 = System.currentTimeMillis();
//		
//		MP3FileSamba m2 = new MP3FileSamba("smb://nobody:nobody@tsat2410-404/newMP3s/okay/02 - Next.mp3");
//		System.out.println(m.getArtist().getTextContent());
//		
//		
//		
//		System.out.println("Dauer1: " + (System.currentTimeMillis()-t1) + " ms");
//		System.out.println("Dauer2: " + (System.currentTimeMillis()-t2) + " ms");
		
	}

}
