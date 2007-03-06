package de.fhaugsburg.rundfunker.rundspieler.view;

import java.io.IOException;
import java.io.OutputStream;

public class DisplayClientRemote implements DisplayClient {

	private OutputStream out;

	private int colNum=0;

	private int rowNum=0;

	private DisplayServer dh;

	public DisplayClientRemote(OutputStream out, DisplayServer dh) {
		this.out = out;
		this.dh=dh;
	}

	public void set(OutputStream out) {
		this.out = out;
	}

	public void setLine(int rowNum, String line) {
		writeBytes("Z" + rowNum + " " + line);
		sendCursorPos();
	}

	private void sendCursorPos(){
//		quick and dirty
		String col=String.valueOf(colNum);
		if(colNum<10)
			col="0"+colNum;

		writeBytes("M"+col+"|"+rowNum);
	}

	public void setLED(int ledNum) {
		writeBytes("L" + String.valueOf(ledNum));

	}

	private void writeBytes(String s) {
		try {
			out.write(2);
			out.write(s.getBytes());
			out.write(3);
			out.flush();
		} catch (Exception e) {
			dh.removeDisplayListener(this);
			e.printStackTrace();
		}
	}

	public void setCursorPos(int rowNum, int colNum) {
		this.rowNum=rowNum;
		this.colNum=colNum;

	}

	public void setCursorVisibility(boolean visible) {
		if(visible){
			writeBytes("C1");
		}else{
			writeBytes("C0");
		}
	}

}
