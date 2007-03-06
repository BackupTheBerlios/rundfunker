package de.fhaugsburg.rundfunker.rundspieler.util;

import java.util.Timer;
import java.util.TimerTask;

import de.fhaugsburg.rundfunker.rundspieler.view.Screen;

public class RenderTimerHandler{

	private Timer t;
	private TimerTask renderTimer;
	private Screen screen;
	private long period;
	boolean isRunning = false;

	public RenderTimerHandler(Screen screen, long period){
		this.screen=screen;
		this.period=period;
		t = new Timer();


	}
	public void startRenderTimer(){
		if(isRunning)
			return;
		isRunning=true;
		renderTimer= new TimerTask(){
			public void run() {
				screen.render();
			}

                        public boolean cancel(){
                            isRunning=false;
                            return super.cancel();
                        }
		};
		t.scheduleAtFixedRate(renderTimer, 400, period);
	}

	public void stopRenderTimer(){
		if(!isRunning)
			return;
		isRunning=false;
		renderTimer.cancel() ;
	}


}
