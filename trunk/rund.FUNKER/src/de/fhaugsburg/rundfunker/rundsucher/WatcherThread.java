package de.fhaugsburg.rundfunker.rundsucher;

import java.util.Iterator;
import java.util.Vector;

/**
 * Ein "Aufpasser"-Thread, der schaut, welche Threads noch aktiv sind.
 * @author Christian
 *
 */
public class WatcherThread extends Thread {

	private Vector mdts = null;
	public final long timeoutMillis;
	private RundSuche rs = null;

	public WatcherThread(RundSuche rs, Vector metaThreads, long timeoutMillis)
	{
		this.rs = rs;
		this.timeoutMillis = timeoutMillis;
		mdts = metaThreads;
	}

	public void run()
	{
		while(true)
		{
			Iterator it = mdts.iterator();
			while(it.hasNext())
			{
				MetaDataThread mdt = null;
				try
				{
					mdt = (MetaDataThread) it.next();
				}
				catch(java.util.ConcurrentModificationException ex) {
					break;
				}

				//false wenn zu lange nix passiert is
				if(!checkStamp(mdt.getLastStartTimestamp()))
				{
					System.err.println("Verarbeiten von File "+mdt.getAktFileString()+" im Thread "+ mdt.getName() +" dauert schon länger als "+timeoutMillis+" Millisekunden. Thread wird gestoppt und durch neuen ersetzt.");

					//Fehler loggen
//					rs.addErrorFile(mdt.getAktFileString());

					// Alten Thread killen und einen neuen an seiner statt erzeugen
					newThread(mdt);
				}
			}

			try {
				sleep(1000);
			} catch (InterruptedException e) {}
		}
	}

	private boolean checkStamp(long lastTimeStamp)
	{
		return (System.currentTimeMillis()-lastTimeStamp) < timeoutMillis;
	}

	private void newThread(MetaDataThread oldThread)
	{
		mdts.remove(oldThread);
		oldThread.stop();
		rs.newMetaDataThread();
	}


}
