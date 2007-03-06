package de.fhaugsburg.rundfunker.rundspieler.util;

import java.util.Timer;
import java.util.TimerTask;

import de.fhaugsburg.rundfunker.rundspieler.Controller;

public class ReturnTimerHandler {
    private String returnMode;

    private Timer t;

    private TimerTask tt;

    private long timeLastAction;

    private boolean isRunning = false;

    private Controller c;

    public ReturnTimerHandler(String returnMode, Controller c) {
        this.c = c;
        this.returnMode = returnMode;
        t=new Timer();
        initTimerTask();
    }

    public void setReturnMode(String returnMode) {
        this.returnMode = returnMode;
    }

    public void doReturn() {
        c.doReturn(returnMode);
    }

    public void resetTimer() {
        timeLastAction = System.currentTimeMillis();
    }

    public void stopTimer() {
        if (isRunning) {
            isRunning = false;
            tt.cancel();
        }
    }

    public void startTimer(String returnMode) {
        resetTimer();
        if (!isRunning) {
            this.returnMode = returnMode;
            initTimerTask();
            t.scheduleAtFixedRate(tt, 0, 400);
            isRunning = true;
        }

    }

    public boolean isRunning() {
        return isRunning;
    }

    private void initTimerTask() {
        tt = new TimerTask() {
            public void run() {
                long delta = System.currentTimeMillis() - timeLastAction;
                if (delta > 5000) {
                	doReturn();
                    stopTimer();
                }
            }
        };
    }
}