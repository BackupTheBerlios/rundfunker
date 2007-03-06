package de.fhaugsburg.rundfunker.rundspieler.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class DynamicTimerHandler {

	private Timer t;

	private TimerTask tt;

	private Object o;

	private Method method;

	private Object[] args;

	private boolean isRunning = false;

	private static DynamicTimerHandler instance;

	public DynamicTimerHandler(Object o, String methodName, Object[] args)
			throws SecurityException, NoSuchMethodException {
			t = new Timer();
			this.o = o;
			if (args != null) {
				Class[] parameterTypes = new Class[args.length];
				for (int i = 0; i < parameterTypes.length; i++) {
					parameterTypes[i] = args[i].getClass();
				}
				method = o.getClass().getMethod(methodName, parameterTypes);
			} else {
				method = o.getClass().getMethod(methodName, null);
			}
			this.args = args;
	}
	
	

	public void start(long delay, long period) {
		if (isRunning) {
			stop();
		}
		tt = new DynamicTimerTask();
		t.scheduleAtFixedRate(tt, delay, period);
		isRunning = true;

	}

	public void start(long period) {
		if (isRunning) {
			stop();
		}
		tt=new DynamicTimerTask();
		t.schedule(tt, period);
		isRunning = true;

	}

	public void stop() {
		if (!isRunning) {
			return;
		}
		tt.cancel();
		isRunning = false;
	}

	private class DynamicTimerTask extends TimerTask {

		public void run() {
			try {
				method.invoke(o, args);
			} catch (IllegalArgumentException e) {
				stop();
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				stop();
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				stop();
				e.printStackTrace();
			}
		}

		public boolean cancel() {
			isRunning = false;
			return super.cancel();
		}
	}
}
